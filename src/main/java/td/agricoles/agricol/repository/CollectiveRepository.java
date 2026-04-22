package td.agricoles.agricol.repository;

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
                stmt.setString(4, "Default specialty"); // Not provided by API
                stmt.setDate(5, Date.valueOf(LocalDate.now()));
                stmt.setDate(6, Date.valueOf(LocalDate.now())); // authorized immediately
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

            // 3. Associate members to collective (update member and insert adhesion_history)
            for (MemberIdentifier mId : create.getMembers()) {
                // Update current_collective_id
                String updateMemberSql = "UPDATE member SET current_collective_id = ? WHERE id_member = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateMemberSql)) {
                    stmt.setInt(1, newId);
                    stmt.setInt(2, Integer.parseInt(mId.getId()));
                    stmt.executeUpdate();
                }
                // Add to history
                String histSql = """
                    INSERT INTO adhesion_history (id_member, id_collective, adhesion_date)
                    VALUES (?, ?, ?)
                """;
                try (PreparedStatement stmt = conn.prepareStatement(histSql)) {
                    stmt.setInt(1, Integer.parseInt(mId.getId()));
                    stmt.setInt(2, newId);
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }
            }

            // 4. Assign specific positions
            assignPosition(conn, mandateId, create.getStructure().getPresident().getId(), "President");
            assignPosition(conn, mandateId, create.getStructure().getVicePresident().getId(), "Vice President");
            assignPosition(conn, mandateId, create.getStructure().getTreasurer().getId(), "Treasurer");
            assignPosition(conn, mandateId, create.getStructure().getSecretary().getId(), "Secretary");

            conn.commit();

            // Build response
            Collectivity coll = new Collectivity();
            coll.setId(String.valueOf(newId));
            coll.setLocation(create.getLocation());

            CollectivityStructure struct = new CollectivityStructure();
            struct.setPresident(memberRepository.findById(create.getStructure().getPresident().getId()));
            struct.setVicePresident(memberRepository.findById(create.getStructure().getVicePresident().getId()));
            struct.setTreasurer(memberRepository.findById(create.getStructure().getTreasurer().getId()));
            struct.setSecretary(memberRepository.findById(create.getStructure().getSecretary().getId()));
            coll.setStructure(struct);

            List<Member> memberList = new ArrayList<>();
            for (MemberIdentifier mId : create.getMembers()) {
                memberList.add(memberRepository.findById(mId.getId()));
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

    private void assignPosition(Connection conn, int mandateId, String memberId, String positionLabel) throws SQLException {
        // Get position id
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
        // Insert occupation
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
}
