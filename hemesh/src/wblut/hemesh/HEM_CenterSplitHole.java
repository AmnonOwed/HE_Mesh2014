/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HEM_CenterSplitHole extends HEM_Modifier {
    
    /**
     * 
     */
    private double d;
    
    /**
     * 
     */
    private double c;
    
    /**
     * 
     */
    private HE_Selection selectionOut;

    /**
     * 
     */
    public HEM_CenterSplitHole() {
	super();
	d = 0;
	c = 0.5;
    }

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public HEM_CenterSplitHole setOffset(final double d) {
	this.d = d;
	return this;
    }

    /**
     * 
     *
     * @param c 
     * @return 
     */
    public HEM_CenterSplitHole setChamfer(final double c) {
	this.c = c;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	final HEM_Extrude ext = new HEM_Extrude().setChamfer(c).setDistance(d);
	mesh.modify(ext);
	mesh.delete(ext.extruded);
	selectionOut = ext.walls;
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	final HEM_Extrude ext = new HEM_Extrude().setChamfer(c).setDistance(d);
	selection.parent.modifySelected(ext, selection);
	selection.parent.delete(ext.extruded);
	selectionOut = ext.walls;
	return selection.parent;
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Selection getWallFaces() {
	return this.selectionOut;
    }
}
