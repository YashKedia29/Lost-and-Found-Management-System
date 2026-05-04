package edu.upes.lostfound.view.panels;

import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.model.ClaimRequest;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.Refreshable;
import edu.upes.lostfound.view.components.RoundedPanel;
import edu.upes.lostfound.view.components.StatusBadgeRenderer;
import edu.upes.lostfound.view.components.TableFactory;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class ClaimsPanel extends JPanel implements Refreshable {
    private final AppController controller;
    private final DefaultTableModel model = TableFactory.model("ID", "Item ID", "Item", "Message", "Status", "Admin Note", "Created");
    private final JTable table = TableFactory.table(model);

    public ClaimsPanel(AppController controller) {
        this.controller = controller;
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(22, 0, 0, 0));
        add(buildCard(), BorderLayout.CENTER);
    }

    @Override
    public void refreshData() {
        try {
            model.setRowCount(0);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
            for (ClaimRequest claim : controller.claimsForCurrentUser()) {
                model.addRow(new Object[]{
                        claim.getId(),
                        claim.getItemId(),
                        claim.getItemName(),
                        claim.getMessage(),
                        claim.getStatus(),
                        claim.getAdminNote() == null ? "-" : claim.getAdminNote(),
                        claim.getCreatedAt() == null ? "-" : claim.getCreatedAt().format(formatter)
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Claims error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildCard() {
        RoundedPanel card = new RoundedPanel(24, UITheme.SURFACE);
        card.setLayout(new BorderLayout(0, 14));
        card.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JLabel title = UITheme.sectionTitle("My claim requests");
        card.add(title, BorderLayout.NORTH);
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer());
        card.add(new JScrollPane(table), BorderLayout.CENTER);
        return card;
    }
}
