package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_Triangle implements Triangle {

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

	protected WB_Triangle() {

	}

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

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
		a = p2.getDistance(p3);
		b = p1.getDistance(p3);
		c = p1.getDistance(p2);

		cosA = ((p2.xd() - p1.xd()) * (p3.xd() - p1.xd()) + (p2.yd() - p1.yd())
				* (p3.yd() - p1.yd()))
				/ (b * c);
		cosB = ((p1.xd() - p2.xd()) * (p3.xd() - p2.xd()) + (p1.yd() - p2.yd())
				* (p3.yd() - p2.yd()))
				/ (a * c);
		cosC = ((p2.xd() - p3.xd()) * (p1.xd() - p3.xd()) + (p2.yd() - p3.yd())
				* (p1.yd() - p3.yd()))
				/ (a * b);

	}

	private static double twiceSignedTriArea2D(final double xA,
			final double yA, final double xB, final double yB, final double xC,
			final double yC) {
		return (xA - xB) * (yB - yC) - (xB - xC) * (yA - yB);
	}

	public WB_Point p1() {
		return p1;
	}

	public WB_Point p2() {
		return p2;
	}

	public WB_Point p3() {
		return p3;
	}

	public double a() {
		return a;
	}

	public double b() {
		return b;
	}

	public double c() {
		return c;
	}

	public double cosA() {
		return cosA;
	}

	public double cosB() {
		return cosB;
	}

	public double cosC() {
		return cosC;
	}

	@Override
	public WB_GeometryType getType() {

		return WB_GeometryType.TRIANGLE;
	}

	@Override
	public int getDimension() {
		return 2;
	}

	@Override
	public int getEmbeddingDimension() {
		return 2;
	}

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

	@Override
	public WB_Point getCenter() {
		return geometryfactory.createMidpoint(p1, p2, p3);
	}

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
		if (P.getNormal().getSqLength() < WB_Epsilon.SQEPSILON) {
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

	public double[] getBarycentricCoordinates(final WB_Coordinate p) {
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
		final WB_Vector m = WB_Vector.getCross(p3.subToVector(p1),
				p2.subToVector(p1));
		double nu, nv, ood;
		final double x = WB_Math.fastAbs(m.x);
		final double y = WB_Math.fastAbs(m.y);
		final double z = WB_Math.fastAbs(m.z);
		if (x >= y && x >= z) {
			nu = WB_Triangle2D.twiceSignedTriArea2D(p.yd(), p.zd(), p2.y, p2.z,
					p3.y, p3.z);
			nv = WB_Triangle2D.twiceSignedTriArea2D(p.yd(), p.zd(), p3.y, p3.z,
					p1.y, p1.z);
			ood = 1.0 / m.x;
		} else if (y >= x && y >= z) {
			nu = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.zd(), p2.x, p2.z,
					p3.x, p3.z);
			nv = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.zd(), p3.x, p3.z,
					p1.x, p1.z);
			ood = -1.0 / m.y;
		} else {
			nu = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.yd(), p2.x, p2.y,
					p3.x, p3.y);
			nv = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.yd(), p3.x, p3.y,
					p1.x, p1.y);
			ood = -1.0 / m.z;
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

		final double x = WB_Math.fastAbs(n.x);
		final double y = WB_Math.fastAbs(n.y);
		final double z = WB_Math.fastAbs(n.z);
		double area = 0;
		int coord = 3;
		if (x >= y && x >= z) {
			coord = 1;
		} else if (y >= x && y >= z) {
			coord = 2;
		}

		switch (coord) {
		case 1:
			area = (p1.y * (p2.z - p3.z)) + (p2.y * (p3.z - p1.z))
					+ (p3.y * (p1.z - p2.z));
			break;
		case 2:
			area = (p1.x * (p2.z - p3.z)) + (p2.x * (p3.z - p1.z))
					+ (p3.x * (p1.z - p2.z));
			break;
		case 3:
			area = (p1.x * (p2.y - p3.y)) + (p2.x * (p3.y - p1.y))
					+ (p3.x * (p1.y - p2.y));
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
}
