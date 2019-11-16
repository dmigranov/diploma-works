package ru.nsu.fit.g16201.migranov.model;

public class SphereFunction extends ManifoldFunction {

    private double r;
    public SphereFunction(double r)
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

    @Override
    public double[] calculateInitialState(double posU, double posV, double dirU, double dirV) {
        double angle = Math.atan2(dirU, dirV);       //zn - расстояние от наблюдателя до экра
        //dirU = realX, dirV = zn

        double cos_v = Math.cos(posV);
        double cos_u = Math.cos(posU), sin_u = Math.sin(posU), sin_v = Math.sin(posV);

        double du = Math.sin(angle)/(cos_v*r);
        double dv = Math.cos(angle)/r;
        return new double[] {du, dv, posU, posV};
    }
}
