package td.agricoles.agricol.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.MemberIdentifier;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivity {
    private String location;
    private List<MemberIdentifier> members;
    private boolean federationApproval;
    private CreateCollectivityStructure structure;
}
