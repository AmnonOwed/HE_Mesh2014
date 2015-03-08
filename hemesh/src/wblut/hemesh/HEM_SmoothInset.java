/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

/**
 * 
 */
public class HEM_SmoothInset extends HEM_Modifier {
    
    /**
     * 
     */
    private int rep;
    
    /**
     * 
     */
    private double offset;
    
    /**
     * 
     */
    public HE_Selection walls;
    
    /**
     * 
     */
    public HE_Selection inset;

    /**
     * 
     */
    public HEM_SmoothInset() {
	rep = 1;
	offset = 0.1;
    }

    /**
     * 
     *
     * @param level 
     * @return 
     */
    public HEM_SmoothInset setLevel(final int level) {
	rep = level;
	return this;
    }

    /**
     * 
     *
     * @param offset 
     * @return 
     */
    public HEM_SmoothInset setOffset(final double offset) {
	this.offset = offset;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	final HEM_Extrude ext = new HEM_Extrude().setChamfer(offset)
		.setRelative(false);
	mesh.modify(ext);
	for (int i = 0; i < rep; i++) {
	    ext.extruded.collectEdgesByFace();
	    final Iterator<HE_Halfedge> eItr = ext.extruded.eItr();
	    while (eItr.hasNext()) {
		mesh.divideEdge(eItr.next(), 2);
	    }
	    ext.extruded.collectVertices();
	    mesh.modifySelected(new HEM_Smooth(), ext.extruded);
	}
	inset = ext.extruded;
	walls = ext.walls;
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
	final HEM_Extrude ext = new HEM_Extrude().setChamfer(offset)
		.setRelative(false);
	selection.parent.modifySelected(ext, selection);
	for (int i = 0; i < rep; i++) {
	    ext.extruded.collectEdgesByFace();
	    final Iterator<HE_Halfedge> eItr = ext.extruded.eItr();
	    while (eItr.hasNext()) {
		selection.parent.divideEdge(eItr.next(), 2);
	    }
	    ext.extruded.collectVertices();
	    selection.parent.modifySelected(new HEM_Smooth(), ext.extruded);
	}
	inset = ext.extruded;
	walls = ext.walls;
	return selection.parent;
    }
}