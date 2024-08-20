package connection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;


public class HikariCpDBConnectionUtil {
  private final String dbUrl;
  private final String dbUsername;
  private final String dbPassword;
  private static final HikariCpDBConnectionUtil hikariCpDbConnectonUtil = new HikariCpDBConnectionUtil();
  private HikariCpDBConnectionUtil(){
    String propertiesFilePath = "src/application.properties";
    Properties properties = new Properties();
    try(InputStream input = new FileInputStream(propertiesFilePath)){
      properties.load(input);
      dbUrl = properties.getProperty("database.url");
      dbUsername = properties.getProperty("database.username");
      dbPassword = properties.getProperty("database.password");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }
  public static HikariCpDBConnectionUtil getInstance(){
    return hikariCpDbConnectonUtil;
  }

  public Connection getConnection(){
    try {
      HikariDataSource dataSource = getDataSource();
      return dataSource.getConnection();
    }catch (SQLException e){
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private HikariDataSource getDataSource() throws IOException {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(dbUrl); // 데이터베이스 URL
    config.setUsername(dbUsername); // 데이터베이스 사용자 이름
    config.setPassword(dbPassword); // 데이터베이스 비밀번호
    config.setDriverClassName("com.mysql.cj.jdbc.Driver"); // JDBC 드라이버 클래스 이름
    // 추가 설정 (옵션)
    config.setMaximumPoolSize(10); // 커넥션 풀의 최대 커넥션 수
    config.setMinimumIdle(5); // 최소 아이들 커넥션 수
    config.setConnectionTimeout(30000); // 커넥션 타임아웃 (밀리초)

    return new HikariDataSource(config);
  }


}
