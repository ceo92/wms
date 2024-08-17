package domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Product {
    private Integer id;
    private ProductCategory productCategory;
    private String code;
    private String name;
    private Double costPrice;
    private String manufacturer;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private Vendor vendor;
}
