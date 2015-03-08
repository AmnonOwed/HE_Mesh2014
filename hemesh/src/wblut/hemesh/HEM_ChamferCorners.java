/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;
import wblut.geom.WB_ClassificationConvex;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;

/**
 * Chamfer all convex corners.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_ChamferCorners extends HEM_Modifier {
    /** Chamfer distance. */
    private WB_Parameter<Double> distance;

    /**
     * Instantiates a new HEM_ChamferCorners.
     */
    public HEM_ChamferCorners() {
	super();
    }

    /**
     * Set chamfer distance along vertex normals.
     * 
     * @param d
     *            distance
     * @return self
     */
    public HEM_ChamferCorners setDistance(final double d) {
	distance = new WB_ConstantParameter<Double>(d);
	return this;
    }

    /**
     * Set chamfer distance along vertex normals.
     * 
     * @param d
     *            WB_Parameter
     * @return self
     */
    public HEM_ChamferCorners setDistance(final WB_Parameter<Double> d) {
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
	if (distance == null) {
	    return mesh;
	}
	final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = mesh.vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getVertexType() == WB_ClassificationConvex.CONVEX) {
		final WB_Vector N = new WB_Vector(v.getVertexNormal());
		final WB_Point O = new WB_Point(N).mulSelf(-distance.value(
			v.xd(), v.yd(), v.zd()));
		N.mulSelf(-1);
		O.addSelf(v);
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
	if (distance == null) {
	    return selection.parent;
	}
	final ArrayList<WB_Plane> cutPlanes = new ArrayList<WB_Plane>();
	selection.collectVertices();
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = selection.vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getVertexType() == WB_ClassificationConvex.CONVEX) {
		final WB_Vector N = new WB_Vector(v.getVertexNormal());
		final WB_Point O = new WB_Point(N).mulSelf(-distance.value(
			v.xd(), v.yd(), v.zd()));
		N.mulSelf(-1);
		O.addSelf(v);
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
