package edu.upes.lostfound.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Database {
    private Database() {
    }

    public static Connection getConnection() throws SQLException {
        String url = AppConfig.get("db.url", "jdbc:mysql://localhost:3306/upes_lost_found");
        String user = AppConfig.get("db.user", "root");
        String password = AppConfig.get("db.password", "root");
        return DriverManager.getConnection(url, user, password);
    }
}
