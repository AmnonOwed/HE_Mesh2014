package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

public class WB_IndexedVector extends WB_AbstractSeqVector {

	public WB_IndexedVector(int i, WB_CoordinateSequence seq) {
		super(i, seq);
	}

	public WB_IndexedVector _addMulSelf(final double f, final double x,
			final double y, final double z) {
		_set(xd() + f * x, yd() + f * y, zd() + f * z);
		return this;
	}

	public WB_IndexedVector _addMulSelf(final double f, final WB_Coordinate p) {
		_set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());

		return this;
	}

	public WB_IndexedVector _addSelf(final double x, final double y,
			final double z) {
		_set(xd() + x, yd() + y, zd() + z);
		return this;
	}

	public WB_IndexedVector _addSelf(final WB_Coordinate p) {
		_set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
		return this;
	}

	public WB_IndexedVector _applyAsNormalSelf(final WB_Transform T) {
		T.applyAsNormal(this, this);
		return this;
	}

	public WB_IndexedVector _applyAsPointSelf(final WB_Transform T) {
		T.applyAsPoint(this, this);
		return this;
	}

	public WB_IndexedVector _applyAsVectorSelf(final WB_Transform T) {
		T.applyAsVector(this, this);
		return this;
	}

	public WB_IndexedVector _crossSelf(final WB_Coordinate p) {
		_set(yd() * p.zd() - this.zd() * p.yd(), this.zd() * p.xd() - this.xd()
				* p.zd(), this.xd() * p.yd() - yd() * p.xd());
		return this;
	}

	public WB_IndexedVector _divSelf(final double f) {
		return _mulSelf(1.0 / f);
	}

	public void _invert() {
		_mulSelf(-1);
	}

	public WB_IndexedVector _mulAddMulSelf(final double f, final double g,
			final WB_Coordinate p) {
		_set(f * xd() + g * p.xd(), f * yd() + g * p.yd(),
				f * zd() + g * p.zd());

		return this;
	}

	public WB_IndexedVector _mulSelf(final double f) {
		_set(f * xd(), f * yd(), f * zd());
		return this;
	}

	public double _normalizeSelf() {
		final double d = getLength();
		if (WB_Epsilon.isZero(d)) {
			_set(0, 0, 0);
		} else {
			_set(xd() / d, yd() / d, zd() / d);
		}
		return d;
	}

	public WB_IndexedVector _scaleSelf(final double f) {
		_mulSelf(f);
		return this;
	}

	public WB_IndexedVector _scaleSelf(final double fx, final double fy,
			final double fz) {
		_set(xd() * fx, yd() * fy, zd() * fz);
		return this;
	}

	public WB_IndexedVector _subSelf(final double x, final double y,
			final double z) {
		_set(xd() - x, yd() - y, zd() - z);
		return this;
	}

	public WB_IndexedVector _subSelf(final WB_Coordinate v) {
		_set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
		return this;
	}

	public void _trimSelf(final double d) {
		if (getSqLength() > d * d) {
			_normalizeSelf();
			_mulSelf(d);
		}
	}

	public double absDot(final WB_Coordinate p) {
		return WB_Math.fastAbs(WB_CoordinateMath.dot(xd(), yd(), zd(), p.xd(),
				p.yd(), p.zd()));
	}

	public double absDot2D(final WB_Coordinate p) {
		return WB_Math.fastAbs(WB_CoordinateMath.dot2D(xd(), yd(), p.xd(),
				p.yd()));
	}

	public WB_Vector add(final double x, final double y, final double z) {
		return new WB_Vector(this.xd() + x, this.yd() + y, this.zd() + z);
	}

	public void add(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.xd() + x, this.yd() + y, this.zd() + z);
	}

	public WB_Vector add(final WB_Coordinate p) {
		return new WB_Vector(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	public void add(final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	public WB_Vector addMul(final double f, final double x, final double y,
			final double z) {
		return new WB_Vector(this.xd() + f * x, this.yd() + f * y, this.zd()
				+ f * z);
	}

	public void addMul(final double f, final double x, final double y,
			final double z, final WB_MutableCoordinate result) {
		result._set(this.xd() + f * x, this.yd() + f * y, this.zd() + f * z);
	}

	public WB_Vector addMul(final double f, final WB_Coordinate p) {
		return new WB_Vector(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f
				* p.zd());
	}

	public void addMul(final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	}

	public void addMulInto(final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	}

	public WB_Vector applySelf(final WB_Transform T) {
		return applyAsVector(T);
	}

	public WB_Vector applyAsNormal(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsNormal(this, result);

		return result;
	}

	public void applyAsNormalInto(final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsNormal(this, result);
	}

	public WB_Vector applyAsPoint(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsPoint(this, result);
		return result;
	}

	public void applyAsPointInto(final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsPoint(this, result);

	}

	public WB_Vector applyAsVector(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsVector(this, result);
		return result;
	}

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

	public WB_Vector cross(final WB_Coordinate p) {
		return new WB_Vector(yd() * p.zd() - zd() * p.yd(), zd() * p.xd()
				- xd() * p.zd(), xd() * p.yd() - yd() * p.xd());
	}

	public void crossInto(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(yd() * p.zd() - zd() * p.yd(),
				zd() * p.xd() - xd() * p.zd(), xd() * p.yd() - yd() * p.xd());
	}

	public WB_Vector div(final double f) {
		return mul(1.0 / f);
	}

	public void div(final double f, final WB_MutableCoordinate result) {
		mul(1.0 / f, result);
	}

	public double dot(final WB_Coordinate p) {
		return WB_CoordinateMath.dot(xd(), yd(), zd(), p.xd(), p.yd(), p.zd());
	}

	public double dot2D(final WB_Coordinate p) {
		return WB_CoordinateMath.dot2D(xd(), yd(), p.xd(), p.yd());
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_IndexedVector)) {
			return false;
		}
		final WB_IndexedVector p = (WB_IndexedVector) o;
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

	public WB_Vector get() {
		return new WB_Vector(xd(), yd(), zd());
	}

	public double getAngle(final WB_Coordinate p) {
		return WB_CoordinateMath.angleBetween(xd(), yd(), zd(), p.xd(), p.yd(),
				p.zd());
	}

	public double getAngleNorm(final WB_Coordinate p) {
		return WB_CoordinateMath.angleBetweenNorm(xd(), yd(), zd(), p.xd(),
				p.yd(), p.zd());
	}

	public double getDistance(final WB_Coordinate p) {
		return WB_CoordinateMath.getDistance(xd(), yd(), zd(), p.xd(), p.yd(),
				p.zd());
	}

	public double getDistance2D(final WB_Coordinate p) {
		return WB_CoordinateMath.getDistance2D(xd(), yd(), p.xd(), p.yd());
	}

	public double getLength() {
		return WB_CoordinateMath.getLength(xd(), yd(), zd());
	}

	public double getLength2D() {
		return WB_CoordinateMath.getLength2D(xd(), yd());
	}

	public double getSqDistance(final WB_Coordinate p) {
		return WB_CoordinateMath.getSqDistance(xd(), yd(), zd(), p.xd(),
				p.yd(), p.zd());
	}

	public double getSqDistance2D(final WB_Coordinate p) {
		return WB_CoordinateMath.getSqDistance2D(xd(), yd(), p.xd(), p.yd());
	}

	public double getSqLength() {
		return WB_CoordinateMath.getSqLength(xd(), yd(), zd());
	}

	public double getSqLength2D() {
		return WB_CoordinateMath.getSqLength2D(xd(), yd());
	}

	@Override
	public int hashCode() {

		return WB_CoordinateMath.calculateHashCode(xd(), yd(), zd());

	}

	public double heading() {
		return Math.atan2(yd(), xd());
	}

	public boolean isCollinear(final WB_Coordinate p, final WB_Coordinate q) {
		if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToPoint2D(p, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToPoint2D(this, q))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToPoint2D(this, p))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(sub(p).cross(sub(q)).getSqLength());
	}

	public boolean isParallel(final WB_Coordinate p) {
		double pm2 = p.xd() * p.xd() + p.yd() * p.yd() + p.zd() * p.zd();
		return (cross(p).getSqLength() / (pm2 * getSqLength()) < WB_Epsilon.SQEPSILON);
	}

	public boolean isParallel(final WB_Coordinate p, final double t) {
		double pm2 = p.xd() * p.xd() + p.yd() * p.yd() + p.zd() * p.zd();
		return (cross(p).getSqLength() / (pm2 * getSqLength()) < t
				+ WB_Epsilon.SQEPSILON);
	}

	public boolean isParallelNorm(final WB_Coordinate p) {
		return (cross(p).getSqLength() < WB_Epsilon.SQEPSILON);
	}

	public boolean isParallelNorm(final WB_Coordinate p, final double t) {
		return (cross(p).getSqLength() < t + WB_Epsilon.SQEPSILON);
	}

	public boolean isZero() {
		return WB_CoordinateMath.isZero(xd(), yd(), zd());
	}

	public WB_Vector mul(final double f) {
		return new WB_Vector(xd() * f, yd() * f, zd() * f);
	}

	public void mul(final double f, final WB_MutableCoordinate result) {
		scale(f, result);
	}

	public WB_Vector mulAddMul(final double f, final double g,
			final WB_Coordinate p) {
		return new WB_Vector(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f
				* zd() + g * p.zd());
	}

	public void mulAddMulInto(final double f, final double g,
			final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g
				* p.zd());
	}

	public void rotateAboutAxis(final double angle, final double p1x,
			final double p1y, final double p1z, final double p2x,
			final double p2y, final double p2z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z),
				new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelfAsVector(this);
	}

	public void rotateAboutAxis(final double angle, final WB_Coordinate p1,
			final WB_Coordinate p2) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));

		raa.applySelfAsVector(this);

	}

	public void rotateAboutAxis(final double angle, final WB_Coordinate p,
			final WB_IndexedVector a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);

		raa.applySelfAsVector(this);

	}

	public double scalarTriple(final WB_Coordinate p, final WB_Coordinate q) {
		return WB_CoordinateMath.scalarTriple(this, p, q);
	}

	public void scale(final double f, final WB_MutableCoordinate result) {
		result._set(xd() * f, yd() * f, zd() * f);
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

	public WB_Vector sub(final double x, final double y, final double z) {
		return new WB_Vector(this.xd() - x, this.yd() - y, this.zd() - z);
	}

	public void sub(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.xd() - x, this.yd() - y, this.zd() - z);
	}

	public WB_Vector sub(final WB_Coordinate p) {
		return new WB_Vector(this.xd() - p.xd(), this.yd() - p.yd(), this.zd()
				- p.zd());
	}

	public void sub(final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(this.xd() - p.xd(), this.yd() - p.yd(), this.zd() - p.zd());
	}

	public WB_M33 tensor(final WB_Coordinate q) {
		return WB_CoordinateMath.tensor(this, q);

	}

	@Override
	public String toString() {
		return "WB_Vector [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]";
	}

	public WB_Vector getOrthoNormal2D() {
		final WB_Vector a = new WB_Vector(-yd(), xd(), 0);
		a._normalizeSelf();
		return a;
	}

	public WB_Vector getOrthoNormal3D() {
		if (Math.abs(zd()) > WB_Epsilon.EPSILON) {
			final WB_Vector a = new WB_Vector(1, 0, -xd() / zd());
			a._normalizeSelf();
			return a;
		} else {
			return new WB_Vector(0, 0, 1);
		}

	}

}
