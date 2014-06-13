package wblut.geom;

public abstract class WB_AbstractVector implements Comparable<WB_Coordinate>,
		WB_MutableCoordinate {

	/** Coordinates. */
	private double x, y, z;

	public WB_AbstractVector() {
		x = y = z = 0;
	}

	public WB_AbstractVector(final double x, final double y) {
		this.x = x;
		this.y = y;
		z = 0;
	}

	public WB_AbstractVector(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WB_AbstractVector(final double[] x) {
		this.x = x[0];
		this.y = x[1];
		this.z = x[2];
	}

	public WB_AbstractVector(final double[] p1, final double[] p2) {
		this.x = p2[0] - p1[0];
		this.y = p2[1] - p1[1];
		this.z = p2[2] - p1[2];
	}

	public WB_AbstractVector(final WB_Coordinate v) {
		x = v.xd();
		y = v.yd();
		z = v.zd();
	}

	public WB_AbstractVector(final WB_Coordinate p1, final WB_Coordinate p2) {
		x = p2.xd() - p1.xd();
		y = p2.yd() - p1.yd();
		z = p2.zd() - p1.zd();
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

}
