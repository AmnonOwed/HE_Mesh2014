package wblut.geom;

public abstract class WB_AbstractSeqVector implements
Comparable<WB_Coordinate>, WB_MutableCoordinate {

	/** Coordinates. */
	private int i;
	private final WB_CoordinateSequence seq;
	private int offset;

	public WB_AbstractSeqVector(final int i, final WB_CoordinateSequence seq) {
		this.i = i;
		this.offset = 4 * i;
		this.seq = seq;
	}

	/**
	 * @deprecated Use {@link #set(double,double)} instead
	 */
	@Override
	public void _set(final double x, final double y) {
		set(x, y);
	}

	@Override
	public void set(final double x, final double y) {
		seq._setRaw(offset, x);
		seq._setRaw(offset + 1, y);
		seq._setRaw(offset + 2, 0);
	}

	/**
	 * @deprecated Use {@link #set(double,double,double)} instead
	 */
	@Override
	public void _set(final double x, final double y, final double z) {
		set(x, y, z);
	}

	@Override
	public void set(final double x, final double y, final double z) {
		seq._setRaw(offset, x);
		seq._setRaw(offset + 1, y);
		seq._setRaw(offset + 2, z);
	}

	/**
	 * @deprecated Use {@link #set(double,double,double,double)} instead
	 */
	@Override
	public void _set(final double x, final double y, final double z,
			final double w) {
				set(x, y, z, w);
			}

	@Override
	public void set(final double x, final double y, final double z,
			final double w) {
		set(x, y, z);

	}

	/**
	 * @deprecated Use {@link #set(WB_Coordinate)} instead
	 */
	@Override
	public void _set(final WB_Coordinate v) {
		set(v);
	}

	@Override
	public void set(final WB_Coordinate v) {
		set(v.xd(), v.yd(), v.zd());
	}

	/**
	 * @deprecated Use {@link #setW(double)} instead
	 */
	@Override
	public void _setW(final double w) {
		setW(w);
	}

	@Override
	public void setW(final double w) {

	}

	/**
	 * @deprecated Use {@link #setX(double)} instead
	 */
	@Override
	public void _setX(final double x) {
		setX(x);
	}

	@Override
	public void setX(final double x) {
		seq._setRaw(offset, x);

	}

	/**
	 * @deprecated Use {@link #setY(double)} instead
	 */
	@Override
	public void _setY(final double y) {
		setY(y);
	}

	@Override
	public void setY(final double y) {
		seq._setRaw(offset + 1, y);

	}

	/**
	 * @deprecated Use {@link #setZ(double)} instead
	 */
	@Override
	public void _setZ(final double z) {
		setZ(z);
	}

	@Override
	public void setZ(final double z) {
		seq._setRaw(offset + 2, z);

	}

	/**
	 * @deprecated Use {@link #setCoord(int,double)} instead
	 */
	@Override
	public void _setCoord(final int i, final double v) {
		setCoord(i, v);
	}

	@Override
	public void setCoord(final int i, final double v) {
		if (i == 0) {
			seq._setRaw(offset, v);
		}
		if (i == 1) {
			seq._setRaw(offset + 1, v);
		}
		if (i == 2) {
			seq._setRaw(offset + 2, v);
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
		return seq.getRaw(offset);
	}

	@Override
	public float xf() {
		return (float) seq.getRaw(offset);
	}

	@Override
	public double yd() {
		return seq.getRaw(offset + 1);
	}

	@Override
	public float yf() {
		return (float) seq.getRaw(offset + 1);
	}

	@Override
	public double zd() {
		return seq.getRaw(offset + 2);
	}

	@Override
	public float zf() {
		return (float) seq.getRaw(offset + 2);
	}

	@Override
	public double getd(final int i) {
		if (i == 0) {
			return seq.getRaw(offset);
		}
		if (i == 1) {
			return seq.getRaw(offset + 1);
		}
		if (i == 2) {
			return seq.getRaw(offset + 2);
		}
		return Double.NaN;
	}

	@Override
	public float getf(final int i) {
		if (i == 0) {
			return (float) seq.getRaw(offset);
		}
		if (i == 1) {
			return (float) seq.getRaw(offset + 1);
		}
		if (i == 2) {
			return (float) seq.getRaw(offset + 2);
		}
		return Float.NaN;
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

	public int getIndex() {
		return i;
	}

	public void setIndex(final int i) {
		this.i = i;
		this.offset = 4 * i;

	}

}
