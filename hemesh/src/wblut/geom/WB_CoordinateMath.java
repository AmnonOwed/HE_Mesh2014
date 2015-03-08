/*
 * 
 */
package wblut.geom;

import wblut.math.WB_M33;

/**
 * 
 */
public interface WB_CoordinateMath {
    
    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public WB_Coordinate add(final double x, final double y, final double z);;

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @param result 
     */
    public void addInto(final double x, final double y, final double z,
	    final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Coordinate add(final WB_Coordinate p);

    /**
     * 
     *
     * @param p 
     * @param result 
     */
    public void addInto(final WB_Coordinate p, final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param f 
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public WB_Coordinate addMul(final double f, final double x, final double y,
	    final double z);

    /**
     * 
     *
     * @param f 
     * @param x 
     * @param y 
     * @param z 
     * @param result 
     */
    public void addMulInto(final double f, final double x, final double y,
	    final double z, final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param f 
     * @param p 
     * @return 
     */
    public WB_Coordinate addMul(final double f, final WB_Coordinate p);

    /**
     * 
     *
     * @param f 
     * @param p 
     * @param result 
     */
    public void addMulInto(final double f, final WB_Coordinate p,
	    final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Coordinate cross(final WB_Coordinate p);

    /**
     * 
     *
     * @param p 
     * @param result 
     */
    public void crossInto(final WB_Coordinate p,
	    final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param f 
     * @return 
     */
    public WB_Coordinate div(final double f);

    /**
     * 
     *
     * @param f 
     * @param result 
     */
    public void divInto(final double f, final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public double dot(final WB_Coordinate p);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public double dot2D(final WB_Coordinate p);

    /**
     * 
     *
     * @param f 
     * @return 
     */
    public WB_Coordinate mul(final double f);

    /**
     * 
     *
     * @param f 
     * @param result 
     */
    public void mulInto(final double f, final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param f 
     * @param g 
     * @param p 
     * @return 
     */
    public WB_Coordinate mulAddMul(final double f, final double g,
	    final WB_Coordinate p);

    /**
     * 
     *
     * @param f 
     * @param g 
     * @param p 
     * @param result 
     */
    public void mulAddMulInto(final double f, final double g,
	    final WB_Coordinate p, final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param v 
     * @param w 
     * @return 
     */
    public double scalarTriple(final WB_Coordinate v, final WB_Coordinate w);

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public WB_Coordinate sub(final double x, final double y, final double z);

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @param result 
     */
    public void subInto(final double x, final double y, final double z,
	    final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Coordinate sub(final WB_Coordinate p);

    /**
     * 
     *
     * @param p 
     * @param result 
     */
    public void subInto(final WB_Coordinate p, final WB_MutableCoordinate result);

    /**
     * 
     *
     * @param v 
     * @return 
     */
    public WB_M33 tensor(final WB_Coordinate v);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public double absDot(final WB_Coordinate p);

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public double absDot2D(final WB_Coordinate p);
}
