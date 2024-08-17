package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WaybillDto {
  private Integer id;
  private Integer dispatchId;
  private String productName;
  private int productQuantity;
  private String buyerName;
  private String buyerRegion;
  private String buyerAddress;
  private String carNum;
  private String creationDate;
  private String estimatedArrivalDate;
  private double chargeAmount;
}
