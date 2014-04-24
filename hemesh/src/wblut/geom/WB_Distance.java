package wblut.geom;

public class WB_Distance {
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	/**
	 * Squared 2D Distance between 2 points.
	 * 
	 * @param p
	 * @param q
	 * @return squared distance
	 */

	public static double sqDistanceToPoint2D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()));
	}

	/**
	 * 2D Distance between 2 points.
	 * 
	 * @param p
	 * @param q
	 * @return distance
	 */
	public static double distanceToPoint2D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(sqDistanceToPoint2D(p, q));
	}

	/**
	 * Squared 3D Distance from point to segment.
	 * 
	 * @param p
	 * @param S
	 * @return squared distance
	 */
	public static double sqDistanceToSegment3D(final WB_Coordinate p,
			final WB_Segment S) {
		final WB_Point ab = geometryfactory.createPoint(S.getEndpoint()).sub(
				S.getOrigin());
		final WB_Point ac = geometryfactory.createPoint(p).sub(S.getOrigin());
		final WB_Point bc = geometryfactory.createPoint(p).sub(S.getEndpoint());
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * 3D Distance from point to segment.
	 * 
	 * @param p
	 * @param S
	 * @return distance
	 */
	public static double distanceToSegment3D(final WB_Coordinate p,
			final WB_Segment S) {
		return Math.sqrt(sqDistanceToSegment3D(p, S));
	}

	/**
	 * Squared 3D Distance from point to segment.
	 * 
	 * @param p
	 * @param a
	 *            start of segment
	 * @param b
	 *            endpoint of segment
	 * @return squared distance
	 */
	public static double sqDistanceToSegment3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = geometryfactory.createVector(a, b);
		final WB_Vector ac = geometryfactory.createVector(a, p);
		final WB_Vector bc = geometryfactory.createVector(b, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * 3D Distance from point to segment.
	 * 
	 * @param p
	 * @param a
	 *            start of segment
	 * @param b
	 *            endpoint of segment
	 * @return distance
	 */
	public static double distanceToSegment3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToSegment3D(p, a, b));
	}

	/**
	 * Squared 2D Distance from point to line.
	 * 
	 * @param p
	 * @param L
	 * @return squared distance
	 */
	public static double sqDistanceToLine2D(final WB_Coordinate p,
			final WB_Line L) {
		final WB_Point ab = geometryfactory.createPoint(L.getDirection().xd(),
				L.getDirection().yd());
		final WB_Point ac = geometryfactory.createPoint(p.xd()
				- L.getOrigin().xd(), p.yd() - L.getOrigin().yd());
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double distanceToLine2D(final WB_Coordinate p, final WB_Line L) {
		return Math.sqrt(sqDistanceToLine2D(p, L));
	}

	public static double sqDistanceToLine2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Point ab = geometryfactory.createPoint(b.xd() - a.xd(), b.yd()
				- a.xd());
		final WB_Point ac = geometryfactory.createPoint(p.xd() - a.xd(), p.yd()
				- a.xd());
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double distanceToLine2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToLine2D(p, a, b));
	}

	/**
	 * Squared 3D Distance from point to line.
	 * 
	 * @param p
	 * @param L
	 * @return squared distance
	 */
	public static double sqDistanceToLine3D(final WB_Coordinate p,
			final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = geometryfactory.createVector(L.getOrigin(), p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * 3D Distance from point to line.
	 * 
	 * @param p
	 * @param L
	 * @return distance
	 */
	public static double distanceToLine3D(final WB_Coordinate p, final WB_Line L) {
		return Math.sqrt(sqDistanceToLine3D(p, L));
	}

	/**
	 * Squared 3D Distance from point to line.
	 * 
	 * @param p
	 * @param a
	 *            point on line
	 * @param b
	 *            second point on line
	 * @return squared distance
	 */
	public static double sqDistanceToLine3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = geometryfactory.createVector(a, b);
		final WB_Vector ac = geometryfactory.createVector(a, p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * 3D Distance from point to line.
	 * 
	 * @param p
	 * @param a
	 *            point on line
	 * @param b
	 *            second point on line
	 * @return distance
	 */
	public static double distanceToLine3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToLine3D(p, a, b));
	}

	/**
	 * Squared 3D Distance from point to ray.
	 * 
	 * @param p
	 * @param R
	 * @return squared distance
	 */
	public static double sqDistanceToRay3D(final WB_Coordinate p, final WB_Ray R) {
		final WB_Vector ab = R.getDirection();
		final WB_Vector ac = geometryfactory.createVector(R.getOrigin(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * 3D Distance from point to ray.
	 * 
	 * @param p
	 * @param R
	 * @return distance
	 */
	public static double distanceToRay3D(final WB_Coordinate p, final WB_Ray R) {
		return Math.sqrt(sqDistanceToRay3D(p, R));
	}

	/**
	 * Squared 3D Distance from point to ray.
	 * 
	 * @param p
	 * @param a
	 *            origin of ray
	 * @param b
	 *            point on ray
	 * @return squared distance
	 */
	public static double sqDistanceToRay3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = geometryfactory.createVector(a, b);
		final WB_Vector ac = geometryfactory.createVector(a, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * 3D Distance from point to ray.
	 * 
	 * @param p
	 * @param a
	 *            origin of ray
	 * @param b
	 *            point on ray
	 * @return distance
	 */
	public static double distanceToRay3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToRay3D(p, a, b));
	}

	/**
	 * Squared 3D Distance between 2 points.
	 * 
	 * @param p
	 * @param q
	 * @return squared distance
	 */

	public static double sqDistanceToPoint3D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()) + (q.zd() - p.zd()) * (q.zd() - p.zd()));
	}

	/**
	 * Squared 3D Distance between 2 points.
	 * 
	 * @param p
	 * @param q
	 * @return distance
	 */
	public static double distanceToPoint3D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(sqDistanceToPoint3D(p, q));
	}

	public static double distanceToPlane3D(final WB_Coordinate p,
			final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return (d < 0) ? -d : d;
	}

	public static double signedDistanceToPlane3D(final WB_Coordinate p,
			final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d;
	}

	public static double sqDistanceToPlane3D(final WB_Coordinate p,
			final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}

}
