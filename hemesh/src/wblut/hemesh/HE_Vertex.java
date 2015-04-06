/*
 *
 */
package wblut.hemesh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javolution.util.FastTable;
import wblut.geom.WB_ClassificationConvex;
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
    /**
     *
     */
    private WB_Point pos;
    /** Halfedge associated with this vertex. */
    private HE_Halfedge _halfedge;
    /** The _data. */
    private HashMap<String, Object> _data;
    /**
     *
     */
    private int vertexcolor;
    private HE_TextureCoordinate uvw;

    /**
     * Instantiates a new HE_Vertex.
     */
    public HE_Vertex() {
	super();
	pos = new WB_Point();
	vertexcolor = -1;
	uvw = null;
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
	uvw = null;
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
	uvw = null;
    }

    /**
     *
     *
     * @return
     */
    public HE_Vertex get() {
	final HE_Vertex copy = new HE_Vertex(pos);
	if (uvw != null) {
	    copy.setUVW(uvw);
	}
	return copy;
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
	pos.set(v);
    }

    /**
     *
     *
     * @param d
     * @return
     */
    public WB_Point getOffset(final double d) {
	return new WB_Point(pos).addMulSelf(d, getVertexNormal());
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
    public WB_ClassificationConvex getVertexType() {
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
	    v.subSelf(he.getVertex());
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
		return WB_ClassificationConvex.SADDLE;
	    } else {
		if (nflat > 0) {
		    return WB_ClassificationConvex.FLATCONCAVE;
		} else {
		    return WB_ClassificationConvex.CONCAVE;
		}
	    }
	} else if (nconvex > 0) {
	    if (nflat > 0) {
		return WB_ClassificationConvex.FLATCONVEX;
	    } else {
		return WB_ClassificationConvex.CONVEX;
	    }
	}
	return WB_ClassificationConvex.FLAT;
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
	    } else {
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

    /**
     *
     *
     * @return
     */
    public List<HE_Vertex> getVertexStar() {
	return getNeighborVertices();
    }

    /**
     *
     *
     * @return
     */
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

    /**
     *
     *
     * @return
     */
    public WB_Point getPoint() {
	return pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#xd()
     */
    @Override
    public double xd() {
	return pos.xd();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#yd()
     */
    @Override
    public double yd() {
	return pos.yd();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#zd()
     */
    @Override
    public double zd() {
	return pos.zd();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#zd()
     */
    @Override
    public double wd() {
	return pos.wd();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#getd(int)
     */
    @Override
    public double getd(final int i) {
	return pos.getd(i);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#xf()
     */
    @Override
    public float xf() {
	return pos.xf();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#yf()
     */
    @Override
    public float yf() {
	return pos.yf();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#zf()
     */
    @Override
    public float zf() {
	return pos.zf();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#zf()
     */
    @Override
    public float wf() {
	return pos.wf();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Coordinate#getf(int)
     */
    @Override
    public float getf(final int i) {
	return pos.getf(i);
    }

    /**
     *
     *
     * @param p
     * @return
     */
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

    /**
     *
     *
     * @param a
     * @param b
     * @return
     */
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

    /**
     *
     *
     * @param T
     */
    public void apply(final WB_Transform T) {
	T.applyAsPoint(this, pos);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#setX(double)
     */
    @Override
    public void setX(final double x) {
	pos.setX(x);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#setY(double)
     */
    @Override
    public void setY(final double y) {
	pos.setY(y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#setZ(double)
     */
    @Override
    public void setZ(final double z) {
	pos.setZ(z);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#setW(double)
     */
    @Override
    public void setW(final double w) {
	pos.setW(w);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#setCoord(int, double)
     */
    @Override
    public void setCoord(final int i, final double v) {
	pos.setCoord(i, v);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#set(wblut.geom.WB_Coordinate)
     */
    @Override
    public void set(final WB_Coordinate p) {
	pos.set(p);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#set(double, double)
     */
    @Override
    public void set(final double x, final double y) {
	pos.set(x, y);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#set(double, double, double)
     */
    @Override
    public void set(final double x, final double y, final double z) {
	pos.set(x, y, z);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinate#set(double, double, double, double)
     */
    @Override
    public void set(final double x, final double y, final double z,
	    final double w) {
	pos.set(x, y, z, w);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_HasColor#getColor()
     */
    @Override
    public int getColor() {
	return vertexcolor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_HasColor#setColor(int)
     */
    @Override
    public void setColor(final int color) {
	vertexcolor = color;
    }

    /**
     *
     *
     * @return
     */
    public WB_CoordinateSystem getCS() {
	final WB_Vector normal = getVertexNormal();
	if (normal == null) {
	    return null;
	}
	WB_Vector t2 = new WB_Vector();
	if (Math.abs(normal.xd()) < Math.abs(normal.yd())) {
	    t2.setX(1.0);
	} else {
	    t2.setY(1.0);
	}
	final WB_Vector t1 = normal.cross(t2);
	final double n = t1.getLength3D();
	if (n < WB_Epsilon.EPSILON) {
	    return null;
	}
	t1.mulSelf(1.0 / n);
	t2 = normal.cross(t1);
	return geometryfactory.createCSFromOXYZ(this, t1, t2, normal);
    }

    // Common area-weighted mean normal
    /**
     *
     *
     * @return
     */
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
	    normal.addMulSelf(area, temp[2]);
	} while (_halfedge.getEndVertex() != d);
	final double n = normal.getLength3D();
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
		    ni.crossInto(tmp, nj);
		    if (tmp.getSqLength3D() < WB_Epsilon.SQEPSILON) {
			degenerate = true;
			break;
		    }
		}
		if (!degenerate) {
		    normal.add(ni);
		}
	    }
	    normal.normalizeSelf();
	    return normal;
	}
	normal.mulSelf(1.0 / n);
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
     *
     * @param meanCurvatureVector
     * @return
     */
    public double getGaussianCurvature(final WB_Vector meanCurvatureVector) {
	meanCurvatureVector.set(0, 0, 0);
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
		meanCurvatureVector.set(0, 0, 0);
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
	    final double area = 0.5 * vect2.getLength3D();
	    if (c31 > 0.0) {
		mixed += 0.5 * area;
	    } else if ((c12 > 0.0) || (c23 > 0.0)) {
		mixed += 0.25 * area;
	    } else {
		// Non-obtuse triangle
		if ((area > 0.0)
			&& (area > (-WB_Epsilon.EPSILON * (c12 + c23)))) {
		    mixed -= (0.125 * 0.5 * ((c12 * vect3.dot(vect3)) + (c23 * vect1
			    .dot(vect1)))) / area;
		}
	    }
	    gauss += Math.abs(Math.atan2(2.0 * area, -c31));
	    meanCurvatureVector.addMulSelf(0.5 / area,
		    vect3.mulAddMul(c12, -c23, vect1));
	} while (ot.getEndVertex() != d);
	meanCurvatureVector.mulSelf(0.5 / mixed);
	// Discrete gaussian curvature
	return ((2.0 * Math.PI) - gauss) / mixed;
    }

    /**
     * Returns the discrete Gaussian curvature. These discrete operators are
     * described in "Discrete Differential-Geometry Operators for Triangulated
     * 2-Manifolds", Mark Meyer, Mathieu Desbrun, Peter Schr�der, and Alan H.
     * Barr. http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf
     * http://www.cs.caltech.edu/~mmeyer/Publications/diffGeomOps.pdf Note: on a
     * sphere, the Gaussian curvature is very accurate, but not the mean
     * curvature. Guoliang Xu suggests improvements in his papers
     * http://lsec.cc.ac.cn/~xuguo/xuguo3.htm
     *
     * 
     * @return
     */
    public double getGaussianCurvature() {
	final WB_Vector meanCurvatureVector = new WB_Vector(0, 0, 0);
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
		meanCurvatureVector.set(0, 0, 0);
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
	    final double area = 0.5 * vect2.getLength3D();
	    if (c31 > 0.0) {
		mixed += 0.5 * area;
	    } else if ((c12 > 0.0) || (c23 > 0.0)) {
		mixed += 0.25 * area;
	    } else {
		// Non-obtuse triangle
		if ((area > 0.0)
			&& (area > (-WB_Epsilon.EPSILON * (c12 + c23)))) {
		    mixed -= (0.125 * 0.5 * ((c12 * vect3.dot(vect3)) + (c23 * vect1
			    .dot(vect1)))) / area;
		}
	    }
	    gauss += Math.abs(Math.atan2(2.0 * area, -c31));
	    meanCurvatureVector.addMulSelf(0.5 / area,
		    vect3.mulAddMul(c12, -c23, vect1));
	} while (ot.getEndVertex() != d);
	meanCurvatureVector.mulSelf(0.5 / mixed);
	// Discrete gaussian curvature
	return ((2.0 * Math.PI) - gauss) / mixed;
    }

    /**
     *
     *
     * @return
     */
    public WB_CoordinateSystem getCurvatureDirections() {
	final WB_CoordinateSystem tangent = getCS();
	if (tangent == null) {
	    return null;
	}
	final WB_Vector vect1 = findOptimalSolution(tangent.getZ(),
		tangent.getX(), tangent.getY());
	if (vect1 == null) {
	    return null;
	}
	double e1, e2;
	if (Math.abs(vect1.yd()) < WB_Epsilon.EPSILON) {
	    if (Math.abs(vect1.xd()) < Math.abs(vect1.zd())) {
		e1 = 0.0;
		e2 = 1.0;
	    } else {
		e1 = 1.0;
		e2 = 0.0;
	    }
	} else {
	    e2 = 1.0;
	    final double delta = Math.sqrt(((vect1.xd() - vect1.zd()) * (vect1
		    .xd() - vect1.zd())) + (4.0 * vect1.yd() * vect1.yd()));
	    double K1;
	    if ((vect1.xd() + vect1.zd()) < 0.0) {
		K1 = 0.5 * ((vect1.xd() + vect1.zd()) - delta);
	    } else {
		K1 = 0.5 * (vect1.xd() + vect1.zd() + delta);
	    }
	    e1 = (K1 - vect1.xd()) / vect1.yd();
	    final double n = Math.sqrt((e1 * e1) + (e2 * e2));
	    e1 /= n;
	    e2 /= n;
	}
	final WB_Vector t1 = tangent.getX();
	final WB_Vector t2 = tangent.getY();
	final WB_Vector X = t1.mulAddMul(e1, e2, t2);
	final WB_Vector Y = t1.mulAddMul(-e2, e1, t2);
	return geometryfactory.createCSFromOXYZ(this, X, Y, tangent.getZ());
    }

    /**
     *
     *
     * @param p0
     * @param p1
     * @param p2
     * @param tempD1
     * @param tempD2
     * @param ret
     * @return
     */
    private static double computeNormal3D(final WB_Point p0, final WB_Point p1,
	    final WB_Point p2, WB_Vector tempD1, WB_Vector tempD2,
	    final WB_Vector ret) {
	tempD1 = p1.subToVector3D(p2);
	tempD2 = p2.subToVector3D(p0);
	tempD1.crossInto(ret, tempD2);
	double norm = ret.getLength3D();
	if ((norm * norm) > (WB_Epsilon.SQEPSILON * ((tempD1.xd() * tempD1.xd())
		+ (tempD1.yd() * tempD1.yd())
		+ (tempD1.zd() * tempD1.zd())
		+ (tempD2.xd() * tempD2.xd()) + (tempD2.yd() * tempD2.yd()) + (tempD2
		.zd() * tempD2.zd())))) {
	    ret.mulSelf(1.0 / norm);
	} else {
	    ret.set(0, 0, 0);
	    norm = 0.0;
	}
	return 0.5 * norm;
    }

    /**
     *
     *
     * @param normal
     * @param t1
     * @param t2
     * @return
     */
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
	    final double area = 0.5 * vect2.getLength3D();
	    final double len2 = vect1.dot(vect1);
	    if (len2 < WB_Epsilon.SQEPSILON) {
		continue;
	    }
	    final double kappa = (2.0 * vect1.dot(normal)) / len2;
	    double d1 = vect1.dot(t1);
	    double d2 = vect1.dot(t2);
	    final double n = Math.sqrt((d1 * d1) + (d2 * d2));
	    if (n < WB_Epsilon.EPSILON) {
		continue;
	    }
	    d1 /= n;
	    d2 /= n;
	    final double omega = (0.5 * ((c12 * vect3.dot(vect3)) + (c23 * vect1
		    .dot(vect1)))) / area;
	    g0.addSelf(omega * d1 * d1 * d1 * d1, omega * 2.0 * d1 * d1 * d1
		    * d2, omega * d1 * d1 * d2 * d2);
	    g1.addSelf(omega * 4.0 * d1 * d1 * d2 * d2, omega * 2.0 * d1 * d2
		    * d2 * d2, omega * d2 * d2 * d2 * d2);
	    h.addSelf(omega * kappa * d1 * d1, omega * kappa * 2.0 * d1 * d2,
		    omega * kappa * d2 * d2);
	} while (ot.getEndVertex() != d);
	g1.setX(g0.yd());
	g2.setX(g0.zd());
	g2.setY(g1.zd());
	WB_M33 G = new WB_M33(g0.xd(), g1.xd(), g2.xd(), g0.yd(), g1.yd(),
		g2.yd(), g0.zd(), g1.zd(), g2.zd());
	G = G.inverse();
	if (G == null) {
	    return null;
	}
	return WB_M33.mulToPoint(G, h);
    }

    /**
     *
     *
     * @param el
     */
    public void copyProperties(final HE_Vertex el) {
	super.copyProperties(el);
	vertexcolor = el.getColor();
	if (el.getVertexUVW() == null) {
	    uvw = null;
	} else {
	    uvw = new HE_TextureCoordinate(el.getVertexUVW());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Element#clear()
     */
    @Override
    public void clear() {
	_data = null;
	_halfedge = null;
	pos = null;
    }

    /**
     *
     *
     * @return
     */
    public double getUmbrellaAngle() {
	double result = 0;
	HE_Halfedge he = _halfedge;
	if (he == null) {
	    return 0;
	}
	do {
	    result += he.getAngle();
	    he = he.getNextInVertex();
	} while (he != _halfedge);
	return result;
    }

    public HE_TextureCoordinate getVertexUVW() {
	if (uvw == null) {
	    return HE_TextureCoordinate.ZERO;
	}
	return uvw;
    }

    public HE_TextureCoordinate getHalfedgeUVW(final HE_Face f) {
	final HE_Halfedge he = getHalfedge(f);
	if (he != null && he.hasTexture()) {
	    return he.getUVW();
	} else {
	    return HE_TextureCoordinate.ZERO;
	}
    }

    public HE_TextureCoordinate getUVW(final HE_Face f) {
	final HE_Halfedge he = getHalfedge(f);
	if (he != null && he.hasTexture()) {
	    return he.getUVW();
	} else if (uvw == null) {
	    return HE_TextureCoordinate.ZERO;
	}
	return uvw;
    }

    public HE_Halfedge getHalfedge(final HE_Face f) {
	HE_Halfedge he = _halfedge;
	if (he == null) {
	    return null;
	}
	do {
	    if (he.getFace() == f) {
		return he;
	    }
	    he = he.getNextInVertex();
	} while (he != _halfedge);
	return null;
    }

    public void setUVW(final double u, final double v, final double w) {
	uvw = new HE_TextureCoordinate(u, v, w);
    }

    public void setUVW(final WB_Coordinate uvw) {
	this.uvw = new HE_TextureCoordinate(uvw);
    }

    public void setUVW(final HE_TextureCoordinate uvw) {
	this.uvw = new HE_TextureCoordinate(uvw);
    }

    public boolean hasVertexTexture() {
	return uvw != null;
    }

    public boolean hasHalfedgeTexture(final HE_Face f) {
	final HE_Halfedge he = getHalfedge(f);
	if (he != null && he.hasTexture()) {
	    return true;
	}
	return false;
    }

    public boolean hasTexture(final HE_Face f) {
	final HE_Halfedge he = getHalfedge(f);
	if (he != null && he.hasTexture()) {
	    return true;
	} else {
	    return uvw != null;
	}
    }
}
