package td.agricoles.agricol.DTOs;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class Collectivity {
    private String id;
    private String location;
    private CollectivityStructure structure;
    private List<Member> members;
}
