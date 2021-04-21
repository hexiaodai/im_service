package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class Db {
    static  DataSource ds;
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/im_java?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8&useSSL=false");
        config.setUsername("root");
        config.setPassword("hjm19980819");
        config.addDataSourceProperty("connectionTimeout", "1000"); // 连接超时：1秒
        config.addDataSourceProperty("idleTimeout", "600000"); // 空闲超时：60秒
        config.addDataSourceProperty("maximumPoolSize", "300"); // 最大连接数：10
        ds = new HikariDataSource(config);
    }
    public static Connection getConn() {
        try{
            return ds.getConnection();
        }catch (SQLException e){
            return null;
        }
    }

    public static void main(String[] args){
        System.out.println(getConn());
    }
}
