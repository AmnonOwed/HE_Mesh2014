package wblut.external.ProGAL;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastTable;
import wblut.external.ProGAL.ExactJavaPredicates.SphereConfig;
import wblut.external.ProGAL.Flips.Flip14;

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
 * A Delaunay complex for a set of d-dimensional points is a tesselation of the
 * points such that no point is inside the circumscribing hypersphere of the
 * d-simplices (for the 3D case: Tetrahedra).
 * </p>
 * 
 * <p>
 * This class builds a three-dimensional Delaunay complex in the constructor and
 * accesses it using e.g. the <code>getTetrahedra</code> method. The following
 * example displays the Delaunay complex of ten random points.
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	// Generate the complex
 * 	List&lt;Point&gt; pl = PointList.generatePointsInCube(10);
 * 	DelaunayComplex dc = new DelaunayComplex(pl);
 * 
 * 	// Display the complex
 * 	J3DScene scene = J3DScene.createJ3DSceneInFrame();
 * 	for (CTetrahedron t : dc.getTetrahedra()) {
 * 		scene.addShape(t, new Color(200, 100, 100, 100));
 * 	}
 * }
 * </pre>
 * 
 * </p>
 * <p>
 * The original point-set is left unaltered and non-referenced by this class. A
 * new set of vertices is allocated using the CVertex class. These are randomly
 * perturbed to avoid degeneracies. If one wishes to associate the original
 * points with a vertex in the complex it would be sufficient to test if the
 * distance between the point and the vertex is less than 0.0001.
 * </p>
 * 
 * <p>
 * The complex is bounded by a big tetrahedron whose corner-points are located
 * sufficiently far from any of the vertices of the complex. The simplices that
 * have one of these 'big points' as corners can not be accessed directly via
 * the getter-methods, but they will be neighbors of other normal simplices. For
 * instance:
 * 
 * <pre>
 * DelaunayComplex dc = new DelaunayComplex(PointList.generatePointsInCube(4));
 * for (CTetrahedron t : dc.getTetrahedra()) {
 * 	System.out.println(t.containsBigPoint());
 * 	System.out.println(t.getNeighbor(0).containsBigPoint());
 * 	System.out.println(t.getNeighbor(1).containsBigPoint());
 * 	System.out.println(t.getNeighbor(2).containsBigPoint());
 * 	System.out.println(t.getNeighbor(3).containsBigPoint());
 * }
 * </pre>
 * 
 * Will print false, true, true, true and true.
 * </p>
 * 
 * @author R.Fonseca
 */
public class DelaunayComplex implements SimplicialComplex {
	private final List<CVertex> points;
	private final List<CEdge> edges;
	private final List<CTriangle> triangles;
	private final List<CTetrahedron> tetrahedra;
	private final ExactJavaPredicates predicates;
	private final Walk walk;
	private final Flip14 f14;
	private final Flips flips;

	/** Builds the Delaunay complex of the specified point-set */
	public DelaunayComplex(final List<Point> points) {
		this.points = new FastTable<CVertex>();

		for (final Point p : points) {
			this.points.add(new CVertex(p));
		}
		edges = new FastTable<CEdge>();
		triangles = new FastTable<CTriangle>();
		tetrahedra = new FastTable<CTetrahedron>();

		predicates = new ExactJavaPredicates();
		walk = new Walk(predicates);
		flips = new Flips(predicates);
		f14 = new Flip14(flips);

		compute();
		completeComplex();
	}

	/** TODO: Finish */
	public boolean isDelaunay() {
		for (final CTetrahedron tetr : tetrahedra) {
			final Sphere sphere = new Sphere(tetr);
		}
		return true;
	}

	/**
	 * Get the tetrahedra in the complex. The tetrahedra that has 'big points'
	 * as corners are not returned
	 */
	@Override
	public List<CTetrahedron> getTetrahedra() {
		List<CTetrahedron> T = new FastTable<CTetrahedron>();
		T.addAll(tetrahedra);
		return T;
	}

	/** returns all tetrahedra (including tetrahedra with bigPoint */
	public List<CTetrahedron> getAllTetrahedra() {
		final List<CTetrahedron> allTetrahedra = new FastTable<CTetrahedron>();
		for (final CVertex v : getVertices()) {
			final List<CTetrahedron> adjacentTetrahedra = v
					.getAllAdjacentTetrahedra();
			for (final CTetrahedron tetr : adjacentTetrahedra) {
				if (!allTetrahedra.contains(tetr)) {
					allTetrahedra.add(tetr);
				}
			}
		}
		return allTetrahedra;
	}

