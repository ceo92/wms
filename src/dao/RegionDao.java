package dao;

import domain.Region;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegionDao {
  public List<Region> findAll(Connection con) {
    String sql = "SELECT * FROM region";
    List<Region> regions = new ArrayList<>();
    try(PreparedStatement stmt = con.prepareStatement(sql)) {
      ResultSet rs = stmt.executeQuery(sql);
      while(rs != null && rs.next()) {
        Region region = Region.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .code(rs.getString("code"))
            .parentId(rs.getObject("parent_id" , Integer.class))
            .build();
        regions.add(region);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return regions;
  }

  public Optional<Region> findById(Connection con, int id) {
    String sql = "SELECT * FROM region WHERE id = ?";
    try(PreparedStatement pstmt = con.prepareStatement(sql)) {
      pstmt.setInt(1, id);
      ResultSet rs = pstmt.executeQuery();
      if(rs != null && rs.next()) {
        Region region = Region.builder()
            .id(rs.getInt("id"))
            .name(rs.getString("name"))
            .code(rs.getString("code"))
            .parentId(rs.getObject("parent_id" , Integer.class))
            .build();
        return Optional.of(region);
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
