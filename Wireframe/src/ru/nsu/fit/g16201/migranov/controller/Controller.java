/*

B-Spline functions: область распределения поделена на узлы

KNOTS:
Let U be a set of m + 1 non-decreasing numbers, u0 <= u2 <= u3 <= ... <= um.
The ui's are called knots, the set U the knot vector, and the half-open interval [ui, ui+1) the i-th knot span.
Note that since some ui's may be equal, some knot spans may not exist.
If a knot ui appears k times (i.e., ui = ui+1 = ... = ui+k-1), where k > 1, ui is a multiple knot of multiplicity k, written as ui(k).
Otherwise, if ui appears only once, it is a simple knot. If the knots are equally spaced (i.e., ui+1 - ui is a constant for 0 <= i <= m - 1), the knot vector or the knot sequence is said uniform; otherwise, it is non-uniform.

The knots can be considered as division points that subdivide the interval [u0, um] into knot spans.
All B-spline basis functions are supposed to have their domain on [u0, um]!!!!!

To define B-spline basis functions, we need one more parameter, the degree of these basis functions, t.
The i-th B-spline basis function of degree y, written as Ni,t(u), is defined recursively.
Its properties are:
    *   Ni,t(u) is a degree t-1 polynomial in u
        В оригинале статьи было degree t polynomial, но у них рекурсия идет до Ni,0, а не Ni,1 как у меня!!!
    *   For all i, p and u, Ni,p(u) is non-negative
    *   Ni,t(u) is a non-zero polynomial on [ui,ui+t+1)  (ui - knots)

B-spline curve involves:
    1) a set of n+1 control points                  (в нашем случае (Ni + 1)x(Nj + 1)
    2) a knot vector of m knots                     (так же)
    3) a degree (order) p.                          (две степени Ti и Tj)
    На самом деле наш многочлен будет степени p-1 при таком определнии, как у нас!
Note that n, m and p must satisfy m = n + p + 1

The point on the curve that corresponds to a knot ui, C(ui), is referred to as a knot point.

A spline of order t is a piecewise polynomial function of degree t − 1

!!! When the knots are distinct, the first n − 1 derivatives of the polynomial pieces are continuous across each knot.
When r knots are coincident, then only the first n − r derivatives of the spline are continuous across that knot.

Свойства кривой порядка t:
Degree t-1 and Ct-2 continuity over range of n+1 control points (но только если все узлы разные)

В трёхмерном случае имеем набор Ni+1 строк и Nj+1 столбцов контрольных точек pij, 0 <= i <= Ni; 0 <= j <= Nj;
Ti, Tj - это степени (degrees). Степени многочлена N по соответсвующим направлениям будут Ti - 1 и Tj - 1. Настоящие степени - это Ti - 1
The continuity of the surface in each parametric direction is k-2, l-2 respectively
uk are known as break points, where they occur on the curve are known as knots.
There are a number of possible options for the knot positions, for example a uniform spacing where uk = k.
Мы используем более сложную расстановку узлов, главное чтобы ui <= u(i+1)

2 <= Ti <= Ni + 1 (можно и 1, это будет просто график контрольных точек)

 */

