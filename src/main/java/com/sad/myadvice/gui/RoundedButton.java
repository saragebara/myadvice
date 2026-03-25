package com.sad.myadvice.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private int radius;
    private Color normalColor;
    private Color hoverColor;

    public RoundedButton(String text, int radius) {
        super(text);
        this.radius = radius;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);

        // simple hover effect
       addMouseListener(new MouseAdapter() {
    @Override
    public void mouseEntered(MouseEvent e) {
        if (hoverColor != null) {
            setBackground(hoverColor);
            repaint();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (normalColor != null) {
            setBackground(normalColor);
            repaint();
        }
    }
    });
    }

    public void setButtonColors(Color normal, Color hover) {
        this.normalColor = normal;
        this.hoverColor = hover;
        setBackground(normal);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        g2.dispose();
        super.paintComponent(g);
    }
}