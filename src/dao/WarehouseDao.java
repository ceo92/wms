package dao;

import domain.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class WarehouseDao {
    public boolean save(Connection con, Warehouse warehouse) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INFO warehouse(")
                .append("manager_id, type_id, code, name, region_id, detail_address, contact, max_capacity, price_per_area, reg_date")
                .append(") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        try(PreparedStatement pstmt = con.prepareStatement(sb.toString(), Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, warehouse.getManager().getId());
            pstmt.setInt(2, warehouse.getType().getId());
            pstmt.setString(3, warehouse.getCode());
            pstmt.setString(4, warehouse.getName());
            pstmt.setInt(5, warehouse.getRegion().getId());
            pstmt.setString(6, warehouse.getDetailAddress());
            pstmt.setString(7, warehouse.getContact());
            pstmt.setDouble(8, warehouse.getMaxCapacity());
            pstmt.setDouble(9, warehouse.getPricePerArea());
            pstmt.setObject(10, warehouse.getRegDate());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows != 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Warehouse> findById(Connection con, int id) {
        String sql = "SELECT * FROM warehouse w, user u, warehouse_type wt, region r"
                + " WHERE w.id = ? AND w.manager_id = u.id AND w.type_id = wt.id AND w.region_id = r.id";
        try {
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                Warehouse warehouse = Warehouse.builder()
                        .id(rs.getInt("w.id"))
                        .manager(User.builder()
                                .id(rs.getInt("u.id"))
                                .phoneNumber(rs.getString("u.phone_number"))
                                .loginId(rs.getString("u.login_id"))
                                .roleType(convertStringToRoleType(rs.getString("u.role_type")))
                                .build()
                        )
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
                        .build();
                return Optional.of(warehouse);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Warehouse> findAllByName(Connection con, String name) {
        return null;
    }

    public List<Warehouse> findAllByRegionId(Connection con, int id) {
        return null;
    }

    public List<Warehouse> findAllByTypeId(Connection con, int id) {
        return null;
    }

    public Optional<Warehouse> findByManagerId(Connection con, int managerId) {
        return null;
    }

    private RoleType convertStringToRoleType(String roleString) {
        return switch (roleString) {
            case "ADMIN" -> RoleType.ADMIN;
            case "WAREHOUSE_MANAGER" -> RoleType.WAREHOUSE_MANAGER;
            case "BUSINESS_MAN" -> RoleType.BUSINESS_MAN;
            default -> RoleType.GUEST;
        };
    }
}
