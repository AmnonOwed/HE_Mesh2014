/**

 * Copyright John E. Lloyd, 2004. All rights reserved. Permission to use,
 * copy, modify and redistribute is granted, provided that this copyright
 * notice is retained and the author is given credit whenever appropriate.
 *
 * This  software is distributed "as is", without any warranty, including 
 * any implied warranty of merchantability or fitness for a particular
 * use. The author assumes no responsibility for, and shall not be liable
 * for, any special, indirect, or consequential damages, or any damages
 * whatsoever, arising out of or in connection with the use of this
 * software.
 */

package wblut.geom;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javolution.util.FastList;
import wblut.WB_Epsilon;

/**
 * Computes the convex hull of a set of three dimensional points.
 * 
 * <p>
 * The algorithm is a three dimensional implementation of Quickhull, as
 * described in Barber, Dobkin, and Huhdanpaa, <a
 * href=http://citeseer.ist.psu.edu/barber96quickhull.html> ``The Quickhull
 * Algorithm for Convex Hulls''</a> (ACM Transactions on Mathematical Software,
 * Vol. 22, No. 4, December 1996), and has a complexity of O(n log(n)) with
 * respect to the number of points. A well-known C implementation of Quickhull
 * that works for arbitrary dimensions is provided by <a
 * href=http://www.qhull.org>qhull</a>.
 * 
 * <p>
 * A hull is constructed by providing a set of points to either a constructor or
 * a build method. After the hull is built, its vertices and faces can be
 * retrieved using getVertices and getFaces. A typical usage might look like
 * this:
 * 
 * <pre>
 * // x y z coordinates of 6 points
 * Point3d[] points = new Point3d[] { new Point3d(0.0, 0.0, 0.0),
 * 		new Point3d(1.0, 0.5, 0.0), new Point3d(2.0, 0.0, 0.0),
 * 		new Point3d(0.5, 0.5, 0.5), new Point3d(0.0, 0.0, 2.0),
 * 		new Point3d(0.1, 0.2, 0.3), new Point3d(0.0, 2.0, 0.0), };
 * 
 * QuickHull3D hull = new QuickHull3D();
 * hull.build(points);
 * 
 * System.out.println(&quot;Vertices:&quot;);
 * Point3d[] vertices = hull.getVertices();
 * for (int i = 0; i &lt; vertices.length; i++) {
 * 	Point3d pnt = vertices[i];
 * 	System.out.println(pnt.x + &quot; &quot; + pnt.y + &quot; &quot; + pnt.z);
 * }
 * 
 * System.out.println(&quot;Faces:&quot;);
 * int[][] faceIndices = hull.getFaces();
 * for (int i = 0; i &lt; faceIndices.length; i++) {
 * 	for (int k = 0; k &lt; faceIndices[i].length; k++) {
 * 		System.out.print(faceIndices[i][k] + &quot; &quot;);
 * 	}
 * 	System.out.println(&quot;&quot;);
 * }
 * </pre>
 * 
 * As a convenience, there are also build and getVertex methods which pass point
 * information using an array of doubles.
 * 
 * <h3><a name=distTol>Robustness</h3> Because this algorithm uses floating
 * point arithmetic, it is potentially vulnerable to errors arising from
 * numerical imprecision. We address this problem in the same way as <a
 * href=http://www.qhull.org>qhull</a>, by merging faces whose edges are not
 * clearly convex. A face is convex if its edges are convex, and an edge is
 * convex if the centroid of each adjacent plane is clearly <i>below</i> the
 * plane of the other face. The centroid is considered below a plane if its
 * distance to the plane is less than the negative of a distance tolerance. This
 * tolerance represents the smallest distance that can be reliably computed
 * within the available numeric precision. It is normally computed automatically
 * from the point data, although an application may set this tolerance
 * explicitly.
 * 
 * <p>
 * Numerical problems are more likely to arise in situations where data points
 * lie on or within the faces or edges of the convex hull. We have tested
 * QuickHull3D for such situations by computing the convex hull of a random
 * point set, then adding additional randomly chosen points which lie very close
 * to the hull vertices and edges, and computing the convex hull again. The hull
 * is deemed correct if check returns <code>true</code>. These tests have been
 * successful for a large number of trials and so we are confident that
 * QuickHull3D is reasonably robust.
 * 
 * <h3>Merged Faces</h3> The merging of faces means that the faces returned by
 * QuickHull3D may be convex polygons instead of triangles. If triangles are
 * desired, the application may triangulate the faces, but it should be noted
 * that this may result in triangles which are very small or thin and hence
 * difficult to perform reliable convexity tests on. In other words,
 * triangulating a merged face is likely to restore the numerical problems which
 * the merging process removed. Hence is it possible that, after triangulation,
 * check will fail (the same behavior is observed with triangulated output from
 * <a href=http://www.qhull.org>qhull</a>).
 * 
 * <h3>Degenerate Input</h3>It is assumed that the input points are
 * non-degenerate in that they are not coincident, colinear, or coplanar, and
 * thus the convex hull has a non-zero volume. If the input points are detected
 * to be degenerate within the distance tolerance, an IllegalArgumentException
 * will be thrown.
 * 
 * author John E. Lloyd, Fall 2004
 * 
 * 
 * Conversion to hemesh datatypes, Frederik Vanhoutte, 2013
 * 
 * 
 */
public class WB_QuickHull3D {

	// estimated size of the point set
	protected double charLength;
	protected List<WB_Point> points;
	protected Vertex[] pointBuffer = new Vertex[0];
	protected int[] vertexPointIndices = new int[0];
	private final Face[] discardedFaces = new Face[3];
	private final Vertex[] maxVtxs = new Vertex[3];
	private final Vertex[] minVtxs = new Vertex[3];

	protected Vector faces = new Vector(16);
	protected Vector horizon = new Vector(16);

	private final FastList<Face> newFaces = new FastList<Face>();
	private final VertexList unclaimed = new VertexList();
	private final VertexList claimed = new VertexList();

	private int numVertices;
	private int numFaces;
	private int numPoints;

	private double tolerance;

