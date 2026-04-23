package td.agricoles.agricol.dto.request;

import jdk.jfr.Frequency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.enums.FeeFrequency;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMembershipFee {
    private LocalDate eligibleFrom;
    private FeeFrequency frequency;
    private Double amount;
    private String label;
}
