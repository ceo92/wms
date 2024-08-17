package domain;

import lombok.Data;

@Data
public class Outbound {
  private Integer id;
  private String buyerName;
  private Integer buyerRegionId;
  private String buyerCity;
  private String buyerAddress;
  private String productName;
  private Integer productQuantity;
  private OutboundType outboundType;
  private Integer businessManId;

  public Outbound(String buyerName, Integer buyerRegionId, String buyerCity, String buyerAddress, String productName, Integer productQuantity, OutboundType outboundType, Integer businessManId) {
    this.buyerName = buyerName;
    this.buyerRegionId = buyerRegionId;
    this.buyerCity = buyerCity;
    this.buyerAddress = buyerAddress;
    this.productName = productName;
    this.productQuantity = productQuantity;
    this.outboundType = outboundType;
    this.businessManId = businessManId;
  }

  public Outbound() {}

  public Outbound(int outboundId, String buyerName, int buyerRegionId, String buyerCity, String buyerAddress, String productName, int productQuantity, OutboundType outboundType, int businessManId) {
    this.id = outboundId;
    this.buyerName = buyerName;
    this.buyerRegionId = buyerRegionId;
    this.buyerCity = buyerCity;
    this.buyerAddress = buyerAddress;
    this.productName = productName;
    this.productQuantity = productQuantity;
    this.outboundType = outboundType;
    this.businessManId = businessManId;
  }
}
