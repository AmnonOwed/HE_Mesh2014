/*
 *
 */
package wblut.hemesh;

import java.util.List;
import javolution.util.FastTable;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Vector;

/**
 * Collection of mesh elements. Contains methods to manipulate selections
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HE_Selection extends HE_MeshStructure {
    /**
     *
     */
    public HE_Mesh parent;

    /**
     * Instantiates a new HE_Selection.
     *
     * @param parent
     */
    public HE_Selection(final HE_Mesh parent) {
	super();
	this.parent = parent;
	vertices = new HE_RASTrove<HE_Vertex>();
	halfedges = new HE_RASTrove<HE_Halfedge>();
	faces = new HE_RASTrove<HE_Face>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_MeshStructure#getNumberOfEdges()
     */
    @Override
    public int getNumberOfEdges() {
	int noe = 0;
	for (final HE_Halfedge he : halfedges) {
	    if (he.isEdge()) {
		noe++;
	    }
	}
	return noe;
    }

    /**
     * Get outer edges.
     *
     * @return outer edges as FastTable<HE_Edge>
     */
    public List<HE_Halfedge> getOuterEdges() {
	final HE_Selection sel = get();
	sel.collectEdgesByFace();
	final List<HE_Halfedge> result = new FastTable<HE_Halfedge>();
	for (final HE_Halfedge he : sel.halfedges) {
	    if (he.isEdge()) {
		final HE_Face f1 = he.getFace();
		final HE_Face f2 = he.getPair().getFace();
		if ((f1 == null) || (f2 == null) || (!contains(f1))
			|| (!contains(f2))) {
		    result.add(he);
		}
	    }
	}
	return result;
    }

    /**
     * Get inner edges.
     *
     * @return inner edges as FastTable<HE_Edge>
     */
    public List<HE_Halfedge> getInnerEdges() {
	final HE_Selection sel = get();
	sel.collectEdgesByFace();
	final List<HE_Halfedge> result = new FastTable<HE_Halfedge>();
	for (final HE_Halfedge he : sel.halfedges) {
	    if (he.isEdge()) {
		final HE_Face f1 = he.getFace();
		final HE_Face f2 = he.getPair().getFace();
		if (!((f1 == null) || (f2 == null) || (!contains(f1)) || (!contains(f2)))) {
		    result.add(he);
		}
	    }
	}
	return result;
    }

    /**
     * Get outer vertices.
     *
     * @return outer vertices as FastTable<HE_Vertex>
     */
    public List<HE_Vertex> getOuterVertices() {
	final List<HE_Vertex> result = new FastTable<HE_Vertex>();
	final List<HE_Halfedge> outerEdges = getOuterEdges();
	for (int i = 0; i < outerEdges.size(); i++) {
	    final HE_Halfedge e = outerEdges.get(i);
	    final HE_Vertex v1 = e.getVertex();
	    final HE_Vertex v2 = e.getEndVertex();
	    if (!result.contains(v1)) {
		result.add(v1);
	    }
	    if (!result.contains(v2)) {
		result.add(v2);
	    }
	}
	return result;
    }

    /**
     * Get inner vertices.
     *
     * @return inner vertices as FastTable<HE_Vertex>
     */
    public List<HE_Vertex> getInnerVertices() {
	final HE_Selection sel = get();
	sel.collectVertices();
	final List<HE_Vertex> result = new FastTable<HE_Vertex>();
	final List<HE_Vertex> outerVertices = getOuterVertices();
	HE_Vertex v;
	final int n = sel.vertices.size();
	for (int i = 0; i < n; i++) {
	    v = sel.getVertexByIndex(i);
	    if (!outerVertices.contains(v)) {
		result.add(v);
	    }
	}
	return result;
    }

    /**
     * Get vertices in selection on mesh boundary.
     *
     * @return boundary vertices in selection as FastTable<HE_Vertex>
     */
    public List<HE_Vertex> getBoundaryVertices() {
	final List<HE_Vertex> result = new FastTable<HE_Vertex>();
	final List<HE_Halfedge> outerEdges = getOuterEdges();
	for (int i = 0; i < outerEdges.size(); i++) {
	    final HE_Halfedge e = outerEdges.get(i);
	    if ((e.getFace() == null) || (e.getPair().getFace() == null)) {
		final HE_Vertex v1 = e.getVertex();
		final HE_Vertex v2 = e.getEndVertex();
		if (!result.contains(v1)) {
		    result.add(v1);
		}
		if (!result.contains(v2)) {
		    result.add(v2);
		}
	    }
	}
	return result;
    }

    /**
     * Get outer halfedges.
     *
     * @return outside halfedges of outer edges as FastTable<HE_halfedge>
     */
    public List<HE_Halfedge> getOuterHalfedges() {
	final HE_Selection sel = get();
	sel.collectHalfedges();
	final List<HE_Halfedge> result = new FastTable<HE_Halfedge>();
	HE_Halfedge he;
	final int n = sel.halfedges.size();
	for (int i = 0; i < n; i++) {
	    he = sel.getHalfedgeByIndex(i);
	    final HE_Face f1 = he.getFace();
	    if ((f1 == null) || (!contains(f1))) {
		result.add(he);
	    }
	}
	return result;
    }

    /**
     * Get outer halfedges.
     *
     * @return inside halfedges of outer edges as FastTable<HE_halfedge>
     */
    public List<HE_Halfedge> getOuterHalfedgesInside() {
	final HE_Selection sel = get();
	sel.collectHalfedges();
	final List<HE_Halfedge> result = new FastTable<HE_Halfedge>();
	HE_Halfedge he;
	final int n = sel.halfedges.size();
	for (int i = 0; i < n; i++) {
	    he = sel.getHalfedgeByIndex(i);
	    final HE_Face f1 = he.getPair().getFace();
	    if ((f1 == null) || (!contains(f1))) {
		result.add(he);
	    }
	}
	return result;
    }

    /**
     * Get innerhalfedges.
     *
     * @return inner halfedges as FastTable<HE_halfedge>
     */
    public List<HE_Halfedge> getInnerHalfedges() {
	final HE_Selection sel = get();
	sel.collectHalfedges();
	final List<HE_Halfedge> result = new FastTable<HE_Halfedge>();
	HE_Halfedge he;
	final int n = sel.halfedges.size();
	for (int i = 0; i < n; i++) {
	    he = sel.getHalfedgeByIndex(i);
	    if (contains(he.getPair().getFace()) && contains(he.getFace())) {
		result.add(he);
	    }
	}
	return result;
    }

    /**
     * Copy selection.
     *
     * @return copy of selection
     */
    public HE_Selection get() {
	final HE_Selection copy = new HE_Selection(parent);
	for (final HE_Face f : faces) {
	    copy.add(f);
	}
	for (final HE_Halfedge he : halfedges) {
	    copy.add(he);
	}
	for (final HE_Vertex v : vertices) {
	    copy.add(v);
	}
	return copy;
    }

    /**
     *
     *
     * @return
     */
    public HE_Mesh getAsMesh() {
	return new HE_Mesh(new HEC_Copy(this));
    }

    /**
     * Add selection.
     *
     * @param sel
     *            selection to add
     */
    public void add(final HE_Selection sel) {
	for (final HE_Face f : sel.faces) {
	    add(f);
	}
	for (final HE_Halfedge he : sel.halfedges) {
	    add(he);
	}
	for (final HE_Vertex v : sel.vertices) {
	    add(v);
	}
    }

    /**
     *
     *
     * @param sel
     */
    public void union(final HE_Selection sel) {
	for (final HE_Face f : sel.faces) {
	    add(f);
	}
	for (final HE_Halfedge he : sel.halfedges) {
	    add(he);
	}
	for (final HE_Vertex v : sel.vertices) {
	    add(v);
	}
    }

    /**
     * Remove selection.
     *
     * @param sel
     *            selection to remove
     */
    public void subtract(final HE_Selection sel) {
	for (final HE_Face f : sel.faces) {
	    remove(f);
	}
	for (final HE_Halfedge he : sel.halfedges) {
	    remove(he);
	}
	for (final HE_Vertex v : sel.vertices) {
	    remove(v);
	}
    }

    /**
     * Remove elements outside selection.
     *
     * @param sel
     *            selection to check
     */
    public void intersect(final HE_Selection sel) {
	final HE_RAS<HE_Face> newFaces = new HE_RASTrove<HE_Face>();
	for (final HE_Face f : sel.faces) {
	    if (faces.contains(f)) {
		newFaces.add(f);
	    }
	}
	faces = newFaces;
	final HE_RAS<HE_Halfedge> newHalfedges = new HE_RASTrove<HE_Halfedge>();
	for (final HE_Halfedge he : sel.halfedges) {
	    if (halfedges.contains(he)) {
		newHalfedges.add(he);
	    }
	}
	halfedges = newHalfedges;
	final HE_RAS<HE_Vertex> newVertices = new HE_RASTrove<HE_Vertex>();
	for (final HE_Vertex v : sel.vertices) {
	    if (vertices.contains(v)) {
		newVertices.add(v);
	    }
	}
	vertices = newVertices;
    }

    /**
     * Grow face selection outwards by one face.
     */
    public void grow() {
	final FastTable<HE_Face> currentFaces = new FastTable<HE_Face>();
	HE_Face f;
	final int n = faces.size();
	for (int i = 0; i < n; i++) {
	    f = getFaceByIndex(i);
	    currentFaces.add(f);
	    addFaces(f.getNeighborFaces());
	}
    }

    /**
     * Grow face selection outwards.
     *
     * @param n
     *            number of faces to grow
     */
    public void grow(final int n) {
	for (int i = 0; i < n; i++) {
	    grow();
	}
    }

    /**
     * Grow face selection inwards by one face.
     */
    public void shrink() {
	final List<HE_Halfedge> outerEdges = getOuterEdges();
	for (int i = 0; i < outerEdges.size(); i++) {
	    final HE_Halfedge e = outerEdges.get(i);
	    final HE_Face f1 = e.getFace();
	    final HE_Face f2 = e.getPair().getFace();
	    if ((f1 == null) || (!contains(f1))) {
		remove(f2);
	    }
	    if ((f2 == null) || (!contains(f2))) {
		remove(f1);
	    }
	}
    }

    /**
     * Shrink face selection inwards.
     *
     * @param n
     *            number of faces to shrink
     */
    public void shrink(final int n) {
	for (int i = 0; i < n; i++) {
	    shrink();
	}
    }

    /**
     * Select faces surrounding current face selection.
     */
    public void surround() {
	final FastTable<HE_Face> currentFaces = new FastTable<HE_Face>();
	HE_Face face;
	final int n = faces.size();
	for (int i = 0; i < n; i++) {
	    face = getFaceByIndex(i);
	    currentFaces.add(face);
	    addFaces(face.getNeighborFaces());
	}
	removeFaces(currentFaces);
    }

    /**
     * Select faces surrounding current face selection at a distance of n-1
     * faces.
     *
     * @param n
     *            distance to current selection
     */
    public void surround(final int n) {
	grow(n - 1);
	surround();
    }

    /**
     * Add faces with certain number of edges in selection to selection.
     *
     * @param threshold
     *            number of edges that have to belong to the selection before a
     *            face is added
     */
    public void smooth(final int threshold) {
	final FastTable<HE_Halfedge> currentHalfedges = new FastTable<HE_Halfedge>();
	HE_Halfedge hei;
	final int n = halfedges.size();
	for (int i = 0; i < n; i++) {
	    hei = getHalfedgeByIndex(i);
	    currentHalfedges.add(hei);
	}
	for (int i = 0; i < currentHalfedges.size(); i++) {
	    final HE_Face f = currentHalfedges.get(i).getPair().getFace();
	    if ((f != null) && (!contains(f))) {
		int ns = 0;
		HE_Halfedge he = f.getHalfedge();
		do {
		    if (contains(he.getPair().getFace())) {
			ns++;
		    }
		    he = he.getNextInFace();
		} while (he != f.getHalfedge());
		if (ns >= threshold) {
		    add(f);
		}
	    }
	}
    }

    /**
     * Add faces with certain proportion of edges in selection to selection.
     *
     * @param threshold
     *            number of edges that have to belong to the selection before a
     *            face is added
     */
    public void smooth(final double threshold) {
	final FastTable<HE_Halfedge> currentHalfedges = new FastTable<HE_Halfedge>();
	HE_Halfedge hei;
	final int n = halfedges.size();
	for (int i = 0; i < n; i++) {
	    hei = getHalfedgeByIndex(i);
	    currentHalfedges.add(hei);
	}
	for (int i = 0; i < currentHalfedges.size(); i++) {
	    final HE_Face f = currentHalfedges.get(i).getPair().getFace();
	    if ((f != null) && (!contains(f))) {
		int ns = 0;
		HE_Halfedge he = f.getHalfedge();
		do {
		    if (contains(he.getPair().getFace())) {
			ns++;
		    }
		    he = he.getNextInFace();
		} while (he != f.getHalfedge());
		if (ns >= (threshold * f.getFaceOrder())) {
		    add(f);
		}
	    }
	}
    }

    /**
     * Select all mesh elements.
     *
     * @return current selection
     */
    public HE_Selection selectAll() {
	clear();
	selectAllFaces();
	selectAllEdges();
	selectAllHalfedges();
	selectAllVertices();
	return this;
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection selectAllFaces() {
	clear();
	for (final HE_Face f : parent.faces) {
	    if (f != null) {
		faces.add(f);
	    }
	}
	return this;
    }

    /**
     *
     *
     * @param r
     * @return
     */
    public HE_Selection selectRandomFaces(final double r) {
	clear();
	for (final HE_Face f : parent.faces) {
	    if (f != null) {
		if (Math.random() < r) {
		    faces.add(f);
		}
	    }
	}
	return this;
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection selectAllEdges() {
	clear();
	for (final HE_Halfedge e : parent.halfedges) {
	    if (e.isEdge()) {
		add(e);
	    }
	}
	return this;
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection selectAllHalfedges() {
	clear();
	for (final HE_Halfedge he : parent.halfedges) {
	    if (he != null) {
		halfedges.add(he);
	    }
	}
	return this;
    }

    /**
     *
     *
     * @return
     */
    public HE_Selection selectAllVertices() {
	clear();
	for (final HE_Vertex v : parent.vertices) {
	    if (v != null) {
		vertices.add(v);
	    }
	}
	return this;
    }

    /**
     * Invert current selection.
     *
     * @return inverted selection
     */
    public HE_Selection invertSelection() {
	invertFaces();
	invertEdges();
	invertHalfedges();
	invertVertices();
	return this;
    }

    /**
     * Invert current face selection.
     *
     * @return inverted face selection
     */
    public HE_Selection invertFaces() {
	final HE_RAS<HE_Face> newFaces = new HE_RASTrove<HE_Face>();
	for (final HE_Face f : parent.faces) {
	    if (!contains(f)) {
		newFaces.add(f);
	    }
	}
	faces = newFaces;
	return this;
    }

    /**
     * Invert current edge election.
     *
     * @return inverted edge selection
     */
    public HE_Selection invertEdges() {
	final HE_RAS<HE_Halfedge> newEdges = new HE_RASTrove<HE_Halfedge>();
	for (final HE_Halfedge e : parent.halfedges) {
	    if (e.isEdge() && (!contains(e))) {
		newEdges.add(e);
	    }
	}
	halfedges = newEdges;
	return this;
    }

    /**
     * Invert current vertex selection.
     *
     * @return inverted vertex selection
     */
    public HE_Selection invertVertices() {
	final HE_RAS<HE_Vertex> newVertices = new HE_RASTrove<HE_Vertex>();
	for (final HE_Vertex v : parent.vertices) {
	    if (!contains(v)) {
		newVertices.add(v);
	    }
	}
	vertices = newVertices;
	return this;
    }

    /**
     * Invert current halfedge selection.
     *
     * @return inverted halfedge selection
     */
    public HE_Selection invertHalfedges() {
	final HE_RAS<HE_Halfedge> newHalfedges = new HE_RASTrove<HE_Halfedge>();
	for (final HE_Halfedge he : parent.halfedges) {
	    if (!contains(he)) {
		newHalfedges.add(he);
	    }
	}
	halfedges = newHalfedges;
	return this;
    }

    /**
     * Clean current selection, removes all elements no longer part of mesh.
     *
     * @return current selection
     */
    public HE_Selection cleanSelection() {
	final HE_RAS<HE_Face> newFaces = new HE_RASTrove<HE_Face>();
	for (final HE_Face f : faces) {
	    if (parent.contains(f)) {
		newFaces.add(f);
	    }
	}
	faces = newFaces;
	final HE_RAS<HE_Halfedge> newHalfedges = new HE_RASTrove<HE_Halfedge>();
	for (final HE_Halfedge he : halfedges) {
	    if (parent.contains(he)) {
		newHalfedges.add(he);
	    }
	}
	halfedges = newHalfedges;
	final HE_RAS<HE_Vertex> newVertices = new HE_RASTrove<HE_Vertex>();
	for (final HE_Vertex v : vertices) {
	    if (parent.contains(v)) {
		newVertices.add(v);
	    }
	}
	vertices = newVertices;
	return this;
    }

    /**
     * Collect vertices belonging to selection elements.
     */
    public void collectVertices() {
	List<HE_Vertex> tmpVertices = new FastTable<HE_Vertex>();
	HE_Face f;
	int n = faces.size();
	for (int i = 0; i < n; i++) {
	    f = getFaceByIndex(i);
	    tmpVertices = f.getFaceVertices();
	    addVertices(tmpVertices);
	}
	HE_Halfedge he;
	n = halfedges.size();
	for (int i = 0; i < n; i++) {
	    he = getHalfedgeByIndex(i);
	    add(he.getVertex());
	}
    }

    /**
     * Collect faces belonging to selection elements.
     */
    public void collectFaces() {
	HE_Vertex v;
	int n = vertices.size();
	for (int i = 0; i < n; i++) {
	    v = getVertexByIndex(i);
	    addFaces(v.getFaceStar());
	}
	HE_Halfedge he;
	n = halfedges.size();
	for (int i = 0; i < n; i++) {
	    he = getHalfedgeByIndex(i);
	    add(he.getFace());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.hemesh.HE_MeshStructure#getFacesWithNormal(wblut.geom.WB_Coordinate
     * , double)
     */
    @Override
    public void getFacesWithNormal(final WB_Coordinate n, final double ta) {
	final WB_Vector nn = geometryfactory.createNormalizedVector(n);
	final double cta = Math.cos(ta);
	for (final HE_Face f : parent.faces) {
	    if (f.getFaceNormal().dot(nn) > cta) {
		add(f);
	    }
	}
    }

    /**
     * Collect edges belonging to face selection.
     */
    public void collectEdgesByFace() {
	final HE_FaceIterator fitr = new HE_FaceIterator(this);
	while (fitr.hasNext()) {
	    addHalfedges(fitr.next().getFaceEdges());
	}
    }

    /**
     *
     */
    public void collectEdgesByVertex() {
	final HE_VertexIterator vitr = new HE_VertexIterator(this);
	while (vitr.hasNext()) {
	    addHalfedges(vitr.next().getEdgeStar());
	}
    }

    /**
     * Collect halfedges belonging to face selection.
     */
    public void collectHalfedges() {
	HE_Face f;
	int n = faces.size();
	for (int i = 0; i < n; i++) {
	    f = getFaceByIndex(i);
	    addHalfedges(f.getFaceHalfedges());
	}
	final FastTable<HE_Halfedge> newhalfedges = new FastTable<HE_Halfedge>();
	n = halfedges.size();
	for (int i = 0; i < n; i++) {
	    newhalfedges.add(getHalfedgeByIndex(i).getNextInFace().getPair());
	}
	addHalfedges(newhalfedges);
    }
}
