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
}
