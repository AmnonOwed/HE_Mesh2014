/*
 * 
 */
package wblut.hemesh;

import gnu.trove.iterator.TLongObjectIterator;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.Iterator;
import java.util.List;
import javolution.util.FastTable;

/**
 * 
 */
public class HEM_CapHoles extends HEM_Modifier {
    
    /**
     * 
     */
    public HEM_CapHoles() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	tracker.setDefaultStatus("Starting HEM_CapHoles.");
	tracker.setDefaultStatus("Uncapping boundary edges.");
	final Iterator<HE_Halfedge> heItr = mesh.heItr();
	HE_Halfedge he;
	final List<HE_Halfedge> remove = new FastTable<HE_Halfedge>();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		he.getVertex().setHalfedge(he.getNextInVertex());
		he.getPair().clearPair();
		he.clearPair();
		remove.add(he);
	    }
	}
	mesh.removeHalfedges(remove);
	tracker.setDefaultStatus("Capping simple planar holes.");
	final List<HE_Face> caps = new FastTable<HE_Face>();
	final List<HE_Halfedge> unpairedEdges = mesh.getUnpairedHalfedges();
	HE_RAS<HE_Halfedge> loopedHalfedges;
	HE_Halfedge start;
	HE_Halfedge hen;
	HE_Face nf;
	HE_RAS<HE_Halfedge> newHalfedges;
	HE_Halfedge phe;
	HE_Halfedge nhe;
	tracker.setDefaultStatus("Finding loops and closing holes.",
		unpairedEdges.size());
	while (unpairedEdges.size() > 0) {
	    loopedHalfedges = new HE_RASTrove<HE_Halfedge>();
	    start = unpairedEdges.get(0);
	    loopedHalfedges.add(start);
	    he = start;
	    hen = start;
	    boolean stuck = false;
	    do {
		for (int i = 0; i < unpairedEdges.size(); i++) {
		    hen = unpairedEdges.get(i);
		    if (hen.getVertex() == he.getNextInFace().getVertex()) {
			loopedHalfedges.add(hen);
			break;
		    }
		}
		if (hen.getVertex() != he.getNextInFace().getVertex()) {
		    stuck = true;
		}
		he = hen;
	    } while ((hen.getNextInFace().getVertex() != start.getVertex())
		    && (!stuck));
	    unpairedEdges.removeAll(loopedHalfedges);
	    nf = new HE_Face();
	    mesh.add(nf);
	    caps.add(nf);
	    newHalfedges = new HE_RASTrove<HE_Halfedge>();
	    for (int i = 0; i < loopedHalfedges.size(); i++) {
		phe = loopedHalfedges.get(i);
		nhe = new HE_Halfedge();
		mesh.add(nhe);
		newHalfedges.add(nhe);
		nhe.setVertex(phe.getNextInFace().getVertex());
		nhe.setPair(phe);
		phe.setPair(nhe);
		nhe.setFace(nf);
		if (nf.getHalfedge() == null) {
		    nf.setHalfedge(nhe);
		}
	    }
	    HE_Mesh.cycleHalfedgesReverse(newHalfedges.getObjects());
	    tracker.incrementCounter(newHalfedges.size());
	}
	tracker.setDefaultStatus("Capped simple, planar holes.");
	tracker.setDefaultStatus("Pairing halfedges.");
	class VertexInfo {
	    FastTable<HE_Halfedge> out;
	    FastTable<HE_Halfedge> in;

	    VertexInfo() {
		out = new FastTable<HE_Halfedge>();
		in = new FastTable<HE_Halfedge>();
	    }
	}
	final TLongObjectMap<VertexInfo> vertexLists = new TLongObjectHashMap<VertexInfo>(
		1024, 0.5f, -1L);
	final List<HE_Halfedge> unpairedHalfedges = mesh.getUnpairedHalfedges();
	HE_Vertex v;
	VertexInfo vi;
	tracker.setDefaultStatus("Classifying unpaired halfedges.",
		unpairedHalfedges.size());
	for (final HE_Halfedge hed : unpairedHalfedges) {
	    v = hed.getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.out.add(hed);
	    v = hed.getNextInFace().getVertex();
	    vi = vertexLists.get(v.key());
	    if (vi == null) {
		vi = new VertexInfo();
		vertexLists.put(v.key(), vi);
	    }
	    vi.in.add(hed);
	    tracker.incrementCounter();
	}
	HE_Halfedge he2;
	tracker.setDefaultStatus("Pairing unpaired halfedges per vertex.",
		vertexLists.size());
	// System.out.println("HE_Mesh : pairing unpaired halfedges per vertex.");
	final TLongObjectIterator<VertexInfo> vitr = vertexLists.iterator();
	VertexInfo vInfo;
	while (vitr.hasNext()) {
	    vitr.advance();
	    vInfo = vitr.value();
	    for (int i = 0; i < vInfo.out.size(); i++) {
		he = vInfo.out.get(i);
		if (he.getPair() == null) {
		    for (int j = 0; j < vInfo.in.size(); j++) {
			he2 = vInfo.in.get(j);
			if ((he2.getPair() == null)
				&& (he.getVertex() == he2.getNextInFace()
					.getVertex())
				&& (he2.getVertex() == he.getNextInFace()
					.getVertex())) {
			    he.setPair(he2);
			    he2.setPair(he);
			    break;
			}
		    }
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Processed unpaired halfedges.");
	tracker.setDefaultStatus("Exiting HEM_CapHoles.");
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
