package service;

import connection.DriverManagerDBConnectionUtil;
import dao.InboundDao;
import dao.InboundItemDao;
import dao.StockDao;
import dao.StockSectionDao;
import domain.*;
import dto.InboundDto;
import dto.InboundItemDto;

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
     * @param inboundDto
     * @param itemDtoList
     */
    public boolean saveInbound(InboundDto inboundDto, List<InboundItemDto> itemDtoList) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            Inbound inbound = inboundDto.toCreateInbound();
            int inboundId = inboundDao.save(con, inbound);
            boolean result = inboundItemDao.saveInboundItems(con, inboundId, itemDtoList);
            if(result) {
                con.commit();
            } else {
                con.rollback();
            }
            return result;
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
     * 회원별 입고 조회
     *
     * @param userId 회원 id
     * @return
     */
    public List<Inbound> findInboundByUserId(int userId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Inbound> inbounds = inboundDao.findInboundByUserId(con, userId);
            con.setReadOnly(false);
            return inbounds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Inbound> findInboundsByStatus(InboundStatus status) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Inbound> inbounds = inboundDao.findByStatus(con, status);
            con.setReadOnly(false);
            return inbounds;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Inbound> findAllInbounds() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            List<Inbound> inbounds = inboundDao.findAll(con);
            con.setReadOnly(false);
            return inbounds;
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
    public boolean updateExpectedDate(int id, String dateString) {
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
            }
            return updateResult;
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 품목 수량 변경
     */
    public boolean updateItemRequestQuantity(int itemId, int quantity) {
        // TODO: itemId, quantity 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            boolean updateResult = inboundItemDao.updateRequestQuantity(con, itemId, quantity);
            if(updateResult) {
                con.commit();
            } else {
                con.rollback();
            }
            return updateResult;
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 품목 삭제
     *
     * @param itemId
     */
    public boolean deleteInboundItem(int itemId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            boolean updateResult = inboundItemDao.deleteInboundItem(con, itemId);
            if(updateResult) {
                con.commit();
            } else {
                con.rollback();
            }
            return updateResult;
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 처리
     * 1. 재고 추가
     * 2. 입고 품목에 완료 수량 업데이트
     *
     * @param itemId 입고 처리할 품목
     * @param stock 등록할 재고 정보
     */
    public boolean confirmInbound(int itemId, Stock stock) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            if(stockDao.saveStock(con, stock) == 0 || !inboundItemDao.increaseCompletedQuantity(con, itemId, stock.getQuantity())) {
                con.rollback();
                return false;
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }

    }

    /**
     * 입고 위치 지정
     *
     * @param warehouseId
     * @param stockId
     * @return
     */
    public void assignInboundSection(int warehouseId, int stockId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            Stock stock = stockDao.findById(con, stockId)
                    .orElseThrow(() -> new RuntimeException("존재하지 않는 재고입니다."));
            int quantity = stock.getQuantity();
            if(quantity <= 0) {
                throw new RuntimeException("재고의 개수가 0 이하입니다.");
            }
            // 1. 해당 제품(product)이 저장된 기존 구역에 적재 (한 구역에는 한 종류의 제품만 적재)
            List<StockSection> sections = stockSectionDao.findAllByWarehouseIdAndProductId(con, warehouseId, stock.getProduct().getId());
            for(StockSection section: sections) {
                int maxLoadableQuantity = getMaxLoadableQuantity(section);
                if(maxLoadableQuantity > 0) {
                    int loadQuantity = Math.min(quantity, maxLoadableQuantity);
                    stockSectionDao.updateQuantity(con, section.getId(), section.getQuantity() + loadQuantity);
                    quantity -= loadQuantity;
                }
                if(quantity <= 0) {
                    break;
                }
            }
            // 2. 빈 구역에 적재
            while(quantity > 0) {
                StockSection section = stockSectionDao.findEmptySection(con, warehouseId)
                        .orElseThrow(() -> new RuntimeException("창고에 재고를 적재할 구역이 없습니다."));
                int maxLoadableQuantity = getMaxLoadableQuantity(section);
                if(maxLoadableQuantity > 0) {
                    int loadQuantity = Math.min(quantity, maxLoadableQuantity);
                    stockSectionDao.updateStockIdAndQuantity(con, section.getId(), stock.getId(), quantity);
                    quantity -= loadQuantity;
                }
            }
            con.commit();
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 품목 조회
     *
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
        sb.append("##################################################################################").append("\n");
        sb.append("                                 입 고 지 시 서").append("\n");
        if(inbound.getUser() instanceof BusinessMan businessMan) {
            sb.append("화주사: ").append(businessMan.getBusinessName()).append("\n");
        }
        sb.append("물류센터: ").append(inbound.getWarehouse().getName()).append("\n");
        sb.append("입고번호: ").append(inbound.getId()).append("\n");
        sb.append("공급사: ").append(inbound.getVendor().getName()).append("\n");
        if(InboundStatus.COMPLETED.equals(inbound.getStatus())) {
            sb.append("입고완료일자: ").append(formatDateString(inbound.getInboundCompletedDate())).append("\n");
        } else {
            sb.append("입고예정일자: ").append(formatDateString(inbound.getInboundExpectedDate())).append("\n");
        }
        sb.append("출력 일시: ").append(formatDateString(LocalDateTime.now())).append("\n");
        sb.append(String.format("%-3s %-10s %-30s %-8s %-8s\n", "No", "상품코드", "상품명", "요청수량", "완료수량"));
        sb.append("---------------------------------------------------------------------------------------------------------").append("\n");
        AtomicInteger noCnt = new AtomicInteger(1);
        inboundItems.forEach(item -> sb.append(String.format("%-3s %-10s %-30s %-8s %-8s\n",
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
    public boolean approvalInbound(int id) {
        try (Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection()) {
            Inbound inbound = inboundDao.findById(con, id).orElseThrow(
                    () -> new IllegalStateException("존재하지 않는 입고 요청입니다."));;
            if(!InboundStatus.PENDING.equals(inbound.getStatus())) {
                throw new IllegalArgumentException("입고 예정 건만 승인 처리 가능합니다.");
            } else {
                return updateInboundStatus(con, id, InboundStatus.APPROVED);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 입고 완료
     * 1. 상태가 입고 대기(APPROVED)
     * 2. 입고 건에 대해 모든 품목이 입고 완료 됨
     *
     * @param id 입고 id
     */
    public void completeInbound(int id) {
        try(Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection()) {
            Inbound inbound = inboundDao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 입고 요청입니다."));
            if(!InboundStatus.APPROVED.equals(inbound.getStatus())) {
                throw new RuntimeException("입고 대기 건만 입고 처리 가능합니다.");
            } else if(!inboundItemDao.checkInboundCompleted(con, id)) {
                throw new RuntimeException("아직 입고 완료되지 않은 품목이 존재합니다.");
            } else {
                updateInboundStatus(con, id, InboundStatus.COMPLETED);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 입고 취소
     *
     * @param id 입고 id
     */
    public void cancelInbound(int id) {
        try(Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection()) {
            Inbound inbound = inboundDao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 입고 요청입니다."));
            if(!InboundStatus.PENDING.equals(inbound.getStatus())) {
                throw new RuntimeException("입고 예정인 건만 취소 가능합니다.");
            } else {
                updateInboundStatus(con, id, InboundStatus.CANCEL);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 입고 상태 업데이트
     *
     * @param id 입고 id
     * @param status 변경할 입고 상태
     */
    private boolean updateInboundStatus(Connection con, int id, InboundStatus status) {
        try {
            con.setAutoCommit(false);
            boolean result = inboundDao.updateStatus(con, id, status);
            if(result) {
                con.commit();
            } else {
                con.rollback();
            }
            return result;
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 재고 구역 내에 적재 가능한 재고의 개수를 계산
     *
     * @param section
     * @return
     */
    private int getMaxLoadableQuantity(StockSection section) {
        double sectionArea = section.getWidth() * section.getHeight(); // 구역 면적
        double stockSize = section.getStock().getWidth() * section.getStock().getHeight(); // 적재할 재고 사이즈
        double availableSectionArea = sectionArea - stockSize * section.getQuantity(); // 적재 가능한 구역 내 면적
        return (int)(availableSectionArea / stockSize);
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
