package dao;

import domain.StockSection;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

public class StockSectionDao {

    public List<StockSection> findByWarehouseIdAndProductId(Connection con, int warehouseId, int productId) {
        String sql = "SELECT * FROM stock_section ss " +
                "JOIN stock s " +
                "ON ss.stock_id = s.id " +
                "WHERE ss.warehouse_id = ? AND s.product_id = ?";
        return null;
    }

    public void updateQuantity(Connection con, int id, int quantity) {

    }

    public void updateStockIdAndQuantity(Connection con, int id, int stockId, int quantity) {

    }

    public Optional<StockSection> findEmptySection(Connection con, int warehouseId) {
        String sql = "SELECT id FROM stock_section " +
                "WHERE warehouse_id = ? AND stock_id is NULL " +
                "ORDER BY id LIMIT 1";
        return null;
    }
}
