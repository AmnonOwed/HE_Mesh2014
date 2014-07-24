package wblut.external.ProGAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import wblut.external.ProGAL.ExactJavaPredicates.SphereConfig;

/**
 * Part of ProGAL: http://www.diku.dk/~rfonseca/ProGAL/
 *
 * Original copyright notice:
 *
 * Copyright (c) 2013, Dept. of Computer Science - Univ. of Copenhagen. All
 * rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * <p>
 * An alpha complex for a set of d-dimensional points and a real number alpha is
 * a subset of the Delaunay complex where all simplices that can be enclosed by
 * an alpha-probe (a hypersphere of radius alpha), without the probe enclosing
 * any points, are removed.
 * </p>
 *
 * @author R. Fonseca
 */
public class AlphaComplex implements SimplicialComplex {

	private final DelaunayComplex del3d;
	private final ExactJavaPredicates p = new ExactJavaPredicates();
	private final AlphaComparator alphaOrdering = new AlphaComparator();
	private final Map<Simplex, SimplexAlphaProperties> propertyMap = new HashMap<Simplex, SimplexAlphaProperties>();

	protected ArrayList<CTetrahedron> tetrahedra = new ArrayList<CTetrahedron>();
	protected ArrayList<CTriangle> triangles = new ArrayList<CTriangle>();
	protected ArrayList<CEdge> edges = new ArrayList<CEdge>();
	protected ArrayList<CVertex> vertices = new ArrayList<CVertex>();
	protected ArrayList<Simplex> simplices = new ArrayList<Simplex>();
	protected double alpha;

	private Map<Simplex, Integer> depthMap = null;

	/**
	 * Build the alpha-complex of the specified point-list. Note that an entire
	 * Delaunay complex is built as part of this constructor.
	 */

	public AlphaComplex(final List<Point> pl) {
		this(new DelaunayComplex(pl), Double.MAX_VALUE);
	}

	public AlphaComplex(final List<Point> pl, final double alpha) {
		this(new DelaunayComplex(pl), alpha);
	}

	/** Build the alpha-complex of the specified Delaunay complex. */
	public AlphaComplex(final DelaunayComplex delaunayComplex,
			final double alpha) {
		this.del3d = delaunayComplex;
		compute();
		this.alpha = alpha;
	}

	private void compute() {
		computeTetrahedraIntervals();
		computeTriangleIntervals();
		computeEdgeIntervals();
		computeVertexIntervals();
		simplices.addAll(vertices);
		simplices.addAll(edges);
		simplices.addAll(triangles);
		simplices.addAll(tetrahedra);
		Collections.sort(simplices, alphaOrdering);
	}

	public List<Simplex> getSimplices() {
		return simplices;
	}

	@Override
	public List<CTetrahedron> getTetrahedra() {
		return tetrahedra;
	}

	@Override
	public List<CTriangle> getTriangles() {
		return triangles;
	}

	@Override
	public List<CEdge> getEdges() {
		return edges;
	}

	/**
	 * Get a list of tetrahedra that are part of the alpha-complex with the
	 * specified probe radius.
	 */
	public List<CTetrahedron> getTetrahedra(final double a) {
		final List<CTetrahedron> ret = new ArrayList<CTetrahedron>();
		for (final CTetrahedron t : tetrahedra) {
			if (getInAlpha(t) < a) {
				ret.add(t);
			}
		}
		return ret;
	}

	/**
	 * Get a list of tetrahedra that are part of the alpha-complex with the
	 * probe radius in a specified interval. Added by PW
	 */
	public List<CTetrahedron> getTetrahedra(final double alphaLow,
			final double alphaHigh) {
		final List<CTetrahedron> ret = new ArrayList<CTetrahedron>();
		for (final CTetrahedron t : tetrahedra) {
			if ((getInAlpha(t) >= alphaLow) && (getInAlpha(t) < alphaHigh)) {
				ret.add(t);
			}
		}
		return ret;
	}

