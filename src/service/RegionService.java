package service;

import connection.DBConnectionUtil;
import dao.RegionDao;
import domain.Region;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RegionService {

    private final RegionDao dao = new RegionDao();

    public List<Region> findAllRegions() {
        Connection con = getConnection();
        try {
            con.setReadOnly(true);
            List<Region> regions = dao.findAll(con);
            con.setReadOnly(false);
            return regions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
  
    public Region findRegionById(int id) {
        Connection con = getConnection();
        try {
            con.setReadOnly(true);
            Region region = dao.findById(con, id).orElseThrow(
                    () -> new RuntimeException("존재하지 않는 지역입니다."));
            con.setReadOnly(false);
            return region;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
  
  private static Connection getConnection(){
    return DBConnectionUtil.getConnection();
  }

  private static void closeConnection(Connection con){
    if (con != null) {
      try {
        con.setAutoCommit(true); //종료 시에는 자동 커밋 모드로 ! , 커넥션 풀 쓸 경우
        if (con.isReadOnly()){
          con.setReadOnly(false);
        }
      } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }
    

}
