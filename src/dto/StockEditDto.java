package dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockEditDto {
    private Integer id;
    private Integer quantity;
    private LocalDateTime manufacturedDate;
    private LocalDateTime expirationDate;
}
