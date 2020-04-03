package ru.nsu.fit.g16201.migranov.model;

import java.util.function.Function;

public abstract class ManifoldFunction implements Function<double[], double[]> {
    double uMin;
    double uMax;
    double vMin;
    double vMax;

    public double getUMin() {return uMin;}
    public double getVMin() {return vMin;}
    public double getUMax() {return uMax;}
    public double getVMax() {return vMax;}

    public double[] apply(double u, double v)
    {
        return apply(new double[]{u, v});
    }


}
