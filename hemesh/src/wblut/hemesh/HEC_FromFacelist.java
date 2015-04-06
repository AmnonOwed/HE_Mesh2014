/*
 *
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javolution.util.FastMap;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_KDTree;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.geom.WB_Point;
import wblut.math.WB_Epsilon;

/**
 * Creates a new mesh from a list of vertices and faces. Vertices can be
 * duplicate.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEC_FromFacelist extends HEC_Creator {
    /** Vertices. */
    private WB_Coordinate[] vertices;
    private WB_Coordinate[] uvws;
    private int[] faceTextureIds;
    /** Face indices. */
    private int[][] faces;
    /** Duplicate vertices?. */
    private boolean duplicate;
    /** Check face normal consistency?. */
    private boolean normalcheck;

    /**
     * Instantiates a new HEC_Facelist.
     *
     */
    public HEC_FromFacelist() {
	super();
	override = true;
	duplicate = true;
	normalcheck = false;
    }

    /**
     * Set vertex coordinates from an array of WB_point. No copies are made.
     *
     * @param vs
     *            vertices
     * @return self
     */
    public HEC_FromFacelist setVertices(final WB_Coordinate[] vs) {
	vertices = vs;
	return this;
    }

    /**
     * Set vertex coordinates from an arraylist of WB_point.
     *
     * @param vs
     *            vertices
     * @return self
     */
    public HEC_FromFacelist setVertices(
	    final Collection<? extends WB_Coordinate> vs) {
	final int n = vs.size();
	final Iterator<? extends WB_Coordinate> itr = vs.iterator();
	vertices = new WB_Coordinate[n];
	int i = 0;
	while (itr.hasNext()) {
	    vertices[i] = itr.next();
	    i++;
	}
	return this;
    }

    public HEC_FromFacelist setUVW(final Collection<? extends WB_Coordinate> vs) {
	final int n = vs.size();
	final Iterator<? extends WB_Coordinate> itr = vs.iterator();
	uvws = new WB_Coordinate[n];
	int i = 0;
	while (itr.hasNext()) {
	    uvws[i] = itr.next();
	    i++;
	}
	return this;
    }

    public HEC_FromFacelist setUVW(final WB_Coordinate[] vs) {
	final int n = vs.length;
	uvws = new WB_Coordinate[n];
	int i = 0;
	for (final WB_Coordinate v : vs) {
	    uvws[i] = v;
	    i++;
	}
	return this;
    }

    public HEC_FromFacelist setUVW(final double[][] vs) {
	final int n = vs.length;
	uvws = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    uvws[i] = new WB_Point(vs[i][0], vs[i][1], vs[i][2]);
	}
	return this;
    }

    /**
     * Set vertex coordinates from an array of WB_point.
     *
     * @param vs
     *            vertices
     * @param copy
     *            copy points?
     * @return self
     */
    public HEC_FromFacelist setVertices(final WB_Coordinate[] vs,
	    final boolean copy) {
	if (copy) {
	    final int n = vs.length;
	    vertices = new WB_Coordinate[n];
	    for (int i = 0; i < n; i++) {
		vertices[i] = new WB_Point(vs[i]);
	    }
	} else {
	    vertices = vs;
	}
	return this;
    }

    /**
     * Set vertex coordinates from a 2D array of double: 1st index=point, 2nd
     * index (0..2) coordinates
     *
     * @param vs
     *            Nx3 2D array of coordinates
     * @return self
     */
    public HEC_FromFacelist setVertices(final double[][] vs) {
	final int n = vs.length;
	vertices = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    vertices[i] = new WB_Point(vs[i][0], vs[i][1], vs[i][2]);
	}
	return this;
    }

    /**
     * Set vertex coordinates from array of double: x0, y0 ,z0 ,x1 ,y1 ,z1 ,...
     *
     * @param vs
     *            array of coordinates
     * @return self
     */
    public HEC_FromFacelist setVertices(final double[] vs) {
	final int n = vs.length;
	vertices = new WB_Point[n / 3];
	for (int i = 0; i < n; i += 3) {
	    vertices[i] = new WB_Point(vs[i], vs[i + 1], vs[i + 2]);
	}
	return this;
    }

    /**
     * Set vertex coordinates from a 2D array of float: 1st index=point, 2nd
     * index (0..2) coordinates
     *
     * @param vs
     *            Nx3 2D array of coordinates
     * @return self
     */
    public HEC_FromFacelist setVertices(final float[][] vs) {
	final int n = vs.length;
	vertices = new WB_Point[n];
	for (int i = 0; i < n; i++) {
	    vertices[i] = new WB_Point(vs[i][0], vs[i][1], vs[i][2]);
	}
	return this;
    }

    /**
     * Set vertex coordinates from array of float: x0, y0 ,z0 ,x1 ,y1 ,z1 ,...
     *
     * @param vs
     *            array of coordinates
     * @return self
     */
    public HEC_FromFacelist setVertices(final float[] vs) {
	final int n = vs.length;
	vertices = new WB_Point[n / 3];
	for (int i = 0; i < n; i += 3) {
	    vertices[i] = new WB_Point(vs[i], vs[i + 1], vs[i + 2]);
	}
	return this;
    }

    /**
     * Set faces from 2D array of int: 1st index=face, 2nd=index of vertex.
     *
     * @param fs
     *            2D array of vertex indices
     * @return self
     */
    public HEC_FromFacelist setFaces(final int[][] fs) {
	faces = fs;
	return this;
    }

    /**
     * Set faces from 2D array of int: 1st index=face, 2nd=index of vertex.
     *
     * @param fs
     *            2D array of vertex indices
     * @return self
     */
    public HEC_FromFacelist setFaces(final List<int[]> fs) {
	faces = new int[fs.size()][];
	int i = 0;
	for (final int[] indices : fs) {
	    faces[i] = indices;
	    i++;
	}
	return this;
    }

    public HEC_FromFacelist setFaceTextureIds(final int[] fts) {
	faceTextureIds = fts;
	return this;
    }

    /**
     * Duplicate vertices in input?.
     *
     * @param b
     *            true/false
     * @return self
     */
    public HEC_FromFacelist setDuplicate(final boolean b) {
	duplicate = b;
	return this;
    }

    /**
     * Check face normals?.
     *
     * @param b
     *            true/false
     * @return self
     */
    public HEC_FromFacelist setCheckNormals(final boolean b) {
	normalcheck = b;
	return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    protected HE_Mesh createBase() {
	final HE_Mesh mesh = new HE_Mesh();
	if ((faces != null) && (vertices != null)) {
	    final boolean useUVW = (uvws != null)
		    && (uvws.length == vertices.length);
	    final HE_Vertex[] uniqueVertices = new HE_Vertex[vertices.length];
	    final boolean[] duplicated = new boolean[vertices.length];
	    if (duplicate) {
		final WB_KDTree<WB_Coordinate, Integer> kdtree = new WB_KDTree<WB_Coordinate, Integer>();
		WB_KDEntry<WB_Coordinate, Integer>[] neighbors;
		HE_Vertex v = new HE_Vertex(vertices[0]);
		if (useUVW) {
		    v.setUVW(uvws[0]);
		}
		kdtree.add(v, 0);
		uniqueVertices[0] = v;
		duplicated[0] = false;
		mesh.add(v);
		for (int i = 1; i < vertices.length; i++) {
		    v = new HE_Vertex(vertices[i]);
		    if (useUVW) {
			v.setUVW(uvws[i]);
		    }
		    neighbors = kdtree.getNearestNeighbors(v, 1);
		    if (neighbors[0].d2 < WB_Epsilon.SQEPSILON) {
			uniqueVertices[i] = uniqueVertices[neighbors[0].value];
			duplicated[i] = true;
		    } else {
			kdtree.add(v, i);
			uniqueVertices[i] = v;
			mesh.add(uniqueVertices[i]);
			duplicated[i] = false;
		    }
		}
	    } else {
		HE_Vertex v;
		for (int i = 0; i < vertices.length; i++) {
		    v = new HE_Vertex(vertices[i]);
		    if (useUVW) {
			v.setUVW(uvws[i]);
		    }
		    v.setInternalLabel(i);
		    uniqueVertices[i] = v;
		    duplicated[i] = false;
		    mesh.add(uniqueVertices[i]);
		}
	    }
	    int id = 0;
	    HE_Halfedge he;
	    if (normalcheck) {
		// Create adjacency table
		final FastMap<Long, int[]> edges = new FastMap<Long, int[]>();
		for (int i = 0; i < faces.length; i++) {
		    final int[] face = faces[i];
		    final int fl = face.length;
		    for (int j = 0; j < fl; j++) {
			final long ohash = ohash(face[j], face[(j + 1) % fl]);
			final int[] faces = edges.get(ohash);
			if (faces == null) {
			    edges.put(ohash, new int[] { i, -1 });
			} else {
			    faces[1] = i;
			}
		    }
		}
		//
		final boolean[] visited = new boolean[faces.length];
		final LinkedList<Integer> queue = new LinkedList<Integer>();
		boolean facesleft = false;
		int starti = 0;
		do {
		    queue.add(starti);
		    int temp;
		    while (!queue.isEmpty()) {
			final Integer index = queue.poll();
			final int[] face = faces[index];
			final int fl = face.length;
			visited[index] = true;
			for (int j = 0; j < fl; j++) {
			    final long ohash = ohash(face[j],
				    face[(j + 1) % fl]);
			    final int[] ns = edges.get(ohash);
			    if (ns != null) {
				edges.remove(ohash);// no need to revisit
				// previous edges
				Integer neighbor;
				if (ns[0] == index) {
				    neighbor = ns[1];
				} else {
				    neighbor = ns[0];
				}
				if (neighbor > -1) {
				    if (visited[neighbor] == false) {
					if (!queue.contains(neighbor)) {
					    queue.add(neighbor);
					}
					if (consistentOrder(j, (j + 1) % fl,
						face, faces[neighbor]) == -1) {
					    final int fln = faces[neighbor].length;
					    for (int k = 0; k < (fln / 2); k++) {
						temp = faces[neighbor][k];
						faces[neighbor][k] = faces[neighbor][fln
						                                     - k - 1];
						faces[neighbor][fln - k - 1] = temp;
					    }
					}
				    }
				}
			    }
			}
		    }
		    facesleft = false;
		    for (; starti < faces.length; starti++) {
			if (!visited[starti]) {
			    facesleft = true;
			    break;
			}
		    }
		} while (facesleft);
	    }
	    final boolean useFaceTextures = (faceTextureIds != null)
		    && (faceTextureIds.length == faces.length);
	    for (final int[] face : faces) {
		final ArrayList<HE_Halfedge> faceEdges = new ArrayList<HE_Halfedge>();
		final HE_Face hef = new HE_Face();
		hef.setInternalLabel(id);
		if (useFaceTextures) {
		    hef.setTextureId(faceTextureIds[id]);
		}
		id++;
		final int fl = face.length;
		final int[] locface = new int[fl];
		int li = 0;
		locface[li++] = face[0];
		for (int i = 1; i < (fl - 1); i++) {
		    if (uniqueVertices[face[i]] != uniqueVertices[face[i - 1]]) {
			locface[li++] = face[i];
		    }
		}
		if ((uniqueVertices[face[fl - 1]] != uniqueVertices[face[fl - 2]])
			&& (uniqueVertices[face[fl - 1]] != uniqueVertices[face[0]])) {
		    locface[li++] = face[fl - 1];
		}
		if (li > 2) {
		    for (int i = 0; i < li; i++) {
			he = new HE_Halfedge();
			faceEdges.add(he);
			he.setFace(hef);
			if (hef.getHalfedge() == null) {
			    hef.setHalfedge(he);
			}
			he.setVertex(uniqueVertices[locface[i]]);
			if (useUVW) {
			    if (duplicated[locface[i]]) {
				final HE_TextureCoordinate uvw = uniqueVertices[locface[i]]
					.getVertexUVW();
				if (uvw.ud() != uvws[locface[i]].xd()
					|| uvw.vd() != uvws[locface[i]].yd()
					|| uvw.wd() != uvws[locface[i]].zd()) {
				    he.setUVW(uvws[locface[i]]);
				}
			    }
			}
			he.getVertex().setHalfedge(he);
		    }
		    mesh.add(hef);
		    HE_Mesh.cycleHalfedges(faceEdges);
		    mesh.addHalfedges(faceEdges);
		}
	    }
	    mesh.pairHalfedges();
	    mesh.capHalfedges();
	}
	return mesh;
    }

    /**
     * Ohash.
     *
     * @param u
     *            the u
     * @param v
     *            the v
     * @return the long
     */
    private Long ohash(final int u, final int v) {
	int lu = u;
	int lv = v;
	if (u > v) {
	    lu = v;
	    lv = u;
	}
	final long A = (lu >= 0) ? 2 * lu : (-2 * lu) - 1;
	final long B = (lv >= 0) ? 2 * lv : (-2 * lv) - 1;
	return (A >= B) ? (A * A) + A + B : A + (B * B);
    }

    /**
     * Consistent order.
     *
     * @param i
     *            the i
     * @param j
     *            the j
     * @param face
     *            the face
     * @param neighbor
     *            the neighbor
     * @return the int
     */
    private int consistentOrder(final int i, final int j, final int[] face,
	    final int[] neighbor) {
	for (int k = 0; k < neighbor.length; k++) {
	    if ((neighbor[k] == face[i])
		    && (neighbor[(k + 1) % neighbor.length] == face[j])) {
		return -1;
	    }
	    if ((neighbor[k] == face[j])
		    && (neighbor[(k + 1) % neighbor.length] == face[i])) {
		return 1;
	    }
	}
	return 0;
    }
}