	/**
	 * Get a list of triangles that are part of the alpha-complex with the
	 * specified probe radius.
	 */
	public List<CTriangle> getTriangles(final double a) {
		final List<CTriangle> ret = new ArrayList<CTriangle>();
		for (final CTriangle t : triangles) {
			if (getInAlpha(t) < alpha) {
				ret.add(t);
			}
		}
		return ret;
	}

	/** Returns triangles in one tetrahedron only */
	public List<Triangle> getSurfaceTriangles(final double a) {
		final List<CTetrahedron> alphatetrahedra = getTetrahedra(a);
		final List<Triangle> alphatriangles = new ArrayList<Triangle>();
		for (final CTetrahedron tetrahedron : alphatetrahedra) {
			for (int i = 0; i < 4; i++) {
				final CTetrahedron neighbor = tetrahedron.getNeighbour(i);
				if ((neighbor == null) || (getInAlpha(neighbor) >= a)) {
					alphatriangles.add(tetrahedron.getTriangle(i));
				}
			}
		}
		return alphatriangles;
	}

	/**
	 * Get a list of edges that are part of the alpha-complex with the specified
	 * probe radius.
	 */
	public List<CEdge> getEdges(final double alpha) {
		final List<CEdge> ret = new ArrayList<CEdge>();
		for (final CEdge t : edges) {
			if (getInAlpha(t) < alpha) {
				ret.add(t);
			}
		}
		return ret;
	}

	/** Get a list of the vertices of the complex. */
	@Override
	public List<CVertex> getVertices() {
		return new ArrayList<CVertex>(vertices);
	}

	/**
	 * Get a list of simplices that are part of the alpha-complex with the
	 * specified probe radius.
	 */
	public List<Simplex> getSimplices(final double alpha) {
		final List<Simplex> ret = new ArrayList<Simplex>();
		for (final Simplex s : simplices) {
			if (getInAlpha(s) < alpha) {
				ret.add(s);
			}
		}
		return ret;
	}

	public List<CTriangle> getAlphaShape(final double alpha) {
		final List<CTriangle> ret = new ArrayList<CTriangle>();
		for (final CTriangle t : getTriangles(alpha)) {
			try {
				final double a0 = getInAlpha(t.getAdjacentTetrahedron(0));
				final double a1 = getInAlpha(t.getAdjacentTetrahedron(1));
				if ((a0 > alpha) ^ (a1 > alpha)) {
					ret.add(t);
				}
			}
			catch (final NullPointerException exc) {
				ret.add(t);
			}
		}
		return ret;
	}

	public int getDim(final Simplex s) {
		final SimplexAlphaProperties prop = propertyMap.get(s);
		return prop.getSimplexType();
	}

	/**
	 * Return the probe-radius at which the simplex <code>s</code> enters the
	 * alpha complex.
	 */
	public double getInAlpha(final Simplex s) {
		return propertyMap.get(s).getInAlphaComplex();
	}

	public boolean getAttached(final Simplex s) {
		return propertyMap.get(s).isAttached();
	}

	/**
	 * Return true iff the simplex is on the convex hull. Calling this method
	 * with a CTetrahedra will throw an error.
	 */
	public boolean getOnCH(final Simplex s) {
		final SimplexAlphaProperties prop = propertyMap.get(s);
		switch (prop.getSimplexType()) {
		case 0:
			return ((VertexAlphaProperties) prop).getOnConvexHull();
		case 1:
			return ((EdgeAlphaProperties) prop).getOnConvexHull();
		case 2:
			return ((TriangleAlphaProperties) prop).getOnConvexHull();
		case 3:
			throw new Error("Tetrahedrons are never completely on convex hull");
		}
		throw new Error("Undefined simplex type");
	}

