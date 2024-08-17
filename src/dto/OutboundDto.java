package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OutboundDto {
	private String buyerName;
	private Integer buyerRegionId;
	private String buyerCity;
	private String buyerAddress;
	private String productName;
	private Integer productQuantity;
	private String outboundType;
}