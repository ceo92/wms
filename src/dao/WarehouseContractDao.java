package dao;

import domain.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WarehouseContractDao {

    public boolean save(Connection con, WarehouseContract contract) {
        String sql = new StringBuilder()
                .append("INSERT INTO warehouse_contract(")
                .append("warehouse_id, business_man_id, capacity, contract_date, contract_month")
                .append(") VALUES (?, ?, ?, ?, ?)")
                .toString();
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, contract.getWarehouse().getId());
            pstmt.setInt(2, contract.getBusinessMan().getId());
            pstmt.setDouble(3, contract.getCapacity());
            pstmt.setDate(4, java.sql.Date.valueOf(contract.getContractDate()));
            pstmt.setInt(5, contract.getContractMonth());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows == 1;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WarehouseContract> findAllByUserId(Connection con, int userId) {
        String sql = new StringBuilder()
                .append("SELECT * FROM warehouse_contract wc, user u, business_man bm, warehouse w, warehouse_type wt, region r")
                .append(" WHERE wc.business_man_id = ?")
                .append(" AND wc.business_man_id = u.id")
                .append(" AND u.id = bm.id")
                .append(" AND wc.warehouse_id = w.id")
                .append(" AND w.type_id = wt.id")
                .append(" AND w.region_id = r.id")
                .toString();
        List<WarehouseContract> contractList = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs != null && rs.next()) {
                    WarehouseContract contract = WarehouseContract.builder()
                            .warehouse(Warehouse.builder()
                                    .id(rs.getInt("w.id"))
                                    .type(new WarehouseType(
                                            rs.getInt("wt.id"),
                                            rs.getString("wt.name")
                                    ))
                                    .code(rs.getString("w.code"))
                                    .name(rs.getString("w.name"))
                                    .region(Region.builder()
                                            .id(rs.getInt("r.id"))
                                            .code(rs.getString("r.code"))
                                            .name(rs.getString("r.name"))
                                            .build()
                                    )
                                    .detailAddress(rs.getString("w.detail_address"))
                                    .contact(rs.getString("w.contact"))
                                    .maxCapacity(rs.getDouble("w.max_capacity"))
                                    .pricePerArea(rs.getDouble("w.price_per_area"))
                                    .regDate(rs.getTimestamp("w.reg_date").toLocalDateTime())
                                    .modDate(rs.getTimestamp("w.mod_date").toLocalDateTime())
                                    .build())
                            .businessMan(new BusinessMan(
                                    rs.getInt("u.id"),
                                    rs.getString("u.name"),
                                    rs.getString("u.phone_number"),
                                    rs.getString("u.login_email"),
                                    null,
                                    RoleType.valueOf(rs.getString("u.role_type")), null, null,
                                    rs.getString("bm.business_num"),
                                    rs.getString("bm.business_name")))
                            .capacity(rs.getDouble("wc.capacity"))
                            .contractDate(rs.getDate("wc.contract_date").toLocalDate())
                            .contractMonth(rs.getInt("wc.contract_month"))
                            .build();
                    contractList.add(contract);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return contractList;
    }
}
