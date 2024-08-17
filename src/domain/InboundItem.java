package domain;

import lombok.Getter;

@Getter
public class InboundItem {

    private Integer id;
    private Product product;
    private Inbound inbound;
    private int requestQuantity;
    private int completeQuantity;
}
