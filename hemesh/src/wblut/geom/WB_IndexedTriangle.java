package wblut.geom;

import wblut.WB_Epsilon;
import wblut.geom.interfaces.Triangle;
import wblut.math.WB_Math;

public class WB_IndexedTriangle implements Triangle {

	private int i1;

	private int i2;

	private final int i3;

	private final WB_Point[] points;

	protected double a;

	protected double b;

	protected double c;

	protected double cosA;

	protected double cosB;

	protected double cosC;

	protected boolean degenerate;

	public WB_IndexedTriangle(final int i1, final int i2, final int i3,
			final WB_Point[] points) {
		this.points = points;
		this.i1 = i1;
		this.i2 = i2;
		this.i3 = i3;
		update();
	}

	public void reverse() {
		final int t = i1;
		i1 = i2;
		i2 = t;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle#p1()
	 */
	public WB_Point p1() {
		return points[i1];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle#p2()
	 */
	public WB_Point p2() {
		return points[i2];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle#p3()
	 */
	public WB_Point p3() {
		return points[i3];
	}

	public int i1() {
		return i1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle#p2()
	 */

	public int i2() {
		return i2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Triangle#p3()
	 */

	public int i3() {
		return i3;
	}

	public void update() {
		a = WB_Distance.getDistance3D(points[i2], points[i3]);
		b = WB_Distance.getDistance3D(points[i1], points[i3]);
		c = WB_Distance.getDistance3D(points[i1], points[i2]);

		cosA = ((points[i2].x - points[i1].x) * (points[i3].x - points[i1].x)
				+ (points[i2].y - points[i1].y) * (points[i3].y - points[i1].y) + (points[i2].z - points[i1].z)
				* (points[i3].z - points[i1].z))
				/ (b * c);
		cosB = ((points[i1].x - points[i2].x) * (points[i3].x - points[i2].x)
				+ (points[i1].y - points[i2].y) * (points[i3].y - points[i2].y) + (points[i1].z - points[i2].z)
				* (points[i3].z - points[i2].z))
				/ (a * c);
		cosC = ((points[i2].x - points[i3].x) * (points[i1].x - points[i3].x)
				+ (points[i2].y - points[i3].y) * (points[i1].y - points[i3].y) + (points[i2].z - points[i3].z)
				* (points[i1].z - points[i3].z))
				/ (a * b);

		degenerate = WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToLine3D(
				points[i1], points[i2], points[i3]));
	}

	public WB_Plane getPlane() {
		final WB_Plane P = new WB_Plane(points[i1], points[i2], points[i3]);
		if (P.getNormal().getSqLength() < WB_Epsilon.SQEPSILON) {
			return null;
		}
		return P;
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
		final WB_Vector m = WB_Vector.getCross(
				points[i3].subToVector(points[i1]),
				points[i2].subToVector(points[i1]));
		double nu, nv, ood;
		final double x = WB_Math.fastAbs(m.x);
		final double y = WB_Math.fastAbs(m.y);
		final double z = WB_Math.fastAbs(m.z);
		if (x >= y && x >= z) {
			nu = WB_Triangle2D.twiceSignedTriArea2D(p.yd(), p.zd(),
					points[i2].y, points[i2].z, points[i3].y, points[i3].z);
			nv = WB_Triangle2D.twiceSignedTriArea2D(p.yd(), p.zd(),
					points[i3].y, points[i3].z, points[i1].y, points[i1].z);
			ood = 1.0 / m.x;
		} else if (y >= x && y >= z) {
			nu = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.zd(),
					points[i2].x, points[i2].z, points[i3].x, points[i3].z);
			nv = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.zd(),
					points[i3].x, points[i3].z, points[i1].x, points[i1].z);
			ood = -1.0 / m.y;
		} else {
			nu = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.yd(),
					points[i2].x, points[i2].y, points[i3].x, points[i3].y);
			nv = WB_Triangle2D.twiceSignedTriArea2D(p.xd(), p.yd(),
					points[i3].x, points[i3].y, points[i1].x, points[i1].y);
			ood = -1.0 / m.z;
		}
		nu *= ood;
		nv *= ood;
		return new WB_Point(nu, nv, 1 - nu - nv);

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
			return p1();
		} else if (i == 1) {
			return p2();
		} else if (i == 2) {
			return p3();
		}
		return null;

	}

	@Override
	public WB_Point getCenter() {
		return geometryfactory.createMidpoint(p1(), p2(), p3());
	}

	@Override
	public WB_Geometry apply(final WB_Transform T) {
		return geometryfactory.createTriangle(p1().applyAsPoint(T), p2()
				.applyAsPoint(T), p3().applyAsPoint(T));
	}

}