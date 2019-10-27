package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class GeodesicsEquations implements FirstOrderDifferentialEquations {

    @Override
    public int getDimension() {
        return 4;
    }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {

    }
}
