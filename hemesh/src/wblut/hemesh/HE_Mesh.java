/*
 *
 */
package wblut.hemesh;

import static wblut.math.WB_Epsilon.isZero;
import static wblut.math.WB_Epsilon.isZeroSq;
import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import javolution.util.FastTable;
import wblut.geom.WB_AABB;
import wblut.geom.WB_ClassificationConvex;
import wblut.geom.WB_ClassificationGeometry;
import wblut.geom.WB_Classify;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateSequence;
import wblut.geom.WB_FaceListMesh;
import wblut.geom.WB_Frame;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_GeometryType;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_HasData;
import wblut.geom.WB_IndexedSegment;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_MeshCreator;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 * Half-edge mesh data structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Mesh extends HE_MeshStructure implements WB_HasData,
WB_HasColor, WB_Mesh {
    /**
     *
     */
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    /**
     *
     */
    private WB_Point center;
    /**
     *
     */
    private boolean isCenterUpdated;
    /**
     *
     */
    protected int label;
    /**
     *
     */
    private HashMap<String, Object> data;
    /**
     *
     */
    private int meshcolor;

    /**
     * Instantiates a new HE_Mesh.
     *
     */
    public HE_Mesh() {
	super();
	center = new WB_Point();
	isCenterUpdated = false;
	label = -1;
    }

    /**
     * Constructor.
     *
     * @param creator
     *            HE_Creator that generates this mesh
     */
    public HE_Mesh(final HEC_Creator creator) {
	super();
	setNoCopy(creator.create());
	isCenterUpdated = false;
	label = -1;
    }

    /**
     *
     *
     * @param mesh
     */
    public HE_Mesh(final WB_Mesh mesh) {
	this(new HEC_FromMesh(mesh));
    }

    /**
     *
     *
     * @param mesh
     */
    public HE_Mesh(final WB_MeshCreator mesh) {
	this(new HEC_FromMesh(mesh.getMesh()));
    }

    /**
     * Modify the mesh.
     *
     * @param modifier
     *            HE_Modifier to apply
     * @return self
     */
    public HE_Mesh modify(final HEM_Modifier modifier) {
	return modifier.apply(this);
    }

    /**
     * Modify selection. Elements should be part of this mesh.
     *
     * @param modifier
     *            HE_Modifier to apply
     * @param selection
     *            the selection
     * @return self
     */
    public HE_Mesh modifySelected(final HEM_Modifier modifier,
	    final HE_Selection selection) {
	return modifier.apply(selection);
    }

    /**
     * Subdivide the mesh.
     *
     * @param subdividor
     *            HE_Subdividor to apply
     * @return self
     */
    public HE_Mesh subdivide(final HES_Subdividor subdividor) {
	return subdividor.apply(this);
    }

    /**
     * Subdivide selection of the mesh.
     *
     * @param subdividor
     *            HE_Subdividor to apply
     * @param selection
     *            HE_Selection
     * @return self
     */
    public HE_Mesh subdivideSelected(final HES_Subdividor subdividor,
	    final HE_Selection selection) {
	return subdividor.apply(selection);
    }

    /**
     * Subdivide the mesh a number of times.
     *
     * @param subdividor
     *            HE_Subdividor to apply
     * @param rep
     *            subdivision iterations. WARNING: higher values will lead to
     *            unmanageable number of faces.
     * @return self
     */
    public HE_Mesh subdivide(final HES_Subdividor subdividor, final int rep) {
	for (int i = 0; i < rep; i++) {
	    subdivide(subdividor);
	}
	return this;
    }

    /**
     * Subdivide a selection of the mesh a number of times.
     *
     * @param subdividor
     *            HE_Subdividor to apply
     * @param selection
     *            HE_Selection initial selection
     * @param rep
     *            subdivision iterations
     * @return self
     */
    public HE_Mesh subdivideSelected(final HES_Subdividor subdividor,
	    final HE_Selection selection, final int rep) {
	for (int i = 0; i < rep; i++) {
	    subdivideSelected(subdividor, selection);
	}
	return this;
    }

    /**
     * Simplify.
     *
     * @param simplifier
     *            the simplifier
     * @return the h e_ mesh
     */
    public HE_Mesh simplify(final HES_Simplifier simplifier) {
	return simplifier.apply(this);
    }

    /**
     * Simplify.
     *
     * @param simplifier
     *            the simplifier
     * @param selection
     *            the selection
     * @return the h e_ mesh
     */
    public HE_Mesh simplifySelected(final HES_Simplifier simplifier,
	    final HE_Selection selection) {
	return simplifier.apply(selection);
    }

    /**
     * Deep copy of mesh.
     *
     * @return copy as new HE_Mesh
     */
    public HE_Mesh get() {
	return new HE_Mesh(new HEC_Copy(this));
    }

    /**
     * Add all mesh elements to this mesh. No copies are made.
     *
     * @param mesh
     *            mesh to add
     */
    public void add(final HE_Mesh mesh) {
	addVertices(mesh);
	addFaces(mesh);
	addHalfedges(mesh);
    }

    /**
     * Add all mesh elements to this mesh. No copies are made. Tries to join
     * geometry.
     *
     * @param mesh
     *            mesh to add
     */
    public void fuse(final HE_Mesh mesh) {
	addVertices(mesh.getVerticesAsArray());
	addFaces(mesh.getFacesAsArray());
	addHalfedges(mesh.getHalfedgesAsArray());
	setNoCopy(new HE_Mesh(new HEC_FromPolygons().setPolygons(this
		.getPolygonList())));
    }

    /**
     * Replace mesh with deep copy of target.
     *
     * @param target
     *            HE_Mesh to be duplicated
     */
    public void set(final HE_Mesh target) {
	final HE_Mesh result = target.get();
	replaceVertices(result);
	replaceFaces(result);
	replaceHalfedges(result);
    }

    /**
     * Replace mesh with shallow copy of target.
     *
     * @param target
     *            HE_Mesh to be duplicated
     */
    void setNoCopy(final HE_Mesh target) {
	vertices = target.vertices;
	halfedges = target.halfedges;
	faces = target.faces;
	center = target.center;
	isCenterUpdated = target.isCenterUpdated;
    }

    /**
     * Return all vertex positions as an array .
     *
     * @return 2D array of float. First index gives vertex. Second index gives
     *         x-,y- or z-coordinate.
     */
    public float[][] getVerticesAsFloat() {
	final float[][] result = new float[getNumberOfVertices()][3];
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    result[i][0] = v.xf();
	    result[i][1] = v.yf();
	    result[i][2] = v.zf();
	    i++;
	}
	return result;
    }

    /**
     * Return all vertex positions as an array .
     *
     * @return 2D array of double. First index gives vertex. Second index gives
     *         x-,y- or z-coordinate.
     */
    public double[][] getVerticesAsDouble() {
	final double[][] result = new double[getNumberOfVertices()][3];
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    result[i][0] = v.xd();
	    result[i][1] = v.yd();
	    result[i][2] = v.zd();
	    i++;
	}
	return result;
    }

    /**
     * Vertex key to index.
     *
     * @return the map
     */
    public Map<Long, Integer> vertexKeyToIndex() {
	final Map<Long, Integer> map = new FastMap<Long, Integer>();
	int i = 0;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    map.put(vItr.next().key(), i);
	    i++;
	}
	return map;
    }

    /**
     * Return all vertex positions.
     *
     * @return array of WB_Point, values are copied.
     */
    public WB_Point[] getVerticesAsNewPoint() {
	final WB_Point[] result = new WB_Point[getNumberOfVertices()];
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    result[i] = new WB_Point(v);
	    i++;
	}
	return result;
    }

    /**
     * Return all vertex positions.
     *
     * @return array of WB_Cooridnate, no copies are made.
     */
    public WB_Point[] getVerticesAsPoint() {
	final WB_Point[] result = new WB_Point[getNumberOfVertices()];
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    result[i] = v.getPoint();
	    i++;
	}
	return result;
    }

    /**
     * Return all vertex normal.
     *
     * @return array of WB_Vector.
     */
    public WB_Vector[] getVertexNormals() {
	final WB_Vector[] result = new WB_Vector[getNumberOfVertices()];
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    result[i] = v.getVertexNormal();
	    i++;
	}
	return result;
    }

    /**
     * Return all vertex normal.
     *
     * @return FastMap of WB_Vector.
     */
    public Map<Long, WB_Vector> getKeyedVertexNormals() {
	final Map<Long, WB_Vector> result = new FastMap<Long, WB_Vector>();
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    result.put(v.key(), v.getVertexNormal());
	}
	return result;
    }

    /**
     * Return the faces as array of vertex indices.
     *
     * @return 2D array of int. First index gives face. Second index gives
     *         vertices.
     */
    @Override
    public int[][] getFacesAsInt() {
	final int[][] result = new int[getNumberOfFaces()][];
	final TLongIntMap vertexKeys = new TLongIntHashMap(10, 0.5f, -1L, -1);
	final Iterator<HE_Vertex> vItr = vItr();
	int i = 0;
	while (vItr.hasNext()) {
	    vertexKeys.put(vItr.next().key(), i);
	    i++;
	}
	final Iterator<HE_Face> fItr = fItr();
	HE_Halfedge he;
	HE_Face f;
	i = 0;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result[i] = new int[f.getFaceOrder()];
	    he = f.getHalfedge();
	    int j = 0;
	    do {
		result[i][j] = vertexKeys.get(he.getVertex().key());
		he = he.getNextInFace();
		j++;
	    } while (he != f.getHalfedge());
	    i++;
	}
	return result;
    }

    /**
     * Return all face normals.
     *
     * @return array of WB_Vector.
     */
    public WB_Vector[] getFaceNormals() {
	final WB_Vector[] result = new WB_Vector[getNumberOfFaces()];
	int i = 0;
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result[i] = f.getFaceNormal();
	    i++;
	}
	return result;
    }

    /**
     * Return all face normals.
     *
     * @return FastMap of WB_Vector.
     */
    public Map<Long, WB_Vector> getKeyedFaceNormals() {
	final Map<Long, WB_Vector> result = new FastMap<Long, WB_Vector>();
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result.put(f.key(), f.getFaceNormal());
	}
	return result;
    }

    /**
     * Return all face centers.
     *
     * @return array of WB_Point.
     */
    public WB_Point[] getFaceCenters() {
	final WB_Point[] result = new WB_Point[getNumberOfFaces()];
	int i = 0;
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result[i] = f.getFaceCenter();
	    i++;
	}
	return result;
    }

    /**
     * Return all face centers.
     *
     * @return FastMap of WB_Point.
     */
    public Map<Long, WB_Point> getKeyedFaceCenters() {
	final Map<Long, WB_Point> result = new FastMap<Long, WB_Point>();
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result.put(f.key(), f.getFaceCenter());
	}
	return result;
    }

    /**
     * Return all edge normals.
     *
     * @return array of WB_Vector.
     */
    public WB_Vector[] getEdgeNormals() {
	final WB_Vector[] result = new WB_Vector[getNumberOfEdges()];
	int i = 0;
	HE_Halfedge e;
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    e = eItr.next();
	    result[i] = e.getEdgeNormal();
	    i++;
	}
	return result;
    }

    /**
     * Return all edge normals.
     *
     * @return FastMap of WB_Vector.
     */
    public Map<Long, WB_Vector> getKeyedEdgeNormals() {
	final Map<Long, WB_Vector> result = new FastMap<Long, WB_Vector>();
	HE_Halfedge e;
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    e = eItr.next();
	    result.put(e.key(), e.getEdgeNormal());
	}
	return result;
    }

    /**
     * Return all edge centers.
     *
     * @return array of WB_Point.
     */
    public WB_Point[] getEdgeCenters() {
	final WB_Point[] result = new WB_Point[getNumberOfEdges()];
	int i = 0;
	HE_Halfedge e;
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    e = eItr.next();
	    result[i] = e.getHalfedgeCenter();
	    i++;
	}
	return result;
    }

    /**
     * Return all edge centers.
     *
     * @return FastMap of WB_Point.
     */
    public Map<Long, WB_Point> getKeyedEdgeCenters() {
	final Map<Long, WB_Point> result = new FastMap<Long, WB_Point>();
	HE_Halfedge e;
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    e = eItr.next();
	    result.put(e.key(), e.getHalfedgeCenter());
	}
	return result;
    }

    /**
     *
     *
     * @return
     */
    public WB_FaceListMesh toFaceListMesh() {
	return WB_GeometryFactory.instance().createMesh(getVerticesAsPoint(),
		getFacesAsInt());
    }

    /**
     * Set vertex positions to values in array.
     *
     * @param values
     *            2D array of float. First index is number of vertices, second
     *            index is 3 (x-,y- and z-coordinate)
     */
    public void setVerticesFromFloat(final float[][] values) {
	int i = 0;
	center.set(0, 0, 0);
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(values[i][0], values[i][1], values[i][2]);
	    i++;
	}
    }

    /**
     * Set vertex positions to values in array.
     *
     * @param values
     *            array of WB_Point.
     */
    public void setVerticesFromPoint(final WB_Point[] values) {
	int i = 0;
	center.set(0, 0, 0);
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(values[i]);
	    i++;
	}
	;
    }

    /**
     * Set vertex positions to values in array.
     *
     * @param values
     *            2D array of double. First index is number of vertices, second
     *            index is 3 (x-,y- and z-coordinate)
     */
    public void setVerticesFromDouble(final double[][] values) {
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(values[i][0], values[i][1], values[i][2]);
	    i++;
	}
	;
    }

    /**
     * Set vertex positions to values in array.
     *
     * @param values
     *            2D array of int. First index is number of vertices, second
     *            index is 3 (x-,y- and z-coordinate)
     */
    public void setVerticesFromInt(final int[][] values) {
	int i = 0;
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(values[i][0], values[i][1], values[i][2]);
	    i++;
	}
	;
    }

    /**
     * Return the mesh as polygon soup.
     *
     * @return array of WB_polygon
     *
     */
    public WB_Polygon[] getPolygons() {
	final WB_Polygon[] result = new WB_Polygon[getNumberOfFaces()];
	final Iterator<HE_Face> fItr = fItr();
	HE_Face f;
	int i = 0;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result[i] = f.toPolygon();
	    i++;
	}
	return result;
    }

    /**
     * Gets the polygon list.
     *
     * @return the polygon list
     */
    public List<WB_Polygon> getPolygonList() {
	final List<WB_Polygon> result = new FastTable<WB_Polygon>();
	final Iterator<HE_Face> fItr = fItr();
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result.add(f.toPolygon());
	}
	return result;
    }

    /**
     *
     *
     * @return
     */
    public List<WB_Triangle> getTriangles() {
	final List<WB_Triangle> result = new FastTable<WB_Triangle>();
	final HE_Mesh trimesh = this.get();
	trimesh.triangulate();
	final Iterator<HE_Face> fItr = trimesh.fItr();
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    result.add(WB_GeometryFactory.instance().createTriangle(
		    f.getHalfedge().getVertex(),
		    f.getHalfedge().getNextInFace().getVertex(),
		    f.getHalfedge().getPrevInFace().getVertex()));
	}
	return result;
    }

    /**
     * Gets the segments.
     *
     * @return the segments
     */
    public WB_Segment[] getSegments() {
	final WB_Segment[] result = new WB_Segment[getNumberOfEdges()];
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge e;
	int i = 0;
	while (eItr.hasNext()) {
	    e = eItr.next();
	    result[i] = new WB_Segment(e.getVertex(), e.getEndVertex());
	    i++;
	}
	return result;
    }

    /**
     * Gets the indexed segments.
     *
     * @return the indexed segments
     */
    public WB_IndexedSegment[] getIndexedSegments() {
	final WB_IndexedSegment[] result = new WB_IndexedSegment[getNumberOfEdges()];
	final WB_Point[] points = getVerticesAsPoint();
	final TLongIntMap map = new TLongIntHashMap(10, 0.5f, -1L, -1);
	map.putAll(vertexKeyToIndex());
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge e;
	int i = 0;
	while (eItr.hasNext()) {
	    e = eItr.next();
	    result[i] = new WB_IndexedSegment(map.get(e.getVertex().key()),
		    map.get(e.getEndVertex().key()), points);
	    i++;
	}
	return result;
    }

    /**
     * Gets the frame.
     *
     * @return the frame
     */
    public WB_Frame getFrame() {
	final WB_Frame frame = new WB_Frame(getVerticesAsPoint());
	final TLongIntMap map = new TLongIntHashMap(10, 0.5f, -1L, -1);
	map.putAll(vertexKeyToIndex());
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge e;
	while (eItr.hasNext()) {
	    e = eItr.next();
	    frame.addStrut(map.get(e.getVertex().key()),
		    map.get(e.getEndVertex().key()));
	}
	return frame;
    }

    /**
     * Apply transform to entire mesh.
     *
     * @param T
     *            WB_Transform to apply
     *
     * @return self
     */
    public HE_Mesh transform(final WB_Transform T) {
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    T.applySelfAsPoint(vItr.next());
	}
	return this;
    }

    /**
     * Translate entire mesh.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @return self
     */
    public HE_Mesh move(final double x, final double y, final double z) {
	center.addSelf(x, y, z);
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    vItr.next().getPoint().addSelf(x, y, z);
	}
	return this;
    }

    /**
     * Translate entire mesh.
     *
     * @param v
     *            the v
     * @return self
     */
    public HE_Mesh move(final WB_Coordinate v) {
	return move(v.xd(), v.yd(), v.zd());
    }

    /**
     * Translate entire mesh to given position.
     *
     * @param x
     *            the x
     * @param y
     *            the y
     * @param z
     *            the z
     * @return self
     */
    public HE_Mesh moveTo(final double x, final double y, final double z) {
	if (!isCenterUpdated) {
	    getCenter();
	}
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    vItr.next().getPoint()
	    .addSelf(x - center.xd(), y - center.yd(), z - center.zd());
	}
	center.set(x, y, z);
	return this;
    }

    /**
     * Translate entire mesh to given position.
     *
     * @param v
     *            the v
     * @return self
     */
    public HE_Mesh moveTo(final WB_Coordinate v) {
	return moveTo(v.xd(), v.yd(), v.zd());
    }

    /**
     * Rotate entire mesh around an arbitrary axis.
     *
     * @param angle
     *            angle
     * @param p1x
     *            x-coordinate of first point on axis
     * @param p1y
     *            y-coordinate of first point on axis
     * @param p1z
     *            z-coordinate of first point on axis
     * @param p2x
     *            x-coordinate of second point on axis
     * @param p2y
     *            y-coordinate of second point on axis
     * @param p2z
     *            z-coordinate of second point on axis
     * @return self
     */
    public HE_Mesh rotateAboutAxis(final double angle, final double p1x,
	    final double p1y, final double p1z, final double p2x,
	    final double p2y, final double p2z) {
	if (!isCenterUpdated) {
	    getCenter();
	}
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z),
		new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
	while (vItr.hasNext()) {
	    v = vItr.next();
	    raa.applySelfAsPoint(v);
	}
	raa.applySelfAsPoint(center);
	return this;
    }

    /**
     * Rotate entire mesh around an arbitrary axis.
     *
     * @param angle
     *            angle
     * @param p1
     *            first point on axis
     * @param p2
     *            second point on axis
     * @return self
     */
    public HE_Mesh rotateAbout2PointAxis(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	if (!isCenterUpdated) {
	    getCenter();
	}
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
	while (vItr.hasNext()) {
	    v = vItr.next();
	    raa.applySelfAsPoint(v);
	}
	raa.applySelfAsPoint(center);
	return this;
    }

    /**
     * Rotate entire mesh around an arbitrary axis.
     *
     * @param angle
     *            angle
     * @param p
     *            rotation point
     * @param a
     *            axis
     * @return self
     */
    public HE_Mesh rotateAboutAxis(final double angle, final WB_Coordinate p,
	    final WB_Coordinate a) {
	if (!isCenterUpdated) {
	    getCenter();
	}
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p, a);
	while (vItr.hasNext()) {
	    v = vItr.next();
	    raa.applySelfAsPoint(v);
	}
	raa.applySelfAsPoint(center);
	;
	return this;
    }

    /**
     * Scale entire mesh around center point.
     *
     * @param scaleFactorx
     *            x-coordinate of scale factor
     * @param scaleFactory
     *            y-coordinate of scale factor
     * @param scaleFactorz
     *            z-coordinate of scale factor
     * @param c
     *            center
     * @return self
     */
    public HE_Mesh scale(final double scaleFactorx, final double scaleFactory,
	    final double scaleFactorz, final WB_Point c) {
	if (!isCenterUpdated) {
	    getCenter();
	}
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(c.xd() + (scaleFactorx * (v.xd() - c.xd())), c.yd()
		    + (scaleFactory * (v.yd() - c.yd())), c.zd()
		    + (scaleFactorz * (v.zd() - c.zd())));
	}
	center.set(c.xd() + (scaleFactorx * (-c.xd() + center.xd())), c.yd()
		+ (scaleFactory * (-c.yd() + center.yd())), c.zd()
		+ (scaleFactorz * (-c.zd() + center.zd())));
	;
	return this;
    }

    /**
     * Scale entire mesh around center point.
     *
     * @param scaleFactor
     *            scale
     * @param c
     *            center
     * @return self
     */
    public HE_Mesh scale(final double scaleFactor, final WB_Point c) {
	return scale(scaleFactor, scaleFactor, scaleFactor, c);
    }

    /**
     * Scale entire mesh around bodycenter.
     *
     * @param scaleFactorx
     *            x-coordinate of scale factor
     * @param scaleFactory
     *            y-coordinate of scale factor
     * @param scaleFactorz
     *            z-coordinate of scale factor
     * @return self
     */
    public HE_Mesh scale(final double scaleFactorx, final double scaleFactory,
	    final double scaleFactorz) {
	if (!isCenterUpdated) {
	    getCenter();
	}
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(center.xd() + (scaleFactorx * (v.xd() - center.xd())),
		    center.yd() + (scaleFactory * (v.yd() - center.yd())),
		    center.zd() + (scaleFactorz * (v.zd() - center.zd())));
	}
	;
	return this;
    }

    /**
     * Scale entire mesh around bodycenter.
     *
     * @param scaleFactor
     *            scale
     * @return self
     */
    public HE_Mesh scale(final double scaleFactor) {
	return scale(scaleFactor, scaleFactor, scaleFactor);
    }

    /**
     * Get the center (average of all vertex positions).
     *
     * @return the center
     */
    @Override
    public WB_Point getCenter() {
	if (isCenterUpdated) {
	    return center;
	} else {
	    resetCenter();
	    return center;
	}
    }

    /**
     * Reset the center to the average of all vertex positions).
     *
     */
    public void resetCenter() {
	center.set(0, 0, 0);
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    center.addSelf(vItr.next());
	}
	center.divSelf(getNumberOfVertices());
	isCenterUpdated = true;
    }

    /**
     * Assign face to halfedge loop.
     *
     * @param face
     *            face
     * @param halfedge
     *            halfedge loop
     */
    protected static void assignFaceToLoop(final HE_Face face,
	    final HE_Halfedge halfedge) {
	HE_Halfedge he = halfedge;
	do {
	    he.setFace(face);
	    he = he.getNextInFace();
	} while (he != halfedge);
    }

    /**
     * Cycle halfedges.
     *
     * @param halfedges
     *            halfedges to cycle
     */
    public static void cycleHalfedges(final List<HE_Halfedge> halfedges) {
	HE_Halfedge he;
	final int n = halfedges.size();
	if (n > 0) {
	    for (int j = 0; j < (n - 1); j++) {
		he = halfedges.get(j);
		he.setNext(halfedges.get(j + 1));
		halfedges.get(j + 1).setPrev(he);
	    }
	    he = halfedges.get(n - 1);
	    halfedges.get(0).setPrev(he);
	    he.setNext(halfedges.get(0));
	}
    }

    /**
     * Cycle halfedges.
     *
     * @param halfedges
     *            halfedges to cycle
     */
    public static void cycleHalfedgesReverse(final List<HE_Halfedge> halfedges) {
	HE_Halfedge he;
	final int n = halfedges.size();
	if (n > 0) {
	    he = halfedges.get(0);
	    he.setNext(halfedges.get(n - 1));
	    halfedges.get(n - 1).setPrev(he);
	    for (int j = 1; j < n; j++) {
		he = halfedges.get(j);
		he.setNext(halfedges.get(j - 1));
		halfedges.get(j - 1).setPrev(he);
	    }
	}
    }

    /**
     * Collect all unpaired halfedges.
     *
     * @return the unpaired halfedges
     */
    public List<HE_Halfedge> getUnpairedHalfedges() {
	tracker.setDefaultStatus("Collecting unpaired halfedges.",
		getNumberOfHalfedges());
	final List<HE_Halfedge> unpairedHalfedges = new FastTable<HE_Halfedge>();
	HE_Halfedge he;
	final Iterator<HE_Halfedge> heItr = heItr();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getPair() == null) {
		unpairedHalfedges.add(he);
	    }
	    tracker.incrementCounter();
	}
	return unpairedHalfedges;
    }

    /**
     * Collect all boundary halfedges.
     *
     * @return boundary halfedges
     */
    public List<HE_Halfedge> getBoundaryHalfedges() {
	final List<HE_Halfedge> boundaryHalfedges = new FastTable<HE_Halfedge>();
	HE_Halfedge he;
	final Iterator<HE_Halfedge> heItr = heItr();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		boundaryHalfedges.add(he);
	    }
	}
	return boundaryHalfedges;
    }

    /**
     * Try to pair all unpaired halfedges.
     */
    public void pairHalfedges() {
	tracker.setDefaultStatus("Pairing halfedges.");
	class VertexInfo {
	    FastTable<HE_Halfedge> out;
	    FastTable<HE_Halfedge> in;

	    VertexInfo() {
		out = new FastTable<HE_Halfedge>();
		in = new FastTable<HE_Halfedge>();
	    }
	}
	final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(
		1024, 0.5f, -1L);
	final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
	HE_Vertex v;
	VertexInfo vi;
	tracker.setDefaultStatus("Classifying unpaired halfedges.",
		unpairedHalfedges.size());
	for (final HE_Halfedge he : unpairedHalfedges) {
	    v = he.getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.out.add(he);
	    v = he.getNextInFace().getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.in.add(he);
	    tracker.incrementCounter();
	}
	HE_Halfedge he;
	HE_Halfedge he2;
	tracker.setDefaultStatus("Pairing unpaired halfedges per vertex.",
		vertexLists.size());
	final TLongObjectIterator<VertexInfo> vitr = vertexLists.iterator();
	VertexInfo vInfo;
	while (vitr.hasNext()) {
	    vitr.advance();
	    vInfo = vitr.value();
	    for (int i = 0; i < vInfo.out.size(); i++) {
		he = vInfo.out.get(i);
		if (he.getPair() == null) {
		    for (int j = 0; j < vInfo.in.size(); j++) {
			he2 = vInfo.in.get(j);
			if ((he2.getPair() == null)
				&& (he.getVertex() == he2.getNextInFace()
				.getVertex())
				&& (he2.getVertex() == he.getNextInFace()
				.getVertex())) {
			    he.setPair(he2);
			    he2.setPair(he);
			    break;
			}
		    }
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Processed unpaired halfedges.");
    }

    /**
     * Pair halfedges.
     *
     * @param unpairedHalfedges
     *            the unpaired halfedges
     */
    public void pairHalfedges(final List<HE_Halfedge> unpairedHalfedges) {
	tracker.setDefaultStatus("Pairing halfedges.");
	class VertexInfo {
	    FastTable<HE_Halfedge> out;
	    FastTable<HE_Halfedge> in;

	    VertexInfo() {
		out = new FastTable<HE_Halfedge>();
		in = new FastTable<HE_Halfedge>();
	    }
	}
	final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(
		1024, 0.5f, -1L);
	HE_Vertex v;
	VertexInfo vi;
	tracker.setDefaultStatus("Classifying unpaired halfedges.",
		unpairedHalfedges.size());
	for (final HE_Halfedge he : unpairedHalfedges) {
	    v = he.getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.out.add(he);
	    v = he.getNextInFace().getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.in.add(he);
	    tracker.incrementCounter();
	}
	HE_Halfedge he;
	HE_Halfedge he2;
	tracker.setDefaultStatus("Pairing unpaired halfedges per vertex.",
		vertexLists.size());
	final TLongObjectIterator<VertexInfo> vitr = vertexLists.iterator();
	VertexInfo vInfo;
	while (vitr.hasNext()) {
	    vitr.advance();
	    vInfo = vitr.value();
	    for (int i = 0; i < vInfo.out.size(); i++) {
		he = vInfo.out.get(i);
		if (he.getPair() == null) {
		    for (int j = 0; j < vInfo.in.size(); j++) {
			he2 = vInfo.in.get(j);
			if ((he2.getPair() == null)
				&& (he.getVertex() == he2.getNextInFace()
				.getVertex())
				&& (he2.getVertex() == he.getNextInFace()
				.getVertex())) {
			    he.setPair(he2);
			    he2.setPair(he);
			    break;
			}
		    }
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Processed unpaired halfedges.");
    }

    /**
     * Cap all remaining unpaired halfedges. Only use after pairHalfedges();
     */
    public void capHalfedges() {
	final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
	final int nuh = unpairedHalfedges.size();
	final HE_Halfedge[] newHalfedges = new HE_Halfedge[nuh];
	HE_Halfedge he1, he2;
	tracker.setDefaultStatus("Capping unpaired halfedges.", nuh);
	for (int i = 0; i < nuh; i++) {
	    he1 = unpairedHalfedges.get(i);
	    he2 = new HE_Halfedge();
	    he2.setVertex(he1.getNextInFace().getVertex());
	    he1.setPair(he2);
	    he2.setPair(he1);
	    newHalfedges[i] = he2;
	    add(he2);
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Cycling new halfedges.", nuh);
	for (int i = 0; i < nuh; i++) {
	    he1 = newHalfedges[i];
	    if (he1.getNextInFace() == null) {
		for (int j = 0; j < nuh; j++) {
		    he2 = newHalfedges[j];
		    if (he2.getVertex() == he1.getPair().getVertex()) {
			he1.setNext(he2);
			he2.setPrev(he1);
			break;
		    }
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Processed unpaired halfedges.");
    }

    /**
     * Uncap halfedges.
     */
    public void uncapBoundaryHalfedges() {
	tracker.setDefaultStatus("Detecting and uncapping  boundary edges.",
		getNumberOfHalfedges());
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	final HE_RAS<HE_Halfedge> keep = new HE_RASTrove<HE_Halfedge>();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		he.getVertex().setHalfedge(he.getNextInVertex());
		he.getPair().clearPair();
		he.clearPair();
	    } else {
		keep.add(he);
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Removing outer boundary halfedges.");
	halfedges = keep;
    }

    /**
     * Cap holes.
     *
     * @return all new faces as FastTable<HE_Face>
     */
    public List<HE_Face> capHoles() {
	tracker.setDefaultStatus("Capping simple planar holes.");
	final List<HE_Face> caps = new FastTable<HE_Face>();
	final List<HE_Halfedge> unpairedEdges = getUnpairedHalfedges();
	HE_RAS<HE_Halfedge> loopedHalfedges;
	HE_Halfedge start;
	HE_Halfedge he;
	HE_Halfedge hen;
	HE_Face nf;
	HE_RAS<HE_Halfedge> newHalfedges;
	HE_Halfedge phe;
	HE_Halfedge nhe;
	tracker.setDefaultStatus("Finding loops and closing holes.",
		unpairedEdges.size());
	while (unpairedEdges.size() > 0) {
	    loopedHalfedges = new HE_RASTrove<HE_Halfedge>();
	    start = unpairedEdges.get(0);
	    loopedHalfedges.add(start);
	    he = start;
	    hen = start;
	    boolean stuck = false;
	    do {
		for (int i = 0; i < unpairedEdges.size(); i++) {
		    hen = unpairedEdges.get(i);
		    if (hen.getVertex() == he.getNextInFace().getVertex()) {
			loopedHalfedges.add(hen);
			break;
		    }
		}
		if (hen.getVertex() != he.getNextInFace().getVertex()) {
		    stuck = true;
		}
		he = hen;
	    } while ((hen.getNextInFace().getVertex() != start.getVertex())
		    && (!stuck));
	    unpairedEdges.removeAll(loopedHalfedges);
	    nf = new HE_Face();
	    add(nf);
	    caps.add(nf);
	    newHalfedges = new HE_RASTrove<HE_Halfedge>();
	    for (int i = 0; i < loopedHalfedges.size(); i++) {
		phe = loopedHalfedges.get(i);
		nhe = new HE_Halfedge();
		add(nhe);
		newHalfedges.add(nhe);
		nhe.setVertex(phe.getNextInFace().getVertex());
		nhe.setPair(phe);
		phe.setPair(nhe);
		nhe.setFace(nf);
		if (nf.getHalfedge() == null) {
		    nf.setHalfedge(nhe);
		}
	    }
	    cycleHalfedgesReverse(newHalfedges.getObjects());
	    tracker.incrementCounter(newHalfedges.size());
	}
	triangulateConcaveFaces(caps);
	tracker.setDefaultStatus("Capped simple, planar holes.");
	return caps;
    }

    /**
     * Clean all mesh elements not used by any faces.
     *
     * @return self
     */
    public HE_Mesh cleanUnusedElementsByFace() {
	final HE_RAS<HE_Vertex> cleanedVertices = new HE_RASTrove<HE_Vertex>();
	final HE_RAS<HE_Halfedge> cleanedHalfedges = new HE_RASTrove<HE_Halfedge>();
	tracker.setDefaultStatus("Cleaning unused elements.");
	HE_Halfedge he;
	tracker.setDefaultStatus("Processing faces.", getNumberOfFaces());
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    he = f.getHalfedge();
	    do {
		if (!cleanedVertices.contains(he.getVertex())) {
		    cleanedVertices.add(he.getVertex());
		    he.getVertex().setHalfedge(he);
		}
		if (!cleanedHalfedges.contains(he)) {
		    cleanedHalfedges.add(he);
		}
		he = he.getNextInFace();
	    } while (he != f.getHalfedge());
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Processing halfedges.",
		cleanedHalfedges.size());
	final int n = cleanedHalfedges.size();
	for (int i = 0; i < n; i++) {
	    he = cleanedHalfedges.get(i);
	    if (!cleanedHalfedges.contains(he.getPair())) {
		if (he.getPair() != null) {
		    he.getPair().clearPair();
		}
		he.clearPair();
		he.getVertex().setHalfedge(he);
	    }
	    tracker.incrementCounter();
	}
	replaceVertices(cleanedVertices.getObjects());
	replaceHalfedges(cleanedHalfedges.getObjects());
	tracker.setDefaultStatus("Done cleaning unused elements.");
	return this;
    }

    /**
     * Reverse all faces. Flips normals.
     *
     * @return
     */
    public HE_Mesh flipAllFaces() {
	tracker.setDefaultStatus("Flipping faces.");
	tracker.setDefaultStatus("Reversing edges.", getNumberOfEdges());
	HE_Halfedge he1;
	HE_Halfedge he2;
	HE_Vertex tmp;
	HE_Halfedge[] prevHe;
	HE_Halfedge he;
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    he1 = eItr.next();
	    he2 = he1.getPair();
	    tmp = he1.getVertex();
	    he1.setVertex(he2.getVertex());
	    he2.setVertex(tmp);
	    he1.getVertex().setHalfedge(he1);
	    he2.getVertex().setHalfedge(he2);
	    tracker.incrementCounter();
	}
	prevHe = new HE_Halfedge[getNumberOfHalfedges()];
	int i = 0;
	Iterator<HE_Halfedge> heItr = heItr();
	tracker.setDefaultStatus("Reordering halfedges.",
		2 * getNumberOfHalfedges());
	while (heItr.hasNext()) {
	    he = heItr.next();
	    prevHe[i] = he.getPrevInFace();
	    i++;
	    tracker.incrementCounter();
	}
	i = 0;
	heItr = heItr();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    he.setNext(prevHe[i]);
	    prevHe[i].setPrev(he);
	    i++;
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Faces flipped.");
	return this;
    }

    /**
     *
     *
     * @param he
     * @return
     */
    public boolean flipEdge(final HE_Halfedge he) {
	// boundary edge
	if (he.getFace() == null) {
	    return false;
	}
	// not a triangle
	if (he.getFace().getFaceOrder() != 3) {
	    return false;
	}
	// unpaired edge
	if (he.getPair() == null) {
	    return false;
	}
	// boundary edge
	if (he.getPair().getFace() == null) {
	    return false;
	}
	// not a triangle
	if (he.getPair().getFace().getFaceOrder() != 3) {
	    return false;
	}
	// flip would result in overlapping triangles, this detected by
	// comparing the areas of the two triangles before and after.
	final WB_Coordinate a = he.getVertex();
	final WB_Coordinate b = he.getNextInFace().getVertex();
	final WB_Coordinate c = he.getNextInFace().getNextInFace().getVertex();
	final WB_Coordinate d = he.getPair().getNextInFace().getNextInFace()
		.getVertex();
	double Ai = new WB_Triangle(a, b, c).getArea();
	Ai += new WB_Triangle(a, d, b).getArea();
	double Af = new WB_Triangle(a, d, c).getArea();
	Af += new WB_Triangle(c, d, b).getArea();
	final double ratio = Ai / Af;
	if ((ratio > 1.000001) || (ratio < 0.99999)) {
	    return false;
	}
	// get the 3 edges of triangle t1 and t2, he1t1 and he1t2 is the edge to
	// be flipped
	final HE_Halfedge he1t1 = he;
	final HE_Halfedge he1t2 = he.getPair();
	final HE_Halfedge he2t1 = he1t1.getNextInFace();
	final HE_Halfedge he2t2 = he1t2.getNextInFace();
	final HE_Halfedge he3t1 = he2t1.getNextInFace();
	final HE_Halfedge he3t2 = he2t2.getNextInFace();
	final HE_Face t1 = he1t1.getFace();
	final HE_Face t2 = he1t2.getFace();
	// Fix vertex assignment
	// First make sure the original vertices get assigned another halfedge
	he1t1.getVertex().setHalfedge(he2t2);
	he1t2.getVertex().setHalfedge(he2t1);
	// Now assign the new vertices to the flipped edges
	he1t1.setVertex(he3t1.getVertex());
	he1t2.setVertex(he3t2.getVertex());
	// Reconstruct triangle t1
	he2t1.setNext(he1t1);
	he1t1.setNext(he3t2);
	he3t2.setNext(he2t1);
	he2t1.setPrev(he3t2);
	he1t1.setPrev(he2t1);
	he3t2.setPrev(he1t1);
	he3t2.setFace(t1);
	t1.setHalfedge(he1t1);
	// reconstruct triangle t2
	he2t2.setNext(he1t2);
	he1t2.setNext(he3t1);
	he3t1.setNext(he2t2);
	he2t2.setPrev(he3t1);
	he1t2.setPrev(he2t2);
	he3t1.setPrev(he1t2);
	he3t1.setFace(t2);
	t2.setHalfedge(he1t2);
	return true;
    }

    /**
     * Collapse halfedge. Start vertex is removed. Degenerate faces are removed.
     * This function can result in non-manifold meshes.
     *
     * @param he
     *            the he
     * @return true, if successful
     */
    public boolean collapseHalfedge(final HE_Halfedge he) {
	if (contains(he)) {
	    final HE_Halfedge hePair = he.getPair();
	    final HE_Face f = he.getFace();
	    final HE_Face fp = hePair.getFace();
	    final HE_Vertex v = he.getVertex();
	    final HE_Vertex vp = hePair.getVertex();
	    final List<HE_Halfedge> tmp = v.getHalfedgeStar();
	    final HE_Halfedge hen = he.getNextInFace();
	    final HE_Halfedge hep = he.getPrevInFace();
	    final HE_Halfedge hePairn = hePair.getNextInFace();
	    final HE_Halfedge hePairp = hePair.getPrevInFace();
	    if (f != null) {
		f.setHalfedge(hen);
	    }
	    if (fp != null) {
		fp.setHalfedge(hePairn);
	    }
	    hep.setNext(hen);
	    hen.setPrev(hep);
	    hePairp.setNext(hePairn);
	    hePairn.setPrev(hePairp);
	    for (int i = 0; i < tmp.size(); i++) {
		tmp.get(i).setVertex(vp);
	    }
	    vp.setHalfedge(hen);
	    remove(he);
	    remove(hePair);
	    remove(v);
	    deleteTwoEdgeFace(f);
	    deleteTwoEdgeFace(fp);
	    return true;
	}
	return false;
    }

    /**
     * Collapse halfedge bp.
     *
     * @param he
     *            the he
     * @return true, if successful
     */
    public boolean collapseHalfedgeBP(final HE_Halfedge he) {
	if (contains(he)) {
	    final HE_Halfedge hePair = he.getPair();
	    final HE_Face f = he.getFace();
	    final HE_Face fp = hePair.getFace();
	    final HE_Vertex v = he.getVertex();
	    final HE_Vertex vp = hePair.getVertex();
	    if (v.isBoundary()) {
		return false;
	    }
	    final List<HE_Halfedge> tmp = v.getHalfedgeStar();
	    for (int i = 0; i < tmp.size(); i++) {
		tmp.get(i).setVertex(vp);
	    }
	    vp.setHalfedge(hePair.getNextInVertex());
	    final HE_Halfedge hen = he.getNextInFace();
	    final HE_Halfedge hep = he.getPrevInFace();
	    final HE_Halfedge hePairn = hePair.getNextInFace();
	    final HE_Halfedge hePairp = hePair.getPrevInFace();
	    if (f != null) {
		f.setHalfedge(hen);
	    }
	    if (fp != null) {
		fp.setHalfedge(hePairn);
	    }
	    hep.setNext(hen);
	    hen.setPrev(hep);
	    hePairp.setNext(hePairn);
	    hePairn.setPrev(hePairp);
	    remove(he);
	    remove(hePair);
	    remove(v);
	    deleteTwoEdgeFace(f);
	    deleteTwoEdgeFace(fp);
	    return true;
	}
	return false;
    }

    /**
     * Collapse edge. End vertices are averaged. Degenerate faces are removed.
     * This function can result in non-manifold meshes.
     *
     * @param e
     *            edge to collapse
     * @return true, if successful
     */
    public boolean collapseEdge(final HE_Halfedge e) {
	if (contains(e)) {
	    final HE_Halfedge he = (e.isEdge()) ? e : e.getPair();
	    final HE_Halfedge hePair = he.getPair();
	    final HE_Face f = he.getFace();
	    final HE_Face fp = hePair.getFace();
	    final HE_Vertex v = he.getVertex();
	    final HE_Vertex vp = hePair.getVertex();
	    vp.getPoint().addSelf(v).mulSelf(0.5);
	    final List<HE_Halfedge> tmp = v.getHalfedgeStar();
	    for (int i = 0; i < tmp.size(); i++) {
		tmp.get(i).setVertex(vp);
	    }
	    vp.setHalfedge(hePair.getNextInVertex());
	    final HE_Halfedge hen = he.getNextInFace();
	    final HE_Halfedge hep = he.getPrevInFace();
	    final HE_Halfedge hePairn = hePair.getNextInFace();
	    final HE_Halfedge hePairp = hePair.getPrevInFace();
	    if (f != null) {
		f.setHalfedge(hen);
	    }
	    if (fp != null) {
		fp.setHalfedge(hePairn);
	    }
	    hep.setNext(hen);
	    hen.setPrev(hep);
	    hePairp.setNext(hePairn);
	    hePairn.setPrev(hePairp);
	    remove(he);
	    remove(hePair);
	    remove(v);
	    deleteTwoEdgeFace(f);
	    deleteTwoEdgeFace(fp);
	    return true;
	}
	return false;
    }

    /**
     *
     *
     * @param e
     * @param strict
     * @return
     */
    public boolean collapseEdgeBP(final HE_Halfedge e, final boolean strict) {
	if (contains(e)) {
	    final HE_Halfedge he = (e.isEdge()) ? e : e.getPair();
	    final HE_Halfedge hePair = he.getPair();
	    final HE_Face f = he.getFace();
	    final HE_Face fp = hePair.getFace();
	    final HE_Vertex v = he.getVertex();
	    final HE_Vertex vp = hePair.getVertex();
	    if (v.isBoundary()) {
		if (vp.isBoundary()) {
		    if ((!he.isBoundary()) || strict) {
			return false;
		    }
		    vp.getPoint().addSelf(v).mulSelf(0.5);
		} else {
		    vp.set(v);
		}
	    } else {
		if (!vp.isBoundary()) {
		    vp.getPoint().addSelf(v).mulSelf(0.5);
		}
	    }
	    final List<HE_Halfedge> tmp = v.getHalfedgeStar();
	    for (int i = 0; i < tmp.size(); i++) {
		tmp.get(i).setVertex(vp);
	    }
	    vp.setHalfedge(hePair.getNextInVertex());
	    final HE_Halfedge hen = he.getNextInFace();
	    final HE_Halfedge hep = he.getPrevInFace();
	    final HE_Halfedge hePairn = hePair.getNextInFace();
	    final HE_Halfedge hePairp = hePair.getPrevInFace();
	    if (f != null) {
		f.setHalfedge(hen);
	    }
	    if (fp != null) {
		fp.setHalfedge(hePairn);
	    }
	    hep.setNext(hen);
	    hen.setPrev(hep);
	    hePairp.setNext(hePairn);
	    hePairn.setPrev(hePairn);
	    remove(he);
	    remove(hePair);
	    remove(e);
	    remove(v);
	    deleteTwoEdgeFace(f);
	    deleteTwoEdgeFace(fp);
	    return true;
	}
	return false;
    }

    /**
     * Remove a face if it has only two vertices and stitch the mesh together.
     *
     * @param f
     *            face to check
     */
    public void deleteTwoEdgeFace(final HE_Face f) {
	if (contains(f)) {
	    final HE_Halfedge he = f.getHalfedge();
	    final HE_Halfedge hen = he.getNextInFace();
	    if (he == hen.getNextInFace()) {
		final HE_Halfedge hePair = he.getPair();
		final HE_Halfedge henPair = hen.getPair();
		remove(f);
		remove(he);
		he.getVertex().setHalfedge(he.getNextInVertex());
		remove(hen);
		hen.getVertex().setHalfedge(hen.getNextInVertex());
		hePair.setPair(henPair);
		henPair.setPair(hePair);
	    }
	}
    }

    /**
     *
     *
     * @param v
     */
    public void deleteTwoEdgeVertex(final HE_Vertex v) {
	if (contains(v) && (v.getVertexOrder() == 2)) {
	    final HE_Halfedge he0 = v.getHalfedge();
	    final HE_Halfedge he1 = he0.getNextInVertex();
	    final HE_Halfedge he0n = he0.getNextInFace();
	    final HE_Halfedge he1n = he1.getNextInFace();
	    final HE_Halfedge he0p = he0.getPair();
	    final HE_Halfedge he1p = he1.getPair();
	    he0p.setNext(he1n);
	    he1p.setNext(he0n);
	    if (he0.getFace() != null) {
		he0.getFace().setHalfedge(he1p);
	    }
	    if (he1.getFace() != null) {
		he1.getFace().setHalfedge(he0p);
	    }
	    he0n.getVertex().setHalfedge(he0n);
	    he1n.getVertex().setHalfedge(he1n);
	    he0p.setPair(he1p);
	    he1p.setPair(he0p);
	    remove(he0);
	    remove(he1);
	    remove(v);
	}
    }

    /**
     *
     */
    public void deleteTwoEdgeVertices() {
	final HE_VertexIterator vitr = new HE_VertexIterator(this);
	HE_Vertex v;
	final List<HE_Vertex> toremove = new FastTable<HE_Vertex>();
	while (vitr.hasNext()) {
	    v = vitr.next();
	    if (v.getVertexOrder() == 2) {
		toremove.add(v);
	    }
	}
	for (final HE_Vertex vtr : toremove) {
	    deleteTwoEdgeVertex(vtr);
	}
    }

    /**
     * Fix halfedge vertex assignment.
     */
    public void fixHalfedgeVertexAssignment() {
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	while (heItr.hasNext()) {
	    he = heItr.next();
	    he.getVertex().setHalfedge(he);
	}
    }

    /**
     * Collapse all zero-length edges.
     *
     */
    public void collapseDegenerateEdges() {
	final FastTable<HE_Halfedge> edgesToRemove = new FastTable<HE_Halfedge>();
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge e;
	while (eItr.hasNext()) {
	    e = eItr.next();
	    if (isZeroSq(WB_GeometryOp.getSqDistance3D(e.getVertex(),
		    e.getEndVertex()))) {
		edgesToRemove.add(e);
	    }
	}
	for (int i = 0; i < edgesToRemove.size(); i++) {
	    collapseEdge(edgesToRemove.get(i));
	}
    }

    /**
     *
     *
     * @param d
     */
    public void collapseDegenerateEdges(final double d) {
	final FastTable<HE_Halfedge> edgesToRemove = new FastTable<HE_Halfedge>();
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge e;
	final double d2 = d * d;
	while (eItr.hasNext()) {
	    e = eItr.next();
	    if (WB_GeometryOp.getSqDistance3D(e.getVertex(), e.getEndVertex()) < d2) {
		edgesToRemove.add(e);
	    }
	}
	for (int i = 0; i < edgesToRemove.size(); i++) {
	    collapseEdge(edgesToRemove.get(i));
	}
    }

    /**
     * Delete face and remove all references.
     *
     * @param f
     *            face to delete
     */
    public void deleteFace(final HE_Face f) {
	HE_Halfedge he = f.getHalfedge();
	do {
	    he.clearFace();
	    he = he.getNextInFace();
	} while (he != f.getHalfedge());
	remove(f);
    }

    /**
     * Delete edge. Adjacent faces are fused.
     *
     * @param e
     *            edge to delete
     * @return fused face (or null)
     */
    public HE_Face deleteEdge(final HE_Halfedge e) {
	HE_Face f = null;
	final HE_Halfedge he1 = e.isEdge() ? e : e.getPair();
	final HE_Halfedge he2 = he1.getPair();
	final HE_Halfedge he1n = he1.getNextInFace();
	final HE_Halfedge he2n = he2.getNextInFace();
	final HE_Halfedge he1p = he1.getPrevInFace();
	final HE_Halfedge he2p = he2.getPrevInFace();
	HE_Vertex v = he1.getVertex();
	if (v.getHalfedge() == he1) {
	    v.setHalfedge(he1.getNextInVertex());
	}
	v = he2.getVertex();
	if (v.getHalfedge() == he2) {
	    v.setHalfedge(he2.getNextInVertex());
	}
	he1p.setNext(he2n);
	he2p.setNext(he1n);
	he2n.setPrev(he1p);
	he1n.setPrev(he2p);
	if ((he1.getFace() != null) && (he2.getFace() != null)) {
	    f = new HE_Face();
	    add(f);
	    f.setHalfedge(he1p);
	    HE_Halfedge he = he1p;
	    do {
		he.setFace(f);
		he = he.getNextInFace();
	    } while (he != he1p);
	}
	if (he1.getFace() != null) {
	    remove(he1.getFace());
	}
	if (he2.getFace() != null) {
	    remove(he2.getFace());
	}
	remove(he1);
	remove(he2);
	return f;
    }

    /**
     * Insert vertex in edge.
     *
     * @param edge
     *            edge to split
     * @param v
     *            position of new vertex
     * @return selection of new vertex and new edge
     */
    public HE_Selection splitEdge(final HE_Halfedge edge, final WB_Coordinate v) {
	final HE_Selection out = new HE_Selection(this);
	final HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
	final HE_Halfedge he1 = he0.getPair();
	final HE_Vertex vNew = new HE_Vertex(v);
	final HE_Halfedge he0new = new HE_Halfedge();
	final HE_Halfedge he1new = new HE_Halfedge();
	final HE_Halfedge he0n = he0.getNextInFace();
	final HE_Halfedge he1n = he1.getNextInFace();
	final HE_Vertex v0 = he0.getVertex();
	final HE_Vertex v1 = he1.getVertex();
	if ((v0.hasVertexTexture() || he0.hasTexture())
		&& (v1.hasVertexTexture() || he1.hasTexture())) {
	    final double d0 = he0.getVertex().getPoint().getDistance3D(v);
	    final double d1 = he1.getVertex().getPoint().getDistance3D(v);
	    final double f0 = d1 / (d0 + d1);
	    final double f1 = d0 / (d0 + d1);
	    if (he0.hasTexture()) {
		if (he0n.hasTexture()) {
		    he0new.setUVW(new HE_TextureCoordinate(f0, he0.getUVW(),
			    he0n.getUVW()));
		} else if (v1.hasVertexTexture()) {
		    he0new.setUVW(new HE_TextureCoordinate(f0, he0.getUVW(), v1
			    .getVertexUVW()));
		}
	    } else if (v0.hasVertexTexture()) {
		if (he0n.hasTexture()) {
		    he0new.setUVW(new HE_TextureCoordinate(f0, v0
			    .getVertexUVW(), he0n.getUVW()));
		}
	    }
	    if (he1.hasTexture()) {
		if (he1n.hasTexture()) {
		    he1new.setUVW(new HE_TextureCoordinate(f1, he1.getUVW(),
			    he1n.getUVW()));
		} else if (v0.hasVertexTexture()) {
		    he1new.setUVW(new HE_TextureCoordinate(f1, he1.getUVW(), v0
			    .getVertexUVW()));
		}
	    } else if (v1.hasVertexTexture()) {
		if (he1n.hasTexture()) {
		    he1new.setUVW(new HE_TextureCoordinate(f1, v1
			    .getVertexUVW(), he1n.getUVW()));
		}
	    }
	    if (v0.hasVertexTexture() && v1.hasVertexTexture()) {
		vNew.setUVW(new HE_TextureCoordinate(f0, v0.getVertexUVW(), v1
			.getVertexUVW()));
	    }
	}
	he0new.setVertex(vNew);
	he1new.setVertex(vNew);
	vNew.setHalfedge(he0new);
	he0new.setNext(he0n);
	he0new.copyProperties(he0);
	he1new.setNext(he1n);
	he1new.copyProperties(he1);
	he0.setNext(he0new);
	he1.setNext(he1new);
	he0.setPair(he1new);
	he1new.setPair(he0);
	he0new.setPair(he1);
	he1.setPair(he0new);
	if (he0.getFace() != null) {
	    he0new.setFace(he0.getFace());
	}
	if (he1.getFace() != null) {
	    he1new.setFace(he1.getFace());
	}
	vNew.setInternalLabel(1);
	add(vNew);
	add(he0new);
	add(he1new);
	out.add(he0new.isEdge() ? he0new : he1new);
	out.add(he0.isEdge() ? he0 : he1);
	out.add(vNew);
	return out;
    }

    /**
     * Insert vertex in edge.
     *
     * @param key
     *            key of edge to split
     * @param v
     *            position of new vertex
     * @return selection of new vertex and new edge
     */
    public HE_Selection splitEdge(final Long key, final WB_Point v) {
	final HE_Halfedge edge = getHalfedgeByKey(key);
	return splitEdge(edge, v);
    }

    /**
     * Insert vertex in edge.
     *
     * @param edge
     *            edge to split
     * @param x
     *            x-coordinate of new vertex
     * @param y
     *            y-coordinate of new vertex
     * @param z
     *            z-coordinate of new vertex
     */
    public void splitEdge(final HE_Halfedge edge, final double x,
	    final double y, final double z) {
	splitEdge(edge, new WB_Point(x, y, z));
    }

    /**
     * Insert vertex in edge.
     *
     * @param key
     *            key of edge to split
     * @param x
     *            x-coordinate of new vertex
     * @param y
     *            y-coordinate of new vertex
     * @param z
     *            z-coordinate of new vertex
     */
    public void splitEdge(final long key, final double x, final double y,
	    final double z) {
	splitEdge(key, new WB_Point(x, y, z));
    }

    /**
     * Split edge in half.
     *
     * @param edge
     *            edge to split.
     * @return selection of new vertex and new edge
     */
    public HE_Selection splitEdge(final HE_Halfedge edge) {
	final WB_Point v = gf.createMidpoint(edge.getVertex(),
		edge.getEndVertex());
	return splitEdge(edge, v);
    }

    /**
     * Split edge in half.
     *
     * @param key
     *            key of edge to split.
     * @return selection of new vertex and new edge
     */
    public HE_Selection splitEdge(final long key) {
	final HE_Halfedge edge = getHalfedgeByKey(key);
	final WB_Point v = gf.createMidpoint(edge.getVertex(),
		edge.getEndVertex());
	return splitEdge(edge, v);
    }

    /**
     * Split edge in two parts.
     *
     * @param edge
     *            edge to split
     * @param f
     *            fraction of first part (0..1)
     * @return selection of new vertex and new edge
     */
    public HE_Selection splitEdge(final HE_Halfedge edge, final double f) {
	final WB_Point v = gf.createInterpolatedPoint(edge.getVertex(),
		edge.getEndVertex(), edge.isEdge() ? f : 1.0 - f);
	return splitEdge(edge, v);
    }

    /**
     * Split edge in two parts.
     *
     * @param key
     *            key of edge to split
     * @param f
     *            fraction of first part (0..1)
     * @return selection of new vertex and new edge
     */
    public HE_Selection splitEdge(final long key, final double f) {
	final HE_Halfedge edge = getHalfedgeByKey(key);
	return splitEdge(edge, f);
    }

    /**
     * Split all edges in half.
     *
     * @return selection of new vertices and new edges
     */
    public HE_Selection splitEdges() {
	final HE_Selection selectionOut = new HE_Selection(this);
	final HE_Halfedge[] edges = getEdgesAsArray();
	final int n = edges.length;
	for (int i = 0; i < n; i++) {
	    selectionOut.add(splitEdge(edges[i], 0.5));
	}
	return selectionOut;
    }

    /**
     * Split all edges in half, offset the center by a given distance along the
     * edge normal.
     *
     * @param offset
     *            the offset
     * @return selection of new vertices and new edges
     */
    public HE_Selection splitEdges(final double offset) {
	final HE_Selection selectionOut = new HE_Selection(this);
	final HE_Halfedge[] edges = getEdgesAsArray();
	final int n = getNumberOfEdges();
	for (int i = 0; i < n; i++) {
	    final WB_Point p = new WB_Point(edges[i].getEdgeNormal());
	    p.mulSelf(offset).addSelf(edges[i].getHalfedgeCenter());
	    selectionOut.add(splitEdge(edges[i], p));
	}
	return selectionOut;
    }

    /**
     * Split edge in half.
     *
     * @param selection
     *            edges to split.
     * @return selection of new vertices and new edges
     */
    public HE_Selection splitEdges(final HE_Selection selection) {
	final HE_Selection selectionOut = new HE_Selection(this);
	selection.collectEdgesByFace();
	final Iterator<HE_Halfedge> eItr = selection.heItr();
	while (eItr.hasNext()) {
	    selectionOut.add(splitEdge(eItr.next(), 0.5));
	}
	selection.addHalfedges(selectionOut.getEdgesAsArray());
	return selectionOut;
    }

    /**
     * Split edge in half, offset the center by a given distance along the edge
     * normal.
     *
     * @param selection
     *            edges to split.
     * @param offset
     *            the offset
     * @return selection of new vertices and new edges
     */
    public HE_Selection splitEdges(final HE_Selection selection,
	    final double offset) {
	final HE_Selection selectionOut = new HE_Selection(this);
	selection.collectEdgesByFace();
	final Iterator<HE_Halfedge> eItr = selection.heItr();
	HE_Halfedge e;
	while (eItr.hasNext()) {
	    e = eItr.next();
	    final WB_Point p = new WB_Point(e.getEdgeNormal());
	    p.mulSelf(offset).addSelf(e.getHalfedgeCenter());
	    selectionOut.add(splitEdge(e, p));
	}
	selection.addHalfedges(selectionOut.getEdgesAsArray());
	return selectionOut;
    }

    /**
     * Split edge in multiple parts.
     *
     * @param edge
     *            edge to split
     * @param f
     *            array of fractions (0..1)
     */
    public void splitEdge(final HE_Halfedge edge, final double[] f) {
	final double[] fArray = Arrays.copyOf(f, f.length);
	Arrays.sort(fArray);
	HE_Halfedge e = edge;
	final HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
	final HE_Halfedge he1 = he0.getPair();
	final HE_Vertex v0 = he0.getVertex();
	final HE_Vertex v1 = he1.getVertex();
	HE_Vertex v = new HE_Vertex();
	for (int i = 0; i < f.length; i++) {
	    final double fi = fArray[i];
	    if ((fi > 0) && (fi < 1)) {
		v = new HE_Vertex(gf.createInterpolatedPoint(v0, v1, fi));
		e = (splitEdge(e, v).eItr().next());
	    }
	}
    }

    /**
     * Split edge in multiple parts.
     *
     * @param key
     *            key of edge to split
     * @param f
     *            array of fractions (0..1)
     */
    public void splitEdge(final long key, final double[] f) {
	final HE_Halfedge edge = getHalfedgeByKey(key);
	splitEdge(edge, f);
    }

    /**
     * Split edge in multiple parts.
     *
     * @param edge
     *            edge to split
     * @param f
     *            array of fractions (0..1)
     */
    public void splitEdge(final HE_Halfedge edge, final float[] f) {
	final float[] fArray = Arrays.copyOf(f, f.length);
	Arrays.sort(fArray);
	HE_Halfedge e = edge;
	final HE_Halfedge he0 = edge.isEdge() ? edge : edge.getPair();
	final HE_Halfedge he1 = he0.getPair();
	final HE_Vertex v0 = he0.getVertex();
	final HE_Vertex v1 = he1.getVertex();
	HE_Vertex v = new HE_Vertex();
	for (int i = 0; i < f.length; i++) {
	    final double fi = fArray[i];
	    if ((fi > 0) && (fi < 1)) {
		v = new HE_Vertex(gf.createInterpolatedPoint(v0, v1, fi));
		e = (splitEdge(e, v).eItr().next());
	    }
	}
    }

    /**
     * Split edge in multiple parts.
     *
     * @param key
     *            key of edge to split
     * @param f
     *            array of fractions (0..1)
     */
    public void splitEdge(final long key, final float[] f) {
	final HE_Halfedge edge = getHalfedgeByKey(key);
	splitEdge(edge, f);
    }

    /**
     * Divide edge.
     *
     * @param origE
     *            edge to divide
     * @param n
     *            number of parts
     */
    public void divideEdge(final HE_Halfedge origE, final int n) {
	if (n > 1) {
	    final double[] f = new double[n - 1];
	    final double in = 1.0 / n;
	    for (int i = 0; i < (n - 1); i++) {
		f[i] = (i + 1) * in;
	    }
	    splitEdge(origE, f);
	}
    }

    /**
     * Divide edge.
     *
     * @param key
     *            key of edge to divide
     * @param n
     *            number of parts
     */
    public void divideEdge(final long key, final int n) {
	final HE_Halfedge edge = getHalfedgeByKey(key);
	divideEdge(edge, n);
    }

    /**
     * Find halfedge shared by vertex and face.
     *
     * @param f
     *            face
     * @param v
     *            vertex
     * @return halfedge
     */
    private HE_Halfedge findHalfedge(final HE_Face f, final HE_Vertex v) {
	HE_Halfedge he = f.getHalfedge();
	do {
	    if (he.getVertex() == v) {
		return he;
	    }
	    he = he.getNextInFace();
	} while (he != f.getHalfedge());
	return null;
    }

    /**
     * Divide face along two vertices.
     *
     * @param face
     *            face to divide
     * @param vi
     *            first vertex
     * @param vj
     *            second vertex
     * @return new face and edge
     */
    public HE_Selection splitFace(final HE_Face face, final HE_Vertex vi,
	    final HE_Vertex vj) {
	final HE_Selection out = new HE_Selection(this);
	final HE_Halfedge hei = findHalfedge(face, vi);
	final HE_Halfedge hej = findHalfedge(face, vj);
	final HE_TextureCoordinate ti = (hei.hasTexture()) ? hei.getUVW()
		: null;
	final HE_TextureCoordinate tj = (hej.hasTexture()) ? hej.getUVW()
		: null;
	final double d = vi.getPoint().getDistance3D(vj);
	boolean degenerate = false;
	if (isZero(d)) {// happens when a collinear (part of a) face
	    // is cut. Do not add a new edge connecting
	    // these two points,rather collapse them into
	    // each other and remove two-edge faces
	    degenerate = true;
	}
	if ((hei.getNextInFace() != hej) || (hei.getPrevInFace() != hej)) {
	    HE_Halfedge heiPrev;
	    HE_Halfedge hejPrev;
	    HE_Face faceNew;
	    if (!degenerate) {
		HE_Halfedge he0new;
		HE_Halfedge he1new;
		heiPrev = hei.getPrevInFace();
		hejPrev = hej.getPrevInFace();
		he0new = new HE_Halfedge();
		he1new = new HE_Halfedge();
		he0new.setVertex(vj);
		if (tj != null) {
		    he0new.setUVW(tj);
		}
		he1new.setVertex(vi);
		if (ti != null) {
		    he1new.setUVW(ti);
		}
		he0new.setNext(hei);
		he1new.setNext(hej);
		heiPrev.setNext(he1new);
		hejPrev.setNext(he0new);
		he0new.setPair(he1new);
		he1new.setPair(he0new);
		he0new.setInternalLabel(1);
		he1new.setInternalLabel(1);
		he0new.setFace(face);
		faceNew = new HE_Face();
		face.setHalfedge(hei);
		faceNew.setHalfedge(hej);
		faceNew.copyProperties(face);
		assignFaceToLoop(faceNew, hej);
		add(he0new);
		add(he1new);
		add(faceNew);
		out.add(he0new.isEdge() ? he0new : he1new);
		out.add(faceNew);
		return out;
	    } else {
		heiPrev = hei.getPrevInFace();
		hejPrev = hej.getPrevInFace();
		for (final HE_Halfedge hejs : vj.getHalfedgeStar()) {
		    hejs.setVertex(vi);
		}
		heiPrev.setNext(hej);
		hejPrev.setNext(hei);
		faceNew = new HE_Face();
		face.setHalfedge(hei);
		faceNew.setHalfedge(hej);
		faceNew.copyProperties(face);
		assignFaceToLoop(faceNew, hej);
		add(faceNew);
		remove(vj);
		out.add(faceNew);
		if (face.getFaceOrder() == 2) {
		    deleteTwoEdgeFace(face);
		}
		if (faceNew.getFaceOrder() == 2) {
		    deleteTwoEdgeFace(faceNew);
		    out.remove(faceNew);
		}
		return out;
	    }
	}
	return null;
    }

    /**
     * Divide face along two vertices.
     *
     * @param fkey
     *            key of face
     * @param vkeyi
     *            key of first vertex
     * @param vkeyj
     *            key of second vertex
     * @return new face and edge
     */
    public HE_Selection splitFace(final long fkey, final long vkeyi,
	    final long vkeyj) {
	return splitFace(getFaceByKey(fkey), getVertexByKey(vkeyi),
		getVertexByKey(vkeyj));
    }

    /**
     * Tri split faces with offset along face normal.
     *
     * @param d
     *            offset along face normal
     * @return selection of new faces and new vertex
     */
    public HE_Selection splitFacesTri(final double d) {
	final HEM_TriSplit ts = new HEM_TriSplit().setOffset(d);
	modify(ts);
	return ts.getSplitFaces();
    }

    /**
     * Tri split faces.
     *
     * @return selection of new faces and new vertex
     */
    public HE_Selection splitFacesTri() {
	final HEM_TriSplit ts = new HEM_TriSplit();
	modify(ts);
	return ts.getSplitFaces();
    }

    /**
     * Tri split faces.
     *
     * @param selection
     *            face selection to split
     * @return selection of new faces and new vertex
     */
    public HE_Selection splitFacesTri(final HE_Selection selection) {
	final HEM_TriSplit ts = new HEM_TriSplit();
	modifySelected(ts, selection);
	return ts.getSplitFaces();
    }

    /**
     * Tri split faces with offset along face normal.
     *
     * @param selection
     *            face selection to split
     * @param d
     *            offset along face normal
     * @return selection of new faces and new vertex
     */
    public HE_Selection splitFacesTri(final HE_Selection selection,
	    final double d) {
	final HEM_TriSplit ts = new HEM_TriSplit().setOffset(d);
	modifySelected(ts, selection);
	return ts.getSplitFaces();
    }

    /**
     * Quad split faces.
     *
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesQuad() {
	final HEM_QuadSplit qs = new HEM_QuadSplit();
	modify(qs);
	return qs.getSplitFaces();
    }

    /**
     * Quad split selected faces.
     *
     * @param sel
     *            selection to split
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesQuad(final HE_Selection sel) {
	final HEM_QuadSplit qs = new HEM_QuadSplit();
	modifySelected(qs, sel);
	return qs.getSplitFaces();
    }

    /**
     * Quad split faces.
     *
     * @param d
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesQuad(final double d) {
	final HEM_QuadSplit qs = new HEM_QuadSplit().setOffset(d);
	;
	modify(qs);
	return qs.getSplitFaces();
    }

    /**
     * Quad split selected faces.
     *
     * @param sel
     *            selection to split
     * @param d
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesQuad(final HE_Selection sel, final double d) {
	final HEM_QuadSplit qs = new HEM_QuadSplit().setOffset(d);
	;
	modifySelected(qs, sel);
	return qs.getSplitFaces();
    }

    /**
     * Hybrid split faces: midsplit for triangles, quad split otherwise.
     *
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesHybrid() {
	final HE_Selection selectionOut = new HE_Selection(this);
	final int n = getNumberOfFaces();
	final WB_Point[] faceCenters = new WB_Point[n];
	final int[] faceOrders = new int[n];
	HE_Face f;
	int i = 0;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    faceCenters[i] = f.getFaceCenter();
	    faceOrders[i] = f.getFaceOrder();
	    i++;
	}
	final HE_Selection orig = new HE_Selection(this);
	orig.addFaces(getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(splitEdges().getVerticesAsArray());
	final HE_Face[] faces = getFacesAsArray();
	HE_Vertex vi = new HE_Vertex();
	int fo;
	for (i = 0; i < n; i++) {
	    f = faces[i];
	    fo = f.getFaceOrder() / 2;
	    if (fo == 3) {
		HE_Halfedge startHE = f.getHalfedge();
		while (orig.contains(startHE.getVertex())) {
		    startHE = startHE.getNextInFace();
		}
		HE_Halfedge he = startHE;
		final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
		final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
		int c = 0;
		do {
		    textures[c++] = (he.hasTexture()) ? he.getUVW() : null;
		    he = he.getNextInFace().getNextInFace();
		} while (he != startHE);
		c = 0;
		do {
		    final HE_Face fn = new HE_Face();
		    fn.copyProperties(f);
		    add(fn);
		    he0[c] = he;
		    he.setFace(fn);
		    fn.setHalfedge(he);
		    he1[c] = he.getNextInFace();
		    he2[c] = new HE_Halfedge();
		    hec[c] = new HE_Halfedge();
		    add(he2[c]);
		    add(hec[c]);
		    hec[c].setVertex(he.getVertex());
		    if (textures[c] != null) {
			hec[c].setUVW(textures[c]);
		    }
		    hec[c].setPair(he2[c]);
		    he2[c].setPair(hec[c]);
		    hec[c].setFace(f);
		    he2[c].setVertex(he.getNextInFace().getNextInFace()
			    .getVertex());
		    if (textures[(c + 1) % fo] != null) {
			he2[c].setUVW(textures[(c + 1) % fo]);
		    }
		    he2[c].setNext(he0[c]);
		    he0[c].setPrev(he2[c]);
		    he1[c].setFace(fn);
		    he2[c].setFace(fn);
		    c++;
		    he = he.getNextInFace().getNextInFace();
		} while (he != startHE);
		f.setHalfedge(hec[0]);
		for (int j = 0; j < c; j++) {
		    he1[j].setNext(he2[j]);
		    he2[j].setPrev(he1[j]);
		    hec[j].setNext(hec[(j + 1) % c]);
		    hec[(j + 1) % c].setPrev(hec[j]);
		}
	    } else if (fo > 3) {
		vi = new HE_Vertex(faceCenters[i]);
		vi.setInternalLabel(2);
		double u = 0;
		double v = 0;
		double w = 0;
		HE_Halfedge he = f.getHalfedge();
		boolean hasTexture = true;
		do {
		    if (!he.getVertex().hasTexture(f)) {
			hasTexture = false;
			break;
		    }
		    u += he.getVertex().getUVW(f).ud();
		    v += he.getVertex().getUVW(f).vd();
		    w += he.getVertex().getUVW(f).wd();
		    he = he.getNextInFace();
		} while (he != f.getHalfedge());
		if (hasTexture) {
		    final double ifo = 1.0 / f.getFaceOrder();
		    vi.setUVW(u * ifo, v * ifo, w * ifo);
		}
		add(vi);
		selectionOut.add(vi);
		HE_Halfedge startHE = f.getHalfedge();
		while (orig.contains(startHE.getVertex())) {
		    startHE = startHE.getNextInFace();
		}
		he = startHE;
		final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
		int c = 0;
		do {
		    HE_Face fc;
		    if (c == 0) {
			fc = f;
		    } else {
			fc = new HE_Face();
			fc.copyProperties(f);
			add(fc);
		    }
		    he0[c] = he;
		    he.setFace(fc);
		    fc.setHalfedge(he);
		    he1[c] = he.getNextInFace();
		    he2[c] = new HE_Halfedge();
		    he3[c] = new HE_Halfedge();
		    add(he2[c]);
		    add(he3[c]);
		    he2[c].setVertex(he.getNextInFace().getNextInFace()
			    .getVertex());
		    if (he2[c].getVertex().hasHalfedgeTexture(f)) {
			he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
		    }
		    he3[c].setVertex(vi);
		    he2[c].setNext(he3[c]);
		    he3[c].setPrev(he2[c]);
		    he3[c].setNext(he);
		    he.setPrev(he3[c]);
		    he1[c].setFace(fc);
		    he2[c].setFace(fc);
		    he3[c].setFace(fc);
		    c++;
		    he = he.getNextInFace().getNextInFace();
		} while (he != startHE);
		vi.setHalfedge(he3[0]);
		for (int j = 0; j < c; j++) {
		    he1[j].setNext(he2[j]);
		    he2[j].setPrev(he1[j]);
		}
	    }
	}
	pairHalfedges();
	return selectionOut;
    }

    /**
     * Hybrid split faces: midsplit for triangles, quad split otherwise.
     *
     * @param sel
     *            the sel
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesHybrid(final HE_Selection sel) {
	final HE_Selection selectionOut = new HE_Selection(this);
	final int n = sel.getNumberOfFaces();
	final WB_Point[] faceCenters = new WB_Point[n];
	final int[] faceOrders = new int[n];
	HE_Face f;
	int i = 0;
	final Iterator<HE_Face> fItr = sel.fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    faceCenters[i] = f.getFaceCenter();
	    faceOrders[i] = f.getFaceOrder();
	    i++;
	}
	final HE_Selection orig = new HE_Selection(this);
	orig.addFaces(sel.getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(splitEdges().getVerticesAsArray());
	final HE_Face[] faces = sel.getFacesAsArray();
	HE_Vertex vi = new HE_Vertex();
	int fo;
	for (i = 0; i < n; i++) {
	    f = faces[i];
	    fo = f.getFaceOrder() / 2;
	    if (fo == 3) {
		HE_Halfedge startHE = f.getHalfedge();
		while (orig.contains(startHE.getVertex())) {
		    startHE = startHE.getNextInFace();
		}
		HE_Halfedge he = startHE;
		final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
		final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
		int c = 0;
		do {
		    textures[c++] = (he.hasTexture()) ? he.getUVW() : null;
		    he = he.getNextInFace().getNextInFace();
		} while (he != startHE);
		c = 0;
		do {
		    final HE_Face fn = new HE_Face();
		    fn.copyProperties(f);
		    add(fn);
		    sel.add(fn);
		    he0[c] = he;
		    he.setFace(fn);
		    fn.setHalfedge(he);
		    he1[c] = he.getNextInFace();
		    he2[c] = new HE_Halfedge();
		    hec[c] = new HE_Halfedge();
		    add(he2[c]);
		    add(hec[c]);
		    hec[c].setVertex(he.getVertex());
		    if (textures[c] != null) {
			hec[c].setUVW(textures[c]);
		    }
		    hec[c].setPair(he2[c]);
		    he2[c].setPair(hec[c]);
		    hec[c].setFace(f);
		    he2[c].setVertex(he.getNextInFace().getNextInFace()
			    .getVertex());
		    if (textures[(c + 1) % fo] != null) {
			he2[c].setUVW(textures[(c + 1) % fo]);
		    }
		    he2[c].setNext(he0[c]);
		    he0[c].setPrev(he2[c]);
		    he1[c].setFace(fn);
		    he2[c].setFace(fn);
		    c++;
		    he = he.getNextInFace().getNextInFace();
		} while (he != startHE);
		f.setHalfedge(hec[0]);
		for (int j = 0; j < c; j++) {
		    he1[j].setNext(he2[j]);
		    hec[j].setNext(hec[(j + 1) % c]);
		    he2[j].setPrev(he1[j]);
		    hec[(j + 1) % c].setPrev(hec[j]);
		}
	    } else if (fo > 3) {
		vi = new HE_Vertex(faceCenters[i]);
		vi.setInternalLabel(2);
		double u = 0;
		double v = 0;
		double w = 0;
		HE_Halfedge he = f.getHalfedge();
		boolean hasTexture = true;
		do {
		    if (!he.getVertex().hasTexture(f)) {
			hasTexture = false;
			break;
		    }
		    u += he.getVertex().getUVW(f).ud();
		    v += he.getVertex().getUVW(f).vd();
		    w += he.getVertex().getUVW(f).wd();
		    he = he.getNextInFace();
		} while (he != f.getHalfedge());
		if (hasTexture) {
		    final double ifo = 1.0 / f.getFaceOrder();
		    vi.setUVW(u * ifo, v * ifo, w * ifo);
		}
		add(vi);
		selectionOut.add(vi);
		HE_Halfedge startHE = f.getHalfedge();
		while (orig.contains(startHE.getVertex())) {
		    startHE = startHE.getNextInFace();
		}
		he = startHE;
		final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
		final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
		int c = 0;
		do {
		    HE_Face fc;
		    if (c == 0) {
			fc = f;
		    } else {
			fc = new HE_Face();
			fc.copyProperties(f);
			add(fc);
			sel.add(fc);
		    }
		    he0[c] = he;
		    he.setFace(fc);
		    fc.setHalfedge(he);
		    he1[c] = he.getNextInFace();
		    he2[c] = new HE_Halfedge();
		    he3[c] = new HE_Halfedge();
		    add(he2[c]);
		    add(he3[c]);
		    he2[c].setVertex(he.getNextInFace().getNextInFace()
			    .getVertex());
		    if (he2[c].getVertex().hasHalfedgeTexture(f)) {
			he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
		    }
		    he3[c].setVertex(vi);
		    he2[c].setNext(he3[c]);
		    he3[c].setNext(he);
		    he1[c].setFace(fc);
		    he2[c].setFace(fc);
		    he3[c].setFace(fc);
		    c++;
		    he = he.getNextInFace().getNextInFace();
		} while (he != startHE);
		vi.setHalfedge(he3[0]);
		for (int j = 0; j < c; j++) {
		    he1[j].setNext(he2[j]);
		}
	    }
	}
	pairHalfedges();
	return selectionOut;
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection splitFacesCenter() {
	final HEM_CenterSplit cs = new HEM_CenterSplit();
	modify(cs);
	return cs.getCenterFaces();
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection splitFacesCenterHole() {
	final HEM_CenterSplitHole csh = new HEM_CenterSplitHole();
	modify(csh);
	return csh.getWallFaces();
    }

    /**
     *
     *
     * @param faces
     * @return
     */
    public HE_Selection splitFacesCenter(final HE_Selection faces) {
	final HEM_CenterSplit cs = new HEM_CenterSplit();
	modifySelected(cs, faces);
	return cs.getCenterFaces();
    }

    /**
     *
     *
     * @param faces
     * @return
     */
    public HE_Selection splitFacesCenterHole(final HE_Selection faces) {
	final HEM_CenterSplitHole csh = new HEM_CenterSplitHole();
	modifySelected(csh, faces);
	return csh.getWallFaces();
    }

    /**
     *
     *
     * @param d
     * @return
     */
    public HE_Selection splitFacesCenter(final double d) {
	final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d);
	modify(cs);
	return cs.getCenterFaces();
    }

    /**
     *
     *
     * @param d
     * @return
     */
    public HE_Selection splitFacesCenterHole(final double d) {
	final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d);
	modify(csh);
	return csh.getWallFaces();
    }

    /**
     *
     *
     * @param faces
     * @param d
     * @return
     */
    public HE_Selection splitFacesCenter(final HE_Selection faces,
	    final double d) {
	final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d);
	modifySelected(cs, faces);
	return cs.getCenterFaces();
    }

    /**
     *
     *
     * @param faces
     * @param d
     * @return
     */
    public HE_Selection splitFacesCenterHole(final HE_Selection faces,
	    final double d) {
	final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d);
	modifySelected(csh, faces);
	return csh.getWallFaces();
    }

    /**
     *
     *
     * @param d
     * @param c
     * @return
     */
    public HE_Selection splitFacesCenter(final double d, final double c) {
	final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d)
		.setChamfer(c);
	modify(cs);
	return cs.getCenterFaces();
    }

    /**
     *
     *
     * @param d
     * @param c
     * @return
     */
    public HE_Selection splitFacesCenterHole(final double d, final double c) {
	final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d)
		.setChamfer(c);
	;
	modify(csh);
	return csh.getWallFaces();
    }

    /**
     *
     *
     * @param faces
     * @param d
     * @param c
     * @return
     */
    public HE_Selection splitFacesCenter(final HE_Selection faces,
	    final double d, final double c) {
	final HEM_CenterSplit cs = new HEM_CenterSplit().setOffset(d)
		.setChamfer(c);
	;
	modifySelected(cs, faces);
	return cs.getCenterFaces();
    }

    /**
     *
     *
     * @param faces
     * @param d
     * @param c
     * @return
     */
    public HE_Selection splitFacesCenterHole(final HE_Selection faces,
	    final double d, final double c) {
	final HEM_CenterSplitHole csh = new HEM_CenterSplitHole().setOffset(d)
		.setChamfer(c);
	;
	modifySelected(csh, faces);
	return csh.getWallFaces();
    }

    /**
     * Midedge split faces.
     *
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesMidEdge() {
	final HE_Selection selectionOut = new HE_Selection(this);
	final int n = getNumberOfFaces();
	final int[] faceOrders = new int[n];
	HE_Face face;
	int i = 0;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    faceOrders[i] = face.getFaceOrder();
	    i++;
	}
	final HE_Selection orig = new HE_Selection(this);
	orig.addFaces(getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(splitEdges().getVerticesAsArray());
	final HE_Face[] faces = getFacesAsArray();
	for (i = 0; i < n; i++) {
	    face = faces[i];
	    HE_Halfedge startHE = face.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    HE_Halfedge he = startHE;
	    final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
	    final int fo = face.getFaceOrder() / 2;
	    final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
	    int c = 0;
	    do {
		textures[c++] = (he.hasTexture()) ? he.getUVW() : null;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    c = 0;
	    he = startHE;
	    do {
		final HE_Face f = new HE_Face();
		f.copyProperties(face);
		add(f);
		he0[c] = he;
		he1[c] = he.getNextInFace();
		he2[c] = new HE_Halfedge();
		hec[c] = new HE_Halfedge();
		add(he2[c]);
		add(hec[c]);
		hec[c].setVertex(he.getVertex());
		if (textures[c] != null) {
		    hec[c].setUVW(textures[c]);
		}
		hec[c].setPair(he2[c]);
		he2[c].setPair(hec[c]);
		hec[c].setFace(face);
		he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
		if (textures[(c + 1) % fo] != null) {
		    he2[c].setUVW(textures[(c + 1) % fo]);
		}
		he2[c].setNext(he0[c]);
		he0[c].setPrev(he2[c]);
		he0[c].setFace(f);
		f.setHalfedge(he0[c]);
		he1[c].setFace(f);
		he2[c].setFace(f);
		c++;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    face.setHalfedge(hec[0]);
	    for (int j = 0; j < c; j++) {
		he1[j].setNext(he2[j]);
		he2[j].setPrev(he1[j]);
		hec[j].setNext(hec[(j + 1) % c]);
		hec[(j + 1) % c].setPrev(hec[j]);
	    }
	}
	return selectionOut;
    }

    /**
     * Mid edge split faces.
     *
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesMidEdgeHole() {
	final HE_Selection selectionOut = new HE_Selection(this);
	final int n = getNumberOfFaces();
	final int[] faceOrders = new int[n];
	HE_Face face;
	int i = 0;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    faceOrders[i] = face.getFaceOrder();
	    i++;
	}
	final HE_Selection orig = new HE_Selection(this);
	orig.addFaces(getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(splitEdges().getVerticesAsArray());
	final HE_Face[] faces = getFacesAsArray();
	for (i = 0; i < n; i++) {
	    face = faces[i];
	    HE_Halfedge startHE = face.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    HE_Halfedge he = startHE;
	    final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
	    final int fo = face.getFaceOrder() / 2;
	    final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
	    int c = 0;
	    do {
		textures[c++] = (he.hasTexture()) ? he.getUVW() : null;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    c = 0;
	    do {
		final HE_Face f = new HE_Face();
		f.copyProperties(face);
		add(f);
		he0[c] = he;
		he.setFace(f);
		f.setHalfedge(he);
		he1[c] = he.getNextInFace();
		he2[c] = new HE_Halfedge();
		hec[c] = new HE_Halfedge();
		add(he2[c]);
		add(hec[c]);
		hec[c].setVertex(he.getVertex());
		if (textures[c] != null) {
		    hec[c].setUVW(textures[c]);
		}
		hec[c].setPair(he2[c]);
		he2[c].setPair(hec[c]);
		hec[c].setFace(face);
		he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
		if (textures[(c + 1) % fo] != null) {
		    he2[c].setUVW(textures[(c + 1) % fo]);
		}
		he2[c].setNext(he0[c]);
		he0[c].setPrev(he2[c]);
		he1[c].setFace(f);
		he2[c].setFace(f);
		c++;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    face.setHalfedge(hec[0]);
	    for (int j = 0; j < c; j++) {
		he1[j].setNext(he2[j]);
		hec[j].setNext(hec[(j + 1) % c]);
		he2[j].setPrev(he1[j]);
		hec[(j + 1) % c].setPrev(hec[j]);
	    }
	    deleteFace(face);
	}
	return selectionOut;
    }

    /**
     * Mid edge split selected faces.
     *
     * @param selection
     *            selection to split
     * @return selection of new faces and new vertices
     */
    public HE_Selection splitFacesMidEdge(final HE_Selection selection) {
	final HE_Selection selectionOut = new HE_Selection(this);
	final int n = selection.getNumberOfFaces();
	final int[] faceOrders = new int[n];
	HE_Face face;
	final Iterator<HE_Face> fItr = selection.fItr();
	int i = 0;
	while (fItr.hasNext()) {
	    face = fItr.next();
	    faceOrders[i] = face.getFaceOrder();
	    i++;
	}
	final HE_Selection orig = new HE_Selection(this);
	orig.addFaces(selection.getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(splitEdges(orig).getVerticesAsArray());
	final HE_Face[] faces = selection.getFacesAsArray();
	for (i = 0; i < n; i++) {
	    face = faces[i];
	    HE_Halfedge startHE = face.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    HE_Halfedge he = startHE;
	    final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
	    final int fo = face.getFaceOrder() / 2;
	    final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
	    int c = 0;
	    do {
		textures[c++] = (he.hasTexture()) ? he.getUVW() : null;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    c = 0;
	    do {
		final HE_Face f = new HE_Face();
		add(f);
		f.copyProperties(face);
		selection.add(f);
		he0[c] = he;
		he.setFace(f);
		f.setHalfedge(he);
		he1[c] = he.getNextInFace();
		he2[c] = new HE_Halfedge();
		hec[c] = new HE_Halfedge();
		add(he2[c]);
		add(hec[c]);
		hec[c].setVertex(he.getVertex());
		if (textures[c] != null) {
		    hec[c].setUVW(textures[c]);
		}
		hec[c].setPair(he2[c]);
		he2[c].setPair(hec[c]);
		hec[c].setFace(face);
		he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
		if (textures[(c + 1) % fo] != null) {
		    he2[c].setUVW(textures[(c + 1) % fo]);
		}
		he2[c].setNext(he0[c]);
		he0[c].setPrev(he2[c]);
		he1[c].setFace(f);
		he2[c].setFace(f);
		c++;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    face.setHalfedge(hec[0]);
	    for (int j = 0; j < c; j++) {
		he1[j].setNext(he2[j]);
		he2[j].setPrev(he1[j]);
		hec[j].setNext(hec[(j + 1) % c]);
		hec[(j + 1) % c].setPrev(hec[j]);
	    }
	}
	return selectionOut;
    }

    /**
     *
     *
     * @param selection
     * @return
     */
    public HE_Selection splitFacesMidEdgeHole(final HE_Selection selection) {
	final HE_Selection selectionOut = new HE_Selection(this);
	final int n = selection.getNumberOfFaces();
	final int[] faceOrders = new int[n];
	HE_Face face;
	final Iterator<HE_Face> fItr = selection.fItr();
	int i = 0;
	while (fItr.hasNext()) {
	    face = fItr.next();
	    faceOrders[i] = face.getFaceOrder();
	    i++;
	}
	final HE_Selection orig = new HE_Selection(this);
	orig.addFaces(selection.getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(splitEdges(orig).getVerticesAsArray());
	final HE_Face[] faces = selection.getFacesAsArray();
	for (i = 0; i < n; i++) {
	    face = faces[i];
	    HE_Halfedge startHE = face.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    HE_Halfedge he = startHE;
	    final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
	    final int fo = face.getFaceOrder() / 2;
	    final HE_TextureCoordinate[] textures = new HE_TextureCoordinate[fo];
	    int c = 0;
	    do {
		textures[c++] = (he.hasTexture()) ? he.getUVW() : null;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    c = 0;
	    do {
		final HE_Face f = new HE_Face();
		add(f);
		f.copyProperties(face);
		selection.add(f);
		he0[c] = he;
		he.setFace(f);
		f.setHalfedge(he);
		he1[c] = he.getNextInFace();
		he2[c] = new HE_Halfedge();
		hec[c] = new HE_Halfedge();
		add(he2[c]);
		add(hec[c]);
		hec[c].setVertex(he.getVertex());
		if (textures[c] != null) {
		    hec[c].setUVW(textures[c]);
		}
		hec[c].setPair(he2[c]);
		he2[c].setPair(hec[c]);
		hec[c].setFace(face);
		he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
		if (textures[(c + 1) % fo] != null) {
		    he2[c].setUVW(textures[(c + 1) % fo]);
		}
		he2[c].setNext(he0[c]);
		he0[c].setPrev(he2[c]);
		he1[c].setFace(f);
		he2[c].setFace(f);
		c++;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    face.setHalfedge(hec[0]);
	    for (int j = 0; j < c; j++) {
		he1[j].setNext(he2[j]);
		hec[j].setNext(hec[(j + 1) % c]);
		he2[j].setPrev(he1[j]);
		hec[(j + 1) % c].setPrev(hec[j]);
	    }
	    deleteFace(face);
	}
	return selectionOut;
    }

    /**
     * Triangulate all concave faces.
     *
     */
    public void triangulateConcaveFaces() {
	final HE_Face[] f = getFacesAsArray();
	final int n = getNumberOfFaces();
	for (int i = 0; i < n; i++) {
	    if (f[i].getFaceType() == WB_ClassificationConvex.CONCAVE) {
		triangulate(f[i].key());
	    }
	}
    }

    /**
     *
     *
     * @param sel
     */
    public void triangulateConcaveFaces(final List<HE_Face> sel) {
	final int n = sel.size();
	for (int i = 0; i < n; i++) {
	    if (sel.get(i).getFaceType() == WB_ClassificationConvex.CONCAVE) {
		triangulate(sel.get(i).key());
	    }
	}
    }

    /**
     * Triangulate face if concave.
     *
     * @param key
     *            key of face
     */
    public void triangulateConcaveFace(final long key) {
	triangulateConcaveFace(getFaceByKey(key));
    }

    /**
     * Triangulate face if concave.
     *
     * @param face
     *            key of face
     */
    public void triangulateConcaveFace(final HE_Face face) {
	if (face.getFaceType() == WB_ClassificationConvex.CONCAVE) {
	    triangulate(face);
	}
    }

    /**
     * Expand vertex to new edge.
     *
     * @param v
     *            vertex to expand
     * @param f1
     *            first face
     * @param f2
     *            second face
     * @param vn
     *            position of new vertex
     */
    public void expandVertexToEdge(final HE_Vertex v, final HE_Face f1,
	    final HE_Face f2, final WB_Point vn) {
	if (f1 == f2) {
	    return;
	}
	HE_Halfedge he = v.getHalfedge();
	HE_Halfedge he1 = new HE_Halfedge();
	HE_Halfedge he2 = new HE_Halfedge();
	do {
	    if (he.getFace() == f1) {
		he1 = he;
	    }
	    if (he.getFace() == f2) {
		he2 = he;
	    }
	    he = he.getNextInVertex();
	} while (he != v.getHalfedge());
	final HE_Vertex vNew = new HE_Vertex(vn);
	vNew.setHalfedge(he1);
	add(vNew);
	he = he1;
	do {
	    he.setVertex(vNew);
	    he = he.getNextInVertex();
	} while (he != he2);
	final HE_Halfedge he1p = he1.getPrevInFace();
	final HE_Halfedge he2p = he2.getPrevInFace();
	final HE_Halfedge he1new = new HE_Halfedge();
	final HE_Halfedge he2new = new HE_Halfedge();
	add(he1new);
	add(he2new);
	he1new.setVertex(v);
	he2new.setVertex(vNew);
	he1p.setNext(he1new);
	he1new.setPrev(he1p);
	he1new.setNext(he1);
	he1.setPrev(he1new);
	he2p.setNext(he2new);
	he2new.setPrev(he2p);
	he2new.setNext(he2);
	he2.setPrev(he2new);
	he1new.setPair(he2new);
	he2new.setPair(he1new);
	he1new.setFace(f1);
	he2new.setFace(f2);
    }

    /**
     * Check consistency of datastructure.
     *
     * @return true or false
     */
    public boolean validate() {
	return HET_Diagnosis.validate(this);
    }

    /**
     * Check if point lies inside mesh.
     *
     * @param p
     *            point to check
     * @param isConvex
     *            do fast check, convex meshes only
     * @return true or false
     */
    public boolean contains(final WB_Coordinate p, final boolean isConvex) {
	final WB_Vector dir = new WB_Vector(Math.random() - 0.5,
		Math.random() - 0.5, Math.random() - 0.5);
	final WB_Ray R = new WB_Ray(p, dir);
	int c = 0;
	WB_Plane P;
	WB_IntersectionResult lpi;
	HE_Face face;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    P = face.toPlane();
	    if (isConvex) {
		if (WB_Classify.classifyPointToPlane3D(p, P) == WB_ClassificationGeometry.BACK) {
		    return false;
		}
	    } else {
		lpi = WB_GeometryOp.getIntersection3D(R, P);
		if (lpi.intersection) {
		    if (pointIsInFace((WB_Point) lpi.object, face)) {
			/*
			 * if (!HE_Mesh.pointIsStrictlyInFace( (WB_Point)
			 * lpi.object, face)) { return contains(p, isConvex); }
			 */
			c++;
		    }
		}
	    }
	}
	return (isConvex) ? true : ((c % 2) == 1);
    }

    /**
     * Check if point lies inside or on edge of face.
     *
     * @param p
     *            point
     * @param f
     *            the f
     * @return true/false
     */
    public static boolean pointIsInFace(final WB_Point p, final HE_Face f) {
	return isZero(WB_GeometryOp.getDistance3D(p,
		WB_GeometryOp.getClosestPoint3D(p, f.toPolygon())));
    }

    /**
     * Check if point lies strictly inside face.
     *
     * @param p
     *            point
     * @param f
     *            the f
     * @return true/false
     */
    public static boolean pointIsStrictlyInFace(final WB_Coordinate p,
	    final HE_Face f) {
	final WB_Polygon poly = f.toPolygon();
	if (!isZeroSq(WB_GeometryOp.getSqDistance3D(p,
		WB_GeometryOp.getClosestPoint3D(p, poly)))) {
	    return false;
	}
	if (!isZeroSq(WB_GeometryOp.getSqDistance3D(p,
		WB_GeometryOp.getClosestPointOnPeriphery3D(p, poly)))) {
	    return false;
	}
	return true;
    }

    /**
     * Fit in aabb.
     *
     * @param AABB
     *            the aabb
     */
    public void fitInAABB(final WB_AABB AABB) {
	final WB_AABB self = getAABB();
	move(new WB_Vector(self.getMin(), AABB.getMin()));
	scale(AABB.getWidth() / self.getWidth(),
		AABB.getHeight() / self.getHeight(),
		AABB.getDepth() / self.getDepth(), new WB_Point(AABB.getMin()));
    }

    /**
     * Fit in aabb constrained.
     *
     * @param AABB
     *            the aabb
     * @return
     */
    public double fitInAABBConstrained(final WB_AABB AABB) {
	final WB_AABB self = getAABB();
	move(new WB_Vector(self.getCenter(), AABB.getCenter()));
	double f = Math.min(AABB.getWidth() / self.getWidth(), AABB.getHeight()
		/ self.getHeight());
	f = Math.min(f, AABB.getDepth() / self.getDepth());
	scale(f, new WB_Point(AABB.getCenter()));
	return f;
    }

    /**
     * Delete face and remove all references.
     *
     * @param faces
     *            faces to delete
     */
    public void delete(final HE_Selection faces) {
	HE_Face f;
	final Iterator<HE_Face> fItr = faces.fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    remove(f);
	}
	cleanUnusedElementsByFace();
	capHalfedges();
    }

    /**
     * Select all faces.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllFaces() {
	final HE_Selection _selection = new HE_Selection(this);
	_selection.addFaces(getFacesAsArray());
	return _selection;
    }

    /**
     *
     *
     * @param chance
     * @return
     */
    public HE_Selection selectRandomFaces(final double chance) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (Math.random() <= chance) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     * Select all faces with given label.
     *
     * @param label
     *            the label
     * @return the h e_ selection
     */
    public HE_Selection selectFacesWithLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getLabel() == label) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     *
     *
     * @param label
     * @return
     */
    public HE_Selection selectFacesWithInternalLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getInternalLabel() == label) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     *
     *
     * @param v
     * @return
     */
    public HE_Selection selectFaces(final WB_Vector v) {
	final HE_Selection _selection = new HE_Selection(this);
	final WB_Vector w = v.get();
	w.normalizeSelf();
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getFaceNormal().dot(v) > (1.0 - WB_Epsilon.EPSILON)) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     *
     *
     * @param P
     * @return
     */
    public HE_Selection selectFaces(final WB_Plane P) {
	final HE_Selection _selection = new HE_Selection(this);
	final HE_FaceIterator fitr = fItr();
	HE_Face f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (WB_Classify.classifyPolygonToPlane3D(f.toPolygon(), P) == WB_ClassificationGeometry.FRONT) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     *
     *
     * @param P
     * @return
     */
    public HE_Selection selectCrossingFaces(final WB_Plane P) {
	final HE_Selection _selection = new HE_Selection(this);
	final HE_FaceIterator fitr = fItr();
	HE_Face f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (WB_Classify.classifyPolygonToPlane3D(f.toPolygon(), P) == WB_ClassificationGeometry.CROSSING) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     * Select all faces except with given label.
     *
     * @param label
     *            the label
     * @return the h e_ selection
     */
    public HE_Selection selectFacesWithOtherLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getLabel() != label) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     *
     *
     * @param label
     * @return
     */
    public HE_Selection selectFacesWithOtherInternalLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getInternalLabel() != label) {
		_selection.add(f);
	    }
	}
	return _selection;
    }

    /**
     * Select all edges.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllEdges() {
	final HE_Selection _selection = new HE_Selection(this);
	_selection.addHalfedges(getEdgesAsArray());
	return _selection;
    }

    /**
     * Select all halfedges.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllHalfedges() {
	final HE_Selection _selection = new HE_Selection(this);
	_selection.addHalfedges(getHalfedgesAsArray());
	return _selection;
    }

    /**
     * Select all vertices.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllVertices() {
	final HE_Selection _selection = new HE_Selection(this);
	_selection.addVertices(getVerticesAsArray());
	return _selection;
    }

    /**
     * Select all vertices with given label.
     *
     * @param label
     *            the label
     * @return the h e_ selection
     */
    public HE_Selection selectVerticesWithLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getLabel() == label) {
		_selection.add(v);
	    }
	}
	return _selection;
    }

    /**
     * Select all vertices except with given label.
     *
     * @param label
     *            the label
     * @return the h e_ selection
     */
    public HE_Selection selectVerticesWithOtherLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getLabel() != label) {
		_selection.add(v);
	    }
	}
	return _selection;
    }

    /**
     *
     *
     * @param label
     * @return
     */
    public HE_Selection selectVerticesWithInternalLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getInternalLabel() == label) {
		_selection.add(v);
	    }
	}
	return _selection;
    }

    /**
     * Select all vertices except with given label.
     *
     * @param label
     *            the label
     * @return the h e_ selection
     */
    public HE_Selection selectVerticesWithOtherInternalLabel(final int label) {
	final HE_Selection _selection = new HE_Selection(this);
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getInternalLabel() != label) {
		_selection.add(v);
	    }
	}
	return _selection;
    }

    /**
     * Select all halfedges on inside of boundary.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllInnerBoundaryHalfedges() {
	final HE_Selection _selection = new HE_Selection(this);
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getPair().getFace() == null) {
		_selection.add(he);
	    }
	}
	return _selection;
    }

    /**
     * Select all halfedges on outside of boundary.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllOuterBoundaryHalfedges() {
	final HE_Selection _selection = new HE_Selection(this);
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		_selection.add(he);
	    }
	}
	return _selection;
    }

    /**
     * Select all edges on boundary.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllBoundaryEdges() {
	final HE_Selection _selection = new HE_Selection(this);
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.isBoundary()) {
		_selection.add(he);
	    }
	}
	return _selection;
    }

    /**
     * Select all faces on boundary.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllBoundaryFaces() {
	final HE_Selection _selection = new HE_Selection(this);
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		_selection.add(he.getPair().getFace());
	    }
	}
	return _selection;
    }

    /**
     * Select all vertices on boundary.
     *
     * @return the h e_ selection
     */
    public HE_Selection selectAllBoundaryVertices() {
	final HE_Selection _selection = new HE_Selection(this);
	final Iterator<HE_Halfedge> heItr = heItr();
	HE_Halfedge he;
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		_selection.add(he.getVertex());
	    }
	}
	return _selection;
    }

    /**
     * Fuse all coplanar faces connected to face. New face can be concave.
     *
     * @param face
     *            starting face
     * @param a
     *            the a
     * @return new face
     */
    public HE_Face fuseCoplanarFace(final HE_Face face, final double a) {
	List<HE_Face> neighbors;
	FastTable<HE_Face> facesToCheck = new FastTable<HE_Face>();
	final FastTable<HE_Face> newFacesToCheck = new FastTable<HE_Face>();
	facesToCheck.add(face);
	final HE_Selection sel = new HE_Selection(this);
	sel.add(face);
	HE_Face f;
	HE_Face fn;
	int ni = -1;
	int nf = 0;
	double sa = Math.sin(a);
	sa *= sa;
	while (ni < nf) {
	    newFacesToCheck.clear();
	    for (int i = 0; i < facesToCheck.size(); i++) {
		f = facesToCheck.get(i);
		neighbors = f.getNeighborFaces();
		for (int j = 0; j < neighbors.size(); j++) {
		    fn = neighbors.get(j);
		    if (!sel.contains(fn)) {
			if (f.getFaceNormal()
				.isParallel(fn.getFaceNormal(), sa)) {
			    sel.add(fn);
			    newFacesToCheck.add(fn);
			}
		    }
		}
	    }
	    facesToCheck = newFacesToCheck;
	    ni = nf;
	    nf = sel.getNumberOfFaces();
	}
	if (sel.getNumberOfFaces() == 1) {
	    return face;
	}
	final List<HE_Halfedge> halfedges = sel.getOuterHalfedgesInside();
	final HE_Face newFace = new HE_Face();
	add(newFace);
	newFace.copyProperties(sel.faces.get(0));
	newFace.setHalfedge(halfedges.get(0));
	for (int i = 0; i < halfedges.size(); i++) {
	    final HE_Halfedge hei = halfedges.get(i);
	    final HE_Halfedge hep = halfedges.get(i).getPair();
	    for (int j = 0; j < halfedges.size(); j++) {
		final HE_Halfedge hej = halfedges.get(j);
		if ((i != j) && (hep.getVertex() == hej.getVertex())) {
		    hei.setNext(hej);
		    hej.setPrev(hei);
		}
	    }
	    hei.setFace(newFace);
	    hei.getVertex().setHalfedge(hei);
	}
	removeFaces(sel.getFacesAsArray());
	cleanUnusedElementsByFace();
	return newFace;
    }

    /**
     * Fuse all planar faces. Can lead to concave faces.
     *
     */
    public void fuseCoplanarFaces() {
	fuseCoplanarFaces(0);
    }

    /**
     * Fuse all planar faces. Can lead to concave faces.
     *
     * @param a
     *            the a
     */
    public void fuseCoplanarFaces(final double a) {
	int ni;
	int no;
	do {
	    ni = getNumberOfFaces();
	    final List<HE_Face> faces = this.getFacesAsList();
	    for (int i = 0; i < faces.size(); i++) {
		final HE_Face f = faces.get(i);
		if (contains(f)) {
		    fuseCoplanarFace(f, a);
		}
	    }
	    no = getNumberOfFaces();
	} while (no < ni);
    }

    /**
     * Remove all redundant vertices in straight edges.
     *
     */
    public void deleteCollinearVertices() {
	final Iterator<HE_Vertex> vItr = vItr();
	HE_Vertex v;
	HE_Halfedge he;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getVertexOrder() == 2) {
		he = v.getHalfedge();
		if (he.getHalfedgeTangent().isParallel(
			he.getNextInVertex().getHalfedgeTangent())) {
		    he.getPrevInFace().setNext(he.getNextInFace());
		    he.getPair().getPrevInFace()
		    .setNext(he.getPair().getNextInFace());
		    he.getPair().getNextInFace()
		    .setVertex(he.getNextInFace().getVertex());
		    if (he.getFace() != null) {
			if (he.getFace().getHalfedge() == he) {
			    he.getFace().setHalfedge(he.getNextInFace());
			}
		    }
		    if (he.getPair().getFace() != null) {
			if (he.getPair().getFace().getHalfedge() == he
				.getPair()) {
			    he.getPair().getFace()
			    .setHalfedge(he.getPair().getNextInFace());
			}
		    }
		    vItr.remove();
		    remove(he);
		    remove(he.getPair());
		}
	    }
	}
    }

    /**
     *
     */
    public void deleteDegenerateTriangles() {
	final List<HE_Face> faces = this.getFacesAsList();
	HE_Halfedge he;
	for (final HE_Face face : faces) {
	    if (!contains(face)) {
		continue; // face already removed by a previous change
	    }
	    if (face.isDegenerate()) {
		final int fo = face.getFaceOrder();
		if (fo == 3) {
		    HE_Halfedge degeneratehe = null;
		    he = face.getHalfedge();
		    do {
			if (isZero(he.getLength())) {
			    degeneratehe = he;
			    break;
			}
			he = he.getNextInFace();
		    } while (he != face.getHalfedge());
		    if (degeneratehe != null) {
			System.out.println("Zero length change!");
			collapseHalfedge(he);
			continue;
		    }
		    he = face.getHalfedge();
		    double d;
		    double dmax = 0;
		    do {
			d = he.getLength();
			if (d > dmax) {
			    degeneratehe = he;
			    dmax = d;
			}
			he = he.getNextInFace();
		    } while (he != face.getHalfedge());
		    System.out.println("Deleting longest edge: " + he);
		    deleteEdge(degeneratehe);
		}
	    }
	}
    }

    /**
     * Reset labels.
     */
    public void resetLabels() {
	resetVertexLabels();
	resetFaceLabels();
	resetEdgeLabels();
    }

    /**
     * Reset vertex labels.
     */
    public void resetVertexLabels() {
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    vItr.next().setLabel(-1);
	}
    }

    /**
     * Reset face labels.
     */
    public void resetFaceLabels() {
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    fItr.next().setLabel(-1);
	}
    }

    /**
     * Reset faces.
     */
    public void resetFaces() {
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    fItr.next().reset();
	}
    }

    /**
     * Reset edge labels.
     */
    public void resetEdgeLabels() {
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    eItr.next().setLabel(-1);
	}
    }

    /**
     * Reset labels.
     */
    public void resetInternalLabels() {
	resetVertexInternalLabels();
	resetFaceInternalLabels();
	resetEdgeInternalLabels();
    }

    /**
     * Reset vertex labels.
     */
    public void resetVertexInternalLabels() {
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    vItr.next().setInternalLabel(-1);
	}
    }

    /**
     * Reset face labels.
     */
    public void resetFaceInternalLabels() {
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    fItr.next().setInternalLabel(-1);
	}
    }

    /**
     * Reset edge labels.
     */
    public void resetEdgeInternalLabels() {
	final Iterator<HE_Halfedge> eItr = eItr();
	while (eItr.hasNext()) {
	    eItr.next().setInternalLabel(-1);
	}
    }

    /**
     * Label all faces of a selection.
     *
     * @param sel
     *            selection
     * @param label
     *            label to use
     */
    public void labelFaceSelection(final HE_Selection sel, final int label) {
	final Iterator<HE_Face> fItr = sel.fItr();
	while (fItr.hasNext()) {
	    fItr.next().setLabel(label);
	}
    }

    /**
     * Label edge selection.
     *
     * @param sel
     *            the sel
     * @param label
     *            the label
     */
    public void labelEdgeSelection(final HE_Selection sel, final int label) {
	final Iterator<HE_Halfedge> eItr = sel.eItr();
	while (eItr.hasNext()) {
	    eItr.next().setLabel(label);
	}
    }

    /**
     * Label vertex selection.
     *
     * @param sel
     *            the sel
     * @param label
     *            the label
     */
    public void labelVertexSelection(final HE_Selection sel, final int label) {
	final Iterator<HE_Vertex> vItr = sel.vItr();
	while (vItr.hasNext()) {
	    vItr.next().setLabel(label);
	}
    }

    /**
     * Return a KD-tree containing all face centers.
     *
     * @return WB_KDTree
     */
    public WB_KDTree<WB_Point, Long> getFaceTree() {
	final WB_KDTree<WB_Point, Long> tree = new WB_KDTree<WB_Point, Long>();
	HE_Face f;
	final Iterator<HE_Face> fItr = fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    tree.add(f.getFaceCenter(), f.key());
	}
	return tree;
    }

    /**
     * Return a KD-tree containing all vertices.
     *
     * @return WB_KDTree
     */
    public WB_KDTree<WB_Coordinate, Long> getVertexTree() {
	final WB_KDTree<WB_Coordinate, Long> tree = new WB_KDTree<WB_Coordinate, Long>();
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    tree.add(v, v.key());
	}
	return tree;
    }

    /**
     * Return the closest vertex on the mesh.
     *
     * @param p
     *            query point
     * @param vertexTree
     *            KD-tree from mesh (from vertexTree())
     * @return HE_Vertex closest vertex
     */
    public HE_Vertex getClosestVertex(final WB_Point p,
	    final WB_KDTree<WB_Point, Long> vertexTree) {
	final WB_KDEntry<WB_Point, Long>[] closestVertex = vertexTree
		.getNearestNeighbors(p, 1);
	if (closestVertex.length == 0) {
	    return null;
	}
	return getVertexByKey(closestVertex[0].value);
    }

    /**
     * Return the closest point on the mesh.
     *
     * @param p
     *            query point
     * @param vertexTree
     *            KD-tree from mesh (from vertexTree())
     * @return WB_Point closest point
     */
    public WB_Point getClosestPoint(final WB_Point p,
	    final WB_KDTree<WB_Point, Long> vertexTree) {
	final WB_KDEntry<WB_Point, Long>[] closestVertex = vertexTree
		.getNearestNeighbors(p, 1);
	final HE_Vertex v = getVertexByKey(closestVertex[0].value);
	if (v == null) {
	    return null;
	}
	final List<HE_Face> faces = v.getFaceStar();
	double d;
	double dmin = Double.POSITIVE_INFINITY;
	WB_Point result = new WB_Point();
	for (int i = 0; i < faces.size(); i++) {
	    final WB_Polygon poly = faces.get(i).toPolygon();
	    final WB_Point tmp = WB_GeometryOp.getClosestPoint3D(p, poly);
	    d = WB_GeometryOp.getSqDistance3D(tmp, p);
	    if (d < dmin) {
		dmin = d;
		result = tmp;
	    }
	}
	return result;
    }

    /**
     * Split the closest face in the query point.
     *
     * @param p
     *            query point
     * @param vertexTree
     *            KD-tree from mesh (from vertexTree())
     */
    public void addPointInClosestFace(final WB_Point p,
	    final WB_KDTree<WB_Coordinate, Long> vertexTree) {
	final WB_KDEntry<WB_Coordinate, Long>[] closestVertex = vertexTree
		.getNearestNeighbors(p, 1);
	final HE_Vertex v = getVertexByKey(closestVertex[0].value);
	final List<HE_Face> faces = v.getFaceStar();
	double d;
	double dmin = Double.POSITIVE_INFINITY;
	HE_Face face = new HE_Face();
	for (int i = 0; i < faces.size(); i++) {
	    final WB_Polygon poly = faces.get(i).toPolygon();
	    final WB_Point tmp = WB_GeometryOp.getClosestPoint3D(p, poly);
	    d = WB_GeometryOp.getSqDistance3D(tmp, p);
	    if (d < dmin) {
		dmin = d;
		face = faces.get(i);
		;
	    }
	}
	final HE_Vertex nv = HEM_TriSplit.splitFaceTri(this, face, p).vItr()
		.next();
	vertexTree.add(nv, nv.key());
    }

    /**
     *
     *
     * @param v1
     * @param v2
     * @return
     */
    public List<HE_Face> getSharedFaces(final HE_Vertex v1, final HE_Vertex v2) {
	final List<HE_Face> result = v1.getFaceStar();
	final List<HE_Face> compare = v2.getFaceStar();
	final Iterator<HE_Face> it = result.iterator();
	while (it.hasNext()) {
	    if (!compare.contains(it.next())) {
		it.remove();
	    }
	}
	return result;
    }

    /**
     *
     *
     * @return
     */
    public List<WB_Polygon> getBoundaryAsPolygons() {
	final List<WB_Polygon> polygons = new FastTable<WB_Polygon>();
	final List<HE_Halfedge> halfedges = getBoundaryHalfedges();
	final List<HE_Halfedge> loop = new FastTable<HE_Halfedge>();
	final List<WB_Coordinate> points = new FastTable<WB_Coordinate>();
	while (halfedges.size() > 0) {
	    points.clear();
	    loop.clear();
	    HE_Halfedge he = halfedges.get(0);
	    do {
		loop.add(he);
		points.add(he.getVertex());
		he = he.getNextInFace();
		if (loop.contains(he)) {
		    break;
		}
	    } while (he != halfedges.get(0));
	    polygons.add(gf.createSimplePolygon(points));
	    halfedges.removeAll(loop);
	}
	return polygons;
    }

    /**
     *
     *
     * @return
     */
    public List<HE_Halfedge> getBoundaryLoopHalfedges() {
	final List<HE_Halfedge> hes = new FastTable<HE_Halfedge>();
	final List<HE_Halfedge> halfedges = getBoundaryHalfedges();
	final List<HE_Halfedge> loop = new FastTable<HE_Halfedge>();
	while (halfedges.size() > 0) {
	    loop.clear();
	    HE_Halfedge he = halfedges.get(0);
	    hes.add(he);
	    do {
		loop.add(he);
		he = he.getNextInFace();
		if (loop.contains(he)) {
		    break;
		}
	    } while (he != halfedges.get(0));
	    halfedges.removeAll(loop);
	}
	return hes;
    }

    /**
     *
     *
     * @return
     */
    public HE_Path[] getBoundaryAsPath() {
	final List<HE_Halfedge> boundaryhes = getBoundaryLoopHalfedges();
	final HE_Path[] result = new HE_Path[boundaryhes.size()];
	for (int i = 0; i < boundaryhes.size(); i++) {
	    result[i] = new HE_Path(boundaryhes.get(i));
	}
	return result;
    }

    /**
     * @deprecated Use {@link #fixNonManifoldVertices()} instead
     */
    @Deprecated
    public void resolvePinchPoints() {
	fixNonManifoldVertices();
    }

    /**
     *
     *
     * @return
     */
    public boolean fixNonManifoldVertices() {
	class VertexInfo {
	    FastTable<HE_Halfedge> out;

	    VertexInfo() {
		out = new FastTable<HE_Halfedge>();
	    }
	}
	final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(
		1024, 0.5f, -1L);
	HE_Vertex v;
	VertexInfo vi;
	tracker.setDefaultStatus("Classifying halfedges per vertex.",
		getNumberOfHalfedges());
	for (final HE_Halfedge he : halfedges) {
	    v = he.getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.out.add(he);
	    tracker.incrementCounter();
	}
	final List<HE_Vertex> toUnweld = new FastTable<HE_Vertex>();
	tracker.setDefaultStatus("Checking vertex umbrellas.",
		getNumberOfVertices());
	Iterator<HE_Vertex> vItr = vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    final List<HE_Halfedge> outgoing = vertexLists.get(v.key()).out;
	    final List<HE_Halfedge> vStar = v.getHalfedgeStar();
	    if (outgoing.size() != vStar.size()) {
		toUnweld.add(v);
	    }
	}
	vItr = toUnweld.iterator();
	tracker.setDefaultStatus("Splitting vertex umbrellas. ",
		toUnweld.size());
	HE_Halfedge he;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    final List<HE_Halfedge> vHalfedges = vertexLists.get(v.key()).out;
	    final List<HE_Halfedge> vStar = v.getHalfedgeStar();
	    final HE_Vertex vc = new HE_Vertex(v);
	    add(vc);
	    for (int i = 0; i < vStar.size(); i++) {
		vStar.get(i).setVertex(vc);
	    }
	    vc.setHalfedge(vStar.get(0));
	    for (int i = 0; i < vHalfedges.size(); i++) {
		he = vHalfedges.get(i);
		if (he.getVertex() == v) {
		    v.setHalfedge(he);
		    break;
		}
	    }
	    tracker.incrementCounter();
	}
	return (toUnweld.size() > 0);
    }

    /**
     *
     *
     * @return
     */
    public double getArea() {
	final Iterator<HE_Face> fItr = fItr();
	double A = 0.0;
	while (fItr.hasNext()) {
	    A += fItr.next().getFaceArea();
	}
	return A;
    }

    /**
     * Triangulate face.
     *
     * @param key
     *            key of face
     * @return
     */
    public HE_Selection triangulate(final long key) {
	return triangulate(getFaceByKey(key));
    }

    /**
     *
     *
     * @param v
     * @return
     */
    public HE_Selection triangulateFaceStar(final HE_Vertex v) {
	final HE_Selection vf = new HE_Selection(this);
	final HE_VertexFaceCirculator vfc = new HE_VertexFaceCirculator(v);
	HE_Face f;
	while (vfc.hasNext()) {
	    f = vfc.next();
	    if (f != null) {
		if (f.getFaceOrder() > 3) {
		    if (!vf.contains(f)) {
			vf.add(f);
		    }
		}
	    }
	}
	return triangulate(vf);
    }

    /**
     *
     *
     * @param vertexkey
     * @return
     */
    public HE_Selection triangulateFaceStar(final long vertexkey) {
	final HE_Selection vf = new HE_Selection(this);
	final HE_VertexFaceCirculator vfc = new HE_VertexFaceCirculator(
		getVertexByKey(vertexkey));
	HE_Face f;
	while (vfc.hasNext()) {
	    f = vfc.next();
	    if (f != null) {
		if (f.getFaceOrder() > 3) {
		    if (!vf.contains(f)) {
			vf.add(f);
		    }
		}
	    }
	}
	return triangulate(vf);
    }

    /**
     *
     *
     * @param face
     * @return
     */
    public HE_Selection triangulate(final HE_Face face) {
	final HE_Selection sel = new HE_Selection(this);
	sel.add(face);
	return triangulate(sel);
    }

    /**
     * Triangulate all faces.
     *
     * @return
     */
    public HE_Selection triangulate() {
	final HEM_Triangulate tri = new HEM_Triangulate();
	modify(new HEM_Triangulate());
	return tri.triangles;
    }

    /**
     * Triangulate.
     *
     * @param sel
     *            the sel
     * @return
     */
    public HE_Selection triangulate(final HE_Selection sel) {
	final HEM_Triangulate tri = new HEM_Triangulate();
	modifySelected(tri, sel);
	return tri.triangles;
    }

    /**
     * Clean.
     */
    public void clean() {
	modify(new HEM_Clean());
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.core.WB_HasData#setData(java.lang.String, java.lang.Object)
     */
    @Override
    public void setData(final String s, final Object o) {
	if (data == null) {
	    data = new HashMap<String, Object>();
	}
	data.put(s, o);
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.core.WB_HasData#getData(java.lang.String)
     */
    @Override
    public Object getData(final String s) {
	return data.get(s);
    }

    /**
     * Smooth.
     */
    public void smooth() {
	subdivide(new HES_CatmullClark());
    }

    /**
     *
     *
     * @param rep
     */
    public void smooth(final int rep) {
	subdivide(new HES_CatmullClark(), rep);
    }

    /**
     * Fix loops.
     */
    public void fixLoops() {
	for (final HE_Halfedge he : getHalfedgesAsList()) {
	    if (he.getPrevInFace() == null) {
		HE_Halfedge hen = he.getNextInFace();
		while (hen.getNextInFace() != he) {
		    hen = hen.getNextInFace();
		}
		hen.setNext(he);
		he.setPrev(hen);
	    }
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.MESH;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public HE_Mesh apply(final WB_Transform T) {
	final HE_Mesh result = get();
	return result.transform(T);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Mesh#getFaceNormal(int)
     */
    @Override
    public WB_Vector getFaceNormal(final int id) {
	return getFaceByIndex(id).getFaceNormal();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Mesh#getFaceCenter(int)
     */
    @Override
    public WB_Point getFaceCenter(final int id) {
	return getFaceByIndex(id).getFaceCenter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Mesh#getVertexNormal(int)
     */
    @Override
    public WB_Vector getVertexNormal(final int i) {
	return getVertexByIndex(i).getVertexNormal();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Mesh#getVertex(int)
     */
    @Override
    public WB_Coordinate getVertex(final int i) {
	return getVertexByIndex(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Mesh#getEdgesAsInt()
     */
    @Override
    public int[][] getEdgesAsInt() {
	final int[][] result = new int[getNumberOfEdges()][2];
	final TLongIntMap vertexKeys = new TLongIntHashMap(10, 0.5f, -1L, -1);
	final Iterator<HE_Vertex> vItr = vItr();
	int i = 0;
	while (vItr.hasNext()) {
	    vertexKeys.put(vItr.next().key(), i);
	    i++;
	}
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge he;
	i = 0;
	while (eItr.hasNext()) {
	    he = eItr.next();
	    result[i][0] = vertexKeys.get(he.getVertex().key());
	    he = he.getPair();
	    result[i][1] = vertexKeys.get(he.getVertex().key());
	    i++;
	}
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Mesh#getPoints()
     */
    @Override
    public WB_CoordinateSequence getPoints() {
	return gf.createPointSequence(getVertices());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_HasColor#getColor()
     */
    @Override
    public int getColor() {
	return meshcolor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_HasColor#setColor(int)
     */
    @Override
    public void setColor(final int color) {
	meshcolor = color;
    }

    /**
     *
     *
     * @param color
     */
    public void setFaceColor(final int color) {
	final HE_FaceIterator fitr = new HE_FaceIterator(this);
	while (fitr.hasNext()) {
	    fitr.next().setColor(color);
	}
    }

    /**
     *
     *
     * @param color
     */
    public void setVertexColor(final int color) {
	final HE_VertexIterator vitr = new HE_VertexIterator(this);
	while (vitr.hasNext()) {
	    vitr.next().setColor(color);
	}
    }

    /**
     *
     *
     * @param color
     */
    public void setHalfedgeColor(final int color) {
	final HE_HalfedgeIterator heitr = new HE_HalfedgeIterator(this);
	while (heitr.hasNext()) {
	    heitr.next().setColor(color);
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setFaceColorWithLabel(final int color, final int i) {
	final HE_FaceIterator fitr = new HE_FaceIterator(this);
	HE_Face f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getLabel() == i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setFaceColorWithInternalLabel(final int color, final int i) {
	final HE_FaceIterator fitr = new HE_FaceIterator(this);
	HE_Face f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getInternalLabel() == i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setVertexColorWithLabel(final int color, final int i) {
	final HE_VertexIterator fitr = new HE_VertexIterator(this);
	HE_Vertex f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getLabel() == i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setVertexColorWithInternalLabel(final int color, final int i) {
	final HE_VertexIterator fitr = new HE_VertexIterator(this);
	HE_Vertex f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getInternalLabel() == i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setHalfedgeColorWithLabel(final int color, final int i) {
	final HE_HalfedgeIterator fitr = new HE_HalfedgeIterator(this);
	HE_Halfedge f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getLabel() == i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setHalfedgeColorWithInternalLabel(final int color, final int i) {
	final HE_HalfedgeIterator fitr = new HE_HalfedgeIterator(this);
	HE_Halfedge f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getInternalLabel() == i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setFaceColorWithOtherLabel(final int color, final int i) {
	final HE_FaceIterator fitr = new HE_FaceIterator(this);
	HE_Face f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getLabel() != i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setFaceColorWithOtherInternalLabel(final int color, final int i) {
	final HE_FaceIterator fitr = new HE_FaceIterator(this);
	HE_Face f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getInternalLabel() != i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setVertexColorWithOtherLabel(final int color, final int i) {
	final HE_VertexIterator fitr = new HE_VertexIterator(this);
	HE_Vertex f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getLabel() != i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setVertexColorWithOtherInternalLabel(final int color,
	    final int i) {
	final HE_VertexIterator fitr = new HE_VertexIterator(this);
	HE_Vertex f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getInternalLabel() != i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setHalfedgeColorWithOtherLabel(final int color, final int i) {
	final HE_HalfedgeIterator fitr = new HE_HalfedgeIterator(this);
	HE_Halfedge f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getLabel() != i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @param color
     * @param i
     */
    public void setHalfedgeColorWithOtherInternalLabel(final int color,
	    final int i) {
	final HE_HalfedgeIterator fitr = new HE_HalfedgeIterator(this);
	HE_Halfedge f;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    if (f.getInternalLabel() != i) {
		f.setColor(color);
	    }
	}
    }

    /**
     *
     *
     * @return
     */
    public int getGenus() {
	return (2 - ((getNumberOfVertices() - getNumberOfEdges()) + getNumberOfFaces())) / 2;
    }
}