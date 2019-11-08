package ru.nsu.fit.g16201.migranov.model;

public class SphereFunction extends ManifoldFunction {

    private double r;
    SphereFunction(double r)
    {
        this.r = r;
        uMax = Double.POSITIVE_INFINITY;
        vMax = Double.POSITIVE_INFINITY;

        uMin = Double.NEGATIVE_INFINITY;
        vMin = Double.NEGATIVE_INFINITY;
    }
    @Override
    public double[] apply(double[] values) {
        double u = values[0], v = values[1];

        return new double[]{r * Math.cos(u) * Math.cos(v), r * Math.sin(v), r * Math.sin(u) * Math.cos(v)};
    }
}
