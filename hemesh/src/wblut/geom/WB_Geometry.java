/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public interface WB_Geometry {
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @return 
     */
    public WB_GeometryType getType();

    /**
     * 
     *
     * @param T 
     * @return 
     */
    public WB_Geometry apply(WB_Transform T);
}
