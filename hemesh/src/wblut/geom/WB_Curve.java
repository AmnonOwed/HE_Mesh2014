/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public interface WB_Curve {
    
    /**
     * 
     *
     * @param u 
     * @return 
     */
    public WB_Point curvePoint(double u);

    /**
     * 
     *
     * @return 
     */
    public double loweru();

    /**
     * 
     *
     * @return 
     */
    public double upperu();
}
