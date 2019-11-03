package ru.nsu.fit.g16201.migranov.model;

import org.apache.commons.math3.analysis.UnivariateMatrixFunction;
import org.apache.commons.math3.analysis.UnivariateVectorFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableMatrixFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableVectorFunction;
import org.apache.commons.math3.ode.FirstOrderDifferentialEquations;
import org.apache.commons.math3.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import org.apache.commons.math3.util.Precision;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class GeodesicsCalculator {
    private SplineCalculator splineCalculator;

    public GeodesicsCalculator(SplineCalculator splineCalculator)
    {
        this.splineCalculator = splineCalculator;
    }

    private double epsilon = 0.01;
    private FiniteDifferencesDifferentiator differentiator =  new FiniteDifferencesDifferentiator(5, epsilon);

    private Function<double[], double[][]> metricTensorFunction = values -> calculateMetricTensor(values[0], values[1]);
    private Function<double[], double[]> splineFunction = new Function<>() {
        @Override
        public double[] apply(double[] values) {
            double u = values[0], v = values[1];
            Point3D p = splineCalculator.calculateSplineFunction(u, v);
            if(p == null) return null;
            return new double[]{p.x, p.y, p.z};
        }
    };

    private double[][] calculateMetricTensor(double u0, double v0)   //это функция, её тоже можно продифференцировать
    {
        double g11, g22, g12, g21;
        double xu, xv, yu, yv, zu, zv;

        double[] values = new double[] {u0, v0};
        double[] drdu = differentiatePolivariateVectorFunction(splineFunction, 0, values);
        double[] drdv = differentiatePolivariateVectorFunction(splineFunction, 1, values);

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
        //todo: при uniform spacing определитель равен нулю, из-за чего проблемы!
        return new double[][]{ {g22/det, -g12/det}, {-g12/det, g11/det}};
    }


    private double[][] calculateContravariantMetricTensor(double u0, double v0)
    {
        return calculateContravariantMetricTensor(calculateMetricTensor(u0, v0));
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

    //double[] -> dobnle[][]
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

    private double[][][] calculateChristoffelSymbol(double u0, double v0)
    {

        double[][][] Cs = new double[2][2][2];  //C[i][k][l] = Г ^i  kl
        double[] values = {u0, v0};
        double[][][] gDiff = new double[2][][];
        double[][] gContra = calculateContravariantMetricTensor(u0, v0);
        Arrays.setAll(gDiff, i -> differentiatePolivariateMatrixFunction(metricTensorFunction, i, values));
        //gDiff[i][j][k] = d gjk / dxi; в данном случае у нас x1 = u, x2 = v - внутренние координаты поверхности

        for(int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                for (int k = 0; k < 2; k++) {
                    double sum = 0;
                    for(int m = 0; m < 2; m++)
                    {
                        sum += gContra[i][m] * (gDiff[k][m][j] + gDiff[j][m][k] - gDiff[m][j][k]);
                    }
                    Cs[i][j][k] = sum/2;
                }
            }
        }

        return Cs;
    }

    //u0_s и v0_s - это вектор из точки u0 v0
    private double[] geodesicEquationStep(double[] state, double t0, double step)
    {
        //кривая на двумерой поверхности задаётся одним парамаетром t
        //по сути внутри просто интегрируем, используя разные разностные операторы
        double[][][] Cs = calculateChristoffelSymbol(state[2], state[3]);
        ClassicalRungeKuttaIntegrator rg = new ClassicalRungeKuttaIntegrator(1.0e-8);   //Это число ни на что не влияет, т.к. использую singleStep
        FirstOrderDifferentialEquations ode = new GeodesicsEquations(Cs);
        return rg.singleStep(ode, t0, state, t0 + step);
    }

    private Point3D[] calculateGeodesic(double uStart, double vStart, double uDir, double vDir)
    {
        //Point3D[] points = new Point3D[30];
        List<Point3D> points = new ArrayList<>();
        double[] state = new double[] {uDir, vDir, uStart, vStart}, newState;
        double t = 0, step = 0.3;
        double eps = 4*epsilon;
        double u = 0, v = 0;
        while(true) //todo или по превышении числа итераций..
        {
            u = state[2];
            v = state[3];
            if(u <= splineCalculator.getUMin() + eps || u >= splineCalculator.getUMax() - eps || v <= splineCalculator.getVMin() + eps || v >= splineCalculator.getVMax() - eps)
                break;
            Point3D p = splineCalculator.calculateSplineFunction(u, v);
            if(p == null)
                break;
            points.add(p);
            t += step;
            state = geodesicEquationStep(state, t, step);;
        }

        return points.toArray(new Point3D[0]);
    }

    public void calculateGeodesic(Geodesic geodesic) {
        Point3D[] points = calculateGeodesic(geodesic.getuStart(), geodesic.getvStart(), geodesic.getuDir(), geodesic.getvDir());
        geodesic.setPoints(points);
    }
}
