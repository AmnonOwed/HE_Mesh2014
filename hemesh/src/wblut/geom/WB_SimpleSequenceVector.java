/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_SimpleSequenceVector implements Comparable<WB_Coordinate>,
WB_MutableCoordinate {
    /** Coordinates. */
    private int i;
    
    /**
     * 
     */
    private final WB_CoordinateSequence seq;
    
    /**
     * 
     */
    private int offset;

    /**
     * 
     *
     * @param i 
     * @param seq 
     */
    public WB_SimpleSequenceVector(final int i, final WB_CoordinateSequence seq) {
	this.i = i;
	this.offset = 4 * i;
	this.seq = seq;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#set(double, double)
     */
    @Override
    public void set(final double x, final double y) {
	seq._setRaw(offset, x);
	seq._setRaw(offset + 1, y);
	seq._setRaw(offset + 2, 0);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#set(double, double, double)
     */
    @Override
    public void set(final double x, final double y, final double z) {
	seq._setRaw(offset, x);
	seq._setRaw(offset + 1, y);
	seq._setRaw(offset + 2, z);
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
	seq._setRaw(offset, x);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setY(double)
     */
    @Override
    public void setY(final double y) {
	seq._setRaw(offset + 1, y);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setZ(double)
     */
    @Override
    public void setZ(final double z) {
	seq._setRaw(offset + 2, z);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MutableCoordinate#setCoord(int, double)
     */
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
	return seq.getRaw(offset);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#xf()
     */
    @Override
    public float xf() {
	return (float) seq.getRaw(offset);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#yd()
     */
    @Override
    public double yd() {
	return seq.getRaw(offset + 1);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#yf()
     */
    @Override
    public float yf() {
	return (float) seq.getRaw(offset + 1);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#zd()
     */
    @Override
    public double zd() {
	return seq.getRaw(offset + 2);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#zf()
     */
    @Override
    public float zf() {
	return (float) seq.getRaw(offset + 2);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#getd(int)
     */
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_Coordinate#getf(int)
     */
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

    /**
     * 
     *
     * @return 
     */
    public int getIndex() {
	return i;
    }

    /**
     * 
     *
     * @param i 
     */
    public void setIndex(final int i) {
	this.i = i;
	this.offset = 4 * i;
    }
}
