package td.agricoles.agricol.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCollectivity {
    private String location;
    private List<String> members;
    private boolean federationApproval;
    private CreateCollectivityStructure structure;
}