	private void computeTetrahedraIntervals() { // this method does the actual
		// interval computation
		for (final CTetrahedron t : del3d.getTetrahedra()) {
			tetrahedra.add(t);

			final double r = p.circumradius(t);
			final TetrahedronAlphaProperties properties = new TetrahedronAlphaProperties(
					r);
			propertyMap.put(t, properties);
		}
		Collections.sort(tetrahedra, alphaOrdering);
	}

	private void computeTriangleIntervals() {
		for (final CTriangle tri : del3d.getTriangles()) {
			triangles.add(tri);

			final boolean ch = tri.getAdjacentTetrahedron(0).containsBigPoint()
					|| tri.getAdjacentTetrahedron(1).containsBigPoint();
			final boolean att = p.insphere(tri, tri.getAdjacentTetrahedron(0)
					.oppositeVertex(tri)) == SphereConfig.INSIDE
					|| p.insphere(tri, tri.getAdjacentTetrahedron(1)
							.oppositeVertex(tri)) == SphereConfig.INSIDE;
			final double minmu = triminmu(tri, ch);
			final double maxmu = trimaxmu(tri, ch);
			final double rho = p.circumradius(tri);
			final TriangleAlphaProperties prop = new TriangleAlphaProperties(
					minmu, maxmu, rho, ch, att);
			propertyMap.put(tri, prop);
		}
		Collections.sort(triangles, alphaOrdering);
	}

	private double triminmu(final CTriangle tri, final boolean ch) { // computes
																		// minmu
		if (tri.getAdjacentTetrahedron(0).containsBigPoint()) {
			return getInAlpha(tri.getAdjacentTetrahedron(1));
		}
		if (tri.getAdjacentTetrahedron(1).containsBigPoint()) {
			return getInAlpha(tri.getAdjacentTetrahedron(0));
		}
		else {
			return Math.min(getInAlpha(tri.getAdjacentTetrahedron(0)),
					getInAlpha(tri.getAdjacentTetrahedron(1)));
		}
	}

	private double trimaxmu(final CTriangle tri, final boolean ch) { // computes
																		// maxmu
		if (tri.getAdjacentTetrahedron(0).containsBigPoint()) {
			return getInAlpha(tri.getAdjacentTetrahedron(1));
		}
		if (tri.getAdjacentTetrahedron(1).containsBigPoint()) {
			return getInAlpha(tri.getAdjacentTetrahedron(0));
			// if(ch) return getInAlpha(tri.getNeighbour(0)); //always put in
			// place
			// 0
		}
		else {
			return Math.max(getInAlpha(tri.getAdjacentTetrahedron(0)),
					getInAlpha(tri.getAdjacentTetrahedron(1)));
		}
	}

	private void computeEdgeIntervals() {
		for (final CEdge e : del3d.getEdges()) {
			edges.add(e);

			boolean ch = false;
			for (final CTriangle t : e.getAdjacentTriangles()) {
				ch |= getOnCH(t);
			}
			boolean att = false;
			for (final CTriangle t : e.getAdjacentTriangles()) {
				att |= p.edgeinsphere(e, t.oppositeVertex(e)) == SphereConfig.INSIDE;
			}
			final double rho = p.edgecircumradius(e);
			final double minmu = edgeminmu(e);
			final double maxmu = edgemaxmu(e);
			final EdgeAlphaProperties prop = new EdgeAlphaProperties(minmu,
					maxmu, rho, ch, att);
			propertyMap.put(e, prop);
		}
		Collections.sort(edges, alphaOrdering);
	}

	private double edgeminmu(final CEdge e) {
		double min = p.edgecircumradius(e);
		for (final CTriangle tri : e.getAdjacentTriangles()) {
			final TriangleAlphaProperties triProps = (TriangleAlphaProperties) propertyMap
					.get(tri);

			// TODO: The following line read:
			// min =
			// Math.min(min,Math.min(triProps.getSingularInterval().getLeft(),
			// triProps.getRegularInterval().getLeft()));
			// but that made no sense to me so i changed it
			if (triProps.isAttached()) {
				min = Math.min(min, triProps.getRegularInterval().getLeft());
			}
			else {
				min = Math.min(min, triProps.getSingularInterval().getLeft());
			}
		}
		return min;
	}

