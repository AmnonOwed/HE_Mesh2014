/*
 * 
 */
package wblut.hemesh;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Turns a solid into a rudimentary shelled structure.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Shell extends HEM_Modifier {
    
    /**
     * 
     */
    private double d;

    /**
     * 
     */
    public HEM_Shell() {
	super();
	d = 0;
    }

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public HEM_Shell setThickness(final double d) {
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
	final HE_Mesh innerMesh = mesh.get();
	final HEM_VertexExpand expm = new HEM_VertexExpand().setDistance(-d);
	innerMesh.modify(expm);
	final HashMap<Long, Long> heCorrelation = new HashMap<Long, Long>();
	final Iterator<HE_Halfedge> heItr1 = mesh.heItr();
	final Iterator<HE_Halfedge> heItr2 = innerMesh.heItr();
	HE_Halfedge he1;
	HE_Halfedge he2;
	while (heItr1.hasNext()) {
	    he1 = heItr1.next();
	    he2 = heItr2.next();
	    if (he1.getFace() == null) {
		heCorrelation.put(he1.key(), he2.key());
	    }
	}
	innerMesh.flipAllFaces();
	mesh.addVertices(innerMesh.getVerticesAsArray());
	mesh.addFaces(innerMesh.getFacesAsArray());
	mesh.addHalfedges(innerMesh.getHalfedgesAsArray());
	final Iterator<Map.Entry<Long, Long>> it = heCorrelation.entrySet()
		.iterator();
	HE_Halfedge heio, heoi;
	HE_Face fNew;
	while (it.hasNext()) {
	    final Map.Entry<Long, Long> pairs = it.next();
	    he1 = mesh.getHalfedgeByKey(pairs.getKey());
	    he2 = mesh.getHalfedgeByKey(pairs.getValue());
	    heio = new HE_Halfedge();
	    heoi = new HE_Halfedge();
	    mesh.add(heio);
	    mesh.add(heoi);
	    heio.setVertex(he1.getPair().getVertex());
	    heoi.setVertex(he2.getPair().getVertex());
	    he1.setNext(heio);
	    heio.setNext(he2);
	    he2.setNext(heoi);
	    heoi.setNext(he1);
	    fNew = new HE_Face();
	    mesh.add(fNew);
	    fNew.setHalfedge(he1);
	    he1.setFace(fNew);
	    he2.setFace(fNew);
	    heio.setFace(fNew);
	    heoi.setFace(fNew);
	}
	mesh.pairHalfedges();
	if (d < 0) {
	    mesh.flipAllFaces();
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
	return apply(selection.parent);
    }
}
