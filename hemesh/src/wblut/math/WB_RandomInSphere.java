/*
 * 
 */
package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * 
 * Random generator for vectors uniformly distributed inside the unit sphere.
 * 
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_RandomInSphere {
    /** The random gen. */
    private final WB_MTRandom randomGen;

    /**
     * Instantiates a new w b_ random sphere.
     */
    public WB_RandomInSphere() {
	randomGen = new WB_MTRandom();
    }

    /**
     * Set random seed.
     * 
     * @param seed
     *            seed
     * @return self
     */
    public WB_RandomInSphere setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     * Next point.
     * 
     * @return next random WB_Normal on unit sphere
     */
    public WB_Point nextPoint() {
	final double elevation = Math.asin((2.0 * randomGen.nextDouble()) - 1);
	final double azimuth = 2 * Math.PI * randomGen.nextDouble();
	final double r = Math.pow(randomGen.nextDouble(), 1.0 / 3.0);
	return new WB_Point(r * Math.cos(elevation) * Math.cos(azimuth), r
		* Math.cos(elevation) * Math.sin(azimuth), r
		* Math.sin(elevation));
    }

    /**
     * Next vector.
     * 
     * @return next random WB_Normal on unit sphere
     */
    public WB_Vector nextVector() {
	final double elevation = Math.asin((2.0 * randomGen.nextDouble()) - 1);
	final double azimuth = 2 * Math.PI * randomGen.nextDouble();
	final double r = Math.pow(randomGen.nextDouble(), 1.0 / 3.0);
	return new WB_Vector(r * Math.cos(elevation) * Math.cos(azimuth), r
		* Math.cos(elevation) * Math.sin(azimuth), r
		* Math.sin(elevation));
    }
}
