package wblut.geom;


public interface Segment extends WB_Simplex {

	/**
	 * Get point along segment at distance t of origin. Return result as a new
	 * point
	 * 
	 * @param t
	 *            distance from origin, unbounded
	 * @return point along segment at distance t of origin
	 */
	public WB_Point getPointOnLine(final double t);

	/**
	 * Get point along segment at distance t of origin. Store result in provided
	 * WB_Mutable_Coordinate (e.g. a WB_Point).
	 * 
	 * @param t
	 *            distance from origin, unbounded
	 * @param result
	 *            implementation of WB_MutableCoordinate to store result in
	 * @return point along segment at distance t of origin
	 */
	public void getPointOnLineInto(final double t,
			final WB_MutableCoordinate result);

	/**
	 * Get point along segment at fraction t of length. Return result as a new
	 * point
	 * 
	 * @param t
	 *            fraction of segment length from origin, clamped to range [0,1]
	 * @return point along segment at distance t of origin
	 */
	public WB_Point getParametricPointOnSegment(final double t);

	/**
	 * Get point along segment at fraction t of length. Store result in provided
	 * WB_Mutable_Coordinate (e.g. a WB_Point).
	 * 
	 * @param t
	 *            fraction of segment length from origin, clamped to range [0,1]
	 * @param result
	 *            implementation of WB_MutableCoordinate to store result in
	 * @return point along segment at distance t of origin
	 */
	public void getParametricPointOnSegmentInto(final double t,
			final WB_MutableCoordinate result);

	/**
	 * 
	 * 
	 * @return origin of segment
	 */
	public WB_Point getOrigin();

	public WB_Point getEndpoint();

	public WB_Point getCenter();

	public WB_Vector getDirection();

	public double getLength();

	public void reverse();

}