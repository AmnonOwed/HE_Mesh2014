package wblut.geom;

import java.util.List;

import javolution.util.FastTable;
import wblut.WB_Epsilon;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.geom.interfaces.Segment;

/**
 * Planar polygon class.
 */
public class WB_SimplePolygon2D {

	/** Ordered array of WB_Point. */
	public WB_Point[] points;

	/** Number of points. */
	public int n;

	/**
	 * Instantiates a new WB_Polygon.
	 */
	public WB_SimplePolygon2D() {
		points = new WB_Point[0];
		n = 0;
	}

	/**
	 * Instantiates a new WB_Polygon.
	 * 
	 * @param points
	 *            array of WB_Point, no copies are made
	 * @param n
	 *            number of points
	 */
	public WB_SimplePolygon2D(final WB_Point[] points, final int n) {
		this.points = points;
		this.n = n;
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
	public WB_SimplePolygon2D(final WB_Point[] points, final int n,
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
	}

	/**
	 * Instantiates a new WB_Polygon2D.
	 * 
	 * @param points
	 *            arrayList of WB_XY
	 */
	public WB_SimplePolygon2D(final List<WB_Point> points) {
		n = points.size();
		this.points = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
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
	}

	/**
	 * Set polygon.
	 * 
	 * @param poly
	 *            source polygon, no copies are made
	 */
	public void set(final WB_SimplePolygon2D poly) {
		points = poly.points;
		n = poly.n;
	}

	/**
	 * Set polygon.
	 * 
	 * @param points
	 *            arrayList of WB_Point, no copies are made
	 * @param n
	 *            number of points
	 */
	public void set(final List<WB_Point> points, final int n) {
		this.points = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			this.points[i] = points.get(i);
		}
		this.n = n;
	}

