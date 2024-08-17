package domain;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class Inbound {
    private Integer id;
    private LocalDate inboundExpectedDate;
    private LocalDate inboundCompletedDate;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private InboundStatus status;
    private User user;
    private Warehouse warehouse;
    private Vendor vendor;
}
