package ru.nsu.fit.g16201.migranov.model;

public class SpongeFunction extends ManifoldFunction {

    private double a;   //амплитуда
    public SpongeFunction(double a)
    {
        this.a = a;
        uMin = -Math.PI * 4;
        uMax = Math.PI * 4;
        vMin =  -Math.PI * 4;
        vMax = Math.PI * 4;
    }
    @Override
    public double[] apply(double[] values) {
        double u = values[0], v = values[1];

        return new double[]{u, v, a * Math.sin(u) * Math.cos(v)};
    }


}
