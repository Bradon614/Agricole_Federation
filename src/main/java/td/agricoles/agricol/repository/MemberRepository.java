package td.agricoles.agricol.repository;
//import com.hei.federation.config.DatabaseConfig;
//import com.hei.federation.dto.MemberIdentifier;
//import com.hei.federation.dto.enums.Gender;
//import com.hei.federation.dto.request.CreateMember;
//import com.hei.federation.dto.response.Member;
//import com.hei.federation.exception.NotFoundException;
import td.agricoles.agricol.config.DatabaseConfig;
import td.agricoles.agricol.dto.enums.Gender;
import td.agricoles.agricol.dto.response.Member;
import td.agricoles.agricol.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MemberRepository {

        public Member findById(String id) throws SQLException {
            String sql = """
            SELECT id_member, last_name, first_names, birth_date, gender, address, profession, phone, email,
                   adhesion_date, current_collective_id
            FROM member WHERE id_member = ?
        """;
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(id));
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
                stmt.setInt(1, Integer.parseInt(memberId));
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
                stmt.setInt(1, Integer.parseInt(memberId));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int collId = rs.getInt("current_collective_id");
                        return rs.wasNull() ? null : String.valueOf(collId);
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
            WHERE cpo.id_member = ? AND p.label = 'Senior Member'
            AND cm.start_date <= CURRENT_DATE AND cm.end_date >= CURRENT_DATE
        """;
            try (Connection conn = DatabaseConfig.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, Integer.parseInt(memberId));
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

                // 1. Insert member
                String insertSql = """
                INSERT INTO member (last_name, first_names, birth_date, gender, address, profession, phone, email,
                                    adhesion_date, status, current_collective_id)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'active', ?)
                RETURNING id_member
            """;
                int newId;
                try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                    stmt.setString(1, createMember.getLastName());
                    stmt.setString(2, createMember.getFirstName());
                    stmt.setDate(3, Date.valueOf(createMember.getBirthDate()));
                    stmt.setString(4, createMember.getGender().name().substring(0, 1)); // 'M' or 'F'
                    stmt.setString(5, createMember.getAddress());
                    stmt.setString(6, createMember.getProfession());
                    stmt.setLong(7, createMember.getPhoneNumber());
                    stmt.setString(8, createMember.getEmail());
                    stmt.setDate(9, Date.valueOf(LocalDate.now())); // adhesion_date = today
                    stmt.setInt(10, Integer.parseInt(createMember.getCollectivityIdentifier()));
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        newId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating member failed, no ID obtained.");
                    }
                }

                // 2. Insert into adhesion_history
                String histSql = """
                INSERT INTO adhesion_history (id_member, id_collective, adhesion_date)
                VALUES (?, ?, ?)
            """;
                try (PreparedStatement stmt = conn.prepareStatement(histSql)) {
                    stmt.setInt(1, newId);
                    stmt.setInt(2, Integer.parseInt(createMember.getCollectivityIdentifier()));
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }

                // 3. Insert sponsorships
                for (MemberIdentifier ref : createMember.getReferees()) {
                    String sponsorSql = """
                    INSERT INTO sponsorship (id_new_member, id_sponsor, sponsorship_date)
                    VALUES (?, ?, ?)
                """;
                    try (PreparedStatement stmt = conn.prepareStatement(sponsorSql)) {
                        stmt.setInt(1, newId);
                        stmt.setInt(2, Integer.parseInt(ref.getId()));
                        stmt.setDate(3, Date.valueOf(LocalDate.now()));
                        stmt.executeUpdate();
                    }
                }

                // 4. Record payments
                // Admission fee 50,000 MGA (OneTime)
                String feeSql = """
                INSERT INTO contribution (id_member, id_collective, contribution_type, amount,
                                          payment_date, payment_method, period)
                VALUES (?, ?, 'OneTime', 50000, ?, 'Mobile Money', 'Admission fee')
            """;
                try (PreparedStatement stmt = conn.prepareStatement(feeSql)) {
                    stmt.setInt(1, newId);
                    stmt.setInt(2, Integer.parseInt(createMember.getCollectivityIdentifier()));
                    stmt.setDate(3, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }

                // Annual contribution: amount should be retrieved dynamically (here fixed 200000)
                double annualAmount = 200000.0; // Adapt according to collective
                String annualSql = """
                INSERT INTO contribution (id_member, id_collective, contribution_type, amount,
                                          payment_date, payment_method, period)
                VALUES (?, ?, 'Periodic', ?, ?, 'Mobile Money', 'Annual')
            """;
                try (PreparedStatement stmt = conn.prepareStatement(annualSql)) {
                    stmt.setInt(1, newId);
                    stmt.setInt(2, Integer.parseInt(createMember.getCollectivityIdentifier()));
                    stmt.setDouble(3, annualAmount);
                    stmt.setDate(4, Date.valueOf(LocalDate.now()));
                    stmt.executeUpdate();
                }

                conn.commit();

                // Build response object
                Member member = new Member();
                member.setId(String.valueOf(newId));
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

        private Member mapRowToMember(ResultSet rs) throws SQLException {
            Member m = new Member();
            m.setId(String.valueOf(rs.getInt("id_member")));
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
}

