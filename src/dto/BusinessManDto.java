package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BusinessManDto {
  private String name;
  private String phoneNumber;
  private String loginEmail;
  private String password;
  private String rePassword; //비밀번호 한번 더
  private String businessNum; //사업자번호
  private String businessName; //상호명


}
