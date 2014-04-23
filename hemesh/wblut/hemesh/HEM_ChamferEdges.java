package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;

import wblut.geom.WB_Convex;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Chamfer all convex edges.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_ChamferEdges extends HEM_Modifier {

	/** Chamfer distance. */
	private double distance;

	/**
	 * Instantiates a new HEM_ChamferEdges.
	 */
	public HEM_ChamferEdges() {
		super();
		distance = 0;
	}

	/**
	 * Set chamfer distance along edge normals.
	 * 
	 * @param d
	 *            distance
	 * @return self
	 */
	public HEM_ChamferEdges setDistance(final double d) {
		distance = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (distance == 0) {
			return mesh;
		}
		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if ((e.getStartVertex().getVertexType() == WB_Convex.CONVEX)
					|| (e.getEndVertex().getVertexType() == WB_Convex.CONVEX)) {
				final WB_Vector N = new WB_Vector(e.getEdgeNormal());
				final WB_Point O = new WB_Point(N)._mulSelf(-distance);
				N._mulSelf(-1);
				O._addSelf(e.getEdgeCenter());
				final WB_Plane P = new WB_Plane(O, N);
				cutPlanes.add(P);
			}

		}
		final HEM_MultiSlice msm = new HEM_MultiSlice();
		msm.setPlanes(cutPlanes);
		mesh.modify(msm);
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
		if (distance == 0) {
			return selection.parent;
		}
		final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
		selection.collectEdges();
		final Iterator<HE_Edge> eItr = selection.parent.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if ((e.getStartVertex().getVertexType() == WB_Convex.CONVEX)
					|| (e.getEndVertex().getVertexType() == WB_Convex.CONVEX)) {
				final WB_Vector N = new WB_Vector(e.getEdgeNormal());
				final WB_Point O = new WB_Point(N)._mulSelf(-distance);
				N._mulSelf(-1);
				O._addSelf(e.getEdgeCenter());
				final WB_Plane P = new WB_Plane(O, N);
				cutPlanes.add(P);
			}

		}
		final HEM_MultiSlice msm = new HEM_MultiSlice();
		msm.setPlanes(cutPlanes);
		selection.parent.modify(msm);

		return selection.parent;
	}
}