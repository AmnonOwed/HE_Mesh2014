/*
 *
 */
package wblut.geom;

import wblut.math.WB_M33;

/**
 * Interface for implementing non-mutable mathematical operations.If the
 * operations should change the calling object use
 * {@link wblut.geom.WB_MutableCoordinateMath}.
 *
 * @author Frederik Vanhoutte
 *
 */
public interface WB_CoordinateMath {
    /**
     * Add coordinate values.
     *
     * @param x
     * @return new WB_coordinate
     */
    public WB_Coordinate add(final double... x);

    /**
     * Add coordinate values and store in mutable coordinate.
     *
     * @param result
     * @param x
     */
    public void addInto(final WB_MutableCoordinate result, final double... x);

    /**
     * Add coordinate values.
     *
     * @param p
     * @return new WB_coordinate
     */
    public WB_Coordinate add(final WB_Coordinate p);

    /**
     * Add coordinate values and store in mutable coordinate.
     *
     * @param result
     * @param p
     */
    public void addInto(final WB_MutableCoordinate result, final WB_Coordinate p);

    /**
     * Add multiple of coordinate values.
     *
     * @param f
     *            multiplier
     * @param x
     * @return new WB_coordinate
     */
    public WB_Coordinate addMul(final double f, final double... x);

    /**
     * Add multiple of coordinate values and store in mutable coordinate.
     *
     * @param result
     * @param f
     *            multiplier
     * @param x
     */
    public void addMulInto(final WB_MutableCoordinate result, final double f,
	    final double... x);

    /**
     * Add multiple of coordinate values.
     *
     * @param f
     * @param p
     * @return new WB_coordinate
     */
    public WB_Coordinate addMul(final double f, final WB_Coordinate p);

    /**
     * Add multiple of coordinate values and store in mutable coordinate.
     *
     * @param result
     * @param f
     * @param p
     */
    public void addMulInto(final WB_MutableCoordinate result, final double f,
	    final WB_Coordinate p);

    /**
     * Subtract coordinate values.
     *
     * @param x
     * @return new WB_coordinate
     */
    public WB_Coordinate sub(final double... x);

    /**
     * Subtract coordinate values and store in mutable coordinate.
     *
     * @param result
     * @param x
     */
    public void subInto(final WB_MutableCoordinate result, final double... x);

    /**
     * Subtract coordinate values.
     *
     * @param p
     * @return new WB_coordinate
     */
    public WB_Coordinate sub(final WB_Coordinate p);

    /**
     * Subtract coordinate values and store in mutable coordinate.
     *
     * @param result
     * @param p
     */
    public void subInto(final WB_MutableCoordinate result, final WB_Coordinate p);

    /**
     * Multiply by factor.
     *
     * @param f
     * @return new WB_coordinate
     */
    public WB_Coordinate mul(final double f);

    /**
     * Multiply by factor and store in mutable coordinate.
     *
     * @param result
     * @param f
     */
    public void mulInto(final WB_MutableCoordinate result, final double f);

    /**
     * Multiply this coordinate by factor f and add other coordinate values
     * multiplied by g.
     *
     * @param f
     * @param g
     * @param x
     * @return new WB_coordinate
     */
    public WB_Coordinate mulAddMul(final double f, final double g,
	    final double... x);

    /**
     * Multiply this coordinate by factor f and add other coordinate values
     * multiplied by g.
     *
     * @param f
     * @param g
     * @param p
     * @return new WB_coordinate
     */
    public WB_Coordinate mulAddMul(final double f, final double g,
	    final WB_Coordinate p);

    /**
     * Multiply this coordinate by factor f, add other coordinate values
     * multiplied by g and store result in mutable coordinate.
     *
     * @param result
     * @param f
     * @param g
     * @param x
     */
    public void mulAddMulInto(final WB_MutableCoordinate result,
	    final double f, final double g, final double... x);

    /**
     * Multiply this coordinate by factor f, add other coordinate values
     * multiplied by g and store result in mutable coordinate.
     *
     * @param result
     * @param f
     * @param g
     * @param p
     */
    public void mulAddMulInto(final WB_MutableCoordinate result,
	    final double f, final double g, final WB_Coordinate p);

    /**
     * Divide by factor.
     *
     * @param f
     * @return new WB_coordinate
     */
    public WB_Coordinate div(final double f);

    /**
     * Divide by factor and store in mutable coordinate.
     *
     * @param result
     * @param f
     */
    public void divInto(final WB_MutableCoordinate result, final double f);

    /**
     * Cross product of this coordinate with other coordinate.
     *
     * @param p
     * @return new WB_coordinate
     */
    public WB_Coordinate cross(final WB_Coordinate p);

    /**
     * Store cross product of this coordinate with other coordinate in mutable
     * coordinate. coordinate.
     *
     * @param result
     * @param p
     */
    public void crossInto(final WB_MutableCoordinate result,
	    final WB_Coordinate p);

    /**
     * Dot product.
     *
     * @param p
     * @return dot product
     */
    public double dot(final WB_Coordinate p);

    /**
     * 2D dot product
     *
     * @param p
     * @return 2D dot product
     */
    public double dot2D(final WB_Coordinate p);

    /**
     * Absolute value of dot product.
     *
     * @param p
     * @return absolute value of dot product
     */
    public double absDot(final WB_Coordinate p);

    /**
     * Absolute value of 2D dot product.
     *
     * @param p
     * @return absolute value of 2D dot product
     */
    public double absDot2D(final WB_Coordinate p);

    /**
     * Tensor product.
     *
     * @param v
     * @return tensor product
     */
    public WB_M33 tensor(final WB_Coordinate v);

    /**
     * Scalar triple: this.(v x w)
     *
     * @param v
     * @param w
     * @return scalar triple
     */
    public double scalarTriple(final WB_Coordinate v, final WB_Coordinate w);
}
