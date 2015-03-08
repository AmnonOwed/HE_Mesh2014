/*
 * 
 */
package wblut.hemesh;

import gnu.trove.map.TLongLongMap;
import gnu.trove.map.hash.TLongLongHashMap;
import java.util.Iterator;

/**
 * Axis Aligned Box.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_Copy extends HEC_Creator {
    
    /**
     * 
     */
    HE_MeshStructure source;
    
    /**
     * 
     */
    HE_Mesh mesh;

    /**
     * 
     */
    public HEC_Copy() {
	super();
	override = true;
    }

    /**
     * 
     *
     * @param source 
     */
    public HEC_Copy(final HE_MeshStructure source) {
	super();
	setMesh(source);
	override = true;
    }

    /**
     * 
     *
     * @param source 
     * @return 
     */
    public HEC_Copy setMesh(final HE_MeshStructure source) {
	this.source = source;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	tracker.setDefaultStatus("Starting HEC_Copy.");
	final HE_Mesh result = new HE_Mesh();
	result.copyProperties(source);
	if (source == null) {
	    tracker.setDefaultStatus("No source mesh. Exiting HEC_Copy.");
	    return result;
	}
	if (source instanceof HE_Selection) {
	    mesh = ((HE_Selection) source).parent;
	} else if (source instanceof HE_Mesh) {
	    mesh = (HE_Mesh) source;
	}
	final TLongLongMap vertexCorrelation = new TLongLongHashMap(10, 0.5f,
		-1L, -1L);
	final TLongLongMap faceCorrelation = new TLongLongHashMap(10, 0.5f,
		-1L, -1L);
	final TLongLongMap halfedgeCorrelation = new TLongLongHashMap(10, 0.5f,
		-1L, -1L);
	HE_Vertex rv;
	HE_Vertex v;
	tracker.setDefaultStatus("Creating vertices.",
		mesh.getNumberOfVertices());
	final Iterator<HE_Vertex> vItr = mesh.vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    rv = new HE_Vertex(v);
	    result.add(rv);
	    rv.copyProperties(v);
	    vertexCorrelation.put(v.key(), rv.key());
	    tracker.incrementCounter();
	}
	HE_Face rf;
	HE_Face f;
	tracker.setDefaultStatus("Creating faces.", mesh.getNumberOfFaces());
	if (source instanceof HE_Selection) {
	    final Iterator<HE_Face> fItr = mesh.fItr();
	    while (fItr.hasNext()) {
		f = fItr.next();
		rf = new HE_Face();
		result.add(rf);
		rf.copyProperties(f);
		if (((HE_Selection) source).contains(f)) {
		    rf.setInternalLabel(0);
		} else {
		    rf.setInternalLabel(-255);
		}
		faceCorrelation.put(f.key(), rf.key());
		tracker.incrementCounter();
	    }
	} else {
	    final Iterator<HE_Face> fItr = mesh.fItr();
	    while (fItr.hasNext()) {
		f = fItr.next();
		rf = new HE_Face();
		result.add(rf);
		rf.copyProperties(f);
		faceCorrelation.put(f.key(), rf.key());
		tracker.incrementCounter();
	    }
	}
	HE_Halfedge rhe;
	HE_Halfedge he;
	tracker.setDefaultStatus("Creating halfedges.",
		mesh.getNumberOfHalfedges());
	final Iterator<HE_Halfedge> heItr = mesh.heItr();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    rhe = new HE_Halfedge();
	    result.add(rhe);
	    rhe.copyProperties(he);
	    halfedgeCorrelation.put(he.key(), rhe.key());
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Setting vertex properties.",
		mesh.getNumberOfVertices());
	HE_Vertex sv;
	HE_Vertex tv;
	final Iterator<HE_Vertex> svItr = mesh.vItr();
	final Iterator<HE_Vertex> tvItr = result.vItr();
	Long key;
	while (svItr.hasNext()) {
	    sv = svItr.next();
	    tv = tvItr.next();
	    tv.set(sv);
	    if (sv.getHalfedge() != null) {
		key = halfedgeCorrelation.get(sv.getHalfedge().key());
		if (key >= 0) {
		    tv.setHalfedge(result.getHalfedgeByKey(key));
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Setting face properties.",
		mesh.getNumberOfFaces());
	HE_Face sf;
	HE_Face tf;
	final Iterator<HE_Face> sfItr = mesh.fItr();
	final Iterator<HE_Face> tfItr = result.fItr();
	while (sfItr.hasNext()) {
	    sf = sfItr.next();
	    tf = tfItr.next();
	    if (sf.getHalfedge() != null) {
		key = halfedgeCorrelation.get(sf.getHalfedge().key());
		if (key >= 0) {
		    tf.setHalfedge(result.getHalfedgeByKey(key));
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Setting halfedge properties.",
		mesh.getNumberOfHalfedges());
	HE_Halfedge she;
	HE_Halfedge the;
	final Iterator<HE_Halfedge> sheItr = mesh.heItr();
	final Iterator<HE_Halfedge> theItr = result.heItr();
	while (sheItr.hasNext()) {
	    she = sheItr.next();
	    the = theItr.next();
	    if (she.getPair() != null) {
		key = halfedgeCorrelation.get(she.getPair().key());
		if (key >= 0) {
		    the.setPair(result.getHalfedgeByKey(key));
		    result.getHalfedgeByKey(key).setPair(the);
		}
	    }
	    if (she.getNextInFace() != null) {
		key = halfedgeCorrelation.get(she.getNextInFace().key());
		if (key >= 0) {
		    the.setNext(result.getHalfedgeByKey(key));
		    result.getHalfedgeByKey(key).setPrev(the);
		}
	    }
	    if (she.getVertex() != null) {
		key = vertexCorrelation.get(she.getVertex().key());
		if (key >= 0) {
		    the.setVertex(result.getVertexByKey(key));
		}
	    }
	    if (she.getFace() != null) {
		key = faceCorrelation.get(she.getFace().key());
		if (key >= 0) {
		    the.setFace(result.getFaceByKey(key));
		}
	    }
	    tracker.incrementCounter();
	}
	if (source instanceof HE_Selection) {
	    final HE_Selection sel = result.selectFacesWithInternalLabel(-255);
	    result.removeFaces(sel.getFaces());
	    result.cleanUnusedElementsByFace();
	    result.capHalfedges();
	}
	tracker.setDefaultStatus("Exiting HEC_Copy.");
	return result;
    }
}
