/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_Triangle implements WB_Simplex {
    /** First point. */
    WB_Point p1;
    /** Second point. */
    WB_Point p2;
    /** Third point. */
    WB_Point p3;
    /** Length of side a. */
    private double a;
    /** Length of side b. */
    private double b;
    /** Length of side c. */
    private double c;
    /** Cosine of angle A. */
    private double cosA;
    /** Cosine of angle B. */
    private double cosB;
    /** Cosine of angle C. */
    private double cosC;

    /**
     * 
     */
    protected WB_Triangle() {
    }

    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @param p1 
     * @param p2 
     * @param p3 
     */
    public WB_Triangle(final WB_Coordinate p1, final WB_Coordinate p2,
	    final WB_Coordinate p3) {
	this.p1 = geometryfactory.createPoint(p1);
	this.p2 = geometryfactory.createPoint(p2);
	this.p3 = geometryfactory.createPoint(p3);
	update();
    }

    /**
     * Update side lengths and corner angles.
     */
    protected void update() {
	a = p2.getDistance3D(p3);
	b = p1.getDistance3D(p3);
	c = p1.getDistance3D(p2);
	cosA = (((p2.xd() - p1.xd()) * (p3.xd() - p1.xd())) + ((p2.yd() - p1
		.yd()) * (p3.yd() - p1.yd()))) / (b * c);
	cosB = (((p1.xd() - p2.xd()) * (p3.xd() - p2.xd())) + ((p1.yd() - p2
		.yd()) * (p3.yd() - p2.yd()))) / (a * c);
	cosC = (((p2.xd() - p3.xd()) * (p1.xd() - p3.xd())) + ((p2.yd() - p3
		.yd()) * (p1.yd() - p3.yd()))) / (a * b);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p1() {
	return p1;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p2() {
	return p2;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p3() {
	return p3;
    }

    /**
     * 
     *
     * @return 
     */
    public double a() {
	return a;
    }

    /**
     * 
     *
     * @return 
     */
    public double b() {
	return b;
    }

    /**
     * 
     *
     * @return 
     */
    public double c() {
	return c;
    }

    /**
     * 
     *
     * @return 
     */
    public double cosA() {
	return cosA;
    }

    /**
     * 
     *
     * @return 
     */
    public double cosB() {
	return cosB;
    }

    /**
     * 
     *
     * @return 
     */
    public double cosC() {
	return cosC;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.TRIANGLE;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Simplex#getPoint(int)
     */
    @Override
    public WB_Point getPoint(final int i) {
	if (i == 0) {
	    return p1;
	} else if (i == 1) {
	    return p2;
	} else if (i == 2) {
	    return p3;
	}
	return null;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Simplex#getCenter()
     */
    @Override
    public WB_Point getCenter() {
	return geometryfactory.createMidpoint(p1, p2, p3);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Geometry apply(final WB_Transform T) {
	return geometryfactory.createTriangle(p1.applyAsPoint(T),
		p2.applyAsPoint(T), p3.applyAsPoint(T));
    }

    /**
     * Get plane of triangle.
     *
     * @return WB_Plane
     */
    public WB_Plane getPlane() {
	final WB_Plane P = new WB_Plane(p1, p2, p3);
	if (P.getNormal().getSqLength3D() < WB_Epsilon.SQEPSILON) {
	    return null;
	}
	return P;
    }

    /**
     * Get centroid.
     *
     * @return centroid
     */
    public WB_Point getCentroid() {
	return getPointFromTrilinear(b * c, c * a, a * b);
    }

    /**
     * Get circumcenter.
     *
     * @return circumcenter
     */
    public WB_Point getCircumcenter() {
	return getPointFromTrilinear(cosA, cosB, cosC);
    }

    /**
     * Get orthocenter.
     *
     * @return orthocenter
     */
    public WB_Point getOrthocenter() {
	final double a2 = a * a;
	final double b2 = b * b;
	final double c2 = c * c;
	return getPointFromBarycentric(((a2 + b2) - c2) * ((a2 - b2) + c2),
		((a2 + b2) - c2) * (-a2 + b2 + c2), ((a2 - b2) + c2)
			* (-a2 + b2 + c2));
    }

    /**
     * Get point from trilinear coordinates.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @return point
     */
    public WB_Point getPointFromTrilinear(final double x, final double y,
	    final double z) {
	final double abc = (a * x) + (b * y) + (c * z);
	final WB_Point ea = p2.sub(p3);
	final WB_Point eb = p1.sub(p3);
	ea.mulSelf(b * y);
	eb.mulSelf(a * x);
	ea.addSelf(eb);
	ea.divSelf(abc);
	ea.addSelf(p3);
	return ea;
    }

    /**
     * Get point from barycentric coordinates.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @return point
     */
    public WB_Point getPointFromBarycentric(final double x, final double y,
	    final double z) {
	return getPointFromTrilinear(x / a, y / b, z / c);
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public double[] getBarycentricCoordinates(final WB_Coordinate p) {
	final double m = ((p3.xd() - p1.xd()) * (p2.yd() - p1.yd()))
		- ((p3.yd() - p1.yd()) * (p2.xd() - p1.xd()));
	double nu, nv, ood;
	nu = twiceSignedTriArea2D(p.xd(), p.yd(), p2.xd(), p2.yd(), p3.xd(),
		p3.yd());
	nv = twiceSignedTriArea2D(p.xd(), p.yd(), p3.xd(), p3.yd(), p1.xd(),
		p1.yd());
	ood = -1.0 / m;
	nu *= ood;
	nv *= ood;
	return new double[] { nu, nv, 1 - nu - nv };
    }

    /**
     * Barycentric.
     *
     * @param p
     *            the p
     * @return the w b_ point
     */
    public WB_Point getBarycentric(final WB_Coordinate p) {
	final WB_Vector m = p3.subToVector3D(p1).cross(p2.subToVector3D(p1));
	double nu, nv, ood;
	final double x = WB_Math.fastAbs(m.xd());
	final double y = WB_Math.fastAbs(m.yd());
	final double z = WB_Math.fastAbs(m.zd());
	if ((x >= y) && (x >= z)) {
	    nu = WB_Triangle.twiceSignedTriArea2D(p.yd(), p.zd(), p2.yd(),
		    p2.zd(), p3.yd(), p3.zd());
	    nv = WB_Triangle.twiceSignedTriArea2D(p.yd(), p.zd(), p3.yd(),
		    p3.zd(), p1.yd(), p1.zd());
	    ood = 1.0 / m.xd();
	} else if ((y >= x) && (y >= z)) {
	    nu = WB_Triangle.twiceSignedTriArea2D(p.xd(), p.zd(), p2.xd(),
		    p2.zd(), p3.xd(), p3.zd());
	    nv = WB_Triangle.twiceSignedTriArea2D(p.xd(), p.zd(), p3.xd(),
		    p3.zd(), p1.xd(), p1.zd());
	    ood = -1.0 / m.yd();
	} else {
	    nu = WB_Triangle.twiceSignedTriArea2D(p.xd(), p.yd(), p2.xd(),
		    p2.yd(), p3.xd(), p3.yd());
	    nv = WB_Triangle.twiceSignedTriArea2D(p.xd(), p.yd(), p3.xd(),
		    p3.yd(), p1.xd(), p1.yd());
	    ood = -1.0 / m.zd();
	}
	nu *= ood;
	nv *= ood;
	return new WB_Point(nu, nv, 1 - nu - nv);
    }

    /**
     * Gets the area.
     *
     * @return the area
     */
    public double getArea() {
	final WB_Plane P = getPlane();
	if (P == null) {
	    return 0.0;
	}
	final WB_Vector n = getPlane().getNormal();
	final double x = WB_Math.fastAbs(n.xd());
	final double y = WB_Math.fastAbs(n.yd());
	final double z = WB_Math.fastAbs(n.zd());
	double area = 0;
	int coord = 3;
	if ((x >= y) && (x >= z)) {
	    coord = 1;
	} else if ((y >= x) && (y >= z)) {
	    coord = 2;
	}
	switch (coord) {
	case 1:
	    area = (p1.yd() * (p2.zd() - p3.zd()))
	    + (p2.yd() * (p3.zd() - p1.zd()))
	    + (p3.yd() * (p1.zd() - p2.zd()));
	    break;
	case 2:
	    area = (p1.xd() * (p2.zd() - p3.zd()))
	    + (p2.xd() * (p3.zd() - p1.zd()))
	    + (p3.xd() * (p1.zd() - p2.zd()));
	    break;
	case 3:
	    area = (p1.xd() * (p2.yd() - p3.yd()))
	    + (p2.xd() * (p3.yd() - p1.yd()))
	    + (p3.xd() * (p1.yd() - p2.yd()));
	    break;
	}
	switch (coord) {
	case 1:
	    area *= (0.5 / x);
	    break;
	case 2:
	    area *= (0.5 / y);
	    break;
	case 3:
	    area *= (0.5 / z);
	}
	return WB_Math.fastAbs(area);
    }

    /**
     * Get circumcircle.
     *
     * @return circumcircle
     */
    public WB_Circle getCircumcircle() {
	final WB_Circle result = new WB_Circle();
	result.setRadius((a * b * c)
		/ Math.sqrt(((2 * a * a * b * b) + (2 * b * b * c * c) + (2 * a
			* a * c * c))
			- (a * a * a * a) - (b * b * b * b) - (c * c * c * c)));
	final double bx = p2.xd() - p1.xd();
	final double by = p2.yd() - p1.yd();
	final double cx = p3.xd() - p1.xd();
	final double cy = p3.yd() - p1.yd();
	double d = 2 * ((bx * cy) - (by * cx));
	if (WB_Epsilon.isZero(d)) {
	    return null;
	}
	d = 1.0 / d;
	final double b2 = (bx * bx) + (by * by);
	final double c2 = (cx * cx) + (cy * cy);
	final double x = ((cy * b2) - (by * c2)) * d;
	final double y = ((bx * c2) - (cx * b2)) * d;
	result.setCenter(x + p1.xd(), y + p1.yd());
	return result;
    }

    /**
     * Get incircle.
     *
     * @return incircle
     */
    public WB_Circle getIncircle() {
	final WB_Circle result = new WB_Circle();
	final double abc = a + b + c;
	result.setRadius(0.5 * Math
		.sqrt((((b + c) - a) * ((c + a) - b) * ((a + b) - c)) / abc));
	final WB_Point ta = p1.mul(a);
	final WB_Point tb = p2.mul(b);
	final WB_Point tc = p3.mul(c);
	tc.addSelf(ta).addSelf(tb).divSelf(abc);
	result.setCenter(tc);
	return result;
    }

    /**
     * Get incenter.
     *
     * @return incenter
     */
    public WB_Point getIncenter() {
	return getPointFromTrilinear(1, 1, 1);
    }

    /**
     * Check if points p1 and p2 lie on same side of line A-B.
     *
     * @param p1
     *            the p1
     * @param p2
     *            the p2
     * @param A
     *            the a
     * @param B
     *            the b
     * @return true, false
     */
    public static boolean sameSide2D(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate A, final WB_Coordinate B) {
	final WB_Point t1 = new WB_Point(B).subSelf(A);
	final WB_Point t2 = new WB_Point(p1).subSelf(A);
	final WB_Point t3 = new WB_Point(p2).subSelf(A);
	final double ct2 = (t1.xd() * t2.yd()) - (t1.yd() * t2.xd());
	final double ct3 = (t1.xd() * t3.yd()) - (t1.yd() * t3.xd());
	if ((ct2 * ct3) >= WB_Epsilon.EPSILON) {
	    return true;
	}
	return false;
    }

    /**
     * Check if point p lies in triangle A-B-C.
     *
     * @param p
     *            the p
     * @param A
     *            the a
     * @param B
     *            the b
     * @param C
     *            the c
     * @return true, false
     */
    public static boolean pointInTriangle2D(final WB_Coordinate p,
	    final WB_Coordinate A, final WB_Coordinate B, final WB_Coordinate C) {
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine2D(A, B, C))) {
	    return false;
	}
	if (sameSide2D(p, A, B, C) && sameSide2D(p, B, A, C)
		&& sameSide2D(p, C, A, B)) {
	    return true;
	}
	return false;
    }

    /*
     * public static boolean pointInTriangle2D(final WB_XY p, final WB_XY A,
     * final WB_XY B, final WB_XY C) { final WB_XY e0 = B.subAndCopy(A); final
     * WB_XY n0 = new WB_XY(e0.y, -e0.x); final double sign = e0.y * (C.x - A.x)
     * - e0.x * (C.y - A.y); if (sign * n0.dot(p.subAndCopy(A)) <
     * WB_Epsilon.EPSILON) { return false; } final WB_XY e1 = C.subAndCopy(B);
     * final WB_XY n1 = new WB_XY(e1.y, -e1.x); if (sign *
     * n1.dot(p.subAndCopy(B)) < WB_Epsilon.EPSILON) { return false; } final
     * WB_XY e2 = A.subAndCopy(C); final WB_XY n2 = new WB_XY(e2.y, -e2.x); if
     * (sign * n2.dot(p.subAndCopy(C)) < WB_Epsilon.EPSILON) { return false; }
     * return true; }
     */
    /**
     * Point in triangle2 d.
     *
     * @param p
     *            the p
     * @param T
     *            the t
     * @return true, if successful
     */
    public static boolean pointInTriangle2D(final WB_Coordinate p,
	    final WB_Triangle T) {
	return pointInTriangle2D(p, T.p1, T.p2, T.p3);
    }

    /**
     * Check if point p lies in triangle A-B-C using barycentric coordinates.
     *
     * @param p
     *            the p
     * @param A
     *            the a
     * @param B
     *            the b
     * @param C
     *            the c
     * @return true, false
     */
    public static boolean pointInTriangleBary2D(final WB_Coordinate p,
	    final WB_Coordinate A, final WB_Coordinate B, final WB_Coordinate C) {
	if (p == A) {
	    return false;
	}
	if (p == B) {
	    return false;
	}
	if (p == C) {
	    return false;
	}
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine2D(A, B, C))) {
	    return false;
	}
	// Compute vectors
	final WB_Point v0 = new WB_Point(C).subSelf(A);
	final WB_Point v1 = new WB_Point(B).subSelf(A);
	final WB_Point v2 = new WB_Point(p).subSelf(A);
	// Compute dot products
	final double dot00 = v0.dot2D(v0);
	final double dot01 = v0.dot2D(v1);
	final double dot02 = v0.dot2D(v2);
	final double dot11 = v1.dot2D(v1);
	final double dot12 = v1.dot2D(v2);
	// Compute barycentric coordinates
	final double invDenom = 1.0 / ((dot00 * dot11) - (dot01 * dot01));
	final double u = ((dot11 * dot02) - (dot01 * dot12)) * invDenom;
	final double v = ((dot00 * dot12) - (dot01 * dot02)) * invDenom;
	// Check if point is in triangle
	return (u > WB_Epsilon.EPSILON) && (v > WB_Epsilon.EPSILON)
		&& ((u + v) < (1 - WB_Epsilon.EPSILON));
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
    public static boolean pointInTriangleBary3D(final WB_Coordinate p,
	    final WB_Coordinate A, final WB_Coordinate B, final WB_Coordinate C) {
	if (p == A) {
	    return false;
	}
	if (p == B) {
	    return false;
	}
	if (p == C) {
	    return false;
	}
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToLine2D(A, B, C))) {
	    return false;
	}
	// Compute vectors
	final WB_Point v0 = new WB_Point(C).subSelf(A);
	final WB_Point v1 = new WB_Point(B).subSelf(A);
	final WB_Point v2 = new WB_Point(p).subSelf(A);
	// Compute dot products
	final double dot00 = v0.dot(v0);
	final double dot01 = v0.dot(v1);
	final double dot02 = v0.dot(v2);
	final double dot11 = v1.dot(v1);
	final double dot12 = v1.dot(v2);
	// Compute barycentric coordinates
	final double invDenom = 1.0 / ((dot00 * dot11) - (dot01 * dot01));
	final double u = ((dot11 * dot02) - (dot01 * dot12)) * invDenom;
	final double v = ((dot00 * dot12) - (dot01 * dot02)) * invDenom;
	// Check if point is in triangle
	return (u > WB_Epsilon.EPSILON) && (v > WB_Epsilon.EPSILON)
		&& ((u + v) < (1 - WB_Epsilon.EPSILON));
    }

    /**
     * Point in triangle bary2 d.
     *
     * @param p
     *            the p
     * @param T
     *            the t
     * @return true, if successful
     */
    public static boolean pointInTriangleBary2D(final WB_Coordinate p,
	    final WB_Triangle T) {
	return pointInTriangleBary2D(p, T.p1, T.p2, T.p3);
    }

    /**
     * 
     *
     * @param p 
     * @param T 
     * @return 
     */
    public static boolean pointInTriangleBary3D(final WB_Coordinate p,
	    final WB_Triangle T) {
	return pointInTriangleBary3D(p, T.p1, T.p2, T.p3);
    }

    /**
     * Twice signed tri area2 d.
     *
     * @param p1
     *            the p1
     * @param p2
     *            the p2
     * @param p3
     *            the p3
     * @return the double
     */
    public static double twiceSignedTriArea2D(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3) {
	return ((p1.xd() - p3.xd()) * (p2.yd() - p3.yd()))
		- ((p1.yd() - p3.yd()) * (p2.xd() - p3.xd()));
    }

    /**
     * Twice signed tri area2 d.
     *
     * @param x1
     *            the x1
     * @param y1
     *            the y1
     * @param x2
     *            the x2
     * @param y2
     *            the y2
     * @param x3
     *            the x3
     * @param y3
     *            the y3
     * @return the double
     */
    public static double twiceSignedTriArea2D(final double x1, final double y1,
	    final double x2, final double y2, final double x3, final double y3) {
	return ((x1 - x2) * (y2 - y3)) - ((x2 - x3) * (y1 - y2));
    }
}
