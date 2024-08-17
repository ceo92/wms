package dao;

import domain.Waybill;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WaybillDao {

  // 운송장 생성 (새로운 운송장 추가)
  public void createWaybill(Waybill waybill) throws SQLException {
    String sql = "INSERT INTO waybill (dispatch_id) VALUES (?)";
    try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, waybill.getDispatchId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("운송장을 생성하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // 모든 운송장 정보 조회
  public List<Waybill> findAllWaybills() throws SQLException {
    String sql = "SELECT * FROM waybill";
    List<Waybill> waybills = new ArrayList<>();
    try (Connection con = Database.getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery()) {
      while (rs.next()) {
        Waybill waybill = mapWaybill(rs);
        waybills.add(waybill);
      }
    } catch (SQLException e) {
      System.out.println("운송장 목록을 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
    return waybills;
  }
}