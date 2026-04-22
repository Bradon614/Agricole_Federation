package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.enums.MobileBankingService;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileBankingAccount implements FinancialAccount {
    private String id;
    private String holderName;
    private MobileBankingService mobileBankingService;
    private Integer mobileNumber;
    private Double amount;
}