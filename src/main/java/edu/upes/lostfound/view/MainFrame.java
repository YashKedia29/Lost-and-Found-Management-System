package edu.upes.lostfound.view;

import edu.upes.lostfound.controller.AuthController;
import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.components.GradientPanel;
import edu.upes.lostfound.view.components.SidebarButton;
import edu.upes.lostfound.view.panels.AdminPanel;
import edu.upes.lostfound.view.panels.BrowsePanel;
import edu.upes.lostfound.view.panels.ClaimsPanel;
import edu.upes.lostfound.view.panels.DashboardPanel;
import edu.upes.lostfound.view.panels.ReportItemPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private final AppController controller;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel content = new JPanel(cardLayout);
    private final JLabel pageTitle = UITheme.sectionTitle("Dashboard");
    private final Map<String, SidebarButton> navButtons = new HashMap<>();
    private String activePage = "Dashboard";

    public MainFrame(AppController controller) {
        super("UPES Lost and Found");
        this.controller = controller;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1240, 760));
        setLocationRelativeTo(null);
        setContentPane(buildContent());
        showPage("Dashboard");
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BACKGROUND);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainArea(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildSidebar() {
        GradientPanel sidebar = new GradientPanel(new Color(15, 23, 42), new Color(30, 64, 175));
        sidebar.setPreferredSize(new Dimension(260, 760));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(28, 20, 24, 20));

        JLabel brand = new JLabel("<html><b>UPES</b><br>Lost and Found</html>");
        brand.setFont(UITheme.H2);
        brand.setForeground(Color.WHITE);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(8));
        JLabel user = new JLabel("<html>" + controller.getCurrentUser().getFullName() + "<br><span style='font-size:10px'>" +
                controller.getCurrentUser().getRole() + "</span></html>");
        user.setFont(UITheme.SMALL);
        user.setForeground(new Color(219, 234, 254));
        sidebar.add(user);
        sidebar.add(Box.createVerticalStrut(30));

        addNav(sidebar, "Dashboard", e -> showPage("Dashboard"));
        addNav(sidebar, "Report Item", e -> showPage("Report Item"));
        addNav(sidebar, "Search & Matches", e -> showPage("Search & Matches"));
        addNav(sidebar, "My Claims", e -> showPage("My Claims"));
        if (controller.isAdmin()) {
            addNav(sidebar, "Admin", e -> showPage("Admin"));
        }
        sidebar.add(Box.createVerticalGlue());
        addNav(sidebar, "Logout", e -> logout());
        return sidebar;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        pageTitle.setFont(UITheme.H2.deriveFont(24f));
        topBar.add(pageTitle, BorderLayout.WEST);
        JLabel right = UITheme.muted("Campus property desk workflow");
        topBar.add(right, BorderLayout.EAST);
        main.add(topBar, BorderLayout.NORTH);

        content.setOpaque(false);
        content.add(new DashboardPanel(controller), "Dashboard");
        content.add(new ReportItemPanel(controller), "Report Item");
        content.add(new BrowsePanel(controller), "Search & Matches");
        content.add(new ClaimsPanel(controller), "My Claims");
        if (controller.isAdmin()) {
            content.add(new AdminPanel(controller), "Admin");
        }
        main.add(content, BorderLayout.CENTER);
        return main;
    }

    private void addNav(JPanel sidebar, String text, ActionListener action) {
        SidebarButton button = new SidebarButton(text);
        button.addActionListener(action);
        navButtons.put(text, button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(8));
    }

    private void showPage(String name) {
        activePage = name;
        pageTitle.setText(name);
        for (Map.Entry<String, SidebarButton> entry : navButtons.entrySet()) {
            entry.getValue().setSelectedState(entry.getKey().equals(activePage));
        }
        cardLayout.show(content, name);
        for (java.awt.Component component : content.getComponents()) {
            if (component.isVisible() && component instanceof Refreshable refreshable) {
                refreshable.refreshData();
            }
        }
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Logout from this session?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            new LoginFrame(new AuthController()).setVisible(true);
            dispose();
        }
    }
}
