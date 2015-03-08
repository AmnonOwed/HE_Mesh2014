/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_GeometryOp {
    
    /**
     * 
     */
    private static final WB_GeometryFactory gf = WB_GeometryFactory.instance();

    /**
     * 
     *
     * @param S 
     * @param P 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(final WB_Segment S,
	    final WB_Plane P) {
	final WB_Vector ab = S.getEndpoint().subToVector3D(S.getOrigin());
	double t = (P.d() - P.getNormal().dot(S.getOrigin()))
		/ P.getNormal().dot(ab);
	if ((t >= -WB_Epsilon.EPSILON) && (t <= (1.0 + WB_Epsilon.EPSILON))) {
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

    /**
     * 
     *
     * @param a 
     * @param b 
     * @param P 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(
	    final WB_Coordinate a, final WB_Coordinate b, final WB_Plane P) {
	final WB_Vector ab = new WB_Vector(a, b);
	double t = (P.d() - P.getNormal().dot(a)) / P.getNormal().dot(ab);
	if ((t >= -WB_Epsilon.EPSILON) && (t <= (1.0 + WB_Epsilon.EPSILON))) {
	    t = WB_Epsilon.clampEpsilon(t, 0, 1);
	    final WB_IntersectionResult i = new WB_IntersectionResult();
	    i.intersection = true;
	    i.t1 = t;
	    i.t2 = t;
	    i.object = new WB_Point(a.xd() + (t * (b.xd() - a.xd())), a.yd()
		    + (t * (b.yd() - a.yd())), a.zd() + (t * (b.zd() - a.zd())));
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
    /**
     * 
     *
     * @param R 
     * @param P 
     * @return 
     */
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

    /**
     * 
     *
     * @param R 
     * @param aabb 
     * @return 
     */
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
	i.sqDist = getSqDistance3D(p, (WB_Point) i.object);
	return i;
    }

    // LINE-PLANE
    /**
     * 
     *
     * @param L 
     * @param P 
     * @return 
     */
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
    /**
     * 
     *
     * @param P1 
     * @param P2 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(final WB_Plane P1,
	    final WB_Plane P2) {
	final WB_Vector N1 = P1.getNormal();
	final WB_Vector N2 = P2.getNormal();
	final WB_Vector N1xN2 = new WB_Vector(N1.cross(N2));
	if (WB_Epsilon.isZeroSq(N1xN2.getSqLength3D())) {
	    final WB_IntersectionResult i = new WB_IntersectionResult();
	    i.intersection = false;
	    i.t1 = 0;
	    i.t2 = 0;
	    i.sqDist = Float.POSITIVE_INFINITY;
	    return i;
	} else {
	    final double d1 = P1.d();
	    final double d2 = P2.d();
	    final double N1N2 = N1.dot(N2);
	    final double det = 1 - (N1N2 * N1N2);
	    final double c1 = (d1 - (d2 * N1N2)) / det;
	    final double c2 = (d2 - (d1 * N1N2)) / det;
	    final WB_Point O = new WB_Point(N1.mul(c1).addSelf(N2.mul(c2)));
	    new WB_Line(O, N1xN2);
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
    /**
     * 
     *
     * @param P1 
     * @param P2 
     * @param P3 
     * @return 
     */
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
	    final WB_Point p = new WB_Point(N2xN3).mulSelf(d1);
	    p.addSelf(N3xN1.mul(d2));
	    p.addSelf(N1xN2.mul(d3));
	    p.divSelf(denom);
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
    /**
     * 
     *
     * @param one 
     * @param other 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_AABB one,
	    final WB_AABB other) {
	if ((one.getMaxX() < other.getMinX())
		|| (one.getMinX() > other.getMaxX())) {
	    return false;
	}
	if ((one.getMaxY() < other.getMinY())
		|| (one.getMinY() > other.getMaxY())) {
	    return false;
	}
	if ((one.getMaxZ() < other.getMinZ())
		|| (one.getMinZ() > other.getMaxZ())) {
	    return false;
	}
	return true;
    }

    /**
     * 
     *
     * @param AABB 
     * @param P 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_AABB AABB,
	    final WB_Plane P) {
	final WB_Point c = AABB.getMax().add(AABB.getMin()).mulSelf(0.5);
	final WB_Point e = AABB.getMax().sub(c);
	final double r = (e.xd() * WB_Math.fastAbs(P.getNormal().xd()))
		+ (e.yd() * WB_Math.fastAbs(P.getNormal().yd()))
		+ (e.zd() * WB_Math.fastAbs(P.getNormal().zd()));
	final double s = P.getNormal().dot(c) - P.d();
	return WB_Math.fastAbs(s) <= r;
    }

    /**
     * 
     *
     * @param AABB 
     * @param S 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_AABB AABB,
	    final WB_Sphere S) {
	final double d2 = getSqDistance3D(S.getCenter(), AABB);
	return d2 <= (S.getRadius() * S.getRadius());
    }

    /**
     * 
     *
     * @param T 
     * @param S 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_Triangle T,
	    final WB_Sphere S) {
	final WB_Point p = getClosestPoint3D(S.getCenter(), T);
	return (p.subToVector3D(S.getCenter())).getSqLength3D() <= (S
		.getRadius() * S.getRadius());
    }

    // TRIANGLE-AABB
    /**
     * 
     *
     * @param T 
     * @param AABB 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_Triangle T,
	    final WB_AABB AABB) {
	double p0, p1, p2, r;
	final WB_Point c = AABB.getMax().add(AABB.getMin()).mulSelf(0.5);
	final double e0 = (AABB.getMaxX() - AABB.getMinX()) * 0.5;
	final double e1 = (AABB.getMaxY() - AABB.getMinY()) * 0.5;
	final double e2 = (AABB.getMaxZ() - AABB.getMinZ()) * 0.5;
	final WB_Point v0 = T.p1().get();
	final WB_Point v1 = T.p2().get();
	final WB_Point v2 = T.p3().get();
	v0.subSelf(c);
	v1.subSelf(c);
	v2.subSelf(c);
	final WB_Vector f0 = v1.subToVector3D(v0);
	final WB_Vector f1 = v2.subToVector3D(v1);
	final WB_Vector f2 = v0.subToVector3D(v2);
	// a00
	final WB_Vector a = new WB_Vector(0, -f0.zd(), f0.yd());// u0xf0
	if (a.isZero()) {
	    a.set(0, v0.yd(), v0.zd());
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a01
	a.set(0, -f1.zd(), f1.yd());// u0xf1
	if (a.isZero()) {
	    a.set(0, v1.yd(), v1.zd());
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a02
	a.set(0, -f2.zd(), f2.yd());// u0xf2
	if (a.isZero()) {
	    a.set(0, v2.yd(), v2.zd());
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a10
	a.set(f0.zd(), 0, -f0.xd());// u1xf0
	if (a.isZero()) {
	    a.set(v0.xd(), 0, v0.zd());
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a11
	a.set(f1.zd(), 0, -f1.xd());// u1xf1
	if (a.isZero()) {
	    a.set(v1.xd(), 0, v1.zd());
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a12
	a.set(f2.zd(), 0, -f2.xd());// u1xf2
	if (a.isZero()) {
	    a.set(v2.xd(), 0, v2.zd());
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a20
	a.set(-f0.yd(), f0.xd(), 0);// u2xf0
	if (a.isZero()) {
	    a.set(v0.xd(), v0.yd(), 0);
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a21
	a.set(-f1.yd(), f1.xd(), 0);// u2xf1
	if (a.isZero()) {
	    a.set(v1.xd(), v1.yd(), 0);
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	// a22
	a.set(-f2.yd(), f2.xd(), 0);// u2xf2
	if (a.isZero()) {
	    a.set(v2.xd(), v2.yd(), 0);
	}
	if (!a.isZero()) {
	    p0 = v0.dot(a);
	    p1 = v1.dot(a);
	    p2 = v2.dot(a);
	    r = (e0 * WB_Math.fastAbs(a.xd())) + (e1 * WB_Math.fastAbs(a.yd()))
		    + (e2 * WB_Math.fastAbs(a.zd()));
	    if (WB_Math.max(WB_Math.min(p0, p1, p2), -WB_Math.max(p0, p1, p2)) > r) {
		return false;
	    }
	}
	if ((WB_Math.max(v0.xd(), v1.xd(), v2.xd()) < -e0)
		|| (WB_Math.max(v0.xd(), v1.xd(), v2.xd()) > e0)) {
	    return false;
	}
	if ((WB_Math.max(v0.yd(), v1.yd(), v2.yd()) < -e1)
		|| (WB_Math.max(v0.yd(), v1.yd(), v2.yd()) > e1)) {
	    return false;
	}
	if ((WB_Math.max(v0.zd(), v1.zd(), v2.zd()) < -e2)
		|| (WB_Math.max(v0.zd(), v1.zd(), v2.zd()) > e2)) {
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
		final WB_Vector t = T.p3().subToVector3D(T.p1());
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
    /**
     * 
     *
     * @param S 
     * @param AABB 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_Segment S,
	    final WB_AABB AABB) {
	final WB_Vector e = AABB.getMax().subToVector3D(AABB.getMin());
	final WB_Vector d = S.getEndpoint().subToVector3D(S.getOrigin());
	final WB_Point m = new WB_Point((S.getEndpoint().xd() + S.getOrigin()
		.xd()) - AABB.getMinX() - AABB.getMaxX(),
		(S.getEndpoint().yd() + S.getOrigin().yd()) - AABB.getMinY()
			- AABB.getMaxY(), (S.getEndpoint().zd() + S.getOrigin()
			.zd()) - AABB.getMinZ() - AABB.getMaxZ());
	double adx = WB_Math.fastAbs(d.xd());
	if (WB_Math.fastAbs(m.xd()) > (e.xd() + adx)) {
	    return false;
	}
	double ady = WB_Math.fastAbs(d.yd());
	if (WB_Math.fastAbs(m.yd()) > (e.yd() + ady)) {
	    return false;
	}
	double adz = WB_Math.fastAbs(d.zd());
	if (WB_Math.fastAbs(m.zd()) > (e.zd() + adz)) {
	    return false;
	}
	adx += WB_Epsilon.EPSILON;
	ady += WB_Epsilon.EPSILON;
	adz += WB_Epsilon.EPSILON;
	if (WB_Math.fastAbs((m.yd() * d.zd()) - (m.zd() * d.yd())) > ((e.yd() * adz) + (e
		.zd() * ady))) {
	    return false;
	}
	if (WB_Math.fastAbs((m.zd() * d.xd()) - (m.xd() * d.zd())) > ((e.xd() * adz) + (e
		.zd() * adx))) {
	    return false;
	}
	if (WB_Math.fastAbs((m.xd() * d.yd()) - (m.yd() * d.xd())) > ((e.xd() * ady) + (e
		.yd() * adx))) {
	    return false;
	}
	return true;
    }

    // SPHERE-SPHERE
    /**
     * 
     *
     * @param S1 
     * @param S2 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_Sphere S1,
	    final WB_Sphere S2) {
	final WB_Vector d = S1.getCenter().subToVector3D(S2.getCenter());
	final double d2 = d.getSqLength3D();
	final double radiusSum = S1.getRadius() + S2.getRadius();
	return d2 <= (radiusSum * radiusSum);
    }

    // RAY-SPHERE
    /**
     * 
     *
     * @param R 
     * @param S 
     * @return 
     */
    public static boolean checkIntersection3D(final WB_Ray R, final WB_Sphere S) {
	final WB_Vector m = R.getOrigin().subToVector3D(S.getCenter());
	final double c = m.dot(m) - (S.getRadius() * S.getRadius());
	if (c <= 0) {
	    return true;
	}
	final double b = m.dot(R.getDirection());
	if (b >= 0) {
	    return false;
	}
	final double disc = (b * b) - c;
	if (disc < 0) {
	    return false;
	}
	return true;
    }

    /**
     * 
     *
     * @param R 
     * @param AABB 
     * @return 
     */
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

    /**
     * 
     *
     * @param R 
     * @param tree 
     * @return 
     */
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

    /**
     * 
     *
     * @param aabb 
     * @param tree 
     * @return 
     */
    public static ArrayList<WB_AABBNode> getIntersection3D(final WB_AABB aabb,
	    final WB_AABBTree tree) {
	final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
	final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
	queue.add(tree.getRoot());
	WB_AABBNode current;
	while (!queue.isEmpty()) {
	    current = queue.pop();
	    if (checkIntersection3D(aabb, current.getAABB())) {
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

    /**
     * 
     *
     * @param L 
     * @param AABB 
     * @return 
     */
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

    /**
     * 
     *
     * @param L 
     * @param tree 
     * @return 
     */
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

    /**
     * 
     *
     * @param S 
     * @param tree 
     * @return 
     */
    public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Segment S,
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

    /**
     * 
     *
     * @param P 
     * @param tree 
     * @return 
     */
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

    /**
     * 
     *
     * @param T 
     * @param tree 
     * @return 
     */
    public static ArrayList<WB_AABBNode> getIntersection3D(final WB_Triangle T,
	    final WB_AABBTree tree) {
	final ArrayList<WB_AABBNode> result = new ArrayList<WB_AABBNode>();
	final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
	queue.add(tree.getRoot());
	WB_AABBNode current;
	while (!queue.isEmpty()) {
	    current = queue.pop();
	    if (checkIntersection3D(T, current.getAABB())) {
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

    // TODO: implement for polygons with holes
    /**
     * 
     *
     * @param poly 
     * @param P 
     * @return 
     */
    public static ArrayList<WB_Segment> getIntersection3D(
	    final WB_Polygon poly, final WB_Plane P) {
	final ArrayList<WB_Segment> result = new ArrayList<WB_Segment>();
	/*
	 * if (cptp == WB_ClassifyPolygonToPlane.POLYGON_ON_PLANE) { return
	 * poly.toSegments(); } if ((cptp ==
	 * WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE) || (cptp ==
	 * WB_ClassifyPolygonToPlane.POLYGON_BEHIND_PLANE)) { return result; }
	 */
	final ArrayList<WB_Coordinate> splitVerts = new ArrayList<WB_Coordinate>();
	final int numVerts = poly.getNumberOfShellPoints();
	if (numVerts > 0) {
	    WB_Coordinate a = poly.getPoint(numVerts - 1);
	    WB_ClassificationGeometry aSide = WB_Classify
		    .classifyPointToPlane3D(a, P);
	    WB_Coordinate b;
	    WB_ClassificationGeometry bSide;
	    for (int n = 0; n < numVerts; n++) {
		WB_IntersectionResult i;
		b = poly.getPoint(n);
		bSide = WB_Classify.classifyPointToPlane3D(b, P);
		if (bSide == WB_ClassificationGeometry.FRONT) {
		    if (aSide == WB_ClassificationGeometry.BACK) {
			i = WB_GeometryOp.getIntersection3D(b, a, P);
			splitVerts.add((WB_Point) i.object);
		    }
		} else if (bSide == WB_ClassificationGeometry.BACK) {
		    if (aSide == WB_ClassificationGeometry.FRONT) {
			i = WB_GeometryOp.getIntersection3D(a, b, P);
			splitVerts.add((WB_Point) i.object);
		    }
		}
		if (aSide == WB_ClassificationGeometry.ON) {
		    splitVerts.add(a);
		}
		a = b;
		aSide = bSide;
	    }
	}
	for (int i = 0; i < splitVerts.size(); i += 2) {
	    if (((i + 1) < splitVerts.size())
		    && (splitVerts.get(i + 1) != null)) {
		result.add(new WB_Segment(splitVerts.get(i), splitVerts
			.get(i + 1)));
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param S1 
     * @param S2 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(final WB_Segment S1,
	    final WB_Segment S2) {
	final WB_Vector d1 = new WB_Vector(S1.getEndpoint());
	d1.subSelf(S1.getOrigin());
	final WB_Vector d2 = new WB_Vector(S2.getEndpoint());
	d2.subSelf(S2.getOrigin());
	final WB_Vector r = new WB_Vector(S1.getOrigin());
	r.subSelf(S2.getOrigin());
	final double a = d1.dot(d1);
	final double e = d2.dot(d2);
	final double f = d2.dot(r);
	if (WB_Epsilon.isZero(a) && WB_Epsilon.isZero(e)) {
	    // Both segments are degenerate
	    final WB_IntersectionResult i = new WB_IntersectionResult();
	    i.sqDist = r.getSqLength3D();
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
	    i.sqDist = r.getSqLength3D();
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
	    i.sqDist = r.getSqLength3D();
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
	final double denom = (a * e) - (b * b);
	if (!WB_Epsilon.isZero(denom)) {
	    // Non-parallel segments
	    t1 = WB_Math.clamp(((b * f) - (c * e)) / denom, 0, 1);
	} else {
	    // Parallel segments, non-parallel code handles case where
	    // projections of segments are disjoint.
	    final WB_Line L1 = new WB_Line(S1.getOrigin(), S1.getDirection());
	    double s1 = 0;
	    double e1 = pointAlongLine(S1.getEndpoint(), L1);
	    double s2 = pointAlongLine(S2.getOrigin(), L1);
	    double e2 = pointAlongLine(S2.getEndpoint(), L1);
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
		if (WB_Epsilon.isZeroSq(getSqDistance3D(S2.getOrigin(), L1))) {
		    // Segments are overlapping
		    final WB_IntersectionResult i = new WB_IntersectionResult();
		    i.sqDist = getSqDistance3D(start, end);
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
		    i.sqDist = getSqDistance3D(start, end);
		    i.intersection = false;
		    i.dimension = 1;
		    start.addSelf(end);
		    start.scaleSelf(0.5);
		    end = getClosestPoint3D(start, S2);
		    i.object = new WB_Segment(start, end);
		    return i;
		}
	    }
	    t1 = 0;
	}
	final double tnom = (b * t1) + f;
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
	i.sqDist = getSqDistance3D(p1, p2);
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

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final WB_Plane P) {
	final WB_Vector n = P.getNormal();
	final double t = n.dot(p) - P.d();
	return new WB_Point(p.xd() - (t * n.xd()), p.yd() - (t * n.yd()),
		p.zd() - (t * n.zd()));
    }

    /**
     * 
     *
     * @param P 
     * @param p 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Plane P,
	    final WB_Coordinate p) {
	return getClosestPoint3D(P, p);
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final WB_Segment S) {
	final WB_Vector ab = S.getEndpoint().subToVector3D(S.getOrigin());
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

    /**
     * 
     *
     * @param S 
     * @param p 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Segment S,
	    final WB_Coordinate p) {
	return getClosestPoint3D(p, S);
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static double getClosestPointT3D(final WB_Coordinate p,
	    final WB_Segment S) {
	final WB_Vector ab = S.getEndpoint().subToVector3D(S.getOrigin());
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

    /**
     * 
     *
     * @param S 
     * @param p 
     * @return 
     */
    public static double getClosestPointT3D(final WB_Segment S,
	    final WB_Coordinate p) {
	return getClosestPointT3D(p, S);
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
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
		return new WB_Point(a.xd() + (t * ab.xd()), a.yd()
			+ (t * ab.yd()), a.zd() + (t * ab.zd()));
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final WB_Line L) {
	final WB_Vector ca = new WB_Vector(p.xd() - L.getOrigin().yd(), p.yd()
		- L.getOrigin().xd(), p.zd() - L.getOrigin().zd());
	return L.getPointOnLine(ca.dot(L.getDirection()));
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static WB_Coordinate getClosestPointToLine3D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b) {
	return getClosestPoint3D(p, new WB_Line(a, b));
    }

    /**
     * 
     *
     * @param p 
     * @param R 
     * @return 
     */
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

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static WB_Point getClosestPointToRay3D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b) {
	return getClosestPoint3D(p, new WB_Ray(a, new WB_Vector(a, b)));
    }

    /**
     * 
     *
     * @param p 
     * @param AABB 
     * @return 
     */
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
	result.setX(v);
	v = p.yd();
	if (v < AABB.getMinY()) {
	    v = AABB.getMinY();
	}
	if (v > AABB.getMaxY()) {
	    v = AABB.getMaxY();
	}
	result.setY(v);
	v = p.zd();
	if (v < AABB.getMinZ()) {
	    v = AABB.getMinZ();
	}
	if (v > AABB.getMaxZ()) {
	    v = AABB.getMaxZ();
	}
	result.setZ(v);
	return result;
    }

    /**
     * 
     *
     * @param p 
     * @param AABB 
     * @param result 
     */
    public static void getClosestPoint3D(final WB_Coordinate p,
	    final WB_AABB AABB, final WB_MutableCoordinate result) {
	double v = p.xd();
	if (v < AABB.getMinX()) {
	    v = AABB.getMinX();
	}
	if (v > AABB.getMaxX()) {
	    v = AABB.getMaxX();
	}
	result.setX(v);
	v = p.yd();
	if (v < AABB.getMinY()) {
	    v = AABB.getMinY();
	}
	if (v > AABB.getMaxY()) {
	    v = AABB.getMaxY();
	}
	result.setY(v);
	v = p.zd();
	if (v < AABB.getMinZ()) {
	    v = AABB.getMinZ();
	}
	if (v > AABB.getMaxZ()) {
	    v = AABB.getMaxZ();
	}
	result.setZ(v);
    }

    // POINT-TRIANGLE
    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final WB_Triangle T) {
	final WB_Vector ab = T.p2().subToVector3D(T.p1());
	final WB_Vector ac = T.p3().subToVector3D(T.p1());
	final WB_Vector ap = new WB_Vector(T.p1(), p);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return T.p1().get();
	}
	final WB_Vector bp = new WB_Vector(T.p2(), p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return T.p2().get();
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return T.p1().add(ab.mulSelf(v));
	}
	final WB_Vector cp = new WB_Vector(T.p3(), p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return T.p3().get();
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return T.p1().add(ac.mulSelf(w));
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return T.p2().add((T.p3().subToVector3D(T.p2())).mulSelf(w));
	}
	final double denom = 1.0 / (va + vb + vc);
	final double v = vb * denom;
	final double w = vc * denom;
	return T.p1().add(ab.mulSelf(v).addSelf(ac.mulSelf(w)));
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @param c 
     * @return 
     */
    public static WB_Point getClosestPointToTriangle3D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b, final WB_Coordinate c) {
	final WB_Vector ab = new WB_Vector(a, b);
	final WB_Vector ac = new WB_Vector(a, c);
	final WB_Vector ap = new WB_Vector(a, p);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return new WB_Point(a);
	}
	final WB_Vector bp = new WB_Vector(b, p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return new WB_Point(b);
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return new WB_Point(a).addSelf(ab.mulSelf(v));
	}
	final WB_Vector cp = new WB_Vector(c, p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return new WB_Point(c);
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return new WB_Point(a).addSelf(ac.mulSelf(w));
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return new WB_Point(b).addSelf(new WB_Vector(b, c).mulSelf(w));
	}
	final double denom = 1.0 / (va + vb + vc);
	final double v = vb * denom;
	final double w = vc * denom;
	return new WB_Point(a).addSelf(ab.mulSelf(v).addSelf(ac.mulSelf(w)));
    }

    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static WB_Point getClosestPointOnPeriphery3D(final WB_Coordinate p,
	    final WB_Triangle T) {
	final WB_Vector ab = T.p2().subToVector3D(T.p1());
	final WB_Vector ac = T.p3().subToVector3D(T.p1());
	final WB_Vector ap = new WB_Vector(T.p1(), p);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return T.p1().get();
	}
	final WB_Vector bp = new WB_Vector(T.p2(), p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return T.p2().get();
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return T.p1().add(ab.mulSelf(v));
	}
	final WB_Vector cp = new WB_Vector(T.p3(), p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return T.p3().get();
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return T.p1().add(ac.mulSelf(w));
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return T.p2().add((T.p3().subToVector3D(T.p2())).mulSelf(w));
	}
	final double denom = 1.0 / (va + vb + vc);
	final double v = vb * denom;
	final double w = vc * denom;
	final double u = 1 - v - w;
	T.p3().subToVector3D(T.p2());
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
	final double dA2 = getSqDistance3D(p, A);
	final WB_Point B = getClosestPointToSegment3D(p, T.p1(), T.p3());
	final double dB2 = getSqDistance3D(p, B);
	final WB_Point C = getClosestPointToSegment3D(p, T.p1(), T.p2());
	final double dC2 = getSqDistance3D(p, C);
	if ((dA2 < dB2) && (dA2 < dC2)) {
	    return A;
	} else if ((dB2 < dA2) && (dB2 < dC2)) {
	    return B;
	} else {
	    return C;
	}
    }

    // POINT-POLYGON
    /**
     * 
     *
     * @param p 
     * @param tris 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final List<? extends WB_Triangle> tris) {
	final int n = tris.size();
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	WB_Triangle T;
	for (int i = 0; i < n; i++) {
	    T = tris.get(i);
	    tmp = getClosestPoint3D(p, T);
	    final double d2 = getSqDistance3D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	return closest;
    }

    // LINE-LINE
    /**
     * 
     *
     * @param L1 
     * @param L2 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint3D(final WB_Line L1,
	    final WB_Line L2) {
	final double a = L1.getDirection().dot(L1.getDirection());
	final double b = L1.getDirection().dot(L2.getDirection());
	final WB_Vector r = L1.getOrigin().subToVector3D(L2.getOrigin());
	final double c = L1.getDirection().dot(r);
	final double e = L2.getDirection().dot(L2.getDirection());
	final double f = L2.getDirection().dot(r);
	double denom = (a * e) - (b * b);
	if (WB_Epsilon.isZero(denom)) {
	    final double t2 = r.dot(L1.getDirection());
	    final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
	    final double d2 = getSqDistance3D(L1.getOrigin().get(), p2);
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
	final double t1 = ((b * f) - (c * e)) * denom;
	final double t2 = ((a * f) - (b * c)) * denom;
	final WB_Point p1 = new WB_Point(L1.getPointOnLine(t1));
	final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
	final double d2 = getSqDistance3D(p1, p2);
	if (WB_Epsilon.isZeroSq(d2)) {
	    final WB_IntersectionResult i = new WB_IntersectionResult();
	    i.intersection = true;
	    i.t1 = t1;
	    i.t2 = t2;
	    i.dimension = 0;
	    i.object = p1;
	    i.sqDist = d2;
	    return i;
	} else {
	    final WB_IntersectionResult i = new WB_IntersectionResult();
	    i.intersection = true;
	    i.t1 = t1;
	    i.t2 = t2;
	    i.dimension = 1;
	    i.object = new WB_Segment(p1, p2);
	    i.sqDist = d2;
	    return i;
	}
    }

    // POINT-TETRAHEDRON
    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final WB_Tetrahedron T) {
	WB_Point closestPt = new WB_Point(p);
	double bestSqDist = Double.POSITIVE_INFINITY;
	if (pointOtherSideOfPlane(p, T.p4, T.p1, T.p2, T.p3)) {
	    final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p2, T.p3);
	    final double sqDist = (q.subToVector3D(p)).getSqLength3D();
	    if (sqDist < bestSqDist) {
		bestSqDist = sqDist;
		closestPt = q;
	    }
	}
	if (pointOtherSideOfPlane(p, T.p2, T.p1, T.p3, T.p4)) {
	    final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p3, T.p4);
	    final double sqDist = (q.subToVector3D(p)).getSqLength3D();
	    if (sqDist < bestSqDist) {
		bestSqDist = sqDist;
		closestPt = q;
	    }
	}
	if (pointOtherSideOfPlane(p, T.p3, T.p1, T.p4, T.p2)) {
	    final WB_Point q = getClosestPointToTriangle3D(p, T.p1, T.p4, T.p2);
	    final double sqDist = (q.subToVector3D(p)).getSqLength3D();
	    if (sqDist < bestSqDist) {
		bestSqDist = sqDist;
		closestPt = q;
	    }
	}
	if (pointOtherSideOfPlane(p, T.p1, T.p2, T.p4, T.p3)) {
	    final WB_Point q = getClosestPointToTriangle3D(p, T.p2, T.p4, T.p3);
	    final double sqDist = (q.subToVector3D(p)).getSqLength3D();
	    if (sqDist < bestSqDist) {
		bestSqDist = sqDist;
		closestPt = q;
	    }
	}
	return new WB_Point(closestPt);
    }

    /**
     * Check if points lies on other side of plane compared with reference
     * points.
     *
     * @param p
     *            point to check
     * @param q
     *            reference point
     * @param a
     *            the a
     * @param b
     *            the b
     * @param c
     *            the c
     * @return true, if successful
     */
    public static boolean pointOtherSideOfPlane(final WB_Coordinate p,
	    final WB_Coordinate q, final WB_Coordinate a,
	    final WB_Coordinate b, final WB_Coordinate c) {
	final double signp = new WB_Vector(a, p).dot(new WB_Vector(a, b)
	.crossSelf(new WB_Vector(a, c)));
	final double signq = new WB_Vector(a, q).dot(new WB_Vector(a, b)
	.crossSelf(new WB_Vector(a, c)));
	return (signp * signq) <= 0;
    }

    /**
     * 
     */
    protected static class TriangleIntersection {
	
	/**
	 * 
	 */
	public WB_Point p0; // the first point of the line
	
	/**
	 * 
	 */
	public WB_Point p1; // the second point of the line
	
	/**
	 * 
	 */
	public double s0; // the distance along the line to the first
	// intersection with the triangle
	/**
	 * 
	 */
	public double s1; // the distance along the line to the second
	// intersection with the triangle
    }

    /**
     * 
     *
     * @param v 
     * @param u 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(final WB_Triangle v,
	    final WB_Triangle u) {
	// Taken from
	// http://jgt.akpeters.com/papers/Moller97/tritri.html#ISECTLINE
	// Compute plane equation of first triangle: n1 * x + d1 = 0.
	final WB_Plane P1 = gf.createPlane(v);
	final WB_Vector n1 = P1.getNormal();
	final double d1 = -P1.d();
	// Evaluate second triangle with plane equation 1 to determine signed
	// distances to the plane.
	double du0 = n1.dot(u.p1()) + d1;
	double du1 = n1.dot(u.p2()) + d1;
	double du2 = n1.dot(u.p3()) + d1;
	// Coplanarity robustness check.
	if (Math.abs(du0) < WB_Epsilon.EPSILON) {
	    du0 = 0;
	}
	if (Math.abs(du1) < WB_Epsilon.EPSILON) {
	    du1 = 0;
	}
	if (Math.abs(du2) < WB_Epsilon.EPSILON) {
	    du2 = 0;
	}
	final double du0du1 = du0 * du1;
	final double du0du2 = du0 * du2;
	if ((du0du1 > 0) && (du0du2 > 0)) {
	    return empty();
	    // same sign on all of them + != 0 ==> no
	}
	// intersection
	final WB_Plane P2 = gf.createPlane(u);
	final WB_Vector n2 = P2.getNormal();
	final double d2 = -P2.d();
	// Compute plane equation of second triangle: n2 * x + d2 = 0
	// Evaluate first triangle with plane equation 2 to determine signed
	// distances to the plane.
	double dv0 = n2.dot(v.p1()) + d2;
	double dv1 = n2.dot(v.p2()) + d2;
	double dv2 = n2.dot(v.p3()) + d2;
	// Coplanarity robustness check.
	if (Math.abs(dv0) < WB_Epsilon.EPSILON) {
	    dv0 = 0;
	}
	if (Math.abs(dv1) < WB_Epsilon.EPSILON) {
	    dv1 = 0;
	}
	if (Math.abs(dv2) < WB_Epsilon.EPSILON) {
	    dv2 = 0;
	}
	final double dv0dv1 = dv0 * dv1;
	final double dv0dv2 = dv0 * dv2;
	if ((dv0dv1 > 0) && (dv0dv2 > 0)) {
	    return empty();
	    // same sign on all of them + != 0 ==> no
	}
	// Compute direction of intersection line.
	final WB_Vector ld = n1.cross(n2);
	// Compute an index to the largest component of line direction.
	double max = Math.abs(ld.xd());
	int index = 0;
	final double b = Math.abs(ld.yd());
	final double c = Math.abs(ld.zd());
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
	final TriangleIntersection isectA = compute_intervals_isectline(v, vp0,
		vp1, vp2, dv0, dv1, dv2, dv0dv1, dv0dv2);
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
	    final double cc = isectA.s0;
	    isectA.s0 = isectA.s1;
	    isectA.s1 = cc;
	    smallest1 = 1;
	}
	// Compute interval for triangle 2.
	final TriangleIntersection isectB = compute_intervals_isectline(u, up0,
		up1, up2, du0, du1, du2, du0du1, du0du2);
	int smallest2 = 0;
	if (isectB.s0 > isectB.s1) {
	    final double cc = isectB.s0;
	    isectB.s0 = isectB.s1;
	    isectB.s1 = cc;
	    smallest2 = 1;
	}
	if ((isectA.s1 < isectB.s0) || (isectB.s1 < isectA.s0)) {
	    return empty();
	}
	// At this point we know that the triangles intersect: there's an
	// intersection line, the triangles are not
	// coplanar, and they overlap.
	final WB_Point[] intersectionVertices = new WB_Point[2];
	if (isectB.s0 < isectA.s0) {
	    if (smallest1 == 0) {
		intersectionVertices[0] = isectA.p0;
	    } else {
		intersectionVertices[0] = isectA.p1;
	    }
	    if (isectB.s1 < isectA.s1) {
		if (smallest2 == 0) {
		    intersectionVertices[1] = isectB.p1;
		} else {
		    intersectionVertices[1] = isectB.p0;
		}
	    } else {
		if (smallest1 == 0) {
		    intersectionVertices[1] = isectA.p1;
		} else {
		    intersectionVertices[1] = isectA.p0;
		}
	    }
	} else {
	    if (smallest2 == 0) {
		intersectionVertices[0] = isectB.p0;
	    } else {
		intersectionVertices[0] = isectB.p1;
	    }
	    if (isectB.s1 > isectA.s1) {
		if (smallest1 == 0) {
		    intersectionVertices[1] = isectA.p1;
		} else {
		    intersectionVertices[1] = isectA.p0;
		}
	    } else {
		if (smallest2 == 0) {
		    intersectionVertices[1] = isectB.p1;
		} else {
		    intersectionVertices[1] = isectB.p0;
		}
	    }
	}
	final WB_IntersectionResult ir = new WB_IntersectionResult();
	ir.intersection = true;
	ir.object = gf.createSegment(intersectionVertices[0],
		intersectionVertices[1]);
	return ir;
    }

    /**
     * 
     *
     * @param v 
     * @param vv0 
     * @param vv1 
     * @param vv2 
     * @param d0 
     * @param d1 
     * @param d2 
     * @param d0d1 
     * @param d0d2 
     * @return 
     */
    protected static TriangleIntersection compute_intervals_isectline(
	    final WB_Triangle v, final double vv0, final double vv1,
	    final double vv2, final double d0, final double d1,
	    final double d2, final double d0d1, final double d0d2) {
	if (d0d1 > 0) {
	    // plane
	    return intersect(v.p3(), v.p1(), v.p2(), vv2, vv0, vv1, d2, d0, d1);
	} else if (d0d2 > 0) {
	    return intersect(v.p2(), v.p1(), v.p3(), vv1, vv0, vv2, d1, d0, d2);
	} else if (((d1 * d2) > 0) || (d0 != 0)) {
	    return intersect(v.p1(), v.p2(), v.p3(), vv0, vv1, vv2, d0, d1, d2);
	} else if (d1 != 0) {
	    return intersect(v.p2(), v.p1(), v.p3(), vv1, vv0, vv2, d1, d0, d2);
	} else if (d2 != 0) {
	    return intersect(v.p3(), v.p1(), v.p2(), vv2, vv0, vv1, d2, d0, d1);
	} else {
	    return null; // triangles are coplanar
	}
    }

    /**
     * 
     *
     * @param v0 
     * @param v1 
     * @param v2 
     * @param vv0 
     * @param vv1 
     * @param vv2 
     * @param d0 
     * @param d1 
     * @param d2 
     * @return 
     */
    protected static TriangleIntersection intersect(final WB_Point v0,
	    final WB_Point v1, final WB_Point v2, final double vv0,
	    final double vv1, final double vv2, final double d0,
	    final double d1, final double d2) {
	final TriangleIntersection intersection = new TriangleIntersection();
	double tmp = d0 / (d0 - d1);
	intersection.s0 = vv0 + ((vv1 - vv0) * tmp);
	WB_Vector diff = new WB_Vector(v0, v1);
	diff.mulSelf(tmp);
	intersection.p0 = v0.add(diff);
	tmp = d0 / (d0 - d2);
	intersection.s1 = vv0 + ((vv2 - vv0) * tmp);
	diff = new WB_Vector(v0, v2);
	diff.mulSelf(tmp);
	intersection.p1 = v0.add(diff);
	return intersection;
    }

    /**
     * 
     *
     * @param n 
     * @param v 
     * @param u 
     * @return 
     */
    protected static boolean coplanarTriangles(final WB_Vector n,
	    final WB_Triangle v, final WB_Triangle u) {
	// First project onto an axis-aligned plane that maximizes the are of
	// the triangles.
	int i0;
	int i1;
	final double[] a = new double[] { Math.abs(n.xd()), Math.abs(n.yd()),
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
	final double[] v0 = new double[] { v.p1().xd(), v.p1().yd(),
		v.p1().zd() };
	final double[] v1 = new double[] { v.p2().xd(), v.p2().yd(),
		v.p2().zd() };
	final double[] v2 = new double[] { v.p3().xd(), v.p3().yd(),
		v.p3().zd() };
	final double[] u0 = new double[] { u.p1().xd(), u.p1().yd(),
		u.p1().zd() };
	final double[] u1 = new double[] { u.p2().xd(), u.p2().yd(),
		u.p2().zd() };
	final double[] u2 = new double[] { u.p3().xd(), u.p3().yd(),
		u.p3().zd() };
	boolean tf = triangleEdgeTest(v0, v1, u0, u1, u2, i0, i1);
	if (tf) {
	    return true;
	}
	tf = triangleEdgeTest(v1, v2, u0, u1, u2, i0, i1);
	if (tf) {
	    return true;
	}
	tf = triangleEdgeTest(v2, v0, u0, u1, u2, i0, i1);
	if (tf) {
	    return true;
	}
	// Finally, test whether one triangle is contained in the other one.
	tf = pointInTri(v0, u0, u1, u2, i0, i1);
	if (tf) {
	    return true;
	}
	return pointInTri(u0, v0, v1, v2, i0, i1);
    }

    /**
     * 
     *
     * @param v0 
     * @param v1 
     * @param u0 
     * @param u1 
     * @param u2 
     * @param i0 
     * @param i1 
     * @return 
     */
    protected static boolean triangleEdgeTest(final double[] v0,
	    final double[] v1, final double[] u0, final double[] u1,
	    final double[] u2, final int i0, final int i1) {
	final double ax = v1[i0] - v0[i0];
	final double ay = v1[i1] - v0[i1];
	// Test edge u0:u1 against v0:v1
	boolean tf = edgeEdgeTest(v0, u0, u1, i0, i1, ax, ay);
	if (tf) {
	    return true;
	}
	// Test edge u1:u2 against v0:v1
	tf = edgeEdgeTest(v0, u1, u2, i0, i1, ax, ay);
	if (tf) {
	    return true;
	}
	// Test edge u2:u0 against v0:v1
	return edgeEdgeTest(v0, u2, u0, i0, i1, ax, ay);
    }

    /**
     * 
     *
     * @param v0 
     * @param u0 
     * @param u1 
     * @param i0 
     * @param i1 
     * @param ax 
     * @param ay 
     * @return 
     */
    protected static boolean edgeEdgeTest(final double[] v0, final double[] u0,
	    final double[] u1, final int i0, final int i1, final double ax,
	    final double ay) {
	final double bx = u0[i0] - u1[i0];
	final double by = u0[i1] - u1[i1];
	final double cx = v0[i0] - u0[i0];
	final double cy = v0[i1] - u0[i1];
	final double f = (ay * bx) - (ax * by);
	final double d = (by * cx) - (bx * cy);
	if (((f > 0) && (d >= 0) && (d <= f))
		|| ((f < 0) && (d <= 0) && (d >= f))) {
	    final double e = (ax * cy) - (ay * cx);
	    if (f > 0) {
		if ((e >= 0) && (e <= f)) {
		    return true;
		}
	    } else {
		if ((e <= 0) && (e >= f)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * 
     *
     * @param v0 
     * @param u0 
     * @param u1 
     * @param u2 
     * @param i0 
     * @param i1 
     * @return 
     */
    protected static boolean pointInTri(final double[] v0, final double[] u0,
	    final double[] u1, final double[] u2, final int i0, final int i1) {
	double a = u1[i1] - u0[i1];
	double b = -(u1[i0] - u0[i0]);
	double c = (-a * u0[i0]) - (b * u0[i1]);
	final double d0 = (a * v0[i0]) + (b * v0[i1]) + c;
	a = u2[i1] - u1[i1];
	b = -(u2[i0] - u1[i0]);
	c = (-a * u1[i0]) - (b * u1[i1]);
	final double d1 = (a * v0[i0]) + (b * v0[i1]) + c;
	a = u0[i1] - u2[i1];
	b = -(u0[i0] - u2[i0]);
	c = (-a * u2[i0]) - (b * u2[i1]);
	final double d2 = (a * v0[i0]) + (b * v0[i1]) + c;
	return ((d0 * d1) > 0) && ((d0 * d2) > 0);
    }

    /**
     * 
     *
     * @return 
     */
    private static WB_IntersectionResult empty() {
	final WB_IntersectionResult i = new WB_IntersectionResult();
	i.intersection = false;
	i.sqDist = Float.POSITIVE_INFINITY;
	return i;
    }

    /**
     * 
     *
     * @param S1 
     * @param S2 
     * @return 
     */
    public static WB_IntersectionResult getIntersection2D(final WB_Segment S1,
	    final WB_Segment S2) {
	final double a1 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(),
		S1.getEndpoint(), S2.getEndpoint());
	final double a2 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(),
		S1.getEndpoint(), S2.getOrigin());
	if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && ((a1 * a2) < 0)) {
	    final double a3 = WB_Triangle.twiceSignedTriArea2D(S2.getOrigin(),
		    S2.getEndpoint(), S1.getOrigin());
	    final double a4 = (a3 + a2) - a1;
	    if ((a3 * a4) < 0) {
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

    /**
     * 
     *
     * @param S1 
     * @param S2 
     * @param i 
     */
    public static void getIntersection2DInto(final WB_Segment S1,
	    final WB_Segment S2, final WB_IntersectionResult i) {
	final double a1 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(),
		S1.getEndpoint(), S2.getEndpoint());
	final double a2 = WB_Triangle.twiceSignedTriArea2D(S1.getOrigin(),
		S1.getEndpoint(), S2.getOrigin());
	if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && ((a1 * a2) < 0)) {
	    final double a3 = WB_Triangle.twiceSignedTriArea2D(S2.getOrigin(),
		    S2.getEndpoint(), S1.getOrigin());
	    final double a4 = (a3 + a2) - a1;
	    if ((a3 * a4) < 0) {
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

    /**
     * 
     *
     * @param S 
     * @param L 
     * @return 
     */
    public static WB_Segment[] splitSegment2D(final WB_Segment S,
	    final WB_Line L) {
	final WB_Segment[] result = new WB_Segment[2];
	final WB_IntersectionResult ir2D = getClosestPoint2D(S, L);
	if (!ir2D.intersection) {
	    return null;
	}
	if (ir2D.dimension == 0) {
	    if (L.classifyPointToLine2D(S.getOrigin()) == WB_ClassificationGeometry.FRONT) {
		result[0] = new WB_Segment(S.getOrigin(),
			(WB_Point) ir2D.object);
		result[1] = new WB_Segment((WB_Point) ir2D.object,
			S.getEndpoint());
	    } else if (L.classifyPointToLine2D(S.getOrigin()) == WB_ClassificationGeometry.BACK) {
		result[1] = new WB_Segment(S.getOrigin(),
			(WB_Point) ir2D.object);
		result[0] = new WB_Segment((WB_Point) ir2D.object,
			S.getEndpoint());
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param u0 
     * @param u1 
     * @param v0 
     * @param v1 
     * @return 
     */
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

    /**
     * 
     *
     * @param poly 
     * @param L 
     * @return 
     */
    public static WB_Polygon[] splitPolygon2D(final WB_Polygon poly,
	    final WB_Line L) {
	final ArrayList<WB_Coordinate> frontVerts = new ArrayList<WB_Coordinate>(
		20);
	final ArrayList<WB_Coordinate> backVerts = new ArrayList<WB_Coordinate>(
		20);
	final int numVerts = poly.numberOfShellPoints;
	if (numVerts > 0) {
	    WB_Coordinate a = poly.getPoint(numVerts - 1);
	    WB_ClassificationGeometry aSide = L.classifyPointToLine2D(a);
	    WB_Coordinate b;
	    WB_ClassificationGeometry bSide;
	    for (int n = 0; n < numVerts; n++) {
		WB_IntersectionResult i = new WB_IntersectionResult();
		b = poly.getPoint(n);
		bSide = L.classifyPointToLine2D(b);
		if (bSide == WB_ClassificationGeometry.FRONT) {
		    if (aSide == WB_ClassificationGeometry.BACK) {
			i = getClosestPoint2D(L, new WB_Segment(a, b));
			WB_Point p1 = null;
			if (i.dimension == 0) {
			    p1 = (WB_Point) i.object;
			} else if (i.dimension == 1) {
			    p1 = ((WB_Segment) i.object).getOrigin();
			}
			frontVerts.add(p1);
			backVerts.add(p1);
		    }
		    frontVerts.add(b);
		} else if (bSide == WB_ClassificationGeometry.BACK) {
		    if (aSide == WB_ClassificationGeometry.FRONT) {
			i = getClosestPoint2D(L, new WB_Segment(a, b));
			/*
			 * if (classifyPointToPlane(i.p1, P) !=
			 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
			 * .println("Inconsistency: intersection not on plane");
			 * }
			 */
			final WB_Point p1 = (WB_Point) i.object;
			frontVerts.add(p1);
			backVerts.add(p1);
		    } else if (aSide == WB_ClassificationGeometry.ON) {
			backVerts.add(a);
		    }
		    backVerts.add(b);
		} else {
		    frontVerts.add(b);
		    if (aSide == WB_ClassificationGeometry.BACK) {
			backVerts.add(b);
		    }
		}
		a = b;
		aSide = bSide;
	    }
	}
	final WB_Polygon[] result = new WB_Polygon[2];
	result[0] = gf.createSimplePolygon(frontVerts);
	result[1] = gf.createSimplePolygon(backVerts);
	return result;
    }

    /**
     * 
     *
     * @param C0 
     * @param C1 
     * @return 
     */
    public static ArrayList<WB_Point> getIntersection2D(final WB_Circle C0,
	    final WB_Circle C1) {
	final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
	final WB_Point u = C1.getCenter().sub(C0.getCenter());
	final double d2 = u.getSqLength3D();
	final double d = Math.sqrt(d2);
	if (WB_Epsilon.isEqualAbs(d, C0.getRadius() + C1.getRadius())) {
	    result.add(gf.createInterpolatedPoint(C0.getCenter(),
		    C1.getCenter(),
		    C0.getRadius() / (C0.getRadius() + C1.getRadius())));
	    return result;
	}
	if ((d > (C0.getRadius() + C1.getRadius()))
		|| (d < WB_Math.fastAbs(C0.getRadius() - C1.getRadius()))) {
	    return result;
	}
	final double r02 = C0.getRadius() * C0.getRadius();
	final double r12 = C1.getRadius() * C1.getRadius();
	final double a = ((r02 - r12) + d2) / (2 * d);
	final double h = Math.sqrt(r02 - (a * a));
	final WB_Point c = u.mul(a / d).addSelf(C0.getCenter());
	final double p0x = c.xd()
		+ ((h * (C1.getCenter().yd() - C0.getCenter().yd())) / d);
	final double p0y = c.yd()
		- ((h * (C1.getCenter().xd() - C0.getCenter().xd())) / d);
	final double p1x = c.xd()
		- ((h * (C1.getCenter().yd() - C0.getCenter().yd())) / d);
	final double p1y = c.yd()
		+ ((h * (C1.getCenter().xd() - C0.getCenter().xd())) / d);
	final WB_Point p0 = new WB_Point(p0x, p0y);
	result.add(p0);
	final WB_Point p1 = new WB_Point(p1x, p1y);
	if (!WB_Epsilon.isZeroSq(getSqDistance2D(p0, p1))) {
	    result.add(new WB_Point(p1x, p1y));
	}
	return result;
    }

    /**
     * 
     *
     * @param L 
     * @param C 
     * @return 
     */
    public static ArrayList<WB_Point> getIntersection2D(final WB_Line L,
	    final WB_Circle C) {
	final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
	final double b = 2 * ((L.getDirection().xd() * (L.getOrigin().xd() - C
		.getCenter().xd())) + (L.getDirection().yd() * (L.getOrigin()
		.yd() - C.getCenter().yd())));
	final double c = (C.getCenter().getSqLength3D() + L.getOrigin()
		.getSqLength3D())
		- (2 * ((C.getCenter().xd() * L.getOrigin().xd()) + (C
			.getCenter().yd() * L.getOrigin().yd())))
		- (C.getRadius() * C.getRadius());
	double disc = (b * b) - (4 * c);
	if (disc < -WB_Epsilon.EPSILON) {
	    return result;
	}
	if (WB_Epsilon.isZero(disc)) {
	    result.add(L.getPointOnLine(-0.5 * b));
	    return result;
	}
	disc = Math.sqrt(disc);
	result.add(L.getPointOnLine(0.5 * (-b + disc)));
	result.add(L.getPointOnLine(0.5 * (-b - disc)));
	return result;
    }

    /**
     * 
     *
     * @param a 
     * @param b 
     * @param c 
     * @param d 
     * @return 
     */
    public static boolean getIntersection2DProper(final WB_Coordinate a,
	    final WB_Coordinate b, final WB_Coordinate c, final WB_Coordinate d) {
	if ((WB_Predicates.orient2D(a, b, c) == 0)
		|| (WB_Predicates.orient2D(a, b, d) == 0)
		|| (WB_Predicates.orient2D(c, d, a) == 0)
		|| (WB_Predicates.orient2D(c, d, b) == 0)) {
	    return false;
	} else if (((WB_Predicates.orient2D(a, b, c) * WB_Predicates.orient2D(
		a, b, d)) > 0)
		|| ((WB_Predicates.orient2D(c, d, a) * WB_Predicates.orient2D(
			c, d, b)) > 0)) {
	    return false;
	} else {
	    return true;
	}
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Coordinate p,
	    final WB_Segment S) {
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

    /**
     * 
     *
     * @param S 
     * @param p 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Segment S,
	    final WB_Coordinate p) {
	return getClosestPoint2D(p, S);
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static WB_Point getClosestPointToSegment2D(final WB_Coordinate p,
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
		return new WB_Point(a.xd() + (t * ab.xd()), a.yd()
			+ (t * ab.yd()));
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Coordinate p,
	    final WB_Line L) {
	if (WB_Epsilon.isZero(L.getDirection().xd())) {
	    return new WB_Point(L.getOrigin().xd(), p.yd());
	}
	if (WB_Epsilon.isZero(L.getDirection().yd())) {
	    return new WB_Point(p.xd(), L.getOrigin().yd());
	}
	final double m = L.getDirection().yd() / L.getDirection().xd();
	final double b = L.getOrigin().yd() - (m * L.getOrigin().xd());
	final double x = (((m * p.yd()) + p.xd()) - (m * b)) / ((m * m) + 1);
	final double y = ((m * m * p.yd()) + (m * p.xd()) + b) / ((m * m) + 1);
	return new WB_Point(x, y);
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static WB_Point getClosestPointToLine2D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b) {
	final WB_Line L = new WB_Line();
	L.setFromPoints(a, b);
	return getClosestPoint2D(p, L);
    }

    /**
     * 
     *
     * @param p 
     * @param R 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Coordinate p,
	    final WB_Ray R) {
	final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
	double t = ac.dot(R.getDirection());
	if (t <= 0) {
	    t = 0;
	    return R.getOrigin().get();
	} else {
	    return R.getPointOnLine(t);
	}
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static WB_Point getClosestPointToRay2D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b) {
	final WB_Ray R = new WB_Ray();
	R.setFromPoints(a, b);
	return getClosestPoint2D(p, R);
    }

    /**
     * 
     *
     * @param S1 
     * @param S2 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint2D(final WB_Segment S1,
	    final WB_Segment S2) {
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
	    i.sqDist = r.getSqLength3D();
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
		final double denom = (a * e) - (b * b);
		if (!WB_Epsilon.isZero(denom)) {
		    t1 = WB_Math.clamp(((b * f) - (c * e)) / denom, 0, 1);
		} else {
		    t1 = 0;
		}
		final double tnom = (b * t1) + f;
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
	i.sqDist = getSqDistance2D(p1, p2);
	if (WB_Epsilon.isZeroSq(i.sqDist)) {
	    i.dimension = 0;
	    i.object = p1;
	} else {
	    i.dimension = 1;
	    i.object = new WB_Segment(p1, p2);
	}
	return i;
    }

    /**
     * 
     *
     * @param L1 
     * @param L2 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint2D(final WB_Line L1,
	    final WB_Line L2) {
	final double a = L1.getDirection().dot(L1.getDirection());
	final double b = L1.getDirection().dot(L2.getDirection());
	final WB_Point r = L1.getOrigin().sub(L2.getOrigin());
	final double c = L1.getDirection().dot(r);
	final double e = L2.getDirection().dot(L2.getDirection());
	final double f = L2.getDirection().dot(r);
	double denom = (a * e) - (b * b);
	if (WB_Epsilon.isZero(denom)) {
	    final double t2 = r.dot(L1.getDirection());
	    final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
	    final double d2 = getSqDistance2D(L1.getOrigin().get(), p2);
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
	final double t1 = ((b * f) - (c * e)) * denom;
	final double t2 = ((a * f) - (b * c)) * denom;
	final WB_Point p1 = new WB_Point(L1.getPointOnLine(t1));
	final WB_Point p2 = new WB_Point(L2.getPointOnLine(t2));
	final double d2 = getSqDistance2D(p1, p2);
	final WB_IntersectionResult i = new WB_IntersectionResult();
	i.intersection = true;
	i.t1 = t1;
	i.t2 = t2;
	i.dimension = 0;
	i.object = p1;
	i.sqDist = d2;
	return i;
    }

    /**
     * 
     *
     * @param L 
     * @param S 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint2D(final WB_Line L,
	    final WB_Segment S) {
	final WB_IntersectionResult i = getClosestPoint2D(L,
		new WB_Line(S.getOrigin(), S.getDirection()));
	if (i.dimension == 0) {
	    return i;
	}
	if (i.t2 <= WB_Epsilon.EPSILON) {
	    i.t2 = 0;
	    i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S
		    .getOrigin().get());
	    i.sqDist = ((WB_Segment) i.object).getLength();
	    i.sqDist *= i.sqDist;
	    i.intersection = false;
	}
	if (i.t2 >= (S.getLength() - WB_Epsilon.EPSILON)) {
	    i.t2 = 1;
	    i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S
		    .getEndpoint().get());
	    i.sqDist = ((WB_Segment) i.object).getLength();
	    i.sqDist *= i.sqDist;
	    i.intersection = false;
	}
	return i;
    }

    /**
     * 
     *
     * @param S 
     * @param L 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint2D(final WB_Segment S,
	    final WB_Line L) {
	return getClosestPoint2D(L, S);
    }

    /**
     * 
     *
     * @param L 
     * @param S 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint3D(final WB_Line L,
	    final WB_Segment S) {
	final WB_IntersectionResult i = getClosestPoint3D(L,
		new WB_Line(S.getOrigin(), S.getDirection()));
	if (i.dimension == 0) {
	    return i;
	}
	if (i.t2 <= WB_Epsilon.EPSILON) {
	    i.t2 = 0;
	    i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S
		    .getOrigin().get());
	    i.sqDist = ((WB_Segment) i.object).getLength();
	    i.sqDist *= i.sqDist;
	    i.intersection = false;
	}
	if (i.t2 >= (S.getLength() - WB_Epsilon.EPSILON)) {
	    i.t2 = 1;
	    i.object = new WB_Segment(((WB_Segment) i.object).getOrigin(), S
		    .getEndpoint().get());
	    i.sqDist = ((WB_Segment) i.object).getLength();
	    i.sqDist *= i.sqDist;
	    i.intersection = false;
	}
	return i;
    }

    // POINT-TRIANGLE
    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Coordinate p,
	    final WB_Triangle T) {
	final WB_Vector ab = T.p2.subToVector3D(T.p1);
	final WB_Vector ac = T.p3.subToVector3D(T.p1);
	final WB_Vector ap = new WB_Vector(T.p1, p);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return T.p1.get();
	}
	final WB_Vector bp = new WB_Vector(T.p2, p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return T.p2.get();
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return T.p1.add(ab.mulSelf(v));
	}
	final WB_Vector cp = new WB_Vector(T.p3, p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return T.p3.get();
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return T.p1.add(ac.mulSelf(w));
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return T.p2.add((T.p3.sub(T.p2)).mulSelf(w));
	}
	final double denom = 1.0 / (va + vb + vc);
	final double v = vb * denom;
	final double w = vc * denom;
	return T.p1.add(ab.mulSelf(v).addSelf(ac.mulSelf(w)));
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @param c 
     * @return 
     */
    public static WB_Point getClosestPointToTriangle2D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b, final WB_Coordinate c) {
	final WB_Vector ab = new WB_Vector(a, b);
	final WB_Vector ac = new WB_Vector(a, c);
	final WB_Vector ap = new WB_Vector(a, p);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return new WB_Point(a);
	}
	final WB_Vector bp = new WB_Vector(b, p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return new WB_Point(b);
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return new WB_Point(a).addMulSelf(v, ab);
	}
	final WB_Vector cp = new WB_Vector(c, p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return new WB_Point(c);
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return new WB_Point(a).addMulSelf(w, ac);
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return new WB_Point(b).addMulSelf(w, new WB_Vector(b, c));
	}
	final double denom = 1.0 / (va + vb + vc);
	final double v = vb * denom;
	final double w = vc * denom;
	return new WB_Point(a).addMulSelf(w, ac).addMulSelf(v, ab);
    }

    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static WB_Point getClosestPointOnPeriphery2D(final WB_Coordinate p,
	    final WB_Triangle T) {
	final WB_Vector ab = T.p2.subToVector3D(T.p1);
	final WB_Vector ac = T.p3.subToVector3D(T.p1);
	final WB_Vector ap = new WB_Vector(T.p1, p);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return T.p1.get();
	}
	final WB_Vector bp = new WB_Vector(T.p2, p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return T.p2.get();
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return T.p1.add(ab.mulSelf(v));
	}
	final WB_Vector cp = new WB_Vector(T.p3, p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return T.p3.get();
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return T.p1.add(ac.mulSelf(w));
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return T.p2.add((T.p3.sub(T.p2)).mulSelf(w));
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
	final WB_Point A = getClosestPointToSegment2D(p, T.p2, T.p3);
	final double dA2 = getSqDistance2D(p, A);
	final WB_Point B = getClosestPointToSegment2D(p, T.p1, T.p3);
	final double dB2 = getSqDistance2D(p, B);
	final WB_Point C = getClosestPointToSegment2D(p, T.p1, T.p2);
	final double dC2 = getSqDistance2D(p, C);
	if ((dA2 < dB2) && (dA2 < dC2)) {
	    return A;
	} else if ((dB2 < dA2) && (dB2 < dC2)) {
	    return B;
	} else {
	    return C;
	}
    }

    // POINT-POLYGON
    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Coordinate p,
	    final WB_Polygon poly) {
	final int[][] tris = poly.getTriangles();
	final int n = tris.length;
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	int[] T;
	for (int i = 0; i < n; i++) {
	    T = tris[i];
	    tmp = getClosestPointToTriangle2D(p, poly.getPoint(T[0]),
		    poly.getPoint(T[1]), poly.getPoint(T[2]));
	    final double d2 = getDistance2D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	return closest;
    }

    /**
     * 
     *
     * @param p 
     * @param tris 
     * @return 
     */
    public static WB_Point getClosestPoint2D(final WB_Coordinate p,
	    final ArrayList<? extends WB_Triangle> tris) {
	final int n = tris.size();
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	WB_Triangle T;
	for (int i = 0; i < n; i++) {
	    T = tris.get(i);
	    tmp = getClosestPoint2D(p, T);
	    final double d2 = getDistance2D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	return closest;
    }

    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public static WB_Point getClosestPointOnPeriphery2D(final WB_Coordinate p,
	    final WB_Polygon poly) {
	final int[][] tris = poly.getTriangles();
	final int n = tris.length;
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	int[] T;
	for (int i = 0; i < n; i++) {
	    T = tris[i];
	    tmp = getClosestPointToTriangle2D(p, poly.getPoint(T[0]),
		    poly.getPoint(T[1]), poly.getPoint(T[2]));
	    final double d2 = getSqDistance2D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	if (WB_Epsilon.isZeroSq(dmax2)) {
	    dmax2 = Double.POSITIVE_INFINITY;
	    WB_Segment S;
	    for (int i = 0, j = poly.getNumberOfShellPoints() - 1; i < poly
		    .getNumberOfShellPoints(); j = i, i++) {
		S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
		tmp = getClosestPoint2D(p, S);
		final double d2 = getSqDistance2D(tmp, p);
		if (d2 < dmax2) {
		    closest = tmp;
		    dmax2 = d2;
		}
	    }
	}
	return closest;
    }

    /**
     * 
     *
     * @param p 
     * @param poly 
     * @param tris 
     * @return 
     */
    public static WB_Point getClosestPointOnPeriphery2D(final WB_Coordinate p,
	    final WB_Polygon poly, final ArrayList<WB_Triangle> tris) {
	final int n = tris.size();
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	WB_Triangle T;
	for (int i = 0; i < n; i++) {
	    T = tris.get(i);
	    tmp = getClosestPoint2D(p, T);
	    final double d2 = getSqDistance2D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	if (WB_Epsilon.isZeroSq(dmax2)) {
	    dmax2 = Double.POSITIVE_INFINITY;
	    WB_Segment S;
	    for (int i = 0, j = poly.getNumberOfShellPoints() - 1; i < poly
		    .getNumberOfShellPoints(); j = i, i++) {
		S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
		tmp = getClosestPoint2D(p, S);
		final double d2 = getSqDistance2D(tmp, p);
		if (d2 < dmax2) {
		    closest = tmp;
		    dmax2 = d2;
		}
	    }
	}
	return closest;
    }

    /**
     * 
     *
     * @param S1 
     * @param S2 
     * @return 
     */
    public static WB_IntersectionResult getClosestPoint3D(final WB_Segment S1,
	    final WB_Segment S2) {
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
	    i.sqDist = r.getSqLength3D();
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
		final double denom = (a * e) - (b * b);
		if (!WB_Epsilon.isZero(denom)) {
		    t1 = WB_Math.clamp(((b * f) - (c * e)) / denom, 0, 1);
		} else {
		    t1 = 0;
		}
		final double tnom = (b * t1) + f;
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
	i.sqDist = getSqDistance2D(p1, p2);
	if (WB_Epsilon.isZeroSq(i.sqDist)) {
	    i.dimension = 0;
	    i.object = p1;
	} else {
	    i.dimension = 1;
	    i.object = new WB_Segment(p1, p2);
	}
	return i;
    }

    // POINT-POLYGON
    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public static WB_Point getClosestPoint3D(final WB_Coordinate p,
	    final WB_Polygon poly) {
	final int[][] tris = poly.getTriangles();
	final int n = tris.length;
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	int[] T;
	for (int i = 0; i < n; i++) {
	    T = tris[i];
	    tmp = getClosestPointToTriangle3D(p, poly.getPoint(T[0]),
		    poly.getPoint(T[1]), poly.getPoint(T[2]));
	    final double d2 = getSqDistance3D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	return closest;
    }

    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public static WB_Point getClosestPointOnPeriphery3D(final WB_Coordinate p,
	    final WB_Polygon poly) {
	final int[][] tris = poly.getTriangles();
	final int n = tris.length;
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	int[] T;
	for (int i = 0; i < n; i++) {
	    T = tris[i];
	    tmp = getClosestPointToTriangle3D(p, poly.getPoint(T[0]),
		    poly.getPoint(T[1]), poly.getPoint(T[2]));
	    final double d2 = getSqDistance3D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	if (WB_Epsilon.isZeroSq(dmax2)) {
	    dmax2 = Double.POSITIVE_INFINITY;
	    WB_Segment S;
	    for (int i = 0, j = poly.getNumberOfPoints() - 1; i < poly
		    .getNumberOfPoints(); j = i, i++) {
		S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
		tmp = getClosestPoint3D(p, S);
		final double d2 = getSqDistance3D(tmp, p);
		if (d2 < dmax2) {
		    closest = tmp;
		    dmax2 = d2;
		}
	    }
	}
	return closest;
    }

    // TODO: correct for polygons with holes
    /**
     * 
     *
     * @param p 
     * @param poly 
     * @param tris 
     * @return 
     */
    public static WB_Point getClosestPointOnPeriphery3D(final WB_Coordinate p,
	    final WB_Polygon poly, final List<? extends WB_Triangle> tris) {
	final int n = tris.size();
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = new WB_Point();
	WB_Point tmp;
	WB_Triangle T;
	for (int i = 0; i < n; i++) {
	    T = tris.get(i);
	    tmp = getClosestPoint3D(p, T);
	    final double d2 = getSqDistance3D(tmp, p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
	    }
	}
	if (WB_Epsilon.isZeroSq(dmax2)) {
	    dmax2 = Double.POSITIVE_INFINITY;
	    WB_Segment S;
	    for (int i = 0, j = poly.getNumberOfPoints() - 1; i < poly
		    .getNumberOfPoints(); j = i, i++) {
		S = new WB_Segment(poly.getPoint(j), poly.getPoint(i));
		tmp = getClosestPoint3D(p, S);
		final double d2 = getSqDistance3D(tmp, p);
		if (d2 < dmax2) {
		    closest = tmp;
		    dmax2 = d2;
		}
	    }
	}
	return closest;
    }

    /**
     * 
     *
     * @param ray 
     * @param poly 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(final WB_Ray ray,
	    final WB_Polygon poly) {
	final WB_IntersectionResult ir = getIntersection3D(ray, poly.getPlane());
	if (ir.intersection == false) {
	    return ir;
	}
	final WB_Point p = (WB_Point) ir.object;
	final WB_Point q = getClosestPoint3D(p, poly);
	final double d2 = getSqDistance3D(q, p);
	if (WB_Epsilon.isZeroSq(d2)) {
	    return ir;
	}
	ir.intersection = false;
	return ir;
    }

    /**
     * 
     *
     * @param line 
     * @param poly 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(final WB_Line line,
	    final WB_Polygon poly) {
	final WB_IntersectionResult ir = getIntersection3D(line,
		poly.getPlane());
	if (ir.intersection == false) {
	    return ir;
	}
	final WB_Point p = (WB_Point) ir.object;
	final WB_Point q = getClosestPoint3D(p, poly);
	final double d2 = getSqDistance3D(q, p);
	if (WB_Epsilon.isZeroSq(d2)) {
	    return ir;
	}
	ir.intersection = false;
	return ir;
    }

    /**
     * 
     *
     * @param segment 
     * @param poly 
     * @return 
     */
    public static WB_IntersectionResult getIntersection3D(
	    final WB_Segment segment, final WB_Polygon poly) {
	final WB_IntersectionResult ir = getIntersection3D(segment,
		poly.getPlane());
	if (ir.intersection == false) {
	    return ir;
	}
	final WB_Point p = (WB_Point) ir.object;
	final WB_Point q = getClosestPoint3D(p, poly);
	final double d2 = getSqDistance3D(q, p);
	if (WB_Epsilon.isZeroSq(d2)) {
	    return ir;
	}
	ir.intersection = false;
	return ir;
    }

    /**
     * 
     *
     * @param a 
     * @param b 
     * @param c 
     * @return 
     */
    public static boolean between2D(final WB_Coordinate a,
	    final WB_Coordinate b, final WB_Coordinate c) {
	if (coincident2D(a, c)) {
	    return true;
	} else if (coincident2D(b, c)) {
	    return true;
	} else {
	    if (getSqDistanceToLine2D(c, a, b) < WB_Epsilon.SQEPSILON) {
		final double d = projectedDistanceNorm(c, a, b);
		if ((0 < d) && (d < 1)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * 
     *
     * @param a 
     * @param b 
     * @param c 
     * @return 
     */
    public static boolean betweenStrict2D(final WB_Coordinate a,
	    final WB_Coordinate b, final WB_Coordinate c) {
	if (coincident2D(a, c)) {
	    return true;
	} else if (coincident2D(b, c)) {
	    return true;
	} else {
	    if (getSqDistanceToLine2D(c, a, b) < WB_Epsilon.SQEPSILON) {
		final double d = projectedDistanceNorm(c, a, b);
		if ((0 < d) && (d < 1)) {
		    return true;
		}
	    }
	}
	return false;
    }

    /**
     * 
     *
     * @param a 
     * @param b 
     * @return 
     */
    public static boolean coincident2D(final WB_Coordinate a,
	    final WB_Coordinate b) {
	if (getSqDistance2D(a, b) < WB_Epsilon.SQEPSILON) {
	    return true;
	}
	return false;
    }

    /**
     * 
     *
     * @param a 
     * @param b 
     * @param p 
     * @return 
     */
    public static double projectedDistanceNorm(final WB_Coordinate a,
	    final WB_Coordinate b, final WB_Coordinate p) {
	double x1, x2, y1, y2;
	x1 = b.xd() - a.xd();
	x2 = p.xd() - a.xd();
	y1 = b.yd() - a.yd();
	y2 = p.yd() - a.yd();
	return ((x1 * x2) + (y1 * y2)) / ((x1 * x1) + (y1 * y1));
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static double pointAlongLine(final WB_Coordinate p, final WB_Line L) {
	final WB_Vector ab = L.getDirection();
	final WB_Vector ac = new WB_Vector(p);
	ac.subSelf(L.getOrigin());
	return ac.dot(ab);
    }

    /**
     * 
     *
     * @param p 
     * @param tree 
     * @return 
     */
    public static boolean contains(final WB_Coordinate p, final WB_AABBTree tree) {
	final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
	queue.add(tree.getRoot());
	WB_AABBNode current;
	while (!queue.isEmpty()) {
	    current = queue.pop();
	    if (contains(p, current.getAABB())) {
		if (current.isLeaf()) {
		    return true;
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
	return false;
    }

    /**
     * 
     *
     * @param p 
     * @param AABB 
     * @return 
     */
    public static boolean contains(final WB_Coordinate p, final WB_AABB AABB) {
	return (p.xd() >= AABB.getMinX()) && (p.yd() >= AABB.getMinY())
		&& (p.zd() >= AABB.getMinZ()) && (p.xd() < AABB.getMaxX())
		&& (p.yd() < AABB.getMaxY()) && (p.zd() < AABB.getMaxZ());
    }

    /**
     * 
     *
     * @param p1 
     * @param p2 
     * @param A 
     * @param B 
     * @return 
     */
    public static boolean sameSide(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate A, final WB_Coordinate B) {
	final WB_Point t1 = new WB_Point(B).subSelf(A);
	WB_Point t2 = new WB_Point(p1).subSelf(A);
	WB_Point t3 = new WB_Point(p2).subSelf(A);
	t2 = t1.cross(t2);
	t3 = t1.cross(t3);
	final double t = t2.dot(t3);
	if (t >= WB_Epsilon.EPSILON) {
	    return true;
	}
	return false;
    }

    /**
     * 
     *
     * @param p 
     * @param A 
     * @param B 
     * @param C 
     * @return 
     */
    public static boolean contains(final WB_Coordinate p,
	    final WB_Coordinate A, final WB_Coordinate B, final WB_Coordinate C) {
	if (WB_Epsilon.isZeroSq(getSqDistanceToLine3D(A, B, C))) {
	    return false;
	}
	if (sameSide(p, A, B, C) && sameSide(p, B, A, C)
		&& sameSide(p, C, A, B)) {
	    return true;
	}
	return false;
    }

    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static boolean contains(final WB_Coordinate p, final WB_Triangle T) {
	return contains(p, T.p1(), T.p2(), T.p3());
    }

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static WB_Point projectOnPlane(final WB_Coordinate p,
	    final WB_Plane P) {
	final WB_Point projection = new WB_Point(p);
	final WB_Vector po = new WB_Vector(P.getOrigin(), p);
	final WB_Vector n = P.getNormal();
	return projection.subSelf(n.mulSelf(n.dot(po)));
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static double distanceToLine2D(final WB_Coordinate p, final WB_Line L) {
	return Math.sqrt(getSqDistanceToLine2D(p, L));
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static double getDistance2D(final WB_Coordinate p, final WB_Segment S) {
	return Math.sqrt(getSqDistance2D(p, S));
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public static double getDistance2D(final WB_Coordinate p,
	    final WB_Coordinate q) {
	return Math.sqrt(getSqDistance2D(p, q));
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static double getDistance2D(final WB_Coordinate p, final WB_Line L) {
	return Math.sqrt(getSqDistance2D(p, L));
    }

    /**
     * 
     *
     * @param p 
     * @param R 
     * @return 
     */
    public static double getDistance2D(final WB_Coordinate p, final WB_Ray R) {
	return Math.sqrt(getSqDistance2D(p, R));
    }

    /**
     * 
     *
     * @param S 
     * @param T 
     * @return 
     */
    public static double getDistance3D(final WB_Segment S, final WB_Segment T) {
	return Math.sqrt(WB_GeometryOp.getIntersection3D(S, T).sqDist);
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p, final WB_Segment S) {
	return Math.sqrt(getSqDistance3D(p, S));
    }

    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p,
	    final WB_Polygon poly) {
	return Math.sqrt(getSqDistance3D(p, poly));
    }

    /**
     * 
     *
     * @param p 
     * @param AABB 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p, final WB_AABB AABB) {
	return Math.sqrt(getSqDistance3D(p, AABB));
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p,
	    final WB_Coordinate q) {
	return Math.sqrt(getSqDistance3D(p, q));
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p, final WB_Line L) {
	return Math.sqrt(getSqDistance3D(p, L));
    }

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p, final WB_Plane P) {
	return P.getNormal().dot(p) - P.d();
    }

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static double getDistance3D(final double p[], final WB_Plane P) {
	final WB_Vector n = P.getNormal();
	return ((n.xd() * p[0]) + (n.yd() * p[1]) + (n.zd() * p[2])) - P.d();
    }

    /**
     * 
     *
     * @param p 
     * @param R 
     * @return 
     */
    public static double getDistance3D(final WB_Coordinate p, final WB_Ray R) {
	return Math.sqrt(getSqDistance3D(p, R));
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
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

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static double getDistanceToPlane3D(final WB_Coordinate p,
	    final WB_Plane P) {
	final double d = P.getNormal().dot(p) - P.d();
	return (d < 0) ? -d : d;
    }

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static double getDistanceToPlane3D(final double[] p, final WB_Plane P) {
	final WB_Vector v = P.getNormal();
	final double d = ((v.xd() * p[0]) + (v.yd() * p[1]) + (v.zd() * p[2]))
		- P.d();
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

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
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

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
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

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public static double getSqLength2D(final WB_Coordinate p) {
	return ((p.xd() * p.xd()) + (p.yf() * p.yf()));
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public static double getSqLength3D(final WB_Coordinate p) {
	return ((p.xd() * p.xd()) + (p.yf() * p.yf()) + (p.zf() * p.zf()));
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public static double getLength2D(final WB_Coordinate p) {
	return Math.sqrt((p.xd() * p.xd()) + (p.yf() * p.yf()));
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public static double getLength3D(final WB_Coordinate p) {
	return Math.sqrt((p.xd() * p.xd()) + (p.yf() * p.yf())
		+ (p.zf() * p.zf()));
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static double getSqDistance2D(final WB_Coordinate p,
	    final WB_Segment S) {
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
	return ac.dot2D(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public static double getSqDistance2D(final WB_Coordinate p,
	    final WB_Coordinate q) {
	return (((q.xd() - p.xd()) * (q.xd() - p.xd())) + ((q.yd() - p.yd()) * (q
		.yd() - p.yd())));
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static double getSqDistance2D(final WB_Coordinate p, final WB_Line L) {
	final WB_Vector ab = L.getDirection();
	final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
	final double e = ac.dot2D(ab);
	final double f = ab.dot2D(ab);
	return ac.dot2D(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param p 
     * @param R 
     * @return 
     */
    public static double getSqDistance2D(final WB_Coordinate p, final WB_Ray R) {
	final WB_Vector ab = R.getDirection();
	final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
	final double e = ac.dot2D(ab);
	if (e <= 0) {
	    return ac.dot2D(ac);
	}
	final double f = ab.dot2D(ab);
	return ac.dot2D(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param S 
     * @param T 
     * @return 
     */
    public static double getSqDistance3D(final WB_Segment S, final WB_Segment T) {
	return WB_GeometryOp.getIntersection3D(S, T).sqDist;
    }

    /**
     * 
     *
     * @param p 
     * @param S 
     * @return 
     */
    public static double getSqDistance3D(final WB_Coordinate p,
	    final WB_Segment S) {
	final WB_Vector ab = S.getEndpoint().subToVector3D(S.getOrigin());
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
	return ac.dot(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public static double getSqDistance3D(final WB_Coordinate p,
	    final WB_Polygon poly) {
	final int[][] tris = poly.getTriangles();
	final int n = tris.length;
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Coordinate tmp;
	int[] T;
	for (int i = 0; i < n; i++) {
	    T = tris[i];
	    tmp = WB_GeometryOp.getClosestPointToTriangle3D(p,
		    poly.getPoint(T[0]), poly.getPoint(T[1]),
		    poly.getPoint(T[2]));
	    final double d2 = getDistance3D(tmp, p);
	    if (d2 < dmax2) {
		dmax2 = d2;
		if (WB_Epsilon.isZeroSq(dmax2)) {
		    break;
		}
	    }
	}
	return dmax2;
    }

    /**
     * 
     *
     * @param p 
     * @param AABB 
     * @return 
     */
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
    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public static double getSqDistance3D(final WB_Coordinate p,
	    final WB_Coordinate q) {
	return (((q.xd() - p.xd()) * (q.xd() - p.xd()))
		+ ((q.yd() - p.yd()) * (q.yd() - p.yd())) + ((q.zd() - p.zd()) * (q
		.zd() - p.zd())));
    }

    /**
     * 
     *
     * @param p 
     * @param L 
     * @return 
     */
    public static double getSqDistance3D(final WB_Coordinate p, final WB_Line L) {
	final WB_Vector ab = L.getDirection();
	final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
	final double e = ac.dot(ab);
	final double f = ab.dot(ab);
	return ac.dot(ac) - ((e * e) / f);
    }

    // POINT-PLANE
    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static double getSqDistance3D(final WB_Coordinate p, final WB_Plane P) {
	final double d = P.getNormal().dot(p) - P.d();
	return d * d;
    }

    /**
     * 
     *
     * @param p 
     * @param R 
     * @return 
     */
    public static double getSqDistance3D(final WB_Coordinate p, final WB_Ray R) {
	final WB_Vector ab = R.getDirection();
	final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
	final double e = ac.dot(ab);
	if (e <= 0) {
	    return ac.dot(ac);
	}
	final double f = ab.dot(ab);
	return ac.dot(ac) - ((e * e) / f);
    }

    // POINT-SEGMENT
    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static double getSqDistanceToLine2D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b) {
	final WB_Vector ab = new WB_Vector(a, b);
	final WB_Vector ac = new WB_Vector(a, p);
	final double e = ac.dot2D(ab);
	final double f = ab.dot2D(ab);
	return ac.dot2D(ac) - ((e * e) / f);
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
	final WB_Point ab = gf.createPoint(L.getDirection().xd(), L
		.getDirection().yd());
	final WB_Point ac = gf.createPoint(p.xd() - L.getOrigin().xd(), p.yd()
		- L.getOrigin().yd());
	final double e = ac.dot2D(ab);
	final double f = ab.dot2D(ab);
	return ac.dot2D(ac) - ((e * e) / f);
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
	return ac.dot(ac) - ((e * e) / f);
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
	final WB_Vector ac = gf.createVectorFromTo(L.getOrigin(), p);
	final double e = ac.dot(ab);
	final double f = ab.dot(ab);
	return ac.dot(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
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
	return (((q.xd() - p.xd()) * (q.xd() - p.xd())) + ((q.yd() - p.yd()) * (q
		.yd() - p.yd())));
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
	return (((q.xd() - p.xd()) * (q.xd() - p.xd()))
		+ ((q.yd() - p.yd()) * (q.yd() - p.yd())) + ((q.zd() - p.zd()) * (q
		.zd() - p.zd())));
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
    public static double getSqDistanceToRay2D(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b) {
	final WB_Vector ab = new WB_Vector(a, b);
	final WB_Vector ac = new WB_Vector(a, p);
	final double e = ac.dot2D(ab);
	if (e <= 0) {
	    return ac.dot2D(ac);
	}
	final double f = ab.dot2D(ab);
	return ac.dot2D(ac) - ((e * e) / f);
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
	final WB_Vector ab = gf.createVectorFromTo(a, b);
	final WB_Vector ac = gf.createVectorFromTo(a, p);
	final double e = ac.dot(ab);
	if (e <= 0) {
	    return ac.dot(ac);
	}
	final double f = ab.dot(ab);
	return ac.dot(ac) - ((e * e) / f);
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
	final WB_Vector ac = gf.createVectorFromTo(R.getOrigin(), p);
	final double e = ac.dot(ab);
	if (e <= 0) {
	    return ac.dot(ac);
	}
	final double f = ab.dot(ab);
	return ac.dot(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @return 
     */
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
	return ac.dot2D(ac) - ((e * e) / f);
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
	final WB_Vector ab = gf.createVectorFromTo(a, b);
	final WB_Vector ac = gf.createVectorFromTo(a, p);
	final WB_Vector bc = gf.createVectorFromTo(b, p);
	final double e = ac.dot(ab);
	if (e <= 0) {
	    return ac.dot(ac);
	}
	final double f = ab.dot(ab);
	if (e >= f) {
	    return bc.dot(bc);
	}
	return ac.dot(ac) - ((e * e) / f);
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
	final WB_Point ab = gf.createPoint(S.getEndpoint()).sub(S.getOrigin());
	final WB_Point ac = gf.createPoint(p).sub(S.getOrigin());
	final WB_Point bc = gf.createPoint(p).sub(S.getEndpoint());
	final double e = ac.dot(ab);
	if (e <= 0) {
	    return ac.dot(ac);
	}
	final double f = ab.dot(ab);
	if (e >= f) {
	    return bc.dot(bc);
	}
	return ac.dot(ac) - ((e * e) / f);
    }

    /**
     * 
     *
     * @param p 
     * @param P 
     * @return 
     */
    public static double signedDistanceToPlane3D(final WB_Coordinate p,
	    final WB_Plane P) {
	final double d = P.getNormal().dot(p) - P.d();
	return d;
    }
}
