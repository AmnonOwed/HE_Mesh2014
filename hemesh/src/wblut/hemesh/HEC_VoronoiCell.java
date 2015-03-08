/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Creates the Voronoi cell of one point in a collection of points, constrained
 * by a mesh.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_VoronoiCell extends HEC_Creator {
    /** Points. */
    private WB_Point[] points;
    /** Number of points. */
    private int numberOfPoints;
    /** Use specific subselection of points. */
    private int[] pointsToUse;
    /** Cell index. */
    private int cellIndex;
    /** Container. */
    private HE_Mesh container;
    /** Treat container as surface?. */
    private boolean surface;
    /** The simple cap. */
    private boolean simpleCap;
    /** Offset. */
    private double offset;
    /** Faces fully interior to cell. */
    public HE_Selection inner;
    /** Faces part of container. */
    public HE_Selection outer;
    /** The limit points. */
    private boolean limitPoints;

    /**
     * Instantiates a new HEC_VoronoiCell.
     * 
     */
    public HEC_VoronoiCell() {
	super();
	override = true;
    }

    /**
     * Set points that define cell centers.
     * 
     * @param points
     *            array of vertex positions
     * @return self
     */
    public HEC_VoronoiCell setPoints(final WB_Point[] points) {
	this.points = points;
	return this;
    }

    /**
     * Set points that define cell centers.
     * 
     * @param points
     *            2D array of double of vertex positions
     * @return self
     */
    public HEC_VoronoiCell setPoints(final double[][] points) {
	final int n = points.length;
	this.points = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	}
	return this;
    }

    /**
     * Set points that define cell centers.
     * 
     * @param points
     *            2D array of float of vertex positions
     * @return self
     */
    public HEC_VoronoiCell setPoints(final float[][] points) {
	final int n = points.length;
	this.points = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	}
	return this;
    }

    /**
     * Sets the points to use.
     * 
     * @param pointsToUse
     *            the points to use
     * @return the hE c_ voronoi cell
     */
    public HEC_VoronoiCell setPointsToUse(final int[] pointsToUse) {
	this.pointsToUse = pointsToUse;
	return this;
    }

    /**
     * Sets the points to use.
     * 
     * @param pointsToUse
     *            the points to use
     * @return the hE c_ voronoi cell
     */
    public HEC_VoronoiCell setPointsToUse(final ArrayList<Integer> pointsToUse) {
	final int n = pointsToUse.size();
	this.pointsToUse = new int[n];
	for (int i = 0; i < n; i++) {
	    this.pointsToUse[i] = pointsToUse.get(i);
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
    public HEC_VoronoiCell setN(final int N) {
	numberOfPoints = N;
	return this;
    }

    /**
     * Set index of cell to create.
     * 
     * @param i
     *            index
     * @return self
     */
    public HEC_VoronoiCell setCellIndex(final int i) {
	cellIndex = i;
	return this;
    }

    /**
     * Set voronoi cell offset.
     * 
     * @param o
     *            offset
     * @return self
     */
    public HEC_VoronoiCell setOffset(final double o) {
	offset = o;
	return this;
    }

    /**
     * Set enclosing mesh limiting cells.
     * 
     * @param container
     *            enclosing mesh
     * @return self
     */
    public HEC_VoronoiCell setContainer(final HE_Mesh container) {
	this.container = container;
	return this;
    }

    /**
     * Limit the points considered to those indices specified in the
     * pointsToUseArray.
     * 
     * @param b
     *            true, false
     * @return self
     */
    public HEC_VoronoiCell setLimitPoints(final boolean b) {
	limitPoints = b;
	return this;
    }

    /**
     * Set optional surface mesh mode.
     * 
     * @param b
     *            true, false
     * @return self
     */
    public HEC_VoronoiCell setSurface(final boolean b) {
	surface = b;
	return this;
    }

    /**
     * Sets the simple cap.
     * 
     * @param b
     *            the b
     * @return the hE c_ voronoi cell
     */
    public HEC_VoronoiCell setSimpleCap(final Boolean b) {
	simpleCap = b;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    public HE_Mesh createBase() {
	if (container == null) {
	    return new HE_Mesh();
	}
	if (points == null) {
	    return container;
	}
	if (numberOfPoints == 0) {
	    numberOfPoints = points.length;
	}
	if ((cellIndex < 0) || (cellIndex >= numberOfPoints)) {
	    return container;
	}
	final HE_Mesh result = container.get();
	final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
	int id = 0;
	final WB_Point O = new WB_Point();
	WB_Plane P;
	final int[] labels;
	if (limitPoints) {
	    labels = new int[pointsToUse.length];
	    for (final int element : pointsToUse) {
		if (cellIndex != element) {
		    final WB_Vector N = new WB_Vector(points[cellIndex]);
		    N.subSelf(points[element]);
		    N.normalizeSelf();
		    O.set(points[cellIndex]); // plane origin=point halfway
		    // between point i and point j
		    O.addSelf(points[element]);
		    O.mulSelf(0.5);
		    if (offset != 0) {
			O.addSelf(N.mul(offset));
		    }
		    P = new WB_Plane(O, N);
		    cutPlanes.add(P);
		    labels[id] = element;
		    id++;
		}
	    }
	} else {
	    labels = new int[numberOfPoints - 1];
	    for (int j = 0; j < numberOfPoints; j++) {
		if (cellIndex != j) {
		    final WB_Vector N = new WB_Vector(points[cellIndex]);
		    N.subSelf(points[j]);
		    N.normalizeSelf();
		    O.set(points[cellIndex]); // plane origin=point halfway
		    // between point i and point j
		    O.addSelf(points[j]);
		    O.mulSelf(0.5);
		    if (offset != 0) {
			O.addSelf(N.mul(offset));
		    }
		    P = new WB_Plane(O, N);
		    cutPlanes.add(P);
		    labels[id] = j;
		    id++;
		}
	    }
	}
	final HEM_MultiSlice msm = new HEM_MultiSlice();
	msm.setPlanes(cutPlanes).setCenter(new WB_Point(points[cellIndex]))
		.setCap(!surface).setKeepCenter(true).setLabels(labels)
		.setSimpleCap(simpleCap);
	result.modify(msm);
	inner = msm.newFaces;
	outer = msm.origFaces;
	return result;
    }
}
