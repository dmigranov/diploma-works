package ru.nsu.fit.g16201.migranov.model;

import java.util.function.Function;

public abstract class ManifoldFunction implements Function<double[], double[]> {
    double uMin;
    double uMax;
    double vMin;
    double vMax;

    double getUMin() {
        return uMin;
    }

    double getVMin() {
        return vMin;
    }

    double getUMax() {
        return uMax;
    }

    double getVMax() {
        return vMax;
    }

    public double[] apply(double u, double v) {
        return apply(new double[]{u, v});
    }

    public abstract double[] calculateInitialState(double posU, double posV, double dirU, double dirV);
}
