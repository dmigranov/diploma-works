package ru.nsu.fit.g16201.migranov.model;

import java.util.function.Function;

public class SplineFunction implements ManifoldFunction {

    private SplineCalculator splineCalculator;

    public SplineFunction(SplineCalculator splineCalculator)
    {
        this.splineCalculator = splineCalculator;
    }


    @Override
    public double[] apply(double[] values) {
        double u = values[0], v = values[1];
        Point3D p = splineCalculator.calculateSplineFunction(u, v);
        if(p == null) return null;
        return new double[]{p.x, p.y, p.z};
    }
}
