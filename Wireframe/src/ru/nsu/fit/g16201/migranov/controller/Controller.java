package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Figure;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Controller {
    private static Matrix splineMatrix = Matrix.multiplyByScalar(1.0/6, new Matrix(4, 4, -1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0));

    private WireframePanel wireframePanel;

    private Point3D eye = new Point3D(-10, 0, 0);
    private Point3D ref = new Point3D(10, 0, 0);
    private Point3D up = new Point3D(0, 1, 0);

    private int n;
    private int m;
    private int k;

    private double zn;
    private double zf;
    private double sw;
    private double sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости

    private Matrix boxMatrix;

    private Color backgroundColor;
    private Matrix sceneRotateMatrix;
    private Matrix cameraMatrix;
    private Matrix projectionMatrix;

    private Figure figure = null;
    private int Ni, Nj, Ti, Tj;

    private Integer prevX = null, prevY = null;

    private int width, height;

    private boolean isDrawingFirstTime = true;

    private double xAllAngle = 0, yAllAngle = 0;

    private int[] knotsI, knotsJ;

    public Controller(WireframePanel wireframePanel) {
        this.wireframePanel = wireframePanel;

        cameraMatrix = Matrix.getViewMatrixNew(eye, ref, up);  //c 153

        wireframePanel.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();

            if(e.isControlDown())
            {
                //todo: не похволять сильно крутить
                double dz = -count * 1;
                Point3D forward = Point3D.add(ref, Point3D.getNegative(eye));
                forward = forward.normalize();
                //ref = Point3D.add(ref, Point3D.multiplyByScalar(dz, forward));
                Point3D oldEye = eye;
                eye = Point3D.add(eye, Point3D.multiplyByScalar(dz, forward));
                cameraMatrix = Matrix.getViewMatrixNew(eye, ref, up);  //матрица получается аналогичная
                Matrix tr = Matrix.getTranslationMatrix(new Point3D(0, 0, -dz));
                //viewMatrix = Matrix.multiply(tr, viewMatrix);
                drawFigure();
            }
            else
            {
                double c;

                c = 1 - count*0.01;
                zn*=c;

                projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
                drawFigure();
            }
        });

        wireframePanel.setFocusable(true);
        wireframePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                double dx = 0, dy = 0, dz = 0;
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT)
                    dx = 0.1;
                else if (key == KeyEvent.VK_RIGHT)
                    dx = -0.1;
                else if (key == KeyEvent.VK_UP)
                    dy = 0.1;
                else if (key == KeyEvent.VK_DOWN)
                    dy = -0.1;

                Matrix tr = Matrix.getTranslationMatrix(new Point3D(dx, dy, dz));

                Point3D z = new Point3D(-(ref.x - eye.x), -(ref.y - eye.y), -(ref.z - eye.z)).normalize();
                Point3D x = Point3D.getVectorProduct(up, z).normalize();
                Point3D y = Point3D.getVectorProduct(z, x);

                ref = Point3D.add(ref, Point3D.multiplyByScalar(dz, z));
                eye = Point3D.add(eye, Point3D.multiplyByScalar(dz, z));
                ref = Point3D.add(ref, Point3D.multiplyByScalar(-dy, y));
                eye = Point3D.add(eye, Point3D.multiplyByScalar(-dy, y));
                ref = Point3D.add(ref, Point3D.multiplyByScalar(dx, x));
                eye = Point3D.add(eye, Point3D.multiplyByScalar(dx, x));
                //viewMatrix = Matrix.getViewMatrixNew(eye, ref, up);
                cameraMatrix = Matrix.multiply(tr, cameraMatrix);
                drawFigure();
            }
        });

        wireframePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                int x = e.getX();
                int y = e.getY();
                if(prevX != null) {
                    int dx = x - prevX;
                    int dy = y - prevY;

                    double xAngle = 0.01 * dx;
                    double yAngle = 0.01 * dy;


                        Matrix rot = figure.getRotateMatrix();
                        Matrix xRot = Matrix.getYRotateMatrix(xAngle);
                        Matrix yRot = Matrix.getZRotateMatrix(-yAngle);
                        Matrix xr = Matrix.multiply(xRot, rot);
                        Matrix xyr = Matrix.multiply(yRot, xr);
                        figure.setRotateMatrix(xyr);

                        /*Matrix xy = Matrix.multiply(yRot, xRot);
                        //надо взять обратную к scenerotate матрицу и умножить на xy?

                        Matrix xSceneInverse = Matrix.getYRotateMatrix(-xAllAngle);
                        Matrix ySceneInverse = Matrix.getZRotateMatrix(yAllAngle);
                        Matrix sceneInverse = Matrix.multiply(xSceneInverse, ySceneInverse);

                        Matrix rotAdd = Matrix.multiply(sceneInverse, xy);
                        Matrix res = Matrix.multiply(rotAdd, sceneRotateMatrix);
                        res = Matrix.multiply(sceneRotateMatrix, res);
                        figures.get(currentRotateFigure).setRotateMatrix(res);*/

                    drawFigure();
                }
                prevX = x;
                prevY = y;
            }
        });

        wireframePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                prevX = null;
                prevY = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                wireframePanel.requestFocusInWindow();
            }
        });

    }

    public void drawFigure()
    {
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE, minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;      //куда??!

        wireframePanel.clear();
        Point3D[][] splinePoints = figure.getSplinePoints();

        /* Step size along the curve */
        double incrementU = (double)(Ni - Ti + 2) / n / k;
        double incrementV = (double)(Nj - Tj + 2) / m / k;

        //n и m - это фактически разрешение

        Point3D[][] modelPoints = figure.getModelPoints();
        Matrix translateMatrix = Matrix.getTranslationMatrix(figure.getCenter());
        Matrix rtm = Matrix.multiply(translateMatrix, figure.getRotateMatrix());

        double u = 0;
        for(int i = 0; i < n * k; i++)  //<=?
        {
            double v = 0;
            for(int j = 0; j < m * k; j++)  //<=?
            {
                if(j == m*k)
                    System.out.print("");
                Point3D Puv = calculateSplineFunction(u, v, splinePoints);

                //System.out.print(Puv.x + " " + Puv.y + " " + Puv.z + "      ");
                v += incrementV;

                double x = Puv.x, y = -Puv.z, z = Puv.y;

                Matrix p = new Matrix(4, 1, x, y, z, 1);

                Matrix np = Matrix.multiply(rtm, p);                        //на самом деле произведение r и t имеет простой вид - можно упростить
                double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
                modelPoints[i][j] = new Point3D(x, y, z);

                if (nx < minX) minX = nx;
                if (nx > maxX) maxX = nx;
                if (ny < minY) minY = ny;
                if (ny > maxY) maxY = ny;
                if (nz < minZ) minZ = nz;
                if (nz > maxZ) maxZ = nz;

            }
            //System.out.println();
            u += incrementU;
        }

        u = 0;
        for(int i = 0; i < n * k; i++)  //<=?
        {
            Point3D Puv = calculateSplineFunctionEdgeV(u, splinePoints);

            double x = Puv.x, y = -Puv.z, z = Puv.y;

            Matrix p = new Matrix(4, 1, x, y, z, 1);

            Matrix np = Matrix.multiply(rtm, p);                        //на самом деле произведение r и t имеет простой вид - можно упростить
            double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
            modelPoints[i][m*k] = new Point3D(x, y, z);

            if (nx < minX) minX = nx;
            if (nx > maxX) maxX = nx;
            if (ny < minY) minY = ny;
            if (ny > maxY) maxY = ny;
            if (nz < minZ) minZ = nz;
            if (nz > maxZ) maxZ = nz;

            u += incrementU;
        }




        //todo:
        //короче, там при i = n*k j = m*k возникают проблемы с вычислением базисной функции, тк там надо N(k+1)!
        //|knotsJ| = Nj + Kj + 1
        //кроме того, там сравнивается 3.003 < 3?

        //считаю только один раз, при запуске, чтобы при перемещении точек одной фигуры остальные остальные фигуры оставались на местах
        if(isDrawingFirstTime) {
            double maxDim = Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ);          //nx = 2 * (x - minX)/(maxx- minx) - 1 и для других - но так не сохр пропорции; поэтому делю на одно и то же
            isDrawingFirstTime = false;

            Matrix boxTranslateMatrix = new Matrix(4, 4, 1, 0, 0, -minX,
                    0, 1, 0, -minY,
                    0, 0, 1, -minZ,
                    0, 0, 0, 1);
            //Matrix boxScaleMatrix = new Matrix(4, 4, 2/maxDim, 0, 0, -1, 0, 2/maxDim, 0, -1, 0, 0, 2/maxDim, -1, 0, 0, 0, 1);  //это несимметрично относительно отн нуля
            Matrix boxScaleMatrix = new Matrix(4, 4, 2 / maxDim, 0, 0, -(maxX - minX) / maxDim,
                    0, 2 / maxDim, 0, -(maxY - minY) / maxDim,
                    0, 0, 2 / maxDim, -(maxZ - minZ) / maxDim,
                    0, 0, 0, 1);
            boxMatrix = Matrix.multiply(boxScaleMatrix, boxTranslateMatrix);
        }

        Matrix projView = Matrix.multiply(projectionMatrix, cameraMatrix);
        Matrix projViewBox = Matrix.multiply(projView, boxMatrix);
        Matrix projViewBoxRot = Matrix.multiply(projViewBox, sceneRotateMatrix);

        Matrix resultMatrix = Matrix.multiply(projViewBoxRot, rtm);

        Color color = figure.getColor();
        Point[] uPrev = new Point[m*k+1];   //m*k
        for (int i = 0; i <= n*k; i++) {
            Point vPrev = null;

            for (int j = 0; j <= m * k; j++) {
                Point3D p = modelPoints[i][j];
                Matrix mp = new Matrix(4, 1, p.x, p.y, p.z, 1);
                Matrix nmp = Matrix.multiply(resultMatrix, mp);
                Point3D np = new Point3D(nmp.get(0, 0), nmp.get(1, 0), nmp.get(2, 0));
                double w = nmp.get(3, 0);
                {
                    int x = (int)((np.x/w + 1)/2*wireframePanel.getCanvasWidth());
                    int y = (int)((np.y/w + 1)/2*wireframePanel.getCanvasHeight());

                    if(vPrev != null && i % k == 0)
                    {
                        wireframePanel.drawLine(vPrev.x, vPrev.y, x, y, color);
                    }
                    vPrev = new Point(x, y);

                    if(uPrev[j] != null && j % k == 0)
                    {
                        wireframePanel.drawLine(uPrev[j].x, uPrev[j].y, x, y, color);
                    }
                    uPrev[j] = new Point(x, y);
                }
            }
        }

        wireframePanel.repaint();
    }

    private Point3D calculateSplineFunctionEdgeV(double u, Point3D[][] splinePoints) {
        double Px = 0, Py = 0, Pz = 0;      //function P(u,v) which is a 3D-point
        for (int i = 0; i <= Ni; i++) {
            double bi = calculateSplineBasisFunction(i, Ti, knotsI, u);

            Px += splinePoints[i][Nj].x * bi;
            Py += splinePoints[i][Nj].y * bi;
            Pz += splinePoints[i][Nj].z * bi;

        }
        return new Point3D(Px, Py, Pz);
    }


    public Point3D calculateSplineFunction(double u, double v, Point3D[][] splinePoints)
    {
        //Pij - array of control points (spline points)
        double Px = 0, Py = 0, Pz = 0;      //function P(u,v) which is a 3D-point
        for (int i = 0; i <= Ni; i++) {
            double bi = calculateSplineBasisFunction(i, Ti, knotsI, u);
            for(int j = 0; j <= Nj; j++)
            {
                double bj = calculateSplineBasisFunction(j, Tj, knotsJ, v);
                Px += splinePoints[i][j].x * bi * bj;
                Py += splinePoints[i][j].y * bi * bj;
                Pz += splinePoints[i][j].z * bi * bj;
            }
        }

        return new Point3D(Px, Py, Pz);
    }

    ///k - index (i, j), t - Ti/Tj, u - knot points, v - coordinate (u/v)
    public double calculateSplineBasisFunction(int k, int t, int[] u, double v)    //aka Blending Function akd N
    {
        //http://paulbourke.net/geometry/spline/
        //чем больше степень Ti/Tj - тем боолее гладкая кривая
        double val;

        if(t == 1)
        {
            if ((u[k] <= v) && (v < u[k+1]))    //todo?
                val = 1;
            else
                val = 0;
        }
        else
        {
            if ((u[k+t-1] == u[k]) && (u[k+t] == u[k+1]))
                val = 0;
            else if (u[k+t-1] == u[k])
                val = (u[k+t] - v) / (u[k+t] - u[k+1]) * calculateSplineBasisFunction(k+1,t-1,u,v);
            else if (u[k+t] == u[k+1])
                val = (v - u[k]) / (u[k+t-1] - u[k]) * calculateSplineBasisFunction(k,t-1,u,v);
            else
                val = (v - u[k]) / (u[k+t-1] - u[k]) * calculateSplineBasisFunction(k,t-1,u,v) +
                        (u[k+t] - v) / (u[k+t] - u[k+1]) * calculateSplineBasisFunction(k+1,t-1,u,v);
        }

        return val;
    }

    private void drawSplineLine() {

        /*width = splinePanel.getPreferredSize().width;
        height = splinePanel.getPreferredSize().height;

        splinePanel.clear();
        //T - вектор строка t^3 t^2 t 1, t [0,1]


        List<Point2D> splinePoints = figure.getSplinePoints();

        drawSplinePoints(splinePoints);

        double length = calculateLength(splinePoints);
        Double xPrev = null, yPrev = null;
        Point uv, uvPrev = null;
        double tempLength = 0;

        for(int i = 1; i < splinePoints.size() - 2; i++)
        {
            Matrix Gx = new Matrix(4, 1, splinePoints.get(i - 1).x, splinePoints.get(i).x, splinePoints.get(i + 1).x, splinePoints.get(i + 2).x);
            Matrix Gy = new Matrix(4, 1, splinePoints.get(i - 1).y, splinePoints.get(i).y, splinePoints.get(i + 1).y, splinePoints.get(i + 2).y);
            for(double t = 0; t <= 1; t+=0.005)
            {
                Matrix T = new Matrix(1, 4, t*t*t, t*t, t, 1);
                Matrix TM = Matrix.multiply(T, splineMatrix);
                Matrix X = Matrix.multiply(TM, Gx);
                Matrix Y = Matrix.multiply(TM, Gy);
                double x = X.get(0, 0), y = Y.get(0, 0);

                uv = getUV(x, y);
                if(uvPrev != null) {
                    if(tempLength > b)
                    {
                        splinePanel.drawLine(uvPrev.x, uvPrev.y, uv.x, uv.y, Color.RED);
                        uvPrev = uv;
                        continue;   //нет нужды уже прибавлять
                    }
                    tempLength += Math.sqrt(Math.pow(xPrev - x, 2) + Math.pow(yPrev - y, 2))/length;
                    //if(tempLength >= a && tempLength <= b)
                    if(tempLength >= a)
                        splinePanel.drawLine(uvPrev.x, uvPrev.y, uv.x, uv.y);
                    else
                        splinePanel.drawLine(uvPrev.x, uvPrev.y, uv.x, uv.y, Color.RED);
                }
                uvPrev = uv;
                xPrev = x;
                yPrev = y;
            }
        }

        //todo: Расчёты полезные, но надо 3d

        splinePanel.repaint();*/
    }

    /*private Point getUV(double x, double y) {
        //width = height!
        int u, v;
        double xm = this.xm.get(currentFigureIndex)*scale.get(currentFigureIndex);
        double ym = this.ym.get(currentFigureIndex)*scale.get(currentFigureIndex);
        if(xm > ym)
        {
            u = (int)((x + xm)/2/xm * width);
            v = (int)((-y + ym)/2/xm * height + (height - ym*width/xm)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }
        else
        {
            v = (int)((-y + ym)/2/ym * height);
            u = (int)((x + xm)/2/ym * width + (width - xm*height/ym)/2);  //от 0 до h' < height - непраивльно (смотри картнку) - надо сдвинуть вниз
        }

        return new Point(u, v);
    }*/

    /*private Point2D getXY(int u, int v)
    {
        double x, y;
        double xm = this.xm.get(currentFigureIndex)*scale.get(currentFigureIndex);  //чтобы оставалось пространство по бокам
        double ym = this.ym.get(currentFigureIndex)*scale.get(currentFigureIndex);

        if(xm > ym)
        {
            x = xm*(2.0*u/width - 1);
            y = -2*xm*v/height - ym*width/height + xm + ym;
        }
        else
        {
            y = -ym*(2.0*v/height - 1);
            x = (xm*height + 2*ym*u)/width - xm - ym;
        }
        //при одинаковых width И height случай xm = ym входит
        return new Point2D(x, y);
    }*/

    /*private Point getUV(Point2D p) {
        return getUV(p.x, p.y);
    }*/

    //возвращает матрицу 4x4
    private Matrix read3x3MatrixByRow(BufferedReader br) throws IOException {
        String[] substrings;
        Matrix matrix = new Matrix(4, 4);
        for(int i = 0; i < 3; i++)
        {
            substrings = readLineAndSplit(br);
            matrix.setRow(i, new double[] {Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]), 0});
        }
        matrix.setRow(3, new double[] {0, 0, 0, 1});
        return matrix;
    }

    public void saveFile(File file) {
        /*try(PrintWriter pw = new PrintWriter(file)) {
            pw.println(n + " " + m + " " + k + " " + a + " " + b + " " + c + " " + d);
            pw.println(zn + " " + zf + " " + sw + " " + sh);

            write3x3MatrixByRow(pw, sceneRotateMatrix);

            pw.println(backgroundColor.getRed() + " " + backgroundColor.getGreen() + " " + backgroundColor.getBlue());
            pw.println(figures.size());

            for(Figure figure : figures)
            {
                Color color = figure.getColor();
                pw.println(color.getRed() + " " + color.getGreen() + " " + color.getBlue());

                Point3D center = figure.getCenter();
                pw.println(center.x + " " + center.y + " " + center.z);

                write3x3MatrixByRow(pw, figure.getRotateMatrix());

                Point3D[][] splinePoints = figure.getSplinePoints();
                pw.println(splinePoints.length + " " + splinePoints[0].length + "0 0");

                for(Point2D p : splinePoints)
                {
                    pw.println(p.x + " " + p.y);
                }
            }
        }
        catch(IOException e)
        {
        }

        */ //todo
    }

    //4 строку и столбец - выбрасываем
    private void write3x3MatrixByRow(PrintWriter pw, Matrix matrix) {
        for(int i = 0; i < 3; i++)
        {
            String s;
            //s = String.format("%f %f %f", matrix.get(i, 0), matrix.get(i, 1), matrix.get(i, 2));
            s = matrix.get(i, 0) + " " + matrix.get(i, 1) + " " +  matrix.get(i, 2);
            pw.println(s);
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int getK() {
        return k;
    }

    public void setConstants(int n, int m, int k, double sw, double sh, double zn, double zf, Color color) {
        this.n = n;
        this.m = m;
        this.k = k;


        figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);

        this.sw = sw;
        this.sh = sh;
        this.zn = zn;
        this.zf = zf;
        this.projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
        this.backgroundColor = color;
        wireframePanel.setBackgroundColor(backgroundColor);

        drawSplineLine();
        drawFigure();
    }

    public double getSw() {
        return sw;
    }

    public void setSw(double sw) {
        this.sw = sw;
    }

    public double getSh() {
        return sh;
    }

    public void setSh(double sh) {
        this.sh = sh;
    }


    public double getZf() {
        return zf;
    }

    public double getZn() {
        return zn;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getCurrentColor() {
        return figure.getColor();
    }

    public Point3D getCurrentCenter() {
        return figure.getCenter();
    }

    public int load3DFile(File file) {
        isDrawingFirstTime = true;

        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] substrings;

            substrings = readLineAndSplit(br);
            n = Integer.parseInt(substrings[0]);
            m = Integer.parseInt(substrings[1]);
            k = Integer.parseInt(substrings[2]);
            if(m <= 0 || n <= 0 || k <= 0)
                throw new IOException("Wrong m, n, or k");

            substrings = readLineAndSplit(br);
            zn = Double.parseDouble(substrings[0]);
            zf = Double.parseDouble(substrings[1]);
            sw = Double.parseDouble(substrings[2]);
            sh = Double.parseDouble(substrings[3]);
            if(!(zn > 0 && zf > zn && sw > 0 && sh > 0))
                throw new IOException("Wrong clipping");
            projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);

            sceneRotateMatrix = read3x3MatrixByRow(br);

            substrings = readLineAndSplit(br);
            backgroundColor = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));
            wireframePanel.setBackgroundColor(backgroundColor);

            substrings = readLineAndSplit(br);
            Color color = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

            substrings = readLineAndSplit(br);
            Point3D center = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

            Matrix rotateMatrix = read3x3MatrixByRow(br);

            substrings = readLineAndSplit(br);
            Ni = Integer.parseInt(substrings[0]);
            Nj = Integer.parseInt(substrings[1]);
            Ti = Integer.parseInt(substrings[2]);
            Tj = Integer.parseInt(substrings[3]);

            //ti, tj - degree
            /*if(Ni < 4 || Nj < 4)
                throw new IOException("Not enough spline points");*/ //todo: ввести условия

            Point3D[][] splinePoints = new Point3D[Ni + 1][Nj + 1];
            for(int i = 0; i <= Ni; i++)
            {
                for(int j = 0; j <= Nj; j++)
                {
                    substrings = readLineAndSplit(br);
                    Point3D splinePoint = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                    splinePoints[i][j] = splinePoint;
                }
            }
            figure = new Figure(center, color, rotateMatrix, splinePoints);

            figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);

            calculateKnots();
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }

        drawFigure();

        return 0;
    }

    private void calculateKnots() {
        knotsI = new int[Ni +  Ti + 1];
        for(int i = 0; i < knotsI.length; i++)
        {
            if(i < Ti)
                knotsI[i] = 0;
            else if (Ti <= i && i <= Ni)
                knotsI[i] = i - Ti + 1;
            else //i > n
                knotsI[i] = Ni - Ti + 2;
        }

        knotsJ = new int[Nj +  Tj + 1];
        for(int j = 0; j < knotsJ.length; j++)
        {
            if(j < Tj)
                knotsJ[j] = 0;
            else if (Tj <= j && j <= Nj)
                knotsJ[j] = j - Tj + 1;
            else //j > n
                knotsJ[j] = Nj - Tj + 2;
        }
    }

    private String[] readLineAndSplit(BufferedReader br) throws IOException
    {
        String line;
        String[] substrings;
        do {
            line = br.readLine();
            if(line == null)
                return null;
            line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
            substrings = line.split("\\s+");
        }
        while(substrings.length == 0 || "".equals(substrings[0]));
        return substrings;
    }
}