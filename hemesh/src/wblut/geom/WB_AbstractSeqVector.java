package wblut.geom;

public abstract class WB_AbstractSeqVector implements
		Comparable<WB_Coordinate>, WB_MutableCoordinate {

	/** Coordinates. */
	private int i;
	private WB_CoordinateSequence seq;
	private int id;

	public WB_AbstractSeqVector(int i, WB_CoordinateSequence seq) {
		this.i = i;
		this.id = 4 * i;
		this.seq = seq;
	}

	public void _set(final double x, final double y) {
		seq._setRaw(id, x);
		seq._setRaw(id + 1, y);
		seq._setRaw(id + 2, 0);
	}

	public void _set(final double x, final double y, final double z) {
		seq._setRaw(id, x);
		seq._setRaw(id + 1, y);
		seq._setRaw(id + 2, z);
	}

	@Override
	public void _set(double x, double y, double z, double w) {
		_set(x, y, z);

	}

	public void _set(final WB_Coordinate v) {
		_set(v.xd(), v.yd(), v.zd());
	}

	@Override
	public void _setW(double w) {

	}

	@Override
	public void _setX(double x) {
		seq._setRaw(id, x);

	}

	@Override
	public void _setY(double y) {
		seq._setRaw(id + 1, y);

	}

	@Override
	public void _setZ(double z) {
		seq._setRaw(id + 2, z);

	}

	@Override
	public void _setCoord(int i, double v) {
		if (i == 0) {
			seq._setRaw(id, v);
		}
		if (i == 1) {
			seq._setRaw(id + 1, v);
		}
		if (i == 2) {
			seq._setRaw(id + 2, v);
		}

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
		return seq.getRaw(id);
	}

	public float xf() {
		return (float) seq.getRaw(id);
	}

	@Override
	public double yd() {
		return seq.getRaw(id + 1);
	}

	public float yf() {
		return (float) seq.getRaw(id + 1);
	}

	@Override
	public double zd() {
		return seq.getRaw(id + 2);
	}

	public float zf() {
		return (float) seq.getRaw(id + 2);
	}

	public double getd(final int i) {
		if (i == 0) {
			return seq.getRaw(id);
		}
		if (i == 1) {
			return seq.getRaw(id + 1);
		}
		if (i == 2) {
			return seq.getRaw(id + 2);
		}
		return Double.NaN;
	}

	public float getf(final int i) {
		if (i == 0) {
			return (float) seq.getRaw(id);
		}
		if (i == 1) {
			return (float) seq.getRaw(id + 1);
		}
		if (i == 2) {
			return (float) seq.getRaw(id + 2);
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
