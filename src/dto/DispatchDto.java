package dto;

import domain.DispatchType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DispatchDto {
  private Integer id;
  private Integer outboundId;
  private Integer deliveryManId;
  private DispatchType dispatchType;
  private String carNum;
  private String productName;
  private int quantity;
}
