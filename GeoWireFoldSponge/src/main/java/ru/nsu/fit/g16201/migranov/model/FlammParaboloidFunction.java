package ru.nsu.fit.g16201.migranov.model;

public class FlammParaboloidFunction extends ManifoldFunction {

    public FlammParaboloidFunction()
    {
        uMin = 0;
        uMax = 2*Math.PI;
        vMin =  1.0001;
        vMax = 10;
    }

    @Override
    public double[] apply(double[] values) {
        double u = values[0], r = values[1];

        return new double[] {r * Math.cos(u), 2*Math.sqrt(r - 1), -r*Math.sin(u)};
    }
}
