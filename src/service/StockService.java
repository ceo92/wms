package service;

import connection.DriverManagerDBConnectionUtil;
import dao.StockDao;
import dao.StockSectionDao;
import domain.Product;
import domain.Stock;
import domain.User;
import dto.StockDto;
import dto.StockEditDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StockService {
    private static final StockDao stockDao = new StockDao();
    private static final StockSectionDao stockSectionDao = new StockSectionDao();
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

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
    public void saveStock(Integer productId, Integer userId) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);

            LocalDateTime manufacturedDate = createValidManufacturedDate();
            int result = stockDao.saveStock(con, Stock.builder()
                    .product(Product.builder()
                            .id(productId)
                            .build())
                    .user(User.builder()
                            .id(userId)
                            .build())
                    .width(createValidWidth())
                    .height(createValidHeight())
                    .quantity(createValidQuantity())
                    .manufacturedDate(manufacturedDate)
                    .expirationDate(createValidExpirationDate(manufacturedDate))
                    .build());
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
    public void updateStock(User user) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = 0;

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> {
                    int stockId = findValidStockId(stockDao.findAll(con), user);
                    LocalDateTime manufacturedDate = createValidManufacturedDate();

                    result = stockDao.updateStock(con, StockEditDto.builder()
                            .id(stockId)
                            .quantity(createValidQuantity())
                            .manufacturedDate(manufacturedDate)
                            .expirationDate(createValidExpirationDate(manufacturedDate))
                            .build());
                }

                case "WAREHOUSE_MANAGER" -> {
                    int stockId = findValidStockId(stockDao.findByManagerId(con, user.getId()), user);
                    LocalDateTime manufacturedDate = createValidManufacturedDate();
                    result = stockDao.updateStock(con, StockEditDto.builder()
                            .id(stockId)
                            .quantity(createValidQuantity())
                            .manufacturedDate(manufacturedDate)
                            .expirationDate(createValidExpirationDate(manufacturedDate))
                            .build());
                }
            }

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
    public void deleteStock(User user) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = 0;

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> {
                    int stockId = findValidStockId(stockDao.findAll(con), user);
                    stockSectionDao.deleteStockSection(con, stockId);
                    result = stockDao.deleteStock(con, stockId);
                }

                case "WAREHOUSE_MANAGER" -> {
                    int stockId = findValidStockId(stockDao.findByManagerId(con, user.getId()), user);
                    stockSectionDao.deleteStockSection(con, stockId);
                    result = stockDao.deleteStock(con, stockId);
                }
            }

            if (result == 1) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException | IOException e) {
            transactionRollback(con);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 유효한 재고 번호를 입력 받음
     * 재고 내역 수정 및 삭제 시 사용
     */
    private int findValidStockId(List<StockDto> stocks, User user) throws IOException {
        while (true) {
            findStocks(user);
            System.out.println("\n재고의 번호를 선택하세요.\n");
            System.out.print("번호 입력: ");
            String stockId = br.readLine();

            if (isNotNumber(stockId)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }

            if (stocks.stream()
                    .map(StockDto::getId)
                    .anyMatch(n -> n == Integer.parseInt(stockId))) {
                return Integer.parseInt(stockId);
            } else {
                System.out.println("\n조회할 수 없는 재고 건입니다.\n");
            }
        }
    }

    /**
     * 유효한 재고 가로 사이즈를 입력 받음
     *
     * @return 재고 가로 사이즈
     */
    private Double createValidWidth() throws IOException {
        String width;

        while (true) {
            System.out.println("\n가로 사이즈를 입력하세요.\n");
            System.out.print("가로: ");
            width = br.readLine();

            if (isNotNumber(width)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Double.parseDouble(width) < 0) {
                System.out.println("\n0cm 이하는 입력할 수 없습니다.\n");
                continue;
            }
            return Double.parseDouble(width);
        }
    }

    /**
     * 유효한 재고 세로 사이즈를 입력 받음
     *
     * @return 재고 세로 사이즈
     */
    private Double createValidHeight() throws IOException {
        String height;

        while (true) {
            System.out.println("\n재고 세로 사이즈를 입력하세요.\n");
            System.out.print("세로: ");
            height = br.readLine();

            if (isNotNumber(height)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Double.parseDouble(height) < 0) {
                System.out.println("\n0cm 이하는 입력할 수 없습니다.\n");
                continue;
            }
            return Double.parseDouble(height);
        }
    }

    /**
     * 유효한 재고 수량을 입력 받음
     *
     * @return 재고 수량
     */
    private Integer createValidQuantity() throws IOException {
        String quantity;

        while (true) {
            System.out.println("\n수량을 입력하세요.\n");
            quantity = br.readLine();

            if (isNotNumber(quantity)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Double.parseDouble(quantity) < 0) {
                System.out.println("\n0개 이하는 입력할 수 없습니다.\n");
                continue;
            }
            return Integer.parseInt(quantity);
        }
    }

    /**
     * 유효한 제조일자를 입력 받음
     *
     * @return 제조일자
     */
    private LocalDateTime createValidManufacturedDate() throws IOException {
        System.out.println("제조기간을 입력합니다.");
        return createValidTime(createValidDate());
    }

    /**
     * 유효한 유효기간을 입력 받음
     *
     * @return 유효기간
     */
    private LocalDateTime createValidExpirationDate(LocalDateTime manufacturedDate) throws IOException {
        while (true) {
            System.out.println("유효기간을 입력합니다.");
            LocalDateTime expirationDate = createValidTime(createValidDate());

            if (expirationDate.isBefore(manufacturedDate)) {
                System.out.println("\n유효기간은 제조일자보다 빠를 수 없습니다.\n");
                continue;
            }
            return expirationDate;
        }
    }

    /**
     * 유효한 날짜를 입력 받음
     *
     * @return 날짜
     */
    private LocalDate createValidDate() throws IOException {
        String year;
        String month;
        String day;

        while (true) {
            System.out.print("연도 입력: ");
            year = br.readLine();

            if (isNotNumber(year)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(year) != 2023 && Integer.parseInt(year) != 2024) {
                System.out.println("\n잘못된 연도 입니다.\n");
                continue;
            }

            System.out.print("월 입력 (1~12): ");
            month = br.readLine();

            if (isNotNumber(month)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(month) < 1 || Integer.parseInt(month) > 12) {
                System.out.println("\n잘못된 월입니다.\n");
                continue;
            } else if (Integer.parseInt(month) == 2) {
                System.out.print("일 입력 (1~28): ");
                day = br.readLine();

                if (isNotNumber(day)) {
                    System.out.println("숫자가 아닙니다.");
                    continue;
                }
                if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 28) {
                    System.out.println("\n잘못된 일입니다.\n");
                    continue;
                }
            } else if (Integer.parseInt(month) == 1 || Integer.parseInt(month) == 3 || Integer.parseInt(month) == 5 ||
                    Integer.parseInt(month) == 7 || Integer.parseInt(month) == 8 || Integer.parseInt(month) == 10 || Integer.parseInt(month) == 12) {
                System.out.print("일 입력 (1~31): ");
                day = br.readLine();

                if (isNotNumber(day)) {
                    System.out.println("숫자가 아닙니다.");
                    continue;
                }
                if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 31) {
                    System.out.println("\n잘못된 일입니다.\n");
                    continue;
                }
            } else {
                System.out.print("일 입력 (1~31): ");
                day = br.readLine();

                if (isNotNumber(day)) {
                    System.out.println("숫자가 아닙니다.");
                    continue;
                }
                if (Integer.parseInt(day) < 1 || Integer.parseInt(day) > 30) {
                    System.out.println("\n잘못된 일입니다.\n");
                    continue;
                }
            }
            return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        }
    }

    /**
     * 유효한 시간을 입력 받음
     *
     * @return 날짜, 시간
     * 간
     */
    private LocalDateTime createValidTime(LocalDate date) throws IOException {
        String hour;
        String minute;
        String second;

        while (true) {
            System.out.print("시간 입력 (1~24): ");
            hour = br.readLine();

            if (isNotNumber(hour)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(hour) < 1 || Integer.parseInt(hour) > 24) {
                System.out.println("\n잘못된 시입니다.\n");
                continue;
            }

            System.out.print("분 입력 (0~59): ");
            minute = br.readLine();

            if (isNotNumber(minute)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(minute) < 0 || Integer.parseInt(minute) > 59) {
                System.out.println("\n잘못된 분입니다.\n");
                continue;
            }

            System.out.print("분 입력 (0~59): ");
            second = br.readLine();

            if (isNotNumber(second)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(second) < 0 || Integer.parseInt(second) > 59) {
                System.out.println("\n잘못된 초입니다.\n");
                continue;
            }
            return LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
                    Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
        }
    }


    /**
     * 숫자를 입력 받을 때 문자, 공백, 특수문자가 포함되어 있는지 확인함
     */
    private boolean isNotNumber(String input) {
        return !input.matches("^[0-9]+$");
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
