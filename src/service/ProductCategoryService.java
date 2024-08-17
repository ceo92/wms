package service;

import connection.DriverManagerDBConnectionUtil;
import dao.ProductCategoryDao;
import domain.ProductCategory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class ProductCategoryService {

    private static final ProductCategoryDao productCategoryDao = new ProductCategoryDao();

    public List<ProductCategory> findMainCategories() {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            return productCategoryDao.findByParentIdIsNull(con);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    public List<ProductCategory> findSubCategories(Integer parentId) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);

            return productCategoryDao.findByParentId(con, parentId);
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
