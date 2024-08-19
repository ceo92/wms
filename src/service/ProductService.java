package service;

import connection.DriverManagerDBConnectionUtil;
import dao.ProductDao;
import domain.Product;

import java.sql.Connection;
import java.sql.SQLException;

public class ProductService {
    private final static ProductDao productDao = new ProductDao();

    public Product findOneById(int id) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            return productDao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 제품입니다."));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
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
