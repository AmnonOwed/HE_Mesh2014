package wblut.hemesh;

import java.util.List;
import java.util.Map;

import javolution.context.LogContext;
import javolution.util.FastList;
import javolution.util.FastMap;
import wblut.WB_Epsilon;
import wblut.geom.Segment;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Distance;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
import wblut.geom.WB_SimplePolygon;
import wblut.geom.WB_Vector;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;

/**
 * Extrudes and scales a face along its face normal.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEM_Extrude extends HEM_Modifier {

	/** Extrusion distance. */
	private WB_Parameter<Double> d;

	/** Threshold angle for hard edges. */
	private double thresholdAngle;

	/** Chamfer factor or distance. */
	private double chamfer;

	/** Hard edge chamfer distance. */
	private double hardEdgeChamfer;

	/** Extrusion mode. */
	private boolean relative;

	/** Fuse coplanar faces. */
	private boolean fuse;

	/** Turn non-extrudable faces into spiked faces?. */
	private boolean peak;

	/** Limit angle for face fusion. */
	private double fuseAngle;

	/** sin(fuseAngle). */
	private double sin2FA;

	/** Vertex normals. */
	private Map<Long, WB_Vector> _faceNormals;

	/** Halfedge normals. */
	private Map<Long, WB_Vector> _halfedgeNormals;

	/** Extrusion widths. */
	private Map<Long, Double> _halfedgeEWs;

	/** Face centers. */
	private Map<Long, WB_Point> _faceCenters;

	private double[] heights;

	/** The walls. */
	public HE_Selection walls;

	/** The extruded. */
	public HE_Selection extruded;

	/** The failed faces. */
	private List<HE_Face> failedFaces;
	private List<Double> failedHeights;

	/** The flat. */
	private boolean flat;

	/**
	 * Instantiates a new HEM_Extrude.
	 */
	public HEM_Extrude() {
		super();
		d = new WB_ConstantParameter(0);
		flat = true;
		thresholdAngle = -1;
		chamfer = 0;
		hardEdgeChamfer = 0;
		relative = true;
		fuseAngle = Math.PI / 36;
		sin2FA = Math.sin(fuseAngle);
		sin2FA *= sin2FA;
		heights = null;
	}

	/**
	 * Set extrusion distance.
	 * 
	 * @param d
	 *            extrusion distance
	 * @return self
	 */
	public HEM_Extrude setDistance(final double d) {
		this.d = new WB_ConstantParameter(d);
		flat = (WB_Epsilon.isZero(d));
		return this;
	}

	/**
	 * Sets the distance.
	 * 
	 * @param d
	 *            the d
	 * @return the hE m_ extrude
	 */
	public HEM_Extrude setDistance(final WB_Parameter<Double> d) {
		this.d = d;
		flat = false;
		return this;
	}

	/**
	 * Set chamfer factor.
	 * 
	 * @param c
	 *            chamfer factor
	 * @return self
	 */
	public HEM_Extrude setChamfer(final double c) {
		chamfer = c;
		return this;
	}

	/**
	 * Set hard edge chamfer distance
	 * 
	 * Set extrusion distance for hard edge.
	 * 
	 * @param c
	 *            extrusion distance
	 * @return self
	 */
	public HEM_Extrude setHardEdgeChamfer(final double c) {
		hardEdgeChamfer = c;
		return this;
	}

	/**
	 * Set chamfer mode.
	 * 
	 * @param relative
	 *            true/false
	 * @return self
	 */
	public HEM_Extrude setRelative(final boolean relative) {
		this.relative = relative;
		return this;
	}

	/**
	 * Set fuse option: merges coplanar faces.
	 * 
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Extrude setFuse(final boolean b) {
		fuse = b;
		return this;
	}

	/**
	 * Set peak option.
	 * 
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEM_Extrude setPeak(final boolean b) {
		peak = b;
		return this;
	}

	/**
	 * Set threshold angle for hard edge.
	 * 
	 * @param a
	 *            threshold angle
	 * @return self
	 */
	public HEM_Extrude setThresholdAngle(final double a) {
		thresholdAngle = a;
		return this;
	}

	/**
	 * Set threshold angle for fuse.
	 * 
	 * @param a
	 *            threshold angle
	 * @return self
	 */
	public HEM_Extrude setFuseAngle(final double a) {
		fuseAngle = a;
		sin2FA = Math.sin(fuseAngle);
		sin2FA *= sin2FA;
		return this;
	}

	public HEM_Extrude setDistances(double[] distances) {
		this.heights = distances;
		return this;
	}

	public HEM_Extrude setDistances(float[] distances) {
		heights = new double[distances.length];
		for (int i = 0; i < distances.length; i++) {
			heights[i] = distances[i];
		}
		return this;
	}

	public HEM_Extrude setDistances(int[] distances) {
		heights = new double[distances.length];
		for (int i = 0; i < distances.length; i++) {
			heights[i] = distances[i];
		}
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		LogContext.enter(LogContext.NULL);
		mesh.resetFaceLabels();
		walls = new HE_Selection(mesh);
		extruded = new HE_Selection(mesh);

		_halfedgeNormals = new FastMap<Long, WB_Vector>();
		_halfedgeEWs = new FastMap<Long, Double>();

		if ((chamfer == 0) && (d == null) && (heights == null)) {
			return mesh;
		}

		HE_Face f;
		HE_Halfedge he;
		final List<HE_Face> faces = mesh.getFacesAsList();
		_faceNormals = mesh.getKeyedFaceNormals();
		_faceCenters = mesh.getKeyedFaceCenters();
		final int nf = faces.size();

		for (int i = 0; i < nf; i++) {
			f = faces.get(i);
			he = f.getHalfedge();
			do {
				_halfedgeNormals.put(he.key(), he.getHalfedgeNormal());
				_halfedgeEWs
						.put(he.key(),
								(he.getHalfedgeDihedralAngle() < thresholdAngle) ? hardEdgeChamfer
										: chamfer);
				he = he.getNextInFace();
			} while (he != f.getHalfedge());

		}

		if (chamfer == 0) {
			return applyStraight(mesh, mesh.getFacesAsList());
		}
		List<HE_Face> facelist = mesh.getFacesAsList();
		if ((relative == true) && (chamfer == 1)) {
			return applyPeaked(mesh, facelist);
		}
		failedFaces = new FastList<HE_Face>();
		failedHeights = new FastList<Double>();
		applyFlat(mesh, faces, flat && fuse);
		if (heights != null) {
			for (int i = 0; i < failedHeights.size(); i++) {
				heights[facelist.indexOf(failedFaces.get(i))] = failedHeights
						.get(i);
			}
		}
		if (peak) {
			applyPeaked(mesh, failedFaces);
		}

		WB_Vector n;
		if (!flat) {
			if (heights != null) {
				if (heights.length == faces.size()) {
					for (int i = 0; i < faces.size(); i++) {
						f = faces.get(i);
						if (!failedFaces.contains(f)) {
							n = _faceNormals.get(f.key());

							he = f.getHalfedge();
							do {
								final HE_Vertex v = he.getVertex();
								he.getVertex().pos._addSelf(heights[i], n);
								he = he.getNextInFace();
							} while (he != f.getHalfedge());
						}
					}

				} else {
					throw new IllegalArgumentException(
							"Length of heights array does not correspond to number of extruded faces.");
				}
			} else {
				for (int i = 0; i < faces.size(); i++) {
					f = faces.get(i);
					if (!failedFaces.contains(f)) {
						n = _faceNormals.get(f.key());

						he = f.getHalfedge();
						do {
							final HE_Vertex v = he.getVertex();
							he.getVertex().pos._addSelf(
									d.value(v.xd(), v.yd(), v.zd()), n);
							he = he.getNextInFace();
						} while (he != f.getHalfedge());
					}
				}
			}
		}
		return mesh;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		LogContext.enter(LogContext.NULL);
		selection.parent.resetFaceLabels();
		walls = new HE_Selection(selection.parent);
		extruded = new HE_Selection(selection.parent);
		if (selection.getNumberOfFaces() == 0) {
			return selection.parent;
		}

		_halfedgeNormals = new FastMap<Long, WB_Vector>();
		_halfedgeEWs = new FastMap<Long, Double>();
		if ((chamfer == 0) && flat && heights == null) {
			return selection.parent;
		}

		HE_Face f;
		HE_Halfedge he;
		final List<HE_Face> selFaces = selection.getFacesAsList();
		_faceNormals = selection.parent.getKeyedFaceNormals();
		_faceCenters = selection.parent.getKeyedFaceCenters();
		final int nf = selFaces.size();
		for (int i = 0; i < nf; i++) {
			f = selFaces.get(i);
			he = f.getHalfedge();
			do {
				_halfedgeNormals.put(he.key(), he.getHalfedgeNormal());
				_halfedgeEWs
						.put(he.key(),
								(he.getHalfedgeDihedralAngle() < thresholdAngle) ? hardEdgeChamfer
										: chamfer);
				he = he.getNextInFace();
			} while (he != f.getHalfedge());

		}

		if (chamfer == 0) {
			return applyStraight(selection.parent, selFaces);
		}

		if ((relative == true) && (chamfer == 1)) {
			return applyPeaked(selection.parent, selFaces);
		}
		failedFaces = new FastList<HE_Face>();
		failedHeights = new FastList<Double>();
		applyFlat(selection.parent, selFaces, flat && fuse);
		if (heights != null) {
			for (int i = 0; i < failedHeights.size(); i++) {
				heights[selFaces.indexOf(failedFaces.get(i))] = failedHeights
						.get(i);
			}
		}
		if (peak) {
			applyPeaked(selection.parent, failedFaces);
		}
		WB_Vector n;
		if (!flat) {
			if (heights != null) {
				if (heights.length == selFaces.size()) {
					for (int i = 0; i < selFaces.size(); i++) {
						f = selFaces.get(i);
						n = _faceNormals.get(f.key());
						he = f.getHalfedge();
						do {
							final HE_Vertex v = he.getVertex();
							he.getVertex().pos._addSelf(heights[i], n);
							he = he.getNextInFace();
						} while (he != f.getHalfedge());

					}
				} else {
					throw new IllegalArgumentException(
							"Length of heights array does not correspond to number of extruded faces.");
				}
			} else {
				for (int i = 0; i < selFaces.size(); i++) {
					f = selFaces.get(i);
					n = _faceNormals.get(f.key());

					he = f.getHalfedge();
					do {
						final HE_Vertex v = he.getVertex();
						he.getVertex().pos._addSelf(
								d.value(v.xd(), v.yd(), v.zd()), n);
						he = he.getNextInFace();
					} while (he != f.getHalfedge());

				}
			}
		}
		return selection.parent;

	}

	/**
	 * Apply straight extrusion.
	 * 
	 * @param mesh
	 *            the mesh
	 * @param faces
	 *            the faces
	 * @return mesh
	 */
	private HE_Mesh applyStraight(final HE_Mesh mesh, final List<HE_Face> faces) {
		final int nf = faces.size();
		final boolean[] visited = new boolean[nf];
		WB_Point fc;
		if (heights != null) {
			if (heights.length == faces.size()) {
				for (int i = 0; i < nf; i++) {
					applyStraightToOneFace(i, faces, mesh, visited, heights[i]);
				}
			} else {
				throw new IllegalArgumentException(
						"Length of heights array does not correspond to number of extruded faces.");
			}
		} else {
			for (int i = 0; i < nf; i++) {
				fc = faces.get(i).getFaceCenter();
				applyStraightToOneFace(i, faces, mesh, visited,
						d.value(fc.xd(), fc.yd(), fc.zd()));
			}
		}
		return mesh;
	}

	/**
	 * Apply straight extrusion to one face.
	 * 
	 * @param id
	 *            the id
	 * @param selfaces
	 *            the selfaces
	 * @param mesh
	 *            the mesh
	 * @param visited
	 *            the visited
	 * @return true, if successful
	 */
	private boolean applyStraightToOneFace(final int id,
			final List<HE_Face> selfaces, final HE_Mesh mesh,
			final boolean[] visited, double d) {

		if (visited[id]) {
			return false;
		}
		final HE_Face f = selfaces.get(id);
		final WB_Vector n = _faceNormals.get(f.key());

		final List<HE_Face> neighborhood = new FastList<HE_Face>();
		neighborhood.add(f);
		f.setLabel(1);
		visited[id] = true;
		int no = 0;
		int nn = 1;
		do {
			nn = neighborhood.size();
			for (int i = no; i < nn; i++) {
				final HE_Face fi = neighborhood.get(i);
				final List<HE_Face> faces = fi.getNeighborFaces();
				for (int j = 0; j < faces.size(); j++) {
					final HE_Face fj = faces.get(j);
					if ((_faceNormals.get(fi.key()) != null)
							&& (_faceNormals.get(fj.key()) != null)) {

						if (_faceNormals.get(fi.key()).isParallel(
								_faceNormals.get(fj.key()))) {
							final int ij = selfaces.indexOf(fj);
							if (ij >= 0) {
								if (!neighborhood.contains(fj)) {
									neighborhood.add(fj);
									fj.setLabel(1);
								}
								visited[ij] = true;
							}
						}
					}
				}
			}
			no = nn;

		} while (neighborhood.size() > nn);

		extruded.addFaces(neighborhood);

		final List<HE_Halfedge> outerHalfedges = new FastList<HE_Halfedge>();
		final List<HE_Halfedge> halfedges = new FastList<HE_Halfedge>();
		final List<HE_Vertex> vertices = new FastList<HE_Vertex>();
		final List<HE_Halfedge> pairHalfedges = new FastList<HE_Halfedge>();
		final List<HE_Vertex> outerVertices = new FastList<HE_Vertex>();
		final List<HE_Vertex> extOuterVertices = new FastList<HE_Vertex>();
		final List<HE_Edge> newEdges = new FastList<HE_Edge>();

		for (int i = 0; i < neighborhood.size(); i++) {
			HE_Halfedge he = neighborhood.get(i).getHalfedge();
			do {
				final HE_Face fp = he.getPair().getFace();
				if ((fp == null) || (!neighborhood.contains(fp))) {
					outerHalfedges.add(he);
				}
				halfedges.add(he);
				if (!vertices.contains(he.getVertex())) {
					vertices.add(he.getVertex());
				}
				he = he.getNextInFace();
			} while (he != neighborhood.get(i).getHalfedge());
		}

		for (int i = 0; i < outerHalfedges.size(); i++) {
			pairHalfedges.add(outerHalfedges.get(i).getPair());
			outerVertices.add(outerHalfedges.get(i).getVertex());
			final HE_Vertex eov = new HE_Vertex(outerHalfedges.get(i)
					.getVertex());

			eov.pos._addSelf(d, n);
			extOuterVertices.add(eov);
			newEdges.add(new HE_Edge());

		}
		mesh.addEdges(newEdges);
		mesh.addVertices(extOuterVertices);
		for (int i = 0; i < vertices.size(); i++) {
			final HE_Vertex v = vertices.get(i);
			if (!outerVertices.contains(v)) {
				v.pos._addSelf(d, n);
			}

		}

		for (int i = 0; i < halfedges.size(); i++) {
			final HE_Halfedge he = halfedges.get(i);
			final int ovi = outerVertices.indexOf(he.getVertex());
			if (ovi >= 0) {
				he.setVertex(extOuterVertices.get(ovi));
				extOuterVertices.get(ovi).setHalfedge(he);
			}

		}
		for (int c = 0; c < outerHalfedges.size(); c++) {
			final HE_Face fNew = new HE_Face();

			walls.add(fNew);
			fNew.setLabel(2);
			final HE_Halfedge heOrig1 = outerHalfedges.get(c);
			final HE_Halfedge heOrig2 = pairHalfedges.get(c);
			final HE_Halfedge heNew1 = new HE_Halfedge();
			final HE_Halfedge heNew2 = new HE_Halfedge();
			final HE_Halfedge heNew3 = new HE_Halfedge();
			final HE_Halfedge heNew4 = new HE_Halfedge();
			final HE_Edge eNew = new HE_Edge();

			HE_Halfedge hen = heOrig1.getNextInFace();
			int cp = -1;
			do {
				cp = outerHalfedges.indexOf(hen);
				hen = hen.getPair().getNextInFace();
			} while ((hen != heOrig1.getNextInFace()) && (cp == -1));

			final HE_Vertex v1 = outerVertices.get(c);
			final HE_Vertex v2 = outerVertices.get(cp);
			final HE_Vertex v4 = extOuterVertices.get(c);
			final HE_Vertex v3 = extOuterVertices.get(cp);
			heNew1.setVertex(v1);
			v1.setHalfedge(heNew1);
			heNew1.setFace(fNew);
			fNew.setHalfedge(heNew1);
			heNew1.setEdge(heOrig2.getEdge());
			heNew1.getEdge().setHalfedge(heNew1);
			heNew1.setPair(heOrig2);

			heNew1.setNext(heNew2);
			heNew2.setVertex(v2);
			heNew2.setEdge(newEdges.get(cp));
			if (heNew2.getEdge().getHalfedge() == null) {
				heNew2.getEdge().setHalfedge(heNew2);
			} else {
				heNew2.setPair(heNew2.getEdge().getHalfedge());
				heNew2.getEdge().getHalfedge().setPair(heNew2);
			}
			v2.setHalfedge(heNew2);
			heNew2.setFace(fNew);
			heNew2.setNext(heNew3);
			heNew3.setVertex(v3);
			v3.setHalfedge(heNew3);
			heNew3.setFace(fNew);
			heNew3.setEdge(eNew);
			heOrig1.setEdge(eNew);
			eNew.setHalfedge(heNew3);
			heNew3.setPair(heOrig1);
			heOrig1.setPair(heNew3);
			heNew3.setNext(heNew4);
			heNew4.setVertex(v4);
			v4.setHalfedge(heNew4);
			heNew4.setFace(fNew);
			heNew4.setNext(heNew1);
			heNew4.setEdge(newEdges.get(c));
			if (heNew4.getEdge().getHalfedge() == null) {
				heNew4.getEdge().setHalfedge(heNew4);
			} else {
				heNew4.setPair(heNew4.getEdge().getHalfedge());
				heNew4.getEdge().getHalfedge().setPair(heNew4);
			}
			heOrig1.setVertex(v4);
			mesh.add(fNew);
			mesh.add(eNew);
			mesh.add(heNew1);
			mesh.add(heNew2);
			mesh.add(heNew3);
			mesh.add(heNew4);
		}

		return true;

	}

	/**
	 * Apply peaked extrusion.
	 * 
	 * @param mesh
	 *            the mesh
	 * @param faces
	 *            the faces
	 * @return mesh
	 */
	private HE_Mesh applyPeaked(final HE_Mesh mesh, final List<HE_Face> faces) {
		final int nf = faces.size();
		HE_Face f;
		WB_Point fc;
		for (int i = 0; i < nf; i++) {
			f = faces.get(i);
			_faceCenters.put(f.key(), f.getFaceCenter());

		}
		if (heights != null) {
			if (heights.length == faces.size()) {
				for (int i = 0; i < nf; i++) {
					applyPeakToOneFace(i, faces, mesh, heights[i]);
				}
			} else {
				throw new IllegalArgumentException(
						"Length of heights array does not correspond to number of extruded faces.");
			}
		} else {
			for (int i = 0; i < nf; i++) {
				fc = faces.get(i).getFaceCenter();
				applyPeakToOneFace(i, faces, mesh,
						d.value(fc.xd(), fc.yd(), fc.zd()));
			}
		}

		return mesh;
	}

	/**
	 * Apply peaked extrusion to one face.
	 * 
	 * @param id
	 *            the id
	 * @param selFaces
	 *            the sel faces
	 * @param mesh
	 *            the mesh
	 */
	private void applyPeakToOneFace(final int id, final List<HE_Face> selFaces,
			final HE_Mesh mesh, double d) {
		final HE_Face f = selFaces.get(id);
		final WB_Vector n = _faceNormals.get(f.key());
		final WB_Point fc = _faceCenters.get(f.key());

		walls.add(f);
		f.setLabel(4);
		final HE_Face[] newFaces = mesh.triSplitFace(f,
				fc._addSelf(n._mulSelf(d))).getFacesAsArray();
		for (final HE_Face newFace : newFaces) {
			newFace.setLabel(4);
		}
		walls.addFaces(newFaces);

	}

	/**
	 * Apply flat extrusion.
	 * 
	 * @param mesh
	 *            the mesh
	 * @param faces
	 *            the faces
	 * @param fuse
	 *            the fuse
	 * @return mesh
	 */
	private HE_Mesh applyFlat(final HE_Mesh mesh, final List<HE_Face> faces,
			final boolean fuse) {

		final HE_Selection sel = new HE_Selection(mesh);
		sel.addFaces(faces);
		sel.collectEdges();

		final List<HE_Edge> originalEdges = sel.getEdgesAsList();
		final int nf = faces.size();
		WB_Point fc;
		if (heights != null) {
			if (heights.length == faces.size()) {
				for (int i = 0; i < nf; i++) {
					if (!applyFlatToOneFace(i, faces, mesh)) {
						failedFaces.add(faces.get(i));
						failedHeights.add(heights[i]);
					}

				}
			} else {
				throw new IllegalArgumentException(
						"Length of heights array does not correspond to number of extruded faces.");
			}
		} else {
			for (int i = 0; i < nf; i++) {
				if (!applyFlatToOneFace(i, faces, mesh)) {
					failedFaces.add(faces.get(i));
					fc = faces.get(i).getFaceCenter();
					failedHeights.add(d.value(fc.xd(), fc.yd(), fc.zd()));
				}
			}
		}

		if (fuse) {
			for (int i = 0; i < originalEdges.size(); i++) {
				final HE_Edge e = originalEdges.get(i);

				final HE_Face f1 = e.getHalfedge().getFace();
				final HE_Face f2 = e.getHalfedge().getPair().getFace();
				if ((f1 != null) && (f2 != null)) {
					if ((f1.getLabel() == 2) && (f2.getLabel() == 2)) {
						if ((f1.getFaceNormal().cross((f2.getFaceNormal()))
								.getSqLength()) < sin2FA) {
							final HE_Face f = mesh.deleteEdge(e);
							if (f != null) {
								f.setLabel(3);
							}
						}
					}
				}

			}
		}

		return mesh;
	}

	/**
	 * Apply flat extrusion to one face.
	 * 
	 * @param id
	 *            the id
	 * @param selFaces
	 *            the sel faces
	 * @param mesh
	 *            the mesh
	 * @return true, if successful
	 */
	private boolean applyFlatToOneFace(final int id,
			final List<HE_Face> selFaces, final HE_Mesh mesh) {
		final HE_Face f = selFaces.get(id);
		final WB_Point fc = _faceCenters.get(f.key());
		final List<HE_Vertex> faceVertices = new FastList<HE_Vertex>();
		final List<HE_Halfedge> faceHalfedges = new FastList<HE_Halfedge>();
		final List<WB_Vector> faceHalfedgeNormals = new FastList<WB_Vector>();
		final List<WB_Point> faceEdgeCenters = new FastList<WB_Point>();
		final List<HE_Vertex> extFaceVertices = new FastList<HE_Vertex>();
		HE_Halfedge he = f.getHalfedge();
		do {
			faceVertices.add(he.getVertex());
			faceHalfedges.add(he);
			faceHalfedgeNormals.add(_halfedgeNormals.get(he.key()));
			faceEdgeCenters.add(he.getHalfedgeCenter());
			extFaceVertices.add(he.getVertex().get());
			he = he.getNextInFace();
		} while (he != f.getHalfedge());

		boolean isPossible = true;
		final int n = faceVertices.size();
		if (relative == true) {
			double ch;
			for (int i = 0; i < n; i++) {
				final HE_Vertex v = faceVertices.get(i);
				final WB_Point diff = fc.sub(v);
				he = faceHalfedges.get(i);
				ch = Math.max(_halfedgeEWs.get(he.key()),
						_halfedgeEWs.get(he.getPrevInFace().key()));
				diff._mulSelf(ch);
				diff._addSelf(v);
				extFaceVertices.get(i)._set(diff);
			}
		} else {
			final double[] d = new double[n];
			for (int i = 0; i < n; i++) {
				d[i] = _halfedgeEWs.get(faceHalfedges.get(i).key());
			}
			if ((chamfer > 0) && (f.getFaceType() == WB_Convex.CONVEX)) {
				final WB_Point[] vPos = new WB_Point[n];
				for (int i = 0; i < n; i++) {
					final HE_Vertex v = faceVertices.get(i);
					vPos[i] = new WB_Point(v);
				}
				final WB_SimplePolygon poly = new WB_SimplePolygon(vPos, n);
				poly.trimConvexPolygon(d);
				if (poly.n == n) {
					final int inew = poly.closestIndex(faceVertices.get(0).pos);

					for (int i = 0; i < n; i++) {
						extFaceVertices.get(i)._set(
								poly.getPoint((inew + i) % n));
					}
				} else if (poly.n > 2) {
					for (int i = 0; i < n; i++) {
						extFaceVertices.get(i)._set(
								poly.closestPoint(faceVertices.get(i).pos));
					}
				} else {
					isPossible = false;
				}
			} else {
				WB_Point v1 = new WB_Point(faceVertices.get(n - 1));
				WB_Point v2 = new WB_Point(faceVertices.get(0));
				for (int i = 0, j = n - 1; i < n; j = i, i++) {
					final WB_Vector n1 = faceHalfedgeNormals.get(j);
					final WB_Vector n2 = faceHalfedgeNormals.get(i);
					final WB_Point v3 = faceVertices.get((i + 1) % n).pos;
					final WB_Segment S1 = new WB_Segment(v1.addMul(d[j], n1),
							v2.addMul(d[j], n1));
					final WB_Segment S2 = new WB_Segment(v2.addMul(d[i], n2),
							v3.addMul(d[i], n2));

					final WB_IntersectionResult ir = WB_Intersection
							.getIntersection3D(S1, S2);
					final WB_Point p = (ir.dimension == 0) ? (WB_Point) ir.object
							: ((Segment) ir.object).getCenter();
					extFaceVertices.get(i)._set(p);
					v1 = v2;
					v2 = v3;

				}
			}
		}
		if (isPossible) {
			extruded.add(f);
			f.setLabel(1);
			final List<HE_Edge> newEdges = new FastList<HE_Edge>();
			final List<HE_Edge> newEdges2 = new FastList<HE_Edge>();
			he = f.getHalfedge();
			do {
				he = he.getNextInFace();
				newEdges.add(new HE_Edge());
			} while (he != f.getHalfedge());
			mesh.addEdges(newEdges);
			int c = 0;
			he = f.getHalfedge();
			do {
				final HE_Face fNew = new HE_Face();
				walls.add(fNew);
				fNew.setLabel(2);
				final HE_Halfedge heOrig1 = he;
				final HE_Halfedge heOrig2 = he.getPair();
				final HE_Halfedge heNew1 = new HE_Halfedge();
				final HE_Halfedge heNew2 = new HE_Halfedge();
				final HE_Halfedge heNew3 = new HE_Halfedge();
				final HE_Halfedge heNew4 = new HE_Halfedge();
				final HE_Edge eNew = new HE_Edge();
				newEdges2.add(eNew);
				final int cp = (c + 1) % faceVertices.size();
				final HE_Vertex v1 = faceVertices.get(c);
				final HE_Vertex v2 = faceVertices.get(cp);
				final HE_Vertex v4 = extFaceVertices.get(c);
				final HE_Vertex v3 = extFaceVertices.get(cp);
				heNew1.setVertex(v1);
				v1.setHalfedge(heNew1);
				heNew1.setFace(fNew);
				fNew.setHalfedge(heNew1);
				heNew1.setEdge(heOrig2.getEdge());
				heNew1.getEdge().setHalfedge(heNew1);
				heNew1.setPair(heOrig2);

				heNew1.setNext(heNew2);
				heNew2.setVertex(v2);
				heNew2.setEdge(newEdges.get(cp));
				if (heNew2.getEdge().getHalfedge() == null) {
					heNew2.getEdge().setHalfedge(heNew2);
				} else {
					heNew2.setPair(heNew2.getEdge().getHalfedge());
					heNew2.getEdge().getHalfedge().setPair(heNew2);
				}
				v2.setHalfedge(heNew2);
				heNew2.setFace(fNew);
				heNew2.setNext(heNew3);
				heNew3.setVertex(v3);
				v3.setHalfedge(heNew3);
				heNew3.setFace(fNew);
				heNew3.setEdge(eNew);
				heOrig1.setEdge(eNew);
				eNew.setHalfedge(heNew3);
				heNew3.setPair(heOrig1);
				heOrig1.setPair(heNew3);
				heNew3.setNext(heNew4);
				heNew4.setVertex(v4);
				v4.setHalfedge(heNew4);
				heNew4.setFace(fNew);
				heNew4.setNext(heNew1);
				heNew4.setEdge(newEdges.get(c));
				if (heNew4.getEdge().getHalfedge() == null) {
					heNew4.getEdge().setHalfedge(heNew4);
				} else {
					heNew4.setPair(heNew4.getEdge().getHalfedge());
					heNew4.getEdge().getHalfedge().setPair(heNew4);
				}
				heOrig1.setVertex(v4);
				mesh.add(fNew);
				mesh.add(eNew);
				mesh.add(v3);
				mesh.add(heNew1);
				mesh.add(heNew2);
				mesh.add(heNew3);
				mesh.add(heNew4);
				he = he.getNextInFace();
				c++;
			} while (he != f.getHalfedge());
			final List<HE_Edge> edgesToRemove = new FastList<HE_Edge>();
			for (int i = 0; i < newEdges2.size(); i++) {
				final HE_Edge e = newEdges2.get(i);
				if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(
						e.getStartVertex(), e.getEndVertex()))) {
					edgesToRemove.add(e);
				}
			}
			for (int i = 0; i < edgesToRemove.size(); i++) {
				mesh.collapseEdge(edgesToRemove.get(i));
			}

		}
		return isPossible;
	}
}