	/**
	 * Creates a convex hull object and initializes it to the convex hull of a
	 * set of points.
	 * 
	 * @param points
	 *            input points.
	 * @throws IllegalArgumentException
	 *             the number of input points is less than four, or the points
	 *             appear to be coincident, colinear, or coplanar.
	 */
	public WB_QuickHull3D(final Collection<? extends WB_Coordinate> points)
			throws IllegalArgumentException {
		this.points = new FastList<WB_Point>(points.size());
		for (final WB_Coordinate point : points) {
			this.points.add(new WB_Point(point));

		}
		build(false);
	}

	public WB_QuickHull3D(final Collection<? extends WB_Coordinate> points,
			final boolean triangulate) throws IllegalArgumentException {
		this.points = new FastList<WB_Point>(points.size());
		for (final WB_Coordinate point : points) {
			this.points.add(new WB_Point(point));

		}
		build(triangulate);
	}

	public WB_QuickHull3D(final WB_Coordinate[] points)
			throws IllegalArgumentException {

		this.points = new FastList<WB_Point>(points.length);
		for (final WB_Coordinate point : points) {
			this.points.add(new WB_Point(point));

		}
		build(false);
	}

	public WB_QuickHull3D(final WB_Coordinate[] points,
			final boolean triangulate) throws IllegalArgumentException {
		this.points = new FastList<WB_Point>(points.length);
		for (final WB_Coordinate point : points) {
			this.points.add(new WB_Point(point));

		}
		build(triangulate);
	}

	public void build(final boolean triangulate)
			throws IllegalArgumentException {
		final int nump = points.size();
		if (nump < 4) {
			throw new IllegalArgumentException(
					"Less than four input points specified");
		}

		initBuffers(nump);
		setPoints(points);
		buildHull();
		if (triangulate) {
			triangulate();
		}
	}

	protected void initBuffers(final int nump) {
		pointBuffer = new Vertex[nump];
		vertexPointIndices = new int[nump];
		faces.clear();
		claimed.clear();
		numFaces = 0;
		numPoints = nump;
	}

	private void addPointToFace(final Vertex vtx, final Face face) {
		vtx.face = face;

		if (face.outside == null) {
			claimed.add(vtx);
		} else {
			claimed.insertBefore(vtx, face.outside);
		}
		face.outside = vtx;
	}

	private void removePointFromFace(final Vertex vtx, final Face face) {
		if (vtx == face.outside) {
			if (vtx.next != null && vtx.next.face == face) {
				face.outside = vtx.next;
			} else {
				face.outside = null;
			}
		}
		claimed.delete(vtx);
	}

	private Vertex removeAllPointsFromFace(final Face face) {
		if (face.outside != null) {
			Vertex end = face.outside;
			while (end.next != null && end.next.face == face) {
				end = end.next;
			}
			claimed.delete(face.outside, end);
			end.next = null;
			return face.outside;
		} else {
			return null;
		}
	}

	/**
	 * Triangulates any non-triangular hull faces. In some cases, due to
	 * precision issues, the resulting triangles may be very thin or small, and
	 * hence appear to be non-convex (this same limitation is present in <a
	 * href=http://www.qhull.org>qhull</a>).
	 */
	public void triangulate() {
		final double minArea = charLength * 2.2204460492503131e-13;
		newFaces.clear();
		for (final Iterator it = faces.iterator(); it.hasNext();) {
			final Face face = (Face) it.next();
			if (face.mark == Face.VISIBLE) {
				face.triangulate(newFaces, minArea);
				// splitFace (face);
			}
		}
		for (final Face face : newFaces) {
			faces.add(face);
		}
	}

	protected void setPoints(final List<WB_Point> points) {
		int i = 0;
		for (final WB_Point p : points) {
			pointBuffer[i] = new Vertex(p, i);
			i++;
		}
	}

	protected void computeMaxAndMin() {
		final WB_Vector max = new WB_Vector();
		final WB_Vector min = new WB_Vector();

		for (int i = 0; i < 3; i++) {
			maxVtxs[i] = (pointBuffer[0]);
			minVtxs[i] = (pointBuffer[0]);
		}

		double xm = pointBuffer[0].pos.xd(), ym = pointBuffer[0].pos.yd(), zm = pointBuffer[0].pos
				.zd();
		double xM = pointBuffer[0].pos.xd(), yM = pointBuffer[0].pos.yd(), zM = pointBuffer[0].pos
				.zd();
		for (int i = 1; i < numPoints; i++) {
			final Vertex v = pointBuffer[i];
			final WB_Vector pnt = v.pos;
			if (pnt.xd() > max.xd()) {
				xM = (pnt.xd());
				maxVtxs[0] = v;
			} else if (pnt.xd() < min.xd()) {
				xm = (pnt.xd());
				minVtxs[0] = v;
			}
			if (pnt.yd() > max.yd()) {
				yM = (pnt.yd());
				maxVtxs[1] = v;
			} else if (pnt.yd() < min.yd()) {
				ym = (pnt.yd());
				minVtxs[1] = v;
			}
			if (pnt.zd() > max.zd()) {
				zM = (pnt.zd());
				maxVtxs[2] = v;
			} else if (pnt.zd() < min.zd()) {
				zm = (pnt.zd());
				minVtxs[2] = v;
			}
			max._set(xM, yM, zM);
			min._set(xm, ym, zm);
		}

		// this epsilon formula comes from QuickHull, and I'm
		// not about to quibble.
		charLength = Math.max(max.xd() - min.xd(), max.yd() - min.yd());
		charLength = Math.max(max.zd() - min.zd(), charLength);

		tolerance = WB_Epsilon.EPSILON;

	}

