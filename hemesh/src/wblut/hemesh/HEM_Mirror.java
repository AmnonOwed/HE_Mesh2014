package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import wblut.geom.WB_Classification;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

public class HEM_Mirror extends HEM_Modifier {

	private WB_Plane P;

	private boolean keepCenter = false;

	private boolean reverse = false;

	public HE_Selection cut;

	private double offset;

	public HEM_Mirror setOffset(final double d) {
		offset = d;
		return this;
	}

	public HEM_Mirror() {
		super();
	}

	public HEM_Mirror setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	public HEM_Mirror setPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	public HEM_Mirror setReverse(final Boolean b) {
		reverse = b;
		return this;
	}

	public HEM_Mirror setKeepCenter(final Boolean b) {
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
			} else {
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
		mesh.capHalfedges();
		HE_Mesh mirrormesh = mesh.get();
		List<HE_Vertex> vertices = mirrormesh.getVerticesAsList();
		for (HE_Vertex v : vertices) {
			WB_Point p = WB_Intersection.getClosestPoint(v, lP);
			WB_Vector dv = v.pos.subToVector(p);
			v.pos._addSelf(-2, dv);
		}
		mirrormesh.flipAllFaces();

		mesh.add(mirrormesh);

		mesh.pairHalfedgesAndCreateEdges();
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
