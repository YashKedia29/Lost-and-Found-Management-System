package edu.upes.lostfound.service;

import edu.upes.lostfound.dao.ClaimRequestDAO;
import edu.upes.lostfound.dao.ItemReportDAO;
import edu.upes.lostfound.model.ClaimRequest;
import edu.upes.lostfound.model.ClaimStatus;
import edu.upes.lostfound.model.ReportStatus;
import edu.upes.lostfound.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;

public class ClaimService {
    private final ClaimRequestDAO claimRequestDAO;
    private final ItemReportDAO itemReportDAO;

    public ClaimService(ClaimRequestDAO claimRequestDAO, ItemReportDAO itemReportDAO) {
        this.claimRequestDAO = claimRequestDAO;
        this.itemReportDAO = itemReportDAO;
    }

    public ClaimRequest requestClaim(int itemId, int claimantId, String message) throws SQLException {
        ValidationUtil.requireText(message, "Claim proof message");
        ClaimRequest claim = new ClaimRequest();
        claim.setItemId(itemId);
        claim.setClaimantId(claimantId);
        claim.setMessage(message.trim());
        claim.setStatus(ClaimStatus.PENDING);
        return claimRequestDAO.create(claim);
    }

    public List<ClaimRequest> claimsForUser(int userId) throws SQLException {
        return claimRequestDAO.findByUser(userId);
    }

    public List<ClaimRequest> allClaims() throws SQLException {
        return claimRequestDAO.findAll();
    }

    public void updateClaimStatus(int claimId, ClaimStatus status, String adminNote) throws SQLException {
        claimRequestDAO.updateStatus(claimId, status, adminNote);
        if (status == ClaimStatus.APPROVED) {
            int itemId = claimRequestDAO.findItemIdForClaim(claimId);
            itemReportDAO.updateStatus(itemId, ReportStatus.CLAIMED);
        }
    }
}
