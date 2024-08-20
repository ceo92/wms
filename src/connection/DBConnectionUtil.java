package connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DBConnectionUtil {
    //java.sql.Connection이 JDBC 표준 인터페이스가 제공하는 Connection
    public static Connection getConnection(){
        String propertiesFilePath = "src/application.properties";
        Properties properties = new Properties();
        try(InputStream input = new FileInputStream(propertiesFilePath)){
            properties.load(input);
            String dbUrl = properties.getProperty("database.url");
            String dbUsername = properties.getProperty("database.username");
            String dbPassword = properties.getProperty("database.password");
            Connection connection = DriverManager.getConnection(dbUrl ,dbUsername , dbPassword);
            log.info("get connection={} , class={}", connection, connection.getClass());
            return connection;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
    }

}
