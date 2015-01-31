package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

public class WB_SequencePoint extends WB_SimpleSequenceVector implements
	WB_MutableCoordinateFull {
    public WB_SequencePoint(final int i, final WB_CoordinateSequence seq) {
	super(i, seq);
    }

    @Override
    public WB_SequencePoint addMulSelf(final double f, final double x,
	    final double y, final double z) {
	set(xd() + f * x, yd() + f * y, zd() + f * z);
	return this;
    }

    @Override
    public WB_SequencePoint addMulSelf(final double f, final WB_Coordinate p) {
	set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	return this;
    }

    @Override
    public WB_SequencePoint addSelf(final double x, final double y,
	    final double z) {
	set(xd() + x, yd() + y, zd() + z);
	return this;
    }

    @Override
    public WB_SequencePoint addSelf(final WB_Coordinate p) {
	set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	return this;
    }

    @Override
    public WB_SequencePoint applyAsNormalSelf(final WB_Transform T) {
	T.applyAsNormal(this, this);
	return this;
    }

    @Override
    public WB_SequencePoint applyAsPointSelf(final WB_Transform T) {
	T.applyAsPoint(this, this);
	return this;
    }

    @Override
    public WB_SequencePoint applyAsVectorSelf(final WB_Transform T) {
	T.applyAsVector(this, this);
	return this;
    }

    @Override
    public WB_SequencePoint crossSelf(final WB_Coordinate p) {
	set(yd() * p.zd() - this.zd() * p.yd(), this.zd() * p.xd() - this.xd()
		* p.zd(), this.xd() * p.yd() - yd() * p.xd());
	return this;
    }

    @Override
    public WB_SequencePoint divSelf(final double f) {
	return mulSelf(1.0 / f);
    }

    public void invert() {
	mulSelf(-1);
    }

    @Override
    public WB_SequencePoint mulAddMulSelf(final double f, final double g,
	    final WB_Coordinate p) {
	set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g * p.zd());
	return this;
    }

    @Override
    public WB_SequencePoint mulSelf(final double f) {
	set(f * xd(), f * yd(), f * zd());
	return this;
    }

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

    @Override
    public WB_SequencePoint scaleSelf(final double f) {
	return mulSelf(f);
    }

    @Override
    public WB_SequencePoint scaleSelf(final double fx, final double fy,
	    final double fz) {
	set(xd() * fx, yd() * fy, zd() * fz);
	return this;
    }

    @Override
    public WB_Point scale(final double f) {
	return mul(f);
    }

    @Override
    public WB_Point scale(final double fx, final double fy, final double fz) {
	return new WB_Point(xd() * fx, yd() * fy, zd() * fz);
    }

    @Override
    public WB_SequencePoint subSelf(final double x, final double y,
	    final double z) {
	set(xd() - x, yd() - y, zd() - z);
	return this;
    }

    @Override
    public WB_SequencePoint subSelf(final WB_Coordinate v) {
	set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
	return this;
    }

    @Override
    public WB_SequencePoint trimSelf(final double d) {
	if (getSqLength3D() > d * d) {
	    normalizeSelf();
	    mulSelf(d);
	}
	return this;
    }

    @Override
    public double absDot(final WB_Coordinate p) {
	return WB_Math.fastAbs(WB_CoordinateOp.dot(xd(), yd(), zd(), p.xd(),
		p.yd(), p.zd()));
    }

    @Override
    public double absDot2D(final WB_Coordinate p) {
	return WB_Math
		.fastAbs(WB_CoordinateOp.dot2D(xd(), yd(), p.xd(), p.yd()));
    }

    @Override
    public WB_Point add(final double x, final double y, final double z) {
	return new WB_Point(this.xd() + x, this.yd() + y, this.zd() + z);
    }

    @Override
    public void addInto(final double x, final double y, final double z,
	    final WB_MutableCoordinate result) {
	result.set(this.xd() + x, this.yd() + y, this.zd() + z);
    }

    @Override
    public WB_Point add(final WB_Coordinate p) {
	return new WB_Point(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
    }

    @Override
    public void addInto(final WB_Coordinate p, final WB_MutableCoordinate result) {
	result.set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
    }

    @Override
    public WB_Point addMul(final double f, final double x, final double y,
	    final double z) {
	return new WB_Point(this.xd() + f * x, this.yd() + f * y, this.zd() + f
		* z);
    }

    @Override
    public void addMulInto(final double f, final double x, final double y,
	    final double z, final WB_MutableCoordinate result) {
	result.set(this.xd() + f * x, this.yd() + f * y, this.zd() + f * z);
    }

    @Override
    public WB_Point addMul(final double f, final WB_Coordinate p) {
	return new WB_Point(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f
		* p.zd());
    }

    @Override
    public void addMulInto(final double f, final WB_Coordinate p,
	    final WB_MutableCoordinate result) {
	result.set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
    }

    @Override
    public WB_Point apply(final WB_Transform T) {
	return applyAsPoint(T);
    }

    @Override
    public void applyInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsPoint(this, result);
    }

    @Override
    public WB_SequencePoint applySelf(final WB_Transform T) {
	return applyAsPointSelf(T);
    }

    @Override
    public WB_Point applyAsNormal(final WB_Transform T) {
	final WB_Point result = new WB_Point();
	T.applyAsNormal(this, result);
	return result;
    }

    @Override
    public void applyAsNormalInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsNormal(this, result);
    }

    @Override
    public WB_Point applyAsPoint(final WB_Transform T) {
	final WB_Point result = new WB_Point();
	T.applyAsPoint(this, result);
	return result;
    }

    @Override
    public void applyAsPointInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsPoint(this, result);
    }

    @Override
    public WB_Point applyAsVector(final WB_Transform T) {
	final WB_Point result = new WB_Point();
	T.applyAsVector(this, result);
	return result;
    }

    @Override
    public void applyAsVectorInto(final WB_Transform T,
	    final WB_MutableCoordinate result) {
	T.applyAsVector(this, result);
    }

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

    public double[] coords() {
	return new double[] { xd(), yd(), zd() };
    }

    @Override
    public WB_Point cross(final WB_Coordinate p) {
	return new WB_Point(yd() * p.zd() - zd() * p.yd(), zd() * p.xd() - xd()
		* p.zd(), xd() * p.yd() - yd() * p.xd());
    }

    @Override
    public void crossInto(final WB_Coordinate p,
	    final WB_MutableCoordinate result) {
	result.set(yd() * p.zd() - zd() * p.yd(),
		zd() * p.xd() - xd() * p.zd(), xd() * p.yd() - yd() * p.xd());
    }

    @Override
    public WB_Point div(final double f) {
	return mul(1.0 / f);
    }

    @Override
    public void divInto(final double f, final WB_MutableCoordinate result) {
	mulInto(1.0 / f, result);
    }

    @Override
    public double dot(final WB_Coordinate p) {
	return WB_CoordinateOp.dot(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
    }

    @Override
    public double dot2D(final WB_Coordinate p) {
	return WB_CoordinateOp.dot2D(xd(), yd(), p.xd(), p.yd());
    }

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

    public WB_Point get() {
	return new WB_Point(xd(), yd(), zd());
    }

    @Override
    public double getAngle(final WB_Coordinate p) {
	return WB_CoordinateOp.angleBetween(xd(), yd(), zd(), p.xd(), p.yd(),
		p.zd());
    }

    @Override
    public double getAngleNorm(final WB_Coordinate p) {
	return WB_CoordinateOp.angleBetweenNorm(xd(), yd(), zd(), p.xd(),
		p.yd(), p.zd());
    }

    @Override
    public double getDistance3D(final WB_Coordinate p) {
	return WB_CoordinateOp.getDistance3D(xd(), yd(), zd(), p.xd(), p.yd(),
		p.zd());
    }

    @Override
    public double getDistance2D(final WB_Coordinate p) {
	return WB_CoordinateOp.getDistance2D(xd(), yd(), p.xd(), p.yd());
    }

    @Override
    public double getLength3D() {
	return WB_CoordinateOp.getLength3D(xd(), yd(), zd());
    }

    @Override
    public double getLength2D() {
	return WB_CoordinateOp.getLength2D(xd(), yd());
    }

    @Override
    public double getSqDistance3D(final WB_Coordinate p) {
	return WB_CoordinateOp.getSqDistance3D(xd(), yd(), zd(), p.xd(),
		p.yd(), p.zd());
    }

    @Override
    public double getSqDistance2D(final WB_Coordinate p) {
	return WB_CoordinateOp.getSqDistance2D(xd(), yd(), p.xd(), p.yd());
    }

    @Override
    public double getSqLength3D() {
	return WB_CoordinateOp.getSqLength3D(xd(), yd(), zd());
    }

    @Override
    public double getSqLength2D() {
	return WB_CoordinateOp.getSqLength2D(xd(), yd());
    }

    @Override
    public int hashCode() {
	return WB_CoordinateOp.calculateHashCode(xd(), yd(), zd());
    }

    @Override
    public double heading2D() {
	return Math.atan2(yd(), xd());
    }

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

    public boolean isParallel(final WB_Coordinate p) {
	final double pm2 = p.xd() * p.xd() + p.yd() * p.yd() + p.zd() * p.zd();
	return (cross(p).getSqLength3D() / (pm2 * getSqLength3D()) < WB_Epsilon.SQEPSILON);
    }

    public boolean isParallel(final WB_Coordinate p, final double t) {
	final double pm2 = p.xd() * p.xd() + p.yd() * p.yd() + p.zd() * p.zd();
	return (cross(p).getSqLength3D() / (pm2 * getSqLength3D()) < t
		+ WB_Epsilon.SQEPSILON);
    }

    public boolean isParallelNorm(final WB_Coordinate p) {
	return (cross(p).getSqLength3D() < WB_Epsilon.SQEPSILON);
    }

    public boolean isParallelNorm(final WB_Coordinate p, final double t) {
	return (cross(p).getSqLength3D() < t + WB_Epsilon.SQEPSILON);
    }

    @Override
    public boolean isZero() {
	return WB_CoordinateOp.isZero3D(xd(), yd(), zd());
    }

    @Override
    public WB_Point mul(final double f) {
	return new WB_Point(xd() * f, yd() * f, zd() * f);
    }

    @Override
    public void mulInto(final double f, final WB_MutableCoordinate result) {
	scale(f, result);
    }

    @Override
    public WB_Point mulAddMul(final double f, final double g,
	    final WB_Coordinate p) {
	return new WB_Point(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f
		* zd() + g * p.zd());
    }

    @Override
    public void mulAddMulInto(final double f, final double g,
	    final WB_Coordinate p, final WB_MutableCoordinate result) {
	result.set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g
		* p.zd());
    }

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

    @Override
    public WB_SequencePoint rotateAbout2PointAxisSelf(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p1, new WB_Point(p1, p2));
	raa.applySelfAsPoint(this);
	return this;
    }

    @Override
    public WB_SequencePoint rotateAboutAxisSelf(final double angle,
	    final WB_Coordinate p, final WB_Coordinate a) {
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p, a);
	raa.applySelfAsPoint(this);
	return this;
    }

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

    @Override
    public WB_Point rotateAbout2PointAxis(final double angle,
	    final WB_Coordinate p1, final WB_Coordinate p2) {
	final WB_Point result = new WB_Point(this);
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p1, new WB_Point(p1, p2));
	raa.applySelfAsPoint(result);
	return result;
    }

    @Override
    public WB_Point rotateAboutAxis(final double angle, final WB_Coordinate p,
	    final WB_Coordinate a) {
	final WB_Point result = new WB_Point(this);
	final WB_Transform raa = new WB_Transform();
	raa.addRotateAboutAxis(angle, p, a);
	raa.applySelfAsPoint(result);
	return result;
    }

    @Override
    public double scalarTriple(final WB_Coordinate v, final WB_Coordinate w) {
	return WB_CoordinateOp.scalarTriple(xd(), yd(), zd(), v.xd(), v.yd(),
		v.zd(), w.xd(), w.yd(), w.zd());
    }

    public void scale(final double f, final WB_MutableCoordinate result) {
	result.set(xd() * f, yd() * f, zd() * f);
    }

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

    @Override
    public WB_Point sub(final double x, final double y, final double z) {
	return new WB_Point(this.xd() - x, this.yd() - y, this.zd() - z);
    }

    @Override
    public void subInto(final double x, final double y, final double z,
	    final WB_MutableCoordinate result) {
	result.set(this.xd() - x, this.yd() - y, this.zd() - z);
    }

    @Override
    public WB_Point sub(final WB_Coordinate p) {
	return new WB_Point(this.xd() - p.xd(), this.yd() - p.yd(), this.zd()
		- p.zd());
    }

    @Override
    public void subInto(final WB_Coordinate p, final WB_MutableCoordinate result) {
	result.set(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
    }

    @Override
    public WB_M33 tensor(final WB_Coordinate v) {
	return new WB_M33(WB_CoordinateOp.tensor3D(xd(), yd(), zd(), v.xd(),
		v.yd(), v.zd()));
    }

    @Override
    public String toString() {
	return "WB_SequenceVector [x=" + xd() + ", y=" + yd() + ", z=" + zd()
		+ "]";
    }

    @Override
    public WB_Point getOrthoNormal2D() {
	final WB_Point a = new WB_Point(-yd(), xd(), 0);
	a.normalizeSelf();
	return a;
    }

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

    public WB_Vector subToVector2D(final double x, final double y,
	    final double z) {
	return new WB_Vector(this.xd() - x, this.yd() - y, 0);
    }

    public WB_Vector subToVector2D(final WB_Coordinate p) {
	return new WB_Vector(xd() - p.xd(), yd() - p.yd(), 0);
    }

    public WB_Vector subToVector3D(final double x, final double y,
	    final double z) {
	return new WB_Vector(this.xd() - x, this.yd() - y, this.zd() - z);
    }

    public WB_Vector subToVector3D(final WB_Coordinate p) {
	return new WB_Vector(xd() - p.xd(), yd() - p.yd(), zd() - p.zd());
    }
}
