package wblut.geom;

import java.util.List;

import javolution.util.FastList;
import wblut.WB_Epsilon;
import wblut.geom.WB_KDTree.WB_KDEntry;

public class WB_IndexedPolygon implements SimplePolygon {

	private WB_Point[] allpoints;

	private int[] indices;

	public int n;

	private WB_Plane P;

	private boolean updated;

	public WB_IndexedPolygon() {
		allpoints = new WB_Point[0];
		indices = new int[0];
		n = 0;
		updated = false;
	}

	public WB_IndexedPolygon(final WB_Point[] points, final int[] indices,
			final int n) {
		allpoints = points;
		this.indices = indices;
		this.n = n;
		P = getPlane();
		updated = true;
	}

	public void set(final WB_Point[] points, final int[] indices, final int n) {
		allpoints = points;
		this.indices = indices;
		this.n = n;
		P = getPlane();
		updated = true;
	}

	public void set(final SimplePolygon poly) {
		allpoints = new WB_Point[n];
		indices = new int[n];
		for (int i = 0; i < n; i++) {
			allpoints[i] = poly.getPoint(i);
			indices[i] = poly.getIndex(i);
		}
		P = getPlane();
	}

	public WB_IndexedPolygon get() {
		final int[] cindices = new int[n];
		for (int i = 0; i < n; i++) {

			cindices[i] = indices[i];
		}
		return new WB_IndexedPolygon(allpoints, cindices, n);

	}

