package wblut.external.ProGAL;

import java.util.ArrayList;
import java.util.List;

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
 */

public class CEdge extends LineSegment {

	private final List<CTriangle> adjacentTriangles = new ArrayList<CTriangle>();

	public CEdge(CVertex p0, CVertex p1) {
		super(p0, p1);
	}

	public void addTriangle(CTriangle tri) {
		adjacentTriangles.add(tri);
	}

	public List<CTriangle> getAdjacentTriangles() {
		return adjacentTriangles;
	}

	public CVertex getPoint(int i) {
		return (CVertex) super.getPoint(i);
	}

	public boolean containsPoint(Point p) {
		return a == p || b == p;
	}

	// Equals and hashCode used in DelaunayComplex to uniquely specify an edge
	public boolean equals(Object o) {
		if (!(o instanceof CEdge))
			return false;
		return (((CEdge) o).a == a && ((CEdge) o).b == b)
				|| (((CEdge) o).a == b && ((CEdge) o).b == a);
	}

	public int hashCode() {
		return a.hashCode() ^ b.hashCode();
	}

	public CVertex opposite(CVertex v) {
		if (v == a)
			return (CVertex) b;
		if (v == b)
			return (CVertex) a;
		throw new Error("Vertex is not an end-point of this edge");
	}
}
