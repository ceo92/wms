package dao;

import domain.*;
import dto.InboundItemDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InboundItemDao {

    public boolean saveInboundItems(Connection con, int inboundId, List<InboundItemDto> items) {
        if(items == null || items.size() == 0) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO inbound_item(")
                .append("inbound_id, product_id, request_quantity, complete_quantity")
                .append(") VALUES (?, ?, ?, ?)");
        sb.append(", (?, ?, ?, ?)".repeat(items.size() - 1));
        try(PreparedStatement pstmt = con.prepareStatement(sb.toString())) {
            int cnt = 1;
            for(InboundItemDto item: items) {
                pstmt.setInt(cnt++, inboundId);
                pstmt.setInt(cnt++, item.getProductId());
                pstmt.setInt(cnt++, item.getRequestQuantity());
                pstmt.setInt(cnt++, 0);
            }
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == items.size();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<InboundItem> findById(Connection con, int id) {
        String sql = "SELECT * FROM inbound_item ii, inbound i, product p, product_category pc, vendor v" +
                " WHERE ii.id = ? AND ii.inbound_id = i.id AND ii.product_id = p.id AND p.category_id = pc.id AND v.id = p.vendor_id";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                InboundItem inboundItem = InboundItem.builder()
                        .id(rs.getInt("ii.id"))
                        .product(Product.builder()
                                .id(rs.getInt("p.id"))
                                .code(rs.getString("p.code"))
                                .name(rs.getString("p.name"))
                                .costPrice(rs.getDouble("p.cost_price"))
                                .manufacturer(rs.getString("p.manufacturer"))
                                .build())
                        .inbound(Inbound.builder()
                                .vendor(new Vendor(rs.getInt("v.id"), rs.getString("v.name")))
                                .user(new User(rs.getInt("i.user_id")))
                                .warehouse(Warehouse.builder()
                                        .id(rs.getInt("i.warehouse_id"))
                                        .build())
                                .status(InboundStatus.valueOf(rs.getString("i.inbound_status")))
                                .build())
                        .completeQuantity(rs.getInt("ii.complete_quantity"))
                        .requestQuantity(rs.getInt("ii.request_quantity"))
                        .build();
                return Optional.of(inboundItem);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<InboundItem> findItemsByInboundId(Connection con, int inboundId) {
        String sql = "SELECT * FROM inbound_item ii, inbound i, product p, product_category pc, vendor v" +
                " WHERE i.id = ? AND ii.inbound_id = i.id AND ii.product_id = p.id AND p.category_id = pc.id AND v.id = p.vendor_id";
        List<InboundItem> items = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, inboundId);
            ResultSet rs = pstmt.executeQuery();
            while(rs != null && rs.next()) {
                InboundItem inboundItem = InboundItem.builder()
                        .id(rs.getInt("ii.id"))
                        .product(Product.builder()
                                .id(rs.getInt("p.id"))
                                .code(rs.getString("p.code"))
                                .name(rs.getString("p.name"))
                                .costPrice(rs.getDouble("p.cost_price"))
                                .manufacturer(rs.getString("p.manufacturer"))
                                .build())
                        .inbound(Inbound.builder()
                                .vendor(new Vendor(rs.getInt("v.id"), rs.getString("v.name")))
                                .user(new User(rs.getInt("i.user_id")))
                                .warehouse(Warehouse.builder()
                                        .id(rs.getInt("i.warehouse_id"))
                                        .build())
                                .status(InboundStatus.valueOf(rs.getString("i.inbound_status")))
                                .build())
                        .completeQuantity(rs.getInt("ii.complete_quantity"))
                        .requestQuantity(rs.getInt("ii.request_quantity"))
                        .build();
                items.add(inboundItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return items;
    }

    public boolean increaseCompletedQuantity(Connection con, Integer id, int increaseQty) {
        String sql = "UPDATE inbound_item SET complete_quantity = complete_quantity + ? WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, increaseQty);
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean checkInboundCompleted(Connection con, int id) {
        String sql = "SELECT EXISTS(" +
                "SELECT 1 FROM inbound_item WHERE inbound_id = ? AND complete_quantity < request_quantity)";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                int result = rs.getInt(1);
                return result == 0;
            } else {
                throw new SQLException("InboundItemDao.checkInboundCompleted");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateRequestQuantity(Connection con, int id, int requestQuantity) {
        String sql = "UPDATE inbound_item SET request_quantity = ?"
                + " WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, requestQuantity);
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteInboundItem(Connection con, int itemId) {
        String sql = "DELETE FROM inbound_item WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, itemId);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
