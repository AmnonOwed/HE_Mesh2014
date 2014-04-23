package wblut.hemesh;

import java.util.Iterator;

import wblut.WB_Epsilon;
import wblut.geom.WB_Distance3D;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Stretch and compress a mesh. Determined by a ground plane, a stretch factor
 * and a compression factor. Most commonly, the ground plane normal is the
 * stretch direction.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Stretch extends HEM_Modifier {

	/** Ground plane. */
	private WB_Plane groundPlane;

	/** Stretch direction. */
	private WB_Vector stretchDirection;

	/** Stretch factor. */
	private double stretchFactor;

	/** Compression factor. */
	private double compressionFactor;

	/** Modify only positive side of ground plane?. */
	private boolean posOnly;

	/**
	 * Instantiates a new HEM_Stretch.
	 */
	public HEM_Stretch() {
		super();
	}

	/**
	 * Set ground plane.
	 * 
	 * @param P
	 *            ground plane
	 * @return self
	 */
	public HEM_Stretch setGroundPlane(final WB_Plane P) {
		groundPlane = P;
		return this;
	}

	/**
	 * Sets the ground plane.
	 * 
	 * @param ox
	 *            the ox
	 * @param oy
	 *            the oy
	 * @param oz
	 *            the oz
	 * @param nx
	 *            the nx
	 * @param ny
	 *            the ny
	 * @param nz
	 *            the nz
	 * @return the hE m_ stretch
	 */
	public HEM_Stretch setGroundPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		groundPlane = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set stretch factor along stretch direction.
	 * 
	 * @param f
	 *            the f
	 * @return self
	 */
	public HEM_Stretch setStretchFactor(final double f) {
		stretchFactor = f;
		compressionFactor = Math.sqrt(f);
		return this;
	}

	/**
	 * Set compression factor perpendicular to stretch direction.
	 * 
	 * @param f
	 *            the f
	 * @return self
	 */
	public HEM_Stretch setCompressionFactor(final double f) {
		if (f != 0) {
			compressionFactor = f;
		}
		return this;
	}

	/**
	 * Positive only? Only apply modifier to positive side of ground plane.
	 * 
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Stretch setPosOnly(final boolean b) {
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
		if ((groundPlane != null) && (stretchDirection == null)) {
			stretchDirection = new WB_Vector(groundPlane.getNormal());
		}

		if ((groundPlane != null) && (stretchFactor != 0)
				&& (compressionFactor != 0)) {
			final WB_Line L = new WB_Line(groundPlane.getOrigin(),
					stretchDirection);
			WB_Point p;
			final Iterator<HE_Vertex> vItr = mesh.vItr();
			HE_Vertex v;
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance3D.distance(v, groundPlane);
				if (!posOnly || (d > WB_Epsilon.EPSILON)) {
					p = WB_Intersection.getClosestPoint(v, groundPlane);
					v.pos._subSelf(p);
					v.pos._mulSelf(stretchFactor);
					v.pos._addSelf(p);
					p = WB_Intersection.getClosestPoint(v, L);
					v.pos._subSelf(p);
					v.pos._mulSelf(1 / compressionFactor);
					v.pos._addSelf(p);
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
		if ((groundPlane != null) && (stretchDirection == null)) {
			stretchDirection = new WB_Vector(groundPlane.getNormal());
		}

		if ((groundPlane != null)
				&& ((stretchFactor != 0) || compressionFactor != 0)) {
			final WB_Line L = new WB_Line(groundPlane.getOrigin(),
					stretchDirection);
			WB_Point p;
			final Iterator<HE_Vertex> vItr = selection.vItr();
			HE_Vertex v;
			while (vItr.hasNext()) {
				v = vItr.next();
				final double d = WB_Distance3D.distance(v, groundPlane);
				if (!posOnly || (d > WB_Epsilon.EPSILON)) {
					p = WB_Intersection.getClosestPoint(v, groundPlane);
					v.pos._subSelf(p);
					v.pos._mulSelf(stretchFactor);
					v.pos._addSelf(p);
					p = WB_Intersection.getClosestPoint(v, L);
					v.pos._subSelf(p);
					v.pos._mulSelf(1 / compressionFactor);
					v.pos._addSelf(p);
				}
			}
		}
		return selection.parent;
	}

}
