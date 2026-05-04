package edu.upes.lostfound.dao;

import edu.upes.lostfound.config.Database;

import java.sql.Connection;
import java.sql.SQLException;

abstract class BaseDAO {
    protected Connection connection() throws SQLException {
        return Database.getConnection();
    }
}
