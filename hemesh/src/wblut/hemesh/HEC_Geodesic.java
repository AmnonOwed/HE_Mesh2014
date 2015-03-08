/*
 *
 */
package wblut.hemesh;

import wblut.geom.WB_Geodesic;

/**
 *
 */
public class HEC_Geodesic extends HEC_Creator {
    /**
     *
     */
    public static final int TETRAHEDRON = 0;
    /**
     *
     */
    public static final int OCTAHEDRON = 1;
    /**
     *
     */
    public static final int CUBE = 2;
    /**
     *
     */
    public static final int DODECAHEDRON = 3;
    /**
     *
     */
    public static final int ICOSAHEDRON = 4;
    /**
     *
     */
    private double rx, ry, rz;
    /**
     *
     */
    private int type;
    /**
     *
     */
    private int b;
    /**
     *
     */
    private int c;

    /**
     *
     */
    public HEC_Geodesic() {
	super();
	rx = ry = rz = 1;
	type = 4;
	b = c = 4;
    }

    /**
     *
     *
     * @param R
     */
    public HEC_Geodesic(final double R) {
	this();
	rx = ry = rz = R;
	b = c = 4;
    }

    /**
     *
     *
     * @param R
     * @return
     */
    public HEC_Geodesic setRadius(final double R) {
	rx = ry = rz = R;
	return this;
    }

    /**
     *
     *
     * @param rx
     * @param ry
     * @param rz
     * @return
     */
    public HEC_Geodesic setRadius(final double rx, final double ry,
	    final double rz) {
	this.rx = rx;
	this.ry = ry;
	this.rz = rz;
	return this;
    }

    /**
     *
     *
     * @param b
     * @return
     */
    public HEC_Geodesic setB(final int b) {
	this.b = b;
	return this;
    }

    /**
     *
     *
     * @param c
     * @return
     */
    public HEC_Geodesic setC(final int c) {
	this.c = c;
	return this;
    }

    /**
     *
     *
     * @param t
     * @return
     */
    public HEC_Geodesic setType(final int t) {
	type = t;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	final WB_Geodesic geo = new WB_Geodesic(1.0, b, c, type);
	final HE_Mesh mesh = new HE_Mesh(new HEC_FromMesh(geo));
	mesh.scale(rx, ry, rz);
	return mesh;
    }
}
