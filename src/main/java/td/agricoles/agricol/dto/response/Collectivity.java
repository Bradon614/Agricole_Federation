package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.CollectivityInformation;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Collectivity extends CollectivityInformation {
    private String id;
    private String location;
    private CollectivityStructure structure;
    private List<Member> members;
}