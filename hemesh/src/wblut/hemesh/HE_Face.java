package wblut.hemesh;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_HasData;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Projection;
import wblut.geom.WB_SimplePolygon;
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

	private HashMap<String, Object> _data;

	private int facecolor;

	private int[][] triangles;

	/**
	 * Instantiates a new HE_Face.
	 */
	public HE_Face() {

		super();
		facecolor = -1;
	}

	public long key() {
		return super.getKey();
	}

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

	public WB_Point getFaceCenter(final double d) {
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

			_normal._addSelf((p0.yd() - p1.yd()) * (p0.zd() + p1.zd()),
					(p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
					(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));

			he = he.getNextInFace();
		} while (he != _halfedge);
		_normal._normalizeSelf();
		return _normal;
	}

	public WB_Vector getFaceNormalNN() {
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

			_normal._addSelf((p0.yd() - p1.yd()) * (p0.zd() + p1.zd()),
					(p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
					(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));

			he = he.getNextInFace();
		} while (he != _halfedge);
		return _normal;
	}

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
		}
		else if (y >= x && y >= z) {
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

	public List<HE_Vertex> getFaceVertices() {

		final List<HE_Vertex> fv = new FastTable<HE_Vertex>();
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

	public List<HE_Halfedge> getFaceHalfedges() {

		final List<HE_Halfedge> fhe = new FastTable<HE_Halfedge>();
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

	public List<HE_Halfedge> getFaceEdges() {

		final List<HE_Halfedge> fe = new FastTable<HE_Halfedge>();
		if (_halfedge == null) {
			return fe;
		}
		HE_Halfedge he = _halfedge;
		do {
			if (he.isEdge()) {
				if (!fe.contains(he)) {
					fe.add(he);
				}
			}
			else {
				if (!fe.contains(he.getPair())) {
					fe.add(he.getPair());
				}

			}
			he = he.getNextInFace();
		} while (he != _halfedge);

		return fe;

	}

	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	public void setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
		reset();
	}

	public void push(final WB_Coordinate c) {
		HE_Halfedge he = _halfedge;

		do {
			he.getVertex().getPoint()._addSelf(c);

			he = he.getNextInFace();
		} while (he != _halfedge);
	}

	public void clearHalfedge() {
		_halfedge = null;

	}

	public WB_Plane toPlane() {
		return new WB_Plane(getFaceCenter(), getFaceNormal());
	}

	public WB_Plane toPlane(final double d) {
		final WB_Vector fn = getFaceNormal();
		return new WB_Plane(getFaceCenter()._addMulSelf(d, fn), fn);
	}

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

		}
	}

	public int[][] getTriangles() {
		if (triangles == null) {
			triangles = toPolygon().triangulate();
		}
		return triangles;
	}

	public void reset() {
		triangles = null;

	}

	public WB_SimplePolygon toPolygon() {
		final int n = getFaceOrder();
		if (n == 0) {
			return null;
		}

		final WB_Point[] points = new WB_Point[n];

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

	public WB_SimplePolygon toPlanarPolygon() {
		final int n = getFaceOrder();

		if (n == 0) {
			return null;
		}

		final WB_Point[] points = new WB_Point[n];

		final WB_Plane P = toPlane();
		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
			points[i] = WB_Projection.projectOnPlane(he.getVertex(), P);
			he = he.getNextInFace();
			i++;
		} while (he != _halfedge);

		return new WB_SimplePolygon(points, n);
	}

	public List<HE_Face> getNeighborFaces() {

		final List<HE_Face> ff = new FastTable<HE_Face>();
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

	@Override
	public int getColor() {

		return facecolor;
	}

	@Override
	public void setColor(final int color) {
		facecolor = color;

	}

	/**
	 * Checks if is boundary.
	 *
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		HE_Halfedge he = _halfedge;
		do {
			if (he.getPair().getFace() == null) {
				return true;
			}
			he = he.getNextInFace();
		} while (he != _halfedge);
		return false;

	}

}
