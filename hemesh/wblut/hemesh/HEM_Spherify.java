package wblut.hemesh;

import java.util.Iterator;

import wblut.geom.WB_Point;
import wblut.geom.WB_Sphere;

public class HEM_Spherify extends HEM_Modifier {

	private final WB_Sphere sphere;

	public HEM_Spherify() {
		super();
		sphere = new WB_Sphere();
	}

	public HEM_Spherify setRadius(final double r) {
		sphere.setRadius(r);
		return this;
	}

	public HEM_Spherify setCenter(final double x, final double y, final double z) {
		sphere.getCenter()._set(x, y, z);
		return this;
	}

	public HEM_Spherify setCenter(final WB_Point c) {
		sphere.setCenter(c);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.modifiers.HEM_Modifier#apply(wblut.hemesh.core.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();
			v.pos._set(sphere.projectToSphere(v));
		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seewblut.hemesh.modifiers.HEM_Modifier#applySelected(wblut.hemesh.core.
	 * HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();
			v.pos._set(sphere.projectToSphere(v));
		}
		return selection.parent;
	}

}
