package service;

import dao.ExpenseDao;
import domain.Expense;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExpenseService {
    private static final ExpenseDao expenseDao = new ExpenseDao();
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
