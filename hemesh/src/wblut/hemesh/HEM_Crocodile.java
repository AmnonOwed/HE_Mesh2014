package wblut.hemesh;

import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;

public class HEM_Crocodile extends HEM_Modifier {
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
	private double distance;

	private double chamfer;

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */

	public HEM_Crocodile() {

		chamfer = 0.5;
	}

	public HEM_Crocodile setDistance(final double d) {
		distance = d;
		return this;

	}

	public HEM_Crocodile setChamfer(final double c) {
		chamfer = c;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		final HE_Selection selection = mesh.selectAllVertices();

		return apply(selection);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		final Map<Long, WB_Point> umbrellapoints = new FastMap<Long, WB_Point>();
		HE_VertexIterator vitr = new HE_VertexIterator(selection);
		HE_Vertex v;
		if (chamfer == 0.5) {

			List<HE_Edge> star;

			while (vitr.hasNext()) {
				v = vitr.next();
				star = v.getEdgeStar();
				for (final HE_Edge e : star) {
					umbrellapoints.put(e._key, e.getEdgeCenter());
				}

			}

			for (final long e : umbrellapoints.keySet()) {
				selection.parent.splitEdge(e, umbrellapoints.get(e));
			}

		}
		else {
			List<HE_Halfedge> star;

			while (vitr.hasNext()) {
				v = vitr.next();
				star = v.getHalfedgeStar();
				for (final HE_Halfedge he : star) {
					umbrellapoints.put(
							he._key,
							gf.createInterpolatedPoint(he.getVertex(),
									he.getEndVertex(), chamfer));
				}

			}

			for (final long he : umbrellapoints.keySet()) {
				selection.parent
				.splitEdge(selection.parent.getHalfedgeByKey(he)
						.getEdge(), umbrellapoints.get(he));
			}

		}
		vitr = new HE_VertexIterator(selection);
		while (vitr.hasNext()) {
			v = vitr.next();
			v.getPoint()._addMulSelf(distance, v.getVertexNormal());

		}
		return selection.parent;
	}
}
