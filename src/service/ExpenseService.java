package service;

import dao.ExpenseDao;
import domain.Expense;
import dto.ExpenseEditDto;
import dto.ExpenseSaveDto;
import dto.ProfitDto;
import dto.TotalAssetDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ExpenseService {
    private static final ExpenseDao expenseDao = new ExpenseDao();
    private static final WarehouseService warehouseService = new WarehouseService();
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * 지출 조회
     * 총 관리자: 전체 지출 내역 조회
     * 창고 관리자: 관리하는 창고의 지출만 조회
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void findExpenses(User user) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> printExpenses(expenseDao.findAll(con));

                case "WAREHOUSE_MANAGER" -> printExpenses(expenseDao.findById(con, user.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 지출 조회 (필터링: 지출 구분)
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void findExpensesByCategory(User user) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> printExpenses(expenseDao.findAllByCategory(con, createValidCategoryId()));

                case "WAREHOUSE_MANAGER" -> printExpenses(expenseDao.findByCategory(con, user.getId(), createValidCategoryId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 연간 지출 내역 조회
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void findExpensesByYear(User user) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> printExpenses(expenseDao.findAllByYear(con, createValidYear()));

                case "WAREHOUSE_MANAGER" -> printExpenses(expenseDao.findByYear(con, user.getId(), createValidYear()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 매출 내역 조회
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void findProfit(User user) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> printProfits(expenseDao.findProfits(con));

                case "WAREHOUSE_MANAGER" -> printProfits(expenseDao.findProfitById(con, user.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 총정산 내역 조회 (지출계, 매출계, 순수익, 순수익 증감률)
     *
     * @User: 총 관리자
     */
    public void findTotalAsset() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            printTotalAsset(expenseDao.findTotalAsset(con));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 지출 등록
     * 총 관리자: 모든 창고에 대한 지출 등록 가능
     * 창고 관리자: 자신이 관리하는 창고의 지출만 등록 가능
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void saveExpense(User user) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = 0;

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> result = expenseDao.saveExpense(con, ExpenseSaveDto.builder()
                        .warehouseId(createValidWarehouseId())
                        .expenseDate(createValidDate())
                        .categoryId(createValidCategoryId())
                        .expenseAmount(createValidAmount())
                        .description(createValidDescription())
                        .paymentMethod(createValidPaymentMethod())
                        .build());

                case "WAREHOUSE_MANAGER" -> result = expenseDao.saveExpense(con, ExpenseSaveDto.builder()
                        .warehouseId(warehouseService.findWarehouseByManagerId(user).getId())
                        .expenseDate(createValidDate())
                        .categoryId(createValidCategoryId())
                        .expenseAmount(createValidAmount())
                        .description(createValidDescription())
                        .paymentMethod(createValidPaymentMethod())
                        .build());
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
     * 지출 수정
     * 총 관리자: 모든 지출 내역 수정 가능
     * 창고 관리자: 자신이 등록한 지출 내역만 수정 가능
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void updateExpense(User user) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = 0;

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> result = expenseDao.updateExpense(con, ExpenseEditDto.builder()
                        .id(findValidExpenseId(expenseDao.findAll(con), user))
                        .expenseDate(createValidDate())
                        .categoryId(createValidCategoryId())
                        .expenseAmount(createValidAmount())
                        .description(createValidDescription())
                        .paymentMethod(createValidPaymentMethod())
                        .build());

                case "WAREHOUSE_MANAGER" -> result = expenseDao.updateExpense(con, ExpenseEditDto.builder()
                        .id(findValidExpenseId(expenseDao.findById(con, user.getId()), user))
                        .expenseDate(createValidDate())
                        .categoryId(createValidCategoryId())
                        .expenseAmount(createValidAmount())
                        .description(createValidDescription())
                        .paymentMethod(createValidPaymentMethod())
                        .build());
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
     * 지출 삭제
     * 총 관리자: 모든 지출 내역 삭제 가능
     * 창고 관리자: 자신이 등록한 지출 내역만 삭제 가능
     *
     * @User: 총 관리자, 창고 관리자
     */
    public void deleteExpense(User user) throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = 0;

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> result = expenseDao.deleteExpense(con, findValidExpenseId(expenseDao.findAll(con), user));

                case "WAREHOUSE_MANAGER" -> result = expenseDao.deleteExpense(con, findValidExpenseId(expenseDao.findById(con, user.getId()), user));
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
     * 유효한 지출일을 입력 받음
     *
     * @return 지출일
     */
    private LocalDate createValidDate() throws IOException {
        String year;
        String month;
        String day;

        while (true) {
            System.out.println("\n지출일을 입력합니다.\n");
            System.out.print("연도 입력 (2023~2024): ");
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
     * 유효한 지출비를 입력 받음
     *
     * @return 지출비
     */
    private double createValidAmount() throws IOException {
        String amount;

        while (true) {
            System.out.println("\n가격을 입력하세요.\n");
            amount = br.readLine();

            if (isNotNumber(amount)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Double.parseDouble(amount) < 0) {
                System.out.println("\n0원 이하는 입력할 수 없습니다.\n");
                continue;
            }
            return Double.parseDouble(amount);
        }
    }

    /**
     * 유효한 지출 설명을 입력 받음
     *
     * @return 지출 설명
     */
    private String createValidDescription() throws IOException {
        String description;

        while (true) {
            System.out.println("\n지출에 대한 설명을 입력하시겠습니까?\n");
            System.out.println("1. 예 | 2. 아니오");
            description = br.readLine();

            if (isNotNumber(description)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(description) == 2) {
                return "";
            } else if (Integer.parseInt(description) == 1) {
                System.out.println("\n지출에 대한 설명을 입력해주세요.\n");
                return br.readLine();
            }
        }
    }

    /**
     * 유효한 지출 방법을 입력 받음
     *
     * @return 지출 방법 번호
     */
    private String createValidPaymentMethod() throws IOException {
        String paymentMethod;

        while (true) {
            System.out.println("\n지출 방법을 선택해세요.\n");
            System.out.println("1. 카드 | 2. 계좌이체");
            paymentMethod = br.readLine();

            if (isNotNumber(paymentMethod)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }

            if (Integer.parseInt(paymentMethod) == 1) {
                return "카드";
            } else if (Integer.parseInt(paymentMethod) == 2) {
                return "계좌이체";
            }
        }
    }

    /**
     * 유효한 지출 구분을 입력 받음
     *
     * @return 지출 구분 카테고리 번호
     */
    private int createValidCategoryId() throws IOException {
        String categoryId;

        while (true) {
            System.out.println("\n지출 구분을 선택하세요.\n");
            System.out.println("1. 유지보수비 | 2. 인건비 | 3. 운송비");
            categoryId = br.readLine();

            if (isNotNumber(categoryId)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (Integer.parseInt(categoryId) < 1 || Integer.parseInt(categoryId) > 3) {
                System.out.println("\n다시 선택해주세요.\n");
                continue;
            }
            return Integer.parseInt(categoryId);
        }
    }

    /**
     * 유효한 연도를 입력 받음
     * 연간 지출 내역 조회 시 사용
     *
     * @return 연도
     */
    private int createValidYear() throws IOException {
        String year;

        while (true) {
            System.out.println("\n조회할 연도를 입력하세요.\n");
            System.out.print("연도: ");
            year = br.readLine();

            if (isNotNumber(year)) {
                System.out.println("숫자가 아닙니다.\n");
                continue;
            }
            if (Integer.parseInt(year) >= 2023 && Integer.parseInt(year) <= 2024) {
                return Integer.parseInt(year);
            } else {
                System.out.println("\n조회할 수 없는 숫자입니다.\n");
            }
        }
    }

    /**
     * 유효한 창고 번호를 입력 받음
     * 총 관리자가 지출을 등록할 때 사용
     *
     * @return 창고 번호
     */
    private int createValidWarehouseId() throws IOException {
        while (true) {
            List<Warehouse> warehouses = warehouseService.findWarehouses();
            warehouseService.printWarehouses(warehouses);
            System.out.println("\n창고를 선택하세요.\n");
            System.out.print("창고 번호: ");
            String warehouseId = br.readLine();

            if (isNotNumber(warehouseId)) {
                System.out.println("숫자가 아닙니다.\n");
                continue;
            }

            if (warehouses.stream()
                    .map(Warehouse::getId)
                    .anyMatch(n -> n == Integer.parseInt(warehouseId))) {
                return Integer.parseInt(warehouseId);
            } else {
                System.out.println("찾을 수 없는 창고입니다.");
            }
        }
    }

    /**
     * 유효한 지출 번호를 입력 받음
     * 지출 내역 수정 및 삭제 시 사용
     */
    private int findValidExpenseId(List<Expense> expenses, User user) throws IOException {
        while (true) {
            findExpenses(user);
            System.out.println("\n지출 내역의 번호를 선택하세요.\n");
            System.out.print("번호 입력: ");
            String expenseId = br.readLine();

            if (isNotNumber(expenseId)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }

            if (expenses.stream()
                    .map(Expense::getId)
                    .anyMatch(n -> n == Integer.parseInt(expenseId))) {
                return Integer.parseInt(expenseId);
            } else {
                System.out.println("\n조회할 수 없는 지출 건입니다.\n");
            }
        }
    }

    /**
     * 숫자를 입력 받을 때 문자, 공백, 특수문자가 포함되어 있는지 확인함
     */
    private boolean isNotNumber(String input) {
        return !input.matches("^[0-9]+$");
    }

    private void printExpenses(List<Expense> expenses) {
        System.out.println("\n\n[지출 현황]");
        System.out.println("-".repeat(150));
        System.out.printf("%-3s| %-5s | %-10s | %-10s | %-15s | %-40s | %-10s |\n",
                "번호", "창고명", "지출일", "지출구분", "지출금액", "설명", "지출방법");
        System.out.println("-".repeat(150));

        for (Expense expense : expenses) {
            System.out.printf("%-5d%-10s%-15s%-15s%-20s%-30s%10s\n",
                    expense.getId(), expense.getWarehouse().getName(),
                    new SimpleDateFormat("yyyy-MM-dd").format(Date.valueOf(expense.getExpenseDate())),
                    expense.getExpenseCategory().getName(), new DecimalFormat("###,###원").format(expense.getExpenseAmount()), expense.getDescription(), expense.getPaymentMethod());
        }
    }

    private void printProfits(List<ProfitDto> profits) {
        System.out.println("\n\n[매출 현황]");
        System.out.println("-".repeat(100));
        System.out.printf("%-3s| %-5s | %-10s | %-20s |\n",
                "순번", "창고명", "계약일", "매출금액");
        System.out.println("-".repeat(100));

        for (ProfitDto profit : profits) {
            System.out.printf("%-5d%-10s%-15s%-15s\n",
                    profits.indexOf(profit) + 1, profit.getName(),
                    new SimpleDateFormat("yyyy-MM-dd").format(Date.valueOf(profit.getContractDate())),
                    new DecimalFormat("###,###원").format(profit.getProfit()));
        }
    }

    private void printTotalAsset(TotalAssetDto asset) {
        System.out.println("\n\n[2024년 총정산]");
        System.out.println("-".repeat(100));
        System.out.printf("%-15s| %-15s | %-15s | %-15s |\n",
                "지출계", "매출계", "순수익", "순수익 증감률");
        System.out.println("-".repeat(100));

        System.out.printf("%-20s%-20s%-20s%s\n",
                new DecimalFormat("###,###원").format(asset.getSumExpense()),
                new DecimalFormat("###,###원").format(asset.getSumProfit()),
                new DecimalFormat("###,###원").format(asset.getNetProfit()),
                new DecimalFormat("#.##%").format(asset.getNetProfitPer()));
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
