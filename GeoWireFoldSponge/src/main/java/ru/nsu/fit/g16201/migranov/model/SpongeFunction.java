package ru.nsu.fit.g16201.migranov.model;

public class SpongeFunction extends ManifoldFunction {

    private double a;   //амплитуда
    public SpongeFunction(double a)
    {
        this.a = a;
        uMax = Double.POSITIVE_INFINITY;
        vMax = Double.POSITIVE_INFINITY;

        uMin = Double.NEGATIVE_INFINITY;
        vMin = Double.NEGATIVE_INFINITY;
    }
    @Override
    public double[] apply(double[] values) {
        double u = values[0], v = values[1];

        return new double[]{u, v, a * Math.sin(u) * Math.cos(v)};
    }


}
