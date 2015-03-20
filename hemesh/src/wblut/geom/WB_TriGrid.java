/*
 *
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WB_TriGrid {
    static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    /**
     *
     */
    double scale;
    /**
     *
     */
    double c60 = Math.cos(Math.PI / 3.0);
    /**
     *
     */
    double s60 = Math.sin(Math.PI / 3.0);

    /**
     *
     */
    public WB_TriGrid() {
	this.scale = 1.0;
    }

    /**
     *
     *
     * @param scale
     */
    public WB_TriGrid(final double scale) {
	this.scale = scale;
    }

    /**
     *
     *
     * @param scale
     */
    public void setScale(final double scale) {
	this.scale = scale;
    }

    /**
     *
     *
     * @param b
     * @param c
     * @return
     */
    public WB_Point getPoint(final int b, final int c) {
	return new WB_Point(scale * ((c60 * c) + b), scale * s60 * c, 0);
    }

    /**
     * http://www.voidinspace.com/2014/07/project-twa-part-1-generating-a-
     * hexagonal-tile-and-its-triangular-grid/
     *
     * @param level
     */
    public WB_Mesh getHex(final int level) {
	final double sin60 = Math.sin(Math.PI / 3);
	final double inv_tan60 = 1.0 / Math.tan(Math.PI / 3);
	final double RdS = scale;
	int num_vertices = 1;
	for (int i = 1; i <= level; i++) {
	    num_vertices += i * 6;
	}
	int vertices_index = 0;
	final WB_Point[] vertices = new WB_Point[num_vertices];
	int num_indices = 0;
	for (int i = 1; i <= level; i++) {
	    num_indices += 12 * i - 6;
	}
	int indices_index = 0;
	final int[][] indices = new int[num_indices][3];
	int current_num_points = 0;
	int prev_row_num_points = 0;
	final int np_col_0 = 2 * level + 1;
	final int col_min = -level;
	final int col_max = level;
	for (int itC = col_min; itC <= col_max; itC++) {
	    final double x = sin60 * RdS * itC;
	    final int np_col_i = np_col_0 - Math.abs(itC);
	    int row_min = -level;
	    if (itC < 0) {
		row_min += Math.abs(itC);
	    }
	    final int row_max = row_min + np_col_i - 1;
	    current_num_points += np_col_i;
	    for (int itR = row_min; itR <= row_max; itR++) {
		final double y = inv_tan60 * x + RdS * itR;
		vertices[vertices_index] = new WB_Point(x, y, 0);
		if (vertices_index < (current_num_points - 1)) {
		    if (itC >= col_min && itC < col_max) {
			int pad_left = 0;
			if (itC < 0) {
			    pad_left = 1;
			}
			indices[indices_index][0] = vertices_index;
			indices[indices_index][1] = vertices_index + 1;
			indices[indices_index][2] = vertices_index + np_col_i
				+ pad_left;
			indices_index++;
		    }
		    if (itC > col_min && itC <= col_max) {
			int pad_right = 0;
			if (itC > 0) {
			    pad_right = 1;
			}
			indices[indices_index][0] = vertices_index + 1;
			indices[indices_index][1] = vertices_index;
			indices[indices_index][2] = vertices_index
				- prev_row_num_points + pad_right;
			indices_index++;
		    }
		}
		vertices_index++;
	    }
	    prev_row_num_points = np_col_i;
	}
	return gf.createMesh(vertices, indices);
    }

    public List<WB_Polygon> getHexTriangles(final int level) {
	final double sin60 = Math.sin(Math.PI / 3);
	final double inv_tan60 = 1.0 / Math.tan(Math.PI / 3);
	final double RdS = scale;
	int num_vertices = 1;
	for (int i = 1; i <= level; i++) {
	    num_vertices += i * 6;
	}
	int vertices_index = 0;
	final WB_Point[] vertices = new WB_Point[num_vertices];
	int num_indices = 0;
	for (int i = 1; i <= level; i++) {
	    num_indices += 12 * i - 6;
	}
	int indices_index = 0;
	final int[][] indices = new int[num_indices][3];
	int current_num_points = 0;
	int prev_row_num_points = 0;
	final int np_col_0 = 2 * level + 1;
	final int col_min = -level;
	final int col_max = level;
	for (int itC = col_min; itC <= col_max; itC++) {
	    final double x = sin60 * RdS * itC;
	    final int np_col_i = np_col_0 - Math.abs(itC);
	    int row_min = -level;
	    if (itC < 0) {
		row_min += Math.abs(itC);
	    }
	    final int row_max = row_min + np_col_i - 1;
	    current_num_points += np_col_i;
	    for (int itR = row_min; itR <= row_max; itR++) {
		final double y = inv_tan60 * x + RdS * itR;
		vertices[vertices_index] = new WB_Point(x, y, 0);
		if (vertices_index < (current_num_points - 1)) {
		    if (itC >= col_min && itC < col_max) {
			int pad_left = 0;
			if (itC < 0) {
			    pad_left = 1;
			}
			indices[indices_index][0] = vertices_index;
			indices[indices_index][1] = vertices_index + 1;
			indices[indices_index][2] = vertices_index + np_col_i
				+ pad_left;
			indices_index++;
		    }
		    if (itC > col_min && itC <= col_max) {
			int pad_right = 0;
			if (itC > 0) {
			    pad_right = 1;
			}
			indices[indices_index][0] = vertices_index + 1;
			indices[indices_index][1] = vertices_index;
			indices[indices_index][2] = vertices_index
				- prev_row_num_points + pad_right;
			indices_index++;
		    }
		}
		vertices_index++;
	    }
	    prev_row_num_points = np_col_i;
	}
	final List<WB_Polygon> triangles = new ArrayList<WB_Polygon>(
		indices.length);
	for (int i = 0; i < indices.length; i++) {
	    final int[] triangle = indices[i];
	    triangles.add(gf.createSimplePolygon(vertices[triangle[0]].get(),
		    vertices[triangle[1]].get(), vertices[triangle[2]].get()));
	}
	return triangles;
    }
}
