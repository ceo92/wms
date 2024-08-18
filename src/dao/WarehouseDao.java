package dao;

import domain.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarehouseDao {
    public int save(Connection con, Warehouse warehouse) {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO warehouse(")
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
            pstmt.setObject(10, java.sql.Timestamp.valueOf(warehouse.getRegDate()));
            int affectedRows = pstmt.executeUpdate();
            if(affectedRows == 1) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if(rs != null && rs.next()) {
                    return rs.getInt(1);
                }
            }
            throw new SQLException("WarehouseDao.save fail");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Warehouse> findById(Connection con, int id) {
        String sql = "SELECT * FROM warehouse w, user u, warehouse_type wt, region r"
                + " WHERE w.id = ? AND w.manager_id = u.id AND w.type_id = wt.id AND w.region_id = r.id";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                Warehouse warehouse = Warehouse.builder()
                        .id(rs.getInt("w.id"))
                        .manager(User.builder()
                                .id(rs.getInt("u.id"))
                                .phoneNumber(rs.getString("u.phone_number"))
                                .loginEmail(rs.getString("u.login_email"))
                                .roleType(RoleType.valueOf(rs.getString("u.role_type")))
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
                        .regDate(convertToLocalDateTime(rs.getTimestamp("w.reg_date")))
                        .modDate(convertToLocalDateTime(rs.getTimestamp("w.mod_date")))
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
        String sql = "SELECT * FROM warehouse w, user u, warehouse_type wt, region r"
                + " WHERE w.name LIKE ? AND w.manager_id = u.id AND w.type_id = wt.id AND w.region_id = r.id";
        List<Warehouse> warehouseList = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1,"%" + name + "%");
            ResultSet rs = pstmt.executeQuery();
            while(rs != null && rs.next()) {
                Warehouse warehouse = Warehouse.builder()
                        .id(rs.getInt("w.id"))
                        .manager(User.builder()
                                .id(rs.getInt("u.id"))
                                .phoneNumber(rs.getString("u.phone_number"))
                                .loginEmail(rs.getString("u.login_email"))
                                .roleType(RoleType.valueOf(rs.getString("u.role_type")))
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
                        .regDate(convertToLocalDateTime(rs.getTimestamp("w.reg_date")))
                        .modDate(convertToLocalDateTime(rs.getTimestamp("w.mod_date")))
                        .build();
                warehouseList.add(warehouse);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return warehouseList;
    }

    public List<Warehouse> findAllByRegionId(Connection con, int regionId) {
        String sql = "SELECT * FROM warehouse w, user u, warehouse_type wt, region r"
                + " WHERE w.region_id = ? AND w.manager_id = u.id AND w.type_id = wt.id AND w.region_id = r.id";
        List<Warehouse> warehouseList = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, regionId);
            ResultSet rs = pstmt.executeQuery();
            while(rs != null && rs.next()) {
                Warehouse warehouse = Warehouse.builder()
                        .id(rs.getInt("w.id"))
                        .manager(User.builder()
                                .id(rs.getInt("u.id"))
                                .phoneNumber(rs.getString("u.phone_number"))
                                .loginEmail(rs.getString("u.login_email"))
                                .roleType(RoleType.valueOf(rs.getString("u.role_type")))
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
                        .regDate(convertToLocalDateTime(rs.getTimestamp("w.reg_date")))
                        .modDate(convertToLocalDateTime(rs.getTimestamp("w.mod_date")))
                        .build();
                warehouseList.add(warehouse);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return warehouseList;
    }

    public List<Warehouse> findAllByTypeId(Connection con, int typeId) {
        String sql = "SELECT * FROM warehouse w, user u, warehouse_type wt, region r"
                + " WHERE w.type_id = ? AND w.manager_id = u.id AND w.type_id = wt.id AND w.region_id = r.id";
        List<Warehouse> warehouseList = new ArrayList<>();
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, typeId);
            ResultSet rs = pstmt.executeQuery();
            while(rs != null && rs.next()) {
                Warehouse warehouse = Warehouse.builder()
                        .id(rs.getInt("w.id"))
                        .manager(User.builder()
                                .id(rs.getInt("u.id"))
                                .phoneNumber(rs.getString("u.phone_number"))
                                .loginEmail(rs.getString("u.login_email"))
                                .roleType(RoleType.valueOf(rs.getString("u.role_type")))
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
                        .regDate(convertToLocalDateTime(rs.getTimestamp("w.reg_date")))
                        .modDate(convertToLocalDateTime(rs.getTimestamp("w.mod_date")))
                        .build();
                warehouseList.add(warehouse);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return warehouseList;
    }

    public Optional<Warehouse> findByManagerId(Connection con, int managerId) {
        String sql = "SELECT * FROM warehouse w, user u, warehouse_type wt, region r"
                + " WHERE w.manager_id = ? AND w.manager_id = u.id AND w.type_id = wt.id AND w.region_id = r.id";
        try(PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, managerId);
            ResultSet rs = pstmt.executeQuery();
            if(rs != null && rs.next()) {
                Warehouse warehouse = Warehouse.builder()
                        .id(rs.getInt("w.id"))
                        .manager(User.builder()
                                .id(rs.getInt("u.id"))
                                .phoneNumber(rs.getString("u.phone_number"))
                                .loginEmail(rs.getString("u.login_email"))
                                .roleType(RoleType.valueOf(rs.getString("u.role_type")))
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
                        .regDate(convertToLocalDateTime(rs.getTimestamp("w.reg_date")))
                        .modDate(convertToLocalDateTime(rs.getTimestamp("w.mod_date")))
                        .build();
                return Optional.of(warehouse);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<WarehouseType> findAllWarehouseType(Connection con) {
        String sql = "SELECT * FROM warehouse_type";
        List<WarehouseType> types = new ArrayList<>();
        try(Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs != null && rs.next()) {
                WarehouseType type = new WarehouseType(rs.getInt("id"), rs.getString("name"));
                types.add(type);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return types;
    }

    public LocalDateTime convertToLocalDateTime(Timestamp timestamp) {
        if(timestamp == null) {
            return null;
        }
        return timestamp.toLocalDateTime();
    }

}
