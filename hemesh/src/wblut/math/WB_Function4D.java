/*
 * 
 */
package wblut.math;

/**
 * 
 *
 * @param <T> 
 */
public interface WB_Function4D<T> {
    
    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @param w 
     * @return 
     */
    public T f(double x, double y, double z, double w);
}
