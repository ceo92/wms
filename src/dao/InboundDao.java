package dao;

import domain.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InboundDao {


    public int save(Connection con, Inbound inbound) {
        String sql = "INSERT INTO inbound("
                + "user_id, vendor_id, warehouse_id, inbound_status, inbound_expected_date, inbound_completed_date, reg_date"
                + ") VALUES (?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inbound.getUser().getId());
            pstmt.setInt(2, inbound.getVendor().getId());
            pstmt.setInt(3, inbound.getWarehouse().getId());
            pstmt.setString(4, inbound.getStatus().getValue());
            pstmt.setDate(5, java.sql.Date.valueOf(inbound.getInboundExpectedDate()));
            pstmt.setDate(6, java.sql.Date.valueOf(inbound.getInboundCompletedDate()));
            pstmt.setTimestamp(7, java.sql.Timestamp.valueOf(inbound.getRegDate()));
            int affectedRows = pstmt.executeUpdate();
            if(affectedRows == 0) {
                throw new SQLException("입고 저장에 실패했습니다.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys != null && generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("입고 저장에 실패했습니다.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateExpectedDate(Connection con, int id, LocalDate date) {
        String sql = "UPDATE inbound SET inbound_expected_date = ? AND mod_date = now()"
                + " WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setDate(1, java.sql.Date.valueOf(date));
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateStatus(Connection con, int id, InboundStatus status) {
        String sql = "UPDATE inbound SET inbound_status = ? AND mod_date = now() "
                + " WHERE id = ?";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, status.getValue());
            pstmt.setInt(2, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean existById(Connection con, int id) {
        String sql = "SELECT EXISTS("
                + "SELECT 1 FROM inbound WHERE id = ?)";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                int result = rs.getInt(1);
                return result == 1;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Inbound> findById(Connection con, int id) {
        String sql = "SELECT * FROM inbound i, warehouse w, user u, vendor v"
                + " WHERE i.id = ? AND i.warehouse_id = w.id AND i.user_id = u.id AND i.vendor_id = v.id";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                Inbound inbound = Inbound.builder()
                        .id(rs.getInt("i.id"))
                        .warehouse(Warehouse.builder()
                                .id(rs.getInt("w.id"))
                                .manager(User.builder()
                                        .id(rs.getInt("w.manager_id"))
                                        .build())
                                .build())
                        .vendor(new Vendor(rs.getInt("v.id"), rs.getString("v.name")))
                        .user(User.builder()
                                .id(rs.getInt("u.id"))
                                .build())
                        .status(InboundStatus.valueOf(rs.getString("i.inbound_status")))
                        .inboundExpectedDate(rs.getDate("i.inbound_expected_date").toLocalDate())
                        .inboundCompletedDate(rs.getDate("i.inbound_completed_date").toLocalDate())
                        .regDate(rs.getTimestamp("i.reg_date").toLocalDateTime())
                        .modDate(rs.getTimestamp("i.mod_date").toLocalDateTime())
                        .build();
                return Optional.of(inbound);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Inbound> findInboundsByPeriod(Connection con, LocalDate fromDate, LocalDate toDate) {
        List<Inbound> inbounds = new ArrayList<>();
        String sql = "SELECT * FROM inbound i, warehouse w, user u, vendor v"
                + " WHERE i.reg_date BETWEEN ? AND ?"
                + " AND i.warehouse_id = w.id AND i.user_id = u.id AND i.vendor_id = v.id";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setDate(1, java.sql.Date.valueOf(fromDate));
            pstmt.setDate(2, java.sql.Date.valueOf(toDate));
            ResultSet rs = pstmt.executeQuery();
            while(rs != null && rs.next()) {
                Inbound inbound = Inbound.builder()
                        .id(rs.getInt("i.id"))
                        .warehouse(Warehouse.builder()
                                .id(rs.getInt("w.id"))
                                .manager(User.builder()
                                        .id(rs.getInt("w.manager_id"))
                                        .build())
                                .build())
                        .vendor(new Vendor(rs.getInt("v.id"), rs.getString("v.name")))
                        .user(User.builder()
                                .id(rs.getInt("u.id"))
                                .build())
                        .status(InboundStatus.valueOf(rs.getString("i.inbound_status")))
                        .inboundExpectedDate(rs.getDate("i.inbound_expected_date").toLocalDate())
                        .inboundCompletedDate(rs.getDate("i.inbound_completed_date").toLocalDate())
                        .regDate(rs.getTimestamp("i.reg_date").toLocalDateTime())
                        .modDate(rs.getTimestamp("i.mod_date").toLocalDateTime())
                        .build();
                inbounds.add(inbound);
            }
            return inbounds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
