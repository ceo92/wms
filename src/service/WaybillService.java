package service;

import dao.WaybillDao;
import domain.DeliveryMan;
import domain.Waybill;
import java.sql.SQLException;
import java.util.List;

// WaybillService: 운송장 관리 서비스
public class WaybillService {

  private final WaybillDao waybillDao = new WaybillDao();

  // 운송장 등록: 필요한 정보를 바탕으로 운송장 생성
  public void registerWaybill(Waybill waybill) throws SQLException {
    try {
      waybillDao.createWaybill(waybill);
    } catch (SQLException e) {
      System.out.println("운송장을 등록하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }


  // 운송장 리스트 조회: 모든 운송장 정보 조회
  public List<Waybill> viewAllWaybills() throws SQLException {
    try {
      return waybillDao.findAllWaybills();
    } catch (SQLException e) {
      System.out.println("운송장 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // 운송장 수정: 운송장 정보 (기사님) 수정
  public void modifyWaybill(int waybillId, DeliveryMan newDeliveryMan) throws SQLException {
    try {
      Waybill waybill = waybillDao.findWaybillById(waybillId);
      if (waybill != null) {
        waybill.getDispatchId().setDelivery_man(newDeliveryMan);
        waybillDao.updateWaybill(waybill);
      }
    } catch (SQLException e) {
      System.out.println("운송장 정보를 수정하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }
}
