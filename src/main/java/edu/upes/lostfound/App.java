package edu.upes.lostfound;

import edu.upes.lostfound.config.Database;
import edu.upes.lostfound.controller.AuthController;
import edu.upes.lostfound.ui.UITheme;
import edu.upes.lostfound.view.LoginFrame;

import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        UITheme.install();
        SwingUtilities.invokeLater(() -> {
            try (Connection ignored = Database.getConnection()) {
                new LoginFrame(new AuthController()).setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "Database connection failed.\n\n" +
                                "1. Start MySQL.\n" +
                                "2. Run database/schema.sql and database/sample_data.sql.\n" +
                                "3. Check src/main/resources/application.properties.\n\n" +
                                "Details: " + ex.getMessage(),
                        "UPES Lost and Found",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