	private double edgemaxmu(final CEdge e) {
		double max = 0;
		for (final CTriangle tri : e.getAdjacentTriangles()) {
			final TriangleAlphaProperties triProps = (TriangleAlphaProperties) propertyMap
					.get(tri);
			if (!triProps.getOnConvexHull()) {
				max = Math.max(max, triProps.getRegularInterval().getRight());
			}
		}
		return max;
	}

	private void computeVertexIntervals() {
		for (final CVertex v : del3d.getVertices()) {
			vertices.add(v);

			final VertexAlphaProperties prop = new VertexAlphaProperties(1, 1,
					false);
			propertyMap.put(v, prop);
		}
	}

	/**
	 * The vertex-hull of v is the set of all tetrahedrons that has v as a
	 * corner-point. Notice that that this method operates on the entire
	 * Delaunay complex and is not affected by any probe size.
	 */
	public Set<CTetrahedron> getVertexHull(final CVertex v) {
		return del3d.getVertexHull(v);
	}

	private int youngest(final int j, final int[] marked,
			final List<Integer>[] T) {
		List<Integer> Lambda = positiveD(j, marked);

		while (true) {
			final int i = Lambda.get(Lambda.size() - 1);
			if (T[i] == null) {
				T[i] = new LinkedList<Integer>();
				T[i].add(j);
				T[i].addAll(Lambda);
				return i;
			}
			Lambda = addLists(Lambda, T[i].subList(1, T[i].size()));
		}
	}

	private List<Integer> positiveD(final int j, final int[] marked) {
		final List<Integer> ret = new LinkedList<Integer>();
		switch (getDim(simplices.get(j))) {
		case 0:
			return ret;
		case 1:
			final CEdge e = (CEdge) simplices.get(j);
			addPositive(simplices.indexOf(e.getA()), marked, ret);
			addPositive(simplices.indexOf(e.getB()), marked, ret);
			break;
		case 2:
			final CTriangle t = (CTriangle) simplices.get(j);
			addPositive(simplices.indexOf(t.getEdge(0)), marked, ret);
			addPositive(simplices.indexOf(t.getEdge(1)), marked, ret);
			addPositive(simplices.indexOf(t.getEdge(2)), marked, ret);
			break;
		case 3:
			final CTetrahedron tet = (CTetrahedron) simplices.get(j);
			addPositive(simplices.indexOf(tet.getTriangle(0)), marked, ret);
			addPositive(simplices.indexOf(tet.getTriangle(1)), marked, ret);
			addPositive(simplices.indexOf(tet.getTriangle(2)), marked, ret);
			addPositive(simplices.indexOf(tet.getTriangle(3)), marked, ret);
			break;
		}
		Collections.sort(ret);
		return ret;
	}

	private static void addPositive(final int i, final int[] marked,
			final List<Integer> ret) {
		if (marked[i] == 1) {
			ret.add(i);
		}
	}

	private static List<Integer> addLists(final List<Integer> a,
			final List<Integer> b) {
		final List<Integer> ret = new LinkedList<Integer>();
		for (final Integer i : a) {
			if (!b.contains(i)) {
				ret.add(i);
			}
		}
		for (final Integer i : b) {
			if (!a.contains(i)) {
				ret.add(i);
			}
		}
		Collections.sort(ret);
		return ret;
	}

	private class AlphaComparator implements Comparator<Simplex> {
		@Override
		public int compare(final Simplex s1, final Simplex s2) {
			final SimplexAlphaProperties p1 = propertyMap.get(s1);
			final SimplexAlphaProperties p2 = propertyMap.get(s2);

			final int c = Double.compare(p1.getInAlphaComplex(),
					p2.getInAlphaComplex());
			if (c != 0) {
				return c;
			}
			else {
				return p1.getSimplexType() - p2.getSimplexType();
			}
		}
	}

