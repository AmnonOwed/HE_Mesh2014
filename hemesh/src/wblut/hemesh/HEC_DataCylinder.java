/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import java.util.List;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class HEC_DataCylinder extends HEC_Creator {
    
    /**
     * 
     */
    private double Ri;
    
    /**
     * 
     */
    private double Ro;
    
    /**
     * 
     */
    private double H;
    
    /**
     * 
     */
    private int _facets;
    
    /**
     * 
     */
    private int _steps;
    
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
    private double taper;
    
    /**
     * 
     */
    private double[][] data;
    
    /**
     * 
     */
    private boolean spiky;
    
    /**
     * 
     */
    private double chamfer;
    
    /**
     * 
     */
    private int reduceSteps;
    
    /**
     * 
     */
    private int reduceFacets;

    /**
     * 
     */
    public HEC_DataCylinder() {
	super();
	Ri = 0;
	Ro = 0;
	H = 0;
	_facets = 6;
	_steps = 1;
	Z = new WB_Vector(WB_Vector.Y());
	topcap = true;
	bottomcap = true;
	taper = 1.0;
	spiky = false;
	reduceSteps = 1;
	reduceFacets = 1;
    }

    /**
     * Instantiates a new cylinder.
     *
     * @param Ri
     *            bottom radius
     * @param Ro
     *            top radius
     * @param H
     *            height
     */
    public HEC_DataCylinder(final double Ri, final double Ro, final double H) {
	this();
	this.Ri = Ri;
	this.Ro = Ro;
	this.H = H;
	taper = 1.0;
    }

    /**
     * Set fixed radius.
     *
     * @param R
     *            radius
     * @return self
     */
    public HEC_DataCylinder setRadius(final double R) {
	Ri = R;
	Ro = R;
	return this;
    }

    /**
     * Set lower and upper radius.
     *
     * @param Ri
     *            lower radius
     * @param Ro
     *            upper radius
     * @return self
     */
    public HEC_DataCylinder setRadius(final double Ri, final double Ro) {
	this.Ri = Ri;
	this.Ro = Ro;
	return this;
    }

    /**
     * set height.
     *
     * @param H
     *            height
     * @return self
     */
    public HEC_DataCylinder setHeight(final double H) {
	this.H = H;
	return this;
    }

    /**
     * Set vertical divisions.
     *
     * @param data
     *            the data
     * @return self
     */
    public HEC_DataCylinder setDataFromDouble(final List<double[]> data) {
	this.data = new double[data.size()][];
	for (int i = 0; i < data.size(); i++) {
	    this.data[i] = data.get(i);
	}
	_steps = data.get(0).length;
	_facets = data.size();
	return this;
    }

    /**
     * Sets the data from double.
     *
     * @param data
     *            the data
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setDataFromDouble(final double[][] data) {
	this.data = new double[data.length][];
	for (int i = 0; i < data.length; i++) {
	    this.data[i] = data[i];
	}
	_steps = data[0].length;
	_facets = data.length;
	return this;
    }

    /**
     * Sets the data from float.
     *
     * @param data
     *            the data
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setDataFromFloat(final List<float[]> data) {
	this.data = new double[data.size()][];
	for (int i = 0; i < data.size(); i++) {
	    final float[] rowdata = data.get(i);
	    final int dl = rowdata.length;
	    this.data[i] = new double[dl];
	    for (int j = 0; j < dl; j++) {
		this.data[i][j] = rowdata[j];
	    }
	}
	_steps = data.get(0).length;
	_facets = data.size();
	return this;
    }

    /**
     * Sets the data from float.
     *
     * @param data
     *            the data
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setDataFromFloat(final float[][] data) {
	this.data = new double[data.length][];
	for (int i = 0; i < data.length; i++) {
	    final float[] rowdata = data[i];
	    final int dl = rowdata.length;
	    this.data[i] = new double[dl];
	    for (int j = 0; j < dl; j++) {
		this.data[i][j] = rowdata[j];
	    }
	}
	_steps = data[0].length;
	_facets = data.length;
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
    public HEC_DataCylinder setCap(final boolean topcap, final boolean bottomcap) {
	this.topcap = topcap;
	this.bottomcap = bottomcap;
	return this;
    }

    /**
     * Sets the taper.
     *
     * @param t
     *            the t
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setTaper(final double t) {
	taper = t;
	return this;
    }

    /**
     * Sets the spiky.
     *
     * @param b
     *            the b
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setSpiky(final boolean b) {
	spiky = b;
	return this;
    }

    /**
     * Sets the chamfer.
     *
     * @param d
     *            the d
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setChamfer(final double d) {
	chamfer = d;
	return this;
    }

    /**
     * Sets the proto.
     *
     * @param reduceSteps
     *            the reduce steps
     * @param reduceFacets
     *            the reduce facets
     * @return the hE c_ data cylinder
     */
    public HEC_DataCylinder setProto(final int reduceSteps,
	    final int reduceFacets) {
	this.reduceSteps = reduceSteps;
	this.reduceFacets = reduceFacets;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	if (WB_Epsilon.isZero(Ro)) {
	    Ro = 1.0;
	}
	if (WB_Epsilon.isZero(Ri)) {
	    Ri = 1.0;
	}
	final int steps = _steps / reduceSteps;
	final int facets = _facets / reduceFacets;
	final double[][] vertices = new double[(steps + 1) * facets][3];
	final double invs = 1.0 / steps;
	for (int i = 0; i < (steps + 1); i++) {
	    final double R = Ri + (Math.pow(i * invs, taper) * (Ro - Ri));
	    final double Hj = i * H * invs;
	    for (int j = 0; j < facets; j++) {
		vertices[j + (i * facets)][0] = R
			* Math.cos(((2 * Math.PI) / facets) * j);
		vertices[j + (i * facets)][2] = R
			* Math.sin(((2 * Math.PI) / facets) * j);
		vertices[j + (i * facets)][1] = Hj;
	    }
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
		faces[bc][j] = j;
	    }
	    if (topcap) {
		faces[tc][facets - 1 - j] = (steps * facets) + j;
	    }
	    for (int i = 0; i < steps; i++) {
		faces[j + (i * facets)] = new int[4];
		faces[j + (i * facets)][0] = j + (i * facets);
		faces[j + (i * facets)][1] = j + (i * facets) + facets;
		faces[j + (i * facets)][2] = ((j + 1) % facets) + facets
			+ (i * facets);
		faces[j + (i * facets)][3] = ((j + 1) % facets) + (i * facets);
	    }
	}
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setVertices(vertices).setFaces(faces);
	final HE_Mesh mesh = fl.createBase();
	final Iterator<HE_Face> fItr = mesh.fItr();
	if (spiky) {
	    for (int i = 0; i < steps; i++) {
		for (int j = 0; j < facets; j++) {
		    final HE_Face currentFace = fItr.next();
		    final int currentstep = i * reduceSteps;
		    final int currentfacet = j * reduceFacets;
		    final double datapoint = data[currentfacet][currentstep];
		    final WB_Point p = currentFace.getFaceCenter();
		    p.addMulSelf(datapoint, currentFace.getFaceNormal());
		    HEM_TriSplit.splitFaceTri(mesh, currentFace, p);
		}
	    }
	} else {
	    final double[] heights = new double[steps * facets];
	    int id = 0;
	    final HE_Selection sel = new HE_Selection(mesh);
	    for (int i = 0; i < steps; i++) {
		for (int j = 0; j < facets; j++) {
		    final HE_Face currentFace = fItr.next();
		    final int currentstep = i * reduceSteps;
		    final int currentfacet = j * reduceFacets;
		    heights[id++] = data[currentfacet][currentstep];
		    sel.add(currentFace);
		}
	    }
	    final HEM_Extrude ef = new HEM_Extrude().setChamfer(chamfer)
		    .setDistances(heights);
	    mesh.modifySelected(ef, sel);
	}
	return mesh;
    }
}
