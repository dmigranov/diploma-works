package ru.nsu.fit.g16201.migranov.model;

public class Point2D {
    public double x, y;

    public Point2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public static double distance(Point2D p1, Point2D p2)
    {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.x - p2.x, 2));
    }
}
