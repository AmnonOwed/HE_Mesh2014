package wblut.geom;

public class WB_Distance2D {

	public static double getSqDistance(final WB_Coordinate p,
			final WB_Coordinate q) {
		return ((q.xd() - p.xd()) * (q.xd() - p.xd()) + (q.yd() - p.yd())
				* (q.yd() - p.yd()));
	}

	public static double getDistance(final WB_Coordinate p,
			final WB_Coordinate q) {
		return Math.sqrt(getSqDistance(p, q));
	}

	public static double getSqDistance(final double[] p, final double[] q) {
		return ((q[0] - p[0]) * (q[0] - p[0]) + (q[1] - p[1]) * (q[1] - p[1]));
	}

	public static double getDistance(final double[] p, final double[] q) {
		return Math.sqrt(getSqDistance(p, q));
	}

	public static double getSqDistance(final WB_Coordinate p, final Segment S) {
		final WB_Vector ab = new WB_Vector(S.getOrigin(), S.getEndpoint());
		final WB_Vector ac = new WB_Vector(p).sub(S.getOrigin());
		final WB_Vector bc = new WB_Vector(p).sub(S.getEndpoint());
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - e * e / f;
	}

	public static double getDistance(final WB_Coordinate p, final Segment S) {
		return Math.sqrt(getSqDistance(p, S));
	}

	public static double getSqDistanceToSegment(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final WB_Vector bc = new WB_Vector(b, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		if (e >= f) {
			return bc.dot(bc);
		}
		return ac.dot(ac) - e * e / f;
	}

	public static double getDistanceToSegment(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToSegment(p, a, b));
	}

	public static double getSqDistance(final WB_Coordinate p, final WB_Line2D L) {
		final WB_Vector ab = L.getDirection();
		final WB_Vector ac = new WB_Vector(L.getOrigin(), p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double getDistance(final WB_Coordinate p, final WB_Line2D L) {
		return Math.sqrt(getSqDistance(p, L));
	}

	public static double getSqDistanceToLine(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot(ab);
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double getDistanceToLine(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToLine(p, a, b));
	}

	public static double getSqDistance(final WB_Coordinate p, final WB_Ray2D R) {
		final WB_Point ab = R.getDirection();
		final WB_Vector ac = new WB_Vector(R.getOrigin(), p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double getDistance(final WB_Coordinate p, final WB_Ray2D R) {
		return Math.sqrt(getSqDistance(p, R));
	}

	public static double getSqDistanceToRay(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		final WB_Vector ab = new WB_Vector(a, b);
		final WB_Vector ac = new WB_Vector(a, p);
		final double e = ac.dot(ab);
		if (e <= 0) {
			return ac.dot(ac);
		}
		final double f = ab.dot(ab);
		return ac.dot(ac) - e * e / f;
	}

	public static double getDistanceToRay(final WB_Coordinate p,
			final WB_Coordinate a, final WB_Coordinate b) {
		return Math.sqrt(getSqDistanceToRay(p, a, b));
	}

}
