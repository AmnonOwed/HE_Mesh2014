/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;
import java.util.List;
import wblut.geom.WB_ClassificationGeometry;
import wblut.geom.WB_Classify;
import wblut.geom.WB_Plane;

/**
 * Planar cut of a mesh. Faces on positive side of cut plane are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_Slice extends HEM_Modifier {
    /** Cut plane. */
    private WB_Plane P;
    /**
     * HEM_slice keeps the part of the mesh on the positive side of the plane.
     * Reverse planar cut.
     */
    private boolean reverse = false;
    /**
     * Cap holes?. Capping holes does not work properly with
     * self-intersection...
     */
    private boolean capHoles = true;
    /** The simple cap. */
    private boolean simpleCap = true;
    /** Keep center of cut mesh?. */
    private boolean keepCenter = false;
    /** Store cut faces. */
    public HE_Selection cut;
    /** Store cap faces. */
    public HE_Selection cap;
    /** The offset. */
    private double offset;
    
    /**
     * 
     */
    HEM_SliceSurface ss;

    /**
     * Set offset.
     *
     * @param d
     *            offset
     * @return self
     */
    public HEM_Slice setOffset(final double d) {
	offset = d;
	return this;
    }

    /**
     * Instantiates a new HEM_Slice.
     */
    public HEM_Slice() {
	super();
    }

    /**
     * Set cut plane.
     *
     * @param P
     *            cut plane
     * @return self
     */
    public HEM_Slice setPlane(final WB_Plane P) {
	this.P = P;
	return this;
    }

    /**
     * Sets the plane.
     *
     * @param ox
     *            the ox
     * @param oy
     *            the oy
     * @param oz
     *            the oz
     * @param nx
     *            the nx
     * @param ny
     *            the ny
     * @param nz
     *            the nz
     * @return the hE m_ slice
     */
    public HEM_Slice setPlane(final double ox, final double oy,
	    final double oz, final double nx, final double ny, final double nz) {
	P = new WB_Plane(ox, oy, oz, nx, ny, nz);
	return this;
    }

    /**
     * Set reverse option.
     *
     * @param b
     *            true, false
     * @return self
     */
    public HEM_Slice setReverse(final Boolean b) {
	reverse = b;
	return this;
    }

    /**
     * Set option to cap holes.
     *
     * @param b
     *            true, false;
     * @return self
     */
    public HEM_Slice setCap(final Boolean b) {
	capHoles = b;
	return this;
    }

    /**
     * Sets the simple cap.
     *
     * @param b
     *            the b
     * @return the hE m_ slice
     */
    public HEM_Slice setSimpleCap(final Boolean b) {
	simpleCap = b;
	return this;
    }

    /**
     * Set option to reset mesh center.
     *
     * @param b
     *            true, false;
     * @return self
     */
    public HEM_Slice setKeepCenter(final Boolean b) {
	keepCenter = b;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	tracker.setDefaultStatus("Starting HEM_Slice.");
	cut = new HE_Selection(mesh);
	cap = new HE_Selection(mesh);
	// no plane defined
	if (P == null) {
	    tracker.setDefaultStatus("No cutplane defined. Exiting HEM_Slice.");
	    return mesh;
	}
	// empty mesh
	if (mesh.getNumberOfVertices() == 0) {
	    tracker.setDefaultStatus("Empty mesh. Exiting HEM_Slice.");
	    return mesh;
	}
	WB_Plane lP = P.get();
	if (reverse) {
	    lP.flipNormal();
	}
	lP = new WB_Plane(lP.getNormal(), lP.d() + offset);
	ss = new HEM_SliceSurface().setPlane(lP);
	mesh.modify(ss);
	cut = ss.cut;
	final HE_Selection newFaces = new HE_Selection(mesh);
	HE_Face face;
	tracker.setDefaultStatus("Classifying faces.", mesh.getNumberOfFaces());
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    final WB_ClassificationGeometry cptp = WB_Classify
		    .classifyPointToPlane3D(face.getFaceCenter(), lP);
	    if ((cptp == WB_ClassificationGeometry.FRONT)
		    || (cptp == WB_ClassificationGeometry.ON)) {
		newFaces.add(face);
	    } else {
		if (cut.contains(face)) {
		    cut.remove(face);
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Removing unwanted faces.");
	mesh.replaceFaces(newFaces.getFacesAsArray());
	cut.cleanSelection();
	mesh.cleanUnusedElementsByFace();
	if (capHoles) {
	    tracker.setDefaultStatus("Capping holes.");
	    if (simpleCap) {
		cap.addFaces(mesh.capHoles());
	    } else {
		final List<HE_Path> cutpaths = ss.getPaths();
		tracker.setDefaultStatus("Triangulating cut paths.");
		final long[][] triKeys = HET_PlanarPathTriangulator
			.getTriangleKeys(cutpaths, lP);
		HE_Face tri;
		HE_Vertex v0, v1, v2;
		HE_Halfedge he0, he1, he2;
		for (int i = 0; i < triKeys.length; i++) {
		    tri = new HE_Face();
		    v0 = mesh.getVertexByKey(triKeys[i][0]);
		    v1 = mesh.getVertexByKey(triKeys[i][1]);
		    v2 = mesh.getVertexByKey(triKeys[i][2]);
		    he0 = new HE_Halfedge();
		    he1 = new HE_Halfedge();
		    he2 = new HE_Halfedge();
		    tri.setHalfedge(he0);
		    he0.setVertex(v0);
		    he1.setVertex(v1);
		    he2.setVertex(v2);
		    he0.setNext(he1);
		    he1.setNext(he2);
		    he2.setNext(he0);
		    he0.setPrev(he2);
		    he1.setPrev(he0);
		    he2.setPrev(he1);
		    he0.setFace(tri);
		    he1.setFace(tri);
		    he2.setFace(tri);
		    cap.add(tri);
		    mesh.add(tri);
		    mesh.add(he0);
		    mesh.add(he1);
		    mesh.add(he2);
		}
	    }
	}
	mesh.pairHalfedges();
	mesh.capHalfedges();
	if (!keepCenter) {
	    mesh.resetCenter();
	}
	// mesh.triangulateConcaveFaces();
	tracker.setDefaultStatus("Ending HEM_Slice.");
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
	return apply(selection.parent);
    }
}
