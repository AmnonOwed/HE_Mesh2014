/*
 * 
 */
package wblut.hemesh;

import java.util.Collection;
import javolution.util.FastTable;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Polygon;

/**
 * Creates a new mesh from a list of polygons. Duplicate vertices are fused.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_FromPolygons extends HEC_Creator {
    
    /**
     * 
     */
    private WB_Polygon[] polygons;
    
    /**
     * 
     */
    private boolean checkNormals;

    /**
     * Instantiates a new HEC_FromPolygons.
     *
     */
    public HEC_FromPolygons() {
	super();
	override = true;
    }

    /**
     * Instantiates a new HEC_FromPolygons.
     *
     * @param qs
     *            the qs
     */
    public HEC_FromPolygons(final WB_Polygon[] qs) {
	this();
	polygons = qs;
    }

    /**
     * Instantiates a new hE c_ from polygons.
     *
     * @param qs
     *            the qs
     */
    public HEC_FromPolygons(final Collection<? extends WB_Polygon> qs) {
	this();
	setPolygons(qs);
    }

    /**
     * Sets the source polygons.
     *
     * @param qs
     *            source polygons
     * @return self
     */
    public HEC_FromPolygons setPolygons(final WB_Polygon[] qs) {
	polygons = qs;
	return this;
    }

    /**
     * Sets the source polygons.
     *
     * @param qs
     *            source polygons
     * @return self
     */
    public HEC_FromPolygons setPolygons(
	    final Collection<? extends WB_Polygon> qs) {
	final int n = qs.size();
	polygons = new WB_Polygon[n];
	int i = 0;
	for (final WB_Polygon poly : qs) {
	    polygons[i] = poly;
	    i++;
	}
	return this;
    }

    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEC_FromPolygons setCheckNormals(final boolean b) {
	checkNormals = b;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	if (polygons != null) {
	    if (polygons.length > 0) {
		final int nq = polygons.length;
		final FastTable<WB_Coordinate> vertices = new FastTable<WB_Coordinate>();
		final int[][] faces = new int[nq][];
		int id = 0;
		for (int i = 0; i < nq; i++) {
		    faces[i] = new int[polygons[i].getNumberOfPoints()];
		    for (int j = 0; j < polygons[i].getNumberOfPoints(); j++) {
			vertices.add(polygons[i].getPoint(j));
			faces[i][j] = id;
			id++;
		    }
		}
		final HEC_FromFacelist ffl = new HEC_FromFacelist()
			.setVertices(vertices).setFaces(faces)
			.setDuplicate(true).setCheckNormals(checkNormals);
		;
		return ffl.createBase();
	    }
	}
	return new HE_Mesh();
    }
}
