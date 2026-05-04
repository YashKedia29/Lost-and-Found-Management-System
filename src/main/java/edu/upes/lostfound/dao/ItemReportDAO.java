package edu.upes.lostfound.dao;

import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.ReportStatus;
import edu.upes.lostfound.model.ReportType;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemReportDAO extends BaseDAO {
    public ItemReport create(ItemReport report) throws SQLException {
        String sql = """
                INSERT INTO item_reports(
                    user_id, report_type, item_name, category, description, location,
                    item_date, image_path, contact_name, contact_phone, storage_location, status
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            fillReportStatement(stmt, report);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    report.setId(keys.getInt(1));
                }
            }
        }
        return report;
    }

    public Optional<ItemReport> findById(int id) throws SQLException {
        String sql = "SELECT * FROM item_reports WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapReport(rs));
                }
            }
        }
        return Optional.empty();
    }

    public List<ItemReport> findRecent(int limit, Integer userId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM item_reports");
        if (userId != null) {
            sql.append(" WHERE user_id = ?");
        }
        sql.append(" ORDER BY created_at DESC LIMIT ?");

        List<ItemReport> reports = new ArrayList<>();
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            if (userId != null) {
                stmt.setInt(index++, userId);
            }
            stmt.setInt(index, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    public List<ItemReport> search(ReportType type, String keyword, String category, String location,
                                   LocalDate fromDate, LocalDate toDate, Integer userId) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT * FROM item_reports WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (type != null) {
            sql.append(" AND report_type = ?");
            params.add(type.name());
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (LOWER(item_name) LIKE ? OR LOWER(description) LIKE ?)");
            String like = "%" + keyword.toLowerCase() + "%";
            params.add(like);
            params.add(like);
        }
        if (category != null && !category.isBlank() && !"All Categories".equals(category)) {
            sql.append(" AND category = ?");
            params.add(category);
        }
        if (location != null && !location.isBlank() && !"All Locations".equals(location)) {
            sql.append(" AND location = ?");
            params.add(location);
        }
        if (fromDate != null) {
            sql.append(" AND item_date >= ?");
            params.add(Date.valueOf(fromDate));
        }
        if (toDate != null) {
            sql.append(" AND item_date <= ?");
            params.add(Date.valueOf(toDate));
        }
        if (userId != null) {
            sql.append(" AND user_id = ?");
            params.add(userId);
        }
        sql.append(" ORDER BY created_at DESC");

        List<ItemReport> reports = new ArrayList<>();
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            bind(stmt, params);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    public List<ItemReport> findCounterpartCandidates(ItemReport report) throws SQLException {
        ReportType opposite = report.getReportType() == ReportType.LOST ? ReportType.FOUND : ReportType.LOST;
        String sql = """
                SELECT * FROM item_reports
                WHERE report_type = ?
                  AND id <> ?
                  AND status IN ('APPROVED', 'MATCHED')
                ORDER BY created_at DESC
                """;
        List<ItemReport> reports = new ArrayList<>();
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, opposite.name());
            stmt.setInt(2, report.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    reports.add(mapReport(rs));
                }
            }
        }
        return reports;
    }

    public void updateStatus(int reportId, ReportStatus status) throws SQLException {
        String sql = "UPDATE item_reports SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, reportId);
            stmt.executeUpdate();
        }
    }

    public void markMatched(int reportId) throws SQLException {
        String sql = """
                UPDATE item_reports
                SET status = CASE WHEN status = 'CLAIMED' THEN 'CLAIMED' ELSE 'MATCHED' END,
                    updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            stmt.executeUpdate();
        }
    }

    private void fillReportStatement(PreparedStatement stmt, ItemReport report) throws SQLException {
        stmt.setInt(1, report.getUserId());
        stmt.setString(2, report.getReportType().name());
        stmt.setString(3, report.getItemName());
        stmt.setString(4, report.getCategory());
        stmt.setString(5, report.getDescription());
        stmt.setString(6, report.getLocation());
        stmt.setDate(7, Date.valueOf(report.getItemDate()));
        stmt.setString(8, report.getImagePath());
        stmt.setString(9, report.getContactName());
        stmt.setString(10, report.getContactPhone());
        stmt.setString(11, report.getStorageLocation());
        stmt.setString(12, report.getStatus().name());
    }

    private void bind(PreparedStatement stmt, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object value = params.get(i);
            if (value instanceof Date date) {
                stmt.setDate(i + 1, date);
            } else if (value instanceof Integer integer) {
                stmt.setInt(i + 1, integer);
            } else {
                stmt.setString(i + 1, String.valueOf(value));
            }
        }
    }

    private ItemReport mapReport(ResultSet rs) throws SQLException {
        ItemReport report = new ItemReport();
        report.setId(rs.getInt("id"));
        report.setUserId(rs.getInt("user_id"));
        report.setReportType(ReportType.valueOf(rs.getString("report_type")));
        report.setItemName(rs.getString("item_name"));
        report.setCategory(rs.getString("category"));
        report.setDescription(rs.getString("description"));
        report.setLocation(rs.getString("location"));
        Date itemDate = rs.getDate("item_date");
        if (itemDate != null) {
            report.setItemDate(itemDate.toLocalDate());
        }
        report.setImagePath(rs.getString("image_path"));
        report.setContactName(rs.getString("contact_name"));
        report.setContactPhone(rs.getString("contact_phone"));
        report.setStorageLocation(rs.getString("storage_location"));
        report.setStatus(ReportStatus.valueOf(rs.getString("status")));
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) {
            report.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            report.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return report;
    }
}
