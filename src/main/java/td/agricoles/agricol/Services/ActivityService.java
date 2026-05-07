package td.agricoles.agricol.Services;

import org.springframework.stereotype.Service;
import td.agricoles.agricol.dto.request.CreateActivityRequest;
import td.agricoles.agricol.dto.request.AttendanceEntryRequest;
import td.agricoles.agricol.dto.response.ActivityResponse;
import td.agricoles.agricol.dto.response.AttendanceResponse;
import td.agricoles.agricol.exception.NotFoundException;
import td.agricoles.agricol.repository.ActivityRepository;
import td.agricoles.agricol.repository.CollectiveRepository;

import java.sql.SQLException;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final CollectiveRepository collectiveRepository;

    public ActivityService(ActivityRepository activityRepository, CollectiveRepository collectiveRepository) {
        this.activityRepository = activityRepository;
        this.collectiveRepository = collectiveRepository;
    }

    public List<ActivityResponse> createActivities(String collectiveId, List<CreateActivityRequest> requests) {
        try {
            if (!collectiveRepository.exists(collectiveId)) {
                throw new NotFoundException("Collectivity not found: " + collectiveId);
            }
            return activityRepository.createActivities(collectiveId, requests);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while creating activities", e);
        }
    }

    public List<ActivityResponse> getActivities(String collectiveId) {
        try {
            if (!collectiveRepository.exists(collectiveId)) {
                throw new NotFoundException("Collectivity not found: " + collectiveId);
            }
            return activityRepository.getActivitiesByCollective(collectiveId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while retrieving activities", e);
        }
    }

    public List<AttendanceResponse> recordAttendance(String activityId, List<AttendanceEntryRequest> entries) {
        try {
            return activityRepository.recordAttendance(activityId, entries);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while recording attendance", e);
        }
    }

    public List<AttendanceResponse> getAttendance(String activityId) {
        try {
            return activityRepository.getAttendanceByActivity(activityId);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while retrieving attendance", e);
        }
    }
}