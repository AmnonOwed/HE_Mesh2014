/*
 * 
 */
package wblut.hemesh;

// TODO: Auto-generated Javadoc
/**
 * Abstract base class for mesh modifications. Implementation should preserve
 * mesh validity.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HEM_Modifier extends HE_Machine {
    /**
     * Instantiates a new HEM_Modifier.
     */
    public HEM_Modifier() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Machine#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public abstract HE_Mesh apply(final HE_Mesh mesh);

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Machine#apply(wblut.hemesh.HE_Selection)
     */
    @Override
    public abstract HE_Mesh apply(final HE_Selection selection);
}
