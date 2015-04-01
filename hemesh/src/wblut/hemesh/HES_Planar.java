/*
 *
 */
package wblut.hemesh;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import javolution.util.FastTable;
import wblut.geom.WB_Point;
import wblut.math.WB_MTRandom;

/**
 * Planar subdivision of a mesh. Divides all edges in half. Non-triangular faces
 * are divided in new faces connecting each vertex with the two adjacent mid
 * edge vertices and the face center. Triangular faces are divided in four new
 * triangular faces by connecting the mid edge points. Faces are tris or quads.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HES_Planar extends HES_Subdividor {
    /** Random subdivision. */
    private boolean random;
    /** Triangular division of triangles?. */
    private boolean keepTriangles;
    /** Random range. */
    private double range;
    /** The random gen. */
    private final WB_MTRandom randomGen;

    /**
     * Instantiates a new HES_Planar.
     */
    public HES_Planar() {
	super();
	random = false;
	range = 1;
	keepTriangles = true;
	randomGen = new WB_MTRandom();
    }

    /**
     * Set random mode.
     *
     * @param b
     *            true, false
     * @return self
     */
    public HES_Planar setRandom(final boolean b) {
	random = b;
	return this;
    }

    /**
     * Set random seed.
     *
     * @param seed
     *            seed
     * @return self
     */
    public HES_Planar setSeed(final long seed) {
	randomGen.setSeed(seed);
	return this;
    }

    /**
     * Set preservation of triangular faces.
     *
     * @param b
     *            true, false
     * @return self
     */
    public HES_Planar setKeepTriangles(final boolean b) {
	keepTriangles = b;
	return this;
    }

    /**
     * Set range of random variation.
     *
     * @param r
     *            range (0..1)
     * @return self
     */
    public HES_Planar setRange(final double r) {
	range = r;
	if (range > 1) {
	    range = 1;
	}
	if (range < 0) {
	    range = 0;
	}
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	final TLongObjectMap<HE_Vertex> faceVertices = new TLongObjectHashMap<HE_Vertex>(
		1024, 0.5f, -1L);
	HE_Face face;
	Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    if (!random) {
		final HE_Vertex fv = new HE_Vertex(face.getFaceCenter());
		double u = 0;
		double v = 0;
		double w = 0;
		HE_Halfedge he = face.getHalfedge();
		boolean hasTexture = true;
		do {
		    if (!he.getVertex().hasTexture(face)) {
			hasTexture = false;
			break;
		    }
		    u += he.getVertex().getUVW(face).ud();
		    v += he.getVertex().getUVW(face).vd();
		    w += he.getVertex().getUVW(face).wd();
		    he = he.getNextInFace();
		} while (he != face.getHalfedge());
		if (hasTexture) {
		    final double ifo = 1.0 / face.getFaceOrder();
		    fv.setUVW(u * ifo, v * ifo, w * ifo);
		}
		faceVertices.put(face.key(), fv);
	    } else {
		HE_Halfedge he = face.getHalfedge();
		HE_Vertex fv = new HE_Vertex();
		int trial = 0;
		do {
		    double c = 0;
		    fv = new HE_Vertex();
		    do {
			final WB_Point tmp = new WB_Point(he.getVertex());
			final double t = 0.5 + ((randomGen.nextDouble() - 0.5) * range);
			tmp.mulSelf(t);
			fv.getPoint().addSelf(tmp);
			c += t;
			he = he.getNextInFace();
		    } while (he != face.getHalfedge());
		    fv.getPoint().divSelf(c);
		    trial++;
		} while ((!HE_Mesh.pointIsStrictlyInFace(fv, face))
			&& (trial < 10));
		if (trial == 10) {
		    fv.set(face.getFaceCenter());
		}
		double u = 0;
		double v = 0;
		double w = 0;
		he = face.getHalfedge();
		boolean hasTexture = true;
		do {
		    if (!he.getVertex().hasTexture(face)) {
			hasTexture = false;
			break;
		    }
		    u += he.getVertex().getUVW(face).ud();
		    v += he.getVertex().getUVW(face).vd();
		    w += he.getVertex().getUVW(face).wd();
		    he = he.getNextInFace();
		} while (he != face.getHalfedge());
		if (hasTexture) {
		    final double ifo = 1.0 / face.getFaceOrder();
		    fv.setUVW(u * ifo, v * ifo, w * ifo);
		}
		faceVertices.put(face.key(), fv);
	    }
	}
	final int n = mesh.getNumberOfEdges();
	final HE_Selection orig = new HE_Selection(mesh);
	orig.addVertices(mesh.getVerticesAsList());
	if (random) {
	    final HE_Halfedge[] origE = mesh.getEdgesAsArray();
	    for (int i = 0; i < n; i++) {
		final double f = 0.5 + ((randomGen.nextDouble() - 0.5) * range);
		mesh.splitEdge(origE[i], f);
	    }
	} else {
	    mesh.splitEdges();
	}
	final ArrayList<HE_Face> newFaces = new ArrayList<HE_Face>();
	FastTable<HE_Halfedge> unpairedhes = new FastTable<HE_Halfedge>();
	fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    // loop
	    HE_Halfedge startHE = face.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    if ((face.getFaceOrder() == 6) && keepTriangles) {
		HE_Halfedge origHE1 = startHE;
		final HE_Face centerFace = new HE_Face();
		newFaces.add(centerFace);
		centerFace.copyProperties(face);
		final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
		do {
		    final HE_Face newFace = new HE_Face();
		    newFaces.add(newFace);
		    newFace.copyProperties(face);
		    final HE_Halfedge origHE2 = origHE1.getNextInFace();
		    final HE_Halfedge origHE3 = origHE2.getNextInFace();
		    final HE_Halfedge newHE = new HE_Halfedge();
		    final HE_Halfedge newHEp = new HE_Halfedge();
		    mesh.add(newHE);
		    mesh.add(newHEp);
		    faceHalfedges.add(newHEp);
		    if (origHE3.getVertex().hasHalfedgeTexture(face)) {
			newHE.setUVW(origHE3.getVertex().getHalfedgeUVW(face));
		    }
		    newFace.setHalfedge(origHE1);
		    origHE2.setNext(newHE);
		    newHE.setNext(origHE1);
		    newHE.setVertex(origHE3.getVertex());
		    newHE.setFace(newFace);
		    origHE1.setFace(newFace);
		    origHE2.setFace(newFace);
		    newHEp.setVertex(origHE1.getVertex());
		    newHE.setPair(newHEp);
		    newHEp.setPair(newHE);
		    newHEp.setFace(centerFace);
		    centerFace.setHalfedge(newHEp);
		    origHE1 = origHE3;
		} while (origHE1 != startHE);
		HE_Mesh.cycleHalfedges(faceHalfedges);
		HE_Halfedge cfhe = centerFace.getHalfedge();
		do {
		    if (cfhe.getPair().getNextInFace().hasTexture()) {
			cfhe.setUVW(cfhe.getPair().getNextInFace().getUVW());
		    }
		    cfhe = cfhe.getNextInFace();
		} while (cfhe != centerFace.getHalfedge());
	    } else {
		HE_Halfedge origHE1 = startHE;
		unpairedhes = new FastTable<HE_Halfedge>();
		do {
		    final HE_Face newFace = new HE_Face();
		    newFaces.add(newFace);
		    newFace.copyProperties(face);
		    final HE_Halfedge origHE2 = origHE1.getNextInFace();
		    final HE_Halfedge origHE3 = origHE2.getNextInFace();
		    final HE_Halfedge newHE1 = new HE_Halfedge();
		    final HE_Halfedge newHE2 = new HE_Halfedge();
		    mesh.add(newHE1);
		    mesh.add(newHE2);
		    if (origHE3.getVertex().hasHalfedgeTexture(face)) {
			newHE1.setUVW(origHE3.getVertex().getHalfedgeUVW(face));
		    }
		    newFace.setHalfedge(origHE1);
		    origHE2.setNext(newHE1);
		    newHE1.setNext(newHE2);
		    newHE2.setNext(origHE1);
		    newHE1.setVertex(origHE3.getVertex());
		    final HE_Vertex fv = faceVertices.get(origHE1.getFace()
			    .key());
		    newHE2.setVertex(fv);
		    if (fv.getHalfedge() == null) {
			fv.setHalfedge(newHE2);
		    }
		    if (!mesh.contains(fv)) {
			mesh.add(fv);
		    }
		    newHE1.setFace(newFace);
		    newHE2.setFace(newFace);
		    origHE1.setFace(newFace);
		    origHE2.setFace(newFace);
		    origHE1 = origHE3;
		    unpairedhes.add(newHE1);
		    unpairedhes.add(newHE2);
		} while (origHE1 != startHE);
		mesh.pairHalfedges(unpairedhes);
	    }
	    face.setInternalLabel(0);
	}// end of face loop
	mesh.replaceFaces(newFaces);
	return mesh;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * wblut.hemesh.subdividors.HEB_Subdividor#subdivideSelected(wblut.hemesh
     * .HE_Mesh, wblut.hemesh.HE_Selection)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	FastTable<HE_Halfedge> unpairedhes = new FastTable<HE_Halfedge>();
	selection.cleanSelection();
	if (selection.getNumberOfFaces() == 0) {
	    return selection.parent;
	}
	final TLongObjectMap<HE_Vertex> faceVertices = new TLongObjectHashMap<HE_Vertex>(
		1024, 0.5f, -1L);
	HE_Face face;
	Iterator<HE_Face> fItr = selection.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    if (!random) {
		final HE_Vertex fv = new HE_Vertex(face.getFaceCenter());
		double u = 0;
		double v = 0;
		double w = 0;
		HE_Halfedge he = face.getHalfedge();
		boolean hasTexture = true;
		do {
		    if (!he.getVertex().hasTexture(face)) {
			hasTexture = false;
			break;
		    }
		    u += he.getVertex().getUVW(face).ud();
		    v += he.getVertex().getUVW(face).vd();
		    w += he.getVertex().getUVW(face).wd();
		    he = he.getNextInFace();
		} while (he != face.getHalfedge());
		if (hasTexture) {
		    final double ifo = 1.0 / face.getFaceOrder();
		    fv.setUVW(u * ifo, v * ifo, w * ifo);
		}
		faceVertices.put(face.key(), fv);
	    } else {
		HE_Halfedge he = face.getHalfedge();
		HE_Vertex fv = new HE_Vertex();
		int trial = 0;
		do {
		    double c = 0;
		    fv = new HE_Vertex();
		    do {
			final WB_Point tmp = new WB_Point(he.getVertex());
			final double t = 0.5 + ((randomGen.nextDouble() - 0.5) * range);
			tmp.mulSelf(t);
			fv.getPoint().addSelf(tmp);
			c += t;
			he = he.getNextInFace();
		    } while (he != face.getHalfedge());
		    fv.getPoint().divSelf(c);
		    trial++;
		} while ((!HE_Mesh.pointIsStrictlyInFace(fv, face))
			&& (trial < 10));
		if (trial == 10) {
		    fv.set(face.getFaceCenter());
		}
		double u = 0;
		double v = 0;
		double w = 0;
		he = face.getHalfedge();
		boolean hasTexture = true;
		do {
		    if (!he.getVertex().hasTexture(face)) {
			hasTexture = false;
			break;
		    }
		    u += he.getVertex().getUVW(face).ud();
		    v += he.getVertex().getUVW(face).vd();
		    w += he.getVertex().getUVW(face).wd();
		    he = he.getNextInFace();
		} while (he != face.getHalfedge());
		if (hasTexture) {
		    final double ifo = 1.0 / face.getFaceOrder();
		    fv.setUVW(u * ifo, v * ifo, w * ifo);
		}
		faceVertices.put(face.key(), fv);
	    }
	}
	selection.collectEdgesByFace();
	final HE_Selection newVertices = new HE_Selection(selection.parent);
	if (random) {
	    final HE_Halfedge[] edges = selection.getEdgesAsArray();
	    final int ne = selection.getNumberOfEdges();
	    for (int i = 0; i < ne; i++) {
		HE_Vertex v;
		final double f = 0.5 + ((randomGen.nextDouble() - 0.5) * range);
		v = selection.parent.splitEdge(edges[i], f).vItr().next();
		if (v != null) {
		    newVertices.add(v);
		}
	    }
	} else {
	    newVertices.add(selection.parent.splitEdges(selection));
	}
	final ArrayList<HE_Face> newFaces = new ArrayList<HE_Face>();
	fItr = selection.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    HE_Halfedge startHE = face.getHalfedge();
	    while (!newVertices.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    if ((face.getFaceOrder() == 6) && keepTriangles) {
		HE_Halfedge origHE1 = startHE;
		final HE_Face centerFace = new HE_Face();
		centerFace.copyProperties(face);
		newFaces.add(centerFace);
		final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
		do {
		    final HE_Face newFace = new HE_Face();
		    newFaces.add(newFace);
		    newFace.copyProperties(face);
		    final HE_Halfedge origHE2 = origHE1.getNextInFace();
		    final HE_Halfedge origHE3 = origHE2.getNextInFace();
		    final HE_Halfedge newHE = new HE_Halfedge();
		    final HE_Halfedge newHEp = new HE_Halfedge();
		    selection.parent.add(newHE);
		    selection.parent.add(newHEp);
		    faceHalfedges.add(newHEp);
		    if (origHE3.getVertex().hasHalfedgeTexture(face)) {
			newHE.setUVW(origHE3.getVertex().getHalfedgeUVW(face));
		    }
		    newFace.setHalfedge(origHE1);
		    origHE2.setNext(newHE);
		    newHE.setNext(origHE1);
		    newHE.setVertex(origHE3.getVertex());
		    newHE.setFace(newFace);
		    origHE1.setFace(newFace);
		    origHE2.setFace(newFace);
		    newHEp.setVertex(origHE1.getVertex());
		    newHE.setPair(newHEp);
		    newHEp.setPair(newHE);
		    newHEp.setFace(centerFace);
		    centerFace.setHalfedge(newHEp);
		    origHE1 = origHE3;
		} while (origHE1 != startHE);
		HE_Mesh.cycleHalfedges(faceHalfedges);
		HE_Halfedge cfhe = centerFace.getHalfedge();
		do {
		    if (cfhe.getPair().getNextInFace().hasTexture()) {
			cfhe.setUVW(cfhe.getPair().getNextInFace().getUVW());
		    }
		    cfhe = cfhe.getNextInFace();
		} while (cfhe != centerFace.getHalfedge());
	    } else {
		HE_Halfedge origHE1 = startHE;
		unpairedhes = new FastTable<HE_Halfedge>();
		do {
		    final HE_Face newFace = new HE_Face();
		    newFaces.add(newFace);
		    newFace.copyProperties(face);
		    newFace.setHalfedge(origHE1);
		    final HE_Halfedge origHE2 = origHE1.getNextInFace();
		    final HE_Halfedge origHE3 = origHE2.getNextInFace();
		    final HE_Halfedge newHE1 = new HE_Halfedge();
		    final HE_Halfedge newHE2 = new HE_Halfedge();
		    selection.parent.add(newHE1);
		    selection.parent.add(newHE2);
		    if (origHE3.getVertex().hasHalfedgeTexture(face)) {
			newHE1.setUVW(origHE3.getVertex().getHalfedgeUVW(face));
		    }
		    origHE2.setNext(newHE1);
		    newHE1.setNext(newHE2);
		    newHE2.setNext(origHE1);
		    newHE1.setVertex(origHE3.getVertex());
		    final HE_Vertex fv = faceVertices.get(origHE1.getFace()
			    .key());
		    newHE2.setVertex(fv);
		    if (fv.getHalfedge() == null) {
			fv.setHalfedge(newHE2);
		    }
		    if (!selection.parent.contains(fv)) {
			selection.parent.add(fv);
		    }
		    newHE1.setFace(newFace);
		    newHE2.setFace(newFace);
		    origHE1.setFace(newFace);
		    origHE2.setFace(newFace);
		    origHE1 = origHE3;
		    unpairedhes.add(newHE1);
		    unpairedhes.add(newHE2);
		} while (origHE1 != startHE);
		selection.parent.pairHalfedges(unpairedhes);
	    }
	}// end of face loop
	selection.parent.pairHalfedges();
	selection.parent.removeFaces(selection.getFacesAsArray());
	selection.parent.addFaces(newFaces);
	return selection.parent;
    }
}