	/**
	 * Get deep copy.
	 * 
	 * @return copy
	 */
	public WB_SimplePolygon2D get() {
		final WB_Point[] newPoints = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			newPoints[i] = points[i].get();
		}
		return new WB_SimplePolygon2D(newPoints, n);

	}

	/**
	 * Get shallow copy.
	 * 
	 * @return copy
	 */
	public WB_SimplePolygon2D getNoCopy() {
		return new WB_SimplePolygon2D(points, n);

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
			final double cd = WB_Distance.getSqDistance2D(p, points[i]);
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
			final double cd = WB_Distance.getSqDistance2D(p, points[i]);
			if (cd < d) {
				id = i;
				d = cd;
			}
		}
		return id;
	}

	/**
	 * Checks if point at index is convex.
	 * 
	 * @param i
	 *            index
	 * @return WB.VertexType.FLAT,WB.VertexType.CONVEX,WB.VertexType.CONCAVE
	 */
	public WB_Convex isConvex(final int i) {
		final WB_Point vp = points[(i == 0) ? n - 1 : i - 1].sub(points[i]);
		vp._normalizeSelf();
		final WB_Point vn = points[(i == n - 1) ? 0 : i + 1].sub(points[i]);
		vn._normalizeSelf();

		final double cross = vp.x * vn.y - vp.y * vn.x;

		if (WB_Epsilon.isZero(cross)) {
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
	 * @return arrayList of WB_Triangle, points are not copied
	 */
	public List<WB_Triangle2D> triangulate() {
		final WB_Triangulate2D tri = new WB_Triangulate2D();
		tri.startWithBoundary(points);
		return tri.getExplicitTrianglesAsList();
	}

	/**
	 * Triangulate polygon.
	 * 
	 * @return arrayList of WB_IndexedTriangle, points are not copied
	 */
	public List<WB_IndexedTriangle2D> indexedTriangulate() {
		final WB_Triangulate2D tri = new WB_Triangulate2D();
		tri.startWithBoundary(points, true);
		return tri.getIndexedTrianglesAsList(points, true);
	}

	/**
	 * Removes point.
	 * 
	 * @param i
	 *            index of point to remove
	 * @return new WB_Polygon with point removed
	 */
	public WB_SimplePolygon2D removePoint(final int i) {
		final WB_Point[] newPoints = new WB_Point[n - 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		for (int j = i; j < n - 1; j++) {
			newPoints[j] = points[j + 1];
		}
		return new WB_SimplePolygon2D(newPoints, n - 1);

	}

	/**
	 * Remove flat points.
	 * 
	 * @return new WB_Polygon with superfluous points removed
	 */
	public WB_SimplePolygon2D removeFlatPoints() {
		return removeFlatPoints(0);
	}

	/**
	 * Removes the flat points.
	 * 
	 * @param start
	 *            the start
	 * @return the w b_ polygon2 d
	 */
	private WB_SimplePolygon2D removeFlatPoints(final int start) {
		for (int i = start; i < n; i++) {
			if (isConvex(i) == WB_Convex.FLAT) {
				return removePoint(i).removeFlatPoints(i);
			}
		}
		return this;
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
	public WB_SimplePolygon2D addPoint(final int i, final WB_Point p) {
		final WB_Point[] newPoints = new WB_Point[n + 1];
		for (int j = 0; j < i; j++) {
			newPoints[j] = points[j];
		}
		newPoints[i] = p;
		for (int j = i + 1; j < n + 1; j++) {
			newPoints[j] = points[j - 1];
		}
		return new WB_SimplePolygon2D(newPoints, n + 1);

	}

	/**
	 * Refine polygon and smooth with simple Laplacian filter.
	 * 
	 * @return new refined WB_Polygon
	 */
	public WB_SimplePolygon2D smooth() {
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

		return new WB_SimplePolygon2D(sPoints, 2 * n);

	}

	/**
	 * Trim convex polygon.
	 * 
	 * @param poly
	 *            the poly
	 * @param d
	 *            the d
	 */
	public static void trimConvexPolygon(final WB_SimplePolygon2D poly,
			final double d) {
		final WB_SimplePolygon2D cpoly = poly.get();
		final int n = cpoly.n; // get number of vertices
		// iterate over n-1 edges
		final WB_SimplePolygon2D frontPoly = new WB_SimplePolygon2D();// needed
																		// by
		// splitPolygon
		// to store one half
		final WB_SimplePolygon2D backPoly = new WB_SimplePolygon2D();// needed
																		// by
		// splitPolygon
		// to store other half
		WB_Point p1, p2, origin;
		WB_Point v, normal;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			p1 = cpoly.points[i];// startpoint of edge
			p2 = cpoly.points[j];// endpoint of edge
			// vector along edge
			v = p2.sub(p1);
			v._normalizeSelf();
			// edge normal is perpendicular to edge and plane normal
			normal = new WB_Point(v.y, -v.x);
			// center of edge
			origin = p1.add(p2)._mulSelf(0.5);
			// offset cutting plane origin by the desired distance d
			origin._addSelf(d * normal.x, d * normal.y, 0);

			splitPolygonInto(poly, new WB_Line2D(origin, v), frontPoly,
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
	public static void trimConvexPolygon(final WB_SimplePolygon2D poly,
			final double[] d) {

		// iterate over n-1 edges
		final WB_SimplePolygon2D frontPoly = new WB_SimplePolygon2D();// needed
																		// by
		// splitPolygon
		// to store one half
		final WB_SimplePolygon2D backPoly = new WB_SimplePolygon2D();// needed
																		// by
		// splitPolygon
		// to store other half
		WB_Point p1, p2, origin;
		WB_Point v, normal;
		for (int i = 0, j = poly.n - 1; i < poly.n; j = i, i++) {
			p1 = poly.points[i];// startpoint of edge
			p2 = poly.points[j];// endpoint of edge
			// vector along edge
			v = p2.sub(p1);
			v._normalizeSelf();
			// edge normal is perpendicular to edge and plane normal
			normal = new WB_Point(v.y, -v.x);
			// center of edge
			origin = p1.add(p2)._mulSelf(0.5);
			// offset cutting plane origin by the desired distance d
			origin._addSelf(d[i] * normal.x, d[i] * normal.y, 0);

			splitPolygonInto(poly, new WB_Line2D(origin, v), frontPoly,
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
	 * Split polygon into.
	 * 
	 * @param poly
	 *            the poly
	 * @param L
	 *            split line
	 * @param frontPoly
	 *            front subpoly
	 * @param backPoly
	 *            back subpoly
	 */
	public static void splitPolygonInto(final WB_SimplePolygon2D poly,
			final WB_Line2D L, final WB_SimplePolygon2D frontPoly,
			final WB_SimplePolygon2D backPoly) {
		int numFront = 0;
		int numBack = 0;

		final FastTable<WB_Point> frontVerts = new FastTable<WB_Point>();
		final FastTable<WB_Point> backVerts = new FastTable<WB_Point>();

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
						i = WB_Intersection.getClosestPoint2D(L,
								new WB_Segment(a, b));
						frontVerts.add((WB_Point) i.object);
						numFront++;
						backVerts.add((WB_Point) i.object);
						numBack++;
					}
					frontVerts.add(b);
					numFront++;
				} else if (bSide == WB_Classification.BACK) {
					if (aSide == WB_Classification.FRONT) {
						i = WB_Intersection.getClosestPoint2D(L,
								new WB_Segment(a, b));

						/*
						 * if (classifyPointToPlane(i.p1, P) !=
						 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
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
	 * @param L
	 *            the l
	 * @param frontPoly
	 *            the front poly
	 * @param backPoly
	 *            the back poly
	 */
	public void splitPolygonInto(final WB_Line2D L,
			final WB_SimplePolygon2D frontPoly,
			final WB_SimplePolygon2D backPoly) {
		splitPolygonInto(get(), L, frontPoly, backPoly);

	}

	/**
	 * To segments.
	 * 
	 * @return the list
	 */
	public List<WB_IndexedSegment> toSegments() {
		final List<WB_IndexedSegment> segments = new FastTable<WB_IndexedSegment>();
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_IndexedSegment(j, i, points));

		}
		return segments;
	}

	/**
	 * To explicit segments.
	 * 
	 * @return the list
	 */
	public List<WB_Segment> toExplicitSegments() {
		final List<WB_Segment> segments = new FastTable<WB_Segment>();
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_Segment(points[j], points[i]));

		}
		return segments;
	}

	/**
	 * Negate.
	 * 
	 * @return the w b_ polygon2 d
	 */
	public WB_SimplePolygon2D negate() {
		final WB_Point[] negPoints = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			negPoints[i] = points[n - 1 - i];
		}
		return new WB_SimplePolygon2D(negPoints, n);

	}

	/**
	 * Negate.
	 * 
	 * @param polys
	 *            the polys
	 * @return the list
	 */
	public static List<WB_SimplePolygon2D> negate(
			final List<WB_SimplePolygon2D> polys) {
		final List<WB_SimplePolygon2D> neg = new FastTable<WB_SimplePolygon2D>();
		for (int i = 0; i < polys.size(); i++) {
			neg.add(polys.get(i).negate());
		}
		return neg;

	}

	/**
	 * Intersection seg.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_Segment> intersectionSeg(final WB_SimplePolygon2D P,
			final WB_SimplePolygon2D Q) {
		final FastTable<WB_Segment> pos = new FastTable<WB_Segment>();
		final FastTable<WB_Segment> neg = new FastTable<WB_Segment>();
		final FastTable<WB_Segment> coSame = new FastTable<WB_Segment>();
		final FastTable<WB_Segment> coDiff = new FastTable<WB_Segment>();
		final FastTable<WB_Segment> intersect = new FastTable<WB_Segment>();
		final WB_BSPTree2D tree = new WB_BSPTree2D();
		tree.build(P);
		for (int i = 0, j = Q.n - 1; i < Q.n; j = i, i++) {
			pos.clear();
			neg.clear();
			coSame.clear();
			coDiff.clear();
			final WB_Segment S = new WB_Segment(Q.points[j], Q.points[i]);
			tree.partitionSegment(S, pos, neg, coSame, coDiff);
			intersect.addAll(pos);
			intersect.addAll(coSame);

		}
		tree.build(Q);
		for (int i = 0, j = P.n - 1; i < P.n; j = i, i++) {
			pos.clear();
			neg.clear();
			coSame.clear();
			coDiff.clear();
			final WB_Segment S = new WB_Segment(P.points[j], P.points[i]);
			tree.partitionSegment(S, pos, neg, coSame, coDiff);
			intersect.addAll(pos);
			intersect.addAll(coSame);

		}

		return intersect;

	}

	/**
	 * Intersection.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_SimplePolygon2D> intersection(
			final WB_SimplePolygon2D P, final WB_SimplePolygon2D Q) {
		return extractPolygons(intersectionSeg(P, Q));
	}

	/**
	 * Union seg.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_Segment> unionSeg(final WB_SimplePolygon2D P,
			final WB_SimplePolygon2D Q) {
		final WB_SimplePolygon2D nP = P.negate();
		final WB_SimplePolygon2D nQ = Q.negate();
		return WB_Segment.negate(intersectionSeg(nP, nQ));
	}

	/**
	 * Union.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_SimplePolygon2D> union(final WB_SimplePolygon2D P,
			final WB_SimplePolygon2D Q) {
		return extractPolygons(unionSeg(P, Q));
	}

	/**
	 * Subtract seg.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_Segment> subtractSeg(final WB_SimplePolygon2D P,
			final WB_SimplePolygon2D Q) {
		final WB_SimplePolygon2D nQ = Q.negate();
		return intersectionSeg(P, nQ);
	}

	/**
	 * Subtract.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_SimplePolygon2D> subtract(final WB_SimplePolygon2D P,
			final WB_SimplePolygon2D Q) {
		return extractPolygons(subtractSeg(P, Q));
	}

	/**
	 * Exclusive or.
	 * 
	 * @param P
	 *            the p
	 * @param Q
	 *            the q
	 * @return the list
	 */
	public static List<WB_SimplePolygon2D> exclusiveOr(
			final WB_SimplePolygon2D P, final WB_SimplePolygon2D Q) {
		final List<WB_SimplePolygon2D> tmp = subtract(P, Q);
		tmp.addAll(subtract(Q, P));
		return tmp;
	}

	/**
	 * Extract polygons.
	 * 
	 * @param segs
	 *            the segs
	 * @return the list
	 */
	public static List<WB_SimplePolygon2D> extractPolygons(
			final List<WB_Segment> segs) {
		final List<WB_SimplePolygon2D> result = new FastTable<WB_SimplePolygon2D>();
		final List<WB_Segment> leftovers = new FastTable<WB_Segment>();
		final List<WB_Segment> cleanedsegs = clean(segs);
		leftovers.addAll(cleanedsegs);
		while (leftovers.size() > 0) {
			final FastTable<WB_Segment> currentPolygon = new FastTable<WB_Segment>();
			final boolean loopFound = tryToFindLoop(leftovers, currentPolygon);
			if (loopFound) {
				final FastTable<WB_Point> points = new FastTable<WB_Point>();
				for (int i = 0; i < currentPolygon.size(); i++) {
					points.add(currentPolygon.get(i).getOrigin());

				}
				if (points.size() > 2) {
					WB_SimplePolygon2D poly = new WB_SimplePolygon2D(points);
					poly = poly.removeFlatPoints();
					result.add(poly);
				}
			}
			leftovers.removeAll(currentPolygon);
		}
		return result;
	}

	/**
	 * Clean.
	 * 
	 * @param segs
	 *            the segs
	 * @return the list
	 */
	private static List<WB_Segment> clean(final List<WB_Segment> segs) {
		final List<WB_Segment> cleanedsegs = new FastTable<WB_Segment>();
		final WB_KDTree<WB_Point, Integer> tree = new WB_KDTree<WB_Point, Integer>();
		int i = 0;
		for (i = 0; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance2D(segs.get(i)
					.getOrigin(), segs.get(i).getEndpoint()))) {
				tree.add(segs.get(i).getOrigin(), 2 * i);
				tree.add(segs.get(i).getEndpoint(), 2 * i + 1);
				cleanedsegs.add(new WB_Segment(segs.get(i).getOrigin(), segs
						.get(i).getEndpoint()));
				break;
			}

		}
		for (; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance2D(segs.get(i)
					.getOrigin(), segs.get(i).getEndpoint()))) {
				WB_Point origin = segs.get(i).getOrigin();
				WB_Point end = segs.get(i).getEndpoint();

				WB_KDEntry<WB_Point, Integer>[] nn = tree.getNearestNeighbors(
						origin, 1);

				if (WB_Epsilon.isZeroSq(nn[0].d2)) {
					origin = nn[0].coord;
				} else {
					tree.add(segs.get(i).getOrigin(), 2 * i);
				}
				nn = tree.getNearestNeighbors(end, 1);
				if (WB_Epsilon.isZeroSq(nn[0].d2)) {
					end = nn[0].coord;
				} else {
					tree.add(segs.get(i).getEndpoint(), 2 * i + 1);
				}
				cleanedsegs.add(new WB_Segment(origin, end));
			}

		}
		return cleanedsegs;
	}

	/**
	 * Try to find loop.
	 * 
	 * @param segs
	 *            the segs
	 * @param loop
	 *            the loop
	 * @return true, if successful
	 */
	private static boolean tryToFindLoop(final List<WB_Segment> segs,
			final List<WB_Segment> loop) {
		final List<WB_Segment> localSegs = new FastTable<WB_Segment>();
		localSegs.addAll(segs);
		Segment start = localSegs.get(0);
		loop.add(localSegs.get(0));
		boolean found = false;
		do {
			found = false;
			for (int i = 0; i < localSegs.size(); i++) {
				if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistance2D(localSegs
						.get(i).getOrigin(), start.getEndpoint()))) {
					// if (localSegs.get(i).origin() == start.end()) {
					start = localSegs.get(i);
					loop.add(localSegs.get(i));
					found = true;
					break;
				}
			}
			if (found) {
				localSegs.remove(start);
			}

		} while ((start != segs.get(0)) && found);
		if ((loop.size() > 0) && (start == segs.get(0))) {
			return true;
		}
		return false;
	}

	/**
	 * To polygon.
	 * 
	 * @return the w b_ explicit polygon
	 */
	public WB_SimplePolygon toPolygon() {
		final WB_Point[] points3D = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			points3D[i] = new WB_Point(points[i].x, points[i].y, 0);
		}
		return new WB_SimplePolygon(points3D, n);

	}

}