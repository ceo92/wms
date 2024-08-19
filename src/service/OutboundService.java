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
  public List<OutboundDto> viewNonApprovedOutbounds() {
    try {
      return outboundDao.findNonApprovedOutbounds();
    } catch (SQLException e) {
      System.out.println("미승인 출고 요청을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  // 출고 요청 승인 및 지연 처리
  public void processOutboundApproval(int outboundId, int availableStock) {
    try {
      Outbound outbound = outboundDao.findOutboundById(outboundId);
      if (outbound != null) {
        if (availableStock >= outbound.getProductQuantity()) {
          outboundDao.approveOutbound(outboundId);
          outboundDao.updateStock(outbound.getProductName(), outbound.getBusinessManId(), -outbound.getProductQuantity());
        } else {
          outboundDao.delayOutbound(outboundId);
        }
      }
    } catch (SQLException e) {
      System.out.println("출고 요청을 처리하는 중 오류가 발생했습니다: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  // 승인된 출고 리스트 조회
  public List<OutboundDto> viewApprovedOutbounds() {
    try {
      return outboundDao.findApprovedOutbounds();
    } catch (SQLException e) {
      System.out.println("승인된 출고 리스트를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  // 출고 상품 검색
  public List<OutboundDto> searchApprovedOutbounds(String productName) {
    try {
      return outboundDao.searchApprovedOutbounds(productName);
    } catch (SQLException e) {
      System.out.println("출고 상품을 검색하는 중 오류가 발생했습니다: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }

  // 출고 지시서 보기
  public List<DispatchDto> viewOutboundInstructions() {
    try {
      return outboundDao.findOutboundInstructions();
    } catch (SQLException e) {
      System.out.println("출고 지시서를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
