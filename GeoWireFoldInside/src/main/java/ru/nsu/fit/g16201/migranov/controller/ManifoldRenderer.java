package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.GeodesicsCalculator;
import ru.nsu.fit.g16201.migranov.view.ManifoldInsidePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
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

    private Texture texture;
    private double[][] g;

    ManifoldRenderer(ManifoldInsidePanel panel, GeodesicsCalculator geodesicsCalculator, double zn, double sw, double sh, double uPos, double vPos, BufferedImage texture)
    {
        this.panel = panel;
        this.geodesicsCalculator = geodesicsCalculator;
        this.zn = zn;
        this.sw = sw;
        this.sh = sh;


        this.posU = uPos;
        this.posV = vPos;

        g = geodesicsCalculator.calculateMetricTensor(uPos, vPos);

        this.texture = new Texture(texture);
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

        System.out.println();

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

    class RendererTask implements Runnable {

        private double realX;
        private int picX;

        RendererTask(double x, int j) {
            realX = x;
            picX = j;
        }

//

        //считаем что в маленькой окрестности наблюдателя лучи идут по евклидовым правилам (и это правильно, см. сфера - маленькие треугольники и большие!)
        @Override
        public void run() {
            double dy = sh/height, realY = dy/2;

            //todo: повороты

            double dirU = realX, dirV = zn;

            System.out.print(realX + " ");
            double len = Math.sqrt(dirU * dirU + dirV * dirV);
            dirU /= len;
            dirV /= len;

            //System.out.print("{" + dirU + " " + dirV + "}");


            double [] state = new double[] {posU, posV, dirU, dirV};

            double nextDist = len, s = 0;
            final double dMultiplier = zn * observerHeight;

            int iters = 0;

            int picY = 0;
            while(realY <= observerHeight)
            {
                double d_ = dMultiplier / realY;
                double u = state[0], v = state[1];
                double du = state[2], dv = state[3];
                if(s >= nextDist)
                {

                    //todo: достать цвет из текстуры по координатам u, v точки куда дошли и дать соответсвующему пикселю

                    colors[picY][picX] = texture.getColorAt(u, v);

                    picY++;     //переходим к следующему пикселю в столбце, соответственно, надо пройти ещё.
                    realY += dy;
                    nextDist = 0; //scrDistAlongRay * camY / sy; //todo; new nextdist >= old nextdist
                }

                double ds = calculateGeodesicLength(dt * du, dt * dv);
                state = geodesicsCalculator.geodesicEquationStep(state, dt);    //шаг по геодезиечской
                s += ds;

                iters++;
            }

            for(; picY < height; picY++)
                colors[picY][picX] = skyColor;
        }




    }


    private double calculateGeodesicLength(double du, double dv)
    {
        return g[0][0] * du * du + 2 * g[0][1] * du * dv + g [1][1] * dv * dv;
    }

}
