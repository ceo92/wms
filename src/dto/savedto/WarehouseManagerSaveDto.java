package dto.savedto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WarehouseManagerSaveDto {
  private String name; //사업자 이름
  private String phoneNumber; //핸드폰 번호
  private String loginEmail; //로그인 아이디(이메일 형식)
  private String password; //비밀번호 SHA-256
  private String rePassword; //비밀번호 한번 더
  private String passwordQuestion;
  private String passwordAnswer;

}
//ADMIN은 주입받을 필요 없음 , 어차피 그냥 내부에서 초기화해주고 저장만 해주면 되니 ㅇㅇ
