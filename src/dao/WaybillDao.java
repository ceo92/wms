package dao;

import connection.HikariCpDBConnectionUtil;
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
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
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
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
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

  // ID로 운송장 정보 조회
  public Waybill findWaybillById(int waybillId) throws SQLException {
    String sql = "SELECT * FROM waybill WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, waybillId);
      try (ResultSet rs = pstmt.executeQuery()) {
        if (rs.next()) {
          return mapWaybill(rs);
        }
      }
    } catch (SQLException e) {
      System.out.println("운송장 정보를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
    return null; // 해당 ID의 운송장 정보가 없는 경우
  }

  // 운송장 정보 업데이트 (운송장에 연결된 배차 정보 변경)
  public void updateWaybill(Waybill waybill) throws SQLException {
    String sql = "UPDATE waybill SET dispatch_id = ? WHERE id = ?";
    try (Connection con = HikariCpDBConnectionUtil.getInstance().getConnection();
        PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, waybill.getDispatchId());
      pstmt.setInt(2, waybill.getId());
      pstmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("운송장 정보를 업데이트하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // 운송장 정보 매핑 (ResultSet을 Waybill 객체로 변환)
  private Waybill mapWaybill(ResultSet rs) {
    Waybill waybill = new Waybill();
    try {
      waybill.setId(rs.getInt("id"));  // Waybill ID 매핑
      waybill.setDispatchId(rs.getInt("dispatch_id"));  // Dispatch ID 매핑
    } catch (SQLException e) {
      System.out.println("Waybill 데이터를 매핑하는 중 오류가 발생했습니다: " + e.getMessage());
      throw new RuntimeException(e);
    }
    return waybill;
  }
}
