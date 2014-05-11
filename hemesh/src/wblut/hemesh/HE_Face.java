package wblut.hemesh;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_SimplePolygon;
import wblut.geom.WB_SimplePolygon2D;
import wblut.geom.WB_Vector;
import wblut.math.WB_Math;

/**
 * Face element of half-edge data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HE_Face extends HE_Element implements WB_HasData, WB_HasColor {

	/** Halfedge associated with this face. */
	private HE_Halfedge _halfedge;

	/** Status of sorting. */
	protected boolean _sorted;

	/** The _data. */
	private HashMap<String, Object> _data;

	private int facecolor;

	/**
	 * Instantiates a new HE_Face.
	 */
	public HE_Face() {

		super();
		facecolor = -1;
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
	 * Get face center.
	 * 
	 * @return center
	 */
	public WB_Point getFaceCenter() {
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		final WB_Point _center = new WB_Point();
		int c = 0;
		do {
			_center._addSelf(he.getVertex());
			c++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		_center._divSelf(c);
		return _center;
	}

	public WB_Point getFaceCenter(double d) {
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		final WB_Point _center = new WB_Point();
		int c = 0;
		do {
			_center._addSelf(he.getVertex());
			c++;
			he = he.getNextInFace();
		} while (he != _halfedge);
		_center._divSelf(c)._addMulSelf(d, getFaceNormal());
		return _center;
	}

	/**
	 * Get face normal. Returns stored value if update status is true.
	 * 
	 * @return normal
	 */
	public WB_Vector getFaceNormal() {
		if (_halfedge == null) {
			return null;
		}
		// calculate normal with Newell's method
		HE_Halfedge he = _halfedge;
		final WB_Vector _normal = new WB_Vector();
		HE_Vertex p0;
		HE_Vertex p1;
		do {
			p0 = he.getVertex();
			p1 = he.getNextInFace().getVertex();

			_normal.x += (p0.yd() - p1.yd()) * (p0.zd() + p1.zd());
			_normal.y += (p0.zd() - p1.zd()) * (p0.xd() + p1.xd());
			_normal.z += (p0.xd() - p1.xd()) * (p0.yd() + p1.yd());

			he = he.getNextInFace();
		} while (he != _halfedge);
		_normal._normalizeSelf();
		return _normal;
	}

	/**
	 * Get face area.
	 * 
	 * @return area
	 */
	public double getFaceArea() {
		if (_halfedge == null) {
			return Double.NaN;
		}
		final WB_Vector n = getFaceNormal();
		final double x = WB_Math.fastAbs(n.xd());
		final double y = WB_Math.fastAbs(n.yd());
		final double z = WB_Math.fastAbs(n.zd());
		double area = 0;
		int coord = 3;
		if (x >= y && x >= z) {
			coord = 1;
		} else if (y >= x && y >= z) {
			coord = 2;
		}
		HE_Halfedge he = _halfedge;
		do {
			switch (coord) {
			case 1:
				area += (he.getVertex().yd() * (he.getNextInFace().getVertex()
						.zd() - he.getPrevInFace().getVertex().zd()));
				break;
			case 2:
				area += (he.getVertex().xd() * (he.getNextInFace().getVertex()
						.zd() - he.getPrevInFace().getVertex().zd()));
				break;
			case 3:
				area += (he.getVertex().xd() * (he.getNextInFace().getVertex()
						.yd() - he.getPrevInFace().getVertex().yd()));
				break;

			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		switch (coord) {
		case 1:
			area *= (0.5 / x);
			break;
		case 2:
			area *= (0.5 / y);
			break;
		case 3:
			area *= (0.5 / z);
		}

		return WB_Math.fastAbs(area);

	}

	/**
	 * Get face type.
	 * 
	 * @return WB_PolygonType2D.CONVEX, WB_PolygonType2D.CONCAVE
	 */
	public WB_Convex getFaceType() {
		if (_halfedge == null) {
			return null;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
				return WB_Convex.CONCAVE;
			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return WB_Convex.CONVEX;
	}

	/**
	 * Get vertices of face as arraylist of HE_Vertex.
	 * 
	 * @return vertices
	 */
	public List<HE_Vertex> getFaceVertices() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Vertex> fv = new FastList<HE_Vertex>();
		if (_halfedge == null) {
			return fv;
		}
		HE_Halfedge he = _halfedge;
		do {

			if (!fv.contains(he.getVertex())) {
				fv.add(he.getVertex());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return fv;

	}

	/**
	 * Get number of vertices in face.
	 * 
	 * @return number of vertices
	 */
	public int getFaceOrder() {

		int result = 0;
		if (_halfedge == null) {
			return 0;
		}
		HE_Halfedge he = _halfedge;
		do {

			result++;
			he = he.getNextInFace();
		} while (he != _halfedge);

		return result;

	}

	/**
	 * Get halfedges of face as arraylist of HE_Halfedge. The halfedge of the
	 * leftmost vertex is returned first.
	 * 
	 * @return halfedges
	 */
	public List<HE_Halfedge> getFaceHalfedges() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Halfedge> fhe = new FastList<HE_Halfedge>();
		if (_halfedge == null) {
			return fhe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (!fhe.contains(he)) {
				fhe.add(he);
			}

			he = he.getNextInFace();
		} while (he != _halfedge);

		return fhe;

	}

	/**
	 * Get edges of face as arraylist of HE_Edge. The edge of the leftmost
	 * vertex is returned first.
	 * 
	 * @return edges
	 */
	public List<HE_Edge> getFaceEdges() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Edge> fe = new FastList<HE_Edge>();
		if (_halfedge == null) {
			return fe;
		}
		HE_Halfedge he = _halfedge;
		do {

			if (!fe.contains(he.getEdge())) {
				fe.add(he.getEdge());
			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return fe;

	}

	/**
	 * Get halfedge.
	 * 
	 * @return halfedge
	 */
	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 * Sets the halfedge.
	 * 
	 * @param halfedge
	 *            the new halfedge
	 */
	public void setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
		_sorted = false;
	}

	public void push(WB_Coordinate c) {
		HE_Halfedge he = _halfedge;

		do {
			he.getVertex().pos._addSelf(c);

			he = he.getNextInFace();
		} while (he != _halfedge);
	}

	/**
	 * Clear halfedge.
	 */
	public void clearHalfedge() {
		_halfedge = null;
		_sorted = false;
	}

	/**
	 * Get plane of face.
	 * 
	 * @return plane
	 */
	public WB_Plane toPlane() {
		return new WB_Plane(getFaceCenter(), getFaceNormal());
	}

	/**
	 * Get plane of face.
	 * 
	 * @param d
	 *            the d
	 * @return plane
	 */
	public WB_Plane toPlane(final double d) {
		final WB_Vector fn = getFaceNormal();
		return new WB_Plane(getFaceCenter()._addSelf(d, fn), fn);
	}

	/**
	 * Sort halfedges in lexicographic order.
	 */
	public void sort() {
		if (_halfedge != null) {

			HE_Halfedge he = _halfedge;
			HE_Halfedge leftmost = he;
			do {
				he = he.getNextInFace();
				if (he.getVertex().compareTo(leftmost.getVertex()) < 0) {
					leftmost = he;
				}
			} while (he != _halfedge);
			_halfedge = leftmost;
			_sorted = true;
		}
	}

	/**
	 * Triangulate the face, returns indexed 2D triangles. The index refers to
	 * the face vertices()
	 * 
	 * @return ArrayList of WB_IndexedTriangle
	 */
	public List<WB_IndexedTriangle2D> triangulate() {
		return toPolygon2D().indexedTriangulate();
	}

	/**
	 * Get the face as a WB_Polygon2D.
	 * 
	 * @return face as WB_Polygon2D
	 */
	public WB_SimplePolygon toPolygon() {
		final int n = getFaceOrder();
		if (n == 0) {
			return null;
		}

		final WB_Point[] points = new WB_Point[n];
		if (!_sorted) {
			sort();
		}

		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
			points[i] = new WB_Point(he.getVertex().xd(), he.getVertex().yd(),
					he.getVertex().zd());

			he = he.getNextInFace();
			i++;
		} while (he != _halfedge);

		return new WB_SimplePolygon(points, n);
	}

	/**
	 * Get the face as a WB_Polygon2D.
	 * 
	 * @return face as WB_Polygon2D
	 */
	public WB_SimplePolygon2D toPolygon2D() {

		return toPolygon().toPolygon2D();
	}

	/**
	 * Get neighboring faces as arraylist of HE_Face. The face of the leftmost
	 * halfedge is returned first.
	 * 
	 * @return neighboring faces
	 */
	public List<HE_Face> getNeighborFaces() {
		if (!isSorted()) {
			sort();
		}
		final List<HE_Face> ff = new FastList<HE_Face>();
		if (getHalfedge() == null) {
			return ff;
		}
		HE_Halfedge he = getHalfedge();
		do {
			final HE_Halfedge hep = he.getPair();
			if (hep.getFace() != null) {
				if (hep.getFace() != this) {
					if (!ff.contains(hep.getFace())) {
						ff.add(hep.getFace());
					}
				}
			}
			he = he.getNextInFace();
		} while (he != getHalfedge());

		return ff;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		String s = "HE_Face key: " + key() + ". Connects " + getFaceOrder()
				+ " vertices: ";
		HE_Halfedge he = getHalfedge();
		for (int i = 0; i < getFaceOrder() - 1; i++) {
			s += he.getVertex()._key + "-";
			he = he.getNextInFace();
		}
		s += he.getVertex()._key + ".";

		return s;
	}

	/**
	 * Checks if is sorted.
	 * 
	 * @return true, if is sorted
	 */
	public boolean isSorted() {
		return _sorted;
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

	@Override
	public int getColor() {

		return facecolor;
	}

	@Override
	public void setColor(int color) {
		facecolor = color;

	}

}
