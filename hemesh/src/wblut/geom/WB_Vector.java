package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_MTRandom;
import wblut.math.WB_Math;

public class WB_Vector implements Comparable<WB_Coordinate>,
		WB_MutableCoordinate {

	static private double k0, k1, k2, d;
	static private int exp0, exp1, exp2, cmp;

	public static WB_Vector ZERO() {
		return new WB_Vector(0, 0, 0);
	}

	public static WB_Vector X() {
		return new WB_Vector(1, 0, 0);
	}

	public static WB_Vector Y() {
		return new WB_Vector(0, 1, 0);
	}

	public static WB_Vector Z() {
		return new WB_Vector(0, 0, 1);
	}

	/** Coordinates. */
	public double x, y, z;

	public WB_Vector() {
		x = y = z = 0;
	}

	public WB_Vector(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WB_Vector(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public WB_Vector(final double[] x) {
		this.x = x[0];
		this.y = x[1];
		this.z = x[2];
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

	public WB_Vector(final double[] p1, final double[] p2) {
		this.x = p2[0] - p1[0];
		this.y = p2[1] - p1[1];
		this.z = p2[2] - p1[2];
	}

	public WB_Vector get() {
		return new WB_Vector(x, y, z);
	}

	public void _set(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void _set(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public void _set(final WB_Coordinate v) {
		_set(v.xd(), v.yd(), v.zd());
	}

	public void _invert() {
		x *= -1;
		y *= -1;
		z *= -1;
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

	public void _trimSelf(final double d) {
		if (getSqLength() > d * d) {
			_normalizeSelf();
			_mulSelf(d);
		}
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

	public void scale(final double f, final WB_MutableCoordinate result) {
		result._set(x * f, y * f, z * f);
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

	public void add(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.x + x, this.y + y, this.z + z);
	}

	public void add(final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(x + p.xd(), y + p.yd(), z + p.zd());
	}

	public WB_Vector add(final double x, final double y, final double z) {
		return new WB_Vector(this.x + x, this.y + y, this.z + z);
	}

	public WB_Vector add(final WB_Coordinate p) {
		return new WB_Vector(x + p.xd(), y + p.yd(), z + p.zd());
	}

	public WB_Vector _addSelf(final double f, final WB_Coordinate p) {
		x += f * p.xd();
		y += f * p.yd();
		z += f * p.zd();
		return this;
	}

	public WB_Vector _addMulSelf(final double f, final double x,
			final double y, final double z) {
		this.x += f * x;
		this.y += f * y;
		this.z += f * z;
		return this;
	}

	public void addMul(final double f, final double x, final double y,
			final double z, final WB_MutableCoordinate result) {
		result._set(this.x + f * x, this.y + f * y, this.z + f * z);
	}

	public void addMul(final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(x + f * p.xd(), y + f * p.yd(), z + f * p.zd());
	}

	public WB_Vector addMul(final double f, final double x, final double y,
			final double z) {
		return new WB_Vector(this.x + f * x, this.y + f * y, this.z + f * z);
	}

	public WB_Vector addMul(final double f, final WB_Coordinate p) {
		return new WB_Vector(x + f * p.xd(), y + f * p.yd(), z + f * p.zd());
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

	public void sub(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.x - x, this.y - y, this.z - z);
	}

	public void sub(final WB_Coordinate p, final WB_MutableCoordinate result) {
		result._set(x - p.xd(), y - p.yd(), z - p.zd());
	}

	public WB_Vector sub(final double x, final double y, final double z) {
		return new WB_Vector(this.x - x, this.y - y, this.z - z);
	}

	public WB_Vector sub(final WB_Coordinate p) {
		return new WB_Vector(x - p.xd(), y - p.yd(), z - p.zd());
	}

	public WB_Vector subToVector(final double x, final double y, final double z) {
		return new WB_Vector(this.x - x, this.y - y, this.z - z);
	}

	public WB_Vector subToVector(final WB_Coordinate p) {
		return new WB_Vector(x - p.xd(), y - p.yd(), z - p.zd());
	}

	public WB_Vector _mulSelf(final double f) {
		_scaleSelf(f);
		return this;
	}

	public void mul(final double f, final WB_MutableCoordinate result) {
		scale(f, result);
	}

	public WB_Vector mul(final double f) {
		return new WB_Vector(x * f, y * f, z * f);
	}

	public WB_Vector _divSelf(final double f) {
		return _mulSelf(1.0 / f);
	}

	public void div(final double f, final WB_MutableCoordinate result) {
		mul(1.0 / f, result);
	}

	public WB_Vector div(final double f) {
		return mul(1.0 / f);
	}

	public static double dot(final WB_Coordinate p, final WB_Coordinate q) {
		double k0 = p.xd() * q.xd();
		double k1 = p.yd() * q.yd();
		double k2 = p.zd() * q.zd();

		double exp0 = WB_Math.getExp(k0);
		double exp1 = WB_Math.getExp(k1);
		double exp2 = WB_Math.getExp(k2);
		if (exp0 < exp1) {
			if (exp0 < exp2) {
				return (k1 + k2) + k0;
			} else {
				return (k0 + k1) + k2;
			}
		} else {
			if (exp1 < exp2) {
				return (k0 + k2) + k1;
			} else {
				return (k0 + k1) + k2;
			}
		}

	}

	public double dot(final WB_Coordinate p) {
		double k0 = p.xd() * x;
		double k1 = p.yd() * y;
		double k2 = p.zd() * z;

		double exp0 = WB_Math.getExp(k0);
		double exp1 = WB_Math.getExp(k1);
		double exp2 = WB_Math.getExp(k2);
		if (exp0 < exp1) {
			if (exp0 < exp2) {
				return (k1 + k2) + k0;
			} else {
				return (k0 + k1) + k2;
			}
		} else {
			if (exp1 < exp2) {
				return (k0 + k2) + k1;
			} else {
				return (k0 + k1) + k2;
			}
		}
	}

	public double angleNorm(final WB_Coordinate p) {
		return Math.acos(p.xd() * x + p.yd() * y + p.zd() * z);
	}

	public static double absDot(final WB_Coordinate p, final WB_Coordinate q) {
		return WB_Math.fastAbs(p.xd() * q.xd() + p.yd() * q.yd() + p.zd()
				* q.zd());
	}

	public double absDot(final WB_Coordinate p) {
		return WB_Math.fastAbs(p.xd() * x + p.yd() * y + p.zd() * z);
	}

	public WB_Vector cross(final WB_Coordinate p) {
		return new WB_Vector(y * p.zd() - z * p.yd(), z * p.xd() - x * p.zd(),
				x * p.yd() - y * p.xd());
	}

	public WB_Vector _crossSelf(final WB_Coordinate p) {
		_set(y * p.zd() - z * p.yd(), z * p.xd() - x * p.zd(), x * p.yd() - y
				* p.xd());
		return this;
	}

	public static WB_Vector getCross(final WB_Coordinate p,
			final WB_Coordinate q) {
		return new WB_Vector(p.yd() * q.zd() - p.zd() * q.yd(), p.zd() * q.xd()
				- p.xd() * q.zd(), p.xd() * q.yd() - p.yd() * q.xd());
	}

	public void crossInto(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(y * p.zd() - z * p.yd(), z * p.xd() - x * p.zd(),
				x * p.yd() - y * p.xd());
	}

	public static double scalarTriple(final WB_Coordinate p,
			final WB_Coordinate q, final WB_Coordinate r) {
		return (dot(p, getCross(q, r)));
	}

	public double scalarTriple(final WB_Coordinate p, final WB_Coordinate q) {
		return (dot(this, getCross(p, q)));
	}

	public static WB_M33 tensor(final WB_Coordinate p, final WB_Coordinate q) {
		return new WB_M33(p.xd() * q.xd(), p.xd() * q.yd(), p.xd() * q.zd(),
				p.yd() * q.xd(), p.yd() * q.yd(), p.yd() * q.zd(), p.zd()
						* q.xd(), p.zd() * q.yd(), p.zd() * q.zd());
	}

	public WB_M33 tensor(final WB_Vector q) {
		return new WB_M33(x * q.x, x * q.y, x * q.z, y * q.x, y * q.y, y * q.z,
				z * q.x, z * q.y, z * q.z);
	}

	public double getSqLength() {
		return x * x + y * y + z * z;
	}

	public double getLength() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public boolean isZero() {
		return (getSqLength() < WB_Epsilon.SQEPSILON);
	}

	public int compareTo(final WB_Vector otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZ.x);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZ.y);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.z);
		return _tmp;
	}

	public int compareToY1st(final WB_Vector otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(y, otherXYZ.y);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(x, otherXYZ.x);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.z);
		return _tmp;
	}

	public boolean smallerThan(final WB_Vector otherXYZ) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZ.x);
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZ.y);
		if (_tmp != 0) {
			return (_tmp < 0);
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZ.z);
		return (_tmp < 0);
	}

	@Override
	public String toString() {
		return "XYZ [x=" + x + ", y=" + y + ", z=" + z + "]";
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

	public void set(final int i, final double v) {
		if (i == 0) {
			x = v;
		} else if (i == 1) {
			y = v;
		} else if (i == 2) {
			z = v;
		}

	}

	public float xf() {
		return (float) x;
	}

	public float yf() {
		return (float) y;
	}

	public float zf() {
		return (float) z;
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

	protected static int calculateHashCode(final double x, final double y,
			final double z) {
		int result = 17;

		final long a = Double.doubleToLongBits(x);
		result += 31 * result + (int) (a ^ (a >>> 32));

		final long b = Double.doubleToLongBits(y);
		result += 31 * result + (int) (b ^ (b >>> 32));

		final long c = Double.doubleToLongBits(z);
		result += 31 * result + (int) (c ^ (c >>> 32));

		return result;

	}

	protected int calculateHashCode() {
		int result = 17;

		final long a = Double.doubleToLongBits(x);
		result += 31 * result + (int) (a ^ (a >>> 32));

		final long b = Double.doubleToLongBits(y);
		result += 31 * result + (int) (b ^ (b >>> 32));

		final long c = Double.doubleToLongBits(z);
		result += 31 * result + (int) (c ^ (c >>> 32));

		return result;

	}

	public void rotateAboutAxis(final double angle, final double p1x,
			final double p1y, final double p1z, final double p2x,
			final double p2y, final double p2z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotate(angle, new WB_Vector(p2x - p1x, p2y - p1y, p2z
				- p1z));
		raa.applySelfAsVector(this);
	}

	public void rotateAboutAxis(final double angle, final WB_Coordinate p1,
			final WB_Coordinate p2) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotate(angle, new WB_Vector(p1, p2));
		raa.applySelfAsVector(this);
	}

	public void rotateAboutAxis(final double angle, final WB_Vector a) {
		final WB_Transform raa = new WB_Transform();
		raa.addRotate(angle, a);
		raa.applySelfAsVector(this);
	}

	public static WB_Vector[] randomVectors(final int n, final double x,
			final double y, final double z) {
		final WB_MTRandom mtr = new WB_MTRandom();
		final WB_Vector[] points = new WB_Vector[n];
		for (int i = 0; i < n; i++) {
			points[i] = new WB_Vector(-x + 2 * mtr.nextDouble() * x, -y + 2
					* mtr.nextDouble() * y, -z + 2 * mtr.nextDouble() * z);
		}

		return points;
	}

	public static WB_Vector[] randomVectors(final int n, final double lx,
			final double ly, final double lz, final double ux, final double uy,
			final double uz) {
		final WB_MTRandom mtr = new WB_MTRandom();
		final WB_Vector[] points = new WB_Vector[n];
		final double dx = ux - lx;
		final double dy = uy - ly;
		final double dz = uz - lz;

		for (int i = 0; i < n; i++) {
			points[i] = new WB_Vector(lx + mtr.nextDouble() * dx, ly
					+ mtr.nextDouble() * dy, lz + mtr.nextDouble() * dz);
		}

		return points;
	}

	public WB_Vector toVector() {
		return new WB_Vector(x, y, z);
	}

	public static WB_Vector interpolate(final WB_Coordinate p0,
			final WB_Coordinate p1, final double t) {
		return new WB_Vector(p0.xd() + t * (p1.xd() - p0.xd()), p0.yd() + t
				* (p1.yd() - p0.yd()), p0.zd() + t * (p1.zd() - p0.zd()));

	}

	public static double angleBetween(final WB_Coordinate v0,
			final WB_Coordinate v1) {
		final WB_Vector lv0 = new WB_Vector(v0);
		final WB_Vector lv1 = new WB_Vector(v1);
		lv0._normalizeSelf();
		lv1._normalizeSelf();
		return Math.acos(lv0.dot(lv1));

	}

	public static double cosAngleBetween(final WB_Coordinate v0,
			final WB_Coordinate v1) {
		final WB_Vector lv0 = new WB_Vector(v0);
		final WB_Vector lv1 = new WB_Vector(v1);
		lv0._normalizeSelf();
		lv1._normalizeSelf();
		return lv0.dot(lv1);

	}

	public double[] coords() {
		return new double[] { x, y, z };
	}

	@Override
	public double xd() {
		return x;
	}

	@Override
	public double yd() {
		return y;
	}

	@Override
	public double zd() {
		return z;
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

	@Override
	public void _setW(double w) {

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
	public void _set(double x, double y, double z, double w) {
		_set(x, y, z);

	}

	public WB_Point toPoint() {
		return new WB_Point(x, y, z);
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + hashCode(x);
		result = 31 * result + hashCode(y);
		result = 31 * result + hashCode(z);
		return result;

	}

	private int hashCode(final double v) {
		final long tmp = Double.doubleToLongBits(v);
		return (int) (tmp ^ (tmp >>> 32));
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

	public WB_Vector add(final WB_CoordinateSequence seq, final int i) {

		return new WB_Vector(xd() + seq.get(i, 0), yd() + seq.get(i, 1), zd()
				+ seq.get(i, 2));
	}

	public WB_Vector addMul(final double f, final WB_CoordinateSequence seq,
			final int i) {
		return new WB_Vector(xd() + f * seq.get(i, 0),
				yd() + f * seq.get(i, 1), zd() + f * seq.get(i, 2));
	}

	public WB_Vector mulAddMul(final double f, final double g,
			final WB_CoordinateSequence seq, final int i) {
		return new WB_Vector(f * xd() + g * seq.get(i, 0), f * yd() + g
				* seq.get(i, 1), f * zd() + g * seq.get(i, 2));
	}

	public WB_Vector sub(final WB_CoordinateSequence seq, final int i) {
		return new WB_Vector(xd() - seq.get(i, 0), yd() - seq.get(i, 1), zd()
				- seq.get(i, 2));
	}

	public WB_Vector _addSelf(final WB_CoordinateSequence seq, final int i) {
		_set(xd() + seq.get(i, 0), yd() + seq.get(i, 1), zd() + seq.get(i, 2));

		return this;
	}

	public WB_Vector _addMulSelf(final double f,
			final WB_CoordinateSequence seq, final int i) {
		_set(xd() + f * seq.get(i, 0), yd() + f * seq.get(i, 1),
				zd() + f * seq.get(i, 2));

		return this;
	}

	public WB_Vector _mulAddMulSelf(final double f, final double g,
			final WB_CoordinateSequence seq, final int i) {
		_set(f * xd() + g * seq.get(i, 0), f * yd() + g * seq.get(i, 1), f
				* zd() + g * seq.get(i, 2));

		return this;
	}

	public WB_Vector _subSelf(final WB_CoordinateSequence seq, final int i) {
		_set(xd() - seq.get(i, 0), yd() - seq.get(i, 1), zd() - seq.get(i, 2));

		return this;
	}

	public double getDistance(final WB_CoordinateSequence seq, final int i) {
		return Math.sqrt((seq.get(i, 0) - xd()) * (seq.get(i, 0) - xd())
				+ (seq.get(i, 1) - yd()) * (seq.get(i, 1) - yd())
				+ (seq.get(i, 2) - zd()) * (seq.get(i, 2) - zd()));
	}

	public double getSqDistance(final WB_CoordinateSequence seq, final int i) {
		return (seq.get(i, 0) - xd()) * (seq.get(i, 0) - xd())
				+ (seq.get(i, 1) - yd()) * (seq.get(i, 1) - yd())
				+ (seq.get(i, 2) - zd()) * (seq.get(i, 2) - zd());
	}

	public double dot(final WB_CoordinateSequence seq, final int i) {

		k0 = seq.get(i, 0) * xd();
		k1 = seq.get(i, 1) * yd();
		k2 = seq.get(i, 2) * zd();

		exp0 = WB_Math.getExp(k0);
		exp1 = WB_Math.getExp(k1);
		exp2 = WB_Math.getExp(k2);
		if (exp0 < exp1) {
			if (exp0 < exp2) {
				return (k1 + k2) + k0;
			} else {
				return (k0 + k1) + k2;
			}
		} else {
			if (exp1 < exp2) {
				return (k0 + k2) + k1;
			} else {
				return (k0 + k1) + k2;
			}
		}
	}

	public WB_M33 tensor(final WB_CoordinateSequence seq, final int i) {

		return new WB_M33(xd() * seq.get(i, 0), xd() * seq.get(i, 1), xd()
				* seq.get(i, 2), yd() * seq.get(i, 0), yd() * seq.get(i, 1),
				yd() * seq.get(i, 2), zd() * seq.get(i, 0), zd()
						* seq.get(i, 1), zd() * seq.get(i, 2));
	}

	public WB_Vector cross(final WB_CoordinateSequence seq, final int i) {
		return new WB_Vector(yd() * seq.get(i, 2) - zd() * seq.get(i, 1), zd()
				* seq.get(i, 0) - xd() * seq.get(i, 2), xd() * seq.get(i, 1)
				- yd() * seq.get(i, 0));
	}

	public WB_Vector _crossSelf(final WB_CoordinateSequence seq, final int i) {
		_set(yd() * seq.get(i, 2) - zd() * seq.get(i, 1), zd() * seq.get(i, 0)
				- xd() * seq.get(i, 2),
				xd() * seq.get(i, 1) - yd() * seq.get(i, 0));

		return this;
	}

	public double getAngle(final WB_CoordinateSequence seq, final int i) {
		d = this.dot(seq, i) / (getLength() * seq.getLength(i));
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return (Math.acos(d));
	}

	public double getAngleNorm(final WB_CoordinateSequence seq, final int i) {
		d = this.dot(seq, i);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return (Math.acos(d));
	}

	public void _set(final WB_CoordinateSequence seq, final int i) {
		_set(seq.get(i, 0), seq.get(i, 1), seq.get(i, 2), seq.get(i, 3));

	}

	public void addInto(final WB_CoordinateSequence seq, final int i,
			final WB_MutableCoordinate result) {
		result._set(xd() + seq.get(i, 0), yd() + seq.get(i, 1),
				zd() + seq.get(i, 2));
	}

	public void addMulInto(final double f, final WB_CoordinateSequence seq,
			final int i, final WB_MutableCoordinate result) {
		result._set(xd() + f * seq.get(i, 0), yd() + f * seq.get(i, 1), zd()
				+ f * seq.get(i, 2));
	}

	public void mulAddMulInto(final double f, final double g,
			final WB_CoordinateSequence seq, final int i,
			final WB_MutableCoordinate result) {
		result._set(f * xd() + g * seq.get(i, 0), f * yd() + g * seq.get(i, 1),
				f * zd() + g * seq.get(i, 2));
	}

	public void subInto(final WB_CoordinateSequence seq, final int i,
			final WB_MutableCoordinate result) {
		result._set(xd() - seq.get(i, 0), yd() - seq.get(i, 1),
				zd() - seq.get(i, 2));
	}

	public void tensorInto(final WB_CoordinateSequence seq, final int i,
			final WB_M33 result) {
		result.set(xd() * seq.get(i, 0), xd() * seq.get(i, 1),
				xd() * seq.get(i, 2), yd() * seq.get(i, 0),
				yd() * seq.get(i, 1), yd() * seq.get(i, 2),
				zd() * seq.get(i, 0), zd() * seq.get(i, 1),
				zd() * seq.get(i, 2));
	}

	public void crossInto(final WB_CoordinateSequence seq, final int i,
			final WB_MutableCoordinate result) {
		result._set(yd() * seq.get(i, 2) - zd() * seq.get(i, 1),
				zd() * seq.get(i, 0) - xd() * seq.get(i, 2),
				xd() * seq.get(i, 1) - yd() * seq.get(i, 0));
	}

	public WB_Vector _addMulSelf(final double f, final WB_Coordinate p) {
		_set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());

		return this;
	}

	public WB_Vector _mulAddMulSelf(final double f, final double g,
			final WB_Coordinate p) {
		_set(f * xd() + g * p.xd(), f * yd() + g * p.yd(),
				f * zd() + g * p.zd());

		return this;
	}

	public void addMulInto(final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		result._set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());
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

	public double getDistance(final WB_Coordinate p) {
		return Math.sqrt((p.xd() - xd()) * (p.xd() - xd()) + (p.yd() - yd())
				* (p.yd() - yd()) + (p.zd() - zd()) * (p.zd() - zd()));
	}

	public double getSqDistance(final WB_Coordinate p) {
		return (p.xd() - xd()) * (p.xd() - xd()) + (p.yd() - yd())
				* (p.yd() - yd()) + (p.zd() - zd()) * (p.zd() - zd());
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

	public double getAngle(final WB_Vector v) {
		d = this.dot(v) / (getLength() * v.getLength());
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return (Math.acos(d));
	}

	public double getAngleNorm(final WB_Vector v) {
		d = this.dot(v);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return (Math.acos(d));
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

	public WB_Vector apply(final WB_Transform T) {

		return applyAsVector(T);
	}

	public WB_Vector applyAsNormal(final WB_Transform T) {
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

	public WB_Vector applyAsPoint(final WB_Transform T) {
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

	public WB_Vector applyAsVector(final WB_Transform T) {
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

}
