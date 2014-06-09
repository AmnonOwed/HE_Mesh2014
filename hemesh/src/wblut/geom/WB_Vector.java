package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

public class WB_Vector implements Comparable<WB_Coordinate>,
		WB_MutableCoordinate {

	public static WB_Coordinate X() {
		return new WB_Vector(1, 0, 0);
	}

	public static WB_Coordinate Y() {
		return new WB_Vector(0, 1, 0);
	}

	public static WB_Coordinate Z() {
		return new WB_Vector(0, 0, 1);
	}

	public static WB_Coordinate ZERO() {
		return new WB_Vector(0, 0, 0);
	}

	/** Coordinates. */
	private double x, y, z;

	public WB_Vector() {
		x = y = z = 0;
	}

	public WB_Vector(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public WB_Vector(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WB_Vector(final double[] x) {
		this.x = x[0];
		this.y = x[1];
		this.z = x[2];
	}

	public WB_Vector(final double[] p1, final double[] p2) {
		this.x = p2[0] - p1[0];
		this.y = p2[1] - p1[1];
		this.z = p2[2] - p1[2];
	}

	public WB_Vector(final WB_Coordinate v) {
		x = v.xd();
		y = v.yd();
		z = v.zd();
	}

	public WB_Vector(final WB_Coordinate p1, final WB_Coordinate p2) {
		x = p2.xd() - p1.xd();
		y = p2.yd() - p1.yd();
		z = p2.zd() - p1.zd();
	}

	public WB_Vector _addMulSelf(final double f, final double x,
			final double y, final double z) {
		this.x += f * x;
		this.y += f * y;
		this.z += f * z;
		return this;
	}

	public WB_Vector _addMulSelf(final double f, final WB_Coordinate p) {
		_set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());

		return this;
	}

	public WB_Vector _addMulSelf(final double f,
			final WB_CoordinateSequence seq, final int i) {
		_set(xd() + f * seq.get(i, 0), yd() + f * seq.get(i, 1),
				zd() + f * seq.get(i, 2));

		return this;
	}

	public WB_Vector _addSelf(final double x, final double y, final double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public WB_Vector _addSelf(final WB_Coordinate p) {
		x += p.xd();
		y += p.yd();
		z += p.zd();
		return this;
	}

	public WB_Vector _addSelf(final WB_CoordinateSequence seq, final int i) {
		_set(xd() + seq.get(i, 0), yd() + seq.get(i, 1), zd() + seq.get(i, 2));

		return this;
	}

	public WB_Vector _applyAsNormalSelf(final WB_Transform T) {
		T.applyAsNormal(this, this);
		return this;
	}

	public WB_Vector _applyAsPointSelf(final WB_Transform T) {
		T.applyAsPoint(this, this);
		return this;
	}

	public WB_Vector _applyAsVectorSelf(final WB_Transform T) {
		T.applyAsVector(this, this);
		return this;
	}

	public WB_Vector _crossSelf(final WB_Coordinate p) {
		_set(y * p.zd() - z * p.yd(), z * p.xd() - x * p.zd(), x * p.yd() - y
				* p.xd());
		return this;
	}

	public WB_Vector _crossSelf(final WB_CoordinateSequence seq, final int i) {
		_set(yd() * seq.get(i, 2) - zd() * seq.get(i, 1), zd() * seq.get(i, 0)
				- xd() * seq.get(i, 2),
				xd() * seq.get(i, 1) - yd() * seq.get(i, 0));

		return this;
	}

	public WB_Vector _divSelf(final double f) {
		return _mulSelf(1.0 / f);
	}

	public void _invert() {
		x *= -1;
		y *= -1;
		z *= -1;
	}

	public WB_Vector _mulAddMulSelf(final double f, final double g,
			final WB_Coordinate p) {
		_set(f * xd() + g * p.xd(), f * yd() + g * p.yd(),
				f * zd() + g * p.zd());

		return this;
	}

	public WB_Vector _mulAddMulSelf(final double f, final double g,
			final WB_CoordinateSequence seq, final int i) {
		_set(f * xd() + g * seq.get(i, 0), f * yd() + g * seq.get(i, 1), f
				* zd() + g * seq.get(i, 2));

		return this;
	}

	public WB_Vector _mulSelf(final double f) {
		_scaleSelf(f);
		return this;
	}

	public double _normalizeSelf() {
		final double d = getLength();
		if (WB_Epsilon.isZero(d)) {
			_set(0, 0, 0);
		} else {
			_set(x / d, y / d, z / d);
		}
		return d;
	}

	public WB_Vector _scaleSelf(final double f) {
		x *= f;
		y *= f;
		z *= f;
		return this;
	}

	public WB_Vector _scaleSelf(final double fx, final double fy,
			final double fz) {
		x *= fx;
		y *= fy;
		z *= fz;
		return this;
	}

	public void _set(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public void _set(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void _set(double x, double y, double z, double w) {
		_set(x, y, z);

	}

	public void _set(final WB_Coordinate v) {
		_set(v.xd(), v.yd(), v.zd());
	}

	public void _set(final WB_CoordinateSequence seq, final int i) {
		_set(seq.get(i, 0), seq.get(i, 1), seq.get(i, 2), seq.get(i, 3));

	}

	@Override
	public void _setCoord(int i, double v) {
		if (i == 0) {
			this.x = v;
		}
		if (i == 1) {
			this.y = v;
		}
		if (i == 2) {
			this.z = v;
		}

	}

	@Override
	public void _setW(double w) {

	}

	@Override
	public void _setX(double x) {
		this.x = x;

	}

	@Override
	public void _setY(double y) {
		this.y = y;

	}

	@Override
	public void _setZ(double z) {
		this.z = z;

	}

	public WB_Vector _subSelf(final double x, final double y, final double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public WB_Vector _subSelf(final WB_Coordinate v) {
		x -= v.xd();
		y -= v.yd();
		z -= v.zd();
		return this;
	}

	public WB_Vector _subSelf(final WB_CoordinateSequence seq, final int i) {
		_set(xd() - seq.get(i, 0), yd() - seq.get(i, 1), zd() - seq.get(i, 2));

		return this;
	}

	public void _trimSelf(final double d) {
		if (getSqLength() > d * d) {
			_normalizeSelf();
			_mulSelf(d);
		}
	}

	public double absDot(final WB_Coordinate p) {
		return WB_Math.fastAbs(WB_CoordinateMath.dot(x, y, z, p.xd(), p.yd(),
				p.zd()));
	}

	public double absDot(final WB_CoordinateSequence seq, int i) {
		return WB_Math.fastAbs(WB_CoordinateMath.dot(x, y, z, seq.get(i, 0),
				seq.get(i, 1), seq.get(i, 2)));
	}

	public double absDot2D(final WB_Coordinate p) {
		return WB_Math.fastAbs(WB_CoordinateMath.dot2D(x, y, p.xd(), p.yd()));
	}

	public double absDot2D(final WB_CoordinateSequence seq, int i) {
		return WB_Math.fastAbs(WB_CoordinateMath.dot2D(x, y, seq.get(i, 0),
				seq.get(i, 1)));
	}

	public WB_Vector add(final double x, final double y, final double z) {
		return new WB_Vector(this.x + x, this.y + y, this.z + z);
	}

	public void add(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.x + x, this.y + y, this.z + z);
	}

	public WB_Vector add(final WB_Coordinate p) {
		return new WB_Vector(x + p.xd(), y + p.yd(), z + p.zd());
	}

	public void add(final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(x + p.xd(), y + p.yd(), z + p.zd());
	}

	public WB_Vector add(final WB_CoordinateSequence seq, final int i) {

		return new WB_Vector(xd() + seq.get(i, 0), yd() + seq.get(i, 1), zd()
				+ seq.get(i, 2));
	}

	public void addInto(final WB_CoordinateSequence seq, final int i,
			final WB_MutableCoordinate result) {
		result._set(xd() + seq.get(i, 0), yd() + seq.get(i, 1),
				zd() + seq.get(i, 2));
	}

	public WB_Vector addMul(final double f, final double x, final double y,
			final double z) {
		return new WB_Vector(this.x + f * x, this.y + f * y, this.z + f * z);
	}

	public void addMul(final double f, final double x, final double y,
			final double z, final WB_MutableCoordinate result) {
		result._set(this.x + f * x, this.y + f * y, this.z + f * z);
	}

	public WB_Vector addMul(final double f, final WB_Coordinate p) {
		return new WB_Vector(x + f * p.xd(), y + f * p.yd(), z + f * p.zd());
	}

	public void addMul(final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(x + f * p.xd(), y + f * p.yd(), z + f * p.zd());
	}

	public WB_Vector addMul(final double f, final WB_CoordinateSequence seq,
			final int i) {
		return new WB_Vector(xd() + f * seq.get(i, 0),
				yd() + f * seq.get(i, 1), zd() + f * seq.get(i, 2));
	}

	public void addMulInto(final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
	}

	public void addMulInto(final double f, final WB_CoordinateSequence seq,
			final int i, final WB_MutableCoordinate result) {
		result._set(xd() + f * seq.get(i, 0), yd() + f * seq.get(i, 1), zd()
				+ f * seq.get(i, 2));
	}

	public WB_Vector applySelf(final WB_Transform T) {
		return applySelfAsVector(T);
	}

	public WB_Vector applySelfAsNormal(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsNormal(this, result);
		return result;
	}

	public void applyAsNormalInto(final WB_Transform T,
			final WB_CoordinateSequence result, final int i) {
		T.applyAsNormal(this, result, i);
	}

	public void applyAsNormalInto(final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsNormal(this, result);
	}

	public WB_Vector applySelfAsPoint(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsPoint(this, result);
		return result;
	}

	public void applyAsPointInto(final WB_Transform T,
			final WB_CoordinateSequence result, final int i) {
		T.applyAsPoint(this, result, i);
	}

	public void applyAsPointInto(final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsPoint(this, result);

	}

	public WB_Vector applySelfAsVector(final WB_Transform T) {
		final WB_Vector result = new WB_Vector();
		T.applyAsVector(this, result);
		return result;
	}

	public void applyAsVectorInto(final WB_Transform T,
			final WB_CoordinateSequence result, final int i) {
		T.applyAsVector(this, result, i);
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
		return new double[] { x, y, z };
	}

	public WB_Vector cross(final WB_Coordinate p) {
		return new WB_Vector(y * p.zd() - z * p.yd(), z * p.xd() - x * p.zd(),
				x * p.yd() - y * p.xd());
	}

	public WB_Vector cross(final WB_CoordinateSequence seq, final int i) {
		return new WB_Vector(yd() * seq.get(i, 2) - zd() * seq.get(i, 1), zd()
				* seq.get(i, 0) - xd() * seq.get(i, 2), xd() * seq.get(i, 1)
				- yd() * seq.get(i, 0));
	}

	public void crossInto(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(y * p.zd() - z * p.yd(), z * p.xd() - x * p.zd(),
				x * p.yd() - y * p.xd());
	}

	public WB_Vector div(final double f) {
		return mul(1.0 / f);
	}

	public void div(final double f, final WB_MutableCoordinate result) {
		mul(1.0 / f, result);
	}

	public double dot(final WB_Coordinate p) {
		return WB_CoordinateMath.dot(x, y, z, p.xd(), p.yd(), p.zd());
	}

	public double dot(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.dot(x, y, z, seq.get(i, 0), seq.get(i, 1),
				seq.get(i, 2));
	}

	public double dot2D(final WB_Coordinate p) {
		return WB_CoordinateMath.dot2D(x, y, p.xd(), p.yd());
	}

	public double dot2D(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.dot2D(x, y, seq.get(i, 0), seq.get(i, 1));
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_Vector)) {
			return false;
		}
		final WB_Vector p = (WB_Vector) o;
		if (!WB_Epsilon.isEqualAbs(x, p.x)) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(y, p.y)) {
			return false;
		}
		if (!WB_Epsilon.isEqualAbs(z, p.z)) {
			return false;
		}
		return true;
	}

	public WB_Vector get() {
		return new WB_Vector(x, y, z);
	}

	public double getAngle(final WB_CoordinateSequence seq, final int i) {

		return WB_CoordinateMath.angleBetween(x, y, z, seq.get(i, 0),
				seq.get(i, 1), seq.get(i, 2));
	}

	public double getAngle(final WB_Coordinate p) {
		return WB_CoordinateMath.angleBetween(x, y, z, p.xd(), p.yd(), p.zd());
	}

	public double getAngleNorm(final WB_Coordinate p) {
		return WB_CoordinateMath.angleBetweenNorm(x, y, z, p.xd(), p.yd(),
				p.zd());
	}

	public double getAngleNorm(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.angleBetweenNorm(x, y, z, seq.get(i, 0),
				seq.get(i, 1), seq.get(i, 2));
	}

	public double getd(final int i) {
		if (i == 0) {
			return x;
		}
		if (i == 1) {
			return y;
		}
		if (i == 2) {
			return z;
		}
		return Double.NaN;
	}

	public double getDistance(final WB_Coordinate p) {
		return WB_CoordinateMath.getDistance(x, y, z, p.xd(), p.yd(), p.zd());
	}

	public double getDistance(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.getDistance(x, y, z, seq.get(i, 0),
				seq.get(i, 1), seq.get(i, 2));
	}

	public double getDistance2D(final WB_Coordinate p) {
		return WB_CoordinateMath.getDistance2D(x, y, p.xd(), p.yd());
	}

	public double getDistance2D(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.getDistance2D(x, y, seq.get(i, 0),
				seq.get(i, 1));
	}

	public float getf(final int i) {
		if (i == 0) {
			return (float) x;
		}
		if (i == 1) {
			return (float) y;
		}
		if (i == 2) {
			return (float) z;
		}
		return Float.NaN;
	}

	public double getLength() {
		return WB_CoordinateMath.getLength(x, y, z);
	}

	public double getLength2D() {
		return WB_CoordinateMath.getLength2D(x, y);
	}

	public double getSqDistance(final WB_Coordinate p) {
		return WB_CoordinateMath.getSqDistance(x, y, z, p.xd(), p.yd(), p.zd());
	}

	public double getSqDistance(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.getSqDistance(x, y, z, seq.get(i, 0),
				seq.get(i, 1), seq.get(i, 2));
	}

	public double getSqDistance2D(final WB_Coordinate p) {
		return WB_CoordinateMath.getSqDistance2D(x, y, p.xd(), p.yd());
	}

	public double getSqDistance2D(final WB_CoordinateSequence seq, final int i) {
		return WB_CoordinateMath.getSqDistance2D(x, y, seq.get(i, 0),
				seq.get(i, 1));
	}

	public double getSqLength() {
		return WB_CoordinateMath.getSqLength(x, y, z);
	}

	public double getSqLength2D() {
		return WB_CoordinateMath.getSqLength2D(x, y);
	}

	@Override
	public int hashCode() {

		return WB_CoordinateMath.calculateHashCode(x, y, z);

	}

	public double heading() {
		return Math.atan2(y, x);
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

	public boolean isCollinear(final WB_CoordinateSequence seq, final int i,
			final int j) {
		if (WB_Epsilon.isZeroSq(seq.getSqDistance(i, j))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(seq.getSqDistance(i, this))) {
			return true;
		}
		if (WB_Epsilon.isZeroSq(seq.getSqDistance(j, this))) {
			return true;
		}
		return WB_Epsilon.isZeroSq(sub(seq, i)._crossSelf(sub(seq, j))
				.getSqLength());
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
		return WB_CoordinateMath.isZero(x, y, z);
	}

	public WB_Vector mul(final double f) {
		return new WB_Vector(x * f, y * f, z * f);
	}

	public void mul(final double f, final WB_MutableCoordinate result) {
		scale(f, result);
	}

	public WB_Vector mulAddMul(final double f, final double g,
			final WB_Coordinate p) {
		return new WB_Vector(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f
				* zd() + g * p.zd());
	}

	public WB_Vector mulAddMul(final double f, final double g,
			final WB_CoordinateSequence seq, final int i) {
		return new WB_Vector(f * xd() + g * seq.get(i, 0), f * yd() + g
				* seq.get(i, 1), f * zd() + g * seq.get(i, 2));
	}

	public void mulAddMulInto(final double f, final double g,
			final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f * zd() + g
				* p.zd());
	}

	public void mulAddMulInto(final double f, final double g,
			final WB_CoordinateSequence seq, final int i,
			final WB_MutableCoordinate result) {
		result._set(f * xd() + g * seq.get(i, 0), f * yd() + g * seq.get(i, 1),
				f * zd() + g * seq.get(i, 2));
	}

	public void rotateAboutAxis(final double angle, final double p1x,
			final double p1y, final double p1z, final double p2x,
			final double p2y, final double p2z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Vector(p1x, p1y, p1z),
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
			final WB_Vector a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);

		raa.applySelfAsVector(this);

	}

	public double scalarTriple(final WB_Coordinate p, final WB_Coordinate q) {
		return WB_CoordinateMath.scalarTriple(this, p, q);
	}

	public void scale(final double f, final WB_MutableCoordinate result) {
		result._set(x * f, y * f, z * f);
	}

	public void set(final int i, final double v) {
		if (i == 0) {
			x = v;
		} else if (i == 1) {
			y = v;
		} else if (i == 2) {
			z = v;
		}

	}

	public boolean smallerThan(final WB_Coordinate otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZ.xd());
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZ.yd());
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.zd());
		return (_tmp < 0);
	}

	public WB_Vector sub(final double x, final double y, final double z) {
		return new WB_Vector(this.x - x, this.y - y, this.z - z);
	}

	public void sub(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.x - x, this.y - y, this.z - z);
	}

	public WB_Vector sub(final WB_Coordinate p) {
		return new WB_Vector(x - p.xd(), y - p.yd(), z - p.zd());
	}

	public void sub(final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(x - p.xd(), y - p.yd(), z - p.zd());
	}

	public WB_Vector sub(final WB_CoordinateSequence seq, final int i) {
		return new WB_Vector(xd() - seq.get(i, 0), yd() - seq.get(i, 1), zd()
				- seq.get(i, 2));
	}

	public WB_M33 tensor(final WB_CoordinateSequence seq, final int i) {

		return new WB_M33(xd() * seq.get(i, 0), xd() * seq.get(i, 1), xd()
				* seq.get(i, 2), yd() * seq.get(i, 0), yd() * seq.get(i, 1),
				yd() * seq.get(i, 2), zd() * seq.get(i, 0), zd()
						* seq.get(i, 1), zd() * seq.get(i, 2));
	}

	public WB_M33 tensor(final WB_Coordinate q) {
		return WB_CoordinateMath.tensor(this, q);

	}

	@Override
	public String toString() {
		return "WB_Vector [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

	@Override
	public double wd() {
		return 0;
	}

	@Override
	public float wf() {

		return 0;
	}

	@Override
	public double xd() {
		return x;
	}

	public float xf() {
		return (float) x;
	}

	@Override
	public double yd() {
		return y;
	}

	public float yf() {
		return (float) y;
	}

	@Override
	public double zd() {
		return z;
	}

	public float zf() {
		return (float) z;
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
