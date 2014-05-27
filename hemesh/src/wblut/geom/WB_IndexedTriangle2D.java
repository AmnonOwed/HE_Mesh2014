package wblut.geom;

import wblut.WB_Epsilon;

public class WB_IndexedTriangle2D implements Triangle2D {

	public int i1;

	public int i2;

	public int i3;

	protected WB_Point[] points;

	protected double a;

	protected double b;

	protected double c;

	protected double cosA;

	protected double cosB;

	protected double cosC;

	protected boolean degenerate;

	public WB_IndexedTriangle2D(final int i1, final int i2, final int i3,
			final WB_Point[] points) {
		this.points = points;
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		update();
	}

	public void update() {
		a = WB_Distance.getDistance2D(points[i2], points[i3]);
		b = WB_Distance.getDistance2D(points[i1], points[i3]);
		c = WB_Distance.getDistance2D(points[i1], points[i2]);

		cosA = ((points[i2].x - points[i1].x) * (points[i3].x - points[i1].x) + (points[i2].y - points[i1].y)
				* (points[i3].y - points[i1].y))
				/ (b * c);
		cosB = ((points[i1].x - points[i2].x) * (points[i3].x - points[i2].x) + (points[i1].y - points[i2].y)
				* (points[i3].y - points[i2].y))
				/ (a * c);
		cosC = ((points[i2].x - points[i3].x) * (points[i1].x - points[i3].x) + (points[i2].y - points[i3].y)
				* (points[i1].y - points[i3].y))
				/ (a * b);

		degenerate = WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToLine2D(
				points[i1], points[i2], points[i3]));
	}

	public WB_Circle getCircumcircle() {
		final WB_Circle result = new WB_Circle();
		if (!degenerate) {

			result.setRadius(a
					* b
					* c
					/ Math.sqrt(2 * a * a * b * b + 2 * b * b * c * c + 2 * a
							* a * c * c - a * a * a * a - b * b * b * b - c * c
							* c * c));
			final double bx = points[i2].x - points[i1].x;
			final double by = points[i2].y - points[i1].y;
			final double cx = points[i3].x - points[i1].x;
			final double cy = points[i3].y - points[i1].y;
			double d = 2 * (bx * cy - by * cx);
			if (WB_Epsilon.isZero(d)) {
				return null;
			}
			d = 1.0 / d;
			final double b2 = bx * bx + by * by;
			final double c2 = cx * cx + cy * cy;
			final double x = (cy * b2 - by * c2) * d;
			final double y = (bx * c2 - cx * b2) * d;
			result.setCenter(x + points[i1].x, y + points[i1].y);
			return result;
		}
		return null;
	}

	public WB_Circle getIncircle() {
		final WB_Circle result = new WB_Circle();
		if (!degenerate) {

			final double abc = a + b + c;
			result.setRadius(0.5 * Math.sqrt(((b + c - a) * (c + a - b) * (a
					+ b - c))
					/ abc));
			final WB_Point ta = points[i1].mul(a);
			final WB_Point tb = points[i2].mul(b);
			final WB_Point tc = points[i3].mul(c);
			tc._addSelf(ta)._addSelf(tb)._divSelf(abc);
			result.setCenter(tc);
			return result;
		}
		return null;
	}

	public WB_Point getIncenter() {
		return getPointFromTrilinear(1, 1, 1);
	}

	public WB_Point getCentroid() {
		return getPointFromTrilinear(b * c, c * a, a * b);
	}

	public WB_Point getCircumcenter() {
		return getPointFromTrilinear(cosA, cosB, cosC);
	}

	public WB_Point getOrthocenter() {
		final double a2 = a * a;
		final double b2 = b * b;
		final double c2 = c * c;
		return getPointFromBarycentric((a2 + b2 - c2) * (a2 - b2 + c2), (a2
				+ b2 - c2)
				* (-a2 + b2 + c2), (a2 - b2 + c2) * (-a2 + b2 + c2));
	}

	public WB_Point getPointFromTrilinear(final double x, final double y,
			final double z) {
		if (!degenerate) {

			final double abc = a * x + b * y + c * z;
			final WB_Point ea = points[i2].sub(points[i3]);
			final WB_Point eb = points[i1].sub(points[i3]);
			ea._mulSelf(b * y);
			eb._mulSelf(a * x);
			ea._addSelf(eb);
			ea._divSelf(abc);
			ea._addSelf(points[i3]);
			return ea;

		}

		return null;

	}

	public WB_Point getPointFromBarycentric(final double x, final double y,
			final double z) {
		if (!degenerate) {
			return getPointFromTrilinear(x / a, y / b, z / c);
		}
		return null;
	}

	public WB_Point getBarycentric(final WB_Coordinate p) {
		final double m = (points[i3].x - points[i1].x)
				* (points[i2].y - points[i1].y) - (points[i3].y - points[i1].y)
				* (points[i2].x - points[i1].x);

		double nu, nv, ood;

		nu = twiceSignedTriArea2D(p.xd(), p.yd(), points[i2].x, points[i2].y,
				points[i3].x, points[i3].y);
		nv = twiceSignedTriArea2D(p.xd(), p.yd(), points[i3].x, points[i3].y,
				points[i1].x, points[i1].y);
		ood = -1.0 / m;

		nu *= ood;
		nv *= ood;
		return new WB_Point(nu, nv, 1 - nu - nv);

	}

	public static boolean sameSide2D(final WB_Coordinate p1,
			final WB_Coordinate p2, final WB_Coordinate A, final WB_Coordinate B) {
		final WB_Point t1 = new WB_Point(B)._subSelf(A);
		final WB_Point t2 = new WB_Point(p1)._subSelf(A);
		final WB_Point t3 = new WB_Point(p2)._subSelf(A);
		final double ct2 = t1.x * t2.y - t1.y * t2.x;
		final double ct3 = t1.x * t3.y - t1.y * t3.x;

		if (ct2 * ct3 >= WB_Epsilon.EPSILON) {
			return true;
		}
		return false;
	}

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

	public static boolean pointInTriangle2D(final WB_Coordinate p,
			final WB_Triangle2D T) {
		return pointInTriangle2D(p, T.p1, T.p2, T.p3);
	}

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

	public static boolean pointInTriangleBary2D(final WB_Coordinate p,
			final WB_Triangle2D T) {
		return pointInTriangleBary2D(p, T.p1, T.p2, T.p3);
	}

	public static double twiceSignedTriArea2D(final WB_Coordinate p1,
			final WB_Coordinate p2, final WB_Coordinate p3) {
		return (p1.xd() - p3.xd()) * (p2.yd() - p3.yd()) - (p1.yd() - p3.yd())
				* (p2.xd() - p3.xd());
	}

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
	 * @see wblut.geom.WB_Triangle2D#points[i1]()
	 */
	public WB_Point p1() {
		return points[i1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle2D#points[i2]()
	 */
	public WB_Point p2() {
		return points[i2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle2D#points[i3]()
	 */
	public WB_Point p3() {
		return points[i3];
	}
}