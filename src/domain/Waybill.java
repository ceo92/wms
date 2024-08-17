package domain;

public class Waybill {
  private Integer id;
  private Integer dispatchId;

  // Getter and Setter methods

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getDispatchId() {
    return dispatchId;
  }

  public void setDispatchId(Integer dispatchId) {
    this.dispatchId = dispatchId;
  }

  @Override
  public String toString() {
    return "Waybill{" +
        "id=" + id +
        ", dispatchId=" + dispatchId +
        '}';
  }

  public void setDispatch(Dispatch dispatch) {
    this.dispatchId = dispatch.getId();
  }

//  public Waybill getDispatch() {
//    return this.dispatchId;
//  }
}
