package edu.upes.lostfound.view.components;

import edu.upes.lostfound.ui.UITheme;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class TableFactory {
    private TableFactory() {
    }

    public static JTable table(DefaultTableModel model) {
        JTable table = new JTable(model);
        UITheme.styleTable(table);
        return table;
    }

    public static DefaultTableModel model(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
