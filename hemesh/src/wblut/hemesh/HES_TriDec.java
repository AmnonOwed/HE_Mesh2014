package wblut.hemesh;

import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.hash.TLongDoubleHashMap;

import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

public class HES_TriDec extends HES_Simplifier {

	private double _lambda;
	private HE_Mesh _mesh;
	private Heap<HE_Halfedge> heap;

	TLongDoubleMap vertexCost;

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
		_mesh = mesh;
		_mesh.triangulate();
		if (_mesh.getNumberOfVertices() < 4) {
			return _mesh;
		}
		buildHeap(_mesh);
		HE_Halfedge he;
		List<HE_Vertex> vertices;
		for (int i = 0; i < rep; i++) {
			if (heap.size() > 0) {
				do {
					he = heap.pop();
				} while (!_mesh.contains(he));
				vertices = he.getVertex().getNeighborVertices();
				if (_mesh.collapseHalfedge(he)) {
					vertexCost.remove(he.getVertex().key());
					// heap.remove(he);
					updateHeap(vertices);
				}
			}
		}
		return _mesh;
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

		return apply(selection.parent);

	}

	private void buildHeap(final HE_MeshStructure sel) {
		heap = new Heap<HE_Halfedge>();
		vertexCost = new TLongDoubleHashMap(10, 0.5f, -1L, Double.NaN);
		final Iterator<HE_Vertex> vItr = sel.vItr();

		double min;
		double c;
		HE_Halfedge minhe;
		List<HE_Halfedge> vstar;
		HE_Vertex v;
		double vvi;
		while (vItr.hasNext()) {
			v = vItr.next();
			vvi = visualImportance(v);

			vertexCost.put(v.key(), vvi);
			vstar = v.getHalfedgeStar();
			minhe = vstar.get(0);
			min = halfedgeCollapseCost(vstar.get(0));

			for (int i = 1; i < vstar.size(); i++) {
				c = halfedgeCollapseCost(vstar.get(i));
				if (c < min) {
					min = c;
					minhe = vstar.get(i);
				}
			}
			heap.push(min * vvi, minhe);

		}
	}

	private void updateHeap(final List<HE_Vertex> vertices) {
		double min;
		double c;
		HE_Halfedge minhe;
		List<HE_Halfedge> vstar;
		double vvi;
		for (final HE_Vertex v : vertices) {
			vvi = visualImportance(v);
			vertexCost.remove(v.key());
			vertexCost.put(v.key(), vvi);
			vstar = v.getHalfedgeStar();
			minhe = vstar.get(0);

			// heap.remove(vstar.get(0));
			min = halfedgeCollapseCost(vstar.get(0));
			for (int i = 1; i < vstar.size(); i++) {
				c = halfedgeCollapseCost(vstar.get(i));
				// heap.remove(vstar.get(i));
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
			nom._addMulSelf(A, f.getFaceNormal());
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
		final List<HE_Face> faces = new FastTable<HE_Face>();
		final HE_Face f = he.getFace();
		final HE_Face fp = he.getPair().getFace();
		if ((f == null) || (fp == null)) {
			return Double.POSITIVE_INFINITY;
		}
		double cost = 0.0;
		HE_Halfedge helooper = he.getNextInVertex();
		WB_Triangle T;
		WB_Plane P;
		do {
			final HE_Face fl = helooper.getFace();
			if (fl != null) {
				if ((fl != f) && (fl != fp)) {
					T = new WB_Triangle(he.getEndVertex(), helooper
							.getNextInFace().getVertex(), helooper
							.getNextInFace().getNextInFace().getVertex());
					P = T.getPlane();
					if (P == null) {
						cost += 0.5 * (T.getArea() + fl.getFaceArea());
					}
					else {
						cost += 0.5
								* (T.getArea() + fl.getFaceArea())
								* (1.0 - fl.getFaceNormal().dot(
										T.getPlane().getNormal()));
					}

				}
			}
			else {
				return Double.POSITIVE_INFINITY;
			}
			helooper = helooper.getNextInVertex();

		} while (helooper != he);

		HE_Halfedge boundary;
		WB_Vector v1;
		WB_Vector v2;
		if ((f == null) || (fp == null)) {
			boundary = he.getNextInVertex();
			while ((he.getFace() != null) && (he.getPair().getFace() != null)) {
				boundary = boundary.getNextInVertex();
			}
			v1 = he.getEndVertex().getPoint().subToVector(he.getVertex());
			v1._normalizeSelf();
			v2 = boundary.getEndVertex().getPoint()
					.subToVector(boundary.getVertex());
			v2._normalizeSelf();
			cost += he.getEdge().getLength() * (1.0 - v1.dot(v2)) * _lambda;
		}
		return cost;

	}

	public class Heap<E> {

		private final List<E> heap;

		private final List<Double> keys;

		public Heap() {
			heap = new FastTable<E>();
			keys = new FastTable<Double>();
		}

		public void push(final Double key, final E obj) {

			heap.add(obj);
			keys.add(key);
			pushUp(heap.size() - 1);
		}

		public E pop() {
			if (heap.size() > 0) {
				swap(0, heap.size() - 1);
				final E result = heap.remove(heap.size() - 1);
				keys.remove(heap.size());
				pushDown(0);
				return result;
			}
			else {
				return null;
			}
		}

		public E getFirst() {
			return heap.get(0);
		}

		public double getFirstKey() {
			return keys.get(0);
		}

		public E get(final int index) {
			return heap.get(index);
		}

		public int size() {
			return heap.size();
		}

		protected int parent(final int i) {
			return (i - 1) / 2;
		}

		protected int left(final int i) {
			return 2 * i + 1;
		}

		protected int right(final int i) {
			return 2 * i + 2;
		}

		protected boolean hasPriority(final int i, final int j) {
			return keys.get(i) <= keys.get(j);
		}

		protected void swap(final int i, final int j) {
			final E tmp = heap.get(i);
			heap.set(i, heap.get(j));
			heap.set(j, tmp);
			final Double tmpv = keys.get(i);
			keys.set(i, keys.get(j));
			keys.set(j, tmpv);
		}

		public void pushDown(final int i) {
			final int left = left(i);
			final int right = right(i);
			int highest = i;

			if (left < heap.size() && !hasPriority(highest, left)) {
				highest = left;
			}
			if (right < heap.size() && !hasPriority(highest, right)) {
				highest = right;
			}

			if (highest != i) {
				swap(highest, i);
				pushDown(highest);
			}
		}

		public void pushUp(int i) {
			while (i > 0 && !hasPriority(parent(i), i)) {
				swap(parent(i), i);
				i = parent(i);
			}
		}

		public void remove(final E obj) {
			final int i = heap.indexOf(obj);
			if (i > -1) {
				heap.remove(i);
				keys.remove(i);
			}
			rebuild();

		}

		public void removeNoRebuild(final E obj) {
			final int i = heap.indexOf(obj);
			if (i > -1) {
				heap.remove(i);
				keys.remove(i);
			}

		}

		public void rebuild() {
			final List<E> cheap = new FastTable<E>();
			final List<Double> ckeys = new FastTable<Double>();
			cheap.addAll(heap);
			ckeys.addAll(keys);
			heap.clear();
			keys.clear();
			for (int i = 0; i < cheap.size(); i++) {
				push(ckeys.get(i), cheap.get(i));
			}

		}

		@Override
		public String toString() {
			final StringBuffer s = new StringBuffer("Heap:\n");
			int rowStart = 0;
			int rowSize = 1;
			for (int i = 0; i < heap.size(); i++) {
				if (i == rowStart + rowSize) {
					s.append('\n');
					rowStart = i;
					rowSize *= 2;
				}
				s.append(get(i));
				s.append(" ");
			}
			return s.toString();
		}

	}

}
