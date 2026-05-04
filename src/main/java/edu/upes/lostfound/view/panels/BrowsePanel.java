package edu.upes.lostfound.view.panels;

import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.MatchResult;
import edu.upes.lostfound.model.ReportStatus;
import edu.upes.lostfound.model.ReportType;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.util.Constants;
import edu.upes.lostfound.util.ValidationUtil;
import edu.upes.lostfound.view.Refreshable;
import edu.upes.lostfound.view.components.ModernButton;
import edu.upes.lostfound.view.components.PlaceholderTextField;
import edu.upes.lostfound.view.components.RoundedPanel;
import edu.upes.lostfound.view.components.StatusBadgeRenderer;
import edu.upes.lostfound.view.components.TableFactory;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BrowsePanel extends JPanel implements Refreshable {
    private final AppController controller;
    private final JComboBox<String> typeCombo = new JComboBox<>(new String[]{"All", "LOST", "FOUND"});
    private final JComboBox<String> categoryCombo = new JComboBox<>(Constants.FILTER_CATEGORIES);
    private final JComboBox<String> locationCombo = new JComboBox<>(Constants.FILTER_LOCATIONS);
    private final PlaceholderTextField keywordField = new PlaceholderTextField("Search keyword");
    private final PlaceholderTextField fromDateField = new PlaceholderTextField("From yyyy-mm-dd");
    private final PlaceholderTextField toDateField = new PlaceholderTextField("To yyyy-mm-dd");
    private final JCheckBox ownOnly = new JCheckBox("My reports only");
    private final DefaultTableModel model = TableFactory.model("ID", "Type", "Item", "Category", "Location", "Date", "Status");
    private final JTable table = TableFactory.table(model);
    private List<ItemReport> reports = new ArrayList<>();

    public BrowsePanel(AppController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout(0, 18));
        setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        add(buildFilters(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        loadReports();
    }

    private JPanel buildFilters() {
        RoundedPanel filters = new RoundedPanel(24, UITheme.SURFACE);
        filters.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        filters.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        addFilter(filters, gbc, 0, 0, keywordField);
        addFilter(filters, gbc, 1, 0, typeCombo);
        addFilter(filters, gbc, 2, 0, categoryCombo);
        addFilter(filters, gbc, 3, 0, locationCombo);
        addFilter(filters, gbc, 0, 1, fromDateField);
        addFilter(filters, gbc, 1, 1, toDateField);
        ownOnly.setOpaque(false);
        ownOnly.setForeground(UITheme.TEXT);
        addFilter(filters, gbc, 2, 1, ownOnly);

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        ModernButton search = new ModernButton("Search", ModernButton.Kind.PRIMARY);
        search.addActionListener(e -> loadReports());
        ModernButton reset = new ModernButton("Reset", ModernButton.Kind.GHOST);
        reset.addActionListener(e -> resetFilters());
        actions.add(search);
        actions.add(reset);
        addFilter(filters, gbc, 3, 1, actions);
        return filters;
    }

    private void addFilter(JPanel parent, GridBagConstraints gbc, int x, int y, java.awt.Component component) {
        gbc.gridx = x;
        gbc.gridy = y;
        parent.add(component, gbc);
    }

    private JPanel buildTableCard() {
        RoundedPanel card = new RoundedPanel(24, UITheme.SURFACE);
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        card.setLayout(new BorderLayout(0, 14));
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        card.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(1, 3, 12, 0));
        actions.setOpaque(false);
        ModernButton details = new ModernButton("View Details", ModernButton.Kind.GHOST);
        details.addActionListener(e -> showDetails());
        ModernButton matches = new ModernButton("Possible Matches", ModernButton.Kind.SECONDARY);
        matches.addActionListener(e -> showMatches());
        ModernButton claim = new ModernButton("Request Claim", ModernButton.Kind.PRIMARY);
        claim.addActionListener(e -> requestClaim());
        actions.add(details);
        actions.add(matches);
        actions.add(claim);
        card.add(actions, BorderLayout.SOUTH);
        return card;
    }

    private void loadReports() {
        try {
            ReportType type = selectedType();
            LocalDate from = fromDateField.getText().isBlank() ? null : ValidationUtil.parseDate(fromDateField.getText(), "From date");
            LocalDate to = toDateField.getText().isBlank() ? null : ValidationUtil.parseDate(toDateField.getText(), "To date");
            reports = controller.searchReports(
                    type,
                    keywordField.getText(),
                    String.valueOf(categoryCombo.getSelectedItem()),
                    String.valueOf(locationCombo.getSelectedItem()),
                    from,
                    to,
                    ownOnly.isSelected()
            );
            fillTable();
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Search error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillTable() {
        model.setRowCount(0);
        for (ItemReport report : reports) {
            model.addRow(new Object[]{
                    report.getId(),
                    report.getReportType(),
                    report.getItemName(),
                    report.getCategory(),
                    report.getLocation(),
                    report.getItemDate(),
                    report.getStatus()
            });
        }
    }

    private ReportType selectedType() {
        String selected = String.valueOf(typeCombo.getSelectedItem());
        if ("LOST".equals(selected)) {
            return ReportType.LOST;
        }
        if ("FOUND".equals(selected)) {
            return ReportType.FOUND;
        }
        return null;
    }

    private ItemReport selectedReport() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a report first.", "Selection required", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        int id = (int) model.getValueAt(row, 0);
        return reports.stream().filter(report -> report.getId() == id).findFirst().orElse(null);
    }

    private void showDetails() {
        ItemReport report = selectedReport();
        if (report == null) {
            return;
        }
        String details = """
                Report ID: #%d
                Type: %s
                Item: %s
                Category: %s
                Location: %s
                Date: %s
                Status: %s
                Contact: %s (%s)
                Storage: %s
                Image: %s

                Description:
                %s
                """.formatted(
                report.getId(), report.getReportType(), report.getItemName(), report.getCategory(),
                report.getLocation(), report.getItemDate(), report.getStatus(), report.getContactName(),
                report.getContactPhone(), blank(report.getStorageLocation()), blank(report.getImagePath()),
                report.getDescription()
        );
        JTextArea area = new JTextArea(details);
        area.setEditable(false);
        area.setFont(UITheme.BODY);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Report details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showMatches() {
        ItemReport report = selectedReport();
        if (report == null) {
            return;
        }
        try {
            List<MatchResult> matches = controller.findMatches(report);
            if (matches.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No strong matches found yet.", "Possible matches", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            StringBuilder message = new StringBuilder();
            for (MatchResult match : matches) {
                message.append("#").append(match.getItem().getId())
                        .append(" | ").append(match.getItem().getItemName())
                        .append(" | ").append(match.getItem().getReportType())
                        .append(" | ").append(match.getScoreLabel())
                        .append("\n").append(match.getReason()).append("\n\n");
            }
            JTextArea area = new JTextArea(message.toString(), 12, 48);
            area.setEditable(false);
            area.setFont(UITheme.BODY);
            JOptionPane.showMessageDialog(this, new JScrollPane(area), "Possible matches", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Match error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void requestClaim() {
        ItemReport report = selectedReport();
        if (report == null) {
            return;
        }
        if (report.getReportType() != ReportType.FOUND) {
            JOptionPane.showMessageDialog(this, "Claims are requested against found item reports.", "Claim request", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (report.getStatus() == ReportStatus.CLAIMED || report.getStatus() == ReportStatus.REJECTED) {
            JOptionPane.showMessageDialog(this, "This item cannot receive new claims.", "Claim request", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String message = JOptionPane.showInputDialog(this,
                "Describe proof of ownership. Example: brand marks, color, last place used, unique details.",
                "Claim proof", JOptionPane.PLAIN_MESSAGE);
        if (message == null) {
            return;
        }
        try {
            controller.requestClaim(report.getId(), message);
            JOptionPane.showMessageDialog(this, "Claim request sent to admin.", "Claim request", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Claim error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        keywordField.setText("");
        typeCombo.setSelectedIndex(0);
        categoryCombo.setSelectedIndex(0);
        locationCombo.setSelectedIndex(0);
        fromDateField.setText("");
        toDateField.setText("");
        ownOnly.setSelected(false);
        loadReports();
    }

    private String blank(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }
}