	/** returns all big tetrahedra */
	public List<CTetrahedron> getBigTetrahedra() {
		final List<CTetrahedron> bigTetrahedra = new FastTable<CTetrahedron>();
		for (final CVertex v : getVertices()) {
			final List<CTetrahedron> adjacentTetrahedra = v
					.getAllAdjacentTetrahedra();
			for (final CTetrahedron tetr : adjacentTetrahedra) {
				if (tetr.containsBigPoint() && !bigTetrahedra.contains(tetr)) {
					bigTetrahedra.add(tetr);
				}
			}
		}
		return bigTetrahedra;
	}

	/**
	 * Get the triangles in the complex. The triangles that has 'big points' as
	 * corners are not returned
	 */
	@Override
	public List<CTriangle> getTriangles() {
		List<CTriangle> T = new FastTable<CTriangle>();
		T.addAll(triangles);
		return T;

	}

	/**
	 * Get the edges in the complex. The edges that has 'big points' as
	 * end-points are not returned
	 */
	@Override
	public List<CEdge> getEdges() {
		List<CEdge> E = new FastTable<CEdge>();
		E.addAll(edges);
		return E;
	}

	/** Get the vertices in the complex. The 'big points' are not returned */
	@Override
	public List<CVertex> getVertices() {
		List<CVertex> V = new FastTable<CVertex>();
		V.addAll(points);
		return V;
	}

	public CVertex getVertex(final int i) {
		return points.get(i);
	}

	protected void compute() {
		final double max = 100;// TODO find a more meaningful max

		// TODO: Take care of degeneracies in a better way than perturbation
		for (final CVertex v : points) {
			v.addThis(new Vector(Randomization.randBetween(-0.00001, 0.00001),
					Randomization.randBetween(-0.00001, 0.00001), Randomization
							.randBetween(-0.00001, 0.00001)));
		}

		// Find the enclosing tetrahedron
		CTetrahedron next_t = new FirstTetrahedron(max);
		flips.addTetrahedron(next_t);

		for (final CVertex p : points) {
			next_t = walk.walk(next_t, p);
			next_t = f14.flip14(next_t, p);
			final CTetrahedron tmp = flips.fixDelaunay();

			if (tmp != null) {
				next_t = tmp;
			}
		}

	}

