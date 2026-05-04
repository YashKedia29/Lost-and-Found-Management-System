package edu.upes.lostfound.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO extends BaseDAO {
    public void create(int userId, String title, String message) throws SQLException {
        String sql = "INSERT INTO notifications(user_id, title, message) VALUES (?, ?, ?)";
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.executeUpdate();
        }
    }

    public List<String> findLatestForUser(int userId, int limit) throws SQLException {
        String sql = """
                SELECT title, message
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
                LIMIT ?
                """;
        List<String> notifications = new ArrayList<>();
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(rs.getString("title") + ": " + rs.getString("message"));
                }
            }
        }
        return notifications;
    }
}
