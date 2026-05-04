package edu.upes.lostfound.service;

import edu.upes.lostfound.dao.ItemReportDAO;
import edu.upes.lostfound.dao.NotificationDAO;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.MatchResult;
import edu.upes.lostfound.model.ReportStatus;
import edu.upes.lostfound.model.ReportType;
import edu.upes.lostfound.util.ValidationUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class ItemService {
    private final ItemReportDAO itemReportDAO;
    private final NotificationDAO notificationDAO;
    private final MatchService matchService;

    public ItemService(ItemReportDAO itemReportDAO, NotificationDAO notificationDAO, MatchService matchService) {
        this.itemReportDAO = itemReportDAO;
        this.notificationDAO = notificationDAO;
        this.matchService = matchService;
    }

    public ItemReport createReport(ItemReport report) throws SQLException {
        validateReport(report);
        report.setStatus(ReportStatus.PENDING);
        ItemReport saved = itemReportDAO.create(report);
        createMatchNotification(saved);
        return saved;
    }

    public List<ItemReport> search(ReportType type, String keyword, String category, String location,
                                   LocalDate fromDate, LocalDate toDate, Integer userId) throws SQLException {
        return itemReportDAO.search(type, keyword, category, location, fromDate, toDate, userId);
    }

    public List<ItemReport> recentReports(int limit, Integer userId) throws SQLException {
        return itemReportDAO.findRecent(limit, userId);
    }

    public Optional<ItemReport> findById(int id) throws SQLException {
        return itemReportDAO.findById(id);
    }

    public List<MatchResult> findMatches(ItemReport report) throws SQLException {
        return matchService.findMatchesFor(report);
    }

    public void updateStatus(int reportId, ReportStatus status) throws SQLException {
        itemReportDAO.updateStatus(reportId, status);
        if (status == ReportStatus.APPROVED) {
            itemReportDAO.findById(reportId).ifPresent(report -> {
                try {
                    List<MatchResult> matches = matchService.findMatchesFor(report);
                    if (!matches.isEmpty()) {
                        itemReportDAO.markMatched(reportId);
                        notificationDAO.create(report.getUserId(), "Possible match found",
                                "Your " + report.getReportType().name().toLowerCase() +
                                        " report for " + report.getItemName() + " has " + matches.size() +
                                        " possible match(es).");
                    }
                } catch (SQLException ex) {
                    throw new IllegalStateException(ex);
                }
            });
        }
    }

    private void validateReport(ItemReport report) {
        ValidationUtil.requireText(report.getItemName(), "Item name");
        ValidationUtil.requireText(report.getCategory(), "Category");
        ValidationUtil.requireText(report.getDescription(), "Description");
        ValidationUtil.requireText(report.getLocation(), "Location");
        ValidationUtil.requireText(report.getContactName(), "Contact name");
        ValidationUtil.validatePhone(report.getContactPhone());
        if (report.getItemDate() == null) {
            throw new IllegalArgumentException("Item date is required.");
        }
        if (report.getReportType() == ReportType.FOUND) {
            ValidationUtil.requireText(report.getStorageLocation(), "Storage location");
        }
    }

    private void createMatchNotification(ItemReport report) throws SQLException {
        List<MatchResult> matches = matchService.findMatchesFor(report);
        if (!matches.isEmpty()) {
            notificationDAO.create(report.getUserId(), "Possible match found",
                    "We found " + matches.size() + " possible match(es) for " + report.getItemName() + ".");
        }
    }
}
