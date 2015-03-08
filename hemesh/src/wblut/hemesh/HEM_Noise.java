/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import wblut.geom.WB_Vector;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;
import wblut.math.WB_RandomOnSphere;

/**
 * Expands or contracts all vertices along the vertex normals.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Noise extends HEM_Modifier {
    /** Expansion distance. */
    private WB_Parameter<Double> d;

    /**
     * Instantiates a new hE m_ noise.
     */
    public HEM_Noise() {
	super();
	setDistance(0);
    }

    /**
     * Set distance to move vertices.
     *
     * @param d
     *            distance
     * @return this
     */
    public HEM_Noise setDistance(final double d) {
	this.d = new WB_ConstantParameter<Double>(d);
	return this;
    }

    /**
     * Sets the distance.
     *
     * @param d
     *            the d
     * @return the hE m_ noise
     */
    public HEM_Noise setDistance(final WB_Parameter<Double> d) {
	this.d = d;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	if ((d == null)) {
	    return mesh;
	}
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = mesh.vItr();
	final WB_RandomOnSphere rs = new WB_RandomOnSphere();
	WB_Vector n;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    n = rs.nextVector();
	    v.getPoint().addSelf(n.mulSelf(d.value(v.xd(), v.yd(), v.zd())));
	}
	mesh.resetFaces();
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	selection.collectVertices();
	final Iterator<HE_Vertex> vItr = selection.vItr();
	HE_Vertex v;
	final WB_RandomOnSphere rs = new WB_RandomOnSphere();
	WB_Vector n;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    n = rs.nextVector();
	    v.getPoint().addSelf(n.mulSelf(d.value(v.xd(), v.yd(), v.zd())));
	}
	selection.parent.resetFaces();
	return selection.parent;
    }
}
