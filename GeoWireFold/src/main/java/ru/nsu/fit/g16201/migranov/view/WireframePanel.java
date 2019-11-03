package ru.nsu.fit.g16201.migranov.view;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class WireframePanel extends JPanel {
    private int width, height;
    private BufferedImage canvas;
    private Graphics2D canvasGraphics;

    private Color backgroundColor;

    public WireframePanel() {
        super();
        width = 1;
        height = 1;
        //setPreferredSize(new Dimension(width, height));
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);
        canvasGraphics.setBackground(Color.WHITE);

        canvasGraphics.clearRect(0, 0, width, height);
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        //canvasGraphics.drawLine(0, 0, width - 1, height - 1);

        g.drawImage(canvas, 0, 0, null, null);
    }

    @Override
    public void setPreferredSize(Dimension d)
    {
        super.setPreferredSize(d);

        width = d.width;
        height = d.height;

        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        canvasGraphics = canvas.createGraphics();
        canvasGraphics.setColor(Color.BLACK);
        canvasGraphics.setBackground(backgroundColor);
        canvasGraphics.clearRect(0, 0, width, height);
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        canvasGraphics.setBackground(backgroundColor);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        canvasGraphics.setColor(color);
        canvasGraphics.drawLine(x1, y1, x2, y2);

    }

    public int getCanvasWidth() {
        return width;
    }

    public int getCanvasHeight() {
        return height;
    }

    public void clear() {
        canvasGraphics.clearRect(0, 0, width, height);
    }
}
