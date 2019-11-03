package ru.nsu.fit.g16201.migranov.model;

import static java.lang.Math.abs;

public class SplineCalculator {
    private int Ni, Nj, Ti, Tj;

    private Point3D[][] splinePoints;

    private double[] knotsI, knotsJ;

    private double uMax, vMax, uMin, vMin;

    public SplineCalculator(int Ni, int Nj, int Ti, int Tj, Point3D[][] splinePoints) {
        this.Ni = Ni;
        this.Nj = Nj;
        this.Ti = Ti;
        this.Tj = Tj;
        this.splinePoints = splinePoints;

        calculateKnots();
    }

    private void calculateKnots() {
        knotsI = new double[Ni + Ti + 1];
        for (int i = 0; i < knotsI.length; i++) {
            if (i < Ti)
                knotsI[i] = 0;
            else if (Ti <= i && i <= Ni)
                knotsI[i] = i - Ti + 1;
            else //i > n
                knotsI[i] = Ni - Ti + 2;
        }
        uMin = knotsI[0];
        uMax = knotsI[Ni + Ti];

        knotsJ = new double[Nj + Tj + 1];
        for (int j = 0; j < knotsJ.length; j++) {
            if (j < Tj)
                knotsJ[j] = 0;
            else if (Tj <= j && j <= Nj)
                knotsJ[j] = j - Tj + 1;
            else //j > n
                knotsJ[j] = Nj - Tj + 2;
        }
        vMin = knotsJ[0];
        vMax = knotsJ[Nj + Tj];
    }

    //k - index (i, j), t - Ti/Tj, u - knot points, v - coordinate (u/v)
    private double calculateSplineBasisFunction(int k, int t, double[] u, double v)    //aka Blending Function akd Ni,p
    {
        //http://paulbourke.net/geometry/spline/
        //чем больше степень Ti/Tj - тем боолее гладкая кривая
        double val;

        if (t == 1) {
            if ((u[k] <= v) && (v < u[k + 1]))
                val = 1;
            else
                val = 0;
        } else {
            if ((u[k + t - 1] == u[k]) && (u[k + t] == u[k + 1]))
                val = 0;
            else if (u[k + t - 1] == u[k])
                val = (u[k + t] - v) / (u[k + t] - u[k + 1]) * calculateSplineBasisFunction(k + 1, t - 1, u, v);
            else if (u[k + t] == u[k + 1])
                val = (v - u[k]) / (u[k + t - 1] - u[k]) * calculateSplineBasisFunction(k, t - 1, u, v);
            else
                val = (v - u[k]) / (u[k + t - 1] - u[k]) * calculateSplineBasisFunction(k, t - 1, u, v) +
                        (u[k + t] - v) / (u[k + t] - u[k + 1]) * calculateSplineBasisFunction(k + 1, t - 1, u, v);
        }

        return val;
    }

    public double getUMin() {
        return uMin;
    }

    public double getUMax() {
        return uMax;
    }

    public double getVMin() {
        return vMin;
    }

    public double getVMax() {
        return vMax;
    }

    public void setDegrees(int Ti, int Tj)
    {
        this.Ti = Ti;
        this.Tj = Tj;
        //todo: проверка (перенести из view?)
        calculateKnots();
    }

    public int getTi() {
        return Ti;
    }

    public int getTj() {
        return Tj;
    }

    public int getNi() {
        return Ni;
    }

    public int getNj() {
        return Nj;
    }

    public Point3D calculateSplineFunction(double u, double v, boolean isUEdge, boolean isVEdge) {
        //Pij - array of control points (spline points)

        if((u < uMin || u > uMax || v < vMin || v > vMax) && !isUEdge && !isVEdge)
            return null;
        double Px = 0, Py = 0, Pz = 0;      //function P(u,v) which is a 3D-point
        if(isUEdge && isVEdge)
            return splinePoints[Ni][Nj];
        else if (!isVEdge && isUEdge)
        {
            for (int j = 0; j <= Nj; j++) {
                double bj = calculateSplineBasisFunction(j, Tj, knotsJ, v);

                Px += splinePoints[Ni][j].x * bj;
                Py += splinePoints[Ni][j].y * bj;
                Pz += splinePoints[Ni][j].z * bj;
            }
        }
        else if (isVEdge /*&& !isUEdge*/)
        {
            for (int i = 0; i <= Ni; i++) {
                double bi = calculateSplineBasisFunction(i, Ti, knotsI, u);

                Px += splinePoints[i][Nj].x * bi;
                Py += splinePoints[i][Nj].y * bi;
                Pz += splinePoints[i][Nj].z * bi;

            }
        }
        else
        {
            for (int i = 0; i <= Ni; i++) {
                double bi = calculateSplineBasisFunction(i, Ti, knotsI, u);
                for (int j = 0; j <= Nj; j++) {
                    double bj = calculateSplineBasisFunction(j, Tj, knotsJ, v);
                    Px += splinePoints[i][j].x * bi * bj;
                    Py += splinePoints[i][j].y * bi * bj;
                    Pz += splinePoints[i][j].z * bi * bj;
                }
            }
        }

        return new Point3D(Px, Py, Pz);
    }

    Point3D calculateSplineFunction(double u, double v) {
        boolean isUEdge = false, isVEdge = false;

        if(u < uMin)
        {
            u = uMin;
        }
        else if (u > uMax)
        {
            u = uMax;
            isUEdge = true;
        }

        if(v < vMin)
        {
            v = vMin;
        }
        else if (v > vMax)
        {
            v = vMax;
            isVEdge = true;
        }

        return calculateSplineFunction(u, v, isUEdge, isVEdge);
    }
}

