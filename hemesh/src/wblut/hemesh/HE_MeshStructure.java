/*
 *
 */
package wblut.hemesh;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javolution.util.FastTable;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_HasData;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Collection of mesh elements. Contains methods to manipulate selections
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_MeshStructure extends HE_Element implements WB_HasData {
    /**
     *
     */
    public static final HET_ProgressTracker tracker = HET_ProgressTracker
	    .instance();

    /**
     *
     *
     * @return
     */
    public static String getStatus() {
	return tracker.getStatus();
    }

    /**
     *
     */
    public HE_RAS<HE_Vertex> vertices;
    /**
     *
     */
    public HE_RAS<HE_Halfedge> halfedges;
    /**
     *
     */
    public HE_RAS<HE_Face> faces;
    /**
     *
     */
    private HashMap<String, Object> _data;

    /**
     * Instantiates a new HE_Selection.
     */
    public HE_MeshStructure() {
	super();
	vertices = new HE_RASTrove<HE_Vertex>();
	halfedges = new HE_RASTrove<HE_Halfedge>();
	faces = new HE_RASTrove<HE_Face>();
    }

    /**
     *
     *
     * @param ms
     */
    public HE_MeshStructure(final HE_MeshStructure ms) {
	this();
	for (final HE_Face f : ms.faces) {
	    add(f);
	}
	for (final HE_Halfedge he : ms.halfedges) {
	    add(he);
	}
	for (final HE_Vertex v : ms.vertices) {
	    add(v);
	}
    }

    /*
     * 
     * public void set(final HE_MeshStructure ms) { final HE_MeshStructure msc =
     * ms.get(); vertices = msc.vertices; halfedges = msc.halfedges; faces =
     * msc.faces; }
     * 
     * 
     * public HE_MeshStructure get() { final HE_MeshStructure copy = new
     * HE_MeshStructure(); for (final HE_Face f : faces) { copy.add(f); } for
     * (final HE_Halfedge he : halfedges) { copy.add(he); } for (final HE_Vertex
     * v : vertices) { copy.add(v); } return copy; }
     */
    /**
     *
     *
     * @param n
     * @param ta
     */
    public void getFacesWithNormal(final WB_Coordinate n, final double ta) {
	final WB_Vector normal = geometryfactory.createNormalizedVector(n);
	final double cta = Math.cos(ta);
	for (final HE_Face f : faces) {
	    if (f.getFaceNormal().dot(normal) > cta) {
		add(f);
	    }
	}
    }

    /**
     * Add face.
     *
     * @param f
     *            face to add
     */
    public final void add(final HE_Face f) {
	faces.add(f);
    }

    /**
     * Adds Halfedge.
     *
     * @param he
     *            halfedge to add
     */
    public final void add(final HE_Halfedge he) {
	halfedges.add(he);
    }

    /**
     * Add vertex.
     *
     * @param v
     *            vertex to add
     */
    public final void add(final HE_Vertex v) {
	vertices.add(v);
    }

    /**
     * Adds faces.
     *
     * @param faces
     *            faces to add as HE_Face[]
     */
    public final void addFaces(final HE_Face[] faces) {
	for (final HE_Face face : faces) {
	    add(face);
	}
    }

    /**
     * Adds faces.
     *
     * @param faces
     *            faces to add as List<HE_Face>
     */
    public final void addFaces(final List<HE_Face> faces) {
	for (int i = 0; i < faces.size(); i++) {
	    add(faces.get(i));
	}
    }

    /**
     *
     *
     * @param source
     */
    public final void addFaces(final HE_Mesh source) {
	tracker.setDefaultStatus("Adding faces", source.getNumberOfFaces());
	for (final HE_Face f : source.faces) {
	    add(f);
	    tracker.incrementCounter();
	}
    }

    /**
     * Adds halfedges.
     *
     * @param halfedges
     *            halfedges to add as HE_Halfedge[]
     */
    public final void addHalfedges(final HE_Halfedge[] halfedges) {
	for (final HE_Halfedge halfedge : halfedges) {
	    add(halfedge);
	}
    }

    /**
     * Adds halfedges.
     *
     * @param halfedges
     *            halfedges to add as List<HE_Halfedge>
     */
    public final void addHalfedges(final List<HE_Halfedge> halfedges) {
	for (int i = 0; i < halfedges.size(); i++) {
	    add(halfedges.get(i));
	}
    }

    /**
     *
     *
     * @param source
     */
    public final void addHalfedges(final HE_Mesh source) {
	tracker.setDefaultStatus("Adding halfedges",
		source.getNumberOfHalfedges());
	for (final HE_Halfedge he : source.halfedges) {
	    add(he);
	    tracker.incrementCounter();
	}
    }

    /**
     * Adds vertices.
     *
     * @param vertices
     *            vertices to add as HE_Vertex[]
     */
    public final void addVertices(final HE_Vertex[] vertices) {
	for (final HE_Vertex vertex : vertices) {
	    add(vertex);
	}
    }

    /**
     *
     *
     * @param source
     */
    public final void addVertices(final HE_Mesh source) {
	tracker.setDefaultStatus("Adding vertices",
		source.getNumberOfVertices());
	for (final HE_Vertex vertex : source.vertices) {
	    add(vertex);
	    tracker.incrementCounter();
	}
    }

    /**
     * Adds vertices.
     *
     * @param vertices
     *            vertices to add as List<HE_Vertex>
     */
    public final void addVertices(final List<HE_Vertex> vertices) {
	for (int i = 0; i < vertices.size(); i++) {
	    add(vertices.get(i));
	}
    }

    /**
     * Clear entire structure.
     */
    @Override
    public void clear() {
	clearVertices();
	clearHalfedges();
	clearFaces();
    }

    /**
     * Clear faces.
     */
    public final void clearFaces() {
	faces = new HE_RASTrove<HE_Face>();
    }

    /**
     * Clear halfedges.
     */
    public final void clearHalfedges() {
	halfedges = new HE_RASTrove<HE_Halfedge>();
    }

    /**
     * Clear vertices.
     */
    public final void clearVertices() {
	vertices = new HE_RASTrove<HE_Vertex>();
    }

    /**
     * Check if structure contains face.
     *
     * @param f
     *            face
     * @return true, if successful
     */
    public final boolean contains(final HE_Face f) {
	return faces.contains(f);
    }

    /**
     * Check if structure contains halfedge.
     *
     * @param he
     *            halfedge
     * @return true, if successful
     */
    public final boolean contains(final HE_Halfedge he) {
	return halfedges.contains(he);
    }

    /**
     * Check if structure contains vertex.
     *
     * @param v
     *            vertex
     * @return true, if successful
     */
    public final boolean contains(final HE_Vertex v) {
	return vertices.contains(v);
    }

    /**
     * Get axis-aligned bounding box surrounding mesh.
     *
     * @return WB_AABB axis-aligned bounding box
     */
    public final WB_AABB getAABB() {
	final double[] result = getLimits();
	final WB_Point min = geometryfactory.createPoint(result[0], result[1],
		result[2]);
	final WB_Point max = geometryfactory.createPoint(result[3], result[4],
		result[5]);
	return new WB_AABB(min, max);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public final HE_Face getFaceByIndex(final int i) {
	return faces.get(i);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public final HE_Halfedge getHalfedgeByIndex(final int i) {
	return halfedges.get(i);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public final HE_Vertex getVertexByIndex(final int i) {
	return vertices.get(i);
    }

    /**
     * Get range of vertex coordinates.
     *
     * @return array of limit values: min x, min y, min z, max x, max y, max z
     */
    public final double[] getLimits() {
	final double[] result = new double[6];
	for (int i = 0; i < 3; i++) {
	    result[i] = Double.POSITIVE_INFINITY;
	}
	for (int i = 3; i < 6; i++) {
	    result[i] = Double.NEGATIVE_INFINITY;
	}
	HE_Vertex v;
	for (int i = 0; i < vertices.size(); i++) {
	    v = getVertexByIndex(i);
	    result[0] = Math.min(result[0], v.xd());
	    result[1] = Math.min(result[1], v.yd());
	    result[2] = Math.min(result[2], v.zd());
	    result[3] = Math.max(result[3], v.xd());
	    result[4] = Math.max(result[4], v.yd());
	    result[5] = Math.max(result[5], v.zd());
	}
	return result;
    }

    /**
     *
     *
     * @return
     */
    public final double[] limits() {
	final double[] result = new double[6];
	for (int i = 0; i < 3; i++) {
	    result[i] = Double.POSITIVE_INFINITY;
	}
	for (int i = 3; i < 6; i++) {
	    result[i] = Double.NEGATIVE_INFINITY;
	}
	HE_Vertex v;
	for (int i = 0; i < vertices.size(); i++) {
	    v = getVertexByIndex(i);
	    result[0] = Math.min(result[0], v.xd());
	    result[1] = Math.min(result[1], v.yd());
	    result[2] = Math.min(result[2], v.zd());
	    result[3] = Math.max(result[3], v.xd());
	    result[4] = Math.max(result[4], v.yd());
	    result[5] = Math.max(result[5], v.zd());
	}
	return result;
    }

    /**
     * Number of edges.
     *
     * @return the number of edges
     */
    public int getNumberOfEdges() {
	return halfedges.size() / 2;
    }

    /**
     * Number of faces.
     *
     * @return the number of faces
     */
    public final int getNumberOfFaces() {
	return faces.size();
    }

    /**
     * Number of halfedges.
     *
     * @return the number of halfedges
     */
    public final int getNumberOfHalfedges() {
	return halfedges.size();
    }

    /**
     * Number of vertices.
     *
     * @return the number of vertices
     */
    public final int getNumberOfVertices() {
	return vertices.size();
    }

    /**
     * Removes face.
     *
     * @param f
     *            face to remove
     */
    public final void remove(final HE_Face f) {
	faces.remove(f);
    }

    /**
     * Removes halfedge.
     *
     * @param he
     *            halfedge to remove
     */
    public final void remove(final HE_Halfedge he) {
	halfedges.remove(he);
    }

    /**
     * Removes vertex.
     *
     * @param v
     *            vertex to remove
     */
    public final void remove(final HE_Vertex v) {
	vertices.remove(v);
    }

    /**
     *
     *
     * @param i
     */
    public final void removeFace(final int i) {
	if (i >= faces.size()) {
	    throw new IllegalArgumentException("Face index " + i
		    + " out of range!");
	}
	faces.removeAt(i);
    }

    /**
     * Removes faces.
     *
     * @param faces
     *            faces to remove as HE_Face[]
     */
    public final void removeFaces(final HE_Face[] faces) {
	for (final HE_Face face : faces) {
	    remove(face);
	}
    }

    /**
     * Removes faces.
     *
     * @param faces
     *            faces to remove as List<HE_Face>
     */
    public final void removeFaces(final Collection<HE_Face> faces) {
	for (final HE_Face f : faces) {
	    remove(f);
	}
    }

    /**
     *
     *
     * @param i
     */
    public final void removeHalfedge(final int i) {
	if (i >= halfedges.size()) {
	    throw new IllegalArgumentException("Halfedge index " + i
		    + " out of range!");
	}
	halfedges.removeAt(i);
    }

    /**
     * Removes halfedges.
     *
     * @param halfedges
     *            halfedges to remove as HE_Halfedge[]
     */
    public final void removeHalfedges(final HE_Halfedge[] halfedges) {
	for (final HE_Halfedge halfedge : halfedges) {
	    remove(halfedge);
	}
    }

    /**
     * Removes halfedges.
     *
     * @param halfedges
     *            halfedges to remove as FastTable<HE_Halfedge>
     */
    public final void removeHalfedges(final Collection<HE_Halfedge> halfedges) {
	for (final HE_Halfedge he : halfedges) {
	    remove(he);
	}
    }

    /**
     *
     *
     * @param i
     */
    public final void removeVertex(final int i) {
	if (i >= vertices.size()) {
	    throw new IllegalArgumentException("Vertex index " + i
		    + " out of range!");
	}
	vertices.removeAt(i);
    }

    /**
     * Removes vertices.
     *
     * @param vertices
     *            vertices to remove as HE_Vertex[]
     */
    public final void removeVertices(final HE_Vertex[] vertices) {
	for (final HE_Vertex vertice : vertices) {
	    remove(vertice);
	}
    }

    /**
     * Removes vertices.
     *
     * @param vertices
     *            vertices to remove as FastTable<HE_Vertex>
     */
    public final void removeVertices(final Collection<HE_Vertex> vertices) {
	for (final HE_Vertex v : vertices) {
	    remove(v);
	}
    }

    /**
     *
     *
     * @return
     */
    public final List<HE_Vertex> getVertices() {
	return vertices.getObjects();
    }

    /**
     *
     *
     * @return
     */
    public final List<HE_Halfedge> getHalfedges() {
	return halfedges.getObjects();
    }

    /**
     *
     *
     * @return
     */
    public final List<HE_Halfedge> getEdges() {
	final List<HE_Halfedge> edges = new FastTable<HE_Halfedge>();
	final HE_HalfedgeIterator heitr = new HE_HalfedgeIterator(this);
	HE_Halfedge he;
	while (heitr.hasNext()) {
	    he = heitr.next();
	    if (he.isEdge()) {
		edges.add(he);
	    }
	}
	return edges;
    }

    /**
     *
     *
     * @return
     */
    public final List<HE_Face> getFaces() {
	return faces.getObjects();
    }

    /**
     *
     *
     * @param key
     * @return
     */
    public final boolean containsFace(final long key) {
	return faces.containsKey(key);
    }

    /**
     *
     *
     * @param key
     * @return
     */
    public final boolean containsHalfedge(final long key) {
	return halfedges.containsKey(key);
    }

    /**
     *
     *
     * @param key
     * @return
     */
    public final boolean containsVertex(final long key) {
	return vertices.containsKey(key);
    }

    /**
     *
     *
     * @param f
     * @return
     */
    public final int getIndex(final HE_Face f) {
	return faces.getIndex(f);
    }

    /**
     *
     *
     * @param he
     * @return
     */
    public final int getIndex(final HE_Halfedge he) {
	return halfedges.getIndex(he);
    }

    /**
     *
     *
     * @param v
     * @return
     */
    public final int getIndex(final HE_Vertex v) {
	return vertices.getIndex(v);
    }

    /**
     * Replace faces.
     *
     * @param faces
     *            faces to replace with as HE_Face[]
     */
    public final void replaceFaces(final HE_Face[] faces) {
	clearFaces();
	for (final HE_Face face : faces) {
	    add(face);
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public final void replaceFaces(final HE_Mesh mesh) {
	faces = mesh.faces;
    }

    /**
     *
     *
     * @param mesh
     */
    public final void replaceVertices(final HE_Mesh mesh) {
	vertices = mesh.vertices;
    }

    /**
     *
     *
     * @param mesh
     */
    public final void replaceHalfedges(final HE_Mesh mesh) {
	halfedges = mesh.halfedges;
    }

    /**
     * Replace faces.
     *
     * @param faces
     *            faces to replace with as List<HE_Face>
     */
    public final void replaceFaces(final List<HE_Face> faces) {
	clearFaces();
	for (int i = 0; i < faces.size(); i++) {
	    add(faces.get(i));
	}
    }

    /**
     * Replace halfedges.
     *
     * @param halfedges
     *            halfedges to replace with as HE_Halfedge[]
     */
    public final void replaceHalfedges(final HE_Halfedge[] halfedges) {
	clearHalfedges();
	for (final HE_Halfedge halfedge : halfedges) {
	    add(halfedge);
	}
    }

    /**
     * Replace halfedges.
     *
     * @param halfedges
     *            halfedges to replace with as List<HE_Halfedge>
     */
    public final void replaceHalfedges(final List<HE_Halfedge> halfedges) {
	clearHalfedges();
	for (int i = 0; i < halfedges.size(); i++) {
	    add(halfedges.get(i));
	}
    }

    /**
     * Replace vertices.
     *
     * @param vertices
     *            vertices to replace with as HE_Vertex[]
     */
    public final void replaceVertices(final HE_Vertex[] vertices) {
	clearVertices();
	for (final HE_Vertex vertice : vertices) {
	    add(vertice);
	}
    }

    /**
     * Replace vertices.
     *
     * @param vertices
     *            vertices to replace with as List<HE_Vertex>
     */
    public final void replaceVertices(final List<HE_Vertex> vertices) {
	clearVertices();
	for (int i = 0; i < vertices.size(); i++) {
	    add(vertices.get(i));
	}
    }

    /**
     * Vertex iterator.
     *
     * @return vertex iterator
     */
    public HE_VertexIterator vItr() {
	return new HE_VertexIterator(this);
    }

    /**
     * Edge iterator.
     *
     * @return edge iterator
     */
    public HE_EdgeIterator eItr() {
	return new HE_EdgeIterator(this);
    }

    /**
     * Hslfedge iterator.
     *
     * @return halfedge iterator
     */
    public HE_HalfedgeIterator heItr() {
	return new HE_HalfedgeIterator(this);
    }

    /**
     *
     *
     * @return
     */
    public HE_FaceIterator fItr() {
	return new HE_FaceIterator(this);
    }

    /**
     * Edges as array.
     *
     * @return all edges as HE_Halfedge[]
     */
    public final HE_Halfedge[] getEdgesAsArray() {
	final HE_Halfedge[] edges = new HE_Halfedge[getNumberOfEdges()];
	final Iterator<HE_Halfedge> eItr = heItr();
	int i = 0;
	HE_Halfedge he;
	while (eItr.hasNext()) {
	    he = eItr.next();
	    if (he.isEdge()) {
		edges[i] = he;
		i++;
	    }
	}
	return edges;
    }

    /**
     * Edges as arrayList.
     *
     * @return all vertices as FastTable<HE_Halfedge>
     */
    public final List<HE_Halfedge> getEdgesAsList() {
	final List<HE_Halfedge> edges = new FastTable<HE_Halfedge>();
	final Iterator<HE_Halfedge> eItr = eItr();
	HE_Halfedge he;
	while (eItr.hasNext()) {
	    he = eItr.next();
	    edges.add(he);
	}
	return (edges);
    }

    /**
     * Get face.
     *
     * @param key
     *            face key
     * @return face
     */
    public final HE_Face getFaceByKey(final long key) {
	return faces.getByKey(key);
    }

    /**
     * Faces as array.
     *
     * @return all faces as HE_Face[]
     */
    public final HE_Face[] getFacesAsArray() {
	final HE_Face[] faces = new HE_Face[getNumberOfFaces()];
	final Iterator<HE_Face> fItr = this.faces.iterator();
	int i = 0;
	while (fItr.hasNext()) {
	    faces[i] = fItr.next();
	    i++;
	}
	return faces;
    }

    /**
     * Faces as arrayList.
     *
     * @return all vertices as FastTable<HE_Face>
     */
    public final List<HE_Face> getFacesAsList() {
	final List<HE_Face> faces = new FastTable<HE_Face>();
	faces.addAll(this.faces);
	return (faces);
    }

    /**
     * Get halfedge.
     *
     * @param key
     *            halfedge key
     * @return halfedge
     */
    public final HE_Halfedge getHalfedgeByKey(final long key) {
	return halfedges.getByKey(key);
    }

    /**
     * Halfedges as array.
     *
     * @return all halfedges as HE_Halfedge[]
     */
    public final HE_Halfedge[] getHalfedgesAsArray() {
	final HE_Halfedge[] halfedges = new HE_Halfedge[getNumberOfHalfedges()];
	final Iterator<HE_Halfedge> heItr = this.halfedges.iterator();
	int i = 0;
	while (heItr.hasNext()) {
	    halfedges[i] = heItr.next();
	    i++;
	}
	return halfedges;
    }

    /**
     * Halfedges as arrayList.
     *
     * @return all vertices as FastTable<HE_Halfedge>
     */
    public final List<HE_Halfedge> getHalfedgesAsList() {
	final List<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
	halfedges.addAll(this.halfedges);
	return (halfedges);
    }

    /**
     * Get vertex.
     *
     * @param key
     *            vertex key
     * @return vertex
     */
    public final HE_Vertex getVertexByKey(final long key) {
	return vertices.getByKey(key);
    }

    /**
     * Vertices as array.
     *
     * @return all vertices as HE_Vertex[]
     */
    public final HE_Vertex[] getVerticesAsArray() {
	final HE_Vertex[] vertices = new HE_Vertex[getNumberOfVertices()];
	final Collection<HE_Vertex> _vertices = this.vertices;
	final Iterator<HE_Vertex> vitr = _vertices.iterator();
	int i = 0;
	while (vitr.hasNext()) {
	    vertices[i] = vitr.next();
	    i++;
	}
	return vertices;
    }

    /**
     * Vertices as arrayList.
     *
     * @return all vertices as FastTable<HE_Vertex>
     */
    public final List<HE_Vertex> getVerticesAsList() {
	final List<HE_Vertex> vertices = new FastTable<HE_Vertex>();
	final Collection<HE_Vertex> _vertices = this.vertices;
	vertices.addAll(_vertices);
	return (vertices);
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.core.WB_HasData#setData(java.lang.String, java.lang.Object)
     */
    @Override
    public void setData(final String s, final Object o) {
	if (_data == null) {
	    _data = new HashMap<String, Object>();
	}
	_data.put(s, o);
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.core.WB_HasData#getData(java.lang.String)
     */
    @Override
    public Object getData(final String s) {
	return _data.get(s);
    }

    /**
     *
     *
     * @param vertices
     * @param loop
     * @return
     */
    public HE_Path createPathFromIndices(final int[] vertices,
	    final boolean loop) {
	final List<HE_Halfedge> halfedges = new FastTable<HE_Halfedge>();
	if (vertices.length > 1) {
	    HE_Halfedge he;
	    for (int i = 0; i < (vertices.length - 1); i++) {
		he = searchHalfedgeFromTo(getVertexByIndex(vertices[i]),
			getVertexByIndex(vertices[i + 1]));
		if (he == null) {
		    throw new IllegalArgumentException("Two vertices "
			    + vertices[i] + " and " + vertices[i + 1]
			    + " in path are not connected.");
		}
		halfedges.add(he);
	    }
	    if (loop) {
		he = searchHalfedgeFromTo(
			getVertexByIndex(vertices[vertices.length - 1]),
			getVertexByIndex(vertices[0]));
		if (he == null) {
		    throw new IllegalArgumentException("Vertices "
			    + vertices[vertices.length - 1] + " and "
			    + vertices[0]
			    + " in path are not connected: path is not a loop.");
		}
	    }
	}
	final HE_Path path = new HE_Path(halfedges, loop);
	return path;
    }

    /**
     *
     *
     * @param v0
     * @param v1
     * @return
     */
    public HE_Halfedge searchHalfedgeFromTo(final HE_Vertex v0,
	    final HE_Vertex v1) {
	final List<HE_Halfedge> hes = v0.getHalfedgeStar();
	for (final HE_Halfedge he : hes) {
	    if (he.getEndVertex() == v1) {
		return he;
	    }
	}
	return null;
    }
}
