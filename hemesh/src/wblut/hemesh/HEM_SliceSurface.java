package wblut.hemesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import javolution.util.FastTable;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Classification;
import wblut.geom.WB_Classify;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_Plane;

/**
 * Planar cut of a mesh. No faces are removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_SliceSurface extends HEM_Modifier {

	/** Cut plane. */
	private WB_Plane P;

	/** Stores cut faces. */
	public HE_Selection cut;

	/** Stores new edges. */
	public HE_Selection cutEdges;

	private List<HE_Path> paths;

	/**
	 * Instantiates a new HEM_SliceSurface.
	 */
	public HEM_SliceSurface() {
		super();
	}

	/**
	 * Set cut plane.
	 *
	 * @param P
	 *            cut plane
	 * @return self
	 */
	public HEM_SliceSurface setPlane(final WB_Plane P) {
		this.P = P;
		return this;
	}

	public HEM_SliceSurface setPlane(final double ox, final double oy,
			final double oz, final double nx, final double ny, final double nz) {
		P = new WB_Plane(ox, oy, oz, nx, ny, nz);
		return this;
	}

	private double offset;

	/**
	 * Set offset.
	 *
	 * @param d
	 *            offset
	 * @return self
	 */
	public HEM_SliceSurface setOffset(final double d) {
		offset = d;
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {

		tracker.setStatus("Starting HEM_SliceSurface.");
		cut = new HE_Selection(mesh);
		cutEdges = new HE_Selection(mesh);
		paths = new FastTable<HE_Path>();
		// no plane defined
		if (P == null) {
			tracker.setStatus("No cutplane defined. Exiting HEM_SliceSurface.");
			return mesh;
		}

		// empty mesh
		if (mesh.getNumberOfVertices() == 0) {
			tracker.setStatus("Empty mesh. Exiting HEM_SliceSurface.");
			return mesh;
		}

		// check if plane intersects mesh
		final WB_Plane lP = new WB_Plane(P.getNormal(), P.d() + offset);
		if (!WB_Intersection.checkIntersection3D(mesh.getAABB(), lP)) {
			tracker.setStatus("Plane doesn't intersect bounding box. Exiting HEM_SliceSurface.");
			return mesh;
		}
		tracker.setStatus("Creating bounding box tree.");
		final WB_AABBTree tree = new WB_AABBTree(mesh, 64);

		final HE_Selection faces = new HE_Selection(mesh);
		tracker.setStatus("Retrieving intersection candidates.");
		faces.addFaces(HE_Intersection.getPotentialIntersectedFaces(tree, lP));
		faces.collectVertices();
		faces.collectEdgesByFace();
		WB_Classification tmp;
		final HashMap<Long, WB_Classification> vertexClass = new HashMap<Long, WB_Classification>();
		tracker.setStatus("Classifying vertices.", faces.getNumberOfVertices());
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = faces.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = lP.classifyPointToPlane(v);
			vertexClass.put(v.key(), tmp);
			tracker.incrementCounter();
		}
		tracker.setStatus("Classifying edges.", faces.getNumberOfEdges());
		List<HE_Vertex> faceVertices = new ArrayList<HE_Vertex>();
		final HE_Selection split = new HE_Selection(mesh);

		final FastMap<Long, Double> edgeInt = new FastMap<Long, Double>();
		final Iterator<HE_Halfedge> eItr = faces.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.ON) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					cutEdges.add(e);
					e.setInternalLabel(1);
					e.getPair().setInternalLabel(1);
				}
				else {
					edgeInt.put(e.key(), 0.0);
				}
			}
			else if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.BACK) {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					edgeInt.put(e.key(), 1.0);
				}
				else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.FRONT) {
					edgeInt.put(e.key(), HE_Intersection.getIntersection(e, lP));
				}
			}
			else {
				if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
					edgeInt.put(e.key(), 1.0);
				}
				else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.BACK) {
					edgeInt.put(e.key(), HE_Intersection.getIntersection(e, lP));
				}
			}
			tracker.incrementCounter();

		}

		tracker.setStatus("Indexing edge intersection.", edgeInt.size());
		for (final Map.Entry<Long, Double> en : edgeInt.entrySet()) {
			final HE_Halfedge ce = mesh.getHalfedgeByKey(en.getKey());
			final double u = en.getValue();
			if (ce.getFace() != null) {

				split.add(ce.getFace());

			}
			if (ce.getPair().getFace() != null) {

				split.add(ce.getPair().getFace());

			}
			if (u == 0.0) {

				split.add(ce.getStartVertex());

			}
			else if (u == 1.0) {

				split.add(ce.getEndVertex());

			}
			else {
				split.add(mesh.splitEdge(ce, u).vItr().next());

			}
			tracker.incrementCounter();
		}

		tracker.setStatus("Splitting faces.", split.getNumberOfFaces());
		HE_Face f;
		final Iterator<HE_Face> fItr = split.fItr();

		while (fItr.hasNext()) {
			f = fItr.next();
			faceVertices = f.getFaceVertices();
			int firstVertex = -1;
			int secondVertex = -1;
			final int n = faceVertices.size();
			for (int j = 0; j < n; j++) {
				v = faceVertices.get(j);
				if (split.contains(v)) {
					if (firstVertex == -1) {
						firstVertex = j;
						j++;// if one cut point is found, skip next point.
						// There should be at least one other vertex in
						// between for a proper cut.
					}
					else {
						secondVertex = j;
						break;
					}
				}
			}
			if ((firstVertex != -1) && (secondVertex != -1)) {
				final int fo = f.getFaceOrder();
				int diff = Math.abs(firstVertex - secondVertex);
				if (diff == fo - 1) {
					diff = 1;
				}

				if (diff > 1) {
					cut.add(f);
					final HE_Selection out = mesh.splitFace(f,
							faceVertices.get(firstVertex),
							faceVertices.get(secondVertex));
					final HE_Face nf = out.fItr().next();
					cut.add(nf);
					final HE_Halfedge ne = out.eItr().next();
					ne.setInternalLabel(1);
					cutEdges.add(ne);
				}
			}
			tracker.incrementCounter();

		}

		buildPaths(cutEdges);
		tracker.setStatus("Exiting HEM_SliceSurface.");
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {

		tracker.setStatus("Starting HEM_SliceSurface.");
		cut = new HE_Selection(selection.parent);
		cutEdges = new HE_Selection(selection.parent);
		paths = new FastTable<HE_Path>();
		// no plane defined
		if (P == null) {
			tracker.setStatus("No cutplane defined. Exiting HEM_SliceSurface.");
			return selection.parent;

		}

		// empty mesh
		if (selection.parent.getNumberOfVertices() == 0) {
			tracker.setStatus("Empty vertex selection. Exiting HEM_SliceSurface.");
			return selection.parent;
		}
		final WB_Plane lP = new WB_Plane(P.getNormal(), P.d() + offset);
		tracker.setStatus("Creating bounding box tree.");
		final WB_AABBTree tree = new WB_AABBTree(selection.parent, 64);

		final HE_Selection faces = new HE_Selection(selection.parent);
		tracker.setStatus("Retrieving intersection candidates.");
		faces.addFaces(HE_Intersection.getPotentialIntersectedFaces(tree, lP));

		final HE_Selection lsel = selection.get();
		lsel.intersect(faces);
		lsel.collectEdgesByFace();
		lsel.collectVertices();

		// empty mesh
		if (lsel.getNumberOfVertices() == 0) {
			tracker.setStatus("Plane doesn't intersect bounding box tree. Exiting HEM_SliceSurface.");
			return lsel.parent;
		}

		// check if plane intersects mesh
		boolean positiveVertexExists = false;
		boolean negativeVertexExists = false;
		WB_Classification tmp;
		final FastMap<Long, WB_Classification> vertexClass = new FastMap<Long, WB_Classification>();
		HE_Vertex v;
		tracker.setStatus("Classifying vertices.", lsel.getNumberOfVertices());
		final Iterator<HE_Vertex> vItr = lsel.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			tmp = lP.classifyPointToPlane(v);
			vertexClass.put(v.key(), tmp);
			if (tmp == WB_Classification.FRONT) {
				positiveVertexExists = true;

			}
			if (tmp == WB_Classification.BACK) {
				negativeVertexExists = true;
			}
			tracker.incrementCounter();
		}

		if (positiveVertexExists && negativeVertexExists) {
			List<HE_Vertex> faceVertices = new ArrayList<HE_Vertex>();
			final HE_Selection split = new HE_Selection(lsel.parent);

			final HashMap<Long, Double> edgeInt = new HashMap<Long, Double>();
			final Iterator<HE_Halfedge> eItr = lsel.eItr();
			HE_Halfedge e;
			tracker.setStatus("Classifying edges.", lsel.getNumberOfEdges());
			while (eItr.hasNext()) {
				e = eItr.next();
				if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.ON) {
					if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
						cutEdges.add(e);
						e.setInternalLabel(1);
					}
					else {
						edgeInt.put(e.key(), 0.0);
					}
				}
				else if (vertexClass.get(e.getStartVertex().key()) == WB_Classification.BACK) {
					if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
						edgeInt.put(e.key(), 1.0);
					}
					else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.FRONT) {
						edgeInt.put(e.key(),
								HE_Intersection.getIntersection(e, lP));
					}
				}
				else {
					if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.ON) {
						edgeInt.put(e.key(), 1.0);
					}
					else if (vertexClass.get(e.getEndVertex().key()) == WB_Classification.BACK) {
						edgeInt.put(e.key(),
								HE_Intersection.getIntersection(e, lP));
					}
				}
				tracker.incrementCounter();
			}

			tracker.setStatus("Indexing edge intersection.", edgeInt.size());
			for (final Map.Entry<Long, Double> en : edgeInt.entrySet()) {
				final HE_Halfedge ce = lsel.parent
						.getHalfedgeByKey(en.getKey());
				final double u = en.getValue();
				if (lsel.contains(ce.getFace())) {
					split.add(ce.getFace());
				}
				if (lsel.contains(ce.getPair().getFace())) {
					split.add(ce.getPair().getFace());
				}
				if (u == 0.0) {
					split.add(ce.getStartVertex());

				}
				else if (u == 1.0) {

					split.add(ce.getEndVertex());

				}
				else {
					split.add(lsel.parent.splitEdge(ce, u).vItr().next());

				}
				tracker.incrementCounter();
			}
			HE_Face f;
			tracker.setStatus("Splitting faces.", split.getNumberOfFaces());
			final Iterator<HE_Face> fItr = split.fItr();
			while (fItr.hasNext()) {
				f = fItr.next();
				faceVertices = f.getFaceVertices();
				int firstVertex = -1;
				int secondVertex = -1;
				final int n = faceVertices.size();
				for (int j = 0; j < n; j++) {
					v = faceVertices.get(j);
					if (split.contains(v)) {
						if (firstVertex == -1) {
							firstVertex = j;
							j++;// if one cut point is found, skip next point.
							// There should be at least one other vertex in
							// between for a proper cut.
						}
						else {
							secondVertex = j;
							break;
						}
					}
				}
				if ((firstVertex != -1) && (secondVertex != -1)) {
					final int fo = f.getFaceOrder();
					int diff = Math.abs(firstVertex - secondVertex);
					if (diff == fo - 1) {
						diff = 1;
					}
					if (diff > 1) {
						cut.add(f);
						final HE_Selection out = lsel.parent.splitFace(f,
								faceVertices.get(firstVertex),
								faceVertices.get(secondVertex));

						final HE_Face nf = out.fItr().next();
						cut.add(nf);
						final HE_Halfedge ne = out.eItr().next();
						ne.setInternalLabel(1);
						cutEdges.add(ne);
					}
				}
				tracker.incrementCounter();
			}
			buildPaths(cutEdges);
		}
		tracker.setStatus("Exiting HEM_SliceSurface.");
		return lsel.parent;
	}

	private void buildPaths(final HE_Selection cutEdges) {
		tracker.setStatus("Building slice paths.");
		paths = new FastTable<HE_Path>();
		if (cutEdges.getNumberOfEdges() == 0) {
			return;
		}
		final List<HE_Halfedge> edges = new FastTable<HE_Halfedge>();

		for (final HE_Halfedge he : cutEdges.getEdgesAsList()) {
			final HE_Face f = he.getFace();
			if (WB_Classify.classifyPointToPlane(P, f.getFaceCenter()) == WB_Classification.FRONT) {

				edges.add(he.getPair());

			}
			else {

				edges.add(he);

			}
		}
		tracker.setStatus("Processing slice edges.", edges.size());
		while (edges.size() > 0) {
			final List<HE_Halfedge> pathedges = new FastTable<HE_Halfedge>();
			HE_Halfedge current = edges.get(0);
			pathedges.add(current);
			boolean loop = false;
			for (int i = 0; i < edges.size(); i++) {

				if (edges.get(i).getVertex() == current.getEndVertex()) {

					if (i > 0) {
						current = edges.get(i);

						pathedges.add(current);
						i = -1;
					}
					else {

						loop = true;
						break;
					}
				}
			}

			if (!loop) {
				final List<HE_Halfedge> reversepathedges = new FastTable<HE_Halfedge>();
				current = edges.get(0);
				for (int i = 0; i < edges.size(); i++) {
					if (edges.get(i).getEndVertex() == current.getVertex()) {
						if (i > 0) {
							current = edges.get(i);
							reversepathedges.add(current);
							i = 0;
						}
					}
				}
				final List<HE_Halfedge> finalpathedges = new FastTable<HE_Halfedge>();
				for (int i = reversepathedges.size() - 1; i > -1; i--) {
					finalpathedges.add(reversepathedges.get(i));
				}
				finalpathedges.addAll(pathedges);
				paths.add(new HE_Path(finalpathedges, loop));
				edges.removeAll(finalpathedges);
			}
			else {
				paths.add(new HE_Path(pathedges, loop));
				edges.removeAll(pathedges);
			}
			tracker.incrementCounter(pathedges.size());

		}
	}

	/**
	 *
	 * @return List of HE_Path created by the cutting plane
	 */
	public List<HE_Path> getPaths() {
		return paths;
	}

}
