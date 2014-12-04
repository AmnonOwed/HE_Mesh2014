package wblut.hemesh;

import java.util.List;

public class HEM_Triangulate extends HEM_Modifier {
    public HE_Selection triangles;

    public HEM_Triangulate() {
	super();
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	triangles = new HE_Selection(mesh);
	tracker.setStatus("Starting HEM_Triangulate.");
	final HE_Face[] f = mesh.getFacesAsArray();
	final int n = mesh.getNumberOfFaces();
	tracker.setStatus("Triangulating faces.", n);
	for (int i = 0; i < n; i++) {
	    triangulateNoPairing(f[i], mesh);
	    tracker.incrementCounter();
	}
	mesh.pairHalfedges();
	tracker.setStatus("Exiting HEM_Triangulate.");
	return mesh;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	triangles = new HE_Selection(selection.parent);
	tracker.setStatus("Starting HEM_Triangulate.");
	final HE_Face[] f = selection.getFacesAsArray();
	final int n = selection.getNumberOfFaces();
	tracker.setStatus("Triangulating faces.", n);
	for (int i = 0; i < n; i++) {
	    triangulateNoPairing(f[i], selection.parent);
	    tracker.incrementCounter();
	}
	selection.parent.pairHalfedges();
	tracker.setStatus("Exiting HEM_Triangulate.");
	return selection.parent;
    }

    private void triangulateNoPairing(final HE_Face face, final HE_Mesh mesh) {
	if (face.getFaceOrder() == 3) {
	    triangles.add(face);
	} else if (face.getFaceOrder() > 3) {
	    final int[][] tris = face.getTriangles();
	    final List<HE_Vertex> vertices = face.getFaceVertices();
	    HE_Halfedge he = face.getHalfedge();
	    mesh.remove(face);
	    do {
		if (he.getPair() != null) {
		    he.getPair().clearPair();
		}
		he.clearPair();
		mesh.remove(he);
		he = he.getNextInFace();
	    } while (he != face.getHalfedge());
	    for (int i = 0; i < tris.length; i++) {
		final int[] tri = tris[i];
		final HE_Face f = new HE_Face();
		mesh.add(f);
		triangles.add(f);
		f.copyProperties(face);
		final HE_Halfedge he1 = new HE_Halfedge();
		final HE_Halfedge he2 = new HE_Halfedge();
		final HE_Halfedge he3 = new HE_Halfedge();
		he1.setVertex(vertices.get(tri[0]));
		he2.setVertex(vertices.get(tri[1]));
		he3.setVertex(vertices.get(tri[2]));
		he1.getVertex().setHalfedge(he1);
		he2.getVertex().setHalfedge(he2);
		he3.getVertex().setHalfedge(he3);
		he1.setFace(f);
		he2.setFace(f);
		he3.setFace(f);
		he1.setNext(he2);
		he2.setNext(he3);
		he3.setNext(he1);
		f.setHalfedge(he1);
		mesh.add(he1);
		mesh.add(he2);
		mesh.add(he3);
	    }
	}
    }
}