	public WB_Point closestPoint(final WB_Coordinate p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance.getSqDistance3D(p,
					allpoints[indices[i]]);
			if (cd < d) {
				id = indices[i];
				d = cd;
			}
		}
		return allpoints[id];
	}

	public int closestIndex(final WB_Coordinate p) {
		double d = Double.POSITIVE_INFINITY;
		int id = -1;
		for (int i = 0; i < n; i++) {
			final double cd = WB_Distance.getSqDistance3D(p,
					allpoints[indices[i]]);
			if (cd < d) {
				id = indices[i];
				d = cd;
			}
		}
		return id;
	}

	public WB_Plane getPlane() {
		if (updated) {
			return P;
		}
		final WB_Vector normal = new WB_Vector();
		final WB_Point center = new WB_Point();
		WB_Point p0;
		WB_Point p1;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			p0 = allpoints[indices[j]];
			p1 = allpoints[indices[i]];
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

	public WB_Convex isConvex(final int i) {
		final WB_Vector vp = allpoints[(i == 0) ? indices[n - 1]
				: indices[i - 1]].subToVector(allpoints[indices[i]]);
		vp._normalizeSelf();
		final WB_Vector vn = allpoints[(i == n - 1) ? indices[0]
				: indices[i + 1]].subToVector(allpoints[indices[i]]);
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
					allpoints));

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
	public WB_IndexedPolygon removePoint(final int i) {
		final int[] newindices = new int[n - 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		for (int j = i; j < n - 1; j++) {
			newindices[j] = indices[j + 1];
		}
		return new WB_IndexedPolygon(allpoints, newindices, n - 1);

	}

	/**
	 * Removes the point self.
	 * 
	 * @param i
	 *            the i
	 */
	public void removePointSelf(final int i) {
		final int[] newindices = new int[n - 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		for (int j = i; j < n - 1; j++) {
			newindices[j] = indices[j + 1];
		}
		set(allpoints, newindices, n - 1);

	}

	/**
	 * Adds point.
	 * 
	 * @param i
	 *            index to put point
	 * @param id
	 *            the id
	 * @return new WB_Polygon with point added
	 */
	public WB_IndexedPolygon addPoint(final int i, final int id) {
		final int[] newindices = new int[n + 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		newindices[i] = id;
		for (int j = i + 1; j < n + 1; j++) {
			newindices[j] = indices[j - 1];
		}
		return new WB_IndexedPolygon(allpoints, newindices, n + 1);

	}

	/**
	 * Adds the point self.
	 * 
	 * @param i
	 *            the i
	 * @param id
	 *            the id
	 */
	public void addPointSelf(final int i, final int id) {
		final int[] newindices = new int[n + 1];
		for (int j = 0; j < i; j++) {
			newindices[j] = indices[j];
		}
		newindices[i] = id;
		for (int j = i + 1; j < n + 1; j++) {
			newindices[j] = indices[j - 1];
		}
		set(allpoints, newindices, n + 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getSegments()
	 */
	public List<WB_IndexedSegment> getSegments() {
		final List<WB_IndexedSegment> segments = new FastList<WB_IndexedSegment>(
				n);
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			segments.add(new WB_IndexedSegment(i, j, allpoints));

		}
		return segments;
	}

	/**
	 * Extract polygons.
	 * 
	 * @param segs
	 *            the segs
	 * @param points
	 *            the points
	 * @return the list
	 */
	public static List<WB_IndexedPolygon> extractPolygons(
			final List<WB_IndexedSegment> segs, final WB_Point[] points) {
		final List<WB_IndexedPolygon> result = new FastList<WB_IndexedPolygon>();
		final List<WB_IndexedSegment> leftovers = new FastList<WB_IndexedSegment>();
		final List<WB_IndexedSegment> cleanedsegs = clean(segs, points);
		leftovers.addAll(cleanedsegs);
		while (leftovers.size() > 0) {
			final List<WB_IndexedSegment> currentPolygon = new FastList<WB_IndexedSegment>();
			final boolean loopFound = tryToFindLoop(leftovers, currentPolygon,
					points);
			if (loopFound) {
				final int[] indices = new int[currentPolygon.size()];
				for (int i = 0; i < currentPolygon.size(); i++) {
					indices[i] = currentPolygon.get(i).i1();
				}
				if (currentPolygon.size() > 2) {
					final WB_IndexedPolygon poly = new WB_IndexedPolygon(
							points, indices, currentPolygon.size());
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
	 * @param points
	 *            the points
	 * @return the list
	 */
	public static List<WB_IndexedSegment> clean(
			final List<WB_IndexedSegment> segs, final WB_Point[] points) {
		final List<WB_IndexedSegment> cleanedsegs = new FastList<WB_IndexedSegment>();
		final WB_KDTree<WB_Point, Integer> tree = new WB_KDTree<WB_Point, Integer>();
		int i = 0;
		for (i = 0; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(segs.get(i)
					.getOrigin(), segs.get(i).getEndpoint()))) {
				tree.add(segs.get(i).getOrigin(), segs.get(i).i1());
				tree.add(segs.get(i).getEndpoint(), segs.get(i).i2());
				cleanedsegs.add(new WB_IndexedSegment(segs.get(i).i1(), segs
						.get(i).i2(), points));
				break;
			}

		}
		for (; i < segs.size(); i++) {
			if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(segs.get(i)
					.getOrigin(), segs.get(i).getEndpoint()))) {
				WB_Point origin = segs.get(i).getOrigin();
				WB_Point end = segs.get(i).getEndpoint();
				int i1 = segs.get(i).i1();
				int i2 = segs.get(i).i2();
				WB_KDEntry<WB_Point, Integer>[] nn = tree.getNearestNeighbors(
						origin, 1);

				if (WB_Epsilon.isZeroSq(nn[0].d2)) {
					origin = nn[0].coord;
					i1 = nn[0].value;
				} else {
					tree.add(segs.get(i).getOrigin(), segs.get(i).i1());
				}
				nn = tree.getNearestNeighbors(end, 1);
				if (WB_Epsilon.isZeroSq(nn[0].d2)) {
					end = nn[0].coord;
					i2 = nn[0].value;
				} else {
					tree.add(segs.get(i).getEndpoint(), segs.get(i).i2());
				}
				cleanedsegs.add(new WB_IndexedSegment(i1, i2, points));
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
	 * @param points
	 *            the points
	 * @return true, if successful
	 */
	private static boolean tryToFindLoop(final List<WB_IndexedSegment> segs,
			final List<WB_IndexedSegment> loop, final WB_Point[] points) {
		final List<WB_IndexedSegment> localSegs = new FastList<WB_IndexedSegment>();
		localSegs.addAll(segs);
		WB_IndexedSegment start = localSegs.get(0);
		loop.add(localSegs.get(0));
		boolean found = false;
		do {
			found = false;
			for (int i = 0; i < localSegs.size(); i++) {
				if (localSegs.get(i).i1() == start.i2()) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#toPolygon2D()
	 */
	public WB_SimplePolygon2D toPolygon2D() {
		final WB_Point[] lpoints = new WB_Point[n];
		for (int i = 0; i < n; i++) {
			lpoints[i] = P.localPoint2D(getPoint(i));
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
		return allpoints[indices[i]];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getIndex(int)
	 */
	public int getIndex(final int i) {
		return indices[i];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Polygon#getPoints()
	 */
	public WB_Point[] getPoints() {
		return allpoints;
	}

	/**
	 * Gets the indices.
	 * 
	 * @return the indices
	 */
	public int[] getIndices() {
		return indices;
	}

}