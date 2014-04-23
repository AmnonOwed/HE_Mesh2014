package wblut.geom;

import java.util.List;

import wblut.WB_Epsilon;

public class WB_Distance3D {
	// POINT-POINT

	public static double sqDistance(final WB_Coordinate p, final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()) + (q.zd() - p.zd()) * (q.zd() - p.zd()));
	}

	public static double distance(final WB_Coordinate p, final WB_Coordinate q) {
		return Math.sqrt(sqDistance(p, q));
	}

	// POINT-PLANE

	public static double sqDistance(final WB_Coordinate p, final WB_Plane P) {
		final double d = P.getNormal().dot(p) - P.d();
		return d * d;
	}

	public static double distance(final WB_Coordinate p, final WB_Plane P) {
		return P.getNormal().dot(p) - P.d();
	}

	// POINT-SEGMENT

	public static double sqDistance(final WB_Coordinate p, final Segment S) {
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

	public static double distance(final WB_Coordinate p, final Segment S) {
		return Math.sqrt(sqDistance(p, S));
	}

	public static double sqDistanceToSegment(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final WB_Vector bc = new WB_Vector(b, p);
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

	public static double distanceToSegment(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToSegment(p, a, b));
	}

	// POINT-LINE

	public static double sqDistance(final WB_Coordinate p, final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double distance(final WB_Coordinate p, final WB_Line L) {
		return Math.sqrt(sqDistance(p, L));
	}

	public static double sqDistanceToLine(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double distanceToLine(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToLine(p, a, b));
	}

	// POINT-RAY

	public static double sqDistance(final WB_Coordinate p, final WB_Ray R) {
		final WB_Vector ab = R.getDirection();
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double distance(final WB_Coordinate p, final WB_Ray R) {
		return Math.sqrt(sqDistance(p, R));
	}

	public static double sqDistanceToRay(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double distanceToRay(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(sqDistanceToRay(p, a, b));
	}

	// POINT-AABB

	public static double sqDistance(final WB_Coordinate p, final WB_AABB AABB) {
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

	public static double distance(final WB_Coordinate p, final WB_AABB AABB) {
		return Math.sqrt(sqDistance(p, AABB));
	}

	public static double sqDistance(final WB_Coordinate p,
			final SimplePolygon poly) {
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Coordinate tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = WB_Intersection.getClosestPoint(p, T);
			final double d2 = WB_Distance3D.distance(tmp, p);
			if (d2 < dmax2) {
				dmax2 = d2;
				if (WB_Epsilon.isZeroSq(dmax2)) {
					break;
				}
			}

		}

		return dmax2;
	}

	public static double distance(final WB_Coordinate p,
			final SimplePolygon poly) {
		return Math.sqrt(sqDistance(p, poly));
	}

	public static double sqDistance(final Segment S, final Segment T) {
		return WB_Intersection.getIntersection(S, T).sqDist;
	}

	public static double distance(final Segment S, final Segment T) {
		return Math.sqrt(WB_Intersection.getIntersection(S, T).sqDist);
	}

}
