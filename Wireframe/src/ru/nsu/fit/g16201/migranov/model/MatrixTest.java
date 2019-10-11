package ru.nsu.fit.g16201.migranov.model;

import static org.junit.jupiter.api.Assertions.*;

class MatrixTest {

    @org.junit.jupiter.api.Test
    void multiply() {
        Matrix m1 = new Matrix(2, 2, 1, 2, 3, 4);
        Matrix m2 = new Matrix(2, 1, 1, 2);
        Matrix m = Matrix.multiply(m1, m2);


        m1 = new Matrix(1, 3, 1, 2, 3);
        m2 = new Matrix(3, 2, 1, 2, 3, 4, 5, 6);
        m = Matrix.multiply(m1, m2);

        Matrix M = Matrix.multiplyByScalar(1.0/6, new Matrix(4, 4, -1, 3, -3, 1, 3, -6, 3, 0, -3, 0, 3, 0, 1, 4, 1, 0));
        Matrix Gx = new Matrix(4, 1, 4, 5, 4, 2);
        Matrix T = new Matrix(1, 4, 27, 9, 3, 1);

        long time = 0;
        for(int i = 0; i < 100; i++)
        {
            long start = System.nanoTime();
            Matrix r = Matrix.multiply(Matrix.multiply(T, M), Gx);
            long end = System.nanoTime();
            //System.out.println(time);
            time += (end - start);
        }
        System.out.println(time/100L);

        m1 = new Matrix(1, 3, 1, 2, 3);
        m2 = new Matrix(1, 3, 1, 2, 3);
        Matrix m3 = Matrix.add(m1, m2);



    }
}