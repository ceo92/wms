package domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class Expense {
    private Integer id;
    private Warehouse warehouse;
    private LocalDate expenseDate;
    private ExpenseCategory expenseCategory;
    private Double expenseAmount;
    private String description;
    private String paymentMethod;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
