package td.agricoles.agricol.DTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class CreateMember extends MemberInformation{
    private String collectivityIdentifier;
    private List<String> referees;
    private boolean registrationFeePaid;
    private boolean membershipDuesPaid;
}
