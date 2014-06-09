package wblut.geom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.geom.interfaces.Segment;
import wblut.geom.interfaces.SimplePolygon;
import wblut.geom.interfaces.Triangle;
import wblut.math.WB_Math;

public class WB_Intersection {
	private static final WB_GeometryFactory factory = WB_GeometryFactory
			.instance();

	public static WB_IntersectionResult getIntersection3D(final Segment S,
			final WB_Plane P) {
		final WB_Vector ab = S.getEndpoint().subToVector(S.getOrigin());
		double t = (P.d() - P.getNormal().dot(S.getOrigin()))
				/ P.getNormal().dot(ab);
		if (t >= -WB_Epsilon.EPSILON && t <= 1.0 + WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, 1);

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = S.getParametricPointOnSegment(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = t;
		i.t2 = t;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	public static WB_IntersectionResult getIntersection3D(
			final WB_Coordinate a, final WB_Coordinate b, final WB_Plane P) {
		final WB_Vector ab = new WB_Vector(a, b);
		double t = (P.d() - P.getNormal().dot(a)) / P.getNormal().dot(ab);
		if (t >= -WB_Epsilon.EPSILON && t <= 1.0 + WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, 1);

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = new WB_Point(a.xd() + t * (b.xd() - a.xd()), a.yd() + t
					* (b.yd() - a.yd()), a.zd() + t * (b.zd() - a.zd()));
			i.dimension = 0;
			i.sqDist = 0;
			return i;

		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = 0;
		i.t2 = 0;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	// RAY-PLANE

	public static WB_IntersectionResult getIntersection3D(final WB_Ray R,
			final WB_Plane P) {
		final WB_Vector ab = R.getDirection();
		double t = (P.d() - P.getNormal().dot(R.getOrigin()))
				/ P.getNormal().dot(ab);

		if (t >= -WB_Epsilon.EPSILON) {
			t = WB_Epsilon.clampEpsilon(t, 0, Double.POSITIVE_INFINITY);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = R.getPointOnLine(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = t;
		i.t2 = t;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	public static WB_IntersectionResult getIntersection3D(final WB_Ray R,
			final WB_AABB aabb) {
		final WB_Vector d = R.getDirection();
		final WB_Point p = R.getOrigin();
		double tmin = 0.0;
		double tmax = Double.POSITIVE_INFINITY;
		if (WB_Epsilon.isZero(d.xd())) {
			if ((p.xd() < aabb.getMinX()) || (p.xd() > aabb.getMaxX())) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}
		} else {
			final double ood = 1.0 / d.xd();
			double t1 = (aabb.getMinX() - p.xd()) * ood;
			double t2 = (aabb.getMaxX() - p.xd()) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}

		}
		if (WB_Epsilon.isZero(d.yd())) {
			if ((p.yd() < aabb.getMinY()) || (p.yd() > aabb.getMaxY())) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}
		} else {
			final double ood = 1.0 / d.yd();
			double t1 = (aabb.getMinY() - p.yd()) * ood;
			double t2 = (aabb.getMaxY() - p.yd()) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}

		}
		if (WB_Epsilon.isZero(d.zd())) {
			if ((p.zd() < aabb.getMinZ()) || (p.zd() > aabb.getMaxZ())) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}
		} else {
			final double ood = 1.0 / d.zd();
			double t1 = (aabb.getMinZ() - p.zd()) * ood;
			double t2 = (aabb.getMaxZ() - p.zd()) * ood;
			if (t1 > t2) {
				final double tmp = t1;
				t1 = t2;
				t2 = tmp;
			}
			tmin = Math.max(tmin, t1);
			tmax = Math.min(tmax, t2);
			if (tmin > tmax) {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = false;
				i.t1 = 0;
				i.t2 = 0;
				i.sqDist = Double.POSITIVE_INFINITY;
				return i;
			}

		}

		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = tmin;
		i.t2 = 0;
		i.object = R.getPointOnLine(tmin);
		i.dimension = 0;
		i.sqDist = WB_Distance.getSqDistance3D(p, (WB_Point) i.object);

		return i;
	}

	// LINE-PLANE

	public static WB_IntersectionResult getIntersection3D(final WB_Line L,
			final WB_Plane P) {
		final WB_Vector ab = L.getDirection();
		final double denom = P.getNormal().dot(ab);
		if (!WB_Epsilon.isZero(denom)) {
			final double t = (P.d() - P.getNormal().dot(L.getOrigin())) / denom;

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = t;
			i.t2 = t;
			i.object = L.getPointOnLine(t);
			i.dimension = 0;
			i.sqDist = 0;
			return i;
		} else {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
			return i;
		}
	}

	// PLANE-PLANE

	public static WB_IntersectionResult getIntersection3D(final WB_Plane P1,
			final WB_Plane P2) {

		final WB_Vector N1 = P1.getNormal().get();
		final WB_Vector N2 = P2.getNormal().get();
		final WB_Vector N1xN2 = new WB_Vector(N1.cross(N2));
		final double d1 = P1.d();
		final double d2 = P2.d();
		if (WB_Epsilon.isZeroSq(N1xN2.getSqLength())) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
			return i;
		} else {
			final double N1N2 = N1.dot(N2);
			final double det = 1 - N1N2 * N1N2;
			final double c1 = (d1 - d2 * N1N2) / det;
			final double c2 = (d2 - d1 * N1N2) / det;
			final WB_Point O = new WB_Point(N1._mulSelf(c1)._addSelf(
					N2._mulSelf(c2)));

			final WB_Line L = new WB_Line(O, N1xN2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Line(O, N1xN2);
			i.dimension = 1;
			i.sqDist = 0;
			return i;

		}

	}

	// PLANE-PLANE-PLANE

	public static WB_IntersectionResult getIntersection3D(final WB_Plane P1,
			final WB_Plane P2, final WB_Plane P3) {

		final WB_Vector N1 = P1.getNormal().get();
		final WB_Vector N2 = P2.getNormal().get();
		final WB_Vector N3 = P3.getNormal().get();

		final double denom = N1.dot(N2.cross(N3));

		if (WB_Epsilon.isZero(denom)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
			return i;
		} else {
			final WB_Vector N1xN2 = N1.cross(N2);
			final WB_Vector N2xN3 = N2.cross(N3);
			final WB_Vector N3xN1 = N3.cross(N1);
			final double d1 = P1.d();
			final double d2 = P2.d();
			final double d3 = P3.d();
			final WB_Point p = new WB_Point(N2xN3)._mulSelf(d1);
			p._addSelf(N3xN1.mul(d2));
			p._addSelf(N1xN2.mul(d3));
			p._divSelf(denom);

			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = true;
			i.t1 = 0;
			i.t2 = 0;
			i.object = p;
			i.dimension = 0;
			i.sqDist = 0;
			return i;

		}

	}

	// AABB-AABB

	public static boolean checkIntersection3D(final WB_AABB one,
			final WB_AABB other) {
		if (one.getMaxX() < other.getMinX() || one.getMinX() > other.getMaxX()) {
			return false;
		}
		if (one.getMaxY() < other.getMinY() || one.getMinY() > other.getMaxY()) {
			return false;
		}
		if (one.getMaxZ() < other.getMinZ() || one.getMinZ() > other.getMaxZ()) {
			return false;
		}
		return true;
	}

	// OBB-OBB

	public static boolean checkIntersection3D(final WB_AABB AABB,
			final WB_Plane P) {
		final WB_Point c = AABB.getMax().add(AABB.getMin())._mulSelf(0.5);
		final WB_Point e = AABB.getMax().sub(c);
		final double r = e.xd() * WB_Math.fastAbs(P.getNormal().xd()) + e.yd()
				* WB_Math.fastAbs(P.getNormal().yd()) + e.zd()
				* WB_Math.fastAbs(P.getNormal().zd());
		final double s = P.getNormal().dot(c) - P.d();
		return WB_Math.fastAbs(s) <= r;
	}

	// OBB-PLANE

	public static boolean checkIntersection3D(final WB_AABB AABB,
			final WB_Sphere S) {
		final double d2 = WB_Distance.getSqDistance3D(S.getCenter(), AABB);
		return d2 <= S.getRadius() * S.getRadius();
	}

	// OBB-SPHERE

	public static boolean checkIntersection3D(final Triangle T,
			final WB_Sphere S) {
		final WB_Point p = getClosestPoint3D(S.getCenter(), T);

		return (p.subToVector(S.getCenter())).getSqLength() <= S.getRadius()
				* S.getRadius();
	}

	// TRIANGLE-AABB

	public static boolean checkIntersection3D(final Triangle T,
			final WB_AABB AABB) {
		double p0, p1, p2, r;
		final WB_Point c = AABB.getMax().add(AABB.getMin())._mulSelf(0.5);
		final double e0 = (AABB.getMaxX() - AABB.getMinX()) * 0.5;
		final double e1 = (AABB.getMaxY() - AABB.getMinY()) * 0.5;
		final double e2 = (AABB.getMaxZ() - AABB.getMinZ()) * 0.5;
		final WB_Point v0 = T.p1().get();
		final WB_Point v1 = T.p2().get();
		final WB_Point v2 = T.p3().get();

		v0._subSelf(c);
		v1._subSelf(c);
		v2._subSelf(c);

		final WB_Vector f0 = v1.subToVector(v0);
		final WB_Vector f1 = v2.subToVector(v1);
		final WB_Vector f2 = v0.subToVector(v2);

		// a00
		final WB_Vector a = new WB_Vector(0, -f0.zd(), f0.yd());// u0xf0
		if (a.isZero()) {
			a._set(0, v0.yd(), v0.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a01
		a._set(0, -f1.zd(), f1.yd());// u0xf1
		if (a.isZero()) {
			a._set(0, v1.yd(), v1.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a02
		a._set(0, -f2.zd(), f2.yd());// u0xf2
		if (a.isZero()) {
			a._set(0, v2.yd(), v2.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a10
		a._set(f0.zd(), 0, -f0.xd());// u1xf0
		if (a.isZero()) {
			a._set(v0.xd(), 0, v0.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a11
		a._set(f1.zd(), 0, -f1.xd());// u1xf1
		if (a.isZero()) {
			a._set(v1.xd(), 0, v1.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a12
		a._set(f2.zd(), 0, -f2.xd());// u1xf2
		if (a.isZero()) {
			a._set(v2.xd(), 0, v2.zd());
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a20
		a._set(-f0.yd(), f0.xd(), 0);// u2xf0
		if (a.isZero()) {
			a._set(v0.xd(), v0.yd(), 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}
		// a21
		a._set(-f1.yd(), f1.xd(), 0);// u2xf1
		if (a.isZero()) {
			a._set(v1.xd(), v1.yd(), 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		// a22
		a._set(-f2.yd(), f2.xd(), 0);// u2xf2
		if (a.isZero()) {
			a._set(v2.xd(), v2.yd(), 0);
		}
		if (!a.isZero()) {
			p0 = v0.dot(a);
			p1 = v1.dot(a);
			p2 = v2.dot(a);
			r = e0 * WB_Math.fastAbs(a.xd()) + e1 * WB_Math.fastAbs(a.yd())
					+ e2 * WB_Math.fastAbs(a.zd());
			if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
				return false;
			}
		}

		if (WB_Math.max(v0.xd(), v1.xd(), v2.xd()) < -e0
				|| WB_Math.max(v0.xd(), v1.xd(), v2.xd()) > e0) {
			return false;
		}
		if (WB_Math.max(v0.yd(), v1.yd(), v2.yd()) < -e1
				|| WB_Math.max(v0.yd(), v1.yd(), v2.yd()) > e1) {
			return false;
		}
		if (WB_Math.max(v0.zd(), v1.zd(), v2.zd()) < -e2
				|| WB_Math.max(v0.zd(), v1.zd(), v2.zd()) > e2) {
			return false;
		}

		WB_Vector n = f0.cross(f1);
		WB_Plane P;
		if (!n.isZero()) {
			P = new WB_Plane(n, n.dot(v0));
		} else {
			n = f0.cross(f2);
			n = f0.cross(n);
			if (!n.isZero()) {
				P = new WB_Plane(n, n.dot(v0));
			} else {
				final WB_Vector t = T.p3().subToVector(T.p1());
				final double a1 = T.p1().dot(t);
				final double a2 = T.p2().dot(t);
				final double a3 = T.p3().dot(t);
				if (a1 < WB_Math.min(a2, a3)) {
					if (a2 < a3) {
						return checkIntersection3D(
								new WB_Segment(T.p1(), T.p3()), AABB);
					} else {
						return checkIntersection3D(
								new WB_Segment(T.p1(), T.p2()), AABB);
					}
				} else if (a2 < WB_Math.min(a1, a3)) {
					if (a1 < a3) {
						return checkIntersection3D(
								new WB_Segment(T.p2(), T.p3()), AABB);
					} else {
						return checkIntersection3D(
								new WB_Segment(T.p2(), T.p1()), AABB);
					}
				} else {
					if (a1 < a2) {
						return checkIntersection3D(
								new WB_Segment(T.p3(), T.p2()), AABB);
					} else {
						return checkIntersection3D(
								new WB_Segment(T.p3(), T.p1()), AABB);
					}
				}

			}

		}
		return checkIntersection3D(AABB, P);

	}

	// SEGMENT-AABB

	public static boolean checkIntersection3D(final Segment S,
			final WB_AABB AABB) {
		final WB_Vector e = AABB.getMax().subToVector(AABB.getMin());
		final WB_Vector d = S.getEndpoint().subToVector(S.getOrigin());
		final WB_Point m = new WB_Point(S.getEndpoint().xd()
				+ S.getOrigin().xd() - AABB.getMinX() - AABB.getMaxX(), S
				.getEndpoint().yd()
				+ S.getOrigin().yd()
				- AABB.getMinY()
				- AABB.getMaxY(), S.getEndpoint().zd() + S.getOrigin().zd()
				- AABB.getMinZ() - AABB.getMaxZ());
		double adx = WB_Math.fastAbs(d.xd());
		if (WB_Math.fastAbs(m.xd()) > e.xd() + adx) {
			return false;
		}
		double ady = WB_Math.fastAbs(d.yd());
		if (WB_Math.fastAbs(m.yd()) > e.yd() + ady) {
			return false;
		}
		double adz = WB_Math.fastAbs(d.zd());
		if (WB_Math.fastAbs(m.zd()) > e.zd() + adz) {
			return false;
		}
		adx += WB_Epsilon.EPSILON;
		ady += WB_Epsilon.EPSILON;
		adz += WB_Epsilon.EPSILON;
		if (WB_Math.fastAbs(m.yd() * d.zd() - m.zd() * d.yd()) > e.yd() * adz
				+ e.zd() * ady) {
			return false;
		}
		if (WB_Math.fastAbs(m.zd() * d.xd() - m.xd() * d.zd()) > e.xd() * adz
				+ e.zd() * adx) {
			return false;
		}
		if (WB_Math.fastAbs(m.xd() * d.yd() - m.yd() * d.xd()) > e.xd() * ady
				+ e.yd() * adx) {
			return false;
		}
		return true;

	}

	// SPHERE-SPHERE

	public static boolean checkIntersection3D(final WB_Sphere S1,
			final WB_Sphere S2) {
		final WB_Vector d = S1.getCenter().subToVector(S2.getCenter());
		final double d2 = d.getSqLength();
		final double radiusSum = S1.getRadius() + S2.getRadius();
		return d2 <= radiusSum * radiusSum;
	}

	// RAY-SPHERE

	public static boolean checkIntersection3D(final WB_Ray R, final WB_Sphere S) {
		final WB_Vector m = R.getOrigin().subToVector(S.getCenter());
		final double c = m.dot(m) - S.getRadius() * S.getRadius();
		if (c <= 0) {
			return true;
		}
		final double b = m.dot(R.getDirection());
		if (b >= 0) {
			return false;
		}
		final double disc = b * b - c;
		if (disc < 0) {
			return false;
		}
		return true;
	}

	public static boolean checkIntersection3D(final WB_Ray R, final WB_AABB AABB) {
		double t0 = 0;
		double t1 = Double.POSITIVE_INFINITY;
		final double irx = 1.0 / R.direction.xd();
		double tnear = (AABB.getMinX() - R.origin.xd()) * irx;
		double tfar = (AABB.getMaxX() - R.origin.xd()) * irx;
		double tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double iry = 1.0 / R.direction.yd();
		tnear = (AABB.getMinY() - R.origin.yd()) * iry;
		tfar = (AABB.getMaxY() - R.origin.yd()) * iry;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double irz = 1.0 / R.direction.zd();
		tnear = (AABB.getMinZ() - R.origin.zd()) * irz;
		tfar = (AABB.getMaxZ() - R.origin.zd()) * irz;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		return true;
	}

	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Ray R,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(R, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static boolean checkIntersection3D(final WB_Line L,
			final WB_AABB AABB) {
		double t0 = Double.NEGATIVE_INFINITY;
		double t1 = Double.POSITIVE_INFINITY;
		final double irx = 1.0 / L.direction.xd();
		double tnear = (AABB.getMinX() - L.origin.xd()) * irx;
		double tfar = (AABB.getMaxX() - L.origin.xd()) * irx;
		double tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double iry = 1.0 / L.direction.yd();
		tnear = (AABB.getMinY() - L.origin.yd()) * iry;
		tfar = (AABB.getMaxY() - L.origin.yd()) * iry;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		final double irz = 1.0 / L.direction.zd();
		tnear = (AABB.getMinZ() - L.origin.zd()) * irz;
		tfar = (AABB.getMaxZ() - L.origin.zd()) * irz;
		tmp = tnear;
		if (tnear > tfar) {
			tnear = tfar;
			tfar = tmp;
		}
		t0 = (tnear > t0) ? tnear : t0;
		t1 = (tfar < t1) ? tfar : t1;
		if (t0 > t1) {
			return false;
		}
		return true;
	}

	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Line L,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(L, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static ArrayList<WB_AABBNode> getIntersection3D(final Segment S,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(S, current.getAABB())) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Plane P,
			final WB_AABBTree tree) {
		final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (checkIntersection3D(current.getAABB(), P)) {
				if (current.isLeaf()) {
					result.add(current);
				} else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return result;
	}

	public static ArrayList<WB_Segment> getIntersection3D(
			final SimplePolygon poly, final WB_Plane P) {

		final WB_Classification cptp = P.classifyPolygonToPlane(poly);
		final ArrayList<WB_Segment> result = new ArrayList<WB_Segment>();
		/*
		 * if (cptp == WB_ClassifyPolygonToPlane.POLYGON_ON_PLANE) { return
		 * poly.toSegments(); } if ((cptp ==
		 * WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE) || (cptp ==
		 * WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE)) { return result; }
		 */
		final ArrayList<WB_Point> splitVerts = new ArrayList<WB_Point>();
		final int numVerts = poly.getN();
		if (numVerts > 0) {
			WB_Point a = poly.getPoint(numVerts - 1);
			WB_Classification aSide = P.classifyPointToPlane(a);
			WB_Point b;
			WB_Classification bSide;
			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i;
				b = poly.getPoint(n);
				bSide = P.classifyPointToPlane(b);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = WB_Intersection.getIntersection3D(b, a, P);
						splitVerts.add((WB_Point) i.object);
					}
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = WB_Intersection.getIntersection3D(a, b, P);
						splitVerts.add((WB_Point) i.object);
					}
				}
				if (aSide == WB_Classification.ON) {
					splitVerts.add(a);

				}
				a = b;
				aSide = bSide;

			}
		}

		for (int i = 0; i < splitVerts.size(); i += 2) {
			if (splitVerts.get(i + 1) != null) {
				result.add(new WB_Segment(splitVerts.get(i), splitVerts
						.get(i + 1)));
			}

		}

		return result;

	}

	public static WB_IntersectionResult getIntersection3D(final Segment S1,
			final Segment S2) {
		final WB_Vector d1 = new WB_Vector(S1.getEndpoint());
		d1._subSelf(S1.getOrigin());
		final WB_Vector d2 = new WB_Vector(S2.getEndpoint());
		d2._subSelf(S2.getOrigin());
		final WB_Vector r = new WB_Vector(S1.getOrigin());
		r._subSelf(S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);

		if (WB_Epsilon.isZero(a) && WB_Epsilon.isZero(e)) {
			// Both segments are degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.getSqLength();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S1.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_Segment(S1.getOrigin(), S2.getOrigin());
			}
			return i;
		}

		if (WB_Epsilon.isZero(a)) {
			// First segment is degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.getSqLength();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S1.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_Segment(S1.getOrigin(), getClosestPoint3D(
						S1.getOrigin(), S2));
			}
			return i;
		}

		if (WB_Epsilon.isZero(e)) {
			// Second segment is degenerate
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.sqDist = r.getSqLength();
			i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
			if (i.intersection) {
				i.dimension = 0;
				i.object = S2.getOrigin();
			} else {
				i.dimension = 1;
				i.object = new WB_Segment(S2.getOrigin(), getClosestPoint3D(
						S2.getOrigin(), S1));
			}
			return i;
		}

		double t1 = 0;
		double t2 = 0;
		final double c = d1.dot(r);
		final double b = d1.dot(d2);
		final double denom = a * e - b * b;

		if (!WB_Epsilon.isZero(denom)) {
			// Non-parallel segments
			t1 = WB_Math.clamp((b * f - c * e) / denom, 0, 1);
		} else {
			// Parallel segments, non-parallel code handles case where
			// projections of segments are disjoint.
			final WB_Line L1 = new WB_Line(S1.getOrigin(), S1.getDirection());
			double s1 = 0;
			double e1 = WB_Geom.pointAlongLine(S1.getEndpoint(), L1);
			double s2 = WB_Geom.pointAlongLine(S2.getOrigin(), L1);
			double e2 = WB_Geom.pointAlongLine(S2.getEndpoint(), L1);
			double tmp;
			if (e2 < s2) {
				tmp = s2;
				s2 = e2;
				e2 = tmp;
			}
			if (s2 < s1) {
				tmp = s2;
				s2 = s1;
				s1 = tmp;
				tmp = e2;
				e2 = e1;
				e1 = tmp;
			}

			if (s2 < e1) {
				// Projections are overlapping
				final WB_Point start = L1.getPointOnLine(s2);
				WB_Point end = L1.getPointOnLine(Math.min(e1, e2));

				if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(
						S2.getOrigin(), L1))) {
					// Segments are overlapping
					final WB_IntersectionResult i = new WB_IntersectionResult();
					i.sqDist = WB_Distance.getSqDistance3D(start, end);
					i.intersection = true;
					if (WB_Epsilon.isZeroSq(i.sqDist)) {
						i.dimension = 0;
						i.object = start;
					} else {
						i.dimension = 1;
						i.object = new WB_Segment(start, end);
					}
					return i;
				} else {
					final WB_IntersectionResult i = new WB_IntersectionResult();
					i.sqDist = WB_Distance.getSqDistance3D(start, end);
					i.intersection = false;
					i.dimension = 1;
					start._addSelf(end);
					start._scaleSelf(0.5);
					end = getClosestPoint3D(start, S2);
					i.object = new WB_Segment(start, end);
					return i;
				}
			}
			t1 = 0;
		}
		final double tnom = b * t1 + f;
		if (tnom < 0) {
			t1 = WB_Math.clamp(-c / a, 0, 1);
		} else if (tnom > e) {
			t2 = 1;
			t1 = WB_Math.clamp((b - c) / a, 0, 1);
		} else {
			t2 = tnom / e;
		}

		final WB_IntersectionResult i = new WB_IntersectionResult();
		final WB_Point p1 = S1.getParametricPointOnSegment(t1);
		final WB_Point p2 = S2.getParametricPointOnSegment(t2);
		i.sqDist = WB_Distance.getSqDistance3D(p1, p2);
		i.intersection = WB_Epsilon.isZeroSq(i.sqDist);
		if (i.intersection) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
		}
		return i;

	}

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final WB_Plane P) {
		final WB_Vector n = P.getNormal();
		final double t = n.dot(p) - P.d();
		return new WB_Point(p.xd() - t * n.xd(), p.yd() - t * n.yd(), p.zd()
				- t * n.zd());
	}

	public static WB_Point getClosestPoint3D(final WB_Plane P,
			final WB_Coordinate p) {
		return getClosestPoint3D(P, p);
	}

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final Segment S) {
		final WB_Vector ab = S.getEndpoint().subToVector(S.getOrigin());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return S.getOrigin().get();
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= denom) {
				t = 1;
				return S.getEndpoint().get();
			} else {
				t = t / denom;
				return new WB_Point(S.getParametricPointOnSegment(t));
			}
		}
	}

	public static WB_Point getClosestPoint3D(final Segment S,
			final WB_Coordinate p) {
		return getClosestPoint3D(p, S);
	}

	public static double getClosestPointT3D(final WB_Coordinate p,
			final Segment S) {
		final WB_Vector ab = S.getEndpoint().subToVector(S.getOrigin());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		double t = ac.dot(ab);
		if (t <= WB_Epsilon.EPSILON) {
			return 0;
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= (denom - WB_Epsilon.EPSILON)) {
				t = 1;
				return 1;
			} else {
				t = t / denom;
				return t;
			}
		}
	}

