package wblut.hemesh;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;
import wblut.WB_Epsilon;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_MutableCoordinate;
import wblut.geom.WB_Point;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Vector;

/**
 * Vertex element of half-edge mesh.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HE_Vertex extends HE_Element implements WB_MutableCoordinate,
		WB_HasData, WB_HasColor {

	public WB_Point pos;

	/** Halfedge associated with this vertex. */
	private HE_Halfedge _halfedge;

	/** The _data. */
	private HashMap<String, Object> _data;

	private int vertexcolor;

	/**
	 * Instantiates a new HE_Vertex.
	 */
	public HE_Vertex() {
		super();
		pos = new WB_Point();
		vertexcolor = -1;
	}

	/**
	 * Instantiates a new HE_Vertex at position x, y, z.
	 * 
	 * @param x
	 *            x-coordinate of vertex
	 * @param y
	 *            y-coordinate of vertex
	 * @param z
	 *            z-coordinate of vertex
	 */
	public HE_Vertex(final double x, final double y, final double z) {
		super();
		pos = new WB_Point(x, y, z);
		vertexcolor = -1;
	}

	/**
	 * Instantiates a new HE_Vertex at position v.
	 * 
	 * @param v
	 *            position of vertex
	 */
	public HE_Vertex(final WB_Coordinate v) {
		super();
		pos = new WB_Point(v);
		vertexcolor = -1;
	}

	public HE_Vertex get() {
		return new HE_Vertex(pos);
	}

	/**
	 * Get halfedge associated with this vertex.
	 * 
	 * @return halfedge
	 */
	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 * Sets the halfedge associated with this vertex.
	 * 
	 * @param halfedge
	 *            the new halfedge
	 */
	public void setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
	}

	/**
	 * Set position to v.
	 * 
	 * @param v
	 *            position
	 */
	public void set(final HE_Vertex v) {
		pos._set(v);
	}

	/**
	 * Get vertex normal. Returns stored value if update status is true.
	 * 
	 * @return normal
	 */
	public WB_Vector getVertexNormal() {

		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		final WB_Vector _normal = new WB_Vector();
		final FastList<WB_Vector> normals = new FastList<WB_Vector>();
		do {
			if (he.getFace() != null) {
				final WB_Vector fn = he.getFace().getFaceNormal();
				normals.add(fn);
			}
			he = he.getNextInVertex();
		} while (he != _halfedge);

		for (int i = 0; i < normals.size(); i++) {
			final WB_Vector ni = normals.get(i);

			boolean degenerate = false;
			for (int j = i + 1; j < normals.size(); j++) {
				final WB_Vector nj = normals.get(j);
				if (ni.isParallel(nj)) {
					degenerate = true;
					break;
				}
			}

			if (!degenerate) {
				_normal._addSelf(ni);

			}

		}

		_normal._normalizeSelf();
		return _normal;
	}

	/**
	 * Get vertex type. Returns stored value if update status is true.
	 * 
	 * @return HE.VertexType.FLAT: vertex is flat in all faces,
	 *         HE.VertexType.CONVEX: vertex is convex in all faces,
	 *         HE.VertexType.CONCAVE: vertex is concave in all faces,
	 *         HE.VertexType.FLATCONVEX: vertex is convex or flat in all faces,
	 *         HE.VertexType.FLATCONCAVE: vertex is concave or flat in all
	 *         faces, HE.VertexType.SADDLE: vertex is convex and concave in at
	 *         least one face each
	 */
	public WB_Convex getVertexType() {

		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		int nconcave = 0;
		int nconvex = 0;
		int nflat = 0;
		do {
			HE_Face f = he.getFace();
			if (f == null) {
				f = he.getPair().getFace();
			}
			final WB_Point v = he.getNextInFace().getVertex().get().pos;
			v._subSelf(he.getVertex());
			he = he.getNextInVertex();
			HE_Face fn = he.getFace();
			if (fn == null) {
				fn = he.getPair().getFace();
			}
			final WB_Vector c = f.getFaceNormal().cross(fn.getFaceNormal());

			final double d = v.dot(c);
			if (Math.abs(d) < WB_Epsilon.EPSILON) {
				nflat++;
			} else if (d < 0) {
				nconcave++;
			} else {
				nconvex++;
			}

		} while (he != _halfedge);
		if (nconcave > 0) {
			if (nconvex > 0) {
				return WB_Convex.SADDLE;
			} else {
				if (nflat > 0) {
					return WB_Convex.FLATCONCAVE;
				} else {
					return WB_Convex.CONCAVE;
				}
			}
		} else if (nconvex > 0) {
			if (nflat > 0) {
				return WB_Convex.FLATCONVEX;
			} else {
				return WB_Convex.CONVEX;
			}
		}

		return WB_Convex.FLAT;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Vertex key: " + key() + " [x=" + xd() + ", y=" + yd()
				+ ", z=" + zd() + "]";
	}

	/**
	 * Clear halfedge.
	 */
	public void clearHalfedge() {
		_halfedge = null;
	}

	/**
	 * Get key.
	 * 
	 * @return key
	 */
	public Long key() {
		return super.getKey();
	}

	/**
	 * Get halfedges in vertex.
	 * 
	 * @return halfedges
	 */
	public List<HE_Halfedge> getHalfedgeStar() {
		final List<HE_Halfedge> vhe = new FastList<HE_Halfedge>();
		if (getHalfedge() == null) {
			return vhe;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (!vhe.contains(he)) {
				vhe.add(he);
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vhe;
	}

	/**
	 * Get edges in vertex.
	 * 
	 * @return edges
	 */
	public List<HE_Edge> getEdgeStar() {

		final List<HE_Edge> ve = new FastList<HE_Edge>();
		if (getHalfedge() == null) {
			return ve;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (!ve.contains(he.getEdge())) {
				ve.add(he.getEdge());
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return ve;
	}

	/**
	 * Get faces in vertex.
	 * 
	 * @return faces
	 */
	public List<HE_Face> getFaceStar() {
		final List<HE_Face> vf = new FastList<HE_Face>();
		if (getHalfedge() == null) {
			return vf;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getFace() != null) {
				if (!vf.contains(he.getFace())) {
					vf.add(he.getFace());
				}
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vf;
	}

	/**
	 * Get neighboring vertices.
	 * 
	 * @return neighbors
	 */
	public List<HE_Vertex> getNeighborVertices() {
		final List<HE_Vertex> vv = new FastList<HE_Vertex>();
		if (getHalfedge() == null) {
			return vv;
		}
		HE_Halfedge he = getHalfedge();
		do {
			final HE_Halfedge hen = he.getNextInFace();
			if ((hen.getVertex() != this) && (!vv.contains(hen.getVertex()))) {
				vv.add(hen.getVertex());
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vv;
	}

	/**
	 * Gets the neighbors as points.
	 * 
	 * @return the neighbors as points
	 */
	public WB_Point[] getNeighborsAsPoints() {
		final WB_Point[] vv = new WB_Point[getVertexOrder()];
		if (getHalfedge() == null) {
			return vv;
		}
		HE_Halfedge he = getHalfedge();
		int i = 0;
		do {
			vv[i] = he.getEndVertex().pos;
			i++;
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return vv;
	}

	/**
	 * Get number of edges in vertex.
	 * 
	 * @return number of edges
	 */
	public int getVertexOrder() {

		int result = 0;
		if (getHalfedge() == null) {
			return 0;
		}
		HE_Halfedge he = getHalfedge();
		do {

			result++;
			he = he.getNextInVertex();
		} while (he != getHalfedge());

		return result;

	}

	/**
	 * Get area of faces bounding vertex.
	 * 
	 * @return area
	 */
	public double getVertexArea() {
		if (getHalfedge() == null) {
			return 0;
		}
		double result = 0;
		int n = 0;
		HE_Halfedge he = getHalfedge();
		do {
			if (he.getFace() != null) {
				result += he.getFace().getFaceArea();
				n++;
			}
			he = he.getNextInVertex();
		} while (he != getHalfedge());
		return result / n;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.core.WB_HasData#setData(java.lang.String, java.lang.Object)
	 */
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
	public Object getData(final String s) {
		return _data.get(s);
	}

	/**
	 * Checks if is boundary.
	 * 
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		HE_Halfedge he = _halfedge;
		do {
			if (he.getFace() == null)
				return true;
			he = he.getNextInVertex();
		} while (he != _halfedge);
		return false;

	}

	public WB_Point getPoint() {
		return geometryfactory.createPoint(pos);
	}

	@Override
	public double xd() {
		return pos.xd();
	}

	@Override
	public double yd() {
		return pos.yd();
	}

	@Override
	public double zd() {
		return pos.zd();
	}

	@Override
	public double wd() {
		return pos.wd();
	}

	@Override
	public double getd(final int i) {

		return pos.getd(i);
	}

	@Override
	public float xf() {
		return pos.xf();
	}

	@Override
	public float yf() {
		return pos.yf();
	}

	@Override
	public float zf() {
		return pos.zf();
	}

	@Override
	public float wf() {
		return pos.wf();
	}

	@Override
	public float getf(final int i) {
		return pos.getf(i);
	}

	public int compareTo(final WB_Coordinate p) {
		final int compX = compare(xd(), p.xd());
		if (compX != 0) {
			return compX;
		}
		final int compY = compare(yd(), p.yd());
		if (compY != 0) {
			return compY;
		}
		return compare(zd(), p.zd());

	}

	public static int compare(final double a, final double b) {
		if (a < b) {
			return -1;
		}
		if (a > b) {
			return 1;
		}

		if (Double.isNaN(a)) {
			if (Double.isNaN(b)) {
				return 0;
			}
			return -1;
		}

		if (Double.isNaN(b)) {
			return 1;
		}
		return 0;
	}

	public void apply(final WB_Transform T) {
		T.applyAsPoint(this, pos);

	}

	@Override
	public void _setX(double x) {
		pos._setX(x);

	}

	@Override
	public void _setY(double y) {
		pos._setY(y);

	}

	@Override
	public void _setZ(double z) {
		pos._setZ(z);

	}

	@Override
	public void _setW(double w) {
		pos._setW(w);

	}

	@Override
	public void _setCoord(int i, double v) {
		pos._setCoord(i, v);

	}

	@Override
	public void _set(WB_Coordinate p) {
		pos._set(p);

	}

	@Override
	public void _set(double x, double y) {
		pos._set(x, y);

	}

	@Override
	public void _set(double x, double y, double z) {
		pos._set(x, y, z);

	}

	@Override
	public void _set(double x, double y, double z, double w) {
		pos._set(x, y, z, w);

	}

	@Override
	public int getColor() {

		return vertexcolor;
	}

	@Override
	public void setColor(int color) {
		vertexcolor = color;

	}

}
