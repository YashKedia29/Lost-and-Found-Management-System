package edu.upes.lostfound.view.components;

import edu.upes.lostfound.ui.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarButton extends JButton {
    private boolean selectedState;
    private boolean hover;

    public SidebarButton(String text) {
        super(text);
        setUI(new BasicButtonUI());
        setFont(UITheme.H3);
        setForeground(Color.WHITE);
        setHorizontalAlignment(LEFT);
        setBorder(BorderFactory.createEmptyBorder(0, 18, 0, 12));
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(220, 44));
        setMinimumSize(new Dimension(220, 44));
        setMaximumSize(new Dimension(220, 44));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    public void setSelectedState(boolean selectedState) {
        this.selectedState = selectedState;
        setFont(selectedState ? UITheme.H3.deriveFont(java.awt.Font.BOLD) : UITheme.H3);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (selectedState || hover) {
            g2.setColor(selectedState ? new Color(255, 255, 255, 48) : new Color(255, 255, 255, 24));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        }
        g2.dispose();
        super.paintComponent(g);
    }
}
