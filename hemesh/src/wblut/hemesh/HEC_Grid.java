/*
 * 
 */
package wblut.hemesh;

import wblut.math.WB_Function2D;

/**
 * 
 */
public class HEC_Grid extends HEC_Creator {
    
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
    private double uSize;
    
    /**
     * 
     */
    private double vSize;
    
    /**
     * 
     */
    private double[][] values;
    
    /**
     * 
     */
    private double WScale;

    /**
     * 
     */
    public HEC_Grid() {
	super();
	uSize = 0;
	vSize = 0;
	U = 1;
	V = 1;
	WScale = 1;
    }

    /**
     * 
     *
     * @param U 
     * @param V 
     * @param uSize 
     * @param vSize 
     */
    public HEC_Grid(final int U, final int V, final double uSize,
	    final double vSize) {
	this();
	this.uSize = uSize;
	this.vSize = vSize;
	this.U = U;
	this.V = V;
    }

    /**
     * 
     *
     * @param U 
     * @return 
     */
    public HEC_Grid setU(final int U) {
	this.U = Math.max(1, U);
	return this;
    }

    /**
     * 
     *
     * @param V 
     * @return 
     */
    public HEC_Grid setV(final int V) {
	this.V = Math.max(1, V);
	return this;
    }

    /**
     * 
     *
     * @param uSize 
     * @return 
     */
    public HEC_Grid setUSize(final double uSize) {
	this.uSize = uSize;
	return this;
    }

    /**
     * 
     *
     * @param vSize 
     * @return 
     */
    public HEC_Grid setVSize(final double vSize) {
	this.vSize = vSize;
	return this;
    }

    /**
     * 
     *
     * @param values 
     * @return 
     */
    public HEC_Grid setWValues(final double[][] values) {
	this.values = values;
	return this;
    }

    /**
     * 
     *
     * @param height 
     * @param ui 
     * @param vi 
     * @param du 
     * @param dv 
     * @return 
     */
    public HEC_Grid setWValues(final WB_Function2D<Double> height,
	    final double ui, final double vi, final double du, final double dv) {
	values = new double[U + 1][V + 1];
	for (int i = 0; i < (U + 1); i++) {
	    for (int j = 0; j < (V + 1); j++) {
		values[i][j] = height.f(ui + (i * du), vi + (j * dv));
	    }
	}
	return this;
    }

    /**
     * 
     *
     * @param values 
     * @return 
     */
    public HEC_Grid setWValues(final float[][] values) {
	this.values = new double[U + 1][V + 1];
	for (int i = 0; i < (U + 1); i++) {
	    for (int j = 0; j < (V + 1); j++) {
		this.values[i][j] = values[i][j];
	    }
	}
	return this;
    }

    /**
     * 
     *
     * @param values 
     * @return 
     */
    public HEC_Grid setWValues(final float[] values) {
	int id = 0;
	this.values = new double[U + 1][V + 1];
	for (int j = 0; j < (V + 1); j++) {
	    for (int i = 0; i < (U + 1); i++) {
		this.values[i][j] = values[id];
		id++;
	    }
	}
	return this;
    }

    /**
     * 
     *
     * @param values 
     * @return 
     */
    public HEC_Grid setWValues(final double[] values) {
	int id = 0;
	this.values = new double[U + 1][V + 1];
	for (int j = 0; j < (V + 1); j++) {
	    for (int i = 0; i < (U + 1); i++) {
		this.values[i][j] = values[id];
		id++;
	    }
	}
	return this;
    }

    /**
     * 
     *
     * @param value 
     * @return 
     */
    public HEC_Grid setWScale(final double value) {
	WScale = value;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.creators.HEB_Creator#createBase()
     */
    @Override
    protected HE_Mesh createBase() {
	if ((uSize == 0) || (vSize == 0)) {
	    return new HE_Mesh();
	}
	final double dU = uSize / U;
	final double dV = vSize / V;
	final double[][] points = new double[(U + 1) * (V + 1)][3];
	final int[][] faces = new int[U * V][4];
	int index = 0;
	if (values == null) {
	    for (int j = 0; j < (V + 1); j++) {
		for (int i = 0; i < (U + 1); i++) {
		    points[index][0] = i * dU;
		    points[index][1] = j * dV;
		    points[index][2] = 0;
		    index++;
		}
	    }
	} else {
	    for (int j = 0; j < (V + 1); j++) {
		for (int i = 0; i < (U + 1); i++) {
		    points[index][0] = i * dU;
		    points[index][1] = j * dV;
		    points[index][2] = WScale * values[i][j];
		    index++;
		}
	    }
	}
	index = 0;
	for (int j = 0; j < V; j++) {
	    for (int i = 0; i < U; i++) {
		faces[index][0] = i + ((U + 1) * j);
		faces[index][1] = i + 1 + ((U + 1) * j);
		faces[index][2] = i + 1 + ((U + 1) * (j + 1));
		faces[index][3] = i + ((U + 1) * (j + 1));
		index++;
	    }
	}
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setVertices(points).setFaces(faces).setDuplicate(false)
		.setCheckNormals(false);
	return fl.createBase();
    }
}
