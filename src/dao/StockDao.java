package dao;

import dto.StockDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockDao {
    public List<StockDto> findAll(Connection con) {
        String query = new StringBuilder()
                .append("SELECT s.id, p.code, p.name, w.name, ss.name, cost_price, quantity, manufactured_date, expiration_date ")
                .append("FROM stock s ")
                .append("JOIN product p ON s.product_id = p.id ")
                .append("JOIN stock_section ss ON s.id = ss.id ")
                .append("JOIN warehouse w ON ss.warehouse_id = w.id ")
                .append("ORDER BY s.id ").toString();

        List<StockDto> stocks = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                stocks.add(StockDto.builder()
                        .id(rs.getInt("s.id"))
                        .productCode(rs.getString("p.code"))
                        .productName(rs.getString("p.name"))
                        .warehouseName(rs.getString("w.name"))
                        .sectionName(rs.getString("ss.name"))
                        .costPrice(rs.getDouble("cost_price"))
                        .quantity(rs.getInt("quantity"))
                        .manufacturedDate(rs.getTimestamp("manufactured_date").toLocalDateTime())
                        .expirationDate(rs.getTimestamp("expiration_date").toLocalDateTime())
                        .build());
            }
            return stocks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockDto> findByManagerId(Connection con, Integer userId) {
        String query = new StringBuilder()
                .append("SELECT s.id, p.code, p.name, w.name, ss.name, cost_price, quantity, manufactured_date, expiration_date ")
                .append("FROM stock s ")
                .append("JOIN product p ON s.product_id = p.id ")
                .append("JOIN stock_section ss ON s.id = ss.id ")
                .append("JOIN warehouse w ON ss.warehouse_id = w.id ")
                .append("WHERE manager_id = ? ")
                .append("ORDER BY s.id ").toString();

        List<StockDto> stocks = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stocks.add(StockDto.builder()
                            .id(rs.getInt("s.id"))
                            .productCode(rs.getString("p.code"))
                            .productName(rs.getString("p.name"))
                            .warehouseName(rs.getString("w.name"))
                            .sectionName(rs.getString("ss.name"))
                            .costPrice(rs.getDouble("cost_price"))
                            .quantity(rs.getInt("quantity"))
                            .manufacturedDate(rs.getTimestamp("manufactured_date").toLocalDateTime())
                            .expirationDate(rs.getTimestamp("expiration_date").toLocalDateTime())
                            .build());
                }
            }
            return stocks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockDto> findByBusinessManId(Connection con, Integer userId) {
        String query = new StringBuilder()
                .append("SELECT s.id, p.code, p.name, w.name, ss.name, cost_price, quantity, manufactured_date, expiration_date ")
                .append("FROM stock s ")
                .append("JOIN product p ON s.product_id = p.id ")
                .append("JOIN stock_section ss ON s.id = ss.id ")
                .append("JOIN warehouse w ON ss.warehouse_id = w.id ")
                .append("WHERE business_man_id = ? ")
                .append("ORDER BY s.id ").toString();

        List<StockDto> stocks = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stocks.add(StockDto.builder()
                            .id(rs.getInt("s.id"))
                            .productCode(rs.getString("p.code"))
                            .productName(rs.getString("p.name"))
                            .warehouseName(rs.getString("w.name"))
                            .sectionName(rs.getString("ss.name"))
                            .costPrice(rs.getDouble("cost_price"))
                            .quantity(rs.getInt("quantity"))
                            .manufacturedDate(rs.getTimestamp("manufactured_date").toLocalDateTime())
                            .expirationDate(rs.getTimestamp("expiration_date").toLocalDateTime())
                            .build());
                }
            }
            return stocks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<StockDto> findByParentId(Connection con, Integer categoryId) {
        String query = new StringBuilder()
                .append("SELECT s.id, p.code, p.name, w.name, ss.name, cost_price, quantity, manufactured_date, expiration_date ")
                .append("FROM stock s ")
                .append("JOIN product p ON s.product_id = p.id ")
                .append("JOIN stock_section ss ON s.id = ss.id ")
                .append("JOIN warehouse w ON ss.warehouse_id = w.id ")
                .append("JOIN product_category pc ON p.category_id = pc.id ")
                .append("WHERE pc.id = ? OR pc.parent_id = ? OR pc.parent_id IN ( ")
                .append("SELECT id FROM product_category WHERE parent_id = ? ) ")
                .append("ORDER BY s.id ").toString();

        List<StockDto> stocks = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, categoryId);
            pstmt.setInt(2, categoryId);
            pstmt.setInt(3, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stocks.add(StockDto.builder()
                            .id(rs.getInt("s.id"))
                            .productCode(rs.getString("p.code"))
                            .productName(rs.getString("p.name"))
                            .warehouseName(rs.getString("w.name"))
                            .sectionName(rs.getString("ss.name"))
                            .costPrice(rs.getDouble("cost_price"))
                            .quantity(rs.getInt("quantity"))
                            .manufacturedDate(rs.getTimestamp("manufactured_date").toLocalDateTime())
                            .expirationDate(rs.getTimestamp("expiration_date").toLocalDateTime())
                            .build());
                }
            }
            return stocks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
