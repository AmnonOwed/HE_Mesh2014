package wblut.hemesh;

import java.util.Iterator;

import org.apache.log4j.Logger;

import wblut.geom.WB_Distance;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;

/**
 * Bend a mesh. Determined by a ground plane, a bend axis and an angle factor.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Bend extends HEM_Modifier {

	/** Ground plane. */
	private WB_Plane groundPlane;

	/** Bend axis. */
	private WB_Line bendAxis;

	/** Angle factor. */
	private double angleFactor;

	/** Positive side of plane only?. */
	private boolean posOnly;

	private static Logger logger = Logger.getLogger(HEM_Bend.class);

	/**
	 * Instantiates a new HEM_Bend.
	 */
	public HEM_Bend() {
		super();
		logger.info("Initializing HEM_Bend");
	}

	/**
	 * Set ground plane.
	 *
	 * @param P
	 *            ground plane
	 * @return self
	 */
	public HEM_Bend setGroundPlane(final WB_Plane P) {

		groundPlane = P;
		logger.debug("Setting ground plane: " + groundPlane);
		return this;
	}

	/**
	 * Sets the ground plane.
	 *
	 * @return self
	 */
	public HEM_Bend setGroundPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		groundPlane = new WB_Plane(ox, oy, oz, nx, ny, nz);
		logger.debug("Setting ground plane: " + groundPlane);
		return this;
	}

	/**
	 * Set bend axis.
	 *
	 * @param a
	 *            bend axis
	 * @return self
	 */
	public HEM_Bend setBendAxis(final WB_Line a) {
		bendAxis = a;
		logger.debug("Setting bending axis: " + bendAxis);
		return this;
	}

	/**
	 * Sets the bend axis.
	 *
	 * @return self
	 * */
	public HEM_Bend setBendAxis(final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		bendAxis = new WB_Line(p1x, p1y, p1z, p2x - p1x, p2y - p1y, p2z - p2y);
		logger.debug("Setting bending axis: " + bendAxis);
		return this;
	}

	/**
	 * Set angle factor, ratio of bend angle in degrees to distance to ground
	 * plane.
	 *
	 * @param f
	 *            direction
	 * @return self
	 */
	public HEM_Bend setAngleFactor(final double f) {
		angleFactor = f * (Math.PI / 180);
		logger.debug("Setting angle factor in radians per unit distance: "
				+ angleFactor);
		return this;
	}

	/**
	 * Positive only? Only apply modifier to positive side of ground plane.
	 *
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Bend setPosOnly(final boolean b) {
		posOnly = b;
		logger.debug("Setting positive side of ground plane only: " + posOnly);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		logger.info("Starting modifier.");
		if ((groundPlane != null) && (bendAxis != null) && (angleFactor != 0)) {
			HE_Vertex v;
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				logger.trace("Current vertex: " + v);
				final double d = WB_Distance.getDistance3D(v, groundPlane);
				logger.trace("Distance of vertex to ground plane :" + d + ".");
				if (!posOnly || (d > 0)) {
					v.getPoint().rotateAboutAxis(d * angleFactor,
							bendAxis.getOrigin(), bendAxis.getDirection());
				}
				logger.trace("New position: " + v.getPoint());
			}
		}
		logger.info("Ending modifier");
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		logger.info("Starting modifier.");
		if ((groundPlane != null) && (bendAxis != null) && (angleFactor != 0)) {
			HE_Vertex v;
			final Iterator<HE_Vertex> vItr = selection.vItr();
			while (vItr.hasNext()) {
				v = vItr.next();
				logger.trace("Current vertex: " + v);
				final double d = WB_Distance.getDistance3D(v, groundPlane);
				logger.trace("Distance of vertex to ground plane :" + d + ".");
				if (!posOnly || (d > 0)) {
					v.getPoint().rotateAboutAxis(d * angleFactor,
							bendAxis.getOrigin(), bendAxis.getDirection());
				}
				logger.trace("New position: " + v.getPoint());
			}
		}
		logger.info("Ending modifier");
		return selection.parent;
	}

}
