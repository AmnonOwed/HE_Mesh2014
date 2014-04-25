package wblut.geom;


public class WB_Triangulation3D {
	private int[] _tetrahedra;
	private int[] _triangles;
	private int[] _edges;

	public WB_Triangulation3D() {

	}

	public WB_Triangulation3D(final int[] tetra, final int[] tri,
			final int[] edge) {
		_tetrahedra = tetra;
		_triangles = tri;
		_edges = edge;
	}

	public int[] getTetrahedra() {
		return _tetrahedra;
	}

	public int[] getTriangles() {
		return _triangles;
	}

	public int[] getEdges() {
		return _edges;
	}

}