package service;

import connection.DriverManagerDBConnectionUtil;
import dao.WarehouseDao;
import domain.Warehouse;
import domain.WarehouseType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class WarehouseService {

    private final static WarehouseDao dao = new WarehouseDao();

    public void saveWarehouse(Warehouse warehouse) {
        // TODO: warehouse 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            boolean result = dao.save(con, warehouse);
            if(result) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public Warehouse findOneById(int id) {
        // TODO: id 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            Warehouse warehouse = dao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 창고입니다."));
            con.setReadOnly(false);
            return warehouse;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public Warehouse findOneByManagerId(int managerId) {
        // TODO: managerId 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            Warehouse warehouse = dao.findByManagerId(con, managerId).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 창고입니다."));
            con.setReadOnly(false);
            return warehouse;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<Warehouse> searchWarehousesByName(String warehouseName) {
        // TODO: warehouseName 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = dao.findAllByName(con, warehouseName);
            con.setReadOnly(false);
            return warehouseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<Warehouse> searchWarehousesByName(int regionId) {
        // TODO: regionId 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = dao.findAllByRegionId(con, regionId);
            con.setReadOnly(false);
            return warehouseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<Warehouse> searchWarehousesByTypeId(int typeId) {
        // TODO: typeId 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = dao.findAllByRegionId(con, typeId);
            con.setReadOnly(false);
            return warehouseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<WarehouseType> findAllWarehouseType() {
        Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection();
        try {
            con.setReadOnly(true);
            List<WarehouseType> types = dao.findAllWarehouseType(con);
            con.setReadOnly(false);
            return types;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    private void transactionRollback(Connection con) {
        try {
            if(con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectionClose(Connection con) {
        try {
            if(con != null) {
                con.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
