package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DeliveryMan extends User{
  private String deliveryManNum; //배송기사번호
  private String carNum;
  public DeliveryMan(String deliveryManNum , String carNum , String name ,String phoneNumber ,String loginEmail ,String password){
    super(name , phoneNumber , loginEmail , password);
    this.carNum = carNum;
    this.deliveryManNum = deliveryManNum;
  }

}
