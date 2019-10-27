package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.ode.SecondOrderDifferentialEquations;

public class GeodesicsEquations implements SecondOrderDifferentialEquations {
    @Override
    public int getDimension() {
        return 2;
    }

    @Override
    public void computeSecondDerivatives(double t, double[] y, double[] yDot, double[] yDDot) {

    }
}
