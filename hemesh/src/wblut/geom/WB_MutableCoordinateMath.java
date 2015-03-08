/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public interface WB_MutableCoordinateMath extends WB_CoordinateMath {
    
    /**
     * 
     *
     * @param f 
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public WB_Coordinate addMulSelf(final double f, final double x,
	    final double y, final double z);

    /**
     * 
     *
     * @param f 
     * @param p 
     * @return 
     */
    public WB_Coordinate addMulSelf(final double f, final WB_Coordinate p);

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public WB_Coordinate addSelf(final double x, final double y, final double z);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Coordinate addSelf(final WB_Coordinate p);

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
     * @param p 
     * @return 
     */
    public WB_Coordinate crossSelf(final WB_Coordinate p);

    /**
     * 
     *
     * @param f 
     * @return 
     */
    public WB_Coordinate divSelf(final double f);

    /**
     * 
     *
     * @param f 
     * @param g 
     * @param p 
     * @return 
     */
    public WB_Coordinate mulAddMulSelf(final double f, final double g,
	    final WB_Coordinate p);

    /**
     * 
     *
     * @param f 
     * @return 
     */
    public WB_Coordinate mulSelf(final double f);

    /**
     * 
     *
     * @return 
     */
    public double normalizeSelf();

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public WB_Coordinate subSelf(final double x, final double y, final double z);

    /**
     * 
     *
     * @param v 
     * @return 
     */
    public WB_Coordinate subSelf(final WB_Coordinate v);

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public WB_Coordinate trimSelf(final double d);

    /**
     * 
     *
     * @param T 
     * @return 
     */
    public WB_Coordinate applySelf(final WB_Transform T);
}
