package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class GeodesicsEquations implements FirstOrderDifferentialEquations {

    private double[][][] Cs;

    GeodesicsEquations(double[][][] Cs)
    {
        this.Cs = Cs;
    }

    @Override
    public int getDimension() {
        return 4;
    }

    @Override
    public void computeDerivatives(double t, double[] y, double[] yDot) throws MaxCountExceededException, DimensionMismatchException {
        //0 - dx1', 1 - dx2', 2 - dx1, 3 - dx2

        for(int j = 0; j < 2; j++)
        {
            double sum = 0;

            yDot[j] = -Cs[j][0][0]*y[0]*y[0] - 2 * Cs[j][0][1]*y[0]*y[1] - Cs[j][1][1]*y[1]*y[1];
        }
        yDot[2] = y[0];
        yDot[3] = y[1];
    }
}
