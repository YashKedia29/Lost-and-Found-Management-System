package edu.upes.lostfound.view.components;

import edu.upes.lostfound.ui.UITheme;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.File;

public class ImagePreviewPanel extends RoundedPanel {
    private final JLabel label = new JLabel("No image selected", JLabel.CENTER);

    public ImagePreviewPanel() {
        super(18, new Color(248, 250, 252));
        setLayout(new BorderLayout());
        label.setFont(UITheme.SMALL);
        label.setForeground(UITheme.MUTED);
        add(label, BorderLayout.CENTER);
    }

    public void showImage(String path) {
        if (path == null || path.isBlank() || !new File(path).exists()) {
            label.setIcon(null);
            label.setText("No image selected");
            return;
        }
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(170, 120, Image.SCALE_SMOOTH);
        label.setText("");
        label.setIcon(new ImageIcon(scaled));
    }
}
