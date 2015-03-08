/*
 * 
 */
package wblut.geom;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import java.awt.Font;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javolution.util.FastTable;
import wblut.external.QuickHull3D.WB_QuickHull3D;
import wblut.external.straightskeleton.Corner;
import wblut.external.straightskeleton.Edge;
import wblut.external.straightskeleton.Loop;
import wblut.external.straightskeleton.LoopL;
import wblut.external.straightskeleton.Machine;
import wblut.external.straightskeleton.Output.Face;
import wblut.external.straightskeleton.Point3d;
import wblut.external.straightskeleton.Skeleton;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;
import com.vividsolutions.jts.densify.Densifier;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.simplify.TopologyPreservingSimplifier;

/**
 * 
 */
public class WB_GeometryFactory {
    
    /**
     * 
     */
    private final WB_Point origin;
    
    /**
     * 
     */
    private final WB_Vector X;
    
    /**
     * 
     */
    private final WB_Vector Y;
    
    /**
     * 
     */
    private final WB_Vector Z;
    
    /**
     * 
     */
    private final WB_Vector mX;
    
    /**
     * 
     */
    private final WB_Vector mY;
    
    /**
     * 
     */
    private final WB_Vector mZ;
    
    /**
     * 
     */
    private WB_Plane XY;
    
    /**
     * 
     */
    private WB_Plane YZ;
    
    /**
     * 
     */
    private WB_Plane ZX;
    
    /**
     * 
     */
    private WB_Plane YX;
    
    /**
     * 
     */
    private WB_Plane ZY;
    
    /**
     * 
     */
    private WB_Plane XZ;
    
    /**
     * 
     */
    private WB_CoordinateSystem currentCS;
    
    /**
     * 
     */
    private WB_Transform toWorld;
    
    /**
     * 
     */
    private WB_CoordinateSystem world;
    
    /**
     * 
     */
    private WB_ShapeReader shapereader;

    /**
     * 
     */
    protected WB_GeometryFactory() {
	origin = createPoint(0, 0, 0);
	X = createVector(1, 0, 0);
	Y = createVector(0, 1, 0);
	Z = createVector(0, 0, 1);
	mX = createVector(-1, 0, 0);
	mY = createVector(0, -1, 0);
	mZ = createVector(0, 0, -1);
    }

    /**
     * 
     */
    private static final WB_GeometryFactory factory = new WB_GeometryFactory();

    /**
     * 
     *
     * @return 
     */
    public static WB_GeometryFactory instance() {
	return factory;
    }

    /**
     * 
     *
     * @param CS 
     */
    public void setCurrentCS(final WB_CoordinateSystem CS) {
	if (currentCS == null) {
	    currentCS = WORLD();
	}
	currentCS = CS;
	toWorld = currentCS.getTransformToWorld();
    }

    /**
     * 
     *
     * @return 
     */
    public WB_CoordinateSystem getCurrentCS() {
	if (currentCS == null) {
	    currentCS = WORLD();
	}
	return currentCS;
    }

    /**
     * 
     */
    public void resetCurrentCS() {
	currentCS = WORLD();
	toWorld = currentCS.getTransformToWorld();
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Transform toWorld() {
	if (currentCS == null) {
	    currentCS = WORLD();
	    toWorld = currentCS.getTransformToWorld();
	}
	return toWorld;
    }

    /**
     *
     * @return default origin
     */
    public WB_Point origin() {
	return origin;
    }

    /**
     *
     * @return default X-axis direction
     */
    public WB_Vector X() {
	return X;
    }

    /**
     *
     * @return default Y-axis direction
     */
    public WB_Vector Y() {
	return Y;
    }

    /**
     *
     * @return default Z-axis direction
     */
    public WB_Vector Z() {
	return Z;
    }

    /**
     *
     * @return negative X-axis direction
     */
    public WB_Vector minX() {
	return mX;
    }

    /**
     *
     * @return negative Y-axis direction
     */
    public WB_Vector minY() {
	return mY;
    }

    /**
     *
     * @return default Z-axis direction
     */
    public WB_Vector minZ() {
	return mZ;
    }

    /**
     *
     * @return default XY-plane
     */
    public WB_Plane XY() {
	if (XY == null) {
	    XY = createPlane(origin(), Z());
	}
	return XY;
    }

    /**
     *
     * @return default YZ-plane
     */
    public WB_Plane YZ() {
	if (YZ == null) {
	    YZ = createPlane(origin(), X());
	}
	return YZ;
    }

    /**
     *
     * @return default ZX-plane
     */
    public WB_Plane ZX() {
	if (ZX == null) {
	    ZX = createPlane(origin(), Y());
	}
	return ZX;
    }

    /**
     *
     * @return default YX-plane
     */
    public WB_Plane YX() {
	if (YX == null) {
	    YX = createPlane(origin(), minZ());
	}
	return XY;
    }

    /**
     *
     * @return default ZY-plane
     */
    public WB_Plane ZY() {
	if (ZY == null) {
	    ZY = createPlane(origin(), minX());
	}
	return ZY;
    }

    /**
     *
     * @return default XZ-plane
     */
    public WB_Plane XZ() {
	if (XZ == null) {
	    XZ = createPlane(origin(), minY());
	}
	return XZ;
    }

    /**
     *
     * @return WORLD coordinate system
     */
    public WB_CoordinateSystem WORLD() {
	if (world == null) {
	    world = new WB_CoordinateSystem();
	}
	return world;
    }

    /**
     * Create a new right-handed coordinate system. The WORLD CS is the default
     * parent; the z-coordinate of X is ignored and X is normalized, Z is
     * (0,0,1) and Y is created from X and Z
     *
     * @param origin
     * @param X
     *
     * @return coordinate
     */
    public WB_CoordinateSystem createCSFromOX(final WB_Coordinate origin,
	    final WB_Coordinate X) {
	final WB_Point lOrigin = createPoint(origin.xd(), origin.yd());
	final WB_Vector lX = createNormalizedVector(X.xd(), X.yd());
	final WB_Vector lY = createVector(-lX.yd(), lX.xd());
	return createCSFromOXY(lOrigin, lX, lY);
    }

    /**
     * Create a new right-handed coordinate ; the z-coordinate of X is ignored
     * and X is normalized, Z is (0,0,1) and Y is created from X and Z.
     *
     * @param origin 
     * @param X 
     * @param parent            parent coordinate system
     * @return coordinate system
     */
    public WB_CoordinateSystem createCSFromOX(final WB_Coordinate origin,
	    final WB_Coordinate X, final WB_CoordinateSystem parent) {
	final WB_Point lOrigin = createPoint(origin.xd(), origin.yd());
	final WB_Vector lX = createNormalizedVector(X.xd(), X.yd());
	final WB_Vector lY = createVector(-lX.yd(), lX.xd());
	return createCSFromOXY(lOrigin, lX, lY, parent);
    }

    /**
     * Create a new right-handed coordinate system. The WORLD CS is the default
     * parent; X is normalized, Y is normalized and orthogonalized and Z is
     * created from X and Y
     *
     * @param origin
     * @param X
     * @param Y
     *
     * @return coordinate system
     */
    public WB_CoordinateSystem createCSFromOXY(final WB_Coordinate origin,
	    final WB_Coordinate X, final WB_Coordinate Y) {
	final WB_Vector lX = createNormalizedVector(X);
	WB_Vector lY = createNormalizedVector(Y);
	final WB_Vector lZ = lX.cross(lY);
	if (WB_Epsilon.isZeroSq(lZ.getSqLength3D())) {
	    throw new IllegalArgumentException("Vectors can not be parallel.");
	}
	lZ.normalizeSelf();
	lY = createNormalizedVector(lZ.cross(lX));
	return new WB_CoordinateSystem(origin, lX, lY, lZ, WORLD());
    }

    /**
     * Create a new right-handed coordinate with a defined parent. X is
     * normalized, Y is normalized and orthogonalized and Z is created from X
     * and Y
     *
     * @param origin
     * @param X
     * @param Y
     * @param parent
     *            parent coordinate system
     *
     * @return coordinate system
     */
    public WB_CoordinateSystem createCSFromOXY(final WB_Coordinate origin,
	    final WB_Coordinate X, final WB_Coordinate Y,
	    final WB_CoordinateSystem parent) {
	final WB_Vector lX = createNormalizedVector(X);
	WB_Vector lY = createNormalizedVector(Y);
	final WB_Vector lZ = lX.cross(lY);
	if (WB_Epsilon.isZeroSq(lZ.getSqLength3D())) {
	    throw new IllegalArgumentException("Vectors can not be parallel.");
	}
	lZ.normalizeSelf();
	lY = createNormalizedVector(lZ.cross(lX));
	return new WB_CoordinateSystem(origin, lX, lY, lZ, parent);
    }

    /**
     * 
     *
     * @param origin 
     * @param X 
     * @param Y 
     * @param Z 
     * @param parent 
     * @return 
     */
    public WB_CoordinateSystem createCSFromOXYZ(final WB_Coordinate origin,
	    final WB_Coordinate X, final WB_Coordinate Y,
	    final WB_Coordinate Z, final WB_CoordinateSystem parent) {
	return new WB_CoordinateSystem(origin, X, Y, Z, parent);
    }

    /**
     * 
     *
     * @param origin 
     * @param X 
     * @param Y 
     * @param Z 
     * @return 
     */
    public WB_CoordinateSystem createCSFromOXYZ(final WB_Coordinate origin,
	    final WB_Coordinate X, final WB_Coordinate Y, final WB_Coordinate Z) {
	return new WB_CoordinateSystem(origin, X, Y, Z, WORLD());
    }

    /**
     * 
     *
     * @param CS 
     * @param T 
     * @param parent 
     * @return 
     */
    public WB_CoordinateSystem createTransformedCS(
	    final WB_CoordinateSystem CS, final WB_Transform T,
	    final WB_CoordinateSystem parent) {
	return CS.apply(T, parent);
    }

    /**
     * 
     *
     * @param CS 
     * @param T 
     * @return 
     */
    public WB_CoordinateSystem createTransformedCS(
	    final WB_CoordinateSystem CS, final WB_Transform T) {
	return CS.apply(T);
    }

    /**
     *
     * @return default 2D context: XY plane
     */
    public WB_Context2D createEmbeddedPlane() {
	return new WB_EmbeddedPlane();
    }

    /**
     * Create a 2D context from an offset coordinate plane.
     *
     * @param mode            0=YZ, 1=ZX, 2=XY, 3=ZY, 4=XZ, 5=YX
     * @param offset            offset of the 2D context origin along plane normal
     * @return 2D context
     */
    public WB_Context2D createEmbeddedPlane(final int mode, final double offset) {
	return new WB_EmbeddedPlane(mode, offset);
    }

    /**
     * Create a 2D context from a coordinate plane.
     *
     * @param mode            0=YZ, 1=ZX, 2=XY, 3=ZY, 4=XZ, 5=YX
     * @return 2D context
     */
    public WB_Context2D createEmbeddedPlane(final int mode) {
	return new WB_EmbeddedPlane(mode);
    }

    /**
     * Create a 2D context from an offset plane.
     *
     * @param P            plane
     * @param offset            offset of the 2D context origin along plane normal
     * @return 2D context
     */
    public WB_Context2D createEmbeddedPlane(final WB_Plane P,
	    final double offset) {
	return new WB_EmbeddedPlane(P, offset);
    }

    /**
     * Create a 2D context from a plane.
     *
     * @param P            plane
     * @return 2D context
     */
    public WB_Context2D createEmbeddedPlane(final WB_Plane P) {
	return new WB_EmbeddedPlane(P);
    }

    /**
     * New point at origin.
     *
     * @return new point at origin
     */
    public WB_Point createPoint() {
	return new WB_Point(0, 0, 0);
    }

    /**
     * Create new point.If parameter p is same class as caller then the original
     * point is returned. Unsafe if the point is aftwerwards modified with
     * unsafe operators (_setSelf,_addSelf,_subSelf,..)
     *
     * @param p
     *            point
     * @return copy of point
     */
    public WB_Point createPoint(final WB_Coordinate p) {
	return new WB_Point(p);
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Point createPoint(final double[] p) {
	return new WB_Point(p);
    }

    /**
     * Copy of coordinate as point, z-ordinate is ignored.
     *
     * @param p            point
     * @return copy of point
     */
    public WB_Point createPoint2D(final WB_Coordinate p) {
	return createPoint(p.xd(), p.yd(), 0);
    }

    /**
     * Point from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Cartesian_coordinate_system
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @return 2D point
     */
    public WB_Point createPoint(final double _x, final double _y) {
	return createPoint(_x, _y, 0);
    }

    /**
     * Point from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Elliptic_coordinates
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @param _z
     *            z
     * @return 3D point
     */
    public WB_Point createPoint(final double _x, final double _y,
	    final double _z) {
	return new WB_Point(_x, _y, _z);
    }

    /**
     * 
     *
     * @param coord 
     * @return 
     */
    public WB_Point createPoint(final Coordinate coord) {
	return createPoint(coord.x, coord.y);
    }

    /**
     * Interpolated point.
     *
     * @param p            point
     * @param q            point
     * @param f            interpolation value, p=0,q=1
     * @return copy of point
     */
    public WB_Point createInterpolatedPoint(final WB_Coordinate p,
	    final WB_Coordinate q, final double f) {
	return new WB_Point(((1.0 - f) * p.xd()) + (f * q.xd()),
		((1.0 - f) * p.yd()) + (f * q.yd()), ((1.0 - f) * p.zd())
			+ (f * q.zd()));
    }

    /**
     * Interpolated point, z-ordinate is ignored.
     *
     * @param p            point
     * @param q            point
     * @param f            interpolation value, p=0,q=1
     * @return copy of point
     */
    public WB_Point createInterpolatedPoint2D(final WB_Coordinate p,
	    final WB_Coordinate q, final double f) {
	return createPoint(((1.0 - f) * p.xd()) + (f * q.xd()),
		((1.0 - f) * p.yd()) + (f * q.yd()));
    }

    /**
     * Point from polar coordinates
     * http://en.wikipedia.org/wiki/Polar_coordinate_system
     *
     * @param r
     *            radius
     * @param phi
     *            angle
     * @return 2D point
     */
    public WB_Point createPointFromPolar(final double r, final double phi) {
	return createPoint(r * Math.cos(phi), r * Math.sin(phi));
    }

    /**
     * Point from bipolar coordinates
     * http://en.wikipedia.org/wiki/Bipolar_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            bipolar coordinate
     * @param tau
     *            bipolar coordinate
     * @return 2D point
     */
    public WB_Point createPointFromBipolar(final double a, final double sigma,
	    final double tau) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createPoint(Math.sinh(tau) * invdenom, Math.sin(sigma)
		* invdenom);
    }

    /**
     * Point from parabolic coordinates
     * http://en.wikipedia.org/wiki/Parabolic_coordinates
     *
     * @param sigma
     *            parabolic coordinate
     * @param tau
     *            parabolic coordinate
     * @return 2D point
     */
    public WB_Point createPointFromParabolic(final double sigma,
	    final double tau) {
	return createPoint(sigma * tau, 0.5 * ((tau * tau) - (sigma * sigma)));
    }

    /**
     * Point from hyperbolic coordinates
     * http://en.wikipedia.org/wiki/Hyperbolic_coordinates
     *
     * @param u
     *            hyperbolic angle
     * @param v
     *            geometric mean >0
     * @return 2D point
     */
    public WB_Point createPointFromHyperbolic(final double u, final double v) {
	return createPoint(v * Math.exp(u), v * Math.exp(-u));
    }

    /**
     * Point from elliptic coordinates
     * http://en.wikipedia.org/wiki/Elliptic_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            elliptic coordinate >=0
     * @param tau
     *            elliptic coordinate between -1 and 1
     * @return 2D point
     */
    public WB_Point createPointFromElliptic(final double a, final double sigma,
	    final double tau) {
	return createPoint(a * sigma * tau,
		Math.sqrt(a * a * ((sigma * sigma) - 1) * (1 - (tau * tau))));
    }

    /**
     * Incenter of triangle, z-ordinate is ignored.
     *
     * @param tri            triangle
     * @return incenter
     */
    public WB_Point createIncenter(final WB_Triangle tri) {
	return createPointFromTrilinearCoordinates(1, 1, 1, tri);
    }

    /**
     * Orthocenter of triangle, z-ordinate is ignored.
     *
     * @param tri            triangle
     * @return orthocenter
     */
    public WB_Point createOrthocenter(final WB_Triangle tri) {
	final double a2 = tri.a() * tri.a();
	final double b2 = tri.b() * tri.b();
	final double c2 = tri.c() * tri.c();
	return createPointFromBarycentricCoordinates(((a2 + b2) - c2)
		* ((a2 - b2) + c2), ((a2 + b2) - c2) * (-a2 + b2 + c2),
		((a2 - b2) + c2) * (-a2 + b2 + c2), tri);
    }

    /**
     * Closest point to 2D line, z-ordinate is ignored.
     *
     * @param p            WB_Coordinate
     * @param L            WB_Line
     * @return closest point on line
     */
    public WB_Point createClosestPointOnLine2D(final WB_Coordinate p,
	    final WB_Line L) {
	if (WB_Epsilon.isZero(L.getDirection().xd())) {
	    return createPoint(L.getOrigin().xd(), p.yd());
	}
	if (WB_Epsilon.isZero(L.getDirection().yd())) {
	    return createPoint(p.xd(), L.getOrigin().yd());
	}
	final double m = L.getDirection().yd() / L.getDirection().xd();
	final double b = L.getOrigin().yd() - (m * L.getOrigin().xd());
	final double x = (((m * p.yd()) + p.xd()) - (m * b)) / ((m * m) + 1);
	final double y = ((m * m * p.yd()) + (m * p.xd()) + b) / ((m * m) + 1);
	return createPoint(x, y);
    }

    /**
     * Closest points between two 2D lines, z-ordinate is ignored.
     *
     * @param L1            2D line
     * @param L2            2D line
     * @return if crossing: intersection, if parallel: origin of L1 + point on
     *         L2 closest to origin of L1
     */
    public List<WB_Point> createClosestPoint(final WB_Line L1, final WB_Line L2) {
	final List<WB_Point> result = new ArrayList<WB_Point>();
	final double a = L1.getDirection().dot(L1.getDirection());
	final double b = L1.getDirection().dot(L2.getDirection());
	final WB_Point r = createPoint(L1.getOrigin()).sub(L2.getOrigin());
	final double c = L1.getDirection().dot(r);
	final double e = L2.getDirection().dot(L2.getDirection());
	final double f = L2.getDirection().dot(r);
	double denom = (a * e) - (b * b);
	if (WB_Epsilon.isZero(denom)) {
	    final double t2 = r.dot(L1.getDirection());
	    result.add(createPoint(L1.getOrigin()));
	    result.add(createPoint(L2.getPointOnLine(t2)));
	    return result;
	}
	denom = 1.0 / denom;
	final double t1 = ((b * f) - (c * e)) * denom;
	result.add(createPoint(L1.getPointOnLine(t1)));
	return result;
    }

    /**
     * Gets intersection points of two circles, z-ordinate is ignored.
     *
     * @param C0 
     * @param C1 
     * @return intersection points of two circles
     */
    public List<WB_Point> createIntersectionPoints(final WB_Circle C0,
	    final WB_Circle C1) {
	final List<WB_Point> result = new ArrayList<WB_Point>();
	final WB_Vector u = createVector2D(C1.getCenter()).sub(C0.getCenter());
	final double d2 = u.getSqLength3D();
	final double d = Math.sqrt(d2);
	if (WB_Epsilon.isEqualAbs(d, C0.getRadius() + C1.getRadius())) {
	    result.add(createInterpolatedPoint(C0.getCenter(), C1.getCenter(),
		    C0.getRadius() / (C0.getRadius() + C1.getRadius())));
	    return result;
	}
	if ((d > (C0.getRadius() + C1.getRadius()))
		|| (d < WB_Math.fastAbs(C0.getRadius() - C1.getRadius()))) {
	    return result;
	}
	final double r02 = C0.getRadius() * C0.getRadius();
	final double r12 = C1.getRadius() * C1.getRadius();
	final double a = ((r02 - r12) + d2) / (2 * d);
	final double h = Math.sqrt(r02 - (a * a));
	final WB_Point c = createPoint(C0.getCenter()).addMulSelf(a / d, u);
	final double p0x = c.xd()
		+ ((h * (C1.getCenter().yd() - C0.getCenter().yd())) / d);
	final double p0y = c.yd()
		- ((h * (C1.getCenter().xd() - C0.getCenter().xd())) / d);
	final double p1x = c.xd()
		- ((h * (C1.getCenter().yd() - C0.getCenter().yd())) / d);
	final double p1y = c.yd()
		+ ((h * (C1.getCenter().xd() - C0.getCenter().xd())) / d);
	final WB_Point p0 = createPoint(p0x, p0y);
	result.add(p0);
	final WB_Point p1 = createPoint(p1x, p1y);
	if (!WB_Epsilon.isZeroSq(p0.getSqDistance3D(p1))) {
	    result.add(p1);
	}
	return result;
    }

    /**
     * Gets intersection points of 2D line and circle, z-ordinate is ignored.
     *
     * @param L 
     * @param C 
     * @return intersection points of line and circle
     */
    public List<WB_Point> createIntersectionPoints(final WB_Line L,
	    final WB_Circle C) {
	final List<WB_Point> result = new ArrayList<WB_Point>();
	final double b = 2 * ((L.getDirection().xd() * (L.getOrigin().xd() - C
		.getCenter().xd())) + (L.getDirection().yd() * (L.getOrigin()
		.yd() - C.getCenter().yd())));
	final double c = (C.getCenter().getSqLength3D() + L.getOrigin()
		.getSqLength3D())
		- (2 * ((C.getCenter().xd() * L.getOrigin().xd()) + (C
			.getCenter().yd() * L.getOrigin().yd())))
		- (C.getRadius() * C.getRadius());
	double disc = (b * b) - (4 * c);
	if (disc < -WB_Epsilon.EPSILON) {
	    return result;
	}
	if (WB_Epsilon.isZero(disc)) {
	    result.add(createPoint(L.getPointOnLine(-0.5 * b)));
	    return result;
	}
	disc = Math.sqrt(disc);
	result.add(createPoint(L.getPointOnLine(0.5 * (-b + disc))));
	result.add(createPoint(L.getPointOnLine(0.5 * (-b - disc))));
	return result;
    }

    /**
     * Gets intersection points of two 2D lines, z-ordinate is ignored.
     *
     * @param L1 
     * @param L2 
     * @return intersection point
     */
    public WB_Point createIntersectionPoint2D(final WB_Line L1, final WB_Line L2) {
	final double a = L1.getDirection().dot(L1.getDirection());
	final double b = L1.getDirection().dot(L2.getDirection());
	final WB_Vector r = createVector2D(L1.getOrigin().sub(L2.getOrigin()));
	final double c = L1.getDirection().dot(r);
	final double e = L2.getDirection().dot(L2.getDirection());
	final double f = L2.getDirection().dot(r);
	double denom = (a * e) - (b * b);
	if (WB_Epsilon.isZero(denom)) {
	    return null;
	}
	denom = 1.0 / denom;
	final double t1 = ((b * f) - (c * e)) * denom;
	final double t2 = ((a * f) - (b * c)) * denom;
	final WB_Point p1 = L1.getPointOnLine(t1);
	final WB_Point p2 = L2.getPointOnLine(t2);
	return p1.mulAddMul(0.5, 0.5, p2);
    }

    /**
     * Mirror 2D point about 2D line.
     *
     * @param p 
     * @param x0 
     * @param y0 
     * @param x1 
     * @param y1 
     * @return mirrored point
     */
    public WB_Point createMirrorPoint(final WB_Coordinate p, final double x0,
	    final double y0, final double x1, final double y1) {
	double dx, dy, a, b;
	double x2, y2;
	dx = x1 - x0;
	dy = y1 - y0;
	a = ((dx * dx) - (dy * dy)) / ((dx * dx) + (dy * dy));
	b = (2 * dx * dy) / ((dx * dx) + (dy * dy));
	x2 = (a * (p.xd() - x0)) + (b * (p.yd() - y0)) + x0;
	y2 = ((b * (p.xd() - x0)) - (a * (p.yd() - y0))) + y0;
	return createPoint(x2, y2);
    }

    /**
     * Mirror 2D point about 2D line.
     *
     * @param p            WB_Coordinate
     * @param L            WB_Linear
     * @return mirrored point
     */
    public WB_Point createMirrorPoint(final WB_Coordinate p, final WB_Linear L) {
	double dx, dy, a, b;
	double x2, y2;
	dx = L.getDirection().xd();
	dy = L.getDirection().yd();
	a = ((dx * dx) - (dy * dy));
	b = 2 * dx * dy;
	x2 = (a * (p.xd() - L.getOrigin().xd()))
		+ (b * (p.yd() - L.getOrigin().yd())) + L.getOrigin().xd();
	y2 = ((b * (p.xd() - L.getOrigin().xd())) - (a * (p.yd() - L
		.getOrigin().yd()))) + L.getOrigin().yd();
	return createPoint(x2, y2);
    }

    /**
     * Get point with triangle barycentric coordinates.
     *
     * @param u 
     * @param v 
     * @param w 
     * @param tri            triangle
     * @return point wit barycentric coordinates (u,v,w)
     */
    public WB_Point createPointFromBarycentricCoordinates(final double u,
	    final double v, final double w, final WB_Triangle tri) {
	return createPointFromTrilinearCoordinates(u / tri.a(), v / tri.b(), w
		/ tri.c(), tri);
    }

    /**
     * Inversion of 2D point p over circle C
     * http://mathworld.wolfram.com/Inversion.html
     *
     * @param p
     *            2D point
     * @param inversionCircle
     *            inversion circle
     *
     * @return Inversion of 2D point p over circle C, null if p coincides with
     *         inversion circle center
     */
    public WB_Point createInversionPoint(final WB_Coordinate p,
	    final WB_Circle inversionCircle) {
	final double r2 = inversionCircle.getRadius()
		* inversionCircle.getRadius();
	final double OP = inversionCircle.getCenter().getDistance3D(p);
	if (WB_Epsilon.isZero(OP)) {
	    return null;
	}
	final double OPp = r2 / OP;
	final WB_Vector v = createNormalizedVectorFromTo(
		inversionCircle.getCenter(), p);
	return createPoint(inversionCircle.getCenter().addMul(OPp, v));
    }

    /**
     * Point from cylindrical coordinates
     * http://en.wikipedia.org/wiki/Cylindrical_coordinate_system
     *
     * @param r
     *            radius
     * @param phi
     *            angle
     * @param z
     *            height
     * @return 3D point
     */
    public WB_Point createPointFromCylindrical(final double r,
	    final double phi, final double z) {
	return createPoint(r * Math.cos(phi), r * Math.sin(phi), z);
    }

    /**
     * Point from spherical coordinates
     * http://en.wikipedia.org/wiki/Spherical_coordinate_system
     *
     * @param r
     *            radius
     * @param theta
     *            inclination coordinate between -0.5*PI and 0.5*PI
     * @param phi
     *            azimuth coordinate between -PI and PI
     * @return 3D point
     */
    public WB_Point createPointFromSpherical(final double r,
	    final double theta, final double phi) {
	return createPoint(r * Math.cos(phi) * Math.sin(theta),
		r * Math.sin(phi) * Math.sin(theta), r * Math.cos(theta));
    }

    /**
     * Point from paraboloidal coordinates
     * http://en.wikipedia.org/wiki/Paraboloidal_coordinates
     *
     * @param sigma
     *            parabolic coordinate
     * @param tau
     *            parabolic coordinate
     * @param phi
     *            azimuth coordinate between -PI and PI
     * @return 3D point
     */
    public WB_Point createPointFromParaboloidal(final double sigma,
	    final double tau, final double phi) {
	return createPoint(sigma * tau * Math.cos(phi),
		sigma * tau * Math.sin(phi),
		0.5 * ((tau * tau) - (sigma * sigma)));
    }

    /**
     * Point from parabolic coordinates
     * http://en.wikipedia.org/wiki/Parabolic_cylindrical_coordinates
     *
     * @param sigma
     *            parabolic coordinate
     * @param tau
     *            parabolic coordinate
     * @param z
     *            height
     * @return 3D point
     */
    public WB_Point createPointFromParabolic(final double sigma,
	    final double tau, final double z) {
	return createPoint(sigma * tau, 0.5 * ((tau * tau) - (sigma * sigma)),
		z);
    }

    /**
     * Point from oblate spheroidal coordinates
     * http://en.wikipedia.org/wiki/Oblate_spheroidal_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            spheroidal coordinate >=0
     * @param nu
     *            spheroidal coordinate between -0.5*PI and 0.5*PI
     * @param phi
     *            azimuth coordinate between -PI and PI
     *
     * @return 3D point
     */
    public WB_Point createPointFromOblateSpheroidal(final double a,
	    final double mu, final double nu, final double phi) {
	final double common = a * Math.cosh(mu) * Math.cos(nu);
	return createPoint(common * Math.cos(phi), common * Math.sin(phi), a
		* Math.sinh(mu) * Math.sin(nu));
    }

    /**
     * Point from prolate spheroidal coordinates
     * http://en.wikipedia.org/wiki/Prolate_spheroidal_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            spheroidal coordinate >=0
     * @param nu
     *            spheroidal coordinate between -0.5*PI and 0.5*PI
     * @param phi
     *            azimuth coordinate between -PI and PI
     *
     * @return 3D point
     */
    public WB_Point createPointFromProlateSpheroidal(final double a,
	    final double mu, final double nu, final double phi) {
	final double common = a * Math.sinh(mu) * Math.sin(nu);
	return createPoint(common * Math.cos(phi), common * Math.sin(phi), a
		* Math.cosh(mu) * Math.cos(nu));
    }

    /**
     * Point from ellipsoidal coordinates
     * http://en.wikipedia.org/wiki/Ellipsoidal_coordinates
     * 
     * lambda<c�<mu<b�<nu<a�
     *
     * @param a            ,b,c focus
     * @param b 
     * @param c 
     * @param lambda            ellipsoidal coordinate
     * @param mu            ellipsoidal coordinate
     * @param nu            ellipsoidal coordinate
     * @return 3D point
     */
    public WB_Point createPointFromEllipsoidal(final double a, final double b,
	    final double c, final double lambda, final double mu,
	    final double nu) {
	final double a2 = a * a;
	final double b2 = b * b;
	final double c2 = c * c;
	return createPoint(
		Math.sqrt(((a2 - lambda) * (a2 - mu) * (a2 - nu)) / (a2 - b2)
			/ (a2 - c2)),
		Math.sqrt(((b2 - lambda) * (b2 - mu) * (b2 - nu)) / (b2 - a2)
			/ (b2 - c2)),
		Math.sqrt(((c2 - lambda) * (c2 - mu) * (c2 - nu)) / (c2 - a2)
			/ (c2 - b2)));
    }

    /**
     * Point from elliptic coordinates
     * http://en.wikipedia.org/wiki/Elliptic_cylindrical_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            elliptic coordinate >=0
     * @param nu
     *            elliptic coordinate between -PI and PI
     * @param z
     *            height
     *
     * @return 3D point
     */
    public WB_Point createPointFromElliptic(final double a, final double mu,
	    final double nu, final double z) {
	return createPoint(a * Math.cosh(mu) * Math.cos(nu), a * Math.sinh(mu)
		* Math.cos(nu), z);
    }

    /**
     * Point from toroidal coordinates
     * http://en.wikipedia.org/wiki/Toroidal_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            toroidal coordinate
     * @param tau
     *            toroidal coordinate
     * @param phi
     *            toroidal coordinate
     *
     * @return 3D point
     */
    public WB_Point createPointFromToroidal(final double a, final double sigma,
	    final double tau, final double phi) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createPoint(Math.sinh(tau) * invdenom * Math.cos(phi),
		Math.sinh(tau) * invdenom * Math.sin(phi), Math.sin(sigma)
			* invdenom);
    }

