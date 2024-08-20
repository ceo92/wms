package service;

import connection.DriverManagerDBConnectionUtil;
import dao.ExpenseDao;
import domain.Expense;
import dto.ExpenseEditDto;
import dto.ExpenseSaveDto;
import dto.ProfitDto;
import dto.TotalAssetDto;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ExpenseService {
    private static final ExpenseDao expenseDao = new ExpenseDao();

    /**
     * 지출 전체 조회
     *
     * @User: 총 관리자
     */
    public List<Expense> findAllExpenses() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findAll(con);
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
    public List<Expense> findAllExpensesById(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findById(con, id);
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
    public List<Expense> findAllExpensesByCategoryId(Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findAllByCategory(con, categoryId);
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
    public List<Expense> findAllExpensesByIdAndCategory(Integer id, Integer categoryId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findByCategory(con, id, categoryId);
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
    public List<Expense> findExpensesByYear(Integer year) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findAllByYear(con, year);
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
    public List<Expense> findExpensesByIdAndYear(Integer id, Integer year) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findByYear(con, id, year);
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
    public List<ProfitDto> findAllProfit() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findProfits(con);
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
    public List<ProfitDto> findProfitById(Integer id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return expenseDao.findProfitById(con, id);
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
    public TotalAssetDto findTotalAsset() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            return (expenseDao.findTotalAsset(con));
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
