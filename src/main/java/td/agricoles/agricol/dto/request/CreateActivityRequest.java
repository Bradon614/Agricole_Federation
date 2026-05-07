package td.agricoles.agricol.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateActivityRequest {
    private String type;
    private String description;
    private LocalDate scheduledDate;
    private boolean isMandatory;
}