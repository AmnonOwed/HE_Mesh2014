/*
 * 
 */
package wblut.hemesh;

/**
 * Abstract base class for mesh subdivision. Implementation should preserve mesh
 * validity.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
abstract public class HES_Subdividor extends HE_Machine {
    /**
     * Instantiates a new HES_Subdividor.
     */
    public HES_Subdividor() {
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
