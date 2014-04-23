package wblut.geom;

import wblut.WB_Epsilon;

public abstract class WB_Linear2D {

	protected WB_Point origin;

	protected WB_Vector direction;

	public WB_Linear2D() {
		origin = new WB_Point();
		direction = new WB_Vector(1, 0);
	}

	public WB_Linear2D(final WB_Coordinate o, final WB_Coordinate d,
			final boolean asDirection) {
		if (asDirection) {
			origin = new WB_Point(o);
			direction = new WB_Vector(d);
			direction._normalizeSelf();
		} else {
			origin = new WB_Point(o);
			direction = new WB_Vector(o, d);
			direction._normalizeSelf();
		}
	}

	public WB_Linear2D(final WB_Coordinate p1, final WB_Coordinate p2) {
		origin = new WB_Point(p1);
		direction = new WB_Vector(p1, p2);
		direction._normalizeSelf();
	}

	public WB_Linear2D(final double x1, final double y1, final double x2,
			final double y2) {
		origin = new WB_Point(x1, y1);
		direction = new WB_Vector(x2 - x1, y2 - y1);
		direction._normalizeSelf();
	}

	public void set(final WB_Coordinate o, final WB_Coordinate d,
			final boolean asDirection) {
		if (asDirection) {
			origin._set(o);
			direction._set(d);
			direction._normalizeSelf();
		} else {
			set(o, d);
		}
	}

	public void set(final WB_Coordinate p1, final WB_Coordinate p2) {
		origin._set(p1);
		direction._set(p2.xd() - p1.xd(), p2.yd() - p1.yd());
		direction._normalizeSelf();
	}

	public WB_Point getPoint(final double t) {
		final WB_Point result = new WB_Point(direction);
		result._scaleSelf(t);
		result.moveBy(origin);
		return result;
	}

	public void getPointInto(final double t, final WB_Point p) {
		p.moveTo(direction);
		p._scaleSelf(t);
		p.moveBy(origin);
	}

	public WB_Point getOrigin() {
		return origin;
	}

	public WB_Vector getDirection() {
		return direction;
	}

	public WB_Point getNormal() {

		WB_Point n = new WB_Point(-direction.y, direction.x);
		final double d = n._normalizeSelf();
		if (WB_Epsilon.isZero(d)) {
			n = new WB_Point(1, 0);
		}
		return n;
	}

	public double a() {
		return -direction.y;
	}

	public double b() {
		return direction.x;
	}

	public double c() {
		return origin.x * direction.y - origin.y * direction.x;
	}

}
