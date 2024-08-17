package dao;

import domain.StockLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockLogDao {

    public List<StockLog> findAll(Connection con) {
        String query = new StringBuilder()
                .append("SELECT * ")
                .append("FROM stock_log ").toString();

        List<StockLog> stockLogs = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                stockLogs.add(StockLog.builder()
                        .id(rs.getInt("id"))
                        .modDate(rs.getTimestamp("mod_date").toLocalDateTime())
                        .code(rs.getString("code"))
                        .name(rs.getString("name"))
                        .previousQuantity(rs.getInt("previous_quantity"))
                        .afterQuantity(rs.getInt("after_quantity"))
                        .build());
            }
            return stockLogs;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
