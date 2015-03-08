/*
 * 
 */
package wblut.hemesh;

/**
 * Expands or contracts all vertices along the vertex normals.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_RemoveSmallFragments extends HEM_Modifier {
    
    /**
     * 
     */
    private int n;

    /**
     * 
     */
    public HEM_RemoveSmallFragments() {
	super();
	n = 4;
    }

    /**
     * 
     *
     * @param n 
     * @return 
     */
    public HEM_RemoveSmallFragments setMinimumFaces(final int n) {
	this.n = n;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	final HEMC_Explode explode = new HEMC_Explode().setMesh(mesh);
	final HE_Mesh[] fragments = explode.create();
	mesh.clear();
	for (final HE_Mesh frag : fragments) {
	    if (frag.getNumberOfFaces() > n) {
		mesh.add(frag);
	    }
	}
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	return apply(selection.parent);
    }
}
