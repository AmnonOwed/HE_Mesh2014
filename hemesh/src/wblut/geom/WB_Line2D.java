package wblut.geom;

import java.util.ArrayList;

import wblut.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_Line2D extends WB_Linear2D {

	public WB_Line2D() {
		super();

	}

	public WB_Line2D(final WB_Coordinate o, final WB_Coordinate d) {
		super(o, d, true);

	}

	public WB_Line2D(final double p1x, final double p1y, final double p2x,
			final double p2y) {
		super(p1x, p1y, p2x, p2y);
	}

	public void setFromPoints(final WB_Coordinate p1, final WB_Coordinate p2) {
		super.set(p1, p2);

	}

	public double getT(final WB_Coordinate p) {
		double t = Double.NaN;
		final WB_Point proj = WB_Intersection2D.closestPoint2D(p, this);
		final double x = WB_Math.fastAbs(direction.x);
		final double y = WB_Math.fastAbs(direction.y);
		if (x >= y) {
			t = (proj.x - origin.x) / (direction.x);
		} else {
			t = (proj.y - origin.y) / (direction.y);
		}
		return t;
	}

	public WB_Classification classifyPointToLine2D(final WB_Coordinate p) {

		final double dist = -direction.y * p.xd() + direction.x * p.yd()
				+ origin.x * direction.y - origin.y * direction.x;

		if (dist > WB_Epsilon.EPSILON) {
			return WB_Classification.FRONT;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	public static WB_Classification classifyPointToLine2D(
			final WB_Coordinate p, final WB_Line2D L) {

		final double dist = L.a() * p.xd() + L.b() * p.yd() + L.c();

		if (dist > WB_Epsilon.EPSILON) {
			return WB_Classification.FRONT;
		}
		if (dist < -WB_Epsilon.EPSILON) {
			return WB_Classification.BACK;
		}
		return WB_Classification.ON;
	}

	public WB_Classification classifySegmentToLine2D(final Segment seg) {
		final WB_Classification a = classifyPointToLine2D(seg.getOrigin());
		final WB_Classification b = classifyPointToLine2D(seg.getEndpoint());
		if (a == WB_Classification.ON) {
			if (b == WB_Classification.ON) {
				return WB_Classification.ON;
			} else if (b == WB_Classification.FRONT) {
				return WB_Classification.FRONT;
			} else {
				return WB_Classification.BACK;
			}
		}
		if (b == WB_Classification.ON) {
			if (a == WB_Classification.FRONT) {
				return WB_Classification.FRONT;
			} else {
				return WB_Classification.BACK;
			}
		}
		if ((a == WB_Classification.FRONT) && (b == WB_Classification.BACK)) {
			return WB_Classification.CROSSING;
		}
		if ((a == WB_Classification.BACK) && (b == WB_Classification.FRONT)) {
			return WB_Classification.CROSSING;
		}

		if (a == WB_Classification.FRONT) {
			return WB_Classification.FRONT;
		}
		return WB_Classification.BACK;
	}

	public WB_Classification classifyPolygonToLine2D(final WB_SimplePolygon2D P) {

		int numFront = 0;
		int numBack = 0;

		for (int i = 0; i < P.n; i++) {

			if (classifyPointToLine2D(P.points[i]) == WB_Classification.FRONT) {
				numFront++;

			} else if (classifyPointToLine2D(P.points[i]) == WB_Classification.BACK) {
				numBack++;
			}

			if (numFront > 0 && numBack > 0) {
				return WB_Classification.CROSSING;
			}

		}
		if (numFront > 0) {
			return WB_Classification.FRONT;
		}
		if (numBack > 0) {
			return WB_Classification.BACK;
		}
		return null;
	}

	public static WB_Line2D getLineTangentToCircleAtPoint(final WB_Circle C,
			final WB_Coordinate p) {
		final WB_Vector v = new WB_Vector(C.getCenter(), p);
		return new WB_Line2D(p, new WB_Point(-v.y, v.x));

	}

	public static ArrayList<WB_Line2D> getLinesTangentToCircleThroughPoint(
			final WB_Circle C, final WB_Coordinate p) {
		final ArrayList<WB_Line2D> result = new ArrayList<WB_Line2D>(2);
		final double dcp = WB_Distance2D.getDistance(C.getCenter(), p);

		if (WB_Epsilon.isZero(dcp - C.getRadius())) {
			final WB_Vector u = new WB_Vector(C.getCenter(), p);
			result.add(new WB_Line2D(p, new WB_Point(-u.y, u.x)));
		} else if (dcp < C.getRadius()) {
			return result;
		} else {
			final WB_Vector u = new WB_Vector(C.getCenter(), p);
			final double ux2 = u.x * u.x;
			final double ux4 = ux2 * ux2;
			final double uy2 = u.y * u.y;
			final double r2 = C.getRadius() * C.getRadius();
			final double r4 = r2 * r2;
			final double num = r2 * uy2;
			final double denom = ux2 + uy2;
			final double rad = Math.sqrt(-r4 * ux2 + r2 * ux4 + r2 * ux2 * uy2);

			result.add(new WB_Line2D(p, new WB_Point(-((r2 * u.y) + rad)
					/ denom, (r2 - (num + u.y * rad) / denom) / u.x)));
			result.add(new WB_Line2D(p, new WB_Point(-((r2 * u.y) - rad)
					/ denom, (r2 - (num - u.y * rad) / denom) / u.x)));

		}

		return result;

	}

	public static ArrayList<WB_Line2D> getLinesTangentTo2Circles(
			final WB_Circle C0, final WB_Circle C1) {
		final ArrayList<WB_Line2D> result = new ArrayList<WB_Line2D>(4);
		final WB_Point w = C1.getCenter().sub(C0.getCenter());
		final double wlensqr = w.getSqLength();
		final double rsum = C0.getRadius() + C1.getRadius();
		if (wlensqr <= rsum * rsum + WB_Epsilon.SQEPSILON) {
			return result;
		}
		final double rdiff = C1.getRadius() - C0.getRadius();
		if (!WB_Epsilon.isZero(rdiff)) {
			final double r0sqr = C0.getRadius() * C0.getRadius();
			final double r1sqr = C1.getRadius() * C1.getRadius();
			final double c0 = -r0sqr;
			final double c1 = 2 * r0sqr;
			final double c2 = C1.getRadius() * C1.getRadius() - r0sqr;
			final double invc2 = 1.0 / c2;
			final double discr = Math.sqrt(WB_Math.fastAbs(c1 * c1 - 4 * c0
					* c2));
			double s, oms, a;
			s = -0.5 * (c1 + discr) * invc2;
			if (s >= 0.5) {
				a = Math.sqrt(WB_Math.fastAbs(wlensqr - r0sqr / (s * s)));
			} else {
				oms = 1.0 - s;
				a = Math.sqrt(WB_Math.fastAbs(wlensqr - r1sqr / (oms * oms)));
			}
			WB_Point[] dir = getDirections(w, a);

			WB_Point org = new WB_Point(C0.getCenter().x + s * w.x,
					C0.getCenter().y + s * w.y);
			result.add(new WB_Line2D(org, dir[0]));
			result.add(new WB_Line2D(org, dir[1]));

			s = -0.5 * (c1 - discr) * invc2;
			if (s >= 0.5) {
				a = Math.sqrt(WB_Math.fastAbs(wlensqr - r0sqr / (s * s)));
			} else {
				oms = 1.0 - s;
				a = Math.sqrt(WB_Math.fastAbs(wlensqr - r1sqr / (oms * oms)));
			}
			dir = getDirections(w, a);

			org = new WB_Point(C0.getCenter().x + s * w.x, C0.getCenter().y + s
					* w.y);
			result.add(new WB_Line2D(org, dir[0]));
			result.add(new WB_Line2D(org, dir[1]));

		} else {
			final WB_Point mid = (C0.getCenter().add(C1.getCenter()))
					._mulSelf(0.5);
			final double a = Math.sqrt(WB_Math.fastAbs(wlensqr - 4
					* C0.getRadius() * C0.getRadius()));
			final WB_Point[] dir = getDirections(w, a);
			result.add(new WB_Line2D(mid, dir[0]));
			result.add(new WB_Line2D(mid, dir[1]));

			final double invwlen = 1.0 / Math.sqrt(wlensqr);
			w.x *= invwlen;
			w.y *= invwlen;
			result.add(new WB_Line2D(new WB_Point(mid.x + C0.getRadius() * w.y,
					mid.y - C0.getRadius() * w.x), w));
			result.add(new WB_Line2D(new WB_Point(mid.x - C0.getRadius() * w.y,
					mid.y + C0.getRadius() * w.x), w));

		}

		return result;
	}

	private static WB_Point[] getDirections(final WB_Coordinate w,
			final double a) {
		final WB_Point[] dir = new WB_Point[2];
		final double asqr = a * a;
		final double wxsqr = w.xd() * w.xd();
		final double wysqr = w.yd() * w.yd();
		final double c2 = wxsqr + wysqr;
		final double invc2 = 1.0 / c2;
		double c0, c1, discr, invwx;
		final double invwy;

		if (WB_Math.fastAbs(w.xd()) >= WB_Math.fastAbs(w.yd())) {
			c0 = asqr - wxsqr;
			c1 = -2 * a * w.yd();
			discr = Math.sqrt(WB_Math.fastAbs(c1 * c1 - 4 * c0 * c2));
			invwx = 1.0 / w.xd();
			final double dir0y = -0.5 * (c1 + discr) * invc2;
			dir[0] = new WB_Point((a - w.yd() * dir0y) * invwx, dir0y);
			final double dir1y = -0.5 * (c1 - discr) * invc2;
			dir[1] = new WB_Point((a - w.yd() * dir1y) * invwx, dir1y);

		} else {
			c0 = asqr - wysqr;
			c1 = -2 * a * w.xd();
			discr = Math.sqrt(WB_Math.fastAbs(c1 * c1 - 4 * c0 * c2));
			invwy = 1.0 / w.yd();
			final double dir0x = -0.5 * (c1 + discr) * invc2;
			dir[0] = new WB_Point(dir0x, (a - w.xd() * dir0x) * invwy);
			final double dir1x = -0.5 * (c1 - discr) * invc2;
			dir[1] = new WB_Point(dir1x, (a - w.xd() * dir1x) * invwy);

		}

		return dir;
	}

	public static WB_Line2D getPerpendicularLineThroughPoint(final WB_Line2D L,
			final WB_Coordinate p) {
		return new WB_Line2D(p, new WB_Point(-L.getDirection().y,
				L.getDirection().x));

	}

	public static WB_Line2D getParallelLineThroughPoint(final WB_Line2D L,
			final WB_Coordinate p) {
		return new WB_Line2D(p, L.getDirection());

	}

	public static WB_Line2D getBisector(final WB_Coordinate p,
			final WB_Coordinate q) {
		return new WB_Line2D(WB_Point.interpolate(p, q, 0.5), new WB_Point(
				p.yd() - q.yd(), q.xd() - p.xd()));
	}

	public static WB_Line2D[] getParallelLines(final WB_Line2D L, final double d) {
		final WB_Line2D[] result = new WB_Line2D[2];
		result[0] = new WB_Line2D(
				new WB_Point(L.getOrigin().x - d * L.getDirection().y,
						L.getOrigin().y + d * L.getDirection().x),
				L.getDirection());
		result[1] = new WB_Line2D(
				new WB_Point(L.getOrigin().x + d * L.getDirection().y,
						L.getOrigin().y - d * L.getDirection().x),
				L.getDirection());
		return result;
	}

	public static WB_Line2D[] getPerpendicularLinesTangentToCircle(
			final WB_Line2D L, final WB_Circle C) {
		final WB_Line2D[] result = new WB_Line2D[2];
		result[0] = new WB_Line2D(new WB_Point(C.getCenter().x + C.getRadius()
				* L.getDirection().x, C.getCenter().y + C.getRadius()
				* L.getDirection().y), new WB_Point(-L.getDirection().y,
				L.getDirection().x));

		result[1] = new WB_Line2D(new WB_Point(C.getCenter().x - C.getRadius()
				* L.getDirection().x, C.getCenter().y - C.getRadius()
				* L.getDirection().y), new WB_Point(-L.getDirection().y,
				L.getDirection().x));
		return result;
	}

}