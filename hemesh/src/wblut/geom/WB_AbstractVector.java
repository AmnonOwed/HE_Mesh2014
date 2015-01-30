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

    public WB_AbstractVector(final double[] fromPoint, final double[] toPoint) {
	this.x = toPoint[0] - fromPoint[0];
	this.y = toPoint[1] - fromPoint[1];
	this.z = toPoint[2] - fromPoint[2];
    }

    public WB_AbstractVector(final WB_Coordinate v) {
	x = v.xd();
	y = v.yd();
	z = v.zd();
    }

    public WB_AbstractVector(final WB_Coordinate fromPoint,
	    final WB_Coordinate toPoint) {
	x = toPoint.xd() - fromPoint.xd();
	y = toPoint.yd() - fromPoint.yd();
	z = toPoint.zd() - fromPoint.zd();
    }

    /**
     * @deprecated Use {@link #set(double,double)} instead
     */
    @Deprecated
    @Override
    public void _set(final double x, final double y) {
	set(x, y);
    }

    @Override
    public void set(final double x, final double y) {
	this.x = x;
	this.y = y;
	z = 0;
    }

    /**
     * @deprecated Use {@link #set(double,double,double)} instead
     */
    @Deprecated
    @Override
    public void _set(final double x, final double y, final double z) {
	set(x, y, z);
    }

    @Override
    public void set(final double x, final double y, final double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
     * @deprecated Use {@link #set(double,double,double,double)} instead
     */
    @Deprecated
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
    @Deprecated
    @Override
    public void _set(final WB_Coordinate v) {
	set(v);
    }

    @Override
    public void set(final WB_Coordinate v) {
	set(v.xd(), v.yd(), v.zd());
    }

    /**
     * @deprecated Use {@link #setCoord(int,double)} instead
     */
    @Deprecated
    @Override
    public void _setCoord(final int i, final double v) {
	setCoord(i, v);
    }

    @Override
    public void setCoord(final int i, final double v) {
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

    /**
     * @deprecated Use {@link #setW(double)} instead
     */
    @Deprecated
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
    @Deprecated
    @Override
    public void _setX(final double x) {
	setX(x);
    }

    @Override
    public void setX(final double x) {
	this.x = x;
    }

    /**
     * @deprecated Use {@link #setY(double)} instead
     */
    @Deprecated
    @Override
    public void _setY(final double y) {
	setY(y);
    }

    @Override
    public void setY(final double y) {
	this.y = y;
    }

    /**
     * @deprecated Use {@link #setZ(double)} instead
     */
    @Deprecated
    @Override
    public void _setZ(final double z) {
	setZ(z);
    }

    @Override
    public void setZ(final double z) {
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

    @Override
    public float xf() {
	return (float) x;
    }

    @Override
    public double yd() {
	return y;
    }

    @Override
    public float yf() {
	return (float) y;
    }

    @Override
    public double zd() {
	return z;
    }

    @Override
    public float zf() {
	return (float) z;
    }

    @Override
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

    @Override
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
}
