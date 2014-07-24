package wblut.hemesh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateSystem;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_HasData;
import wblut.geom.WB_MutableCoordinate;
import wblut.geom.WB_Point;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;

/**
 * Vertex element of half-edge mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Vertex extends HE_Element implements WB_MutableCoordinate,
WB_HasData, WB_HasColor {

	private final WB_Point pos;

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

	public WB_Point getOffset(final double d) {
		return new WB_Point(pos)._addMulSelf(d, getVertexNormal());

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
			}
			else if (d < 0) {
				nconcave++;
			}
			else {
				nconvex++;
			}

		} while (he != _halfedge);
		if (nconcave > 0) {
			if (nconvex > 0) {
				return WB_Convex.SADDLE;
			}
			else {
				if (nflat > 0) {
					return WB_Convex.FLATCONCAVE;
				}
				else {
					return WB_Convex.CONCAVE;
				}
			}
		}
		else if (nconvex > 0) {
			if (nflat > 0) {
				return WB_Convex.FLATCONVEX;
			}
			else {
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
	public long key() {
		return super.getKey();
	}

	/**
	 * Get halfedges in vertex.
	 *
	 * @return halfedges
	 */
	public List<HE_Halfedge> getHalfedgeStar() {
		final List<HE_Halfedge> vhe = new FastTable<HE_Halfedge>();
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
	public List<HE_Halfedge> getEdgeStar() {

		final List<HE_Halfedge> ve = new FastTable<HE_Halfedge>();
		if (getHalfedge() == null) {
			return ve;
		}
		HE_Halfedge he = getHalfedge();
		do {
			if (he.isEdge()) {
				if (!ve.contains(he)) {
					ve.add(he);
				}
			}
			else {
				if (!ve.contains(he.getPair())) {
					ve.add(he.getPair());
				}

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
		final List<HE_Face> vf = new FastTable<HE_Face>();
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
		final List<HE_Vertex> vv = new FastTable<HE_Vertex>();
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

	public List<HE_Vertex> getVertexStar() {
		return getNeighborVertices();

	}

	public List<HE_Vertex> getNextNeighborVertices() {
		final List<HE_Vertex> result = new FastTable<HE_Vertex>();
		if (getHalfedge() == null) {
			return result;
		}
		final List<HE_Vertex> vv = getNeighborVertices();
		for (final HE_Vertex v : vv) {
			result.addAll(v.getNeighborVertices());
		}

		final Iterator<HE_Vertex> vitr = result.iterator();
		HE_Vertex w;
		while (vitr.hasNext()) {
			w = vitr.next();
			if ((w == this) || (vv.contains(w))) {
				vitr.remove();
			}
		}
		return result;
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
	 * Checks if is boundary.
	 *
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		HE_Halfedge he = _halfedge;
		do {
			if (he.getFace() == null) {
				return true;
			}
			he = he.getNextInVertex();
		} while (he != _halfedge);
		return false;

	}

	public WB_Point getPoint() {
		return pos;
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
	public void _setX(final double x) {
		pos._setX(x);

	}

	@Override
	public void _setY(final double y) {
		pos._setY(y);

	}

	@Override
	public void _setZ(final double z) {
		pos._setZ(z);

	}

	@Override
	public void _setW(final double w) {
		pos._setW(w);

	}

	@Override
	public void _setCoord(final int i, final double v) {
		pos._setCoord(i, v);

	}

	@Override
	public void _set(final WB_Coordinate p) {
		pos._set(p);

	}

	@Override
	public void _set(final double x, final double y) {
		pos._set(x, y);

	}

	@Override
	public void _set(final double x, final double y, final double z) {
		pos._set(x, y, z);

	}

	@Override
	public void _set(final double x, final double y, final double z,
			final double w) {
		pos._set(x, y, z, w);

	}

	@Override
	public int getColor() {

		return vertexcolor;
	}

	@Override
	public void setColor(final int color) {
		vertexcolor = color;

	}

	public WB_CoordinateSystem getCS() {
		final WB_Vector normal = getVertexNormal();
		if (normal == null) {
			return null;
		}
		WB_Vector t2 = new WB_Vector();

		if (Math.abs(normal.xd()) < Math.abs(normal.yd())) {
			t2._setX(1.0);
		}
		else {
			t2._setY(1.0);
		}
		final WB_Vector t1 = normal.cross(t2);
		final double n = t1.getLength();
		if (n < WB_Epsilon.EPSILON) {
			return null;
		}
		t1._mulSelf(1.0 / n);
		t2 = normal.cross(t1);
		return geometryfactory.createCSFromOXYZ(this, t1, t2, normal);
	}

	// Common area-weighted mean normal
	public WB_Vector getVertexNormal() {
		if (_halfedge == null) {
			return null;
		}
		WB_Vector normal = new WB_Vector();
		final WB_Vector[] temp = new WB_Vector[3];
		for (int i = 0; i < 3; i++) {
			temp[i] = new WB_Vector();
		}
		final HE_Vertex d = _halfedge.getEndVertex();
		do {
			_halfedge = _halfedge.getNextInVertex();
			if (_halfedge.getFace() == null) {
				continue;
			}
			final double area = computeNormal3D(pos,
					_halfedge.getEndVertex().pos, _halfedge.getPrevInFace()
							.getVertex().pos, temp[0], temp[1], temp[2]);

			normal._addMulSelf(area, temp[2]);
		} while (_halfedge.getEndVertex() != d);
		final double n = normal.getLength();
		if (n < WB_Epsilon.EPSILON) {
			HE_Halfedge he = _halfedge;
			normal = geometryfactory.createVector();
			final FastTable<WB_Vector> normals = new FastTable<WB_Vector>();
			do {
				if (he.getFace() != null) {
					final WB_Vector fn = he.getFace().getFaceNormal();
					normals.add(fn);
				}
				he = he.getNextInVertex();
			} while (he != _halfedge);
			final WB_Vector tmp = geometryfactory.createVector();
			for (int i = 0; i < normals.size(); i++) {
				final WB_Vector ni = normals.get(i);
				boolean degenerate = false;
				for (int j = i + 1; j < normals.size(); j++) {
					final WB_Vector nj = normals.get(j);
					ni.crossInto(nj, tmp);
					if (tmp.getSqLength() < WB_Epsilon.SQEPSILON) {
						degenerate = true;
						break;
					}
				}
				if (!degenerate) {
					normal.add(ni);

				}

			}

			normal._normalizeSelf();
			return normal;
		}
		normal._mulSelf(1.0 / n);
		return normal;
	}

	/**
	 * Returns the discrete Gaussian curvature and the mean normal. These
	 * discrete operators are described in "Discrete Differential-Geometry
	 * Operators for Triangulated 2-Manifolds", Mark Meyer, Mathieu Desbrun,
	 * Peter Schr�der, and Alan H. Barr.
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
	 * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
	 * sphere, the Gaussian curvature is very accurate, but not the mean
	 * curvature. Guoliang Xu suggests improvements in his papers
	 * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
	 */
	public double getGaussianCurvature(final WB_Vector meanCurvatureVector) {

		meanCurvatureVector._set(0, 0, 0);
		WB_Point vect1 = new WB_Point();
		WB_Point vect2 = new WB_Point();
		WB_Point vect3 = new WB_Point();

		double mixed = 0.0;
		double gauss = 0.0;
		HE_Halfedge ot = getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			if (ot.getPair().getFace() == null) {
				meanCurvatureVector._set(0, 0, 0);
				return 0.0;
			}
			final HE_Vertex p1 = ot.getEndVertex();
			final HE_Vertex p2 = ot.getPrevInFace().getVertex();
			vect1 = p1.pos.sub(pos);
			vect2 = p2.pos.sub(p1.pos);
			vect3 = pos.sub(p2.pos);
			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			final double c31 = vect3.dot(vect1);
			// Override vect2
			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength();
			if (c31 > 0.0) {
				mixed += 0.5 * area;
			}
			else if (c12 > 0.0 || c23 > 0.0) {
				mixed += 0.25 * area;
			}
			else {
				// Non-obtuse triangle
				if (area > 0.0 && area > -WB_Epsilon.EPSILON * (c12 + c23)) {
					mixed -= 0.125 * 0.5
							* (c12 * vect3.dot(vect3) + c23 * vect1.dot(vect1))
							/ area;
				}
			}
			gauss += Math.abs(Math.atan2(2.0 * area, -c31));

			meanCurvatureVector._addMulSelf(0.5 / area,
					vect3.mulAddMul(c12, -c23, vect1));
		} while (ot.getEndVertex() != d);
		meanCurvatureVector._mulSelf(0.5 / mixed);
		// Discrete gaussian curvature
		return (2.0 * Math.PI - gauss) / mixed;
	}

	public WB_CoordinateSystem getCurvatureDirections() {

		final WB_CoordinateSystem tangent = getCS();
		if (tangent == null) {
			return null;
		}
		// To compute B eigenvectors, we search for the minimum of
		// E(a,b,c) = sum omega_ij (T(d_ij) B d_ij - kappa_ij)^2
		// d_ij is the unit direction of the edge ij in the tangent
		// plane, so it can be written in the (t1,t2) local frame:
		// d_ij = d1_ij t1 + d2_ij t2
		// Then
		// T(d_ij) B d_ij = a d1_ij^2 + 2b d1_ij d2_ij + c d2_ij^2
		// We solve grad E = 0
		// dE/da = 2 d1_ij^2 (a d1_ij^2 + 2b d1_ij d2_ij + c d2_ij^2 - kappa_ij)
		// dE/db = 4 d1_ij d2_ij (a d1_ij^2 + 2b d1_ij d2_ij + c d2_ij^2 -
		// kappa_ij)
		// dE/dc = 2 d2_ij^2 (a d1_ij^2 + 2b d1_ij d2_ij + c d2_ij^2 - kappa_ij)
		// We may decrease the dimension by using a+c=Kh identity,
		// but we found that Kh is much less accurate than Kg on
		// a sphere, so we do not use this identity.
		// (1/2) grad E = G (a b c) - H
		final WB_Vector vect1 = findOptimalSolution(tangent.getZ(),
				tangent.getX(), tangent.getY());
		if (vect1 == null) {
			return null;
		}
		// We can eventually compute eigenvectors of B(a b; b c).
		// Let first compute the eigenvector associated to K1
		double e1, e2;
		if (Math.abs(vect1.yd()) < WB_Epsilon.EPSILON) {
			if (Math.abs(vect1.xd()) < Math.abs(vect1.zd())) {
				e1 = 0.0;
				e2 = 1.0;
			}
			else {
				e1 = 1.0;
				e2 = 0.0;
			}
		}
		else {
			e2 = 1.0;
			final double delta = Math
					.sqrt((vect1.xd() - vect1.zd()) * (vect1.xd() - vect1.zd())
							+ 4.0 * vect1.yd() * vect1.yd());
			double K1;
			if (vect1.xd() + vect1.zd() < 0.0) {
				K1 = 0.5 * (vect1.xd() + vect1.zd() - delta);
			}
			else {
				K1 = 0.5 * (vect1.xd() + vect1.zd() + delta);
			}
			e1 = (K1 - vect1.xd()) / vect1.yd();
			final double n = Math.sqrt(e1 * e1 + e2 * e2);
			e1 /= n;
			e2 /= n;
		}

		final WB_Vector t1 = tangent.getX();
		final WB_Vector t2 = tangent.getY();

		final WB_Vector X = t1.mulAddMul(e1, e2, t2);
		final WB_Vector Y = t1.mulAddMul(-e2, e1, t2);

		return geometryfactory.createCSFromOXYZ(this, X, Y, tangent.getZ());
	}

	private static double computeNormal3D(final WB_Point p0, final WB_Point p1,
			final WB_Point p2, WB_Vector tempD1, WB_Vector tempD2,
			final WB_Vector ret) {
		tempD1 = p1.subToVector(p2);
		tempD2 = p2.subToVector(p0);
		tempD1.crossInto(tempD2, ret);

		double norm = ret.getLength();
		if (norm * norm > WB_Epsilon.SQEPSILON
				* (tempD1.xd() * tempD1.xd() + tempD1.yd() * tempD1.yd()
						+ tempD1.zd() * tempD1.zd() + tempD2.xd() * tempD2.xd()
						+ tempD2.yd() * tempD2.yd() + tempD2.zd() * tempD2.zd())) {
			ret._mulSelf(1.0 / norm);

		}
		else {
			ret._set(0, 0, 0);
			norm = 0.0;
		}
		return 0.5 * norm;
	}

	private WB_Vector findOptimalSolution(final WB_Vector normal,
			final WB_Vector t1, final WB_Vector t2) {

		WB_Vector vect1 = new WB_Vector();
		WB_Vector vect2 = new WB_Vector();
		WB_Vector vect3 = new WB_Vector();
		final WB_Vector g0 = new WB_Vector();
		final WB_Vector g1 = new WB_Vector();
		final WB_Vector g2 = new WB_Vector();
		final WB_Vector h = new WB_Vector();

		HE_Halfedge ot = getHalfedge();
		final HE_Vertex d = ot.getEndVertex();
		do {
			ot = ot.getNextInVertex();
			if (ot.getFace() == null) {
				continue;
			}
			final WB_Point p1 = ot.getEndVertex().pos;
			final WB_Point p2 = ot.getPrevInFace().getVertex().pos;

			vect1 = new WB_Vector(this, p1);
			vect2 = new WB_Vector(p1, p2);
			vect3 = new WB_Vector(p2, this);

			final double c12 = vect1.dot(vect2);
			final double c23 = vect2.dot(vect3);
			// Override vect2
			vect2 = vect1.cross(vect3);
			final double area = 0.5 * vect2.getLength();
			final double len2 = vect1.dot(vect1);
			if (len2 < WB_Epsilon.SQEPSILON) {
				continue;
			}
			final double kappa = 2.0 * vect1.dot(normal) / len2;
			double d1 = vect1.dot(t1);
			double d2 = vect1.dot(t2);
			final double n = Math.sqrt(d1 * d1 + d2 * d2);
			if (n < WB_Epsilon.EPSILON) {
				continue;
			}
			d1 /= n;
			d2 /= n;
			final double omega = 0.5
					* (c12 * vect3.dot(vect3) + c23 * vect1.dot(vect1)) / area;
			g0._addSelf(omega * d1 * d1 * d1 * d1, omega * 2.0 * d1 * d1 * d1
					* d2, omega * d1 * d1 * d2 * d2);
			g1._addSelf(omega * 4.0 * d1 * d1 * d2 * d2, omega * 2.0 * d1 * d2
					* d2 * d2, omega * d2 * d2 * d2 * d2);
			h._addSelf(omega * kappa * d1 * d1, omega * kappa * 2.0 * d1 * d2,
					omega * kappa * d2 * d2);
		} while (ot.getEndVertex() != d);
		g1._setX(g0.yd());
		g2._setX(g0.zd());
		g2._setY(g1.zd());
		WB_M33 G = new WB_M33(g0.xd(), g1.xd(), g2.xd(), g0.yd(), g1.yd(),
				g2.yd(), g0.zd(), g1.zd(), g2.zd());
		G = G.inverse();
		if (G == null) {
			return null;
		}
		return WB_M33.mulToPoint(G, h);
	}

}
