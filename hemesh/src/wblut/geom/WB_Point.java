/*
 *
 */
package wblut.geom;

import wblut.math.WB_Epsilon;

/**
 *
 */
public class WB_Point extends WB_Vector {
    /**
     *
     *
     * @return
     */
    public static WB_Coordinate X() {
	return new WB_Point(1, 0, 0);
    }

    /**
     *
     *
     * @return
     */
    public static WB_Coordinate Y() {
	return new WB_Point(0, 1, 0);
    }

    /**
     *
     *
     * @return
     */
    public static WB_Coordinate Z() {
	return new WB_Point(0, 0, 1);
    }

    /**
     *
     *
     * @return
     */
    public static WB_Coordinate ZERO() {
	return new WB_Point(0, 0, 0);
    }

    /**
     *
     *
     * @return
     */
    public static WB_Coordinate ORIGIN() {
	return new WB_Point(0, 0, 0);
    }

    /**
     *
     */
    public WB_Point() {
	super();
    }

    /**
     *
     *
     * @param x
     * @param y
     */
    public WB_Point(final double x, final double y) {
	super(x, y);
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     */
    public WB_Point(final double x, final double y, final double z) {
	super(x, y, z);
    }

    /**
     *
     *
     * @param x
     */
    public WB_Point(final double[] x) {
	super(x);
    }

    /**
     *
     *
     * @param fromPoint
     * @param toPoint
     */
    public WB_Point(final double[] fromPoint, final double[] toPoint) {
	super(fromPoint, toPoint);
    }

    /**
     *
     *
     * @param v
     */
    public WB_Point(final WB_Coordinate v) {
	super(v);
    }

    /**
     *
     *
     * @param fromPoint
     * @param toPoint
     */
    public WB_Point(final WB_Coordinate fromPoint, final WB_Coordinate toPoint) {
	super(fromPoint, toPoint);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#addMulSelf(double, double, double, double)
     */
    @Override
    public WB_Point addMulSelf(final double f, final double x, final double y,
	    final double z) {
	set(xd() + (f * x), yd() + (f * y), zd() + (f * z));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#addMulSelf(double, wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point addMulSelf(final double f, final WB_Coordinate p) {
	set(xd() + (f * p.xd()), yd() + (f * p.yd()), zd() + (f * p.zd()));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#addSelf(double, double, double)
     */
    @Override
    public WB_Point addSelf(final double x, final double y, final double z) {
	set(xd() + x, yd() + y, zd() + z);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#addSelf(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point addSelf(final WB_Coordinate p) {
	set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applyAsNormalSelf(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applyAsNormalSelf(final WB_Transform T) {
	T.applyAsNormal(this, this);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applyAsPointSelf(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applyAsPointSelf(final WB_Transform T) {
	T.applyAsPoint(this, this);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applyAsVectorSelf(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applyAsVectorSelf(final WB_Transform T) {
	T.applyAsVector(this, this);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#crossSelf(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point crossSelf(final WB_Coordinate p) {
	set((yd() * p.zd()) - (this.zd() * p.yd()), (this.zd() * p.xd())
		- (this.xd() * p.zd()), (this.xd() * p.yd()) - (yd() * p.xd()));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#divSelf(double)
     */
    @Override
    public WB_Point divSelf(final double f) {
	return mulSelf(1.0 / f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#mulAddMulSelf(double, double,
     * wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point mulAddMulSelf(final double f, final double g,
	    final WB_Coordinate p) {
	set((f * xd()) + (g * p.xd()), (f * yd()) + (g * p.yd()), (f * zd())
		+ (g * p.zd()));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#mulSelf(double)
     */
    @Override
    public WB_Point mulSelf(final double f) {
	set(f * xd(), f * yd(), f * zd());
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#scaleSelf(double)
     */
    @Override
    public WB_Point scaleSelf(final double f) {
	mulSelf(f);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#scale(double)
     */
    @Override
    public WB_Point scale(final double f) {
	return mul(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#scaleSelf(double, double, double)
     */
    @Override
    public WB_Point scaleSelf(final double fx, final double fy, final double fz) {
	set(xd() * fx, yd() * fy, zd() * fz);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#scale(double, double, double)
     */
    @Override
    public WB_Point scale(final double fx, final double fy, final double fz) {
	return new WB_Point(xd() * fx, yd() * fy, zd() * fz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#subSelf(double, double, double)
     */
    @Override
    public WB_Point subSelf(final double x, final double y, final double z) {
	set(xd() - x, yd() - y, zd() - z);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#subSelf(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point subSelf(final WB_Coordinate v) {
	set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#trimSelf(double)
     */
    @Override
    public WB_Point trimSelf(final double d) {
	if (getSqLength3D() > (d * d)) {
	    normalizeSelf();
	    mulSelf(d);
	}
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_Vector#add(double[])
     */
    @Override
    public WB_Point add(final double... x) {
	return new WB_Point(this.xd() + x[0], this.yd() + x[1], this.zd()
		+ x[2]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#add(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point add(final WB_Coordinate p) {
	return new WB_Point(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_Vector#addMul(double, double[])
     */
    @Override
    public WB_Point addMul(final double f, final double... x) {
	return new WB_Point(this.xd() + (f * x[0]), this.yd() + (f * x[1]),
		this.zd() + (f * x[2]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#addMul(double, wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point addMul(final double f, final WB_Coordinate p) {
	return new WB_Point(xd() + (f * p.xd()), yd() + (f * p.yd()), zd()
		+ (f * p.zd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point apply(final WB_Transform T) {
	final WB_Point p = new WB_Point(this);
	return p.applySelf(T);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applySelf(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applySelf(final WB_Transform T) {
	return applyAsPoint(T);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applyAsNormal(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applyAsNormal(final WB_Transform T) {
	final WB_Point result = new WB_Point();
	T.applyAsNormal(this, result);
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applyAsPoint(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applyAsPoint(final WB_Transform T) {
	final WB_Point result = new WB_Point();
	T.applyAsPoint(this, result);
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#applyAsVector(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point applyAsVector(final WB_Transform T) {
	final WB_Point result = new WB_Point();
	T.applyAsVector(this, result);
	return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#cross(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point cross(final WB_Coordinate p) {
	return new WB_Point((yd() * p.zd()) - (zd() * p.yd()), (zd() * p.xd())
		- (xd() * p.zd()), (xd() * p.yd()) - (yd() * p.xd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#div(double)
     */
    @Override
    public WB_Point div(final double f) {
	return mul(1.0 / f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
	if (o == null) {
	    return false;
	}
	if (o == this) {
	    return true;
	}
	if (!(o instanceof WB_Point)) {
	    return false;
	}
	final WB_Point p = (WB_Point) o;
	if (!WB_Epsilon.isEqualAbs(xd(), p.xd())) {
	    return false;
	}
	if (!WB_Epsilon.isEqualAbs(yd(), p.yd())) {
	    return false;
	}
	if (!WB_Epsilon.isEqualAbs(zd(), p.zd())) {
	    return false;
	}
	return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#get()
     */
    @Override
    public WB_Point get() {
	return new WB_Point(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#hashCode()
     */
    @Override
    public int hashCode() {
	return WB_CoordinateOp.calculateHashCode(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#mul(double)
     */
    @Override
    public WB_Point mul(final double f) {
	return new WB_Point(xd() * f, yd() * f, zd() * f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#mulAddMul(double, double,
     * wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point mulAddMul(final double f, final double g,
	    final WB_Coordinate p) {
	return new WB_Point((f * xd()) + (g * p.xd()), (f * yd())
		+ (g * p.yd()), (f * zd()) + (g * p.zd()));
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_Vector#sub(double[])
     */
    @Override
    public WB_Point sub(final double... x) {
	return new WB_Point(this.xd() - x[0], this.yd() - x[1], this.zd()
		- x[2]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#sub(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point sub(final WB_Coordinate p) {
	return new WB_Point(this.xd() - p.xd(), this.yd() - p.yd(), this.zd()
		- p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#toString()
     */
    @Override
    public String toString() {
	return "WB_Point [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#getOrthoNormal2D()
     */
    @Override
    public WB_Point getOrthoNormal2D() {
	final WB_Point a = new WB_Point(-yd(), xd(), 0);
	a.normalizeSelf();
	return a;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Vector#getOrthoNormal3D()
     */
    @Override
    public WB_Point getOrthoNormal3D() {
	if (Math.abs(zd()) > WB_Epsilon.EPSILON) {
	    final WB_Point a = new WB_Point(1, 0, -xd() / zd());
	    a.normalizeSelf();
	    return a;
	} else {
	    return new WB_Point(0, 0, 1);
	}
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public WB_Vector subToVector3D(final double x, final double y,
	    final double z) {
	return new WB_Vector(this.xd() - x, this.yd() - y, this.zd() - z);
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public WB_Vector subToVector3D(final WB_Coordinate p) {
	return new WB_Vector(xd() - p.xd(), yd() - p.yd(), zd() - p.zd());
    }

    /**
     *
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public WB_Vector subToVector2D(final double x, final double y,
	    final double z) {
	return new WB_Vector(this.xd() - x, this.yd() - y, 0);
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public WB_Vector subToVector2D(final WB_Coordinate p) {
	return new WB_Vector(xd() - p.xd(), yd() - p.yd(), 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
     * double, double, double, double, double, double)
     */
    @Override
    public WB_Point rotateAbout2PointAxis(final double angle, final double p1x,
	    final double p1y, final double p1z, final double p2x,
	    final double p2y, final double p2z) {
	final WB_Point result = new WB_Point(this);
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z),
		new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
	raa.applySelfAsPoint(result);
	return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_CoordinateTransform#rotateAbout2PointAxis(double,
     * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point rotateAbout2PointAxis(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	final WB_Point result = new WB_Point(this);
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
	raa.applySelfAsPoint(result);
	return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
     * , double, double, double, double, double, double)
     */
    @Override
    public WB_Point rotateAbout2PointAxisSelf(final double angle,
	    final double p1x, final double p1y, final double p1z,
	    final double p2x, final double p2y, final double p2z) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z),
		new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
	raa.applySelfAsPoint(this);
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
     * , wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point rotateAbout2PointAxisSelf(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));
	raa.applySelfAsPoint(this);
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
     * , double, double, double, double, double, double)
     */
    @Override
    public WB_Point rotateAboutAxis(final double angle, final double px,
	    final double py, final double pz, final double ax, final double ay,
	    final double az) {
	final WB_Point result = new WB_Point(this);
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(
		ax, ay, az));
	raa.applySelfAsPoint(result);
	return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_CoordinateTransform#rotateAboutAxis(double,
     * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point rotateAboutAxis(final double angle, final WB_Coordinate p,
	    final WB_Coordinate a) {
	final WB_Point result = new WB_Point(this);
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p, a);
	raa.applySelfAsPoint(result);
	return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
     * , double, double, double, double, double, double)
     */
    @Override
    public WB_Point rotateAboutAxisSelf(final double angle, final double px,
	    final double py, final double pz, final double ax, final double ay,
	    final double az) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(
		ax, ay, az));
	raa.applySelfAsPoint(this);
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.geom.WB_MutableCoordinateTransform#rotateAboutAxisSelf(double,
     * wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point rotateAboutAxisSelf(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p, a);
	raa.applySelfAsPoint(this);
	return this;
    }
}
