package service;

import java.sql.Connection;
import java.sql.SQLException;

public class InboundService {


    /**
     * 트랜잭션 롤백
     *
     * @param con
     */
    private void transactionRollback(Connection con) {
        try {
            if(con != null) {
                con.rollback();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 데이터베이스 커넥션 close
     *
     * @param con
     */
    private void connectionClose(Connection con) {
        try {
            if(con != null) {
                con.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
