package wblut.geom;

import java.util.List;


public class WB_Triangulation2DWithPoints extends WB_Triangulation2D {
	private List<WB_Point> _points;

	public WB_Triangulation2DWithPoints() {

	}

	public WB_Triangulation2DWithPoints(final int[][] T, final int[][] E,
			final List<WB_Point> P) {
		super(T, E);
		_points = P;
	}

	protected WB_Triangulation2DWithPoints(final WB_Triangulation2D tri) {
		super(tri.getTriangles(), tri.getEdges());
		_points = null;
	}

	public List<WB_Point> getPoints() {
		return _points;
	}

}