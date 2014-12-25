package wblut.geom;

import wblut.math.WB_Epsilon;

public class WB_Distance {
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public static double distanceToLine2D(final WB_Coordinate p, final WB_Line L) {
		return Math.sqrt(getSqDistanceToLine2D(p, L));
	}

	public static double getDistance2D(final double[] p, final double[] q) {
		return Math.sqrt(getSqDistance2D(p, q));
	}

	public static double getDistance2D(final WB_Coordinate p, final Segment S) {
		return Math.sqrt(getSqDistance2D(p, S));
	}

	public static double getDistance2D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(getSqDistance2D(p, q));
	}

	public static double getDistance2D(final WB_Coordinate p, final WB_Line L) {
		return Math.sqrt(getSqDistance2D(p, L));
	}

	public static double getDistance2D(final WB_Coordinate p, final WB_Ray R) {
		return Math.sqrt(getSqDistance2D(p, R));
	}

	public static double getDistance3D(final Segment S, final Segment T) {
		return Math.sqrt(WB_Intersection.getIntersection3D(S, T).sqDist);
	}

	public static double getDistance3D(final WB_Coordinate p, final Segment S) {
		return Math.sqrt(getSqDistance3D(p, S));
	}

	public static double getDistance3D(final WB_Coordinate p,
			final WB_Polygon poly) {
		return Math.sqrt(getSqDistance3D(p, poly));
	}

	public static double getDistance3D(final WB_Coordinate p, final WB_AABB AABB) {
		return Math.sqrt(getSqDistance3D(p, AABB));
	}

	public static double getDistance3D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(getSqDistance3D(p, q));
	}

	public static double getDistance3D(final WB_Coordinate p, final WB_Line L) {
		return Math.sqrt(getSqDistance3D(p, L));
	}

	public static double getDistance3D(final WB_Coordinate p, final WB_Plane P) {
		return P.getNormal().dot(p) - P.d();
	}

	public static double getDistance3D(final double p[], final WB_Plane P) {
		final WB_Vector n = P.getNormal();
		return n.xd() * p[0] + n.yd() * p[1] + n.zd() * p[2] - P.d();
	}

	public static double getDistance3D(final WB_Coordinate p, final WB_Ray R) {
		return Math.sqrt(getSqDistance3D(p, R));
	}

	public static double getDistanceToLine2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToLine2D(p, a, b));
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
	public static double getDistanceToLine3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToLine3D(p, a, b));
	}

	/**
	 * 3D Distance from point to line.
	 *
	 * @param p
	 * @param L
	 * @return distance
	 */
	public static double getDistanceToLine3D(final WB_Coordinate p,
			final WB_Line L) {
		return Math.sqrt(getSqDistanceToLine3D(p, L));
	}

	public static double getDistanceToPlane3D(final WB_Coordinate p,
			final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return (d < 0) ? -d : d;
	}

	public static double getDistanceToPlane3D(final double[] p, final WB_Plane P) {
		final WB_Vector v = P.getNormal();
		final double d = v.xd() * p[0] + v.yd() * p[1] + v.zd() * p[2] - P.d();
		return (d < 0) ? -d : d;
	}

	/**
	 * 2D Distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return distance
	 */
	public static double getDistanceToPoint2D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(getSqDistanceToPoint2D(p, q));
	}

	/**
	 * Squared 3D Distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return distance
	 */
	public static double getDistanceToPoint3D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(getSqDistanceToPoint3D(p, q));
	}

	public static double getDistanceToRay2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToRay2D(p, a, b));
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
	public static double getDistanceToRay3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToRay3D(p, a, b));
	}

	/**
	 * 3D Distance from point to ray.
	 *
	 * @param p
	 * @param R
	 * @return distance
	 */
	public static double getDistanceToRay3D(final WB_Coordinate p,
			final WB_Ray R) {
		return Math.sqrt(getSqDistanceToRay3D(p, R));
	}

	public static double getDistanceToSegment2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToSegment2D(p, a, b));
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
	public static double getDistanceToSegment3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToSegment3D(p, a, b));
	}

	/**
	 * 3D Distance from point to segment.
	 *
	 * @param p
	 * @param S
	 * @return distance
	 */
	public static double getDistanceToSegment3D(final WB_Coordinate p,
			final WB_Segment S) {
		return Math.sqrt(getSqDistanceToSegment3D(p, S));
	}

	public static double getSqDistance2D(final double[] p, final double[] q) {
		return ((q[0] - p[0]) * (q[0] - p[0]) + (q[1] - p[1]) * (q[1] - p[1]));
	}

	public static double getSqDistance2D(final WB_Coordinate p, final Segment S) {
		final WB_Vector ab = new WB_Vector(S.getOrigin(), S.getEndpoint());
		final WB_Vector ac = new WB_Vector(p).sub(S.getOrigin());
		final WB_Vector bc = new WB_Vector(p).sub(S.getEndpoint());
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		if (e >= f) {
			return bc.dot2D(bc);
		}
		return ac.dot2D(ac) - e * e / f;
	}

	public static double getSqDistance2D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()));
	}

	public static double getSqDistance2D(final WB_Coordinate p, final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
		final double e = ac.dot2D(ab);
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - e * e / f;
	}

	public static double getSqDistance2D(final WB_Coordinate p, final WB_Ray R) {
		final WB_Vector ab = R.getDirection();
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - e * e / f;
	}

	public static double getSqDistance3D(final Segment S, final Segment T) {
		return WB_Intersection.getIntersection3D(S, T).sqDist;
	}

	public static double getSqDistance3D(final WB_Coordinate p, final Segment S) {
		final WB_Vector ab = S.getEndpoint().subToVector(S.getOrigin());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		final WB_Vector bc = new WB_Vector(S.getEndpoint(), p);
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

	public static double getSqDistance3D(final WB_Coordinate p,
			final WB_Polygon poly) {
		final int[][] tris = poly.getTriangles();
		final int n = tris.length;
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Coordinate tmp;
		int[] T;
		for (int i = 0; i < n; i++) {
			T = tris[i];
			tmp = WB_Intersection.getClosestPointToTriangle3D(p,
					poly.getPoint(T[0]), poly.getPoint(T[1]),
					poly.getPoint(T[2]));
			final double d2 = WB_Distance.getDistance3D(tmp, p);
			if (d2 < dmax2) {
				dmax2 = d2;
				if (WB_Epsilon.isZeroSq(dmax2)) {
					break;
				}
			}

		}

		return dmax2;
	}

	public static double getSqDistance3D(final WB_Coordinate p,
			final WB_AABB AABB) {
		double sqDist = 0;
		double v = p.xd();
		if (v < AABB.getMinX()) {
			sqDist += (AABB.getMinX() - v) * (AABB.getMinX() - v);
		}
		if (v > AABB.getMaxX()) {
			sqDist += (v - AABB.getMaxX()) * (v - AABB.getMaxX());
		}
		v = p.yd();
		if (v < AABB.getMinY()) {
			sqDist += (AABB.getMinY() - v) * (AABB.getMinY() - v);
		}
		if (v > AABB.getMaxY()) {
			sqDist += (v - AABB.getMaxY()) * (v - AABB.getMaxY());
		}
		v = p.zd();
		if (v < AABB.getMinZ()) {
			sqDist += (AABB.getMinZ() - v) * (AABB.getMinZ() - v);
		}
		if (v > AABB.getMaxZ()) {
			sqDist += (v - AABB.getMaxZ()) * (v - AABB.getMaxZ());
		}
		return sqDist;
	}

	// POINT-POINT

	public static double getSqDistance3D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()) + (q.zd() - p.zd()) * (q.zd() - p.zd()));
	}

	public static double getSqDistance3D(final WB_Coordinate p, final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	// POINT-PLANE

	public static double getSqDistance3D(final WB_Coordinate p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}

	public static double getSqDistance3D(final WB_Coordinate p, final WB_Ray R) {
		final WB_Vector ab = R.getDirection();
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	// POINT-SEGMENT

	public static double getSqDistanceToLine2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot2D(ab);
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - e * e / f;
	}

	/**
	 * Squared 2D Distance from point to line.
	 *
	 * @param p
	 * @param L
	 * @return squared distance
	 */
	public static double getSqDistanceToLine2D(final WB_Coordinate p,
			final WB_Line L) {
		final WB_Point ab = geometryfactory.createPoint(L.getDirection().xd(),
				L.getDirection().yd());
		final WB_Point ac = geometryfactory.createPoint(p.xd()
				- L.getOrigin().xd(), p.yd() - L.getOrigin().yd());
		final double e = ac.dot2D(ab);
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - e * e / f;
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

	public static double getSqDistanceToLine3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	/**
	 * Squared 3D Distance from point to line.
	 *
	 * @param p
	 * @param L
	 * @return squared distance
	 */
	public static double getSqDistanceToLine3D(final WB_Coordinate p,
			final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = geometryfactory.createVectorFromTo(L.getOrigin(),
				p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double getSqDistanceToPlane3D(final WB_Coordinate p,
			final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}

	/**
	 * Squared 2D Distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return squared distance
	 */

	public static double getSqDistanceToPoint2D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()));
	}

	// POINT-RAY

	/**
	 * Squared 3D Distance between 2 points.
	 *
	 * @param p
	 * @param q
	 * @return squared distance
	 */

	public static double getSqDistanceToPoint3D(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()) + (q.zd() - p.zd()) * (q.zd() - p.zd()));
	}

	public static double getSqDistanceToRay2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		return ac.dot2D(ac) - e * e / f;
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
	public static double getSqDistanceToRay3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = geometryfactory.createVectorFromTo(a, b);
		final WB_Vector ac = geometryfactory.createVectorFromTo(a, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	// POINT-AABB

	/**
	 * Squared 3D Distance from point to ray.
	 *
	 * @param p
	 * @param R
	 * @return squared distance
	 */
	public static double getSqDistanceToRay3D(final WB_Coordinate p,
			final WB_Ray R) {
		final WB_Vector ab = R.getDirection();
		final WB_Vector ac = geometryfactory.createVectorFromTo(R.getOrigin(),
				p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double getSqDistanceToSegment2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final WB_Vector bc = new WB_Vector(b, p);
		final double e = ac.dot2D(ab);
		if (e <= 0) {
			return ac.dot2D(ac);
		}
		final double f = ab.dot2D(ab);
		if (e >= f) {
			return bc.dot2D(bc);
		}
		return ac.dot2D(ac) - e * e / f;
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
	public static double getSqDistanceToSegment3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = geometryfactory.createVectorFromTo(a, b);
		final WB_Vector ac = geometryfactory.createVectorFromTo(a, p);
		final WB_Vector bc = geometryfactory.createVectorFromTo(b, p);
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
	 * Squared 3D Distance from point to segment.
	 *
	 * @param p
	 * @param S
	 * @return squared distance
	 */
	public static double getSqDistanceToSegment3D(final WB_Coordinate p,
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

	public static double signedDistanceToPlane3D(final WB_Coordinate p,
			final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d;
	}

}
