package ru.nsu.fit.g16201.migranov.presenter;

import ru.nsu.fit.g16201.migranov.model.*;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
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

    private Integer prevX = null, prevY = null;

    private int width, height;

    private boolean needsToBeRedrawn = true, isDrawingFirstTime = true;

    //фигура
    private Color figureColor;
    private Matrix figureRotateMatrix;
    private Point3D[][] splinePoints;
    private Point3D[][] modelPoints;
    private List<Geodesic> geodesics;

    private SplineCalculator splineCalculator;
    private GeodesicsCalculator geodesicsCalculator;

    //private ManifoldFunction spongeFunction = new SpongeFunction(1);
    //private ManifoldFunction spongeFunction = new BumpFunction(10);
    private ManifoldFunction spongeFunction = new FlammParaboloidFunction();

    public Presenter(WireframePanel wireframePanel) {
        this.wireframePanel = wireframePanel;

        cameraMatrix = Matrix.getViewMatrixNew(eye, ref, up);  //c 153

        wireframePanel.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();

            if(e.isControlDown())
            {
                double dz = -count * 0.5;
                Point3D forward = Point3D.add(ref, Point3D.getNegative(eye));
                forward = forward.normalize();
                //ref = Point3D.add(ref, Point3D.multiplyByScalar(dz, forward));
                Point3D oldEye = eye;
                eye = Point3D.add(eye, Point3D.multiplyByScalar(dz, forward));
                if(eye.x >= -1)
                {
                    eye = oldEye;
                    return;
                }
                cameraMatrix = Matrix.getViewMatrixNew(eye, ref, up);  //матрица получается аналогичная
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
                else if (key == KeyEvent.VK_Q)
                {
                    var g1 = geodesics.get(0);
                    var g2 = geodesics.get(1);
                    g1.setuStart(g1.getuStart() - 0.02);
                    g2.setuStart(g2.getuStart() + 0.02);
                    geodesicsCalculator.calculateGeodesic(g1);
                    geodesicsCalculator.calculateGeodesic(g2);
                }

                else if (key == KeyEvent.VK_E)
                {
                    var g1 = geodesics.get(0);
                    var g2 = geodesics.get(1);
                    g1.setuStart(g1.getuStart() + 0.02);
                    g2.setuStart(g2.getuStart() - 0.02);
                    geodesicsCalculator.calculateGeodesic(g1);
                    geodesicsCalculator.calculateGeodesic(g2);
                }

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
            for(var geodesic : geodesics )
                geodesicsCalculator.calculateGeodesic(geodesic);

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

        //drawGeodesic
        {
            for(var geodesic : geodesics) {
                Point prev = null;
                Color geoColor = geodesic.getColor();
                for (Point3D p : geodesic.getPoints()) {
                    Matrix mp = new Matrix(4, 1, p.x, -p.z, p.y, 1);
                    Matrix nmp = Matrix.multiply(resultMatrix, mp);
                    Point3D np = new Point3D(nmp.get(0, 0), nmp.get(1, 0), nmp.get(2, 0));
                    double w = nmp.get(3, 0);
                    int x = (int) ((np.x / w + 1) / 2 * wireframePanel.getCanvasWidth());
                    int y = (int) ((np.y / w + 1) / 2 * wireframePanel.getCanvasHeight());
                    if (prev != null)
                        wireframePanel.drawLine(prev.x, prev.y, x, y, geoColor, 3);
                    prev = new Point(x, y);
                }
            }
        }

        wireframePanel.repaint();
    }




    private void findModelPoints() {
        boolean isSponge = true;

        double uMin, uMax, vMin, vMax;


        if(isSponge)
        {
            uMin = spongeFunction.getUMin();
            uMax = spongeFunction.getUMax();
            vMin = spongeFunction.getVMin();
            vMax = spongeFunction.getVMax();
        }
        else {
            uMin = splineCalculator.getUMin();
            uMax = splineCalculator.getUMax();
            vMin = splineCalculator.getVMin();
            vMax = splineCalculator.getVMax();
        }
        double incrementU = (uMax - uMin) / n / k;
        double incrementV = (vMax - vMin) / m / k;

        double u = uMin, v;

        for(int i = 0; i <= n * k; i++)
        {
            v = vMin;
            for(int j = 0; j <= m * k; j++)
            {
                Point3D Puv;

                if(isSponge) {
                    double[] vals = spongeFunction.apply(u, v);
                    Puv = new Point3D(vals[0], vals[1], vals[2]);
                }
                else
                    Puv = splineCalculator.calculateSplineFunction(u, v, i == n * k, j == m * k);   //todo: заменитт на SplineFunction
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
    }

    private void reevaluateMinMax(double nx, double ny, double nz) {
        if (nx < minX) minX = nx;
        if (nx > maxX) maxX = nx;
        if (ny < minY) minY = ny;
        if (ny > maxY) maxY = ny;
        if (nz < minZ) minZ = nz;
        if (nz > maxZ) maxZ = nz;
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
            int Ni = Integer.parseInt(substrings[0]);
            int Nj = Integer.parseInt(substrings[1]);
            int Ti = Integer.parseInt(substrings[2]);
            int Tj = Integer.parseInt(substrings[3]);

            if(Ni <= 0 || Nj <= 0)
                throw new IOException("Wrong Ni or Nj");
            if(1 > Ti || 1 > Tj || Ti > Ni || Tj > Nj)
                throw new IOException("Wrong Ti or Tj, 1 <= Ti <= Ni ");

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

            splineCalculator = new SplineCalculator(Ni, Nj, Ti, Tj, splinePoints);
            geodesicsCalculator = new GeodesicsCalculator(spongeFunction);
            modelPoints = new Point3D[n*k + 1][m*k + 1];
            geodesics = new ArrayList<>();
            /*
            geodesics.add(new Geodesic(0.1, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.RED));
            geodesics.add(new Geodesic(0.2, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.GREEN));
            geodesics.add(new Geodesic(0.3, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.BLUE));
            geodesics.add(new Geodesic(0.4, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.RED));
            geodesics.add(new Geodesic(0.5, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.GREEN));
            geodesics.add(new Geodesic(0.6, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.BLUE));
            geodesics.add(new Geodesic(0.7, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.RED));
            geodesics.add(new Geodesic(0.8, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.GREEN));
            geodesics.add(new Geodesic(0.9, spongeFunction.getVMin() + 0.05, 0, 0.2, Color.BLUE));
            */
            geodesics.add(new Geodesic(0.1, spongeFunction.getVMax() - 0.5, 0.006, -0.5, Color.YELLOW));
            geodesics.add(new Geodesic(0.15, spongeFunction.getVMax() - 0.5, 0.006, -0.5, Color.GREEN));
            geodesics.add(new Geodesic(0.2, spongeFunction.getVMax() - 0.5, 0.006, -0.5, Color.RED));
            geodesics.add(new Geodesic(0.25, spongeFunction.getVMax() - 0.5, 0.006, -0.5, Color.BLUE));

            //geodesics.add(new Geodesic(0.1, spongeFunction.getVMax() - 0.6, -0.6, -0.1, Color.GREEN));
            //geodesics.add(new Geodesic(0.1, spongeFunction.getVMax() - 0.7, -0.6, -0.1, Color.BLUE));
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

    public void setConstants(int n, int m, int k, double sw, double sh, double zn, double zf, Color backgroundColor, Color figureColor, int ti, int tj) {
        this.n = n;
        this.m = m;
        this.k = k;

        modelPoints = new Point3D[n*k + 1][m*k + 1];

        this.sw = sw;
        this.sh = sh;
        this.zn = zn;
        this.zf = zf;
        this.projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
        this.backgroundColor = backgroundColor;
        this.figureColor = figureColor;
        wireframePanel.setBackgroundColor(backgroundColor);

        splineCalculator.setDegrees(ti, tj);

        needsToBeRedrawn = true;
        drawFigure();
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

    public double getSh() {
        return sh;
    }

    public double getZn() {
        return zn;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getFigureColor() {
        return figureColor;
    }

    public int getTi() {
        return splineCalculator.getTi();
    }

    public int getTj() {
        return splineCalculator.getTj();
    }

    public int getNi() {
        return splineCalculator.getNi();
    }

    public int getNj() {
        return splineCalculator.getNj();
    }
}