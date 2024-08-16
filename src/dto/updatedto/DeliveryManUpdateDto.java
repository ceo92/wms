package dto.updatedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryManUpdateDto {
  private String name; //배송기사 이름
  private String phoneNumber; //핸드폰 번호
  private String deliveryManNum; //배송기사번호
  private String carNum;
}
