package dao;

import domain.Stock;
import dto.StockDto;
import dto.StockEditDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
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

    public int saveStock(Connection con, Stock stock) {
        String query = new StringBuilder()
                .append("INSERT INTO stock (product_id, business_man_id, width, height, quantity, manufactured_date, expiration_date) VALUES ")
                .append("(?, ?, ?, ?, ?, ?, ? ) ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, stock.getProduct().getId());
            pstmt.setInt(2, stock.getUser().getId());
            pstmt.setDouble(3, stock.getWidth());
            pstmt.setDouble(4, stock.getHeight());
            pstmt.setInt(5, stock.getQuantity());
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(stock.getManufacturedDate()));
            pstmt.setTimestamp(7, java.sql.Timestamp.valueOf(stock.getExpirationDate()));

            if (pstmt.executeUpdate() == 1) {
                con.commit();
                System.out.println("재고가 등록되었습니다.");
                return 1;
            } else {
                throw new RuntimeException("재고를 등록할 수 없습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateStock(Connection con, StockEditDto request) {
        String query = new StringBuilder()
                .append("UPDATE stock ")
                .append("SET quantity = ? , manufactured_date = ? , expiration_date = ? , mod_date = ? ")
                .append("WHERE id = ? ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, request.getQuantity());
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(request.getManufacturedDate()));
            pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(request.getExpirationDate()));
            pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(5, request.getId());

            if (pstmt.executeUpdate() == 1) {
                con.commit();
                System.out.println("재고 내역이 수정되었습니다.");
                return 1;
            } else {
                throw new RuntimeException("해당 재고를 수정할 수 없습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteStock(Connection con, int stockId) {
        String query = new StringBuilder()
                .append("DELETE FROM stock ")
                .append("WHERE id = ? ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, stockId);


            if (pstmt.executeUpdate() == 1) {
                con.commit();
                System.out.println("재고 내역이 삭제되었습니다.");
                return 1;
            } else {
                throw new RuntimeException("해당 재고를 삭제할 수 없습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
