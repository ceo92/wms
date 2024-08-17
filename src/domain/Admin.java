package domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Admin extends User{
  public Admin(String name ,String phoneNumber ,String loginEmail ,String password){
    super(name , phoneNumber , loginEmail , password);
  }
}
