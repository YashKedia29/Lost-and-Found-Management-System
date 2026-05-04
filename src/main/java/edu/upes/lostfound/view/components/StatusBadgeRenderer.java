package edu.upes.lostfound.view.components;

import edu.upes.lostfound.ui.UITheme;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;

public class StatusBadgeRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        setHorizontalAlignment(CENTER);
        setForeground(Color.WHITE);
        setBackground(colorFor(String.valueOf(value)));
        if (isSelected) {
            setForeground(UITheme.TEXT);
        }
        return c;
    }

    private Color colorFor(String value) {
        return switch (value) {
            case "APPROVED", "APPROVED CLAIM" -> UITheme.SUCCESS;
            case "MATCHED" -> UITheme.ACCENT;
            case "CLAIMED" -> UITheme.PRIMARY;
            case "REJECTED" -> UITheme.DANGER;
            default -> UITheme.WARNING;
        };
    }
}
