package service;

import connection.DriverManagerDBConnectionUtil;
import dao.StockLogDao;
import domain.StockLog;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class StockLogService {
    private static final StockLogDao stockLogDao = new StockLogDao();

    /**
     * 재고 로그 조회
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void findStockLogs() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            printStockLogs(stockLogDao.findAll(con));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    private void printStockLogs(List<StockLog> stockLogs) {
        System.out.println("\n\n[재고 실사 현황]");
        System.out.println("-".repeat(150));
        System.out.printf("%-3s| %-20s | %-5s | %-35s | %-10s | %-10s |\n",
                "순번", "변경일", "제품코드", "제품명", "변경 전 수량", "변경 후 수량");
        System.out.println("-".repeat(150));

        for (StockLog stockLog : stockLogs) {
            System.out.printf("%-5d%-1d.%-1d.%-1d %-1d:%-1d:%-10d%-10s%-40s%-20s%-15s\n",
                    stockLog.getId(), stockLog.getModDate().getYear(), stockLog.getModDate().getMonthValue(), stockLog.getModDate().getDayOfMonth(),
                    stockLog.getModDate().getHour(), stockLog.getModDate().getMinute(), stockLog.getModDate().getSecond(),
                    stockLog.getCode(), stockLog.getName(),
                    new DecimalFormat("###,###개").format(stockLog.getPreviousQuantity()),
                    new DecimalFormat("###,###개").format(stockLog.getAfterQuantity()));
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
