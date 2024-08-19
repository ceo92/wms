package domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 사업자 엔티티
 */
@Getter @Setter(AccessLevel.PRIVATE) //설정자 닫아놔야됨 , 무분별한 수정 안되게끔
public class BusinessMan extends User {
  private String businessNum; //사업자번호 , PK가 아님
  private String businessName; //상호명

  public BusinessMan(Integer id , String name, String phoneNumber,
      String loginEmail, String password, RoleType roleType , String passwordQuestion ,
      String passwordAnswer , String businessNum, String businessName) {
    super(id , name, phoneNumber, loginEmail, password  ,roleType , passwordQuestion , passwordAnswer);
    this.businessNum = businessNum;
    this.businessName = businessName;
  }


  public BusinessMan(String name, String phoneNumber,
      String loginEmail, String password, RoleType roleType , String passwordQuestion ,
      String passwordAnswer , String businessNum, String businessName) {
    super(name, phoneNumber, loginEmail, password  ,roleType , passwordQuestion , passwordAnswer);
    this.businessNum = businessNum;
    this.businessName = businessName;
  }






  public void changeBasicInformation(String name , String phoneNumber , String businessNum , String businessName){
    super.changeBasicInformation(name , phoneNumber);
    setBusinessNum(businessNum);
    setBusinessName(businessName);
  }



}
