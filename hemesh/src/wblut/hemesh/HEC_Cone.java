/*
 *
 */
package wblut.hemesh;

import wblut.geom.WB_Vector;

/**
 * Cone.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Cone extends HEC_Creator {
    /** Base radius. */
    private double R;
    /** Height. */
    private double H;
    /** Height segments. */
    private int steps;
    /** Facets. */
    private int facets;
    /** The cap. */
    private boolean cap;
    /** The reverse. */
    private boolean reverse;
    /** The taper. */
    private double taper;

    /**
     * Instantiates a new cone.
     *
     */
    public HEC_Cone() {
	super();
	R = 0;
	H = 0;
	facets = 6;
	steps = 1;
	Z = new WB_Vector(WB_Vector.Y());
	cap = true;
	taper = 1.0;
    }

    /**
     * Instantiates a new cone.
     *
     * @param R
     *            radius
     * @param H
     *            heights
     * @param facets
     *            number of facets
     * @param steps
     *            number of height divisions
     */
    public HEC_Cone(final double R, final double H, final int facets,
	    final int steps) {
	this();
	this.R = R;
	this.H = H;
	this.facets = facets;
	this.steps = steps;
	taper = 1.0;
    }

    /**
     * Set base radius.
     *
     * @param R
     *            base radius
     * @return self
     */
    public HEC_Cone setRadius(final double R) {
	this.R = R;
	return this;
    }

    /**
     * Set height.
     *
     * @param H
     *            height
     * @return self
     */
    public HEC_Cone setHeight(final double H) {
	this.H = H;
	return this;
    }

    /**
     * Set number of sides.
     *
     * @param facets
     *            number of sides
     * @return self
     */
    public HEC_Cone setFacets(final int facets) {
	this.facets = facets;
	return this;
    }

    /**
     * Set number of vertical divisions.
     *
     * @param steps
     *            vertical divisions
     * @return self
     */
    public HEC_Cone setSteps(final int steps) {
	this.steps = steps;
	return this;
    }

    /**
     * Set capping options.
     *
     * @param cap
     *            create cap?
     * @return self
     */
    public HEC_Cone setCap(final boolean cap) {
	this.cap = cap;
	return this;
    }

    /**
     * Reverse cone.
     *
     * @param rev
     *            the rev
     * @return self
     */
    public HEC_Cone setReverse(final boolean rev) {
	reverse = rev;
	return this;
    }

    /**
     * Sets the taper.
     *
     * @param t
     *            the t
     * @return the hE c_ cone
     */
    public HEC_Cone setTaper(final double t) {
	taper = t;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	final double[][] vertices = new double[(facets + 1) * steps + facets
		+ ((cap) ? facets : 0)][3];
	final double[][] uvws = new double[(facets + 1) * steps + facets
		+ ((cap) ? facets : 0)][3];
	final int[][] faces = new int[(cap) ? (facets * steps) + facets
		: facets * steps][];
	final int[] faceTextureIds = new int[(cap) ? (facets * steps) + facets
		: facets * steps];
	double Ri;
	double Hj;
	final double invs = 1.0 / steps;
	int id = 0;
	for (int i = 0; i < steps; i++) {
	    Ri = R - (Math.pow(i * invs, taper) * R);
	    Hj = (reverse) ? H - ((i * H) / steps) : (i * H) / steps;
	    for (int j = 0; j < facets + 1; j++) {
		vertices[id][0] = Ri * Math.cos(((2 * Math.PI) / facets) * j);
		vertices[id][2] = Ri * Math.sin(((2 * Math.PI) / facets) * j);
		vertices[id][1] = Hj;
		uvws[id][0] = j * 1.0 / facets;
		uvws[id][1] = i * 1.0 / steps;
		uvws[id][2] = 0;
		id++;
	    }
	}
	final int tipoffset = id;
	for (int j = 0; j < facets; j++) {
	    vertices[id][0] = 0;
	    vertices[id][2] = 0;
	    vertices[id][1] = (reverse) ? 0 : H;
	    uvws[id][0] = 0.5;
	    uvws[id][1] = 1;
	    uvws[id][2] = 0;
	    id++;
	}
	final int capoffset = id;
	if (cap) {
	    for (int j = 0; j < facets; j++) {
		vertices[id][0] = 0;
		vertices[id][2] = 0;
		vertices[id][1] = (reverse) ? H : 0;
		uvws[id][0] = 0.5;
		uvws[id][1] = 1;
		uvws[id][2] = 0;
		id++;
	    }
	}
	id = 0;
	for (int j = 0; j < facets; j++) {
	    for (int i = 0; i < (steps - 1); i++) {
		faces[id] = new int[4];
		faceTextureIds[id] = 0;
		faces[id][0] = j + i * (facets + 1);
		faces[id][1] = j + (i + 1) * (facets + 1);
		faces[id][2] = j + 1 + (i + 1) * (facets + 1);
		faces[id][3] = j + 1 + i * (facets + 1);
		id++;
	    }
	    faces[id] = new int[3];
	    faceTextureIds[id] = 0;
	    faces[id][0] = tipoffset + j;
	    faces[id][2] = j + (steps - 1) * (facets + 1);
	    faces[id][1] = j + 1 + (steps - 1) * (facets + 1);
	    id++;
	    if (cap) {
		faces[id] = new int[3];
		faceTextureIds[id] = 1;
		faces[id][0] = j;
		faces[id][2] = j + capoffset;
		faces[id][1] = (j + 1);
		id++;
	    }
	}
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setVertices(vertices).setUVW(uvws).setFaces(faces)
	.setFaceTextureIds(faceTextureIds);
	return fl.createBase();
    }
}