	public double getAlpha() {
		return alpha;
	}

	/**
	 * Return the depth of simplex from the surface of the alpha complex. Note
	 * that a depth is returned for all simplices in the Delaunay complex. The
	 * depth of exposed simplices not in the alpha complex is -1. The depth of
	 * all other k-simplices (including simplices in voids) is the smallest
	 * depth of adjacent k-simplices plus one.
	 */
	public int getDepth(final Simplex s) {
		if (depthMap == null) {
			calculateDepthsOld();
		}
		return depthMap.get(s);
	}

	public void setAlpha(final double alpha) {
		this.alpha = alpha;
		calculateDepths();
	}

	private boolean isBoundaryTetrahedron(final CTetrahedron t) {
		return t.getNeighbour(0).containsBigPoint()
				|| t.getNeighbour(1).containsBigPoint()
				|| t.getNeighbour(2).containsBigPoint()
				|| t.getNeighbour(3).containsBigPoint();
	}

	private void calculateDepths() {
		calculateDepthsInit();

		boolean update = true;
		int dist = 0;
		while (update) {
			update = false;
			for (final CTetrahedron t : tetrahedra) {
				if (depthMap.get(t) == dist) {
					final boolean inAC = getInAlpha(t) <= alpha;
					for (int i = 0; i < 4; i++) {
						final CTetrahedron tx = t.getNeighbour(i);
						if (!tx.containsBigPoint()) {
							final double txAlpha = getInAlpha(tx);
							if ((inAC && (txAlpha <= alpha))
									|| (!inAC && (txAlpha > alpha))) {
								if (depthMap.get(tx) == Integer.MAX_VALUE) {
									depthMap.put(tx, dist + 1);
									update = true;
								}
							}
						}
					}
				}
			}
			dist++;
		}
		for (final CTetrahedron t : tetrahedra) {
			// System.out.println("depth = " + depthMap.get(t) + ", alpha = "
			// + getInAlpha(t));
		}
	}

	private void calculateDepthsInit() {
		depthMap = new HashMap<Simplex, Integer>();
		for (final CTetrahedron t : tetrahedra) {
			depthMap.put(t, Integer.MAX_VALUE);
			for (int n = 0; n < 4; n++) {
				final CTetrahedron neighbor = t.getNeighbour(n);
				if (neighbor.containsBigPoint()) {
					depthMap.put(neighbor, -1);
					depthMap.put(t, 0);
				}
			}
		}
	}

	private void calculateDepthsOld() {
		calculateDepthsInit();

		// Now iterate through all tetrahedra and update depths as long as they
		// havent converged
		// This could probably be done much more efficiently (e.g. single source
		// shortest path)
		boolean update = true;
		while (update) {
			update = false;
			for (final CTetrahedron t : tetrahedra) {
				final int oldDepth = depthMap.get(t);
				int newDepth = Math.min(
						Math.min(depth(t.getNeighbour(0)),
								depth(t.getNeighbour(1))),
								Math.min(depth(t.getNeighbour(2)),
										depth(t.getNeighbour(3))));
				if (getInAlpha(t) > alpha && newDepth == -1) {
					newDepth = -1;
				}
				else if (newDepth != Integer.MAX_VALUE) {
					newDepth++;
				}

				if (oldDepth != newDepth) {
					depthMap.put(t, newDepth);
					update = true;
				}
			}
		}
	}

	private int depth(final Simplex s) {
		final Integer d = depthMap.get(s);
		if (d == null) {
			return Integer.MAX_VALUE;
		}
		return d;
	}

	public CTetrahedron getDeepestCavityTetrahedron() {
		final double alpha = getAlpha();
		CTetrahedron deep = null;
		int deepDepth = -1;
		for (final CTetrahedron t : tetrahedra) {
			final int depth = getDepth(t);
			if ((getInAlpha(t) > alpha) && (depth > deepDepth)
					&& (depth != Integer.MAX_VALUE)) {
				deepDepth = depth;
				deep = t;
			}
		}
		return deep;
	}

