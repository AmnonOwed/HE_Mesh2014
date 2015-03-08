/*
 * 
 */
package wblut.hemesh;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import javolution.util.FastMap;
import wblut.external.QuickHull3D.WB_QuickHull3D;
import wblut.geom.WB_Point;

/**
 * Creates the convex hull of a collection of points.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_ConvexHull extends HEC_Creator {
    /** Points. */
    private WB_Point[] points;
    /** Number of points. */
    private int numberOfPoints;
    /** The vertex to point index. */
    public Map<Long, Integer> vertexToPointIndex;

    /**
     * Instantiates a new HEC_ConvexHull.
     *
     */
    public HEC_ConvexHull() {
	super();
	override = true;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            array of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPoints(final WB_Point[] points) {
	this.points = points;
	return this;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            array of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPoints(final HE_Vertex[] points) {
	this.points = new WB_Point[points.length];
	for (int i = 0; i < points.length; i++) {
	    this.points[i] = new WB_Point(points[i]);
	}
	return this;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            any Collection of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPoints(final Collection<? extends WB_Point> points) {
	this.points = new WB_Point[points.size()];
	final Iterator<? extends WB_Point> itr = points.iterator();
	int i = 0;
	while (itr.hasNext()) {
	    this.points[i] = itr.next();
	    i++;
	}
	return this;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            any Collection of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPointsFromVertices(
	    final Collection<HE_Vertex> points) {
	this.points = new WB_Point[points.size()];
	final Iterator<HE_Vertex> itr = points.iterator();
	int i = 0;
	while (itr.hasNext()) {
	    this.points[i] = itr.next().getPoint();
	    i++;
	}
	return this;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            2D array of double of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPoints(final double[][] points) {
	final int n = points.length;
	this.points = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	}
	return this;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            2D array of float of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPoints(final float[][] points) {
	final int n = points.length;
	this.points = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	}
	return this;
    }

    /**
     * Set points that define vertices.
     *
     * @param points
     *            2D array of float of vertex positions
     * @return self
     */
    public HEC_ConvexHull setPoints(final int[][] points) {
	final int n = points.length;
	this.points = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	}
	return this;
    }

    /**
     * Set number of points.
     *
     * @param N
     *            number of points
     * @return self
     */
    public HEC_ConvexHull setN(final int N) {
	numberOfPoints = N;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    public HE_Mesh createBase() {
	return createWithQuickHull();
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Mesh createWithQuickHull() {
	if (points == null) {
	    return new HE_Mesh();
	}
	if (numberOfPoints == 0) {
	    numberOfPoints = points.length;
	}
	final WB_QuickHull3D hull = new WB_QuickHull3D(points);
	final int[][] faceIndices = hull.getFaces();
	final int[] originalindices = hull.getVertexPointIndices();
	final HEC_FromFacelist ffl = new HEC_FromFacelist()
	.setVertices(hull.getVertices()).setFaces(faceIndices)
	.setDuplicate(false).setCheckNormals(false);
	final HE_Mesh result = ffl.createBase();
	vertexToPointIndex = new FastMap<Long, Integer>();
	final Iterator<HE_Vertex> vItr = result.vItr();
	int i = 0;
	while (vItr.hasNext()) {
	    vertexToPointIndex.put(vItr.next().key(), originalindices[i++]);
	}
	result.cleanUnusedElementsByFace();
	return result;
    }
}
