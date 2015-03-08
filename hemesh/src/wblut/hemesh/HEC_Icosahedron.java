/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HEC_Icosahedron extends HEC_Creator {
    
    /**
     * 
     */
    private double R;

    /**
     * 
     */
    public HEC_Icosahedron() {
	super();
	R = 0f;
    }

    /**
     * 
     *
     * @param E 
     * @return 
     */
    public HEC_Icosahedron setEdge(final double E) {
	R = 0.9510565 * E;
	return this;
    }

    /**
     * 
     *
     * @param R 
     * @return 
     */
    public HEC_Icosahedron setInnerRadius(final double R) {
	this.R = R * 1.2584086;
	return this;
    }

    /**
     * 
     *
     * @param R 
     * @return 
     */
    public HEC_Icosahedron setOuterRadius(final double R) {
	this.R = R;
	return this;
    }

    /**
     * 
     *
     * @param R 
     * @return 
     */
    public HEC_Icosahedron setMidRadius(final double R) {
	this.R = R * 1.175570;
	return this;
    }

    /*
     * Code adapted from http://www.cs.umbc.edu/~squire/ (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	final double[][] vertices = new double[12][3]; /*
						        * 12 vertices with x, y,
						        * z coordinates
						        */
	final double Pi = 3.141592653589793238462643383279502884197;
	final double phiaa = 26.56505; /* phi needed for generation */
	final double phia = (Pi * phiaa) / 180.0; /* 2 sets of four points */
	final double theb = (Pi * 36.0) / 180.0; /* offset second set 36 degrees */
	final double the72 = (Pi * 72.0) / 180; /* step 72 degrees */
	vertices[0][0] = 0;
	vertices[0][1] = 0;
	vertices[0][2] = R;
	vertices[11][0] = 0;
	vertices[11][1] = 0;
	vertices[11][2] = -R;
	double the = 0.0;
	for (int i = 1; i < 6; i++) {
	    vertices[i][0] = R * Math.cos(the + (Math.PI / 5.0))
		    * Math.cos(phia);
	    vertices[i][1] = R * Math.sin(the + (Math.PI / 5.0))
		    * Math.cos(phia);
	    vertices[i][2] = R * Math.sin(phia);
	    the = the + the72;
	}
	the = theb;
	for (int i = 6; i < 11; i++) {
	    vertices[i][0] = R * Math.cos(the + (Math.PI / 5.0))
		    * Math.cos(-phia);
	    vertices[i][1] = R * Math.sin(the + (Math.PI / 5.0))
		    * Math.cos(-phia);
	    vertices[i][2] = R * Math.sin(-phia);
	    the = the + the72;
	}
	final int[][] faces = { { 0, 1, 2 }, { 0, 2, 3 }, { 0, 3, 4 },
		{ 0, 4, 5 }, { 0, 5, 1 }, { 11, 7, 6 }, { 11, 8, 7 },
		{ 11, 9, 8 }, { 11, 10, 9 }, { 11, 6, 10 }, { 1, 6, 2 },
		{ 2, 7, 3 }, { 4, 3, 8 }, { 5, 4, 9 }, { 1, 5, 10 },
		{ 6, 7, 2 }, { 7, 8, 3 }, { 8, 9, 4 }, { 9, 10, 5 },
		{ 10, 6, 1 } };
	final HEC_FromFacelist fl = new HEC_FromFacelist();
	fl.setVertices(vertices).setFaces(faces);
	return fl.createBase();
    }
}
