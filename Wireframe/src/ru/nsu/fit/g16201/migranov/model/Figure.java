package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;
import java.util.List;

public class Figure {
    private Color color;
    private Matrix rotateMatrix;

    private Point3D[][] modelPoints;

    private double length;
    private Point3D[][] splinePoints;

    public Figure(Color color, Matrix rotateMatrix, Point3D[][] splinePoints) {
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
