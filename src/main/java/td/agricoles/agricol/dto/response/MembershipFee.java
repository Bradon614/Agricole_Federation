package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.enums.ActivityStatus;
import td.agricoles.agricol.dto.request.CreateMembershipFee;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MembershipFee extends CreateMembershipFee {
    private String id;
    private ActivityStatus status;
}