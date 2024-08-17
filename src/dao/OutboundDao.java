package dao;

import domain.DispatchType;
import domain.Outbound;
import domain.OutboundType;
import dto.DispatchDto;
import dto.OutboundDto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OutboundDao {

  // 출고 요청 삽입
  public void insertOutbound(Outbound outbound) throws SQLException {
    String query = "INSERT INTO outbound (buyer_name, buyer_region_id, buyer_city, buyer_address, product_name, product_quantity, outbound_type, business_man_id) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(query)) {
      ps.setString(1, outbound.getBuyerName());
      ps.setInt(2, outbound.getBuyerRegionId());
      ps.setString(3, outbound.getBuyerCity());
      ps.setString(4, outbound.getBuyerAddress());
      ps.setString(5, outbound.getProductName());
      ps.setInt(6, outbound.getProductQuantity());
      ps.setString(7, outbound.getOutboundType().name());
      ps.setInt(8, outbound.getBusinessManId());
      ps.executeUpdate();
    } catch (SQLException e) {
      System.out.println("출고 요청을 삽입하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
  }

  // ID로 출고 요청 정보 조회
  public Outbound findOutboundById(int outboundId) throws SQLException {
    String sql = "SELECT * FROM outbound WHERE id = ?";
    try (Connection conn = Database.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {
      ps.setInt(1, outboundId);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return mapOutbound(rs);
        }
      }
    } catch (SQLException e) {
      System.out.println("출고 요청 정보를 조회하는 중 오류가 발생했습니다: " + e.getMessage());
      throw e;
    }
    return null; // 해당 ID의 출고 요청 정보가 없는 경우
  }
}
