package edu.upes.lostfound.view.panels;

import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.model.ItemReport;
import edu.upes.lostfound.model.MatchResult;
import edu.upes.lostfound.model.ReportType;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.util.Constants;
import edu.upes.lostfound.util.ValidationUtil;
import edu.upes.lostfound.view.components.ImagePreviewPanel;
import edu.upes.lostfound.view.components.ModernButton;
import edu.upes.lostfound.view.components.PlaceholderTextField;
import edu.upes.lostfound.view.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class ReportItemPanel extends JPanel {
    private final AppController controller;
    private final JComboBox<ReportType> typeCombo = new JComboBox<>(ReportType.values());
    private final PlaceholderTextField itemNameField = new PlaceholderTextField("Example: Black Dell laptop");
    private final JComboBox<String> categoryCombo = new JComboBox<>(Constants.CATEGORIES);
    private final JComboBox<String> locationCombo = new JComboBox<>(Constants.LOCATIONS);
    private final PlaceholderTextField dateField = new PlaceholderTextField("yyyy-mm-dd");
    private final JTextArea descriptionArea = new JTextArea();
    private final PlaceholderTextField contactNameField = new PlaceholderTextField("Contact person");
    private final PlaceholderTextField contactPhoneField = new PlaceholderTextField("10 digit phone");
    private final PlaceholderTextField storageField = new PlaceholderTextField("Example: Security desk, Admin Block");
    private final PlaceholderTextField imagePathField = new PlaceholderTextField("Image path");
    private final ImagePreviewPanel previewPanel = new ImagePreviewPanel();

    public ReportItemPanel(AppController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        add(buildFormCard(), BorderLayout.CENTER);
        typeCombo.addActionListener(e -> updateStorageState());
        updateStorageState();
    }

    private RoundedPanel buildFormCard() {
        RoundedPanel card = new RoundedPanel(26, UITheme.SURFACE);
        card.setLayout(new BorderLayout(20, 0));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 14, 16);

        addRow(form, gbc, "Report type", typeCombo);
        addRow(form, gbc, "Item name", itemNameField);
        addRow(form, gbc, "Category", categoryCombo);
        addRow(form, gbc, "Location", locationCombo);
        addRow(form, gbc, "Date lost/found", dateField);

        UITheme.styleTextArea(descriptionArea);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(480, 115));
        addRow(form, gbc, "Description", descScroll);

        addRow(form, gbc, "Contact name", contactNameField);
        addRow(form, gbc, "Contact phone", contactPhoneField);
        addRow(form, gbc, "Storage location", storageField);

        JPanel imageRow = new JPanel(new BorderLayout(10, 0));
        imageRow.setOpaque(false);
        imageRow.add(imagePathField, BorderLayout.CENTER);
        ModernButton choose = new ModernButton("Browse", ModernButton.Kind.GHOST);
        choose.addActionListener(e -> chooseImage());
        imageRow.add(choose, BorderLayout.EAST);
        addRow(form, gbc, "Image", imageRow);

        JPanel actions = new JPanel(new BorderLayout(12, 0));
        actions.setOpaque(false);
        ModernButton submit = new ModernButton("Submit Report", ModernButton.Kind.PRIMARY);
        submit.addActionListener(e -> submitReport());
        ModernButton clear = new ModernButton("Clear", ModernButton.Kind.GHOST);
        clear.addActionListener(e -> resetForm());
        actions.add(submit, BorderLayout.CENTER);
        actions.add(clear, BorderLayout.EAST);
        gbc.gridx = 1;
        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 0, 16);
        form.add(actions, gbc);

        RoundedPanel side = new RoundedPanel(24, new java.awt.Color(248, 250, 252));
        side.setLayout(new BorderLayout());
        side.setPreferredSize(new Dimension(260, 1));
        side.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        side.add(UITheme.sectionTitle("Photo preview"), BorderLayout.NORTH);
        side.add(previewPanel, BorderLayout.CENTER);
        JLabel hint = UITheme.muted("<html>Store the image path. In a real deployment, copy uploads to a server folder.</html>");
        side.add(hint, BorderLayout.SOUTH);

        card.add(form, BorderLayout.CENTER);
        card.add(side, BorderLayout.EAST);
        return card;
    }

    private void addRow(JPanel form, GridBagConstraints gbc, String label, java.awt.Component input) {
        gbc.gridx = 0;
        gbc.weightx = 0;
        gbc.insets = new Insets(0, 0, 14, 16);
        JLabel fieldLabel = new JLabel(label);
        fieldLabel.setFont(UITheme.H3);
        fieldLabel.setForeground(UITheme.TEXT);
        form.add(fieldLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        form.add(input, gbc);
        gbc.gridy++;
    }

    private void updateStorageState() {
        boolean found = typeCombo.getSelectedItem() == ReportType.FOUND;
        storageField.setEnabled(found);
        if (!found) {
            storageField.setText("");
        }
    }

    private void chooseImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            imagePathField.setText(file.getAbsolutePath());
            previewPanel.showImage(file.getAbsolutePath());
        }
    }

    private void submitReport() {
        try {
            ItemReport report = new ItemReport();
            report.setReportType((ReportType) typeCombo.getSelectedItem());
            report.setItemName(itemNameField.getText().trim());
            report.setCategory(String.valueOf(categoryCombo.getSelectedItem()));
            report.setLocation(String.valueOf(locationCombo.getSelectedItem()));
            report.setItemDate(ValidationUtil.parseDate(dateField.getText(), "Date"));
            report.setDescription(descriptionArea.getText().trim());
            report.setContactName(contactNameField.getText().trim());
            report.setContactPhone(contactPhoneField.getText().trim());
            report.setStorageLocation(storageField.getText().trim());
            report.setImagePath(imagePathField.getText().trim());

            ItemReport saved = controller.createReport(report);
            List<MatchResult> matches = controller.findMatches(saved);
            showSubmittedMessage(saved, matches);
            resetForm();
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Report error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSubmittedMessage(ItemReport report, List<MatchResult> matches) {
        StringBuilder message = new StringBuilder("Report #" + report.getId() + " submitted for admin approval.");
        if (!matches.isEmpty()) {
            message.append("\n\nPossible matches:\n");
            for (MatchResult match : matches) {
                message.append("- #").append(match.getItem().getId())
                        .append(" ").append(match.getItem().getItemName())
                        .append(" (").append(match.getScoreLabel()).append(")\n");
            }
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Report submitted", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetForm() {
        itemNameField.setText("");
        dateField.setText("");
        descriptionArea.setText("");
        contactNameField.setText(controller.getCurrentUser().getFullName());
        contactPhoneField.setText(controller.getCurrentUser().getPhone());
        storageField.setText("");
        imagePathField.setText("");
        previewPanel.showImage("");
    }
}
