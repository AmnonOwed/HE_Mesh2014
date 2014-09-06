package wblut.geom;

import wblut.geom.interfaces.SimplePolygon;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_Plane {

	public static final WB_Plane XY() {
		return new WB_Plane(0, 0, 0, 0, 0, 1);
	}

	public static final WB_Plane XZ() {
		return new WB_Plane(0, 0, 0, 0, 1, 0);
	}

	public static final WB_Plane YZ() {
		return new WB_Plane(0, 0, 0, 1, 0, 0);
	}

	/** Plane normal. */
	private WB_Vector n;

	/** Origin. */
	private WB_Point origin;

	/**
	 * d-parameter: p.n = d with p point on plane, n the normal and . the dot
	 * product.
	 */
	private double d;

	private WB_Vector u, v;

	public WB_Plane(final WB_Coordinate p1, final WB_Coordinate p2,
			final WB_Coordinate p3) {
		final WB_Vector v21 = new WB_Vector(p1, p2);
		final WB_Vector v31 = new WB_Vector(p1, p3);
		n = new WB_Vector(v21.cross(v31));
		n.normalizeSelf();
		d = n.xd() * p1.xd() + n.yd() * p1.yd() + n.zd() * p1.zd();
		origin = new WB_Point(p1);
		setAxes();
	}

	public WB_Plane(final double ox, final double oy, final double oz,
			final double nx, final double ny, final double nz) {
		origin = new WB_Point(ox, oy, oz);
		n = new WB_Vector(nx, ny, nz);
		n.normalizeSelf();
		d = n.dot(origin);
		setAxes();
	}

	public WB_Plane(final WB_Coordinate o, final WB_Coordinate n) {
		origin = new WB_Point(o);
		this.n = new WB_Vector(n);
		this.n.normalizeSelf();
		d = this.n.dot(origin);
		setAxes();
	}

	protected void set(final WB_Coordinate o, final WB_Coordinate n) {
		origin = new WB_Point(o);
		this.n = new WB_Vector(n);
		this.n.normalizeSelf();
		d = this.n.dot(origin);
		setAxes();
	}

	public WB_Plane(final WB_Coordinate n, final double d) {
		this.n = new WB_Vector(n);
		this.n.normalizeSelf();
		this.d = d;
		origin = WB_Intersection.getClosestPoint3D(new WB_Point(), this);
		setAxes();
	}

	public WB_Plane get() {
		return new WB_Plane(origin, n);
	}

	public WB_Vector getNormal() {
		return n.get();
	}

	public double d() {
		return d;
	}

	public WB_Point getOrigin() {
		return origin.get();
	}

	public void flipNormal() {
		n.mulSelf(-1);
		setAxes();
	}

	@Override
	public String toString() {
		return "Plane o: [" + origin + "] n: [" + n + "] d: [" + d + "]";
	}

	public WB_Classification classifyPointToPlane(final WB_Coordinate p) {

		final double dist = getNormal().dot(p) - d();
		if (dist > WB_Epsilon.EPSILON) {
			return WB_Classification.FRONT;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	public static WB_Classification classifyPointToPlane(final WB_Point p,
			final WB_Plane P) {

		final double dist = P.getNormal().dot(p) - P.d();
		if (dist > WB_Epsilon.EPSILON) {
			return WB_Classification.FRONT;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	/**
	 * Check if points lies on positive side of plane defined by 3 clockwise
	 * points.
	 * 
	 * @param p
	 *            point to check
	 * @param a
	 *            the a
	 * @param b
	 *            the b
	 * @param c
	 *            the c
	 * @return true, if successful
	 */
	public static boolean pointOutsideOfPlane(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b, final WB_Coordinate c) {
		return new WB_Vector(a, p).dot(new WB_Vector(a, b)
				.crossSelf(new WB_Vector(a, c))) >= 0;
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
		return signp * signq <= 0;
	}

	/**
	 * Classify polygon to plane.
	 * 
	 * @param poly
	 *            the poly
	 * @return the w b_ classify polygon to plane
	 */
	public WB_Classification classifyPolygonToPlane(final SimplePolygon poly) {
		int numInFront = 0;
		int numBehind = 0;
		for (int i = 0; i < poly.getN(); i++) {
			switch (classifyPointToPlane(poly.getPoint(i))) {
			case FRONT:
				numInFront++;
				break;
			case BACK:
				numBehind++;
				break;
			}
			if (numBehind > 0 && numInFront > 0) {
				return WB_Classification.CROSSING;
			}
		}

		if (numInFront > 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind > 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	/**
	 * Classify polygon to plane.
	 * 
	 * @param poly
	 *            the poly
	 * @param P
	 *            the p
	 * @return the w b_ classify polygon to plane
	 */
	public static WB_Classification classifyPolygonToPlane(
			final SimplePolygon poly, final WB_Plane P) {
		int numInFront = 0;
		int numBehind = 0;
		for (int i = 0; i < poly.getN(); i++) {
			switch (classifyPointToPlane(poly.getPoint(i), P)) {
			case FRONT:
				numInFront++;
				break;
			case BACK:
				numBehind++;
				break;
			}
			if (numBehind != 0 && numInFront != 0) {
				return WB_Classification.CROSSING;
			}
		}

		if (numInFront != 0) {
			return WB_Classification.FRONT;
		}
		if (numBehind != 0) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	/**
	 * Are the planes equal?.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return true/false
	 */
	public static boolean isEqual(final WB_Plane P, final WB_Plane Q) {
		if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(P.getOrigin(), Q))) {
			return false;
		}
		if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(Q.getOrigin(), P))) {
			return false;
		}
		if (!P.getNormal().isParallelNorm(Q.getNormal())) {
			return false;
		}
		return true;
	}

	/**
	 * Sets the axes.
	 */
	private void setAxes() {
		final double x = WB_Math.fastAbs(n.xd());
		final double y = WB_Math.fastAbs(n.yd());

		if (x >= y) {
			u = new WB_Vector(n.zd(), 0, -n.xd());

		} else {
			u = new WB_Vector(0, n.zd(), -n.yd());
		}
		u.normalizeSelf();
		v = n.cross(u);

	}

	// Return coordinates relative to plane axes
	/**
	 * Local point.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ point3d
	 */
	public WB_Point localPoint(final WB_Coordinate p) {
		return new WB_Point(u.xd() * (p.xd() - origin.xd()) + u.yd()
				* (p.yd() - origin.yd()) + u.zd() * (p.zd() - origin.zd()),
				v.xd() * (p.xd() - origin.xd()) + v.yd()
						* (p.yd() - origin.yd()) + v.zd()
						* (p.zd() - origin.zd()), n.xd()
						* (p.xd() - origin.xd()) + n.yd()
						* (p.yd() - origin.yd()) + n.zd()
						* (p.zd() - origin.zd()));

	}

	/**
	 * Local point2 d.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ point2d
	 */
	public WB_Point localPoint2D(final WB_Coordinate p) {
		return new WB_Point(u.xd() * (p.xd() - origin.xd()) + u.yd()
				* (p.yd() - origin.yd()) + u.zd() * (p.zd() - origin.zd()),
				v.xd() * (p.xd() - origin.xd()) + v.yd()
						* (p.yd() - origin.yd()) + v.zd()
						* (p.zd() - origin.zd()));

	}

	// Return embedded point coordinates relative to world axes
	/**
	 * Extract point.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ point3d
	 */
	public WB_Point extractPoint(final WB_Coordinate p) {
		return new WB_Point(origin.xd() + p.xd() * u.xd() + p.yd() * v.xd(),
				origin.yd() + p.xd() * u.yd() + p.yd() * v.yd(), origin.zd()
						+ p.xd() * u.zd() + p.yd() * v.zd());
	}

	// Return embedded point coordinates relative to world axes
	/**
	 * Extract point.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return the w b_ point3d
	 */
	public WB_Point extractPoint(final double x, final double y) {
		return new WB_Point(origin.xd() + x * u.xd() + y * v.xd(), origin.yd()
				+ x * u.yd() + y * v.yd(), origin.zd() + x * u.zd() + y
				* v.zd());
	}

	// Return coordinates relative to world axes
	/**
	 * Extract point.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ point3d
	 */
	public WB_Point extractPoint2D(final WB_Coordinate p) {
		return new WB_Point(origin.xd() + p.xd() * u.xd() + p.yd() * v.xd()
				+ p.zd() * n.xd(), origin.yd() + p.xd() * u.yd() + p.yd()
				* v.yd() + p.zd() * n.yd(), origin.zd() + p.xd() * u.zd()
				+ p.yd() * v.zd() + p.zd() * n.zd());
	}

	// Return coordinates relative to world axes
	/**
	 * Extract point.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return the w b_ point3d
	 */
	public WB_Point extractPoint(final double x, final double y, final double z) {
		return new WB_Point(origin.xd() + x * u.xd() + y * v.xd() + z * n.xd(),
				origin.yd() + x * u.yd() + y * v.yd() + z * n.yd(), origin.zd()
						+ x * u.zd() + y * v.zd() + z * n.zd());
	}

	// Return new point mirrored across plane
	/**
	 * Mirror point.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ point3d
	 */
	public WB_Point mirrorPoint(final WB_Coordinate p) {
		if (WB_Epsilon.isZero(WB_Distance.getDistance3D(p, this))) {
			return new WB_Point(p);
		}
		return extractPoint2D(localPoint(p).scaleSelf(1, 1, -1));

	}

	// Return copy of u coordinate axis in world coordinates
	/**
	 * Gets the u.
	 * 
	 * @return the u
	 */
	public WB_Vector getU() {
		return u.get();
	}

	// Return copy of v coordinate axis in world coordinates
	/**
	 * Gets the v.
	 * 
	 * @return the v
	 */
	public WB_Vector getV() {
		return v.get();
	}

	// Return copy of w coordinate axis in world coordinates
	/**
	 * Gets the w.
	 * 
	 * @return the w
	 */
	public WB_Vector getW() {
		return getNormal();
	}
}
