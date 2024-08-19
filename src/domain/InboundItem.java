package domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InboundItem {

    private Integer id;
    private Product product;
    private Inbound inbound;
    private int requestQuantity;
    private int completeQuantity;
}
