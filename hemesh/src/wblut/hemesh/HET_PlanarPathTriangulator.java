/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.List;
import javolution.util.FastTable;
import wblut.geom.WB_Context2D;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Triangulate;
import wblut.geom.WB_Triangulation2DWithPoints;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * 
 */
class HET_PlanarPathTriangulator {
    
    /**
     * 
     */
    private static GeometryFactory JTSgf = new GeometryFactory();
    
    /**
     * 
     */
    public static final HET_ProgressTracker tracker = HET_ProgressTracker
	    .instance();
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     */
    public HET_PlanarPathTriangulator() {
    }

    /**
     * 
     *
     * @param paths 
     * @param P 
     * @return 
     */
    public static long[][] getTriangleKeys(final List<? extends HE_Path> paths,
	    final WB_Plane P) {
	tracker.setDefaultStatus("Starting planar path triangulation.");
	final WB_Context2D emb = geometryfactory.createEmbeddedPlane(P);
	final RingTree ringtree = new RingTree();
	List<HE_Vertex> vertices;
	Coordinate[] pts;
	final WB_KDTree<WB_Point, Long> vertextree = new WB_KDTree<WB_Point, Long>();
	tracker.setDefaultStatus("Building contours tree.");
	for (int i = 0; i < paths.size(); i++) {
	    final HE_Path path = paths.get(i);
	    if (path.isLoop() && (path.getPathOrder() > 2)) {
		vertices = path.getPathVertices();
		pts = new Coordinate[vertices.size() + 1];
		for (int j = 0; j < vertices.size(); j++) {
		    final WB_Point proj = geometryfactory.createPoint();
		    emb.pointTo2D(vertices.get(j), proj);
		    vertextree.add(proj, vertices.get(j).getKey());
		    pts[vertices.size() - j] = new Coordinate(proj.xd(),
			    proj.yd(), 0);
		}
		final WB_Point proj = geometryfactory.createPoint();
		emb.pointTo2D(vertices.get(0), proj);
		pts[0] = new Coordinate(proj.xd(), proj.yd(), 0);
		ringtree.add(JTSgf.createLinearRing(pts));
	    }
	}
	tracker.setDefaultStatus("Extracting polygons from contours tree.");
	final List<WB_Polygon> polygons = ringtree.extractPolygons();
	final List<WB_Coordinate[]> triangles = new FastTable<WB_Coordinate[]>();
	tracker.setDefaultStatus("Triangulating polygons.", polygons.size());
	for (final WB_Polygon poly : polygons) {
	    final int[][] tris = poly.getTriangles();
	    for (int i = 0; i < tris.length; i++) {
		triangles.add(new WB_Coordinate[] { poly.getPoint(tris[i][0]),
			poly.getPoint(tris[i][1]), poly.getPoint(tris[i][2]) });
	    }
	    tracker.incrementCounter();
	}
	final long[][] trianglekeys = new long[triangles.size()][3];
	for (int i = 0; i < triangles.size(); i++) {
	    final WB_Coordinate[] tri = triangles.get(i);
	    final long key0 = vertextree.getNearestNeighbor(tri[0]).value;
	    final long key1 = vertextree.getNearestNeighbor(tri[1]).value;
	    final long key2 = vertextree.getNearestNeighbor(tri[2]).value;
	    // reverse triangles
	    trianglekeys[i] = new long[] { key0, key2, key1 };
	}
	tracker.setDefaultStatus("All paths triangulated.");
	return trianglekeys;
    }

