/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HEM_HybridSplit extends HEM_Modifier {
    
    /**
     * 
     */
    public HEM_HybridSplit() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	mesh.splitFacesHybrid();
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	selection.parent.splitFacesHybrid(selection);
	return selection.parent;
    }
}
