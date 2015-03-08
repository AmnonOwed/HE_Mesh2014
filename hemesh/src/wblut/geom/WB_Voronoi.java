/*
 * 
 */
package wblut.geom;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javolution.util.FastTable;
import wblut.external.ProGAL.CTetrahedron;
import wblut.external.ProGAL.CVertex;
import wblut.external.ProGAL.DelaunayComplex;
import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;

/**
 * 
 */
public class WB_Voronoi {
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @param points 
     * @param n 
     * @param aabb 
     * @param precision 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final WB_Coordinate[] points, final int n, final WB_AABB aabb,
	    final double precision) {
	final WB_Delaunay triangulation = WB_Delaunay.getTriangulation3D(
		points, precision);
	final int nv = Math.min(n, points.length);
	final List<WB_VoronoiCell3D> result = new FastTable<WB_VoronoiCell3D>();
	for (int i = 0; i < nv; i++) {
	    final int[] tetras = triangulation.Vertices[i];
	    final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
	    for (int t = 0; t < tetras.length; t++) {
		hullpoints.add(triangulation.circumcenters[tetras[t]]);
	    }
	    final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(hullpoints,
		    geometryfactory.createPoint(points[i]), i);
	    if (vor.cell != null) {
		vor.constrain(aabb);
	    }
	    if (vor.cell != null) {
		result.add(vor);
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param points 
     * @param n 
     * @param aabb 
     * @param precision 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final List<? extends WB_Coordinate> points, final int n,
	    final WB_AABB aabb, final double precision) {
	final WB_Delaunay triangulation = WB_Delaunay.getTriangulation3D(
		points, precision);
	final int nv = Math.min(n, points.size());
	final List<WB_VoronoiCell3D> result = new FastTable<WB_VoronoiCell3D>();
	for (int i = 0; i < nv; i++) {
	    final int[] tetras = triangulation.Vertices[i];
	    final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
	    for (int t = 0; t < tetras.length; t++) {
		hullpoints.add(triangulation.circumcenters[tetras[t]]);
	    }
	    hullpoints.add(new WB_Point(points.get(i)));
	    final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(hullpoints,
		    geometryfactory.createPoint(points.get(i)), i);
	    if (vor.cell != null) {
		vor.constrain(aabb);
	    }
	    if (vor.cell != null) {
		result.add(vor);
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param points 
     * @param aabb 
     * @param precision 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final List<? extends WB_Coordinate> points, final WB_AABB aabb,
	    final double precision) {
	return getVoronoi3D(points, points.size(), aabb, precision);
    }

    /**
     * 
     *
     * @param points 
     * @param aabb 
     * @param precision 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final WB_Coordinate[] points, final WB_AABB aabb,
	    final double precision) {
	return getVoronoi3D(points, points.length, aabb, precision);
    }

    /**
     * 
     *
     * @param points 
     * @param aabb 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final WB_Coordinate[] points, final WB_AABB aabb) {
	return getVoronoi3D(points, points.length, aabb);
    }

    /**
     * 
     *
     * @param points 
     * @param nv 
     * @param aabb 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final WB_Coordinate[] points, int nv, final WB_AABB aabb) {
	nv = Math.min(nv, points.length);
	if (nv <= 4) {
	    return getVoronoi3DBF(points, nv, aabb);
	}
	final int n = points.length;
	final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(
		n);
	final WB_KDTree<WB_Coordinate, Integer> tree = new WB_KDTree<WB_Coordinate, Integer>();
	for (int i = 0; i < n; i++) {
	    tmppoints.add(new wblut.external.ProGAL.Point(points[i].xd(),
		    points[i].yd(), points[i].zd()));
	    tree.add(points[i], i);
	}
	final DelaunayComplex dc = new DelaunayComplex(tmppoints);
	final List<CVertex> vertices = dc.getVertices();
	final List<WB_VoronoiCell3D> result = new FastTable<WB_VoronoiCell3D>();
	for (int i = 0; i < nv; i++) {
	    final CVertex v = vertices.get(i);
	    final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
	    final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
	    for (final CTetrahedron tetra : vertexhull) {
		// if (!tetra.containsBigPoint()) {
		hullpoints.add(toPoint(tetra.circumcenter()));
		// }
	    }
	    final List<WB_Point> finalpoints = new FastTable<WB_Point>();
	    for (int j = 0; j < hullpoints.size(); j++) {
		finalpoints.add(geometryfactory.createPoint(hullpoints.get(j)));
	    }
	    final int index = tree.getNearestNeighbor(toPoint(v)).value;
	    final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(finalpoints,
		    geometryfactory.createPoint(points[index]), index);
	    if (aabb != null) {
		vor.constrain(aabb);
	    }
	    if (vor.cell != null) {
		result.add(vor);
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public static int[][] getVoronoi3DNeighbors(final WB_Coordinate[] points) {
	final int nv = points.length;
	if (nv == 2) {
	    return new int[][] { { 1 }, { 0 } };
	} else if (nv == 3) {
	    return new int[][] { { 1, 2 }, { 0, 2 }, { 0, 1 } };
	} else if (nv == 4) {
	    return new int[][] { { 1, 2, 3 }, { 0, 2, 3 }, { 0, 1, 3 },
		    { 0, 1, 2 } };
	}
	final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(
		nv);
	final WB_KDTree<WB_Coordinate, Integer> tree = new WB_KDTree<WB_Coordinate, Integer>();
	for (int i = 0; i < nv; i++) {
	    tmppoints.add(new wblut.external.ProGAL.Point(points[i].xd(),
		    points[i].yd(), points[i].zd()));
	    tree.add(points[i], i);
	}
	final DelaunayComplex dc = new DelaunayComplex(tmppoints);
	final List<CVertex> vertices = dc.getVertices();
	final int[][] ns = new int[nv][];
	for (int i = 0; i < nv; i++) {
	    final CVertex v = vertices.get(i);
	    final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
	    final TIntSet neighbors = new TIntHashSet();
	    for (final CTetrahedron tetra : vertexhull) {
		for (int j = 0; j < 4; j++) {
		    if (!tetra.getPoint(j).isBigpoint()) {
			neighbors.add(tree.getNearestNeighbor(toPoint(tetra
				.getPoint(j))).value);
		    }
		}
	    }
	    ns[i] = neighbors.toArray();
	}
	return ns;
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public static int[][] getVoronoi3DNeighbors(
	    final List<? extends WB_Coordinate> points) {
	final int nv = points.size();
	if (nv == 2) {
	    return new int[][] { { 1 }, { 0 } };
	} else if (nv == 3) {
	    return new int[][] { { 1, 2 }, { 0, 2 }, { 0, 1 } };
	} else if (nv == 4) {
	    return new int[][] { { 1, 2, 3 }, { 0, 2, 3 }, { 0, 1, 3 },
		    { 0, 1, 2 } };
	}
	final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(
		nv);
	final WB_KDTree<WB_Coordinate, Integer> tree = new WB_KDTree<WB_Coordinate, Integer>();
	WB_Coordinate p;
	for (int i = 0; i < nv; i++) {
	    p = points.get(i);
	    tmppoints.add(new wblut.external.ProGAL.Point(p.xd(), p.yd(), p
		    .zd()));
	    tree.add(p, i);
	}
	final DelaunayComplex dc = new DelaunayComplex(tmppoints);
	final List<CVertex> vertices = dc.getVertices();
	final int[][] ns = new int[nv][];
	for (int i = 0; i < nv; i++) {
	    final CVertex v = vertices.get(i);
	    final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
	    final TIntSet neighbors = new TIntHashSet();
	    for (final CTetrahedron tetra : vertexhull) {
		for (int j = 0; j < 4; j++) {
		    if (!tetra.getPoint(j).isBigpoint()) {
			neighbors.add(tree.getNearestNeighbor(toPoint(tetra
				.getPoint(j))).value);
		    }
		}
	    }
	    ns[i] = neighbors.toArray();
	}
	return ns;
    }

    /**
     * 
     *
     * @param points 
     * @param aabb 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final List<? extends WB_Coordinate> points, final WB_AABB aabb) {
	return getVoronoi3D(points, points.size(), aabb);
    }

    /**
     * 
     *
     * @param points 
     * @param nv 
     * @param aabb 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3D(
	    final List<? extends WB_Coordinate> points, int nv,
	    final WB_AABB aabb) {
	nv = Math.min(nv, points.size());
	if (nv <= 4) {
	    return getVoronoi3DBF(points, nv, aabb);
	}
	final int n = points.size();
	final List<wblut.external.ProGAL.Point> tmppoints = new ArrayList<wblut.external.ProGAL.Point>(
		n);
	final WB_KDTree<WB_Coordinate, Integer> tree = new WB_KDTree<WB_Coordinate, Integer>();
	int i = 0;
	for (final WB_Coordinate p : points) {
	    tmppoints.add(new wblut.external.ProGAL.Point(p.xd(), p.yd(), p
		    .zd()));
	    tree.add(p, i++);
	}
	final DelaunayComplex dc = new DelaunayComplex(tmppoints);
	final List<CVertex> vertices = dc.getVertices();
	final List<WB_VoronoiCell3D> result = new FastTable<WB_VoronoiCell3D>();
	for (i = 0; i < nv; i++) {
	    final CVertex v = vertices.get(i);
	    final Set<CTetrahedron> vertexhull = dc.getVertexHull(v);
	    v.getAdjacentTriangles();
	    final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
	    for (final CTetrahedron tetra : vertexhull) {
		// if (!tetra.containsBigPoint()) {
		hullpoints.add(toPoint(tetra.circumcenter()));
		// }
	    }
	    final List<WB_Point> finalpoints = new FastTable<WB_Point>();
	    for (int j = 0; j < hullpoints.size(); j++) {
		finalpoints.add(geometryfactory.createPoint(hullpoints.get(j)));
	    }
	    final int index = tree.getNearestNeighbor(toPoint(v)).value;
	    final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(finalpoints,
		    geometryfactory.createPoint(points.get(index)), index);
	    if (vor.cell != null) {
		vor.constrain(aabb);
	    }
	    if (vor.cell != null) {
		result.add(vor);
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param points 
     * @param nv 
     * @param aabb 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3DBF(
	    final List<? extends WB_Coordinate> points, int nv,
	    final WB_AABB aabb) {
	nv = Math.min(nv, points.size());
	final int n = points.size();
	final List<WB_VoronoiCell3D> result = new FastTable<WB_VoronoiCell3D>();
	for (int i = 0; i < nv; i++) {
	    final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
	    final WB_Point O = new WB_Point();
	    WB_Plane P;
	    final WB_FaceListMesh cell = geometryfactory.createMesh(aabb);
	    for (int j = 0; j < n; j++) {
		if (j != i) {
		    final WB_Vector N = new WB_Vector(points.get(i));
		    N.subSelf(points.get(j));
		    N.normalizeSelf();
		    O.set(points.get(i)); // plane origin=point halfway
		    // between point i and point j
		    O.addSelf(points.get(j));
		    O.mulSelf(0.5);
		    P = new WB_Plane(O, N);
		    cutPlanes.add(P);
		}
	    }
	    boolean unique;
	    final ArrayList<WB_Plane> cleaned = new ArrayList<WB_Plane>();
	    for (int j = 0; j < cutPlanes.size(); j++) {
		P = cutPlanes.get(j);
		unique = true;
		for (int k = 0; k < j; k++) {
		    final WB_Plane Pj = cutPlanes.get(j);
		    if (WB_Plane.isEqual(P, Pj)) {
			unique = false;
			break;
		    }
		}
		if (unique) {
		    cleaned.add(P);
		}
	    }
	    final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(cell,
		    geometryfactory.createPoint(points.get(i)), i);
	    if (vor.cell != null) {
		vor.constrain(cutPlanes);
	    }
	    if (vor.cell != null) {
		result.add(vor);
	    }
	    result.add(vor);
	}
	return result;
    }

    /**
     * 
     *
     * @param points 
     * @param nv 
     * @param aabb 
     * @return 
     */
    public static List<WB_VoronoiCell3D> getVoronoi3DBF(
	    final WB_Coordinate[] points, int nv, final WB_AABB aabb) {
	nv = Math.min(nv, points.length);
	final int n = points.length;
	final List<WB_VoronoiCell3D> result = new FastTable<WB_VoronoiCell3D>();
	for (int i = 0; i < nv; i++) {
	    final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
	    final WB_Point O = new WB_Point();
	    WB_Plane P;
	    final WB_FaceListMesh cell = geometryfactory.createMesh(aabb);
	    for (int j = 0; j < n; j++) {
		if (j != i) {
		    final WB_Vector N = new WB_Vector(points[i]);
		    N.subSelf(points[j]);
		    N.normalizeSelf();
		    O.set(points[i]); // plane origin=point halfway
		    // between point i and point j
		    O.addSelf(points[j]);
		    O.mulSelf(0.5);
		    P = new WB_Plane(O, N);
		    cutPlanes.add(P);
		}
	    }
	    boolean unique;
	    final ArrayList<WB_Plane> cleaned = new ArrayList<WB_Plane>();
	    for (int j = 0; j < cutPlanes.size(); j++) {
		P = cutPlanes.get(j);
		unique = true;
		for (int k = 0; k < j; k++) {
		    final WB_Plane Pj = cutPlanes.get(j);
		    if (WB_Plane.isEqual(P, Pj)) {
			unique = false;
			break;
		    }
		}
		if (unique) {
		    cleaned.add(P);
		}
	    }
	    final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(cell,
		    geometryfactory.createPoint(points[i]), i);
	    if (vor.cell != null) {
		vor.constrain(cutPlanes);
	    }
	    if (vor.cell != null) {
		result.add(vor);
	    }
	    result.add(vor);
	}
	return result;
    }

    /**
     * 
     *
     * @param v 
     * @return 
     */
    private static WB_Point toPoint(final wblut.external.ProGAL.Point v) {
	return geometryfactory.createPoint(v.x(), v.y(), v.z());
    }

    /**
     * 
     *
     * @param points 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Point[] points,
	    final WB_Context2D context) {
	final int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	return getVoronoi2D(coords, context);
    }

    /**
     * 
     *
     * @param points 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getVoronoi2D(
	    final Collection<? extends WB_Point> points,
	    final WB_Context2D context) {
	final int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	return getVoronoi2D(coords, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Point[] points,
	    final double d, final WB_Context2D context) {
	return getVoronoi2D(points, d, 2, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getVoronoi2D(
	    final Collection<? extends WB_Point> points, final double d,
	    final WB_Context2D context) {
	return getVoronoi2D(points, d, 2, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Point[] points,
	    final double d, final int c, final WB_Context2D context) {
	final int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	return getVoronoi2D(coords, d, c, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getVoronoi2D(
	    final Collection<? extends WB_Point> points, final double d,
	    final int c, final WB_Context2D context) {
	final int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	return getVoronoi2D(coords, d, c, context);
    }

    /**
     * 
     *
     * @param points 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final WB_Point[] points, final WB_Context2D context) {
	final int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	return getClippedVoronoi2D(coords, context);
    }

    /**
     * 
     *
     * @param points 
     * @param boundary 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final WB_Point[] points, final WB_Point[] boundary,
	    final WB_Context2D context) {
	int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	n = boundary.length;
	final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    bdcoords.add(toCoordinate(boundary[i], i, context));
	}
	if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
	    bdcoords.add(bdcoords.get(0));
	}
	return getClippedVoronoi2D(coords, bdcoords, context);
    }

    /**
     * 
     *
     * @param points 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final Collection<? extends WB_Point> points,
	    final WB_Context2D context) {
	final int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	return getClippedVoronoi2D(coords, context);
    }

    /**
     * 
     *
     * @param points 
     * @param boundary 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final Collection<? extends WB_Point> points,
	    final Collection<? extends WB_Point> boundary,
	    final WB_Context2D context) {
	int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	n = boundary.size();
	final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
	id = 0;
	for (final WB_Point p : boundary) {
	    bdcoords.add(toCoordinate(p, id, context));
	    id++;
	}
	if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
	    bdcoords.add(bdcoords.get(0));
	}
	return getClippedVoronoi2D(coords, bdcoords, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final WB_Point[] points, final double d, final WB_Context2D context) {
	final int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	return getClippedVoronoi2D(coords, d, 2, context);
    }

    /**
     * 
     *
     * @param points 
     * @param boundary 
     * @param d 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final WB_Point[] points, final WB_Point[] boundary, final double d,
	    final WB_Context2D context) {
	int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	n = boundary.length;
	final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    bdcoords.add(toCoordinate(boundary[i], i, context));
	}
	if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
	    bdcoords.add(bdcoords.get(0));
	}
	return getClippedVoronoi2D(coords, bdcoords, d, 2, context);
    }

    /**
     * 
     *
     * @param points 
     * @param boundary 
     * @param d 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final Collection<? extends WB_Point> points,
	    final Collection<? extends WB_Point> boundary, final double d,
	    final WB_Context2D context) {
	int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	n = boundary.size();
	final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
	id = 0;
	for (final WB_Point p : boundary) {
	    bdcoords.add(toCoordinate(p, id, context));
	    id++;
	}
	if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
	    bdcoords.add(bdcoords.get(0));
	}
	return getClippedVoronoi2D(coords, bdcoords, d, 2, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final Collection<? extends WB_Point> points, final double d,
	    final WB_Context2D context) {
	final int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	return getClippedVoronoi2D(coords, d, 2, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final WB_Point[] points, final double d, final int c,
	    final WB_Context2D context) {
	final int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	return getClippedVoronoi2D(coords, d, c, context);
    }

    /**
     * 
     *
     * @param points 
     * @param boundary 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final WB_Point[] points, final WB_Point[] boundary, final double d,
	    final int c, final WB_Context2D context) {
	int n = points.length;
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    coords.add(toCoordinate(points[i], i, context));
	}
	n = boundary.length;
	final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
	for (int i = 0; i < n; i++) {
	    bdcoords.add(toCoordinate(boundary[i], i, context));
	}
	if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
	    bdcoords.add(bdcoords.get(0));
	}
	return getClippedVoronoi2D(coords, bdcoords, d, c, context);
    }

    /**
     * 
     *
     * @param points 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final Collection<? extends WB_Point> points, final double d,
	    final int c, final WB_Context2D context) {
	final int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	return getClippedVoronoi2D(coords, d, c, context);
    }

    /**
     * 
     *
     * @param points 
     * @param boundary 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final Collection<? extends WB_Point> points,
	    final Collection<? extends WB_Point> boundary, final double d,
	    final int c, final WB_Context2D context) {
	int n = points.size();
	final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
	int id = 0;
	for (final WB_Point p : points) {
	    coords.add(toCoordinate(p, id, context));
	    id++;
	}
	n = boundary.size();
	final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
	id = 0;
	for (final WB_Point p : boundary) {
	    bdcoords.add(toCoordinate(p, id, context));
	    id++;
	}
	if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
	    bdcoords.add(bdcoords.get(0));
	}
	return getClippedVoronoi2D(coords, bdcoords, d, c, context);
    }

    /**
     * 
     *
     * @param coords 
     * @param context 
     * @return 
     */
    private static List<WB_VoronoiCell2D> getVoronoi2D(
	    final ArrayList<Coordinate> coords, final WB_Context2D context) {
	final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
	dtb.setSites(coords);
	final QuadEdgeSubdivision qes = dtb.getSubdivision();
	final GeometryCollection polys = (GeometryCollection) qes
		.getVoronoiDiagram(new GeometryFactory());
	final int npolys = polys.getNumGeometries();
	final List<WB_VoronoiCell2D> result = new FastTable<WB_VoronoiCell2D>();
	for (int i = 0; i < npolys; i++) {
	    final Polygon poly = (Polygon) polys.getGeometryN(i);
	    final Coordinate[] polycoord = poly.getCoordinates();
	    final List<WB_Point> polypoints = new FastTable<WB_Point>();
	    for (final Coordinate element : polycoord) {
		polypoints.add(toPoint(element.x, element.y, context));
	    }
	    final Point centroid = poly.getCentroid();
	    final WB_Point pc = (centroid == null) ? null : toPoint(
		    centroid.getX(), centroid.getY(), context);
	    final int index = (int) ((Coordinate) poly.getUserData()).z;
	    final double area = poly.getArea();
	    result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory
		    .createPoint(coords.get(index)), area, pc));
	}
	return result;
    }

    /**
     * 
     *
     * @param coords 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    private static List<WB_VoronoiCell2D> getVoronoi2D(
	    final ArrayList<Coordinate> coords, final double d, final int c,
	    final WB_Context2D context) {
	final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
	dtb.setSites(coords);
	final QuadEdgeSubdivision qes = dtb.getSubdivision();
	final GeometryCollection polys = (GeometryCollection) qes
		.getVoronoiDiagram(new GeometryFactory());
	final int npolys = polys.getNumGeometries();
	final List<WB_VoronoiCell2D> result = new FastTable<WB_VoronoiCell2D>();
	for (int i = 0; i < npolys; i++) {
	    Geometry poly = polys.getGeometryN(i);
	    poly = poly.buffer(-d, c);
	    poly = polys.getGeometryN(0);
	    final Coordinate[] polycoord = poly.getCoordinates();
	    final List<WB_Point> polypoints = new FastTable<WB_Point>();
	    ;
	    for (final Coordinate element : polycoord) {
		polypoints.add(toPoint(element.x, element.y, context));
	    }
	    final Point centroid = poly.getCentroid();
	    final WB_Point pc = (centroid == null) ? null : toPoint(
		    centroid.getX(), centroid.getY(), context);
	    final int index = (int) ((Coordinate) poly.getUserData()).z;
	    final double area = poly.getArea();
	    result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory
		    .createPoint(coords.get(index)), area, pc));
	}
	return result;
    }

    /**
     * 
     *
     * @param coords 
     * @param context 
     * @return 
     */
    private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final ArrayList<Coordinate> coords, final WB_Context2D context) {
	final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
	dtb.setSites(coords);
	final QuadEdgeSubdivision qes = dtb.getSubdivision();
	final GeometryCollection polys = (GeometryCollection) qes
		.getVoronoiDiagram(new GeometryFactory());
	final int npolys = polys.getNumGeometries();
	final List<WB_VoronoiCell2D> result = new FastTable<WB_VoronoiCell2D>();
	Coordinate[] coordsArray = new Coordinate[coords.size()];
	coordsArray = coords.toArray(coordsArray);
	final ConvexHull ch = new ConvexHull(coordsArray, new GeometryFactory());
	final Geometry hull = ch.getConvexHull();
	for (int i = 0; i < npolys; i++) {
	    Polygon poly = (Polygon) polys.getGeometryN(i);
	    final Geometry intersect = poly.intersection(hull.getGeometryN(0));
	    final double cellindex = ((Coordinate) poly.getUserData()).z;
	    if (intersect.getGeometryType().equals("Polygon")) {
		poly = (Polygon) intersect.getGeometryN(0);
		final Coordinate[] polycoord = poly.getCoordinates();
		final List<WB_Point> polypoints = new FastTable<WB_Point>();
		for (final Coordinate element : polycoord) {
		    polypoints.add(toPoint(element.x, element.y, context));
		}
		final Point centroid = poly.getCentroid();
		final WB_Point pc = (centroid == null) ? null : toPoint(
			centroid.getX(), centroid.getY(), context);
		final int index = (int) cellindex;
		final double area = poly.getArea();
		result.add(new WB_VoronoiCell2D(polypoints, index,
			geometryfactory.createPoint(coords.get(index)), area,
			pc));
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param coords 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final ArrayList<Coordinate> coords, final double d, final int c,
	    final WB_Context2D context) {
	final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
	dtb.setSites(coords);
	final QuadEdgeSubdivision qes = dtb.getSubdivision();
	final GeometryCollection polys = (GeometryCollection) qes
		.getVoronoiDiagram(new GeometryFactory());
	final int npolys = polys.getNumGeometries();
	final List<WB_VoronoiCell2D> result = new FastTable<WB_VoronoiCell2D>();
	Coordinate[] coordsArray = new Coordinate[coords.size()];
	coordsArray = coords.toArray(coordsArray);
	final ConvexHull ch = new ConvexHull(coordsArray, new GeometryFactory());
	final Geometry hull = ch.getConvexHull();
	for (int i = 0; i < npolys; i++) {
	    Polygon poly = (Polygon) polys.getGeometryN(i);
	    Geometry intersect = poly.intersection(hull.getGeometryN(0));
	    intersect = intersect.buffer(-d, c);
	    final double cellindex = ((Coordinate) poly.getUserData()).z;
	    if (intersect.getGeometryType().equals("Polygon")) {
		poly = (Polygon) intersect.getGeometryN(0);
		final Coordinate[] polycoord = poly.getCoordinates();
		final List<WB_Point> polypoints = new FastTable<WB_Point>();
		for (final Coordinate element : polycoord) {
		    polypoints.add(toPoint(element.x, element.y, context));
		}
		final Point centroid = poly.getCentroid();
		final WB_Point pc = (centroid == null) ? null : toPoint(
			centroid.getX(), centroid.getY(), context);
		final int index = (int) cellindex;
		final double area = poly.getArea();
		result.add(new WB_VoronoiCell2D(polypoints, index,
			geometryfactory.createPoint(coords.get(index)), area,
			pc));
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param coords 
     * @param bdcoords 
     * @param context 
     * @return 
     */
    private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final ArrayList<Coordinate> coords,
	    final ArrayList<Coordinate> bdcoords, final WB_Context2D context) {
	final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
	dtb.setSites(coords);
	final QuadEdgeSubdivision qes = dtb.getSubdivision();
	final GeometryCollection polys = (GeometryCollection) qes
		.getVoronoiDiagram(new GeometryFactory());
	final int npolys = polys.getNumGeometries();
	final List<WB_VoronoiCell2D> result = new FastTable<WB_VoronoiCell2D>();
	Coordinate[] bdcoordsArray = new Coordinate[bdcoords.size()];
	bdcoordsArray = bdcoords.toArray(bdcoordsArray);
	final Polygon hull = new GeometryFactory().createPolygon(bdcoordsArray);
	for (int i = 0; i < npolys; i++) {
	    Polygon poly = (Polygon) polys.getGeometryN(i);
	    final Geometry intersect = poly.intersection(hull);
	    final double cellindex = ((Coordinate) poly.getUserData()).z;
	    if (intersect.getGeometryType().equals("Polygon")
		    && !intersect.isEmpty()) {
		poly = (Polygon) intersect.getGeometryN(0);
		final Coordinate[] polycoord = poly.getCoordinates();
		final List<WB_Point> polypoints = new FastTable<WB_Point>();
		for (final Coordinate element : polycoord) {
		    polypoints.add(toPoint(element.x, element.y, context));
		}
		final Point centroid = poly.getCentroid();
		final WB_Point pc = (centroid == null) ? null : toPoint(
			centroid.getX(), centroid.getY(), context);
		final int index = (int) cellindex;
		final double area = poly.getArea();
		result.add(new WB_VoronoiCell2D(polypoints, index,
			geometryfactory.createPoint(coords.get(index)), area,
			pc));
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param coords 
     * @param bdcoords 
     * @param d 
     * @param c 
     * @param context 
     * @return 
     */
    private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
	    final ArrayList<Coordinate> coords,
	    final ArrayList<Coordinate> bdcoords, final double d, final int c,
	    final WB_Context2D context) {
	final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
	dtb.setSites(coords);
	final QuadEdgeSubdivision qes = dtb.getSubdivision();
	final GeometryCollection polys = (GeometryCollection) qes
		.getVoronoiDiagram(new GeometryFactory());
	final int npolys = polys.getNumGeometries();
	final List<WB_VoronoiCell2D> result = new FastTable<WB_VoronoiCell2D>();
	Coordinate[] bdcoordsArray = new Coordinate[bdcoords.size()];
	bdcoordsArray = bdcoords.toArray(bdcoordsArray);
	final Polygon hull = new GeometryFactory().createPolygon(bdcoordsArray);
	for (int i = 0; i < npolys; i++) {
	    Polygon poly = (Polygon) polys.getGeometryN(i);
	    Geometry intersect = poly.intersection(hull);
	    intersect = intersect.buffer(-d, c);
	    final double cellindex = ((Coordinate) poly.getUserData()).z;
	    if (intersect.getGeometryType().equals("Polygon")
		    && !intersect.isEmpty()) {
		poly = (Polygon) intersect.getGeometryN(0);
		final Coordinate[] polycoord = poly.getCoordinates();
		final List<WB_Point> polypoints = new FastTable<WB_Point>();
		;
		for (final Coordinate element : polycoord) {
		    polypoints.add(toPoint(element.x, element.y, context));
		}
		final Point centroid = poly.getCentroid();
		final WB_Point pc = (centroid == null) ? null : toPoint(
			centroid.getX(), centroid.getY(), context);
		final int index = (int) cellindex;
		final double area = poly.getArea();
		result.add(new WB_VoronoiCell2D(polypoints, index,
			geometryfactory.createPoint(coords.get(index)), area,
			pc));
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param p 
     * @param i 
     * @param context 
     * @return 
     */
    private static Coordinate toCoordinate(final WB_Coordinate p, final int i,
	    final WB_Context2D context) {
	final WB_Point tmp = geometryfactory.createPoint();
	context.pointTo2D(p, tmp);
	final Coordinate c = new Coordinate(tmp.xd(), tmp.yd(), i);
	return c;
    }

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param context 
     * @return 
     */
    private static WB_Point toPoint(final double x, final double y,
	    final WB_Context2D context) {
	final WB_Point tmp = geometryfactory.createPoint();
	context.pointTo3D(x, y, 0, tmp);
	return tmp;
    }
}
