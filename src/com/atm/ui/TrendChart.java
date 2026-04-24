package com.atm.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TrendChart extends JPanel {
    private List<Double> data;

    public TrendChart(List<Double> data) {
        this.data = data;
        setOpaque(false);
    }

    private String formatValue(double value) {
        String sign = value < 0 ? "-" : "+";
        double absValue = Math.abs(value);
        if (absValue >= 1000) return sign + String.format("%.1fK", absValue / 1000.0);
        return sign + String.format("%.0f", absValue);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (data == null || data.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int paddingLeft = 65; 
        int paddingBottom = 45;
        int paddingTop = 40;
        int paddingRight = 60; // Increased right padding for "$14.5K" text

        int width = getWidth() - paddingLeft - paddingRight;
        int height = getHeight() - paddingTop - paddingBottom;

        double maxBalance = data.stream().mapToDouble(Double::doubleValue).max().orElse(1);
        double minBalance = data.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double diff = maxBalance - minBalance;
        if (diff == 0) diff = 1;

        int xStep = (data.size() > 1) ? width / (data.size() - 1) : width;
        int[] xPoints = new int[data.size()];
        int[] yPoints = new int[data.size()];

        for (int i = 0; i < data.size(); i++) {
            xPoints[i] = paddingLeft + (i * xStep);
            double scale = (data.get(i) - minBalance) / diff;
            yPoints[i] = (int) ((paddingTop + height) - (scale * height));
        }

        // 1. Draw Axis Lines
        g2.setColor(new Color(255, 255, 255, 120));
        g2.drawLine(paddingLeft, paddingTop, paddingLeft, paddingTop + height);
        g2.drawLine(paddingLeft, paddingTop + height, paddingLeft + width, paddingTop + height);

        // 2. Draw Y-Axis (Max/Min)
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        g2.drawString("₹" + formatValue(maxBalance).replace("+", ""), 5, paddingTop + 5);
        g2.drawString("₹" + formatValue(minBalance).replace("+", ""), 5, paddingTop + height);

        // 3. Draw the Line
        g2.setStroke(new BasicStroke(3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.WHITE);
        g2.drawPolyline(xPoints, yPoints, data.size());

        // 4. Draw Dots and Specific Delta Labels
        for (int i = 0; i < data.size(); i++) {
            g2.setColor(new Color(100, 255, 100));
            g2.fillOval(xPoints[i] - 5, yPoints[i] - 5, 10, 10);

            if (i > 0) {
                double change = data.get(i) - data.get(i - 1);
                String label = formatValue(change);
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                g2.setColor(change >= 0 ? new Color(160, 255, 160) : new Color(255, 160, 160));

                int xOff = 0, yOff = 0;

                // MANUAL POSITIONING BY POINT INDEX
                if (i == 1) { xOff = -15; yOff = 25; } // +500
                else if (i == 2) { xOff = -35; yOff = -15; } // +1.5K
                else if (i == 3) { xOff = -45; yOff = 20; }  // -1.5K (Move Left)
                else if (i == 4) { xOff = 10; yOff = 20; }   // -200 (Move Right)
                else if (i == 5) { xOff = -35; yOff = -15; } // +3.0K
                else if (i == 6) { xOff = -45; yOff = 15; }  // -3.3K (Move Left)

                g2.drawString(label, xPoints[i] + xOff, yPoints[i] + yOff);
            }
        }

        // 5. Final Now Balance ($14.5K) - Put it to the RIGHT of the point
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("SansSerif", Font.BOLD, 13));
        String nowLabel = "₹" + formatValue(data.get(data.size()-1)).replace("+", "");
        g2.drawString(nowLabel, xPoints[data.size()-1] + 12, yPoints[data.size()-1] - 5);

        // X-Axis Text
        g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2.drawString("Past", paddingLeft, paddingTop + height + 25);
        g2.drawString("Now", paddingLeft + width - 10, paddingTop + height + 25);
    }
}