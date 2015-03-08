/*
 * 
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * 
 * Random generator for vectors uniformly distributed on the unit sphere.
 * 
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_RandomOnSphere {
    /** The random gen. */
    private final WB_MTRandom randomGen;

    /**
     * Instantiates a new w b_ random sphere.
     */
    public WB_RandomOnSphere() {
	randomGen = new WB_MTRandom();
    }

    /**
     * Set random seed.
     * 
     * @param seed
     *            seed
     * @return self
     */
    public WB_RandomOnSphere setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     * Next point.
     * 
     * @return next random WB_Normal on unit sphere
     */
    public WB_Point nextPoint() {
	final double eps = randomGen.nextDouble();
	final double z = 1.0 - (2.0 * eps);
	final double r = Math.sqrt(1.0 - (z * z));
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Point(r * Math.cos(t), r * Math.sin(t), z);
    }

    /**
     * Next vector.
     * 
     * @return next random WB_Normal on unit sphere
     */
    public WB_Vector nextVector() {
	final double eps = randomGen.nextDouble();
	final double z = 1.0 - (2.0 * eps);
	final double r = Math.sqrt(1.0 - (z * z));
	final double t = 2 * Math.PI * randomGen.nextDouble();
	return new WB_Vector(r * Math.cos(t), r * Math.sin(t), z);
    }
}
