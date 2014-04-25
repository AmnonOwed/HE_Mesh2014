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

public class CVertex extends Point {
	private static final long serialVersionUID = 1L;

	private boolean degenerate = false;

	public enum DegenerateCase {
		ONFACE, ONEDGE
	};

	private DegenerateCase degCase;
	private CVertex degPointOpposite;
	private CVertex degPointA;
	private CVertex degPointB;
	private final boolean bigPoint;

	private final List<CEdge> adjacentEdges = new ArrayList<CEdge>();

	public boolean isBigpoint() {
		return bigPoint;
	}

	public CVertex getDegPointA() {
		return degPointA;
	}

	public void setDegPointA(CVertex degPointA) {
		this.degPointA = degPointA;
	}

	public CVertex getDegPointB() {
		return degPointB;
	}

	public void setDegPointB(CVertex degPointB) {
		this.degPointB = degPointB;
	}

	public DegenerateCase getDegCase() {
		return this.degCase;
	}

	public void setDegCase(DegenerateCase degCase) {
		this.degCase = degCase;
	}

	public boolean isDegenerate() {
		return degenerate;
	}

	public void setDegenerate(boolean degenerate) {
		this.degenerate = degenerate;
	}

	public CVertex(Point p) {
		this(p, false);
	}

	public CVertex(Point p, boolean bigpoint) {
		super(p);
		setDegenerate(false);
		this.bigPoint = bigpoint;
	}

	public CVertex getDegPointOpposite() {
		return degPointOpposite;
	}

	public void setDegPointOpposite(CVertex degPointOpposite) {
		this.degPointOpposite = degPointOpposite;
	}

	public void addAdjacentEdge(CEdge e) {
		adjacentEdges.add(e);
	}

	public List<CEdge> getAdjacentEdges() {
		return adjacentEdges;
	}

	public List<CTriangle> getAdjacentTriangles() {
		List<CTriangle> adjacentTriangles = new ArrayList<CTriangle>();
		for (CEdge edge : adjacentEdges) {
			List<CTriangle> triangles = edge.getAdjacentTriangles();
			for (CTriangle triangle : triangles) {
				if (!adjacentTriangles.contains(triangle))
					adjacentTriangles.add(triangle);
			}
		}
		return adjacentTriangles;
	}

	public List<CTriangle> getOppositeTriangles() {
		List<CTriangle> oppositeTriangles = new ArrayList<CTriangle>();
		for (CTetrahedron tetr : getAllAdjacentTetrahedra()) {
			int index = tetr.findpoint(this);
			if (!tetr.containsBigPoint())
				oppositeTriangles.add(tetr.getTriangle(index));
		}
		return oppositeTriangles;
	}

	public List<CTetrahedron> getAdjacentTetrahedra() {
		List<CTetrahedron> adjacentTetrahedra = new ArrayList<CTetrahedron>();
		for (CTriangle triangle : getAdjacentTriangles()) {
			for (int i = 0; i < 2; i++) {
				CTetrahedron tetr = triangle.getAdjacentTetrahedron(i);
				if (!tetr.containsBigPoint()) {
					if (!adjacentTetrahedra.contains(tetr))
						adjacentTetrahedra.add(tetr);
				}
			}
		}
		return adjacentTetrahedra;
	}

	// public List<CTetrahedron> getOppositeTetrahedra() {
	// List<CTetrahedron> oppositeTetrahedra = new ArrayList<CTetrahedron>();
	// for (CTetrahedron tetr : getAllAdjacentTetrahedra()) {
	// tetr.
	//
	// }
	// }

	/** returns all tetrahedra adjacent to the vertex (including big tetrahedra) */
	public List<CTetrahedron> getAllAdjacentTetrahedra() {
		List<CTetrahedron> adjacentTetrahedra = new ArrayList<CTetrahedron>();
		for (CTriangle triangle : getAdjacentTriangles()) {
			for (int i = 0; i < 2; i++) {
				CTetrahedron tetr = triangle.getAdjacentTetrahedron(i);
				if (!adjacentTetrahedra.contains(tetr))
					adjacentTetrahedra.add(tetr);
			}
		}
		return adjacentTetrahedra;
	}

	/** returns all big tetrahedra adjacent to the vertex */
	public List<CTetrahedron> getBigAdjacentTetrahedra() {
		List<CTetrahedron> adjacentBigTetrahedra = new ArrayList<CTetrahedron>();
		for (CTetrahedron tetr : getAllAdjacentTetrahedra()) {
			if (tetr.containsBigPoint()
					&& !adjacentBigTetrahedra.contains(tetr))
				adjacentBigTetrahedra.add(tetr);
		}
		return adjacentBigTetrahedra;
	}

	public String toString() {
		return toString(2);
	}

	public String toString(int dec) {
		return String.format("CVertex[x=%." + dec + "f,y=%." + dec + "f,z=%."
				+ dec + "f%s]", x(), y(), z(), bigPoint ? ",big point" : "");
	}
}
