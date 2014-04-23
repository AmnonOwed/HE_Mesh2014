package wblut.geom;

import java.util.ArrayList;
import java.util.List;

import wblut.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_Intersection2D {

	public static WB_IntersectionResult intersect2D(final Segment S1,
			final Segment S2) {
		final double a1 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getEndpoint());
		final double a2 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && a1 * a2 < 0) {
			final double a3 = WB_Triangle2D.twiceSignedTriArea2D(
					S2.getOrigin(), S2.getEndpoint(), S1.getOrigin());
			final double a4 = a3 + a2 - a1;
			if (a3 * a4 < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				final WB_IntersectionResult i = new WB_IntersectionResult();
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getParametricPoint(t1);
				i.dimension = 0;
				i.sqDist = 0;
				return i;

			}

		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = false;
		i.t1 = 0;
		i.t2 = 0;
		i.sqDist = Float.POSITIVE_INFINITY;
		return i;

	}

	public static void intersect2DInto(final Segment S1, final Segment S2,
			final WB_IntersectionResult i) {
		final double a1 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getEndpoint());
		final double a2 = WB_Triangle2D.twiceSignedTriArea2D(S1.getOrigin(),
				S1.getEndpoint(), S2.getOrigin());
		if (!WB_Epsilon.isZero(a1) && !WB_Epsilon.isZero(a2) && a1 * a2 < 0) {
			final double a3 = WB_Triangle2D.twiceSignedTriArea2D(
					S2.getOrigin(), S2.getEndpoint(), S1.getOrigin());
			final double a4 = a3 + a2 - a1;
			if (a3 * a4 < 0) {
				final double t1 = a3 / (a3 - a4);
				final double t2 = a1 / (a1 - a2);
				i.intersection = true;
				i.t1 = t1;
				i.t2 = t2;
				i.object = S1.getParametricPoint(t1);
				i.dimension = 0;
				i.sqDist = 0;

			}

		} else {
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.sqDist = Float.POSITIVE_INFINITY;
		}
	}

	public static WB_Segment[] splitSegment(final WB_Segment S,
			final WB_Line2D L) {
		final WB_Segment[] result = new WB_Segment[2];
		final WB_IntersectionResult ir2D = closestPoint2D(S, L);
		if (!ir2D.intersection) {
			return null;
		}
		if (ir2D.dimension == 0) {
			if (L.classifyPointToLine2D(S.getOrigin()) == WB_Classification.FRONT) {
				result[0] = new WB_Segment(S.getOrigin(),
						(WB_Point) ir2D.object);
				result[1] = new WB_Segment((WB_Point) ir2D.object,
						S.getEndpoint());
			} else if (L.classifyPointToLine2D(S.getOrigin()) == WB_Classification.BACK) {
				result[1] = new WB_Segment(S.getOrigin(),
						(WB_Point) ir2D.object);
				result[0] = new WB_Segment((WB_Point) ir2D.object,
						S.getEndpoint());
			}
		}
		return result;

	}

	public static double[] intervalIntersection(final double u0,
			final double u1, final double v0, final double v1) {
		if ((u0 >= u1) || (v0 >= v1)) {
			throw new IllegalArgumentException(
					"Interval degenerate or reversed.");
		}
		final double[] result = new double[3];
		if ((u1 < v0) || (u0 > v1)) {
			return result;
		}
		if (u1 > v0) {
			if (u0 < v1) {
				result[0] = 2;
				if (u0 < v0) {
					result[1] = v0;
				} else {
					result[1] = u0;
				}
				if (u1 > v1) {
					result[2] = v1;
				} else {
					result[2] = u1;
				}
			} else {
				result[0] = 1;
				result[1] = u0;
			}
		} else {
			result[0] = 1;
			result[1] = u1;
		}
		return result;

	}

	public static WB_SimplePolygon2D[] splitPolygon2D(
			final WB_SimplePolygon2D poly, final WB_Line2D L) {
		int numFront = 0;
		int numBack = 0;

		final ArrayList<WB_Point> frontVerts = new ArrayList<WB_Point>(20);
		final ArrayList<WB_Point> backVerts = new ArrayList<WB_Point>(20);

		final int numVerts = poly.n;
		if (numVerts > 0) {
			WB_Point a = poly.points[numVerts - 1];
			WB_Classification aSide = L.classifyPointToLine2D(a);
			WB_Point b;
			WB_Classification bSide;

			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i = new WB_IntersectionResult();
				b = poly.points[n];
				bSide = L.classifyPointToLine2D(b);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = closestPoint2D(L, new WB_Segment(a, b));
						WB_Point p1 = null;
						if (i.dimension == 0) {
							p1 = (WB_Point) i.object;
						} else if (i.dimension == 1) {
							p1 = ((Segment) i.object).getOrigin();
						}
						frontVerts.add(p1);
						numFront++;
						backVerts.add(p1);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = closestPoint2D(L, new WB_Segment(a, b));

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */
						final WB_Point p1 = (WB_Point) i.object;

						frontVerts.add(p1);
						numFront++;
						backVerts.add(p1);
						numBack++;
					} else if (aSide == WB_Classification.ON) {
						backVerts.add(a);
						numBack++;
					}
					backVerts.add(b);
					numBack++;
				} else {
					frontVerts.add(b);
					numFront++;
					if (aSide == WB_Classification.BACK) {
						backVerts.add(b);
						numBack++;
					}
				}
				a = b;
				aSide = bSide;

			}

		}
		final WB_SimplePolygon2D[] result = new WB_SimplePolygon2D[2];
		result[0] = new WB_SimplePolygon2D(frontVerts);
		result[1] = new WB_SimplePolygon2D(backVerts);
		return result;

	}

	public static ArrayList<WB_Point> intersect2D(final WB_Circle C0,
			final WB_Circle C1) {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
		final WB_Point u = C1.getCenter().sub(C0.getCenter());
		final double d2 = u.getSqLength();
		final double d = Math.sqrt(d2);
		if (WB_Epsilon.isEqualAbs(d, C0.getRadius() + C1.getRadius())) {
			result.add(WB_Point.interpolate(C0.getCenter(), C1.getCenter(),
					C0.getRadius() / (C0.getRadius() + C1.getRadius())));
			return result;
		}
		if (d > (C0.getRadius() + C1.getRadius())
				|| d < WB_Math.fastAbs(C0.getRadius() - C1.getRadius())) {
			return result;
		}
		final double r02 = C0.getRadius() * C0.getRadius();
		final double r12 = C1.getRadius() * C1.getRadius();
		final double a = (r02 - r12 + d2) / (2 * d);
		final double h = Math.sqrt(r02 - a * a);
		final WB_Point c = u.mul(a / d)._addSelf(C0.getCenter());
		final double p0x = c.x + h * (C1.getCenter().y - C0.getCenter().y) / d;
		final double p0y = c.y - h * (C1.getCenter().x - C0.getCenter().x) / d;
		final double p1x = c.x - h * (C1.getCenter().y - C0.getCenter().y) / d;
		final double p1y = c.y + h * (C1.getCenter().x - C0.getCenter().x) / d;
		final WB_Point p0 = new WB_Point(p0x, p0y);
		result.add(p0);
		final WB_Point p1 = new WB_Point(p1x, p1y);
		if (!WB_Epsilon.isZeroSq(WB_Distance2D.getSqDistance(p0, p1))) {
			result.add(new WB_Point(p1x, p1y));
		}
		return result;
	}

	public static ArrayList<WB_Point> intersect2D(final WB_Line2D L,
			final WB_Circle C) {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();

		final double b = 2 * (L.getDirection().x
				* (L.getOrigin().x - C.getCenter().x) + L.getDirection().y
				* (L.getOrigin().y - C.getCenter().y));
		final double c = C.getCenter().getSqLength()
				+ L.getOrigin().getSqLength()
				- 2
				* (C.getCenter().x * L.getOrigin().x + C.getCenter().y
						* L.getOrigin().y) - C.getRadius() * C.getRadius();
		double disc = b * b - 4 * c;
		if (disc < -WB_Epsilon.EPSILON) {
			return result;
		}

		if (WB_Epsilon.isZero(disc)) {
			result.add(L.getPoint(-0.5 * b));
			return result;
		}
		disc = Math.sqrt(disc);
		result.add(L.getPoint(0.5 * (-b + disc)));
		result.add(L.getPoint(0.5 * (-b - disc)));
		return result;
	}

	public static boolean getIntersection2DProper(final WB_Point a,
			final WB_Point b, final WB_Point c, final WB_Point d) {
		if (WB_Predicates2D.orient2d(a, b, c) == 0
				|| WB_Predicates2D.orient2d(a, b, d) == 0
				|| WB_Predicates2D.orient2d(c, d, a) == 0
				|| WB_Predicates2D.orient2d(c, d, b) == 0) {
			return false;
		} else if (WB_Predicates2D.orient2d(a, b, c)
				* WB_Predicates2D.orient2d(a, b, d) > 0
				|| WB_Predicates2D.orient2d(c, d, a)
						* WB_Predicates2D.orient2d(c, d, b) > 0) {
			return false;
		} else {
			return true;
		}
	}

	public static WB_Point closestPoint2D(final WB_Point p, final Segment S) {
		final WB_Point ab = S.getEndpoint().sub(S.getOrigin());
		final WB_Point ac = p.sub(S.getOrigin());
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return S.getOrigin().get();
		} else {
			final double denom = S.getLength() * S.getLength();
			if (t >= denom) {
				t = 1;
				return S.getEndpoint().get();
			} else {
				t = t / denom;
				return new WB_Point(S.getParametricPoint(t));
			}
		}
	}

	public static WB_Point closestPoint2D(final Segment S, final WB_Point p) {
		return closestPoint2D(p, S);
	}

	public static WB_Point closestPointToSegment(final WB_Point p,
			final WB_Point a, final WB_Point b) {
		final WB_Point ab = b.sub(a);
		final WB_Point ac = p.sub(a);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			return a.get();
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				return b.get();
			} else {
				t = t / denom;
				return new WB_Point(a.x + t * ab.x, a.y + t * ab.y);
			}
		}
	}

	public static void closestPointToSegmentInto(final WB_Point p,
			final WB_Point a, final WB_Point b, final WB_Point result) {
		final WB_Point ab = b.sub(a);
		final WB_Point ac = p.sub(a);
		double t = ac.dot(ab);
		if (t <= 0) {
			t = 0;
			result._set(a);
		} else {
			final double denom = ab.dot(ab);
			if (t >= denom) {
				t = 1;
				result._set(b);
			} else {
				t = t / denom;
				result._set(a.x + t * ab.x, a.y + t * ab.y);
			}
		}
	}

	public static WB_Point closestPoint2D(final WB_Point p, final WB_Line2D L) {

		if (WB_Epsilon.isZero(L.getDirection().x)) {
			return new WB_Point(L.getOrigin().x, p.y);
		}
		if (WB_Epsilon.isZero(L.getDirection().y)) {
			return new WB_Point(p.x, L.getOrigin().y);
		}

		final double m = L.getDirection().y / L.getDirection().x;
		final double b = L.getOrigin().y - m * L.getOrigin().x;

		final double x = (m * p.y + p.x - m * b) / (m * m + 1);
		final double y = (m * m * p.y + m * p.x + b) / (m * m + 1);

		return new WB_Point(x, y);

	}

	public static WB_Point closestPointToLine2D(final WB_Point p,
			final WB_Point a, final WB_Point b) {
		final WB_Line2D L = new WB_Line2D();
		L.setFromPoints(a, b);
		return closestPoint2D(p, L);
	}

	public static WB_Point closestPoint2D(final WB_Point p, final WB_Ray2D R) {
		final WB_Point ac = p.sub(R.getOrigin());
		double t = ac.dot(R.getDirection());
		if (t <= 0) {
			t = 0;
			return R.getOrigin().get();
		} else {
			return R.getPoint(t);
		}
	}

	public static WB_Point closestPointToRay2D(final WB_Point p,
			final WB_Point a, final WB_Point b) {
		final WB_Ray2D R = new WB_Ray2D();
		R.setFromPoints(a, b);
		return closestPoint2D(p, R);
	}

	public static WB_IntersectionResult closestPoint2D(final Segment S1,
			final Segment S2) {
		final WB_Point d1 = S1.getEndpoint().sub(S1.getOrigin());
		final WB_Point d2 = S2.getEndpoint().sub(S2.getOrigin());
		final WB_Point r = S1.getOrigin().sub(S2.getOrigin());
		final double a = d1.dot(d1);
		final double e = d2.dot(d2);
		final double f = d2.dot(r);

		if (WB_Epsilon.isZero(a) || WB_Epsilon.isZero(e)) {
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = 0;
			i.object = new WB_Segment(S1.getOrigin().get(), S2.getOrigin()
					.get());
			i.dimension = 1;
			i.sqDist = r.getSqLength();
			return i;
		}

		double t1 = 0;
		double t2 = 0;
		if (WB_Epsilon.isZero(a)) {

			t2 = WB_Math.clamp(f / e, 0, 1);
		} else {
			final double c = d1.dot(r);
			if (WB_Epsilon.isZero(e)) {

				t1 = WB_Math.clamp(-c / a, 0, 1);
			} else {
				final double b = d1.dot(d2);
				final double denom = a * e - b * b;
				if (!WB_Epsilon.isZero(denom)) {
					t1 = WB_Math.clamp((b * f - c * e) / denom, 0, 1);
				} else {
					t1 = 0;
				}
				final double tnom = b * t1 + f;
				if (tnom < 0) {
					t1 = WB_Math.clamp(-c / a, 0, 1);
				} else if (tnom > e) {
					t2 = 1;
					t1 = WB_Math.clamp((b - c) / a, 0, 1);
				} else {
					t2 = tnom / e;
				}
			}
		}
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = (t1 > 0) && (t1 < 1) && (t2 > 0) && (t2 < 1);
		i.t1 = t1;
		i.t2 = t2;
		final WB_Point p1 = S1.getParametricPoint(t1);
		final WB_Point p2 = S2.getParametricPoint(t2);
		i.sqDist = WB_Distance2D.getSqDistance(p1, p2);
		if (i.intersection) {
			i.dimension = 0;
			i.object = p1;
		} else {
			i.dimension = 1;
			i.object = new WB_Segment(p1, p2);
		}
		return i;

	}

	public static WB_IntersectionResult closestPoint2D(final WB_Line2D L1,
			final WB_Line2D L2) {
		final double a = L1.getDirection().dot(L1.getDirection());
		final double b = L1.getDirection().dot(L2.getDirection());
		final WB_Point r = L1.getOrigin().sub(L2.getOrigin());
		final double c = L1.getDirection().dot(r);
		final double e = L2.getDirection().dot(L2.getDirection());
		final double f = L2.getDirection().dot(r);
		double denom = a * e - b * b;
		if (WB_Epsilon.isZero(denom)) {
			final double t2 = r.dot(L1.getDirection());
			final WB_Point p2 = new WB_Point(L2.getPoint(t2));
			final double d2 = WB_Distance2D.getSqDistance(L1.getOrigin().get(),
					p2);
			final WB_IntersectionResult i = new WB_IntersectionResult();
			i.intersection = false;
			i.t1 = 0;
			i.t2 = t2;
			i.dimension = 1;
			i.object = new WB_Segment(L1.getOrigin().get(), p2);
			i.sqDist = d2;
			return i;
		}
		denom = 1.0 / denom;
		final double t1 = (b * f - c * e) * denom;
		final double t2 = (a * f - b * c) * denom;
		final WB_Point p1 = new WB_Point(L1.getPoint(t1));
		final WB_Point p2 = new WB_Point(L2.getPoint(t2));
		final double d2 = WB_Distance2D.getSqDistance(p1, p2);
		final WB_IntersectionResult i = new WB_IntersectionResult();
		i.intersection = true;
		i.t1 = t1;
		i.t2 = t2;
		i.dimension = 0;
		i.object = p1;
		i.sqDist = d2;
		return i;

	}

	public static WB_IntersectionResult closestPoint2D(final WB_Line2D L,
			final Segment S) {
		final WB_IntersectionResult i = closestPoint2D(L,
				new WB_Line2D(S.getOrigin(), S.getDirection()));
		if (i.dimension == 0) {
			return i;
		}
		if (i.t2 <= WB_Epsilon.EPSILON) {
			i.t2 = 0;
			i.object = new WB_Segment(((Segment) i.object).getOrigin(), S
					.getOrigin().get());
			i.sqDist = ((Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		if (i.t2 >= S.getLength() - WB_Epsilon.EPSILON) {
			i.t2 = 1;
			i.object = new WB_Segment(((Segment) i.object).getOrigin(), S
					.getEndpoint().get());
			i.sqDist = ((Segment) i.object).getLength();
			i.sqDist *= i.sqDist;
			i.intersection = false;
		}
		return i;
	}

	public static WB_IntersectionResult closestPoint2D(final Segment S,
			final WB_Line2D L) {

		return closestPoint2D(L, S);
	}

	// POINT-TRIANGLE

	public static WB_Point closestPoint2D(final WB_Point p,
			final WB_Triangle2D T) {
		final WB_Point ab = T.p2.sub(T.p1);
		final WB_Point ac = T.p3.sub(T.p1);
		final WB_Point ap = p.sub(T.p1);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1.get();
		}

		final WB_Point bp = p.sub(T.p2);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1.add(ab._mulSelf(v));
		}

		final WB_Point cp = p.sub(T.p3);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1.add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.add((T.p3.sub(T.p2))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return T.p1.add(ab._mulSelf(v)._addSelf(ac._mulSelf(w)));
	}

	public static WB_Point closestPointToTriangle2D(final WB_Point p,
			final WB_Point a, final WB_Point b, final WB_Point c) {
		final WB_Point ab = b.sub(a);
		final WB_Point ac = c.sub(a);
		final WB_Point ap = p.sub(a);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return a.get();
		}

		final WB_Point bp = p.sub(b);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return b.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return a.add(ab._mulSelf(v));
		}

		final WB_Point cp = p.sub(c);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return c.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return a.add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return b.add((c.sub(b))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		return a.add(ab._mulSelf(v)._addSelf(ac._mulSelf(w)));
	}

	public static WB_Point closestPointOnPeriphery2D(final WB_Point p,
			final WB_Triangle2D T) {
		final WB_Point ab = T.p2.sub(T.p1);
		final WB_Point ac = T.p3.sub(T.p1);
		final WB_Point ap = p.sub(T.p1);
		final double d1 = ab.dot(ap);
		final double d2 = ac.dot(ap);
		if (d1 <= 0 && d2 <= 0) {
			return T.p1.get();
		}

		final WB_Point bp = p.sub(T.p2);
		final double d3 = ab.dot(bp);
		final double d4 = ac.dot(bp);
		if (d3 >= 0 && d4 <= d3) {
			return T.p2.get();
		}

		final double vc = d1 * d4 - d3 * d2;
		if (vc <= 0 && d1 >= 0 && d3 <= 0) {
			final double v = d1 / (d1 - d3);
			return T.p1.add(ab._mulSelf(v));
		}

		final WB_Point cp = p.sub(T.p3);
		final double d5 = ab.dot(cp);
		final double d6 = ac.dot(cp);
		if (d6 >= 0 && d5 <= d6) {
			return T.p3.get();
		}

		final double vb = d5 * d2 - d1 * d6;
		if (vb <= 0 && d2 >= 0 && d6 <= 0) {
			final double w = d2 / (d2 - d6);
			return T.p1.add(ac._mulSelf(w));
		}

		final double va = d3 * d6 - d5 * d4;
		if (va <= 0 && (d4 - d3) >= 0 && (d5 - d6) >= 0) {
			final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
			return T.p2.add((T.p3.sub(T.p2))._mulSelf(w));
		}

		final double denom = 1.0 / (va + vb + vc);
		final double v = vb * denom;
		final double w = vc * denom;
		final double u = 1 - v - w;
		T.p3.sub(T.p2);
		if (WB_Epsilon.isZero(u - 1)) {
			return T.p1.get();
		}
		if (WB_Epsilon.isZero(v - 1)) {
			return T.p2.get();
		}
		if (WB_Epsilon.isZero(w - 1)) {
			return T.p3.get();
		}
		final WB_Point A = closestPointToSegment(p, T.p2, T.p3);
		final double dA2 = WB_Distance2D.getSqDistance(p, A);
		final WB_Point B = closestPointToSegment(p, T.p1, T.p3);
		final double dB2 = WB_Distance2D.getSqDistance(p, B);
		final WB_Point C = closestPointToSegment(p, T.p1, T.p2);
		final double dC2 = WB_Distance2D.getSqDistance(p, C);
		if ((dA2 < dB2) && (dA2 < dC2)) {
			return A;
		} else if ((dB2 < dA2) && (dB2 < dC2)) {
			return B;
		} else {
			return C;
		}

	}

	// POINT-POLYGON

	public static WB_Point closestPoint2D(final WB_Point p,
			final WB_SimplePolygon2D poly) {
		final List<WB_Triangle2D> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.getDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	public static WB_Point closestPoint2D(final WB_Point p,
			final ArrayList<? extends WB_Triangle2D> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.getDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}

		return closest;
	}

	public static WB_Point closestPointOnPeriphery2D(final WB_Point p,
			final WB_SimplePolygon2D poly) {
		final List<WB_Triangle2D> tris = poly.triangulate();
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.getSqDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			WB_IndexedSegment S;
			for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
				S = new WB_IndexedSegment(j, i, poly.points);
				tmp = closestPoint2D(p, S);
				final double d2 = WB_Distance2D.getSqDistance(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}

		return closest;
	}

	public static WB_Point closestPointOnPeriphery2D(final WB_Point p,
			final WB_SimplePolygon2D poly, final ArrayList<WB_Triangle2D> tris) {
		final int n = tris.size();
		double dmax2 = Double.POSITIVE_INFINITY;
		WB_Point closest = new WB_Point();
		WB_Point tmp;
		WB_Triangle2D T;
		for (int i = 0; i < n; i++) {
			T = tris.get(i);
			tmp = closestPoint2D(p, T);
			final double d2 = WB_Distance2D.getSqDistance(tmp, p);
			if (d2 < dmax2) {
				closest = tmp;
				dmax2 = d2;
			}

		}
		if (WB_Epsilon.isZeroSq(dmax2)) {
			dmax2 = Double.POSITIVE_INFINITY;
			Segment S;
			for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
				S = new WB_IndexedSegment(j, i, poly.points);
				tmp = closestPoint2D(p, S);
				final double d2 = WB_Distance2D.getSqDistance(tmp, p);
				if (d2 < dmax2) {
					closest = tmp;
					dmax2 = d2;
				}

			}

		}
		return closest;
	}

}
