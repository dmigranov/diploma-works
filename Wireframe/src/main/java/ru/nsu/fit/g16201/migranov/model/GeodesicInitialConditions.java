package ru.nsu.fit.g16201.migranov.model;

import ru.nsu.fit.g16201.migranov.model.Point3D;

public class GeodesicInitialConditions {
    private double uStart;
    private double vStart;

    private double uDir;
    private double vDir;


    public GeodesicInitialConditions(double uStart, double vStart, double uDir, double vDir) {
        this.uStart = uStart;
        this.vStart = vStart;
        this.uDir = uDir;
        this.vDir = vDir;
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
}
