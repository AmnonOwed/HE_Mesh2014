/*
 *
 */
package wblut.hemesh;

import wblut.geom.WB_Point;

/**
 * Torus.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Torus extends HEC_Creator {
    /** Tube radius. */
    private double Ri;
    /** Torus Radius. */
    private double Ro;
    /** Facets. */
    private int tubefacets;
    /** Height steps. */
    private int torusfacets;
    /** The twist. */
    private int twist;

    public HEC_Torus() {
	super();
	Ri = 0;
	Ro = 0;
	tubefacets = 6;
	torusfacets = 6;
    }

    /**
     * Instantiates a new torus.
     *
     * @param Ri
     *
     * @param Ro
     *
     * @param tubefacets
     *
     * @param torusfacets
     *
     */
    public HEC_Torus(final double Ri, final double Ro, final int tubefacets,
	    final int torusfacets) {
	this();
	this.Ri = Ri;
	this.Ro = Ro;
	this.tubefacets = tubefacets;
	this.torusfacets = torusfacets;
    }

    /**
     * Sets the radius.
     *
     * @param Ri
     *
     * @param Ro
     *
     * @return
     */
    public HEC_Torus setRadius(final double Ri, final double Ro) {
	this.Ri = Ri;
	this.Ro = Ro;
	return this;
    }

    /**
     * Sets the tube facets.
     *
     * @param facets
     *
     * @return
     */
    public HEC_Torus setTubeFacets(final int facets) {
	tubefacets = facets;
	return this;
    }

    /**
     * Sets the torus facets.
     *
     * @param facets
     *
     * @return
     */
    public HEC_Torus setTorusFacets(final int facets) {
	torusfacets = facets;
	return this;
    }

    /**
     * Sets twist.
     *
     * @param t
     *
     * @return
     */
    public HEC_Torus setTwist(final int t) {
	twist = Math.max(0, t);
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	final WB_Point[] vertices = new WB_Point[(tubefacets + 1)
		* (torusfacets + 1)];
	final WB_Point[] uvws = new WB_Point[(tubefacets + 1)
		* (torusfacets + 1)];
	final double dtua = (2 * Math.PI) / tubefacets;
	final double dtoa = (2 * Math.PI) / torusfacets;
	final double dv = 1.0 / tubefacets;
	final double du = 1.0 / torusfacets;
	final double dtwa = (twist * dtoa) / tubefacets;
	int id = 0;
	WB_Point basevertex;
	for (int j = 0; j < torusfacets + 1; j++) {
	    final int lj = (j == torusfacets) ? 0 : j;
	    final double ca = Math.cos(lj * dtoa);
	    final double sa = Math.sin(lj * dtoa);
	    for (int i = 0; i < tubefacets + 1; i++) {
		final int li = (i == tubefacets) ? 0 : i;
		basevertex = new WB_Point(Ro
			+ (Ri * Math.cos((dtua * li) + (j * dtwa))), 0, Ri
			* Math.sin((dtua * li) + (j * dtwa)));
		vertices[id] = new WB_Point(ca * basevertex.xd(), sa
			* basevertex.xd(), basevertex.zd());
		uvws[id] = new WB_Point(j * du, i * dv, 0);
		id++;
	    }
	}
	final int nfaces = tubefacets * torusfacets;
	id = 0;
	final int[][] faces = new int[nfaces][];
	int j = 0;
	for (j = 0; j < torusfacets; j++) {
	    for (int i = 0; i < tubefacets; i++) {
		faces[id] = new int[4];
		faces[id][0] = i + (j * (tubefacets + 1));
		faces[id][1] = i + ((j + 1) * (tubefacets + 1));
		faces[id][2] = (i + 1) + ((j + 1) * (tubefacets + 1));
		faces[id][3] = (i + 1) + (j * (tubefacets + 1));
		id++;
	    }
	}
	/*
	 * for (int i = 0; i < tubefacets; i++) { faces[id] = new int[4];
	 * faces[id][0] = i + ((torusfacets - 1) * (tubefacets + 1));
	 * faces[id][1] = (i + twist) % (tubefacets + 1) + (torusfacets *
	 * (tubefacets + 1)); faces[id][2] = (i + twist + 1) % (tubefacets + 1)
	 * + (torusfacets * (tubefacets + 1)); faces[id][3] = (i + 1) +
	 * ((torusfacets - 1) * (tubefacets + 1)); id++; }
	 */
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setVertices(vertices).setFaces(faces).setUVW(uvws);
	return fl.createBase();
    }
}
