package dao;

import domain.Product;
import domain.ProductCategory;
import domain.Vendor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductDao {

    public Optional<Product> findById(Connection con, Integer id) {
        String query = new StringBuilder()
                .append("SELECT * FROM product WHERE id = ? ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs != null && rs.next()) {
                    return Optional.of(Product.builder()
                            .id(rs.getInt("id"))
                            .productCategory(ProductCategory.builder()
                                    .id(rs.getInt("category_id"))
                                    .build())
                            .code(rs.getString("code"))
                            .name(rs.getString("name"))
                            .costPrice(rs.getDouble("cost_price"))
                            .manufacturer(rs.getString("manufacturer"))
                            .regDate(rs.getTimestamp("reg_date").toLocalDateTime())
                            .modDate(rs.getTimestamp("mod_date").toLocalDateTime())
                            .build());
                } else {
                    return Optional.empty();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
