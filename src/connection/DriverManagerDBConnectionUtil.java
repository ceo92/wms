package connection;



import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DriverManagerDBConnectionUtil {

  public static Connection getConnection() {
    Properties properties = new Properties();

    // application.properties 파일 경로를 지정합니다.
    String propertiesFilePath = "src/application.properties";
    Connection con = null;
    try (InputStream input = new FileInputStream(propertiesFilePath)) {
      // properties 파일을 로드합니다.
      properties.load(input);

      // properties 값을 가져옵니다.
      String dbUrl = properties.getProperty("database.url");
      String dbUsername = properties.getProperty("database.username");
      String dbPassword = properties.getProperty("database.password");
      con = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (SQLException e) {
      throw new IllegalStateException(e.getMessage());
    }
    return con;

  }
}
