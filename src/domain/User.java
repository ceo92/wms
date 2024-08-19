package domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 공통 User 정의 , 배송기사와 회원 정도만 상속받음 , 총관리자(ADMIN) , 창고관리자(WarehouseAdmin)는 해당 User 폼에서 모든 데이터 받을 수 있으므로 별도로 extends 하지 않았음
 * 배송기사(DeliveryMan) , 회원(Member)은 배송기사번호 , 사업자번호를 받아야하므로 클래스 생성해서 extends 하였음
 */
// DB와 자바는 매핑 방법이 다르므로

@NoArgsConstructor
@Getter @Setter(AccessLevel.PROTECTED)
public class User {
  private Integer id;//PK
  private String name; //이름
  private String phoneNumber; //핸드폰 번호
  private String loginEmail; //로그인 아이디(이메일 형식)
  private String password; //비밀번호 SHA-256
  private String passwordQuestion;
  private String passwordAnswer;
  private RoleType roleType; //권한 및 DTYPE


  public User(Integer id, String name, String phoneNumber, String loginEmail, String password, RoleType roleType , String passwordQuestion , String passwordAnswer) {
    this.id = id;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.loginEmail = loginEmail;
    this.password = password;
    this.roleType = roleType;
    this.passwordQuestion = passwordQuestion;
    this.passwordAnswer = passwordAnswer;
  }


  public User(String name, String phoneNumber, String loginEmail, String password, RoleType roleType , String passwordQuestion , String passwordAnswer) {
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.loginEmail = loginEmail;
    this.password = password;
    this.roleType = roleType;
    this.passwordQuestion = passwordQuestion;
    this.passwordAnswer = passwordAnswer;
  }

  public void changePassword(String password){
    setPassword(password); //this.password = password
  }

  public void changeBasicInformation(String name , String phoneNumber){
    this.name = name;
    this.phoneNumber = phoneNumber;
  }
}
