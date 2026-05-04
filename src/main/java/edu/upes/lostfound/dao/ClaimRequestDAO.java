package edu.upes.lostfound.dao;

import edu.upes.lostfound.model.ClaimRequest;
import edu.upes.lostfound.model.ClaimStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ClaimRequestDAO extends BaseDAO {
    public ClaimRequest create(ClaimRequest claim) throws SQLException {
        String sql = """
                INSERT INTO claim_requests(item_id, claimant_id, message, status)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, claim.getItemId());
            stmt.setInt(2, claim.getClaimantId());
            stmt.setString(3, claim.getMessage());
            stmt.setString(4, ClaimStatus.PENDING.name());
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    claim.setId(keys.getInt(1));
                }
            }
        }
        return claim;
    }

    public List<ClaimRequest> findAll() throws SQLException {
        String sql = """
                SELECT c.*, u.full_name AS claimant_name, i.item_name
                FROM claim_requests c
                JOIN users u ON u.id = c.claimant_id
                JOIN item_reports i ON i.id = c.item_id
                ORDER BY c.created_at DESC
                """;
        return queryClaims(sql, null);
    }

    public List<ClaimRequest> findByUser(int userId) throws SQLException {
        String sql = """
                SELECT c.*, u.full_name AS claimant_name, i.item_name
                FROM claim_requests c
                JOIN users u ON u.id = c.claimant_id
                JOIN item_reports i ON i.id = c.item_id
                WHERE c.claimant_id = ?
                ORDER BY c.created_at DESC
                """;
        return queryClaims(sql, userId);
    }

    public void updateStatus(int claimId, ClaimStatus status, String adminNote) throws SQLException {
        String sql = """
                UPDATE claim_requests
                SET status = ?, admin_note = ?, updated_at = CURRENT_TIMESTAMP
                WHERE id = ?
                """;
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setString(2, adminNote);
            stmt.setInt(3, claimId);
            stmt.executeUpdate();
        }
    }

    public int findItemIdForClaim(int claimId) throws SQLException {
        String sql = "SELECT item_id FROM claim_requests WHERE id = ?";
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, claimId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("item_id");
                }
            }
        }
        throw new SQLException("Claim request not found: " + claimId);
    }

    private List<ClaimRequest> queryClaims(String sql, Integer userId) throws SQLException {
        List<ClaimRequest> claims = new ArrayList<>();
        try (Connection conn = connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (userId != null) {
                stmt.setInt(1, userId);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    claims.add(mapClaim(rs));
                }
            }
        }
        return claims;
    }

    private ClaimRequest mapClaim(ResultSet rs) throws SQLException {
        ClaimRequest claim = new ClaimRequest();
        claim.setId(rs.getInt("id"));
        claim.setItemId(rs.getInt("item_id"));
        claim.setClaimantId(rs.getInt("claimant_id"));
        claim.setClaimantName(rs.getString("claimant_name"));
        claim.setItemName(rs.getString("item_name"));
        claim.setMessage(rs.getString("message"));
        claim.setStatus(ClaimStatus.valueOf(rs.getString("status")));
        claim.setAdminNote(rs.getString("admin_note"));
        Timestamp createdAt = rs.getTimestamp("created_at");
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (createdAt != null) {
            claim.setCreatedAt(createdAt.toLocalDateTime());
        }
        if (updatedAt != null) {
            claim.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        return claim;
    }
}
