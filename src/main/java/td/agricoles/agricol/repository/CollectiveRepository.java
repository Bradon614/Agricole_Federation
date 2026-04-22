package td.agricoles.agricol.repository;

import td.agricoles.agricol.config.DatabaseConfig;
import td.agricoles.agricol.dto.request.CreateCollectivity;
import td.agricoles.agricol.dto.response.Collectivity;
import td.agricoles.agricol.dto.response.CollectivityStructure;
import td.agricoles.agricol.dto.response.Member;
import td.agricoles.agricol.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CollectiveRepository {

    private final MemberRepository memberRepository = new MemberRepository();

    public boolean exists(String collectiveId) throws SQLException {
        String sql = "SELECT 1 FROM collective WHERE id_collective = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(collectiveId));
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

            // 1. Insert collective
            String insertSql = """
                INSERT INTO collective (unique_number, unique_name, city, agricultural_specialty, creation_date,
                                        opening_authorization_date, id_federation)
                VALUES (?, ?, ?, ?, ?, ?, 1)
                RETURNING id_collective
            """;
            int newId;
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                String numero = "COLL-" + System.currentTimeMillis();
                String nom = "Collective of " + create.getLocation();
                stmt.setString(1, numero);
                stmt.setString(2, nom);
                stmt.setString(3, create.getLocation());
                stmt.setString(4, "Default specialty");
                stmt.setDate(5, Date.valueOf(LocalDate.now()));
                stmt.setDate(6, Date.valueOf(LocalDate.now()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    newId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating collective failed, no ID obtained.");
                }
            }

            // 2. Create mandate for current year
            int currentYear = LocalDate.now().getYear();
            String mandateSql = """
                INSERT INTO collective_mandate (id_collective, year, start_date, end_date)
                VALUES (?, ?, ?, ?)
                RETURNING id_mandate
            """;
            int mandateId;
            try (PreparedStatement stmt = conn.prepareStatement(mandateSql)) {
                LocalDate debut = LocalDate.of(currentYear, 1, 1);
                LocalDate fin = debut.plusYears(1);
                stmt.setInt(1, newId);
                stmt.setInt(2, currentYear);
                stmt.setDate(3, Date.valueOf(debut));
                stmt.setDate(4, Date.valueOf(fin));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    mandateId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating mandate failed.");
                }
            }

            // 3. Associate members to collective
            for (String memberId : create.getMembers()) {
                String updateMemberSql = "UPDATE member SET current_collective_id = ? WHERE id_member = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateMemberSql)) {
                    stmt.setInt(1, newId);
                    stmt.setInt(2, Integer.parseInt(memberId));
                    stmt.executeUpdate();
                }
                String histSql = """
                    INSERT INTO adhesion_history (id_member, id_collective, adhesion_date)
                    VALUES (?, ?, ?)
                """;
                try (PreparedStatement stmt = conn.prepareStatement(histSql)) {
                    stmt.setInt(1, Integer.parseInt(memberId));
                    stmt.setInt(2, newId);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }
            }

            // 4. Assign specific positions
            assignPosition(conn, mandateId, create.getStructure().getPresident(), "President");
            assignPosition(conn, mandateId, create.getStructure().getVicePresident(), "Vice President");
            assignPosition(conn, mandateId, create.getStructure().getTreasurer(), "Treasurer");
            assignPosition(conn, mandateId, create.getStructure().getSecretary(), "Secretary");

            conn.commit();

            // Build response
            Collectivity coll = new Collectivity();
            coll.setId(String.valueOf(newId));
            coll.setLocation(create.getLocation());

            CollectivityStructure struct = new CollectivityStructure();
            struct.setPresident(getMemberSafely(create.getStructure().getPresident()));
            struct.setVicePresident(getMemberSafely(create.getStructure().getVicePresident()));
            struct.setTreasurer(getMemberSafely(create.getStructure().getTreasurer()));
            struct.setSecretary(getMemberSafely(create.getStructure().getSecretary()));
            coll.setStructure(struct);

            List<Member> memberList = new ArrayList<>();
            for (String memberId : create.getMembers()) {
                Member m = getMemberSafely(memberId);
                if (m != null) {
                    memberList.add(m);
                }
            }
            coll.setMembers(memberList);

            return coll;

        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) conn.setAutoCommit(true);
        }
    }

    private Member getMemberSafely(String memberId) {
        try {
            return memberRepository.findById(memberId);
        } catch (SQLException e) {
            System.err.println("Failed to fetch member " + memberId + ": " + e.getMessage());
            return null;
        }
    }

    private void assignPosition(Connection conn, int mandateId, String memberId, String positionLabel) throws SQLException {
        String posSql = "SELECT id_position FROM position WHERE label = ?::position_label_enum";
        int positionId;
        try (PreparedStatement stmt = conn.prepareStatement(posSql)) {
            stmt.setString(1, positionLabel);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                positionId = rs.getInt(1);
            } else {
                throw new SQLException("Position not found: " + positionLabel);
            }
        }
        String occupSql = """
            INSERT INTO collective_position_occupation (id_mandate, id_position, id_member)
            VALUES (?, ?, ?)
        """;
        try (PreparedStatement stmt = conn.prepareStatement(occupSql)) {
            stmt.setInt(1, mandateId);
            stmt.setInt(2, positionId);
            stmt.setInt(3, Integer.parseInt(memberId));
            stmt.executeUpdate();
        }
    }

    // ---------- Feature J methods ----------
    public boolean hasNumberAssigned(String collectiveId) throws SQLException {
        String sql = "SELECT unique_number FROM collective WHERE id_collective = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(collectiveId));
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
            stmt.setInt(1, Integer.parseInt(collectiveId));
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

            String updateSql = """
                UPDATE collective
                SET unique_number = ?, unique_name = ?
                WHERE id_collective = ?
            """;
            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, number);
                stmt.setString(2, name);
                stmt.setInt(3, Integer.parseInt(collectiveId));
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
        String sql = """
            SELECT id_collective, unique_number, unique_name, city
            FROM collective
            WHERE id_collective = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Integer.parseInt(collectiveId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Collectivity coll = new Collectivity();
                    coll.setId(String.valueOf(rs.getInt("id_collective")));
                    coll.setNumber(rs.getString("unique_number"));
                    coll.setName(rs.getString("unique_name"));
                    coll.setLocation(rs.getString("city"));
                    return coll;
                }
                throw new NotFoundException("Collective not found: " + collectiveId);
            }
        }
    }
}