    /**
     * 
     *
     * @param paths 
     * @param P 
     * @return 
     */
    public static HE_Mesh getConstrainedCaps(
	    final List<? extends HE_Path> paths, final WB_Plane P) {
	tracker.setDefaultStatus("Starting planar path triangulation.");
	final WB_Context2D emb = geometryfactory.createEmbeddedPlane(P);
	final RingTree ringtree = new RingTree();
	List<HE_Vertex> vertices;
	Coordinate[] pts;
	final WB_KDTree<WB_Point, Long> vertextree = new WB_KDTree<WB_Point, Long>();
	tracker.setDefaultStatus("Building contours tree.");
	for (int i = 0; i < paths.size(); i++) {
	    final HE_Path path = paths.get(i);
	    if (path.isLoop() && (path.getPathOrder() > 2)) {
		vertices = path.getPathVertices();
		pts = new Coordinate[vertices.size() + 1];
		for (int j = 0; j < vertices.size(); j++) {
		    final WB_Point proj = geometryfactory.createPoint();
		    emb.pointTo2D(vertices.get(j), proj);
		    vertextree.add(proj, vertices.get(j).getKey());
		    pts[vertices.size() - j] = new Coordinate(proj.xd(),
			    proj.yd(), 0);
		}
		final WB_Point proj = geometryfactory.createPoint();
		emb.pointTo2D(vertices.get(0), proj);
		pts[0] = new Coordinate(proj.xd(), proj.yd(), 0);
		ringtree.add(JTSgf.createLinearRing(pts));
	    }
	}
	tracker.setDefaultStatus("Extracting polygons from contours tree.");
	final List<WB_Polygon> polygons = ringtree.extractPolygons();
	tracker.setDefaultStatus("Triangulating polygons.", polygons.size());
	final HE_Mesh cap = new HE_Mesh();
	for (final WB_Polygon poly : polygons) {
	    final WB_Triangulation2DWithPoints tri = WB_Triangulate
		    .getConformingTriangulation2D(poly);
	    cap.add(toMesh(tri));
	    tracker.incrementCounter();
	}
	final HE_VertexIterator vitr = cap.vItr();
	HE_Vertex v;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    emb.pointTo3D(v.xd(), v.yd(), v);
	}
	tracker.setDefaultStatus("All paths triangulated.");
	return cap;
    }

    /**
     * 
     *
     * @param tri 
     * @return 
     */
    static HE_Mesh toMesh(final WB_Triangulation2DWithPoints tri) {
	final HEC_FromFacelist ffl = new HEC_FromFacelist().setFaces(
		tri.getTriangles()).setVertices(tri.getPoints());
	return new HE_Mesh(ffl);
    }

    // The JTS implementation of ShapeReader does not handle overlapping
    // polygons well. All code below this point is my solution for this. A
    // hierarchical tree that orders rings from the outside in. All input has to
    // be well-ordered: CCW for shell, CW for hole.
    /**
     * 
     */
    private static class RingNode {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	RingNode parent;
	
	/**
	 * 
	 */
	List<RingNode> children;
	
	/**
	 * 
	 */
	LinearRing ring;
	
	/**
	 * 
	 */
	Polygon poly;// redundant, but useful for within/contains checks
	
	/**
	 * 
	 */
	boolean hole;

	/**
	 * 
	 */
	RingNode() {
	    parent = null;
	    ring = null;
	    children = new ArrayList<RingNode>();
	    hole = true;
	}

	/**
	 * 
	 *
	 * @param parent 
	 * @param ring 
	 */
	RingNode(final RingNode parent, final LinearRing ring) {
	    this.parent = parent;
	    this.ring = ring;
	    final Coordinate[] coords = ring.getCoordinates();
	    poly = JTSgf.createPolygon(coords);
	    hole = !CGAlgorithms.isCCW(coords);
	    children = new ArrayList<RingNode>();
	}
    }

    /**
     * 
     */
    private static class RingTree {
	
	/**
	 * 
	 */
	RingNode root;

	/**
	 * 
	 */
	RingTree() {
	    root = new RingNode();
	}

	/**
	 * 
	 *
	 * @param ring 
	 */
	void add(final LinearRing ring) {
	    final Polygon poly = JTSgf.createPolygon(ring);
	    RingNode currentParent = root;
	    RingNode foundParent;
	    do {
		foundParent = null;
		for (int i = 0; i < currentParent.children.size(); i++) {
		    final RingNode node = currentParent.children.get(i);
		    final Polygon other = node.poly;
		    if (poly.within(other)) {
			foundParent = node;
			currentParent = node;
			break;
		    }
		}
	    } while (foundParent != null);
	    final RingNode newNode = new RingNode(currentParent, ring);
	    final List<RingNode> nodesToRemove = new ArrayList<RingNode>();
	    for (int i = 0; i < currentParent.children.size(); i++) {
		final RingNode node = currentParent.children.get(i);
		final Polygon other = node.poly;
		if (other.within(poly)) {
		    newNode.children.add(node);
		    nodesToRemove.add(node);
		}
	    }
	    currentParent.children.removeAll(nodesToRemove);
	    currentParent.children.add(newNode);
	}

	/**
	 * 
	 *
	 * @return 
	 */
	List<WB_Polygon> extractPolygons() {
	    final List<WB_Polygon> polygons = new ArrayList<WB_Polygon>();
	    final List<RingNode> shellNodes = new ArrayList<RingNode>();
	    addExteriorNodes(root, shellNodes);
	    for (final RingNode node : shellNodes) {
		int count = 0;
		for (int i = 0; i < node.children.size(); i++) {
		    if (node.children.get(i).hole) {
			count++;
		    }
		}
		final LinearRing[] holes = new LinearRing[count];
		int index = 0;
		for (int i = 0; i < node.children.size(); i++) {
		    if (node.children.get(i).hole) {
			holes[index++] = node.children.get(i).ring;
		    }
		}
		final Geometry result = JTSgf.createPolygon(node.ring, holes);
		if (result.getGeometryType().equals("Polygon")) {
		    polygons.add(geometryfactory
			    .createPolygonFromJTSPolygon((Polygon) result));
		} else if (result.getGeometryType().equals("MultiPolygon")) {
		    for (int j = 0; j < result.getNumGeometries(); j++) {
			final Geometry ggeo = result.getGeometryN(j);
			polygons.add(geometryfactory
				.createPolygonFromJTSPolygon((Polygon) ggeo));
		    }
		}
	    }
	    return polygons;
	}

	/**
	 * 
	 *
	 * @param parent 
	 * @param shellNodes 
	 */
	void addExteriorNodes(final RingNode parent,
		final List<RingNode> shellNodes) {
	    for (final RingNode node : parent.children) {
		if (node.hole == false) {
		    shellNodes.add(node);
		}
		addExteriorNodes(node, shellNodes);
	    }
	}
    }
}
