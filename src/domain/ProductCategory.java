package domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductCategory {
    private Integer id;
    private String name;
    private Integer parentId;
}
