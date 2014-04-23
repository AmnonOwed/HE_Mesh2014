package wblut.geom;

import java.util.List;

import javolution.util.FastList;
import wblut.WB_Epsilon;

public class WB_SimplePolygon implements SimplePolygon {

	/** Ordered array of WB_Point. */
	private WB_Point[] points;

	/** Number of points. */
	public int n;

	/** Stored plane of polygon. */
	private WB_Plane P;

	/** Status of stored plane. */
	private boolean updated;

	/**
	 * Instantiates a new WB_Polygon.
	 */
	public WB_SimplePolygon() {
		points = new WB_Point[0];
		n = 0;
		updated = false;
	}

	/**
	 * Instantiates a new WB_Polygon.
	 * 
	 * @param points
	 *            array of WB_Point, no copies are made
	 * @param n
	 *            number of points
	 */
	public WB_SimplePolygon(final WB_Point[] points, final int n) {
		this.points = points;
		this.n = n;
		P = getPlane();
		updated = true;
	}

	/**
	 * Instantiates a new WB_Polygon.
	 * 
	 * @param points
	 *            array of WB_Point
	 * @param n
	 *            number of points
	 * @param copy
	 *            copy points?
	 */
	public WB_SimplePolygon(final WB_Point[] points, final int n,
			final boolean copy) {
		if (copy == false) {
			this.points = points;
		} else {
			this.points = new WB_Point[n];
			for (int i = 0; i < n; i++) {
				this.points[i] = points[i].get();
			}

		}
		this.n = n;
		P = getPlane();
	}

	/**
	 * Instantiates a new WB_Polygon.
	 * 
	 * @param points
	 *            arrayList of WB_Point
	 */
	public WB_SimplePolygon(final List<WB_Point> points) {
		n = points.size();
		this.points = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}

