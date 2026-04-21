package td.agricoles.agricol.DTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivity {
    private String location;
    private List<String> members;
    private boolean federationApproval;
    private CreateCollectivityStructure structure;
}
