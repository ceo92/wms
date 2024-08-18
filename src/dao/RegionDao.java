package dao;

import domain.Region;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RegionDao {

    public List<Region> findAll(Connection con) {
        String sql = "SELECT * FROM region";
        List<Region> regions = new ArrayList<>();
        try(Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while(rs != null && rs.next()) {
                Region region = Region.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .code(rs.getString("code"))
                        .parentId(rs.getInt("parent_id"))
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
            ResultSet rs = pstmt.executeQuery(sql);
            if(rs != null && rs.next()) {
                Region region = Region.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .code(rs.getString("code"))
                        .parentId(rs.getInt("parent_id"))
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
