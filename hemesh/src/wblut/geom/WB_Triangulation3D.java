package wblut.geom;

public class WB_Triangulation3D {
	private int[][] _tetrahedra;

	public WB_Triangulation3D() {

	}

	public WB_Triangulation3D(final int[][] tetra) {
		_tetrahedra = tetra;
	}

	public int[][] getTetrahedra() {
		return _tetrahedra;
	}

}