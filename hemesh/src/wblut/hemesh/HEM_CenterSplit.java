/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HEM_CenterSplit extends HEM_Modifier {
    
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
    public HEM_CenterSplit() {
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
    public HEM_CenterSplit setOffset(final double d) {
	this.d = d;
	return this;
    }

    /**
     * 
     *
     * @param c 
     * @return 
     */
    public HEM_CenterSplit setChamfer(final double c) {
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
	tracker.setDefaultStatus("Starting HEC_CenterSplit.");
	final HEM_Extrude ext = new HEM_Extrude().setChamfer(c).setDistance(d);
	mesh.modify(ext);
	selectionOut = ext.extruded;
	tracker.setDefaultStatus("Exiting HEC_CenterSplit.");
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	tracker.setDefaultStatus("Starting HEC_CenterSplit.");
	final HEM_Extrude ext = new HEM_Extrude().setChamfer(c).setDistance(d);
	selection.parent.modifySelected(ext, selection);
	selectionOut = ext.extruded;
	tracker.setDefaultStatus("Exiting HEC_CenterSplit.");
	return selection.parent;
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Selection getCenterFaces() {
	return this.selectionOut;
    }
}
