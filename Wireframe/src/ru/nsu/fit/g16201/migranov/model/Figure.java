package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;
import java.util.List;

public class Figure {
    private Color color;
    private Point3D center;
    private Matrix rotateMatrix;

    private Point3D[][] modelPoints;

    private double length;
    private List<Point2D> splinePoints;

    private Matrix[] axes = new Matrix[4];

    public Figure(Point3D center, Color color, Matrix rotateMatrix, List<Point2D> splinePoints) {
        this.center = center;
        this.color = color;
        this.rotateMatrix = rotateMatrix;
        this.splinePoints = splinePoints;

        axes[0] = new Matrix(4, 1, center.x, center.y, center.z, 1);
        axes[1] = new Matrix(4, 1, center.x + 50, center.y, center.z, 1);
        axes[2] = new Matrix(4, 1, center.x, center.y + 50, center.z, 1);
        axes[3] = new Matrix(4, 1, center.x, center.y, center.z + 50, 1);
    }

    public List<Point2D> getSplinePoints() {
        return splinePoints;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Point3D getCenter() {
        return center;
    }

    public void setCenter(Point3D center) {
        this.center = center;
    }

    public Matrix getRotateMatrix() {
        return rotateMatrix;
    }

    public void setRotateMatrix(Matrix rotateMatrix) {
        this.rotateMatrix = rotateMatrix;
    }

    public Point3D[][] getModelPoints() {
        return modelPoints;
    }

    public void setModelPoints(Point3D[][] modelPoints) {
        this.modelPoints = modelPoints;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public Matrix[] getAxes() {
        return axes;
    }

    public void setAxes(Matrix[] axes) {
        this.axes = axes;
    }
}
