package com.sad.myadvice.gui;

import javax.swing.*;
import java.awt.*;

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
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(normalColor);
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