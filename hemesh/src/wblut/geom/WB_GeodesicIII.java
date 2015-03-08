/*
 * 
 */
package wblut.geom;

import java.security.InvalidParameterException;
import java.util.List;
import javolution.util.FastTable;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class WB_GeodesicIII {
    
    /**
     * 
     */
    public static final int TETRAHEDRON = 0;
    
    /**
     * 
     */
    public static final int OCTAHEDRON = 1;
    
    /**
     * 
     */
    public static final int ICOSAHEDRON = 2;
    
    /**
     * 
     */
    private final double[][] centralanglesabc;
    
    /**
     * 
     */
    private static double PI = Math.PI;
    
    /**
     * 
     */
    private static double[][] surfaceanglesABC = new double[][] {
	{ PI / 3.0, PI / 3.0, PI / 2.0 }, { PI / 3.0, PI / 4.0, PI / 2.0 },
	{ PI / 3.0, PI / 5.0, PI / 2.0 } };
    
    /**
     * 
     */
    private final int b, c, v;
    
    /**
     * 
     */
    private final double radius;
    
    /**
     * 
     */
    private final int type;
    
    /**
     * 
     */
    private WB_FaceListMesh mesh;
    
    /**
     * 
     */
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    
    /**
     * 
     */
    public List<WB_Point> points;
    
    /**
     * 
     */
    public List<WB_Point> PPT;
    
    /**
     * 
     */
    public List<WB_Point> zeropoints;

    /**
     * 
     *
     * @param radius 
     * @param b 
     * @param c 
     * @param type 
     */
    public WB_GeodesicIII(final double radius, final int b, final int c,
	    final int type) {
	if ((b <= 0) || (c <= 0) || (b == c)) {
	    throw new InvalidParameterException("Invalid values for b and c.");
	}
	if ((type < 0) || (type > 2)) {
	    throw new InvalidParameterException(
		    "Type should be one of TETRAHEDRON (0), OCTAHEDRON (1) or ICOSAHEDRON (2).");
	}
	this.type = type;
	this.radius = radius;
	this.b = b;
	this.c = c;
	this.v = b + c;
	centralanglesabc = new double[3][3];
	for (int i = 0; i < 3; i++) {
	    centralanglesabc[i][0] = Math.acos(Math.cos(surfaceanglesABC[i][0])
		    / Math.sin(surfaceanglesABC[i][1]));// cos a = cos A / sin B
	    centralanglesabc[i][1] = Math.acos(Math.cos(surfaceanglesABC[i][1])
		    / Math.sin(surfaceanglesABC[i][0]));// cos b = cos B / sin A
	    centralanglesabc[i][2] = Math.acos(Math.cos(centralanglesabc[i][0])
		    * Math.cos(centralanglesabc[i][1]));// cos c = cos a x cos b
	}
    }

    /**
     * 
     *
     * @return 
     */
    public WB_FaceListMesh getMesh() {
	createMesh();
	return mesh;
    }

    /**
     * 
     */
    private void createMesh() {
	final WB_TriGrid trigrid = new WB_TriGrid();
	final WB_Point p0 = trigrid.getPoint(0, 0);
	final WB_Point p1 = trigrid.getPoint(b, c);
	final WB_Point p2 = trigrid.getPoint(b + c, c - (b + c));
	WB_Point p;
	WB_Point cp;
	double scalefactor = 1.0;
	WB_Vector zshift = new WB_Vector(0, 0, 1);
	switch (type) {
	case TETRAHEDRON:
	    scalefactor = Math.sqrt(8.0 / 3.0) / p1.getLength3D();
	    zshift = new WB_Vector(0, 0, 1.0 / 3.0);
	    break;
	case OCTAHEDRON:
	    scalefactor = Math.sqrt(2.0) / p1.getLength3D();
	    zshift = new WB_Vector(0, 0, Math.sqrt(3.0) / 3.0);
	    break;
	case ICOSAHEDRON:
	default:
	    scalefactor = 1.0 / Math.sin(0.4 * Math.PI) / p1.getLength3D();
	    zshift = new WB_Vector(0, 0,
		    ((Math.sqrt(3) / 12.0) * (3 + Math.sqrt(5)))
			    / Math.sin(0.4 * Math.PI));
	}
	p0.mulSelf(scalefactor);
	p1.mulSelf(scalefactor);
	p2.mulSelf(scalefactor);
	trigrid.setScale(scalefactor);
	PPT = new FastTable<WB_Point>();
	for (int i = -v; i <= v; i++) {
	    for (int j = -v; j <= v; j++) {
		p = trigrid.getPoint(i, j);
		cp = WB_GeometryOp.getClosestPointToTriangle3D(p, p0, p1, p2);
		if (WB_Epsilon.isZeroSq(cp.getSqDistance3D(p))) {
		    PPT.add(p);
		}
	    }
	}
	zeropoints = new FastTable<WB_Point>();
	final double angle = (Math.PI / 6.0) - p1.heading2D();
	final WB_Point center = gf.createMidpoint(p0, p1, p2).mulSelf(-1);
	WB_Transform T = new WB_Transform().addTranslate(center)
		.addRotateZ(angle).addTranslate(zshift)
		.addRotateY(centralanglesabc[type][2]);
	for (int i = 0; i < PPT.size(); i++) {
	    p = T.applyAsPoint(PPT.get(i));
	    p.normalizeSelf();
	    p.mulSelf(radius);
	    zeropoints.add(p);
	    PPT.get(i).applyAsPointSelf(T);
	    PPT.get(i).mulSelf(radius);
	}
	final double threshold = zeropoints.get(0).getDistance3D(
		zeropoints.get(1))
		/ (2 * v);
	/*
	 * T = new WB_Transform().addRotateY(Math.PI / 180 * 36); for (WB_Point
	 * pos : zeropoints) { pos._applyAsPointSelf(T);
	 * 
	 * }
	 */
	points = new FastTable<WB_Point>();
	points.addAll(zeropoints);
	switch (type) {
	case TETRAHEDRON:
	    T = new WB_Transform().addRotateZ((Math.PI / 180.0) * 120.0);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ((Math.PI / 180.0) * 240.0);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ(Math.PI).addRotateY(
		    (Math.PI / 180.0) * 250.5288);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    break;
	case OCTAHEDRON:
	    final List<WB_Point> points4 = new FastTable<WB_Point>();
	    T = new WB_Transform().addRotateZ(Math.PI).addRotateY(Math.PI);
	    for (final WB_Point point : zeropoints) {
		points4.add(T.applyAsPoint(point));
	    }
	    points.addAll(points4);
	    T = new WB_Transform().addRotateZ(Math.PI / 2.0);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points4) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ(Math.PI / 2.0);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points4) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ(Math.PI);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points4) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ(1.5 * Math.PI);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points4) {
		points.add(T.applyAsPoint(point));
	    }
	    break;
	case ICOSAHEDRON:
	default:
	    T = new WB_Transform().addRotateZ(Math.PI).addRotateY(
		    (Math.PI / 180) * 116.5651);
	    final List<WB_Point> points5 = new FastTable<WB_Point>();
	    for (final WB_Point point : zeropoints) {
		points5.add(T.applyAsPoint(point));
	    }
	    points.addAll(points5);
	    T = new WB_Transform().addRotateY((Math.PI / 180) * 63.43495)
		    .addRotateZ((Math.PI / 180) * 36);
	    final List<WB_Point> points6 = new FastTable<WB_Point>();
	    for (final WB_Point point : zeropoints) {
		points6.add(T.applyAsPoint(point));
	    }
	    points.addAll(points6);
	    T = new WB_Transform().addRotateY(-Math.PI).addRotateZ(
		    (-Math.PI / 180) * 144);
	    final List<WB_Point> points15 = new FastTable<WB_Point>();
	    for (final WB_Point point : zeropoints) {
		points15.add(T.applyAsPoint(point));
	    }
	    points.addAll(points15);
	    T = new WB_Transform().addRotateZ((Math.PI / 180.0) * 72.0);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points5) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points6) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points15) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ((Math.PI / 180.0) * 144);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points5) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points6) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points15) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ((Math.PI / 180.0) * 216);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points5) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points6) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points15) {
		points.add(T.applyAsPoint(point));
	    }
	    T = new WB_Transform().addRotateZ((Math.PI / 180.0) * 288);
	    for (final WB_Point point : zeropoints) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points5) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points6) {
		points.add(T.applyAsPoint(point));
	    }
	    for (final WB_Point point : points15) {
		points.add(T.applyAsPoint(point));
	    }
	}
	mesh = gf.createConvexHullWithThreshold(points, false, threshold);
    }
}
