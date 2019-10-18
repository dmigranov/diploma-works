package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class GeodesicsCalculator {
    private Matrix calculateMetricTensor(double u0, double v0)
    {
        double g11, g22, g12, g21;

        double xu, xv, yu, yv, zu, zv;

        DerivativeStructure u = new DerivativeStructure(2, 1, 0, u0);  //an instance representing a variable u
        DerivativeStructure v = new DerivativeStructure(2, 1, 1, v0);  //an instance representing a variable v


        g11 = xu*xu + yu*yu + zu*zu;
        g12 = g21 = xu*xv + yu*yv + zu*zv;
        g22 = xv*xv + yv*yv + zv*zv;

        return new Matrix(2, 2, g11, g12, g21, g22);
    }

}
