/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import javolution.util.FastTable;
import wblut.geom.WB_Vector;

/**
 * 
 */
public class HEM_VertexExpand extends HEM_Modifier {
    
    /**
     * 
     */
    private double d;

    /**
     * 
     */
    public HEM_VertexExpand() {
	super();
    }

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public HEM_VertexExpand setDistance(final double d) {
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
	if (d == 0) {
	    return mesh;
	}
	HE_Vertex v;
	Iterator<HE_Vertex> vItr = mesh.vItr();
	final FastTable<WB_Vector> normals = new FastTable<WB_Vector>();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    normals.add(v.getVertexNormal());
	}
	final Iterator<WB_Vector> vnItr = normals.iterator();
	vItr = mesh.vItr();
	WB_Vector n;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    n = vnItr.next();
	    v.getPoint().addSelf(n.mulSelf(d));
	}
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	if (d == 0) {
	    return selection.parent;
	}
	selection.collectVertices();
	final Iterator<HE_Vertex> vItr = selection.vItr();
	HE_Vertex v;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.getPoint().addSelf(v.getVertexNormal().mulSelf(d));
	}
	return selection.parent;
    }
}
