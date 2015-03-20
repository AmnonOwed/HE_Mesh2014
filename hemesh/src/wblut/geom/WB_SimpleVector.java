/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_SimpleVector implements Comparable<WB_Coordinate>,
WB_MutableCoordinate {
    /** Coordinates. */
    private double x, y, z;

    /**
     * 
     */
    public WB_SimpleVector() {
	x = y = z = 0;
    }

    /**
     * 
     *
     * @param x 
     * @param y 
     */
    public WB_SimpleVector(final double x, final double y) {
	this.x = x;
	this.y = y;
	z = 0;
    }

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     */
    public WB_SimpleVector(final double x, final double y, final double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /**
     * 
     *
     * @param x 
     */
    public WB_SimpleVector(final double[] x) {
	this.x = x[0];
	this.y = x[1];
	this.z = x[2];
    }

    /**
     * 
     *
     * @param fromPoint 
     * @param toPoint 
     */
    public WB_SimpleVector(final double[] fromPoint, final double[] toPoint) {
	this.x = toPoint[0] - fromPoint[0];
	this.y = toPoint[1] - fromPoint[1];
	this.z = toPoint[2] - fromPoint[2];
    }

    /**
     * 
     *
     * @param v 
     */
    public WB_SimpleVector(final WB_Coordinate v) {
	x = v.xd();
	y = v.yd();
	z = v.zd();
    }

    /**
     * 
     *
     * @param fromPoint 
     * @param toPoint 
     */
    public WB_SimpleVector(final WB_Coordinate fromPoint,
	    final WB_Coordinate toPoint) {
	x = toPoint.xd() - fromPoint.xd();
	y = toPoint.yd() - fromPoint.yd();
	z = toPoint.zd() - fromPoint.zd();
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#set(double, double)
     */
    @Override
    public void set(final double x, final double y) {
	this.x = x;
	this.y = y;
	z = 0;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#set(double, double, double)
     */
    @Override
    public void set(final double x, final double y, final double z) {
	this.x = x;
	this.y = y;
	this.z = z;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#set(double, double, double, double)
     */
    @Override
    public void set(final double x, final double y, final double z,
	    final double w) {
	set(x, y, z);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#set(wblut.geom.WB_Coordinate)
     */
    @Override
    public void set(final WB_Coordinate v) {
	set(v.xd(), v.yd(), v.zd());
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setCoord(int, double)
     */
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setW(double)
     */
    @Override
    public void setW(final double w) {
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setX(double)
     */
    @Override
    public void setX(final double x) {
	this.x = x;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setY(double)
     */
    @Override
    public void setY(final double y) {
	this.y = y;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setZ(double)
     */
    @Override
    public void setZ(final double z) {
	this.z = z;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#wd()
     */
    @Override
    public double wd() {
	return 0;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#wf()
     */
    @Override
    public float wf() {
	return 0;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#xd()
     */
    @Override
    public double xd() {
	return x;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#xf()
     */
    @Override
    public float xf() {
	return (float) x;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#yd()
     */
    @Override
    public double yd() {
	return y;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#yf()
     */
    @Override
    public float yf() {
	return (float) y;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#zd()
     */
    @Override
    public double zd() {
	return z;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#zf()
     */
    @Override
    public float zf() {
	return (float) z;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#getd(int)
     */
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#getf(int)
     */
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

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
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
