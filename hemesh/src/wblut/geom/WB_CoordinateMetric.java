package wblut.geom;

public interface WB_CoordinateMetric {
    public double getAngle(final WB_Coordinate p);

    public double getAngleNorm(final WB_Coordinate p);

    public double getDistance2D(final WB_Coordinate p);

    public double getDistance3D(final WB_Coordinate p);

    public double getLength2D();

    public double getLength3D();

    public WB_Coordinate getOrthoNormal2D();

    public WB_Coordinate getOrthoNormal3D();

    public double getSqDistance2D(final WB_Coordinate p);

    public double getSqDistance3D(final WB_Coordinate p);

    public double getSqLength2D();

    public double getSqLength3D();

    public double heading();

    public boolean isZero();
}
