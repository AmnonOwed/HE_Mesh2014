/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Epsilon;

/**
 * 
 */
public class WB_Classify {
    
    /**
     * 
     */
    final public static WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * Classify 2D point to 2D line.
     *
     * @param p
     *            2D point
     * @param L
     * @return WB_ClassificationGeometry.FRONT, WB_ClassificationGeometry.BACK,
     *         WB_ClassificationGeometry.ON
     */
    public static WB_ClassificationGeometry classifyPointToLine2D(
	    final WB_Coordinate p, final WB_Line L) {
	final double dist = ((-L.getDirection().yd() * p.xd())
		+ (L.getDirection().xd() * p.yd()) + (L.getOrigin().xd() * L
		.getDirection().yd()))
		- (L.getOrigin().yd() * L.getDirection().xd());
	if (dist > WB_Epsilon.EPSILON) {
	    return WB_ClassificationGeometry.FRONT;
	}
	if (dist < -WB_Epsilon.EPSILON) {
	    return WB_ClassificationGeometry.BACK;
	}
	return WB_ClassificationGeometry.ON;
    }

    /**
     * Classify 2D point to circle.
     *
     * @param p
     * @param C
     * @return WB_ClassificationGeometry.INSIDE,
     *         WB_ClassificationGeometry.OUTSIDE, WB_ClassificationGeometry.ON
     */
    public static WB_ClassificationGeometry classifyPointToCircle2D(
	    final WB_Coordinate p, final WB_Circle C) {
	final double dist = WB_GeometryOp
		.getDistanceToPoint2D(p, C.getCenter());
	if (WB_Epsilon.isZero(dist - C.getRadius())) {
	    return WB_ClassificationGeometry.ON;
	} else if (dist < C.getRadius()) {
	    return WB_ClassificationGeometry.INSIDE;
	} else {
	    return WB_ClassificationGeometry.OUTSIDE;
	}
    }

    /**
     * Classify circle C1 to circle C2.
     *
     * @param C1
     * @param C2
     * @return WB_ClassificationGeometry.INSIDE: C1 inside C2
     *         WB_ClassificationGeometry.CONTAINING: C2 inside C1
     *         WB_ClassificationGeometry.OUTSIDE: C1 outside C2
     *         WB_ClassificationGeometry.CROSSING:C1 intersecting C2
     *         WB_ClassificationGeometry.ON: C1=C2
     */
    public static WB_ClassificationGeometry classifyCircleToCircle2D(
	    final WB_Circle C1, final WB_Circle C2) {
	if (C1.equals(C2)) {
	    return WB_ClassificationGeometry.ON;
	}
	final double dist = WB_GeometryOp.getDistanceToPoint2D(C1.getCenter(),
		C2.getCenter());
	final double rsum = C1.getRadius() + C2.getRadius();
	final double rdiff = Math.abs(C1.getRadius() - C2.getRadius());
	if (dist >= rsum) {
	    return WB_ClassificationGeometry.OUTSIDE;
	} else if (dist <= rdiff) {
	    if (C1.getRadius() < C2.getRadius()) {
		return WB_ClassificationGeometry.INSIDE;
	    } else {
		return WB_ClassificationGeometry.CONTAINING;
	    }
	}
	return WB_ClassificationGeometry.CROSSING;
    }

    /**
     * Classify circle C to line L.
     *
     * @param C
     * @param L
     * @return WB_ClassificationGeometry.CROSSING: C crosses L
     *         WB_ClassificationGeometry.OUTSIDE: C outside of L
     *         WB_ClassificationGeometry.TANGENT:C is tangent to L
     */
    public static WB_ClassificationGeometry classifyCircleToLine2D(
	    final WB_Circle C, final WB_Line L) {
	final double d = WB_GeometryOp.distanceToLine2D(C.getCenter(), L);
	if (WB_Epsilon.isZero(d - C.getRadius())) {
	    return WB_ClassificationGeometry.TANGENT;
	} else if (d < C.getRadius()) {
	    return WB_ClassificationGeometry.CROSSING;
	}
	return WB_ClassificationGeometry.OUTSIDE;
    }

    /**
     * Check if two 2D points are on the same side of 2D line. If one or both of
     * the points are on the line they are considered to be on the same side.
     *
     * @param p
     *            2D point
     * @param q
     *            2D point
     * @param L
     *            2D line
     * @return WB_ClassificationGeometry.SAME, WB_ClassificationGeometry.DIFF
     */
    public static WB_ClassificationGeometry sameSideOfLine2D(
	    final WB_Coordinate p, final WB_Coordinate q, final WB_Line L) {
	final WB_Predicates pred = new WB_Predicates();
	final WB_Point pL = L.getPointOnLine(1.0);
	final double pside = Math.signum(pred.orientTri(L.getOrigin(), pL, p));
	final double qside = Math.signum(pred.orientTri(L.getOrigin(), pL, q));
	if ((pside == 0) || (qside == 0) || (pside == qside)) {
	    return WB_ClassificationGeometry.SAME;
	}
	return WB_ClassificationGeometry.DIFF;
    }

