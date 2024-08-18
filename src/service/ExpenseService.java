package service;

import connection.DriverManagerDBConnectionUtil;
import dao.ExpenseDao;
import domain.Expense;
import dto.ExpenseEditDto;
import dto.ExpenseSaveDto;
import dto.ProfitDto;
import dto.TotalAssetDto;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ExpenseService {
    private static final ExpenseDao expenseDao = new ExpenseDao();

    /**
     * 지출 전체 조회
     *
     * @User: 총 관리자
     */
    public void findAllExpenses() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printExpenses(expenseDao.findAll(con));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 지출 전체 조회
     *
     * @param id: 창고 관리자 id
     * @User: 창고 관리자
     */
    public void findAllExpensesById(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printExpenses(expenseDao.findById(con, id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 지출 구분별 내역 조회
     *
     * @param categoryId: 지출 구분 id
     * @User: 총 관리자
     */
    public void findAllExpensesByCategoryId(Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printExpenses(expenseDao.findAllByCategory(con, categoryId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 지출 구분별 내역 조회
     *
     * @param id:         창고 관리자 id
     * @param categoryId: 지출 구분 id
     * @User: 창고 관리자
     */
    public void findAllExpensesByIdAndCategory(Integer id, Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printExpenses(expenseDao.findByCategory(con, id, categoryId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 연간 지출 내역 조회
     *
     * @param year: 연도
     * @User: 총 관리자
     */
    public void findExpensesByYear(Integer year) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printExpenses(expenseDao.findAllByYear(con, year));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 연간 지출 내역 조회
     *
     * @param id:   창고 관리자 id
     * @param year: 연도
     * @User: 창고 관리자
     */
    public void findExpensesByIdAndYear(Integer id, Integer year) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printExpenses(expenseDao.findByYear(con, id, year));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 매출 내역 조회
     *
     * @User: 총 관리자
     */
    public void findAllProfit() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printProfits(expenseDao.findProfits(con));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 매출 내역 조회
     *
     * @param id: 창고 관리자 id
     * @User: 창고 관리자
     */
    public void findProfitById(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            printProfits(expenseDao.findProfitById(con, id));
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
    public void saveExpense(ExpenseSaveDto request) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = expenseDao.saveExpense(con, request);

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
    public void updateExpense(ExpenseEditDto request) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = expenseDao.updateExpense(con, request);

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
    public void deleteExpense(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int result = expenseDao.deleteExpense(con, id);

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
