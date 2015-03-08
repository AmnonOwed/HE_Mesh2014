/*
 * 
 */
package wblut.geom;

/**
 * Simple interface for anything that present itself as a mutable 2D or 3D tuple
 * of double.
 * 
 * @author Frederik Vanhoutte
 * 
 */
public interface WB_MutableCoordinate extends WB_Coordinate {
    
    /**
     * Set x.
     *
     * @param x 
     * @deprecated Use {@link #setX(double)} instead
     */
    @Deprecated
    public void _setX(double x);

    /**
     * Set x.
     *
     * @param x 
     */
    public void setX(double x);

    /**
     * Set y.
     *
     * @param y 
     * @deprecated Use {@link #setY(double)} instead
     */
    @Deprecated
    public void _setY(double y);

    /**
     * Set y.
     *
     * @param y 
     */
    public void setY(double y);

    /**
     * Set z.
     *
     * @param z 
     * @deprecated Use {@link #setZ(double)} instead
     */
    @Deprecated
    public void _setZ(double z);

    /**
     * Set z.
     *
     * @param z 
     */
    public void setZ(double z);

    /**
     * Set w.
     *
     * @param w 
     * @deprecated Use {@link #setW(double)} instead
     */
    @Deprecated
    public void _setW(double w);

    /**
     * Set w.
     *
     * @param w 
     */
    public void setW(double w);

    /**
     * Set i'th ordinate.
     *
     * @param i            ordinate to set
     * @param v            value
     * @deprecated Use {@link #setCoord(int,double)} instead
     */
    @Deprecated
    public void _setCoord(int i, double v);

    /**
     * Set i'th ordinate.
     *
     * @param i            ordinate to set
     * @param v            value
     */
    public void setCoord(int i, double v);

    /**
     * Set to value of tuple.
     *
     * @param p            tuple to copy
     * @deprecated Use {@link #set(WB_Coordinate)} instead
     */
    @Deprecated
    public void _set(WB_Coordinate p);

    /**
     * Set to value of tuple.
     *
     * @param p            tuple to copy
     */
    public void set(WB_Coordinate p);

    /**
     * Set to coordinates.
     *
     * @param x            x-ordinate
     * @param y            y-ordinate
     * @deprecated Use {@link #set(double,double)} instead
     */
    @Deprecated
    public void _set(double x, double y);

    /**
     * Set to coordinates.
     *
     * @param x            x-ordinate
     * @param y            y-ordinate
     */
    public void set(double x, double y);

    /**
     * Set to coordinates.
     *
     * @param x            x-ordinate
     * @param y            y-ordinate
     * @param z            z-ordinate
     * @deprecated Use {@link #set(double,double,double)} instead
     */
    @Deprecated
    public void _set(double x, double y, double z);

    /**
     * Set to coordinates.
     *
     * @param x            x-ordinate
     * @param y            y-ordinate
     * @param z            z-ordinate
     */
    public void set(double x, double y, double z);

    /**
     * Set to coordinates.
     *
     * @param x            x-ordinate
     * @param y            y-ordinate
     * @param z            z-ordinate
     * @param w            w-ordinate
     * @deprecated Use {@link #set(double,double,double,double)} instead
     */
    @Deprecated
    public void _set(double x, double y, double z, double w);

    /**
     * Set to coordinates.
     *
     * @param x            x-ordinate
     * @param y            y-ordinate
     * @param z            z-ordinate
     * @param w            w-ordinate
     */
    public void set(double x, double y, double z, double w);
}
