package controller;

import domain.Expense;
import domain.User;
import domain.Warehouse;
import dto.ExpenseEditDto;
import dto.ExpenseSaveDto;
import dto.ProfitDto;
import dto.TotalAssetDto;
import service.ExpenseService;
import service.WarehouseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;

public class ExpenseController {
    private static final ExpenseService expenseService = new ExpenseService();
    private static final WarehouseService warehouseService = new WarehouseService();
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void start(User user) {
        while (true) {
            switch (user.getRoleType().toString()) {
                case "ADMIN" -> {
                    while (true) {
                        try {
                            System.out.println("\n1. 지출내역 조회 | 2. 지출내역 등록 | 3. 지출내역 수정 | 4. 지출내역 삭제 | 5. 매출내역 조회 | 6. 총 정산 내역 조회 | 7. 이전으로");
                            int input = Integer.parseInt(br.readLine());

                            try {
                                switch (input) {

                                    case 1 -> {
                                        printExpenses(expenseService.findAllExpenses());

                                        Loop:
                                        while (true) {
                                            System.out.println("\n1. 지출 구분별 내역 조회 | 2. 연간 지출내역 조회 | 3. 이전으로");
                                            try {
                                                input = Integer.parseInt(br.readLine());

                                                switch (input) {
                                                    case 1 -> printExpenses(expenseService.findAllExpensesByCategoryId(createValidCategoryId()));

                                                    case 2 -> printExpenses(expenseService.findExpensesByYear(createValidYear()));

                                                    case 3 -> {
                                                        break Loop;
                                                    }
                                                    default -> System.out.println("잘못된 접근입니다.");
                                                }
                                            } catch (IOException | NumberFormatException e) {
                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                            }
                                        }
                                    }

                                    case 2 -> expenseService.saveExpense(ExpenseSaveDto.builder()
                                            .warehouseId(createValidWarehouseId())
                                            .expenseDate(createValidDate())
                                            .categoryId(createValidCategoryId())
                                            .expenseAmount(createValidAmount())
                                            .description(createValidDescription())
                                            .paymentMethod(createValidPaymentMethod())
                                            .build());

                                    case 3 -> {
                                        printExpenses(expenseService.findAllExpenses());
                                        expenseService.updateExpense(ExpenseEditDto.builder()
                                                .id(findValidExpenseId(expenseService.findAllExpenses()))
                                                .expenseDate(createValidDate())
                                                .categoryId(createValidCategoryId())
                                                .expenseAmount(createValidAmount())
                                                .description(createValidDescription())
                                                .paymentMethod(createValidPaymentMethod())
                                                .build());
                                    }

                                    case 4 -> {
                                        printExpenses(expenseService.findAllExpenses());
                                        expenseService.deleteExpense(findValidExpenseId(expenseService.findAllExpenses()));
                                    }

                                    case 5 -> printProfits(expenseService.findAllProfit());

                                    case 6 -> printTotalAsset(expenseService.findTotalAsset());

                                    case 7 -> {
                                        return;
                                    }
                                    default -> System.out.println("잘못된 접근입니다.");
                                }
                            } catch (IOException | NumberFormatException e) {
                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                            }
                        } catch (IOException | NumberFormatException e) {
                            System.out.println("오류가 발생했습니다: " + e.getMessage());
                        }
                    }
                }

                case "WAREHOUSE_MANAGER" -> {
                    while (true) {
                        try {
                            System.out.println("\n1. 지출내역 조회 | 2. 지출내역 등록 | 3. 지출내역 수정 | 4. 지출내역 삭제 | 5. 매출내역 조회 | 6. 이전으로");
                            int input = Integer.parseInt(br.readLine());

                            try {
                                switch (input) {

                                    case 1 -> {
                                        printExpenses(expenseService.findAllExpensesById(user.getId()));

                                        Loop:
                                        while (true) {
                                            System.out.println("\n1. 지출 구분별 내역 조회 | 2. 연간 지출내역 조회 | 3. 이전으로");
                                            try {
                                                input = Integer.parseInt(br.readLine());

                                                switch (input) {
                                                    case 1 -> printExpenses(expenseService.findAllExpensesByIdAndCategory(user.getId(), createValidCategoryId()));

                                                    case 2 -> printExpenses(expenseService.findExpensesByIdAndYear(user.getId(), createValidYear()));

                                                    case 3 -> {
                                                        break Loop;
                                                    }
                                                    default -> System.out.println("잘못된 접근입니다.");
                                                }
                                            } catch (IOException | NumberFormatException e) {
                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                            }
                                        }
                                    }

                                    case 2 -> expenseService.saveExpense(ExpenseSaveDto.builder()
                                            .warehouseId(warehouseService.findOneByManagerId(user.getId()).getId())
                                            .expenseDate(createValidDate())
                                            .categoryId(createValidCategoryId())
                                            .expenseAmount(createValidAmount())
                                            .description(createValidDescription())
                                            .paymentMethod(createValidPaymentMethod())
                                            .build());

                                    case 3 -> {
                                        printExpenses(expenseService.findAllExpensesById(user.getId()));
                                        expenseService.updateExpense(ExpenseEditDto.builder()
                                                .id(findValidExpenseId(expenseService.findAllExpensesById(user.getId())))
                                                .expenseDate(createValidDate())
                                                .categoryId(createValidCategoryId())
                                                .expenseAmount(createValidAmount())
                                                .description(createValidDescription())
                                                .paymentMethod(createValidPaymentMethod())
                                                .build());
                                    }

                                    case 4 -> {
                                        printExpenses(expenseService.findAllExpensesById(user.getId()));
                                        expenseService.deleteExpense(findValidExpenseId(expenseService.findAllExpensesById(user.getId())));
                                    }

                                    case 5 -> printProfits(expenseService.findProfitById(user.getId()));

                                    case 6 -> {
                                        return;
                                    }
                                    default -> System.out.println("잘못된 접근입니다.");
                                }
                            } catch (IOException | NumberFormatException e) {
                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                            }
                        } catch (IOException | NumberFormatException e) {
                            System.out.println("오류가 발생했습니다: " + e.getMessage());
                        }
                    }
                }
                default -> System.out.println("잘못된 접근입니다.");
            }
        }
    }

