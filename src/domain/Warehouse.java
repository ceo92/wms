package domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class Warehouse {

    private Integer id;
    private User manager;
    private WarehouseType type;
    private String code;
    private String name;
    private Region region;
    private String detailAddress;
    private String contact;
    private double maxCapacity;
    private double pricePerArea;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
