package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;
import java.util.Arrays;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Matrix {
    private double[] data;

    private int rows, cols;
    public Matrix(int rows, int cols)
    {
        this.rows = rows;
        this.cols = cols;
        data = new double[rows * cols];
    }

    public Matrix(int rows, int cols, double ... elements)
    {
        if(elements.length != rows * cols)
            throw new IllegalArgumentException();
        this.rows = rows;
        this.cols = cols;

        data = elements; //на самом деле нехорошо - а вдруг изменят значение (теоретически можно подать прям массив)?

        //data = new double[rows * cols];
        //for(int i = 0; )

    }

    public static Matrix getTranslationMatrix(Point3D where) {
        return new Matrix(4, 4,
                1, 0, 0, where.x,
                0, 1, 0, where.y,
                0, 0, 1, where.z,
                0, 0, 0, 1);
    }


    //в левосторонней ск вроде верно
    public static Matrix getViewMatrix(Point3D eye, Point3D ref, Point3D up) {
        Point3D z = new Point3D(ref.x - eye.x, ref.y - eye.y, ref.z - eye.z).normalize();

        Point3D x = Point3D.getVectorProduct(up, z).normalize();
        Point3D y = Point3D.getVectorProduct(z, x);

        return new Matrix(4, 4,
                x.x, x.y, x.z, -(x.x*eye.x + x.y*eye.y + x.z* eye.z),
                y.x, y.y, y.z, -(y.x*eye.x + y.y*eye.y + y.z* eye.z),
                z.x, z.y, z.z, -(z.x*eye.x + z.y*eye.y + z.z* eye.z),
                0, 0, 0, 1
        );
    }


    private static Matrix transpose(Matrix a) {
        Matrix b = new Matrix(a.cols, a.rows);

        double[] adata = a.data;
        double[] bdata = b.data;
        for(int i = 0; i < a.rows; i++)
            for(int j = 0; j < a.cols; j++)
            {
                bdata[j * a.rows + i] = adata[i * a.cols + j];
            }
        return b;
    }

    //WARNING! работает только для матриц 4x4 с вырожденными 4-ми строкой и столбцом!
    /*private static Matrix getInverse(Matrix a)
    {
        Matrix b = new Matrix(4, 4);
        b.set(3, 3, 1);
    }*/

    private void set(int row, int col, double val) {
        data[row * cols + col] = val;
    }

    //zfar znear
    public static Matrix getProjectionMatrix(double sw, double sh, double zf, double zn) {
        return new Matrix(4, 4,
                2/sw*zn, 0, 0, 0,
                0, 2/sh*zn, 0, 0,
                0, 0, zf/(zf - zn), -zn*zf/(zf - zn),
                0, 0, 1, 0);      //проекц

    }

    public static Matrix getXRotateMatrix(double angle) {
        return new Matrix(4, 4,
                1, 0, 0, 0,
                0, cos(angle), -sin(angle), 0,
                0, sin(angle), cos(angle), 0,
                0, 0, 0, 1);
    }

    public static Matrix getRotateMatrix2D(double angle) {
        return new Matrix(43, 3,
                cos(angle), -sin(angle), 0,
                sin(angle), cos(angle), 0,
                0, 0, 1);
    }

    public static Matrix getRotateMatrix(double angle, Point3D axe) {
        double x = axe.x, y= axe.y, z = axe.z;
        return new Matrix(4, 4,
                cos(angle) + (1 - cos(angle))*x*x, (1 - cos(angle))*x*y - z * sin(angle), (1 - cos(angle))*x*z + y * sin(angle), 0,
                (1 - cos(angle))*x*y + z * sin(angle), cos(angle) + (1 - cos(angle))*y*y, (1 - cos(angle))*y*z - x * sin(angle), 0,
                (1 - cos(angle))*x*z - y * sin(angle), (1 - cos(angle))*z*y + x * sin(angle), cos(angle) + (1 - cos(angle)) *z*z, 0,
                0, 0, 0, 1);
    }

    public static Matrix getYRotateMatrix(double angle) {
        return new Matrix(4, 4,
                cos(angle), 0, sin(angle), 0,
                0, 1, 0, 0,
                -sin(angle), 0, cos(angle), 0,
                0, 0, 0, 1);
    }

    public static Matrix getZRotateMatrix(double angle) {
        return new Matrix(4, 4,
                cos(angle), -sin(angle), 0, 0,
                sin(angle), cos(angle), 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1);
    }

    public void setRow(int rowNumber, double[] row)
    {
        if(row.length != cols || rowNumber >= rows)
            throw new IllegalArgumentException();
        System.arraycopy(row, 0, data, rowNumber*cols, row.length);
    }


    public static Matrix add(Matrix M1, Matrix M2)
    {
        double[] m1d = M1.data;
        double[] m2d = M2.data;
        if(m1d.length != m2d.length || M1.cols != M2.cols)
            throw new IllegalArgumentException();

        Matrix N = new Matrix(M1.rows, M1.cols);

        double[] nd = N.data;
        for(int i = 0; i < m1d.length; i++)
        {
            nd[i] = m1d[i] + m2d[i];
        }
        return N;
    }

    public static Matrix multiply(Matrix M1, Matrix M2)
    {
        Matrix N = new Matrix(M1.rows, M2.cols);
        double[] m1d = M1.data;
        double[] m2d = M2.data;
        double[] nd = N.data;

        int l = M1.rows;
        int m = M1.cols; //=m2.rows
        int n = M2.cols;

        for(int i = 0; i < l; i++)
            for(int j = 0; j < n;j++)    //m2.rows = m1.cols
                for(int k = 0; k < m; k++)
                    nd[i * n + j] += m1d[i * m + k] * m2d[k * n + j];

        return N;
    }

    public static Matrix multiplyByScalar(double a, Matrix m)
    {
        Matrix n = new Matrix(m.rows, m.cols);
        for(int i = 0; i < m.rows*m.cols; i++)
        {
            n.data[i] = m.data[i] * a;
        }
        return n;
    }


    public double get(int row, int col) {
        return data[row * cols + col];
    }

    public static Matrix getVector4(Point3D p)
    {
        return new Matrix(4, 1, p.x, p.y, p.z, 1);
    }

    public Point3D getPoint() {
        return new Point3D(data[0], data[1], data[2]);
    }

    public Matrix get3x3Submatrix()
    {
        Matrix r = new Matrix(3, 3);
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++)
            {
                r.set(i, j, get(i, j)) ;
            }
        return r;
    }
    public Matrix get4x4MatrixWithout4RowCol()
    {
        Matrix r = new Matrix(4, 4);
        for(int i = 0; i < 4; i++)
            for(int j = 0; j < 4; j++)
            {
                if(i != 3 && j != 3)
                    r.set(i, j, get(i, j)) ;
                else
                    r.set(i, j, 0) ;
            }
        return r;
    }

    public Point3D applyMatrix(Point3D point)
    {
        Matrix pointMatrix = new Matrix(4, 1, point.x, point.y, point.z, 1);
        Matrix resultPointMatrix = Matrix.multiply(this, pointMatrix);
        //double w = resultPointMatrix.get(3, 0);
        //resultPointMatrix = Matrix.multiplyByScalar(w, resultPointMatrix)
        return resultPointMatrix.getPoint();
    }
}
