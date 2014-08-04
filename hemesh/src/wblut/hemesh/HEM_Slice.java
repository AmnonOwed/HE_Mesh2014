package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;

import wblut.geom.WB_Classification;
import wblut.geom.WB_Plane;

/**
 * Planar cut of a mesh. Faces on positive side of cut plane are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Slice extends HEM_Modifier {

	/** Cut plane. */
	private WB_Plane P;

	/** Reverse planar cut. */
	private boolean reverse = false;

	/** Cap holes?. */
	private boolean capHoles = true;

	/** The simple cap. */
	private boolean simpleCap = true;

	/** Keep center of cut mesh?. */
	private boolean keepCenter = false;

	/** Store cut faces. */
	public HE_Selection cut;

	/** Store cap faces. */
	public HE_Selection cap;

	/** The offset. */
	private double offset;

	/**
	 * Set offset.
	 *
	 * @param d
	 *            offset
	 * @return self
	 */
	public HEM_Slice setOffset(final double d) {
		offset = d;
		return this;
	}

	/**
	 * Instantiates a new HEM_Slice.
	 */
	public HEM_Slice() {
		super();

	}

	/**
	 * Set cut plane.
	 *
	 * @param P
	 *            cut plane
	 * @return self
	 */
	public HEM_Slice setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	/**
	 * Sets the plane.
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
	 * @return the hE m_ slice
	 */
	public HEM_Slice setPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	/**
	 * Set reverse option.
	 *
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Slice setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	/**
	 * Set option to cap holes.
	 *
	 * @param b
	 *            true, false;
	 * @return self
	 */

	public HEM_Slice setCap(final Boolean b) {
		capHoles = b;
		return this;
	}

	/**
	 * Sets the simple cap.
	 *
	 * @param b
	 *            the b
	 * @return the hE m_ slice
	 */
	public HEM_Slice setSimpleCap(final Boolean b) {
		simpleCap = true;// b;
		return this;
	}

	/**
	 * Set option to reset mesh center.
	 *
	 * @param b
	 *            true, false;
	 * @return self
	 */

	public HEM_Slice setKeepCenter(final Boolean b) {
		keepCenter = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		cut = new HE_Selection(mesh);
		cap = new HE_Selection(mesh);
		// no plane defined
		if (P == null) {
			return mesh;
		}

		// empty mesh
		if (mesh.getNumberOfVertices() == 0) {
			return mesh;
		}

		WB_Plane lP = P.get();
		if (reverse) {
			lP.flipNormal();
		}
		lP = new WB_Plane(lP.getNormal(), lP.d() + offset);
		HEM_SliceSurface ss;
		ss = new HEM_SliceSurface().setPlane(lP);
		mesh.modify(ss);

		cut = ss.cut;
		final HE_Selection newFaces = new HE_Selection(mesh);
		HE_Face face;
		Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			final WB_Classification cptp = WB_Plane.classifyPolygonToPlane(
					face.toPolygon(), lP);
			if ((cptp == WB_Classification.FRONT)
					|| (cptp == WB_Classification.ON)) {
				newFaces.add(face);
			}
			else {
				if (cut.contains(face)) {
					cut.remove(face);
				}
			}

		}

		mesh.replaceFaces(newFaces.getFacesAsArray());
		cut.cleanSelection();
		mesh.cleanUnusedElementsByFace();
		final ArrayList<HE_Face> facesToRemove = new ArrayList<HE_Face>();
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			face = fItr.next();
			if (face.getFaceOrder() < 3) {
				facesToRemove.add(face);
			}
		}
		mesh.removeFaces(facesToRemove);
		mesh.cleanUnusedElementsByFace();
		if (capHoles) {
			if (simpleCap) {
				cap.addFaces(mesh.capHoles());
				mesh.pairHalfedges();
				mesh.capHalfedges();
			}
			else {
				// TODO

			}

		}
		else {
			mesh.pairHalfedges();
			mesh.capHalfedges();
		}

		// mesh.triangulateConcaveFaces();
		if (!keepCenter) {
			mesh.resetCenter();
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
		return apply(selection.parent);
	}

}
