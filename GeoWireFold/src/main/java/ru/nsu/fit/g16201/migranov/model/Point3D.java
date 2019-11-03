package ru.nsu.fit.g16201.migranov.model;

public class Point3D {
    public double x, y, z;

    public Point3D(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Point3D getVectorProduct(Point3D a, Point3D b) {
        //return new Point3D(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
        //return new Point3D(-(a.y*b.z - a.z*b.y), -(a.z*b.x - a.x*b.z), -(a.x*b.y - a.y*b.x));
        return getLeftVectorProduct(a, b);
    }

    public static Point3D multiplyByScalar(double k, Point3D a) {
        return new Point3D(a.x * k, a.y * k, a.z * k);
    }

    public static double getScalarProduct(Point3D a, Point3D b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    static Point3D getRightVectorProduct(Point3D a, Point3D b)
    {
        return new Point3D(a.y*b.z - a.z*b.y, a.z*b.x - a.x*b.z, a.x*b.y - a.y*b.x);
    }

    static Point3D getLeftVectorProduct(Point3D a, Point3D b)
    {
        return new Point3D(-(a.y*b.z - a.z*b.y), -(a.z*b.x - a.x*b.z), -(a.x*b.y - a.y*b.x));
    }

    public static double getDistanceSquare(Point3D r0, Point3D intersectionPoint) {
        return Math.pow(r0.x - intersectionPoint.x, 2) + Math.pow(r0.y - intersectionPoint.y, 2) + Math.pow(r0.z - intersectionPoint.z, 2);
    }


    public Point3D normalize()
    {
        double len = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
        return new Point3D(x/len, y/len, z/len);
    }

    public static Point3D add(Point3D p1, Point3D p2)
    {
        return new Point3D(p1.x + p2.x, p1.y + p2.y, p1.z + p2.z);
    }

    public static Point3D subtract(Point3D p1, Point3D p2) {
        return new Point3D(p1.x - p2.x, p1.y - p2.y, p1.z - p2.z);
    }

    public static Point3D getNegative(Point3D p1)
    {
        return new Point3D(-p1.x, -p1.y, -p1.z);
    }

    public double getLength()
    {
        return Math.sqrt(x*x + y*y + z*z);
    }

    @Override
    public boolean equals(Object obj) {
        Point3D p = (Point3D)obj;
        return x==p.x && y==p.y && z==p.z;
    }
}
