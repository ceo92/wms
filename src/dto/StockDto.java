package dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockDto {
    private Integer id;
    private String productCode;
    private String productName;
    private String warehouseName;
    private String sectionName;
    private Double costPrice;
    private Integer quantity;
    private LocalDateTime manufacturedDate;
    private LocalDateTime expirationDate;
}
