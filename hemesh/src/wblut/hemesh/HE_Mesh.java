package wblut.hemesh;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongIntMap;
import gnu.trove.map.TLongLongMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongIntHashMap;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastTable;

import org.apache.log4j.Logger;

import wblut.geom.WB_AABB;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateSequence;
import wblut.geom.WB_Distance;
import wblut.geom.WB_FaceListMesh;
import wblut.geom.WB_Frame;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryType;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_HasData;
import wblut.geom.WB_IndexedSegment;
import wblut.geom.WB_Intersection;
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
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
	/** Stored mesh center. */
	private WB_Point _center;

	/** Status of mesh center. */
	private boolean _centerUpdated;

	/** General purpose label. */
	protected int label;

	/** The _data. */
	private HashMap<String, Object> _data;

	private static Logger logger = Logger.getLogger(HE_Mesh.class);

	private int meshcolor;

	/**
	 * Instantiates a new HE_Mesh.
	 *
	 */
	public HE_Mesh() {
		super();
		_center = new WB_Point();
		_centerUpdated = false;
		label = -1;
	}

	// CREATE

	/**
	 * Constructor.
	 *
	 * @param creator
	 *            HE_Creator that generates this mesh
	 */
	public HE_Mesh(final HEC_Creator creator) {
		super();
		setNoCopy(creator.create());
		_centerUpdated = false;
		label = -1;
	}

	public HE_Mesh(final WB_Mesh mesh) {
		this(new HEC_FromMesh(mesh));
	}

	public HE_Mesh(final WB_MeshCreator mesh) {
		this(new HEC_FromMesh(mesh.getMesh()));
	}

	// MODIFY

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
		return modifier.apply(selection.get());
	}

	// SUBDIVIDE

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
	public HE_Mesh simplify(final HES_Simplifier simplifier,
			final HE_Selection selection) {
		return simplifier.apply(selection);

	}

	/**
	 * Deep copy of mesh.
	 *
	 * @return copy as new HE_Mesh, includes selection
	 */
	@Override
	public HE_Mesh get() {
		final HE_Mesh result = new HE_Mesh();
		final TLongLongMap vertexCorrelation = new TLongLongHashMap(10, 0.5f,
				-1L, -1L);
		final TLongLongMap faceCorrelation = new TLongLongHashMap(10, 0.5f,
				-1L, -1L);
		final TLongLongMap halfedgeCorrelation = new TLongLongHashMap(10, 0.5f,
				-1L, -1L);
		HE_Vertex rv;
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			rv = new HE_Vertex(v);
			result.add(rv);
			rv.copyProperties(v);
			vertexCorrelation.put(v.key(), rv.key());
		}
		HE_Face rf;
		HE_Face f;
		final Iterator<HE_Face> fItr = fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			rf = new HE_Face();
			result.add(rf);
			rf.copyProperties(f);
			faceCorrelation.put(f.key(), rf.key());
		}
		HE_Halfedge rhe;
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			rhe = new HE_Halfedge();
			result.add(rhe);
			rhe.copyProperties(he);
			halfedgeCorrelation.put(he.key(), rhe.key());
		}

		HE_Vertex sv;
		HE_Vertex tv;
		final Iterator<HE_Vertex> svItr = vItr();
		final Iterator<HE_Vertex> tvItr = result.vItr();
		Long key;
		while (svItr.hasNext()) {
			sv = svItr.next();
			tv = tvItr.next();
			tv.set(sv);
			if (sv.getHalfedge() != null) {
				key = halfedgeCorrelation.get(sv.getHalfedge().key());
				tv.setHalfedge(result.getHalfedgeByKey(key));
			}
		}

		HE_Face sf;
		HE_Face tf;

		final Iterator<HE_Face> sfItr = fItr();
		final Iterator<HE_Face> tfItr = result.fItr();
		while (sfItr.hasNext()) {
			sf = sfItr.next();
			tf = tfItr.next();
			if (sf.getHalfedge() != null) {
				key = halfedgeCorrelation.get(sf.getHalfedge().key());
				tf.setHalfedge(result.getHalfedgeByKey(key));
			}

		}

		HE_Halfedge she;
		HE_Halfedge the;

		final Iterator<HE_Halfedge> sheItr = heItr();
		final Iterator<HE_Halfedge> theItr = result.heItr();
		while (sheItr.hasNext()) {
			she = sheItr.next();
			the = theItr.next();
			if (she.getPair() != null) {
				key = halfedgeCorrelation.get(she.getPair().key());
				the.setPair(result.getHalfedgeByKey(key));
				result.getHalfedgeByKey(key).setPair(the);
			}
			if (she.getNextInFace() != null) {
				key = halfedgeCorrelation.get(she.getNextInFace().key());
				the.setNext(result.getHalfedgeByKey(key));
				result.getHalfedgeByKey(key).setPrev(the);
			}
			if (she.getVertex() != null) {
				key = vertexCorrelation.get(she.getVertex().key());
				the.setVertex(result.getVertexByKey(key));
			}
			if (she.getFace() != null) {
				key = faceCorrelation.get(she.getFace().key());
				the.setFace(result.getFaceByKey(key));
			}

		}
		result._center.set(_center);
		result._centerUpdated = _centerUpdated;
		return result;
	}

	/**
	 * Add all mesh elements to this mesh. No copies are made.
	 *
	 * @param mesh
	 *            mesh to add
	 */
	public void add(final HE_Mesh mesh) {
		addVertices(mesh.getVerticesAsArray());
		addFaces(mesh.getFacesAsArray());

		addHalfedges(mesh.getHalfedgesAsArray());

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
		set(new HE_Mesh(new HEC_FromPolygons().setPolygons(this
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
		replaceVertices(result.getVerticesAsArray());
		replaceFaces(result.getFacesAsArray());
		replaceHalfedges(result.getHalfedgesAsArray());

	}

	/**
	 * Replace mesh with shallow copy of target.
	 *
	 * @param target
	 *            HE_Mesh to be duplicated
	 */
	private void setNoCopy(final HE_Mesh target) {
		vertices = target.vertices;
		halfedges = target.halfedges;

		faces = target.faces;

		_center = target._center;
		_centerUpdated = target._centerUpdated;

	}

	// CONVERT

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
		_center.set(0, 0, 0);
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
		_center.set(0, 0, 0);
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

	// TRANSFORM
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
		_center.addSelf(x, y, z);
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
		if (!_centerUpdated) {
			getCenter();
		}
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			vItr.next()
			.getPoint()
			.addSelf(x - _center.xd(), y - _center.yd(),
					z - _center.zd());
		}
		_center.set(x, y, z);
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
		if (!_centerUpdated) {
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
		raa.applySelfAsPoint(_center);
		;
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
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Point p1,
			final WB_Point p2) {
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, p2.subToVector(p1));
		while (vItr.hasNext()) {
			v = vItr.next();
			raa.applySelfAsPoint(v);
		}
		raa.applySelfAsPoint(_center);

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
	public HE_Mesh rotateAboutAxis(final double angle, final WB_Point p,
			final WB_Vector a) {
		if (!_centerUpdated) {
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
		raa.applySelfAsPoint(_center);
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
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(c.xd() + scaleFactorx * (v.xd() - c.xd()), c.yd()
					+ scaleFactory * (v.yd() - c.yd()), c.zd() + scaleFactorz
					* (v.zd() - c.zd()));
		}
		_center.set(c.xd() + scaleFactorx * (-c.xd() + _center.xd()), c.yd()
				+ scaleFactory * (-c.yd() + _center.yd()), c.zd()
				+ scaleFactorz * (-c.zd() + _center.zd()));
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
		if (!_centerUpdated) {
			getCenter();
		}
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			v.set(_center.xd() + scaleFactorx * (v.xd() - _center.xd()),
					_center.yd() + scaleFactory * (v.yd() - _center.yd()),
					_center.zd() + scaleFactorz * (v.zd() - _center.zd()));
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

	// DERIVED ELEMENTS

	/**
	 * Get the center (average of all vertex positions).
	 *
	 * @return the center
	 */
	@Override
	public WB_Point getCenter() {
		if (_centerUpdated) {
			return _center;
		}
		else {
			resetCenter();
			return _center;
		}
	}

	/**
	 * Reset the center to the average of all vertex positions).
	 *
	 */
	public void resetCenter() {
		_center.set(0, 0, 0);
		final Iterator<HE_Vertex> vItr = vItr();
		while (vItr.hasNext()) {
			_center.addSelf(vItr.next());
		}
		_center.divSelf(getNumberOfVertices());
		_centerUpdated = true;

	}

	// HELPERS

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
			for (int j = 0; j < n - 1; j++) {
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
		final List<HE_Halfedge> unpairedHalfedges = new FastTable<HE_Halfedge>();
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair() == null) {
				unpairedHalfedges.add(he);
			}
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
		// System.out.println("HE_Mesh : collating " + unpairedHalfedges.size()
		// + " unpaired halfedges per vertex.");
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
		}
		HE_Halfedge he;
		HE_Halfedge he2;

		// System.out.println("HE_Mesh : pairing unpaired halfedges per vertex.");
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
		}

	}

	/**
	 * Pair halfedges.
	 *
	 * @param unpairedHalfedges
	 *            the unpaired halfedges
	 */
	public void pairHalfedges(final List<HE_Halfedge> unpairedHalfedges) {
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
		// System.out.println("HE_Mesh : collating " + unpairedHalfedges.size()
		// + " unpaired halfedges per vertex.");
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
		}
		HE_Halfedge he;
		HE_Halfedge he2;

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
		}
	}

	/**
	 * Cap all remaining unpaired halfedges. Only use after pairHalfedges();
	 */
	public void capHalfedges() {
		final List<HE_Halfedge> unpairedHalfedges = getUnpairedHalfedges();
		final int nuh = unpairedHalfedges.size();
		final HE_Halfedge[] newHalfedges = new HE_Halfedge[nuh];
		HE_Halfedge he1, he2;

		for (int i = 0; i < nuh; i++) {
			he1 = unpairedHalfedges.get(i);
			he2 = new HE_Halfedge();
			he2.setVertex(he1.getNextInFace().getVertex());
			he1.setPair(he2);
			he2.setPair(he1);
			newHalfedges[i] = he2;
			add(he2);

		}

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
		}
	}

	/**
	 * Uncap halfedges.
	 */
	public void uncapBoundaryHalfedgesAndRemoveBoundaryEdges() {
		final Iterator<HE_Halfedge> heItr = heItr();
		HE_Halfedge he;
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				he.getVertex().setHalfedge(he.getPair());

				he.getPair().clearPair();
				he.clearPair();
				heItr.remove();
			}

		}

	}

	/**
	 * Cap holes.
	 *
	 * @return all new faces as FastTable<HE_Face>
	 */
	public List<HE_Face> capHoles() {
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

		}
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

		HE_Halfedge he;

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
		}
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

		}
		replaceVertices(cleanedVertices.getObjects());
		replaceHalfedges(cleanedHalfedges.getObjects());

		return this;
	}

	// MESH OPERATIONS

	/**
	 * Reverse all faces. Flips normals.
	 *
	 * @return the h e_ mesh
	 */
	public HE_Mesh flipAllFaces() {
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
		}
		prevHe = new HE_Halfedge[getNumberOfHalfedges()];
		int i = 0;
		Iterator<HE_Halfedge> heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			prevHe[i] = he.getPrevInFace();
			i++;
		}
		i = 0;
		heItr = heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			he.setNext(prevHe[i]);
			prevHe[i].setPrev(he);
			i++;

		}
		return this;
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
				}
				else {
					vp.set(v);
				}
			}
			else {
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

	public void deleteTwoEdgeVertex(final HE_Vertex v) {
		if (contains(v) && v.getVertexOrder() == 2) {
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
			if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(e.getVertex(),
					e.getEndVertex()))) {
				edgesToRemove.add(e);
			}
		}
		for (int i = 0; i < edgesToRemove.size(); i++) {
			collapseEdge(edgesToRemove.get(i));
		}
	}

	public void collapseDegenerateEdges(final double d) {
		final FastTable<HE_Halfedge> edgesToRemove = new FastTable<HE_Halfedge>();
		final Iterator<HE_Halfedge> eItr = eItr();
		HE_Halfedge e;
		final double d2 = d * d;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (WB_Distance.getSqDistance3D(e.getVertex(), e.getEndVertex()) < d2) {
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
		he0new.setVertex(vNew);
		he1new.setVertex(vNew);
		vNew.setHalfedge(he0new);
		he0new.setNext(he0.getNextInFace());
		he0new.copyProperties(he0);
		he1new.setNext(he1.getNextInFace());
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
			selectionOut.union(splitEdge(edges[i], 0.5));
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
			selectionOut.union(splitEdge(edges[i], p));
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
			selectionOut.union(splitEdge(eItr.next(), 0.5));
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
			selectionOut.union(splitEdge(e, p));
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
			for (int i = 0; i < n - 1; i++) {
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
		HE_Halfedge heiPrev;
		HE_Halfedge hejPrev;
		HE_Halfedge he0new;
		HE_Halfedge he1new;

		HE_Face faceNew;
		HE_Halfedge he;
		if ((hei.getNextInFace() != hej) || (hei.getPrevInFace() != hej)) {
			heiPrev = hei.getPrevInFace();
			hejPrev = hej.getPrevInFace();
			he0new = new HE_Halfedge();
			he1new = new HE_Halfedge();
			he0new.setVertex(vj);
			he1new.setVertex(vi);
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
			he = face.getHalfedge();
			do {
				he = he.getNextInFace();
			} while (he != face.getHalfedge());
			return out;
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
	 * Tri split face.
	 *
	 * @param face
	 *            face
	 * @param v
	 *            new vertex
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFaceTri(final HE_Face face, final WB_Point v) {
		HE_Halfedge he = face.getHalfedge();
		final HE_Vertex vi = new HE_Vertex(v);
		vi.setInternalLabel(2);
		final HE_Selection out = new HE_Selection(this);
		int c = 0;
		boolean onEdge = false;
		do {
			c++;
			final WB_Plane P = new WB_Plane(he.getHalfedgeCenter(),
					he.getHalfedgeNormal());
			final double d = WB_Distance.getDistance3D(v, P);
			if (WB_Epsilon.isZero(d)) {
				onEdge = true;
				break;
			}
			he = he.getNextInFace();
		} while (he != face.getHalfedge());
		if (!onEdge) {
			add(vi);

			final HE_Halfedge[] he0 = new HE_Halfedge[c];
			final HE_Halfedge[] he1 = new HE_Halfedge[c];
			final HE_Halfedge[] he2 = new HE_Halfedge[c];
			c = 0;
			do {
				HE_Face f;
				if (c == 0) {
					f = face;
				}
				else {
					f = new HE_Face();
					f.copyProperties(face);
					add(f);
					out.add(f);
				}
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = new HE_Halfedge();
				he2[c] = new HE_Halfedge();
				add(he1[c]);
				add(he2[c]);
				he1[c].setVertex(he.getNextInFace().getVertex());
				he2[c].setVertex(vi);
				he1[c].setNext(he2[c]);
				he2[c].setNext(he);
				he1[c].setFace(f);
				he2[c].setFace(f);
				c++;
				he = he.getNextInFace();
			} while (he != face.getHalfedge());
			vi.setHalfedge(he2[0]);
			for (int i = 0; i < c; i++) {
				he0[i].setNext(he1[i]);
				he1[i].setPair(he2[i == c - 1 ? 0 : i + 1]);
				he2[i == c - 1 ? 0 : i + 1].setPair(he1[i]);
			}
			out.add(vi);
			return out;
		}
		return null;

	}

	/**
	 * Tri split face.
	 *
	 * @param face
	 *            face
	 * @param x
	 *            x-coordinate of new vertex
	 * @param y
	 *            y-coordinate of new vertex
	 * @param z
	 *            z-coordinate of new vertex
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFaceTri(final HE_Face face, final double x,
			final double y, final double z) {
		return splitFaceTri(face, new WB_Point(x, y, z));
	}

	/**
	 * Tri split face.
	 *
	 * @param face
	 *            face
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFaceTri(final HE_Face face) {
		return splitFaceTri(face, face.getFaceCenter());
	}

	/**
	 * Tri split face with offset along face normal.
	 *
	 * @param face
	 *            face
	 * @param d
	 *            offset along face normal
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFaceTri(final HE_Face face, final double d) {
		return splitFaceTri(face,
				face.getFaceCenter().addMulSelf(d, face.getFaceNormal()));
	}

	/**
	 * Tri split faces with offset along face normal.
	 *
	 * @param d
	 *            offset along face normal
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFacesTri(final double d) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = getFacesAsArray();
		final int n = getNumberOfFaces();
		for (int i = 0; i < n; i++) {
			selectionOut.union(splitFaceTri(faces[i], d));

		}
		return selectionOut;
	}

	/**
	 * Tri split faces.
	 *
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFacesTri() {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = getFacesAsArray();
		final int n = getNumberOfFaces();
		for (int i = 0; i < n; i++) {
			selectionOut.union(splitFaceTri(faces[i]));

		}
		return selectionOut;
	}

	/**
	 * Tri split faces.
	 *
	 * @param selection
	 *            face selection to split
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFacesTri(final HE_Selection selection) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = selection.getFacesAsArray();
		final int n = selection.getNumberOfFaces();
		for (int i = 0; i < n; i++) {

			selectionOut.union(splitFaceTri(faces[i]));

		}
		selection.union(selectionOut);
		return selectionOut;
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
		final HE_Selection selectionOut = new HE_Selection(this);
		final HE_Face[] faces = selection.getFacesAsArray();
		final int n = selection.getNumberOfFaces();
		for (int i = 0; i < n; i++) {

			selectionOut.union(splitFaceTri(faces[i], d));

		}
		selection.union(selectionOut);
		return selectionOut;
	}

	/**
	 * Split face by connecting all face vertices with new vertex.
	 *
	 * @param key
	 *            key of face
	 * @param v
	 *            position of new vertex
	 * @return selection of new faces and new vertex
	 */

	public HE_Selection splitFaceTri(final long key, final WB_Point v) {
		return splitFaceTri(getFaceByKey(key), v);
	}

	/**
	 * Split face by connecting all face vertices with new vertex.
	 *
	 * @param key
	 *            key of face
	 * @param x
	 *            x-coordinate of new vertex
	 * @param y
	 *            y-coordinate of new vertex
	 * @param z
	 *            z-coordinate of new vertex
	 * @return selection of new faces and new vertex
	 */
	public HE_Selection splitFaceTri(final long key, final double x,
			final double y, final double z) {
		return splitFaceTri(getFaceByKey(key), new WB_Point(x, y, z));
	}

	/**
	 * Quad split faces.
	 *
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesQuad() {
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
		for (i = 0; i < n; i++) {
			f = faces[i];
			vi = new HE_Vertex(faceCenters[i]);
			vi.setInternalLabel(2);
			add(vi);
			selectionOut.add(vi);
			HE_Halfedge startHE = f.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;

			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {
				HE_Face fc;
				if (c == 0) {
					fc = f;
				}
				else {
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
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
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
		pairHalfedges();
		return selectionOut;

	}

	/**
	 * Quad split selected faces.
	 *
	 * @param sel
	 *            selection to split
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesQuad(final HE_Selection sel) {
		final HE_Selection selectionOut = new HE_Selection(this);
		final int n = sel.getNumberOfFaces();
		final WB_Point[] faceCenters = new WB_Point[n];
		final int[] faceOrders = new int[n];
		HE_Face face;
		final Iterator<HE_Face> fItr = sel.fItr();
		int i = 0;
		while (fItr.hasNext()) {
			face = fItr.next();
			faceCenters[i] = face.getFaceCenter();
			faceOrders[i] = face.getFaceOrder();
			i++;
		}

		final HE_Selection orig = new HE_Selection(this);
		orig.addFaces(sel.getFacesAsArray());
		orig.collectVertices();
		orig.collectEdgesByFace();
		selectionOut.addVertices(splitEdges(orig).getVerticesAsArray());
		final HE_Face[] faces = sel.getFacesAsArray();
		for (i = 0; i < n; i++) {
			face = faces[i];
			final HE_Vertex vi = new HE_Vertex(faceCenters[i]);
			add(vi);
			vi.setInternalLabel(2);
			selectionOut.add(vi);
			HE_Halfedge startHE = face.getHalfedge();
			while (orig.contains(startHE.getVertex())) {
				startHE = startHE.getNextInFace();
			}
			HE_Halfedge he = startHE;

			final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
			final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
			int c = 0;
			do {
				HE_Face f;
				if (c == 0) {
					f = face;
				}
				else {
					f = new HE_Face();
					add(f);
					f.copyProperties(face);
					sel.add(f);
				}
				he0[c] = he;
				he.setFace(f);
				f.setHalfedge(he);
				he1[c] = he.getNextInFace();
				he2[c] = new HE_Halfedge();
				he3[c] = new HE_Halfedge();
				add(he2[c]);
				add(he3[c]);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
				he3[c].setVertex(vi);

				he2[c].setNext(he3[c]);
				he3[c].setNext(he);
				he1[c].setFace(f);
				he2[c].setFace(f);
				he3[c].setFace(f);
				c++;
				he = he.getNextInFace().getNextInFace();
			} while (he != startHE);
			vi.setHalfedge(he3[0]);
			for (int j = 0; j < c; j++) {
				he1[j].setNext(he2[j]);
			}
		}
		pairHalfedges();
		return selectionOut;

	}

	/**
	 * Hybrid split faces: midsplit for triangles, quad split otherwise.
	 *
	 * @return selection of new faces and new vertices
	 */
	public HE_Selection splitFacesHybrid() {
		logger.debug("Entering splitFacesHybrid().");
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
		logger.debug("Starting split of " + orig.getNumberOfEdges() + " edges.");
		selectionOut.addVertices(splitEdges().getVerticesAsArray());
		final HE_Face[] faces = getFacesAsArray();
		HE_Vertex vi = new HE_Vertex();
		int fo;
		logger.debug("Starting split of " + n + " faces.");
		for (i = 0; i < n; i++) {
			f = faces[i];
			fo = f.getFaceOrder() / 2;
			if (fo == 3) {
				logger.trace("Splitting 3-face in 4 triangles.");
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;
				final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
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
					hec[c].setPair(he2[c]);
					he2[c].setPair(hec[c]);
					hec[c].setFace(f);
					he2[c].setVertex(he.getNextInFace().getNextInFace()
							.getVertex());
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

			}

			else if (fo > 3) {
				if (logger.isDebugEnabled()) {
					logger.trace("Splitting " + Integer.toString(fo)
							+ "-face in " + Integer.toString(fo) + " quads.");
				}
				vi = new HE_Vertex(faceCenters[i]);
				vi.setInternalLabel(2);
				add(vi);
				selectionOut.add(vi);
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;

				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {
					HE_Face fc;
					if (c == 0) {
						fc = f;
					}
					else {
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
		logger.debug("Pairing all new unpaired halfedges.");
		pairHalfedges();
		logger.debug("Exiting splitFacesHybrid().");
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
		for (i = 0; i < n; i++) {
			f = faces[i];
			if (f.getFaceOrder() == 3) {
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;
				final HE_Halfedge[] hec = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
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
					hec[c].setPair(he2[c]);
					he2[c].setPair(hec[c]);
					hec[c].setFace(f);
					he2[c].setVertex(he.getNextInFace().getNextInFace()
							.getVertex());
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

			}

			else if (f.getFaceOrder() > 3) {
				vi = new HE_Vertex(faceCenters[i]);
				vi.setInternalLabel(2);
				add(vi);
				selectionOut.add(vi);
				HE_Halfedge startHE = f.getHalfedge();
				while (orig.contains(startHE.getVertex())) {
					startHE = startHE.getNextInFace();
				}
				HE_Halfedge he = startHE;

				final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
				final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
				int c = 0;
				do {
					HE_Face fc;
					if (c == 0) {
						fc = f;
					}
					else {
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

	public HE_Selection splitFacesCenter() {
		final HEM_Extrude ext = new HEM_Extrude().setChamfer(0.5);
		modify(ext);
		return ext.extruded;
	}

	public HE_Selection splitFacesCenterHole() {
		final HEM_Extrude ext = new HEM_Extrude().setChamfer(0.5);
		modify(ext);
		delete(ext.extruded);
		return ext.walls;
	}

	public HE_Selection splitFacesCenter(final HE_Selection faces) {
		final HEM_Extrude ext = new HEM_Extrude().setChamfer(0.5);
		modifySelected(ext, faces);
		return ext.extruded;
	}

	public HE_Selection splitFacesCenterHole(final HE_Selection faces) {
		final HEM_Extrude ext = new HEM_Extrude().setChamfer(0.5);
		modifySelected(ext, faces);
		delete(ext.extruded);
		return ext.walls;
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
			int c = 0;
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
				hec[c].setPair(he2[c]);
				he2[c].setPair(hec[c]);
				hec[c].setFace(face);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
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
			int c = 0;
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
				hec[c].setPair(he2[c]);
				he2[c].setPair(hec[c]);
				hec[c].setFace(face);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
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
			int c = 0;
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
				hec[c].setPair(he2[c]);
				he2[c].setPair(hec[c]);
				hec[c].setFace(face);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
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
			int c = 0;
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
				hec[c].setPair(he2[c]);
				he2[c].setPair(hec[c]);
				hec[c].setFace(face);
				he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
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
			if (f[i].getFaceType() == WB_Convex.CONCAVE) {
				triangulate(f[i].key());
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

		if (face.getFaceType() == WB_Convex.CONCAVE) {
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
				if (P.classifyPointToPlane(p) == WB_Classification.BACK) {
					return false;
				}
			}
			else {

				lpi = WB_Intersection.getIntersection3D(R, P);
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

		return (isConvex) ? true : (c % 2 == 1);
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
		return WB_Epsilon.isZero(WB_Distance.getDistance3D(p,
				WB_Intersection.getClosestPoint3D(p, f.toPolygon())));
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
		if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(p,
				WB_Intersection.getClosestPoint3D(p, poly)))) {
			return false;
		}
		if (!WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(p,
				WB_Intersection.getClosestPointOnPeriphery3D(p, poly)))) {
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
	 * Select faces.
	 *
	 * @param v
	 *            the v
	 * @return the h e_ selection
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
	 * Select faces.
	 *
	 * @param P
	 *            the p
	 * @return the h e_ selection
	 */
	public HE_Selection selectFaces(final WB_Plane P) {
		final HE_Selection _selection = new HE_Selection(this);
		_selection.addFaces(HE_Intersection.getPotentialIntersectedFaces(this,
				P));

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
		final List<HE_Face> faces = this.getFacesAsList();
		for (int i = 0; i < faces.size(); i++) {
			final HE_Face f = faces.get(i);
			if (contains(f)) {
				fuseCoplanarFace(f, a);
			}
		}

	}

	/**
	 * Remove all redundant vertices in straight edges.
	 *
	 */
	public void removeColinearVertices() {
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
			final WB_Point tmp = WB_Intersection.getClosestPoint3D(p, poly);
			d = WB_Distance.getSqDistance3D(tmp, p);
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
			final WB_Point tmp = WB_Intersection.getClosestPoint3D(p, poly);
			d = WB_Distance.getSqDistance3D(tmp, p);
			if (d < dmin) {
				dmin = d;
				face = faces.get(i);
				;
			}
		}
		final HE_Vertex nv = splitFaceTri(face, p).vItr().next();
		vertexTree.add(nv, nv.key());
	}

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

	public HE_Path[] getBoundaryAsPath() {
		final List<HE_Halfedge> boundaryhes = getBoundaryLoopHalfedges();
		final HE_Path[] result = new HE_Path[boundaryhes.size()];
		for (int i = 0; i < boundaryhes.size(); i++) {
			result[i] = new HE_Path(boundaryhes.get(i));

		}
		return result;
	}

	/**
	 * Try to identify and correct corner and edge welds. Can occur when
	 * combining meshes joined at a single vertex or edge. Needs two passes to
	 * complete.
	 */
	public void resolvePinchPoints() {

		Iterator<HE_Vertex> vItr = vItr();
		Iterator<HE_Halfedge> heItr;
		HE_Vertex v;
		HE_Halfedge he;
		boolean pinchFound;
		final FastTable<HE_Halfedge> vHalfedges = new FastTable<HE_Halfedge>();
		int run = 0;
		do {
			vItr = vItr();
			pinchFound = false;
			run++;
			// System.out.println("HE_Mesh, trying to resolve pinch points pass "
			// + run + ".");
			while (vItr.hasNext()) {
				v = vItr.next();
				heItr = heItr();
				vHalfedges.clear();
				while (heItr.hasNext()) {
					he = heItr.next();
					if (he.getVertex() == v) {
						vHalfedges.add(he);
					}
				}
				final List<HE_Halfedge> vStar = v.getHalfedgeStar();
				if (vStar.size() != vHalfedges.size()) {
					pinchFound = true;
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
				}
			}
		} while (pinchFound && run < 10);
	}

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
	 */
	public void triangulate(final long key) {
		triangulate(getFaceByKey(key));

	}

	public void triangulateFaceStar(final HE_Vertex v) {
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
		triangulate(vf);

	}

	public void triangulateFaceStar(final long vertexkey) {
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
		triangulate(vf);

	}

	public void triangulate(final HE_Face face) {
		if (face.getFaceOrder() > 3) {
			final int[][] tris = face.getTriangles();
			final List<HE_Vertex> vertices = face.getFaceVertices();
			HE_Halfedge he = face.getHalfedge();
			remove(face);
			do {

				he.getPair().clearPair();

				he.clearPair();

				remove(he);
				he = he.getNextInFace();
			} while (he != face.getHalfedge());

			for (int i = 0; i < tris.length; i++) {
				final int[] tri = tris[i];
				final HE_Face f = new HE_Face();
				add(f);
				f.copyProperties(face);
				final HE_Halfedge he1 = new HE_Halfedge();
				final HE_Halfedge he2 = new HE_Halfedge();
				final HE_Halfedge he3 = new HE_Halfedge();
				he1.setVertex(vertices.get(tri[0]));
				he2.setVertex(vertices.get(tri[1]));
				he3.setVertex(vertices.get(tri[2]));
				he1.getVertex().setHalfedge(he1);
				he2.getVertex().setHalfedge(he2);
				he3.getVertex().setHalfedge(he3);
				he1.setFace(f);
				he2.setFace(f);
				he3.setFace(f);
				he1.setNext(he2);
				he2.setNext(he3);
				he3.setNext(he1);

				f.setHalfedge(he1);
				add(he1);
				add(he2);
				add(he3);
			}

			pairHalfedges();
		}
	}

	private void triangulateNoPairing(final HE_Face face) {
		if (face.getFaceOrder() > 3) {
			final int[][] tris = face.getTriangles();
			final List<HE_Vertex> vertices = face.getFaceVertices();
			HE_Halfedge he = face.getHalfedge();
			remove(face);
			do {

				if (he.getPair() != null) {

					he.getPair().clearPair();
				}

				he.clearPair();
				remove(he);
				he = he.getNextInFace();
			} while (he != face.getHalfedge());

			for (int i = 0; i < tris.length; i++) {
				final int[] tri = tris[i];
				final HE_Face f = new HE_Face();
				add(f);
				f.copyProperties(face);
				final HE_Halfedge he1 = new HE_Halfedge();
				final HE_Halfedge he2 = new HE_Halfedge();
				final HE_Halfedge he3 = new HE_Halfedge();
				he1.setVertex(vertices.get(tri[0]));
				he2.setVertex(vertices.get(tri[1]));
				he3.setVertex(vertices.get(tri[2]));
				he1.getVertex().setHalfedge(he1);
				he2.getVertex().setHalfedge(he2);
				he3.getVertex().setHalfedge(he3);
				he1.setFace(f);
				he2.setFace(f);
				he3.setFace(f);
				he1.setNext(he2);
				he2.setNext(he3);
				he3.setNext(he1);
				f.setHalfedge(he1);
				add(he1);
				add(he2);
				add(he3);
			}
		}
	}

	/**
	 * Triangulate all faces.
	 *
	 */
	public void triangulate() {
		final HE_Face[] f = getFacesAsArray();
		final int n = getNumberOfFaces();
		for (int i = 0; i < n; i++) {
			triangulateNoPairing(f[i]);
		}
		pairHalfedges();
	}

	/**
	 * Triangulate.
	 *
	 * @param sel
	 *            the sel
	 */
	public void triangulate(final HE_Selection sel) {
		final HE_Face[] f = sel.getFacesAsArray();
		final int n = sel.getNumberOfFaces();
		for (int i = 0; i < n; i++) {
			triangulateNoPairing(f[i]);
		}
		pairHalfedges();
	}

	/**
	 * Clean.
	 */
	public void clean() {
		final WB_Polygon[] polygons = getPolygons();
		final HEC_FromPolygons creator = new HEC_FromPolygons();
		creator.setPolygons(polygons);
		set(creator.create());
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
	 * Smooth.
	 */
	public void smooth() {
		subdivide(new HES_CatmullClark());

	}

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

	// WB_Mesh methods

	@Override
	public WB_GeometryType getType() {
		return WB_GeometryType.MESH;
	}

	@Override
	public HE_Mesh apply(final WB_Transform T) {
		final HE_Mesh result = get();

		return result.transform(T);
	}

	@Override
	public WB_Vector getFaceNormal(final int id) {
		return getFaceByIndex(id).getFaceNormal();
	}

	@Override
	public WB_Point getFaceCenter(final int id) {
		return getFaceByIndex(id).getFaceCenter();
	}

	@Override
	public WB_Vector getVertexNormal(final int i) {
		return getVertexByIndex(i).getVertexNormal();
	}

	@Override
	public WB_Coordinate getVertex(final int i) {
		return getVertexByIndex(i);
	}

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

	@Override
	public WB_CoordinateSequence getPoints() {

		return gf.createPointSequence(getVertices());
	}

	@Override
	public int getColor() {

		return meshcolor;
	}

	@Override
	public void setColor(final int color) {
		meshcolor = color;

	}

	public void setFaceColor(final int color) {
		final HE_FaceIterator fitr = new HE_FaceIterator(this);
		while (fitr.hasNext()) {
			fitr.next().setColor(color);
		}

	}

	public void setVertexColor(final int color) {
		final HE_VertexIterator vitr = new HE_VertexIterator(this);
		while (vitr.hasNext()) {
			vitr.next().setColor(color);
		}

	}

	public void setHalfedgeColor(final int color) {
		final HE_HalfedgeIterator heitr = new HE_HalfedgeIterator(this);
		while (heitr.hasNext()) {
			heitr.next().setColor(color);
		}

	}

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

	public int getGenus() {
		return (2 - (getNumberOfVertices() - getNumberOfEdges() + getNumberOfFaces())) / 2;

	}

}