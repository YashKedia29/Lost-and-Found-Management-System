package edu.upes.lostfound.view;

import edu.upes.lostfound.config.AppConfig;
import edu.upes.lostfound.controller.AppController;
import edu.upes.lostfound.controller.AuthController;
import edu.upes.lostfound.model.User;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.components.GradientPanel;
import edu.upes.lostfound.view.components.ModernButton;
import edu.upes.lostfound.view.components.PlaceholderPasswordField;
import edu.upes.lostfound.view.components.PlaceholderTextField;
import edu.upes.lostfound.view.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final AuthController authController;
    private final PlaceholderTextField emailField = new PlaceholderTextField("Email address");
    private final PlaceholderPasswordField passwordField = new PlaceholderPasswordField("Password");

    public LoginFrame(AuthController authController) {
        super(AppConfig.get("app.name", "UPES Lost and Found"));
        this.authController = authController;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1080, 680));
        setLocationRelativeTo(null);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BACKGROUND);
        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildLoginPanel(), BorderLayout.CENTER);
        return root;
    }

    private JPanel buildBrandPanel() {
        GradientPanel brand = new GradientPanel(new Color(15, 23, 42), new Color(30, 64, 175));
        brand.setPreferredSize(new Dimension(450, 680));
        brand.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(40, 44, 40, 44));

        JLabel campus = new JLabel("UNIVERSITY CAMPUS");
        campus.setFont(UITheme.SMALL.deriveFont(13f));
        campus.setForeground(new Color(186, 230, 253));
        JLabel title = new JLabel("<html>Lost and Found<br>Management System</html>");
        title.setFont(UITheme.H1.deriveFont(38f));
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("<html>A practical desktop system for reporting lost items, managing found property, matching reports, and verifying claims.</html>");
        subtitle.setFont(UITheme.BODY.deriveFont(15f));
        subtitle.setForeground(new Color(219, 234, 254));

        RoundedPanel metric = new RoundedPanel(24, new Color(255, 255, 255, 35));
        metric.setOpaque(false);
        metric.setLayout(new BoxLayout(metric, BoxLayout.Y_AXIS));
        metric.setBorder(BorderFactory.createEmptyBorder(22, 24, 22, 24));
        JLabel metricTitle = new JLabel("Smart Matching Engine");
        metricTitle.setFont(UITheme.H2);
        metricTitle.setForeground(Color.WHITE);
        JLabel metricText = new JLabel("<html>Compares category, location proximity, and keyword similarity to suggest possible matches.</html>");
        metricText.setFont(UITheme.BODY);
        metricText.setForeground(new Color(226, 232, 240));
        metric.add(metricTitle);
        metric.add(Box.createVerticalStrut(8));
        metric.add(metricText);

        content.add(campus);
        content.add(Box.createVerticalStrut(14));
        content.add(title);
        content.add(Box.createVerticalStrut(20));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(46));
        content.add(metric);

        brand.add(content);
        return brand;
    }

    private JPanel buildLoginPanel() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(UITheme.BACKGROUND);

        RoundedPanel card = new RoundedPanel(28, UITheme.SURFACE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(34, 38, 34, 38));
        card.setPreferredSize(new Dimension(430, 430));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 14, 0);
        gbc.weightx = 1;

        JLabel welcome = UITheme.sectionTitle("Welcome back");
        JLabel helper = UITheme.muted("Sign in with your campus account to continue.");
        card.add(welcome, gbc);
        gbc.gridy = 1;
        card.add(helper, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(18, 0, 12, 0);
        emailField.setPreferredSize(new Dimension(340, 44));
        card.add(emailField, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 20, 0);
        passwordField.setPreferredSize(new Dimension(340, 44));
        card.add(passwordField, gbc);

        gbc.gridy = 4;
        ModernButton loginButton = new ModernButton("Login", ModernButton.Kind.PRIMARY);
        loginButton.setPreferredSize(new Dimension(340, 46));
        loginButton.addActionListener(e -> login());
        card.add(loginButton, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(16, 0, 0, 0);
        JLabel register = new JLabel("<html><u>Create a new student account</u></html>", JLabel.CENTER);
        register.setFont(UITheme.BODY);
        register.setForeground(UITheme.PRIMARY);
        register.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        register.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterDialog(LoginFrame.this, authController).setVisible(true);
            }
        });
        card.add(register, gbc);

        area.add(card);
        SwingUtilities.invokeLater(emailField::requestFocusInWindow);
        return area;
    }

    private void login() {
        try {
            User user = authController.login(emailField.getText(), new String(passwordField.getPassword()));
            new MainFrame(new AppController(user)).setVisible(true);
            dispose();
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
