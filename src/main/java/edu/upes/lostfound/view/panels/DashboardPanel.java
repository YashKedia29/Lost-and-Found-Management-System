package edu.upes.lostfound.view.panels;

import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.model.DashboardStats;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.Refreshable;
import edu.upes.lostfound.view.components.RoundedPanel;
import edu.upes.lostfound.view.components.StatusBadgeRenderer;
import edu.upes.lostfound.view.components.TableFactory;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel implements Refreshable {
    private final AppController controller;
    private final JLabel lostValue = statValue();
    private final JLabel foundValue = statValue();
    private final JLabel claimValue = statValue();
    private final JLabel matchValue = statValue();
    private final JLabel resolvedValue = statValue();
    private final DefaultTableModel recentModel = TableFactory.model("ID", "Type", "Item", "Location", "Status", "Created");
    private final JTable recentTable = TableFactory.table(recentModel);
    private final JPanel notificationList = new JPanel();

    public DashboardPanel(AppController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout(0, 18));
        setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        add(buildStats(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        try {
            DashboardStats stats = controller.dashboardStats();
            lostValue.setText(String.valueOf(stats.getTotalLost()));
            foundValue.setText(String.valueOf(stats.getTotalFound()));
            claimValue.setText(String.valueOf(stats.getPendingClaims()));
            matchValue.setText(String.valueOf(stats.getMatchedItems()));
            resolvedValue.setText(String.valueOf(stats.getClaimedItems()));
            loadRecent();
            loadNotifications();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private JPanel buildStats() {
        JPanel stats = new JPanel(new GridLayout(1, 5, 16, 0));
        stats.setOpaque(false);
        stats.add(statCard("Lost Reports", lostValue, UITheme.PRIMARY));
        stats.add(statCard("Found Reports", foundValue, UITheme.ACCENT));
        stats.add(statCard("Pending Claims", claimValue, UITheme.WARNING));
        stats.add(statCard("Matched Items", matchValue, new Color(124, 58, 237)));
        stats.add(statCard("Claimed", resolvedValue, UITheme.SUCCESS));
        return stats;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(18, 0));
        body.setOpaque(false);

        RoundedPanel tableCard = new RoundedPanel(24, UITheme.SURFACE);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        tableCard.add(UITheme.sectionTitle("Recent activity"), BorderLayout.NORTH);
        recentTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        JScrollPane scrollPane = new JScrollPane(recentTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(14, 0, 0, 0));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        RoundedPanel notifications = new RoundedPanel(24, UITheme.SURFACE);
        notifications.setPreferredSize(new Dimension(330, 1));
        notifications.setLayout(new BorderLayout());
        notifications.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        notifications.add(UITheme.sectionTitle("Notifications"), BorderLayout.NORTH);
        notificationList.setOpaque(false);
        notificationList.setLayout(new BoxLayout(notificationList, BoxLayout.Y_AXIS));
        notifications.add(notificationList, BorderLayout.CENTER);

        body.add(tableCard, BorderLayout.CENTER);
        body.add(notifications, BorderLayout.EAST);
        return body;
    }

    private RoundedPanel statCard(String title, JLabel value, Color color) {
        RoundedPanel card = new RoundedPanel(22, UITheme.SURFACE);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        JLabel label = UITheme.muted(title);
        value.setForeground(color);
        card.add(label);
        card.add(Box.createVerticalStrut(8));
        card.add(value);
        return card;
    }

    private JLabel statValue() {
        JLabel label = new JLabel("0");
        label.setFont(UITheme.H1);
        return label;
    }

    private void loadRecent() throws SQLException {
        recentModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm");
        for (ItemReport report : controller.recentReports(12)) {
            recentModel.addRow(new Object[]{
                    report.getId(),
                    report.getReportType(),
                    report.getItemName(),
                    report.getLocation(),
                    report.getStatus(),
                    report.getCreatedAt() == null ? "-" : report.getCreatedAt().format(formatter)
            });
        }
    }

    private void loadNotifications() throws SQLException {
        notificationList.removeAll();
        List<String> notifications = controller.notifications();
        if (notifications.isEmpty()) {
            notificationList.add(UITheme.muted("No alerts yet. Possible matches will appear here."));
        } else {
            for (String notification : notifications) {
                RoundedPanel alert = new RoundedPanel(18, new Color(239, 246, 255));
                alert.setLayout(new BorderLayout());
                alert.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
                JLabel text = new JLabel("<html>" + notification + "</html>");
                text.setFont(UITheme.BODY);
                text.setForeground(UITheme.TEXT);
                alert.add(text, BorderLayout.CENTER);
                notificationList.add(alert);
                notificationList.add(Box.createVerticalStrut(10));
            }
        }
        notificationList.revalidate();
        notificationList.repaint();
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Dashboard error", JOptionPane.ERROR_MESSAGE);
    }
}
