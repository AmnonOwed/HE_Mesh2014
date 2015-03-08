/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class WB_VoronoiCell3D {
    
    /**
     * 
     */
    WB_Point generator;
    
    /**
     * 
     */
    int index;
    
    /**
     * 
     */
    WB_FaceListMesh cell;
    
    /**
     * 
     */
    boolean open;
    
    /**
     * 
     */
    boolean sliced;
    
    /**
     * 
     */
    boolean[] onBoundary;
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @param points 
     * @param generator 
     * @param index 
     */
    public WB_VoronoiCell3D(final WB_Coordinate[] points,
	    final WB_Point generator, final int index) {
	this.generator = generator;
	this.index = index;
	cell = geometryfactory.createConvexHull(points, false);
	if (cell != null) {
	    onBoundary = new boolean[cell.getNumberOfVertices()];
	}
    }

    /**
     * 
     *
     * @param points 
     * @param generator 
     * @param index 
     */
    public WB_VoronoiCell3D(final List<? extends WB_Coordinate> points,
	    final WB_Point generator, final int index) {
	this.generator = generator;
	this.index = index;
	cell = geometryfactory.createConvexHull(points, false);
	if (cell != null) {
	    onBoundary = new boolean[cell.getNumberOfVertices()];
	}
    }

    /**
     * 
     *
     * @param cell 
     * @param generator 
     * @param index 
     */
    public WB_VoronoiCell3D(final WB_FaceListMesh cell,
	    final WB_Point generator, final int index) {
	this.generator = generator;
	this.index = index;
	this.cell = cell;
	if (cell != null) {
	    onBoundary = new boolean[cell.getNumberOfVertices()];
	}
    }

    /**
     * 
     *
     * @param container 
     */
    public void constrain(final WB_AABB container) {
	final WB_AABB aabb = cell.getAABB();
	if (container.contains(aabb)) {
	    return;
	}
	if (aabb.intersects(container)) {
	    final double[] min = container._min;
	    final double[] max = container._max;
	    final WB_Point mmm = geometryfactory.createPoint(min[0], min[1],
		    min[2]);
	    final WB_Point Mmm = geometryfactory.createPoint(max[0], min[1],
		    min[2]);
	    final WB_Point mMm = geometryfactory.createPoint(min[0], max[1],
		    min[2]);
	    final WB_Point mmM = geometryfactory.createPoint(min[0], min[1],
		    max[2]);
	    final WB_Point MMM = geometryfactory.createPoint(max[0], max[1],
		    max[2]);
	    final WB_Point mMM = geometryfactory.createPoint(min[0], max[1],
		    max[2]);
	    final WB_Point MmM = geometryfactory.createPoint(max[0], min[1],
		    max[2]);
	    final WB_Point MMm = geometryfactory.createPoint(max[0], max[1],
		    min[2]);
	    final List<WB_Plane> planes = new ArrayList<WB_Plane>(6);
	    planes.add(geometryfactory.createPlane(mmm, mMm, Mmm));
	    planes.add(geometryfactory.createPlane(mmm, mmM, mMm));
	    planes.add(geometryfactory.createPlane(mmm, Mmm, mmM));
	    planes.add(geometryfactory.createPlane(MMM, mMM, MmM));
	    planes.add(geometryfactory.createPlane(MMM, MMm, mMM));
	    planes.add(geometryfactory.createPlane(MMM, MmM, MMm));
	    constrain(planes);
	} else {
	    cell = null;
	}
    }

    /**
     * 
     *
     * @param convexMesh 
     * @param d 
     */
    public void constrain(final WB_FaceListMesh convexMesh, final double d) {
	constrain(convexMesh.getPlanes(d));
    }

    /**
     * 
     *
     * @param convexMesh 
     */
    public void constrain(final WB_FaceListMesh convexMesh) {
	constrain(convexMesh.getPlanes(0));
    }

    /**
     * 
     *
     * @param planes 
     */
    public void constrain(final Collection<? extends WB_Plane> planes) {
	for (final WB_Plane P : planes) {
	    if (cell != null) {
		slice(P);
	    }
	}
	if (cell != null) {
	    onBoundary = new boolean[cell.getNumberOfVertices()];
	    double d;
	    WB_SequencePoint p;
	    pointloop: for (int i = 0; i < cell.getNumberOfVertices(); i++) {
		p = cell.getVertex(i);
		for (final WB_Plane WB_Point : planes) {
		    d = WB_GeometryOp.getDistanceToPlane3D(p, WB_Point);
		    if (WB_Epsilon.isZero(d)) {
			onBoundary[i] = true;
			continue pointloop;
		    }
		}
	    }
	    final int hfl = cell.getNumberOfFaces();
	    for (int i = hfl - 1; i > -1; i--) {
		final int[] face = cell.getFace(i);
		boolean boundary = true;
		for (int j = 0; j < face.length; j++) {
		    if (!onBoundary[face[j]]) {
			boundary = false;
			break;
		    }
		}
		if (boundary) {
		    open = true;
		}
	    }
	}
    }

    /**
     * 
     *
     * @param P 
     */
    private void slice(final WB_Plane P) {
	final WB_ClassificationGeometry[] classifyPoints = ptsPlane(P);
	final List<WB_Coordinate> newPoints = new ArrayList<WB_Coordinate>();
	for (int i = 0; i < classifyPoints.length; i++) {
	    if (classifyPoints[i] != WB_ClassificationGeometry.BACK) {
		newPoints.add(cell.getVertex(i));
	    }
	}
	final int[][] edges = cell.getEdgesAsInt();
	for (final int[] edge : edges) {
	    if (((classifyPoints[edge[0]] == WB_ClassificationGeometry.BACK) && (classifyPoints[edge[1]] == WB_ClassificationGeometry.FRONT))
		    || ((classifyPoints[edge[1]] == WB_ClassificationGeometry.BACK) && (classifyPoints[edge[0]] == WB_ClassificationGeometry.FRONT))) {
		final WB_SequencePoint a = cell.getVertex(edge[0]);
		final WB_SequencePoint b = cell.getVertex(edge[1]);
		newPoints.add((WB_Point) WB_GeometryOp.getIntersection3D(a, b,
			P).object);
		sliced = true;
	    }
	}
	cell = geometryfactory.createConvexHull(newPoints, false);
    }

    /**
     * 
     *
     * @param WB_Point 
     * @return 
     */
    private WB_ClassificationGeometry[] ptsPlane(final WB_Plane WB_Point) {
	final WB_ClassificationGeometry[] result = new WB_ClassificationGeometry[cell
		.getNumberOfVertices()];
	for (int i = 0; i < cell.getNumberOfVertices(); i++) {
	    result[i] = WB_Classify.classifyPointToPlane3D(cell.getVertex(i),
		    WB_Point);
	}
	return result;
    }

    /**
     * 
     *
     * @return 
     */
    public int getIndex() {
	return index;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point getGenerator() {
	return generator;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_FaceListMesh getMesh() {
	return cell;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean[] boundaryArray() {
	return onBoundary;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean isOpen() {
	return open;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean isSliced() {
	return sliced;
    }
}