    /**
     * Point from bispherical coordinates
     * http://en.wikipedia.org/wiki/Bispherical_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            toroidal coordinate
     * @param tau
     *            toroidal coordinate
     * @param phi
     *            toroidal coordinate
     *
     * @return 3D point
     */
    public WB_Point createPointFromBispherical(final double a,
	    final double sigma, final double tau, final double phi) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createPoint(Math.sin(sigma) * invdenom * Math.cos(phi),
		Math.sin(sigma) * invdenom * Math.sin(phi), Math.sinh(tau)
			* invdenom);
    }

    /**
     * Point from bipolar cylindrical coordinates
     * http://en.wikipedia.org/wiki/Bipolar_cylindrical_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            toroidal coordinate
     * @param tau
     *            toroidal coordinate
     * @param z
     *            height
     *
     * @return 3D point
     */
    public WB_Point createPointFromBipolarCylindrical(final double a,
	    final double sigma, final double tau, final double z) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createPoint(Math.sinh(tau) * invdenom, Math.sin(sigma)
		* invdenom, z);
    }

    /**
     * Point from conical coordinates
     * http://en.wikipedia.org/wiki/Conical_coordinates
     * 
     * nu�<c�<mu�<b�
     *
     * @param b            ,c conical constants
     * @param c 
     * @param r            radius
     * @param mu            conical coordinate
     * @param nu            conical coordinate
     * @return 3D point
     */
    public WB_Point createPointFromConical(final double b, final double c,
	    final double r, final double mu, final double nu) {
	final double b2 = b * b;
	final double c2 = c * c;
	final double mu2 = mu * mu;
	final double nu2 = nu * nu;
	return createPoint((r * mu * nu) / b / c,
		(r / b) * Math.sqrt(((mu2 - b2) * (nu2 - b2)) / (b2 - c2)),
		(r / c) * Math.sqrt(((mu2 - c2) * (nu2 - c2)) / (c2 - b2)));
    }

    /**
     * Centroid of triangle.
     *
     * @param tri            triangle
     * @return centroid
     */
    public WB_Point createCentroid(final WB_Triangle tri) {
	return createPointFromTrilinearCoordinates(tri.b() * tri.c(), tri.c()
		* tri.a(), tri.a() * tri.b(), tri);
    }

    /**
     * Circumcenter of triangle.
     *
     * @param tri            triangle
     * @return circumcenter
     */
    public WB_Point createCircumcenter(final WB_Triangle tri) {
	return createPointFromTrilinearCoordinates(tri.cosA(), tri.cosB(),
		tri.cosC(), tri);
    }

    /**
     * Orthocenter of triangle.
     *
     * @param tri            triangle
     * @return orthocenter
     */
    public WB_Point createOrthocenter2D(final WB_Triangle tri) {
	final double a2 = tri.a() * tri.a();
	final double b2 = tri.b() * tri.b();
	final double c2 = tri.c() * tri.c();
	return createPointFromBarycentricCoordinates(((a2 + b2) - c2)
		* ((a2 - b2) + c2), ((a2 + b2) - c2) * (-a2 + b2 + c2),
		((a2 - b2) + c2) * (-a2 + b2 + c2), tri);
    }

    /**
     * Get point with triangle trilinear coordinates.
     *
     * @param u 
     * @param v 
     * @param w 
     * @param tri            triangle
     * @return point wit trilinear coordinates (u,v,w)
     */
    public WB_Point createPointFromTrilinearCoordinates(final double u,
	    final double v, final double w, final WB_Triangle tri) {
	final double invabc = 1.0 / ((tri.a() * u) + (tri.b() * v) + (tri.c() * w));
	final double bv = tri.b() * v;
	final double au = tri.a() * u;
	final double eax = ((((tri.p2().xd() - tri.p3().xd()) * bv) + ((tri
		.p1().xd() - tri.p3().xd()) * au)) * invabc) + tri.p3().xd();
	final double eay = ((((tri.p2().yd() - tri.p3().yd()) * bv) + ((tri
		.p1().yd() - tri.p3().yd()) * au)) * invabc) + tri.p3().yd();
	final double eaz = ((((tri.p2().zd() - tri.p3().zd()) * bv) + ((tri
		.p1().zd() - tri.p3().zd()) * au)) * invabc) + tri.p3().zd();
	return createPoint(eax, eay, eaz);
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public WB_Point createMidpoint(final WB_Coordinate p, final WB_Coordinate q) {
	return createPoint((p.xd() + q.xd()) * 0.5, (p.yd() + q.yd()) * 0.5,
		(p.zd() + q.zd()) * 0.5);
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Point createMidpoint(final WB_Coordinate... p) {
	final WB_Point m = createPoint();
	for (final WB_Coordinate point : p) {
	    m.addSelf(point);
	}
	m.divSelf(p.length);
	return m;
    }

    /**
     * New zero-length vector.
     *
     * @return zero-length vector
     */
    public WB_Vector createVector() {
	return createVector(0, 0, 0);
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public WB_Vector createVectorFromTo(final WB_Coordinate p,
	    final WB_Coordinate q) {
	return createVector(q.xd() - p.xd(), q.yd() - p.yd(), q.zd() - p.zd());
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public WB_Vector createVectorFromTo2D(final WB_Coordinate p,
	    final WB_Coordinate q) {
	return createVector(q.xd() - p.xd(), q.yd() - p.yd(), 0);
    }

    /**
     * Copy of coordinate as vector.
     *
     * @param p            vector
     * @return vector
     */
    public final WB_Vector createVector(final WB_Coordinate p) {
	return new WB_Vector(p);
    }

    /**
     * Copy of coordinate as vector, z-ordinate is ignored.
     *
     * @param p            vector
     * @return vector
     */
    public WB_Vector createVector2D(final WB_Coordinate p) {
	return createVector(p.xd(), p.yd(), 0);
    }

    /**
     * Vector from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Cartesian_coordinate_system
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @return 2D vector
     */
    public WB_Vector createVector(final double _x, final double _y) {
	return createVector(_x, _y, 0);
    }

    /**
     * Vector from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Elliptic_coordinates
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @param _z
     *            z
     * @return 3D vector
     */
    public WB_Vector createVector(final double _x, final double _y,
	    final double _z) {
	return new WB_Vector(_x, _y, _z);
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Vector createNormalizedVector(final WB_Coordinate p) {
	final WB_Vector vec = createVector(p);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public WB_Vector createNormalizedVectorFromTo(final WB_Coordinate p,
	    final WB_Coordinate q) {
	final WB_Vector vec = createVector(q.xd() - p.xd(), q.yd() - p.yd(),
		q.zd() - p.zd());
	vec.normalizeSelf();
	return vec;
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Vector createNormalizedVector2D(final WB_Coordinate p) {
	final WB_Vector vec = createVector2D(p);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     * @return 
     */
    public WB_Vector createNormalizedVectorFromTo2D(final WB_Coordinate p,
	    final WB_Coordinate q) {
	final WB_Vector vec = createVector(q.xd() - p.xd(), q.yd() - p.yd(), 0);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * Normalized vector from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Cartesian_coordinate_system
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @return 2D vector
     */
    public WB_Vector createNormalizedVector(final double _x, final double _y) {
	final WB_Vector vec = createVector(_x, _y, 0);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * Normalized vector from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Cartesian_coordinate_system
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @param _z
     *            z
     *
     * @return 3D vector
     */
    public WB_Vector createNormalizedVector(final double _x, final double _y,
	    final double _z) {
	final WB_Vector vec = createVector(_x, _y, _z);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * 
     *
     * @param _x 
     * @param _y 
     * @param _z 
     * @param _w 
     * @return 
     */
    public WB_Vector createNormalizedVector(final double _x, final double _y,
	    final double _z, final double _w) {
	final WB_Vector vec = createVector(_x, _y, _z);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * Normalized vector from Cartesian coordinates
     * http://en.wikipedia.org/wiki/Cartesian_coordinate_system
     *
     * @param _x
     *            x
     * @param _y
     *            y
     * @return 2D vector
     */
    public WB_Vector createNormalizedPerpendicularVector(final double _x,
	    final double _y) {
	final WB_Vector vec = createVector(-_y, _x, 0);
	vec.normalizeSelf();
	return vec;
    }

    /**
     * 
     *
     * @param _x 
     * @param _y 
     * @param _z 
     * @return 
     */
    public WB_Vector createNormalizedPerpendicularVector(final double _x,
	    final double _y, final double _z) {
	if (_x > _y) {
	    if (_y > _z) {
		return createNormalizedVector(-_y, _x, 0);
	    } else {
		return createNormalizedVector(-_z, 0, _x);
	    }
	} else {
	    if (_x > _z) {
		return createNormalizedVector(-_y, _x, 0);
	    } else {
		return createNormalizedVector(0, -_z, _x);
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Vector createNormalizedPerpendicularVector(final WB_Coordinate p) {
	if (p.xd() > p.yd()) {
	    if (p.yd() > p.zd()) {
		return createNormalizedVector(-p.yd(), p.xd(), 0);
	    } else {
		return createNormalizedVector(-p.zd(), 0, p.xd());
	    }
	} else {
	    if (p.xd() > p.zd()) {
		return createNormalizedVector(-p.yd(), p.xd(), 0);
	    } else {
		return createNormalizedVector(0, -p.zd(), p.xd());
	    }
	}
    }

    /**
     * Vector from polar coordinates
     * http://en.wikipedia.org/wiki/Polar_coordinate_system
     *
     * @param r
     *            radius
     * @param phi
     *            angle
     * @return 2D vector
     */
    public WB_Vector createVectorFromPolar(final double r, final double phi) {
	return createVector(r * Math.cos(phi), r * Math.sin(phi));
    }

    /**
     * Vector from bipolar coordinates
     * http://en.wikipedia.org/wiki/Bipolar_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            bipolar coordinate
     * @param tau
     *            bipolar coordinate
     * @return 2D vector
     */
    public WB_Vector createVectorFromBipolar(final double a,
	    final double sigma, final double tau) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createVector(Math.sinh(tau) * invdenom, Math.sin(sigma)
		* invdenom);
    }

    /**
     * Vector from parabolic coordinates
     * http://en.wikipedia.org/wiki/Parabolic_coordinates
     *
     * @param sigma
     *            parabolic coordinate
     * @param tau
     *            parabolic coordinate
     * @return 2D vector
     */
    public WB_Vector createVectorFromParabolic(final double sigma,
	    final double tau) {
	return createVector(sigma * tau, 0.5 * ((tau * tau) - (sigma * sigma)));
    }

    /**
     * Vector from hyperbolic coordinates
     * http://en.wikipedia.org/wiki/Hyperbolic_coordinates
     *
     * @param u
     *            hyperbolic angle
     * @param v
     *            geometric mean >0
     * @return 2D vector
     */
    public WB_Vector createVectorFromHyperbolic(final double u, final double v) {
	return createVector(v * Math.exp(u), v * Math.exp(-u));
    }

    /**
     * Vector from elliptic coordinates
     * http://en.wikipedia.org/wiki/Elliptic_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            elliptic coordinate >=0
     * @param nu
     *            elliptic coordinate between -PI and PI
     * @return 2D vector
     */
    public WB_Vector createVectorFromElliptic(final double a, final double mu,
	    final double nu) {
	return createVector(a * Math.cosh(mu) * Math.cos(nu), a * Math.sinh(mu)
		* Math.cos(nu));
    }

    /**
     * Vector from cylindrical coordinates
     * http://en.wikipedia.org/wiki/Cylindrical_coordinate_system
     *
     * @param r
     *            radius
     * @param phi
     *            angle
     * @param z
     *            height
     * @return 3D vector
     */
    public WB_Vector createVectorFromCylindrical(final double r,
	    final double phi, final double z) {
	return createVector(r * Math.cos(phi), r * Math.sin(phi), z);
    }

    /**
     * Vector from spherical coordinates
     * http://en.wikipedia.org/wiki/Spherical_coordinate_system
     *
     * @param r
     *            radius
     * @param theta
     *            inclination coordinate between -0.5*PI and 0.5*PI
     * @param phi
     *            azimuth coordinate between -PI and PI
     * @return 3D vector
     */
    public WB_Vector createVectorFromSpherical(final double r,
	    final double theta, final double phi) {
	return createVector(r * Math.cos(phi) * Math.sin(theta),
		r * Math.sin(phi) * Math.sin(theta), r * Math.cos(theta));
    }

    /**
     * Vector from paraboloidal coordinates
     * http://en.wikipedia.org/wiki/Paraboloidal_coordinates
     *
     * @param sigma
     *            parabolic coordinate
     * @param tau
     *            parabolic coordinate
     * @param phi
     *            azimuth coordinate between -PI and PI
     * @return 3D vector
     */
    public WB_Vector createVectorFromParaboloidal(final double sigma,
	    final double tau, final double phi) {
	return createVector(sigma * tau * Math.cos(phi),
		sigma * tau * Math.sin(phi),
		0.5 * ((tau * tau) - (sigma * sigma)));
    }

    /**
     * Vector from parabolic coordinates
     * http://en.wikipedia.org/wiki/Parabolic_cylindrical_coordinates
     *
     * @param sigma
     *            parabolic coordinate
     * @param tau
     *            parabolic coordinate
     * @param z
     *            height
     * @return 3D vector
     */
    public WB_Vector createVectorFromParabolic(final double sigma,
	    final double tau, final double z) {
	return createVector(sigma * tau, 0.5 * ((tau * tau) - (sigma * sigma)),
		z);
    }

    /**
     * Vector from oblate spheroidal coordinates
     * http://en.wikipedia.org/wiki/Oblate_spheroidal_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            spheroidal coordinate >=0
     * @param nu
     *            spheroidal coordinate between -0.5*PI and 0.5*PI
     * @param phi
     *            azimuth coordinate between -PI and PI
     *
     * @return 3D vector
     */
    public WB_Vector createVectorFromOblateSpheroidal(final double a,
	    final double mu, final double nu, final double phi) {
	final double common = a * Math.cosh(mu) * Math.cos(nu);
	return createVector(common * Math.cos(phi), common * Math.sin(phi), a
		* Math.sinh(mu) * Math.sin(nu));
    }

    /**
     * Vector from prolate spheroidal coordinates
     * http://en.wikipedia.org/wiki/Prolate_spheroidal_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            spheroidal coordinate >=0
     * @param nu
     *            spheroidal coordinate between -0.5*PI and 0.5*PI
     * @param phi
     *            azimuth coordinate between -PI and PI
     *
     * @return 3D vector
     */
    public WB_Vector createVectorFromProlateSpheroidal(final double a,
	    final double mu, final double nu, final double phi) {
	final double common = a * Math.sinh(mu) * Math.sin(nu);
	return createVector(common * Math.cos(phi), common * Math.sin(phi), a
		* Math.cosh(mu) * Math.cos(nu));
    }

    /**
     * Vector from ellipsoidal coordinates
     * http://en.wikipedia.org/wiki/Ellipsoidal_coordinates
     * 
     * lambda<c�<mu<b�<nu<a�
     *
     * @param a            ,b,c focus
     * @param b 
     * @param c 
     * @param lambda            ellipsoidal coordinate
     * @param mu            ellipsoidal coordinate
     * @param nu            ellipsoidal coordinate
     * @return 3D vector
     */
    public WB_Vector createVectorFromEllipsoidal(final double a,
	    final double b, final double c, final double lambda,
	    final double mu, final double nu) {
	final double a2 = a * a;
	final double b2 = b * b;
	final double c2 = c * c;
	return createVector(
		Math.sqrt(((a2 - lambda) * (a2 - mu) * (a2 - nu)) / (a2 - b2)
			/ (a2 - c2)),
		Math.sqrt(((b2 - lambda) * (b2 - mu) * (b2 - nu)) / (b2 - a2)
			/ (b2 - c2)),
		Math.sqrt(((c2 - lambda) * (c2 - mu) * (c2 - nu)) / (c2 - a2)
			/ (c2 - b2)));
    }

    /**
     * Vector from elliptic coordinates
     * http://en.wikipedia.org/wiki/Elliptic_cylindrical_coordinates
     *
     * @param a
     *            focus
     * @param mu
     *            elliptic coordinate >=0
     * @param nu
     *            elliptic coordinate between -PI and PI
     * @param z
     *            height
     *
     * @return 3D vector
     */
    public WB_Vector createVectorFromElliptic(final double a, final double mu,
	    final double nu, final double z) {
	return createVector(a * Math.cosh(mu) * Math.cos(nu), a * Math.sinh(mu)
		* Math.cos(nu), z);
    }

    /**
     * Vector from toroidal coordinates
     * http://en.wikipedia.org/wiki/Toroidal_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            toroidal coordinate
     * @param tau
     *            toroidal coordinate
     * @param phi
     *            toroidal coordinate
     *
     * @return 3D vector
     */
    public WB_Vector createVectorFromToroidal(final double a,
	    final double sigma, final double tau, final double phi) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createVector(Math.sinh(tau) * invdenom * Math.cos(phi),
		Math.sinh(tau) * invdenom * Math.sin(phi), Math.sin(sigma)
			* invdenom);
    }

    /**
     * Vector from bispherical coordinates
     * http://en.wikipedia.org/wiki/Bispherical_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            toroidal coordinate
     * @param tau
     *            toroidal coordinate
     * @param phi
     *            toroidal coordinate
     *
     * @return 3D vector
     */
    public WB_Vector createVectorFromBispherical(final double a,
	    final double sigma, final double tau, final double phi) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createVector(Math.sin(sigma) * invdenom * Math.cos(phi),
		Math.sin(sigma) * invdenom * Math.sin(phi), Math.sinh(tau)
			* invdenom);
    }

    /**
     * Vector from bipolar cylindrical coordinates
     * http://en.wikipedia.org/wiki/Bipolar_cylindrical_coordinates
     *
     * @param a
     *            focus
     * @param sigma
     *            toroidal coordinate
     * @param tau
     *            toroidal coordinate
     * @param z
     *            height
     *
     * @return 3D vector
     */
    public WB_Vector createVectorFromBipolarCylindrical(final double a,
	    final double sigma, final double tau, final double z) {
	double invdenom = (Math.cosh(tau) - Math.cos(sigma));
	invdenom = WB_Epsilon.isZero(invdenom) ? 0.0 : a / invdenom;
	return createVector(Math.sinh(tau) * invdenom, Math.sin(sigma)
		* invdenom, z);
    }

    /**
     * Vector from conical coordinates
     * http://en.wikipedia.org/wiki/Conical_coordinates
     * 
     * nu�<c�<mu�<b�
     *
     * @param b            ,c conical constants
     * @param c 
     * @param r            radius
     * @param mu            conical coordinate
     * @param nu            conical coordinate
     * @return 3D vector
     */
    public WB_Vector createVectorFromConical(final double b, final double c,
	    final double r, final double mu, final double nu) {
	final double b2 = b * b;
	final double c2 = c * c;
	final double mu2 = mu * mu;
	final double nu2 = nu * nu;
	return createVector((r * mu * nu) / b / c,
		(r / b) * Math.sqrt(((mu2 - b2) * (nu2 - b2)) / (b2 - c2)),
		(r / c) * Math.sqrt(((mu2 - c2) * (nu2 - c2)) / (c2 - b2)));
    }

    /**
     * Get line through two points. The first point will become the origin
     *
     * @param p1
     *            point 1
     * @param p2
     *            point 2
     * @return line through points
     */
    public WB_Line createLineThroughPoints(final WB_Coordinate p1,
	    final WB_Coordinate p2) {
	return createLineWithDirection(p1, createVectorFromTo(p1, p2));
    }

    /**
     * Get line through two points. The first point will become the origin
     *
     * @param x1
     *            x-ordinate of point 1
     * @param y1
     *            y-ordinate of point 1
     * @param x2
     *            x-ordinate of point 2
     * @param y2
     *            y-ordinate of point 2
     * @return line through points
     */
    public WB_Line createLineThroughPoints(final double x1, final double y1,
	    final double x2, final double y2) {
	return createLineWithDirection(createPoint(x1, y1),
		createVector(x2 - x1, y2 - y1));
    }

    // 3D
    /**
     * Get line through two points. The first point will become the origin
     *
     * @param x1            x-ordinate of point 1
     * @param y1            y-ordinate of point 1 * @param z1 z-ordinate of point 1
     * @param z1 
     * @param x2            x-ordinate of point 2
     * @param y2            y-ordinate of point 2
     * @param z2            z-ordinate of point 2
     * @return line through points
     */
    public WB_Line createLineThroughPoints(final double x1, final double y1,
	    final double z1, final double x2, final double y2, final double z2) {
	return createLineWithDirection(createPoint(x1, y1, z1),
		createVector(x2 - x1, y2 - y1, z2 - z1));
    }

    /**
     * Get line through point with given direction.
     *
     * @param origin
     *            point on line
     * @param direction
     *            direction
     * @return line through point with direction
     */
    public WB_Line createLineWithDirection(final WB_Coordinate origin,
	    final WB_Coordinate direction) {
	return new WB_Line(origin, direction);
    }

    // 3D
    /**
     * Get 2D line through point with given direction.
     *
     * @param ox
     *            x-ordinate of origin
     * @param oy
     *            y-ordinate of origin
     * @param dx
     *            x-ordinate of direction
     * @param dy
     *            y-ordinate of direction
     * @return 2D line through point with given direction
     */
    public WB_Line createLineWithDirection(final double ox, final double oy,
	    final double dx, final double dy) {
	return createLineWithDirection(createPoint(ox, oy),
		createVector(dx, dy));
    }

    /**
     * Get 3D line through point with given direction.
     *
     * @param ox
     *            x-ordinate of origin
     * @param oy
     *            y-ordinate of origin
     * @param oz
     *            z-ordinate of origin
     * @param dx
     *            x-ordinate of direction
     * @param dy
     *            y-ordinate of direction
     * @param dz
     *            z-ordinate of direction
     * @return 3D line through point with given direction
     */
    public WB_Line createLineWithDirection(final double ox, final double oy,
	    final double oz, final double dx, final double dy, final double dz) {
	return createLineWithDirection(createPoint(ox, oy, oz),
		createVector(dx, dy, dz));
    }

    /**
     * Get a line parallel to a line and through point.
     *
     * @param L
     *            line
     * @param p
     *            point
     * @return parallel line through point
     */
    public WB_Line createParallelLineThroughPoint(final WB_Linear L,
	    final WB_Coordinate p) {
	return createLineWithDirection(p, L.getDirection());
    }

    /**
     * Get a 2D line perpendicular to 2D line and through 2D point.
     *
     * @param L
     *            2D line
     * @param p
     *            2D point
     * @return perpendicular 2D line through point
     */
    public WB_Line createPerpendicularLineThroughPoint2D(final WB_Line L,
	    final WB_Coordinate p) {
	return createLineWithDirection(p,
		createVector(-L.getDirection().yd(), L.getDirection().xd()));
    }

    /**
     * Get the two 2D lines parallel to a 2D line and separated by a distance d.
     *
     * @param L
     *            2D line
     * @param d
     *            distance
     * @return two parallel 2D lines
     */
    public List<WB_Line> createParallelLines2D(final WB_Line L, final double d) {
	final List<WB_Line> result = new ArrayList<WB_Line>(2);
	result.add(createLineWithDirection(
		createPoint(L.getOrigin().xd() - (d * L.getDirection().yd()), L
			.getOrigin().yd() + (d * L.getDirection().xd())),
		L.getDirection()));
	result.add(createLineWithDirection(
		createPoint(L.getOrigin().xd() + (d * L.getDirection().yd()), L
			.getOrigin().yd() - (d * L.getDirection().xd())),
		L.getDirection()));
	return result;
    }

    /**
     * Get the 2D bisector of two 2D points. The points should be distinct.
     *
     * @param p
     *            2D point
     * @param q
     *            2D point
     * @return 2D bisector
     */
    public WB_Line createBisector2D(final WB_Coordinate p, final WB_Coordinate q) {
	return createLineWithDirection(createPoint(p)
		.mulAddMulSelf(0.5, 0.5, q),
		createVector(p.yd() - q.yd(), q.xd() - p.xd()));
    }

    /**
     * Get the 2D angle bisectors of two 2D lines.
     *
     * @param L1
     *            2D line
     * @param L2
     *            2D line
     * @return 2D angle bisector
     */
    public List<WB_Line> createAngleBisector2D(final WB_Line L1,
	    final WB_Line L2) {
	final WB_Point intersection = createIntersectionPoint2D(L1, L2);
	final List<WB_Line> result = new ArrayList<WB_Line>(2);
	if (intersection == null) {
	    final WB_Point L1onL2 = createClosestPointOnLine2D(L1.getOrigin(),
		    L2);
	    result.add(createLineWithDirection(
		    L1onL2.mulAddMul(0.5, 0.5, L1.getOrigin()),
		    L1.getDirection()));
	    return result;
	} else {
	    if (L1.getDirection().dot(L2.getDirection()) > 0) {
		final WB_Point p1 = intersection.addMul(100, L1.getDirection());
		final WB_Point p2 = intersection.addMul(100, L2.getDirection());
		final WB_Vector dir = createVector2D(p1.mulAddMul(0.5, 0.5, p2)
			.sub(intersection));
		result.add(createLineWithDirection(intersection, dir));
		result.add(createLineWithDirection(intersection.xd(),
			intersection.yd(), -dir.yd(), dir.xd()));
		return result;
	    } else {
		final WB_Point p1 = intersection.addMul(100, L1.getDirection());
		final WB_Point p2 = intersection
			.addMul(-100, L2.getDirection());
		final WB_Vector dir = createVector2D(p1.mulAddMul(0.5, 0.5, p2)
			.sub(intersection));
		result.add(createLineWithDirection(intersection, dir));
		result.add(createLineWithDirection(intersection.xd(),
			intersection.yd(), -dir.yd(), dir.xd()));
		return result;
	    }
	}
    }

    /**
     * Get the 2D line tangent to a circle at a 2D point.
     *
     * @param C
     *            circle
     * @param p
     *            point
     * @return 2D line tangent to circle at point
     */
    public WB_Line createLineTangentToCircleInPoint(final WB_Circle C,
	    final WB_Coordinate p) {
	final WB_Vector v = createVector2D(p).sub(C.getCenter());
	return createLineWithDirection(p, createVector(-v.yd(), v.xd()));
    }

    /**
     * Gets the 2D lines tangent to a circle through 2D point.
     *
     * @param C
     *            circle
     * @param p
     *            point
     * @return 2D lines tangent to circle through point
     */
    public List<WB_Line> createLinesTangentToCircleThroughPoint(
	    final WB_Circle C, final WB_Coordinate p) {
	final List<WB_Line> result = new ArrayList<WB_Line>(2);
	final double dcp = C.getCenter().getDistance3D(p);
	final WB_Vector u = createVector2D(p).sub(C.getCenter());
	if (WB_Epsilon.isZero(dcp - C.getRadius())) {
	    result.add(createLineWithDirection(p, createVector(-u.yd(), u.xd())));
	} else if (dcp < C.getRadius()) {
	    return result;
	} else if (!WB_Epsilon.isZero(u.xd())) {
	    final double ux2 = u.xd() * u.xd();
	    final double ux4 = ux2 * ux2;
	    final double uy2 = u.yd() * u.yd();
	    final double r2 = C.getRadius() * C.getRadius();
	    final double r4 = r2 * r2;
	    final double num = r2 * uy2;
	    final double denom = ux2 + uy2;
	    final double rad = Math.sqrt((-r4 * ux2) + (r2 * ux4)
		    + (r2 * ux2 * uy2));
	    result.add(createLineWithDirection(
		    p,
		    createVector(-((r2 * u.yd()) + rad) / denom,
			    (r2 - ((num + (u.yd() * rad)) / denom)) / u.xd())));
	    result.add(createLineWithDirection(
		    p,
		    createVector(-((r2 * u.yd()) - rad) / denom,
			    (r2 - ((num - (u.yd() * rad)) / denom)) / u.xd())));
	} else {
	    final double ux2 = u.yd() * u.yd();
	    final double ux4 = ux2 * ux2;
	    final double uy2 = u.xd() * u.xd();
	    final double r2 = C.getRadius() * C.getRadius();
	    final double r4 = r2 * r2;
	    final double num = r2 * uy2;
	    final double denom = ux2 + uy2;
	    final double rad = Math.sqrt((-r4 * ux2) + (r2 * ux4)
		    + (r2 * ux2 * uy2));
	    result.add(createLineWithDirection(
		    p,
		    createVector(
			    (r2 - ((num + (u.xd() * rad)) / denom)) / u.yd(),
			    -((r2 * u.xd()) + rad) / denom)));
	    result.add(createLineWithDirection(
		    p,
		    createVector(
			    (r2 - ((num - (u.xd() * rad)) / denom)) / u.yd(),
			    -((r2 * u.xd()) - rad) / denom)));
	}
	return result;
    }

    /**
     * Gets the 2D lines tangent to 2 circles.
     *
     * @param C0
     *            circle
     * @param C1
     *            circle
     * @return the 2D lines tangent to the 2 circles
     */
    public List<WB_Line> createLinesTangentTo2Circles(final WB_Circle C0,
	    final WB_Circle C1) {
	final List<WB_Line> result = new ArrayList<WB_Line>(4);
	final WB_Vector w = createVector2D(C1.getCenter()).sub(C0.getCenter());
	final double wlensqr = w.getSqLength3D();
	final double rsum = C0.getRadius() + C1.getRadius();
	final double rdiff = C1.getRadius() - C0.getRadius();
	if (wlensqr < (rdiff * rdiff)) {
	    return result;
	}
	boolean inside = false;
	if (wlensqr <= (rsum * rsum)) {
	    inside = true;
	}
	if (!WB_Epsilon.isZero(rdiff)) {
	    final double r0sqr = C0.getRadius() * C0.getRadius();
	    final double r1sqr = C1.getRadius() * C1.getRadius();
	    final double c0 = -r0sqr;
	    final double c1 = 2 * r0sqr;
	    final double c2 = (C1.getRadius() * C1.getRadius()) - r0sqr;
	    final double invc2 = 1.0 / c2;
	    final double discr = Math.sqrt(WB_Math.fastAbs((c1 * c1)
		    - (4 * c0 * c2)));
	    double s, oms, a;
	    s = -0.5 * (c1 + discr) * invc2;
	    if (s >= 0.5) {
		a = Math.sqrt(WB_Math.fastAbs(wlensqr - (r0sqr / (s * s))));
	    } else {
		oms = 1.0 - s;
		a = Math.sqrt(WB_Math.fastAbs(wlensqr - (r1sqr / (oms * oms))));
	    }
	    List<WB_Vector> dir = getDirections(w, a);
	    WB_Point org = createPoint(C0.getCenter().xd() + (s * w.xd()), C0
		    .getCenter().yd() + (s * w.yd()));
	    result.add(createLineWithDirection(org, dir.get(0)));
	    result.add(createLineWithDirection(org, dir.get(1)));
	    s = -0.5 * (c1 - discr) * invc2;
	    if (s >= 0.5) {
		a = Math.sqrt(WB_Math.fastAbs(wlensqr - (r0sqr / (s * s))));
	    } else {
		oms = 1.0 - s;
		a = Math.sqrt(WB_Math.fastAbs(wlensqr - (r1sqr / (oms * oms))));
	    }
	    dir = getDirections(w, a);
	    org = createPoint(C0.getCenter().xd() + (s * w.xd()), C0
		    .getCenter().yd() + (s * w.yd()));
	    if (!inside) {
		result.add(createLineWithDirection(org, dir.get(0)));
	    }
	    if (!inside) {
		result.add(createLineWithDirection(org, dir.get(1)));
	    }
	} else {
	    final WB_Point mid = createPoint2D(C0.getCenter()).mulAddMul(0.5,
		    0.5, C1.getCenter());
	    final double a = Math.sqrt(WB_Math.fastAbs(wlensqr
		    - (4 * C0.getRadius() * C0.getRadius())));
	    final double invwlen = 1.0 / Math.sqrt(wlensqr);
	    result.add(createLineWithDirection(
		    createPoint(mid.xd() + (C0.getRadius() * w.yd() * invwlen),
			    mid.yd() - (C0.getRadius() * w.xd() * invwlen)), w));
	    result.add(createLineWithDirection(
		    createPoint(mid.xd() - (C0.getRadius() * w.yd() * invwlen),
			    mid.yd() + (C0.getRadius() * w.xd() * invwlen)), w));
	    final List<WB_Vector> dir = getDirections(w, a);
	    if (!inside) {
		result.add(createLineWithDirection(mid, dir.get(0)));
	    }
	    if (!inside) {
		result.add(createLineWithDirection(mid, dir.get(1)));
	    }
	}
	if (WB_Epsilon.isZeroSq(wlensqr - (rsum * rsum))) {
	    final WB_Point org = createInterpolatedPoint(C0.getCenter(),
		    C1.getCenter(),
		    C0.getRadius() / (C0.getRadius() + C1.getRadius()));
	    final WB_Vector dir = createNormalizedVector(C1.getCenter().xd()
		    - C0.getCenter().xd(), C1.getCenter().yd()
		    - C0.getCenter().yd());
	    result.add(createLineWithDirection(org,
		    createVector(-dir.yd(), dir.xd())));
	}
	if (WB_Epsilon.isZeroSq(wlensqr - (rdiff * rdiff))) {
	    final WB_Point org = createInterpolatedPoint(C0.getCenter(),
		    C1.getCenter(),
		    C0.getRadius() / (C0.getRadius() - C1.getRadius()));
	    final WB_Vector dir = createNormalizedVector(C1.getCenter().xd()
		    - C0.getCenter().xd(), C1.getCenter().yd()
		    - C0.getCenter().yd());
	    result.add(createLineWithDirection(org,
		    createVector(-dir.yd(), dir.xd())));
	}
	return result;
    }

    /**
     * 
     *
     * @param w 
     * @param a 
     * @return 
     */
    private List<WB_Vector> getDirections(final WB_Coordinate w, final double a) {
	final List<WB_Vector> dir = new ArrayList<WB_Vector>(2);
	final double asqr = a * a;
	final double wxsqr = w.xd() * w.xd();
	final double wysqr = w.yd() * w.yd();
	final double c2 = wxsqr + wysqr;
	final double invc2 = 1.0 / c2;
	double c0, c1, discr, invwx;
	final double invwy;
	if (WB_Math.fastAbs(w.xd()) >= WB_Math.fastAbs(w.yd())) {
	    c0 = asqr - wxsqr;
	    c1 = -2 * a * w.yd();
	    discr = Math.sqrt(WB_Math.fastAbs((c1 * c1) - (4 * c0 * c2)));
	    invwx = 1.0 / w.xd();
	    final double dir0y = -0.5 * (c1 + discr) * invc2;
	    dir.add(createVector((a - (w.yd() * dir0y)) * invwx, dir0y));
	    final double dir1y = -0.5 * (c1 - discr) * invc2;
	    dir.add(createVector((a - (w.yd() * dir1y)) * invwx, dir1y));
	} else {
	    c0 = asqr - wysqr;
	    c1 = -2 * a * w.xd();
	    discr = Math.sqrt(WB_Math.fastAbs((c1 * c1) - (4 * c0 * c2)));
	    invwy = 1.0 / w.yd();
	    final double dir0x = -0.5 * (c1 + discr) * invc2;
	    dir.add(createVector(dir0x, (a - (w.xd() * dir0x)) * invwy));
	    final double dir1x = -0.5 * (c1 - discr) * invc2;
	    dir.add(createVector(dir1x, (a - (w.xd() * dir1x)) * invwy));
	}
	return dir;
    }

    /**
     * Gets the two 2D lines perpendicular to a 2D line and tangent to a circle.
     *
     * @param L
     *            2D line
     * @param C
     *            circle
     * @return 2D lines perpendicular to line and tangent to circle
     */
    public List<WB_Line> createPerpendicularLinesTangentToCircle(
	    final WB_Line L, final WB_Circle C) {
	final List<WB_Line> result = new ArrayList<WB_Line>(2);
	result.add(createLineWithDirection(
		createPoint(C.getCenter().xd()
			+ (C.getRadius() * L.getDirection().xd()), C
			.getCenter().yd()
			+ (C.getRadius() * L.getDirection().yd())),
		createVector(-L.getDirection().yd(), L.getDirection().xd())));
	result.add(createLineWithDirection(
		createPoint(C.getCenter().xd()
			- (C.getRadius() * L.getDirection().xd()), C
			.getCenter().yd()
			- (C.getRadius() * L.getDirection().yd())),
		createVector(-L.getDirection().yd(), L.getDirection().xd())));
	return result;
    }

    /**
     * Get ray through two points. The first point will become the origin
     *
     * @param p1
     *            point 1
     * @param p2
     *            point 2
     * @return ray through points
     */
    public WB_Ray createRayThroughPoints(final WB_Coordinate p1,
	    final WB_Coordinate p2) {
	return createRayWithDirection(p1, createVector(p2).subSelf(p1));
    }

    /**
     * Get ray through two points. The first point will become the origin
     *
     * @param x1
     *            x-ordinate of point 1
     * @param y1
     *            y-ordinate of point 1
     * @param x2
     *            x-ordinate of point 2
     * @param y2
     *            y-ordinate of point 2
     * @return ray through points
     */
    public WB_Ray createRayThroughPoints(final double x1, final double y1,
	    final double x2, final double y2) {
	return createRayWithDirection(createPoint(x1, y1),
		createVector(x2 - x1, y2 - y1));
    }

    // 3D
    /**
     * Get ray through two points. The first point will become the origin
     *
     * @param x1            x-ordinate of point 1
     * @param y1            y-ordinate of point 1 * @param z1 z-ordinate of point 1
     * @param z1 
     * @param x2            x-ordinate of point 2
     * @param y2            y-ordinate of point 2
     * @param z2            z-ordinate of point 2
     * @return ray through points
     */
    public WB_Ray createRayThroughPoints(final double x1, final double y1,
	    final double z1, final double x2, final double y2, final double z2) {
	return createRayWithDirection(createPoint(x1, y1, z1),
		createVector(x2 - x1, y2 - y1, z2 - z1));
    }

    /**
     * Get ray through point with given direction.
     *
     * @param origin
     *            point on line
     * @param direction
     *            direction
     * @return ray through point with direction
     */
    public WB_Ray createRayWithDirection(final WB_Coordinate origin,
	    final WB_Coordinate direction) {
	return new WB_Ray(origin, direction);
    }

    /**
     * Get 2D ray through point with given direction.
     *
     * @param ox
     *            x-ordinate of origin
     * @param oy
     *            y-ordinate of origin
     * @param dx
     *            x-ordinate of direction
     * @param dy
     *            y-ordinate of direction
     * @return 2D ray through point with given direction
     */
    public WB_Ray createRayWithDirection(final double ox, final double oy,
	    final double dx, final double dy) {
	return createRayWithDirection(createPoint(ox, oy), createVector(dx, dy));
    }

    // 3D
    /**
     * Get 3D ray through point with given direction.
     *
     * @param ox
     *            x-ordinate of origin
     * @param oy
     *            y-ordinate of origin
     * @param oz
     *            z-ordinate of origin
     * @param dx
     *            x-ordinate of direction
     * @param dy
     *            y-ordinate of direction
     * @param dz
     *            z-ordinate of direction
     * @return 3D ray through point with given direction
     */
    public WB_Ray createRayWithDirection(final double ox, final double oy,
	    final double oz, final double dx, final double dy, final double dz) {
	return createRayWithDirection(createPoint(ox, oy, oz),
		createVector(dx, dy, dz));
    }

    /**
     * Get a ray parallel to a line and through point.
     *
     * @param L
     *            line
     * @param p
     *            point
     * @return parallel line through point
     */
    public WB_Ray createParallelRayThroughPoint(final WB_Linear L,
	    final WB_Coordinate p) {
	return createRayWithDirection(p, L.getDirection());
    }

    /**
     * Get segment between two points. The first point will become the origin
     *
     * @param p1
     *            point 1
     * @param p2
     *            point 2
     * @return segment
     */
    public WB_Segment createSegment(final WB_Coordinate p1,
	    final WB_Coordinate p2) {
	return new WB_Segment(p1, p2);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_PolyLine createPolyLine(final WB_Coordinate[] points) {
	return new WB_PolyLine(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_PolyLine createPolyLine(
	    final Collection<? extends WB_Coordinate> points) {
	return new WB_PolyLine(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_PolyLine createPolyLine(final WB_CoordinateSequence points) {
	return new WB_PolyLine(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_Ring createRing(final WB_Coordinate[] points) {
	return new WB_Ring(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_Ring createRing(final List<? extends WB_Coordinate> points) {
	return new WB_Ring(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_Ring createRing(final WB_CoordinateSequence points) {
	return new WB_Ring(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_Polygon createSimplePolygon(final WB_Coordinate... points) {
	return new WB_Polygon(points);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_Polygon createSimplePolygon(
	    final Collection<? extends WB_Coordinate> points) {
	return new WB_Polygon(points);
    }

    /**
     * 
     *
     * @param tuples 
     * @param indices 
     * @return 
     */
    public WB_Polygon createSimplePolygon(
	    final List<? extends WB_Coordinate> tuples, final int[] indices) {
	final List<WB_Coordinate> coords = new FastTable<WB_Coordinate>();
	for (final int indice : indices) {
	    coords.add(tuples.get(indice));
	}
	return createSimplePolygon(coords);
    }

    /**
     * 
     *
     * @param coords 
     * @return 
     */
    public WB_Polygon createSimplePolygon(final WB_CoordinateSequence coords) {
	return new WB_Polygon(coords);
    }

    /**
     * 
     *
     * @param points 
     * @param innerpoints 
     * @return 
     */
    public WB_Polygon createPolygonWithHole(final WB_Coordinate[] points,
	    final WB_Coordinate[] innerpoints) {
	return new WB_Polygon(points, innerpoints);
    }

    /**
     * 
     *
     * @param points 
     * @param innerpoints 
     * @return 
     */
    public WB_Polygon createPolygonWithHole(
	    final Collection<? extends WB_Coordinate> points,
	    final Collection<? extends WB_Coordinate> innerpoints) {
	return new WB_Polygon(points, innerpoints);
    }

    /**
     * 
     *
     * @param points 
     * @param innerpoints 
     * @return 
     */
    public WB_Polygon createPolygonWithHoles(final WB_Coordinate[] points,
	    final WB_Coordinate[][] innerpoints) {
	return new WB_Polygon(points, innerpoints);
    }

    /**
     * 
     *
     * @param points 
     * @param innerpoints 
     * @return 
     */
    public WB_Polygon createPolygonWithHoles(
	    final Collection<? extends WB_Coordinate> points,
	    final List<? extends WB_Coordinate>[] innerpoints) {
	return new WB_Polygon(points, innerpoints);
    }

    /**
     * 
     *
     * @param JTSpoly 
     * @return 
     */
    public WB_Polygon createPolygonFromJTSPolygon(final Polygon JTSpoly) {
	final LineString shell = JTSpoly.getExteriorRing();
	Coordinate[] coords = shell.getCoordinates();
	final WB_Coordinate[] points = new WB_Coordinate[coords.length - 1];
	for (int i = 0; i < (coords.length - 1); i++) {
	    points[i] = createPoint(coords[i]);
	}
	final int numholes = JTSpoly.getNumInteriorRing();
	if (numholes > 0) {
	    final WB_Coordinate[][] holecoords = new WB_Coordinate[numholes][];
	    for (int i = 0; i < numholes; i++) {
		final LineString hole = JTSpoly.getInteriorRingN(i);
		coords = hole.getCoordinates();
		holecoords[i] = new WB_Coordinate[coords.length - 1];
		for (int j = 0; j < (coords.length - 1); j++) {
		    holecoords[i][j] = createPoint(coords[j]);
		}
	    }
	    return createPolygonWithHoles(points, holecoords);
	} else {
	    return createSimplePolygon(points);
	}
    }

    /**
     * 
     *
     * @param geometry 
     * @return 
     */
    private List<WB_Polygon> createPolygonsFromJTSGeometry(
	    final Geometry geometry) {
	final List<WB_Polygon> polygons = new FastTable<WB_Polygon>();
	for (int i = 0; i < geometry.getNumGeometries(); i++) {
	    final Geometry geo = geometry.getGeometryN(i);
	    if (!geo.isEmpty()) {
		if (geo.getGeometryType().equals("Polygon")) {
		    polygons.add(createPolygonFromJTSPolygon((Polygon) geo));
		} else if (geo.getGeometryType().equals("MultiPolygon")) {
		    for (int j = 0; j < geo.getNumGeometries(); j++) {
			final Geometry ggeo = geo.getGeometryN(j);
			polygons.add(createPolygonFromJTSPolygon((Polygon) ggeo));
		    }
		} else if (geo.getGeometryType().equals("GeometryCollection")) {
		    for (int j = 0; j < geo.getNumGeometries(); j++) {
			final Geometry ggeo = geo.getGeometryN(j);
			polygons.addAll(createPolygonsFromJTSGeometry(ggeo));
		    }
		}
	    }
	}
	return polygons;
    }

    /**
     * Get segment between two points. The first point will become the origin
     *
     * @param origin
     *            origin
     * @param direction
     *            direction
     * @param length
     *            length
     * @return segment
     */
    public WB_Segment createSegmentWithLength(final WB_Coordinate origin,
	    final WB_Coordinate direction, final double length) {
	return createSegment(
		origin,
		createPoint(origin).addMulSelf(length,
			createNormalizedVector(direction)));
    }

    /**
     * Get segment. The first point will become the origin
     *
     * @param x1
     *            x-ordinate of point 1
     * @param y1
     *            y-ordinate of point 1
     * @param x2
     *            x-ordinate of point 2
     * @param y2
     *            y-ordinate of point 2
     * @return line through points
     */
    public WB_Segment createSegment(final double x1, final double y1,
	    final double x2, final double y2) {
	return createSegment(createPoint(x1, y1), createVector(x2, y2));
    }

    /**
     * Get segment from point, direction and length.
     *
     * @param ox
     *            x-ordinate of origin
     * @param oy
     *            y-ordinate of origin
     * @param dx
     *            x-ordinate of direction
     * @param dy
     *            y-ordinate of direction
     * @param length
     *            length
     * @return segment
     */
    public WB_Segment createSegmentWithLength(final double ox, final double oy,
	    final double dx, final double dy, final double length) {
	return createSegment(
		createPoint(ox, oy),
		createPoint(ox, oy).addMul(length,
			createNormalizedVector(dx, dy)));
    }

    // 3D
    /**
     * Get segment. The first point will become the origin
     *
     * @param x1            x-ordinate of point 1
     * @param y1            y-ordinate of point 1 * @param z1 z-ordinate of point 1
     * @param z1 
     * @param x2            x-ordinate of point 2
     * @param y2            y-ordinate of point 2
     * @param z2            z-ordinate of point 2
     * @return line through points
     */
    public WB_Segment createSegment(final double x1, final double y1,
	    final double z1, final double x2, final double y2, final double z2) {
	return createSegment(createPoint(x1, y1, z1), createVector(x2, y2, z2));
    }

    /**
     * Get segment from point, direction and length.
     *
     * @param ox
     *            x-ordinate of origin
     * @param oy
     *            y-ordinate of origin
     * @param oz
     *            z-ordinate of origin
     * @param dx
     *            x-ordinate of direction
     * @param dy
     *            y-ordinate of direction
     * @param dz
     *            z-ordinate of direction
     * @param length
     *            length
     * @return segment
     */
    public WB_Segment createSegmentWithLength(final double ox, final double oy,
	    final double oz, final double dx, final double dy, final double dz,
	    final double length) {
	return createSegment(createPoint(ox, oy, oz), createPoint(ox, oy, oz)
		.addMul(length, createNormalizedVector(dx, dy, dz)));
    }

    /**
     * Get triangle from 3 points.
     *
     * @param p1x            x-ordinate of first point of triangle
     * @param p1y            y-ordinate of first point of triangle
     * @param p2x            x-ordinate of second point of triangle
     * @param p2y            y-ordinate of second point of triangle
     * @param p3x            x-ordinate of third point of triangle
     * @param p3y            y-ordinate of third point of triangle
     * @return triangle
     */
    public WB_Triangle createTriangle(final double p1x, final double p1y,
	    final double p2x, final double p2y, final double p3x,
	    final double p3y) {
	return createTriangle(createPoint(p1x, p1y), createPoint(p2x, p2y),
		createPoint(p3x, p3y));
    }

    // 3D
    /**
     * Get triangle from 3 points.
     *
     * @param p1x            x-ordinate of first point of triangle
     * @param p1y            y-ordinate of first point of triangle
     * @param p1z            z-ordinate of first point of triangle
     * @param p2x            x-ordinate of second point of triangle
     * @param p2y            y-ordinate of second point of triangle
     * @param p2z            z-ordinate of second point of triangle
     * @param p3x            x-ordinate of third point of triangle
     * @param p3y            y-ordinate of third point of triangle
     * @param p3z            z-ordinate of third point of triangle
     * @return triangle
     */
    public WB_Triangle createTriangle(final double p1x, final double p1y,
	    final double p1z, final double p2x, final double p2y,
	    final double p2z, final double p3x, final double p3y,
	    final double p3z) {
	return createTriangle(createPoint(p1x, p1y, p1z),
		createPoint(p2x, p2y, p2z), createPoint(p3x, p3y, p3z));
    }

    /**
     * Get triangle from 3 points.
     *
     * @param p1            first point of triangle
     * @param p2            second point of triangle
     * @param p3            third point of triangle
     * @return triangle
     */
    public WB_Triangle createTriangle(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3) {
	return new WB_Triangle(p1, p2, p3);
    }

    /**
     * Circle with center and radius.
     *
     * @param center 
     * @param normal 
     * @param radius 
     * @return circle
     */
    public WB_Circle createCircleWithRadius(final WB_Coordinate center,
	    final WB_Coordinate normal, final double radius) {
	return new WB_Circle(center, normal, radius);
    }

    /**
     * 
     *
     * @param center 
     * @param radius 
     * @return 
     */
    public WB_Circle createCircleWithRadius(final WB_Coordinate center,
	    final double radius) {
	return new WB_Circle(center, radius);
    }

    /**
     * Circle with center and diameter.
     *
     * @param center 
     * @param diameter 
     * @return circle
     */
    public WB_Circle createCircleWithDiameter(final WB_Coordinate center,
	    final double diameter) {
	return createCircleWithRadius(center, .5 * diameter);
    }

    /**
     * Circle with center and radius.
     *
     * @param x 
     * @param y 
     * @param radius 
     * @return circle
     */
    public WB_Circle createCircleWithRadius(final double x, final double y,
	    final double radius) {
	return createCircleWithRadius(createPoint(x, y), radius);
    }

    /**
     * Circle with diameter and radius.
     *
     * @param x 
     * @param y 
     * @param diameter 
     * @return circle
     */
    public WB_Circle createCircleWithDiameter(final double x, final double y,
	    final double diameter) {
	return createCircleWithRadius(createPoint(x, y), .5 * diameter);
    }

    /**
     * Inversion of circle C over circle inversionCircle
     * http://mathworld.wolfram.com/Inversion.html
     *
     * @param C
     *            circle
     * @param inversionCircle
     *            inversion circle
     *
     * @return of circle C over circle inversionCircle, null if C is tangent to
     *         inversionCircle
     */
    public WB_Circle createInversionCircle(final WB_Circle C,
	    final WB_Circle inversionCircle) {
	if (WB_Classify.classifyPointToCircle2D(inversionCircle.getCenter(), C) == WB_ClassificationGeometry.ON) {
	    return null;
	}
	final double x0 = inversionCircle.getCenter().xd();
	final double y0 = inversionCircle.getCenter().yd();
	final double k = inversionCircle.getRadius();
	final double k2 = k * k;
	final double s = k2
		/ (C.getCenter().getSqDistance3D(inversionCircle.getCenter()) - (C
			.getRadius() * C.getRadius()));
	return createCircleWithRadius(x0 + (s * (C.getCenter().xd() - x0)), y0
		+ (s * (C.getCenter().yd() - y0)), Math.abs(s) * C.getRadius());
    }

    // 3D
    /**
     * Get circumcircle of 2D triangle, z-ordinate is ignored.
     *
     * @param tri            triangle
     * @return circumcircle
     */
    public WB_Circle createCircumcircle(final WB_Triangle tri) {
	final double a = tri.a();
	final double b = tri.b();
	final double c = tri.c();
	final double radius = (a * b * c)
		/ Math.sqrt(((2 * a * a * b * b) + (2 * b * b * c * c) + (2 * a
			* a * c * c))
			- (a * a * a * a) - (b * b * b * b) - (c * c * c * c));
	final double bx = tri.p2().xd() - tri.p1().xd();
	final double by = tri.p2().yd() - tri.p1().yd();
	final double cx = tri.p3().xd() - tri.p1().xd();
	final double cy = tri.p3().yd() - tri.p1().yd();
	double d = 2 * ((bx * cy) - (by * cx));
	if (WB_Epsilon.isZero(d)) {
	    return null;
	}
	d = 1.0 / d;
	final double b2 = (bx * bx) + (by * by);
	final double c2 = (cx * cx) + (cy * cy);
	final double x = ((cy * b2) - (by * c2)) * d;
	final double y = ((bx * c2) - (cx * b2)) * d;
	return createCircleWithRadius(
		createPoint(x + tri.p1().xd(), y + tri.p1().yd()), radius);
    }

    /**
     * Get incircle of triangle, z-ordinate is ignored.
     *
     * @param tri            triangle
     * @return incircle
     */
    public WB_Circle createIncircle(final WB_Triangle tri) {
	final double a = tri.a();
	final double b = tri.b();
	final double c = tri.c();
	final double invabc = 1.0 / (a + b + c);
	final double radius = 0.5 * Math
		.sqrt((((b + c) - a) * ((c + a) - b) * ((a + b) - c)) * invabc);
	final double x = ((tri.p1().xd() * a) + (tri.p2().xd() * b) + (tri.p3()
		.xd() * c)) * invabc;
	final double y = ((tri.p1().yd() * a) + (tri.p2().yd() * b) + (tri.p3()
		.yd() * c)) * invabc;
	return createCircleWithRadius(createPoint(x, y), radius);
    }

    /**
     * Gets the circle through 3 2D points, z-ordinate is ignored.
     *
     * @param p0 
     * @param p1 
     * @param p2 
     * @return circle through 3 points
     */
    public WB_Circle createCirclePPP(final WB_Coordinate p0,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	final WB_Predicates pred = new WB_Predicates();
	final double[] circumcenter = pred.circumcenterTri(toDouble(p0),
		toDouble(p1), toDouble(p2));
	final WB_Point center = createPoint(circumcenter[0], circumcenter[1]);
	return createCircleWithRadius(center, center.getDistance3D(p0));
    }

    /**
     * http://www.cut-the-knot.org/Curriculum/Geometry/GeoGebra/PPL.shtml
     *
     * @param p 
     * @param q 
     * @param L 
     * @return circles through 2 points and tangent to line
     */
    public List<WB_Circle> createCirclePPL(final WB_Coordinate p,
	    final WB_Coordinate q, final WB_Line L) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	if (WB_Classify.sameSideOfLine2D(p, q, L) == WB_ClassificationGeometry.DIFF) {
	    return result;
	}
	if ((WB_Classify.classifyPointToLine2D(p, L) == WB_ClassificationGeometry.ON)
		&& (WB_Classify.classifyPointToLine2D(q, L) == WB_ClassificationGeometry.ON)) {
	    return result;
	}
	final WB_Line PQ = createLineThroughPoints(p, q);
	if (WB_Classify.classifyPointToLine2D(p, L) == WB_ClassificationGeometry.ON) {
	    if (WB_Epsilon.isZeroSq(createClosestPointOnLine2D(q, L)
		    .getSqDistance3D(p))) {
		result.add(createCircleWithRadius(
			createPoint(p).mulAddMul(0.5, 0.5, q),
			0.5 * WB_GeometryOp.getDistanceToPoint2D(p, q)));
	    } else {
		final WB_Line perp = createPerpendicularLineThroughPoint2D(L, p);
		final WB_Line PQbis = createBisector2D(p, q);
		final WB_Point intersect = createIntersectionPoint2D(perp,
			PQbis);
		result.add(createCircleWithRadius(intersect,
			WB_GeometryOp.getDistanceToPoint2D(p, intersect)));
	    }
	    return result;
	}
	if (WB_Classify.classifyPointToLine2D(q, L) == WB_ClassificationGeometry.ON) {
	    if (WB_Epsilon.isZeroSq(createClosestPointOnLine2D(p, L)
		    .getSqDistance3D(q))) {
		result.add(createCircleWithRadius(
			createPoint(p).mulAddMulSelf(0.5, 0.5, q),
			0.5 * WB_GeometryOp.getDistanceToPoint2D(p, q)));
	    } else {
		final WB_Line perp = createPerpendicularLineThroughPoint2D(L, q);
		final WB_Line PQbis = createBisector2D(p, q);
		final WB_Point intersect = createIntersectionPoint2D(perp,
			PQbis);
		result.add(createCircleWithRadius(intersect,
			WB_GeometryOp.getDistanceToPoint2D(p, intersect)));
	    }
	    return result;
	}
	final WB_Point F = createIntersectionPoint2D(L, PQ);
	if (F == null) {
	    final WB_Point r = createClosestPointOnLine2D(
		    createPoint((0.5 * p.xd()) + (0.5 * q.xd()), (0.5 * p.yd())
			    + (0.5 * q.yd())), L);
	    result.add(createCirclePPP(p, q, r));
	} else {
	    double d = WB_GeometryOp.getDistanceToPoint2D(p, q);
	    final WB_Circle OPQ = createCircleThrough2Points(p, q, d).get(0);
	    final WB_Point center = F.mulAddMul(0.5, 0.5, OPQ.getCenter());
	    final WB_Circle STF = createCircleWithRadius(center,
		    center.getDistance3D(F));
	    final List<WB_Point> intersections = createIntersectionPoints(STF,
		    OPQ);
	    d = F.getDistance3D(intersections.get(0));
	    final WB_Point K = F.addMul(d, L.getDirection());
	    final WB_Point J = F.addMul(-d, L.getDirection());
	    result.add(createCirclePPP(p, q, K));
	    result.add(createCirclePPP(p, q, J));
	}
	return uniqueOnly(result);
    }

    /**
     * Gets circles tangent to 2 2D lines through point.
     * http://www.cut-the-knot.org/Curriculum/Geometry/GeoGebra/PLL.shtml
     *
     * @param p            point
     * @param L1            line
     * @param L2            line
     * @return circles tangent to 2 2D lines through point
     */
    public List<WB_Circle> createCirclePLL(final WB_Coordinate p,
	    final WB_Line L1, final WB_Line L2) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>(2);
	final WB_Point A = createIntersectionPoint2D(L1, L2);
	final List<WB_Line> bis = createAngleBisector2D(L1, L2);
	if (A == null) {
	    final double r = WB_GeometryOp.distanceToLine2D(L1.getOrigin(),
		    bis.get(0));
	    final WB_Circle C = createCircleWithRadius(p, r);
	    final List<WB_Point> intersections = createIntersectionPoints(
		    bis.get(0), C);
	    for (final WB_Point point : intersections) {
		result.add(createCircleWithRadius(point, r));
	    }
	} else {
	    final List<WB_Circle> circles = createCircleTangentTo2Lines(L1, L2,
		    100.0);
	    final List<WB_Circle> selcircles = new ArrayList<WB_Circle>();
	    for (final WB_Circle C : circles) {
		if ((WB_Classify.sameSideOfLine2D(p, C.getCenter(), L1) == WB_ClassificationGeometry.SAME)
			&& (WB_Classify.sameSideOfLine2D(p, C.getCenter(), L2) == WB_ClassificationGeometry.SAME)) {
		    selcircles.add(C);
		}
	    }
	    for (final WB_Circle C : selcircles) {
		final WB_Point E = C.getCenter();
		final WB_Line Ap = createLineThroughPoints(A, p);
		final List<WB_Point> intersections = createIntersectionPoints(
			Ap, C);
		if (intersections.size() == 1) {
		    final WB_Point G = intersections.get(0);
		    final double AG = A.getDistance3D(G);
		    final double AD = A.getDistance3D(p);
		    final double AE = A.getDistance3D(E);
		    final double AK = (AD / AG) * AE;
		    final WB_Vector v = createNormalizedVector(E.sub(A));
		    final WB_Point K = createPoint(A.addMul(AK, v));
		    result.add(createCircleWithRadius(K,
			    WB_GeometryOp.distanceToLine2D(K, L1)));
		} else if (intersections.size() == 2) {
		    final WB_Point G = intersections.get(0);
		    final WB_Point H = intersections.get(1);
		    final double AH = A.getDistance3D(H);
		    final double AG = A.getDistance3D(G);
		    final double AD = A.getDistance3D(p);
		    final double AE = A.getDistance3D(E);
		    final double AI = (AD / AH) * AE;
		    final double AK = (AD / AG) * AE;
		    final WB_Vector v = createNormalizedVector(E.sub(A));
		    final WB_Point I = createPoint(A.addMul(AI, v));
		    final WB_Point K = createPoint(A.addMul(AK, v));
		    result.add(createCircleWithRadius(I,
			    WB_GeometryOp.distanceToLine2D(I, L1)));
		    result.add(createCircleWithRadius(K,
			    WB_GeometryOp.distanceToLine2D(K, L1)));
		}
	    }
	}
	return uniqueOnly(result);
    }

    /**
     * Gets circles through two 2d points tangent to circle.
     * http://mathafou.free.fr/pbg_en/sol136.html
     *
     * @param p
     *            2D point
     * @param q
     *            2D point
     * @param C
     *            circle
     * @return circles through two 2d points tangent to circle
     */
    public List<WB_Circle> createCirclePPC(final WB_Coordinate p,
	    final WB_Coordinate q, final WB_Circle C) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	final WB_ClassificationGeometry pType = WB_Classify
		.classifyPointToCircle2D(p, C);
	final WB_ClassificationGeometry qType = WB_Classify
		.classifyPointToCircle2D(q, C);
	if (WB_Epsilon.isZero(WB_GeometryOp.getDistanceToPoint2D(p, q))) {
	    return result;
	}
	// Both points on circle: only solution is circle itself
	if ((pType == WB_ClassificationGeometry.ON)
		&& (qType == WB_ClassificationGeometry.ON)) {
	    return result;
	    // Point p on circle, arbitrary point q.
	} else if (pType == WB_ClassificationGeometry.ON) {
	    final WB_Line ABbis = createBisector2D(p, q);
	    final WB_Line ATbis = createLineThroughPoints(p, C.getCenter());
	    final WB_Point center = createIntersectionPoint2D(ABbis, ATbis);
	    if (center != null) {
		result.add(createCircleWithRadius(center,
			center.getDistance3D(p)));
	    }
	    return result;
	    // Point q on circle, arbitrary point p.
	} else if (qType == WB_ClassificationGeometry.ON) {
	    final WB_Line ABbis = createBisector2D(p, q);
	    final WB_Line ATbis = createLineThroughPoints(q, C.getCenter());
	    final WB_Point center = createIntersectionPoint2D(ABbis, ATbis);
	    if (center != null) {
		result.add(createCircleWithRadius(center,
			center.getDistance3D(p)));
	    }
	    return result;
	}
	// One point inside, one point outside. (All cases with points on circle
	// are already handled). No solution.
	if (pType != qType) {
	    return result;
	}
	// Both points outside
	else if (pType == WB_ClassificationGeometry.OUTSIDE) {
	    final WB_Line AB = createLineThroughPoints(p, q);
	    final WB_Line ABbis = createBisector2D(p, q);
	    if (C.getCenter().isCollinear(ABbis.getOrigin(),
		    ABbis.getPointOnLine(100.0))) {
		final List<WB_Point> points = createIntersectionPoints(ABbis, C);
		for (final WB_Point pt : points) {
		    result.add(createCirclePPP(pt, p, q));
		}
	    } else {
		WB_Vector v = createVector(-AB.getDirection().yd(), AB
			.getDirection().xd());
		WB_Point E = C.getCenter().addMul(0.5 * C.getRadius(), v);
		if (E.isCollinear(p, q)) {
		    v = createVector(AB.getDirection().yd(), -AB.getDirection()
			    .xd());
		    E = C.getCenter().addMul(0.5 * C.getRadius(), v);
		}
		final WB_Circle circle = createCirclePPP(p, q, E);
		final List<WB_Point> intersections = createIntersectionPoints(
			circle, C);
		final WB_Line MN = createLineThroughPoints(
			intersections.get(0), intersections.get(1));
		final WB_Point point = createIntersectionPoint2D(AB, MN);
		if (point == null) {
		    return result;
		}
		final List<WB_Line> tangents = createLinesTangentToCircleThroughPoint(
			C, point);
		for (final WB_Line L : tangents) {
		    final WB_Point T = createClosestPointOnLine2D(
			    C.getCenter(), L);
		    final WB_Line ATbis;
		    ATbis = createBisector2D(p, T);
		    final WB_Point center = createIntersectionPoint2D(ABbis,
			    ATbis);
		    if (center != null) {
			result.add(createCircleWithRadius(center,
				center.getDistance3D(p)));
		    }
		}
	    }
	    // Both points inside, solve with inversion
	} else {
	    final List<WB_Circle> iresult = new ArrayList<WB_Circle>();
	    final WB_Circle iC;
	    final WB_Circle iC2;
	    final double k2;
	    final boolean dp = WB_Epsilon
		    .isZero(C.getCenter().getDistance3D(p));
	    final boolean dq = WB_Epsilon
		    .isZero(C.getCenter().getDistance3D(q));
	    if (dp || dq) {
		final WB_Vector v = createNormalizedVector(-p.yd() - q.yd(),
			p.xd() + q.xd());
		iC = createCircleWithRadius(
			C.getCenter().addMul(0.5 * C.getRadius(), v),
			C.getRadius() * 3);
		k2 = iC.getRadius() * iC.getRadius();
		final double s = k2
			/ (C.getCenter().getSqDistance3D(iC.getCenter()) - (C
				.getRadius() * C.getRadius()));
		iC2 = createCircleWithRadius(iC.getCenter().xd()
			+ (s * (C.getCenter().xd() - iC.getCenter().xd())), iC
			.getCenter().yd()
			+ (s * (C.getCenter().yd() - iC.getCenter().yd())),
				Math.abs(s) * C.getRadius());
	    } else {
		iC = C;
		k2 = iC.getRadius() * iC.getRadius();
		iC2 = C;
	    }
	    final WB_Point ip = createInversionPoint(p, iC);
	    final WB_Point iq = createInversionPoint(q, iC);
	    final WB_Line ABbis = createBisector2D(ip, iq);
	    if (iC2.getCenter().isCollinear(ABbis.getOrigin(),
		    ABbis.getPointOnLine(100.0))) {
		final List<WB_Point> points = createIntersectionPoints(ABbis,
			iC2);
		for (final WB_Point pt : points) {
		    iresult.add(createCirclePPP(pt, ip, iq));
		}
	    } else {
		final WB_Line AB = createLineThroughPoints(ip, iq);
		WB_Vector v = createVector(-AB.getDirection().yd(), AB
			.getDirection().xd());
		WB_Point E = iC2.getCenter().addMul(0.5 * iC2.getRadius(), v);
		if (E.isCollinear(ip, iq)) {
		    v = createVector(AB.getDirection().yd(), -AB.getDirection()
			    .xd());
		    E = iC2.getCenter().addMul(0.5 * iC2.getRadius(), v);
		}
		final WB_Circle circle = createCirclePPP(ip, iq, E);
		final List<WB_Point> intersections = createIntersectionPoints(
			circle, iC2);
		final WB_Line MN = createLineThroughPoints(
			intersections.get(0), intersections.get(1));
		final WB_Point point = createIntersectionPoint2D(AB, MN);
		if (point == null) {
		    return result;
		}
		final List<WB_Line> tangents = createLinesTangentToCircleThroughPoint(
			iC2, point);
		for (final WB_Line L : tangents) {
		    final WB_Point T = createClosestPointOnLine2D(
			    iC2.getCenter(), L);
		    final WB_Line ATbis;
		    ATbis = createBisector2D(ip, T);
		    final WB_Point center = createIntersectionPoint2D(ABbis,
			    ATbis);
		    if (center != null) {
			iresult.add(createCircleWithRadius(center,
				center.getDistance3D(ip)));
		    }
		}
	    }
	    for (final WB_Circle circle : iresult) {
		final double s = k2
			/ (circle.getCenter().getSqDistance3D(iC.getCenter()) - (circle
				.getRadius() * circle.getRadius()));
		result.add(createCircleWithRadius(
			iC.getCenter().xd()
				+ (s * (circle.getCenter().xd() - iC
					.getCenter().xd())), iC.getCenter()
				.yd()
				+ (s * (circle.getCenter().yd() - iC
					.getCenter().yd())), Math.abs(s)
				* circle.getRadius()));
	    }
	}
	return uniqueOnly(result);
    }

    /**
     * http://www.cut-the-knot.org/Curriculum/Geometry/GeoGebra/PCC.shtml#
     * solution
     *
     * @param p
     * @param C1
     * @param C2
     * @return circles through point and tangent to two circles
     */
    public List<WB_Circle> createCirclePCC(final WB_Coordinate p,
	    final WB_Circle C1, final WB_Circle C2) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	if (C1.equals(C2)) {
	    return result;
	}
	// p on C1
	if (WB_Classify.classifyPointToCircle2D(p, C1) == WB_ClassificationGeometry.ON) {
	    final WB_Line tangent = createLineTangentToCircleInPoint(C1, p);
	    return createCirclePLC(p, tangent, C2);
	}
	// p on C2
	if (WB_Classify.classifyPointToCircle2D(p, C2) == WB_ClassificationGeometry.ON) {
	    final WB_Line tangent = createLineTangentToCircleInPoint(C2, p);
	    return createCirclePLC(p, tangent, C1);
	}
	final WB_ClassificationGeometry C1toC2 = WB_Classify
		.classifyCircleToCircle2D(C1, C2);
	// C1 tangent to C2
	if (C1.isTangent(C2)) {
	    final WB_Point q = createIntersectionPoints(C1, C2).get(0);
	    result.addAll(createCirclePPC(p, q, C1));
	    // C1 inside C2, transform to outside case
	} else if (C1toC2 == WB_ClassificationGeometry.INSIDE) {
	    if (WB_Classify.classifyPointToCircle2D(p, C1) == WB_ClassificationGeometry.INSIDE) {
		return result;
	    }
	    final WB_Vector v = !WB_Epsilon.isZero(C1.getCenter()
		    .getDistance3D(C2.getCenter())) ? createNormalizedVectorFromTo(
		    C1.getCenter(), C2.getCenter()) : X();
	    WB_Point invcenter = C1.getCenter().addMul(
		    0.5 * (C1.getRadius() + C2.getRadius()), v);
	    if (WB_Epsilon.isZero(invcenter.getDistance3D(p))) {
		invcenter = C1.getCenter().addMul(
			C1.getRadius()
				+ (0.4 * (C2.getRadius() - C1.getRadius())), v);
	    }
	    final WB_Circle invC = createCircleWithRadius(invcenter,
		    2 * (C1.getRadius() + C2.getRadius()));
	    final WB_Point q = createInversionPoint(p, invC);
	    final WB_Circle invC1 = createInversionCircle(C1, invC);
	    final WB_Circle invC2 = createInversionCircle(C2, invC);
	    if (invC1 != null) {
		final List<WB_Circle> invResult = createCirclePCC(q, invC1,
			invC2);
		for (final WB_Circle inv : invResult) {
		    result.add(createInversionCircle(inv, invC));
		}
	    }
	    // C2 inside C1, transfrom to outside case
	} else if (C1toC2 == WB_ClassificationGeometry.CONTAINING) {
	    if (WB_Classify.classifyPointToCircle2D(p, C2) == WB_ClassificationGeometry.INSIDE) {
		return result;
	    }
	    final WB_Vector v = !WB_Epsilon.isZero(C1.getCenter()
		    .getDistance3D(C2.getCenter())) ? createNormalizedVectorFromTo(
		    C2.getCenter(), C1.getCenter()) : X();
	    WB_Point invcenter = C2.getCenter().addMul(
		    0.5 * (C1.getRadius() + C2.getRadius()), v);
	    if (WB_Epsilon.isZero(invcenter.getDistance3D(p))) {
		invcenter = C2.getCenter().addMul(
			C2.getRadius()
				+ (0.4 * (C1.getRadius() - C2.getRadius())), v);
	    }
	    final WB_Circle invC = createCircleWithRadius(invcenter,
		    2 * (C1.getRadius() + C2.getRadius()));
	    final WB_Point q = createInversionPoint(p, invC);
	    final WB_Circle invC1 = createInversionCircle(C1, invC);
	    final WB_Circle invC2 = createInversionCircle(C2, invC);
	    if (invC1 != null) {
		final List<WB_Circle> invResult = createCirclePCC(q, invC1,
			invC2);
		for (final WB_Circle inv : invResult) {
		    result.add(createInversionCircle(inv, invC));
		}
	    }
	}
	// C1 and C2 outside or C1 and C2 crossing with p in intersection or
	// completely outside
	else if ((C1toC2 == WB_ClassificationGeometry.OUTSIDE)
		|| ((C1toC2 == WB_ClassificationGeometry.CROSSING) && (!(WB_Classify
			.classifyPointToCircle2D(p, C1) == WB_ClassificationGeometry.OUTSIDE) ^ (WB_Classify
			.classifyPointToCircle2D(p, C2) == WB_ClassificationGeometry.OUTSIDE)))) {
	    final List<WB_Line> tangents = createLinesTangentTo2Circles(C1, C2);
	    // if ((WB_Classify.classifyPointToCircle2D(p, C1) ==
	    // WB_Classification.INSIDE)
	    // || (WB_Classify.classifyPointToCircle2D(p, C2) ==
	    // WB_Classification.INSIDE)) {
	    // return result;
	    // }
	    if (WB_Classify.classifyPointToCircle2D(p, C1) == WB_ClassificationGeometry.ON) {
		final WB_Line L = createLineTangentToCircleInPoint(C1, p);
		return createCirclePLC(p, L, C2);
	    }
	    if (WB_Classify.classifyPointToCircle2D(p, C2) == WB_ClassificationGeometry.ON) {
		final WB_Line L = createLineTangentToCircleInPoint(C2, p);
		return createCirclePLC(p, L, C1);
	    }
	    WB_Line T1;
	    WB_Line T2;
	    WB_Point point;
	    if (tangents.size() > 1) {
		T1 = tangents.get(0);
		T2 = tangents.get(1);
		point = createIntersectionPoint2D(T1, T2);
		if (point != null) {
		    final WB_Point G = createClosestPointOnLine2D(
			    C1.getCenter(), T1);
		    final WB_Point H = createClosestPointOnLine2D(
			    C2.getCenter(), T1);
		    final WB_Circle circle = createCirclePPP(G, H, p);
		    final WB_Line Pp = createLineThroughPoints(p, point);
		    final List<WB_Point> intersections = createIntersectionPoints(
			    Pp, circle);
		    WB_Point Ep = null;
		    if (!WB_Epsilon.isZero(intersections.get(0)
			    .getDistance3D(p))) {
			Ep = intersections.get(0);
		    } else if (!WB_Epsilon.isZero(intersections.get(1)
			    .getDistance3D(p))) {
			Ep = intersections.get(1);
		    }
		    if (Ep != null) {
			result.addAll(createCirclePPC(p, Ep, C1));
		    }
		} else {// tangents T1 and T2 are parallel
		    final WB_Point G = createClosestPointOnLine2D(
			    C1.getCenter(), T1);
		    final WB_Point H = createClosestPointOnLine2D(
			    C2.getCenter(), T1);
		    final WB_Circle circle = createCirclePPP(G, H, p);
		    final WB_Line Pp = createParallelLineThroughPoint(T1, p);
		    final List<WB_Point> intersections = createIntersectionPoints(
			    Pp, circle);
		    if (intersections.size() == 2) {
			WB_Point Ep = null;
			if (!WB_Epsilon.isZero(intersections.get(0)
				.getDistance3D(p))) {
			    Ep = intersections.get(0);
			} else if (!WB_Epsilon.isZero(intersections.get(1)
				.getDistance3D(p))) {
			    Ep = intersections.get(1);
			}
			if (Ep != null) {
			    result.addAll(createCirclePPC(p, Ep, C1));
			}
		    } else if (intersections.size() == 1) {
			final WB_Line L = createLineThroughPoints(
				C1.getCenter(), C2.getCenter());
			result.addAll(createCircleLCC(L, C1, C2));
			final List<WB_Circle> filter = new ArrayList<WB_Circle>();
			for (int i = 0; i < result.size(); i++) {
			    final WB_Circle C = result.get(i);
			    if (C.isTangent(C1)
				    && C.isTangent(C2)
				    && (WB_Classify.classifyPointToCircle2D(p,
					    C) == WB_ClassificationGeometry.ON)) {
				filter.add(C);
			    }
			}
			return uniqueOnly(filter);
		    }
		}
	    }
	    if (tangents.size() == 4) {
		T1 = tangents.get(2);
		T2 = tangents.get(3);
		point = createIntersectionPoint2D(T1, T2);
		if (point != null) {
		    final WB_Point G = createClosestPointOnLine2D(
			    C1.getCenter(), T1);
		    final WB_Point H = createClosestPointOnLine2D(
			    C2.getCenter(), T1);
		    final WB_Circle circle = createCirclePPP(G, H, p);
		    final WB_Line Pp = createLineThroughPoints(p, point);
		    final List<WB_Point> intersections = createIntersectionPoints(
			    Pp, circle);
		    WB_Point Ep = null;
		    if (!WB_Epsilon.isZero(intersections.get(0)
			    .getDistance3D(p))) {
			Ep = intersections.get(0);
		    } else if (!WB_Epsilon.isZero(intersections.get(1)
			    .getDistance3D(p))) {
			Ep = intersections.get(1);
		    }
		    if (Ep != null) {
			result.addAll(createCirclePPC(p, Ep, C1));
		    }
		}
	    }
	}
	// C1 and C2 crossing and p in only one of the two circles
	else {
	    if (WB_Classify.classifyPointToCircle2D(p, C1) == WB_ClassificationGeometry.INSIDE) {
		final double r1 = C1.getRadius()
			- C1.getCenter().getDistance3D(p);
		final double r2 = C2.getCenter().getDistance3D(p)
			- C2.getRadius();
		WB_Circle invC;
		if (r1 <= r2) {
		    final WB_Vector v = createLineThroughPoints(C1.getCenter(),
			    p).getDirection();
		    final WB_Point center = createPoint(p).addMulSelf(
			    0.45 * r1, v);
		    invC = createCircleWithRadius(center, 0.45 * r1);
		} else {
		    final WB_Vector v = createLineThroughPoints(C2.getCenter(),
			    p).getDirection();
		    final WB_Point center = createPoint(p).addMulSelf(
			    0.45 * r2, v);
		    invC = createCircleWithRadius(center, 0.45 * r2);
		}
		final WB_Circle invC1 = createInversionCircle(C1, invC);
		final WB_Circle invC2 = createInversionCircle(C2, invC);
		if (invC1 != null) {
		    final List<WB_Circle> invResult = createCirclePCC(p, invC1,
			    invC2);
		    for (final WB_Circle inv : invResult) {
			result.add(createInversionCircle(inv, invC));
		    }
		}
	    } else {
		final double r1 = -C1.getRadius()
			+ C1.getCenter().getDistance3D(p);
		final double r2 = -C2.getCenter().getDistance3D(p)
			+ C2.getRadius();
		WB_Circle invC;
		if (r1 <= r2) {
		    final WB_Vector v = createLineThroughPoints(C1.getCenter(),
			    p).getDirection();
		    final WB_Point center = createPoint(p).addMulSelf(
			    0.45 * r1, v);
		    invC = createCircleWithRadius(center, 0.45 * r1);
		} else {
		    final WB_Vector v = createLineThroughPoints(C2.getCenter(),
			    p).getDirection();
		    final WB_Point center = createPoint(p).addMulSelf(
			    0.45 * r2, v);
		    invC = createCircleWithRadius(center, 0.45 * r2);
		}
		final WB_Circle invC1 = createInversionCircle(C1, invC);
		final WB_Circle invC2 = createInversionCircle(C2, invC);
		if (invC1 != null) {
		    final List<WB_Circle> invResult = createCirclePCC(p, invC1,
			    invC2);
		    for (final WB_Circle inv : invResult) {
			result.add(createInversionCircle(inv, invC));
		    }
		}
	    }
	}
	return uniqueOnly(result);
    }

    /**
     * Gets circles through a 2D point tangent to a circle and a 2D line.
     * http://www.epab.bme.hu/geoc2/GC2_Lecture_notes_11_Spring.pdf
     *
     * @param p            2D point
     * @param L            2D line
     * @param C            circle
     * @return circles through a 2D point tangent to circle and 2D line
     */
    public List<WB_Circle> createCirclePLC(final WB_Coordinate p,
	    final WB_Line L, final WB_Circle C) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	createIntersectionPoints(L, C).size();
	WB_Line Lperp = createPerpendicularLineThroughPoint2D(L, C.getCenter());
	if (WB_Classify.classifyPointToCircle2D(p, C) == WB_ClassificationGeometry.ON) {
	    final WB_Line tangent = createLineTangentToCircleInPoint(C, p);
	    result.addAll(createCirclePLL(p, tangent, L));
	} else if (WB_Classify.classifyPointToLine2D(p, L) == WB_ClassificationGeometry.ON) {
	    List<WB_Point> points = createIntersectionPoints(Lperp, C);
	    final WB_Point A1 = points.get(0);
	    final WB_Point A2 = points.get(1);
	    if (WB_Classify.classifyPointToLine2D(p, Lperp) != WB_ClassificationGeometry.ON) {
		final WB_Point B = createIntersectionPoint2D(L, Lperp);
		if (WB_Epsilon.isZero(A2.getDistance3D(B))) {
		    final WB_Line A1P = createLineThroughPoints(A1, p);
		    points = createIntersectionPoints(A1P, C);
		    WB_Point Q1 = null;
		    if (!WB_Epsilon.isZeroSq(points.get(0).getSqDistance3D(A1))) {
			Q1 = points.get(0);
		    } else if (!WB_Epsilon.isZeroSq(points.get(1)
			    .getSqDistance3D(A1))) {
			Q1 = points.get(1);
		    }
		    if (Q1 != null) {
			Lperp = createPerpendicularLineThroughPoint2D(L, p);
			final WB_Line Q1P = createBisector2D(Q1, p);
			final WB_Point intersection = createIntersectionPoint2D(
				Lperp, Q1P);
			result.add(createCircleWithRadius(intersection,
				intersection.getDistance3D(Q1)));
		    }
		} else if (!A2.isCollinear(B, p)) {
		    final WB_Line A1P = createLineThroughPoints(A1, p);
		    final WB_Circle BA2P = createCirclePPP(B, A2, p);
		    points = createIntersectionPoints(A1P, BA2P);
		    WB_Point Q1 = null;
		    if (!WB_Epsilon.isZeroSq(points.get(0).getSqDistance3D(p))) {
			Q1 = points.get(0);
		    } else if (!WB_Epsilon.isZeroSq(points.get(1)
			    .getSqDistance3D(p))) {
			Q1 = points.get(1);
		    }
		    if (Q1 != null) {
			result.addAll(createCirclePPL(p, Q1, L));
		    }
		}
		if (WB_Epsilon.isZero(A1.getDistance3D(B))) {
		    final WB_Line A2P = createLineThroughPoints(A2, p);
		    points = createIntersectionPoints(A2P, C);
		    WB_Point Q1 = null;
		    if (!WB_Epsilon.isZeroSq(points.get(0).getSqDistance3D(A2))) {
			Q1 = points.get(0);
		    } else if (!WB_Epsilon.isZeroSq(points.get(1)
			    .getSqDistance3D(A1))) {
			Q1 = points.get(1);
		    }
		    if (Q1 != null) {
			Lperp = createPerpendicularLineThroughPoint2D(L, p);
			final WB_Line Q1P = createBisector2D(Q1, p);
			final WB_Point intersection = createIntersectionPoint2D(
				Lperp, Q1P);
			result.add(createCircleWithRadius(intersection,
				intersection.getDistance3D(Q1)));
		    }
		} else if (!A1.isCollinear(B, p)) {
		    final WB_Line A2P = createLineThroughPoints(A2, p);
		    final WB_Circle BA1P = createCirclePPP(B, A1, p);
		    points = createIntersectionPoints(A2P, BA1P);
		    WB_Point Q2 = null;
		    if (!WB_Epsilon.isZeroSq(points.get(0).getSqDistance3D(p))) {
			Q2 = points.get(0);
		    } else if (!WB_Epsilon.isZeroSq(points.get(1)
			    .getSqDistance3D(p))) {
			Q2 = points.get(1);
		    }
		    if (Q2 != null) {
			result.addAll(createCirclePPL(p, Q2, L));
		    }
		}
	    } else {
		double d = WB_GeometryOp.distanceToLine2D(A1, L);
		if (!WB_Epsilon.isZero(d)) {
		    result.add(createCircleWithRadius(
			    createInterpolatedPoint(p, A1, 0.5), 0.5 * d));
		}
		d = WB_GeometryOp.distanceToLine2D(A2, L);
		if (!WB_Epsilon.isZero(d)) {
		    result.add(createCircleWithRadius(
			    createInterpolatedPoint(p, A2, 0.5), 0.5 * d));
		}
	    }
	} else {
	    final boolean tangentcircle = (WB_Classify.classifyCircleToLine2D(
		    C, L) == WB_ClassificationGeometry.TANGENT);
	    if (tangentcircle) {
		result.addAll(createCirclePPL(p,
			createClosestPointOnLine2D(C.getCenter(), L), L));
		if ((WB_Classify.classifyPointToLine2D(p, L) == WB_Classify
			.classifyPointToLine2D(C.getCenter(), L))
			&& (WB_Classify.classifyPointToCircle2D(p, C) != WB_ClassificationGeometry.INSIDE)) {
		    final WB_Circle inversion = createCircleWithRadius(p, 100.0);
		    WB_Point p1 = createInversionPoint(L.getOrigin(), inversion);
		    WB_Point p2 = createInversionPoint(L.getPointOnLine(100.0),
			    inversion);
		    final WB_Circle invL = createCirclePPP(p, p1, p2);
		    p1 = createInversionPoint(
			    C.getCenter().addMul(C.getRadius(), X()), inversion);
		    p2 = createInversionPoint(
			    C.getCenter().addMul(-C.getRadius(), X()),
			    inversion);
		    final WB_Point p3 = createInversionPoint(C.getCenter()
			    .addMul(C.getRadius(), Y()), inversion);
		    final WB_Circle invC = createCirclePPP(p1, p2, p3);
		    final List<WB_Line> invResult = createLinesTangentTo2Circles(
			    invL, invC);
		    for (int i = 0; i < Math.min(2, invResult.size()); i++) {
			final WB_Line inv = invResult.get(i);
			p1 = createInversionPoint(inv.getOrigin(), inversion);
			p2 = createInversionPoint(inv.getPointOnLine(100.0),
				inversion);
			result.add(createCirclePPP(p, p1, p2));
		    }
		}
	    } else {
		final WB_Circle inversion = createCircleWithRadius(p, 100.0);
		WB_Point p1 = createInversionPoint(L.getOrigin(), inversion);
		WB_Point p2 = createInversionPoint(L.getPointOnLine(100.0),
			inversion);
		final WB_Circle invL = createCirclePPP(p, p1, p2);
		p1 = createInversionPoint(
			C.getCenter().addMul(C.getRadius(), X()), inversion);
		p2 = createInversionPoint(
			C.getCenter().addMul(-C.getRadius(), X()), inversion);
		final WB_Point p3 = createInversionPoint(
			C.getCenter().addMul(C.getRadius(), Y()), inversion);
		final WB_Circle invC = createCirclePPP(p1, p2, p3);
		final List<WB_Line> invResult = createLinesTangentTo2Circles(
			invL, invC);
		for (int i = 0; i < invResult.size(); i++) {
		    final WB_Line inv = invResult.get(i);
		    p1 = createInversionPoint(inv.getOrigin(), inversion);
		    p2 = createInversionPoint(inv.getPointOnLine(100.0),
			    inversion);
		    result.add(createCirclePPP(p, p1, p2));
		}
	    }
	}
	final List<WB_Circle> filter = new ArrayList<WB_Circle>();
	for (int i = 0; i < result.size(); i++) {
	    if (!C.equals(result.get(i))) {
		filter.add(result.get(i));
	    }
	}
	return uniqueOnly(filter);
    }

    /**
     * Gets the circle tangent to 3 2D lines.
     *
     * @param L1
     *
     * @param L2
     *
     * @param L3
     *
     * @return circle tangent to 3 lines
     */
    public List<WB_Circle> createCircleLLL(final WB_Line L1, final WB_Line L2,
	    final WB_Line L3) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	final List<WB_Line> lines12 = createAngleBisector2D(L1, L2);
	final List<WB_Line> lines23 = createAngleBisector2D(L2, L3);
	final List<WB_Line> lines31 = createAngleBisector2D(L3, L1);
	final List<WB_Point> intersections = new ArrayList<WB_Point>();
	WB_Point point;
	for (int i = 0; i < lines12.size(); i++) {
	    for (int j = 0; j < lines23.size(); j++) {
		point = createIntersectionPoint2D(lines12.get(i),
			lines23.get(j));
		if (point != null) {
		    intersections.add(point);
		}
	    }
	    for (int j = 0; j < lines31.size(); j++) {
		point = createIntersectionPoint2D(lines12.get(i),
			lines31.get(j));
		if (point != null) {
		    intersections.add(point);
		}
	    }
	}
	for (int i = 0; i < lines23.size(); i++) {
	    for (int j = 0; j < lines31.size(); j++) {
		point = createIntersectionPoint2D(lines23.get(i),
			lines31.get(j));
		if (point != null) {
		    intersections.add(point);
		}
	    }
	}
	for (final WB_Point p : intersections) {
	    final WB_Point p2 = createClosestPointOnLine2D(p, L1);
	    result.add(createCircleWithRadius(p, p.getDistance3D(p2)));
	}
	return uniqueOnly(result);
    }

    /**
     * Gets circles tangent to 2 2D lines and a circle.
     *
     * @param L1
     *            line
     * @param L2
     *            line
     * @param C
     *            circle
     * @return circles tangent to 2 2D lines through point
     */
    public List<WB_Circle> createCircleLLC(final WB_Line L1, final WB_Line L2,
	    final WB_Circle C) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	final WB_Point p = createPoint(C.getCenter());
	final WB_Point A = createIntersectionPoint2D(L1, L2);
	final List<WB_Line> bis = createAngleBisector2D(L1, L2);
	if (A == null) {
	    final WB_Line bisec = bis.get(0);
	    final double d = 0.5 * WB_GeometryOp.distanceToLine2D(
		    L1.getOrigin(), L2);
	    final WB_Circle C1 = createCircleWithRadius(C.getCenter(),
		    d + C.getRadius());
	    final WB_Circle C2 = createCircleWithRadius(C.getCenter(),
		    d - C.getRadius());
	    final List<WB_Point> points = createIntersectionPoints(bisec, C1);
	    points.addAll(createIntersectionPoints(bisec, C2));
	    for (final WB_Point point : points) {
		result.add(createCircleWithRadius(point, d));
	    }
	} else if (WB_Epsilon.isZeroSq(A.getSqDistance3D(p))) {
	    final List<WB_Point> points = createIntersectionPoints(bis.get(0),
		    C);
	    points.addAll(createIntersectionPoints(bis.get(1), C));
	    for (final WB_Point point : points) {
		result.addAll(createCirclePLL(point, L1, L2));
	    }
	} else {
	    final WB_Vector v1 = createVector(-L1.getDirection().yd(), L1
		    .getDirection().xd());
	    final WB_Vector v2 = createVector(-L2.getDirection().yd(), L2
		    .getDirection().xd());
	    WB_Line L1s = createLineWithDirection(
		    L1.getOrigin().addMul(-C.getRadius(), v1),
		    L1.getDirection());
	    WB_Line L2s = createLineWithDirection(
		    L2.getOrigin().addMul(-C.getRadius(), v2),
		    L2.getDirection());
	    List<WB_Circle> tmp = createCirclePLL(p, L1s, L2s);
	    for (final WB_Circle circle : tmp) {
		result.add(createCircleWithRadius(circle.getCenter(),
			WB_GeometryOp.distanceToLine2D(circle.getCenter(), L1)));
	    }
	    L1s = createLineWithDirection(
		    L1.getOrigin().addMul(C.getRadius(), v1), L1.getDirection());
	    L2s = createLineWithDirection(
		    L2.getOrigin().addMul(C.getRadius(), v2), L2.getDirection());
	    tmp = createCirclePLL(p, L1s, L2s);
	    for (final WB_Circle circle : tmp) {
		result.add(createCircleWithRadius(circle.getCenter(),
			WB_GeometryOp.distanceToLine2D(circle.getCenter(), L1)));
	    }
	    L1s = createLineWithDirection(
		    L1.getOrigin().addMul(-C.getRadius(), v1),
		    L1.getDirection());
	    L2s = createLineWithDirection(
		    L2.getOrigin().addMul(C.getRadius(), v2), L2.getDirection());
	    tmp = createCirclePLL(p, L1s, L2s);
	    for (final WB_Circle circle : tmp) {
		result.add(createCircleWithRadius(circle.getCenter(),
			WB_GeometryOp.distanceToLine2D(circle.getCenter(), L1)));
	    }
	    L1s = createLineWithDirection(
		    L1.getOrigin().addMul(C.getRadius(), v1), L1.getDirection());
	    L2s = createLineWithDirection(
		    L2.getOrigin().addMul(-C.getRadius(), v2),
		    L2.getDirection());
	    tmp = createCirclePLL(p, L1s, L2s);
	    for (final WB_Circle circle : tmp) {
		result.add(createCircleWithRadius(circle.getCenter(),
			WB_GeometryOp.distanceToLine2D(circle.getCenter(), L1)));
	    }
	}
	final List<WB_Circle> filter = new ArrayList<WB_Circle>();
	for (int i = 0; i < result.size(); i++) {
	    if ((!result.get(i).equals(C))
		    && WB_Epsilon.isEqualAbs(result.get(i).getRadius(),
			    WB_GeometryOp.distanceToLine2D(result.get(i)
				    .getCenter(), L1))
		    && WB_Epsilon.isEqualAbs(result.get(i).getRadius(),
			    WB_GeometryOp.distanceToLine2D(result.get(i)
				    .getCenter(), L2))) {
		filter.add(result.get(i));
	    }
	}
	return uniqueOnly(filter);
    }

    /**
     * Gets circles tangent to 2D line and two circles.
     *
     * @param L
     *            line
     * @param C1
     *            circle
     * @param C2
     *            circle
     * @return circles tangent to 2D line and two circles
     */
    public List<WB_Circle> createCircleLCC(final WB_Line L, final WB_Circle C1,
	    final WB_Circle C2) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	final List<WB_Circle> tmp;
	if (C1.getRadius() == C2.getRadius()) {
	    final WB_Vector v = createVector(-L.getDirection().yd(), L
		    .getDirection().xd());
	    final WB_Line L1s = createLineWithDirection(
		    L.getOrigin().addMul(-C1.getRadius(), v), L.getDirection());
	    final WB_Line L2s = createLineWithDirection(
		    L.getOrigin().addMul(C1.getRadius(), v), L.getDirection());
	    tmp = createCirclePPL(C1.getCenter(), C2.getCenter(), L1s);
	    tmp.addAll(createCirclePPL(C1.getCenter(), C2.getCenter(), L2s));
	    createCircleWithRadius(C1.getCenter(),
		    C1.getRadius() + C2.getRadius());
	    tmp.addAll(createCirclePLC(
		    C1.getCenter(),
		    L1s,
		    createCircleWithRadius(C2.getCenter(),
			    C1.getRadius() + C2.getRadius())));
	    tmp.addAll(createCirclePLC(
		    C1.getCenter(),
		    L2s,
		    createCircleWithRadius(C2.getCenter(),
			    C1.getRadius() + C2.getRadius())));
	    tmp.addAll(createCirclePLC(
		    C2.getCenter(),
		    L1s,
		    createCircleWithRadius(C1.getCenter(),
			    C1.getRadius() + C2.getRadius())));
	    tmp.addAll(createCirclePLC(
		    C2.getCenter(),
		    L2s,
		    createCircleWithRadius(C1.getCenter(),
			    C1.getRadius() + C2.getRadius())));
	} else {
	    WB_Circle Cm;
	    WB_Circle CM;
	    if (C1.getRadius() < C2.getRadius()) {
		Cm = C1;
		CM = C2;
	    } else {
		Cm = C2;
		CM = C1;
	    }
	    WB_Circle C = createCircleWithRadius(CM.getCenter(), CM.getRadius()
		    - Cm.getRadius());
	    final WB_Vector v = createVector(-L.getDirection().yd(), L
		    .getDirection().xd());
	    final WB_Line L1s = createLineWithDirection(
		    L.getOrigin().addMul(-Cm.getRadius(), v), L.getDirection());
	    final WB_Line L2s = createLineWithDirection(
		    L.getOrigin().addMul(Cm.getRadius(), v), L.getDirection());
	    tmp = createCirclePLC(Cm.getCenter(), L1s, C);
	    tmp.addAll(createCirclePLC(Cm.getCenter(), L2s, C));
	    C = createCircleWithRadius(CM.getCenter(),
		    CM.getRadius() + Cm.getRadius());
	    tmp.addAll(createCirclePLC(Cm.getCenter(), L1s, C));
	    tmp.addAll(createCirclePLC(Cm.getCenter(), L2s, C));
	}
	for (final WB_Circle circle : tmp) {
	    final WB_Circle newC = createCircleWithRadius(circle.getCenter(),
		    WB_GeometryOp.distanceToLine2D(circle.getCenter(), L));
	    if (newC.isTangent(C1) && newC.isTangent(C2)) {
		result.add(newC);
	    }
	}
	final List<WB_Circle> filter = new ArrayList<WB_Circle>();
	for (int i = 0; i < result.size(); i++) {
	    if ((!result.get(i).equals(C1)) && (!result.get(i).equals(C2))) {
		filter.add(result.get(i));
	    }
	}
	return uniqueOnly(filter);
    }

    /**
     * 
     *
     * @param C1 
     * @param C2 
     * @param C3 
     * @return 
     */
    public List<WB_Circle> createCircleCCC(WB_Circle C1, WB_Circle C2,
	    WB_Circle C3) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	if (C1.equals(C2) || C2.equals(C3) || C1.equals(C3)) {
	    return result;
	}
	WB_Circle C;
	if (C1.getRadius() > C2.getRadius()) {
	    C = C2;
	    C2 = C1;
	    C1 = C;
	}
	if (C2.getRadius() > C3.getRadius()) {
	    C = C3;
	    C3 = C2;
	    C2 = C;
	}
	if (C1.getRadius() > C2.getRadius()) {
	    C = C2;
	    C2 = C1;
	    C1 = C;
	}
	// if (C1.getCenter().isCollinear(C2.getCenter(), C3.getCenter())) {
	final List<WB_Circle> circles = new ArrayList<WB_Circle>();
	if ((C1.getRadius() == C2.getRadius())
		&& (C2.getRadius() == C3.getRadius())) {
	    final double R = C1.getRadius();
	    circles.add(createCirclePPP(C1.getCenter(), C2.getCenter(),
		    C3.getCenter()));
	    circles.addAll(createCirclePPC(C1.getCenter(), C2.getCenter(),
		    createCircleWithRadius(C3.getCenter(), 2 * R)));
	    circles.addAll(createCirclePPC(C1.getCenter(), C3.getCenter(),
		    createCircleWithRadius(C2.getCenter(), 2 * R)));
	    circles.addAll(createCirclePPC(C3.getCenter(), C2.getCenter(),
		    createCircleWithRadius(C1.getCenter(), 2 * R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	} else if (C1.getRadius() == C2.getRadius()) {
	    final double R = C1.getRadius();
	    circles.addAll(createCirclePPC(C1.getCenter(), C2.getCenter(),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePPC(C1.getCenter(), C2.getCenter(),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), 2 * R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), 2 * R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	} else if (C2.getRadius() == C3.getRadius()) {
	    double R = C1.getRadius();
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() - R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() - R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	    R = C2.getRadius();
	    circles.addAll(createCirclePCC(C2.getCenter(),
		    createCircleWithRadius(C1.getCenter(), C1.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C3.getCenter(),
		    createCircleWithRadius(C1.getCenter(), C1.getRadius() + R),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() + R)));
	    circles.addAll(createCirclePPC(C2.getCenter(), C3.getCenter(),
		    createCircleWithRadius(C1.getCenter(), C1.getRadius() + R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	} else {
	    double R = C1.getRadius();
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() - R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C1.getCenter(),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() - R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	    R = C2.getRadius();
	    circles.addAll(createCirclePCC(C2.getCenter(),
		    createCircleWithRadius(C1.getCenter(), C1.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() + R)));
	    circles.addAll(createCirclePCC(C2.getCenter(),
		    createCircleWithRadius(C1.getCenter(), C1.getRadius() + R),
		    createCircleWithRadius(C3.getCenter(), C3.getRadius() - R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	    R = C3.getRadius();
	    circles.addAll(createCirclePCC(C3.getCenter(),
		    createCircleWithRadius(C1.getCenter(), C1.getRadius() + R),
		    createCircleWithRadius(C2.getCenter(), C2.getRadius() + R)));
	    for (final WB_Circle circle : circles) {
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() + R));
		result.add(createCircleWithRadius(circle.getCenter(),
			circle.getRadius() - R));
	    }
	}
	final List<WB_Circle> filter = new ArrayList<WB_Circle>();
	for (int i = 0; i < result.size(); i++) {
	    C = result.get(i);
	    if ((!C.equals(C1)) && (!C.equals(C2)) && (!C.equals(C3))) {
		if (C.isTangent(C1) && C.isTangent(C2) && C.isTangent(C3)) {
		    filter.add(result.get(i));
		}
	    }
	}
	return uniqueOnly(filter);
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    private double[] toDouble(final WB_Coordinate p) {
	return new double[] { p.xd(), p.yd(), 0 };
    }

    /**
     * 
     *
     * @param circles 
     * @return 
     */
    private List<WB_Circle> uniqueOnly(final List<WB_Circle> circles) {
	final List<WB_Circle> uniqcircles = new ArrayList<WB_Circle>();
	for (int i = 0; i < circles.size(); i++) {
	    boolean uniq = true;
	    for (int j = 0; j < uniqcircles.size(); j++) {
		if (circles.get(i).equals(uniqcircles.get(j))) {
		    uniq = false;
		    break;
		}
	    }
	    if (uniq) {
		uniqcircles.add(circles.get(i));
	    }
	}
	return uniqcircles;
    }

    /**
     * Gets the circles with given radius through 2 points.
     *
     * @param p0
     *
     * @param p1
     *
     * @param r
     *            radius
     * @return circles with given radius through 2 points
     */
    public List<WB_Circle> createCircleThrough2Points(final WB_Coordinate p0,
	    final WB_Coordinate p1, final double r) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	final WB_Circle C0 = createCircleWithRadius(p0, r);
	final WB_Circle C1 = createCircleWithRadius(p1, r);
	final List<WB_Point> intersection = createIntersectionPoints(C0, C1);
	for (int i = 0; i < intersection.size(); i++) {
	    result.add(createCircleWithRadius(intersection.get(i), r));
	}
	return result;
    }

    /**
     * Gets circles with given radius tangent to 2D line through 2D point.
     *
     * @param L
     *            line
     * @param p
     *            point
     * @param r
     *            radius
     * @return circles with given radius tangent to line through point
     */
    public List<WB_Circle> createCircleTangentToLineThroughPoint(
	    final WB_Line L, final WB_Coordinate p, final double r) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>();
	double cPrime = L.c() + (L.a() * p.xd()) + (L.b() * p.yd());
	if (WB_Epsilon.isZero(cPrime)) {
	    result.add(createCircleWithRadius(
		    createPoint(p.xd() + (r * L.a()), p.yd() + (r * L.b())), r));
	    result.add(createCircleWithRadius(
		    createPoint(p.xd() - (r * L.a()), p.yd() - (r * L.b())), r));
	    return result;
	}
	double a, b;
	if (cPrime < 0) {
	    a = -L.a();
	    b = -L.b();
	    cPrime *= -1;
	} else {
	    a = L.a();
	    b = L.b();
	}
	final double tmp1 = cPrime - r;
	double tmp2 = (r * r) - (tmp1 * tmp1);
	if (WB_Epsilon.isZero(tmp2)) {
	    result.add(createCircleWithRadius(
		    createPoint(p.xd() - (tmp1 * a), p.yd() - (tmp1 * b)), r));
	    return result;
	} else if (tmp2 < 0) {
	    return result;
	} else {
	    tmp2 = Math.sqrt(tmp2);
	    final WB_Point tmpp = createPoint(p.xd() - (a * tmp1), p.yd()
		    - (b * tmp1));
	    result.add(createCircleWithRadius(
		    createPoint(tmpp.xd() + (tmp2 * b), tmpp.yd() - (tmp2 * a)),
		    r));
	    result.add(createCircleWithRadius(
		    createPoint(tmpp.xd() - (tmp2 * b), tmpp.yd() + (tmp2 * a)),
		    r));
	    return result;
	}
    }

    /**
     * Gets circles with given radius tangent to 2 2D lines.
     *
     * @param L0
     *            line
     * @param L1
     *            line
     * @param r
     *            radius
     * @return circles with radius tangent to 2 2D lines
     */
    public List<WB_Circle> createCircleTangentTo2Lines(final WB_Line L0,
	    final WB_Line L1, final double r) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>(4);
	final double discrm0 = r;
	final double discrm1 = r;
	final double invDenom = 1.0 / ((-L1.a() * L0.b()) + (L0.a() * L1.b()));
	double cx = -((L1.b() * (L0.c() + discrm0)) - (L0.b() * (L1.c() + discrm1)))
		* invDenom;
	double cy = +((L1.a() * (L0.c() + discrm0)) - (L0.a() * (L1.c() + discrm1)))
		* invDenom;
	result.add(createCircleWithRadius(createPoint(cx, cy), r));
	cx = -((L1.b() * (L0.c() + discrm0)) - (L0.b() * (L1.c() - discrm1)))
		* invDenom;
	cy = +((L1.a() * (L0.c() + discrm0)) - (L0.a() * (L1.c() - discrm1)))
		* invDenom;
	result.add(createCircleWithRadius(createPoint(cx, cy), r));
	cx = -((L1.b() * (L0.c() - discrm0)) - (L0.b() * (L1.c() + discrm1)))
		* invDenom;
	cy = +((L1.a() * (L0.c() - discrm0)) - (L0.a() * (L1.c() + discrm1)))
		* invDenom;
	result.add(createCircleWithRadius(createPoint(cx, cy), r));
	cx = -((L1.b() * (L0.c() - discrm0)) - (L0.b() * (L1.c() - discrm1)))
		* invDenom;
	cy = +((L1.a() * (L0.c() - discrm0)) - (L0.a() * (L1.c() - discrm1)))
		* invDenom;
	result.add(createCircleWithRadius(createPoint(cx, cy), r));
	return result;
    }

    /**
     * Gets circles with given radius through 2D point and tangent to circle.
     *
     * @param p
     *            point
     * @param C
     *            circle
     * @param r
     *            radius
     * @return circles with given radius through point and tangent to circle
     */
    public List<WB_Circle> createCircleThroughPointTangentToCircle(
	    final WB_Coordinate p, final WB_Circle C, final double r) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>(4);
	final double dcp = createPoint(p).getDistance3D(C.getCenter());
	if (dcp > (C.getRadius() + (2 * r))) {
	    return result;
	} else if (dcp < (C.getRadius() - (2 * r))) {
	    return result;
	} else {
	    final WB_Circle ctmp1 = createCircleWithRadius(p, r);
	    WB_Circle ctmp2 = createCircleWithRadius(C.getCenter(),
		    r + C.getRadius());
	    List<WB_Point> intersection = createIntersectionPoints(ctmp1, ctmp2);
	    for (int i = 0; i < intersection.size(); i++) {
		result.add(createCircleWithRadius(intersection.get(i), r));
	    }
	    ctmp2 = createCircleWithRadius(C.getCenter(),
		    WB_Math.fastAbs(r - C.getRadius()));
	    intersection = createIntersectionPoints(ctmp1, ctmp2);
	    for (int i = 0; i < intersection.size(); i++) {
		result.add(createCircleWithRadius(intersection.get(i), r));
	    }
	}
	return result;
    }

    /**
     * Gets the circle tangent to line and circle.
     *
     * @param L
     *            the l
     * @param C
     *            the c
     * @param r
     *            the r
     * @return the circle tangent to line and circle
     */
    public List<WB_Circle> createCircleTangentToLineAndCircle(final WB_Line L,
	    final WB_Circle C, final double r) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>(8);
	final double d = WB_GeometryOp.distanceToLine2D(C.getCenter(), L);
	if (d > ((2 * r) + C.getRadius())) {
	    return result;
	}
	final WB_Line L1 = createLineWithDirection(
		L.getOrigin().addMul(
			r,
			createVector(L.getDirection().yd(), -L.getDirection()
				.xd())), L.getDirection());
	final WB_Line L2 = createLineWithDirection(
		L.getOrigin().addMul(
			r,
			createVector(-L.getDirection().yd(), +L.getDirection()
				.xd())), L.getDirection());
	final WB_Circle C1 = createCircleWithRadius(C.getCenter(),
		C.getRadius() + r);
	final WB_Circle C2 = createCircleWithRadius(C.getCenter(),
		WB_Math.fastAbs(C.getRadius() - r));
	final List<WB_Coordinate> intersections = new ArrayList<WB_Coordinate>();
	intersections.addAll(createIntersectionPoints(L1, C1));
	intersections.addAll(createIntersectionPoints(L1, C2));
	intersections.addAll(createIntersectionPoints(L2, C1));
	intersections.addAll(createIntersectionPoints(L2, C2));
	for (int i = 0; i < intersections.size(); i++) {
	    result.add(createCircleWithRadius(intersections.get(i), r));
	}
	return result;
    }

    /**
     * Gets circles with given radius tangent to two circles. This will return
     * all tangent circles with a certaun radius whose center are non-collinear
     * with the the two centers.
     *
     * @param C0
     *
     * @param C1
     *
     * @param r
     *            radius
     * @return non-collinear circles with given radius tangent to two circles
     */
    public List<WB_Circle> createNonCollinearCircleTangentTo2Circles(
	    final WB_Circle C0, final WB_Circle C1, final double r) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>(2);
	final double d = C0.getCenter().getDistance3D(C1.getCenter());
	if (WB_Epsilon.isZero(d)) {
	    return result;
	}
	final WB_Circle C0r = createCircleWithRadius(C0.getCenter(),
		C0.getRadius() + r);
	final WB_Circle C1r = createCircleWithRadius(C1.getCenter(),
		C1.getRadius() + r);
	final List<WB_Point> intersections = createIntersectionPoints(C0r, C1r);
	if (intersections.size() < 2) {
	    return result;
	}
	for (int i = 0; i < intersections.size(); i++) {
	    result.add(createCircleWithRadius(intersections.get(i), r));
	}
	final Iterator<WB_Circle> itr = result.iterator();
	WB_Circle C;
	while (itr.hasNext()) {
	    C = itr.next();
	    if (C.equals(C0) || C.equals(C1)) {
		itr.remove();
	    }
	}
	return result;
    }

    /**
     * Gets circles tangent to two circles. This will return all tangent circles
     * whose center are collinear with the the two centers.
     *
     * @param C0
     *
     * @param C1
     *
     * @return collinear circles tangent to two circles
     */
    public List<WB_Circle> createCollinearCircleTangentTo2Circles(
	    final WB_Circle C0, final WB_Circle C1) {
	final List<WB_Circle> result = new ArrayList<WB_Circle>(2);
	final double d = C0.getCenter().getDistance3D(C1.getCenter());
	if (WB_Epsilon.isZero(d)) {
	    return result;
	}
	double r = 0.5 * (d + C0.getRadius() + C1.getRadius());
	double f = (r - C0.getRadius())
		/ ((2 * r) - C0.getRadius() - C1.getRadius());
	if (!WB_Epsilon.isZero(r)) {
	    result.add(createCircleWithRadius(
		    createInterpolatedPoint(C0.getCenter(), C1.getCenter(), f),
		    WB_Math.fastAbs(r)));
	}
	r = 0.5 * ((d + C0.getRadius()) - C1.getRadius());
	f = (r - C0.getRadius()) / d;
	if (!WB_Epsilon.isZero(r)) {
	    result.add(createCircleWithRadius(
		    createInterpolatedPoint(C0.getCenter(), C1.getCenter(), f),
		    WB_Math.fastAbs(r)));
	}
	r = 0.5 * ((d + C1.getRadius()) - C0.getRadius());
	f = (r - C1.getRadius()) / d;
	if (!WB_Epsilon.isZero(r)) {
	    result.add(createCircleWithRadius(
		    createInterpolatedPoint(C1.getCenter(), C0.getCenter(), f),
		    WB_Math.fastAbs(r)));
	}
	r = 0.5 * (d - C1.getRadius() - C0.getRadius());
	f = (r + C0.getRadius()) / d;
	if (!WB_Epsilon.isZero(r)) {
	    result.add(createCircleWithRadius(
		    createInterpolatedPoint(C0.getCenter(), C1.getCenter(), f),
		    WB_Math.fastAbs(r)));
	}
	final Iterator<WB_Circle> itr = result.iterator();
	WB_Circle C;
	while (itr.hasNext()) {
	    C = itr.next();
	    if (C.equals(C0) || C.equals(C1)) {
		itr.remove();
	    }
	}
	return result;
    }

    /**
     * Get plane through point on plane with normal direction.
     *
     * @param origin            point on plane
     * @param normal 
     * @return plane
     */
    public WB_Plane createPlane(final WB_Coordinate origin,
	    final WB_Coordinate normal) {
	return new WB_Plane(origin, normal);
    }

    /**
     * Get plane through point on plane with normal direction.
     *
     * @param ox            x-ordinate of point on plane
     * @param oy            y-ordinate of point on plane
     * @param oz            z-ordinate of point on plane
     * @param nx 
     * @param ny 
     * @param nz 
     * @return plane
     */
    public WB_Plane createPlane(final double ox, final double oy,
	    final double oz, final double nx, final double ny, final double nz) {
	return new WB_Plane(createPoint(ox, oy, oz), createVector(nx, ny, nz));
    }

    /**
     * Get plane through 3 points.
     *
     * @param p1            point on plane
     * @param p2            point on plane
     * @param p3            point on plane
     * @return plane
     */
    public WB_Plane createPlane(final WB_Coordinate p1, final WB_Coordinate p2,
	    final WB_Coordinate p3) {
	final WB_Vector v21 = createVectorFromTo(p1, p2);
	final WB_Vector v31 = createVectorFromTo(p1, p3);
	return new WB_Plane(p1, v21.crossSelf(v31));
    }

    /**
     * 
     *
     * @param T 
     * @return 
     */
    public WB_Plane createPlane(final WB_Triangle T) {
	return new WB_Plane(T.p1(), T.p2(), T.p3());
    }

    /**
     * 
     *
     * @param P 
     * @return 
     */
    public WB_Plane createFlippedPlane(final WB_Plane P) {
	return new WB_Plane(P.getOrigin(), P.getNormal().mul(-1));
    }

    /**
     * Get plane through point on plane with normal direction.
     *
     * @param origin            point on plane
     * @param normal 
     * @param offset            offset
     * @return plane
     */
    public WB_Plane createOffsetPlane(final WB_Coordinate origin,
	    final WB_Coordinate normal, final double offset) {
	return new WB_Plane(createPoint(origin).addMulSelf(offset, normal),
		normal);
    }

    /**
     * Get plane through point on plane with normal direction.
     *
     * @param ox            x-ordinate of point on plane
     * @param oy            y-ordinate of point on plane
     * @param oz            z-ordinate of point on plane
     * @param nx 
     * @param ny 
     * @param nz 
     * @param offset            offset
     * @return plane
     */
    public WB_Plane createOffsetPlane(final double ox, final double oy,
	    final double oz, final double nx, final double ny, final double nz,
	    final double offset) {
	return new WB_Plane(createPoint(ox + (offset * nx), oy + (offset * ny),
		oz + (offset * nz)), createVector(nx, ny, nz));
    }

    /**
     * Get offset plane through 3 points.
     *
     * @param p1            point on plane
     * @param p2            point on plane
     * @param p3            point on plane
     * @param offset            offset
     * @return plane
     */
    public WB_Plane createOffsetPlane(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3, final double offset) {
	final WB_Vector v21 = createVectorFromTo(p1, p2);
	final WB_Vector v31 = createVectorFromTo(p1, p3);
	final WB_Vector n = v21.crossSelf(v31);
	n.normalizeSelf();
	return new WB_Plane(createPoint(p1).addMulSelf(offset, n), n);
    }

    /**
     * 
     *
     * @param points 
     * @param faces 
     * @return 
     */
    public WB_FaceListMesh createMesh(final WB_Coordinate[] points,
	    final int[][] faces) {
	return new WB_FaceListMesh(points, faces);
    }

    /**
     * 
     *
     * @param points 
     * @param faces 
     * @return 
     */
    public WB_FaceListMesh createMesh(
	    final Collection<? extends WB_Coordinate> points,
	    final int[][] faces) {
	return new WB_FaceListMesh(points, faces);
    }

    /**
     * 
     *
     * @param ordinates 
     * @param faces 
     * @return 
     */
    public WB_FaceListMesh createMesh(final double[] ordinates,
	    final int[][] faces) {
	return new WB_FaceListMesh(ordinates, faces);
    }

    /**
     * 
     *
     * @param aabb 
     * @return 
     */
    public WB_FaceListMesh createMesh(final WB_AABB aabb) {
	return createMesh(aabb.getPoints(), aabb.getFaces());
    }

    /**
     * 
     *
     * @param mesh 
     * @return 
     */
    public WB_FaceListMesh createUniqueMesh(final WB_FaceListMesh mesh) {
	final List<WB_SequencePoint> uniqueVertices = new FastTable<WB_SequencePoint>();
	final TIntIntMap oldnew = new TIntIntHashMap(10, 0.5f, -1, -1);
	final WB_KDTree<WB_SequencePoint, Integer> kdtree = new WB_KDTree<WB_SequencePoint, Integer>();
	WB_KDEntry<WB_SequencePoint, Integer> neighbor;
	WB_SequencePoint v = mesh.getVertex(0);
	kdtree.add(v, 0);
	uniqueVertices.add(v);
	oldnew.put(0, 0);
	int nuv = 1;
	for (int i = 1; i < mesh.getNumberOfVertices(); i++) {
	    v = mesh.getVertex(i);
	    neighbor = kdtree.getNearestNeighbor(v);
	    if (neighbor.d2 < WB_Epsilon.SQEPSILON) {
		oldnew.put(i, neighbor.value);
	    } else {
		kdtree.add(v, nuv);
		uniqueVertices.add(v);
		oldnew.put(i, nuv++);
	    }
	}
	final int[][] newfaces = new int[mesh.getNumberOfFaces()][];
	for (int i = 0; i < mesh.getNumberOfFaces(); i++) {
	    final int[] face = mesh.getFace(i);
	    newfaces[i] = new int[face.length];
	    for (int j = 0; j < face.length; j++) {
		newfaces[i][j] = oldnew.get(face[j]);
	    }
	}
	return createMesh(uniqueVertices, newfaces);
    }

    /**
     * 
     *
     * @param mesh 
     * @param threshold 
     * @return 
     */
    public WB_FaceListMesh createUniqueMesh(final WB_FaceListMesh mesh,
	    final double threshold) {
	final List<WB_SequencePoint> uniqueVertices = new FastTable<WB_SequencePoint>();
	final TIntIntMap oldnew = new TIntIntHashMap(10, 0.5f, -1, -1);
	final WB_KDTree<WB_SequencePoint, Integer> kdtree = new WB_KDTree<WB_SequencePoint, Integer>();
	final double t2 = threshold * threshold;
	WB_KDEntry<WB_SequencePoint, Integer> neighbor;
	WB_SequencePoint v = mesh.getVertex(0);
	kdtree.add(v, 0);
	uniqueVertices.add(v);
	oldnew.put(0, 0);
	int nuv = 1;
	for (int i = 1; i < mesh.getNumberOfVertices(); i++) {
	    v = mesh.getVertex(i);
	    neighbor = kdtree.getNearestNeighbor(v);
	    if (neighbor.d2 < t2) {
		oldnew.put(i, neighbor.value);
	    } else {
		kdtree.add(v, nuv);
		uniqueVertices.add(v);
		oldnew.put(i, nuv++);
	    }
	}
	final int[][] newfaces = new int[mesh.getNumberOfFaces()][];
	for (int i = 0; i < mesh.getNumberOfFaces(); i++) {
	    final int[] face = mesh.getFace(i);
	    newfaces[i] = new int[face.length];
	    for (int j = 0; j < face.length; j++) {
		newfaces[i][j] = oldnew.get(face[j]);
	    }
	}
	return createMesh(uniqueVertices, newfaces);
    }

    /**
     * 
     *
     * @param points 
     * @param faces 
     * @return 
     */
    public WB_FaceListMesh createTriMesh(final WB_Coordinate[] points,
	    final int[][] faces) {
	return new WB_TriangleMesh(points, faces);
    }

    /**
     * 
     *
     * @param points 
     * @param faces 
     * @return 
     */
    public WB_FaceListMesh createTriMesh(
	    final Collection<? extends WB_Coordinate> points,
	    final int[][] faces) {
	return new WB_TriangleMesh(points, faces);
    }

    /**
     * 
     *
     * @param mesh 
     * @return 
     */
    public WB_FaceListMesh createTriMesh(final WB_FaceListMesh mesh) {
	return new WB_TriangleMesh(mesh);
    }

    /**
     * 
     *
     * @param n 
     * @param radius 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createRegularPrism(final int n, final double radius,
	    final double h) {
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	for (int i = 0; i < n; i++) {
	    lpoints.add(createPoint(
		    radius * Math.cos(((Math.PI * 2.0) / n) * i),
		    radius * Math.sin(((Math.PI * 2.0) / n) * i), 0));
	    lpoints.add(createPoint(
		    radius * Math.cos(((Math.PI * 2.0) / n) * i),
		    radius * Math.sin(((Math.PI * 2.0) / n) * i), h));
	}
	return createMesh(lpoints, createPrismFaces(n));
    }

    /**
     * 
     *
     * @param points 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createPrism(
	    final Collection<? extends WB_Coordinate> points, final double h) {
	final WB_Vector offset = createVector(0, 0, h);
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	for (final WB_Coordinate point : points) {
	    lpoints.add(createPoint(point));
	    lpoints.add(createPoint(point).addSelf(offset));
	}
	return createMesh(lpoints, createPrismFaces(points.size()));
    }

    /**
     * 
     *
     * @param points 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createPrism(final WB_Coordinate[] points,
	    final double h) {
	final WB_Vector offset = createVector(0, 0, h);
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	for (final WB_Coordinate point : points) {
	    lpoints.add(createPoint(point));
	    lpoints.add(createPoint(point).addSelf(offset));
	}
	return createMesh(lpoints, createPrismFaces(points.length));
    }

    /**
     * 
     *
     * @param n 
     * @return 
     */
    private int[][] createPrismFaces(final int n) {
	final int[][] faces = new int[2 + n][];
	faces[n] = new int[n];
	faces[n + 1] = new int[n];
	for (int i = 0; i < n; i++) {
	    faces[n][n - i - 1] = 2 * i;
	    faces[n + 1][i] = (2 * i) + 1;
	}
	for (int i = 0; i < n; i++) {
	    faces[i] = new int[4];
	    faces[i][0] = 2 * i;
	    faces[i][1] = 2 * ((i + 1) % n);
	    faces[i][2] = (2 * ((i + 1) % n)) + 1;
	    faces[i][3] = (2 * i) + 1;
	}
	return faces;
    }

    /**
     * 
     *
     * @param poly 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createPrism(final WB_Polygon poly, final double h) {
	final WB_Vector offset = createVector(0, 0, h);
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Coordinate point;
	for (int i = 0; i < poly.getNumberOfPoints(); i++) {
	    point = poly.getPoint(i);
	    lpoints.add(createPoint(point));
	    lpoints.add(createPoint(point).addSelf(offset));
	}
	final int numfaces = poly.getNumberOfPoints();
	final int[][] triangles = poly.getTriangles();
	final int[][] prismfaces = new int[(2 * triangles.length) + numfaces][];
	int index = 0;
	for (final int[] triangle : triangles) {
	    prismfaces[index] = new int[3];
	    prismfaces[index][0] = 2 * triangle[0];
	    prismfaces[index][1] = 2 * triangle[2];
	    prismfaces[index][2] = 2 * triangle[1];
	    index++;
	    prismfaces[index] = new int[3];
	    prismfaces[index][0] = (2 * triangle[0]) + 1;
	    prismfaces[index][1] = (2 * triangle[1]) + 1;
	    prismfaces[index][2] = (2 * triangle[2]) + 1;
	    index++;
	}
	final int[] npc = poly.getNumberOfPointsPerContour();
	int start = 0;
	for (int j = 0; j < poly.getNumberOfContours(); j++) {
	    final int n = npc[j];
	    for (int i = 0; i < n; i++) {
		prismfaces[index] = new int[4];
		prismfaces[index][0] = 2 * (start + i);
		prismfaces[index][1] = 2 * (start + ((i + 1) % n));
		prismfaces[index][2] = (2 * (start + ((i + 1) % n))) + 1;
		prismfaces[index][3] = (2 * (start + i)) + 1;
		index++;
	    }
	    start += n;
	}
	return createMesh(lpoints, prismfaces);
    }

    /**
     * 
     *
     * @param n 
     * @param radius 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createRegularAntiPrism(final int n,
	    final double radius, final double h) {
	final List<WB_Point> points = new FastTable<WB_Point>();
	for (int i = 0; i < n; i++) {
	    points.add(createPoint(
		    radius * Math.cos(((Math.PI * 2.0) / n) * i),
		    radius * Math.sin(((Math.PI * 2.0) / n) * i), 0));
	    points.add(createPoint(
		    radius * Math.cos(((Math.PI * 2.0) / n) * (i + 0.5)),
		    radius * Math.sin(((Math.PI * 2.0) / n) * (i + 0.5)), h));
	}
	return createMesh(points, createAntiprismFaces(n));
    }

    /**
     * 
     *
     * @param points 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createAntiPrism(
	    final Collection<? extends WB_Coordinate> points, final double h) {
	final WB_Vector offset = createVector(0, 0, h);
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	for (final WB_Coordinate point : points) {
	    lpoints.add(createPoint(point));
	    lpoints.add(createPoint(point).addSelf(offset));
	}
	return createMesh(lpoints, createAntiprismFaces(points.size()));
    }

    /**
     * 
     *
     * @param points 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createAntiPrism(final WB_Coordinate[] points,
	    final double h) {
	final WB_Vector offset = createVector(0, 0, h);
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	for (final WB_Coordinate point : points) {
	    lpoints.add(createPoint(point));
	    lpoints.add(createPoint(point).addSelf(offset));
	}
	return createMesh(lpoints, createAntiprismFaces(points.length));
    }

    /**
     * 
     *
     * @param n 
     * @return 
     */
    private int[][] createAntiprismFaces(final int n) {
	final int[][] faces = new int[2 + (2 * n)][];
	faces[2 * n] = new int[n];
	faces[(2 * n) + 1] = new int[n];
	for (int i = 0; i < n; i++) {
	    faces[2 * n][n - i - 1] = 2 * i;
	    faces[(2 * n) + 1][i] = (2 * i) + 1;
	}
	for (int i = 0; i < n; i++) {
	    faces[2 * i] = new int[3];
	    faces[2 * i][0] = 2 * i;
	    faces[2 * i][1] = 2 * ((i + 1) % n);
	    faces[2 * i][2] = (2 * i) + 1;
	    faces[(2 * i) + 1] = new int[3];
	    faces[(2 * i) + 1][0] = (2 * i) + 1;
	    faces[(2 * i) + 1][1] = 2 * ((i + 1) % n);
	    faces[(2 * i) + 1][2] = (2 * ((i + 1) % n)) + 1;
	}
	return faces;
    }

    /**
     * 
     *
     * @param poly 
     * @param h 
     * @return 
     */
    public WB_FaceListMesh createAntiPrism(final WB_Polygon poly, final double h) {
	final WB_Vector offset = createVector(0, 0, h);
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Coordinate point;
	for (int i = 0; i < poly.getNumberOfPoints(); i++) {
	    point = poly.getPoint(i);
	    lpoints.add(createPoint(point));
	    lpoints.add(createPoint(point).addSelf(offset));
	}
	final int numfaces = poly.getNumberOfPoints();
	final int[][] triangles = poly.getTriangles();
	final int[][] prismfaces = new int[(2 * triangles.length)
		+ (2 * numfaces)][];
	int index = 0;
	for (final int[] triangle : triangles) {
	    prismfaces[index] = new int[3];
	    prismfaces[index][0] = 2 * triangle[0];
	    prismfaces[index][1] = 2 * triangle[2];
	    prismfaces[index][2] = 2 * triangle[1];
	    index++;
	    prismfaces[index] = new int[3];
	    prismfaces[index][0] = (2 * triangle[0]) + 1;
	    prismfaces[index][1] = (2 * triangle[1]) + 1;
	    prismfaces[index][2] = (2 * triangle[2]) + 1;
	    index++;
	}
	final int[] npc = poly.getNumberOfPointsPerContour();
	int start = 0;
	for (int j = 0; j < poly.getNumberOfContours(); j++) {
	    final int n = npc[j];
	    for (int i = 0; i < n; i++) {
		prismfaces[index] = new int[3];
		prismfaces[index][0] = 2 * (start + i);
		prismfaces[index][1] = 2 * (start + ((i + 1) % n));
		prismfaces[index][2] = (2 * (start + i)) + 1;
		index++;
		prismfaces[index] = new int[3];
		prismfaces[index][0] = (2 * (start + i)) + 1;
		prismfaces[index][1] = 2 * (start + ((i + 1) % n));
		prismfaces[index][2] = (2 * (start + ((i + 1) % n))) + 1;
		index++;
	    }
	    start += n;
	}
	return createMesh(lpoints, prismfaces);
    }

    /**
     * 
     *
     * @param type 
     * @param edgeLength 
     * @return 
     */
    public WB_FaceListMesh createArchimedes(final int type,
	    final double edgeLength) {
	final List<WB_Point> vertices;
	final int[][] faces;
	vertices = createVerticesFromArray(WB_PolyhedraData.Avertices[type]);
	faces = WB_PolyhedraData.Afaces[type];
	final WB_Point p0 = vertices.get(faces[0][0]);
	final WB_Point p1 = vertices.get(faces[0][1]);
	final double el = p0.getDistance3D(p1);
	final double scale = edgeLength / el;
	final WB_Point cog = createPoint();
	for (final WB_Point p : vertices) {
	    p.mulSelf(scale);
	    cog.addSelf(p);
	}
	cog.div(vertices.size());
	for (final WB_Point p : vertices) {
	    p.subSelf(cog);
	}
	return createMesh(vertices, faces);
    }

    /**
     * 
     *
     * @param type 
     * @param edgeLength 
     * @return 
     */
    public WB_FaceListMesh createCatalan(final int type, final double edgeLength) {
	final List<WB_Point> vertices;
	final int[][] faces;
	vertices = createVerticesFromArray(WB_PolyhedraData.Cvertices[type]);
	faces = WB_PolyhedraData.Cfaces[type];
	final WB_Point p0 = vertices.get(faces[0][0]);
	final WB_Point p1 = vertices.get(faces[0][1]);
	final double el = p0.getDistance3D(p1);
	final double scale = edgeLength / el;
	final WB_Point cog = createPoint();
	for (final WB_Point p : vertices) {
	    p.mulSelf(scale);
	    cog.addSelf(p);
	}
	cog.div(vertices.size());
	for (final WB_Point p : vertices) {
	    p.subSelf(cog);
	}
	return createMesh(vertices, faces);
    }

    /**
     * 
     *
     * @param vertices 
     * @return 
     */
    private List<WB_Point> createVerticesFromArray(final double[][] vertices) {
	final List<WB_Point> points = new FastTable<WB_Point>();
	for (final double[] vertice : vertices) {
	    points.add(createPoint(vertice[0], vertice[1], vertice[2]));
	}
	return points;
    }

    /**
     * Johnson polyhedra.
     * 
     * Implemented by Frederik Vanhoutte (W:Blut), painstakingly collected by
     * David Marec. Many thanks, without David this wouldn't be here.
     *
     * @param type 
     * @param edgeLength 
     * @return 
     */
    public WB_FaceListMesh createJohnson(final int type, final double edgeLength) {
	final List<WB_Point> vertices;
	final int[][] faces;
	if (type < 23) {
	    vertices = createVerticesFromArray(WB_JohnsonPolyhedraData01.vertices[type]);
	    faces = WB_JohnsonPolyhedraData01.faces[type];
	} else if (type < 46) {
	    vertices = createVerticesFromArray(WB_JohnsonPolyhedraData02.vertices[type - 23]);
	    faces = WB_JohnsonPolyhedraData02.faces[type - 23];
	} else if (type < 70) {
	    vertices = createVerticesFromArray(WB_JohnsonPolyhedraData03.vertices[type - 46]);
	    faces = WB_JohnsonPolyhedraData03.faces[type - 46];
	} else {
	    vertices = createVerticesFromArray(WB_JohnsonPolyhedraData04.vertices[type - 70]);
	    faces = WB_JohnsonPolyhedraData04.faces[type - 70];
	}
	final WB_Point p0 = vertices.get(faces[0][0]);
	final WB_Point p1 = vertices.get(faces[0][1]);
	final double el = p0.getDistance3D(p1);
	final double scale = edgeLength / el;
	final WB_Point cog = createPoint();
	for (final WB_Point p : vertices) {
	    p.mulSelf(scale);
	    cog.addSelf(p);
	}
	cog.div(vertices.size());
	for (final WB_Point p : vertices) {
	    p.subSelf(cog);
	}
	return createMesh(vertices, faces);
    }

    /**
     * 
     *
     * @param type 
     * @param edgeLength 
     * @return 
     */
    public WB_FaceListMesh createOtherPolyhedron(final int type,
	    final double edgeLength) {
	final List<WB_Point> vertices;
	final int[][] faces;
	vertices = createVerticesFromArray(WB_PolyhedraData.Overtices[type]);
	faces = WB_PolyhedraData.Ofaces[type];
	final WB_Point p0 = vertices.get(faces[0][0]);
	final WB_Point p1 = vertices.get(faces[0][1]);
	final double el = p0.getDistance3D(p1);
	final double scale = edgeLength / el;
	final WB_Point cog = createPoint();
	for (final WB_Point p : vertices) {
	    p.mulSelf(scale);
	    cog.addSelf(p);
	}
	cog.div(vertices.size());
	for (final WB_Point p : vertices) {
	    p.subSelf(cog);
	}
	return createMesh(vertices, faces);
    }

    /**
     * 
     *
     * @param type 
     * @param edgeLength 
     * @return 
     */
    public WB_FaceListMesh createPlato(final int type, final double edgeLength) {
	final List<WB_Point> vertices;
	final int[][] faces;
	vertices = createVerticesFromArray(WB_PolyhedraData.Pvertices[type]);
	faces = WB_PolyhedraData.Pfaces[type];
	final WB_Point p0 = vertices.get(faces[0][0]);
	final WB_Point p1 = vertices.get(faces[0][1]);
	final double el = p0.getDistance3D(p1);
	final double scale = edgeLength / el;
	final WB_Point cog = createPoint();
	for (final WB_Point p : vertices) {
	    p.mulSelf(scale);
	    cog.addSelf(p);
	}
	cog.div(vertices.size());
	for (final WB_Point p : vertices) {
	    p.subSelf(cog);
	}
	return createMesh(vertices, faces);
    }

    /**
     * 
     *
     * @param name 
     * @param radius 
     * @return 
     */
    public WB_FaceListMesh createPolyhedron(String name, final double radius) {
	final BufferedReader br = new BufferedReader(new InputStreamReader(this
		.getClass().getClassLoader()
		.getResourceAsStream("resources/" + name + ".wrl")));
	final List<WB_Point> points = new FastTable<WB_Point>();
	final List<int[]> faces = new FastTable<int[]>();
	String line;
	String[] words;
	try {
	    while ((line = br.readLine()) != null) {
		if (line.contains("Title Info")) {
		    line = br.readLine().trim();
		    words = line.split("\"");
		    name = words[1].trim();
		}
		if (line.contains("Coordinate3")) {
		    line = br.readLine().trim();
		    line = br.readLine().trim();
		    while (!line.contains("]")) {
			words = line.split("\\s+");
			words[2] = words[2].substring(0, words[2].length() - 1);
			points.add(createPoint(
				Double.parseDouble(words[0].trim()),
				Double.parseDouble(words[1].trim()),
				Double.parseDouble(words[2].trim())));
			line = br.readLine().trim();
		    }
		}
		if (line.contains("IndexedFaceSet")) {
		    line = br.readLine().trim();
		    line = br.readLine().trim();
		    while (!line.contains("]")) {
			words = line.split(",");
			final int[] face = new int[words.length - 1];
			for (int i = 0; i < (words.length - 1); i++) {
			    face[i] = Integer.parseInt(words[i].trim());
			}
			faces.add(face);
			line = br.readLine().trim();
		    }
		}
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	final int[][] ifaces = new int[faces.size()][];
	for (int i = 0; i < faces.size(); i++) {
	    ifaces[i] = faces.get(i);
	}
	double d2 = 0;
	for (final WB_Point p : points) {
	    d2 = Math.max(d2, p.getSqLength3D());
	}
	d2 = radius / Math.sqrt(d2);
	for (final WB_Point p : points) {
	    p.mulSelf(d2);
	}
	return createMesh(points, ifaces);
    }

    /**
     * 
     *
     * @param vectors 
     * @param scale 
     * @return 
     */
    public WB_FaceListMesh createZonohedron(final WB_Coordinate[] vectors,
	    final double scale) {
	final int n = vectors.length;
	if (n < 3) {
	    return null;
	}
	final int nop = (int) Math.pow(2, n);
	final List<WB_Point> points = new FastTable<WB_Point>();
	for (int i = 0; i < nop; i++) {
	    final WB_Point point = createPoint();
	    int div = i;
	    for (int p = 0; p < n; p++) {
		if ((div % 2) == 0) {
		    point.subSelf(vectors[p]);
		} else {
		    point.addSelf(vectors[p]);
		}
		div = div / 2;
	    }
	    point.mulSelf(scale);
	    points.add(point);
	}
	return createConvexHull(points, false);
    }

    /**
     * 
     *
     * @param type 
     * @param radius 
     * @return 
     */
    public WB_FaceListMesh createStellatedIcosahedron(final int type,
	    final double radius) {
	final BufferedReader br = new BufferedReader(
		new InputStreamReader(this
			.getClass()
			.getClassLoader()
			.getResourceAsStream(
				"resources/stellated_icosahedron1-59.txt")));
	final List<WB_Point> points = new ArrayList<WB_Point>();
	final List<int[]> faces = new ArrayList<int[]>();
	String thisline;
	String[] pointindices;
	String[] faceindices;
	String[] coordinates;
	String[] facedata;
	try {
	    thisline = br.readLine();
	    pointindices = thisline.split("\\s+");
	    final int startpoint = Integer.parseInt(pointindices[2 * type]);
	    final int endpoint = Integer.parseInt(pointindices[(2 * type) + 1]);
	    thisline = br.readLine();
	    faceindices = thisline.split("\\s+");
	    final int startface = Integer.parseInt(faceindices[2 * type]);
	    final int endface = Integer.parseInt(faceindices[(2 * type) + 1]);
	    int currentline = 2;
	    while (((thisline = br.readLine()) != null)
		    && (currentline <= endface)) {
		if ((currentline >= startpoint) && (currentline <= endpoint)) {
		    coordinates = thisline.split(",");
		    points.add(createPoint(Double.parseDouble(coordinates[0]),
			    Double.parseDouble(coordinates[1]),
			    Double.parseDouble(coordinates[2])));
		}
		if ((currentline >= startface) && (currentline <= endface)) {
		    facedata = thisline.split(",");
		    final int[] face = new int[facedata.length];
		    for (int i = 0; i < facedata.length; i++) {
			face[i] = Integer.parseInt(facedata[i]);
		    }
		    faces.add(face);
		}
		currentline++;
	    }
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	final int[][] ifaces = new int[faces.size()][];
	for (int i = 0; i < faces.size(); i++) {
	    ifaces[i] = faces.get(i);
	}
	double d2 = 0;
	for (final WB_Point p : points) {
	    d2 = Math.max(d2, p.getSqLength3D());
	}
	d2 = radius / Math.sqrt(d2);
	for (final WB_Point p : points) {
	    p.mulSelf(d2);
	}
	return createMesh(points, ifaces);
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param b 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngles(
	    final Collection<? extends WB_Coordinate> points,
	    final double[] angles, final boolean b, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[b ? n + 1 : n][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param b 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngles(
	    final WB_Coordinate[] points, final double[] angles,
	    final boolean b, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[b ? n + 1 : n][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param b 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngleRange(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final boolean b,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[b ? n + 1 : n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param b 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngleRange(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final boolean b, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[b ? n + 1 : n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngleRange(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle,
	    final WB_Context2D context) {
	return createPyramidWithAngleRange(points, minangle, maxangle, context);
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngleRange(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final WB_Context2D context) {
	return createPyramidWithAngleRange(points, minangle, maxangle, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngle(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final WB_Context2D context) {
	return createPyramidWithAngleRange(points, angle, angle, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngle(final WB_Coordinate[] points,
	    final double angle, final WB_Context2D context) {
	return createPyramidWithAngleRange(points, angle, angle, context);
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngleRange(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle) {
	return createPyramidWithAngleRange(points, minangle, maxangle,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngleRange(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle) {
	return createPyramidWithAngleRange(points, minangle, maxangle,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngle(
	    final Collection<? extends WB_Coordinate> points, final double angle) {
	return createPyramidWithAngle(points, angle, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithAngle(final WB_Coordinate[] points,
	    final double angle) {
	return createPyramidWithAngle(points, angle, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param b 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double height, final boolean b, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine(0.25 * Math.PI);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? 0 : height, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[b ? n + 1 : n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param b 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double height, final boolean b) {
	return createPyramidWithHeight(points, height, b, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double height) {
	return createPyramidWithHeight(points, height, true,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param b 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithHeight(
	    final WB_Coordinate[] points, final double height, final boolean b,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine(0.25 * Math.PI);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? 0 : height, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[b ? n + 1 : n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param b 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithHeight(
	    final WB_Coordinate[] points, final double height, final boolean b) {
	return createPyramidWithHeight(points, height, b, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createPyramidWithHeight(
	    final WB_Coordinate[] points, final double height) {
	return createPyramidWithHeight(points, height, true,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithAngleRange(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int flc = faceloop.count();
		final int[] tmp = new int[flc];
		final int[] tmp2 = new int[flc];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    tmp2[flc - 1 - i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z, point);
		    lpoints.add(point);
		    i++;
		}
		tmpfaces.add(tmp);
		tmpfaces.add(tmp2);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithAngleRange(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int flc = faceloop.count();
		final int[] tmp = new int[flc];
		final int[] tmp2 = new int[flc];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    tmp2[flc - 1 - i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z, point);
		    lpoints.add(point);
		    i++;
		}
		tmpfaces.add(tmp);
		tmpfaces.add(tmp2);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithAngle(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final WB_Context2D context) {
	return createDipyramidWithAngleRange(points, angle, angle, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithAngle(
	    final WB_Coordinate[] points, final double angle,
	    final WB_Context2D context) {
	return createDipyramidWithAngleRange(points, angle, angle, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithAngle(
	    final Collection<? extends WB_Coordinate> points, final double angle) {
	return createDipyramidWithAngle(points, angle, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithAngle(
	    final WB_Coordinate[] points, final double angle) {
	return createDipyramidWithAngle(points, angle, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double height, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine(Math.PI * 0.25);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int flc = faceloop.count();
		final int[] tmp = new int[flc];
		final int[] tmp2 = new int[flc];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? 0 : height, point);
		    lpoints.add(point);
		    tmp2[flc - 1 - i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? 0 : -height, point);
		    lpoints.add(point);
		    i++;
		}
		tmpfaces.add(tmp);
		tmpfaces.add(tmp2);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithHeight(
	    final WB_Coordinate[] points, final double height) {
	return createDipyramidWithHeight(points, height, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithHeight(
	    final WB_Coordinate[] points, final double height,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine(Math.PI * 0.25);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int flc = faceloop.count();
		final int[] tmp = new int[flc];
		final int[] tmp2 = new int[flc];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? 0 : height, point);
		    lpoints.add(point);
		    tmp2[flc - 1 - i] = counter++;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? 0 : -height, point);
		    lpoints.add(point);
		    i++;
		}
		tmpfaces.add(tmp);
		tmpfaces.add(tmp2);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createDipyramidWithHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double height) {
	return createDipyramidWithHeight(points, height, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAnglesAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double[] angles, final double height, final boolean b,
	    final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? 1 : 0) + (t ? top.size() : 0)][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + (b ? 1 : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAnglesAndHeight(
	    final WB_Coordinate[] points, final double angles[],
	    final double height, final boolean b, final boolean t,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? 1 : 0) + (t ? top.size() : 0)][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + (b ? 1 : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height,
	    final boolean b, final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? 1 : 0) + (t ? top.size() : 0)][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + (b ? 1 : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height, final boolean b,
	    final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? 1 : 0) + (t ? top.size() : 0)][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (b) {
	    faces[n] = new int[n];
	    i = 0;
	    for (final WB_Coordinate p : points) {
		faces[n][i++] = counter++;
		point = createPoint();
		context.pointTo3D(p.xd(), p.yd(), point);
		lpoints.add(point);
	    }
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + (b ? 1 : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height,
	    final WB_Context2D context) {
	return createTaperWithAngleRangeAndHeight(points, minangle, maxangle,
		height, context);
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height,
	    final WB_Context2D context) {
	return createTaperWithAngleRangeAndHeight(points, minangle, maxangle,
		height, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final double height, final WB_Context2D context) {
	return createTaperWithAngleRangeAndHeight(points, angle, angle, height,
		true, true, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleAndHeight(
	    final WB_Coordinate[] points, final double angle,
	    final double height, final WB_Context2D context) {
	return createTaperWithAngleRangeAndHeight(points, angle, angle, height,
		true, true, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final double height) {
	return createTaperWithAngleAndHeight(points, angle, height,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createTaperWithAngleAndHeight(
	    final WB_Coordinate[] points, final double angle,
	    final double height) {
	return createTaperWithAngleAndHeight(points, angle, height,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAnglesAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double[] angles, final double height, final boolean b,
	    final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? top.size() : 0)
		+ (t ? top.size() : 0)][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	if (b) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		tmp.reverse();
		final int index = n + (t ? top.size() : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, -c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAnglesAndHeight(
	    final WB_Coordinate[] points, final double[] angles,
	    final double height, final boolean b, final boolean t,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? top.size() : 0)
		+ (t ? top.size() : 0)][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	if (b) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		tmp.reverse();
		final int index = n + (t ? top.size() : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, -c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height,
	    final boolean b, final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? top.size() : 0)
		+ (t ? top.size() : 0)][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	if (b) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		tmp.reverse();
		final int index = n + (t ? top.size() : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, -c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height, final boolean b,
	    final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	for (final Edge e : poly) {
	    e.machine = new Machine((Math.random() * (maxangle - minangle))
		    + minangle);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, height);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		int i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? top.size() : 0)
		+ (t ? top.size() : 0)][];
	int i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	if (b) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		tmp.reverse();
		final int index = n + (t ? top.size() : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, -c.z, point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height,
	    final WB_Context2D context) {
	return createBitaperWithAngleRangeAndHeight(points, minangle, maxangle,
		height, true, true, context);
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height,
	    final WB_Context2D context) {
	return createBitaperWithAngleRangeAndHeight(points, minangle, maxangle,
		height, true, true, context);
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height) {
	return createBitaperWithAngleRangeAndHeight(points, minangle, maxangle,
		height, true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height) {
	return createBitaperWithAngleRangeAndHeight(points, minangle, maxangle,
		height, true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final double height, final WB_Context2D context) {
	return createBitaperWithAngleRangeAndHeight(points, angle, angle,
		height, true, true, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleAndHeight(
	    final WB_Coordinate[] points, final double angle,
	    final double height, final WB_Context2D context) {
	return createBitaperWithAngleRangeAndHeight(points, angle, angle,
		height, true, true, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final double height) {
	return createBitaperWithAngleAndHeight(points, angle, height,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @return 
     */
    public WB_FaceListMesh createBitaperWithAngleAndHeight(
	    final WB_Coordinate[] points, final double angle,
	    final double height) {
	return createBitaperWithAngleAndHeight(points, angle, height,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param cap 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAnglesAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double[] angles, final double height, final double cap,
	    final boolean b, final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, cap);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	i = 0;
	WB_Point point;
	for (final WB_Coordinate p : points) {
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), -height * 0.5, point);
	    lpoints.add(point);
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), +height * 0.5, point);
	    lpoints.add(point);
	}
	counter = lpoints.size();
	for (i = 0; i < points.size(); i++) {
	    final int[] tmp = new int[4];
	    tmp[0] = 2 * i;
	    tmp[1] = 2 * ((i + 1) % points.size());
	    tmp[2] = (2 * ((i + 1) % points.size())) + 1;
	    tmp[3] = (2 * i) + 1;
	    tmpfaces.add(tmp);
	}
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z + (height * 0.5), point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z - (height * 0.5), point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? top.size() : 0)
		+ (t ? top.size() : 0)][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z + (height * 0.5), point);
		    lpoints.add(point);
		}
	    }
	}
	if (b) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		tmp.reverse();
		final int index = n + (t ? top.size() : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, -c.z - (height * 0.5), point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param cap 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height,
	    final double cap, final boolean b, final boolean t,
	    final WB_Context2D context) {
	final double[] angles = new double[points.size()];
	for (int i = 0; i < points.size(); i++) {
	    angles[i] = ((Math.random() * (maxangle - minangle)) + minangle);
	}
	return createCapsuleWithAnglesAndHeight(points, angles, height, cap, b,
		t, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param cap 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final double height, final double cap,
	    final boolean b, final boolean t, final WB_Context2D context) {
	final double[] angles = new double[points.size()];
	for (int i = 0; i < points.size(); i++) {
	    angles[i] = angle;
	}
	return createCapsuleWithAnglesAndHeight(points, angles, height, cap, b,
		t, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAnglesAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double[] angles, final double height, final double cap) {
	return createCapsuleWithAnglesAndHeight(points, angles, height, cap,
		true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleRangeAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double minangle, final double maxangle, final double height,
	    final double cap) {
	return createCapsuleWithAngleRangeAndHeight(points, minangle, maxangle,
		height, cap, true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double angle, final double height, final double cap) {
	return createCapsuleWithAngleRangeAndHeight(points, angle, angle,
		height, cap, true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param cap 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAnglesAndHeight(
	    final WB_Coordinate[] points, final double[] angles,
	    final double height, final double cap, final boolean b,
	    final boolean t, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, cap);
	skel.skeleton();
	final LoopL<Corner> top = skel.flatTop;
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	WB_Point point;
	i = 0;
	for (final WB_Coordinate p : points) {
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), -height * 0.5, point);
	    lpoints.add(point);
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), +height * 0.5, point);
	    lpoints.add(point);
	}
	counter = lpoints.size();
	for (i = 0; i < points.length; i++) {
	    final int[] tmp = new int[4];
	    tmp[0] = 2 * i;
	    tmp[1] = 2 * ((i + 1) % points.length);
	    tmp[2] = (2 * ((i + 1) % points.length)) + 1;
	    tmp[3] = (2 * i) + 1;
	    tmpfaces.add(tmp);
	}
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, p.z + (height * 0.5), point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, -p.z - (height * 0.5), point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n + (b ? top.size() : 0)
		+ (t ? top.size() : 0)][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	if (t) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		final int index = n + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, c.z + (height * 0.5), point);
		    lpoints.add(point);
		}
	    }
	}
	if (b) {
	    Loop<Corner> tmp;
	    for (i = 0; i < top.size(); i++) {
		tmp = top.get(i);
		tmp.reverse();
		final int index = n + (t ? top.size() : 0) + i;
		faces[index] = new int[tmp.count()];
		int j = 0;
		for (final Corner c : tmp) {
		    faces[index][j++] = counter++;
		    point = createPoint();
		    context.pointTo3D(c.x, c.y, -c.z - (height * 0.5), point);
		    lpoints.add(point);
		}
	    }
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param cap 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height, final double cap,
	    final boolean b, final boolean t, final WB_Context2D context) {
	final double[] angles = new double[points.length];
	for (int i = 0; i < points.length; i++) {
	    angles[i] = ((Math.random() * (maxangle - minangle)) + minangle);
	}
	return createCapsuleWithAnglesAndHeight(points, angles, height, cap, b,
		t, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param cap 
     * @param b 
     * @param t 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleAndHeight(
	    final WB_Coordinate[] points, final double angle,
	    final double height, final double cap, final boolean b,
	    final boolean t, final WB_Context2D context) {
	final double[] angles = new double[points.length];
	for (int i = 0; i < points.length; i++) {
	    angles[i] = angle;
	}
	return createCapsuleWithAnglesAndHeight(points, angles, height, cap, b,
		t, context);
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAnglesAndHeight(
	    final WB_Coordinate[] points, final double[] angles,
	    final double height, final double cap) {
	return createCapsuleWithAnglesAndHeight(points, angles, height, cap,
		true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param minangle 
     * @param maxangle 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleRangeAndHeight(
	    final WB_Coordinate[] points, final double minangle,
	    final double maxangle, final double height, final double cap) {
	return createCapsuleWithAngleRangeAndHeight(points, minangle, maxangle,
		height, cap, true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angle 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createCapsuleWithAngleAndHeight(
	    final WB_Coordinate[] points, final double angle,
	    final double height, final double cap) {
	return createCapsuleWithAngleRangeAndHeight(points, angle, angle,
		height, cap, true, true, createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param cap 
     * @param context 
     * @return 
     */
    WB_FaceListMesh createSpindleWithAnglesAndHeight(
	    final Collection<? extends WB_Coordinate> points,
	    final double[] angles, final double height, final double cap,
	    final WB_Context2D context) {
	final Corner[] corners = new Corner[points.size()];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.size(); i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.size()]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	i = 0;
	WB_Point point;
	for (final WB_Coordinate p : points) {
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), -height * 0.5, point);
	    lpoints.add(point);
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), +height * 0.5, point);
	    lpoints.add(point);
	}
	counter = lpoints.size();
	for (i = 0; i < points.size(); i++) {
	    final int[] tmp = new int[4];
	    tmp[0] = 2 * i;
	    tmp[1] = 2 * ((i + 1) % points.size());
	    tmp[2] = (2 * ((i + 1) % points.size())) + 1;
	    tmp[3] = (2 * i) + 1;
	    tmpfaces.add(tmp);
	}
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? p.z
			    + (height * 0.5) : (+height * 0.5) + cap, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? -p.z
			    - (height * 0.5) : (-height * 0.5) - cap, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param cap 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createSpindle(
	    final Collection<? extends WB_Coordinate> points,
	    final double height, final double cap, final WB_Context2D context) {
	final double[] angles = new double[points.size()];
	for (int i = 0; i < points.size(); i++) {
	    angles[i] = 0.25 * Math.PI;
	}
	return createSpindleWithAnglesAndHeight(points, angles, height, cap,
		context);
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createSpindle(
	    final Collection<? extends WB_Coordinate> points,
	    final double height, final double cap) {
	final double[] angles = new double[points.size()];
	for (int i = 0; i < points.size(); i++) {
	    angles[i] = 0.25 * Math.PI;
	}
	return createSpindleWithAnglesAndHeight(points, angles, height, cap,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @param angles 
     * @param height 
     * @param cap 
     * @param context 
     * @return 
     */
    WB_FaceListMesh createSpindleWithAnglesAndHeight(
	    final WB_Coordinate[] points, final double[] angles,
	    final double height, final double cap, final WB_Context2D context) {
	final Corner[] corners = new Corner[points.length];
	// final WB_Point local = createPoint();
	int id = 0;
	for (final WB_Coordinate p : points) {
	    // local.set(context.pointTo2D(p));
	    corners[id++] = new Corner(p.xd(), p.yd());// new Corner(local.x(),
	    // local.y());
	}
	final Loop<Edge> poly = new Loop<Edge>();
	for (int i = 0; i < points.length; i++) {
	    poly.append(new Edge(corners[i], corners[(i + 1) % points.length]));
	}
	int i = 0;
	for (final Edge e : poly) {
	    e.machine = new Machine(angles[i++]);
	}
	final LoopL<Edge> out = new LoopL<Edge>();
	out.add(poly);
	final Skeleton skel = new Skeleton(out, true);
	skel.skeleton();
	final Collection<Face> expfaces = skel.output.faces.values();
	int counter = 0;
	final List<int[]> tmpfaces = new FastTable<int[]>();
	final List<WB_Point> lpoints = new FastTable<WB_Point>();
	i = 0;
	WB_Point point;
	for (final WB_Coordinate p : points) {
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), -height * 0.5, point);
	    lpoints.add(point);
	    point = createPoint();
	    context.pointTo3D(p.xd(), p.yd(), +height * 0.5, point);
	    lpoints.add(point);
	}
	counter = lpoints.size();
	for (i = 0; i < points.length; i++) {
	    final int[] tmp = new int[4];
	    tmp[0] = 2 * i;
	    tmp[1] = 2 * ((i + 1) % points.length);
	    tmp[2] = (2 * ((i + 1) % points.length)) + 1;
	    tmp[3] = (2 * i) + 1;
	    tmpfaces.add(tmp);
	}
	for (final Face face : expfaces) {
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? p.z
			    + (height * 0.5) : (height * 0.5) + cap, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	    for (final Loop<Point3d> faceloop : face.points) {
		final int[] tmp = new int[faceloop.count()];
		i = 0;
		for (final Point3d p : faceloop) {
		    tmp[faceloop.count() - 1 - i++] = counter;
		    point = createPoint();
		    context.pointTo3D(p.x, p.y, (p.z == 0) ? -p.z
			    - (height * 0.5) : (-height * 0.5) - cap, point);
		    lpoints.add(point);
		    counter++;
		}
		tmpfaces.add(tmp);
	    }
	}
	final int n = tmpfaces.size();
	final int[][] faces = new int[n][];
	i = 0;
	for (final int[] tmp : tmpfaces) {
	    faces[i++] = tmp;
	}
	return createUniqueMesh(createMesh(lpoints, faces));
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param cap 
     * @param context 
     * @return 
     */
    public WB_FaceListMesh createSpindle(final WB_Coordinate[] points,
	    final double height, final double cap, final WB_Context2D context) {
	final double[] angles = new double[points.length];
	for (int i = 0; i < points.length; i++) {
	    angles[i] = 0.25 * Math.PI;
	}
	return createSpindleWithAnglesAndHeight(points, angles, height, cap,
		context);
    }

    /**
     * 
     *
     * @param points 
     * @param height 
     * @param cap 
     * @return 
     */
    public WB_FaceListMesh createSpindle(final WB_Coordinate[] points,
	    final double height, final double cap) {
	final double[] angles = new double[points.length];
	for (int i = 0; i < points.length; i++) {
	    angles[i] = 0.25 * Math.PI;
	}
	return createSpindleWithAnglesAndHeight(points, angles, height, cap,
		createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_FaceListMesh createConvexHull(
	    final List<? extends WB_Coordinate> points) {
	return createConvexHull(points, true);
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    public WB_FaceListMesh createConvexHull(final WB_Coordinate[] points) {
	return createConvexHull(points, true);
    }

    /**
     * 
     *
     * @param points 
     * @param triangulate 
     * @return 
     */
    public WB_FaceListMesh createConvexHull(final WB_Coordinate[] points,
	    final boolean triangulate) {
	final List<WB_Coordinate> uniqueVertices = new FastTable<WB_Coordinate>();
	final WB_KDTree<WB_Coordinate, Integer> kdtree = new WB_KDTree<WB_Coordinate, Integer>();
	WB_KDEntry<WB_Coordinate, Integer> neighbor;
	int n = 0;
	for (final WB_Coordinate p : points) {
	    if (n == 0) {
		kdtree.add(p, n++);
		uniqueVertices.add(p);
	    } else {
		neighbor = kdtree.getNearestNeighbor(p);
		if (neighbor.d2 > WB_Epsilon.SQEPSILON) {
		    kdtree.add(p, n++);
		    uniqueVertices.add(p);
		}
	    }
	}
	if (n < 4) {
	    return null;
	}
	try {
	    final WB_QuickHull3D hull = new WB_QuickHull3D(uniqueVertices,
		    triangulate);
	    final int[][] faces = hull.getFaces();
	    final List<WB_Point> hullpoints = hull.getVertices();
	    return createMesh(hullpoints, faces);
	} catch (final Exception e) {
	    return null;
	}
    }

    /**
     * 
     *
     * @param points 
     * @param triangulate 
     * @return 
     */
    public WB_FaceListMesh createConvexHull(
	    final List<? extends WB_Coordinate> points,
	    final boolean triangulate) {
	final List<WB_Coordinate> uniqueVertices = new FastTable<WB_Coordinate>();
	final WB_KDTree<WB_Coordinate, Integer> kdtree = new WB_KDTree<WB_Coordinate, Integer>();
	WB_KDEntry<WB_Coordinate, Integer> neighbor;
	int n = 0;
	for (final WB_Coordinate p : points) {
	    if (n == 0) {
		kdtree.add(p, n++);
		uniqueVertices.add(p);
	    } else {
		neighbor = kdtree.getNearestNeighbor(p);
		if (neighbor.d2 > WB_Epsilon.SQEPSILON) {
		    kdtree.add(p, n++);
		    uniqueVertices.add(p);
		}
	    }
	}
	if (n < 4) {
	    return null;
	}
	try {
	    final WB_QuickHull3D hull = new WB_QuickHull3D(uniqueVertices,
		    triangulate);
	    final int[][] faces = hull.getFaces();
	    final List<WB_Point> hullpoints = hull.getVertices();
	    return createMesh(hullpoints, faces);
	} catch (final Exception e) {
	    return null;
	}
    }

    /**
     * 
     *
     * @param points 
     * @param triangulate 
     * @param threshold 
     * @return 
     */
    public WB_FaceListMesh createConvexHullWithThreshold(
	    final WB_Coordinate[] points, final boolean triangulate,
	    final double threshold) {
	final List<WB_Coordinate> uniqueVertices = new FastTable<WB_Coordinate>();
	final WB_KDTree<WB_Coordinate, Integer> kdtree = new WB_KDTree<WB_Coordinate, Integer>();
	WB_KDEntry<WB_Coordinate, Integer> neighbor;
	final double t2 = threshold * threshold;
	int n = 0;
	for (final WB_Coordinate p : points) {
	    if (n == 0) {
		kdtree.add(p, n++);
		uniqueVertices.add(p);
	    } else {
		neighbor = kdtree.getNearestNeighbor(p);
		if (neighbor.d2 > t2) {
		    kdtree.add(p, n++);
		    uniqueVertices.add(p);
		}
	    }
	}
	if (n < 4) {
	    return null;
	}
	try {
	    final WB_QuickHull3D hull = new WB_QuickHull3D(uniqueVertices,
		    triangulate);
	    final int[][] faces = hull.getFaces();
	    final List<WB_Point> hullpoints = hull.getVertices();
	    return createMesh(hullpoints, faces);
	} catch (final Exception e) {
	    return null;
	}
    }

    /**
     * 
     *
     * @param points 
     * @param triangulate 
     * @param threshold 
     * @return 
     */
    public WB_FaceListMesh createConvexHullWithThreshold(
	    final List<? extends WB_Coordinate> points,
	    final boolean triangulate, final double threshold) {
	final List<WB_Coordinate> uniqueVertices = new FastTable<WB_Coordinate>();
	final WB_KDTree<WB_Coordinate, Integer> kdtree = new WB_KDTree<WB_Coordinate, Integer>();
	WB_KDEntry<WB_Coordinate, Integer> neighbor;
	final double t2 = threshold * threshold;
	int n = 0;
	for (final WB_Coordinate p : points) {
	    if (n == 0) {
		kdtree.add(p, n++);
		uniqueVertices.add(p);
	    } else {
		neighbor = kdtree.getNearestNeighbor(p);
		if (neighbor.d2 > t2) {
		    kdtree.add(p, n++);
		    uniqueVertices.add(p);
		}
	    }
	}
	if (n < 4) {
	    return null;
	}
	try {
	    final WB_QuickHull3D hull = new WB_QuickHull3D(uniqueVertices,
		    triangulate);
	    final int[][] faces = hull.getFaces();
	    final List<WB_Point> hullpoints = hull.getVertices();
	    return createMesh(hullpoints, faces);
	} catch (final Exception e) {
	    return null;
	}
    }

    /**
     * 
     *
     * @param points 
     * @param filter 
     * @return 
     */
    public WB_FaceListMesh createConcaveHull(
	    final List<? extends WB_Coordinate> points, final double filter) {
	final WB_AlphaComplex ac = new WB_AlphaComplex(points);
	return createMesh(points, ac.getAlphaComplexShape(filter));
    }

    /**
     * 
     *
     * @param points 
     * @param filter 
     * @return 
     */
    public WB_FaceListMesh createConcaveHull(final WB_Coordinate[] points,
	    final double filter) {
	final WB_AlphaComplex ac = new WB_AlphaComplex(points);
	return createMesh(points, ac.getAlphaComplexShape(filter));
    }

    /**
     * 
     *
     * @param points 
     * @param ac 
     * @param filter 
     * @return 
     */
    public WB_FaceListMesh createConcaveHull(
	    final List<? extends WB_Coordinate> points,
	    final WB_AlphaComplex ac, final double filter) {
	return createMesh(points, ac.getAlphaComplexShape(filter));
    }

    /**
     * 
     *
     * @param points 
     * @param ac 
     * @param filter 
     * @return 
     */
    public WB_FaceListMesh createConcaveHull(final WB_Coordinate[] points,
	    final WB_AlphaComplex ac, final double filter) {
	return createMesh(points, ac.getAlphaComplexShape(filter));
    }

    /**
     * 
     *
     * @param text 
     * @param fontName 
     * @param pointSize 
     * @return 
     */
    public List<WB_Polygon> createTextWithTTFFont(final String text,
	    final String fontName, final float pointSize) {
	try {
	    final InputStream is = new FileInputStream(fontName);
	    final Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	    return createText(text, font.deriveFont(pointSize), 400.0);
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 
     *
     * @param text 
     * @param fontName 
     * @param pointSize 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> createTextWithTTFFont(final String text,
	    final String fontName, final float pointSize, final double flatness) {
	try {
	    final InputStream is = new FileInputStream(fontName);
	    final Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	    return createText(text, font.deriveFont(pointSize), flatness);
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 
     *
     * @param text 
     * @param fontName 
     * @param style 
     * @param pointSize 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> createTextWithTTFFont(final String text,
	    final String fontName, final int style, final float pointSize,
	    final double flatness) {
	try {
	    final InputStream is = new FileInputStream(fontName);
	    final Font font = Font.createFont(Font.TRUETYPE_FONT, is);
	    return createText(text, font.deriveFont(style, pointSize), flatness);
	} catch (final Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

    /**
     * 
     *
     * @param text 
     * @param fontName 
     * @param pointSize 
     * @return 
     */
    public List<WB_Polygon> createText(final String text,
	    final String fontName, final float pointSize) {
	final Font font = new Font(fontName, 0, (int) pointSize);
	return createText(text, font, 400.0);
    }

    /**
     * 
     *
     * @param text 
     * @param fontName 
     * @param pointSize 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> createText(final String text,
	    final String fontName, final float pointSize, final double flatness) {
	final Font font = new Font(fontName, 0, (int) pointSize);
	return createText(text, font, flatness);
    }

    /**
     * 
     *
     * @param text 
     * @param fontName 
     * @param style 
     * @param pointSize 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> createText(final String text,
	    final String fontName, final int style, final float pointSize,
	    final double flatness) {
	final Font font = new Font(fontName, style, (int) pointSize);
	return createText(text, font, flatness);
    }

    /**
     * 
     *
     * @param text 
     * @param font 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> createText(final String text, final Font font,
	    final double flatness) {
	if (shapereader == null) {
	    shapereader = new WB_ShapeReader();
	}
	final char[] chs = text.toCharArray();
	final FontRenderContext fontContext = new FontRenderContext(null,
		false, true);
	final GlyphVector gv = font.createGlyphVector(fontContext, chs);
	final List<WB_Polygon> geometries = new FastTable<WB_Polygon>();
	for (int i = 0; i < gv.getNumGlyphs(); i++) {
	    geometries
		    .addAll(shapereader.read(gv.getGlyphOutline(i), flatness));
	}
	return geometries;
    }

    /**
     * 
     *
     * @param shape 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> createShape(final Shape shape, final double flatness) {
	if (shapereader == null) {
	    shapereader = new WB_ShapeReader();
	}
	return shapereader.read(shape, flatness);
    }

    /**
     * 
     *
     * @param p 
     * @param a 
     * @param b 
     * @param c 
     * @return 
     */
    public WB_Point createClosestPointOnTriangle(final WB_Coordinate p,
	    final WB_Coordinate a, final WB_Coordinate b, final WB_Coordinate c) {
	final WB_Vector ab = createVectorFromTo(a, b);
	final WB_Vector ac = createVectorFromTo(a, c);
	final WB_Vector ap = createVectorFromTo(a, b);
	final double d1 = ab.dot(ap);
	final double d2 = ac.dot(ap);
	if ((d1 <= 0) && (d2 <= 0)) {
	    return createPoint(a);
	}
	final WB_Vector bp = createVectorFromTo(b, p);
	final double d3 = ab.dot(bp);
	final double d4 = ac.dot(bp);
	if ((d3 >= 0) && (d4 <= d3)) {
	    return createPoint(b);
	}
	final double vc = (d1 * d4) - (d3 * d2);
	if ((vc <= 0) && (d1 >= 0) && (d3 <= 0)) {
	    final double v = d1 / (d1 - d3);
	    return (createPoint(a).addSelf(ab.mul(v)));
	}
	final WB_Vector cp = createVectorFromTo(c, p);
	final double d5 = ab.dot(cp);
	final double d6 = ac.dot(cp);
	if ((d6 >= 0) && (d5 <= d6)) {
	    return createPoint(c);
	}
	final double vb = (d5 * d2) - (d1 * d6);
	if ((vb <= 0) && (d2 >= 0) && (d6 <= 0)) {
	    final double w = d2 / (d2 - d6);
	    return (createPoint(a).addSelf(ac.mul(w)));
	}
	final double va = (d3 * d6) - (d5 * d4);
	if ((va <= 0) && ((d4 - d3) >= 0) && ((d5 - d6) >= 0)) {
	    final double w = (d4 - d3) / ((d4 - d3) + (d5 - d6));
	    return (createPoint(b).addSelf(createVectorFromTo(c, b).mul(w)));
	}
	final double denom = 1.0 / (va + vb + vc);
	final double v = vb * denom;
	final double w = vc * denom;
	return (createPoint(a).addSelf(ab.mul(v)).addSelf(ac.mul(w)));
    }

    /**
     * 
     *
     * @param p 
     * @param poly 
     * @return 
     */
    public WB_Point createClosestPointOnPolygon(final WB_Coordinate p,
	    final WB_Polygon poly) {
	final int[][] triangles = poly.getTriangles();
	final int n = triangles.length;
	double dmax2 = Double.POSITIVE_INFINITY;
	WB_Point closest = null;
	WB_Point tmp;
	int[] T;
	for (int i = 0; i < n; i++) {
	    T = triangles[i];
	    tmp = createClosestPointOnTriangle(p, poly.getPoint(T[0]),
		    poly.getPoint(T[1]), poly.getPoint(T[2]));
	    final double d2 = tmp.getSqDistance3D(p);
	    if (d2 < dmax2) {
		closest = tmp;
		dmax2 = d2;
		if (WB_Epsilon.isZeroSq(d2)) {
		    return closest;
		}
	    }
	}
	return closest;
    }

    /**
     * Sphere with center and radius.
     *
     * @param center 
     * @param radius 
     * @return sphere
     */
    public WB_Sphere createSphereWithRadius(final WB_Coordinate center,
	    final double radius) {
	return new WB_Sphere(center, radius);
    }

    /**
     * Sphere with center and diameter.
     *
     * @param center 
     * @param diameter 
     * @return sphere
     */
    public WB_Sphere createSphereWithDiameter(final WB_Coordinate center,
	    final double diameter) {
	return createSphereWithRadius(center, .5 * diameter);
    }

    /**
     * Sphere with center and radius.
     *
     * @param x 
     * @param y 
     * @param z 
     * @param radius 
     * @return sphere
     */
    public WB_Sphere createSphereWithRadius(final double x, final double y,
	    final double z, final double radius) {
	return createSphereWithRadius(createPoint(x, y, z), radius);
    }

    /**
     * Sphere with diameter and radius.
     *
     * @param x 
     * @param y 
     * @param z 
     * @param diameter 
     * @return sphere
     */
    public WB_Sphere createSphereWithDiameter(final double x, final double y,
	    final double z, final double diameter) {
	return createSphereWithRadius(createPoint(x, y, z), .5 * diameter);
    }

    /**
     * Get tetrahedron from 4 points.
     *
     * @param p1            first point of tetrahedron
     * @param p2            second point of tetrahedron
     * @param p3            third point of tetrahedron
     * @param p4            fourth point of tetrahedron
     * @return tetrahedron
     */
    public WB_Tetrahedron createTetrahedron(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3,
	    final WB_Coordinate p4) {
	return new WB_Tetrahedron(p1, p2, p3, p4);
    }

    /**
     * Get dihedral angle defined by three vectors.
     *
     * @param p1 
     * @param p2 
     * @param p3 
     * @param p4 
     * @return dihedral angle
     */
    public double getDihedralAngle(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3,
	    final WB_Coordinate p4) {
	return getDihedralAngle(createVectorFromTo(p2, p1),
		createVectorFromTo(p3, p2), createVectorFromTo(p4, p3));
    }

    /**
     * Get dihedral angle defined by three vectors.
     *
     * @param v1            WB_Coordinate
     * @param v2            WB_Coordinate
     * @param v3            WB_Coordinate
     * @return dihedral angle
     */
    public double getDihedralAngle(final WB_Coordinate v1,
	    final WB_Coordinate v2, final WB_Coordinate v3) {
	final WB_Vector b2xb3 = createVector(v2).crossSelf(v3);
	final WB_Vector b1xb2 = createVector(v1).crossSelf(v2);
	final double x = b1xb2.dot(b2xb3);
	final double y = b1xb2.crossSelf(b2xb3).dot(createNormalizedVector(v2));
	return Math.atan2(y, x);
    }

    /**
     * Get cosine of dihedral angle defined by three vectors.
     *
     * @param p1 
     * @param p2 
     * @param p3 
     * @param p4 
     * @return cosine of dihedral angle
     */
    public double getCosDihedralAngle(final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3,
	    final WB_Coordinate p4) {
	return getCosDihedralAngle(createVectorFromTo(p2, p1),
		createVectorFromTo(p3, p2), createVectorFromTo(p4, p3));
    }

    /**
     * Get cosine of dihedral angle defined by three vectors.
     *
     * @param u            WB_Coordinate
     * @param v            WB_Coordinate
     * @param w            WB_Coordinate
     * @return cosine of dihedral angle
     */
    public double getCosDihedralAngle(final WB_Coordinate u,
	    final WB_Coordinate v, final WB_Coordinate w) {
	final WB_Vector uv = createVector(u).crossSelf(
		createVector(u).addSelf(v));
	final WB_Vector vw = createVector(v).crossSelf(
		createVector(v).addSelf(w));
	return (uv.dot(vw) / (uv.getLength3D() * vw.getLength3D()));
    }

    /**
     * 
     */
    private final GeometryFactory JTSgf = new GeometryFactory();

    /**
     * 
     *
     * @param poly 
     * @param d 
     * @return 
     */
    public List<WB_Polygon> buffer(final WB_Polygon poly, final double d) {
	final Polygon JTSpoly = toJTSPolygon(poly);
	final Geometry result = BufferOp.bufferOp(JTSpoly, d);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly 
     * @return 
     */
    public List<WB_Polygon> boundary(final WB_Polygon poly) {
	final Polygon JTSpoly = toJTSPolygon(poly);
	final LineString result = JTSpoly.getExteriorRing();
	return createPolygonsFromJTSGeometry(JTSgf.createPolygon(result
		.getCoordinates()));
    }

    /**
     * 
     *
     * @param poly 
     * @param tol 
     * @return 
     */
    public List<WB_Polygon> simplify(final WB_Polygon poly, final double tol) {
	final Polygon JTSpoly = toJTSPolygon(poly);
	// final Geometry result = DouglasPeuckerSimplifier.simplify(JTSpoly,
	// tol);
	final Geometry result = TopologyPreservingSimplifier.simplify(JTSpoly,
		tol);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly 
     * @param max 
     * @return 
     */
    public List<WB_Polygon> densify(final WB_Polygon poly, final double max) {
	final Polygon JTSpoly = toJTSPolygon(poly);
	// final Geometry result = DouglasPeuckerSimplifier.simplify(JTSpoly,
	// tol);
	final Geometry result = Densifier.densify(JTSpoly, max);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> union(final WB_Polygon poly1, final WB_Polygon poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon JTSpoly2 = toJTSPolygon(poly2);
	final Geometry result = JTSpoly1.union(JTSpoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> union(final WB_Polygon poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	int i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = JTSpoly1.union(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> union(final Collection<? extends WB_Polygon> poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon[] allPoly1 = new Polygon[poly1.size()];
	int i = 0;
	for (final WB_Polygon poly : poly1) {
	    allPoly1[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly1 = JTSgf.createMultiPolygon(allPoly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = collPoly1.union(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> difference(final WB_Polygon poly1,
	    final WB_Polygon poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon JTSpoly2 = toJTSPolygon(poly2);
	final Geometry result = JTSpoly1.difference(JTSpoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> difference(final WB_Polygon poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	int i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = JTSpoly1.difference(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> difference(
	    final Collection<? extends WB_Polygon> poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon[] allPoly1 = new Polygon[poly1.size()];
	int i = 0;
	for (final WB_Polygon poly : poly1) {
	    allPoly1[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly1 = JTSgf.createMultiPolygon(allPoly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = collPoly1.difference(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> difference(
	    final Collection<? extends WB_Polygon> poly1, final WB_Polygon poly2) {
	final Polygon[] allPoly1 = new Polygon[poly1.size()];
	int i = 0;
	for (final WB_Polygon poly : poly1) {
	    allPoly1[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly1 = JTSgf.createMultiPolygon(allPoly1);
	final Polygon JTSpoly2 = toJTSPolygon(poly2);
	final Geometry result = collPoly1.difference(JTSpoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> intersection(final WB_Polygon poly1,
	    final WB_Polygon poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon JTSpoly2 = toJTSPolygon(poly2);
	final Geometry result = JTSpoly1.intersection(JTSpoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> intersection(final WB_Polygon poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	int i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = JTSpoly1.intersection(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> intersection(
	    final Collection<? extends WB_Polygon> poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon[] allPoly1 = new Polygon[poly1.size()];
	int i = 0;
	for (final WB_Polygon poly : poly1) {
	    allPoly1[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly1 = JTSgf.createMultiPolygon(allPoly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = collPoly1.intersection(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> symDifference(final WB_Polygon poly1,
	    final WB_Polygon poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon JTSpoly2 = toJTSPolygon(poly2);
	final Geometry result = JTSpoly1.symDifference(JTSpoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> symDifference(final WB_Polygon poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon JTSpoly1 = toJTSPolygon(poly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	int i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = JTSpoly1.symDifference(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly1 
     * @param poly2 
     * @return 
     */
    public List<WB_Polygon> symDifference(
	    final Collection<? extends WB_Polygon> poly1,
	    final Collection<? extends WB_Polygon> poly2) {
	final Polygon[] allPoly1 = new Polygon[poly1.size()];
	int i = 0;
	for (final WB_Polygon poly : poly1) {
	    allPoly1[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly1 = JTSgf.createMultiPolygon(allPoly1);
	final Polygon[] allPoly2 = new Polygon[poly2.size()];
	i = 0;
	for (final WB_Polygon poly : poly2) {
	    allPoly2[i++] = toJTSPolygon(poly);
	}
	final MultiPolygon collPoly2 = JTSgf.createMultiPolygon(allPoly2);
	final Geometry result = collPoly1.symDifference(collPoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly 
     * @param container 
     * @return 
     */
    public List<WB_Polygon> constrain(final WB_Polygon poly,
	    final WB_Polygon container) {
	final Polygon JTSpoly1 = toJTSPolygon(poly);
	final Polygon JTSpoly2 = toJTSPolygon(container);
	final Geometry result = JTSpoly1.intersection(JTSpoly2);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param polygons 
     * @param container 
     * @return 
     */
    public List<WB_Polygon> constrain(final WB_Polygon[] polygons,
	    final WB_Polygon container) {
	final List<WB_Polygon> polys = new FastTable<WB_Polygon>();
	for (final WB_Polygon poly : polygons) {
	    final Polygon JTSpoly1 = toJTSPolygon(poly);
	    final Polygon JTSpoly2 = toJTSPolygon(container);
	    final Geometry result = JTSpoly1.intersection(JTSpoly2);
	    polys.addAll(createPolygonsFromJTSGeometry(result));
	}
	return polys;
    }

    /**
     * 
     *
     * @param polygons 
     * @param container 
     * @return 
     */
    public List<WB_Polygon> constrain(final List<WB_Polygon> polygons,
	    final WB_Polygon container) {
	final List<WB_Polygon> polys = new FastTable<WB_Polygon>();
	for (final WB_Polygon poly : polygons) {
	    final Polygon JTSpoly1 = toJTSPolygon(poly);
	    final Polygon JTSpoly2 = toJTSPolygon(container);
	    final Geometry result = JTSpoly1.intersection(JTSpoly2);
	    if (!result.isEmpty()) {
		polys.addAll(createPolygonsFromJTSGeometry(result));
	    }
	}
	return polys;
    }

    /**
     * 
     *
     * @param poly 
     * @return 
     */
    Polygon toJTSPolygon(final WB_Polygon poly) {
	final int[] npc = poly.getNumberOfPointsPerContour();
	Coordinate[] coords = new Coordinate[npc[0] + 1];
	int i = 0;
	for (i = 0; i < npc[0]; i++) {
	    coords[i] = toJTSCoordinate(poly.getPoint(i), i);
	}
	coords[i] = toJTSCoordinate(poly.getPoint(0), 0);
	final LinearRing shell = JTSgf.createLinearRing(coords);
	final LinearRing[] holes = new LinearRing[poly.getNumberOfHoles()];
	int index = poly.getNumberOfShellPoints();
	for (i = 0; i < poly.getNumberOfHoles(); i++) {
	    coords = new Coordinate[npc[i + 1] + 1];
	    coords[npc[i + 1]] = toJTSCoordinate(poly.getPoint(index), index);
	    for (int j = 0; j < npc[i + 1]; j++) {
		coords[j] = toJTSCoordinate(poly.getPoint(index), index);
		index++;
	    }
	    holes[i] = JTSgf.createLinearRing(coords);
	}
	return JTSgf.createPolygon(shell, holes);
    }

    /**
     * 
     *
     * @param point 
     * @param i 
     * @return 
     */
    Coordinate toJTSCoordinate(final WB_Coordinate point, final int i) {
	return new Coordinate(point.xd(), point.yd(), i);
    }

    /**
     * 
     *
     * @param poly 
     * @param P 
     * @return 
     */
    public WB_Polygon[] splitSimplePolygon(final WB_Polygon poly,
	    final WB_Plane P) {
	List<WB_Point> frontVerts = new FastTable<WB_Point>();
	List<WB_Point> backVerts = new FastTable<WB_Point>();
	final int numVerts = poly.getNumberOfPoints();
	final WB_Polygon[] polys = new WB_Polygon[2];
	if (numVerts > 0) {
	    WB_Point a = new WB_Point(poly.getPoint(numVerts - 1));
	    WB_ClassificationGeometry aSide = WB_Classify
		    .classifyPointToPlane3D(a, P);
	    WB_Point b;
	    WB_ClassificationGeometry bSide;
	    for (int n = 0; n < numVerts; n++) {
		WB_Point intersection;
		b = new WB_Point(poly.getPoint(n));
		bSide = WB_Classify.classifyPointToPlane3D(b, P);
		if (bSide == WB_ClassificationGeometry.FRONT) {
		    if (aSide == WB_ClassificationGeometry.BACK) {
			intersection = getIntersection(b, a, P);
			frontVerts.add(intersection);
			backVerts.add(intersection);
		    }
		    frontVerts.add(b);
		} else if (bSide == WB_ClassificationGeometry.BACK) {
		    if (aSide == WB_ClassificationGeometry.FRONT) {
			intersection = getIntersection(a, b, P);
			/*
			 * if (classifyPointToPlane(i.p1, WB_Point) !=
			 * ClassifyPointToPlane.POINT_ON_PLANE) { System.out
			 * .println("Inconsistency: intersection not on plane");
			 * }
			 */
			frontVerts.add(intersection);
			backVerts.add(intersection);
		    } else if (aSide == WB_ClassificationGeometry.ON) {
			backVerts.add(a);
		    }
		    backVerts.add(b);
		} else {
		    frontVerts.add(b);
		    if (aSide == WB_ClassificationGeometry.BACK) {
			backVerts.add(b);
		    }
		}
		a = b;
		aSide = bSide;
	    }
	    frontVerts = clean(frontVerts);
	    backVerts = clean(backVerts);
	    if (frontVerts.size() > 2) {
		polys[0] = createSimplePolygon(frontVerts);
	    }
	    if (backVerts.size() > 2) {
		polys[1] = createSimplePolygon(backVerts);
	    }
	}
	return polys;
    }

    /**
     * 
     *
     * @param points 
     * @return 
     */
    private List<WB_Point> clean(final List<WB_Point> points) {
	final List<WB_Point> result = new FastTable<WB_Point>();
	final int n = points.size();
	for (int i = 0; i < n; i++) {
	    if (!points.get(i).equals(points.get((i + 1) % n))) {
		result.add(points.get(i));
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param a 
     * @param b 
     * @param P 
     * @return 
     */
    public WB_Point getIntersection(final WB_Coordinate a,
	    final WB_Coordinate b, final WB_Plane P) {
	final WB_Vector ab = createVectorFromTo(a, b);
	final WB_Vector N = P.getNormal();
	double t = (P.d() - N.dot(a)) / N.dot(ab);
	if ((t >= -WB_Epsilon.EPSILON) && (t <= (1.0 + WB_Epsilon.EPSILON))) {
	    t = WB_Epsilon.clampEpsilon(t, 0, 1);
	    return createPoint(a.xd() + (t * (b.xd() - a.xd())), a.yd()
		    + (t * (b.yd() - a.yd())), a.zd() + (t * (b.zd() - a.zd())));
	}
	return null;
    }

    /**
     * 
     *
     * @param T 
     */
    public void apply(final WB_Transform T) {
	setCurrentCS(createTransformedCS(getCurrentCS(), T));
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createPointSequence(
	    final Collection<? extends WB_Coordinate> tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createPointSequence(
	    final WB_Coordinate[] tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createPointSequence(
	    final WB_CoordinateSequence tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * 
     *
     * @param tuples 
     * @param indices 
     * @return 
     */
    public WB_CoordinateSequence createPointSequence(
	    final List<? extends WB_Coordinate> tuples, final int[] indices) {
	final List<WB_Coordinate> coords = new FastTable<WB_Coordinate>();
	for (final int indice : indices) {
	    coords.add(tuples.get(indice));
	}
	return new WB_CoordinateSequence(coords);
    }

    /**
     * 
     *
     * @param ordinates 
     * @return 
     */
    public WB_CoordinateSequence createPointSequence(final double[] ordinates) {
	return new WB_CoordinateSequence(ordinates);
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createPointSequence(final double[][] tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createVectorSequence(
	    final Collection<? extends WB_Coordinate> tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createVectorSequence(
	    final WB_Coordinate[] tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * 
     *
     * @param ordinates 
     * @return 
     */
    public WB_CoordinateSequence createVectorSequence(final double[] ordinates) {
	return new WB_CoordinateSequence(ordinates);
    }

    /**
     * 
     *
     * @param tuples 
     * @return 
     */
    public WB_CoordinateSequence createVectorSequence(final double[][] tuples) {
	return new WB_CoordinateSequence(tuples);
    }

    /**
     * Create an empty WB_GeometryCollection, a utility WB_Geometry that
     * consists of a collection of WB_Geometry objects including other
     * WB_GeometryCollection.
     *
     * @return WB_GeometryCollection
     */
    public WB_GeometryCollection createCollection() {
	return new WB_GeometryCollection();
    }

    /**
     * Create a WB_GeometryCollection, a utility WB_Geometry that consists of a
     * collection of WB_Geometry objects including other WB_GeometryCollection.
     *
     * @param collection 
     * @return WB_GeometryCollection
     */
    public WB_GeometryCollection createCollection(
	    final Collection<WB_Geometry> collection) {
	return new WB_GeometryCollection(collection);
    }

    /**
     * Create a WB_GeometryCollection, a utility WB_Geometry that consists of a
     * collection of WB_Geometry objects including other WB_GeometryCollection.
     *
     * @param geometries            : 0 or more WB_Geometry objects or an array of WB_Geometry
     * @return WB_GeometryCollection
     */
    public WB_GeometryCollection createCollection(
	    final WB_Geometry... geometries) {
	return new WB_GeometryCollection(geometries);
    }

    /**
     * 
     *
     * @param poly 
     * @param d 
     * @return 
     */
    public List<WB_Polygon> ribbon(final WB_Polygon poly, final double d) {
	final Polygon JTSpoly = toJTSPolygon(poly);
	final Geometry outer = BufferOp.bufferOp(JTSpoly, d * 0.5);
	final Geometry inner = BufferOp.bufferOp(JTSpoly, -d * 0.5);
	final Geometry result = outer.difference(inner);
	return createPolygonsFromJTSGeometry(result);
    }

    /**
     * 
     *
     * @param poly 
     * @param d 
     * @return 
     */
    public List<WB_Polygon> ribbon(final Collection<? extends WB_Polygon> poly,
	    final double d) {
	final Polygon[] allPoly = new Polygon[poly.size()];
	int i = 0;
	for (final WB_Polygon pol : poly) {
	    allPoly[i++] = toJTSPolygon(pol);
	}
	final MultiPolygon collPoly = JTSgf.createMultiPolygon(allPoly);
	final Geometry outer = BufferOp.bufferOp(collPoly, d * 0.5);
	final Geometry inner = BufferOp.bufferOp(collPoly, -d * 0.5);
	final Geometry result = outer.difference(inner);
	return createPolygonsFromJTSGeometry(result);
    }
}
