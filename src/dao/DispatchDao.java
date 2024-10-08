package dao;

import connection.HikariCpDBConnectionUtil;
import domain.DeliveryMan;
import domain.Dispatch;
import domain.DispatchType;
import domain.Outbound;
import domain.OutboundType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DispatchDao {

  // 배차 미할당 상태의 배차 정보 목록 조회
  public List<Dispatch> findNonAssignedDispatches() throws SQLException {
    String sql = "SELECT * FROM dispatch WHERE dispatchType = ?";
    List<Dispatch> dispatches = new ArrayList<>();
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, DispatchType.NONASSIGNED.name());
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          Dispatch dispatch = mapDispatch(rs);
          dispatches.add(dispatch);
        }
      }
    } catch (SQLException e) {
      System.out.println("배차 미할당 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
    return dispatches;
  }

  // 배차 할당 상태의 배차 정보 목록 조회
  public List<Dispatch> findAssignedDispatches() throws SQLException {
    String sql = "SELECT * FROM dispatch WHERE dispatchType = ?";
    List<Dispatch> dispatches = new ArrayList<>();
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, DispatchType.ASSIGNED.name());
      try (ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
          Dispatch dispatch = mapDispatch(rs);
          dispatches.add(dispatch);
        }
      }
    } catch (SQLException e) {
      System.out.println("배차 할당 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
    return dispatches;
  }

  // ID로 배차 정보 조회
  public Dispatch findAssignedDispatchById(int dispatchId) throws SQLException {
    String sql = "SELECT * FROM dispatch WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, dispatchId);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapDispatch(rs);
        }
      }
    } catch (SQLException e) {
      System.out.println("배차 정보를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
    return null; // 해당 ID의 배차 정보가 없는 경우
  }

  // 배차 정보 업데이트 (배차 상태 변경 및 배송기사 변경)
  public void updateDispatch(Dispatch dispatch) throws SQLException {
    String sql = "UPDATE dispatch SET dispatchType = ?, delivery_man_id = ? WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, dispatch.getDispatchType().name());
      pstmt.setInt(2, dispatch.getDeliveryMan().getId());
      pstmt.setInt(3, dispatch.getId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("배차 정보를 업데이트하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // 배차 취소 (배차 상태를 배차미할당으로 변경)
  public void cancelDispatch(int dispatchId) throws SQLException {
    String sql = "UPDATE dispatch SET dispatchType = ? WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setString(1, DispatchType.NONASSIGNED.name());
      pstmt.setInt(2, dispatchId);
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("배차를 취소하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // 배차 정보 매핑 (ResultSet을 Dispatch 객체로 변환)
  private Dispatch mapDispatch(ResultSet rs) throws SQLException {
    Dispatch dispatch = new Dispatch(
        new Outbound(
            rs.getInt("outbound_id"),  // Outbound ID
            rs.getString("buyer_name"),  // 구매자 이름
            rs.getInt("buyer_region_id"),  // 구매자 지역 ID
            rs.getString("buyer_city"),  // 구매자 도시
            rs.getString("buyer_address"),  // 구매자 상세 주소
            rs.getString("product_name"),  // 상품명
            rs.getInt("product_quantity"),  // 수량
            OutboundType.valueOf(rs.getString("outbound_type")),  // 출고 상태
            rs.getInt("business_man_id")  // 사업자 ID
        ),
        new DeliveryMan(
            rs.getInt("delivery_man_id"),  // 배송기사 ID
            rs.getString("delivery_man_num"),  // 배송기사 번호
            rs.getString("car_num")  // 차량 번호
        ),
        DispatchType.valueOf(rs.getString("dispatchType"))  // 배차 상태
    );
    dispatch.setId(rs.getInt("id"));  // Dispatch ID 매핑
    return dispatch;
  }
}