	public static double getClosestPointT3D(final Segment S,
			final WB_Coordinate p) {
		return getClosestPointT3D(p, S);
	}

	public static WB_Point getClosestPointToSegment3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return new WB_Point(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				return new WB_Point(b);
			} else {
				t = t / denom;
				return new WB_Point(a.xd() + t * ab.xd(), a.yd() + t * ab.yd(),
						a.zd() + t * ab.zd());
			}
		}
	}

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final WB_Line L) {
		final WB_Vector ca = new WB_Vector(p.xd() - L.getOrigin().yd(), p.yd()
				- L.getOrigin().xd(), p.zd() - L.getOrigin().zd());
		return L.getPointOnLine(ca.dot(L.getDirection()));
	}

	public static WB_Coordinate getClosestPointToLine3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return getClosestPoint3D(p, new WB_Line(a, b));
	}

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final WB_Ray R) {
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return R.getOrigin().get();
		} else {
			return new WB_Point(R.getPointOnLine(t));
		}
	}

	public static WB_Point getClosestPointToRay3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return getClosestPoint3D(p, new WB_Ray(a, new WB_Vector(a, b)));
	}

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final WB_AABB AABB) {
		final WB_Point result = new WB_Point();
		double v = p.xd();
		if (v < AABB.getMinX()) {
			v = AABB.getMinX();
		}
		if (v > AABB.getMaxX()) {
			v = AABB.getMaxX();
		}
		result._setX(v);
		v = p.yd();
		if (v < AABB.getMinY()) {
			v = AABB.getMinY();
		}
		if (v > AABB.getMaxY()) {
			v = AABB.getMaxY();
		}
		result._setY(v);
		v = p.zd();
		if (v < AABB.getMinZ()) {
			v = AABB.getMinZ();
		}
		if (v > AABB.getMaxZ()) {
			v = AABB.getMaxZ();
		}
		result._setZ(v);

		return result;
	}

	public static void getClosestPoint3D(final WB_Coordinate p,
			final WB_AABB AABB, final WB_MutableCoordinate result) {
		double v = p.xd();
		if (v < AABB.getMinX()) {
			v = AABB.getMinX();
		}
		if (v > AABB.getMaxX()) {
			v = AABB.getMaxX();
		}
		result._setX(v);
		v = p.yd();
		if (v < AABB.getMinY()) {
			v = AABB.getMinY();
		}
		if (v > AABB.getMaxY()) {
			v = AABB.getMaxY();
		}
		result._setY(v);
		v = p.zd();
		if (v < AABB.getMinZ()) {
			v = AABB.getMinZ();
		}
		if (v > AABB.getMaxZ()) {
			v = AABB.getMaxZ();
		}
		result._setZ(v);
	}

	// POINT-TRIANGLE

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final Triangle T) {
		final WB_Vector ab = T.p2().subToVector(T.p1());
		final WB_Vector ac = T.p3().subToVector(T.p1());
		final WB_Vector ap = new WB_Vector(T.p1(), p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1().get();
		}

		final WB_Vector bp = new WB_Vector(T.p2(), p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2().get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1().add(ab._mulSelf(v));
		}

		final WB_Vector cp = new WB_Vector(T.p3(), p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3().get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1().add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2().add((T.p3().subToVector(T.p2()))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return T.p1().add(ab._mulSelf(v)._addSelf(ac._mulSelf(w)));
	}

	public static WB_Point getClosestPointToTriangle3D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b, final WB_Coordinate c) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, c);
		final WB_Vector ap = new WB_Vector(a, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return new WB_Point(a);
		}

		final WB_Vector bp = new WB_Vector(b, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return new WB_Point(b);
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return new WB_Point(a)._addSelf(ab._mulSelf(v));
		}

		final WB_Vector cp = new WB_Vector(c, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return new WB_Point(c);
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return new WB_Point(a)._addSelf(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return new WB_Point(b)._addSelf(new WB_Vector(b, c)._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return new WB_Point(a)
				._addSelf(ab._mulSelf(v)._addSelf(ac._mulSelf(w)));
	}

	public static WB_Point getClosestPointOnPeriphery3D(final WB_Coordinate p,
			final Triangle T) {
		final WB_Vector ab = T.p2().subToVector(T.p1());
		final WB_Vector ac = T.p3().subToVector(T.p1());
		final WB_Vector ap = new WB_Vector(T.p1(), p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1().get();
		}

		final WB_Vector bp = new WB_Vector(T.p2(), p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2().get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1().add(ab._mulSelf(v));
		}

		final WB_Vector cp = new WB_Vector(T.p3(), p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3().get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1().add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2().add((T.p3().subToVector(T.p2()))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		T.p3().subToVector(T.p2());
		if (WB_Epsilon.isZero(u - 1)) {
			return T.p1().get();
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return T.p2().get();
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return T.p3().get();
		}
		final WB_Point A = getClosestPointToSegment3D(p, T.p2(), T.p3());
		final double dA2 = WB_Distance.getSqDistance3D(p, A);
		final WB_Point B = getClosestPointToSegment3D(p, T.p1(), T.p3());
		final double dB2 = WB_Distance.getSqDistance3D(p, B);
		final WB_Point C = getClosestPointToSegment3D(p, T.p1(), T.p2());
		final double dC2 = WB_Distance.getSqDistance3D(p, C);
		if ((dA2 < dB2) && (dA2 < dC2)) {
			return A;
		} else if ((dB2 < dA2) && (dB2 < dC2)) {
			return B;
		} else {
			return C;
		}

	}

	// POINT-POLYGON

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final SimplePolygon poly) {
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint3D(p, T);
			final double d2 = WB_Distance.getDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final List<? extends Triangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		Triangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint3D(p, T);
			final double d2 = WB_Distance.getDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	public static WB_Point getClosestPointOnPeriphery3D(final WB_Coordinate p,
			final SimplePolygon poly) {
		final List<WB_IndexedTriangle> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint3D(p, T);
			final double d2 = WB_Distance.getSqDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_IndexedSegment S;
			for (int i = 0, j = poly.getN() - 1; i < poly.getN(); j = i, i++) {
				S = new WB_IndexedSegment(poly.getIndex(j), poly.getIndex(i),
						poly.getPoints());
				tmp = getClosestPoint3D(p, S);
				final double d2 = WB_Distance.getSqDistance3D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}

		return closest;
	}

	public static WB_Point getClosestPointOnPeriphery3D(final WB_Coordinate p,
			final SimplePolygon poly, final List<WB_IndexedTriangle> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_IndexedTriangle T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint3D(p, T);
			final double d2 = WB_Distance.getSqDistance3D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_Segment S;
			for (int i = 0, j = poly.getN() - 1; i < poly.getN(); j = i, i++) {
				S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
				tmp = getClosestPoint3D(p, S);
				final double d2 = WB_Distance.getSqDistance3D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}
		return closest;
	}

	// LINE-LINE

	public static WB_IntersectionResult getClosestPoint3D(final WB_Line L1,
			final WB_Line L2) {
		final double a = L1.getDirection().dot(L1.getDirection());
		final double b = L1.getDirection().dot(L2.getDirection());
		final WB_Vector r = L1.getOrigin().subToVector(L2.getOrigin());
		final double c = L1.getDirection().dot(r);
		final double e = L2.getDirection().dot(L2.getDirection());
		final double f = L2.getDirection().dot(r);
		double denom = a * e - b * b;
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
			final double d2 = WB_Distance.getSqDistance3D(L1.getOrigin().get(),
					p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.object = new WB_Segment(L1.getOrigin().get(), p2);
			i.dimension = 1;
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = (b * f - c * e) * denom;
		final double t2 = (a * f - b * c) * denom;
		final WB_Point p1 = new WB_Point(L1.getPointOnLine(t1));
		final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
		final double d2 = WB_Distance.getSqDistance3D(p1, p2);
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = t1;
		i.t2 = t2;
		i.dimension = 0;
		i.object = p1;
		i.sqDist = d2;
		return i;

	}

	// POINT-TETRAHEDRON

	public static WB_Point getClosestPoint3D(final WB_Coordinate p,
			final WB_Tetrahedron T) {
		WB_Point closestPt = new WB_Point(p);
		double bestSqDist = Double.POSITIVE_INFINITY;
		if (WB_Plane.pointOtherSideOfPlane(p, T.p4, T.p1, T.p2, T.p3)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p2, T.p3);
			final double sqDist = (q.subToVector(p)).getSqLength();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		if (WB_Plane.pointOtherSideOfPlane(p, T.p2, T.p1, T.p3, T.p4)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p3, T.p4);
			final double sqDist = (q.subToVector(p)).getSqLength();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		if (WB_Plane.pointOtherSideOfPlane(p, T.p3, T.p1, T.p4, T.p2)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p4, T.p2);
			final double sqDist = (q.subToVector(p)).getSqLength();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		if (WB_Plane.pointOtherSideOfPlane(p, T.p1, T.p2, T.p4, T.p3)) {
			final WB_Point q = getClosestPointToTriangle3D(p, T.p2, T.p4, T.p3);
			final double sqDist = (q.subToVector(p)).getSqLength();
			if (sqDist < bestSqDist) {
				bestSqDist = sqDist;
				closestPt = q;
			}
		}

		return new WB_Point(closestPt);

	}

	protected static class TriangleIntersection {
		public WB_Point p0; // the first point of the line
		public WB_Point p1; // the second point of the line
		public double s0; // the distance along the line to the first
							// intersection with the triangle
		public double s1; // the distance along the line to the second
							// intersection with the triangle
	}

	public static WB_IntersectionResult getIntersection3D(Triangle v, Triangle u) {
		// Taken from
		// http://jgt.akpeters.com/papers/Moller97/tritri.html#ISECTLINE

		// Compute plane equation of first triangle: n1 * x + d1 = 0.

		WB_Plane P1 = factory.createPlane(v);

		WB_Vector n1 = P1.getNormal();
		double d1 = -P1.d();

		// Evaluate second triangle with plane equation 1 to determine signed
		// distances to the plane.
		double du0 = n1.dot(u.p1()) + d1;
		double du1 = n1.dot(u.p2()) + d1;
		double du2 = n1.dot(u.p3()) + d1;
		// Coplanarity robustness check.
		if (Math.abs(du0) < WB_Epsilon.EPSILON)
			du0 = 0;
		if (Math.abs(du1) < WB_Epsilon.EPSILON)
			du1 = 0;
		if (Math.abs(du2) < WB_Epsilon.EPSILON)
			du2 = 0;

		double du0du1 = du0 * du1;
		double du0du2 = du0 * du2;

		if (du0du1 > 0 && du0du2 > 0) {
			return empty();
			// same sign on all of them + != 0 ==> no
		}
		// intersection
		WB_Plane P2 = factory.createPlane(u);

		WB_Vector n2 = P2.getNormal();
		double d2 = -P2.d();
		// Compute plane equation of second triangle: n2 * x + d2 = 0

		// Evaluate first triangle with plane equation 2 to determine signed
		// distances to the plane.
		double dv0 = n2.dot(v.p1()) + d2;
		double dv1 = n2.dot(v.p2()) + d2;
		double dv2 = n2.dot(v.p3()) + d2;

		// Coplanarity robustness check.
		if (Math.abs(dv0) < WB_Epsilon.EPSILON)
			dv0 = 0;
		if (Math.abs(dv1) < WB_Epsilon.EPSILON)
			dv1 = 0;
		if (Math.abs(dv2) < WB_Epsilon.EPSILON)
			dv2 = 0;

		double dv0dv1 = dv0 * dv1;
		double dv0dv2 = dv0 * dv2;

		if (dv0dv1 > 0 && dv0dv2 > 0) {
			return empty();
			// same sign on all of them + != 0 ==> no
		}

		// Compute direction of intersection line.
		WB_Vector ld = n1.cross(n2);

		// Compute an index to the largest component of line direction.
		double max = Math.abs(ld.xd());
		int index = 0;
		double b = Math.abs(ld.yd());
		double c = Math.abs(ld.zd());
		if (b > max) {
			max = b;
			index = 1;
		}
		if (c > max) {
			index = 2;
		}

		// This is the simplified projection onto the line of intersection.
		double vp0 = v.p1().xd();
		double vp1 = v.p2().xd();
		double vp2 = v.p3().xd();

		double up0 = u.p1().xd();
		double up1 = u.p2().xd();
		double up2 = u.p3().xd();
		if (index == 1) {
			vp0 = v.p1().yd();
			vp1 = v.p2().yd();
			vp2 = v.p3().yd();

			up0 = u.p1().yd();
			up1 = u.p2().yd();
			up2 = u.p3().yd();
		} else if (index == 2) {
			vp0 = v.p1().zd();
			vp1 = v.p2().zd();
			vp2 = v.p3().zd();

			up0 = u.p1().zd();
			up1 = u.p2().zd();
			up2 = u.p3().zd();
		}

		// Compute interval for triangle 1.
		TriangleIntersection isectA = compute_intervals_isectline(v, vp0, vp1,
				vp2, dv0, dv1, dv2, dv0dv1, dv0dv2);

		if (isectA == null) {
			if (coplanarTriangles(n1, v, u)) {
				return empty();
			} else {
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;

				return i;
			}
		}

		int smallest1 = 0;
		if (isectA.s0 > isectA.s1) {
			double cc = isectA.s0;
			isectA.s0 = isectA.s1;
			isectA.s1 = cc;
			smallest1 = 1;
		}

		// Compute interval for triangle 2.
		TriangleIntersection isectB = compute_intervals_isectline(u, up0, up1,
				up2, du0, du1, du2, du0du1, du0du2);

		int smallest2 = 0;
		if (isectB.s0 > isectB.s1) {
			double cc = isectB.s0;
			isectB.s0 = isectB.s1;
			isectB.s1 = cc;
			smallest2 = 1;
		}

		if (isectA.s1 < isectB.s0 || isectB.s1 < isectA.s0)
			return empty();

		// At this point we know that the triangles intersect: there's an
		// intersection line, the triangles are not
		// coplanar, and they overlap.
		WB_Point[] intersectionVertices = new WB_Point[2];
		if (isectB.s0 < isectA.s0) {
			if (smallest1 == 0)
				intersectionVertices[0] = isectA.p0;
			else
				intersectionVertices[0] = isectA.p1;

			if (isectB.s1 < isectA.s1) {
				if (smallest2 == 0)
					intersectionVertices[1] = isectB.p1;
				else
					intersectionVertices[1] = isectB.p0;
			} else {
				if (smallest1 == 0)
					intersectionVertices[1] = isectA.p1;
				else
					intersectionVertices[1] = isectA.p0;
			}
		} else {
			if (smallest2 == 0)
				intersectionVertices[0] = isectB.p0;
			else
				intersectionVertices[0] = isectB.p1;

			if (isectB.s1 > isectA.s1) {
				if (smallest1 == 0)
					intersectionVertices[1] = isectA.p1;
				else
					intersectionVertices[1] = isectA.p0;
			} else {
				if (smallest2 == 0)
					intersectionVertices[1] = isectB.p1;
				else
					intersectionVertices[1] = isectB.p0;
			}
		}
		WB_IntersectionResult ir = new WB_IntersectionResult();
		ir.intersection = true;
		ir.object = factory.createSegment(intersectionVertices[0],
				intersectionVertices[1]);

		return ir;
	}

	protected static TriangleIntersection compute_intervals_isectline(
			Triangle v, double vv0, double vv1, double vv2, double d0,
			double d1, double d2, double d0d1, double d0d2) {
		if (d0d1 > 0) // D0, D1 are on the same side, D2 on the other or on the
						// plane
			return intersect(v.p3(), v.p1(), v.p2(), vv2, vv0, vv1, d2, d0, d1);
		else if (d0d2 > 0)
			return intersect(v.p2(), v.p1(), v.p3(), vv1, vv0, vv2, d1, d0, d2);
		else if (d1 * d2 > 0 || d0 != 0)
			return intersect(v.p1(), v.p2(), v.p3(), vv0, vv1, vv2, d0, d1, d2);
		else if (d1 != 0)
			return intersect(v.p2(), v.p1(), v.p3(), vv1, vv0, vv2, d1, d0, d2);
		else if (d2 != 0)
			return intersect(v.p3(), v.p1(), v.p2(), vv2, vv0, vv1, d2, d0, d1);
		else
			return null; // triangles are coplanar
	}

	protected static TriangleIntersection intersect(WB_Point v0, WB_Point v1,
			WB_Point v2, double vv0, double vv1, double vv2, double d0,
			double d1, double d2) {
		TriangleIntersection intersection = new TriangleIntersection();

		double tmp = d0 / (d0 - d1);
		intersection.s0 = vv0 + (vv1 - vv0) * tmp;
		WB_Vector diff = new WB_Vector(v0, v1);
		diff._mulSelf(tmp);
		intersection.p0 = v0.add(diff);

		tmp = d0 / (d0 - d2);
		intersection.s1 = vv0 + (vv2 - vv0) * tmp;
		diff = new WB_Vector(v0, v2);
		diff._mulSelf(tmp);
		intersection.p1 = v0.add(diff);

		return intersection;
	}

	protected static boolean coplanarTriangles(WB_Vector n, Triangle v,
			Triangle u) {
		// First project onto an axis-aligned plane that maximizes the are of
		// the triangles.
		int i0;
		int i1;

		double[] a = new double[] { Math.abs(n.xd()), Math.abs(n.yd()),
				Math.abs(n.zd()) };
		if (a[0] > a[1]) // X > Y
		{
			if (a[0] > a[2]) { // X is greatest
				i0 = 1;
				i1 = 2;
			} else { // Z is greatest
				i0 = 0;
				i1 = 1;
			}
		} else // X < Y
		{
			if (a[2] > a[1]) { // Z is greatest
				i0 = 0;
				i1 = 1;
			} else { // Y is greatest
				i0 = 0;
				i1 = 2;
			}
		}

		// Test all edges of triangle 1 against the edges of triangle 2.
		double[] v0 = new double[] { v.p1().xd(), v.p1().yd(), v.p1().zd() };
		double[] v1 = new double[] { v.p2().xd(), v.p2().yd(), v.p2().zd() };
		double[] v2 = new double[] { v.p3().xd(), v.p3().yd(), v.p3().zd() };

		double[] u0 = new double[] { u.p1().xd(), u.p1().yd(), u.p1().zd() };
		double[] u1 = new double[] { u.p2().xd(), u.p2().yd(), u.p2().zd() };
		double[] u2 = new double[] { u.p3().xd(), u.p3().yd(), u.p3().zd() };

		boolean tf = triangleEdgeTest(v0, v1, u0, u1, u2, i0, i1);
		if (tf)
			return true;

		tf = triangleEdgeTest(v1, v2, u0, u1, u2, i0, i1);
		if (tf)
			return true;

		tf = triangleEdgeTest(v2, v0, u0, u1, u2, i0, i1);
		if (tf)
			return true;

		// Finally, test whether one triangle is contained in the other one.
		tf = pointInTri(v0, u0, u1, u2, i0, i1);
		if (tf)
			return true;

		return pointInTri(u0, v0, v1, v2, i0, i1);
	}

	protected static boolean triangleEdgeTest(double[] v0, double[] v1,
			double[] u0, double[] u1, double[] u2, int i0, int i1) {
		double ax = v1[i0] - v0[i0];
		double ay = v1[i1] - v0[i1];

		// Test edge u0:u1 against v0:v1
		boolean tf = edgeEdgeTest(v0, u0, u1, i0, i1, ax, ay);
		if (tf)
			return true;

		// Test edge u1:u2 against v0:v1
		tf = edgeEdgeTest(v0, u1, u2, i0, i1, ax, ay);
		if (tf)
			return true;

		// Test edge u2:u0 against v0:v1
		return edgeEdgeTest(v0, u2, u0, i0, i1, ax, ay);
	}

	protected static boolean edgeEdgeTest(double[] v0, double[] u0,
			double[] u1, int i0, int i1, double ax, double ay) {
		double bx = u0[i0] - u1[i0];
		double by = u0[i1] - u1[i1];
		double cx = v0[i0] - u0[i0];
		double cy = v0[i1] - u0[i1];

		double f = ay * bx - ax * by;
		double d = by * cx - bx * cy;

		if ((f > 0 && d >= 0 && d <= f) || (f < 0 && d <= 0 && d >= f)) {
			double e = ax * cy - ay * cx;
			if (f > 0) {
				if (e >= 0 && e <= f)
					return true;
			} else {
				if (e <= 0 && e >= f)
					return true;
			}
		}

		return false;
	}

	protected static boolean pointInTri(double[] v0, double[] u0, double[] u1,
			double[] u2, int i0, int i1) {
		double a = u1[i1] - u0[i1];
		double b = -(u1[i0] - u0[i0]);
		double c = -a * u0[i0] - b * u0[i1];
		double d0 = a * v0[i0] + b * v0[i1] + c;

		a = u2[i1] - u1[i1];
		b = -(u2[i0] - u1[i0]);
		c = -a * u1[i0] - b * u1[i1];
		double d1 = a * v0[i0] + b * v0[i1] + c;

		a = u0[i1] - u2[i1];
		b = -(u0[i0] - u2[i0]);
		c = -a * u2[i0] - b * u2[i1];
		double d2 = a * v0[i0] + b * v0[i1] + c;

		return d0 * d1 > 0 && d0 * d2 > 0;
	}

	private static WB_IntersectionResult empty() {

		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;
	}

	public static WB_IntersectionResult getIntersection2D(final Segment S1,
			final Segment S2) {
		final double a1 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getEndpoint());
		final double a2 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && a1 * a2 < 0) {
			final double a3 = WB_Triangle2D.twiceSignedTriArea2D(
					S2.getOrigin(), S2.getEndpoint(), S1.getOrigin());
			final double a4 = a3 + a2 - a1;
			if (a3 * a4 < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getParametricPointOnSegment(t1);
				i.dimension = 0;
				i.sqDist = 0;
				return i;

			}

		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = 0;
		i.t2 = 0;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;

	}

	public static void getIntersection2DInto(final Segment S1,
			final Segment S2, final WB_IntersectionResult i) {
		final double a1 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getEndpoint());
		final double a2 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && a1 * a2 < 0) {
			final double a3 = WB_Triangle2D.twiceSignedTriArea2D(
					S2.getOrigin(), S2.getEndpoint(), S1.getOrigin());
			final double a4 = a3 + a2 - a1;
			if (a3 * a4 < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getParametricPointOnSegment(t1);
				i.dimension = 0;
				i.sqDist = 0;

			}

		} else {
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
		}
	}

	public static WB_Segment[] splitSegment2D(final WB_Segment S,
			final WB_Line2D L) {
		final WB_Segment[] result = new WB_Segment[2];
		final WB_IntersectionResult ir2D = getClosestPoint2D(S, L);
		if (!ir2D.intersection) {
			return null;
		}
		if (ir2D.dimension == 0) {
			if (L.classifyPointToLine2D(S.getOrigin()) == WB_Classification.FRONT) {
				result[0] = new WB_Segment(S.getOrigin(),
						(WB_Point) ir2D.object);
				result[1] = new WB_Segment((WB_Point) ir2D.object,
						S.getEndpoint());
			} else if (L.classifyPointToLine2D(S.getOrigin()) == WB_Classification.BACK) {
				result[1] = new WB_Segment(S.getOrigin(),
						(WB_Point) ir2D.object);
				result[0] = new WB_Segment((WB_Point) ir2D.object,
						S.getEndpoint());
			}
		}
		return result;

	}

	public static double[] getIntervalIntersection2D(final double u0,
			final double u1, final double v0, final double v1) {
		if ((u0 >= u1) || (v0 >= v1)) {
			throw new IllegalArgumentException(
					"Interval degenerate or reversed.");
		}
		final double[] result = new double[3];
		if ((u1 < v0) || (u0 > v1)) {
			return result;
		}
		if (u1 > v0) {
			if (u0 < v1) {
				result[0] = 2;
				if (u0 < v0) {
					result[1] = v0;
				} else {
					result[1] = u0;
				}
				if (u1 > v1) {
					result[2] = v1;
				} else {
					result[2] = u1;
				}
			} else {
				result[0] = 1;
				result[1] = u0;
			}
		} else {
			result[0] = 1;
			result[1] = u1;
		}
		return result;

	}

	public static WB_SimplePolygon2D[] splitPolygon2D(
			final WB_SimplePolygon2D poly, final WB_Line2D L) {
		int numFront = 0;
		int numBack = 0;

		final ArrayList<WB_Point> frontVerts = new ArrayList<WB_Point>(20);
		final ArrayList<WB_Point> backVerts = new ArrayList<WB_Point>(20);

		final int numVerts = poly.n;
		if (numVerts > 0) {
			WB_Point a = poly.points[numVerts - 1];
			WB_Classification aSide = L.classifyPointToLine2D(a);
			WB_Point b;
			WB_Classification bSide;

			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i = new WB_IntersectionResult();
				b = poly.points[n];
				bSide = L.classifyPointToLine2D(b);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = getClosestPoint2D(L, new WB_Segment(a, b));
						WB_Point p1 = null;
						if (i.dimension == 0) {
							p1 = (WB_Point) i.object;
						} else if (i.dimension == 1) {
							p1 = ((Segment) i.object).getOrigin();
						}
						frontVerts.add(p1);
						numFront++;
						backVerts.add(p1);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = getClosestPoint2D(L, new WB_Segment(a, b));

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */
						final WB_Point p1 = (WB_Point) i.object;

						frontVerts.add(p1);
						numFront++;
						backVerts.add(p1);
						numBack++;
					} else if (aSide == WB_Classification.ON) {
						backVerts.add(a);
						numBack++;
					}
					backVerts.add(b);
					numBack++;
				} else {
					frontVerts.add(b);
					numFront++;
					if (aSide == WB_Classification.BACK) {
						backVerts.add(b);
						numBack++;
					}
				}
				a = b;
				aSide = bSide;

			}

		}
		final WB_SimplePolygon2D[] result = new WB_SimplePolygon2D[2];
		result[0] = new WB_SimplePolygon2D(frontVerts);
		result[1] = new WB_SimplePolygon2D(backVerts);
		return result;

	}

	public static ArrayList<WB_Point> getIntersection2D(final WB_Circle C0,
			final WB_Circle C1) {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
		final WB_Point u = C1.getCenter().sub(C0.getCenter());
		final double d2 = u.getSqLength();
		final double d = Math.sqrt(d2);
		if (WB_Epsilon.isEqualAbs(d, C0.getRadius() + C1.getRadius())) {
			result.add(factory.createInterpolatedPoint(C0.getCenter(),
					C1.getCenter(),
					C0.getRadius() / (C0.getRadius() + C1.getRadius())));
			return result;
		}
		if (d > (C0.getRadius() + C1.getRadius())
				|| d < WB_Math.fastAbs(C0.getRadius() - C1.getRadius())) {
			return result;
		}
		final double r02 = C0.getRadius() * C0.getRadius();
		final double r12 = C1.getRadius() * C1.getRadius();
		final double a = (r02 - r12 + d2) / (2 * d);
		final double h = Math.sqrt(r02 - a * a);
		final WB_Point c = u.mul(a / d)._addSelf(C0.getCenter());
		final double p0x = c.xd() + h
				* (C1.getCenter().yd() - C0.getCenter().yd()) / d;
		final double p0y = c.yd() - h
				* (C1.getCenter().xd() - C0.getCenter().xd()) / d;
		final double p1x = c.xd() - h
				* (C1.getCenter().yd() - C0.getCenter().yd()) / d;
		final double p1y = c.yd() + h
				* (C1.getCenter().xd() - C0.getCenter().xd()) / d;
		final WB_Point p0 = new WB_Point(p0x, p0y);
		result.add(p0);
		final WB_Point p1 = new WB_Point(p1x, p1y);
		if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance2D(p0, p1))) {
			result.add(new WB_Point(p1x, p1y));
		}
		return result;
	}

	public static ArrayList<WB_Point> getIntersection2D(final WB_Line2D L,
			final WB_Circle C) {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();

		final double b = 2 * (L.getDirection().xd()
				* (L.getOrigin().xd() - C.getCenter().xd()) + L.getDirection()
				.yd() * (L.getOrigin().yd() - C.getCenter().yd()));
		final double c = C.getCenter().getSqLength()
				+ L.getOrigin().getSqLength()
				- 2
				* (C.getCenter().xd() * L.getOrigin().xd() + C.getCenter().yd()
						* L.getOrigin().yd()) - C.getRadius() * C.getRadius();
		double disc = b * b - 4 * c;
		if (disc < -WB_Epsilon.EPSILON) {
			return result;
		}

		if (WB_Epsilon.isZero(disc)) {
			result.add(L.getPoint(-0.5 * b));
			return result;
		}
		disc = Math.sqrt(disc);
		result.add(L.getPoint(0.5 * (-b + disc)));
		result.add(L.getPoint(0.5 * (-b - disc)));
		return result;
	}

	public static boolean getIntersection2DProper(final WB_Coordinate a,
			final WB_Coordinate b, final WB_Coordinate c, final WB_Coordinate d) {
		if (WB_Predicates2D.orient2d(a, b, c) == 0
				|| WB_Predicates2D.orient2d(a, b, d) == 0
				|| WB_Predicates2D.orient2d(c, d, a) == 0
				|| WB_Predicates2D.orient2d(c, d, b) == 0) {
			return false;
		} else if (WB_Predicates2D.orient2d(a, b, c)
				* WB_Predicates2D.orient2d(a, b, d) > 0
				|| WB_Predicates2D.orient2d(c, d, a)
						* WB_Predicates2D.orient2d(c, d, b) > 0) {
			return false;
		} else {
			return true;
		}
	}

	public static WB_Point getClosestPoint2D(final WB_Coordinate p,
			final Segment S) {
		final WB_Vector ab = new WB_Vector(S.getOrigin(), S.getEndpoint());
		final WB_Vector ac = new WB_Vector(S.getOrigin(), p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return S.getOrigin().get();
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= denom) {
				t = 1;
				return S.getEndpoint().get();
			} else {
				t = t / denom;
				return new WB_Point(S.getParametricPointOnSegment(t));
			}
		}
	}

	public static WB_Point getClosestPoint2D(final Segment S,
			final WB_Coordinate p) {
		return getClosestPoint2D(p, S);
	}

	public static WB_Point getClosestPointToSegment(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return new WB_Point(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				return new WB_Point(b);
			} else {
				t = t / denom;
				return new WB_Point(a.xd() + t * ab.xd(), a.yd() + t * ab.yd());
			}
		}
	}

	public static void getClosestPointToSegmentInto(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b,
			final WB_MutableCoordinate result) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			result._set(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				result._set(b);
			} else {
				t = t / denom;
				result._set(a.xd() + t * ab.xd(), a.yd() + t * ab.yd());
			}
		}
	}

	public static WB_Point getClosestPoint2D(final WB_Coordinate p,
			final WB_Line2D L) {

		if (WB_Epsilon.isZero(L.getDirection().xd())) {
			return new WB_Point(L.getOrigin().xd(), p.yd());
		}
		if (WB_Epsilon.isZero(L.getDirection().yd())) {
			return new WB_Point(p.xd(), L.getOrigin().yd());
		}

		final double m = L.getDirection().yd() / L.getDirection().xd();
		final double b = L.getOrigin().yd() - m * L.getOrigin().xd();

		final double x = (m * p.yd() + p.xd() - m * b) / (m * m + 1);
		final double y = (m * m * p.yd() + m * p.xd() + b) / (m * m + 1);

		return new WB_Point(x, y);

	}

	public static WB_Point getClosestPointToLine2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Line2D L = new WB_Line2D();
		L.setFromPoints(a, b);
		return getClosestPoint2D(p, L);
	}

	public static WB_Point getClosestPoint2D(final WB_Coordinate p,
			final WB_Ray2D R) {
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return R.getOrigin().get();
		} else {
			return R.getPoint(t);
		}
	}

	public static WB_Point getClosestPointToRay2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Ray2D R = new WB_Ray2D();
		R.setFromPoints(a, b);
		return getClosestPoint2D(p, R);
	}

	public static WB_IntersectionResult getClosestPoint2D(final Segment S1,
			final Segment S2) {
		final WB_Point d1 = S1.getEndpoint().sub(S1.getOrigin());
		final WB_Point d2 = S2.getEndpoint().sub(S2.getOrigin());
		final WB_Point r = S1.getOrigin().sub(S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);

		if (WB_Epsilon.isZero(a) || WB_Epsilon.isZero(e)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Segment(S1.getOrigin().get(), S2.getOrigin()
					.get());
			i.dimension = 1;
			i.sqDist = r.getSqLength();
			return i;
		}

		double t1 = 0;
		double t2 = 0;
		if (WB_Epsilon.isZero(a)) {

			t2 = WB_Math.clamp(f / e, 0, 1);
		} else {
			final double c = d1.dot(r);
			if (WB_Epsilon.isZero(e)) {

				t1 = WB_Math.clamp(-c / a, 0, 1);
			} else {
				final double b = d1.dot(d2);
				final double denom = a * e - b * b;
				if (!WB_Epsilon.isZero(denom)) {
					t1 = WB_Math.clamp((b * f - c * e) / denom, 0, 1);
				} else {
					t1 = 0;
				}
				final double tnom = b * t1 + f;
				if (tnom < 0) {
					t1 = WB_Math.clamp(-c / a, 0, 1);
				} else if (tnom > e) {
					t2 = 1;
					t1 = WB_Math.clamp((b - c) / a, 0, 1);
				} else {
					t2 = tnom / e;
				}
			}
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = (t1 > 0) && (t1 < 1) && (t2 > 0) && (t2 < 1);
		i.t1 = t1;
		i.t2 = t2;
		final WB_Point p1 = S1.getParametricPointOnSegment(t1);
		final WB_Point p2 = S2.getParametricPointOnSegment(t2);
		i.sqDist = WB_Distance.getSqDistance2D(p1, p2);
		if (i.intersection) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
		}
		return i;

	}

	public static WB_IntersectionResult getClosestPoint2D(final WB_Line2D L1,
			final WB_Line2D L2) {
		final double a = L1.getDirection().dot(L1.getDirection());
		final double b = L1.getDirection().dot(L2.getDirection());
		final WB_Point r = L1.getOrigin().sub(L2.getOrigin());
		final double c = L1.getDirection().dot(r);
		final double e = L2.getDirection().dot(L2.getDirection());
		final double f = L2.getDirection().dot(r);
		double denom = a * e - b * b;
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point p2 = new WB_Point(L2.getPoint(t2));
			final double d2 = WB_Distance.getSqDistance2D(L1.getOrigin().get(),
					p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.dimension = 1;
			i.object = new WB_Segment(L1.getOrigin().get(), p2);
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = (b * f - c * e) * denom;
		final double t2 = (a * f - b * c) * denom;
		final WB_Point p1 = new WB_Point(L1.getPoint(t1));
		final WB_Point p2 = new WB_Point(L2.getPoint(t2));
		final double d2 = WB_Distance.getSqDistance2D(p1, p2);
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = t1;
		i.t2 = t2;
		i.dimension = 0;
		i.object = p1;
		i.sqDist = d2;
		return i;

	}

	public static WB_IntersectionResult getClosestPoint2D(final WB_Line2D L,
			final Segment S) {
		final WB_IntersectionResult i = getClosestPoint2D(L,
				new WB_Line2D(S.getOrigin(), S.getDirection()));
		if (i.dimension == 0) {
			return i;
		}
		if (i.t2 <= WB_Epsilon.EPSILON) {
			i.t2 = 0;
			i.object = new WB_Segment(((Segment) i.object).getOrigin(), S
					.getOrigin().get());
			i.sqDist = ((Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		if (i.t2 >= S.getLength() - WB_Epsilon.EPSILON) {
			i.t2 = 1;
			i.object = new WB_Segment(((Segment) i.object).getOrigin(), S
					.getEndpoint().get());
			i.sqDist = ((Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		return i;
	}

	public static WB_IntersectionResult getClosestPoint2D(final Segment S,
			final WB_Line2D L) {

		return getClosestPoint2D(L, S);
	}

	// POINT-TRIANGLE

	public static WB_Point getClosestPoint2D(final WB_Coordinate p,
			final WB_Triangle2D T) {
		final WB_Vector ab = T.p2.subToVector(T.p1);
		final WB_Vector ac = T.p3.subToVector(T.p1);
		final WB_Vector ap = new WB_Vector(T.p1, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1.get();
		}

		final WB_Vector bp = new WB_Vector(T.p2, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1.add(ab._mulSelf(v));
		}

		final WB_Vector cp = new WB_Vector(T.p3, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1.add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.add((T.p3.sub(T.p2))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return T.p1.add(ab._mulSelf(v)._addSelf(ac._mulSelf(w)));
	}

	public static WB_Point getClosestPointToTriangle2D(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b, final WB_Coordinate c) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, c);
		final WB_Vector ap = new WB_Vector(a, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return new WB_Point(a);
		}

		final WB_Vector bp = new WB_Vector(b, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return new WB_Point(b);
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return new WB_Point(a)._addMulSelf(v, ab);
		}

		final WB_Vector cp = new WB_Vector(c, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return new WB_Point(c);
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return new WB_Point(a)._addMulSelf(w, ac);
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return new WB_Point(b)._addMulSelf(w, new WB_Vector(b, c));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return new WB_Point(a)._addMulSelf(w, ac)._addMulSelf(v, ab);

	}

	public static WB_Point getClosestPointOnPeriphery2D(final WB_Coordinate p,
			final WB_Triangle2D T) {
		final WB_Vector ab = T.p2.subToVector(T.p1);
		final WB_Vector ac = T.p3.subToVector(T.p1);
		final WB_Vector ap = new WB_Vector(T.p1, p);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1.get();
		}

		final WB_Vector bp = new WB_Vector(T.p2, p);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1.add(ab._mulSelf(v));
		}

		final WB_Vector cp = new WB_Vector(T.p3, p);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1.add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.add((T.p3.sub(T.p2))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		T.p3.sub(T.p2);
		if (WB_Epsilon.isZero(u - 1)) {
			return T.p1.get();
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return T.p2.get();
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return T.p3.get();
		}
		final WB_Point A = getClosestPointToSegment(p, T.p2, T.p3);
		final double dA2 = WB_Distance.getSqDistance2D(p, A);
		final WB_Point B = getClosestPointToSegment(p, T.p1, T.p3);
		final double dB2 = WB_Distance.getSqDistance2D(p, B);
		final WB_Point C = getClosestPointToSegment(p, T.p1, T.p2);
		final double dC2 = WB_Distance.getSqDistance2D(p, C);
		if ((dA2 < dB2) && (dA2 < dC2)) {
			return A;
		} else if ((dB2 < dA2) && (dB2 < dC2)) {
			return B;
		} else {
			return C;
		}

	}

	// POINT-POLYGON

	public static WB_Point getClosestPointPoint2D(final WB_Coordinate p,
			final WB_SimplePolygon2D poly) {
		final List<WB_Triangle2D> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint2D(p, T);
			final double d2 = WB_Distance.getDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	public static WB_Point getClosestPointPoint2D(final WB_Coordinate p,
			final ArrayList<? extends WB_Triangle2D> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint2D(p, T);
			final double d2 = WB_Distance.getDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	public static WB_Point getClosestPointPointOnPeriphery2D(
			final WB_Coordinate p, final WB_SimplePolygon2D poly) {
		final List<WB_Triangle2D> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint2D(p, T);
			final double d2 = WB_Distance.getSqDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_IndexedSegment S;
			for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
				S = new WB_IndexedSegment(j, i, poly.points);
				tmp = getClosestPoint2D(p, S);
				final double d2 = WB_Distance.getSqDistance2D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}

		return closest;
	}

	public static WB_Point getClosestPointPointOnPeriphery2D(
			final WB_Coordinate p, final WB_SimplePolygon2D poly,
			final ArrayList<WB_Triangle2D> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = getClosestPoint2D(p, T);
			final double d2 = WB_Distance.getSqDistance2D(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			Segment S;
			for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
				S = new WB_IndexedSegment(j, i, poly.points);
				tmp = getClosestPoint2D(p, S);
				final double d2 = WB_Distance.getSqDistance2D(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}
		return closest;
	}

}
