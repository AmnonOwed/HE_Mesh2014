/*
 *
 */
package wblut.geom;

/**
 *
 */
public interface WB_CoordinateTransform {
    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate apply(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @param result
     */
    public void applyInto(final WB_Transform T, WB_MutableCoordinate result);

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applyAsNormal(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @param result
     */
    public void applyAsNormalInto(final WB_Transform T,
	    final WB_MutableCoordinate result);

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applyAsPoint(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @param result
     */
    public void applyAsPointInto(final WB_Transform T,
	    final WB_MutableCoordinate result);

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applyAsVector(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @param result
     */
    public void applyAsVectorInto(final WB_Transform T,
	    final WB_MutableCoordinate result);

    /**
     *
     *
     * @param angle
     * @param p1x
     * @param p1y
     * @param p1z
     * @param p2x
     * @param p2y
     * @param p2z
     * @return
     */
    public WB_Coordinate rotateAbout2PointAxis(final double angle,
	    final double p1x, final double p1y, final double p1z,
	    final double p2x, final double p2y, final double p2z);

    /**
     *
     *
     * @param angle
     * @param p1
     * @param p2
     * @return
     */
    public WB_Coordinate rotateAbout2PointAxis(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2);

    /**
     *
     *
     * @param angle
     * @param px
     * @param py
     * @param pz
     * @param ax
     * @param ay
     * @param az
     * @return
     */
    public WB_Coordinate rotateAboutAxis(final double angle, final double px,
	    final double py, final double pz, final double ax, final double ay,
	    final double az);

    /**
     *
     *
     * @param angle
     * @param p
     * @param a
     * @return
     */
    public WB_Coordinate rotateAboutAxis(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a);

    /**
     *
     *
     * @param f
     * @return
     */
    public WB_Coordinate scale(final double f);

    /**
     *
     *
     * @param fx
     * @param fy
     * @param fz
     * @return
     */
    public WB_Coordinate scale(final double fx, final double fy, final double fz);
}
