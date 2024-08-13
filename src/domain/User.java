package domain;

import lombok.Getter;
import lombok.Setter;

/**
 * 공통 User 정의 , 배송기사와 회원 정도만 상속받음 , 총관리자(ADMIN) , 창고관리자(WarehouseAdmin)는 해당 User 폼에서 모든 데이터 받을 수 있으므로 별도로 extends 하지 않았음
 * 배송기사(DeliveryMan) , 회원(Member)은 배송기사번호 , 사업자번호를 받아야하므로 클래스 생성해서 extends 하였음
 */
// DB와 자바는 매핑 방법이 다르므로

@Getter @Setter
public abstract class User {
  private Integer id;//PK
  private String name; //이름
  private String phoneNumber; //핸드폰 번호
  private String loginEmail; //로그인 아이디(이메일 형식)
  private String password; //비밀번호 SHA-256
  private String dType; //DTYPE 어느 자식의 데이터인지를 식별하기 위한 값
  private RoleType roleType; //권한

  public User(String name , String phoneNumber , String loginEmail , String password){
    this.name =name;
    this.phoneNumber = phoneNumber;
    this.loginEmail = loginEmail;
    this.password = password;
  }



}
