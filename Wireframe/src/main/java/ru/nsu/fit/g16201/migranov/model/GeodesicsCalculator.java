package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableMatrixFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction;
import org.apache.commons.math3.util.Precision;

import java.util.Arrays;
import java.util.List;

public class GeodesicsCalculator {
    private SplineCalculator splineCalculator;

    public GeodesicsCalculator(SplineCalculator splineCalculator)
    {
        this.splineCalculator = splineCalculator;
    }


    private FiniteDifferencesDifferentiator differentiator =  new FiniteDifferencesDifferentiator(5, 0.01);
    public double[][] calculateMetricTensor(double u0, double v0)   //это функция, её тоже можно продифференцировать
    {
        double g11, g22, g12, g21;

        double xu, xv, yu, yv, zu, zv;

        double[] drdu = differentiateUnivariateVectorFunction(u -> {
            Point3D p = splineCalculator.calculateSplineFunction(u, v0, Precision.equals(u, splineCalculator.getUMax()), Precision.equals(v0, splineCalculator.getVMax()));
            return new double[] {p.x, p.y, p.z};
        }, u0);

        double[] drdv = differentiateUnivariateVectorFunction(v -> {
            Point3D p = splineCalculator.calculateSplineFunction(u0, v, Precision.equals(u0, splineCalculator.getUMax()), Precision.equals(v, splineCalculator.getVMax()));
            return new double[] {p.x, p.y, p.z};
        }, v0);

        xu = drdu[0]; yu = drdu[1]; zu = drdu[2];
        xv = drdv[0]; yv = drdv[1]; zv = drdv[2];

        g11 = xu*xu + yu*yu + zu*zu;
        g12 = g21 = xu*xv + yu*yv + zu*zv;
        g22 = xv*xv + yv*yv + zv*zv;

        return new double[][] {{g11, g12}, {g21, g22}};
    }

    private double[][] calculateContravariantMetricTensor(double[][] g)
    {
        double g11 = g[0][0], g12 = g[0][1], g22 = g[1][1];  //symm
        double det = g11*g22 - g12*g12;

        //todo: а если не обратима? det = 0

        return new double[][]{ {g22/det, -g12/det}, {-g12/det, g11/det}};
    }

    //f: (u, v) |-> (x, y, z)
    private double[] differentiateUnivariateVectorFunction(UnivariateVectorFunction f, double value)
    {
        UnivariateDifferentiableVectorFunction dvf = differentiator.differentiate(f);

        DerivativeStructure drvs = new DerivativeStructure(1, 1, 0, value); //просто переменная с такимто значением

        DerivativeStructure [] dr = dvf.value(drvs);
        //return new Point3D(dr[0].getPartialDerivative(1), dr[1].getPartialDerivative(1), dr[2].getPartialDerivative(1));
        return Arrays.stream(dr).mapToDouble(derivativeStructure -> derivativeStructure.getPartialDerivative(1)).toArray();
    }

    private double[][] differentiateUnivariateMatrixFunction(UnivariateMatrixFunction f, double value)
    {
        UnivariateDifferentiableMatrixFunction dmf = differentiator.differentiate(f);

        DerivativeStructure drvs = new DerivativeStructure(1, 1, 0, value); //просто переменная с такимто значением

        DerivativeStructure [][] dr = dmf.value(drvs);
        return null;
    }

    //потом просто пожставить, чтобы не вычитывать каждый раз производные заново при вызове метода!
    private double[][][] calculateChristoffelSymbol()
    {

        return null;
    }

}