	/**
	 * Creates the initial simplex from which the hull will be built.
	 */
	protected void createInitialSimplex() throws IllegalArgumentException {
		double max = 0;
		int imax = 0;
		final long st = System.currentTimeMillis();
		for (int i = 0; i < 3; i++) {
			final double diff = maxVtxs[i].pos.getd(i) - minVtxs[i].pos.getd(i);
			if (diff > max) {
				max = diff;
				imax = i;
			}
		}

		if (max <= tolerance) {
			throw new IllegalArgumentException(
					"Input points appear to be coincident");
		}
		final Vertex[] vtx = new Vertex[4];
		// set first two vertices to be those with the greatest
		// one dimensional separation

		vtx[0] = maxVtxs[imax];
		vtx[1] = minVtxs[imax];

		// set third vertex to be the vertex farthest from
		// the line between vtx0 and vtx1
		final WB_Vector u01 = new WB_Vector(vtx[1].pos.xd(), vtx[1].pos.yd(),
				vtx[1].pos.zd());
		final WB_Vector diff02 = new WB_Vector();
		final WB_Vector nrml = new WB_Vector();
		final WB_Vector xprod = new WB_Vector();
		double maxSqr = 0;
		u01._subSelf(vtx[0].pos);
		u01._normalizeSelf();

		for (int i = 0; i < numPoints; i++) {
			final Vertex p = pointBuffer[i];
			diff02._set(p.pos.xd() - vtx[0].pos.xd(),
					p.pos.yd() - vtx[0].pos.yd(), p.pos.zd() - vtx[0].pos.zd());
			xprod._set(u01.yd() * diff02.zd() - u01.zd() * diff02.yd(),
					u01.zd() * diff02.xd() - u01.xd() * diff02.zd(), u01.xd()
							* diff02.yd() - u01.yd() * diff02.xd());
			final double lenSqr = xprod.getSqLength();
			if (lenSqr > maxSqr && p != vtx[0] && // paranoid
					p != vtx[1]) {
				maxSqr = lenSqr;
				vtx[2] = p;
				nrml._set(xprod);
			}
		}
		if (Math.sqrt(maxSqr) <= 100 * tolerance) {
			throw new IllegalArgumentException(
					"Input points appear to be colinear");
		}
		nrml._normalizeSelf();

		double maxDist = 0;
		final double d0 = nrml.dot(vtx[2].pos);
		for (int i = 0; i < numPoints; i++) {
			final Vertex p = pointBuffer[i];
			final double dist = Math.abs(nrml.dot(p.pos) - d0);
			if (dist > maxDist && p != vtx[0] && // paranoid
					p != vtx[1] && p != vtx[2]) {
				maxDist = dist;
				vtx[3] = p;
			}
		}
		if (Math.abs(maxDist) <= 100 * tolerance) {
			throw new IllegalArgumentException(
					"Input points appear to be coplanar");
		}
		final long et = System.currentTimeMillis();
		// System.out.println((et - st));
		final Face[] tris = new Face[4];

		if (nrml.dot(vtx[3].pos) - d0 < 0) {
			tris[0] = Face.createTriangle(vtx[0], vtx[1], vtx[2]);
			tris[1] = Face.createTriangle(vtx[3], vtx[1], vtx[0]);
			tris[2] = Face.createTriangle(vtx[3], vtx[2], vtx[1]);
			tris[3] = Face.createTriangle(vtx[3], vtx[0], vtx[2]);

			for (int i = 0; i < 3; i++) {
				final int k = (i + 1) % 3;
				tris[i + 1].getEdge(1).setOpposite(tris[k + 1].getEdge(0));
				tris[i + 1].getEdge(2).setOpposite(tris[0].getEdge(k));
			}
		} else {
			tris[0] = Face.createTriangle(vtx[0], vtx[2], vtx[1]);
			tris[1] = Face.createTriangle(vtx[3], vtx[0], vtx[1]);
			tris[2] = Face.createTriangle(vtx[3], vtx[1], vtx[2]);
			tris[3] = Face.createTriangle(vtx[3], vtx[2], vtx[0]);

			for (int i = 0; i < 3; i++) {
				final int k = (i + 1) % 3;
				tris[i + 1].getEdge(0).setOpposite(tris[k + 1].getEdge(1));
				tris[i + 1].getEdge(2)
						.setOpposite(tris[0].getEdge((3 - i) % 3));
			}
		}

		for (int i = 0; i < 4; i++) {
			faces.add(tris[i]);
		}

		for (int i = 0; i < numPoints; i++) {
			final Vertex v = pointBuffer[i];

			if (v == vtx[0] || v == vtx[1] || v == vtx[2] || v == vtx[3]) {
				continue;
			}

			maxDist = tolerance;
			Face maxFace = null;
			for (int k = 0; k < 4; k++) {
				final double dist = tris[k].distanceToPlane(v.pos);
				if (dist > maxDist) {
					maxFace = tris[k];
					maxDist = dist;
				}
			}
			if (maxFace != null) {
				addPointToFace(v, maxFace);
			}
		}
	}

	/**
	 * Returns the number of vertices in this hull.
	 * 
	 * @return number of vertices
	 */
	public int getNumVertices() {
		return numVertices;
	}

	/**
	 * Returns the vertex points in this hull.
	 * 
	 * @return array of vertex points
	 */
	public List<WB_Point> getVertices() {
		final List<WB_Point> vtxs = new FastList<WB_Point>(numVertices);
		for (int i = 0; i < numVertices; i++) {
			vtxs.add(points.get(vertexPointIndices[i]));
		}
		return vtxs;
	}

	/**
	 * Returns an array specifing the index of each hull vertex with respect to
	 * the original input points.
	 * 
	 * @return vertex indices with respect to the original points
	 */
	public int[] getVertexPointIndices() {
		final int[] indices = new int[numVertices];
		for (int i = 0; i < numVertices; i++) {
			indices[i] = vertexPointIndices[i];
		}
		return indices;
	}

	/**
	 * Returns the number of faces in this hull.
	 * 
	 * @return number of faces
	 */
	public int getNumFaces() {
		return faces.size();
	}

	public int[][] getFaces() {
		final int[][] allFaces = new int[faces.size()][];
		int k = 0;
		for (final Iterator it = faces.iterator(); it.hasNext();) {
			final Face face = (Face) it.next();
			allFaces[k] = new int[face.numVertices()];
			getFaceIndices(allFaces[k], face);
			k++;
		}
		return allFaces;
	}

