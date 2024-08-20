package service;

import connection.DriverManagerDBConnectionUtil;
import dao.WarehouseContractDao;
import dao.WarehouseDao;
import domain.Warehouse;
import domain.WarehouseContract;
import domain.WarehouseType;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class WarehouseService {

    private final static WarehouseDao warehouseDao = new WarehouseDao();
    private final static WarehouseContractDao warehouseContractDao = new WarehouseContractDao();

    public int saveWarehouse(Warehouse warehouse) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int id = warehouseDao.save(con, warehouse);
            con.commit();
            return id;
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public void saveWarehouseContract(WarehouseContract contract) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            boolean result = warehouseContractDao.save(con, contract);
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

    public Warehouse findWarehouseById(int id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            Warehouse warehouse = warehouseDao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 창고입니다."));
            con.setReadOnly(false);
            return warehouse;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<Warehouse> findAllWarehouses() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = warehouseDao.findAll(con);
            con.setReadOnly(false);
            return warehouseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public Warehouse findWarehouseByManagerId(int managerId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            Warehouse warehouse = warehouseDao.findByManagerId(con, managerId).orElseThrow(
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
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = warehouseDao.findAllByName(con, warehouseName);
            con.setReadOnly(false);
            return warehouseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<Warehouse> searchWarehousesByRegionId(int regionId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = warehouseDao.findAllByRegionId(con, regionId);
            con.setReadOnly(false);
            return warehouseList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<Warehouse> searchWarehousesByTypeId(int typeId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Warehouse> warehouseList = warehouseDao.findAllByTypeId(con, typeId);
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
            List<WarehouseType> types = warehouseDao.findAllWarehouseType(con);
            con.setReadOnly(false);
            return types;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<WarehouseContract> findWarehouseContractByUserId(int userId) {
        Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection();
        try {
            con.setReadOnly(true);
            List<WarehouseContract> warehouseContracts = warehouseContractDao.findAllByUserId(con, userId);
            con.setReadOnly(false);
            return warehouseContracts;
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

    public String generateWarehouseCode() {
        return "WH-" + UUID.randomUUID().toString().substring(0, 7);
    }
}
