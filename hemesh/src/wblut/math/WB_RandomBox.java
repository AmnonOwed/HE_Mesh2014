package wblut.math;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * 
 * Random generator for vectors uniformly distributed in the unit cube.
 * 
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_RandomBox {

	/** The random gen. */
	private final WB_MTRandom randomGen;

	/**
	 * Instantiates a new w b_ random box.
	 */
	public WB_RandomBox() {
		randomGen = new WB_MTRandom();
	}

	/**
	 * Set random seed.
	 * 
	 * @param seed
	 *            seed
	 * @return self
	 */
	public WB_RandomBox setSeed(final long seed) {
		randomGen.setSeed(seed);
		return this;
	}

	/**
	 * Next point.
	 * 
	 * @return the w b_ point3d
	 */
	public WB_Point nextPoint() {
		return new WB_Point(randomGen.nextDouble(), randomGen.nextDouble(),
				randomGen.nextDouble());
	}

	/**
	 * Next vector.
	 * 
	 * @return the w b_ vector3d
	 */
	public WB_Vector nextVector() {
		return new WB_Vector(randomGen.nextDouble(), randomGen.nextDouble(),
				randomGen.nextDouble());
	}

	/**
	 * Next normal.
	 * 
	 * @return the w b_ normal3d
	 */
	public WB_Vector nextNormal() {
		return new WB_Vector(randomGen.nextDouble(), randomGen.nextDouble(),
				randomGen.nextDouble());
	}

}
