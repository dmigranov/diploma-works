package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction;
import org.apache.commons.math3.util.Precision;

public class GeodesicsCalculator {
    private SplineCalculator splineCalculator;

    public GeodesicsCalculator(SplineCalculator splineCalculator)
    {
        this.splineCalculator = splineCalculator;
    }

    public Matrix calculateMetricTensor(double u0, double v0)   //это функция, её тоже можно продифференцировать
    {
        double g11, g22, g12, g21;

        double xu, xv, yu, yv, zu, zv;

        FiniteDifferencesDifferentiator differentiator =  new FiniteDifferencesDifferentiator(5, 0.01);
        UnivariateDifferentiableVectorFunction drdu = differentiator.differentiate((UnivariateVectorFunction) u -> {
            Point3D p;
            p = splineCalculator.calculateSplineFunction(u, v0, Precision.equals(u, splineCalculator.getUMax()), Precision.equals(v0, splineCalculator.getVMax()));
            return new double[] {p.x, p.y, p.z};
        });

        UnivariateDifferentiableVectorFunction drdv = differentiator.differentiate((UnivariateVectorFunction) v -> {
            Point3D p;
            p = splineCalculator.calculateSplineFunction(u0, v, Precision.equals(u0, splineCalculator.getUMax()), Precision.equals(v, splineCalculator.getVMax()));
            return new double[] {p.x, p.y, p.z};
        });

        DerivativeStructure u0drvs = new DerivativeStructure(1, 1, 0, u0); //просто переменная с такимто значением
        DerivativeStructure v0drvs = new DerivativeStructure(1, 1, 0, v0);

        DerivativeStructure [] drdu0 = drdu.value(u0drvs);
        DerivativeStructure [] drdv0 = drdv.value(v0drvs);

        xu = drdu0[0].getPartialDerivative(1); yu = drdu0[1].getPartialDerivative(1); zu = drdu0[2].getPartialDerivative(1);
        xv = drdv0[0].getPartialDerivative(1); yv = drdv0[1].getPartialDerivative(1); zv = drdv0[2].getPartialDerivative(1);

        g11 = xu*xu + yu*yu + zu*zu;
        g12 = g21 = xu*xv + yu*yv + zu*zv;
        g22 = xv*xv + yv*yv + zv*zv;

        return new Matrix(2, 2, g11, g12, g21, g22);
    }

    public Matrix calculateContravariantMetricTensor(Matrix g)
    {
        //это будет просто обратная матрица к обычному (ковариантному) метрическому тензору
        return new Matrix(1, 1, 1);

    }

    private double[] differentiateVectorFunction()
    {

    }

}
