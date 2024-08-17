package dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExpenseEditDto {

    private Integer id;
    private LocalDate expenseDate;
    private Integer categoryId;
    private Double expenseAmount;
    private String description;
    private String paymentMethod;
}
