package ru.nsu.fit.g16201.migranov.model;

public class BumpFunction extends ManifoldFunction {

    public BumpFunction()
    {
        uMin = -3;
        uMax = 3;
        vMin =  -3;
        vMax = 3;
    }

    @Override
    public double[] apply(double[] values) {
        double u = values[0], v = values[1];
        double rSquared = u*u + v*v;

        if(rSquared < 1)
            return new double[] { u, v, Math.exp(-1/(1 - rSquared)) };
        else
            return new double[] { u, v, 0. };
    }
}