	private void getFaceIndices(final int[] indices, final Face face) {

		HalfEdge hedge = face.he0;
		int k = 0;
		do {
			final int idx = hedge.head().hullindex;
			indices[k++] = idx;
			hedge = hedge.next;
		} while (hedge != face.he0);
	}

	protected void resolveUnclaimedPoints(final List<Face> newFaces) {
		Vertex vtxNext = unclaimed.first();
		for (Vertex vtx = vtxNext; vtx != null; vtx = vtxNext) {
			vtxNext = vtx.next;

			double maxDist = tolerance;
			Face maxFace = null;
			for (final Face newFace : newFaces) {
				if (newFace.mark == Face.VISIBLE) {
					final double dist = newFace.distanceToPlane(vtx);
					if (dist > maxDist) {
						maxDist = dist;
						maxFace = newFace;
					}
					if (maxDist > 1000 * tolerance) {
						break;
					}
				}
			}
			if (maxFace != null) {
				addPointToFace(vtx, maxFace);

			}
		}
	}

	protected void deleteFacePoints(final Face face, final Face absorbingFace) {
		final Vertex faceVtxs = removeAllPointsFromFace(face);
		if (faceVtxs != null) {
			if (absorbingFace == null) {
				unclaimed.addAll(faceVtxs);
			} else {
				Vertex vtxNext = faceVtxs;
				for (Vertex vtx = vtxNext; vtx != null; vtx = vtxNext) {
					vtxNext = vtx.next;
					final double dist = absorbingFace.distanceToPlane(vtx);
					if (dist > tolerance) {
						addPointToFace(vtx, absorbingFace);
					} else {
						unclaimed.add(vtx);
					}
				}
			}
		}
	}

	private static final int NONCONVEX_WRT_LARGER_FACE = 1;
	private static final int NONCONVEX = 2;

	protected double oppFaceDistance(final HalfEdge he) {
		return he.face.distanceToPlane(he.opposite.face.getCentroid());
	}

	private boolean doAdjacentMerge(final Face face, final int mergeType) {
		HalfEdge hedge = face.he0;

		boolean convex = true;
		do {
			final Face oppFace = hedge.oppositeFace();
			boolean merge = false;
			double dist1;
			final double dist2;

			if (mergeType == NONCONVEX) { // then merge faces if they are
											// definitively non-convex
				if (oppFaceDistance(hedge) > -tolerance
						|| oppFaceDistance(hedge.opposite) > -tolerance) {
					merge = true;
				}
			} else // mergeType == NONCONVEX_WRT_LARGER_FACE
			{ // merge faces if they are parallel or non-convex
				// wrt to the larger face; otherwise, just mark
				// the face non-convex for the second pass.
				if (face.area > oppFace.area) {
					if ((dist1 = oppFaceDistance(hedge)) > -tolerance) {
						merge = true;
					} else if (oppFaceDistance(hedge.opposite) > -tolerance) {
						convex = false;
					}
				} else {
					if (oppFaceDistance(hedge.opposite) > -tolerance) {
						merge = true;
					} else if (oppFaceDistance(hedge) > -tolerance) {
						convex = false;
					}
				}
			}

			if (merge) {

				final int numd = face.mergeAdjacentFace(hedge, discardedFaces);
				for (int i = 0; i < numd; i++) {
					deleteFacePoints(discardedFaces[i], face);
				}

				return true;
			}
			hedge = hedge.next;
		} while (hedge != face.he0);
		if (!convex) {
			face.mark = Face.NON_CONVEX;
		}
		return false;
	}

	protected void calculateHorizon(final double eyePntx, final double eyePnty,
			final double eyePntz, HalfEdge edge0, final Face face,
			final Vector horizon) {
		deleteFacePoints(face, null);
		face.mark = Face.DELETED;

		HalfEdge edge;
		if (edge0 == null) {
			edge0 = face.getEdge(0);
			edge = edge0;
		} else {
			edge = edge0.getNext();
		}
		do {
			final Face oppFace = edge.oppositeFace();
			if (oppFace.mark == Face.VISIBLE) {
				if (oppFace.distanceToPlane(eyePntx, eyePnty, eyePntz) > tolerance) {
					calculateHorizon(eyePntx, eyePnty, eyePntz,
							edge.getOpposite(), oppFace, horizon);
				} else {
					horizon.add(edge);

				}
			}
			edge = edge.getNext();
		} while (edge != edge0);
	}

	private HalfEdge addAdjoiningFace(final Vertex eyeVtx, final HalfEdge he) {
		final Face face = Face.createTriangle(eyeVtx, he.tail(), he.head());
		faces.add(face);
		face.getEdge(-1).setOpposite(he.getOpposite());
		return face.getEdge(0);
	}

	protected void addNewFaces(final List<Face> newFaces, final Vertex eyeVtx,
			final Vector horizon) {
		newFaces.clear();

		HalfEdge hedgeSidePrev = null;
		HalfEdge hedgeSideBegin = null;

		for (final Iterator it = horizon.iterator(); it.hasNext();) {
			final HalfEdge horizonHe = (HalfEdge) it.next();
			final HalfEdge hedgeSide = addAdjoiningFace(eyeVtx, horizonHe);

			if (hedgeSidePrev != null) {
				hedgeSide.next.setOpposite(hedgeSidePrev);
			} else {
				hedgeSideBegin = hedgeSide;
			}
			newFaces.add(hedgeSide.getFace());
			hedgeSidePrev = hedgeSide;
		}
		hedgeSideBegin.next.setOpposite(hedgeSidePrev);
	}

	protected Vertex nextPointToAdd() {
		if (!claimed.isEmpty()) {
			final Face eyeFace = claimed.first().face;
			Vertex eyeVtx = null;
			double maxDist = 0;
			for (Vertex vtx = eyeFace.outside; vtx != null
					&& vtx.face == eyeFace; vtx = vtx.next) {
				final double dist = eyeFace.distanceToPlane(vtx);
				if (dist > maxDist) {
					maxDist = dist;
					eyeVtx = vtx;
				}
			}
			return eyeVtx;
		} else {
			return null;
		}
	}

