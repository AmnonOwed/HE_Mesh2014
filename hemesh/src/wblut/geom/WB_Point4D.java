package wblut.geom;

import wblut.math.WB_Epsilon;

public class WB_Point4D implements Comparable<WB_Coordinate>,
		WB_MutableCoordinate {

	public static final WB_Point4D ZERO() {
		return new WB_Point4D(0, 0, 0, 1);
	}

	public double x, y, z, w;

	public WB_Point4D() {
		x = y = z = w = 0;
	}

	public WB_Point4D(final double x, final double y, final double z,
			final double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public WB_Point4D(final WB_Point4D v) {
		x = v.x;
		y = v.y;
		z = v.z;
		w = v.w;
	}

	public WB_Point4D(final WB_Coordinate v, final double w) {
		x = v.xd();
		y = v.yd();
		z = v.zd();
		this.w = w;
	}

	public double mag2() {
		return x * x + y * y + z * z + w * w;
	}

	public double mag() {
		return Math.sqrt(x * x + y * y + z * z + w * w);
	}

	public boolean isZero() {
		return (mag2() < WB_Epsilon.SQEPSILON);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(final WB_Point4D otherXYZW) {
		int _tmp = WB_Epsilon.compareAbs(x, otherXYZW.x);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(y, otherXYZW.y);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(z, otherXYZW.z);
		if (_tmp != 0) {
			return _tmp;
		}
		_tmp = WB_Epsilon.compareAbs(w, otherXYZW.w);
		return _tmp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "XYZW [x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + "]";
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
		if (i == 3) {
			return w;
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
		if (i == 3) {
			return (float) w;
		}
		return Float.NaN;
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

	public float wf() {
		return (float) w;
	}

	public WB_Point4D get() {
		return new WB_Point4D(x, y, z, w);
	}

	public WB_Point4D moveTo(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	public WB_Point4D moveTo(final WB_Coordinate p) {
		x = p.xd();
		y = p.yd();
		z = p.zd();
		return this;
	}

	public WB_Point4D moveBy(final double x, final double y, final double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public WB_Point4D moveBy(final WB_Coordinate v) {
		x += v.xd();
		y += v.yd();
		z += v.zd();
		return this;
	}

	public void moveByInto(final double x, final double y, final double z,
			final WB_Point4D result) {
		result.x = this.x + x;
		result.y = this.y + y;
		result.z = this.z + z;
		result.w = w;
	}

	public void moveByInto(final WB_Coordinate v, final WB_Point4D result) {
		result.x = x + v.xd();
		result.y = y + v.yd();
		result.z = z + v.zd();
		result.w = w;
	}

	public WB_Point4D moveByAndCopy(final double x, final double y,
			final double z) {
		return new WB_Point4D(this.x + x, this.y + y, this.z + z, w);
	}

	public WB_Point4D moveByAndCopy(final WB_Point v) {
		return new WB_Point4D(x + v.xd(), y + v.yd(), z + v.zd(), w);
	}

	public WB_Point4D invert() {
		x *= -1;
		y *= -1;
		z *= -1;
		w *= -1;
		return this;
	}

	public double normalize() {
		final double d = mag();
		if (WB_Epsilon.isZero(d)) {
			_set(0, 0, 0, 0);
		} else {
			_set(x / d, y / d, z / d, w / d);
		}
		return d;
	}

	public void trim(final double d) {
		if (mag2() > d * d) {
			normalize();
			mult(d);
		}
	}

	public WB_Point4D scale(final double f) {
		x *= f;
		y *= f;
		z *= f;
		w *= f;
		return this;
	}

	public void scaleInto(final double f, final WB_Point4D result) {
		result.x = x * f;
		result.y = y * f;
		result.z = z * f;
		result.w = w * f;
	}

	public WB_Point4D add(final double x, final double y, final double z,
			final double w) {
		this.x += x;
		this.y += y;
		this.z += z;
		this.w += w;
		return this;
	}

	public WB_Point4D add(final double x, final double y, final double z,
			final double w, final double f) {
		this.x += f * x;
		this.y += f * y;
		this.z += f * z;
		this.w += f * w;
		return this;
	}

	public WB_Point4D add(final WB_Point4D p) {
		x += p.x;
		y += p.y;
		z += p.z;
		w += p.w;
		return this;
	}

	public WB_Point4D add(final WB_Point4D p, final double f) {
		x += f * p.x;
		y += f * p.y;
		z += f * p.z;
		w += f * p.w;
		return this;
	}

	public void addInto(final double x, final double y, final double z,
			final double w, final WB_Point4D result) {
		result.x = (this.x + x);
		result.y = (this.y + y);
		result.z = (this.z + z);
		result.w = this.w + w;
	}

	public void addInto(final WB_Point4D p, final WB_Point4D result) {
		result.x = x + p.x;
		result.y = y + p.y;
		result.z = z + p.z;
		result.w = w + p.w;
	}

	public WB_Point4D addAndCopy(final double x, final double y,
			final double z, final double w) {
		return new WB_Point4D(this.x + x, this.y + y, this.z + z, this.w + w);
	}

	public WB_Point4D addAndCopy(final double x, final double y,
			final double z, final double w, final double f) {
		return new WB_Point4D(this.x + f * x, this.y + f * y, this.z + f * z,
				this.w + f * w);
	}

	public WB_Point4D addAndCopy(final WB_Point4D p) {
		return new WB_Point4D(x + p.x, y + p.y, z + p.z, w + p.w);
	}

	public WB_Point4D sub(final double x, final double y, final double z,
			final double w) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		this.w -= w;
		return this;
	}

	public WB_Point4D sub(final WB_Point4D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		w -= v.w;
		return this;
	}

	public void subInto(final double x, final double y, final double z,
			final double w, final WB_Point4D result) {
		result.x = (this.x - x);
		result.y = (this.y - y);
		result.z = (this.z - z);
		result.w = this.w - w;
	}

	public void subInto(final WB_Point4D p, final WB_Point4D result) {
		result.x = x - p.x;
		result.y = y - p.y;
		result.z = z - p.z;
		result.w = w - p.w;
	}

	public WB_Point4D subAndCopy(final double x, final double y,
			final double z, final double w) {
		return new WB_Point4D(this.x - x, this.y - y, this.z - z, this.w - w);
	}

	public WB_Point4D subAndCopy(final WB_Point4D p) {
		return new WB_Point4D(x - p.x, y - p.y, z - p.z, w - p.w);
	}

	public WB_Point4D mult(final double f) {
		scale(f);
		return this;
	}

	public void multInto(final double f, final WB_Point4D result) {
		scaleInto(f, result);
	}

	public WB_Point4D multAndCopy(final double f) {
		return new WB_Point4D(x * f, y * f, z * f, w * f);
	}

	public WB_Point4D div(final double f) {
		return mult(1.0 / f);
	}

	public void divInto(final double f, final WB_Point4D result) {
		multInto(1.0 / f, result);
	}

	public WB_Point4D divAndCopy(final double f) {
		return multAndCopy(1.0 / f);
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
		return w;
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
		this.w = w;
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
		if (i == 3) {
			this.w = v;
		}

	}

	@Override
	public void _set(double x, double y, double z, double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public void _set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = 0;
	}

	@Override
	public void _set(double x, double y) {
		this.x = x;
		this.y = y;
		this.z = 0;
		this.w = 0;
	}

	public static WB_Point4D interpolate(final WB_Point4D p0,
			final WB_Point4D p1, final double t) {
		return new WB_Point4D(p0.x + t * (p1.x - p0.x), p0.y + t
				* (p1.y - p0.y), p0.z + t * (p1.z - p0.z), p0.w + t
				* (p1.w - p0.w));

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

	@Override
	public void _set(WB_Coordinate p) {
		this.x = p.xd();
		this.y = p.yd();
		this.z = p.zd();
		this.w = p.wd();

	}

	public WB_Point4D rotateXY(double theta) {
		_set(x * Math.cos(theta) + y * -Math.sin(theta), x * Math.sin(theta)
				+ y * Math.cos(theta), z, w);
		return this;
	}

	public WB_Point4D rotateYZ(double theta) {
		_set(x, y * Math.cos(theta) + z * Math.sin(theta), y * -Math.sin(theta)
				+ z * Math.cos(theta), w);
		return this;
	}

	public WB_Point4D rotateXZ(double theta) {

		_set(x * Math.cos(theta) + z * -Math.sin(theta), y, x * Math.sin(theta)
				+ z * Math.cos(theta), w);
		return this;
	}

	public WB_Point4D rotateXW(double theta) {

		_set(x * Math.cos(theta) + w * Math.sin(theta), y, z,
				x * -Math.sin(theta) + w * Math.cos(theta));
		return this;
	}

	public WB_Point4D rotateYW(double theta) {

		_set(x, y * Math.cos(theta) + w * -Math.sin(theta), z,
				y * Math.sin(theta) + w * Math.cos(theta));
		return this;
	}

	public WB_Point4D rotateZW(double theta) {

		_set(x, y, z * Math.cos(theta) + w * -Math.sin(theta),
				z * Math.sin(theta) + w * Math.cos(theta));
		return this;
	}

}
