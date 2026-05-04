package edu.upes.lostfound.dao;

import edu.upes.lostfound.model.DashboardStats;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardDAO extends BaseDAO {
    public DashboardStats fetchStats(Integer userId) throws SQLException {
        DashboardStats stats = new DashboardStats();
        stats.setTotalLost(countReports("LOST", null, userId));
        stats.setTotalFound(countReports("FOUND", null, userId));
        stats.setMatchedItems(countReports(null, "MATCHED", userId));
        stats.setClaimedItems(countReports(null, "CLAIMED", userId));
        stats.setPendingClaims(countPendingClaims(userId));
        return stats;
    }

    private int countReports(String type, String status, Integer userId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM item_reports WHERE 1=1");
        if (type != null) {
            sql.append(" AND report_type = ?");
        }
        if (status != null) {
            sql.append(" AND status = ?");
        }
        if (userId != null) {
            sql.append(" AND user_id = ?");
        }
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (type != null) {
                stmt.setString(index++, type);
            }
            if (status != null) {
                stmt.setString(index++, status);
            }
            if (userId != null) {
                stmt.setInt(index, userId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private int countPendingClaims(Integer userId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM claim_requests WHERE status = 'PENDING'");
        if (userId != null) {
            sql.append(" AND claimant_id = ?");
        }
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            if (userId != null) {
                stmt.setInt(1, userId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}
