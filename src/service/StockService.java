package service;

import connection.DriverManagerDBConnectionUtil;
import dao.StockDao;
import dao.StockSectionDao;
import domain.Stock;
import dto.StockDto;
import dto.StockEditDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class StockService {
    private static final StockDao stockDao = new StockDao();
    private static final StockSectionDao stockSectionDao = new StockSectionDao();

    /**
     * 재고 전체 조회
     *
     * @User: 총 관리자
     */
    public void findAllStocks() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printStocks(stockDao.findAll(con));
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
    public void findAllStocksByManagerId(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printStocks(stockDao.findByManagerId(con, id));
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
    public void findAllStocksByBusinessManId(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printStocks(stockDao.findByBusinessManId(con, id));
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
    public void findStocksByParentId(Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printStocks(stockDao.findByParentId(con, categoryId));
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
    public void findStocksByParentIdAndManagerId(Integer id, Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printStocks(stockDao.findByParentIdAndManagerId(con, id, categoryId));
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
    public void findStocksByParentIdAndBusinessManId(Integer id, Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printStocks(stockDao.findByParentIdAndBusinessManId(con, id, categoryId));
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

    private void printStocks(List<StockDto> stocks) {
        System.out.print("\n\n[재고 현황]\n");
        System.out.println("-".repeat(200));
        System.out.printf("%-5s | %-10s | %-45s| %-15s | %-15s | %-20s | %-10s | %-20s | %-20s\n",
                "번호", "제품코드", "제품명", "창고위치", "재고위치", "원가", "수량", "제조일자", "유효기간");
        System.out.println("-".repeat(200));

        for (StockDto stock : stocks) {
            System.out.printf("%-10d%-15s%-45s%-20s%-20s%-20s%-20s%-1d.%-1d.%-1d %-1d:%-1d:%-10d%-1d.%-1d.%-1d %-1d:%-1d:%-1d\n",
                    stock.getId(), stock.getProductCode(), stock.getProductName(), stock.getWarehouseName(), stock.getSectionName(),
                    new DecimalFormat("###,###원").format(stock.getCostPrice()), new DecimalFormat("###,###개").format(stock.getQuantity()),
                    stock.getManufacturedDate().getYear(), stock.getManufacturedDate().getMonthValue(), stock.getManufacturedDate().getDayOfMonth(),
                    stock.getManufacturedDate().getHour(), stock.getManufacturedDate().getMinute(), stock.getManufacturedDate().getSecond(),
                    stock.getExpirationDate().getYear(), stock.getExpirationDate().getMonthValue(), stock.getExpirationDate().getDayOfMonth(),
                    stock.getExpirationDate().getHour(), stock.getExpirationDate().getMinute(), stock.getExpirationDate().getSecond());
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
