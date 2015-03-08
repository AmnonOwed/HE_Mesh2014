/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_Triangulation2D {
    
    /**
     * 
     */
    private int[][] _triangles;
    
    /**
     * 
     */
    private int[][] _edges;

    /**
     * 
     */
    public WB_Triangulation2D() {
    }

    /**
     * 
     *
     * @param T 
     * @param E 
     */
    public WB_Triangulation2D(final int[][] T, final int[][] E) {
	_triangles = T;
	_edges = E;
    }

    /**
     * 
     *
     * @return 
     */
    public int[][] getTriangles() {
	return _triangles;
    }

    /**
     * 
     *
     * @return 
     */
    public int[][] getEdges() {
	return _edges;
    }
}