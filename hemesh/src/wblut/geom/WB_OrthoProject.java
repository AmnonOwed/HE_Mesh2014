/*
 * 
 */
package wblut.geom;

/**
 *
 * WB_OrthoProject projects coordinates from world space to the X, Y or Z-plane.
 * Since a projection is not reversible, the 2D-to-3D functions always return a
 * point on the X-,Y- or Z-plane, unless the w-coordinate is explicitly given.
 *
 */
public class WB_OrthoProject implements WB_Context2D {
    
    /**
     * 
     */
    int id;
    
    /**
     * 
     */
    private int mode;
    
    /**
     * 
     */
    public static final int X = 0;
    
    /**
     * 
     */
    public static final int Y = 1;
    
    /**
     * 
     */
    public static final int Z = 2;
    
    /**
     * 
     */
    public static final int Xrev = 3;
    
    /**
     * 
     */
    public static final int Yrev = 4;
    
    /**
     * 
     */
    public static final int Zrev = 5;
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     */
    protected WB_OrthoProject() {
	this(Z);
    }

    /**
     * 
     *
     * @param mode 
     */
    protected WB_OrthoProject(final int mode) {
	super();
	if ((mode < 0) || (mode > 2)) {
	    throw (new IndexOutOfBoundsException());
	}
	this.mode = mode;
    }

    /**
     * 
     *
     * @param v 
     */
    protected WB_OrthoProject(final WB_Coordinate v) {
    }

    /**
     * 
     *
     * @param c 
     */
    public void set(final WB_Coordinate c) {
	if (c.xd() > c.yd()) {
	    mode = (c.xd() > c.zd()) ? X : Z;
	} else {
	    mode = (c.yd() > c.zd()) ? Y : Z;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#pointTo2D(wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void pointTo2D(final WB_Coordinate p,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(p.xd(), p.yd(), 0);
	    break;
	case X:
	    result.set(p.yd(), p.zd(), 0);
	    break;
	case Y:
	    result.set(p.zd(), p.xd(), 0);
	    break;
	case Zrev:
	    result.set(p.yd(), p.xd(), 0);
	    break;
	case Xrev:
	    result.set(p.zd(), p.yd(), 0);
	    break;
	case Yrev:
	    result.set(p.xd(), p.zd(), 0);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#pointTo2D(double, double, double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void pointTo2D(final double x, final double y, final double z,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(x, y, 0);
	    break;
	case X:
	    result.set(y, z, 0);
	    break;
	case Y:
	    result.set(z, x, 0);
	    break;
	case Zrev:
	    result.set(y, x, 0);
	    break;
	case Xrev:
	    result.set(z, y, 0);
	    break;
	case Yrev:
	    result.set(x, z, 0);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#pointTo3D(wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void pointTo3D(final WB_Coordinate p,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(p.xd(), p.yd(), 0);
	    break;
	case X:
	    result.set(0, p.xd(), p.yd());
	    break;
	case Y:
	    result.set(p.yd(), 0, p.xd());
	    break;
	case Zrev:
	    result.set(p.yd(), p.xd(), 0);
	    break;
	case Xrev:
	    result.set(0, p.yd(), p.xd());
	    break;
	case Yrev:
	    result.set(p.xd(), 0, p.yd());
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#pointTo3D(double, double, double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void pointTo3D(final double u, final double v, final double w,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(u, v, w);
	    break;
	case X:
	    result.set(w, u, v);
	    break;
	case Y:
	    result.set(v, w, u);
	    break;
	case Zrev:
	    result.set(v, u, -w);
	    break;
	case Xrev:
	    result.set(-w, v, u);
	    break;
	case Yrev:
	    result.set(u, -w, v);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#pointTo3D(double, double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void pointTo3D(final double u, final double v,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(u, v, 0);
	    break;
	case X:
	    result.set(0, u, v);
	    break;
	case Y:
	    result.set(v, 0, u);
	    break;
	case Zrev:
	    result.set(v, u, 0);
	    break;
	case Xrev:
	    result.set(0, v, u);
	    break;
	case Yrev:
	    result.set(u, 0, v);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#vectorTo2D(wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void vectorTo2D(final WB_Coordinate v,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(v.xd(), v.yd(), 0);
	    break;
	case X:
	    result.set(v.yd(), v.zd(), 0);
	    break;
	case Y:
	    result.set(v.zd(), v.xd(), 0);
	    break;
	case Zrev:
	    result.set(v.yd(), v.xd(), 0);
	    break;
	case Xrev:
	    result.set(v.zd(), v.yd(), 0);
	    break;
	case Yrev:
	    result.set(v.xd(), v.zd(), 0);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#vectorTo2D(double, double, double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void vectorTo2D(final double x, final double y, final double z,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(x, y, 0);
	    break;
	case X:
	    result.set(y, z, 0);
	    break;
	case Y:
	    result.set(z, x, 0);
	    break;
	case Zrev:
	    result.set(y, x, 0);
	    break;
	case Xrev:
	    result.set(z, y, 0);
	    break;
	case Yrev:
	    result.set(x, z, 0);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#vectorTo3D(wblut.geom.WB_Coordinate, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void vectorTo3D(final WB_Coordinate v,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(v.xd(), v.yd(), 0);
	    break;
	case X:
	    result.set(0, v.xd(), v.yd());
	    break;
	case Y:
	    result.set(v.yd(), 0, v.xd());
	    break;
	case Zrev:
	    result.set(v.yd(), v.xd(), 0);
	    break;
	case Xrev:
	    result.set(0, v.yd(), v.xd());
	    break;
	case Yrev:
	    result.set(v.xd(), 0, v.yd());
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#vectorTo3D(double, double, double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void vectorTo3D(final double u, final double v, final double w,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(u, v, w);
	    break;
	case X:
	    result.set(w, u, v);
	    break;
	case Y:
	    result.set(v, w, u);
	    break;
	case Zrev:
	    result.set(v, u, -w);
	    break;
	case Xrev:
	    result.set(-w, v, u);
	    break;
	case Yrev:
	    result.set(u, -w, v);
	    break;
	}
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Context2D#vectorTo3D(double, double, wblut.geom.WB_MutableCoordinate)
     */
    @Override
    public void vectorTo3D(final double u, final double v,
	    final WB_MutableCoordinate result) {
	switch (mode) {
	case Z:
	    result.set(u, v, 0);
	    break;
	case X:
	    result.set(0, u, v);
	    break;
	case Y:
	    result.set(v, 0, u);
	    break;
	case Zrev:
	    result.set(v, u, 0);
	    break;
	case Xrev:
	    result.set(0, v, u);
	    break;
	case Yrev:
	    result.set(u, 0, v);
	    break;
	}
    }
}
