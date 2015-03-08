/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.List;
import wblut.external.ProGAL.AlphaComplex;
import wblut.external.ProGAL.CTetrahedron;
import wblut.external.ProGAL.CTriangle;
import wblut.external.ProGAL.CVertex;
import wblut.external.ProGAL.Point;

/**
 * 
 */
public class WB_AlphaComplex {
    
    /**
     * 
     */
    private final double[] tetrainalphas;
    
    /**
     * 
     */
    private final double[] tetraorients;
    
    /**
     * 
     */
    private final int[][] tetraindices;
    
    /**
     * 
     */
    private final int npoints;
    
    /**
     * 
     */
    private final List<Point> _points;
    
    /**
     * 
     */
    private final AlphaComplex af;
    
    /**
     * 
     */
    private final WB_KDTree<WB_Coordinate, Integer> tree;
    
    /**
     * 
     */
    private final WB_Predicates predicates = new WB_Predicates();

    /**
     * 
     *
     * @param points 
     */
    public WB_AlphaComplex(final WB_Coordinate[] points) {
	npoints = points.length;
	_points = new ArrayList<Point>(npoints);
	tree = new WB_KDTree<WB_Coordinate, Integer>();
	for (int i = 0; i < npoints; i++) {
	    _points.add(new Point(points[i].xd(), points[i].yd(), points[i]
		    .zd()));
	    tree.add(points[i], i);
	}
	af = new AlphaComplex(_points);
	final List<CTetrahedron> tetras = af.getTetrahedra();
	final int ntetra = tetras.size();
	tetrainalphas = new double[ntetra];
	int i = 0;
	for (final CTetrahedron tetra : tetras) {
	    tetrainalphas[i] = af.getInAlpha(tetra);
	    i++;
	}
	tetraindices = new int[ntetra][4];
	tetraorients = new double[ntetra];
	for (i = 0; i < ntetra; i++) {
	    final CTetrahedron tetra = tetras.get(i);
	    int index = tree.getNearestNeighbor(convert(tetra.getPoint(0))).value;
	    tetraindices[i][0] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(1))).value;
	    tetraindices[i][1] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(2))).value;
	    tetraindices[i][2] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(3))).value;
	    tetraindices[i][3] = index;
	    tetraorients[i] = predicates.orientTetra(
		    points[tetraindices[i][0]], points[tetraindices[i][1]],
		    points[tetraindices[i][2]], points[tetraindices[i][3]]);
	    if (tetraorients[i] > 0) {
		int tmp = tetraindices[i][0];
		tetraindices[i][0] = tetraindices[i][3];
		tetraindices[i][3] = tmp;
		tmp = tetraindices[i][1];
		tetraindices[i][1] = tetraindices[i][2];
		tetraindices[i][2] = tmp;
	    }
	}
    }

    /**
     * 
     *
     * @param points 
     * @param joggle 
     */
    public WB_AlphaComplex(final WB_Coordinate[] points, final double joggle) {
	npoints = points.length;
	_points = new ArrayList<Point>(npoints);
	tree = new WB_KDTree<WB_Coordinate, Integer>();
	for (int i = 0; i < npoints; i++) {
	    final WB_Point p = new WB_Point(points[i].xd()
		    + ((Math.random() - 0.5) * joggle), points[i].yd()
		    + ((Math.random() - 0.5) * joggle), points[i].zd()
		    + ((Math.random() - 0.5) * joggle));
	    _points.add(new Point(p.xd(), p.yd(), p.zd()));
	    tree.add(p, i);
	}
	af = new AlphaComplex(_points);
	List<CTetrahedron> tetras = af.getTetrahedra();
	tetras = af.getTetrahedra();
	final int ntetra = tetras.size();
	tetrainalphas = new double[ntetra];
	int i = 0;
	for (final CTetrahedron tetra : tetras) {
	    tetrainalphas[i] = af.getInAlpha(tetra);
	    i++;
	}
	tetraindices = new int[ntetra][4];
	tetraorients = new double[ntetra];
	for (i = 0; i < ntetra; i++) {
	    final CTetrahedron tetra = tetras.get(i);
	    int index = tree.getNearestNeighbor(convert(tetra.getPoint(0))).value;
	    tetraindices[i][0] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(1))).value;
	    tetraindices[i][1] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(2))).value;
	    tetraindices[i][2] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(3))).value;
	    tetraindices[i][3] = index;
	    tetraorients[i] = predicates.orientTetra(
		    points[tetraindices[i][0]], points[tetraindices[i][1]],
		    points[tetraindices[i][2]], points[tetraindices[i][3]]);
	    if (tetraorients[i] > 0) {
		int tmp = tetraindices[i][0];
		tetraindices[i][0] = tetraindices[i][3];
		tetraindices[i][3] = tmp;
		tmp = tetraindices[i][1];
		tetraindices[i][1] = tetraindices[i][2];
		tetraindices[i][2] = tmp;
	    }
	}
    }

    /**
     * 
     *
     * @param points 
     */
    public WB_AlphaComplex(final List<? extends WB_Coordinate> points) {
	npoints = points.size();
	_points = new ArrayList<Point>(npoints);
	tree = new WB_KDTree<WB_Coordinate, Integer>();
	for (int i = 0; i < npoints; i++) {
	    _points.add(new Point(points.get(i).xd(), points.get(i).yd(),
		    points.get(i).zd()));
	    tree.add(points.get(i), i);
	}
	af = new AlphaComplex(_points);
	final List<CTetrahedron> tetras = af.getTetrahedra();
	final int ntetra = tetras.size();
	tetrainalphas = new double[ntetra];
	int i = 0;
	for (final CTetrahedron tetra : tetras) {
	    tetrainalphas[i] = af.getInAlpha(tetra);
	    i++;
	}
	tetraindices = new int[ntetra][4];
	tetraorients = new double[ntetra];
	for (i = 0; i < ntetra; i++) {
	    final CTetrahedron tetra = tetras.get(i);
	    int index = tree.getNearestNeighbor(convert(tetra.getPoint(0))).value;
	    tetraindices[i][0] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(1))).value;
	    tetraindices[i][1] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(2))).value;
	    tetraindices[i][2] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(3))).value;
	    tetraindices[i][3] = index;
	    tetraorients[i] = predicates.orientTetra(
		    points.get(tetraindices[i][0]),
		    points.get(tetraindices[i][1]),
		    points.get(tetraindices[i][2]),
		    points.get(tetraindices[i][3]));
	    if (tetraorients[i] > 0) {
		int tmp = tetraindices[i][0];
		tetraindices[i][0] = tetraindices[i][3];
		tetraindices[i][3] = tmp;
		tmp = tetraindices[i][1];
		tetraindices[i][1] = tetraindices[i][2];
		tetraindices[i][2] = tmp;
	    }
	}
    }

    /**
     * 
     *
     * @param points 
     * @param joggle 
     */
    public WB_AlphaComplex(final List<? extends WB_Coordinate> points,
	    final double joggle) {
	npoints = points.size();
	_points = new ArrayList<Point>(npoints);
	tree = new WB_KDTree<WB_Coordinate, Integer>();
	for (int i = 0; i < npoints; i++) {
	    final WB_Point p = new WB_Point(points.get(i).xd()
		    + ((Math.random() - 0.5) * joggle), points.get(i).yd()
		    + ((Math.random() - 0.5) * joggle), points.get(i).zd()
		    + ((Math.random() - 0.5) * joggle));
	    _points.add(new Point(p.xd(), p.yd(), p.zd()));
	    tree.add(p, i);
	}
	af = new AlphaComplex(_points);
	List<CTetrahedron> tetras = af.getTetrahedra();
	tetras = af.getTetrahedra();
	final int ntetra = tetras.size();
	tetrainalphas = new double[ntetra];
	int i = 0;
	for (final CTetrahedron tetra : tetras) {
	    tetrainalphas[i] = af.getInAlpha(tetra);
	    i++;
	}
	tetraindices = new int[ntetra][4];
	tetraorients = new double[ntetra];
	for (i = 0; i < ntetra; i++) {
	    final CTetrahedron tetra = tetras.get(i);
	    int index = tree.getNearestNeighbor(convert(tetra.getPoint(0))).value;
	    tetraindices[i][0] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(1))).value;
	    tetraindices[i][1] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(2))).value;
	    tetraindices[i][2] = index;
	    index = tree.getNearestNeighbor(convert(tetra.getPoint(3))).value;
	    tetraindices[i][3] = index;
	    tetraorients[i] = predicates.orientTetra(
		    points.get(tetraindices[i][0]),
		    points.get(tetraindices[i][1]),
		    points.get(tetraindices[i][2]),
		    points.get(tetraindices[i][3]));
	    if (tetraorients[i] > 0) {
		int tmp = tetraindices[i][0];
		tetraindices[i][0] = tetraindices[i][3];
		tetraindices[i][3] = tmp;
		tmp = tetraindices[i][1];
		tetraindices[i][1] = tetraindices[i][2];
		tetraindices[i][2] = tmp;
	    }
	}
    }

    /**
     * 
     *
     * @param filter 
     * @return 
     */
    public int[][] getAlphaComplexShape(final double filter) {
	final List<int[]> tmpresult = new ArrayList<int[]>();
	final List<CTriangle> tris = af.getTriangles();
	for (final CTriangle tri : tris) {
	    final CTetrahedron T0 = tri.getAdjacentTetrahedron(0);
	    final CTetrahedron T1 = tri.getAdjacentTetrahedron(1);
	    final double a0 = (T0.containsBigPoint()) ? Double.NaN : af
		    .getInAlpha(T0);
	    final double a1 = (T1.containsBigPoint()) ? Double.NaN : af
		    .getInAlpha(T1);
	    boolean include = false;
	    if (Double.isNaN(a0) && Double.isNaN(a1)) {
		include = false;
	    } else if (Double.isNaN(a0)) {
		include = (a1 <= filter);
	    } else if (Double.isNaN(a1)) {
		include = (a0 <= filter);
	    } else if (((a0 > filter) && (a1 <= filter))
		    || ((a1 > filter) && (a0 <= filter))) {
		include = true;
	    }
	    if (include) {
		final int[] tmp = new int[3];
		for (int k = 0; k < 3; k++) {
		    final int index = tree.getNearestNeighbor(convert(tri
			    .getPoint(k))).value;
		    tmp[k] = index;
		}
		tmpresult.add(tmp);
	    }
	    // }
	}
	final int[][] result = new int[tmpresult.size()][3];
	for (int i = 0; i < tmpresult.size(); i++) {
	    for (int j = 0; j < 3; j++) {
		result[i][j] = tmpresult.get(i)[j];
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param v 
     * @return 
     */
    private static WB_Point convert(final CVertex v) {
	return new WB_Point(v.x(), v.y(), v.z());
    }

    /**
     * 
     *
     * @param v 
     * @return 
     */
    private static WB_Point convert(final Point v) {
	return new WB_Point(v.x(), v.y(), v.z());
    }
}