package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class Controller {
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

    private int Ni, Nj, Ti, Tj;

    private Integer prevX = null, prevY = null;

    private int width, height;

    private boolean needsToBeRedrawn = true, isDrawingFirstTime = true;

    private double xAllAngle = 0, yAllAngle = 0;

    private int[] knotsI, knotsJ;

    //фигура
    private Color figureColor;
    private Matrix figureRotateMatrix;
    private Point3D[][] splinePoints;
    private Point3D[][] modelPoints;

    double uMax, vMax, uMin, vMin;


    public Controller(WireframePanel wireframePanel) {
        this.wireframePanel = wireframePanel;

        cameraMatrix = Matrix.getViewMatrixNew(eye, ref, up);  //c 153

        wireframePanel.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();

            if(e.isControlDown())
            {
                //todo: не позволять сильно зумить
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
                    Matrix rot = figureRotateMatrix;
                    Matrix xRot = Matrix.getYRotateMatrix(xAngle);
                    Matrix yRot = Matrix.getZRotateMatrix(-yAngle);
                    Matrix xr = Matrix.multiply(xRot, rot);
                    Matrix xyr = Matrix.multiply(yRot, xr);
                    figureRotateMatrix = xyr;

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

    private double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE, minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;      //куда??!

    public void drawFigure() {
        wireframePanel.clear();

        /* Step size along the curve */
        //n*k и m*k - это фактически разрешение
        if (needsToBeRedrawn) {
            findModelPoints();

            //короче, там при i = n*k j = m*k возникают проблемы с вычислением базисной функции, тк там надо N(k+1)!
            //|knotsJ| = Nj + Kj + 1
            //todo: сделал, но разоабраться получше!

            double maxDim = Math.max(Math.max(maxX - minX, maxY - minY), maxZ - minZ);          //nx = 2 * (x - minX)/(maxx- minx) - 1 и для других - но так не сохр пропорции; поэтому делю на одно и то же
            needsToBeRedrawn = false;
            isDrawingFirstTime = false;

            Matrix boxTranslateMatrix = new Matrix(4, 4, 1, 0, 0, -minX,
                    0, 1, 0, -minY,
                    0, 0, 1, -minZ,
                    0, 0, 0, 1);
            Matrix boxScaleMatrix = new Matrix(4, 4, 2 / maxDim, 0, 0, -(maxX - minX) / maxDim,
                    0, 2 / maxDim, 0, -(maxY - minY) / maxDim,
                    0, 0, 2 / maxDim, -(maxZ - minZ) / maxDim,
                    0, 0, 0, 1);
            boxMatrix = Matrix.multiply(boxScaleMatrix, boxTranslateMatrix);
        }

        Matrix projView = Matrix.multiply(projectionMatrix, cameraMatrix);
        Matrix projViewBox = Matrix.multiply(projView, boxMatrix);
        Matrix projViewBoxRot = Matrix.multiply(projViewBox, sceneRotateMatrix);

        Matrix resultMatrix = Matrix.multiply(projViewBoxRot, figureRotateMatrix);

        Color color = figureColor;
        Point[] uPrev = new Point[m * k + 1];   //m*k
        for (int i = 0; i <= n * k; i++) {
            Point vPrev = null;

            for (int j = 0; j <= m * k; j++) {
                Point3D p = modelPoints[i][j];
                Matrix mp = new Matrix(4, 1, p.x, p.y, p.z, 1);
                Matrix nmp = Matrix.multiply(resultMatrix, mp);
                Point3D np = new Point3D(nmp.get(0, 0), nmp.get(1, 0), nmp.get(2, 0));
                double w = nmp.get(3, 0);
                {
                    int x = (int) ((np.x / w + 1) / 2 * wireframePanel.getCanvasWidth());
                    int y = (int) ((np.y / w + 1) / 2 * wireframePanel.getCanvasHeight());

                    if (vPrev != null && i % k == 0) {
                        wireframePanel.drawLine(vPrev.x, vPrev.y, x, y, color);
                    }
                    vPrev = new Point(x, y);

                    if (uPrev[j] != null && j % k == 0) {
                        wireframePanel.drawLine(uPrev[j].x, uPrev[j].y, x, y, color);
                    }
                    uPrev[j] = new Point(x, y);
                }
            }
        }

        wireframePanel.repaint();
    }


    private void findModelPoints() {
        double incrementU = (double)(uMax - uMin) / n / k;
        double incrementV = (double)(vMax - vMin) / m / k;

        double u = uMin, v = vMin;
        for(int i = 0; i < n * k; i++)
        {
            v = vMin;
            for(int j = 0; j < m * k; j++)
            {
                Point3D Puv = calculateSplineFunction(u, v, splinePoints);

                v += incrementV;

                double x = Puv.x, y = -Puv.z, z = Puv.y;
                Matrix p = new Matrix(4, 1, x, y, z, 1);
                Matrix np = Matrix.multiply(figureRotateMatrix, p);
                double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
                modelPoints[i][j] = new Point3D(x, y, z);

                if(isDrawingFirstTime)
                    reevaluateMinMax(nx, ny, nz);
            }
            u += incrementU;
        }

        u = uMin;
        for(int i = 0; i < n * k; i++)  //<=?
        {
            Point3D Puv = calculateSplineFunctionEdgeV(u, splinePoints);

            double x = Puv.x, y = -Puv.z, z = Puv.y;
            Matrix p = new Matrix(4, 1, x, y, z, 1);
            Matrix np = Matrix.multiply(figureRotateMatrix, p);
            double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
            modelPoints[i][m*k] = new Point3D(x, y, z);

            if(isDrawingFirstTime)
                reevaluateMinMax(nx, ny, nz);

            u += incrementU;
        }

        v = vMin;
        for(int j = 0; j < m * k; j++)
        {
            Point3D Puv = calculateSplineFunctionEdgeU(v, splinePoints);

            double x = Puv.x, y = -Puv.z, z = Puv.y;
            Matrix p = new Matrix(4, 1, x, y, z, 1);
            Matrix np = Matrix.multiply(figureRotateMatrix, p);
            double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
            modelPoints[n*k][j] = new Point3D(x, y, z);

            if(isDrawingFirstTime)
                reevaluateMinMax(nx, ny, nz);

            v += incrementV;
        }

        {
            Point3D Puv = splinePoints[Ni][Nj];
            double x = Puv.x, y = -Puv.z, z = Puv.y;
            Matrix p = new Matrix(4, 1, x, y, z, 1);
            Matrix np = Matrix.multiply(figureRotateMatrix, p);
            double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
            modelPoints[n * k][m * k] = new Point3D(x, y, z);

            if(isDrawingFirstTime)
                reevaluateMinMax(nx, ny, nz);
        }
    }

    private void reevaluateMinMax(double nx, double ny, double nz) {
        if (nx < minX) minX = nx;
        if (nx > maxX) maxX = nx;
        if (ny < minY) minY = ny;
        if (ny > maxY) maxY = ny;
        if (nz < minZ) minZ = nz;
        if (nz > maxZ) maxZ = nz;
    }

    private void calculateKnots() {
        knotsI = new int[Ni + Ti + 1];
        for(int i = 0; i < knotsI.length; i++)
        {
            if(i < Ti)
                knotsI[i] = 0;
            else if (Ti <= i && i <= Ni)
                knotsI[i] = i - Ti + 1;
            else //i > n
                knotsI[i] = Ni - Ti + 2;
        }
        uMin = knotsI[0];
        uMax = knotsI[Ni + Ti];

        knotsJ = new int[Nj + Tj + 1];
        for(int j = 0; j < knotsJ.length; j++)
        {
            if(j < Tj)
                knotsJ[j] = 0;
            else if (Tj <= j && j <= Nj)
                knotsJ[j] = j - Tj + 1;
            else //j > n
                knotsJ[j] = Nj - Tj + 2;
        }
        vMin = knotsJ[0];
        vMax = knotsJ[Nj + Tj];
    }

    private Point3D calculateSplineFunctionEdgeU(double v, Point3D[][] splinePoints) {
        double Px = 0, Py = 0, Pz = 0;      //function P(u,v) which is a 3D-point
        for (int j = 0; j <= Nj; j++) {
            double bj = calculateSplineBasisFunction(j, Tj, knotsJ, v);

            Px += splinePoints[Ni][j].x * bj;
            Py += splinePoints[Ni][j].y * bj;
            Pz += splinePoints[Ni][j].z * bj;
        }
        return new Point3D(Px, Py, Pz);
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


    private Point3D calculateSplineFunction(double u, double v, Point3D[][] splinePoints)
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

    //k - index (i, j), t - Ti/Tj, u - knot points, v - coordinate (u/v)
    private double calculateSplineBasisFunction(int k, int t, int[] u, double v)    //aka Blending Function akd Ni,p
    {
        //http://paulbourke.net/geometry/spline/
        //чем больше степень Ti/Tj - тем боолее гладкая кривая
        double val;

        if(t == 1)
        {
            if ((u[k] <= v) && (v < u[k+1]))
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

    public void saveFile(File file) {  //todo
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

        */
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

    public double getZn() {
        return zn;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int load3DFile(File file) {
        needsToBeRedrawn = true;
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
            figureColor = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

            figureRotateMatrix = read3x3MatrixByRow(br);

            substrings = readLineAndSplit(br);
            Ni = Integer.parseInt(substrings[0]);
            Nj = Integer.parseInt(substrings[1]);
            Ti = Integer.parseInt(substrings[2]);
            Tj = Integer.parseInt(substrings[3]);

            if(Ni <= 0 || Nj <= 0)
                throw new IOException("Wrong Ni or Nj");
            if(2 > Ti || 2 > Tj || Ti > Ni + 1 || Tj > Nj + 1)
                throw new IOException("Wrong Ti or Tj, 2 <= Ti <= Ni + 1 ");

            splinePoints = new Point3D[Ni + 1][Nj + 1];
            for(int i = 0; i <= Ni; i++)
            {
                for(int j = 0; j <= Nj; j++)
                {
                    substrings = readLineAndSplit(br);
                    Point3D splinePoint = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));
                    splinePoints[i][j] = splinePoint;
                }
            }
            modelPoints = new Point3D[n*k + 1][m*k + 1];

            calculateKnots();
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }

        drawFigure();

        return 0;
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

    public void setCommonConstants(double sw, double sh, double zn, double zf, Color color) {
        this.sw = sw;
        this.sh = sh;
        this.zn = zn;
        this.zf = zf;
        this.projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
        this.backgroundColor = color;
        wireframePanel.setBackgroundColor(backgroundColor);

        drawFigure();
    }

    public void setConstants(int n, int m, int k, double sw, double sh, double zn, double zf, Color color) {
        this.n = n;
        this.m = m;
        this.k = k;

        modelPoints = new Point3D[n*k + 1][m*k + 1];

        this.sw = sw;
        this.sh = sh;
        this.zn = zn;
        this.zf = zf;
        this.projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
        this.backgroundColor = color;
        wireframePanel.setBackgroundColor(backgroundColor);

        needsToBeRedrawn = true;
        drawFigure();
    }
}