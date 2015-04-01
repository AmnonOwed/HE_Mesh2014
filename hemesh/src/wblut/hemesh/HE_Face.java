/*
 *
 */
package wblut.hemesh;

import static wblut.geom.WB_GeometryOp.projectOnPlane;
import java.util.HashMap;
import java.util.List;
import javolution.util.FastTable;
import wblut.geom.WB_AABB;
import wblut.geom.WB_ClassificationConvex;
import wblut.geom.WB_Context2D;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_HasColor;
import wblut.geom.WB_HasData;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Triangulate;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.valid.IsValidOp;

/**
 * Face element of half-edge data structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Face extends HE_Element implements WB_HasData, WB_HasColor {
    /** Halfedge associated with this face. */
    private HE_Halfedge _halfedge;
    /**
     *
     */
    private HashMap<String, Object> _data;
    /**
     *
     */
    private int facecolor;
    /**
     *
     */
    private int[][] triangles;
    /**
     *
     */
    private int textureId;
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
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();

    /**
     * Instantiates a new HE_Face.
     */
    public HE_Face() {
	super();
	facecolor = -1;
    }

    /**
     *
     *
     * @return
     */
    public long key() {
	return super.getKey();
    }

    /**
     *
     *
     * @return
     */
    public WB_Point getFaceCenter() {
	if (_halfedge == null) {
	    return null;
	}
	HE_Halfedge he = _halfedge;
	final WB_Point _center = new WB_Point();
	int c = 0;
	do {
	    _center.addSelf(he.getVertex());
	    c++;
	    he = he.getNextInFace();
	} while (he != _halfedge);
	_center.divSelf(c);
	return _center;
    }

    /**
     *
     *
     * @param d
     * @return
     */
    public WB_Point getFaceCenter(final double d) {
	if (_halfedge == null) {
	    return null;
	}
	HE_Halfedge he = _halfedge;
	final WB_Point _center = new WB_Point();
	int c = 0;
	do {
	    _center.addSelf(he.getVertex());
	    c++;
	    he = he.getNextInFace();
	} while (he != _halfedge);
	_center.divSelf(c).addMulSelf(d, getFaceNormal());
	return _center;
    }

    /**
     *
     *
     * @return
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
	    _normal.addSelf((p0.yd() - p1.yd()) * (p0.zd() + p1.zd()),
		    (p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
		    (p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
	    he = he.getNextInFace();
	} while (he != _halfedge);
	_normal.normalizeSelf();
	return _normal;
    }

    /**
     *
     *
     * @return
     */
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
	    _normal.addSelf((p0.yd() - p1.yd()) * (p0.zd() + p1.zd()),
		    (p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
		    (p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
	    he = he.getNextInFace();
	} while (he != _halfedge);
	return _normal;
    }

    /**
     *
     *
     * @return
     */
    public double getFaceArea() {
	if (_halfedge == null) {
	    return 0;
	}
	final WB_Vector n = getFaceNormal();
	if (n.getLength3D() < 0.5) {
	    return 0;
	}
	final double x = WB_Math.fastAbs(n.xd());
	final double y = WB_Math.fastAbs(n.yd());
	final double z = WB_Math.fastAbs(n.zd());
	double area = 0;
	int coord = 3;
	if ((x >= y) && (x >= z)) {
	    coord = 1;
	} else if ((y >= x) && (y >= z)) {
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
     *
     *
     * @return
     */
    public WB_ClassificationConvex getFaceType() {
	if (_halfedge == null) {
	    return null;
	}
	HE_Halfedge he = _halfedge;
	do {
	    if (he.getHalfedgeType() != WB_ClassificationConvex.CONVEX) {
		return WB_ClassificationConvex.CONCAVE;
	    }
	    he = he.getNextInFace();
	} while (he != _halfedge);
	return WB_ClassificationConvex.CONVEX;
    }

    /**
     *
     *
     * @return
     */
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

    /**
     *
     *
     * @return
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
     *
     *
     * @return
     */
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

    /**
     *
     *
     * @return
     */
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
	    } else {
		if (!fe.contains(he.getPair())) {
		    fe.add(he.getPair());
		}
	    }
	    he = he.getNextInFace();
	} while (he != _halfedge);
	return fe;
    }

    /**
     *
     *
     * @return
     */
    public HE_Halfedge getHalfedge() {
	return _halfedge;
    }

    public HE_Halfedge getHalfedge(final HE_Vertex v) {
	HE_Halfedge he = _halfedge;
	if (he == null) {
	    return null;
	}
	do {
	    if (he.getVertex() == v) {
		return he;
	    }
	    he = he.getNextInFace();
	} while (he != _halfedge);
	return null;
    }

    /**
     *
     *
     * @param halfedge
     */
    public void setHalfedge(final HE_Halfedge halfedge) {
	_halfedge = halfedge;
	reset();
    }

    /**
     *
     *
     * @param c
     */
    public void push(final WB_Coordinate c) {
	HE_Halfedge he = _halfedge;
	do {
	    he.getVertex().getPoint().addSelf(c);
	    he = he.getNextInFace();
	} while (he != _halfedge);
    }

    /**
     *
     */
    public void clearHalfedge() {
	_halfedge = null;
    }

    /**
     *
     *
     * @return
     */
    public WB_Plane toPlane() {
	WB_Vector fn = getFaceNormal();
	if (fn.getSqLength3D() < 0.5) {
	    if (WB_Epsilon.isEqualAbs(_halfedge.getVertex().xd(), _halfedge
		    .getEndVertex().xd())) {
		fn = new WB_Vector(1, 0, 0);
	    } else {
		fn = new WB_Vector(0, 0, 1);
	    }
	}
	return new WB_Plane(getFaceCenter(), fn);
    }

    /**
     *
     *
     * @param d
     * @return
     */
    public WB_Plane toPlane(final double d) {
	final WB_Vector fn = getFaceNormal();
	return new WB_Plane(getFaceCenter().addMulSelf(d, fn), fn);
    }

    /**
     *
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
	}
    }

    /**
     *
     *
     * @return
     */
    public int[][] getTriangles() {
	return getTriangles(true);
    }

    /**
     *
     *
     * @param optimize
     * @return
     */
    public int[][] getTriangles(final boolean optimize) {
	// tracker.setStatus("Starting getTriangles() in face " + getKey() +
	// ".");
	if (triangles == null) {
	    // tracker.setStatus("Triangles not calculated, starting face triangulation.");
	    final int fo = getFaceOrder();
	    if (fo < 3) {
		return new int[][] { { 0, 0, 0 } };
	    } else if (fo == 3) {
		// tracker.setStatus("Triangulating face with " + fo
		// + " vertices.");
		// logger.trace("Trivial triangulation of triangle face.");
		return new int[][] { { 0, 1, 2 } };
	    } else if (isDegenerate()) {
		// degenerate face
		triangles = new int[fo - 2][3];
		for (int i = 0; i < (fo - 2); i++) {
		    triangles[i] = new int[] { 0, i + 1, i + 2 };
		}
	    } else if (fo == 4) {
		// tracker.setStatus("Triangulating face with " + fo
		// + " vertices.");
		// logger.trace("Triangulation of quad face.");
		final WB_Point[] points = new WB_Point[4];
		int i = 0;
		HE_Halfedge he = _halfedge;
		do {
		    points[i] = new WB_Point(he.getVertex().xd(), he
			    .getVertex().yd(), he.getVertex().zd());
		    he = he.getNextInFace();
		    i++;
		} while (he != _halfedge);
		return WB_Triangulate.triangulateQuad(points[0], points[1],
			points[2], points[3]);
	    } else {
		// logger.trace("Starting triangulation of face with " + fo +
		// " faces.");
		// tracker.setStatus("Triangulating face with " + fo
		// + " vertices.");
		triangles = WB_Triangulate.getPolygonTriangulation2D(
			this.toPolygon(), true).getTriangles();
	    }
	}
	// // logger.debug("Returning triangles.");
	// tracker.setStatus("Triangulation done.");
	return triangles;
    }

    /**
     *
     */
    public void reset() {
	triangles = null;
    }

    /**
     *
     *
     * @return
     */
    public WB_AABB toAABB() {
	final WB_AABB aabb = new WB_AABB();
	HE_Halfedge he = getHalfedge();
	do {
	    aabb.expandToInclude(he.getVertex());
	    he = he.getNextInFace();
	} while (he != getHalfedge());
	return aabb;
    }

    /**
     *
     *
     * @return
     */
    public WB_Triangle toTriangle() {
	if (getFaceOrder() != 3) {
	    return null;
	}
	return new WB_Triangle(_halfedge.getVertex(), _halfedge.getEndVertex(),
		_halfedge.getNextInFace().getEndVertex());
    }

    /**
     *
     *
     * @return
     */
    public WB_Polygon toPolygon() {
	final int n = getFaceOrder();
	if (n == 0) {
	    return null;
	}
	final WB_Point[] points = new WB_Point[n];
	int i = 0;
	HE_Halfedge he = _halfedge;
	do {
	    points[i++] = new WB_Point(he.getVertex().xd(),
		    he.getVertex().yd(), he.getVertex().zd());
	    he = he.getNextInFace();
	} while (he != _halfedge);
	return gf.createSimplePolygon(points);
    }

    /**
     *
     *
     * @return
     */
    public WB_Polygon toPlanarPolygon() {
	final int n = getFaceOrder();
	if (n == 0) {
	    return null;
	}
	final WB_Point[] points = new WB_Point[n];
	final WB_Plane P = toPlane();
	int i = 0;
	HE_Halfedge he = _halfedge;
	do {
	    points[i] = projectOnPlane(he.getVertex(), P);
	    he = he.getNextInFace();
	    i++;
	} while (he != _halfedge);
	return gf.createSimplePolygon(points);
    }

    /**
     *
     *
     * @return
     */
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
	for (int i = 0; i < (getFaceOrder() - 1); i++) {
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

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_HasColor#getColor()
     */
    @Override
    public int getColor() {
	return facecolor;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_HasColor#setColor(int)
     */
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

    /**
     *
     *
     * @return
     */
    public boolean isDegenerate() {
	return getFaceNormal().getLength3D() < 0.5;
    }

    /**
     *
     *
     * @param el
     */
    public void copyProperties(final HE_Face el) {
	super.copyProperties(el);
	facecolor = el.getColor();
	textureId = el.textureId;
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
	triangles = null;
    }

    /**
     *
     */
    public void checkValidity() {
	final Coordinate[] coords = new Coordinate[getFaceOrder() + 1];
	final WB_Point point = geometryfactory.createPoint();
	final WB_Context2D context = geometryfactory
		.createEmbeddedPlane(toPlane());
	HE_Halfedge he = _halfedge;
	int i = 0;
	do {
	    context.pointTo2D(he.getVertex(), point);
	    coords[i] = new Coordinate(point.xd(), point.yd(), i);
	    he = he.getNextInFace();
	    i++;
	} while (he != _halfedge);
	context.pointTo2D(he.getVertex(), point);
	coords[i] = new Coordinate(point.xd(), point.yd(), i);
	he = he.getNextInFace();
	final Polygon inputPolygon = new GeometryFactory()
	.createPolygon(coords);
	final IsValidOp isValidOp = new IsValidOp(inputPolygon);
	if (!IsValidOp.isValid(inputPolygon)) {
	    System.out.println(this);
	    System.out.println(this.getFaceArea() + " " + this.getFaceNormal());
	    he = _halfedge;
	    i = 0;
	    do {
		System.out.println("  " + i + ": " + he.getVertex());
		he = he.getNextInFace();
		i++;
	    } while (he != _halfedge);
	    System.out.println(isValidOp.getValidationError());
	}
    }

    public int getTextureId() {
	return textureId;
    }

    public void setTextureId(final int i) {
	textureId = i;
    }
}
