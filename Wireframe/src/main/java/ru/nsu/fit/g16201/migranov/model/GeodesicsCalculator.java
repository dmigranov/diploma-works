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
import java.util.function.Function;

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

        Function<double[], double[]> func = new Function<double[], double[]>() {
            @Override
            public double[] apply(double[] vals) {
                double u = vals[0], v = vals[1];
                Point3D p = splineCalculator.calculateSplineFunction(u, v, Precision.equals(u, splineCalculator.getUMax()), Precision.equals(v, splineCalculator.getVMax()));
                return new double[] {p.x, p.y, p.z};
            }
        };
        double[] values = new double[] {u0, v0};

        double[] drdu = differentiatePolivariateVectorFunction(func, 0, values);
        double[] drdv = differentiatePolivariateVectorFunction(func, 1, values);

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
        return Arrays.stream(dr).mapToDouble(derivativeStructure -> derivativeStructure.getPartialDerivative(1)).toArray();
    }

    //double[] -> double[]
    private double[] differentiatePolivariateVectorFunction(Function<double[], double[]> fPoli, int diffArgNumber, double[] values)
    {
        //todo: добавить все необходимые проверки на аргументы
        UnivariateVectorFunction f = new UnivariateVectorFunction() {
            @Override
            public double[] value(double x) {
                double[] fValues = new double[values.length];
                Arrays.setAll(fValues, i -> i == diffArgNumber ? x : values[i]);
                return fPoli.apply(fValues);
            }
        };

        UnivariateDifferentiableVectorFunction dvf = differentiator.differentiate(f);

        DerivativeStructure drvs = new DerivativeStructure(1, 1, 0, values[diffArgNumber]); //просто переменная с такимто значением

        DerivativeStructure [] dr = dvf.value(drvs);
        return Arrays.stream(dr).mapToDouble(derivativeStructure -> derivativeStructure.getPartialDerivative(1)).toArray();

    }

    private double[][] differentiateUnivariateMatrixFunction(UnivariateMatrixFunction f, double value)
    {
        UnivariateDifferentiableMatrixFunction dmf = differentiator.differentiate(f);

        DerivativeStructure drvs = new DerivativeStructure(1, 1, 0, value); //просто переменная с такимто значением
        DerivativeStructure [][] dr = dmf.value(drvs);
        double[][] retArray = new double[dr.length][];

        for(int i = 0; i < dr.length; i++)
        {
            int len = dr[i].length;
            retArray[i] = new double[len];
            for(int j = 0; j < len; j++)
                retArray[i][j] = dr[i][j].getPartialDerivative(1);
        }
        return retArray;
    }

    //double[] -> dounle[][]
    private double[][] differentiatePolivariateMatrixFunction(Function<double[], double[][]> fPoli, int diffArgNumber, double[] values)
    {
        //todo: добавить все необходимые проверки на аргументы
        UnivariateMatrixFunction f = new UnivariateMatrixFunction() {
            @Override
            public double[][] value(double x) {
                double[] fValues = new double[values.length];
                Arrays.setAll(fValues, i -> i == diffArgNumber ? x : values[i]);
                return fPoli.apply(fValues);
            }
        };

        UnivariateDifferentiableMatrixFunction dmf = differentiator.differentiate(f);

        DerivativeStructure drvs = new DerivativeStructure(1, 1, 0, values[diffArgNumber]); //просто переменная с такимто значением
        DerivativeStructure [][] dr = dmf.value(drvs);
        double[][] retArray = new double[dr.length][];

        for(int i = 0; i < dr.length; i++)
        {
            int len = dr[i].length;
            retArray[i] = new double[len];
            for(int j = 0; j < len; j++)
                retArray[i][j] = dr[i][j].getPartialDerivative(1);
        }
        return retArray;
    }

    //потом просто пожставить, чтобы не вычитывать каждый раз производные заново при вызове метода!
    private double[][][] calculateChristoffelSymbol()
    {
        //в данном случае у нас x1 = u, x2 = v - внутренние координаты поверхности
        //по ним будем дифференцировать gij
        double[][][] Cs = new double[2][2][2];
        for(int i = 0; i < 2; i++)
            for(int j = 0; j < 2; j++)
                for(int k = 0; k < 2; k++)
                    ;


        return null;
    }

}
