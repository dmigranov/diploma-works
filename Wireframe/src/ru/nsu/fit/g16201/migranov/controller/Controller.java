package ru.nsu.fit.g16201.migranov.controller;

import ru.nsu.fit.g16201.migranov.model.Figure;
import ru.nsu.fit.g16201.migranov.model.Matrix;
import ru.nsu.fit.g16201.migranov.model.Point2D;
import ru.nsu.fit.g16201.migranov.model.Point3D;
import ru.nsu.fit.g16201.migranov.view.SplinePanel;
import ru.nsu.fit.g16201.migranov.view.WireframePanel;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class Controller {
    private static Matrix splineMatrix = Matrix.multiplyByScalar(1.0/6, new Matrix(4, 4, -1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0));

    private SplinePanel splinePanel;
    private WireframePanel wireframePanel;

    //private Double[] xm, ym;
    //private double[] scale;
    private List<Double> xm, ym;
    private List<Double> scale;

    private Point3D eye = new Point3D(-10, 0, 0);
    private Point3D ref = new Point3D(10, 0, 0);
    private Point3D up = new Point3D(0, 1, 0);

    private int n;
    private int m;
    private int k;
    private double a, b, c, d;
    private double zn;
    private double zf;
    private double sw;
    private double sh;  //расстояние до ближней/дальней клиппирующей плоскости; размеры грани объёма визуализации на ближней плоскости

    private Matrix boxMatrix;

    private Color backgroundColor;
    private Matrix sceneRotateMatrix;
    private Matrix cameraMatrix;
    private Matrix projectionMatrix;

    private List<Figure> figures;
    private Figure currentFigure = null;
    private int currentFigureIndex = 0;

    private List<Point> screenSplinePoints = new ArrayList<>();  //нужен только один, при смене текущего менять; нумерация такой же, как в текущем списке точек модели
    private boolean pointIsGrabbed = false, startedMoving = false;
    private int grabbedPointIndex;

    private Integer prevX = null, prevY = null;

    private int width, height;

    private boolean isDrawingFirstTime = true;
    private int currentRotateFigure;

    private double xAllAngle = 0, yAllAngle = 0;

    private Matrix[] axes = new Matrix[4];

    public Controller(SplinePanel splinePanel, WireframePanel wireframePanel) {
        this.splinePanel = splinePanel;
        this.wireframePanel = wireframePanel;

        cameraMatrix = Matrix.getViewMatrixNew(eye, ref, up);  //c 153
        //cameraMatrix = new Matrix(4, 4, 0, 0, -1, 0, 0, 1, 0, 0, 1, 0, 0, 10, 0, 0, 0, 1);

        wireframePanel.addMouseWheelListener(e -> {
            int count = e.getWheelRotation();

            if(zn - 0.1*count < zf && zn - 0.1*count > 0) {
                zn -= 0.1 * count;
                projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
                drawFigures();
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

                    if(currentRotateFigure < 0) {
                        xAllAngle+=xAngle;
                        yAllAngle+=yAngle;
                        Matrix xRot = Matrix.getYRotateMatrix(xAngle);
                        Matrix yRot = Matrix.getZRotateMatrix(-yAngle);
                        Matrix xr = Matrix.multiply(xRot, sceneRotateMatrix);
                        Matrix xyr = Matrix.multiply(yRot, xr);
                        //Matrix cxyr = Matrix.multiply(Matrix.getViewTranslationMatrix(eye, ref, up), xyr);
                        sceneRotateMatrix = xyr;
                    }
                    else
                    {
                        Matrix rot = figures.get(currentRotateFigure).getRotateMatrix();
                        Matrix xRot = Matrix.getYRotateMatrix(xAngle);
                        Matrix yRot = Matrix.getZRotateMatrix(-yAngle);
                        Matrix xr = Matrix.multiply(xRot, rot);
                        Matrix xyr = Matrix.multiply(yRot, xr);
                        figures.get(currentRotateFigure).setRotateMatrix(xyr);

                        /*Matrix xy = Matrix.multiply(yRot, xRot);
                        //надо взять обратную к scenerotate матрицу и умножить на xy?

                        Matrix xSceneInverse = Matrix.getYRotateMatrix(-xAllAngle);
                        Matrix ySceneInverse = Matrix.getZRotateMatrix(yAllAngle);
                        Matrix sceneInverse = Matrix.multiply(xSceneInverse, ySceneInverse);

                        Matrix rotAdd = Matrix.multiply(sceneInverse, xy);
                        Matrix res = Matrix.multiply(rotAdd, sceneRotateMatrix);
                        res = Matrix.multiply(sceneRotateMatrix, res);
                        figures.get(currentRotateFigure).setRotateMatrix(res);*/
                    }
                    drawFigures();
                }
                prevX = x;
                prevY = y;
            }
        });

        wireframePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);

                Point3D p = figures.get(currentRotateFigure).getCenter();
                double x = p.x, y = p.y, z = p.z;

                int location = e.getKeyLocation();
                /*if (location == KeyEvent.KEY_LOCATION_LEFT) {
                    x -= ;
                } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
                    locationString += "right";
                }*/
            }
        });

        wireframePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                prevX = null;
                prevY = null;
            }
        });

        splinePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if(startedMoving && !pointIsGrabbed)
                    return;
                startedMoving = true;

                int x = e.getX(), y = e.getY();

                if(!pointIsGrabbed) {
                    if (!(x < 0 || y < 0 || x >= splinePanel.getWidth() || y >= splinePanel.getHeight()) && splinePanel.getRGB(x, y) == splinePanel.getSplinePointColor()) {
                        int radius = splinePanel.getSplinePointRadius();
                        Point grabbedPoint = null;
                        for (Point p : screenSplinePoints) {
                            if (Math.abs(p.x - x) <= radius && Math.abs(p.y - y) <= radius) {
                                pointIsGrabbed = true;
                                grabbedPoint = p;
                                break;
                            }
                        }
                        if (grabbedPoint == null)
                            return;
                        grabbedPointIndex = screenSplinePoints.indexOf(grabbedPoint);
                    }
                }
                if(pointIsGrabbed)
                {
                    if(x < 0)
                        x = 0;
                    else if(x >= width)
                        x = width -1 ;
                    if(y < 0)
                        y = 0;
                    else if(y >= height)
                        y = height -1 ;

                    //я не трогаю mx и my, чтобы не менять масштаб
                    Point2D movedPoint = currentFigure.getSplinePoints().get(grabbedPointIndex);
                    Point2D newCoords = getXY(x, y);
                    movedPoint.x = newCoords.x;
                    movedPoint.y = newCoords.y;
                    drawSplineLine();
                    drawFigures();
                }
            }
        });

        splinePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                startedMoving = false;
                pointIsGrabbed = false;
            }
        });
    }

    public void drawFigures() {
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE, minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE, minZ = Double.MAX_VALUE, maxZ = -Double.MAX_VALUE;      //куда??!

        wireframePanel.clear();
        for (Figure figure : figures) {
            List<Point2D> splinePoints = figure.getSplinePoints();
            if(splinePoints.size() < 4)
                continue;
            double length = calculateLength(splinePoints), tempLength = 0;
            figure.setLength(length);

            double[] u = new double[n * k + 1];
            Point2D[] Gu = new Point2D[n * k + 1];
            u[0] = a * length;
            for (int i = 1; i < n * k; i++)  //можно и до n+1, но для надежности снизу
            {
                u[i] = u[i - 1] + (b - a) * length / n / k;
            }
            u[n * k] = b * length;
            int uIndex = 0;

            Double xPrev = null, yPrev = null;
            for (int i = 1; i < splinePoints.size() - 2; i++) {
                Matrix Gx = new Matrix(4, 1, splinePoints.get(i - 1).x, splinePoints.get(i).x, splinePoints.get(i + 1).x, splinePoints.get(i + 2).x);
                Matrix Gy = new Matrix(4, 1, splinePoints.get(i - 1).y, splinePoints.get(i).y, splinePoints.get(i + 1).y, splinePoints.get(i + 2).y);
                for (double t = 0; t <= 1; t += 0.005)  //??? я думаю, все таким лучше чтобы было одинаков в подсчете длины и тут, чтобы не было неточности
                {
                    Matrix T = new Matrix(1, 4, t * t * t, t * t, t, 1);
                    Matrix TM = Matrix.multiply(T, splineMatrix);
                    Matrix X = Matrix.multiply(TM, Gx);
                    Matrix Y = Matrix.multiply(TM, Gy);
                    double x = X.get(0, 0), y = Y.get(0, 0);

                    if (xPrev != null)
                        tempLength += Math.sqrt(Math.pow(xPrev - x, 2) + Math.pow(yPrev - y, 2));

                    try {
                        if (tempLength >= u[uIndex]) {
                            Gu[uIndex] = new Point2D(x, y);
                            uIndex++;
                        }
                    }catch(IndexOutOfBoundsException e)
                    {}
                    xPrev = x;
                    yPrev = y;
                }
            }

            Point3D[][] modelPoints = figure.getModelPoints();
            Matrix translateMatrix = Matrix.getTranslationMatrix(figure.getCenter());
            //Matrix rtm = Matrix.multiply(figure.getRotateMatrix(), translateMatrix);  //?
            Matrix rtm = Matrix.multiply(translateMatrix, figure.getRotateMatrix());
            for (int i = 0; i < Gu.length; i++) {
                Point2D gu = Gu[i];

                for (int j = 0; j <= m * k; j++) {
                    double v = (d - c) * j / m / k + c;
                    double x = gu.y * Math.cos(v);
                    double y = gu.y * Math.sin(v);
                    double z = gu.x;

                    Matrix p = new Matrix(4, 1, x, y, z, 1);

                    Matrix np = Matrix.multiply(rtm, p);                        //на самом деле произведение r и t имеет простой вид - можно упростить
                    double nx = np.get(0, 0), ny = np.get(1, 0), nz = np.get(2, 0);
                    //modelPoints[i][j] = new Point3D(nx, ny, nz);
                    modelPoints[i][j] = new Point3D(x, y, z);

                    if (nx < minX) minX = nx;
                    if (nx > maxX) maxX = nx;
                    if (ny < minY) minY = ny;
                    if (ny > maxY) maxY = ny;
                    if (nz < minZ) minZ = nz;
                    if (nz > maxZ) maxZ = nz;
                }
            }
        }

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

        for (Figure figure : figures)
        {
            Matrix translateMatrix = Matrix.getTranslationMatrix(figure.getCenter());
            Matrix rtm = Matrix.multiply(translateMatrix, figure.getRotateMatrix());

            Matrix resultMatrix = Matrix.multiply(projViewBoxRot, rtm);

            if(figure.getSplinePoints().size() < 4)
                continue;

            Point3D[][] modelPoints = figure.getModelPoints();
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

                    //РИСУЕМ ВСЁ
                    //if(np.z/w >= 0 && np.z/w <= 1)
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
            Matrix[] axes = figure.getAxes();
            Matrix axeCenter = Matrix.multiply(resultMatrix, axes[0]);
            axeCenter = Matrix.multiplyByScalar(1/axeCenter.get(3, 0), axeCenter);
            int cx = (int)((axeCenter.get(0, 0) + 1)/2*wireframePanel.getCanvasWidth());
            int cy = (int)((axeCenter.get(1, 0) + 1)/2*wireframePanel.getCanvasHeight());
            int axeColor = 0x00FF0000;
            for(int i = 1; i <=3; i++)
            {
                Matrix axePoint = Matrix.multiply(resultMatrix, axes[i]);
                axePoint = Matrix.multiplyByScalar(1/axePoint.get(3, 0), axePoint);

                int px = (int)((axePoint.get(0, 0) + 1)/2*wireframePanel.getCanvasWidth());
                int py = (int)((axePoint.get(1, 0) + 1)/2*wireframePanel.getCanvasHeight());

                wireframePanel.drawLine(cx, cy, px, py, new Color(axeColor));
                axeColor>>=8;
            }

        }

        Matrix axeCenter = Matrix.multiply(projViewBoxRot, axes[0]);
        axeCenter = Matrix.multiplyByScalar(1/axeCenter.get(3, 0), axeCenter);
        int cx = (int)((axeCenter.get(0, 0) + 1)/2*wireframePanel.getCanvasWidth());
        int cy = (int)((axeCenter.get(1, 0) + 1)/2*wireframePanel.getCanvasHeight());
        int axeColor = 0x00FF0000;
        for(int i = 1; i <=3; i++)
        {
            Matrix axePoint = Matrix.multiply(projViewBoxRot, axes[i]);
            axePoint = Matrix.multiplyByScalar(1/axePoint.get(3, 0), axePoint);

            int px = (int)((axePoint.get(0, 0) + 1)/2*wireframePanel.getCanvasWidth());
            int py = (int)((axePoint.get(1, 0) + 1)/2*wireframePanel.getCanvasHeight());

            wireframePanel.drawLine(cx, cy, px, py, new Color(axeColor));
            axeColor>>=8;
        }

        wireframePanel.repaint();
    }

    //область определения сплайна (чтобы знать как масштабировать)
    private void calculateSplineArea() {
        if(xm.get(currentFigureIndex) != null)
            return;

        double xMin = Double.MAX_VALUE, xMax = -Double.MAX_VALUE, yMin = Double.MAX_VALUE, yMax = -Double.MAX_VALUE;

        Figure figure = figures.get(currentFigureIndex);
        List<Point2D> splinePoints = figure.getSplinePoints();

        for(Point2D p : splinePoints)
        {
            xMax = p.x > xMax ? p.x : xMax;
            xMin = p.x < xMin ? p.x : xMin;
            yMax = p.y > yMax ? p.y : yMax;
            yMin = p.y < yMin ? p.y : yMin;
        }

        xm.set(currentFigureIndex, Math.max(Math.abs(xMax), Math.abs(xMin)));
        ym.set(currentFigureIndex, Math.max(Math.abs(yMax), Math.abs(yMin)));
    }

    private void drawSplineLine() {

        width = splinePanel.getPreferredSize().width;
        height = splinePanel.getPreferredSize().height;

        splinePanel.clear();
        //T - вектор строка t^3 t^2 t 1, t [0,1]
        Figure figure = figures.get(currentFigureIndex);
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
        splinePanel.repaint();
    }

    private double calculateLength(List<Point2D> splinePoints)
    {
        Double xPrev = null, yPrev = null;
        double length = 0;
        for(int i = 1; i < splinePoints.size() - 2; i++)
        {
            Matrix Gx = new Matrix(4, 1, splinePoints.get(i - 1).x, splinePoints.get(i).x, splinePoints.get(i + 1).x, splinePoints.get(i + 2).x);
            Matrix Gy = new Matrix(4, 1, splinePoints.get(i - 1).y, splinePoints.get(i).y, splinePoints.get(i + 1).y, splinePoints.get(i + 2).y);
            for(double t = 0; t <= 1; t+=0.01)
            {
                Matrix T = new Matrix(1, 4, t*t*t, t*t, t, 1);
                Matrix TM = Matrix.multiply(T, splineMatrix);

                Matrix X = Matrix.multiply(TM, Gx);
                Matrix Y = Matrix.multiply(TM, Gy);

                double x = X.get(0, 0), y = Y.get(0, 0);

                if(xPrev != null) {

                    length += Math.sqrt(Math.pow(xPrev - x, 2) + Math.pow(yPrev - y, 2));
                }
                xPrev = x;
                yPrev = y;
            }
        }
        return length;
    }

    private void drawSplinePoints(List<Point2D> splinePoints) {
        screenSplinePoints.clear();
        for(Point2D p : splinePoints)
        {
            Point uv = getUV(p);
            splinePanel.drawSplinePoint(uv.x, uv.y);
            screenSplinePoints.add(uv);   //может куда-то ещё ложить номер, чтобы легко найти
        }
    }

    private Point getUV(double x, double y) {
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
    }

    private Point2D getXY(int u, int v)
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
    }

    private Point getUV(Point2D p) {
        return getUV(p.x, p.y);
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

    private String[] readLineAndSplit(BufferedReader br) throws IOException
    {
        String line;
        line = br.readLine();
        line = line.substring(0, line.indexOf('/') != -1 ? line.indexOf('/') : line.length());
        return line.split("\\s+");
    }

    public void addSplinePoint(int index) {
        currentFigure.getSplinePoints().add(index, new Point2D(0, 0));
        drawSplineLine();
    }

    public void deleteSplinePoint(int index) {
        if(index >= currentFigure.getSplinePoints().size() || index < 0)
            return;
        currentFigure.getSplinePoints().remove(index);
        drawSplineLine();
    }

    public int getSplinePointsCount() {
        return currentFigure.getSplinePoints().size();
    }

    public void saveFile(File file) {
        try(PrintWriter pw = new PrintWriter(file)) {
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

                List<Point2D> splinePoints = figure.getSplinePoints();
                pw.println(splinePoints.size());

                for(Point2D p : splinePoints)
                {
                    pw.println(p.x + " " + p.y);
                }
            }
        }
        catch(IOException e)
        {
        }
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

    public double getA() {
        return a;
    }

    public double getB() {
        return b;
    }

    public double getC() {
        return c;
    }

    public double getD() {
        return d;
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

    public void setConstants(int n, int m, int k, double a, double b, double c, double d, double sw, double sh, double zn, double zf, Color color) {
        this.n = n;
        this.m = m;
        this.k = k;

        for(Figure figure : figures)
            figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);

        this.a = a;
        this.b = b;
        this.c = c;
        if(d >= 6.28)
            d = 2*Math.PI;
        this.d = d;

        this.sw = sw;
        this.sh = sh;
        this.zn = zn;
        this.zf = zf;
        this.projectionMatrix = Matrix.getProjectionMatrix(sw, sh, zf, zn);
        this.backgroundColor = color;
        wireframePanel.setBackgroundColor(backgroundColor);

        drawSplineLine();
        drawFigures();
    }

    public void changeScale(double ds) {
        if(scale.get(currentFigureIndex) + ds > 0)
            scale.set(currentFigureIndex, scale.get(currentFigureIndex) + ds);
        drawSplineLine();
    }

    public void setCurrentFigure(int index) {
        //масштаб при изначальной загрузке такой, чтобы всё было красиво вписано (он не одинаковый для разных вкладок!)
        currentFigureIndex = index;
        currentFigure = figures.get(index);
        calculateSplineArea();
        drawSplineLine();
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

    public void setCurrentRotateFigure(int i) {
        currentRotateFigure = i;
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

    public void deleteFigure(int i) {
        if(figures.size() > 0) {
            figures.remove(i);
            xm.remove(i);
            ym.remove(i);
            scale.remove(i);
            isDrawingFirstTime = true;
            drawFigures();
        }
    }

    public void addFigure() {
        Random random = new Random();
        if(figures.size() == 0) {
            Figure figure = new Figure(new Point3D(0, 0, 0), new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)), new Matrix(4,4, 1, 0, 0, 0, 0, 1,0,0,0,0,1,0,0,0,0,1), new ArrayList<Point2D>());
            figures.add(figure);
            figure.setModelPoints(new Point3D[n * k + 1][m * k + 1]);
            figure.getSplinePoints().add(new Point2D(1, 1));
            figure.getSplinePoints().add(new Point2D(1, -1));
            figure.getSplinePoints().add(new Point2D(-1, 1));
            figure.getSplinePoints().add(new Point2D(-1, -1));
        }
        else
        {
            Figure oldFigure = figures.get(figures.size() - 1);
            Point3D oldFigureCenter = oldFigure.getCenter();

            List<Point2D> newSplinePoints = new ArrayList<>();
            for(Point2D p : oldFigure.getSplinePoints())
            {
                Point2D n = new Point2D(p.x, p.y);
                newSplinePoints.add(p);
            }
            Figure figure = new Figure(new Point3D(oldFigureCenter.x + oldFigureCenter.x, oldFigureCenter.y, oldFigureCenter.z), new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)), new Matrix(4,4, 1, 0, 0, 0, 0, 1,0,0,0,0,1,0,0,0,0,1), newSplinePoints);
            figures.add(figure);
            figure.setModelPoints(new Point3D[n * k + 1][m * k + 1]);
        }

        xm.add(null);
        ym.add(null);
        scale.add(1.1);

        isDrawingFirstTime = true;

        drawFigures();
    }

    public Color getCurrentColor() {
        return figures.get(currentFigureIndex).getColor();
    }

    public void setAB(double a, double b) {
        this.a = a;
        this.b = b;

        drawSplineLine();
        drawFigures();
    }

    public void setABColorCenter(double a, double b, Color color, Point3D center) {
        this.a = a;
        this.b = b;

        figures.get(currentFigureIndex).setColor(color);
        figures.get(currentFigureIndex).setCenter(center);

        drawSplineLine();
        drawFigures();
    }

    public Point3D getCurrentCenter() {
        return figures.get(currentFigureIndex).getCenter();
    }

    public int load3DFile(File file) {
        isDrawingFirstTime = true;

        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] substrings;

            axes[0] = new Matrix(4, 1, 0, 0, 0, 1);
            axes[1] = new Matrix(4, 1, 100, 0, 0, 1);
            axes[2] = new Matrix(4, 1, 0, 100, 0, 1);
            axes[3] = new Matrix(4, 1, 0, 0, 100, 1);

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
            int Ni = Integer.parseInt(substrings[0]), Nj = Integer.parseInt(substrings[1]);
            if(Ni < 4 || Nj < 4)
                throw new IOException("Not enough spline points");
            List<Point2D> splinePoints = new ArrayList<>();
            for(int j = 0; j < splinePointCount; j++)
            {
                substrings = readLineAndSplit(br);
                Point2D splinePoint = new Point2D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]));
                splinePoints.add(splinePoint);
            }
            Figure figure  = new Figure(center, color, rotateMatrix, splinePoints);
            figures.add(figure);
            figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }

        return 0;
    }

    public int loadFile(File file) {
        currentRotateFigure = -1;
        isDrawingFirstTime = true;
        int figureCount;
        currentFigureIndex = 0;
        try(BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            String[] substrings;

            substrings = readLineAndSplit(br);
            n = Integer.parseInt(substrings[0]);
            m = Integer.parseInt(substrings[1]);
            k = Integer.parseInt(substrings[2]);
            if(m <= 0 || n <= 0 || k <= 0)
                throw new IOException("Wrong m, n, or k");
            a = Double.parseDouble(substrings[3]);
            b = Double.parseDouble(substrings[4]);
            if(!(b > a && a >= 0 && 1 >= b))
                throw new IOException("Wrong a or b");
            c = Double.parseDouble(substrings[5]);
            d = Double.parseDouble(substrings[6]);
            if(!(d > c && c >= 0 && 2*Math.PI >= d))
                throw new IOException("Wrong c or d");
            if(d >= 6.28)
                d = 2*Math.PI;

            axes[0] = new Matrix(4, 1, 0, 0, 0, 1);
            axes[1] = new Matrix(4, 1, 100, 0, 0, 1);
            axes[2] = new Matrix(4, 1, 0, 100, 0, 1);
            axes[3] = new Matrix(4, 1, 0, 0, 100, 1);

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
            figureCount = Integer.parseInt(substrings[0]);
            figures = new ArrayList<>();

            for (int i = 0; i < figureCount; i++)
            {
                substrings = readLineAndSplit(br);
                Color color = new Color(Integer.parseInt(substrings[0]), Integer.parseInt(substrings[1]), Integer.parseInt(substrings[2]));

                substrings = readLineAndSplit(br);
                Point3D center = new Point3D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]), Double.parseDouble(substrings[2]));

                Matrix rotateMatrix = read3x3MatrixByRow(br);

                substrings = readLineAndSplit(br);
                int splinePointCount = Integer.parseInt(substrings[0]);
                if(splinePointCount < 4)
                    throw new IOException("Not enough spline points");
                List<Point2D> splinePoints = new ArrayList<>();
                for(int j = 0; j < splinePointCount; j++)
                {
                    substrings = readLineAndSplit(br);
                    Point2D splinePoint = new Point2D(Double.parseDouble(substrings[0]), Double.parseDouble(substrings[1]));
                    splinePoints.add(splinePoint);
                }
                Figure figure  = new Figure(center, color, rotateMatrix, splinePoints);
                figures.add(figure);
                figure.setModelPoints(new Point3D[n*k + 1][m*k + 1]);
            }
        }
        catch (IOException | ArrayIndexOutOfBoundsException | IllegalArgumentException e)
        {
            return -1;
        }

        xm = new ArrayList<>(figureCount);
        ym = new ArrayList<>(figureCount);
        scale = new ArrayList<>(figureCount);

        for (int i = 0; i < figureCount; i++) {
            scale.add(1.1);
            xm.add(null);
            ym.add(null);
        }

        currentFigure = figures.get(0);
        calculateSplineArea();
        drawSplineLine();
        drawFigures();

        return figureCount;
    }
}