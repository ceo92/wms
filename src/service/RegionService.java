package service;

import connection.HikariCpDBConnectionUtil;
import dao.RegionDao;
import domain.Region;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RegionService {
  private final RegionDao regionDao = new RegionDao();

  public List<Region> findAllRegions() {
    Connection con = null;
    try {
      con = getConnection();
      con.setReadOnly(true);
      return regionDao.findAll(con);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
  }

  public Region findRegionById(int id) {
    Connection con = null;
    try {
      con = getConnection();
      con.setReadOnly(true);
      return regionDao.findById(con, id).orElseThrow(() -> new RuntimeException("존재하지 않는 지역입니다."));
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }finally {
      closeConnection(con);
    }
  }

  private static Connection getConnection(){
    return HikariCpDBConnectionUtil.getInstance().getConnection();
  }

  private static void closeConnection(Connection con){
    if (con != null) {
      try {
        con.setAutoCommit(true); //종료 시에는 자동 커밋 모드로 ! , 커넥션 풀 쓸 경우
        if (con.isReadOnly()){
          con.setReadOnly(false);
        }
        con.close(); //Connection 닫기
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
  }

}
