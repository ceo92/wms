package dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TotalAssetDto {
    private Double sumExpense;
    private Double sumProfit;
    private Double netProfit;
    private Double netProfitPer;
}
