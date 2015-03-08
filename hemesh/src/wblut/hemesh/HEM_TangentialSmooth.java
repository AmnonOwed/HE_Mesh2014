/*
 * 
 */
package wblut.hemesh;

import static wblut.geom.WB_GeometryOp.projectOnPlane;
import java.util.Iterator;
import java.util.List;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;

/**
 * 
 */
public class HEM_TangentialSmooth extends HEM_Modifier {
    
    /**
     * 
     */
    private boolean autoRescale;
    
    /**
     * 
     */
    private boolean keepBoundary;
    
    /**
     * 
     */
    private int iter;

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
     */
    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEM_TangentialSmooth setAutoRescale(final boolean b) {
	autoRescale = b;
	return this;
    }

    /**
     * 
     *
     * @param r 
     * @return 
     */
    public HEM_TangentialSmooth setIterations(final int r) {
	iter = r;
	return this;
    }

    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEM_TangentialSmooth setKeepBoundary(final boolean b) {
	keepBoundary = b;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	tracker.setDefaultStatus("Starting HEM_Smooth.");
	WB_AABB box = new WB_AABB();
	if (autoRescale) {
	    box = mesh.getAABB();
	}
	final WB_Point[] newPositions = new WB_Point[mesh.getNumberOfVertices()];
	if (iter < 1) {
	    iter = 1;
	}
	tracker.setDefaultStatus("Smoothing vertices.",
		iter * mesh.getNumberOfVertices());
	for (int r = 0; r < iter; r++) {
	    Iterator<HE_Vertex> vItr = mesh.vItr();
	    HE_Vertex v;
	    List<HE_Vertex> neighbors;
	    int id = 0;
	    WB_Point p;
	    WB_Plane tangent;
	    while (vItr.hasNext()) {
		v = vItr.next();
		tangent = new WB_Plane(v, v.getVertexNormal());
		if (v.isBoundary() && keepBoundary) {
		    newPositions[id] = v.getPoint();
		} else {
		    p = new WB_Point(v);
		    neighbors = v.getNeighborVertices();
		    p.mulSelf(neighbors.size());
		    for (int i = 0; i < neighbors.size(); i++) {
			p.addSelf(neighbors.get(i));
		    }
		    newPositions[id] = projectOnPlane(
			    p.scaleSelf(0.5 / neighbors.size()), tangent);
		}
		id++;
	    }
	    vItr = mesh.vItr();
	    id = 0;
	    while (vItr.hasNext()) {
		vItr.next().set(newPositions[id]);
		id++;
		tracker.incrementCounter();
	    }
	}
	mesh.resetCenter();
	if (autoRescale) {
	    mesh.fitInAABB(box);
	}
	tracker.setDefaultStatus("Exiting HEM_Smooth.");
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	tracker.setDefaultStatus("Starting HEM_Smooth.");
	selection.collectVertices();
	WB_AABB box = new WB_AABB();
	if (autoRescale) {
	    box = selection.parent.getAABB();
	}
	final WB_Point[] newPositions = new WB_Point[selection
	                                             .getNumberOfVertices()];
	if (iter < 1) {
	    iter = 1;
	}
	tracker.setDefaultStatus("Smoothing vertices.",
		iter * selection.getNumberOfVertices());
	WB_Plane tangent;
	for (int r = 0; r < iter; r++) {
	    Iterator<HE_Vertex> vItr = selection.vItr();
	    HE_Vertex v;
	    HE_Vertex n;
	    List<HE_Vertex> neighbors;
	    int id = 0;
	    while (vItr.hasNext()) {
		v = vItr.next();
		tangent = new WB_Plane(v, v.getVertexNormal());
		final WB_Point p = new WB_Point(v);
		if (v.isBoundary() && keepBoundary) {
		    newPositions[id] = v.getPoint();
		} else {
		    neighbors = v.getNeighborVertices();
		    final Iterator<HE_Vertex> nItr = neighbors.iterator();
		    while (nItr.hasNext()) {
			n = nItr.next();
			if (!selection.contains(n)) {
			    nItr.remove();
			}
		    }
		    p.mulSelf(neighbors.size());
		    for (int i = 0; i < neighbors.size(); i++) {
			p.addSelf(neighbors.get(i));
		    }
		    newPositions[id] = projectOnPlane(
			    p.scaleSelf(0.5 / neighbors.size()), tangent);
		}
		id++;
	    }
	    vItr = selection.vItr();
	    id = 0;
	    while (vItr.hasNext()) {
		vItr.next().set(newPositions[id]);
		id++;
		tracker.incrementCounter();
	    }
	}
	selection.parent.resetCenter();
	if (autoRescale) {
	    selection.parent.fitInAABB(box);
	}
	tracker.setDefaultStatus("Exiting HEM_Smooth.");
	return selection.parent;
    }
}
