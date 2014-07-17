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

	public HE_RAS<HE_Vertex> vertices;
	public HE_RAS<HE_Halfedge> halfedges;
	public HE_RAS<HE_Edge> edges;
	public HE_RAS<HE_Face> faces;
	/** The _data. */
	private HashMap<String, Object> _data;

	/**
	 * Instantiates a new HE_Selection.
	 */
	public HE_MeshStructure() {
		super();
		vertices = new HE_RAS<HE_Vertex>(1024);
		halfedges = new HE_RAS<HE_Halfedge>(1024);
		edges = new HE_RAS<HE_Edge>(1024);
		faces = new HE_RAS<HE_Face>(1024);
	}

	public HE_MeshStructure(final HE_MeshStructure ms) {
		this();
		for (final HE_Edge e : ms.edges) {
			add(e);
		}
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

	public void set(final HE_MeshStructure ms) {
		final HE_MeshStructure msc = ms.get();
		vertices = msc.vertices;
		halfedges = msc.halfedges;
		edges = msc.edges;
		faces = msc.faces;
	}

	public HE_MeshStructure get() {
		final HE_MeshStructure copy = new HE_MeshStructure();
		for (final HE_Edge e : edges) {
			copy.add(e);
		}
		for (final HE_Face f : faces) {
			copy.add(f);
		}
		for (final HE_Halfedge he : halfedges) {
			copy.add(he);
		}
		for (final HE_Vertex v : vertices) {
			copy.add(v);
		}
		return copy;
	}

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
	 * Add edge.
	 *
	 * @param e
	 *            edge to add
	 */
	public final void add(final HE_Edge e) {
		edges.add(e);

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
	 * Adds edges.
	 *
	 * @param edges
	 *            edges to add as HE_Edge[]
	 */
	public final void addEdges(final HE_Edge[] edges) {
		for (final HE_Edge edge : edges) {
			add(edge);
		}
	}

	/**
	 * Adds edges.
	 *
	 * @param edges
	 *            edges to add as FastTable<HE_Edge>
	 */
	public final void addEdges(final List<HE_Edge> edges) {
		for (int i = 0; i < edges.size(); i++) {
			add(edges.get(i));
		}
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
	public void clear() {
		clearVertices();
		clearEdges();
		clearHalfedges();
		clearFaces();
	}

	/**
	 * Clear edges.
	 */
	public final void clearEdges() {
		edges = new HE_RAS<HE_Edge>(1024);
	}

	/**
	 * Clear faces.
	 */
	public final void clearFaces() {
		faces = new HE_RAS<HE_Face>(1024);
	}

	/**
	 * Clear halfedges.
	 */
	public final void clearHalfedges() {
		halfedges = new HE_RAS<HE_Halfedge>(1024);
	}

	/**
	 * Clear vertices.
	 */
	public final void clearVertices() {
		vertices = new HE_RAS<HE_Vertex>(1024);
	}

	/**
	 * Check if structure contains edge.
	 *
	 * @param e
	 *            edge
	 * @return true, if successful
	 */
	public final boolean contains(final HE_Edge e) {
		return edges.contains(e);
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

	public final HE_Edge getEdgeByIndex(final int i) {
		return edges.get(i);
	}

	public final HE_Face getFaceByIndex(final int i) {
		return faces.get(i);
	}

	public final HE_Halfedge getHalfedgeByIndex(final int i) {
		return halfedges.get(i);
	}

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
	public final int getNumberOfEdges() {
		return edges.size();
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
	 * Removes edge.
	 *
	 * @param e
	 *            edge to remove
	 */
	public final void remove(final HE_Edge e) {
		edges.remove(e);
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

	public final void removeEdge(final int i) {
		if (i >= edges.size()) {
			throw new IllegalArgumentException("Edge index " + i
					+ " out of range!");
		}
		edges.removeAt(i);
	}

	/**
	 * Removes edges.
	 *
	 * @param edges
	 *            edges to remove as HE_Edge[]
	 */
	public final void removeEdges(final HE_Edge[] edges) {
		for (final HE_Edge edge : edges) {
			remove(edge);
		}
	}

	/**
	 * Removes edges.
	 *
	 * @param edges
	 *            edges to remove as List<HE_Edge>
	 */
	public final void removeEdges(final List<HE_Edge> edges) {
		for (int i = 0; i < edges.size(); i++) {
			remove(edges.get(i));
		}
	}

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
	public final void removeFaces(final List<HE_Face> faces) {
		for (int i = 0; i < faces.size(); i++) {
			remove(faces.get(i));
		}
	}

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
	public final void removeHalfedges(final List<HE_Halfedge> halfedges) {
		for (int i = 0; i < halfedges.size(); i++) {
			remove(halfedges.get(i));
		}
	}

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
	public final void removeVertices(final List<HE_Vertex> vertices) {
		for (int i = 0; i < vertices.size(); i++) {
			remove(vertices.get(i));
		}
	}

	public final List<HE_Vertex> getVertices() {
		return vertices.getObjects();
	}

	public final List<HE_Halfedge> getHalfedges() {
		return halfedges.getObjects();
	}

	public final List<HE_Edge> getEdges() {
		return edges.getObjects();
	}

	public final List<HE_Face> getFaces() {
		return faces.getObjects();
	}

	public final boolean containsEdge(final long key) {
		return edges.containsKey(key);
	}

	public final boolean containsFace(final long key) {
		return faces.containsKey(key);
	}

	public final boolean containsHalfedge(final long key) {
		return halfedges.containsKey(key);
	}

	public final boolean containsVertex(final long key) {
		return vertices.containsKey(key);
	}

	public final int getIndex(final HE_Edge e) {
		return edges.getIndex(e);
	}

	public final int getIndex(final HE_Face f) {
		return faces.getIndex(f);
	}

	public final int getIndex(final HE_Halfedge he) {
		return halfedges.getIndex(he);
	}

	public final int getIndex(final HE_Vertex v) {
		return vertices.getIndex(v);
	}

	/**
	 * Replace edges.
	 *
	 * @param edges
	 *            edges to replace with as HE_Edge[]
	 */
	public final void replaceEdges(final HE_Edge[] edges) {
		clearEdges();
		for (final HE_Edge edge : edges) {
			add(edge);
		}
	}

	/**
	 * Replace edges.
	 *
	 * @param edges
	 *            edges to replace with as List<HE_Edge>
	 */
	public final void replaceEdges(final List<HE_Edge> edges) {
		clearEdges();
		for (int i = 0; i < edges.size(); i++) {
			add(edges.get(i));
		}
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
	public Iterator<HE_Vertex> vItr() {
		return vertices.objects.iterator();
	}

	/**
	 * Edge iterator.
	 *
	 * @return edge iterator
	 */
	public Iterator<HE_Edge> eItr() {
		return edges.objects.iterator();
	}

	/**
	 * Hslfedge iterator.
	 *
	 * @return halfedge iterator
	 */
	public Iterator<HE_Halfedge> heItr() {
		return halfedges.objects.iterator();
	}

	public Iterator<HE_Face> fItr() {
		return faces.objects.iterator();
	}

	/**
	 * Get edge.
	 *
	 * @param key
	 *            edge key
	 * @return edge
	 */
	public final HE_Edge getEdgeByKey(final long key) {
		return edges.getByKey(key);
	}

	/**
	 * Edges as array.
	 *
	 * @return all edges as HE_Edge[]
	 */
	public final HE_Edge[] getEdgesAsArray() {
		final HE_Edge[] edges = new HE_Edge[getNumberOfEdges()];
		final Iterator<HE_Edge> eItr = this.edges.iterator();
		int i = 0;
		while (eItr.hasNext()) {
			edges[i] = eItr.next();
			i++;
		}
		return edges;
	}

	/**
	 * Edges as arrayList.
	 *
	 * @return all vertices as FastTable<HE_Edge>
	 */
	public final List<HE_Edge> getEdgesAsList() {
		final List<HE_Edge> edges = new FastTable<HE_Edge>();
		edges.addAll(this.edges);
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

	public HE_Path createPathFromIndices(final int[] vertices) {
		final HE_Path path = new HE_Path();
		if (vertices.length > 1) {
			HE_PathHalfedge phe = new HE_PathHalfedge();
			HE_Halfedge he = selectHalfedge(getVertexByIndex(vertices[0]),
					getVertexByIndex(vertices[1]));
			if (he == null) {
				throw new IllegalArgumentException("Two vertices "
						+ vertices[0] + " and " + vertices[1]
						+ " in path are not connected.");
			}
			phe.setHalfedge(he);
			path.setPathHalfedge(phe);
			HE_PathHalfedge prevphe = phe;
			for (int i = 1; i < vertices.length - 1; i++) {
				he = selectHalfedge(getVertexByIndex(vertices[i]),
						getVertexByIndex(vertices[i + 1]));
				if (he == null) {
					throw new IllegalArgumentException("Two vertices "
							+ vertices[i] + " and " + vertices[i + 1]
							+ " in path are not connected.");
				}
				phe = new HE_PathHalfedge();
				phe.setHalfedge(he);
				prevphe.setNext(phe);
				phe.setPrev(prevphe);
				prevphe = phe;
			}

		}
		return path;

	}

	public HE_Halfedge selectHalfedge(final HE_Vertex v0, final HE_Vertex v1) {
		final List<HE_Halfedge> hes = v0.getHalfedgeStar();
		for (final HE_Halfedge he : hes) {
			if (he.getEndVertex() == v1) {
				return he;
			}
		}
		return null;
	}

}
