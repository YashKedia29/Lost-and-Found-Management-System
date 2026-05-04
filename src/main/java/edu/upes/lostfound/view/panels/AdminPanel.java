package edu.upes.lostfound.view.panels;

import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.model.ClaimRequest;
import edu.upes.lostfound.model.ClaimStatus;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.ReportStatus;
import edu.upes.lostfound.model.Role;
import edu.upes.lostfound.model.User;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.Refreshable;
import edu.upes.lostfound.view.components.ModernButton;
import edu.upes.lostfound.view.components.RoundedPanel;
import edu.upes.lostfound.view.components.StatusBadgeRenderer;
import edu.upes.lostfound.view.components.TableFactory;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class AdminPanel extends JPanel implements Refreshable {
    private final AppController controller;
    private final DefaultTableModel reportsModel = TableFactory.model("ID", "User", "Type", "Item", "Category", "Location", "Status");
    private final DefaultTableModel claimsModel = TableFactory.model("ID", "Item ID", "Item", "Claimant", "Message", "Status", "Admin Note");
    private final DefaultTableModel usersModel = TableFactory.model("ID", "Name", "University ID", "Email", "Role", "Active", "Created");
    private final JTable reportsTable = TableFactory.table(reportsModel);
    private final JTable claimsTable = TableFactory.table(claimsModel);
    private final JTable usersTable = TableFactory.table(usersModel);

    public AdminPanel(AppController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        add(buildTabs(), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        loadReports();
        loadClaims();
        loadUsers();
    }

    private JTabbedPane buildTabs() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.H3);
        tabs.add("Reports", buildReportsTab());
        tabs.add("Claims", buildClaimsTab());
        tabs.add("Users", buildUsersTab());
        return tabs;
    }

    private JPanel buildReportsTab() {
        RoundedPanel panel = adminCard();
        reportsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        panel.add(new JScrollPane(reportsTable), BorderLayout.CENTER);
        JPanel actions = actionGrid(4);
        ModernButton approve = new ModernButton("Approve", ModernButton.Kind.SECONDARY);
        approve.addActionListener(e -> updateSelectedReport(ReportStatus.APPROVED));
        ModernButton reject = new ModernButton("Reject", ModernButton.Kind.DANGER);
        reject.addActionListener(e -> updateSelectedReport(ReportStatus.REJECTED));
        ModernButton claimed = new ModernButton("Mark Claimed", ModernButton.Kind.PRIMARY);
        claimed.addActionListener(e -> updateSelectedReport(ReportStatus.CLAIMED));
        ModernButton refresh = new ModernButton("Refresh", ModernButton.Kind.GHOST);
        refresh.addActionListener(e -> refreshData());
        actions.add(approve);
        actions.add(reject);
        actions.add(claimed);
        actions.add(refresh);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildClaimsTab() {
        RoundedPanel panel = adminCard();
        claimsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusBadgeRenderer());
        panel.add(new JScrollPane(claimsTable), BorderLayout.CENTER);
        JPanel actions = actionGrid(3);
        ModernButton approve = new ModernButton("Approve Claim", ModernButton.Kind.SECONDARY);
        approve.addActionListener(e -> updateSelectedClaim(ClaimStatus.APPROVED));
        ModernButton reject = new ModernButton("Reject Claim", ModernButton.Kind.DANGER);
        reject.addActionListener(e -> updateSelectedClaim(ClaimStatus.REJECTED));
        ModernButton refresh = new ModernButton("Refresh", ModernButton.Kind.GHOST);
        refresh.addActionListener(e -> refreshData());
        actions.add(approve);
        actions.add(reject);
        actions.add(refresh);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildUsersTab() {
        RoundedPanel panel = adminCard();
        panel.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        JPanel actions = actionGrid(4);
        ModernButton makeAdmin = new ModernButton("Make Admin", ModernButton.Kind.PRIMARY);
        makeAdmin.addActionListener(e -> updateSelectedUser(Role.ADMIN, null));
        ModernButton makeStudent = new ModernButton("Make Student", ModernButton.Kind.SECONDARY);
        makeStudent.addActionListener(e -> updateSelectedUser(Role.STUDENT, null));
        ModernButton toggleActive = new ModernButton("Toggle Active", ModernButton.Kind.GHOST);
        toggleActive.addActionListener(e -> updateSelectedUser(null, true));
        ModernButton refresh = new ModernButton("Refresh", ModernButton.Kind.GHOST);
        refresh.addActionListener(e -> refreshData());
        actions.add(makeAdmin);
        actions.add(makeStudent);
        actions.add(toggleActive);
        actions.add(refresh);
        panel.add(actions, BorderLayout.SOUTH);
        return panel;
    }

    private RoundedPanel adminCard() {
        RoundedPanel panel = new RoundedPanel(24, UITheme.SURFACE);
        panel.setLayout(new BorderLayout(0, 14));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        return panel;
    }

    private JPanel actionGrid(int columns) {
        JPanel actions = new JPanel(new GridLayout(1, columns, 12, 0));
        actions.setOpaque(false);
        return actions;
    }

    private void loadReports() {
        try {
            reportsModel.setRowCount(0);
            for (ItemReport report : controller.searchReports(null, "", "", "", null, null, false)) {
                reportsModel.addRow(new Object[]{
                        report.getId(),
                        report.getUserId(),
                        report.getReportType(),
                        report.getItemName(),
                        report.getCategory(),
                        report.getLocation(),
                        report.getStatus()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadClaims() {
        try {
            claimsModel.setRowCount(0);
            for (ClaimRequest claim : controller.allClaims()) {
                claimsModel.addRow(new Object[]{
                        claim.getId(),
                        claim.getItemId(),
                        claim.getItemName(),
                        claim.getClaimantName(),
                        claim.getMessage(),
                        claim.getStatus(),
                        claim.getAdminNote() == null ? "-" : claim.getAdminNote()
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void loadUsers() {
        try {
            usersModel.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            for (User user : controller.allUsers()) {
                usersModel.addRow(new Object[]{
                        user.getId(),
                        user.getFullName(),
                        user.getUniversityId(),
                        user.getEmail(),
                        user.getRole(),
                        user.isActive(),
                        user.getCreatedAt() == null ? "-" : user.getCreatedAt().format(formatter)
                });
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateSelectedReport(ReportStatus status) {
        int id = selectedId(reportsTable, reportsModel);
        if (id < 0) {
            return;
        }
        try {
            controller.updateReportStatus(id, status);
            loadReports();
            JOptionPane.showMessageDialog(this, "Report updated to " + status + ".", "Admin", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException | IllegalStateException ex) {
            showError(ex);
        }
    }

    private void updateSelectedClaim(ClaimStatus status) {
        int id = selectedId(claimsTable, claimsModel);
        if (id < 0) {
            return;
        }
        String note = JOptionPane.showInputDialog(this, "Admin note:", status == ClaimStatus.APPROVED ? "Verified by admin" : "Insufficient proof");
        if (note == null) {
            return;
        }
        try {
            controller.updateClaimStatus(id, status, note);
            loadClaims();
            loadReports();
            JOptionPane.showMessageDialog(this, "Claim updated to " + status + ".", "Admin", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void updateSelectedUser(Role newRole, Boolean toggleActive) {
        int row = usersTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user first.", "Admin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) usersModel.getValueAt(row, 0);
        Role role = newRole == null ? (Role) usersModel.getValueAt(row, 4) : newRole;
        boolean active = (boolean) usersModel.getValueAt(row, 5);
        if (toggleActive != null) {
            active = !active;
        }
        try {
            controller.updateUser(id, role, active);
            loadUsers();
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private int selectedId(JTable table, DefaultTableModel model) {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row first.", "Admin", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) model.getValueAt(row, 0);
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Admin error", JOptionPane.ERROR_MESSAGE);
    }
}
