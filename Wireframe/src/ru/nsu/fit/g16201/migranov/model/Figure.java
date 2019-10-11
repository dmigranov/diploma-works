package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;
import java.util.List;

public class Figure {
    private Color color;
    private Point3D center;
    private Matrix rotateMatrix;

    private Point3D[][] modelPoints;

    private double length;
    private Point3D[][] splinePoints;

    public Figure(Point3D center, Color color, Matrix rotateMatrix, Point3D[][] splinePoints) {
        this.center = center;
        this.color = color;
        this.rotateMatrix = rotateMatrix;
        this.splinePoints = splinePoints;
    }

    public Point3D[][] getSplinePoints() {
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
}
