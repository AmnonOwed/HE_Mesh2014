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
public class WB_SequencePoint extends WB_SimpleSequenceVector implements
	WB_MutableCoordinateFull {
    /**
     *
     *
     * @param i
     * @param seq
     */
    public WB_SequencePoint(final int i, final WB_CoordinateSequence seq) {
	super(i, seq);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#addMulSelf(double, double,
     * double, double)
     */
    @Override
    public WB_SequencePoint addMulSelf(final double f, final double x,
	    final double y, final double z) {
	set(xd() + (f * x), yd() + (f * y), zd() + (f * z));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#addMulSelf(double,
     * wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_SequencePoint addMulSelf(final double f, final WB_Coordinate p) {
	set(xd() + (f * p.xd()), yd() + (f * p.yd()), zd() + (f * p.zd()));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#addSelf(double, double, double)
     */
    @Override
    public WB_SequencePoint addSelf(final double x, final double y,
	    final double z) {
	set(xd() + x, yd() + y, zd() + z);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#addSelf(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_SequencePoint addSelf(final WB_Coordinate p) {
	set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#applyAsNormalSelf(wblut.geom.WB_Transform
     * )
     */
    @Override
    public WB_SequencePoint applyAsNormalSelf(final WB_Transform T) {
	T.applyAsNormal(this, this);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#applyAsPointSelf(wblut.geom.WB_Transform
     * )
     */
    @Override
    public WB_SequencePoint applyAsPointSelf(final WB_Transform T) {
	T.applyAsPoint(this, this);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#applyAsVectorSelf(wblut.geom.WB_Transform
     * )
     */
    @Override
    public WB_SequencePoint applyAsVectorSelf(final WB_Transform T) {
	T.applyAsVector(this, this);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#crossSelf(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_SequencePoint crossSelf(final WB_Coordinate p) {
	set((yd() * p.zd()) - (this.zd() * p.yd()), (this.zd() * p.xd())
		- (this.xd() * p.zd()), (this.xd() * p.yd()) - (yd() * p.xd()));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#divSelf(double)
     */
    @Override
    public WB_SequencePoint divSelf(final double f) {
	return mulSelf(1.0 / f);
    }

    /**
     *
     */
    public void invert() {
	mulSelf(-1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#mulAddMulSelf(double, double,
     * wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_SequencePoint mulAddMulSelf(final double f, final double g,
	    final WB_Coordinate p) {
	set((f * xd()) + (g * p.xd()), (f * yd()) + (g * p.yd()), (f * zd())
		+ (g * p.zd()));
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#mulSelf(double)
     */
    @Override
    public WB_SequencePoint mulSelf(final double f) {
	set(f * xd(), f * yd(), f * zd());
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#normalizeSelf()
     */
    @Override
    public double normalizeSelf() {
	final double d = getLength3D();
	if (WB_Epsilon.isZero(d)) {
	    set(0, 0, 0);
	} else {
	    set(xd() / d, yd() / d, zd() / d);
	}
	return d;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateTransform#scaleSelf(double)
     */
    @Override
    public WB_SequencePoint scaleSelf(final double f) {
	return mulSelf(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateTransform#scaleSelf(double, double,
     * double)
     */
    @Override
    public WB_SequencePoint scaleSelf(final double fx, final double fy,
	    final double fz) {
	set(xd() * fx, yd() * fy, zd() * fz);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateTransform#scale(double)
     */
    @Override
    public WB_Point scale(final double f) {
	return mul(f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateTransform#scale(double, double, double)
     */
    @Override
    public WB_Point scale(final double fx, final double fy, final double fz) {
	return new WB_Point(xd() * fx, yd() * fy, zd() * fz);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#subSelf(double, double, double)
     */
    @Override
    public WB_SequencePoint subSelf(final double x, final double y,
	    final double z) {
	set(xd() - x, yd() - y, zd() - z);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#subSelf(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_SequencePoint subSelf(final WB_Coordinate v) {
	set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_MutableCoordinateMath#trimSelf(double)
     */
    @Override
    public WB_SequencePoint trimSelf(final double d) {
	if (getSqLength3D() > (d * d)) {
	    normalizeSelf();
	    mulSelf(d);
	}
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#absDot(wblut.geom.WB_Coordinate)
     */
    @Override
    public double absDot(final WB_Coordinate p) {
	return WB_Math.fastAbs(WB_CoordinateOp.dot(xd(), yd(), zd(), p.xd(),
		p.yd(), p.zd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#absDot2D(wblut.geom.WB_Coordinate)
     */
    @Override
    public double absDot2D(final WB_Coordinate p) {
	return WB_Math
		.fastAbs(WB_CoordinateOp.dot2D(xd(), yd(), p.xd(), p.yd()));
    }

    @Override
    public WB_Point add(final double... x) {
	return new WB_Point(this.xd() + x[0], this.yd() + x[1], this.zd()
		+ x[2]);
    }

    @Override
    public void addInto(final WB_MutableCoordinate result, final double... x) {
	result.set(this.xd() + x[0], this.yd() + x[1], this.zd() + x[2]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#add(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point add(final WB_Coordinate p) {
	return new WB_Point(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#addInto(wblut.geom.WB_Coordinate,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void addInto(final WB_MutableCoordinate result, final WB_Coordinate p) {
	result.set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
    }

    @Override
    public WB_Point addMul(final double f, final double... x) {
	return new WB_Point(this.xd() + (f * x[0]), this.yd() + (f * x[1]),
		this.zd() + (f * x[2]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#addMulInto(double, double, double,
     * double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void addMulInto(final WB_MutableCoordinate result, final double f,
	    final double... x) {
	result.set(this.xd() + (f * x[0]), this.yd() + (f * x[1]), this.zd()
		+ (f * x[2]));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#addMul(double,
     * wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point addMul(final double f, final WB_Coordinate p) {
	return new WB_Point(xd() + (f * p.xd()), yd() + (f * p.yd()), zd()
		+ (f * p.zd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#addMulInto(double,
     * wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void addMulInto(final WB_MutableCoordinate result, final double f,
	    final WB_Coordinate p) {
	result.set(xd() + (f * p.xd()), yd() + (f * p.yd()), zd()
		+ (f * p.zd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateTransform#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Point apply(final WB_Transform T) {
	return applyAsPoint(T);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateTransform#applyInto(wblut.geom.WB_Transform,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void applyInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsPoint(this, result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateMath#applySelf(wblut.geom.WB_Transform)
     */
    @Override
    public WB_SequencePoint applySelf(final WB_Transform T) {
	return applyAsPointSelf(T);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateTransform#applyAsNormal(wblut.geom.WB_Transform)
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
     * @see
     * wblut.geom.WB_CoordinateTransform#applyAsNormalInto(wblut.geom.WB_Transform
     * , wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void applyAsNormalInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsNormal(this, result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateTransform#applyAsPoint(wblut.geom.WB_Transform)
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
     * @see
     * wblut.geom.WB_CoordinateTransform#applyAsPointInto(wblut.geom.WB_Transform
     * , wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void applyAsPointInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsPoint(this, result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateTransform#applyAsVector(wblut.geom.WB_Transform)
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
     * @see
     * wblut.geom.WB_CoordinateTransform#applyAsVectorInto(wblut.geom.WB_Transform
     * , wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void applyAsVectorInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsVector(this, result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_SimpleSequenceVector#compareTo(wblut.geom.WB_Coordinate)
     */
    @Override
    public int compareTo(final WB_Coordinate p) {
	int cmp = Double.compare(xd(), p.xd());
	if (cmp != 0) {
	    return cmp;
	}
	cmp = Double.compare(yd(), p.yd());
	if (cmp != 0) {
	    return cmp;
	}
	cmp = Double.compare(zd(), p.zd());
	if (cmp != 0) {
	    return cmp;
	}
	return Double.compare(wd(), p.wd());
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public int compareToY1st(final WB_Coordinate p) {
	int cmp = Double.compare(yd(), p.yd());
	if (cmp != 0) {
	    return cmp;
	}
	cmp = Double.compare(xd(), p.xd());
	if (cmp != 0) {
	    return cmp;
	}
	cmp = Double.compare(zd(), p.zd());
	if (cmp != 0) {
	    return cmp;
	}
	return Double.compare(wd(), p.wd());
    }

    /**
     *
     *
     * @return
     */
    public double[] coords() {
	return new double[] { xd(), yd(), zd() };
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#cross(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point cross(final WB_Coordinate p) {
	return new WB_Point((yd() * p.zd()) - (zd() * p.yd()), (zd() * p.xd())
		- (xd() * p.zd()), (xd() * p.yd()) - (yd() * p.xd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#crossInto(wblut.geom.WB_Coordinate,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void crossInto(final WB_MutableCoordinate result,
	    final WB_Coordinate p) {
	result.set((yd() * p.zd()) - (zd() * p.yd()), (zd() * p.xd())
		- (xd() * p.zd()), (xd() * p.yd()) - (yd() * p.xd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#div(double)
     */
    @Override
    public WB_Point div(final double f) {
	return mul(1.0 / f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#divInto(double,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void divInto(final WB_MutableCoordinate result, final double f) {
	mulInto(result, 1.0 / f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#dot(wblut.geom.WB_Coordinate)
     */
    @Override
    public double dot(final WB_Coordinate p) {
	return WB_CoordinateOp.dot(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#dot2D(wblut.geom.WB_Coordinate)
     */
    @Override
    public double dot2D(final WB_Coordinate p) {
	return WB_CoordinateOp.dot2D(xd(), yd(), p.xd(), p.yd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object o) {
	if (o == null) {
	    return false;
	}
	if (o == this) {
	    return true;
	}
	if (!(o instanceof WB_SequencePoint)) {
	    return false;
	}
	return ((WB_SequencePoint) o).getIndex() == getIndex();
    }

    /**
     *
     *
     * @return
     */
    public WB_Point get() {
	return new WB_Point(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#getAngle(wblut.geom.WB_Coordinate)
     */
    @Override
    public double getAngle(final WB_Coordinate p) {
	return WB_CoordinateOp.angleBetween(xd(), yd(), zd(), p.xd(), p.yd(),
		p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateMetric#getAngleNorm(wblut.geom.WB_Coordinate)
     */
    @Override
    public double getAngleNorm(final WB_Coordinate p) {
	return WB_CoordinateOp.angleBetweenNorm(xd(), yd(), zd(), p.xd(),
		p.yd(), p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateMetric#getDistance3D(wblut.geom.WB_Coordinate)
     */
    @Override
    public double getDistance3D(final WB_Coordinate p) {
	return WB_CoordinateOp.getDistance3D(xd(), yd(), zd(), p.xd(), p.yd(),
		p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateMetric#getDistance2D(wblut.geom.WB_Coordinate)
     */
    @Override
    public double getDistance2D(final WB_Coordinate p) {
	return WB_CoordinateOp.getDistance2D(xd(), yd(), p.xd(), p.yd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#getLength3D()
     */
    @Override
    public double getLength3D() {
	return WB_CoordinateOp.getLength3D(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#getLength2D()
     */
    @Override
    public double getLength2D() {
	return WB_CoordinateOp.getLength2D(xd(), yd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateMetric#getSqDistance3D(wblut.geom.WB_Coordinate)
     */
    @Override
    public double getSqDistance3D(final WB_Coordinate p) {
	return WB_CoordinateOp.getSqDistance3D(xd(), yd(), zd(), p.xd(),
		p.yd(), p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_CoordinateMetric#getSqDistance2D(wblut.geom.WB_Coordinate)
     */
    @Override
    public double getSqDistance2D(final WB_Coordinate p) {
	return WB_CoordinateOp.getSqDistance2D(xd(), yd(), p.xd(), p.yd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#getSqLength3D()
     */
    @Override
    public double getSqLength3D() {
	return WB_CoordinateOp.getSqLength3D(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#getSqLength2D()
     */
    @Override
    public double getSqLength2D() {
	return WB_CoordinateOp.getSqLength2D(xd(), yd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	return WB_CoordinateOp.calculateHashCode(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#heading2D()
     */
    @Override
    public double heading2D() {
	return Math.atan2(yd(), xd());
    }

    /**
     *
     *
     * @param p
     * @param q
     * @return
     */
    public boolean isCollinear(final WB_Coordinate p, final WB_Coordinate q) {
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(p, q))) {
	    return true;
	}
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(this, q))) {
	    return true;
	}
	if (WB_Epsilon.isZeroSq(WB_GeometryOp.getSqDistanceToPoint2D(this, p))) {
	    return true;
	}
	return WB_Epsilon.isZeroSq(sub(p).cross(sub(q)).getSqLength3D());
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public boolean isParallel(final WB_Coordinate p) {
	final double pm2 = (p.xd() * p.xd()) + (p.yd() * p.yd())
		+ (p.zd() * p.zd());
	return ((cross(p).getSqLength3D() / (pm2 * getSqLength3D())) < WB_Epsilon.SQEPSILON);
    }

    /**
     *
     *
     * @param p
     * @param t
     * @return
     */
    public boolean isParallel(final WB_Coordinate p, final double t) {
	final double pm2 = (p.xd() * p.xd()) + (p.yd() * p.yd())
		+ (p.zd() * p.zd());
	return ((cross(p).getSqLength3D() / (pm2 * getSqLength3D())) < (t + WB_Epsilon.SQEPSILON));
    }

    /**
     *
     *
     * @param p
     * @return
     */
    public boolean isParallelNorm(final WB_Coordinate p) {
	return (cross(p).getSqLength3D() < WB_Epsilon.SQEPSILON);
    }

    /**
     *
     *
     * @param p
     * @param t
     * @return
     */
    public boolean isParallelNorm(final WB_Coordinate p, final double t) {
	return (cross(p).getSqLength3D() < (t + WB_Epsilon.SQEPSILON));
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#isZero()
     */
    @Override
    public boolean isZero() {
	return WB_CoordinateOp.isZero3D(xd(), yd(), zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#mul(double)
     */
    @Override
    public WB_Point mul(final double f) {
	return new WB_Point(xd() * f, yd() * f, zd() * f);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#mulInto(double,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void mulInto(final WB_MutableCoordinate result, final double f) {
	scale(f, result);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#mulAddMul(double, double,
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
     * @see wblut.geom.WB_CoordinateMath#mulAddMulInto(double, double,
     * wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void mulAddMulInto(final WB_MutableCoordinate result,
	    final double f, final double g, final WB_Coordinate p) {
	result.set((f * xd()) + (g * p.xd()), (f * yd()) + (g * p.yd()),
		(f * zd()) + (g * p.zd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
     * , double, double, double, double, double, double)
     */
    @Override
    public WB_SequencePoint rotateAbout2PointAxisSelf(final double angle,
	    final double p1x, final double p1y, final double p1z,
	    final double p2x, final double p2y, final double p2z) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z),
		new WB_Point(p2x - p1x, p2y - p1y, p2z - p1z));
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
    public WB_SequencePoint rotateAbout2PointAxisSelf(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p1, new WB_Point(p1, p2));
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
    public WB_SequencePoint rotateAboutAxisSelf(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p, a);
	raa.applySelfAsPoint(this);
	return this;
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
	raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z),
		new WB_Point(p2x - p1x, p2y - p1y, p2z - p1z));
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
	raa.addRotateAboutAxis(angle, p1, new WB_Point(p1, p2));
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
     * @see wblut.geom.WB_CoordinateMath#scalarTriple(wblut.geom.WB_Coordinate,
     * wblut.geom.WB_Coordinate)
     */
    @Override
    public double scalarTriple(final WB_Coordinate v, final WB_Coordinate w) {
	return WB_CoordinateOp.scalarTriple(xd(), yd(), zd(), v.xd(), v.yd(),
		v.zd(), w.xd(), w.yd(), w.zd());
    }

    /**
     *
     *
     * @param f
     * @param result
     */
    public void scale(final double f, final WB_MutableCoordinate result) {
	result.set(xd() * f, yd() * f, zd() * f);
    }

    /**
     *
     *
     * @param otherXYZ
     * @return
     */
    public boolean smallerThan(final WB_Coordinate otherXYZ) {
	int _tmp = WB_Epsilon.compareAbs(xd(), otherXYZ.xd());
	if (_tmp != 0) {
	    return (_tmp < 0);
	}
	_tmp = WB_Epsilon.compareAbs(yd(), otherXYZ.yd());
	if (_tmp != 0) {
	    return (_tmp < 0);
	}
	_tmp = WB_Epsilon.compareAbs(zd(), otherXYZ.zd());
	return (_tmp < 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#sub(double, double, double)
     */
    @Override
    public WB_Point sub(final double... x) {
	return new WB_Point(this.xd() - x[0], this.yd() - x[1], this.zd()
		- x[2]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#subInto(double, double, double,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void subInto(final WB_MutableCoordinate result, final double... x) {
	result.set(this.xd() - x[0], this.yd() - x[1], this.zd() - x[2]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#sub(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_Point sub(final WB_Coordinate p) {
	return new WB_Point(this.xd() - p.xd(), this.yd() - p.yd(), this.zd()
		- p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#subInto(wblut.geom.WB_Coordinate,
     * wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void subInto(final WB_MutableCoordinate result, final WB_Coordinate p) {
	result.set(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMath#tensor(wblut.geom.WB_Coordinate)
     */
    @Override
    public WB_M33 tensor(final WB_Coordinate v) {
	return new WB_M33(WB_CoordinateOp.tensor3D(xd(), yd(), zd(), v.xd(),
		v.yd(), v.zd()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "WB_SequenceVector [x=" + xd() + ", y=" + yd() + ", z=" + zd()
		+ "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_CoordinateMetric#getOrthoNormal2D()
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
     * @see wblut.geom.WB_CoordinateMetric#getOrthoNormal3D()
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

    @Override
    public WB_Coordinate mulAddMul(final double f, final double g,
	    final double... x) {
	return new WB_Point(f * this.xd() + (g * x[0]), f * this.yd()
		+ (g * x[1]), f * this.zd() + (g * x[2]));
    }

    @Override
    public void mulAddMulInto(final WB_MutableCoordinate result,
	    final double f, final double g, final double... x) {
	result.set(f * this.xd() + (g * x[0]), f * this.yd() + (g * x[1]), f
		* this.zd() + (g * x[2]));
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
     * @see
     * wblut.geom.WB_MutableCoordinateTransform#rotateAbout2PointAxisSelf(double
     * , double, double, double, double, double, double)
     */
    @Override
    public WB_SequencePoint rotateAboutAxisSelf(final double angle,
	    final double px, final double py, final double pz, final double ax,
	    final double ay, final double az) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, new WB_Vector(px, py, pz), new WB_Vector(
		ax, ay, az));
	raa.applySelfAsPoint(this);
	return this;
    }
}