    /**
     * Classify a 2D segment to 2D line.
     *
     * @param seg
     *            2D segment
     * @param L
     *            2D line
     * @return WB_ClassificationGeometry.ON, WB_ClassificationGeometry.FRONT,
     *         WB_ClassificationGeometry.BACK or
     *         WB_ClassificationGeometry.CROSSING
     */
    public static WB_ClassificationGeometry classifySegmentToLine2D(
	    final WB_Segment seg, final WB_Line L) {
	final WB_ClassificationGeometry a = classifyPointToLine2D(
		seg.getOrigin(), L);
	final WB_ClassificationGeometry b = classifyPointToLine2D(
		seg.getEndpoint(), L);
	if (a == WB_ClassificationGeometry.ON) {
	    if (b == WB_ClassificationGeometry.ON) {
		return WB_ClassificationGeometry.ON;
	    } else if (b == WB_ClassificationGeometry.FRONT) {
		return WB_ClassificationGeometry.FRONT;
	    } else {
		return WB_ClassificationGeometry.BACK;
	    }
	}
	if (b == WB_ClassificationGeometry.ON) {
	    if (a == WB_ClassificationGeometry.FRONT) {
		return WB_ClassificationGeometry.FRONT;
	    } else {
		return WB_ClassificationGeometry.BACK;
	    }
	}
	if ((a == WB_ClassificationGeometry.FRONT)
		&& (b == WB_ClassificationGeometry.BACK)) {
	    return WB_ClassificationGeometry.CROSSING;
	}
	if ((a == WB_ClassificationGeometry.BACK)
		&& (b == WB_ClassificationGeometry.FRONT)) {
	    return WB_ClassificationGeometry.CROSSING;
	}
	if (a == WB_ClassificationGeometry.FRONT) {
	    return WB_ClassificationGeometry.FRONT;
	}
	return WB_ClassificationGeometry.BACK;
    }

    /**
     * Classify 2D polygon to 2D line.
     *
     * @param P
     *            2D polygon
     * @param L
     *            2D line
     * @return WB_ClassificationGeometry.FRONT, WB_ClassificationGeometry.BACK
     *         or WB_ClassificationGeometry.CROSSING
     */
    public static WB_ClassificationGeometry classifyPolygonToLine2D(
	    final WB_Polygon P, final WB_Line L) {
	int numFront = 0;
	int numBack = 0;
	for (int i = 0; i < P.getNumberOfPoints(); i++) {
	    if (classifyPointToLine2D(P.getPoint(i), L) == WB_ClassificationGeometry.FRONT) {
		numFront++;
	    } else if (classifyPointToLine2D(P.getPoint(i), L) == WB_ClassificationGeometry.BACK) {
		numBack++;
	    }
	    if ((numFront > 0) && (numBack > 0)) {
		return WB_ClassificationGeometry.CROSSING;
	    }
	}
	if (numFront > 0) {
	    return WB_ClassificationGeometry.FRONT;
	}
	if (numBack > 0) {
	    return WB_ClassificationGeometry.BACK;
	}
	return null;
    }

    /**
     *
     * @param p
     * @param P
     * @return
     */
    public static WB_ClassificationGeometry classifyPointToPlaneFast3D(
	    final WB_Coordinate p, final WB_Plane P) {
	return classifyPointToPlaneFast3D(P, p);
    }

    /**
     *
     * @param p
     * @param P
     * @return
     */
    public static WB_ClassificationGeometry classifyPointToPlane3D(
	    final WB_Coordinate p, final WB_Plane P) {
	return classifyPointToPlane3D(P, p);
    }

    /**
     *
     * @param P
     * @param p
     * @return
     */
    public static WB_ClassificationGeometry classifyPointToPlaneFast3D(
	    final WB_Plane P, final WB_Coordinate p) {
	final double signp = WB_GeometryOp.signedDistanceToPlane3D(p, P);
	if (WB_Epsilon.isZero(signp)) {
	    return WB_ClassificationGeometry.ON;
	}
	if (signp > 0) {
	    return WB_ClassificationGeometry.FRONT;
	}
	return WB_ClassificationGeometry.BACK;
    }

