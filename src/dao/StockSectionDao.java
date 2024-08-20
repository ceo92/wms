package dao;

import domain.StockSection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StockSectionDao {

    public List<StockSection> findAllByWarehouseIdAndProductId(Connection con, int warehouseId, int productId) {
        String sql = "SELECT * FROM stock_section ss" +
                " JOIN stock s" +
                " ON ss.stock_id = s.id" +
                " WHERE ss.warehouse_id = ? AND s.product_id = ?";
        List<StockSection> stockSections = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, warehouseId);
            pstmt.setInt(2, productId);
            ResultSet rs = pstmt.executeQuery();
            while(rs != null && rs.next()) {

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean updateQuantity(Connection con, int id, int quantity) {
        String sql = "UPDATE stock_section SET quantity = ? WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateStockIdAndQuantity(Connection con, int id, int stockId, int quantity) {
        String sql = "UPDATE stock_section SET stock_id = ?, quantity = ? WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, stockId);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<StockSection> findEmptySection(Connection con, int warehouseId) {
        String sql = "SELECT id FROM stock_section" +
                " WHERE warehouse_id = ? AND stock_id IS NULL" +
                " ORDER BY id LIMIT 1";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, warehouseId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    public boolean deleteStockSection(Connection con, Integer stockId) {
        String query = "DELETE FROM stock_section WHERE stock_id = ?";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, stockId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
