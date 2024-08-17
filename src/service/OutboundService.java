package service;

import dao.OutboundDao;
import domain.Outbound;
import dto.DispatchDto;
import dto.OutboundDto;
import java.sql.SQLException;
import java.util.List;

// OutboundService: 출고 요청 및 출고 관리 서비스
public class OutboundService {

  private final OutboundDao outboundDao = new OutboundDao();

  // 출고 요청 등록
  public void requestOutbound(Outbound outbound) throws SQLException {
    try {
      outboundDao.insertOutbound(outbound);
    } catch (SQLException e) {
      System.out.println("출고 요청을 등록하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // 미승인 출고 요청 조회
  public List<OutboundDto> viewNonApprovedOutbounds() throws SQLException {
    try {
      return outboundDao.findNonApprovedOutbounds();
    } catch (SQLException e) {
      System.out.println("미승인 출고 요청을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }
}
