package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WarehouseManager extends User{
  public WarehouseManager(String name ,String phoneNumber ,String loginEmail ,String password){
    super(name , phoneNumber , loginEmail , password);
  }

}
