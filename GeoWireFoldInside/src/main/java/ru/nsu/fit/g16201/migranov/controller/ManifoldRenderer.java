package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.GeodesicsCalculator;
import ru.nsu.fit.g16201.migranov.model.ManifoldFunction;
import ru.nsu.fit.g16201.migranov.view.ManifoldInsidePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ManifoldRenderer {

    private ManifoldInsidePanel panel;
    private ThreadPoolExecutor executor;

    private int[][] colors;
    private GeodesicsCalculator geodesicsCalculator;
    private ManifoldFunction function;
    private double zn;
    private double sw;
    private double sh;
    private int width, height;

    private double observerHeight;
    private double posU, posV;
    private double rotationAngle = 0;

    private double dt = 1;

    private int skyColor = Color.CYAN.getRGB();

    private Texture texture;
    private double[][] g;

    ManifoldRenderer(ManifoldInsidePanel panel, GeodesicsCalculator geodesicsCalculator, double zn, double sw, double sh, double uPos, double vPos, Texture texture)
    {
        this.panel = panel;
        this.geodesicsCalculator = geodesicsCalculator;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;


        this.posU = uPos;
        this.posV = vPos;

        g = geodesicsCalculator.calculateMetricTensor(uPos, vPos);
        function = geodesicsCalculator.getFunction();
        this.texture = texture;
    }

    void render(int numberOfThreads)   //todo: направление?
    {
        observerHeight = sh/2;

        width = panel.getCanvasWidth();
        height = panel.getCanvasHeight();

        colors = new int[height][width];

        executor = new ThreadPoolExecutor(numberOfThreads, numberOfThreads, 1000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(width));

        double nearStartX = -sw/2;

        double dx = sw / width;
        double x;

        x = nearStartX + dx/2;
        for(int j = 0; j < width; j++)
        {
            //центры пикселя
            executor.execute(new RendererTask(x, j));
            x += dx;
        }

        executor.shutdown();

        //запускать это в новом треде, а не в свинговом, нет смысла, так как появятся плохие эффекты связанные с изменением размера и сохранением размера массива
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
                panel.setPixel(j, height - i - 1, colors[i][j]);
            }
        }

        panel.repaint();
    }

    public void moveForward(double distance) {
        double s = 0;
        int iters = 0;

        double dirU = 0, dirV = zn;

        double[] state = function.calculateInitialState(posU, posV, dirU, dirV);

        double u, v, du, dv, t = 0;
        while(s < distance && iters < 1000) {
            u = state[2]; v = state[3];
            du = state[0]; dv = state[1];
            double ds = calculateGeodesicLength(dt * du, dt * dv);
            state = geodesicsCalculator.geodesicEquationStep(state, t, dt);    //шаг по геодезиечской
            s += ds;
            iters++;
        }
        posU = state[2];
        posV = state[3];
    }


    public void moveBack(double distance) {

        double s = 0;
        int iters = 0;

        double dirU = 0, dirV = zn;

        double[] state = function.calculateInitialState(posU, posV, dirU, dirV);

        double u, v, du, dv, t = 0;
        while(s < distance && iters < 1000) {
            u = state[2]; v = state[3];
            du = state[0]; dv = state[1];
            double ds = calculateGeodesicLength(dt * du, dt * dv);
            state = geodesicsCalculator.geodesicEquationStep(state, t, -dt);    //шаг по геодезиечской
            s += ds;
            iters++;
        }
        posU = state[2];
        posV = state[3];

    }


    class RendererTask implements Runnable {

        private double realX;
        private int picX;

        RendererTask(double x, int j) {
            realX = x;
            picX = j;
        }

        //считаем что в маленькой окрестности наблюдателя лучи идут по евклидовым правилам (и это правильно, см. сфера - маленькие треугольники и большие!)
        @Override
        public void run() {
            double dy = sh/height, realY = dy/2;

            //todo: повороты

            //стопэ, но ведь u и v это углы
            double dirU = realX, dirV = zn;

            double len = Math.sqrt(dirU * dirU + dirV * dirV);  //это только по плоскости, не учитывает высоту!

            //double [] state = new double[] {dirU, dirV, posU, posV};
            double [] state = function.calculateInitialState(posU, posV, dirU, dirV);

            double nextDistance = len, s = 0;
            final double dMultiplier = zn * observerHeight;

            int iters = 0;
            int picY = 0;

            double t = 0;
            while(realY <= observerHeight && iters < 10000)
            {
                double u = state[2], v = state[3];
                double du = state[0], dv = state[1];
                if(s >= nextDistance)
                {
                    colors[picY][picX] = texture.getColorAt(u, v);
                    picY++;     //переходим к следующему пикселю в столбце, соответственно, надо пройти ещё.
                    realY += dy;
                    nextDistance = len * (observerHeight / (sh - realY));
                    //nextDistance = zn * realY/(observerHeight - realY);   //todo подумать
                }

                double ds = calculateGeodesicLength(dt * du, dt * dv);
                s += ds;
                state[0] /= ds; //а t? сейчас t = 1, а если другое?
                state[1] /= ds;

                state = geodesicsCalculator.geodesicEquationStep(state, t, dt);    //шаг по геодезиечской
                t += dt;

                iters++;
            }


            for(; picY < height; picY++)
                colors[picY][picX] = skyColor;
        }

    }




    private double calculateGeodesicLength(double du, double dv)
    {
        return Math.sqrt(g[0][0] * du * du + 2 * g[0][1] * du * dv + g [1][1] * dv * dv);
    }

    public double getDt() {
        return dt;
    }
}
