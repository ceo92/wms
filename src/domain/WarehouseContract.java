package domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WarehouseContract {

    private Integer id;
    private Warehouse warehouse;
    private BusinessMan businessMan;
    private double capacity;
    private LocalDate contractDate;
    private int contractMonth;
}
