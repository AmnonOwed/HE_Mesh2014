/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import wblut.geom.WB_Plane;

/**
 * Multiple planar cuts of a mesh. No faces are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_MultiSliceSurface extends HEM_Modifier {
    /** Cut planes. */
    private ArrayList<WB_Plane> planes;
    /** Store cut faces. */
    public HE_Selection cut;
    /** The new edges. */
    public HE_Selection newEdges;
    /** The offset. */
    private double offset;

    /**
     * Set offset.
     *
     * @param d
     *            offset
     * @return self
     */
    public HEM_MultiSliceSurface setOffset(final double d) {
	offset = d;
	return this;
    }

    /**
     * Instantiates a new HEM_MultiSlice surface.
     */
    public HEM_MultiSliceSurface() {
	super();
    }

    /**
     * Set cut planes from an arrayList of WB_Plane.
     *
     * @param planes
     *            arrayList of WB_Plane
     * @return self
     */
    public HEM_MultiSliceSurface setPlanes(final Collection<WB_Plane> planes) {
	this.planes = new ArrayList<WB_Plane>();
	this.planes.addAll(planes);
	return this;
    }

    /**
     * Set cut planes from an array of WB_Plane.
     *
     * @param planes
     *            array of WB_Plane
     * @return self
     */
    public HEM_MultiSliceSurface setPlanes(final WB_Plane[] planes) {
	this.planes = new ArrayList<WB_Plane>();
	for (final WB_Plane plane : planes) {
	    this.planes.add(plane);
	}
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	cut = new HE_Selection(mesh);
	newEdges = new HE_Selection(mesh);
	mesh.resetFaceInternalLabels();
	mesh.resetEdgeInternalLabels();
	if (planes == null) {
	    return mesh;
	}
	final HEM_SliceSurface slice = new HEM_SliceSurface();
	for (int k = 0; k < planes.size(); k++) {
	    final WB_Plane P = planes.get(k);
	    slice.setPlane(P).setOffset(offset);
	    slice.apply(mesh);
	    cut.add(slice.cut);
	    newEdges.add(slice.cutEdges);
	}
	cut.cleanSelection();
	newEdges.cleanSelection();
	final Iterator<HE_Halfedge> eItr = newEdges.eItr();
	while (eItr.hasNext()) {
	    eItr.next().setInternalLabel(1);
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
	selection.parent.resetFaceInternalLabels();
	selection.parent.resetEdgeInternalLabels();
	cut = new HE_Selection(selection.parent);
	newEdges = new HE_Selection(selection.parent);
	if (planes == null) {
	    return selection.parent;
	}
	final HEM_SliceSurface slice = new HEM_SliceSurface();
	for (int k = 0; k < planes.size(); k++) {
	    final WB_Plane P = planes.get(k);
	    slice.setPlane(P).setOffset(offset);
	    slice.apply(selection);
	    cut.add(slice.cut);
	    newEdges.add(slice.cutEdges);
	}
	cut.cleanSelection();
	newEdges.cleanSelection();
	final Iterator<HE_Halfedge> eItr = newEdges.eItr();
	while (eItr.hasNext()) {
	    eItr.next().setInternalLabel(1);
	}
	return selection.parent;
    }
}