	protected void addPointToHull(final Vertex eyeVtx) {
		horizon.clear();
		unclaimed.clear();

		removePointFromFace(eyeVtx, eyeVtx.face);
		calculateHorizon(eyeVtx.pos.xd(), eyeVtx.pos.yd(), eyeVtx.pos.zd(),
				null, eyeVtx.face, horizon);
		newFaces.clear();
		addNewFaces(newFaces, eyeVtx, horizon);

		// first merge pass ... merge faces which are non-convex
		// as determined by the larger face

		for (final Face face : newFaces) {
			if (face.mark == Face.VISIBLE) {
				while (doAdjacentMerge(face, NONCONVEX_WRT_LARGER_FACE)) {
					;
				}
			}
		}
		// second merge pass ... merge faces which are non-convex
		// wrt either face
		for (final Face face : newFaces) {
			if (face.mark == Face.NON_CONVEX) {
				face.mark = Face.VISIBLE;
				while (doAdjacentMerge(face, NONCONVEX)) {
					;
				}
			}
		}
		resolveUnclaimedPoints(newFaces);
	}

	protected void buildHull() {
		int cnt = 0;
		Vertex eyeVtx;

		computeMaxAndMin();

		createInitialSimplex();

		while ((eyeVtx = nextPointToAdd()) != null) {
			addPointToHull(eyeVtx);
			cnt++;

		}

		reindexFacesAndVertices();

	}

	private void markFaceVertices(final Face face) {
		final HalfEdge he0 = face.getFirstEdge();
		HalfEdge he = he0;
		do {
			he.head().flag = true;
			he.head().hullindex = 0;
			he = he.next;
		} while (he != he0);
	}

	protected void reindexFacesAndVertices() {
		for (int i = 1; i < numPoints; i++) {
			final Vertex p = pointBuffer[i];
			p.flag = false;
			p.hullindex = -1;
		}
		// remove inactive faces and mark active vertices
		numFaces = 0;
		for (final Iterator it = faces.iterator(); it.hasNext();) {
			final Face face = (Face) it.next();
			if (face.mark != Face.VISIBLE) {
				it.remove();
			} else {
				markFaceVertices(face);
				numFaces++;
			}
		}
		// reindex vertices
		numVertices = 0;

		for (int i = 0; i < numPoints; i++) {
			final Vertex vtx = pointBuffer[i];
			if (vtx.flag) {
				vertexPointIndices[numVertices] = i;
				vtx.hullindex = numVertices++;
			}

		}
	}

	static class Vertex {

		WB_Vector pos;

		/**
		 * Back index into an array.
		 */

		int hullindex;

		/**
		 * List forward link.
		 */
		Vertex prev;

		/**
		 * List backward link.
		 */
		Vertex next;

		/**
		 * Current face that this vertex is outside of.
		 */
		Face face;

		boolean flag;

		/**
		 * Constructs a vertex and sets its coordinates to 0.
		 */
		protected Vertex() {
			flag = false;
		}

		/**
		 * Constructs a vertex with the specified coordinates and index.
		 */
		protected Vertex(final WB_Coordinate p, final int idx) {
			pos = new WB_Vector(p);
			hullindex = idx;
			flag = false;
		}

	}

	static class Face {
		HalfEdge he0;
		private final WB_Vector normal;
		double area;
		private WB_Vector centroid;
		double planeOffset;
		int index;
		int numVerts;

		Face next;

		static final int VISIBLE = 1;
		static final int NON_CONVEX = 2;
		static final int DELETED = 3;

		int mark = VISIBLE;

		Vertex outside;

		protected WB_Vector computeCentroid() {
			centroid = new WB_Vector();
			HalfEdge he = he0;
			Vertex v;
			do {
				v = he.head();
				centroid._addSelf(v.pos);
				he = he.next;
			} while (he != he0);
			return centroid._divSelf(numVerts);
		}

		protected void computeNormal(final WB_Vector normal,
				final double minArea) {
			computeNormal(normal);

			if (area < minArea) {
				System.out.println("area=" + area);
				// make the normal more robust by removing
				// components parallel to the longest edge

				HalfEdge hedgeMax = null;
				double lenSqrMax = 0;
				HalfEdge hedge = he0;
				do {
					final double lenSqr = hedge.lengthSquared();
					if (lenSqr > lenSqrMax) {
						hedgeMax = hedge;
						lenSqrMax = lenSqr;
					}
					hedge = hedge.next;
				} while (hedge != he0);

				final Vertex p2 = hedgeMax.head();
				final Vertex p1 = hedgeMax.tail();
				final double lenMax = Math.sqrt(lenSqrMax);
				final WB_Vector u = p2.pos.sub(p1.pos)._divSelf(lenMax);
				final double dot = normal.dot(u);
				normal.addMul(-dot, u);

				normal._normalizeSelf();
			}
		}

		protected void computeNormal(final WB_Vector normal) {
			HalfEdge he1 = he0.next;
			HalfEdge he2 = he1.next;

			final Vertex p0 = he0.head();
			Vertex p2 = he1.head();

			double d2x = p2.pos.xd() - p0.pos.xd();
			double d2y = p2.pos.yd() - p0.pos.yd();
			double d2z = p2.pos.zd() - p0.pos.zd();

			normal._set(0, 0, 0);

			numVerts = 2;

			while (he2 != he0) {
				final double d1x = d2x;
				final double d1y = d2y;
				final double d1z = d2z;

				p2 = he2.head();
				d2x = p2.pos.xd() - p0.pos.xd();
				d2y = p2.pos.yd() - p0.pos.yd();
				d2z = p2.pos.zd() - p0.pos.zd();

				normal._addSelf(d1y * d2z - d1z * d2y, d1z * d2x - d1x * d2z,
						d1x * d2y - d1y * d2x);

				he1 = he2;
				he2 = he2.next;
				numVerts++;
			}
			area = normal.getLength();
			normal._normalizeSelf();

		}

		private void computeNormalAndCentroid() {
			computeNormal(normal);
			centroid = computeCentroid();
			planeOffset = normal.dot(centroid);
			int numv = 0;
			HalfEdge he = he0;
			do {
				numv++;
				he = he.next;
			} while (he != he0);
			if (numv != numVerts) {
				throw new InternalErrorException("face " + getVertexString()
						+ " numVerts=" + numVerts + " should be " + numv);
			}
		}

