package wblut.geom;

import wblut.WB_Epsilon;
import wblut.geom.interfaces.Triangle2D;

/**
 * 3D Triangle.
 */
public class WB_Triangle2D implements Triangle2D {

	/** First point. */
	protected WB_Point p1;

	/** Second point. */
	protected WB_Point p2;

	/** Third point. */
	protected WB_Point p3;

	/** Length of side a. */
	protected double a;

	/** Length of side b. */
	protected double b;

	/** Length of side c. */
	protected double c;

	/** Cosine of angle A. */
	protected double cosA;

	/** Cosine of angle B. */
	protected double cosB;

	/** Cosine of angle C. */
	protected double cosC;

	/** Is triangle degenerate?. */
	protected boolean degenerate;

	/**
	 * Instantiates a new WB_Triangle2D. No copies are made.
	 * 
	 * @param p1
	 *            first point
	 * @param p2
	 *            second point
	 * @param p3
	 *            third point
	 */
	public WB_Triangle2D(final WB_Coordinate p1, final WB_Coordinate p2,
			final WB_Coordinate p3) {
		this.p1 = new WB_Point(p1);
		this.p2 = new WB_Point(p2);
		this.p3 = new WB_Point(p3);
		update();
	}

	/**
	 * Update side lengths and corner angles.
	 */
	public void update() {
		a = WB_Distance.getDistance2D(p2, p3);
		b = WB_Distance.getDistance2D(p1, p3);
		c = WB_Distance.getDistance2D(p1, p2);

		cosA = ((p2.xd() - p1.xd()) * (p3.xd() - p1.xd()) + (p2.yd() - p1.yd())
				* (p3.yd() - p1.yd()))
				/ (b * c);
		cosB = ((p1.xd() - p2.xd()) * (p3.xd() - p2.xd()) + (p1.yd() - p2.yd())
				* (p3.yd() - p2.yd()))
				/ (a * c);
		cosC = ((p2.xd() - p3.xd()) * (p1.xd() - p3.xd()) + (p2.yd() - p3.yd())
				* (p1.yd() - p3.yd()))
				/ (a * b);

		degenerate = WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToLine2D(p1,
				p2, p3));
	}

	/**
	 * Get circumcircle.
	 * 
	 * @return circumcircle
	 */
	public WB_Circle getCircumcircle() {
		final WB_Circle result = new WB_Circle();
		if (!degenerate) {

			result.setRadius(a
					* b
					* c
					/ Math.sqrt(2 * a * a * b * b + 2 * b * b * c * c + 2 * a
							* a * c * c - a * a * a * a - b * b * b * b - c * c
							* c * c));
			final double bx = p2.xd() - p1.xd();
			final double by = p2.yd() - p1.yd();
			final double cx = p3.xd() - p1.xd();
			final double cy = p3.yd() - p1.yd();
			double d = 2 * (bx * cy - by * cx);
			if (WB_Epsilon.isZero(d)) {
				return null;
			}
			d = 1.0 / d;
			final double b2 = bx * bx + by * by;
			final double c2 = cx * cx + cy * cy;
			final double x = (cy * b2 - by * c2) * d;
			final double y = (bx * c2 - cx * b2) * d;
			result.setCenter(x + p1.xd(), y + p1.yd());
			return result;
		}
		return null;
	}

	/**
	 * Get incircle.
	 * 
	 * @return incircle
	 */
	public WB_Circle getIncircle() {
		final WB_Circle result = new WB_Circle();
		if (!degenerate) {

			final double abc = a + b + c;
			result.setRadius(0.5 * Math.sqrt(((b + c - a) * (c + a - b) * (a
					+ b - c))
					/ abc));
			final WB_Point ta = p1.mul(a);
			final WB_Point tb = p2.mul(b);
			final WB_Point tc = p3.mul(c);
			tc._addSelf(ta)._addSelf(tb)._divSelf(abc);
			result.setCenter(tc);
			return result;
		}
		return null;
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
		return getPointFromBarycentric((a2 + b2 - c2) * (a2 - b2 + c2), (a2
				+ b2 - c2)
				* (-a2 + b2 + c2), (a2 - b2 + c2) * (-a2 + b2 + c2));
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
		if (!degenerate) {

			final double abc = a * x + b * y + c * z;
			final WB_Point ea = p2.sub(p3);
			final WB_Point eb = p1.sub(p3);
			ea._mulSelf(b * y);
			eb._mulSelf(a * x);
			ea._addSelf(eb);
			ea._divSelf(abc);
			ea._addSelf(p3);
			return ea;

		}

		return null;

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
		if (!degenerate) {
			return getPointFromTrilinear(x / a, y / b, z / c);
		}
		return null;
	}

	/**
	 * Barycentric coordinates of point.
	 * 
	 * @param p
	 *            point
	 * @return barycentric coordinates as WB_XYZ
	 */
	public WB_Point getBarycentric(final WB_Coordinate p) {
		final double m = (p3.xd() - p1.xd()) * (p2.yd() - p1.yd())
				- (p3.yd() - p1.yd()) * (p2.xd() - p1.xd());

		double nu, nv, ood;

		nu = twiceSignedTriArea2D(p.xd(), p.yd(), p2.xd(), p2.yd(), p3.xd(),
				p3.yd());
		nv = twiceSignedTriArea2D(p.xd(), p.yd(), p3.xd(), p3.yd(), p1.xd(),
				p1.yd());
		ood = -1.0 / m;

		nu *= ood;
		nv *= ood;
		return new WB_Point(nu, nv, 1 - nu - nv);

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
		final WB_Point t1 = new WB_Point(B)._subSelf(A);
		final WB_Point t2 = new WB_Point(p1)._subSelf(A);
		final WB_Point t3 = new WB_Point(p2)._subSelf(A);
		final double ct2 = t1.xd() * t2.yd() - t1.yd() * t2.xd();
		final double ct3 = t1.xd() * t3.yd() - t1.yd() * t3.xd();

		if (ct2 * ct3 >= WB_Epsilon.EPSILON) {
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
		if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToLine2D(A, B, C))) {
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
			final WB_Triangle2D T) {
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
		if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToLine2D(A, B, C))) {
			return false;
		}
		// Compute vectors
		final WB_Point v0 = new WB_Point(C)._subSelf(A);
		final WB_Point v1 = new WB_Point(B)._subSelf(A);
		final WB_Point v2 = new WB_Point(p)._subSelf(A);

		// Compute dot products
		final double dot00 = v0.dot(v0);
		final double dot01 = v0.dot(v1);
		final double dot02 = v0.dot(v2);
		final double dot11 = v1.dot(v1);
		final double dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		final double invDenom = 1.0 / (dot00 * dot11 - dot01 * dot01);
		final double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		final double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u > WB_Epsilon.EPSILON) && (v > WB_Epsilon.EPSILON)
				&& (u + v < 1 - WB_Epsilon.EPSILON);
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
			final WB_Triangle2D T) {
		return pointInTriangleBary2D(p, T.p1, T.p2, T.p3);
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
		return (p1.xd() - p3.xd()) * (p2.yd() - p3.yd()) - (p1.yd() - p3.yd())
				* (p2.xd() - p3.xd());
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
		return (x1 - x2) * (y2 - y3) - (x2 - x3) * (y1 - y2);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle2D#getCenter()
	 */
	public WB_Point getCenter() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle2D#p1()
	 */
	public WB_Point p1() {
		return p1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle2D#p2()
	 */
	public WB_Point p2() {
		return p2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle2D#p3()
	 */
	public WB_Point p3() {
		return p3;
	}

}
