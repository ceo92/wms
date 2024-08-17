package dao;

import domain.Expense;
import domain.ExpenseCategory;
import dto.ProfitDto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
}
