package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.MaxCountExceededException;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;

public class GeodesicsEquations implements FirstOrderDifferentialEquations {

    private double Cs[][][];

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
            for (int k = 0; k < 2; k++)
                for (int i = 0; i < 2; i++)
                    sum += -Cs[0][k][i] * y[k] * y[i];
            yDot[j] = sum;
        }
        yDot[2] = y[0];
        yDot[3] = y[1];
    }
}