		P = getPlane();
	}

	/**
	 * Set polygon.
	 * 
	 * @param points
	 *            array of WB_Point, no copies are made
	 * @param n
	 *            number of points
	 */
	public void set(final WB_Point[] points, final int n) {
		this.points = points;
		this.n = n;
		P = getPlane();
	}

	/**
	 * Set polygon.
	 * 
	 * @param poly
	 *            source polygon, no copies are made
	 */
	public void set(final SimplePolygon poly) {
		points = poly.getPoints();
		n = poly.getN();
		P = getPlane();
	}

	/**
	 * Set polygon.
	 * 
	 * @param points
	 *            arrayList of WB_Point, no copies are made
	 * @param n
	 *            number of points
	 */
	public void set(final FastList<WB_Point> points, final int n) {
		this.points = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
		this.n = n;
		P = getPlane();
	}

	/**
	 * Get deep copy.
	 * 
	 * @return copy
	 */
	public WB_SimplePolygon get() {
		final WB_Point[] newPoints = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			newPoints[i] = points[i].get();
		}
		return new WB_SimplePolygon(newPoints, n);

	}

	/**
	 * Get shallow copy.
	 * 
	 * @return copy
	 */
	public WB_SimplePolygon getNoCopy() {
		return new WB_SimplePolygon(points, n);

	}

	/**
	 * Closest point on polygon to given point.
	 * 
	 * @param p
	 *            point
	 * @return closest point of polygon
	 */
	public WB_Point closestPoint(final WB_Point p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance3D.sqDistance(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return points[id];
	}

	/**
	 * Index of closest point on polygon to given point.
	 * 
	 * @param p
	 *            point
	 * @return index of closest point of polygon
	 */
	public int closestIndex(final WB_Point p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance3D.sqDistance(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return id;
	}

	/**
	 * Plane of polygon.
	 * 
	 * @return plane
	 */
	public WB_Plane getPlane() {
		if (updated) {
			return P;
		}
		final WB_Vector normal = new WB_Vector();
		final WB_Point center = new WB_Point();
		WB_Point p0;
		WB_Point p1;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {

			p0 = points[j];
			p1 = points[i];
			normal.x += (p0.y - p1.y) * (p0.z + p1.z);
			normal.y += (p0.z - p1.z) * (p0.x + p1.x);
			normal.z += (p0.x - p1.x) * (p0.y + p1.y);
			center._addSelf(p1);
		}
		normal._normalizeSelf();
		center._divSelf(n);
		P = new WB_Plane(center, normal);
		updated = true;
		return P;

	}

	/**
	 * Checks if point at index is convex.
	 * 
	 * @param i
	 *            index
	 * @return WB.VertexType.FLAT,WB.VertexType.CONVEX,WB.VertexType.CONCAVE
	 */
	public WB_Convex isConvex(final int i) {

		final WB_Vector vp = points[(i == 0) ? n - 1 : i - 1]
				.subToVector(points[i]);
		vp._normalizeSelf();
		final WB_Vector vn = points[(i == n - 1) ? 0 : i + 1]
				.subToVector(points[i]);
		vn._normalizeSelf();

		final double cross = vp.cross(vn).getSqLength();

		if (WB_Epsilon.isZeroSq(cross)) {
			return WB_Convex.FLAT;
		} else if (Math.acos(vp.dot(vn)) < Math.PI) {
			return WB_Convex.CONVEX;
		} else {
			return WB_Convex.CONCAVE;
		}
	}

	/**
	 * Triangulate polygon.
	 * 
	 * @return arrayList of WB_IndexedTriangle, points are not copied
	 */
	public List<WB_IndexedTriangle> triangulate() {
		final List<WB_IndexedTriangle> tris = new FastList<WB_IndexedTriangle>();
		final WB_SimplePolygon2D tmp = toPolygon2D();
		final List<WB_IndexedTriangle2D> tris2d = tmp.indexedTriangulate();
		WB_IndexedTriangle2D tri2d;
		for (int i = 0; i < tris2d.size(); i++) {
			tri2d = tris2d.get(i);
			tris.add(new WB_IndexedTriangle(tri2d.i1, tri2d.i2, tri2d.i3,
					points));

		}
		return tris;
	}

	/**
	 * Removes point.
	 * 
	 * @param i
	 *            index of point to remove
	 * @return new WB_Polygon with point removed
	 */
	public WB_SimplePolygon removePoint(final int i) {
		final WB_Point[] newPoints = new WB_Point[n - 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		for (int j = i; j < n - 1; j++) {
			newPoints[j] = points[j + 1];
		}
		return new WB_SimplePolygon(newPoints, n - 1);

	}

	/**
	 * Removes the point self.
	 * 
	 * @param i
	 *            the i
	 */
	public void removePointSelf(final int i) {
		final WB_Point[] newPoints = new WB_Point[n - 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		for (int j = i; j < n - 1; j++) {
			newPoints[j] = points[j + 1];
		}
		set(newPoints, n - 1);

	}

	/**
	 * Adds point.
	 * 
	 * @param i
	 *            index to put point
	 * @param p
	 *            point
	 * @return new WB_Polygon with point added
	 */
	public WB_SimplePolygon addPoint(final int i, final WB_Point p) {
		final WB_Point[] newPoints = new WB_Point[n + 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		newPoints[i] = p;
		for (int j = i + 1; j < n + 1; j++) {
			newPoints[j] = points[j - 1];
		}
		return new WB_SimplePolygon(newPoints, n + 1);

	}

	/**
	 * Adds the point self.
	 * 
	 * @param i
	 *            the i
	 * @param p
	 *            the p
	 */
	public void addPointSelf(final int i, final WB_Point p) {
		final WB_Point[] newPoints = new WB_Point[n + 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		newPoints[i] = p;
		for (int j = i + 1; j < n + 1; j++) {
			newPoints[j] = points[j - 1];
		}
		set(newPoints, n + 1);

	}

	/**
	 * Refine polygon and smooth with simple Laplacian filter.
	 * 
	 * @return new refined WB_Polygon
	 */
	public WB_SimplePolygon smooth() {
		final WB_Point[] newPoints = new WB_Point[2 * n];

		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			newPoints[2 * i] = points[j].add(points[i]);
			newPoints[2 * i]._mulSelf(0.5);
			newPoints[2 * i + 1] = points[i].get();
		}
		final WB_Point[] sPoints = new WB_Point[2 * n];
		for (int i = 0, j = 2 * n - 1; i < 2 * n; j = i, i++) {
			int k = i + 1;
			if (k == 2 * n) {
				k = 0;
			}
			sPoints[i] = newPoints[j].add(newPoints[k]);
			sPoints[i]._mulSelf(0.5);
		}

		return new WB_SimplePolygon(sPoints, 2 * n);

	}

	/**
	 * Trim convex polygon.
	 * 
	 * @param poly
	 *            the poly
	 * @param d
	 *            the d
	 */
	public static void trimConvexPolygon(final WB_SimplePolygon poly,
			final double d) {
		final WB_SimplePolygon cpoly = poly.get();
		final int n = cpoly.n; // get number of vertices
		final WB_Plane P = cpoly.getPlane(); // get plane of poly
		// iterate over n-1 edges
		final WB_SimplePolygon frontPoly = new WB_SimplePolygon();// needed
		// by
		// splitPolygon
		// to store one half
		final WB_SimplePolygon backPoly = new WB_SimplePolygon();// needed
		// by
		// splitPolygon
		// to store other half
		WB_Point p1, p2, origin;
		WB_Vector v, normal;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			p1 = cpoly.points[i];// startpoint of edge
			p2 = cpoly.points[j];// endpoint of edge
			// vector along edge
			v = p2.subToVector(p1);
			v._normalizeSelf();
			// edge normal is perpendicular to edge and plane normal
			normal = v.cross(P.getNormal());
			// center of edge
			origin = p1.add(p2)._mulSelf(0.5);
			// offset cutting plane origin by the desired distance d
			origin._addSelf(d * normal.x, d * normal.y, d * normal.z);

			splitPolygonInto(poly, new WB_Plane(origin, normal), frontPoly,
					backPoly);
			poly.set(frontPoly);

		}
	}

	/**
	 * Trim convex polygon.
	 * 
	 * @param d
	 *            the d
	 */
	public void trimConvexPolygon(final double d) {
		trimConvexPolygon(this, d);
	}

	/**
	 * Trim convex polygon.
	 * 
	 * @param poly
	 *            the poly
	 * @param d
	 *            the d
	 */
	public static void trimConvexPolygon(final WB_SimplePolygon poly,
			final double[] d) {

		final WB_Plane P = poly.getPlane(); // get plane of poly
		// iterate over n-1 edges
		final WB_SimplePolygon frontPoly = new WB_SimplePolygon();// needed
		// by
		// splitPolygon
		// to store one half
		final WB_SimplePolygon backPoly = new WB_SimplePolygon();// needed
		// by
		// splitPolygon
		// to store other half
		WB_Point p1, p2, origin;
		WB_Vector v, normal;
		for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
			p1 = poly.points[i];// startpoint of edge
			p2 = poly.points[j];// endpoint of edge
			// vector along edge
			v = p2.subToVector(p1);
			v._normalizeSelf();
			// edge normal is perpendicular to edge and plane normal
			normal = v.cross(P.getNormal());
			// center of edge
			origin = p1.add(p2)._mulSelf(0.5);
			// offset cutting plane origin by the desired distance d
			origin._addSelf(d[j] * normal.x, d[j] * normal.y, d[j] * normal.z);

			splitPolygonInto(poly, new WB_Plane(origin, normal), frontPoly,
					backPoly);
			poly.set(frontPoly);

		}
	}

	/**
	 * Trim convex polygon.
	 * 
	 * @param d
	 *            the d
	 */
	public void trimConvexPolygon(final double[] d) {
		trimConvexPolygon(this, d);
	}

	/**
	 * Split polygon into pre test.
	 * 
	 * @param poly
	 *            the poly
	 * @param P
	 *            the p
	 * @param frontPoly
	 *            the front poly
	 * @param backPoly
	 *            the back poly
	 */
	public static void splitPolygonIntoPreTest(final WB_SimplePolygon poly,
			final WB_Plane P, final WB_SimplePolygon frontPoly,
			final WB_SimplePolygon backPoly) {
		int numFront = 0;
		int numBack = 0;

		final WB_AABB AABB = new WB_AABB(poly.points);
		if (WB_Intersection.checkIntersection(AABB, P)) {

			final FastList<WB_Point> frontVerts = new FastList<WB_Point>(20);
			final FastList<WB_Point> backVerts = new FastList<WB_Point>(20);

			final int numVerts = poly.n;
			WB_Point a = poly.points[numVerts - 1];
			WB_Classification aSide = P.classifyPointToPlane(a);
			WB_Point b;
			WB_Classification bSide;

			for (int n = 0; n < numVerts; n++) {
				WB_IntersectionResult i;
				b = poly.points[n];
				bSide = P.classifyPointToPlane(b);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = WB_Intersection.getIntersection(b, a, P);

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.ON) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */

						frontVerts.add((WB_Point) i.object);
						numFront++;
						backVerts.add((WB_Point) i.object);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = WB_Intersection.getIntersection(a, b, P);

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.ON) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */

						frontVerts.add((WB_Point) i.object);
						numFront++;
						backVerts.add((WB_Point) i.object);
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
			frontPoly.set(frontVerts, numFront);
			backPoly.set(backVerts, numBack);
		} else {
			int c = 0;
			WB_Point a = poly.points[c];
			WB_Classification aSide = P.classifyPointToPlane(a);

			if (aSide == WB_Classification.FRONT) {
				frontPoly.set(poly.get());
				backPoly.set(new WB_SimplePolygon());

			} else if (aSide == WB_Classification.BACK) {
				backPoly.set(poly.get());
				frontPoly.set(new WB_SimplePolygon());
			} else {
				c++;
				do {
					a = poly.points[c];
					aSide = P.classifyPointToPlane(a);
					c++;
				} while (aSide == WB_Classification.ON && c < poly.n);
				if (aSide == WB_Classification.BACK) {
					backPoly.set(poly.get());
					frontPoly.set(new WB_SimplePolygon());
				} else {
					frontPoly.set(poly.get());
					backPoly.set(new WB_SimplePolygon());

				}

			}

		}

	}

	/**
	 * Split polygon into.
	 * 
	 * @param poly
	 *            the poly
	 * @param P
	 *            the p
	 * @param frontPoly
	 *            the front poly
	 * @param backPoly
	 *            the back poly
	 */
	public static void splitPolygonInto(final WB_SimplePolygon poly,
			final WB_Plane P, final WB_SimplePolygon frontPoly,
			final WB_SimplePolygon backPoly) {
		int numFront = 0;
		int numBack = 0;

		final FastList<WB_Point> frontVerts = new FastList<WB_Point>(20);
		final FastList<WB_Point> backVerts = new FastList<WB_Point>(20);

		final int numVerts = poly.n;
		if (numVerts > 0) {
			WB_Point a = poly.points[numVerts - 1];
			WB_Classification aSide = P.classifyPointToPlane(a);
			WB_Point b;
			WB_Classification bSide;

			for (int n = 0; n < numVerts; n++) {
				final WB_IntersectionResult i;
				b = poly.points[n];
				bSide = P.classifyPointToPlane(b);
				if (bSide == WB_Classification.FRONT) {
					if (aSide == WB_Classification.BACK) {
						i = WB_Intersection.getIntersection(b, a, P);

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.ON) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */

						frontVerts.add((WB_Point) i.object);
						numFront++;
						backVerts.add((WB_Point) i.object);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = WB_Intersection.getIntersection(a, b, P);

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.ON) { System.out
						 * .println("Inconsistency: intersection not on plane");
						 * }
						 */

						frontVerts.add((WB_Point) i.object);
						numFront++;
						backVerts.add((WB_Point) i.object);
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
			frontPoly.set(frontVerts, numFront);
			backPoly.set(backVerts, numBack);
		}

	}

	/**
	 * Split polygon into.
	 * 
	 * @param P
	 *            the p
	 * @param frontPoly
	 *            the front poly
	 * @param backPoly
	 *            the back poly
	 */
	public void splitPolygonInto(final WB_Plane P,
			final WB_SimplePolygon frontPoly, final WB_SimplePolygon backPoly) {
		splitPolygonInto(get(), P, frontPoly, backPoly);

	}

	/**
	 * Split polygon into pre test.
	 * 
	 * @param P
	 *            the p
	 * @param frontPoly
	 *            the front poly
	 * @param backPoly
	 *            the back poly
	 */
	public void splitPolygonIntoPreTest(final WB_Plane P,
			final WB_SimplePolygon frontPoly, final WB_SimplePolygon backPoly) {
		splitPolygonIntoPreTest(get(), P, frontPoly, backPoly);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getSegments()
	 */
	public FastList<WB_IndexedSegment> getSegments() {
		final FastList<WB_IndexedSegment> segments = new FastList<WB_IndexedSegment>(
				n);
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_IndexedSegment(i, j, points));

		}
		return segments;
	}

	/**
	 * Negate.
	 * 
	 * @return the w b_ explicit polygon
	 */
	public WB_SimplePolygon negate() {
		final WB_Point[] negPoints = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			negPoints[i] = points[n - 1 - i];
		}
		return new WB_SimplePolygon(negPoints, n);

	}

	/**
	 * Negate.
	 * 
	 * @param polys
	 *            the polys
	 * @return the list
	 */
	public static List<WB_SimplePolygon> negate(
			final List<WB_SimplePolygon> polys) {
		final List<WB_SimplePolygon> neg = new FastList<WB_SimplePolygon>();
		for (int i = 0; i < polys.size(); i++) {
			neg.add(polys.get(i).negate());
		}
		return neg;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#toPolygon2D()
	 */
	public WB_SimplePolygon2D toPolygon2D() {
		final WB_Point[] lpoints = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			lpoints[i] = P.localPoint2D(points[i]);
		}
		return new WB_SimplePolygon2D(lpoints, n);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getN()
	 */
	public int getN() {
		return n;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getPoint(int)
	 */
	public WB_Point getPoint(final int i) {
		return points[i];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getIndex(int)
	 */
	public int getIndex(final int i) {
		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getPoints()
	 */
	public WB_Point[] getPoints() {

		return points;
	}

}