package wblut.geom;

import wblut.math.WB_Math;

public class WB_Ray2D {

	private WB_Point origin;

	private WB_Point direction;

	public WB_Ray2D() {
		origin = new WB_Point();
		direction = new WB_Point(1, 0);

	}

	public WB_Ray2D(final WB_Point o, final WB_Point d) {
		origin = o.get();
		direction = d.get();
		direction._normalizeSelf();

	}

	public void set(final WB_Point o, final WB_Point d) {
		origin = o.get();
		direction = d.get();
		direction._normalizeSelf();

	}

	public void setFromPoints(final WB_Point p1, final WB_Point p2) {
		origin = p1.get();
		direction = p2.sub(p1);
		direction._normalizeSelf();

	}

	public WB_Point getPoint(final double t) {
		final WB_Point result = new WB_Point(direction);
		result._scaleSelf(WB_Math.max(0, t));
		result.moveBy(origin);
		return result;
	}

	public void getPointInto(final double t, final WB_Point p) {
		p.moveTo(direction);
		if (t > 0) {
			p._scaleSelf(t);
		}
		p.moveBy(origin);
	}

	public WB_Point getOrigin() {
		return origin;
	}

	public WB_Point getDirection() {
		return direction;
	}
}
