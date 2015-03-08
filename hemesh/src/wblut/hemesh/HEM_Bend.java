/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import wblut.geom.WB_GeometryOp;
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

    /**
     * Instantiates a new HEM_Bend.
     */
    public HEM_Bend() {
	super();
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
	return this;
    }

    /**
     * Sets the ground plane.
     *
     * @param ox 
     * @param oy 
     * @param oz 
     * @param nx 
     * @param ny 
     * @param nz 
     * @return self
     */
    public HEM_Bend setGroundPlane(final double ox, final double oy,
	    final double oz, final double nx, final double ny, final double nz) {
	groundPlane = new WB_Plane(ox, oy, oz, nx, ny, nz);
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
	return this;
    }

    /**
     * Sets the bend axis.
     *
     * @param p1x 
     * @param p1y 
     * @param p1z 
     * @param p2x 
     * @param p2y 
     * @param p2z 
     * @return self
     */
    public HEM_Bend setBendAxis(final double p1x, final double p1y,
	    final double p1z, final double p2x, final double p2y,
	    final double p2z) {
	bendAxis = new WB_Line(p1x, p1y, p1z, p2x - p1x, p2y - p1y, p2z - p2y);
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
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	if ((groundPlane != null) && (bendAxis != null) && (angleFactor != 0)) {
	    HE_Vertex v;
	    final Iterator<HE_Vertex> vItr = mesh.vItr();
	    while (vItr.hasNext()) {
		v = vItr.next();
		final double d = WB_GeometryOp.getDistance3D(v, groundPlane);
		if (!posOnly || (d > 0)) {
		    v.getPoint().rotateAboutAxisSelf(d * angleFactor,
			    bendAxis.getOrigin(), bendAxis.getDirection());
		}
	    }
	}
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
	if ((groundPlane != null) && (bendAxis != null) && (angleFactor != 0)) {
	    HE_Vertex v;
	    final Iterator<HE_Vertex> vItr = selection.vItr();
	    while (vItr.hasNext()) {
		v = vItr.next();
		final double d = WB_GeometryOp.getDistance3D(v, groundPlane);
		if (!posOnly || (d > 0)) {
		    v.getPoint().rotateAboutAxisSelf(d * angleFactor,
			    bendAxis.getOrigin(), bendAxis.getDirection());
		}
	    }
	}
	return selection.parent;
    }
}
