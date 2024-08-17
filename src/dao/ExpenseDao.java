package dao;

import domain.Expense;
import domain.ExpenseCategory;
import dto.ExpenseEditDto;
import dto.ExpenseSaveDto;
import dto.ProfitDto;
import dto.TotalAssetDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDao {
    public List<Expense> findAll(Connection con) {
        String query = new StringBuilder()
                .append("SELECT e.id, w.name, expense_date, ec.name, expense_amount, description, payment_method ")
                .append("FROM expense e ")
                .append("JOIN expense_category ec ON e.category_id = ec.id ")
                .append("JOIN warehouse w ON e.warehouse_id = w.id ")
                .append("ORDER BY e.id ").toString();

        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                expenses.add(Expense.builder()
                        .id(rs.getInt("e.id"))
                        .warehouse(Warehouse.builder()
                                .name(rs.getString("w.name"))
                                .build())
                        .expenseDate(rs.getDate("expense_date").toLocalDate())
                        .expenseCategory(ExpenseCategory.builder()
                                .name(rs.getString("ec.name"))
                                .build())
                        .expenseAmount(rs.getDouble("expense_amount"))
                        .description(rs.getString("description"))
                        .paymentMethod(rs.getString("payment_method"))
                        .build());
            }
            return expenses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Expense> findById(Connection con, Integer userId) {
        String query = new StringBuilder()
                .append("SELECT e.id, w.name, expense_date, ec.name, expense_amount, description, payment_method ")
                .append("FROM expense e ")
                .append("JOIN expense_category ec ON e.category_id = ec.id ")
                .append("JOIN warehouse w ON e.warehouse_id = w.id ")
                .append("JOIN User u ON w.manager_id = u.role_id ")
                .append("WHERE u.role_id = ? ")
                .append("ORDER BY e.id ").toString();

        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(Expense.builder()
                            .id(rs.getInt("e.id"))
                            .warehouse(Warehouse.builder()
                                    .name(rs.getString("w.name"))
                                    .build())
                            .expenseDate(rs.getDate("expense_date").toLocalDate())
                            .expenseCategory(ExpenseCategory.builder()
                                    .name(rs.getString("ec.name"))
                                    .build())
                            .expenseAmount(rs.getDouble("expense_amount"))
                            .description(rs.getString("description"))
                            .paymentMethod(rs.getString("payment_method"))
                            .build());
                }
                return expenses;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Expense> findAllByCategory(Connection con, Integer categoryId) {
        String query = new StringBuilder()
                .append("SELECT e.id, w.name, expense_date, ec.name, expense_amount, description, payment_method ")
                .append("FROM expense e ")
                .append("JOIN expense_category ec ON e.category_id = ec.id ")
                .append("JOIN warehouse w ON e.warehouse_id = w.id ")
                .append("WHERE ec.id = ? ")
                .append("ORDER BY e.id ").toString();

        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(Expense.builder()
                            .id(rs.getInt("e.id"))
                            .warehouse(Warehouse.builder()
                                    .name(rs.getString("w.name"))
                                    .build())
                            .expenseDate(rs.getDate("expense_date").toLocalDate())
                            .expenseCategory(ExpenseCategory.builder()
                                    .name(rs.getString("ec.name"))
                                    .build())
                            .expenseAmount(rs.getDouble("expense_amount"))
                            .description(rs.getString("description"))
                            .paymentMethod(rs.getString("payment_method"))
                            .build());
                }
                return expenses;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Expense> findByCategory(Connection con, Integer userId, Integer categoryId) {
        String query = new StringBuilder()
                .append("SELECT e.id, w.name, expense_date, ec.name, expense_amount, description, payment_method ")
                .append("FROM expense e ")
                .append("JOIN expense_category ec ON e.category_id = ec.id ")
                .append("JOIN warehouse w ON e.warehouse_id = w.id ")
                .append("JOIN User u ON w.manager_id = u.role_id ")
                .append("WHERE u.role_id = ? AND ec.id = ? ")
                .append("ORDER BY e.id ").toString();

        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, categoryId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(Expense.builder()
                            .id(rs.getInt("e.id"))
                            .warehouse(Warehouse.builder()
                                    .name(rs.getString("w.name"))
                                    .build())
                            .expenseDate(rs.getDate("expense_date").toLocalDate())
                            .expenseCategory(ExpenseCategory.builder()
                                    .name(rs.getString("ec.name"))
                                    .build())
                            .expenseAmount(rs.getDouble("expense_amount"))
                            .description(rs.getString("description"))
                            .paymentMethod(rs.getString("payment_method"))
                            .build());
                }
                return expenses;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Expense> findAllByYear(Connection con, Integer year) {
        String query = new StringBuilder()
                .append("SELECT e.id, w.name, expense_date, ec.name, expense_amount, description, payment_method ")
                .append("FROM expense e ")
                .append("JOIN expense_category ec ON e.category_id = ec.id ")
                .append("JOIN warehouse w ON e.warehouse_id = w.id ")
                .append("WHERE YEAR(expense_date) = ? ")
                .append("ORDER BY e.id ").toString();

        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(Expense.builder()
                            .id(rs.getInt("e.id"))
                            .warehouse(Warehouse.builder()
                                    .name(rs.getString("w.name"))
                                    .build())
                            .expenseDate(rs.getDate("expense_date").toLocalDate())
                            .expenseCategory(ExpenseCategory.builder()
                                    .name(rs.getString("ec.name"))
                                    .build())
                            .expenseAmount(rs.getDouble("expense_amount"))
                            .description(rs.getString("description"))
                            .paymentMethod(rs.getString("payment_method"))
                            .build());
                }
            }
            return expenses;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Expense> findByYear(Connection con, Integer userId, Integer year) {
        String query = new StringBuilder()
                .append("SELECT e.id, w.name, expense_date, ec.name, expense_amount, description, payment_method ")
                .append("FROM expense e ")
                .append("JOIN expense_category ec ON e.category_id = ec.id ")
                .append("JOIN warehouse w ON e.warehouse_id = w.id ")
                .append("JOIN User u ON w.manager_id = u.role_id ")
                .append("WHERE u.role_id = ? AND YEAR(expense_date) = ? ")
                .append("ORDER BY e.id ").toString();

        List<Expense> expenses = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    expenses.add(Expense.builder()
                            .id(rs.getInt("e.id"))
                            .warehouse(Warehouse.builder()
                                    .name(rs.getString("w.name"))
                                    .build())
                            .expenseDate(rs.getDate("expense_date").toLocalDate())
                            .expenseCategory(ExpenseCategory.builder()
                                    .name(rs.getString("ec.name"))
                                    .build())
                            .expenseAmount(rs.getDouble("expense_amount"))
                            .description(rs.getString("description"))
                            .paymentMethod(rs.getString("payment_method"))
                            .build());
                }
                return expenses;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProfitDto> findProfits(Connection con) {
        String query = new StringBuilder()
                .append("SELECT w.name, contract_date, (price_per_area * capacity * contract_month) AS profit ")
                .append("FROM warehouse_contract wc ")
                .append("JOIN warehouse w ON wc.warehouse_id = w.id ").toString();

        List<ProfitDto> profits = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                profits.add(ProfitDto.builder()
                        .name(rs.getString("w.name"))
                        .contractDate(rs.getDate("contract_date").toLocalDate())
                        .profit(rs.getDouble("profit"))
                        .build());
            }
            return profits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProfitDto> findProfitById(Connection con, Integer userId) {
        String query = new StringBuilder()
                .append("SELECT w.name, contract_date, (price_per_area * capacity * contract_month) AS profit ")
                .append("FROM warehouse_contract wc ")
                .append("JOIN warehouse w ON wc.warehouse_id = w.id ")
                .append("JOIN User u ON w.manager_id = u.role_id ")
                .append("WHERE u.role_id = ? ").toString();

        List<ProfitDto> profits = new ArrayList<>();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    profits.add(ProfitDto.builder()
                            .name(rs.getString("w.name"))
                            .contractDate(rs.getDate("contract_date").toLocalDate())
                            .profit(rs.getDouble("profit"))
                            .build());
                }
            }
            return profits;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public TotalAssetDto findTotalAsset(Connection con) {
        String query = new StringBuilder()
                .append("WITH year_sum_expense AS ( ")
                .append("SELECT YEAR(expense_date) AS year1, SUM(expense_amount) AS sum_expense ")
                .append("FROM expense ")
                .append("GROUP BY year1), ")
                .append("year_sum_profit AS ( ")
                .append("SELECT YEAR(contract_date) AS year2, SUM(price_per_area * capacity * contract_month) AS sum_profit ")
                .append("FROM warehouse_contract wc ")
                .append("JOIN warehouse w ON wc.warehouse_id = w.id ")
                .append("GROUP BY year2), ")
                .append("ranked AS( ")
                .append("SELECT yse.year1, yse.sum_expense, ysp.sum_profit, (ysp.sum_profit - yse.sum_expense)AS net_profit, ROW_NUMBER()OVER(ORDER BY yse.year1)AS rn ")
                .append("FROM year_sum_expense yse ")
                .append("JOIN year_sum_profit ysp ON yse.year1 = ysp.year2 ) ")
                .append("SELECT sum_expense, sum_profit, net_profit, ")
                .append("((SELECT net_profit FROM ranked WHERE rn = 2) - ")
                .append("(SELECT net_profit FROM ranked WHERE rn = 1)) / (SELECT net_profit FROM ranked WHERE rn = 1) AS net_profit_per ")
                .append("FROM ranked; ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            rs.next();
            return TotalAssetDto.builder()
                    .sumExpense(rs.getDouble("sum_expense"))
                    .sumProfit(rs.getDouble("sum_profit"))
                    .netProfit(rs.getDouble("net_profit"))
                    .netProfitPer(rs.getDouble("net_profit_per"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int saveExpense(Connection con, ExpenseSaveDto request) {
        String query = new StringBuilder()
                .append("INSERT INTO expense (warehouse_id, expense_date, category_id, expense_amount, description, payment_method) VALUES ")
                .append("(?, ?, ?, ?, ?, ? ) ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setInt(1, request.getWarehouseId());
            pstmt.setDate(2, java.sql.Date.valueOf(request.getExpenseDate()));
            pstmt.setInt(3, request.getCategoryId());
            pstmt.setDouble(4, request.getExpenseAmount());
            pstmt.setString(5, request.getDescription());
            pstmt.setString(6, request.getPaymentMethod());

            if (pstmt.executeUpdate() == 1) {
                con.commit();
                System.out.println("지출이 등록되었습니다.");
                return 1;
            } else {
                throw new RuntimeException("지출을 등록할 수 없습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int updateExpense(Connection con, ExpenseEditDto request) {
        String query = new StringBuilder()
                .append("UPDATE expense ")
                .append("SET expense_date = ? , category_id = ? , expense_amount = ? , description = ? , payment_method = ? , mod_date = ? ")
                .append("WHERE id = ? ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {

            pstmt.setDate(1, java.sql.Date.valueOf(request.getExpenseDate()));
            pstmt.setInt(2, request.getCategoryId());
            pstmt.setDouble(3, request.getExpenseAmount());
            pstmt.setString(4, request.getDescription());
            pstmt.setString(5, request.getPaymentMethod());
            pstmt.setTimestamp(6, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(7, request.getId());

            if (pstmt.executeUpdate() == 1) {
                con.commit();
                System.out.println("지출 내역이 수정되었습니다.");
                return 1;
            } else {
                throw new RuntimeException("해당 지출 내역을 수정할 수 없습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int deleteExpense(Connection con, Integer expenseId) {
        String query = new StringBuilder()
                .append("DELETE FROM expense ")
                .append("WHERE id = ? ").toString();

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setInt(1, expenseId);

            if (pstmt.executeUpdate() == 1) {
                con.commit();
                System.out.println("지출 내역이 삭제되었습니다.");
                return 1;
            } else {
                throw new RuntimeException("해당 지출 내역을 삭제할 수 없습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
