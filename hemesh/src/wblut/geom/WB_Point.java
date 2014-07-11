package wblut.geom;

import wblut.core.WB_Epsilon;

public class WB_Point extends WB_Vector {

	public static WB_Coordinate X() {
		return new WB_Point(1, 0, 0);
	}

	public static WB_Coordinate Y() {
		return new WB_Point(0, 1, 0);
	}

	public static WB_Coordinate Z() {
		return new WB_Point(0, 0, 1);
	}

	public static WB_Coordinate ZERO() {
		return new WB_Point(0, 0, 0);
	}

	public WB_Point() {
		super();
	}

	public WB_Point(final double x, final double y) {
		super(x, y);
	}

	public WB_Point(final double x, final double y, final double z) {
		super(x, y, z);
	}

	public WB_Point(final double[] x) {
		super(x);
	}

	public WB_Point(final WB_Coordinate v) {
		super(v);
	}

	public WB_Point _addMulSelf(final double f, final double x, final double y,
			final double z) {
		_set(xd() + f * x, yd() + f * y, zd() + f * z);
		return this;
	}

	public WB_Point _addMulSelf(final double f, final WB_Coordinate p) {
		_set(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f * p.zd());

		return this;
	}

	public WB_Point _addMulSelf(final double f,
			final WB_CoordinateSequence seq, final int i) {
		_set(xd() + f * seq.get(i, 0), yd() + f * seq.get(i, 1),
				zd() + f * seq.get(i, 2));

		return this;
	}

	public WB_Point _addSelf(final double x, final double y, final double z) {
		_set(xd() + x, yd() + y, zd() + z);
		return this;
	}

	public WB_Point _addSelf(final WB_Coordinate p) {
		_set(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
		return this;
	}

	public WB_Point _addSelf(final WB_CoordinateSequence seq, final int i) {
		_set(xd() + seq.get(i, 0), yd() + seq.get(i, 1), zd() + seq.get(i, 2));

		return this;
	}

	public WB_Point _applyAsNormalSelf(final WB_Transform T) {
		T.applyAsNormal(this, this);
		return this;
	}

	public WB_Point _applyAsPointSelf(final WB_Transform T) {
		T.applyAsPoint(this, this);
		return this;
	}

	public WB_Point _applyAsVectorSelf(final WB_Transform T) {
		T.applyAsVector(this, this);
		return this;
	}

	public WB_Point _crossSelf(final WB_Coordinate p) {
		_set(yd() * p.zd() - zd() * p.yd(), zd() * p.xd() - xd() * p.zd(), xd()
				* p.yd() - yd() * p.xd());
		return this;
	}

	public WB_Point _crossSelf(final WB_CoordinateSequence seq, final int i) {
		_set(yd() * seq.get(i, 2) - zd() * seq.get(i, 1), zd() * seq.get(i, 0)
				- xd() * seq.get(i, 2),
				xd() * seq.get(i, 1) - yd() * seq.get(i, 0));

		return this;
	}

	public WB_Point _divSelf(final double f) {
		return _mulSelf(1.0 / f);
	}

	public void _invert() {
		_mulSelf(-1.0);
	}

	public WB_Point _mulAddMulSelf(final double f, final double g,
			final WB_Coordinate p) {
		_set(f * xd() + g * p.xd(), f * yd() + g * p.yd(),
				f * zd() + g * p.zd());

		return this;
	}

	public WB_Point _mulAddMulSelf(final double f, final double g,
			final WB_CoordinateSequence seq, final int i) {
		_set(f * xd() + g * seq.get(i, 0), f * yd() + g * seq.get(i, 1), f
				* zd() + g * seq.get(i, 2));

		return this;
	}

	public WB_Point _mulSelf(final double f) {
		_set(f * xd(), f * yd(), f * zd());
		return this;
	}

	public WB_Point _scaleSelf(final double f) {

		return _mulSelf(f);
	}

	public WB_Point _scaleSelf(final double fx, final double fy, final double fz) {
		_set(xd() * fx, yd() * fy, zd() * fz);
		return this;
	}

	public WB_Point _subSelf(final double x, final double y, final double z) {
		_set(xd() - x, yd() - y, zd() - z);
		return this;
	}

	public WB_Point _subSelf(final WB_Coordinate v) {
		_set(xd() - v.xd(), yd() - v.yd(), zd() - v.zd());
		return this;
	}

	public WB_Point _subSelf(final WB_CoordinateSequence seq, final int i) {
		_set(xd() - seq.get(i, 0), yd() - seq.get(i, 1), zd() - seq.get(i, 2));

		return this;
	}

	public WB_Point add(final double x, final double y, final double z) {
		return new WB_Point(this.xd() + x, this.yd() + y, this.zd() + z);
	}

	public WB_Point add(final WB_Coordinate p) {
		return new WB_Point(xd() + p.xd(), yd() + p.yd(), zd() + p.zd());
	}

	public WB_Point add(final WB_CoordinateSequence seq, final int i) {

		return new WB_Point(xd() + seq.get(i, 0), yd() + seq.get(i, 1), zd()
				+ seq.get(i, 2));
	}

	public WB_Point addMul(final double f, final double x, final double y,
			final double z) {
		return new WB_Point(this.xd() + f * x, this.yd() + f * y, this.zd() + f
				* z);
	}

	public WB_Point addMul(final double f, final WB_Coordinate p) {
		return new WB_Point(xd() + f * p.xd(), yd() + f * p.yd(), zd() + f
				* p.zd());
	}

	public WB_Point addMul(final double f, final WB_CoordinateSequence seq,
			final int i) {
		return new WB_Point(xd() + f * seq.get(i, 0), yd() + f * seq.get(i, 1),
				zd() + f * seq.get(i, 2));
	}

	public WB_Point apply(final WB_Transform T) {
		return applyAsPoint(T);
	}

	public WB_Point cross(final WB_Coordinate p) {
		return new WB_Point(yd() * p.zd() - zd() * p.yd(), zd() * p.xd() - xd()
				* p.zd(), xd() * p.yd() - yd() * p.xd());
	}

	public WB_Point cross(final WB_CoordinateSequence seq, final int i) {
		return new WB_Point(yd() * seq.get(i, 2) - zd() * seq.get(i, 1), zd()
				* seq.get(i, 0) - xd() * seq.get(i, 2), xd() * seq.get(i, 1)
				- yd() * seq.get(i, 0));
	}

	public WB_Point div(final double f) {
		return mul(1.0 / f);
	}

	@Override
	public boolean equals(final Object o) {
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

	public WB_Point get() {
		return new WB_Point(this);
	}

	public WB_Point moveBy(final double x, final double y, final double z) {
		_addSelf(x, y, z);
		return this;
	}

	public WB_Point moveBy(final WB_Point v) {
		_addSelf(v);
		return this;
	}

	public WB_Point moveByAndCopy(final double x, final double y, final double z) {
		return new WB_Point(this.xd() + x, this.yd() + y, this.zd() + z);
	}

	public WB_Point moveByAndCopy(final WB_Coordinate v) {
		return new WB_Point(xd() + v.xd(), yd() + v.yd(), zd() + v.zd());
	}

	public void moveByInto(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		result._set(this.xd() + x, this.yd() + y, this.zd() + z);
	}

	public void moveByInto(final WB_Coordinate v,
			final WB_MutableCoordinate result) {
		result._set(xd() + v.xd(), yd() + v.yd(), zd() + v.zd());
	}

	public WB_Point moveTo(final double x, final double y) {
		_set(x, y);
		return this;
	}

	public WB_Point moveTo(final double x, final double y, final double z) {
		_set(x, y, z);
		return this;
	}

	public WB_Point moveTo(final WB_Coordinate p) {
		_set(p);
		return this;
	}

	public WB_Point mul(final double f) {

		return new WB_Point(xd() * f, yd() * f, zd() * f);
	}

	public WB_Point mulAddMul(final double f, final double g,
			final WB_Coordinate p) {
		return new WB_Point(f * xd() + g * p.xd(), f * yd() + g * p.yd(), f
				* zd() + g * p.zd());
	}

	public WB_Point mulAddMul(final double f, final double g,
			final WB_CoordinateSequence seq, final int i) {
		return new WB_Point(f * xd() + g * seq.get(i, 0), f * yd() + g
				* seq.get(i, 1), f * zd() + g * seq.get(i, 2));
	}

	public void rotateAboutAxis(final double angle, final double p1x,
			final double p1y, final double p1z, final double p2x,
			final double p2y, final double p2z) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, new WB_Point(p1x, p1y, p1z),
				new WB_Vector(p2x - p1x, p2y - p1y, p2z - p1z));
		raa.applySelfAsPoint(this);
	}

