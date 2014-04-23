package wblut.hemesh;

import java.util.Iterator;

import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

public class HEM_SphereInversion extends HEM_Modifier {

	private WB_Point center;

	private double r;

	private double r2;

	private double icutoff;

	private boolean linear;

	public HEM_SphereInversion() {
		super();
		center = new WB_Point(0, 0, 0);
		icutoff = 0.0001;
		linear = false;
	}

	public HEM_SphereInversion setCenter(final WB_Point c) {
		center = c;
		return this;
	}

	public HEM_SphereInversion setCenter(final double x, final double y,
			final double z) {
		center = new WB_Point(x, y, z);
		return this;
	}

	public HEM_SphereInversion setRadius(final double r) {
		this.r = r;
		r2 = r * r;
		return this;
	}

	public HEM_SphereInversion setCutoff(final double cutoff) {
		icutoff = 1.0 / cutoff;
		return this;
	}

	public HEM_SphereInversion setLinear(final boolean b) {
		linear = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.modifiers.HEM_Modifier#modify(wblut.hemesh.core.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (center == null) {
			return mesh;
		}
		if (r == 0) {
			return mesh;
		}
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		WB_Vector d;
		WB_Point surf;
		double ri, rf;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (linear) {
				d = v.pos.subToVector(center);
				d._normalizeSelf();
				surf = new WB_Point(center)._addSelf(r, d);
				d = surf.subToVector(v)._mulSelf(2);
				v.pos._addSelf(d);
			} else {
				d = v.pos.subToVector(center);
				ri = d.getLength();
				d._normalizeSelf();
				rf = r2 * Math.max(icutoff, 1.0 / ri);
				v._set(center);
				v.pos._addSelf(rf, d);
			}

		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.modifiers.HEM_Modifier#modifySelected(wblut.hemesh.core.
	 * HE_Mesh, wblut.hemesh.core.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		if (center == null) {
			return selection.parent;
		}
		if (r == 0) {
			return selection.parent;
		}
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		WB_Vector d;
		WB_Point surf;
		double ri, rf;
		while (vItr.hasNext()) {
			v = vItr.next();
			if (linear) {
				d = v.pos.subToVector(center);
				d._normalizeSelf();
				surf = new WB_Point(center)._addSelf(r, d);
				d = v.pos.subToVector(surf);
				v.pos._addSelf(d);
			} else {
				d = v.pos.subToVector(center);
				ri = d.getLength();
				d._normalizeSelf();
				rf = r2 * Math.max(icutoff, 1.0 / ri);
				v.pos._set(center);
				v.pos._addSelf(rf, d);
			}

		}
		return selection.parent;
	}

}