		private void computeNormalAndCentroid(final double minArea) {
			computeNormal(normal, minArea);
			centroid = computeCentroid();
			planeOffset = normal.dot(centroid);
		}

		protected static Face createTriangle(final Vertex v0, final Vertex v1,
				final Vertex v2) {
			return createTriangle(v0, v1, v2, 0);
		}

		/**
		 * Constructs a triangule Face from vertices v0, v1, and v2.
		 * 
		 * @param v0
		 *            first vertex
		 * @param v1
		 *            second vertex
		 * @param v2
		 *            third vertex
		 */
		protected static Face createTriangle(final Vertex v0, final Vertex v1,
				final Vertex v2, final double minArea) {
			final Face face = new Face();
			final HalfEdge he0 = new HalfEdge(v0, face);
			final HalfEdge he1 = new HalfEdge(v1, face);
			final HalfEdge he2 = new HalfEdge(v2, face);

			he0.prev = he2;
			he0.next = he1;
			he1.prev = he0;
			he1.next = he2;
			he2.prev = he1;
			he2.next = he0;

			face.he0 = he0;

			// compute the normal and offset
			face.computeNormalAndCentroid(minArea);
			return face;
		}

		protected static Face create(final FastList<Vertex> vtxArray,
				final int[] indices) {
			final Face face = new Face();
			HalfEdge hePrev = null;
			for (final int indice : indices) {
				final HalfEdge he = new HalfEdge(vtxArray.get(indice), face);
				if (hePrev != null) {
					he.setPrev(hePrev);
					hePrev.setNext(he);
				} else {
					face.he0 = he;
				}
				hePrev = he;
			}
			face.he0.setPrev(hePrev);
			hePrev.setNext(face.he0);

			// compute the normal and offset
			face.computeNormalAndCentroid();
			return face;
		}

		protected Face() {
			normal = new WB_Vector();
			centroid = new WB_Vector();
			mark = VISIBLE;
		}

		/**
		 * Gets the i-th half-edge associated with the face.
		 * 
		 * @param i
		 *            the half-edge index, in the range 0-2.
		 * @return the half-edge
		 */
		protected HalfEdge getEdge(int i) {
			HalfEdge he = he0;
			while (i > 0) {
				he = he.next;
				i--;
			}
			while (i < 0) {
				he = he.prev;
				i++;
			}
			return he;
		}

		protected HalfEdge getFirstEdge() {
			return he0;
		}

		/**
		 * Finds the half-edge within this face which has tail <code>vt</code>
		 * and head <code>vh</code>.
		 * 
		 * @param vt
		 *            tail point
		 * @param vh
		 *            head point
		 * @return the half-edge, or null if none is found.
		 */
		protected HalfEdge findEdge(final Vertex vt, final Vertex vh) {
			HalfEdge he = he0;
			do {
				if (he.head() == vh && he.tail() == vt) {
					return he;
				}
				he = he.next;
			} while (he != he0);
			return null;
		}

		/**
		 * Computes the distance from a point p to the plane of this face.
		 * 
		 * @param p
		 *            the point
		 * @return distance from the point to the plane
		 */
		protected double distanceToPlane(final WB_Coordinate p) {
			return normal.xd() * p.xd() + normal.yd() * p.yd() + normal.zd()
					* p.zd() - planeOffset;
		}

		protected double distanceToPlane(final double x, final double y,
				final double z) {
			return normal.xd() * x + normal.yd() * y + normal.zd() * z
					- planeOffset;
		}

		protected double distanceToPlane(final double[] p) {
			return normal.xd() * p[0] + normal.yd() * p[1] + normal.zd() * p[2]
					- planeOffset;
		}

		protected double distanceToPlane(final Vertex v) {
			return normal.xd() * v.pos.xd() + normal.yd() * v.pos.yd()
					+ normal.zd() * v.pos.zd() - planeOffset;
		}

		/**
		 * Returns the normal of the plane associated with this face.
		 * 
		 * @return the planar normal
		 */
		protected WB_Vector getNormal() {
			return normal;
		}

		protected WB_Vector getCentroid() {
			return centroid;
		}

		protected int numVertices() {
			return numVerts;
		}

		protected String getVertexString() {
			String s = null;
			HalfEdge he = he0;
			do {
				if (s == null) {
					s = "" + he.head().hullindex;
				} else {
					s += " " + he.head().hullindex;
				}
				he = he.next;
			} while (he != he0);
			return s;
		}

		protected void getVertexIndices(final int[] idxs) {
			HalfEdge he = he0;
			int i = 0;
			do {
				idxs[i++] = he.head().hullindex;
				he = he.next;
			} while (he != he0);
		}

		private Face connectHalfEdges(final HalfEdge hedgePrev,
				final HalfEdge hedge) {
			Face discardedFace = null;

			if (hedgePrev.oppositeFace() == hedge.oppositeFace()) { // then
																	// there is
																	// a
																	// redundant
																	// edge that
																	// we can
																	// get rid
																	// off

				final Face oppFace = hedge.oppositeFace();
				HalfEdge hedgeOpp;

				if (hedgePrev == he0) {
					he0 = hedge;
				}
				if (oppFace.numVertices() == 3) { // then we can get rid of the
													// opposite face altogether
					hedgeOpp = hedge.getOpposite().prev.getOpposite();

					oppFace.mark = DELETED;
					discardedFace = oppFace;
				} else {
					hedgeOpp = hedge.getOpposite().next;

					if (oppFace.he0 == hedgeOpp.prev) {
						oppFace.he0 = hedgeOpp;
					}
					hedgeOpp.prev = hedgeOpp.prev.prev;
					hedgeOpp.prev.next = hedgeOpp;
				}
				hedge.prev = hedgePrev.prev;
				hedge.prev.next = hedge;

				hedge.opposite = hedgeOpp;
				hedgeOpp.opposite = hedge;

				// oppFace was modified, so need to recompute
				oppFace.computeNormalAndCentroid();
			} else {
				hedgePrev.next = hedge;
				hedge.prev = hedgePrev;
			}
			return discardedFace;
		}

