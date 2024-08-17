package domain;

import lombok.Data;

@Data
public class Dispatch {
  private Integer id;
  private Outbound outbound;
  private DeliveryMan delivery_man;
  private DispatchType dispatchType;

  public Dispatch(Outbound outbound, DeliveryMan delivery_man, DispatchType dispatchType) {
    this.outbound = outbound;
    this.delivery_man = delivery_man;
    this.dispatchType = dispatchType;
  }

  public Dispatch(int dispatchId, Object deliveryMan, Object dispatchType, DispatchType dispatchType1) {
  }
}
