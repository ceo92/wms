package dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExpenseSaveDto {
    private Integer warehouseId;
    private LocalDate expenseDate;
    private Integer categoryId;
    private Double expenseAmount;
    private String description;
    private String paymentMethod;
}
