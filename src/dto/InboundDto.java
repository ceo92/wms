package dto;


import domain.*;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public class InboundDto {

    private int userId;
    private int warehouseId;
    private int vendorId;
    private LocalDate inboundExpectedDate;

    public Inbound toCreateInbound() {
        return Inbound.builder()
                .user(new User(userId))
                .warehouse(Warehouse.builder().id(warehouseId).build())
                .vendor(new Vendor(vendorId))
                .inboundExpectedDate(inboundExpectedDate)
                .inboundCompletedDate(null)
                .regDate(LocalDateTime.now())
                .status(InboundStatus.PENDING)
                .build();
    }
}