    /**
     * 유효한 지출 번호를 입력 받음
     * 지출 내역 수정 및 삭제 시 사용
     */
    private int findValidExpenseId(List<Expense> expenses) throws IOException {
        while (true) {
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
     * 유효한 창고 번호를 입력 받음
     * 총 관리자가 지출을 등록할 때 사용
     *
     * @return 창고 번호
     */
    private static int createValidWarehouseId() throws IOException {
        while (true) {
            List<Warehouse> warehouses = warehouseService.findAllWarehouses();
            printWarehouses(warehouses);
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
     * 유효한 지출일을 입력 받음
     *
     * @return 지출일
     */
    private static LocalDate createValidDate() throws IOException {
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
     * 유효한 지출 구분을 입력 받음
     *
     * @return 지출 구분 카테고리 번호
     */
    private static int createValidCategoryId() throws IOException {
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
     * 유효한 지출비를
     * 입력 받음
     *
     * @return 지출비
     */
    private static double createValidAmount() throws IOException {
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
    private static String createValidDescription() throws IOException {
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
    private static String createValidPaymentMethod() throws IOException {
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
     * 유효한 연도를 입력 받음
     * 연간 지출 내역 조회 시 사용
     *
     * @return 연도
     */
    private static int createValidYear() throws IOException {
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
     * 숫자를 입력 받을 때 문자, 공백, 특수문자가 포함되어 있는지 확인함
     */
    private static boolean isNotNumber(String input) {
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

    public static void printWarehouses(List<Warehouse> warehouses) {
        System.out.println("\n\n[창고 현황]");
        System.out.println("-".repeat(150));
        System.out.printf("%-3s| %-5s | %-10s\n",
                "번호", "창고코드", "창고명");
        System.out.println("-".repeat(150));

        for (Warehouse warehouse : warehouses) {
            System.out.printf("%-5d%-10s%-10s\n",
                    warehouse.getId(), warehouse.getCode(), warehouse.getName());
        }
    }
}
