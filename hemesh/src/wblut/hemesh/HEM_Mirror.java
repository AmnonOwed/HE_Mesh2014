/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import wblut.geom.WB_ClassificationGeometry;
import wblut.geom.WB_Classify;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class HEM_Mirror extends HEM_Modifier {
    
    /**
     * 
     */
    private WB_Plane P;
    
    /**
     * 
     */
    private boolean keepCenter = false;
    
    /**
     * 
     */
    private boolean reverse = false;
    
    /**
     * 
     */
    public HE_Selection cut;
    
    /**
     * 
     */
    private double offset;

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public HEM_Mirror setOffset(final double d) {
	offset = d;
	return this;
    }

    /**
     * 
     */
    public HEM_Mirror() {
	super();
    }

    /**
     * 
     *
     * @param P 
     * @return 
     */
    public HEM_Mirror setPlane(final WB_Plane P) {
	this.P = P;
	return this;
    }

    /**
     * 
     *
     * @param ox 
     * @param oy 
     * @param oz 
     * @param nx 
     * @param ny 
     * @param nz 
     * @return 
     */
    public HEM_Mirror setPlane(final double ox, final double oy,
	    final double oz, final double nx, final double ny, final double nz) {
	P = new WB_Plane(ox, oy, oz, nx, ny, nz);
	return this;
    }

    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEM_Mirror setReverse(final Boolean b) {
	reverse = b;
	return this;
    }

    /**
     * 
     *
     * @param b 
     * @return 
     */
    public HEM_Mirror setKeepCenter(final Boolean b) {
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
	tracker.setDefaultStatus("Starting HEM_Mirror.");
	cut = new HE_Selection(mesh);
	// no plane defined
	if (P == null) {
	    tracker.setDefaultStatus("No mirror plane defined. Exiting HEM_Mirror.");
	    return mesh;
	}
	// empty mesh
	if (mesh.getNumberOfVertices() == 0) {
	    tracker.setDefaultStatus("No vertices in mesh. Exiting HEM_Mirror.");
	    return mesh;
	}
	WB_Plane lP = P.get();
	if (reverse) {
	    lP.flipNormal();
	}
	lP = new WB_Plane(lP.getNormal(), lP.d() + offset);
	HEM_SliceSurface ss;
	ss = new HEM_SliceSurface().setPlane(lP);
	mesh.modify(ss);
	cut = ss.cut;
	final HE_Selection newFaces = new HE_Selection(mesh);
	HE_Face face;
	tracker.setDefaultStatus("Classifying mesh faces.",
		mesh.getNumberOfFaces());
	Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    final WB_ClassificationGeometry cptp = WB_Classify
		    .classifyPolygonToPlane3D(face.toPolygon(), lP);
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
	mesh.replaceFaces(newFaces.getFacesAsArray());
	cut.cleanSelection();
	mesh.cleanUnusedElementsByFace();
	final ArrayList<HE_Face> facesToRemove = new ArrayList<HE_Face>();
	fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    face = fItr.next();
	    if (face.getFaceOrder() < 3) {
		facesToRemove.add(face);
	    }
	}
	mesh.removeFaces(facesToRemove);
	mesh.cleanUnusedElementsByFace();
	mesh.capHalfedges();
	final HE_Mesh mirrormesh = mesh.get();
	tracker.setDefaultStatus("Mirroring vertices.",
		mesh.getNumberOfVertices());
	final List<HE_Vertex> vertices = mirrormesh.getVerticesAsList();
	HE_Vertex v, origv;
	for (int i = 0; i < vertices.size(); i++) {
	    v = vertices.get(i);
	    final WB_Point p = WB_GeometryOp.getClosestPoint3D(v, lP);
	    final WB_Vector dv = v.getPoint().subToVector3D(p);
	    if (dv.getLength3D() <= WB_Epsilon.EPSILON) {
		final List<HE_Halfedge> star = v.getHalfedgeStar();
		origv = mesh.getVertexByIndex(i);
		for (final HE_Halfedge he : star) {
		    he.setVertex(origv);
		}
		mirrormesh.remove(v);
	    } else {
		v.getPoint().addMulSelf(-2, dv);
	    }
	    tracker.incrementCounter();
	}
	mirrormesh.flipAllFaces();
	mesh.uncapBoundaryHalfedges();
	mirrormesh.uncapBoundaryHalfedges();
	mesh.add(mirrormesh);
	mesh.pairHalfedges();
	if (!keepCenter) {
	    mesh.resetCenter();
	}
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
