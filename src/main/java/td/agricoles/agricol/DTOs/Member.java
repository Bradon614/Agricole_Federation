package td.agricoles.agricol.DTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Member extends MemberInformation {
    private String id;
    private List<Member> referees;
}