		void checkConsistency() {
			// do a sanity check on the face
			HalfEdge hedge = he0;
			double maxd = 0;
			int numv = 0;

			if (numVerts < 3) {
				throw new InternalErrorException("degenerate face: "
						+ getVertexString());
			}
			do {
				final HalfEdge hedgeOpp = hedge.getOpposite();
				if (hedgeOpp == null) {
					throw new InternalErrorException("face "
							+ getVertexString() + ": "
							+ "unreflected half edge "
							+ hedge.getVertexString());
				} else if (hedgeOpp.getOpposite() != hedge) {
					throw new InternalErrorException("face "
							+ getVertexString() + ": " + "opposite half edge "
							+ hedgeOpp.getVertexString() + " has opposite "
							+ hedgeOpp.getOpposite().getVertexString());
				}
				if (hedgeOpp.head() != hedge.tail()
						|| hedge.head() != hedgeOpp.tail()) {
					throw new InternalErrorException("face "
							+ getVertexString() + ": " + "half edge "
							+ hedge.getVertexString() + " reflected by "
							+ hedgeOpp.getVertexString());
				}
				final Face oppFace = hedgeOpp.face;
				if (oppFace == null) {
					throw new InternalErrorException("face "
							+ getVertexString() + ": "
							+ "no face on half edge "
							+ hedgeOpp.getVertexString());
				} else if (oppFace.mark == DELETED) {
					throw new InternalErrorException("face "
							+ getVertexString() + ": " + "opposite face "
							+ oppFace.getVertexString() + " not on hull");
				}
				final double d = Math.abs(distanceToPlane(hedge.head()));
				if (d > maxd) {
					maxd = d;
				}
				numv++;
				hedge = hedge.next;
			} while (hedge != he0);

			if (numv != numVerts) {
				throw new InternalErrorException("face " + getVertexString()
						+ " numVerts=" + numVerts + " should be " + numv);
			}

		}

		protected int mergeAdjacentFace(final HalfEdge hedgeAdj,
				final Face[] discarded) {
			final Face oppFace = hedgeAdj.oppositeFace();
			int numDiscarded = 0;

			discarded[numDiscarded++] = oppFace;

			oppFace.mark = DELETED;

			final HalfEdge hedgeOpp = hedgeAdj.getOpposite();

			HalfEdge hedgeAdjPrev = hedgeAdj.prev;
			HalfEdge hedgeAdjNext = hedgeAdj.next;
			HalfEdge hedgeOppPrev = hedgeOpp.prev;
			HalfEdge hedgeOppNext = hedgeOpp.next;

			while (hedgeAdjPrev.oppositeFace() == oppFace) {
				hedgeAdjPrev = hedgeAdjPrev.prev;
				hedgeOppNext = hedgeOppNext.next;
			}

			while (hedgeAdjNext.oppositeFace() == oppFace) {
				hedgeOppPrev = hedgeOppPrev.prev;
				hedgeAdjNext = hedgeAdjNext.next;
			}

			HalfEdge hedge;

			for (hedge = hedgeOppNext; hedge != hedgeOppPrev.next; hedge = hedge.next) {
				hedge.face = this;
			}

			if (hedgeAdj == he0) {
				he0 = hedgeAdjNext;
			}

			// handle the half edges at the head
			Face discardedFace;

			discardedFace = connectHalfEdges(hedgeOppPrev, hedgeAdjNext);
			if (discardedFace != null) {
				discarded[numDiscarded++] = discardedFace;
			}

			// handle the half edges at the tail
			discardedFace = connectHalfEdges(hedgeAdjPrev, hedgeOppNext);
			if (discardedFace != null) {
				discarded[numDiscarded++] = discardedFace;
			}

			computeNormalAndCentroid();
			checkConsistency();

			return numDiscarded;
		}

		private double areaSquared(final HalfEdge hedge0, final HalfEdge hedge1) {
			// return the squared area of the triangle defined
			// by the half edge hedge0 and the point at the
			// head of hedge1.

			final Vertex p0 = hedge0.tail();
			final Vertex p1 = hedge0.head();
			final Vertex p2 = hedge1.head();

			final double dx1 = p1.pos.xd() - p0.pos.xd();
			final double dy1 = p1.pos.yd() - p0.pos.yd();
			final double dz1 = p1.pos.zd() - p0.pos.zd();

			final double dx2 = p2.pos.xd() - p0.pos.xd();
			final double dy2 = p2.pos.yd() - p0.pos.yd();
			final double dz2 = p2.pos.zd() - p0.pos.zd();

			final double x = dy1 * dz2 - dz1 * dy2;
			final double y = dz1 * dx2 - dx1 * dz2;
			final double z = dx1 * dy2 - dy1 * dx2;

			return x * x + y * y + z * z;
		}

		protected void triangulate(final List<Face> newFaces,
				final double minArea) {
			HalfEdge hedge;

			if (numVertices() < 4) {
				return;
			}

			final Vertex v0 = he0.head();
			final Face prevFace = null;

			hedge = he0.next;
			HalfEdge oppPrev = hedge.opposite;
			Face face0 = null;

			for (hedge = hedge.next; hedge != he0.prev; hedge = hedge.next) {
				final Face face = createTriangle(v0, hedge.prev.head(),
						hedge.head(), minArea);
				face.he0.next.setOpposite(oppPrev);
				face.he0.prev.setOpposite(hedge.opposite);
				oppPrev = face.he0;
				newFaces.add(face);
				if (face0 == null) {
					face0 = face;
				}
			}
			hedge = new HalfEdge(he0.prev.prev.head(), this);
			hedge.setOpposite(oppPrev);

			hedge.prev = he0;
			hedge.prev.next = hedge;

			hedge.next = he0.prev;
			hedge.next.prev = hedge;

			computeNormalAndCentroid(minArea);
			checkConsistency();

			for (Face face = face0; face != null; face = face.next) {
				face.checkConsistency();
			}

		}
	}

	static class HalfEdge {
		/**
		 * The vertex associated with the head of this half-edge.
		 */
		Vertex vertex;

