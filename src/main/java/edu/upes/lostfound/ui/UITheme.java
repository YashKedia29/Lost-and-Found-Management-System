package edu.upes.lostfound.ui;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Font;

public final class UITheme {
    public static final Color BACKGROUND = new Color(244, 247, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color PRIMARY_DARK = new Color(30, 64, 175);
    public static final Color ACCENT = new Color(20, 184, 166);
    public static final Color WARNING = new Color(245, 158, 11);
    public static final Color DANGER = new Color(220, 38, 38);
    public static final Color SUCCESS = new Color(22, 163, 74);
    public static final Color BORDER = new Color(226, 232, 240);
    public static final Font H1 = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font H2 = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font H3 = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font BODY = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private UITheme() {
    }

    public static void install() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignoredAgain) {
                // Swing will fall back to its default look and feel.
            }
        }
        UIManager.put("control", BACKGROUND);
        UIManager.put("info", SURFACE);
        UIManager.put("nimbusBase", PRIMARY);
        UIManager.put("nimbusBlueGrey", BORDER);
        UIManager.put("nimbusFocus", ACCENT);
        UIManager.put("text", TEXT);
        UIManager.put("Table.font", BODY);
        UIManager.put("TableHeader.font", H3);
        UIManager.put("Label.font", BODY);
        UIManager.put("TextField.font", BODY);
        UIManager.put("PasswordField.font", BODY);
        UIManager.put("ComboBox.font", BODY);
        UIManager.put("TextArea.font", BODY);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(H1);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel sectionTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(H2);
        label.setForeground(TEXT);
        return label;
    }

    public static JLabel muted(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SMALL);
        label.setForeground(MUTED);
        return label;
    }

    public static void padded(JComponent component, int top, int left, int bottom, int right) {
        component.setBorder(new EmptyBorder(top, left, bottom, right));
    }

    public static void styleField(JComponent field) {
        field.setFont(BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(9, 12, 9, 12)
        ));
    }

    public static void styleTextArea(JTextArea area) {
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(BODY);
        area.setBorder(new EmptyBorder(10, 12, 10, 12));
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(BODY);
        table.setForeground(TEXT);
        table.setGridColor(BORDER);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.getTableHeader().setForeground(TEXT);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
    }
}
