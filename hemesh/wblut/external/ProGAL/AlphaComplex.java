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

	public AlphaComplex(List<Point> pl) {
		this(new DelaunayComplex(pl), Double.MAX_VALUE);
	}

	public AlphaComplex(List<Point> pl, double alpha) {
		this(new DelaunayComplex(pl), alpha);
	}

	/** Build the alpha-complex of the specified Delaunay complex. */
	public AlphaComplex(DelaunayComplex delaunayComplex, double alpha) {
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
	public List<CTetrahedron> getTetrahedra(double alpha) {
		List<CTetrahedron> ret = new ArrayList<CTetrahedron>();
		for (CTetrahedron t : tetrahedra)
			if (getInAlpha(t) < alpha)
				ret.add(t);
		return ret;
	}

	/**
	 * Get a list of tetrahedra that are part of the alpha-complex with the
	 * probe radius in a specified interval. Added by PW
	 */
	public List<CTetrahedron> getTetrahedra(double alphaLow, double alphaHigh) {
		List<CTetrahedron> ret = new ArrayList<CTetrahedron>();
		for (CTetrahedron t : tetrahedra)
			if ((getInAlpha(t) >= alphaLow) && (getInAlpha(t) < alphaHigh))
				ret.add(t);
		return ret;
	}

	/**
	 * Get a list of triangles that are part of the alpha-complex with the
	 * specified probe radius.
	 */
	public List<CTriangle> getTriangles(double alpha) {
		List<CTriangle> ret = new ArrayList<CTriangle>();
		for (CTriangle t : triangles)
			if (getInAlpha(t) < alpha)
				ret.add(t);
		return ret;
	}

	/** Returns triangles in one tetrahedron only */
	public List<Triangle> getSurfaceTriangles(double alpha) {
		List<CTetrahedron> tetrahedra = getTetrahedra(alpha);
		List<Triangle> triangles = new ArrayList<Triangle>();
		for (CTetrahedron tetrahedron : tetrahedra) {
			for (int i = 0; i < 4; i++) {
				CTetrahedron neighbor = tetrahedron.getNeighbour(i);
				if ((neighbor == null) || (getInAlpha(neighbor) >= alpha))
					triangles.add(tetrahedron.getTriangle(i));
			}
		}
		return triangles;
	}

	/**
	 * Get a list of edges that are part of the alpha-complex with the specified
	 * probe radius.
	 */
	public List<CEdge> getEdges(double alpha) {
		List<CEdge> ret = new ArrayList<CEdge>();
		for (CEdge t : edges)
			if (getInAlpha(t) < alpha)
				ret.add(t);
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
	public List<Simplex> getSimplices(double alpha) {
		List<Simplex> ret = new ArrayList<Simplex>();
		for (Simplex s : simplices)
			if (getInAlpha(s) < alpha)
				ret.add(s);
		return ret;
	}

	public List<CTriangle> getAlphaShape(double alpha) {
		List<CTriangle> ret = new ArrayList<CTriangle>();
		for (CTriangle t : getTriangles(alpha)) {
			try {
				double a0 = getInAlpha(t.getAdjacentTetrahedron(0));
				double a1 = getInAlpha(t.getAdjacentTetrahedron(1));
				if ((a0 > alpha) ^ (a1 > alpha))
					ret.add(t);
			} catch (NullPointerException exc) {
				ret.add(t);
			}
		}
		return ret;
	}

	public int getDim(Simplex s) {
		SimplexAlphaProperties prop = propertyMap.get(s);
		return prop.getSimplexType();
	}

	/**
	 * Return the probe-radius at which the simplex <code>s</code> enters the
	 * alpha complex.
	 */
	public double getInAlpha(Simplex s) {
		return propertyMap.get(s).getInAlphaComplex();
	}

	public boolean getAttached(Simplex s) {
		return propertyMap.get(s).isAttached();
	}

	/**
	 * Return true iff the simplex is on the convex hull. Calling this method
	 * with a CTetrahedra will throw an error.
	 */
	public boolean getOnCH(Simplex s) {
		SimplexAlphaProperties prop = propertyMap.get(s);
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
		for (CTetrahedron t : del3d.getTetrahedra()) {
			tetrahedra.add(t);

			double r = p.circumradius(t);
			TetrahedronAlphaProperties properties = new TetrahedronAlphaProperties(
					r);
			propertyMap.put(t, properties);
		}
		Collections.sort(tetrahedra, alphaOrdering);
	}

	private void computeTriangleIntervals() {
		for (CTriangle tri : del3d.getTriangles()) {
			triangles.add(tri);

			boolean ch = tri.getAdjacentTetrahedron(0).containsBigPoint()
					|| tri.getAdjacentTetrahedron(1).containsBigPoint();
			boolean att = p.insphere(tri, tri.getAdjacentTetrahedron(0)
					.oppositeVertex(tri)) == SphereConfig.INSIDE
					|| p.insphere(tri, tri.getAdjacentTetrahedron(1)
							.oppositeVertex(tri)) == SphereConfig.INSIDE;
			double minmu = triminmu(tri, ch);
			double maxmu = trimaxmu(tri, ch);
			double rho = p.circumradius(tri);
			TriangleAlphaProperties prop = new TriangleAlphaProperties(minmu,
					maxmu, rho, ch, att);
			propertyMap.put(tri, prop);
		}
		Collections.sort(triangles, alphaOrdering);
	}

	private double triminmu(CTriangle tri, boolean ch) { // computes minmu
		if (tri.getAdjacentTetrahedron(0).containsBigPoint())
			return getInAlpha(tri.getAdjacentTetrahedron(1));
		if (tri.getAdjacentTetrahedron(1).containsBigPoint())
			return getInAlpha(tri.getAdjacentTetrahedron(0));
		else
			return Math.min(getInAlpha(tri.getAdjacentTetrahedron(0)),
					getInAlpha(tri.getAdjacentTetrahedron(1)));
	}

	private double trimaxmu(CTriangle tri, boolean ch) { // computes maxmu
		if (tri.getAdjacentTetrahedron(0).containsBigPoint())
			return getInAlpha(tri.getAdjacentTetrahedron(1));
		if (tri.getAdjacentTetrahedron(1).containsBigPoint())
			return getInAlpha(tri.getAdjacentTetrahedron(0));
		// if(ch) return getInAlpha(tri.getNeighbour(0)); //always put in place
		// 0
		else
			return Math.max(getInAlpha(tri.getAdjacentTetrahedron(0)),
					getInAlpha(tri.getAdjacentTetrahedron(1)));
	}

	private void computeEdgeIntervals() {
		for (CEdge e : del3d.getEdges()) {
			edges.add(e);

			boolean ch = false;
			for (CTriangle t : e.getAdjacentTriangles())
				ch |= getOnCH(t);
			boolean att = false;
			for (CTriangle t : e.getAdjacentTriangles())
				att |= p.edgeinsphere(e, t.oppositeVertex(e)) == SphereConfig.INSIDE;
			double rho = p.edgecircumradius(e);
			double minmu = edgeminmu(e);
			double maxmu = edgemaxmu(e);
			EdgeAlphaProperties prop = new EdgeAlphaProperties(minmu, maxmu,
					rho, ch, att);
			propertyMap.put(e, prop);
		}
		Collections.sort(edges, alphaOrdering);
	}

	private double edgeminmu(CEdge e) {
		double min = p.edgecircumradius(e);
		for (CTriangle tri : e.getAdjacentTriangles()) {
			TriangleAlphaProperties triProps = (TriangleAlphaProperties) propertyMap
					.get(tri);

			// TODO: The following line read:
			// min =
			// Math.min(min,Math.min(triProps.getSingularInterval().getLeft(),
			// triProps.getRegularInterval().getLeft()));
			// but that made no sense to me so i changed it
			if (triProps.isAttached())
				min = Math.min(min, triProps.getRegularInterval().getLeft());
			else
				min = Math.min(min, triProps.getSingularInterval().getLeft());
		}
		return min;
	}

	private double edgemaxmu(CEdge e) {
		double max = 0;
		for (CTriangle tri : e.getAdjacentTriangles()) {
			TriangleAlphaProperties triProps = (TriangleAlphaProperties) propertyMap
					.get(tri);
			if (!triProps.getOnConvexHull())
				max = Math.max(max, triProps.getRegularInterval().getRight());
		}
		return max;
	}

	private void computeVertexIntervals() {
		for (CVertex v : del3d.getVertices()) {
			vertices.add(v);

			VertexAlphaProperties prop = new VertexAlphaProperties(1, 1, false);
			propertyMap.put(v, prop);
		}
	}

	/**
	 * The vertex-hull of v is the set of all tetrahedrons that has v as a
	 * corner-point. Notice that that this method operates on the entire
	 * Delaunay complex and is not affected by any probe size.
	 */
	public Set<CTetrahedron> getVertexHull(CVertex v) {
		return del3d.getVertexHull(v);
	}

	private int youngest(int j, int[] marked, List<Integer>[] T) {
		List<Integer> Lambda = positiveD(j, marked);

		while (true) {
			int i = Lambda.get(Lambda.size() - 1);
			if (T[i] == null) {
				T[i] = new LinkedList<Integer>();
				T[i].add(j);
				T[i].addAll(Lambda);
				return i;
			}
			Lambda = addLists(Lambda, T[i].subList(1, T[i].size()));
		}
	}

	private List<Integer> positiveD(int j, int[] marked) {
		List<Integer> ret = new LinkedList<Integer>();
		switch (getDim(simplices.get(j))) {
		case 0:
			return ret;
		case 1:
			CEdge e = (CEdge) simplices.get(j);
			addPositive(simplices.indexOf(e.getA()), marked, ret);
			addPositive(simplices.indexOf(e.getB()), marked, ret);
			break;
		case 2:
			CTriangle t = (CTriangle) simplices.get(j);
			addPositive(simplices.indexOf(t.getEdge(0)), marked, ret);
			addPositive(simplices.indexOf(t.getEdge(1)), marked, ret);
			addPositive(simplices.indexOf(t.getEdge(2)), marked, ret);
			break;
		case 3:
			CTetrahedron tet = (CTetrahedron) simplices.get(j);
			addPositive(simplices.indexOf(tet.getTriangle(0)), marked, ret);
			addPositive(simplices.indexOf(tet.getTriangle(1)), marked, ret);
			addPositive(simplices.indexOf(tet.getTriangle(2)), marked, ret);
			addPositive(simplices.indexOf(tet.getTriangle(3)), marked, ret);
			break;
		}
		Collections.sort(ret);
		return ret;
	}

	private static void addPositive(int i, int[] marked, List<Integer> ret) {
		if (marked[i] == 1)
			ret.add(i);
	}

	private static List<Integer> addLists(List<Integer> a, List<Integer> b) {
		List<Integer> ret = new LinkedList<Integer>();
		for (Integer i : a)
			if (!b.contains(i))
				ret.add(i);
		for (Integer i : b)
			if (!a.contains(i))
				ret.add(i);
		Collections.sort(ret);
		return ret;
	}

	private class AlphaComparator implements Comparator<Simplex> {
		@Override
		public int compare(Simplex s1, Simplex s2) {
			SimplexAlphaProperties p1 = propertyMap.get(s1);
			SimplexAlphaProperties p2 = propertyMap.get(s2);

			int c = Double.compare(p1.getInAlphaComplex(),
					p2.getInAlphaComplex());
			if (c != 0)
				return c;
			else
				return p1.getSimplexType() - p2.getSimplexType();
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
	public int getDepth(Simplex s) {
		if (depthMap == null)
			calculateDepthsOld();
		return depthMap.get(s);
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
		calculateDepths();
	}

	private boolean isBoundaryTetrahedron(CTetrahedron t) {
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
			for (CTetrahedron t : tetrahedra) {
				if (depthMap.get(t) == dist) {
					boolean inAC = getInAlpha(t) <= alpha;
					for (int i = 0; i < 4; i++) {
						CTetrahedron tx = t.getNeighbour(i);
						if (!tx.containsBigPoint()) {
							double txAlpha = getInAlpha(tx);
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
		for (CTetrahedron t : tetrahedra) {
			System.out.println("depth = " + depthMap.get(t) + ", alpha = "
					+ getInAlpha(t));
		}
	}

	private void calculateDepthsInit() {
		depthMap = new HashMap<Simplex, Integer>();
		for (CTetrahedron t : tetrahedra) {
			depthMap.put(t, Integer.MAX_VALUE);
			for (int n = 0; n < 4; n++) {
				CTetrahedron neighbor = t.getNeighbour(n);
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
			for (CTetrahedron t : tetrahedra) {
				int oldDepth = depthMap.get(t);
				int newDepth = Math.min(
						Math.min(depth(t.getNeighbour(0)),
								depth(t.getNeighbour(1))),
						Math.min(depth(t.getNeighbour(2)),
								depth(t.getNeighbour(3))));
				if (getInAlpha(t) > alpha && newDepth == -1)
					newDepth = -1;
				else if (newDepth != Integer.MAX_VALUE)
					newDepth++;

				if (oldDepth != newDepth) {
					depthMap.put(t, newDepth);
					update = true;
				}
			}
		}
	}

	private int depth(Simplex s) {
		Integer d = depthMap.get(s);
		if (d == null)
			return Integer.MAX_VALUE;
		return d;
	}

	public CTetrahedron getDeepestCavityTetrahedron() {
		double alpha = getAlpha();
		CTetrahedron deep = null;
		int deepDepth = -1;
		for (CTetrahedron t : tetrahedra) {
			int depth = getDepth(t);
			if ((getInAlpha(t) > alpha) && (depth > deepDepth)
					&& (depth != Integer.MAX_VALUE)) {
				deepDepth = depth;
				deep = t;
			}
		}
		return deep;
	}

	public ArrayList<CTetrahedron> getAllDeepestCavityTetrahedra(int depthBound) {
		double alpha = getAlpha();
		ArrayList<CTetrahedron> tetras = new ArrayList<CTetrahedron>();

		for (CTetrahedron t : tetrahedra) {
			double tDepth = getDepth(t);
			CTetrahedron n0 = t.getNeighbour(0);
			CTetrahedron n1 = t.getNeighbour(1);
			CTetrahedron n2 = t.getNeighbour(2);
			CTetrahedron n3 = t.getNeighbour(3);
			if ((tDepth > depthBound)
					&& (getInAlpha(t) > alpha)
					&& (tDepth != Integer.MAX_VALUE)
					&& (n0.containsBigPoint() || (getInAlpha(n0) <= alpha) || (getDepth(n0) <= tDepth))
					&& (n1.containsBigPoint() || (getInAlpha(n1) <= alpha) || (getDepth(n1) <= tDepth))
					&& (n2.containsBigPoint() || (getInAlpha(n2) <= alpha) || (getDepth(n2) <= tDepth))
					&& (n3.containsBigPoint() || (getInAlpha(n3) <= alpha) || (getDepth(n3) <= tDepth))) {
				tetras.add(t);
				System.out.println("Deepest tetrahedron found. Depth = "
						+ getDepth(t) + ", alpha = " + getInAlpha(t));
			}
		}
		return tetras;
	}

	public void getCavity(CTetrahedron t, int lowerBound) {
		double alpha = getAlpha();
		Stack<CTetrahedron> stack = new Stack<CTetrahedron>();
		if (getDepth(t) > lowerBound)
			stack.push(t);

		t.setModified(true);
		while (!stack.isEmpty()) {
			CTetrahedron ct = stack.pop();

			System.out.println("Tetrahedron of depth " + getDepth(ct)
					+ " added to the cavity. Its alpha is " + getInAlpha(ct));
			for (int i = 0; i < 4; i++) {
				CTetrahedron nt = ct.getNeighbour(i);
				if (!nt.isModified() && (getInAlpha(nt) > alpha)
						&& (getDepth(nt) > lowerBound)) {
					stack.push(nt);
					nt.setModified(true);
				}
			}

		}
	}

	public void getAllCavities(ArrayList<CTetrahedron> tetras, int lowerBound) {
		for (CTetrahedron t : tetras)
			getCavity(t, lowerBound);
	}

	public void getAllCavityPaths(ArrayList<CTetrahedron> tetras) {
		for (CTetrahedron t : tetras)
			getCavityPath(t);
	}

	public void getCavityPath(CTetrahedron deep) {
		double alpha = getAlpha();

		deep.setModified(true);
		int depth = getDepth(deep);
		while (depth != 0) {
			CTetrahedron t = deep.getNeighbour(0);
			if ((getDepth(t) == depth - 1) && (getInAlpha(t) > alpha))
				deep = t;
			else {
				t = deep.getNeighbour(1);
				if ((getDepth(t) == depth - 1) && (getInAlpha(t) > alpha))
					deep = t;
				else {
					t = deep.getNeighbour(2);
					if ((getDepth(t) == depth - 1) && (getInAlpha(t) > alpha))
						deep = t;
					else {
						t = deep.getNeighbour(3);
						if ((getDepth(t) == depth - 1)
								&& (getInAlpha(t) > alpha))
							deep = t;
					}
				}
			}
			if (t.isModified())
				depth = 0;
			else {
				System.out.println("red tetrahedron with depth = "
						+ getDepth(deep) + " and alpha = " + getInAlpha(deep));

				deep.setModified(true);
				depth--;
			}
		}

	}

}
