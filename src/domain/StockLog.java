package domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockLog {
    private Integer id;
    private LocalDateTime modDate;
    private String code;
    private String name;
    private Integer previousQuantity;
    private Integer afterQuantity;
}
