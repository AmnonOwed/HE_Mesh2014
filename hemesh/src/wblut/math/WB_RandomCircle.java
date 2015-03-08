/*
 * 
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * 
 * Random generator for vectors uniformly distributed on the unit circle.
 * 
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_RandomCircle {
    /** The random gen. */
    private final WB_MTRandom randomGen;

    /**
     * Instantiates a new w b_ random disc.
     */
    public WB_RandomCircle() {
	randomGen = new WB_MTRandom();
    }

    /**
     * Set random seed.
     * 
     * @param seed
     *            seed
     * @return self
     */
    public WB_RandomCircle setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     * Next point.
     * 
     * @return next random WB_Point on unit circle
     */
    public WB_Point nextPoint() {
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Point(Math.cos(t), Math.sin(t), 0);
    }

    /**
     * Next vector.
     * 
     * @return next random WB_Vector on unit circle
     */
    public WB_Vector nextVector() {
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Vector(Math.cos(t), Math.sin(t), 0);
    }
}