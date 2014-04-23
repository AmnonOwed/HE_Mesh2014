package wblut.hemesh;

import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;
import wblut.WB_Epsilon;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;

public class HES_TriDec extends HES_Simplifier {

	private double _lambda;

	private Heap<HE_Halfedge> heap;

	FastMap<Long, Double> vertexCost;

	private int rep;

	public HES_TriDec() {
		_lambda = 10;

	}

	public HES_TriDec setLambda(final double f) {
		_lambda = f;
		return this;
	}

	public HES_TriDec setRep(final int r) {
		rep = r;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.simplifiers.HES_Simplifier#apply(wblut.hemesh.core.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		mesh.triangulate();
		if (mesh.getNumberOfVertices() < 4) {
			return mesh;
		}
		buildHeap(mesh);
		for (int i = 0; i < rep; i++) {
			if (heap.size() > 0) {
				final HE_Halfedge he = heap.pop();
				final List<HE_Vertex> vertices = he.getVertex()
						.getNeighborVertices();
				if (mesh.collapseHalfedge(he)) {
					vertexCost.remove(he.getVertex().key());
					buildHeap(mesh);
				}
			}

		}
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.simplifiers.HES_Simplifier#apply(wblut.hemesh.core.HE_Selection
	 * )
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		HE_Selection sel = selection.get();
		sel.collectFaces();
		sel.collectVertices();
		selection.parent.triangulate();
		if (selection.parent.getNumberOfVertices() < 4) {
			return selection.parent;
		}
		buildHeap(sel);
		for (int i = 0; i < rep; i++) {
			if (heap.size() > 0) {
				final HE_Halfedge he = heap.pop();
				final List<HE_Vertex> vertices = he.getVertex()
						.getNeighborVertices();
				if (selection.parent.collapseHalfedge(he)) {
					vertexCost.remove(he.getVertex().key());
					buildHeap(sel);
				}
			}

		}
		return selection.parent;

	}

	private void buildHeap(HE_MeshStructure sel) {
		heap = new Heap<HE_Halfedge>();
		vertexCost = new FastMap<Long, Double>(sel.getNumberOfVertices());
		final Iterator<HE_Vertex> vItr = sel.vItr();
		while (vItr.hasNext()) {
			final HE_Vertex v = vItr.next();
			final double vvi = visualImportance(v);

			vertexCost.put(v.key(), vvi);
			final List<HE_Halfedge> vstar = v.getHalfedgeStar();
			HE_Halfedge minhe = vstar.get(0);
			double min = halfedgeCollapseCost(vstar.get(0));
			for (int i = 1; i < vstar.size(); i++) {
				final double c = halfedgeCollapseCost(vstar.get(i));
				if (c < min) {
					min = c;
					minhe = vstar.get(i);
				}
			}
			heap.push(min * vvi, minhe);

		}
	}

	private double visualImportance(final HE_Vertex v) {
		final List<HE_Face> faces = v.getFaceStar();
		final WB_Vector nom = new WB_Vector();
		double denom = 0.0;
		double A;
		for (final HE_Face f : faces) {
			A = f.getFaceArea();
			nom._addSelf(A, f.getFaceNormal());
			denom += A;
		}
		if (WB_Epsilon.isZero(denom)) {
			throw new IllegalArgumentException(
					"HES_TriDec: can't simplify meshes with degenerate faces.");
		}
		nom._divSelf(denom);
		return 1.0 - nom.getLength();
	}

	private double halfedgeCollapseCost(final HE_Halfedge he) {
		final List<HE_Face> faces = new FastList<HE_Face>();
		final HE_Face f = he.getFace();
		final HE_Face fp = he.getPair().getFace();
		if ((f == null) || (fp == null)) {
			return Double.POSITIVE_INFINITY;
		}
		double cost = 0.0;
		HE_Halfedge helooper = he.getNextInVertex();
		do {
			final HE_Face fl = helooper.getFace();
			if (fl != null) {
				if ((fl != f) && (fl != fp)) {
					final WB_Triangle T = new WB_Triangle(
							he.getEndVertex().pos, helooper.getNextInFace()
									.getVertex().pos, helooper.getNextInFace()
									.getNextInFace().getVertex().pos);
					final WB_Plane P = T.getPlane();
					if (P == null) {
						cost += 0.5 * (T.getArea() + fl.getFaceArea());
					} else {
						cost += 0.5
								* (T.getArea() + fl.getFaceArea())
								* (1.0 - fl.getFaceNormal().dot(
										T.getPlane().getNormal()));
					}

				}
			} else {
				return Double.POSITIVE_INFINITY;
			}
			helooper = helooper.getNextInVertex();

		} while (helooper != he);
		if ((f == null) || (fp == null)) {
			HE_Halfedge boundary = he.getNextInVertex();
			while ((he.getFace() != null) && (he.getPair().getFace() != null)) {
				boundary = boundary.getNextInVertex();
			}
			final WB_Vector v1 = he.getEndVertex().pos.subToVector(he
					.getVertex());
			v1._normalizeSelf();
			final WB_Vector v2 = boundary.getEndVertex().pos
					.subToVector(boundary.getVertex());
			v2._normalizeSelf();
			cost += he.getEdge().getLength() * (1.0 - v1.dot(v2)) * _lambda;
		}
		return cost;

	}
}
