package ru.nsu.fit.g16201.migranov.model;

public class BumpFunction extends ManifoldFunction {

    private double height, radius, radiusSquared;

    public BumpFunction(double height)
    {
        uMin = -5;
        uMax = 5;
        vMin =  -5;
        vMax = 5;
        this.height = height;
        /*if(radius < 0)
            radius= Math.abs(radius);
        this.radius = radius;
        radiusSquared = radius*radius;*/
    }

    @Override
    public double[] apply(double[] values) {
        double u = values[0], v = values[1];
        double rSquared = u*u + v*v;

        if(rSquared < 1.)
            return new double[] { u, v, height * Math.exp(-1/(1. - rSquared)) };
        else
            return new double[] { u, v, 0. };
    }
}
