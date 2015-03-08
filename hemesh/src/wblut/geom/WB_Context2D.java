/*
 * 
 */
package wblut.geom;

/**
 *
 * WB_Context2D is an interface for classes that transform between 3D
 * coordinates and 2D coordinates through some from of mapping or projection.
 *
 *
 */
public interface WB_Context2D {
    
    /**
     * Map 3D point to 2D space.
     *
     * @param p            3D point
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void pointTo2D(WB_Coordinate p, WB_MutableCoordinate result);

    /**
     * Map 3D point to 2D space.
     *
     * @param x 
     * @param y 
     * @param z 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void pointTo2D(double x, double y, double z,
	    WB_MutableCoordinate result);

    /**
     * Map 2D point to 3D space.
     *
     * @param p 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void pointTo3D(WB_Coordinate p, WB_MutableCoordinate result);

    /**
     * Map 2D point to 3D space.
     *
     * @param u 
     * @param v 
     * @param w 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void pointTo3D(double u, double v, double w,
	    WB_MutableCoordinate result);

    /**
     * Map 2D point to 3D space.
     *
     * @param u 
     * @param v 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void pointTo3D(double u, double v, WB_MutableCoordinate result);

    /**
     * Map 3D vector to 2D space.
     *
     * @param p 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void vectorTo2D(WB_Coordinate p, WB_MutableCoordinate result);

    /**
     * Map 3D vector to 2D space.
     *
     * @param x 
     * @param y 
     * @param z 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void vectorTo2D(double x, double y, double z,
	    WB_MutableCoordinate result);

    /**
     * Map 2D vector to 3D space.
     *
     * @param p 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void vectorTo3D(WB_Coordinate p, WB_MutableCoordinate result);

    /**
     * Map 2D vector to 3D space.
     *
     * @param u 
     * @param v 
     * @param w 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void vectorTo3D(double u, double v, double w,
	    WB_MutableCoordinate result);

    /**
     * Map 2D vector to 3D space.
     *
     * @param u 
     * @param v 
     * @param result            object implementing the WB_MutableCoordinate interface to
     *            receive the result;
     */
    public void vectorTo3D(double u, double v, WB_MutableCoordinate result);
}
