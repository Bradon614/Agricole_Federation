package td.agricoles.agricol.repository;

import td.agricoles.agricol.config.DatabaseConfig;
import td.agricoles.agricol.dto.enums.Gender;
import td.agricoles.agricol.dto.request.CreateMember;
import td.agricoles.agricol.dto.request.CreateMemberPayment;
import td.agricoles.agricol.dto.response.Member;
import td.agricoles.agricol.dto.response.MemberPayment;
import td.agricoles.agricol.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {

    public static Member findById(String id) throws SQLException {
        String sql = """
            SELECT id_member, last_name, first_names, birth_date, gender, address, profession, phone, email,
                   adhesion_date, current_collective_id
            FROM member WHERE id_member = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToMember(rs);
                }
                return null;
            }
        }
    }

    public LocalDate getAdhesionDate(String memberId) throws SQLException {
        String sql = "SELECT adhesion_date FROM member WHERE id_member = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("adhesion_date").toLocalDate();
                }
                throw new NotFoundException("Member not found: " + memberId);
            }
        }
    }

    public String getCurrentCollectiveId(String memberId) throws SQLException {
        String sql = "SELECT current_collective_id FROM member WHERE id_member = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("current_collective_id");
                }
                throw new NotFoundException("Member not found: " + memberId);
            }
        }
    }

    public boolean isSeniorMember(String memberId) throws SQLException {
        String sql = """
            SELECT 1 FROM collective_position_occupation cpo
            JOIN position p ON cpo.id_position = p.id_position
            JOIN collective_mandate cm ON cpo.id_mandate = cm.id_mandate
            WHERE cpo.id_member = ? AND p.label = 'SENIOR'
            AND cm.start_date <= CURRENT_DATE AND cm.end_date >= CURRENT_DATE
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public Member save(CreateMember createMember) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseConfig.getConnection();
            conn.setAutoCommit(false);

            String newMemberId = "mem-" + java.util.UUID.randomUUID().toString().substring(0, 8);

            String insertSql = """
            INSERT INTO member (id_member, last_name, first_names, birth_date, gender, address, profession, phone, email,
                                adhesion_date, status, current_collective_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'active', ?)
        """;
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                stmt.setString(1, newMemberId);
                stmt.setString(2, createMember.getLastName());
                stmt.setString(3, createMember.getFirstName());
                stmt.setDate(4, Date.valueOf(createMember.getBirthDate()));
                stmt.setString(5, createMember.getGender().name().substring(0, 1));
                stmt.setString(6, createMember.getAddress());
                stmt.setString(7, createMember.getProfession());
                stmt.setLong(8, createMember.getPhoneNumber());
                stmt.setString(9, createMember.getEmail());
                stmt.setDate(10, Date.valueOf(LocalDate.now()));
                stmt.setString(11, createMember.getCollectivityIdentifier());
                stmt.executeUpdate();
            }

            String histSql = "INSERT INTO adhesion_history (id_member, id_collective, adhesion_date) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(histSql)) {
                stmt.setString(1, newMemberId);
                stmt.setString(2, createMember.getCollectivityIdentifier());
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }


            for (String sponsorId : createMember.getReferees()) {
                String sponsorSql = "INSERT INTO sponsorship (id_new_member, id_sponsor, sponsorship_date) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sponsorSql)) {
                    stmt.setString(1, newMemberId);           // id du nouveau membre
                    stmt.setString(2, sponsorId);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }
            }

            String feeSql = "INSERT INTO contribution (id_member, id_collective, contribution_type, amount, payment_date, payment_method, period) VALUES (?, ?, 'OneTime', 50000, ?, 'Mobile Money', 'Admission fee')";
            try (PreparedStatement stmt = conn.prepareStatement(feeSql)) {
                stmt.setString(1, newMemberId);
                stmt.setString(2, createMember.getCollectivityIdentifier());
                stmt.setDate(3, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }

            double annualAmount = 200000.0;
            String annualSql = "INSERT INTO contribution (id_member, id_collective, contribution_type, amount, payment_date, payment_method, period) VALUES (?, ?, 'Periodic', ?, ?, 'Mobile Money', 'Annual')";
            try (PreparedStatement stmt = conn.prepareStatement(annualSql)) {
                stmt.setString(1, newMemberId);
                stmt.setString(2, createMember.getCollectivityIdentifier());
                stmt.setDouble(3, annualAmount);
                stmt.setDate(4, Date.valueOf(LocalDate.now()));
                stmt.executeUpdate();
            }

            conn.commit();


            Member member = new Member();
            member.setId(newMemberId);
            member.setFirstName(createMember.getFirstName());
            member.setLastName(createMember.getLastName());
            member.setBirthDate(createMember.getBirthDate());
            member.setGender(createMember.getGender());
            member.setAddress(createMember.getAddress());
            member.setProfession(createMember.getProfession());
            member.setPhoneNumber(createMember.getPhoneNumber());
            member.setEmail(createMember.getEmail());
            member.setOccupation(createMember.getOccupation());
            return member;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    public static Member mapRowToMember(ResultSet rs) throws SQLException {
        Member m = new Member();
        m.setId(rs.getString("id_member"));
        m.setLastName(rs.getString("last_name"));
        m.setFirstName(rs.getString("first_names"));
        m.setBirthDate(rs.getDate("birth_date").toLocalDate());
        String genderStr = rs.getString("gender");
        m.setGender("M".equals(genderStr) ? Gender.MALE : Gender.FEMALE);
        m.setAddress(rs.getString("address"));
        m.setProfession(rs.getString("profession"));
        m.setPhoneNumber(rs.getLong("phone"));
        m.setEmail(rs.getString("email"));
        return m;
    }

    public static List<MemberPayment> savePayments(String memberId, List<CreateMemberPayment> payments) throws SQLException {
        List<MemberPayment> result = new ArrayList<>();
        String sql = """
        INSERT INTO member_payment (id_payment, id_member, amount, id_membership_fee, id_account_credited, payment_mode, creation_date)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        RETURNING id_payment
    """;
        String transactionSql = """
        INSERT INTO transaction (id_transaction, id_collective, id_account_credited, id_member_debited, amount, payment_mode, creation_date)
        SELECT ?, a.id_collective, ?, ?, ?, ?, ?
        FROM account a WHERE a.id_account = ?
    """;
        try (Connection conn = DatabaseConfig.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 PreparedStatement txStmt = conn.prepareStatement(transactionSql)) {
                for (CreateMemberPayment payment : payments) {
                    LocalDate now = LocalDate.now();
                    String paymentId = "pay-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                    String transactionId = "tx-" + java.util.UUID.randomUUID().toString().substring(0, 8);

                    stmt.setString(1, paymentId);
                    stmt.setString(2, memberId);
                    stmt.setInt(3, payment.getAmount());
                    stmt.setString(4, payment.getMembershipFeeIdentifier());
                    stmt.setString(5, payment.getAccountCreditedIdentifier());
                    stmt.setString(6, payment.getPaymentMode().name());
                    stmt.setDate(7, Date.valueOf(now));
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        txStmt.setString(1, transactionId);
                        txStmt.setString(2, payment.getAccountCreditedIdentifier());
                        txStmt.setString(3, memberId);
                        txStmt.setInt(4, payment.getAmount());
                        txStmt.setString(5, payment.getPaymentMode().name());
                        txStmt.setDate(6, Date.valueOf(now));
                        txStmt.setString(7, payment.getAccountCreditedIdentifier());
                        txStmt.executeUpdate();

                        MemberPayment mp = new MemberPayment();
                        mp.setId(paymentId);
                        mp.setAmount(payment.getAmount());
                        mp.setPaymentMode(payment.getPaymentMode());
                        mp.setCreationDate(now);
                        result.add(mp);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return result;
    }
}