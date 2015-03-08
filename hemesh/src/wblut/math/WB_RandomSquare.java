/*
 * 
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * 
 * Random generator for vectors uniformly distributed in the unit square.
 * 
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_RandomSquare {
    
    /**
     * 
     */
    private final WB_MTRandom randomGen;

    /**
     * 
     */
    public WB_RandomSquare() {
	randomGen = new WB_MTRandom();
    }

    /**
     * 
     *
     * @param seed 
     * @return 
     */
    public WB_RandomSquare setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point nextPoint() {
	return new WB_Point(randomGen.nextDouble(), randomGen.nextDouble(), 0);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Vector nextVector() {
	return new WB_Vector(randomGen.nextDouble(), randomGen.nextDouble(), 0);
    }
}
