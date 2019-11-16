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
        static if (poleSingularity)
            if (abs(cos_v) < 0.000001) cos_v = cos_v >= 0 ? 0.000001 : 0.000001;
        enum u_code = format("du = sin(angle) / sqrt(%s);", codes[3]).txtSimp;
        enum v_code = format("dv = cos(angle) / sqrt(%s);", codes[5]).txtSimp;
        pragma(msg, u_code);
        pragma(msg, v_code);
        mixin(u_code);
        mixin(v_code);
        //du = sin(angle)/(cos_v*R); dv = cos(angle)/R;

    }
}
