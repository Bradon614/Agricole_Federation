package td.agricoles.agricol.repository;

import td.agricoles.agricol.config.DatabaseConfig;
import td.agricoles.agricol.dto.enums.*;
import td.agricoles.agricol.dto.request.CreateCollectivity;
import td.agricoles.agricol.dto.request.CreateMembershipFee;
import td.agricoles.agricol.dto.response.*;
import td.agricoles.agricol.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollectiveRepository {

    private final MemberRepository memberRepository = new MemberRepository();

    public static boolean exists(String collectiveId) throws SQLException {
        String sql = "SELECT 1 FROM collective WHERE id_collective = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Collectivity save(CreateCollectivity create) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);


            String newId = "col-" + java.util.UUID.randomUUID().toString().substring(0, 8);
            String insertSql = """
                INSERT INTO collective (id_collective, unique_number, unique_name, city, agricultural_specialty, creation_date,
                                        opening_authorization_date, id_federation)
                VALUES (?, ?, ?, ?, ?, ?, ?, 'fed-1')
            """;
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, newId);
                stmt.setString(2, "COLL-" + System.currentTimeMillis());
                stmt.setString(3, "Collective of " + create.getLocation());
                stmt.setString(4, create.getLocation());
                stmt.setString(5, "Default specialty");
                stmt.setDate(6, Date.valueOf(LocalDate.now()));
                stmt.setDate(7, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }


            int currentYear = LocalDate.now().getYear();
            String mandateSql = "INSERT INTO collective_mandate (id_collective, year, start_date, end_date) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(mandateSql)) {
                LocalDate debut = LocalDate.of(currentYear, 1, 1);
                LocalDate fin = debut.plusYears(1);
                stmt.setString(1, newId);
                stmt.setInt(2, currentYear);
                stmt.setDate(3, Date.valueOf(debut));
                stmt.setDate(4, Date.valueOf(fin));
                stmt.executeUpdate();
            }


            for (String memberId : create.getMembers()) {
                String updateMemberSql = "UPDATE member SET current_collective_id = ? WHERE id_member = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateMemberSql)) {
                    stmt.setString(1, newId);
                    stmt.setString(2, memberId);
                    stmt.executeUpdate();
                }
                String histSql = "INSERT INTO adhesion_history (id_member, id_collective, adhesion_date) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(histSql)) {
                    stmt.setString(1, memberId);
                    stmt.setString(2, newId);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }
            }


            assignPosition(conn, newId, create.getStructure().getPresident(), "President");
            assignPosition(conn, newId, create.getStructure().getVicePresident(), "Vice President");
            assignPosition(conn, newId, create.getStructure().getTreasurer(), "Treasurer");
            assignPosition(conn, newId, create.getStructure().getSecretary(), "Secretary");

            conn.commit();

            Collectivity coll = new Collectivity();
            coll.setId(newId);
            coll.setLocation(create.getLocation());

            return coll;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    private void assignPosition(Connection conn, String collectiveId, String memberId, String positionLabel) throws SQLException {
        String posSql = "SELECT id_position FROM position WHERE label = ?::position_label_enum";
        String positionId;
        try (PreparedStatement stmt = conn.prepareStatement(posSql)) {
            stmt.setString(1, positionLabel);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                positionId = rs.getString(1);
            } else {
                throw new SQLException("Position not found: " + positionLabel);
            }
        }

        String mandateSql = "SELECT id_mandate FROM collective_mandate WHERE id_collective = ? AND start_date <= CURRENT_DATE AND end_date >= CURRENT_DATE";
        String mandateId;
        try (PreparedStatement stmt = conn.prepareStatement(mandateSql)) {
            stmt.setString(1, collectiveId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                mandateId = rs.getString(1);
            } else {
                throw new SQLException("No active mandate for collective " + collectiveId);
            }
        }
        String occupSql = "INSERT INTO collective_position_occupation (id_mandate, id_position, id_member) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(occupSql)) {
            stmt.setString(1, mandateId);
            stmt.setString(2, positionId);
            stmt.setString(3, memberId);
            stmt.executeUpdate();
        }
    }


    public boolean hasNumberAssigned(String collectiveId) throws SQLException {
        String sql = "SELECT unique_number FROM collective WHERE id_collective = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String number = rs.getString("unique_number");
                    return number != null && !number.isBlank();
                }
                throw new NotFoundException("Collective not found: " + collectiveId);
            }
        }
    }

    public boolean hasNameAssigned(String collectiveId) throws SQLException {
        String sql = "SELECT unique_name FROM collective WHERE id_collective = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("unique_name");
                    return name != null && !name.isBlank();
                }
                throw new NotFoundException("Collective not found: " + collectiveId);
            }
        }
    }

    public boolean numberExists(String number) throws SQLException {
        String sql = "SELECT 1 FROM collective WHERE unique_number = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, number);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean nameExists(String name) throws SQLException {
        String sql = "SELECT 1 FROM collective WHERE unique_name = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Collectivity assignIdentifiers(String collectiveId, String number, String name) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            String updateSql = "UPDATE collective SET unique_number = ?, unique_name = ? WHERE id_collective = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, number);
                stmt.setString(2, name);
                stmt.setString(3, collectiveId);
                int rows = stmt.executeUpdate();
                if (rows == 0) {
                    throw new NotFoundException("Collective not found: " + collectiveId);
                }
            }
            conn.commit();
            return findById(collectiveId);
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public Collectivity findById(String collectiveId) throws SQLException {
        String sql = "SELECT id_collective, unique_number, unique_name, city FROM collective WHERE id_collective = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Collectivity coll = new Collectivity();
                    coll.setId(rs.getString("id_collective"));
                    coll.setNumber(rs.getString("unique_number"));
                    coll.setName(rs.getString("unique_name"));
                    coll.setLocation(rs.getString("city"));
                    return coll;
                }
                throw new NotFoundException("Collective not found: " + collectiveId);
            }
        }
    }


    public static List<MembershipFee> findMembershipFeesByCollectiveId(String collectiveId) throws SQLException {
        List<MembershipFee> fees = new ArrayList<>();
        String sql = """
            SELECT id_membership_fee, eligible_from, frequency, amount, label, status
            FROM membership_fee WHERE id_collective = ? ORDER BY eligible_from DESC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MembershipFee fee = new MembershipFee();
                    fee.setId(rs.getString("id_membership_fee"));
                    fee.setEligibleFrom(rs.getDate("eligible_from").toLocalDate());
                    fee.setFrequency(td.agricoles.agricol.dto.enums.FeeFrequency.valueOf(rs.getString("frequency")));
                    fee.setAmount(rs.getDouble("amount"));
                    fee.setLabel(rs.getString("label"));
                    fee.setStatus(ActivityStatus.valueOf(rs.getString("status")));
                    fees.add(fee);
                }
            }
        }
        return fees;
    }

    public static List<MembershipFee> saveMembershipFees(String collectiveId, List<CreateMembershipFee> fees) throws SQLException {
        List<MembershipFee> created = new ArrayList<>();
        String sql = """
            INSERT INTO membership_fee (id_collective, eligible_from, frequency, amount, label, status)
            VALUES (?, ?, ?, ?, ?, 'ACTIVE') RETURNING id_membership_fee
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (CreateMembershipFee fee : fees) {
                stmt.setString(1, collectiveId);
                stmt.setDate(2, Date.valueOf(fee.getEligibleFrom()));
                stmt.setString(3, fee.getFrequency().name());
                stmt.setDouble(4, fee.getAmount());
                stmt.setString(5, fee.getLabel());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    MembershipFee mf = new MembershipFee();
                    mf.setId(rs.getString(1));
                    mf.setEligibleFrom(fee.getEligibleFrom());
                    mf.setFrequency(fee.getFrequency());
                    mf.setAmount(fee.getAmount());
                    mf.setLabel(fee.getLabel());
                    mf.setStatus(ActivityStatus.ACTIVE);
                    created.add(mf);
                }
            }
        }
        return created;
    }

    public static List<CollectivityTransaction> findTransactionsByCollectiveIdAndPeriod(
            String collectiveId, LocalDate from, LocalDate to) throws SQLException {
        List<CollectivityTransaction> transactions = new ArrayList<>();
        String sql = """
            SELECT t.id_transaction, t.creation_date, t.amount, t.payment_mode,
                   a.id_account, a.account_type, a.holder_name, a.balance,
                   m.id_member, m.last_name, m.first_names
            FROM transaction t
            JOIN account a ON t.id_account_credited = a.id_account
            JOIN member m ON t.id_member_debited = m.id_member
            WHERE a.id_collective = ? AND t.creation_date BETWEEN ? AND ?
            ORDER BY t.creation_date DESC
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            stmt.setDate(2, Date.valueOf(from));
            stmt.setDate(3, Date.valueOf(to));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CollectivityTransaction tx = new CollectivityTransaction();
                    tx.setId(rs.getString("id_transaction"));
                    tx.setCreationDate(rs.getDate("creation_date").toLocalDate());
                    tx.setAmount(rs.getDouble("amount"));
                    tx.setPaymentMode(PaymentMode.valueOf(rs.getString("payment_mode")));
                    Member member = new Member();
                    member.setId(rs.getString("id_member"));
                    member.setLastName(rs.getString("last_name"));
                    member.setFirstName(rs.getString("first_names"));
                    tx.setMemberDebited(member);
                    transactions.add(tx);
                }
            }
        }
        return transactions;
    }

    public Collectivity findByIdFull(String collectiveId) throws SQLException {
        String sql = """
            SELECT id_collective, unique_number, unique_name, city, agricultural_specialty,
                   creation_date, opening_authorization_date
            FROM collective WHERE id_collective = ?
        """;
        Collectivity coll = null;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    coll = new Collectivity();
                    coll.setId(rs.getString("id_collective"));
                    coll.setNumber(rs.getString("unique_number"));
                    coll.setName(rs.getString("unique_name"));
                    coll.setLocation(rs.getString("city"));
                } else {
                    return null;
                }
            }
        }
        if (coll != null) {
            coll.setStructure(getCurrentStructure(collectiveId));
            coll.setMembers(getActiveMembers(collectiveId));
        }
        return coll;
    }

    private CollectivityStructure getCurrentStructure(String collectiveId) throws SQLException {
        String sql = """
            SELECT p.label, m.id_member, m.last_name, m.first_names, m.email
            FROM collective_position_occupation cpo
            JOIN position p ON cpo.id_position = p.id_position
            JOIN member m ON cpo.id_member = m.id_member
            JOIN collective_mandate cm ON cpo.id_mandate = cm.id_mandate
            WHERE cm.id_collective = ? AND cm.start_date <= CURRENT_DATE AND cm.end_date >= CURRENT_DATE
              AND p.label IN ('President', 'Vice President', 'Treasurer', 'Secretary')
        """;
        CollectivityStructure struct = new CollectivityStructure();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String label = rs.getString("label");
                    Member m = new Member();
                    m.setId(rs.getString("id_member"));
                    m.setLastName(rs.getString("last_name"));
                    m.setFirstName(rs.getString("first_names"));
                    m.setEmail(rs.getString("email"));
                    switch (label) {
                        case "President" -> struct.setPresident(m);
                        case "Vice President" -> struct.setVicePresident(m);
                        case "Treasurer" -> struct.setTreasurer(m);
                        case "Secretary" -> struct.setSecretary(m);
                    }
                }
            }
        }
        return struct;
    }

    private List<Member> getActiveMembers(String collectiveId) throws SQLException {
        String sql = """
            SELECT m.id_member, m.last_name, m.first_names, m.birth_date, m.gender, m.address,
                   m.profession, m.phone, m.email, m.adhesion_date
            FROM member m
            WHERE m.current_collective_id = ? AND m.status = 'active'
        """;
        List<Member> members = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(MemberRepository.mapRowToMember(rs));
                }
            }
        }
        return members;
    }


    public List<FinancialAccount> findFinancialAccounts(String collectiveId, LocalDate at) throws SQLException {
        List<FinancialAccount> accounts = new ArrayList<>();

        String accountSql = """
        SELECT a.id_account, a.account_type, a.holder_name, a.balance,
               mma.phone_number, mma.operator
        FROM account a
        LEFT JOIN mobile_money_account mma ON a.id_account = mma.id_account
        WHERE a.id_collective = ?
    """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(accountSql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String type = rs.getString("account_type");
                    String id = rs.getString("id_account");

                    FinancialAccount account = switch (type) {
                        case "Cash" -> {
                            CashAccount ca = new CashAccount();
                            ca.setId(id);
                            ca.setAmount(rs.getInt("balance")); // balance initiale (on la remplacera après calcul)
                            yield ca;
                        }
                        case "MobileMoney" -> {
                            MobileBankingAccount mma = new MobileBankingAccount();
                            mma.setId(id);
                            mma.setHolderName(rs.getString("holder_name"));
                            mma.setMobileBankingService(
                                    MobileBankingService.valueOf(rs.getString("operator"))
                            );
                            mma.setMobileNumber(rs.getInt("phone_number"));
                            mma.setAmount(rs.getDouble("balance")); // balance initiale
                            yield mma;
                        }
                        default -> throw new SQLException("Unknown account type: " + type);
                    };


                    double balanceAtDate = getAccountBalance(id, at);
                    if (account instanceof CashAccount ca) {
                        ca.setAmount((int) balanceAtDate);
                    } else if (account instanceof MobileBankingAccount mma) {
                        mma.setAmount(balanceAtDate);
                    }
                    accounts.add(account);
                }
            }
        }
        return accounts;
    }

    private double getAccountBalance(String accountId, LocalDate at) throws SQLException {

        String initSql = "SELECT COALESCE(balance, 0) FROM account WHERE id_account = ?";
        double initial;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(initSql)) {
            stmt.setString(1, accountId);
            ResultSet rs = stmt.executeQuery();
            initial = rs.next() ? rs.getDouble(1) : 0.0;
        }


        String creditSql = """
        SELECT COALESCE(SUM(amount), 0) FROM transaction
        WHERE id_account_credited = ? AND creation_date <= ?
    """;
        double credits;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(creditSql)) {
            stmt.setString(1, accountId);
            stmt.setDate(2, Date.valueOf(at));
            ResultSet rs = stmt.executeQuery();
            credits = rs.next() ? rs.getDouble(1) : 0.0;
        }

        return initial + credits;
    }
    public static boolean testConnection() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Connection failed: " + e.getMessage());
            return false;
        }
    }
}