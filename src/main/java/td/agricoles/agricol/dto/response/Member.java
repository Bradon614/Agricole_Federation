package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.MemberInformation;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Member extends MemberInformation {
    private String id;
    private List<Member> referees;

}
