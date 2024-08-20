package dao;

import domain.Product;
import domain.ProductCategory;
import domain.Vendor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
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

    public List<Product> findByVendorId(Connection con, int vendorId) {
        String query = new StringBuilder()
                .append("SELECT * FROM product p, product_category pc, vendor v WHERE p.category_id = pc.id AND p.vendor_id = v.id AND p.vendor_id = ? ").toString();
        List<Product> productList = new ArrayList<>();
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, vendorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs != null && rs.next()) {
                    Product product = Product.builder()
                            .id(rs.getInt("p.id"))
                            .productCategory(ProductCategory.builder()
                                    .id(rs.getInt("p.category_id"))
                                    .name(rs.getString("pc.name"))
                                    .build())
                            .code(rs.getString("p.code"))
                            .name(rs.getString("p.name"))
                            .costPrice(rs.getDouble("p.cost_price"))
                            .manufacturer(rs.getString("p.manufacturer"))
                            .regDate(rs.getTimestamp("p.reg_date").toLocalDateTime())
                            .vendor(new Vendor(rs.getInt("v.id"), rs.getString("v.name")))
                            .build();
                    productList.add(product);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return productList;
    }

    public List<Vendor> findAllVendors(Connection con) {
        String query = new StringBuilder()
                .append("SELECT * FROM vendor").toString();
        List<Vendor> vendors = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            try (ResultSet rs = stmt.executeQuery(query)) {
                while (rs != null && rs.next()) {
                    Vendor vendor = new Vendor(rs.getInt("id"), rs.getString("name"));
                    vendors.add(vendor);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return vendors;
    }
}
