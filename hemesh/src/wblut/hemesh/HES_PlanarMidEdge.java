/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 
 */
public class HES_PlanarMidEdge extends HES_Subdividor {
    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	mesh.splitEdges();
	final ArrayList<HE_Face> newFaces = new ArrayList<HE_Face>();
	HE_Face face;
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    final HE_Halfedge startHE = face.getHalfedge().getNextInFace();
	    HE_Halfedge origHE1 = startHE;
	    final HE_Face centerFace = new HE_Face();
	    newFaces.add(centerFace);
	    final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
	    do {
		final HE_Face newFace = new HE_Face();
		newFaces.add(newFace);
		newFace.setHalfedge(origHE1);
		final HE_Halfedge origHE2 = origHE1.getNextInFace();
		final HE_Halfedge origHE3 = origHE2.getNextInFace();
		final HE_Halfedge newHE = new HE_Halfedge();
		final HE_Halfedge newHEp = new HE_Halfedge();
		mesh.add(newHE);
		mesh.add(newHEp);
		faceHalfedges.add(newHEp);
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
	}
	mesh.pairHalfedges();
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
	selection.parent.splitEdges(selection);
	final ArrayList<HE_Face> newFaces = new ArrayList<HE_Face>();
	HE_Face face;
	final Iterator<HE_Face> fItr = selection.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    final HE_Halfedge startHE = face.getHalfedge().getNextInFace();
	    HE_Halfedge origHE1 = startHE;
	    final HE_Face centerFace = new HE_Face();
	    newFaces.add(centerFace);
	    final ArrayList<HE_Halfedge> faceHalfedges = new ArrayList<HE_Halfedge>();
	    do {
		final HE_Face newFace = new HE_Face();
		newFaces.add(newFace);
		newFace.setHalfedge(origHE1);
		final HE_Halfedge origHE2 = origHE1.getNextInFace();
		final HE_Halfedge origHE3 = origHE2.getNextInFace();
		final HE_Halfedge newHE = new HE_Halfedge();
		final HE_Halfedge newHEp = new HE_Halfedge();
		selection.parent.add(newHE);
		selection.parent.add(newHEp);
		faceHalfedges.add(newHEp);
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
	}
	selection.parent.pairHalfedges();
	selection.parent.removeFaces(selection.getFacesAsArray());
	selection.parent.addFaces(newFaces);
	return null;
    }
}
