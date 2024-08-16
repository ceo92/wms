package domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @Builder
public class DeliveryMan extends User{
  private String deliveryManNum; //배송기사번호
  private String carNum;
  public DeliveryMan(String deliveryManNum, String carNum, String name, String phoneNumber,
      String loginEmail, String password, RoleType roleType) {
    super(name, phoneNumber, loginEmail, password, roleType);
    this.carNum = carNum;
    this.deliveryManNum = deliveryManNum;
  }

  public DeliveryMan(Integer id, String name, String phoneNumber, String loginEmail,
      String password, RoleType roleType, String deliveryManNum, String carNum) {
    super(id, name, phoneNumber, loginEmail, password, roleType);
    this.deliveryManNum = deliveryManNum;
    this.carNum = carNum;
  }



  public void changeBasicInformation(String name, String phoneNumber , String deliveryManNum , String carNum) {
    setName(name);
    setPhoneNumber(phoneNumber);
    setDeliveryManNum(deliveryManNum);
    setCarNum(carNum);
  }
}
