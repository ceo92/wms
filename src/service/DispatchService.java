package service;

import dao.DispatchDao;
import domain.DeliveryMan;
import domain.Dispatch;
import domain.DispatchType;
import java.sql.SQLException;
import java.util.List;

// DispatchService: 배차 관리 서비스
public class DispatchService {

  private final DispatchDao dispatchDao = new DispatchDao();

  // 배차등록: 주문자의 지역ID와 배송기사의 지역ID가 같은 경우 배차 상태를 배차할당으로 변경
  public void registerDispatch() throws SQLException {
    try {
      List<Dispatch> nonAssignedDispatches = dispatchDao.findNonAssignedDispatches();
      for (Dispatch dispatch : nonAssignedDispatches) {
        if (dispatch.getOutbound().getBuyerRegionId().equals(dispatch.getDelivery_man().getRegion_id())) {
          dispatch.setDispatchType(DispatchType.ASSIGNED);
          dispatchDao.updateDispatch(dispatch);
        }
      }
    } catch (SQLException e) {
      System.out.println("배차 등록 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }
}
