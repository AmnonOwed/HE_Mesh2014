/*
 * 
 */
package wblut.geom;

import java.util.List;
import javolution.util.FastTable;

/**
 * 
 */
public class WB_Triangulation2DWithPoints extends WB_Triangulation2D {
    
    /**
     * 
     */
    private List<WB_Coordinate> _points;

    /**
     * 
     */
    public WB_Triangulation2DWithPoints() {
    }

    /**
     * 
     *
     * @param T 
     * @param E 
     * @param P 
     */
    public WB_Triangulation2DWithPoints(final int[][] T, final int[][] E,
	    final List<? extends WB_Coordinate> P) {
	super(T, E);
	_points = new FastTable<WB_Coordinate>();
	_points.addAll(P);
    }

    /**
     * 
     *
     * @param tri 
     */
    protected WB_Triangulation2DWithPoints(final WB_Triangulation2D tri) {
	super(tri.getTriangles(), tri.getEdges());
	_points = null;
    }

    /**
     * 
     *
     * @return 
     */
    public List<WB_Coordinate> getPoints() {
	return _points;
    }
}