	public void rotateAboutAxis(final double angle, final WB_Coordinate p1,
			final WB_Coordinate p2) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p1, new WB_Vector(p1, p2));

		raa.applySelfAsPoint(this);

	}

	public void rotateAboutAxis(final double angle, final WB_Coordinate p,
			final WB_Vector a) {

		final WB_Transform raa = new WB_Transform();
		raa.addRotateAboutAxis(angle, p, a);

		raa.applySelfAsPoint(this);

	}

	public WB_Point sub(final double x, final double y, final double z) {
		return new WB_Point(this.xd() - x, this.yd() - y, this.zd() - z);
	}

	public WB_Point sub(final WB_Coordinate p) {
		return new WB_Point(xd() - p.xd(), yd() - p.yd(), zd() - p.zd());
	}

	public WB_Point sub(final WB_CoordinateSequence seq, final int i) {
		return new WB_Point(xd() - seq.get(i, 0), yd() - seq.get(i, 1), zd()
				- seq.get(i, 2));
	}

	public WB_Vector subToVector(final double x, final double y, final double z) {
		return new WB_Vector(this.xd() - x, this.yd() - y, this.zd() - z);
	}

	public WB_Vector subToVector(final WB_Coordinate p) {
		return new WB_Vector(xd() - p.xd(), yd() - p.yd(), zd() - p.zd());
	}

	public WB_Vector subToVector2D(final double x, final double y,
			final double z) {
		return new WB_Vector(this.xd() - x, this.yd() - y, 0);
	}

	public WB_Vector subToVector2D(final WB_Coordinate p) {
		return new WB_Vector(xd() - p.xd(), yd() - p.yd(), 0);
	}

	@Override
	public String toString() {
		return "WB_Point [x=" + xd() + ", y=" + yd() + ", z=" + zd() + "]";
	}

	public WB_Vector toVector() {
		return new WB_Vector(xd(), yd(), zd());
	}

	public WB_Point applyAsNormal(final WB_Transform T) {
		final WB_Point result = new WB_Point();
		T.applyAsNormal(this, result);
		return result;
	}

	public WB_Point applyAsPoint(final WB_Transform T) {
		final WB_Point result = new WB_Point();
		T.applyAsPoint(this, result);
		return result;
	}

	public WB_Point applyAsVector(final WB_Transform T) {
		final WB_Point result = new WB_Point();
		T.applyAsVector(this, result);
		return result;
	}

}
