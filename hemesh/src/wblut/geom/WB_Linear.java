package wblut.geom;

import wblut.WB_Epsilon;

public abstract class WB_Linear {

	protected WB_Point origin;

	protected WB_Vector direction;

	public WB_Linear() {
		origin = new WB_Point();
		direction = new WB_Vector(1, 0, 0);
	}

	public WB_Linear(final WB_Coordinate o, final WB_Coordinate d) {
		origin = new WB_Point(o);
		direction = new WB_Vector(d);
		direction._normalizeSelf();
	}

	protected void set(final WB_Coordinate o, final WB_Coordinate d) {
		origin._set(o);
		direction._set(d);
		direction._normalizeSelf();
	}

	public WB_Point getPointOnLine(final double t) {
		final WB_Point result = new WB_Point(direction);
		result._scaleSelf(t);
		result.moveBy(origin);
		return result;
	}

	public void getPointOnLineInto(final double t, final WB_MutableCoordinate p) {
		p._set(direction.mul(t)._addSelf(origin));
	}

	public WB_Point getOrigin() {
		return origin;
	}

	public WB_Vector getDirection() {
		return direction;
	}

	public WB_Vector getNormal() {
		WB_Vector n = new WB_Vector(0, 0, 1);
		n = n.cross(direction);
		final double d = n._normalizeSelf();
		if (WB_Epsilon.isZero(d)) {
			n = new WB_Vector(1, 0, 0);
		}
		return n;
	}
}
