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
	 * Set x
	 * 
	 * @param x
	 */
	public void _setX(double x);

	/**
	 * Set y
	 * 
	 * @param y
	 */
	public void _setY(double y);

	/**
	 * Set z
	 * 
	 * @param z
	 */
	public void _setZ(double z);

	/**
	 * Set w
	 * 
	 * @param w
	 */
	public void _setW(double w);

	/**
	 * Set i'th ordinate
	 * 
	 * @param i
	 *            ordinate to set
	 * @param v
	 *            value
	 */
	public void _setCoord(int i, double v);

	/**
	 * Set to value of tuple
	 * 
	 * @param p
	 *            tuple to copy
	 */
	public void _set(WB_Coordinate p);

	/**
	 * Set to coordinates
	 * 
	 * @param x
	 *            x-ordinate
	 * @param y
	 *            y-ordinate
	 */
	public void _set(double x, double y);

	/**
	 * Set to coordinates
	 * 
	 * @param x
	 *            x-ordinate
	 * @param y
	 *            y-ordinate
	 * @param z
	 *            z-ordinate
	 */
	public void _set(double x, double y, double z);

	/**
	 * Set to coordinates
	 * 
	 * @param x
	 *            x-ordinate
	 * @param y
	 *            y-ordinate
	 * @param z
	 *            z-ordinate
	 * @param w
	 *            w-ordinate
	 */
	public void _set(double x, double y, double z, double w);

}
