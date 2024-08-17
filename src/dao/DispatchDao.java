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

  // 배차 할당 상태의 배차 정보 목록 조회
  public List<Dispatch> findAssignedDispatches() throws SQLException {
    String sql = "SELECT * FROM dispatch WHERE dispatchType = ?";
    List<Dispatch> dispatches = new ArrayList<>();
    try (Connection con = Database.getConnection();
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
    try (Connection con = Database.getConnection();
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
}