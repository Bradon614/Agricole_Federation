package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.enums.PaymentMode;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectivityTransaction {
    private String id;
    private LocalDate creationDate;
    private Double amount;
    private PaymentMode paymentMode;
    private FinancialAccount accountCredited;
    private Member memberDebited;
}