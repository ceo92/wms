package dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InboundItemDto {

    private int productId;
    private int requestQuantity;

}
