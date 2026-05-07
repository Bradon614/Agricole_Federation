package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityOverallStatistics {
    private CollectivityInfo collectivityInformation;
    private int newMembersNumber;
    private double overallMemberCurrentDuePercentage;
    private double overallAttendanceRate;
}