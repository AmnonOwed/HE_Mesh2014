package wblut.external.ProGAL;

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
public class CTriangle extends Triangle {
	private final CTetrahedron[] adjacentTetrahedra = new CTetrahedron[2];
	private final CEdge[] edges = new CEdge[3];

	public CTriangle(CVertex p0, CVertex p1, CVertex p2, CTetrahedron t1,
			CTetrahedron t2) {
		super(p0, p1, p2);
		orderPoints(p0, p1, p2);

		this.adjacentTetrahedra[0] = t1;
		this.adjacentTetrahedra[1] = t2;
	}

	public void setNeighbour(int index, CTetrahedron t) {
		this.adjacentTetrahedra[index] = t;
	}

	public CTetrahedron getAdjacentTetrahedron(int index) {
		return this.adjacentTetrahedra[index];
	}

	/*
	 * public CTriangle[] getNeighborTriangles() { CTriangle[] triangles = new
	 * CTriangle[3]; return triangles; }
	 */

	public CEdge getEdge(int i) {
		return edges[i];
	}

	public void setEdge(int i, CEdge e) {
		edges[i] = e;
	}

	/** TODO: Copy to Triangle */
	public CVertex oppositeVertex(CEdge e) {
		if (!e.containsPoint(p1))
			return (CVertex) p1;
		if (!e.containsPoint(p2))
			return (CVertex) p2;
		else
			return (CVertex) p3;
	}

	private void orderPoints(CVertex a, CVertex b, CVertex c) {
		CVertex p[] = new CVertex[3];
		if (a.dominates(b) && a.dominates(c)) {
			p[0] = a;
			if (b.dominates(c)) {
				p[1] = b;
				p[2] = c;
			} else {
				p[1] = c;
				p[2] = b;
			}
		} else if (b.dominates(a) && b.dominates(c)) {
			p[0] = b;
			if (a.dominates(c)) {
				p[1] = a;
				p[2] = c;
			} else {
				p[1] = c;
				p[2] = a;
			}
		} else {
			p[0] = c;
			if (a.dominates(b)) {
				p[1] = a;
				p[2] = b;
			} else {
				p[1] = b;
				p[2] = a;
			}
		}
		super.p1 = p[0];
		super.p2 = p[1];
		super.p3 = p[2];
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CTriangle))
			return false;
		return ((CTriangle) o).p1 == p1 && ((CTriangle) o).p2 == p2
				&& ((CTriangle) o).p3 == p3;
	}

	@Override
	public int hashCode() {
		return p1.hashCode() ^ p2.hashCode() ^ p3.hashCode();
	}

	public boolean containsPoint(CVertex point) {
		return (p1 == point || p2 == point || p3 == point);
	}

	public boolean containsBigPoint() {
		return ((CVertex) p1).isBigpoint() || ((CVertex) p2).isBigpoint()
				|| ((CVertex) p3).isBigpoint();
	}

}
