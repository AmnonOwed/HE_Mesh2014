/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import wblut.geom.WB_ClassificationConvex;
import wblut.geom.WB_Point;

/**
 * Catmull-Clark subdivision of a mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HES_Smooth extends HES_Subdividor {
    /** Keep edges?. */
    private boolean keepEdges = true;
    /** Keep boundary?. */
    private boolean keepBoundary = false;
    /** Weight of original vertex. */
    private double origWeight;
    /** Weight of neighbor vertex. */
    private double neigWeight;

    /**
     * Instantiates a new hE s_ smooth.
     */
    public HES_Smooth() {
	super();
	origWeight = 1.0;
	neigWeight = 1.0;
    }

    /**
     * Keep edges of selection fixed when subdividing selection?.
     *
     * @param b
     *            true/false
     * @return self
     */
    public HES_Smooth setKeepEdges(final boolean b) {
	keepEdges = b;
	return this;
    }

    /**
     * Keep boundary edges fixed?.
     *
     * @param b
     *            true/false
     * @return self
     */
    public HES_Smooth setKeepBoundary(final boolean b) {
	keepBoundary = b;
	return this;
    }

    /**
     * Set vertex weights?.
     *
     * @param origWeight
     *            weight of original vertex
     * @param neigWeight
     *            weight of neighbors
     * @return self
     */
    public HES_Smooth setWeight(final double origWeight, final double neigWeight) {
	this.origWeight = origWeight;
	this.neigWeight = neigWeight;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	mesh.splitFacesQuad();
	final WB_Point[] newPositions = new WB_Point[mesh.getNumberOfVertices()];
	final HE_Selection all = mesh.selectAllFaces();
	final List<HE_Vertex> boundary = all.getOuterVertices();
	final List<HE_Vertex> inner = all.getInnerVertices();
	HE_Vertex v;
	HE_Vertex n;
	List<HE_Vertex> neighbors;
	int id = 0;
	Iterator<HE_Vertex> vItr = inner.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    final WB_Point p = new WB_Point(v);
	    neighbors = v.getNeighborVertices();
	    p.mulSelf(origWeight);
	    double c = origWeight;
	    for (int i = 0; i < neighbors.size(); i++) {
		n = neighbors.get(i);
		p.addSelf(neigWeight * n.xd(), neigWeight * n.yd(), neigWeight
			* n.zd());
		c += neigWeight;
	    }
	    newPositions[id] = p.scaleSelf(1.0 / c);
	    id++;
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (keepBoundary) {
		newPositions[id] = v.getPoint();
	    } else {
		final WB_Point p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		p.mulSelf(origWeight);
		double c = origWeight;
		int nc = 0;
		for (int i = 0; i < neighbors.size(); i++) {
		    n = neighbors.get(i);
		    if (boundary.contains(n)) {
			p.addSelf(neigWeight * n.xd(), neigWeight * n.yd(),
				neigWeight * n.zd());
			c += neigWeight;
			nc++;
		    }
		}
		newPositions[id] = (nc > 1) ? p.scaleSelf(1.0 / c) : v
			.getPoint();
	    }
	    id++;
	}
	vItr = inner.iterator();
	id = 0;
	while (vItr.hasNext()) {
	    vItr.next().set(newPositions[id]);
	    id++;
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    vItr.next().set(newPositions[id]);
	    id++;
	}
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.hemesh.subdividors.HEB_Subdividor#subdivideSelected(wblut.hemesh
     * .HE_Mesh, wblut.hemesh.HE_Selection)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	selection.parent.splitFacesQuad(selection);
	final List<WB_Point> newPositions = new ArrayList<WB_Point>();
	final List<HE_Vertex> boundary = selection.getBoundaryVertices();
	final List<HE_Vertex> inner = selection.getInnerVertices();
	final List<HE_Vertex> outer = selection.getOuterVertices();
	List<HE_Face> sharedFaces;
	HE_Vertex v;
	Iterator<HE_Vertex> vItr = outer.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (boundary.contains(v)) {
		vItr.remove();
	    }
	}
	HE_Vertex n;
	List<HE_Vertex> neighbors;
	int id = 0;
	vItr = inner.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    final WB_Point p = new WB_Point(v);
	    neighbors = v.getNeighborVertices();
	    p.mulSelf(origWeight);
	    double c = origWeight;
	    for (int i = 0; i < neighbors.size(); i++) {
		n = neighbors.get(i);
		p.addSelf(neigWeight * n.xd(), neigWeight * n.yd(), neigWeight
			* n.zd());
		c += neigWeight;
	    }
	    newPositions.add(p.scaleSelf(1.0 / c));
	    id++;
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (keepBoundary) {
		newPositions.add(v.getPoint());
	    } else {
		final WB_Point p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		p.mulSelf(origWeight);
		double c = origWeight;
		int nc = 0;
		for (int i = 0; i < neighbors.size(); i++) {
		    n = neighbors.get(i);
		    if (boundary.contains(n)) {
			p.addSelf(neigWeight * n.xd(), neigWeight * n.yd(),
				neigWeight * n.zd());
			c += neigWeight;
			nc++;
		    }
		}
		newPositions
			.add((nc > 1) ? p.scaleSelf(1.0 / c) : v.getPoint());
	    }
	    id++;
	}
	vItr = outer.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if ((keepEdges)
		    || (v.getVertexType() != WB_ClassificationConvex.FLAT)) {
		newPositions.add(v.getPoint());
	    } else {
		final WB_Point p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		p.mulSelf(origWeight);
		double c = origWeight;
		int nc = 0;
		for (int i = 0; i < neighbors.size(); i++) {
		    n = neighbors.get(i);
		    if (outer.contains(n)) {
			sharedFaces = selection.parent.getSharedFaces(v, n);
			boolean singleFaceGap = true;
			for (int j = 0; j < sharedFaces.size(); j++) {
			    if (selection.contains(sharedFaces.get(j))) {
				singleFaceGap = false;
				break;
			    }
			}
			if (!singleFaceGap) {
			    p.addSelf(neigWeight * n.xd(), neigWeight * n.yd(),
				    neigWeight * n.zd());
			    c += neigWeight;
			    nc++;
			}
		    }
		}
		newPositions
			.add((nc > 1) ? p.scaleSelf(1.0 / c) : v.getPoint());
	    }
	    id++;
	}
	vItr = inner.iterator();
	id = 0;
	while (vItr.hasNext()) {
	    vItr.next().set(newPositions.get(id));
	    id++;
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    vItr.next().set(newPositions.get(id));
	    id++;
	}
	vItr = outer.iterator();
	while (vItr.hasNext()) {
	    vItr.next().set(newPositions.get(id));
	    id++;
	}
	return selection.parent;
    }
}
