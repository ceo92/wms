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
     * 입고 예상 날짜 수정
     * @param id 입고 id
     * @param dateString 날짜 문자열
     */
    public void updateExpectedDate(int id, String dateString) {
        // TODO: id, dateString 검증
        LocalDate date = convertStringToDate(dateString);
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            boolean isExist = inboundDao.existById(con, id);
            if(!isExist) {
                throw new RuntimeException("존재하지 않는 입고 요청입니다.");
            }
            boolean updateResult = inboundDao.updateExpectedDate(con, id, date);
            if(updateResult) {
                con.commit();
            } else {
                con.rollback();
                throw new RuntimeException("입고 예상 날짜 수정에 실패했습니다.");
            }
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 품목 조회
     * @param inboundId
     * @return
     */
    public List<InboundItem> findInboundItems(int inboundId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<InboundItem> items = inboundItemDao.findItemsByInboundId(con, inboundId);
            con.setReadOnly(false);
            return items;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 지시서 출력
     *
     * @param id 입고 id
     * @return
     */
    public String generateInboundOrder(int id) {
        Inbound inbound = findInboundById(id);
        List<InboundItem> inboundItems = findInboundItems(inbound.getId());
        StringBuilder sb = new StringBuilder();
        sb.append("#########################################");
        sb.append("              입 고 지 시 서");
        if(inbound.getUser() instanceof BusinessMan businessMan) {
            sb.append("화주사: ").append(businessMan.getBusinessName());
        }
        sb.append("물류센터: ").append(inbound.getWarehouse().getName());
        sb.append("입고번호: ").append(inbound.getId());
        sb.append("공급사: ").append(inbound.getVendor().getName());
        if(InboundStatus.COMPLETED.equals(inbound.getStatus())) {
            sb.append("입고완료일자: ").append(formatDateString(inbound.getInboundCompletedDate()));
        } else {
            sb.append("입고예정일자: ").append(formatDateString(inbound.getInboundExpectedDate()));
        }
        sb.append("출력 일시: ").append(formatDateString(LocalDateTime.now()));
        sb.append(String.format("%5s %15s %30s %8s %8s", "No", "상품코드", "상품명", "요청수량", "완료수량"));
        sb.append("---------------------------------------------------------------------------------------------------------");
        AtomicInteger noCnt = new AtomicInteger(1);
        inboundItems.forEach(item -> sb.append(String.format("%5s %15s %30s %8s %8s",
                noCnt.getAndIncrement(),
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getRequestQuantity(),
                item.getCompleteQuantity())));
        return sb.toString();
    }


    /**
     * 입고 승인
     *
     * @param id 입고 id
     */
    // 재고에 입고 요청한 건 추가
    public void approvalInbound(int id) {
        Inbound inbound = findInboundById(id);
        if(!InboundStatus.PENDING.equals(inbound.getStatus())) {
            throw new RuntimeException("입고 예정 건만 승인 처리 가능합니다.");
        }
        updateInboundStatus(id, InboundStatus.APPROVED);
        // 재고 테이블에 품목 추가
    }

    /**
     * 입고 완료
     *
     * @param id 입고 id
     */
    public void completeInbound(int id) {
        Inbound inbound = findInboundById(id);
        if(!InboundStatus.APPROVED.equals(inbound.getStatus())) {
            throw new RuntimeException("입고 대기 건만 입고 처리 가능합니다.");
        }
        updateInboundStatus(id, InboundStatus.COMPLETED);
    }

    /**
     * 입고 취소
     *
     * @param id 입고 id
     */
    public void cancelInbound(int id) {
        Inbound inbound = findInboundById(id);
        if(!InboundStatus.PENDING.equals(inbound.getStatus())) {
            throw new RuntimeException("입고 예정인 건만 취소 가능합니다.");
        }
        updateInboundStatus(id, InboundStatus.CANCEL);
    }

    /**
     * 입고 상태 업데이트
     *
     * @param id 입고 id
     * @param status 변경할 입고 상태
     */
    private void updateInboundStatus(int id, InboundStatus status) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            boolean result = inboundDao.updateStatus(con, id, status);
            if(result) {
                con.commit();
            } else {
                con.rollback();
                throw new RuntimeException("입고 상태 변경을 실패했습니다.");
            }
        } catch (SQLException e) {
            transactionRollback(con);
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
     * LocalDate 타입을 문자열로 포맷팅
     *
     * @param date
     * @return
     */
    private String formatDateString(LocalDate date) {
        if(date == null) {
            return "-";
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    /**
     * LocalDateTime 타입을 문자열로 포맷팅
     *
     * @param dateTime
     * @return
     */
    private String formatDateString(LocalDateTime dateTime) {
        if(dateTime == null) {
            return "-";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
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
