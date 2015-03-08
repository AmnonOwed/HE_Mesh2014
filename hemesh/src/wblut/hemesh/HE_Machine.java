/*
 * 
 */
package wblut.hemesh;

/**
 * 
 */
public abstract class HE_Machine {
    
    /**
     * 
     */
    public static final HET_ProgressTracker tracker = HET_ProgressTracker
	    .instance();

    /**
     * 
     *
     * @param mesh 
     * @return 
     */
    public abstract HE_Mesh apply(HE_Mesh mesh);

    /**
     * 
     *
     * @param selection 
     * @return 
     */
    public abstract HE_Mesh apply(HE_Selection selection);

    /**
     * 
     *
     * @return 
     */
    public String getStatus() {
	return tracker.getStatus();
    }
}
