package controller;

import domain.Product;
import domain.ProductCategory;
import domain.Stock;
import domain.User;
import dto.StockDto;
import dto.StockEditDto;
import service.ProductCategoryService;
import service.ProductService;
import service.StockLogService;
import service.StockService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class StockController {

    private static final StockService stockService = new StockService();
    private static final ProductService productService = new ProductService();
    private static final StockLogService stockLogService = new StockLogService();
    private static final ProductCategoryService productCategoryService = new ProductCategoryService();
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    public void start(User user) {
        while (true) {
            switch (user.getRoleType().toString()) {
                case "ADMIN" -> {
                    Loop:
                    while (true) {
                        try {
                            System.out.println("\n1. 재고 조회 | 2. 재고 등록 | 3. 재고 수정 | 4. 재고 삭제 | 5. 재고실사 조회 | 6. 이전으로");
                            int input = Integer.parseInt(br.readLine());

                            try {
                                switch (input) {
                                    case 1 -> {
                                        printStocks(stockService.findAllStocks());

                                        while (true) {
                                            System.out.println("1. 카테고리별 재고 조회 | 2. 이전으로");
                                            input = Integer.parseInt(br.readLine());

                                            try {
                                                switch (input) {

                                                    case 1 -> {
                                                        //대분류 선택
                                                        System.out.println("\n대분류를 선택하세요.");

                                                        int mainCategoryId = createValidMainCategoryId();
                                                        printStocks(stockService.findStocksByParentId(mainCategoryId));

                                                        while (true) {
                                                            System.out.println("1. 중분류 선택 | 2. 이전으로");
                                                            input = Integer.parseInt(br.readLine());

                                                            try {
                                                                switch (input) {
                                                                    case 1 -> {
                                                                        System.out.println("\n중분류를 선택하세요.");
                                                                        int subCategoryId = createValidSubCategoryId(mainCategoryId);
                                                                        printStocks(stockService.findStocksByParentId(subCategoryId));

                                                                        while (true) {
                                                                            System.out.println("1. 소분류 선택 | 2. 이전으로");
                                                                            input = Integer.parseInt(br.readLine());

                                                                            try {
                                                                                switch (input) {
                                                                                    case 1 -> {
                                                                                        System.out.println("\n소분류를 선택하세요.");
                                                                                        int thirdCategoryId = createValidSubCategoryId(subCategoryId);
                                                                                        printStocks(stockService.findStocksByParentId(thirdCategoryId));
                                                                                        break Loop;
                                                                                    }

                                                                                    case 2 -> {
                                                                                        break Loop;
                                                                                    }
                                                                                    default -> System.out.println("잘못된 접근입니다.");
                                                                                }
                                                                            } catch (IOException | NumberFormatException e) {
                                                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                                                            }
                                                                        }
                                                                    }

                                                                    case 2 -> {
                                                                        break Loop;
                                                                    }

                                                                    default -> System.out.println("잘못된 접근입니다.");
                                                                }
                                                            } catch (IOException | NumberFormatException e) {
                                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                                            }
                                                        }
                                                    }

                                                    case 2 -> {
                                                        break Loop;
                                                    }
                                                    default -> System.out.println("잘못된 접근입니다.");
                                                }
                                            } catch (IOException | NumberFormatException e) {
                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                            }
                                        }
                                    }
                                    case 2 -> {
                                        LocalDateTime manufacturedDate = createValidManufacturedDate();

                                        stockService.saveStock(Stock.builder()
                                                .product(Product.builder()
                                                        .id(productService.findOneById(5).getId())
                                                        .build())
                                                .user(user)
                                                .width(createValidWidth())
                                                .height(createValidHeight())
                                                .quantity(createValidQuantity())
                                                .manufacturedDate(manufacturedDate)
                                                .expirationDate(createValidExpirationDate(manufacturedDate))
                                                .build());
                                    }

                                    case 3 -> {
                                        printStocks(stockService.findAllStocks());
                                        int stockId = findValidStockId(stockService.findAllStocks());
                                        LocalDateTime manufacturedDate = createValidManufacturedDate();

                                        stockService.updateStock(StockEditDto.builder()
                                                .id(stockId)
                                                .quantity(createValidQuantity())
                                                .manufacturedDate(manufacturedDate)
                                                .expirationDate(createValidExpirationDate(manufacturedDate))
                                                .build());
                                    }

                                    case 4 -> {
                                        printStocks(stockService.findAllStocks());
                                        int stockId = findValidStockId(stockService.findAllStocks());
                                        stockService.deleteStock(stockId);
                                    }

                                    case 5 -> stockLogService.findStockLogs();

                                    case 6 -> {
                                        return;
                                    }

                                    default -> System.out.println("잘못된 접근입니다.\n");
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
                    Loop:
                    while (true) {
                        try {
                            System.out.println("\n1. 재고 조회 | 2. 재고 등록 | 3. 재고 수정 | 4. 재고 삭제 | 5. 재고실사 조회 | 6. 이전으로");
                            int input = Integer.parseInt(br.readLine());

                            try {
                                switch (input) {
                                    case 1 -> {
                                        printStocks(stockService.findAllStocksByManagerId(user.getId()));

                                        while (true) {
                                            System.out.println("1. 카테고리별 재고 조회 | 2. 이전으로");
                                            input = Integer.parseInt(br.readLine());

                                            try {
                                                switch (input) {

                                                    case 1 -> {
                                                        //대분류 선택
                                                        System.out.println("\n대분류를 선택하세요.");

                                                        int mainCategoryId = createValidMainCategoryId();
                                                        printStocks(stockService.findStocksByParentIdAndManagerId(user.getId(), mainCategoryId));

                                                        while (true) {
                                                            System.out.println("1. 중분류 선택 | 2. 이전으로");
                                                            input = Integer.parseInt(br.readLine());

                                                            try {
                                                                switch (input) {
                                                                    case 1 -> {
                                                                        System.out.println("\n중분류를 선택하세요.");
                                                                        int subCategoryId = createValidSubCategoryId(mainCategoryId);
                                                                        printStocks(stockService.findStocksByParentIdAndManagerId(user.getId(), subCategoryId));

                                                                        while (true) {
                                                                            System.out.println("1. 소분류 선택 | 2. 이전으로");
                                                                            input = Integer.parseInt(br.readLine());

                                                                            try {
                                                                                switch (input) {
                                                                                    case 1 -> {
                                                                                        System.out.println("\n소분류를 선택하세요.");
                                                                                        int thirdCategoryId = createValidSubCategoryId(subCategoryId);
                                                                                        printStocks(stockService.findStocksByParentIdAndManagerId(user.getId(), thirdCategoryId));
                                                                                        break Loop;
                                                                                    }

                                                                                    case 2 -> {
                                                                                        break Loop;
                                                                                    }
                                                                                    default -> System.out.println("잘못된 접근입니다.");
                                                                                }
                                                                            } catch (IOException | NumberFormatException e) {
                                                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                                                            }
                                                                        }
                                                                    }

                                                                    case 2 -> {
                                                                        break Loop;
                                                                    }

                                                                    default -> System.out.println("잘못된 접근입니다.");
                                                                }
                                                            } catch (IOException | NumberFormatException e) {
                                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                                            }
                                                        }
                                                    }

                                                    case 2 -> {
                                                        break Loop;
                                                    }
                                                    default -> System.out.println("잘못된 접근입니다.");
                                                }
                                            } catch (IOException | NumberFormatException e) {
                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                            }
                                        }
                                    }
                                    case 2 -> {
                                        LocalDateTime manufacturedDate = createValidManufacturedDate();

                                        stockService.saveStock(Stock.builder()
                                                .product(Product.builder()
                                                        .id(productService.findOneById(5).getId())
                                                        .build())
                                                .user(user)
                                                .width(createValidWidth())
                                                .height(createValidHeight())
                                                .quantity(createValidQuantity())
                                                .manufacturedDate(manufacturedDate)
                                                .expirationDate(createValidExpirationDate(manufacturedDate))
                                                .build());
                                    }

                                    case 3 -> {
                                        printStocks(stockService.findAllStocksByManagerId(user.getId()));
                                        int stockId = findValidStockId(stockService.findAllStocksByManagerId(user.getId()));
                                        LocalDateTime manufacturedDate = createValidManufacturedDate();

                                        stockService.updateStock(StockEditDto.builder()
                                                .id(stockId)
                                                .quantity(createValidQuantity())
                                                .manufacturedDate(manufacturedDate)
                                                .expirationDate(createValidExpirationDate(manufacturedDate))
                                                .build());
                                    }

                                    case 4 -> {
                                        printStocks(stockService.findAllStocksByManagerId(user.getId()));
                                        int stockId = findValidStockId(stockService.findAllStocksByManagerId(user.getId()));
                                        stockService.deleteStock(stockId);
                                    }

                                    case 5 -> stockLogService.findStockLogs();

                                    case 6 -> {
                                        return;
                                    }
                                    default -> System.out.println("잘못된 접근입니다.\n");
                                }
                            } catch (IOException | NumberFormatException e) {
                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                            }
                        } catch (IOException | NumberFormatException e) {
                            System.out.println("오류가 발생했습니다: " + e.getMessage());
                        }
                    }
                }

                case "BUSINESS_MAN" -> {
                    Loop:
                    while (true) {
                        try {
                            System.out.println("\n1. 재고 조회 | 2. 이전으로");
                            int input = Integer.parseInt(br.readLine());

                            try {
                                switch (input) {
                                    case 1 -> {
                                        printStocks(stockService.findAllStocksByBusinessManId(user.getId()));

                                        while (true) {
                                            System.out.println("1. 카테고리별 재고 조회 | 2. 이전으로");
                                            input = Integer.parseInt(br.readLine());

                                            try {
                                                switch (input) {

                                                    case 1 -> {
                                                        //대분류 선택
                                                        System.out.println("\n대분류를 선택하세요.");

                                                        int mainCategoryId = createValidMainCategoryId();
                                                        printStocks(stockService.findStocksByParentIdAndBusinessManId(user.getId(), mainCategoryId));

                                                        while (true) {
                                                            System.out.println("1. 중분류 선택 | 2. 이전으로");
                                                            input = Integer.parseInt(br.readLine());

                                                            try {
                                                                switch (input) {
                                                                    case 1 -> {
                                                                        System.out.println("\n중분류를 선택하세요.");
                                                                        int subCategoryId = createValidSubCategoryId(mainCategoryId);
                                                                        printStocks(stockService.findStocksByParentIdAndBusinessManId(user.getId(), subCategoryId));

                                                                        while (true) {
                                                                            System.out.println("1. 소분류 선택 | 2. 이전으로");
                                                                            input = Integer.parseInt(br.readLine());

                                                                            try {
                                                                                switch (input) {
                                                                                    case 1 -> {
                                                                                        System.out.println("\n소분류를 선택하세요.");
                                                                                        int thirdCategoryId = createValidSubCategoryId(subCategoryId);
                                                                                        printStocks(stockService.findStocksByParentIdAndBusinessManId(user.getId(), thirdCategoryId));
                                                                                        break Loop;
                                                                                    }

                                                                                    case 2 -> {
                                                                                        break Loop;
                                                                                    }
                                                                                    default -> System.out.println("잘못된 접근입니다.");
                                                                                }
                                                                            } catch (IOException | NumberFormatException e) {
                                                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                                                            }
                                                                        }
                                                                    }

                                                                    case 2 -> {
                                                                        break Loop;
                                                                    }

                                                                    default -> System.out.println("잘못된 접근입니다.");
                                                                }
                                                            } catch (IOException | NumberFormatException e) {
                                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                                            }
                                                        }
                                                    }

                                                    case 2 -> {
                                                        break Loop;
                                                    }
                                                    default -> System.out.println("잘못된 접근입니다.");
                                                }
                                            } catch (IOException | NumberFormatException e) {
                                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                                            }
                                        }
                                    }
                                    case 2 -> {
                                        return;
                                    }
                                }
                            } catch (IOException | NumberFormatException e) {
                                System.out.println("오류가 발생했습니다: " + e.getMessage());
                            }
                        } catch (IOException | NumberFormatException e) {
                            System.out.println("오류가 발생했습니다: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * 유효한 대분류 카테고리 id 입력 받음
     */
    private static int createValidMainCategoryId() throws IOException {
        while (true) {
            List<ProductCategory> categories = productCategoryService.findMainCategories();
            printCategories(categories);
            System.out.print("\n번호 입력: ");

            String categoryId = br.readLine();

            if (isNotNumber(categoryId)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (categories.stream()
                    .map(ProductCategory::getId)
                    .anyMatch(n -> n == Integer.parseInt(categoryId))) {
                return Integer.parseInt(categoryId);
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    /**
     * 유효한 재고 중, 소분류 카테고리 번호를 입력 받음
     */
    private static int createValidSubCategoryId(Integer parentId) throws IOException {
        while (true) {
            List<ProductCategory> categories = productCategoryService.findSubCategories(parentId);
            printCategories(categories);
            System.out.print("\n번호 입력: ");

            String categoryId = br.readLine();

            if (isNotNumber(categoryId)) {
                System.out.println("숫자가 아닙니다.");
                continue;
            }
            if (categories.stream()
                    .map(ProductCategory::getId)
                    .anyMatch(n -> n == Integer.parseInt(categoryId))) {
                return Integer.parseInt(categoryId);
            } else {
                System.out.println("잘못된 입력입니다.");
            }
        }
    }

    /**
     * 유효한 재고 가로 사이즈를 입력 받음
     *
     * @return 재고 가로 사이즈
     */
    private static Double createValidWidth() throws IOException {
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
    private static Double createValidHeight() throws IOException {
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
    private static Integer createValidQuantity() throws IOException {
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

    private static LocalDateTime createValidManufacturedDate() throws IOException {
        System.out.println("제조기간을 입력합니다.");
        return createValidTime(createValidDate());
    }

    /**
     * 유효한 유효기간을 입력 받음
     *
     * @return 유효기간
     */
    private static LocalDateTime createValidExpirationDate(LocalDateTime manufacturedDate) throws
            IOException {
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
    private static LocalDate createValidDate() throws IOException {
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
    private static LocalDateTime createValidTime(LocalDate date) throws IOException {
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
                    Integer.parseInt(hour) - 1, Integer.parseInt(minute), Integer.parseInt(second));
        }
    }

    /**
     * 유효한 재고 번호를 입력 받음
     * 재고 내역 수정 및 삭제 시 사용
     */
    private static int findValidStockId(List<StockDto> stocks) throws IOException {
        while (true) {
//            findStocks(user);
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
     * 숫자를 입력 받을 때 문자, 공백, 특수문자가 포함되어 있는지 확인함
     */
    private static boolean isNotNumber(String input) {
        return !input.matches("^[0-9]+$");
    }

    private static void printCategories(List<ProductCategory> categories) {
        System.out.print("\n\n[분류 선택]\n");
        System.out.println("-".repeat(50));
        System.out.printf("%-5s| %-10s\n",
                "번호", "분류명");
        System.out.println("-".repeat(50));

        for (ProductCategory category : categories) {
            System.out.printf("%-10d%-10s\n",
                    category.getId(), category.getName());
        }
    }

    private static void printStocks(List<StockDto> stocks) {
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
}
