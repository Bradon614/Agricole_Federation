package td.agricoles.agricol.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import td.agricoles.agricol.dto.enums.Bank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankAccount implements FinancialAccount {
    private String id;
    private String holderName;
    private Bank bankName;
    private Integer bankCode;
    private Integer bankBranchCode;
    private Integer bankAccountNumber;
    private Integer bankAccountKey;
    private Double amount;
}