	/** Add edges and triangles and remove auxiliary tetrahedra. */
	protected void completeComplex() {
		tetrahedra.clear();
		triangles.clear();
		edges.clear();

		// Add the non-modified tetrahedra that doesnt contain one of the big
		// points
		for (final CTetrahedron t : flips.getTetrahedrastack()) {
			if (!t.isModified() && !t.containsBigPoint()) {
				tetrahedra.add(t);
			}
		}
		// flips.setTetrahedrastack(null);//Should free up some memory after
		// garbage collection
		// flips.getTetrahedrastack().clear();

		class VertexPair {
			CVertex p1, p2;

			VertexPair(final CVertex p1, final CVertex p2) {
				this.p1 = p1;
				this.p2 = p2;
			}

			@Override
			public boolean equals(final Object o) {
				return (((VertexPair) o).p1 == p1 && ((VertexPair) o).p2 == p2)
						|| (((VertexPair) o).p1 == p2 && ((VertexPair) o).p2 == p1);
			}

			@Override
			public int hashCode() {
				return p1.hashCode() ^ p2.hashCode();
			}
		}

		// Construct edges
		final Map<VertexPair, CEdge> edgeMap = new FastMap<VertexPair, CEdge>();
		for (final CTetrahedron t : tetrahedra) {
			edgeMap.put(new VertexPair(t.getPoint(0), t.getPoint(1)),
					new CEdge(t.getPoint(0), t.getPoint(1)));
			edgeMap.put(new VertexPair(t.getPoint(0), t.getPoint(2)),
					new CEdge(t.getPoint(0), t.getPoint(2)));
			edgeMap.put(new VertexPair(t.getPoint(0), t.getPoint(3)),
					new CEdge(t.getPoint(0), t.getPoint(3)));
			edgeMap.put(new VertexPair(t.getPoint(1), t.getPoint(2)),
					new CEdge(t.getPoint(1), t.getPoint(2)));
			edgeMap.put(new VertexPair(t.getPoint(1), t.getPoint(3)),
					new CEdge(t.getPoint(1), t.getPoint(3)));
			edgeMap.put(new VertexPair(t.getPoint(2), t.getPoint(3)),
					new CEdge(t.getPoint(2), t.getPoint(3)));
		}
		edges.addAll(edgeMap.values());

		for (final CEdge e : edges) {
			((CVertex) e.getA()).addAdjacentEdge(e);
			((CVertex) e.getB()).addAdjacentEdge(e);
		}

		// Construct triangles
		final Set<CTriangle> triangleSet = new HashSet<CTriangle>();
		for (final CTetrahedron t : tetrahedra) {
			triangleSet.add(new CTriangle(t.getPoint(1), t.getPoint(2), t
					.getPoint(3), t, t.getNeighbour(0)));
			triangleSet.add(new CTriangle(t.getPoint(0), t.getPoint(2), t
					.getPoint(3), t, t.getNeighbour(1)));
			triangleSet.add(new CTriangle(t.getPoint(0), t.getPoint(1), t
					.getPoint(3), t, t.getNeighbour(2)));
			triangleSet.add(new CTriangle(t.getPoint(0), t.getPoint(1), t
					.getPoint(2), t, t.getNeighbour(3)));
		}
		triangles.addAll(triangleSet);
		for (final CTriangle t : triangles) {
			final CEdge e1 = edgeMap.get(new VertexPair(
					(CVertex) t.getPoint(0), (CVertex) t.getPoint(1)));
			final CEdge e2 = edgeMap.get(new VertexPair(
					(CVertex) t.getPoint(1), (CVertex) t.getPoint(2)));
			final CEdge e3 = edgeMap.get(new VertexPair(
					(CVertex) t.getPoint(2), (CVertex) t.getPoint(0)));
			t.setEdge(0, e1);
			t.setEdge(1, e2);
			t.setEdge(2, e3);
			e1.addTriangle(t);
			e2.addTriangle(t);
			e3.addTriangle(t);
		}

		// Set faces of tetrahedra
		for (final CTriangle t : triangles) {
			CTetrahedron tet = t.getAdjacentTetrahedron(0);
			if (tet.getNeighbour(0).containsTriangle(t)) {
				tet.setTriangle(0, t);
			} else if (tet.getNeighbour(1).containsTriangle(t)) {
				tet.setTriangle(1, t);
			} else if (tet.getNeighbour(2).containsTriangle(t)) {
				tet.setTriangle(2, t);
			} else if (tet.getNeighbour(3).containsTriangle(t)) {
				tet.setTriangle(3, t);
			}

			tet = t.getAdjacentTetrahedron(1);
			if (tet.getNeighbour(0).containsTriangle(t)) {
				tet.setTriangle(0, t);
			} else if (tet.getNeighbour(1).containsTriangle(t)) {
				tet.setTriangle(1, t);
			} else if (tet.getNeighbour(2).containsTriangle(t)) {
				tet.setTriangle(2, t);
			} else if (tet.getNeighbour(3).containsTriangle(t)) {
				tet.setTriangle(3, t);
			}
		}
	}

	/**
	 * The vertex-hull of v is the set of all tetrahedrons that has v as a
	 * corner-point
	 */
	public Set<CTetrahedron> getVertexHull(final CVertex v) {
		final Set<CTetrahedron> hull = new HashSet<CTetrahedron>();
		for (final CEdge e : v.getAdjacentEdges()) {
			for (final CTriangle tri : e.getAdjacentTriangles()) {
				hull.add(tri.getAdjacentTetrahedron(0));
				hull.add(tri.getAdjacentTetrahedron(1));
			}
		}
		return hull;
	}

	/** Checks that all tetrahedra comply with the Delaunay-criteria. */
	public boolean checkTetrahedra() {
		for (final CTetrahedron t : tetrahedra) {
			for (final CVertex p : points) {
				if (t.getPoint(0) != p
						&& t.getPoint(1) != p
						&& t.getPoint(2) != p
						&& t.getPoint(3) != p
						&& predicates.insphere(t, p)
								.equals(SphereConfig.INSIDE)) {
					return false;
				}
			}
		}
		return true;
	}
}