	public ArrayList<CTetrahedron> getAllDeepestCavityTetrahedra(
			final int depthBound) {
		final double alpha = getAlpha();
		final ArrayList<CTetrahedron> tetras = new ArrayList<CTetrahedron>();

		for (final CTetrahedron t : tetrahedra) {
			final double tDepth = getDepth(t);
			final CTetrahedron n0 = t.getNeighbour(0);
			final CTetrahedron n1 = t.getNeighbour(1);
			final CTetrahedron n2 = t.getNeighbour(2);
			final CTetrahedron n3 = t.getNeighbour(3);
			if ((tDepth > depthBound)
					&& (getInAlpha(t) > alpha)
					&& (tDepth != Integer.MAX_VALUE)
					&& (n0.containsBigPoint() || (getInAlpha(n0) <= alpha) || (getDepth(n0) <= tDepth))
					&& (n1.containsBigPoint() || (getInAlpha(n1) <= alpha) || (getDepth(n1) <= tDepth))
					&& (n2.containsBigPoint() || (getInAlpha(n2) <= alpha) || (getDepth(n2) <= tDepth))
					&& (n3.containsBigPoint() || (getInAlpha(n3) <= alpha) || (getDepth(n3) <= tDepth))) {
				tetras.add(t);
				// System.out.println("Deepest tetrahedron found. Depth = "
				// + getDepth(t) + ", alpha = " + getInAlpha(t));
			}
		}
		return tetras;
	}

	public void getCavity(final CTetrahedron t, final int lowerBound) {
		final double alpha = getAlpha();
		final Stack<CTetrahedron> stack = new Stack<CTetrahedron>();
		if (getDepth(t) > lowerBound) {
			stack.push(t);
		}

		t.setModified(true);
		while (!stack.isEmpty()) {
			final CTetrahedron ct = stack.pop();

			// System.out.println("Tetrahedron of depth " + getDepth(ct)
			// + " added to the cavity. Its alpha is " + getInAlpha(ct));
			for (int i = 0; i < 4; i++) {
				final CTetrahedron nt = ct.getNeighbour(i);
				if (!nt.isModified() && (getInAlpha(nt) > alpha)
						&& (getDepth(nt) > lowerBound)) {
					stack.push(nt);
					nt.setModified(true);
				}
			}

		}
	}

	public void getAllCavities(final ArrayList<CTetrahedron> tetras,
			final int lowerBound) {
		for (final CTetrahedron t : tetras) {
			getCavity(t, lowerBound);
		}
	}

	public void getAllCavityPaths(final ArrayList<CTetrahedron> tetras) {
		for (final CTetrahedron t : tetras) {
			getCavityPath(t);
		}
	}

	public void getCavityPath(CTetrahedron deep) {
		final double alpha = getAlpha();

		deep.setModified(true);
		int depth = getDepth(deep);
		while (depth != 0) {
			CTetrahedron t = deep.getNeighbour(0);
			if ((getDepth(t) == depth - 1) && (getInAlpha(t) > alpha)) {
				deep = t;
			}
			else {
				t = deep.getNeighbour(1);
				if ((getDepth(t) == depth - 1) && (getInAlpha(t) > alpha)) {
					deep = t;
				}
				else {
					t = deep.getNeighbour(2);
					if ((getDepth(t) == depth - 1) && (getInAlpha(t) > alpha)) {
						deep = t;
					}
					else {
						t = deep.getNeighbour(3);
						if ((getDepth(t) == depth - 1)
								&& (getInAlpha(t) > alpha)) {
							deep = t;
						}
					}
				}
			}
			if (t.isModified()) {
				depth = 0;
			}
			else {
				// System.out.println("red tetrahedron with depth = "
				// + getDepth(deep) + " and alpha = " + getInAlpha(deep));

				deep.setModified(true);
				depth--;
			}
		}

	}

}
