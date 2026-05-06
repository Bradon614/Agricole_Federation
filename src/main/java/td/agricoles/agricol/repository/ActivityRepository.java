package td.agricoles.agricol.repository;

import td.agricoles.agricol.config.DatabaseConfig;
import td.agricoles.agricol.dto.request.CreateActivityRequest;
import td.agricoles.agricol.dto.request.AttendanceEntryRequest;
import td.agricoles.agricol.dto.response.ActivityResponse;
import td.agricoles.agricol.dto.response.AttendanceResponse;
import td.agricoles.agricol.dto.response.MemberDescription;
import td.agricoles.agricol.exception.NotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActivityRepository {

    public List<ActivityResponse> createActivities(String collectiveId, List<CreateActivityRequest> requests) throws SQLException {
        List<ActivityResponse> result = new ArrayList<>();
        String sql = """
            INSERT INTO activity (id_activity, id_collective, activity_type, description, scheduled_date, is_mandatory, creation_date)
            VALUES (?, ?, ?::activity_type_enum, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (CreateActivityRequest req : requests) {
                String activityId = "act-" + UUID.randomUUID().toString().substring(0, 8);
                LocalDate now = LocalDate.now();
                stmt.setString(1, activityId);
                stmt.setString(2, collectiveId);
                stmt.setString(3, req.getType());
                stmt.setString(4, req.getDescription());
                stmt.setDate(5, Date.valueOf(req.getScheduledDate()));
                stmt.setBoolean(6, req.isMandatory());
                stmt.setDate(7, Date.valueOf(now));
                stmt.executeUpdate();
                result.add(new ActivityResponse(activityId, req.getType(), req.getDescription(), req.getScheduledDate(), req.isMandatory(), now));
            }
        }
        return result;
    }

    public List<ActivityResponse> getActivitiesByCollective(String collectiveId) throws SQLException {
        List<ActivityResponse> list = new ArrayList<>();
        String sql = "SELECT id_activity, activity_type, description, scheduled_date, is_mandatory, creation_date FROM activity WHERE id_collective = ? ORDER BY scheduled_date DESC";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, collectiveId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new ActivityResponse(
                            rs.getString("id_activity"),
                            rs.getString("activity_type"),
                            rs.getString("description"),
                            rs.getDate("scheduled_date").toLocalDate(),
                            rs.getBoolean("is_mandatory"),
                            rs.getDate("creation_date").toLocalDate()
                    ));
                }
            }
        }
        return list;
    }

    public List<AttendanceResponse> recordAttendance(String activityId, List<AttendanceEntryRequest> entries) throws SQLException {
        if (!activityExists(activityId)) {
            throw new NotFoundException("Activity not found: " + activityId);
        }

        List<AttendanceResponse> result = new ArrayList<>();
        String sql = """
            INSERT INTO attendance (id_attendance, id_activity, id_member, status, created_at)
            VALUES (?, ?, ?, ?::attendance_status_enum, ?)
            ON CONFLICT (id_activity, id_member) DO NOTHING
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (AttendanceEntryRequest entry : entries) {
                String attId = "att-" + UUID.randomUUID().toString().substring(0, 8);
                LocalDate now = LocalDate.now();
                stmt.setString(1, attId);
                stmt.setString(2, activityId);
                stmt.setString(3, entry.getMemberId());
                stmt.setString(4, entry.getStatus());
                stmt.setDate(5, Date.valueOf(now));
                stmt.executeUpdate();
                MemberDescription md = getMemberDescription(entry.getMemberId());
                result.add(new AttendanceResponse(md, entry.getStatus(), now));
            }
        }
        return result;
    }

    public List<AttendanceResponse> getAttendanceByActivity(String activityId) throws SQLException {
        List<AttendanceResponse> list = new ArrayList<>();
        String sql = """
            SELECT a.status, a.created_at, m.id_member, m.first_names, m.last_name, m.email, m.occupation
            FROM attendance a
            JOIN member m ON a.id_member = m.id_member
            WHERE a.id_activity = ?
        """;
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    MemberDescription md = new MemberDescription(
                            rs.getString("id_member"),
                            rs.getString("first_names"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("occupation")
                    );
                    list.add(new AttendanceResponse(md, rs.getString("status"), rs.getDate("created_at").toLocalDate()));
                }
            }
        }
        return list;
    }

    private boolean activityExists(String activityId) throws SQLException {
        String sql = "SELECT 1 FROM activity WHERE id_activity = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, activityId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private MemberDescription getMemberDescription(String memberId) throws SQLException {
        String sql = "SELECT id_member, first_names, last_name, email, occupation FROM member WHERE id_member = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, memberId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new MemberDescription(
                            rs.getString("id_member"),
                            rs.getString("first_names"),
                            rs.getString("last_name"),
                            rs.getString("email"),
                            rs.getString("occupation")
                    );
                }
                throw new NotFoundException("Member not found: " + memberId);
            }
        }
    }
}