		/**
		 * Triangular face associated with this half-edge.
		 */
		Face face;

		/**
		 * Next half-edge in the triangle.
		 */
		HalfEdge next;

		/**
		 * Previous half-edge in the triangle.
		 */
		HalfEdge prev;

		/**
		 * Half-edge associated with the opposite triangle adjacent to this
		 * edge.
		 */
		HalfEdge opposite;

		/**
		 * Constructs a HalfEdge with head vertex <code>v</code> and left-hand
		 * triangular face <code>f</code>.
		 * 
		 * @param v
		 *            head vertex
		 * @param f
		 *            left-hand triangular face
		 */
		protected HalfEdge(final Vertex v, final Face f) {
			vertex = v;
			face = f;
		}

		protected HalfEdge() {
		}

		/**
		 * Sets the value of the next edge adjacent (counter-clockwise) to this
		 * one within the triangle.
		 * 
		 * @param edge
		 *            next adjacent edge
		 */
		protected void setNext(final HalfEdge edge) {
			next = edge;
		}

		/**
		 * Gets the value of the next edge adjacent (counter-clockwise) to this
		 * one within the triangle.
		 * 
		 * @return next adjacent edge
		 */
		protected HalfEdge getNext() {
			return next;
		}

		/**
		 * Sets the value of the previous edge adjacent (clockwise) to this one
		 * within the triangle.
		 * 
		 * @param edge
		 *            previous adjacent edge
		 */
		protected void setPrev(final HalfEdge edge) {
			prev = edge;
		}

		/**
		 * Gets the value of the previous edge adjacent (clockwise) to this one
		 * within the triangle.
		 * 
		 * @return previous adjacent edge
		 */
		protected HalfEdge getPrev() {
			return prev;
		}

		/**
		 * Returns the triangular face located to the left of this half-edge.
		 * 
		 * @return left-hand triangular face
		 */
		protected Face getFace() {
			return face;
		}

		/**
		 * Returns the half-edge opposite to this half-edge.
		 * 
		 * @return opposite half-edge
		 */
		protected HalfEdge getOpposite() {
			return opposite;
		}

		/**
		 * Sets the half-edge opposite to this half-edge.
		 * 
		 * @param edge
		 *            opposite half-edge
		 */
		protected void setOpposite(final HalfEdge edge) {
			opposite = edge;
			edge.opposite = this;
		}

		/**
		 * Returns the head vertex associated with this half-edge.
		 * 
		 * @return head vertex
		 */
		protected Vertex head() {
			return vertex;
		}

		/**
		 * Returns the tail vertex associated with this half-edge.
		 * 
		 * @return tail vertex
		 */
		protected Vertex tail() {
			return prev != null ? prev.vertex : null;
		}

		/**
		 * Returns the opposite triangular face associated with this half-edge.
		 * 
		 * @return opposite triangular face
		 */
		protected Face oppositeFace() {
			return opposite != null ? opposite.face : null;
		}

		/**
		 * Produces a string identifying this half-edge by the point index
		 * values of its tail and head vertices.
		 * 
		 * @return identifying string
		 */
		protected String getVertexString() {
			if (tail() != null) {
				return "" + tail().hullindex + "-" + head().hullindex;
			} else {
				return "?-" + head().hullindex;
			}
		}

		/**
		 * Returns the length of this half-edge.
		 * 
		 * @return half-edge length
		 */
		protected double length() {
			if (tail() != null) {

				return WB_Distance3D.distance(head().pos, tail().pos);
			} else {
				return -1;
			}
		}

		/**
		 * Returns the length squared of this half-edge.
		 * 
		 * @return half-edge length squared
		 */
		protected double lengthSquared() {
			if (tail() != null) {
				return WB_Distance3D.sqDistance(head().pos, tail().pos);
			} else {
				return -1;
			}
		}

	}

	static class VertexList {
		private Vertex head;
		private Vertex tail;

		/**
		 * Clears this list.
		 */
		protected void clear() {
			head = tail = null;
		}

		/**
		 * Adds a vertex to the end of this list.
		 */
		protected void add(final Vertex vtx) {
			if (head == null) {
				head = vtx;
			} else {
				tail.next = vtx;
			}
			vtx.prev = tail;
			vtx.next = null;
			tail = vtx;
		}

		/**
		 * Adds a chain of vertices to the end of this list.
		 */
		protected void addAll(Vertex vtx) {
			if (head == null) {
				head = vtx;
			} else {
				tail.next = vtx;
			}
			vtx.prev = tail;
			while (vtx.next != null) {
				vtx = vtx.next;
			}
			tail = vtx;
		}

		/**
		 * Deletes a vertex from this list.
		 */
		protected void delete(final Vertex vtx) {
			if (vtx.prev == null) {
				head = vtx.next;
			} else {
				vtx.prev.next = vtx.next;
			}
			if (vtx.next == null) {
				tail = vtx.prev;
			} else {
				vtx.next.prev = vtx.prev;
			}
		}

		/**
		 * Deletes a chain of vertices from this list.
		 */
		protected void delete(final Vertex vtx1, final Vertex vtx2) {
			if (vtx1.prev == null) {
				head = vtx2.next;
			} else {
				vtx1.prev.next = vtx2.next;
			}
			if (vtx2.next == null) {
				tail = vtx1.prev;
			} else {
				vtx2.next.prev = vtx1.prev;
			}
		}

		/**
		 * Inserts a vertex into this list before another specificed vertex.
		 */
		protected void insertBefore(final Vertex vtx, final Vertex next) {
			vtx.prev = next.prev;
			if (next.prev == null) {
				head = vtx;
			} else {
				next.prev.next = vtx;
			}
			vtx.next = next;
			next.prev = vtx;
		}

		/**
		 * Returns the first element in this list.
		 */
		protected Vertex first() {
			return head;
		}

		/**
		 * Returns true if this list is empty.
		 */
		protected boolean isEmpty() {
			return head == null;
		}
	}

	protected static class InternalErrorException extends RuntimeException {
		protected InternalErrorException(final String msg) {
			super(msg);
		}
	}

}
