/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public class HEM_MidEdgeSplit extends HEM_Modifier {
    
    /**
     * 
     */
    public HEM_MidEdgeSplit() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	mesh.splitFacesMidEdge();
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	selection.parent.splitFacesMidEdge(selection);
	return selection.parent;
    }
}
