package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.GeodesicsCalculator;
import ru.nsu.fit.g16201.migranov.view.ManifoldInsidePanel;
import ru.nsu.fit.g16201.migranov.view.ManifoldOutsidePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ManifoldRenderer {

    private ManifoldInsidePanel panel;
    private ThreadPoolExecutor executor;

    private int[][] colors;
    private GeodesicsCalculator geodesicsCalculator;

    private double zn;
    private double sw;
    private double sh;
    private int width, height;

    private double observerHeight;
    private double posU, posV;
    private double rotationAngle = 0;

    private double dt = 1;

    private int skyColor = Color.CYAN.getRGB();

    private BufferedImage texture;

    public ManifoldRenderer(ManifoldInsidePanel panel, GeodesicsCalculator geodesicsCalculator, double zn, double sw, double sh, double uPos, double vPos, BufferedImage texture)
    {
        this.panel = panel;
        this.geodesicsCalculator = geodesicsCalculator;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;
        this.posU = uPos;
        this.posV = vPos;
        this.texture = texture;
    }

    public void render(int numberOfThreads)
    {
        observerHeight = sh/2;

        width = panel.getWidth();
        height = panel.getHeight();

        colors = new int[height][width];

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


        Runnable checker = () -> {
            while(true)
            {
                try {
                    if (executor.awaitTermination(300, TimeUnit.MILLISECONDS)) break;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0; i < height; i++)
            {
                for(int j = 0; j < width; j++)
                {
                    panel.setPixel(j, i, colors[i][j]);
                }
            }

            panel.repaint();
        };
        new Thread(checker).start();
    }

    class RendererTask implements Runnable {

        private double pixelX;
        private int picX;

        RendererTask(double x, int j) {
            pixelX = x;
            picX = j;
        }

//

        //считаем что в маленькой окрестности наблюдателя лучи идут по евклидовым правилам (и это правильно, см. сфера - маленькие треугольники и большие!)
        @Override
        public void run() {
            //double nearStartY = -sh/2, dy = sh/height;

            //double y = nearStartY + dy/2;
            double dy = sh/height, y = dy/2;

            //todo: повороты

            double dirU = pixelX, dirV = zn;

            double len = Math.sqrt(dirU * dirU + dirV * dirV);
            dirU /= len;
            dirV /= len;

            double [] state = new double[] {posU, posV, dirU, dirV};


            //y = h'

            double nextDist = len, s = 0;
            final double dMultiplier = zn * observerHeight;
            for(int i = 0; i < height; i++) {
                y += dy;
                int iters = 0;
                if(y <= observerHeight) {

                    /*double d_ = dMultiplier / y;
                    while(sy > minsy && iters < 5000)
                    {


                        iters++;
                    }*/

                    colors[i][picX] = Color.gray.getRGB();
                }
                else
                {
                    colors[i][picX] = skyColor; //todo: можно сделать заранее, массово, для всего ряда
                }


            }
        }




    }


}
