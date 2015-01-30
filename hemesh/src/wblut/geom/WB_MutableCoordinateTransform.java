package wblut.geom;

public interface WB_MutableCoordinateTransform extends WB_CoordinateTransform {
    public WB_Coordinate applySelf(final WB_Transform T);

    public WB_Coordinate applyAsNormalSelf(final WB_Transform T);

    public WB_Coordinate applyAsPointSelf(final WB_Transform T);

    public WB_Coordinate applyAsVectorSelf(final WB_Transform T);

    public WB_Coordinate rotateAbout2PointAxisSelf(final double angle,
	    final double p1x, final double p1y, final double p1z,
	    final double p2x, final double p2y, final double p2z);

    public WB_Coordinate rotateAbout2PointAxisSelf(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2);

    public WB_Coordinate rotateAboutAxisSelf(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a);

    public WB_Coordinate scaleSelf(final double f);

    public WB_Coordinate scaleSelf(final double fx, final double fy,
	    final double fz);
}
