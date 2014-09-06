package wblut.hemesh;

import java.util.Iterator;

import wblut.geom.WB_AABB;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

public class HEM_Inflate extends HEM_Modifier {

	private boolean autoRescale;

	private int iter;

	private double radius;

	private double factor;

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */

	public HEM_Inflate() {
		radius = 10;
		factor = 0.1;
	}

	public HEM_Inflate setAutoRescale(final boolean b) {
		autoRescale = b;
		return this;

	}

	public HEM_Inflate setIterations(final int r) {
		iter = r;
		return this;

	}

	public HEM_Inflate setRadius(final double r) {
		radius = r;
		return this;

	}

	public HEM_Inflate setFactor(final double f) {
		factor = f;
		return this;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		WB_AABB box = new WB_AABB();
		if (autoRescale) {
			box = mesh.getAABB();
		}

		final WB_KDTree<HE_Vertex, Integer> tree = new WB_KDTree<HE_Vertex, Integer>();
		Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		int id = 0;
		while (vItr.hasNext()) {
			tree.add(vItr.next(), id++);
		}
		final WB_Point[] newPositions = new WB_Point[mesh.getNumberOfVertices()];
		if (iter < 1) {
			iter = 1;
		}
		for (int r = 0; r < iter; r++) {
			vItr = mesh.vItr();
			WB_KDTree.WB_KDEntry<HE_Vertex, Integer>[] neighbors;
			id = 0;
			WB_Vector dv;
			while (vItr.hasNext()) {
				v = vItr.next();
				dv = new WB_Vector(v);
				neighbors = tree.getRange(v, radius);

				for (int i = 0; i < neighbors.length; i++) {
					if (neighbors[i].coord != v) {
						final WB_Vector tmp = neighbors[i].coord.getPoint()
								.subToVector(v);
						tmp.normalizeSelf();
						dv.addSelf(tmp);
					}
				}
				dv.normalizeSelf();
				dv.mulSelf(factor);
				newPositions[id] = v.getPoint().add(dv);

				id++;

			}
			vItr = mesh.vItr();
			id = 0;
			while (vItr.hasNext()) {
				vItr.next().set(newPositions[id]);
				id++;
			}

		}
		mesh.resetCenter();
		if (autoRescale) {
			mesh.fitInAABBConstrained(box);
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
		selection.collectVertices();

		WB_AABB box = new WB_AABB();
		if (autoRescale) {
			box = selection.parent.getAABB();
		}

		final WB_KDTree<HE_Vertex, Integer> tree = new WB_KDTree<HE_Vertex, Integer>();
		Iterator<HE_Vertex> vItr = selection.parent.vItr();
		HE_Vertex v;
		int id = 0;
		while (vItr.hasNext()) {
			tree.add(vItr.next(), id++);
		}
		final WB_Point[] newPositions = new WB_Point[selection
		                                             .getNumberOfVertices()];
		if (iter < 1) {
			iter = 1;
		}
		for (int r = 0; r < iter; r++) {
			vItr = selection.vItr();
			final HE_Vertex n;
			WB_KDTree.WB_KDEntry<HE_Vertex, Integer>[] neighbors;
			id = 0;

			while (vItr.hasNext()) {
				v = vItr.next();
				final WB_Vector dv = new WB_Vector(v);
				neighbors = tree.getRange(v, radius);
				for (int i = 0; i < neighbors.length; i++) {
					if (neighbors[i].coord != v) {
						final WB_Vector tmp = neighbors[i].coord.getPoint()
								.subToVector(v);
						tmp.normalizeSelf();
						dv.addSelf(tmp);
					}
				}
				dv.normalizeSelf();
				dv.mulSelf(factor);
				newPositions[id] = v.getPoint().add(dv);
				id++;
			}
			vItr = selection.vItr();
			id = 0;
			while (vItr.hasNext()) {
				vItr.next().set(newPositions[id]);
				id++;
			}
		}
		selection.parent.resetCenter();
		if (autoRescale) {
			selection.parent.fitInAABBConstrained(box);
		}
		return selection.parent;
	}

}
