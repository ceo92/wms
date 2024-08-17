package dao;

import domain.InboundItem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class InboundItemDao {

    public boolean saveInboundItems(Connection con, int inboundId, List<InboundItem> items) {
        String sql = "INSERT INTO inbound_item(" +
                "product_id, inbound_id, request_quantity, complete_quantity" +
                ") VALUES (?, ?, ?, ?)";
        StringBuilder sb = new StringBuilder();
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }

    public Optional<InboundItem> findInboundItem(Connection con, int inboundItemId) {
        return null;
    }

    public List<InboundItem> findItemsByInboundId(Connection con, int inboundId) {
        return null;
    }

    public boolean increaseCompletedQuantity(Connection con, Integer id, int increaseQty) {
        return false;
    }
}