    /**
     *
     * @param P
     * @param p
     * @return
     */
    public static WB_ClassificationGeometry classifyPointToPlane3D(
	    final WB_Plane P, final WB_Coordinate p) {
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getDistanceToPlane3D(p, P))) {
	    return WB_ClassificationGeometry.ON;
	}
	final WB_Predicates predicates = new WB_Predicates();
	final double signp = predicates.orientTetra(P.getOrigin(), P
		.getOrigin().addMul(100, P.getU()),
		P.getOrigin().addMul(100, P.getV()), p);
	if (signp == 0) {
	    return WB_ClassificationGeometry.ON;
	}
	if (signp < 0) {
	    return WB_ClassificationGeometry.FRONT;
	}
	return WB_ClassificationGeometry.BACK;
    }

    /**
     *
     * @param T
     * @param p
     * @return
     */
    public static WB_ClassificationGeometry classifyPointToTetrahedron3D(
	    final WB_Tetrahedron T, final WB_Coordinate p) {
	final WB_Plane pl012 = geometryfactory.createPlane(T.p1(), T.p2(),
		T.p3());
	final WB_Plane pl013 = geometryfactory.createPlane(T.p1(), T.p2(),
		T.p4());
	final WB_Plane pl023 = geometryfactory.createPlane(T.p1(), T.p3(),
		T.p4());
	final WB_Plane pl123 = geometryfactory.createPlane(T.p2(), T.p3(),
		T.p4());
	int on = 0;
	int front = 0;
	int back = 0;
	final WB_ClassificationGeometry c012 = classifyPointToPlane3D(pl012, p);
	if (c012 == WB_ClassificationGeometry.ON) {
	    on++;
	} else if (c012 == WB_ClassificationGeometry.FRONT) {
	    front++;
	} else {
	    back++;
	}
	final WB_ClassificationGeometry c013 = classifyPointToPlane3D(pl013, p);
	if (c013 == WB_ClassificationGeometry.ON) {
	    on++;
	} else if (c013 == WB_ClassificationGeometry.FRONT) {
	    front++;
	} else {
	    back++;
	}
	final WB_ClassificationGeometry c023 = classifyPointToPlane3D(pl023, p);
	if (c023 == WB_ClassificationGeometry.ON) {
	    on++;
	} else if (c023 == WB_ClassificationGeometry.FRONT) {
	    front++;
	} else {
	    back++;
	}
	final WB_ClassificationGeometry c123 = classifyPointToPlane3D(pl123, p);
	if (c123 == WB_ClassificationGeometry.ON) {
	    on++;
	} else if (c123 == WB_ClassificationGeometry.FRONT) {
	    front++;
	} else {
	    back++;
	}
	if ((front == 4) || (back == 4)) {
	    return WB_ClassificationGeometry.INSIDE;
	}
	if (((front + on) == 4) || ((back + on) == 4)) {
	    return WB_ClassificationGeometry.ON;
	}
	return WB_ClassificationGeometry.OUTSIDE;
    }

    /**
     *
     * @param poly
     * @param P
     * @return
     */
    public static WB_ClassificationGeometry classifyPolygonToPlane3D(
	    final WB_Polygon poly, final WB_Plane P) {
	int numInFront = 0;
	int numBehind = 0;
	for (int i = 0; i < poly.getNumberOfPoints(); i++) {
	    switch (classifyPointToPlane3D(P, poly.getPoint(i))) {
	    case FRONT:
		numInFront++;
		break;
	    case BACK:
		numBehind++;
		break;
	    }
	    if ((numBehind != 0) && (numInFront != 0)) {
		return WB_ClassificationGeometry.CROSSING;
	    }
	}
	if (numInFront != 0) {
	    return WB_ClassificationGeometry.FRONT;
	}
	if (numBehind != 0) {
	    return WB_ClassificationGeometry.BACK;
	}
	return WB_ClassificationGeometry.ON;
    }

    /**
     *
     * @param poly
     * @param P
     * @return
     */
    public static WB_ClassificationGeometry classifyPolygonToPlaneFast3D(
	    final WB_Polygon poly, final WB_Plane P) {
	int numInFront = 0;
	int numBehind = 0;
	double d;
	for (int i = 0; i < poly.getNumberOfPoints(); i++) {
	    d = WB_GeometryOp.signedDistanceToPlane3D(poly.getPoint(i), P);
	    if (d > WB_Epsilon.EPSILON) {
		numInFront++;
	    } else if (d < -WB_Epsilon.EPSILON) {
		numBehind++;
	    }
	    if ((numBehind != 0) && (numInFront != 0)) {
		return WB_ClassificationGeometry.CROSSING;
	    }
	}
	if (numInFront != 0) {
	    return WB_ClassificationGeometry.FRONT;
	}
	if (numBehind != 0) {
	    return WB_ClassificationGeometry.BACK;
	}
	return WB_ClassificationGeometry.ON;
    }
}
