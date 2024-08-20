package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WarehouseManager extends User{
  public WarehouseManager(Integer id , String name ,String phoneNumber ,String loginEmail ,String password , RoleType roleType , String passwordQuestion , String passwordAnswer){
    super(id, name, phoneNumber, loginEmail, password  ,roleType , passwordQuestion , passwordAnswer);
  }


  public WarehouseManager(String name ,String phoneNumber ,String loginEmail ,String password , RoleType roleType , String passwordQuestion , String passwordAnswer){
    super(name, phoneNumber, loginEmail, password  ,roleType , passwordQuestion , passwordAnswer);
  }




  public void changeBasicInformation(String name , String phoneNumber){
    setName(name);
    setPhoneNumber(phoneNumber);
  }

}
