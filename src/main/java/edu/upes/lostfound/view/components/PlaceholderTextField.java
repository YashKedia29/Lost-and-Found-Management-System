package edu.upes.lostfound.view.components;

import edu.upes.lostfound.ui.UITheme;

import javax.swing.JTextField;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class PlaceholderTextField extends JTextField {
    private final String placeholder;

    public PlaceholderTextField(String placeholder) {
        this.placeholder = placeholder;
        UITheme.styleField(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getText().isEmpty() && !isFocusOwner()) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(148, 163, 184));
            g2.setFont(getFont());
            g2.drawString(placeholder, getInsets().left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
            g2.dispose();
        }
    }
}
