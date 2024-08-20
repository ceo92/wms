package domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Stock {
    private Integer id;
    private Product product;
    private User user;
    private Double width;
    private Double height;
    private Integer quantity;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private LocalDateTime manufacturedDate;
    private LocalDateTime expirationDate;
}
