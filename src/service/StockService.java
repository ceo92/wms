package service;

import dao.StockDao;
import domain.ProductCategory;
import dto.StockDto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class StockService {
    private static final StockDao stockDao = new StockDao();
    private static final ProductCategoryService productCategoryService = new ProductCategoryService();
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

    /**
     * 재고 조회
     * 총 관리자: 전체 재고 내역 조회
     * 창고 관리자: 관리하는 창고의 재고만 조회
     * 사업자: 자신의 재고만 조회
     *
     * @User: 총 관리자, 창고 관리자, 사업자
     */
    public void findStocks(User user) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            switch (user.getRoleType().toString()) {
                case "ADMIN" -> printStocks(stockDao.findAll(con));

                case "WAREHOUSE_MANAGER" -> printStocks(stockDao.findByManagerId(con, user.getId()));

                case "BUSINESS_MAN" -> printStocks(stockDao.findByBusinessManId(con, user.getId()));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 카테고리별 재고 조회 (대, 중, 소분류)
     *
     * @User: 총 관리자, 창고 관리자, 사업자
     */
    public void findStocksByCategories() throws IOException {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            //대분류 선택
            System.out.println("\n대분류를 선택하세요.");
            int mainCategoryId = createValidMainCategoryId();
            printStocks(stockDao.findByParentId(con, mainCategoryId));

            //중분류 선택
            Loop:
            while (true) {
                System.out.println("\n1. 중분류 선택 | 2. 나가기");
                String input = br.readLine();

                if (isNotNumber(input)) {
                    System.out.println("숫자가 아닙니다.");
                    continue;
                }
                if (Integer.parseInt(input) == 1) {
                    System.out.println("\n중분류를 선택하세요.");
                    int subCategoryId = createValidSubCategoryId(mainCategoryId);
                    printStocks(stockDao.findByParentId(con, subCategoryId));

                    //소분류 선택
                    while (true) {
                        System.out.println("\n1. 소분류 선택 | 2. 나가기");
                        input = br.readLine();

                        if (isNotNumber(input)) {
                            System.out.println("숫자가 아닙니다.");
                            continue;
                        }
                        if (Integer.parseInt(input) == 1) {
                            System.out.println("\n소분류를 선택하세요.");
                            printStocks(stockDao.findByParentId(con, createValidSubCategoryId(subCategoryId)));
                            break Loop;
                        } else if (Integer.parseInt(input) != 2) {
                            System.out.println("\n잘못된 번호입니다.");
                            continue;
                        }
                        break Loop;
                    }
                } else if (Integer.parseInt(input) == 2) {
                    break;
                } else {
                    System.out.println("잘못된 번호입니다.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 유효한 재고 대분류 카테고리 번호를 입력 받음
     */
    private int createValidMainCategoryId() throws IOException {
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
    private int createValidSubCategoryId(Integer parentId) throws IOException {
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

    private void printCategories(List<ProductCategory> categories) {
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
