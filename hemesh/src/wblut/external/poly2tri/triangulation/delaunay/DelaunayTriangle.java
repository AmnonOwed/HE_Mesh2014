/* Poly2Tri
 * Copyright (c) 2009-2010, Poly2Tri Contributors
 * http://code.google.com/p/poly2tri/
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of Poly2Tri nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package wblut.external.poly2tri.triangulation.delaunay;

import java.util.ArrayList;

import wblut.external.poly2tri.triangulation.TriangulationPoint;
import wblut.external.poly2tri.triangulation.delaunay.sweep.DTSweepConstraint;

public class DelaunayTriangle {

	/** Neighbor pointers */
	public final DelaunayTriangle[] neighbors = new DelaunayTriangle[3];
	/** Flags to determine if an edge is a Constrained edge */
	public final boolean[] cEdge = new boolean[] { false, false, false };
	/** Flags to determine if an edge is a Delaunay edge */
	public final boolean[] dEdge = new boolean[] { false, false, false };
	/** Has this triangle been marked as an interior triangle? */
	protected boolean interior = false;

	public final TriangulationPoint[] points = new TriangulationPoint[3];

	public DelaunayTriangle(final TriangulationPoint p1,
			final TriangulationPoint p2, final TriangulationPoint p3) {
		points[0] = p1;
		points[1] = p2;
		points[2] = p3;
	}

	public int index(final TriangulationPoint p) {
		if (p == points[0]) {
			return 0;
		}
		else if (p == points[1]) {
			return 1;
		}
		else if (p == points[2]) {
			return 2;
		}
		throw new RuntimeException(
				"Calling index with a point that doesn't exist in triangle");
	}

	public int indexCW(final TriangulationPoint p) {
		final int index = index(p);
		switch (index) {
		case 0:
			return 2;
		case 1:
			return 0;
		default:
			return 1;
		}
	}

	public int indexCCW(final TriangulationPoint p) {
		final int index = index(p);
		switch (index) {
		case 0:
			return 1;
		case 1:
			return 2;
		default:
			return 0;
		}
	}

	public boolean contains(final TriangulationPoint p) {
		return (p == points[0] || p == points[1] || p == points[2]);
	}

	public boolean contains(final DTSweepConstraint e) {
		return (contains(e.p) && contains(e.q));
	}

	public boolean contains(final TriangulationPoint p,
			final TriangulationPoint q) {
		return (contains(p) && contains(q));
	}

	// Update neighbor pointers
	private void markNeighbor(final TriangulationPoint p1,
			final TriangulationPoint p2, final DelaunayTriangle t) {
		if ((p1 == points[2] && p2 == points[1])
				|| (p1 == points[1] && p2 == points[2])) {
			neighbors[0] = t;
		}
		else if ((p1 == points[0] && p2 == points[2])
				|| (p1 == points[2] && p2 == points[0])) {
			neighbors[1] = t;
		}
		else if ((p1 == points[0] && p2 == points[1])
				|| (p1 == points[1] && p2 == points[0])) {
			neighbors[2] = t;
		}

	}

	/* Exhaustive search to update neighbor pointers */
	public void markNeighbor(final DelaunayTriangle t) {
		if (t.contains(points[1], points[2])) {
			neighbors[0] = t;
			t.markNeighbor(points[1], points[2], this);
		}
		else if (t.contains(points[0], points[2])) {
			neighbors[1] = t;
			t.markNeighbor(points[0], points[2], this);
		}
		else if (t.contains(points[0], points[1])) {
			neighbors[2] = t;
			t.markNeighbor(points[0], points[1], this);
		}

	}

	public void clearNeighbors() {
		neighbors[0] = neighbors[1] = neighbors[2] = null;
	}

	public void clearNeighbor(final DelaunayTriangle triangle) {
		if (neighbors[0] == triangle) {
			neighbors[0] = null;
		}
		else if (neighbors[1] == triangle) {
			neighbors[1] = null;
		}
		else {
			neighbors[2] = null;
		}
	}

	/**
	 * Clears all references to all other triangles and points
	 */
	public void clear() {
		DelaunayTriangle t;
		for (int i = 0; i < 3; i++) {
			t = neighbors[i];
			if (t != null) {
				t.clearNeighbor(this);
			}
		}
		clearNeighbors();
		points[0] = points[1] = points[2] = null;
	}

	/**
	 * @param t
	 *            - opposite triangle
	 * @param p
	 *            - the point in t that isn't shared between the triangles
	 * @return
	 */
	public TriangulationPoint oppositePoint(final DelaunayTriangle t,
			final TriangulationPoint p) {
		assert t != this : "self-pointer error";
		return pointCW(t.pointCW(p));
	}

	// The neighbor clockwise to given point
	public DelaunayTriangle neighborCW(final TriangulationPoint point) {
		if (point == points[0]) {
			return neighbors[1];
		}
		else if (point == points[1]) {
			return neighbors[2];
		}
		return neighbors[0];
	}

	// The neighbor counter-clockwise to given point
	public DelaunayTriangle neighborCCW(final TriangulationPoint point) {
		if (point == points[0]) {
			return neighbors[2];
		}
		else if (point == points[1]) {
			return neighbors[0];
		}
		return neighbors[1];
	}

	// The neighbor across to given point
	public DelaunayTriangle neighborAcross(final TriangulationPoint opoint) {
		if (opoint == points[0]) {
			return neighbors[0];
		}
		else if (opoint == points[1]) {
			return neighbors[1];
		}
		return neighbors[2];
	}

	// The point counter-clockwise to given point
	public TriangulationPoint pointCCW(final TriangulationPoint point) {
		if (point == points[0]) {
			return points[1];
		}
		else if (point == points[1]) {
			return points[2];
		}
		else if (point == points[2]) {
			return points[0];
		}

		throw new RuntimeException("[FIXME] point location error");
	}

	// The point counter-clockwise to given point
	public TriangulationPoint pointCW(final TriangulationPoint point) {
		if (point == points[0]) {
			return points[2];
		}
		else if (point == points[1]) {
			return points[0];
		}
		else if (point == points[2]) {
			return points[1];
		}
		throw new RuntimeException("[FIXME] point location error");
	}

	// Legalize triangle by rotating clockwise around oPoint
	public void legalize(final TriangulationPoint oPoint,
			final TriangulationPoint nPoint) {
		if (oPoint == points[0]) {
			points[1] = points[0];
			points[0] = points[2];
			points[2] = nPoint;
		}
		else if (oPoint == points[1]) {
			points[2] = points[1];
			points[1] = points[0];
			points[0] = nPoint;
		}
		else if (oPoint == points[2]) {
			points[0] = points[2];
			points[2] = points[1];
			points[1] = nPoint;
		}
		else {

			throw new RuntimeException("legalization bug");
		}
	}

	public void printDebug() {
		System.out.println(points[0] + "," + points[1] + "," + points[2]);
	}

	// Finalize edge marking
	public void markNeighborEdges() {
		for (int i = 0; i < 3; i++) {
			if (cEdge[i]) {
				switch (i) {
				case 0:
					if (neighbors[0] != null) {
						neighbors[0].markConstrainedEdge(points[1], points[2]);
					}
					break;
				case 1:
					if (neighbors[1] != null) {
						neighbors[1].markConstrainedEdge(points[0], points[2]);
					}
					break;
				case 2:
					if (neighbors[2] != null) {
						neighbors[2].markConstrainedEdge(points[0], points[1]);
					}
					break;
				}
			}
		}
	}

	public void markEdge(final DelaunayTriangle triangle) {
		for (int i = 0; i < 3; i++) {
			if (cEdge[i]) {
				switch (i) {
				case 0:
					triangle.markConstrainedEdge(points[1], points[2]);
					break;
				case 1:
					triangle.markConstrainedEdge(points[0], points[2]);
					break;
				case 2:
					triangle.markConstrainedEdge(points[0], points[1]);
					break;
				}
			}
		}
	}

	public void markEdge(final ArrayList<DelaunayTriangle> tList) {

		for (final DelaunayTriangle t : tList) {
			for (int i = 0; i < 3; i++) {
				if (t.cEdge[i]) {
					switch (i) {
					case 0:
						markConstrainedEdge(t.points[1], t.points[2]);
						break;
					case 1:
						markConstrainedEdge(t.points[0], t.points[2]);
						break;
					case 2:
						markConstrainedEdge(t.points[0], t.points[1]);
						break;
					}
				}
			}
		}
	}

	public void markConstrainedEdge(final int index) {
		cEdge[index] = true;
	}

	public void markConstrainedEdge(final DTSweepConstraint edge) {
		markConstrainedEdge(edge.p, edge.q);
		if ((edge.q == points[0] && edge.p == points[1])
				|| (edge.q == points[1] && edge.p == points[0])) {
			cEdge[2] = true;
		}
		else if ((edge.q == points[0] && edge.p == points[2])
				|| (edge.q == points[2] && edge.p == points[0])) {
			cEdge[1] = true;
		}
		else if ((edge.q == points[1] && edge.p == points[2])
				|| (edge.q == points[2] && edge.p == points[1])) {
			cEdge[0] = true;
		}
	}

	// Mark edge as constrained
	public void markConstrainedEdge(final TriangulationPoint p,
			final TriangulationPoint q) {
		if ((q == points[0] && p == points[1])
				|| (q == points[1] && p == points[0])) {
			cEdge[2] = true;
		}
		else if ((q == points[0] && p == points[2])
				|| (q == points[2] && p == points[0])) {
			cEdge[1] = true;
		}
		else if ((q == points[1] && p == points[2])
				|| (q == points[2] && p == points[1])) {
			cEdge[0] = true;
		}
	}

	public double area() {
		final double a = (points[0].xd() - points[2].xd())
				* (points[1].yd() - points[0].yd());
		final double b = (points[0].xd() - points[1].xd())
				* (points[2].yd() - points[0].yd());

		return 0.5 * Math.abs(a - b);
	}

	public TriangulationPoint centroid() {
		final double cx = (points[0].xd() + points[1].xd() + points[2].xd()) / 3d;
		final double cy = (points[0].yd() + points[1].yd() + points[2].yd()) / 3d;
		return new TriangulationPoint(cx, cy);
	}

	/**
	 * Get the neighbor that share this edge
	 *
	 * @param constrainedEdge
	 * @return index of the shared edge or -1 if edge isn't shared
	 */
	public int edgeIndex(final TriangulationPoint p1,
			final TriangulationPoint p2) {
		if (points[0] == p1) {
			if (points[1] == p2) {
				return 2;
			}
			else if (points[2] == p2) {
				return 1;
			}
		}
		else if (points[1] == p1) {
			if (points[2] == p2) {
				return 0;
			}
			else if (points[0] == p2) {
				return 2;
			}
		}
		else if (points[2] == p1) {
			if (points[0] == p2) {
				return 1;
			}
			else if (points[1] == p2) {
				return 0;
			}
		}
		return -1;
	}

	public boolean getConstrainedEdgeCCW(final TriangulationPoint p) {
		if (p == points[0]) {
			return cEdge[2];
		}
		else if (p == points[1]) {
			return cEdge[0];
		}
		return cEdge[1];
	}

	public boolean getConstrainedEdgeCW(final TriangulationPoint p) {
		if (p == points[0]) {
			return cEdge[1];
		}
		else if (p == points[1]) {
			return cEdge[2];
		}
		return cEdge[0];
	}

	public boolean getConstrainedEdgeAcross(final TriangulationPoint p) {
		if (p == points[0]) {
			return cEdge[0];
		}
		else if (p == points[1]) {
			return cEdge[1];
		}
		return cEdge[2];
	}

	public void setConstrainedEdgeCCW(final TriangulationPoint p,
			final boolean ce) {
		if (p == points[0]) {
			cEdge[2] = ce;
		}
		else if (p == points[1]) {
			cEdge[0] = ce;
		}
		else {
			cEdge[1] = ce;
		}
	}

	public void setConstrainedEdgeCW(final TriangulationPoint p,
			final boolean ce) {
		if (p == points[0]) {
			cEdge[1] = ce;
		}
		else if (p == points[1]) {
			cEdge[2] = ce;
		}
		else {
			cEdge[0] = ce;
		}
	}

	public void setConstrainedEdgeAcross(final TriangulationPoint p,
			final boolean ce) {
		if (p == points[0]) {
			cEdge[0] = ce;
		}
		else if (p == points[1]) {
			cEdge[1] = ce;
		}
		else {
			cEdge[2] = ce;
		}
	}

	public boolean getDelunayEdgeCCW(final TriangulationPoint p) {
		if (p == points[0]) {
			return dEdge[2];
		}
		else if (p == points[1]) {
			return dEdge[0];
		}
		return dEdge[1];
	}

	public boolean getDelunayEdgeCW(final TriangulationPoint p) {
		if (p == points[0]) {
			return dEdge[1];
		}
		else if (p == points[1]) {
			return dEdge[2];
		}
		return dEdge[0];
	}

	public boolean getDelunayEdgeAcross(final TriangulationPoint p) {
		if (p == points[0]) {
			return dEdge[0];
		}
		else if (p == points[1]) {
			return dEdge[1];
		}
		return dEdge[2];
	}

	public void setDelunayEdgeCCW(final TriangulationPoint p, final boolean e) {
		if (p == points[0]) {
			dEdge[2] = e;
		}
		else if (p == points[1]) {
			dEdge[0] = e;
		}
		else {
			dEdge[1] = e;
		}
	}

	public void setDelunayEdgeCW(final TriangulationPoint p, final boolean e) {
		if (p == points[0]) {
			dEdge[1] = e;
		}
		else if (p == points[1]) {
			dEdge[2] = e;
		}
		else {
			dEdge[0] = e;
		}
	}

	public void setDelunayEdgeAcross(final TriangulationPoint p, final boolean e) {
		if (p == points[0]) {
			dEdge[0] = e;
		}
		else if (p == points[1]) {
			dEdge[1] = e;
		}
		else {
			dEdge[2] = e;
		}
	}

	public void clearDelunayEdges() {
		dEdge[0] = false;
		dEdge[1] = false;
		dEdge[2] = false;
	}

	public boolean isInterior() {
		return interior;
	}

	public void isInterior(final boolean b) {
		interior = b;
	}
}
