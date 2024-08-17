package dao;

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
    try (Connection con = Database.getConnection();
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
}