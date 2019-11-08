package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.view.ManifoldInsidePanel;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ManifoldRenderer {
    private final double observerHeight = 1;

    private ManifoldInsidePanel panel;
    private ThreadPoolExecutor executor;

    private int[][] floatColors;
    private double zn, sw, sh;
    private int width, height;


    public ManifoldRenderer(ManifoldInsidePanel panel)
    {
        this.panel = panel;
    }

    public void render(int numberOfThreads, double zn, double sw, double sh)
    {
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;
        width = panel.getWidth();
        height = panel.getHeight();

        floatColors = new int[height][width];

        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width));

        double nearStartX = -sw/2;


        double dx = sw/ width;
        double x;


        x = nearStartX + dx/2;
        for(int j = 0; j < width; j++)
        {
            //центры пикселя
            executor.execute(new RendererTask(x, j));
            x += dx;
        }

        executor.shutdown();
    }

    class RendererTask implements Runnable {

        private double pixelX;
        private int picX;

        RendererTask(double x, int j) {
            pixelX = x;
            picX = j;
        }

        @Override
        public void run() {
            double nearStartY = -sh/2, dy = sh/height;

            double y = nearStartY + dy/2;

            for(int i = 0; i < height; i++) {
                y += dy;
            }
        }


    }


}
