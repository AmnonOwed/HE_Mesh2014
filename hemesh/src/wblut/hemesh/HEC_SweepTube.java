/*
 * 
 */
package wblut.hemesh;

import wblut.geom.WB_BSpline;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * Circle swept along curve.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_SweepTube extends HEC_Creator {
    /** Base radius. */
    private double R;
    /** Facets. */
    private int facets;
    /** Steps along curve. */
    private int steps;
    /** Sweep curve. */
    private WB_BSpline curve;
    
    /**
     * 
     */
    private boolean topcap;
    
    /**
     * 
     */
    private boolean bottomcap;

    /**
     * 
     */
    public HEC_SweepTube() {
	super();
	R = 0;
	facets = 6;
	steps = 1;
	topcap = true;
	bottomcap = true;
	override = true;
    }

    /**
     * 
     *
     * @param R 
     * @param facets 
     * @param steps 
     * @param curve 
     */
    public HEC_SweepTube(final double R, final int facets, final int steps,
	    final WB_BSpline curve) {
	this();
	this.R = R;
	this.facets = facets;
	this.steps = steps;
	this.curve = curve;
	override = true;
    }

    /**
     * Set fixed radius.
     *
     * @param R
     *            radius
     * @return self
     */
    public HEC_SweepTube setRadius(final double R) {
	this.R = R;
	return this;
    }

    /**
     * Set vertical divisions.
     *
     * @param steps
     *            vertical divisions
     * @return self
     */
    public HEC_SweepTube setSteps(final int steps) {
	this.steps = steps;
	return this;
    }

    /**
     * Set number of sides.
     *
     * @param facets
     *            number of sides
     * @return self
     */
    public HEC_SweepTube setFacets(final int facets) {
	this.facets = facets;
	return this;
    }

    /**
     * Set capping options.
     *
     * @param topcap
     *            create top cap?
     * @param bottomcap
     *            create bottom cap?
     * @return self
     */
    public HEC_SweepTube setCap(final boolean topcap, final boolean bottomcap) {
	this.topcap = topcap;
	this.bottomcap = bottomcap;
	return this;
    }

    /**
     * 
     *
     * @param curve 
     * @return 
     */
    public HEC_SweepTube setCurve(final WB_BSpline curve) {
	this.curve = curve;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	final WB_Point[] vertices = new WB_Point[(steps + 1) * facets];
	final WB_Point[] basevertices = new WB_Point[facets];
	final double da = (2 * Math.PI) / facets;
	for (int i = 0; i < facets; i++) {
	    // normal(0,0,1);
	    basevertices[i] = new WB_Point(R * Math.cos(da * i), R
		    * Math.sin(da * i), 0);
	}
	final double ds = 1.0 / steps;
	WB_Point onCurve;
	WB_Vector deriv;
	WB_Vector oldderiv = new WB_Vector(0, 0, 1);
	final WB_Point origin = new WB_Point(0, 0, 0);
	for (int i = 0; i < (steps + 1); i++) {
	    onCurve = curve.curvePoint(i * ds);
	    if (curve.p() == 1) {
		deriv = new WB_Vector(0, 0, 0);
		if (i > 0) {
		    deriv.addSelf(onCurve.subToVector3D(curve
			    .curvePoint((i - 1) * ds)));
		}
		if (i < steps) {
		    deriv.addSelf(curve.curvePoint((i + 1) * ds).subToVector3D(
			    onCurve));
		}
		deriv.normalizeSelf();
	    } else {
		deriv = curve.curveFirstDeriv(i * ds);
	    }
	    final WB_Vector axis = oldderiv.cross(deriv);
	    final double angle = Math.acos(WB_Math.clamp(oldderiv.dot(deriv),
		    -1, 1));
	    for (int j = 0; j < facets; j++) {
		if (!WB_Epsilon.isZeroSq(axis.getSqLength3D())) {
		    basevertices[j].rotateAboutAxisSelf(angle, origin, axis);
		}
		vertices[j + (i * facets)] = new WB_Point(basevertices[j]);
		vertices[j + (i * facets)].addSelf(onCurve);
	    }
	    oldderiv = deriv;
	}
	int nfaces = steps * facets;
	int bc = 0;
	int tc = 0;
	if (bottomcap) {
	    bc = nfaces;
	    nfaces++;
	}
	if (topcap) {
	    tc = nfaces;
	    nfaces++;
	}
	final int[][] faces = new int[nfaces][];
	if (bottomcap) {
	    faces[bc] = new int[facets];
	}
	if (topcap) {
	    faces[tc] = new int[facets];
	}
	for (int j = 0; j < facets; j++) {
	    if (bottomcap) {
		faces[bc][facets - 1 - j] = j;
	    }
	    if (topcap) {
		faces[tc][j] = (steps * facets) + j;
	    }
	    for (int i = 0; i < steps; i++) {
		faces[j + (i * facets)] = new int[4];
		faces[j + (i * facets)][0] = j + (i * facets);
		faces[j + (i * facets)][3] = j + (i * facets) + facets;
		faces[j + (i * facets)][2] = ((j + 1) % facets) + facets
			+ (i * facets);
		faces[j + (i * facets)][1] = ((j + 1) % facets) + (i * facets);
	    }
	}
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setVertices(vertices).setFaces(faces);
	return fl.createBase();
    }
}
