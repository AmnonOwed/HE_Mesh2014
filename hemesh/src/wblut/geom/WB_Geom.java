package wblut.geom;

import wblut.math.WB_Epsilon;

public class WB_Geom {

	public static boolean between2D(final WB_Coordinate a,
			final WB_Coordinate b, final WB_Coordinate c) {
		if (WB_Geom.coincident2D(a, c)) {
			return true;
		}
		else if (WB_Geom.coincident2D(b, c)) {
			return true;
		}
		else {
			if (WB_Distance.getSqDistanceToLine2D(c, a, b) < WB_Epsilon.SQEPSILON) {
				final double d = projectedDistanceNorm(c, a, b);
				if (0 < d && d < 1) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean betweenStrict2D(final WB_Coordinate a,
			final WB_Coordinate b, final WB_Coordinate c) {
		if (WB_Geom.coincident2D(a, c)) {
			return true;
		}
		else if (WB_Geom.coincident2D(b, c)) {
			return true;
		}
		else {
			if (WB_Distance.getSqDistanceToLine2D(c, a, b) < WB_Epsilon.SQEPSILON) {
				final double d = projectedDistanceNorm(c, a, b);
				if (0 < d && d < 1) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean coincident2D(final WB_Coordinate a,
			final WB_Coordinate b) {
		if (WB_Distance.getSqDistance2D(a, b) < WB_Epsilon.SQEPSILON) {
			return true;
		}
		return false;
	}

	public static double projectedDistanceNorm(final WB_Coordinate a,
			final WB_Coordinate b, final WB_Coordinate p) {
		double x1, x2, y1, y2;
		x1 = b.xd() - a.xd();
		x2 = p.xd() - a.xd();
		y1 = b.yd() - a.yd();
		y2 = p.yd() - a.yd();
		return (x1 * x2 + y1 * y2) / (x1 * x1 + y1 * y1);
	}

	public static double pointAlongLine(final WB_Coordinate p, final WB_Line L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(p);
		ac.subSelf(L.getOrigin());
		return ac.dot(ab);
	}

}
