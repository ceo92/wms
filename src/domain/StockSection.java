package domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StockSection {

    private Integer id;
    private Warehouse warehouse;
    private Stock stock;
    private String name;
    private double width;
    private double height;
    private int quantity;

}
