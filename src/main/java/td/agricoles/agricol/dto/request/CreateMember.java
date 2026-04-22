package td.agricoles.agricol.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.MemberIdentifier;
import td.agricoles.agricol.dto.MemberInformation;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateMember extends MemberInformation {
    private String collectivityIdentifier;
    private List<MemberIdentifier> referees;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;
}
