package td.agricoles.agricol.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(CashAccount.class),
        @JsonSubTypes.Type(MobileBankingAccount.class),
        @JsonSubTypes.Type(BankAccount.class)
})
public interface FinancialAccount {
}