package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//컨트롤러에서 주입받는 용도
@Data
@AllArgsConstructor
public class DeliveryManDto {
  private String name; //배송기사 이름
  private String phoneNumber; //핸드폰 번호
  private String loginEmail; //로그인 아이디(이메일 형식)
  private String password; //비밀번호 SHA-256
  private String rePassword; //비밀번호 한번 더
  private String deliveryManNum; //배송기사번호
  private String carNum;
}
