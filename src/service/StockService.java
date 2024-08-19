package service;

import connection.DriverManagerDBConnectionUtil;
import dao.StockDao;
import dao.StockSectionDao;
import domain.Stock;
import dto.StockDto;
import dto.StockEditDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class StockService {
    private static final StockDao stockDao = new StockDao();
    private static final StockSectionDao stockSectionDao = new StockSectionDao();

    /**
     * 재고 전체 조회
     *
     * @User: 총 관리자
     */
    public List<StockDto> findAllStocks() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return stockDao.findAll(con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 재고 전체 조회
     *
     * @param id: 창고 관리자 id
     * @User: 창고 관리자
     */
    public List<StockDto> findAllStocksByManagerId(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return stockDao.findByManagerId(con, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 재고 전체 조회
     *
     * @param id: 사업자 id
     * @User: 사업자
     */
    public List<StockDto> findAllStocksByBusinessManId(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return stockDao.findByBusinessManId(con, id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 카테고리별 재고 조회
     *
     * @param categoryId: 카테고리 id
     * @User: 총 관리자
     */
    public List<StockDto> findStocksByParentId(Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return stockDao.findByParentId(con, categoryId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 카테고리별 재고 조회
     *
     * @param id:         창고 관리자 id
     * @param categoryId: 카테고리 id
     * @User: 창고 관리자
     */
    public List<StockDto> findStocksByParentIdAndManagerId(Integer id, Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return stockDao.findByParentIdAndManagerId(con, id, categoryId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 카테고리별 재고 조회
     *
     * @param id:         사업자 id
     * @param categoryId: 카테고리 id
     * @User: 사업자
     */
    public List<StockDto> findStocksByParentIdAndBusinessManId(Integer id, Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return stockDao.findByParentIdAndBusinessManId(con, id, categoryId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 재고 등록
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void saveStock(Stock stock) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);

            int result = stockDao.saveStock(con, stock);

            if (result == 1) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            transactionRollback(con);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 재고 수정
     * 수량, 제조일자, 유효기간 수정 가능
     * 총 관리자: 모든 재고 내역 수정 가능
     * 창고 관리자: 자신의 창고에 등록된 재고만 수정 가능
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void updateStock(StockEditDto request) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = stockDao.updateStock(con, request);

            if (result == 1) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            transactionRollback(con);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 재고 삭제
     * 총 관리자: 모든 재고 내역 삭제 가능
     * 창고 관리자: 자신의 창고에 등록된 재고만 삭제 가능
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void deleteStock(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            stockSectionDao.deleteStockSection(con, id);
            int result = stockDao.deleteStock(con, id);

            if (result == 1) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            transactionRollback(con);
        } finally {
            connectionClose(con);
        }
    }

    private void transactionRollback(Connection con) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectionClose(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
