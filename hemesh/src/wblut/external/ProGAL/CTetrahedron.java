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
 * 
 * An extension of the normal Tetrahedron that is used in complexes. In addition
 * to the four corner-points, pointers to the triangular faces (of the type
 * CTriangle) and the four neighboring tetrahedra are maintained.
 * 
 * @author R.Fonseca
 */
public class CTetrahedron extends Tetrahedron {
	private final CTetrahedron[] neighbours = new CTetrahedron[4];
	private final CTriangle[] triangles = new CTriangle[4];
	private boolean modified = false;
	private boolean flat = false;

	public CTetrahedron(CVertex p0, CVertex p1, CVertex p2, CVertex p3) {
		super(p0, p1, p2, p3);
	}

	protected CTetrahedron() {
		this(null, null, null, null);
	}

	public void setFlat(boolean flat) {
		this.flat = flat;
	}

	public void setModified(boolean modified) {
		this.modified = modified;
	}

	public void setPoint(CVertex p, int i) {
		super.corners[i] = p;
	}

	public void setNeighbour(int index, CTetrahedron t) {
		neighbours[index] = t;
	}

	public void setTriangle(int index, CTriangle t) {
		triangles[index] = t;
	}

	@Override
	public CVertex getPoint(int i) {
		return (CVertex) corners[i];
	}

	public CTetrahedron getNeighbour(int index) {
		return neighbours[index];
	}

	public CTriangle getTriangle(int index) {
		return triangles[index];
	}

	public boolean isModified() {
		return modified;
	}

	public boolean isFlat() {
		return flat;
	}

	/**
	 * For computational convenience, the representation of a complex is based
	 * on a big tetrahedron that encloses all vertices. It has 4 so-called 'big
	 * points' as corners. This method indicates if this tetrahedron has one of
	 * these 'big points' as corners.
	 * 
	 */
	public boolean containsBigPoint() {
		if (getPoint(0).isBigpoint() || getPoint(1).isBigpoint()
				|| getPoint(2).isBigpoint() || getPoint(3).isBigpoint())
			return true;
		return false;
	}

	public int getNumberBigPoints() {
		int count = 0;
		for (int i = 0; i < 4; i++) {
			if (getPoint(i).isBigpoint())
				count++;
		}
		return count;
	}

	/** returns neighbour tetrahedron containing specified vertex */
	public CTetrahedron getNeighbour(CVertex v) {
		for (int i = 0; i < 4; i++) {
			CTetrahedron tetr = getNeighbour(i);
			if (tetr.containsPoint(v))
				return tetr;
		}
		return null;
	}

	public int getID(CVertex v) {
		if (v == getPoint(0))
			return 0;
		else {
			if (v == getPoint(1))
				return 1;
			else {
				if (v == getPoint(2))
					return 2;
				else {
					if (v == getPoint(3))
						return 3;
					else
						return -1;
				}
			}
		}
	}

	/** returns the vertices shared by two tetrahedra. */
	public CVertex[] getCommonVertices(CTetrahedron tetr) {
		CVertex[] points = new CVertex[4];
		int n = 0;
		for (int i = 0; i < 4; i++) {
			if (tetr.containsPoint(getPoint(i))) {
				points[n] = new CVertex(getPoint(i));
				for (int k = 0; k < 3; k++)
					if (Math.abs(points[n].get(k)) > 100.0)
						points[n].set(k, points[n].get(k) / 1);
				n++;
			}
		}
		return points;
	}

	/**
	 * returns plane through common triangle of this and another tetrahedron.
	 * The apex of this tetrahedron is below the plane.
	 */
	public Plane getPlane(CTetrahedron tetr) {
		CVertex[] points = new CVertex[3];
		CVertex v = null;
		int i = 0;
		int j = 0;
		while (i < 3) {
			if (tetr.containsPoint(getPoint(j))) {
				points[i] = getPoint(j);
				i++;
			} else
				v = getPoint(j);
			j++;
		}
		Plane plane;
		if (!points[0].isBigpoint())
			plane = new Plane(points[0], points[1], points[2]);
		else {
			if (!points[1].isBigpoint())
				plane = new Plane(points[1], points[2], points[0]);
			else
				plane = new Plane(points[2], points[0], points[1]);
		}
		if (plane.above(v) == 1)
			plane.setNormal(plane.getNormal().multiplyThis(-1));
		return plane;
	}

	public void updateNeighbour(CTetrahedron lookfor, CTetrahedron replacement) {
		for (int i = 0; i < 4; i++) {
			if (neighbours[i] == lookfor) {
				neighbours[i] = replacement;
				break;
			}
		}
	}

	// find id of point
	public int findpoint(CVertex p) {
		for (int i = 0; i < 4; i++) {
			if (getPoint(i) == p) {
				return i;
			}
		}
		System.out.println("Problemer med findpoint\n");
		// never happens:
		return -1;
	}

	/** returns neighbouring tetrahedron containing v as the oppposite vertex */
	public CTetrahedron findNeighbour(CVertex v) {
		for (int i = 0; i < 4; i++) {
			if (getNeighbour(i).containsPoint(v))
				return getNeighbour(i);
		}
		return null;
	}

	/*
	 * this tetrahedron and tetr must be neighbours. Return the vertex of this
	 * tetrahedron not in tetr
	 */
	public CVertex findVertex(CTetrahedron tetr) {
		CVertex p;
		for (int i = 0; i < 4; i++) {
			p = getPoint(i);
			if (!tetr.containsPoint(p))
				return p;
		}
		return null;
	}

	public boolean containsPoint(CVertex p) {
		for (int i = 0; i < 4; i++) {
			if (getPoint(i) == p)
				return true;
		}
		return false;
	}

	public boolean containsTriangle(CTriangle t) {
		for (int tp = 0; tp < 3; tp++) {
			boolean found = false;
			for (int p = 0; p < 4; p++)
				if (this.getPoint(p) == t.getPoint(tp)) {
					found = true;
					break;
				}
			if (!found)
				return false;
		}
		return true;
	}

	/** TODO: Copy to Tetrahedron */
	public CVertex oppositeVertex(CTriangle base) {
		for (int p = 0; p < 4; p++) {
			if (!base.containsPoint(getPoint(p)))
				return getPoint(p);
		}
		throw new RuntimeException(
				"The triangle is not part of this tetrahedron");
	}

	public CTriangle oppositeTriangle(CVertex v) {
		for (CTriangle t : triangles) {
			if (t != null && !t.containsPoint(v))
				return t;
		}
		throw new RuntimeException("The vertex is not part of this tetrahedron");
	}

	// given a point index this method finds the index of the apex - meaning the
	// opposite point id that is in
	// the tetrahedron opposite the given point id
	// input: point index
	// output: point index of the point opposite
	public int apexid(int index) {
		// Point ap0,ap1,ap2,ap3;
		CTetrahedron apex_tet = getNeighbour(index);
		if (apex_tet != null) {
			for (int i = 0; i < 4; i++) {
				if (apex_tet.getNeighbour(i) == this) {
					return i;
				}
			}
		}
		// never happens:
		return -1;

	}

}
