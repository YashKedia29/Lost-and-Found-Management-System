package edu.upes.lostfound.service;

import edu.upes.lostfound.dao.DashboardDAO;
import edu.upes.lostfound.dao.NotificationDAO;
import edu.upes.lostfound.model.DashboardStats;

import java.sql.SQLException;
import java.util.List;

public class DashboardService {
    private final DashboardDAO dashboardDAO;
    private final NotificationDAO notificationDAO;

    public DashboardService(DashboardDAO dashboardDAO, NotificationDAO notificationDAO) {
        this.dashboardDAO = dashboardDAO;
        this.notificationDAO = notificationDAO;
    }

    public DashboardStats stats(Integer userId) throws SQLException {
        return dashboardDAO.fetchStats(userId);
    }

    public List<String> notifications(int userId) throws SQLException {
        return notificationDAO.findLatestForUser(userId, 5);
    }
}
