/*
 * 
 */
package wblut.hemesh;

import wblut.geom.WB_Point;
import wblut.geom.WB_Surface;

/**
 * 
 */
public class HEC_FromSurface extends HEC_Creator {
    
    /**
     * 
     */
    private int U;
    
    /**
     * 
     */
    private int V;
    
    /**
     * 
     */
    private WB_Surface surf;
    
    /**
     * 
     */
    private boolean uWrap;
    
    /**
     * 
     */
    private boolean vWrap;

    /**
     * 
     */
    public HEC_FromSurface() {
	super();
	override = true;
    }

    /**
     * 
     *
     * @param surf 
     * @param U 
     * @param V 
     * @param uWrap 
     * @param vWrap 
     */
    public HEC_FromSurface(final WB_Surface surf, final int U, final int V,
	    final boolean uWrap, final boolean vWrap) {
	this();
	this.U = U;
	this.V = V;
	this.uWrap = uWrap;
	this.vWrap = vWrap;
	this.surf = surf;
    }

    /**
     * 
     *
     * @param U 
     * @return 
     */
    public HEC_FromSurface setU(final int U) {
	this.U = U;
	return this;
    }

    /**
     * 
     *
     * @param V 
     * @return 
     */
    public HEC_FromSurface setV(final int V) {
	this.V = V;
	return this;
    }

    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEC_FromSurface setUWrap(final boolean b) {
	uWrap = b;
	return this;
    }

    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEC_FromSurface setVWrap(final boolean b) {
	vWrap = b;
	return this;
    }

    /**
     * 
     *
     * @param surf 
     * @return 
     */
    public HEC_FromSurface setSurface(final WB_Surface surf) {
	this.surf = surf;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.nurbs.WB_Surface#toHemesh(PApplet, double, double)
     */
    @Override
    public HE_Mesh createBase() {
	final WB_Point[] points = new WB_Point[(U + 1) * (V + 1)];
	final double iU = 1.0 / U;
	final double iV = 1.0 / V;
	double u;
	double v;
	for (int i = 0; i <= U; i++) {
	    u = surf.loweru() + (i * iU * (surf.upperu() - surf.loweru()));
	    for (int j = 0; j <= V; j++) {
		v = surf.lowerv() + (j * iV * (surf.upperv() - surf.lowerv()));
		points[i + ((U + 1) * j)] = surf.surfacePoint(u, v);
	    }
	}
	final int[][] faces = new int[U * V][4];
	int li, lj;
	for (int i = 0; i < U; i++) {
	    li = (uWrap && (i == (U - 1))) ? 0 : i + 1;
	    for (int j = 0; j < V; j++) {
		lj = (vWrap && (j == (V - 1))) ? 0 : j + 1;
		faces[i + (U * j)][0] = i + ((U + 1) * j);
		faces[i + (U * j)][1] = li + ((U + 1) * j);
		faces[i + (U * j)][2] = li + ((U + 1) * lj);
		faces[i + (U * j)][3] = i + ((U + 1) * lj);
	    }
	}
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setFaces(faces).setVertices(points).setDuplicate(false)
		.setCheckNormals(false);
	return new HE_Mesh(fl);
    }
}
