package service;

import connection.DriverManagerDBConnectionUtil;
import dao.RegionDao;
import domain.Region;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class RegionService {

    private final RegionDao dao = new RegionDao();

    public List<Region> findAllRegions() {
        Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection();
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
        Connection con = DriverManagerDBConnectionUtil.getInstance().getConnection();
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
}
