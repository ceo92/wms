package service;

import connection.DriverManagerDBConnectionUtil;
import dao.InboundDao;
import dao.InboundItemDao;
import dao.StockDao;
import dao.StockSectionDao;
import domain.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InboundService {

    private final InboundDao inboundDao = new InboundDao();
    private final InboundItemDao inboundItemDao = new InboundItemDao();

    /**
     * 입고, 입고 품목 등록
     *
     * @param inbound
     * @param items
     */
    public void saveInbound(Inbound inbound, List<InboundItem> items) {
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setAutoCommit(false);
            int inboundId = inboundDao.save(con, inbound);
            boolean result = inboundItemDao.saveInboundItems(con, inboundId, items);
            if(result) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException e) {
            transactionRollback(con);
            throw new RuntimeException(e);
        } finally {
            connectionClose(con);
        }
    }

    /**
     * 입고 단건 조회
     *
     * @param id 입고 id
     * @return
     */
    public Inbound findInboundById(int id) {
        // TODO: id 검증
        Connection con = null;
        try {
            con = DriverManagerDBConnectionUtil.getInstance().getConnection();
            con.setReadOnly(true);
            Inbound inbound = inboundDao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 입고 요청입니다."));
            con.setReadOnly(false);
            return inbound;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

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
