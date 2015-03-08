/*
 * 
 */
package wblut.geom;

/**
 * Simple interface for anything that present itself as a 2D, 3D or 4D tuple of
 * double. Useful to create a wrapper for other point and vector-classes. If the
 * class is intended to be mutable use {@link wblut.geom.WB_MutableCoordinate}.
 *
 * @author Frederik Vanhoutte
 *
 */
public interface WB_Coordinate {
    
    /**
     * Get x as double.
     *
     * @return x
     */
    public double xd();

    /**
     * Get y as double.
     *
     * @return y
     */
    public double yd();

    /**
     * Get z as double.
     *
     * @return z
     */
    public double zd();

    /**
     * Get w as double.
     *
     * @return w
     */
    public double wd();

    /**
     * Get i'th ordinate as double.
     *
     * @param i 
     * @return i'th ordinate
     */
    public double getd(int i);

    /**
     * Get x as float.
     *
     * @return x
     */
    public float xf();

    /**
     * Get y as float.
     *
     * @return y
     */
    public float yf();

    /**
     * Get z as float.
     *
     * @return z
     */
    public float zf();

    /**
     * Get w as float.
     *
     * @return w
     */
    public float wf();

    /**
     * Get i'th ordinate as float.
     *
     * @param i 
     * @return i'th ordinate
     */
    public float getf(int i);
}
