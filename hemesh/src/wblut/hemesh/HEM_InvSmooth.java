/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import java.util.List;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Point;

/**
 * Simple Laplacian smooth modifier. Does not add new vertices.
 *
 * @author Frederik Vanhoutte, W:Blut
 *
 */
public class HEM_InvSmooth extends HEM_Modifier {
    /** The auto rescale. */
    private boolean autoRescale;
    /** The iter. */
    private int iter;

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
     */
    /**
     * Sets the auto rescale.
     *
     * @param b
     *            the b
     * @return the hE m_ smooth
     */
    public HEM_InvSmooth setAutoRescale(final boolean b) {
	autoRescale = b;
	return this;
    }

    /**
     * Sets the iterations.
     *
     * @param r
     *            the r
     * @return the hE m_ smooth
     */
    public HEM_InvSmooth setIterations(final int r) {
	iter = r;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	WB_AABB box = new WB_AABB();
	if (autoRescale) {
	    box = mesh.getAABB();
	}
	final WB_Point[] newPositions = new WB_Point[mesh.getNumberOfVertices()];
	if (iter < 1) {
	    iter = 1;
	}
	for (int r = 0; r < iter; r++) {
	    Iterator<HE_Vertex> vItr = mesh.vItr();
	    HE_Vertex v;
	    List<HE_Vertex> neighbors;
	    int id = 0;
	    WB_Point p;
	    while (vItr.hasNext()) {
		v = vItr.next();
		p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		p.mulSelf(neighbors.size());
		for (int i = 0; i < neighbors.size(); i++) {
		    p.addSelf(neighbors.get(i));
		}
		newPositions[id] = p.scaleSelf(0.5 / neighbors.size());
		id++;
	    }
	    vItr = mesh.vItr();
	    id = 0;
	    while (vItr.hasNext()) {
		v = vItr.next();
		v.getPoint().addSelf(v.getPoint().sub(newPositions[id++]));
	    }
	}
	mesh.resetCenter();
	if (autoRescale) {
	    mesh.fitInAABB(box);
	}
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
	for (int r = 0; r < iter; r++) {
	    Iterator<HE_Vertex> vItr = selection.vItr();
	    HE_Vertex v;
	    HE_Vertex n;
	    List<HE_Vertex> neighbors;
	    int id = 0;
	    while (vItr.hasNext()) {
		v = vItr.next();
		final WB_Point p = new WB_Point(v);
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
		newPositions[id] = p.scaleSelf(0.5 / neighbors.size());
		id++;
	    }
	    vItr = selection.vItr();
	    id = 0;
	    while (vItr.hasNext()) {
		v = vItr.next();
		v.getPoint().addSelf(v.getPoint().sub(newPositions[id++]));
	    }
	}
	selection.parent.resetCenter();
	if (autoRescale) {
	    selection.parent.fitInAABB(box);
	}
	return selection.parent;
    }
}
