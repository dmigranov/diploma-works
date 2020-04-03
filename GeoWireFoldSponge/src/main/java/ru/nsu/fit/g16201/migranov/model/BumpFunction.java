package ru.nsu.fit.g16201.migranov.model;

public class BumpFunction extends ManifoldFunction {
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
