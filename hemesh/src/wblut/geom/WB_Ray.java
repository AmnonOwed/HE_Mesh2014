package wblut.geom;

import wblut.math.WB_Math;

public class WB_Ray extends WB_Linear {

	public static WB_Ray X() {
		return new WB_Ray(0, 0, 0, 1, 0, 0);
	}

	public static WB_Ray Y() {
		return new WB_Ray(0, 0, 0, 0, 1, 0);
	}

	public static WB_Ray Z() {
		return new WB_Ray(0, 0, 0, 0, 0, 1);
	}

	public WB_Ray() {
		origin = new WB_Point();
		direction = new WB_Vector(1, 0, 0);

	}

	public WB_Ray(final WB_Coordinate o, final WB_Coordinate d) {
		origin = new WB_Point(o);
		direction = new WB_Vector(d);
		direction._normalizeSelf();
	}

	public WB_Ray(final double ox, final double oy, final double oz,
			final double dx, final double dy, final double dz) {
		origin = new WB_Point(ox, oy, oz);
		direction = new WB_Vector(dx, dy, dz);
		direction._normalizeSelf();
	}

	@Override
	public String toString() {
		return "Ray: " + origin.toString() + " " + direction.toString();
	}

	public void set(final WB_Coordinate o, final WB_Coordinate d) {
		origin = new WB_Point(o);
		direction = new WB_Vector(d);
		direction._normalizeSelf();

	}

	public void setFromPoints(final WB_Coordinate p1, final WB_Coordinate p2) {
		origin = new WB_Point(p1);
		direction = new WB_Vector(p1, p2);
		direction._normalizeSelf();

	}

	public WB_Point getPointOnLine(final double t) {
		final WB_Point result = new WB_Point(direction);
		result._scaleSelf(WB_Math.max(0, t));
		result.moveBy(origin);
		return result;
	}

	public void getPointOnLineInto(final double t, final WB_Point p) {
		p.moveTo(direction);
		if (t > 0) {
			p._scaleSelf(t);
		}
		p.moveBy(origin);
	}

	public WB_Point getOrigin() {
		return origin;
	}

	public WB_Vector getDirection() {
		return direction;
	}

}