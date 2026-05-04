package edu.upes.lostfound.view.components;

import edu.upes.lostfound.ui.UITheme;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    public enum Kind {
        PRIMARY,
        SECONDARY,
        GHOST,
        DANGER
    }

    private final Kind kind;
    private boolean hover;

    public ModernButton(String text, Kind kind) {
        super(text);
        this.kind = kind;
        setFont(UITheme.H3);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setOpaque(false);
        setForeground(foregroundFor(kind));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 16, 10, 16));
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

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundFor(kind, hover));
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
        if (kind == Kind.GHOST) {
            g2.setColor(UITheme.BORDER);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
        }
        g2.dispose();
        super.paintComponent(g);
    }

    private Color backgroundFor(Kind kind, boolean hover) {
        return switch (kind) {
            case PRIMARY -> hover ? UITheme.PRIMARY_DARK : UITheme.PRIMARY;
            case SECONDARY -> hover ? new Color(13, 148, 136) : UITheme.ACCENT;
            case DANGER -> hover ? new Color(185, 28, 28) : UITheme.DANGER;
            case GHOST -> hover ? new Color(241, 245, 249) : UITheme.SURFACE;
        };
    }

    private Color foregroundFor(Kind kind) {
        return kind == Kind.GHOST ? UITheme.TEXT : Color.WHITE;
    }
}
