/*
 * 
 */
package wblut.hemesh;

/**
 * Abstract base class for mesh reduction. Implementation should preserve mesh
 * validity.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HES_Simplifier extends HE_Machine {
    /**
     * Instantiates a new HES_Simplifier.
     */
    public HES_Simplifier() {
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