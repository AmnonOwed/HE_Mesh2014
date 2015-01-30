package wblut.geom;

public interface WB_CoordinateTransform {
    public WB_Coordinate apply(final WB_Transform T);

    public void applyInto(final WB_Transform T, WB_MutableCoordinate result);

    public WB_Coordinate applyAsNormal(final WB_Transform T);

    public void applyAsNormalInto(final WB_Transform T,
	    final WB_MutableCoordinate result);

    public WB_Coordinate applyAsPoint(final WB_Transform T);

    public void applyAsPointInto(final WB_Transform T,
	    final WB_MutableCoordinate result);

    public WB_Coordinate applyAsVector(final WB_Transform T);

    public void applyAsVectorInto(final WB_Transform T,
	    final WB_MutableCoordinate result);

    public WB_Coordinate rotateAbout2PointAxis(final double angle,
	    final double p1x, final double p1y, final double p1z,
	    final double p2x, final double p2y, final double p2z);

    public WB_Coordinate rotateAbout2PointAxis(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2);

    public WB_Coordinate rotateAboutAxis(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a);

    public WB_Coordinate scale(final double f);

    public WB_Coordinate scale(final double fx, final double fy, final double fz);
}
