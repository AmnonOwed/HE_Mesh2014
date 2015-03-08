/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_Sphere implements WB_Geometry {
    /** Center. */
    WB_Point center;
    /** Radius. */
    double radius;
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     */
    public WB_Sphere() {
	this.center = geometryfactory.createPoint();
	this.radius = WB_Math.fastAbs(0);
    }

    /**
     * Instantiates a new WB_Circle.
     *
     * @param center 
     * @param radius 
     */
    public WB_Sphere(final WB_Coordinate center, final double radius) {
	this.center = geometryfactory.createPoint(center);
	this.radius = WB_Math.fastAbs(radius);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
	if (o == this) {
	    return true;
	}
	if (!(o instanceof WB_Sphere)) {
	    return false;
	}
	return (WB_Epsilon.isEqualAbs(radius, ((WB_Sphere) o).getRadius()))
		&& (center.equals(((WB_Sphere) o).getCenter()));
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return (31 * center.hashCode()) + hashCode(radius);
    }

    /**
     * 
     *
     * @param v 
     * @return 
     */
    private int hashCode(final double v) {
	final long tmp = Double.doubleToLongBits(v);
	return (int) (tmp ^ (tmp >>> 32));
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.SPHERE;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Sphere apply(final WB_Transform T) {
	return geometryfactory.createSphereWithRadius(center.applyAsPoint(T),
		radius);
    }

    /**
     * Get copy.
     *
     * @return copy
     */
    public WB_Sphere get() {
	return new WB_Sphere(center, radius);
    }

    /**
     * Gets the center.
     *
     * @return the center
     */
    public WB_Point getCenter() {
	return center;
    }

    /**
     * Sets the center.
     *
     * @param c
     *            the new center
     */
    public void setCenter(final WB_Coordinate c) {
	this.center = new WB_Point(c);
    }

    /**
     * Gets the radius.
     *
     * @return the radius
     */
    public double getRadius() {
	return radius;
    }

    /**
     * Sets the radius.
     *
     * @param r
     *            the new radius
     */
    public void setRadius(final double r) {
	this.radius = r;
    }

    /**
     * Approximate sphere enclosing points, calculated from distant points.
     *
     * @param points
     *            WB_Point[]
     * @param numPoints
     *            number of points
     * @return sphere
     */
    public static WB_Sphere sphereFromDistantPoints(
	    final WB_Coordinate[] points, final int numPoints) {
	int minx = 0;
	int maxx = 0;
	int miny = 0;
	int maxy = 0;
	int minz = 0;
	int maxz = 0;
	for (int i = 1; i < numPoints; i++) {
	    if (points[i].xd() < points[minx].xd()) {
		minx = i;
	    }
	    if (points[i].xd() > points[maxx].xd()) {
		maxx = i;
	    }
	    if (points[i].yd() < points[miny].yd()) {
		miny = i;
	    }
	    if (points[i].yd() > points[maxy].yd()) {
		maxy = i;
	    }
	    if (points[i].zd() < points[minz].zd()) {
		minz = i;
	    }
	    if (points[i].zd() > points[maxz].zd()) {
		maxz = i;
	    }
	}
	final double dist2x = WB_GeometryOp.getSqDistanceToPoint3D(
		points[maxx], points[minx]);
	final double dist2y = WB_GeometryOp.getSqDistanceToPoint3D(
		points[maxy], points[miny]);
	final double dist2z = WB_GeometryOp.getSqDistanceToPoint3D(
		points[maxz], points[minz]);
	int min = minx;
	int max = maxx;
	if ((dist2y > dist2x) && (dist2y > dist2z)) {
	    max = maxy;
	    min = miny;
	}
	if ((dist2z > dist2x) && (dist2z > dist2y)) {
	    max = maxz;
	    min = minz;
	}
	final WB_Point c = geometryfactory.createMidpoint(points[min],
		points[max]);
	final double r = WB_GeometryOp.getDistanceToPoint3D(points[max], (c));
	return new WB_Sphere(c, r);
    }

    /**
     * Get Ritter sphere enclosing points.
     *
     * @param points
     *            WB_Point[]
     * @param numPoints
     *            number of points
     * @return sphere
     */
    public static WB_Sphere ritterSphere(final WB_Point[] points,
	    final int numPoints) {
	final WB_Sphere s = sphereFromDistantPoints(points, numPoints);
	for (int i = 0; i < numPoints; i++) {
	    s.growSpherebyPoint(points[i]);
	}
	return s;
    }

    /**
     * Get iterative Ritter sphere enclosing points.
     *
     * @param points
     *            WB_Point[]
     * @param numPoints
     *            number of points
     * @param iter
     *            number of iterations (8 should be fine)
     * @return sphere
     */
    public static WB_Sphere ritterIterativeSphere(final WB_Point[] points,
	    final int numPoints, final int iter) {
	WB_Sphere s = ritterSphere(points, numPoints);
	final WB_Sphere s2 = s.get();
	for (int k = 0; k < iter; k++) {
	    s2.radius = s2.radius * 0.95;
	    for (int i = 0; i < numPoints; i++) {
		int j = i + 1
			+ (int) (.999999 * Math.random() * (numPoints - i - 1));
		if (j > (numPoints - 1)) {
		    j = numPoints - 1;
		}
		final WB_Point tmp = points[i];
		points[i] = points[j];
		points[j] = tmp;
		s2.growSpherebyPoint(points[i]);
	    }
	    if (s2.radius < s.radius) {
		s = s2;
	    }
	}
	return s;
    }

    /**
     * Get Eigensphere enclosing points.
     *
     * @param points
     *            WB_Point[]
     * @param numPoints
     *            number of points
     * @return sphere
     */
    public static WB_Sphere eigenSphere(final WB_Point[] points,
	    final int numPoints) {
	WB_M33 m;
	WB_M33 v;
	m = WB_M33.covarianceMatrix(points, numPoints);
	v = m.Jacobi();
	final WB_Vector e = new WB_Vector();
	if ((WB_Math.fastAbs(m.m11) >= WB_Math.fastAbs(m.m22))
		&& (WB_Math.fastAbs(m.m11) >= WB_Math.fastAbs(m.m33))) {
	    e.set(v.m11, v.m21, v.m31);
	}
	if ((WB_Math.fastAbs(m.m22) >= WB_Math.fastAbs(m.m11))
		&& (WB_Math.fastAbs(m.m22) >= WB_Math.fastAbs(m.m33))) {
	    e.set(v.m12, v.m22, v.m32);
	}
	if ((WB_Math.fastAbs(m.m33) >= WB_Math.fastAbs(m.m11))
		&& (WB_Math.fastAbs(m.m33) >= WB_Math.fastAbs(m.m11))) {
	    e.set(v.m13, v.m23, v.m33);
	}
	final int[] iminmax = extremePointsAlongDirection(points, numPoints, e);
	final WB_Point minpt = points[iminmax[0]];
	final WB_Point maxpt = points[iminmax[1]];
	final double dist = WB_GeometryOp.getDistance3D(minpt, maxpt);
	return new WB_Sphere(minpt.addSelf(maxpt).mulSelf(0.5), 0.5 * dist);
    }

    /**
     * Get Ritter Eigensphere enclosing points.
     *
     * @param points
     *            WB_Point[]
     * @param numPoints
     *            number of points
     * @return sphere
     */
    public static WB_Sphere ritterEigenSphere(final WB_Point[] points,
	    final int numPoints) {
	final WB_Sphere s = eigenSphere(points, numPoints);
	for (int i = 0; i < numPoints; i++) {
	    s.growSpherebyPoint(points[i]);
	}
	return s;
    }

    /**
     * Grow sphere to include point.
     *
     * @param p
     *            point to include
     */
    public void growSpherebyPoint(final WB_Point p) {
	final WB_Vector d = p.subToVector3D(center);
	final double dist2 = d.getSqLength3D();
	if (dist2 > (radius * radius)) {
	    final double dist = Math.sqrt(dist2);
	    final double newRadius = (radius + dist) * 0.5;
	    final double k = (newRadius - radius) / dist;
	    radius = newRadius;
	    center.addSelf(k * d.xd(), k * d.yd(), k * d.zd());
	}
    }

    /**
     * Extreme points along direction.
     *
     * @param points
     *            the points
     * @param numPoints
     *            the num points
     * @param dir
     *            the dir
     * @return the int[]
     */
    private static int[] extremePointsAlongDirection(final WB_Point[] points,
	    final int numPoints, final WB_Vector dir) {
	final int[] result = new int[] { -1, -1 };
	double minproj = Double.POSITIVE_INFINITY;
	double maxproj = Double.NEGATIVE_INFINITY;
	double proj;
	for (int i = 0; i < numPoints; i++) {
	    proj = points[i].dot(dir);
	    if (proj < minproj) {
		minproj = proj;
		result[0] = i;
	    }
	    if (proj > maxproj) {
		maxproj = proj;
		result[1] = i;
	    }
	}
	return result;
    }

    /**
     * Project point to sphere.
     *
     * @param v
     *            the v
     * @return point projected to sphere
     */
    public WB_Point projectToSphere(final WB_Coordinate v) {
	final WB_Point vc = new WB_Point(v).sub(center);
	final double er = vc.normalizeSelf();
	if (WB_Epsilon.isZero(er)) {
	    return null;
	}
	return center.addMul(radius, vc);
    }
}
