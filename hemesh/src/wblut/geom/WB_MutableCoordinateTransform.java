/*
 *
 */
package wblut.geom;

/**
 *
 */
public interface WB_MutableCoordinateTransform extends WB_CoordinateTransform {
    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applySelf(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applyAsNormalSelf(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applyAsPointSelf(final WB_Transform T);

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_Coordinate applyAsVectorSelf(final WB_Transform T);

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
    public WB_Coordinate rotateAbout2PointAxisSelf(final double angle,
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
    public WB_Coordinate rotateAbout2PointAxisSelf(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2);

    /**
     *
     *
     * @param angle
     * @param p
     * @param a
     * @return
     */
    public WB_Coordinate rotateAboutAxisSelf(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a);

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
    public WB_Coordinate rotateAboutAxisSelf(final double angle,
	    final double px, final double py, final double pz, final double ax,
	    final double ay, final double az);

    /**
     *
     *
     * @param f
     * @return
     */
    public WB_Coordinate scaleSelf(final double f);

    /**
     *
     *
     * @param fx
     * @param fy
     * @param fz
     * @return
     */
    public WB_Coordinate scaleSelf(final double fx, final double fy,
	    final double fz);
}
