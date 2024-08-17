package service;

import connection.DriverManagerDBConnectionUtil;
import dao.InboundDao;
import dao.InboundItemDao;
import dao.StockDao;
import dao.StockSectionDao;
import domain.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InboundService {


    private final InboundDao inboundDao = new InboundDao();
    private final StockSectionDao stockSectionDao = new StockSectionDao();
    private final InboundItemDao inboundItemDao = new InboundItemDao();
    private final StockDao stockDao = new StockDao();

    /**
     * 입고, 입고 품목 등록
     *
     * @param inbound
     * @param items
     */
    public void saveInbound(Inbound inbound, List<InboundItem> items) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int inboundId = inboundDao.save(con, inbound);
            boolean result = inboundItemDao.saveInboundItems(con, inboundId, items);
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

    /**
     * 입고 단건 조회
     *
     * @param id 입고 id
     * @return
     */
    public Inbound findInboundById(int id) {
        // TODO: id 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            Inbound inbound = inboundDao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 입고 요청입니다."));
            con.setReadOnly(false);
            return inbound;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 기간별 입고 현황 조회
     *
     * @param fromDateStr 시작 날짜 문자열
     * @param toDateStr 종료 날짜 문자열
     * @return
     */
    public List<Inbound> findInboundsByPeriod(String fromDateStr, String toDateStr) {
        LocalDate fromDate = convertStringToDate(fromDateStr);
        LocalDate toDate = convertStringToDate(toDateStr);
        if(fromDate.isAfter(toDate)) {
            throw new RuntimeException("시작 날짜는 종료 날짜와 같거나 이전 날짜로 입력해주세요.");
        } else if(toDate.isAfter(LocalDate.now())) {
            throw new RuntimeException("종료 날짜는 현재 날짜와 같거나 이전 날짜로 입력해주세요.");
        }
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Inbound> inbounds = inboundDao.findInboundsByPeriod(con, fromDate, toDate);
            con.setReadOnly(false);
            return inbounds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 월별 입고 현황 조회
     *
     * @param year 연도 문자열
     * @param month 월 문자열
     * @return
     */
    public List<Inbound> findInboundsByYearMonth(String year, String month) {
        // TODO: year, month 검증
        LocalDate fromDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
        LocalDate toDate = fromDate.withDayOfMonth(fromDate.lengthOfMonth());
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Inbound> inbounds = inboundDao.findInboundsByPeriod(con, fromDate, toDate);
            con.setReadOnly(false);
            return inbounds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }


    /**
     * 문자열을 LocalDate 타입으로 변환
     *
     * @param dateString
     * @return
     */
    private LocalDate convertStringToDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("날짜 형식이 올바르지 않습니다. yyyy-mm-dd 형식으로 입력해주세요.");
        }
    }

    /**
     * 트랜잭션 롤백
     *
     * @param con
     */
    private void transactionRollback(Connection con) {
        try {
            if(con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 데이터베이스 커넥션 close
     *
     * @param con
     */
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
