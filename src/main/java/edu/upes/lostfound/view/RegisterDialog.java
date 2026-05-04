package edu.upes.lostfound.view;

import edu.upes.lostfound.controller.AuthController;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.components.ModernButton;
import edu.upes.lostfound.view.components.PlaceholderPasswordField;
import edu.upes.lostfound.view.components.PlaceholderTextField;
import edu.upes.lostfound.view.components.RoundedPanel;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;

public class RegisterDialog extends JDialog {
    private final AuthController authController;
    private final PlaceholderTextField nameField = new PlaceholderTextField("Full name");
    private final PlaceholderTextField universityIdField = new PlaceholderTextField("UPES SAP ID / Roll number");
    private final PlaceholderTextField emailField = new PlaceholderTextField("University email");
    private final PlaceholderTextField phoneField = new PlaceholderTextField("10 digit phone");
    private final PlaceholderPasswordField passwordField = new PlaceholderPasswordField("Password");

    public RegisterDialog(JFrame owner, AuthController authController) {
        super(owner, "Create Student Account", true);
        this.authController = authController;
        setSize(500, 540);
        setLocationRelativeTo(owner);
        setContentPane(buildContent());
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BACKGROUND);
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        RoundedPanel card = new RoundedPanel(24, UITheme.SURFACE);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(26, 28, 26, 28));
        root.add(card, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 12, 0);

        JLabel title = UITheme.sectionTitle("Register");
        card.add(title, gbc);

        JLabel hint = UITheme.muted("Student accounts are created with the STUDENT role.");
        gbc.gridy = 1;
        card.add(hint, gbc);

        PlaceholderTextField[] fields = {nameField, universityIdField, emailField, phoneField};
        for (int i = 0; i < fields.length; i++) {
            gbc.gridy = i + 2;
            gbc.insets = new Insets(i == 0 ? 18 : 0, 0, 12, 0);
            fields[i].setPreferredSize(new Dimension(390, 42));
            card.add(fields[i], gbc);
        }

        gbc.gridy = 6;
        passwordField.setPreferredSize(new Dimension(390, 42));
        card.add(passwordField, gbc);

        gbc.gridy = 7;
        gbc.insets = new Insets(14, 0, 0, 0);
        ModernButton create = new ModernButton("Create Account", ModernButton.Kind.PRIMARY);
        create.setPreferredSize(new Dimension(390, 46));
        create.addActionListener(e -> register());
        card.add(create, gbc);
        return root;
    }

    private void register() {
        try {
            authController.register(
                    nameField.getText(),
                    universityIdField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    new String(passwordField.getPassword())
            );
            JOptionPane.showMessageDialog(this, "Account created. You can now login.", "Registration", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (IllegalArgumentException | SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
