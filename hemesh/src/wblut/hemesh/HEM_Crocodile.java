/*
 * 
 */
package wblut.hemesh;

import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;

/**
 * 
 */
public class HEM_Crocodile extends HEM_Modifier {
    
    /**
     * 
     */
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    
    /**
     * 
     */
    private double distance;
    
    /**
     * 
     */
    public HE_Selection spikes;
    
    /**
     * 
     */
    private double chamfer;

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.modifiers.HEB_Modifier#modify(wblut.hemesh.HE_Mesh)
     */
    /**
     * 
     */
    public HEM_Crocodile() {
	chamfer = 0.5;
    }

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public HEM_Crocodile setDistance(final double d) {
	distance = d;
	return this;
    }

    /**
     * 
     *
     * @param c 
     * @return 
     */
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
	spikes = new HE_Selection(selection.parent);
	selection.collectVertices();
	tracker.setDefaultStatus("Starting HEM_Crocodile.");
	final Map<Long, WB_Point> umbrellapoints = new FastMap<Long, WB_Point>();
	HE_VertexIterator vitr = new HE_VertexIterator(selection);
	HE_Vertex v;
	if (chamfer == 0) {
	    tracker.setDefaultStatus("Chamfer is 0, nothing to do. Exiting HEM_Crocodile.");
	    return selection.parent;
	}
	if (chamfer < 0) {
	    chamfer *= -1;
	}
	if ((chamfer > 0.5) && (chamfer < 1.0)) {
	    chamfer = 1.0 - chamfer;
	} else if ((chamfer < 0) || (chamfer > 1)) {
	    tracker.setDefaultStatus("Chamfer is outside range (0-0.5), nothing to do. Exiting HEM_Crocodile.");
	    return selection.parent;
	}
	if (chamfer == 0.5) {
	    tracker.setDefaultStatus("Enumerating vertex umbrellas.",
		    selection.getNumberOfVertices());
	    List<HE_Halfedge> star;
	    while (vitr.hasNext()) {
		v = vitr.next();
		star = v.getEdgeStar();
		for (final HE_Halfedge e : star) {
		    umbrellapoints.put(e._key, e.getEdgeCenter());
		}
		tracker.incrementCounter();
	    }
	    tracker.setDefaultStatus("Splitting edges.", umbrellapoints.size());
	    for (final long e : umbrellapoints.keySet()) {
		selection.parent.splitEdge(e, umbrellapoints.get(e));
		tracker.incrementCounter();
	    }
	} else {
	    List<HE_Halfedge> star;
	    tracker.setDefaultStatus("Enumerating vertex umbrellas.",
		    selection.getNumberOfVertices());
	    while (vitr.hasNext()) {
		v = vitr.next();
		star = v.getHalfedgeStar();
		for (final HE_Halfedge he : star) {
		    umbrellapoints.put(
			    he._key,
			    gf.createInterpolatedPoint(he.getVertex(),
				    he.getEndVertex(), chamfer));
		}
		tracker.incrementCounter();
	    }
	    tracker.setDefaultStatus("Splitting edges.", umbrellapoints.size());
	    for (final long he : umbrellapoints.keySet()) {
		selection.parent.splitEdge(
			selection.parent.getHalfedgeByKey(he),
			umbrellapoints.get(he));
		tracker.incrementCounter();
	    }
	}
	tracker.setDefaultStatus("Splitting faces.",
		selection.getNumberOfVertices());
	vitr = new HE_VertexIterator(selection);
	while (vitr.hasNext()) {
	    v = vitr.next();
	    final HE_VertexHalfedgeOutCirculator vhoc = new HE_VertexHalfedgeOutCirculator(
		    v);
	    HE_Halfedge he;
	    while (vhoc.hasNext()) {
		he = vhoc.next();
		if (he.getFace() != null) {
		    spikes.union(selection.parent.splitFace(he.getFace(), he
			    .getEndVertex(), he.getPrevInVertex()
			    .getEndVertex()));
		}
	    }
	    tracker.incrementCounter();
	    v.getPoint().addMulSelf(distance, v.getVertexNormal());
	}
	tracker.setDefaultStatus("Exiting HEM_Crocodile.");
	return selection.parent;
    }
}
