package wblut.geom;

import wblut.WB_Epsilon;

public class WB_Geom {

	public static boolean between2D(final WB_Point a, final WB_Point b,
			final WB_Point c) {
		if (WB_Geom.coincident2D(a, c)) {
			return true;
		} else if (WB_Geom.coincident2D(b, c)) {
			return true;
		} else {
			if (WB_Distance2D.getSqDistanceToLine(c, a, b) < WB_Epsilon.SQEPSILON) {
				final double d = projectedDistanceNorm(c, a, b);
				if (0 < d && d < 1) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean betweenStrict2D(final WB_Point a, final WB_Point b,
			final WB_Point c) {
		if (WB_Geom.coincident2D(a, c)) {
			return true;
		} else if (WB_Geom.coincident2D(b, c)) {
			return true;
		} else {
			if (WB_Distance2D.getSqDistanceToLine(c, a, b) < WB_Epsilon.SQEPSILON) {
				final double d = projectedDistanceNorm(c, a, b);
				if (0 < d && d < 1) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean coincident2D(final WB_Point a, final WB_Point b) {
		if (WB_Distance2D.getSqDistance(a, b) < WB_Epsilon.SQEPSILON) {
			return true;
		}
		return false;
	}

	public static double projectedDistanceNorm(final WB_Point a,
			final WB_Point b, final WB_Point p) {
		double x1, x2, y1, y2;
		x1 = b.x - a.x;
		x2 = p.x - a.x;
		y1 = b.y - a.y;
		y2 = p.y - a.y;
		return (x1 * x2 + y1 * y2) / (x1 * x1 + y1 * y1);
	}

	public static double pointAlongLine(final WB_Point p, final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(p);
		ac._subSelf(L.getOrigin());
		return ac.dot(ab);
	}

}
