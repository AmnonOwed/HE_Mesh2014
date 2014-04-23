package wblut.geom;

public class WB_Triangulation2D {
	private int[][] _triangles;
	private int[][] _edges;

	public WB_Triangulation2D() {

	}

	public WB_Triangulation2D(final int[][] T, final int[][] E) {
		_triangles = T;
		_edges = E;
	}

	public int[][] getTriangles() {
		return _triangles;
	}

	public int[][] getEdges() {
		return _edges;
	}

}