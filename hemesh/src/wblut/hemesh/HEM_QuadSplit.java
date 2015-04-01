/*
 *
 */
package wblut.hemesh;

import java.util.Iterator;
import wblut.geom.WB_Point;

/**
 *
 */
public class HEM_QuadSplit extends HEM_Modifier {
    /**
     *
     */
    private HE_Selection selectionOut;
    /**
     *
     */
    private double d;

    /**
     *
     */
    public HEM_QuadSplit() {
	super();
	d = 0;
    }

    /**
     *
     *
     * @param d
     * @return
     */
    public HEM_QuadSplit setOffset(final double d) {
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
	tracker.setDefaultStatus("Starting HEM_QuadSplit.");
	selectionOut = new HE_Selection(mesh);
	final int n = mesh.getNumberOfFaces();
	final WB_Point[] faceCenters = new WB_Point[n];
	final int[] faceOrders = new int[n];
	HE_Face f;
	int i = 0;
	tracker.setDefaultStatus("Getting face centers.", n);
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    f = fItr.next();
	    faceCenters[i] = f.getFaceCenter().addMul(d, f.getFaceNormal());
	    faceOrders[i] = f.getFaceOrder();
	    i++;
	    tracker.incrementCounter();
	}
	final HE_Selection orig = mesh.selectAllFaces();
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(mesh.splitEdges().getVerticesAsArray());
	final HE_Face[] faces = mesh.getFacesAsArray();
	HE_Vertex vi = new HE_Vertex();
	tracker.setDefaultStatus("Splitting faces into quads.", n);
	for (i = 0; i < n; i++) {
	    f = faces[i];
	    vi = new HE_Vertex(faceCenters[i]);
	    vi.setInternalLabel(2);
	    double u = 0;
	    double v = 0;
	    double w = 0;
	    HE_Halfedge he = f.getHalfedge();
	    boolean hasTexture = true;
	    do {
		if (!he.getVertex().hasTexture(f)) {
		    hasTexture = false;
		    break;
		}
		u += he.getVertex().getUVW(f).ud();
		v += he.getVertex().getUVW(f).vd();
		w += he.getVertex().getUVW(f).wd();
		he = he.getNextInFace();
	    } while (he != f.getHalfedge());
	    if (hasTexture) {
		final double ifo = 1.0 / f.getFaceOrder();
		vi.setUVW(u * ifo, v * ifo, w * ifo);
	    }
	    mesh.add(vi);
	    selectionOut.add(vi);
	    HE_Halfedge startHE = f.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    he = startHE;
	    final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
	    int c = 0;
	    do {
		HE_Face fc;
		if (c == 0) {
		    fc = f;
		} else {
		    fc = new HE_Face();
		    fc.copyProperties(f);
		    mesh.add(fc);
		}
		he0[c] = he;
		he.setFace(fc);
		fc.setHalfedge(he);
		he1[c] = he.getNextInFace();
		he2[c] = new HE_Halfedge();
		he3[c] = new HE_Halfedge();
		mesh.add(he2[c]);
		mesh.add(he3[c]);
		he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
		if (he2[c].getVertex().hasHalfedgeTexture(f)) {
		    he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
		}
		he3[c].setVertex(vi);
		he2[c].setNext(he3[c]);
		he3[c].setNext(he);
		he1[c].setFace(fc);
		he2[c].setFace(fc);
		he3[c].setFace(fc);
		c++;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    vi.setHalfedge(he3[0]);
	    for (int j = 0; j < c; j++) {
		he1[j].setNext(he2[j]);
	    }
	    tracker.incrementCounter();
	}
	mesh.pairHalfedges();
	tracker.setDefaultStatus("Exiting HEM_QuadSplit.");
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection sel) {
	tracker.setDefaultStatus("Starting HEM_QuadSplit.");
	selectionOut = new HE_Selection(sel.parent);
	final int n = sel.getNumberOfFaces();
	final WB_Point[] faceCenters = new WB_Point[n];
	final int[] faceOrders = new int[n];
	HE_Face face;
	final Iterator<HE_Face> fItr = sel.fItr();
	int i = 0;
	tracker.setDefaultStatus("Getting face centers.", n);
	while (fItr.hasNext()) {
	    face = fItr.next();
	    faceCenters[i] = face.getFaceCenter().addMul(d,
		    face.getFaceNormal());
	    faceOrders[i] = face.getFaceOrder();
	    i++;
	    tracker.incrementCounter();
	}
	final HE_Selection orig = new HE_Selection(sel.parent);
	orig.addFaces(sel.getFacesAsArray());
	orig.collectVertices();
	orig.collectEdgesByFace();
	selectionOut.addVertices(sel.parent.splitEdges(orig)
		.getVerticesAsArray());
	final HE_Face[] faces = sel.getFacesAsArray();
	tracker.setDefaultStatus("Splitting faces into quads.", n);
	for (i = 0; i < n; i++) {
	    face = faces[i];
	    final HE_Vertex vi = new HE_Vertex(faceCenters[i]);
	    sel.parent.add(vi);
	    vi.setInternalLabel(2);
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
		vi.setUVW(u * ifo, v * ifo, w * ifo);
	    }
	    selectionOut.add(vi);
	    HE_Halfedge startHE = face.getHalfedge();
	    while (orig.contains(startHE.getVertex())) {
		startHE = startHE.getNextInFace();
	    }
	    he = startHE;
	    final HE_Halfedge[] he0 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he1 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he2 = new HE_Halfedge[faceOrders[i]];
	    final HE_Halfedge[] he3 = new HE_Halfedge[faceOrders[i]];
	    int c = 0;
	    do {
		HE_Face f;
		if (c == 0) {
		    f = face;
		} else {
		    f = new HE_Face();
		    sel.parent.add(f);
		    f.copyProperties(face);
		    sel.add(f);
		}
		he0[c] = he;
		he.setFace(f);
		f.setHalfedge(he);
		he1[c] = he.getNextInFace();
		he2[c] = new HE_Halfedge();
		he3[c] = new HE_Halfedge();
		sel.parent.add(he2[c]);
		sel.parent.add(he3[c]);
		he2[c].setVertex(he.getNextInFace().getNextInFace().getVertex());
		if (he2[c].getVertex().hasHalfedgeTexture(f)) {
		    he2[c].setUVW(he2[c].getVertex().getHalfedgeUVW(f));
		}
		he3[c].setVertex(vi);
		he2[c].setNext(he3[c]);
		he3[c].setNext(he);
		he1[c].setFace(f);
		he2[c].setFace(f);
		he3[c].setFace(f);
		c++;
		he = he.getNextInFace().getNextInFace();
	    } while (he != startHE);
	    vi.setHalfedge(he3[0]);
	    for (int j = 0; j < c; j++) {
		he1[j].setNext(he2[j]);
	    }
	    tracker.incrementCounter();
	}
	sel.parent.pairHalfedges();
	tracker.setDefaultStatus("Exiting HEM_QuadSplit.");
	return sel.parent;
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection getSplitFaces() {
	return this.selectionOut;
    }
}
