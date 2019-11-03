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


    public ManifoldRenderer(ManifoldInsidePanel panel)
    {
        this.panel = panel;
    }

    public void render(int numberOfThreads)
    {
        int width = panel.getWidth();
        int height = panel.getHeight();

        floatColors = new int[height][width];


        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width));

    }

    public class RendererTask implements Runnable {

        @Override
        public void run() {

        }
    }


}
