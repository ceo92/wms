package dao;

import domain.ProductCategory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductCategoryDao {

    public List<ProductCategory> findByParentIdIsNull(Connection con) {
        String query = new StringBuilder()
                .append("SELECT id, name ")
                .append("FROM product_category ")
                .append("WHERE parent_id IS NULL ").toString();

        List<ProductCategory> categories = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                categories.add(ProductCategory.builder()
                        .id(rs.getInt("id"))
                        .name(rs.getString("name"))
                        .build());
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductCategory> findByParentId(Connection con, Integer parentId) {
        String query = new StringBuilder()
                .append("SELECT id, name ")
                .append("FROM product_category ")
                .append("WHERE parent_id = ? ").toString();

        List<ProductCategory> categories = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, parentId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    categories.add(ProductCategory.builder()
                            .id(rs.getInt("id"))
                            .name(rs.getString("name"))
                            .build());
                }
            }
            return categories;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
