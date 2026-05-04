package edu.upes.lostfound.controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import edu.upes.lostfound.dao.ClaimRequestDAO;
import edu.upes.lostfound.dao.DashboardDAO;
import edu.upes.lostfound.dao.ItemReportDAO;
import edu.upes.lostfound.dao.NotificationDAO;
import edu.upes.lostfound.dao.UserDAO;
import edu.upes.lostfound.model.ClaimRequest;
import edu.upes.lostfound.model.ClaimStatus;
import edu.upes.lostfound.model.DashboardStats;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.MatchResult;
import edu.upes.lostfound.model.ReportStatus;
import edu.upes.lostfound.model.ReportType;
import edu.upes.lostfound.model.Role;
import edu.upes.lostfound.model.User;
import edu.upes.lostfound.service.ClaimService;
import edu.upes.lostfound.service.DashboardService;
import edu.upes.lostfound.service.ItemService;
import edu.upes.lostfound.service.MatchService;

public class AppController {
    private final User currentUser;
    private final UserDAO userDAO;
    private final ItemService itemService;
    private final ClaimService claimService;
    private final DashboardService dashboardService;

    public AppController(User currentUser) {
        this.currentUser = currentUser;
        ItemReportDAO itemReportDAO = new ItemReportDAO();
        NotificationDAO notificationDAO = new NotificationDAO();
        this.userDAO = new UserDAO();
        MatchService matchService = new MatchService(itemReportDAO);
        this.itemService = new ItemService(itemReportDAO, notificationDAO, matchService);
        this.claimService = new ClaimService(new ClaimRequestDAO(), itemReportDAO);
        this.dashboardService = new DashboardService(new DashboardDAO(), notificationDAO);
    }

    public User getCurrentUser() 
    {
        return currentUser;
    }

    public boolean isAdmin() 
    {
        return currentUser.getRole() == Role.ADMIN;
    }

    public DashboardStats dashboardStats() throws SQLException 
    {
        return dashboardService.stats(isAdmin() ? null : currentUser.getId());
    }

    public List<String> notifications() throws SQLException 
    {
        return dashboardService.notifications(currentUser.getId());
    }

    public ItemReport createReport(ItemReport report) throws SQLException 
    {
        report.setUserId(currentUser.getId());
        return itemService.createReport(report);
    }

    public List<ItemReport> searchReports(ReportType type, String keyword, String category, String location,
                                          LocalDate fromDate, LocalDate toDate, boolean ownOnly) throws SQLException {
        Integer userId = ownOnly ? currentUser.getId() : null;
        return itemService.search(type, keyword, category, location, fromDate, toDate, userId);
    }

    public List<ItemReport> recentReports(int limit) throws SQLException {
        return itemService.recentReports(limit, isAdmin() ? null : currentUser.getId());
    }

    public Optional<ItemReport> findReport(int id) throws SQLException {
        return itemService.findById(id);
    }

    public List<MatchResult> findMatches(ItemReport report) throws SQLException {
        return itemService.findMatches(report);
    }

    public void updateReportStatus(int reportId, ReportStatus status) throws SQLException {
        itemService.updateStatus(reportId, status);
    }

    public ClaimRequest requestClaim(int itemId, String message) throws SQLException {
        return claimService.requestClaim(itemId, currentUser.getId(), message);
    }

    public List<ClaimRequest> claimsForCurrentUser() throws SQLException {
        return claimService.claimsForUser(currentUser.getId());
    }

    public List<ClaimRequest> allClaims() throws SQLException {
        return claimService.allClaims();
    }

    public void updateClaimStatus(int claimId, ClaimStatus status, String note) throws SQLException {
        claimService.updateClaimStatus(claimId, status, note);
    }

    public List<User> allUsers() throws SQLException {
        return userDAO.findAll();
    }

    public void updateUser(int userId, Role role, boolean active) throws SQLException {
        userDAO.updateRoleAndActive(userId, role, active);
    }
}
