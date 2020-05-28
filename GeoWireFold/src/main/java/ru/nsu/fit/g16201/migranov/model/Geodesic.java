package ru.nsu.fit.g16201.migranov.model;

import java.awt.*;

public class Geodesic {
    private double uStart;
    private double vStart;
    private double uDir;
    private double vDir;
    private Color color;

    private Point3D[] points;

    private boolean needsToBeRecalculated = true;

    public Geodesic(double uStart, double vStart, double uDir, double vDir, Color color) {
        this.uStart = uStart;
        this.vStart = vStart;
        this.uDir = uDir;
        this.vDir = vDir;
        this.color = color;
    }


    public double getuStart() {
        return uStart;
    }

    public double getvStart() {
        return vStart;
    }

    public double getuDir() {
        return uDir;
    }

    public double getvDir() {
        return vDir;
    }

    public Point3D[] getPoints() {
        return points;
    }

    public void setPoints(Point3D[] points) {
        this.points = points;
    }

    public Color getColor() {
        return color;
    }

    public void setuStart(double uStart) {
        this.uStart = uStart;
    }

    public void setvStart(double vStart) {
        this.vStart = vStart;
    }

    public void setuDir(double uDir) {
        this.uDir = uDir;
    }

    public void setvDir(double vDir) {
        this.vDir = vDir;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public boolean needsToBeRecalculated()
    {
        return needsToBeRecalculated;
    }

    public void setNeedsToBeRecalculated(boolean b)
    {
        needsToBeRecalculated = b;
    }
}
