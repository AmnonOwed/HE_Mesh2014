package wblut.geom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import wblut.external.ProGAL.AlphaComplex;
import wblut.external.ProGAL.CTetrahedron;
import wblut.external.ProGAL.CTriangle;
import wblut.external.ProGAL.CVertex;
import wblut.external.ProGAL.Point;

public class WB_AlphaComplex {

	private final double[] tetrainalphas;

	private final double[] tetraorients;

	private final int[][] tetraindices;

	private final int npoints;

	private final List<Point> _points;

	private final AlphaComplex af;

	private final WB_KDTree<WB_Coordinate, Integer> tree;

	private final WB_Predicates predicates = new WB_Predicates();

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
		List<CTetrahedron> tetras = af.getTetrahedra();
		int ntetra = tetras.size();
		tetrainalphas = new double[ntetra];
		int i = 0;
		for (CTetrahedron tetra : tetras) {
			tetrainalphas[i] = af.getInAlpha(tetra);
			Point c = tetra.circumcenter();
			i++;
		}
		tetraindices = new int[ntetra][4];
		tetraorients = new double[ntetra];
		for (i = 0; i < ntetra; i++) {
			CTetrahedron tetra = tetras.get(i);
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

	public WB_AlphaComplex(final WB_Coordinate[] points, final double joggle) {
		npoints = points.length;
		_points = new ArrayList<Point>(npoints);
		tree = new WB_KDTree<WB_Coordinate, Integer>();
		for (int i = 0; i < npoints; i++) {
			WB_Point p = new WB_Point(points[i].xd() + (Math.random() - 0.5)
					* joggle, points[i].yd() + (Math.random() - 0.5) * joggle,
					points[i].zd() + (Math.random() - 0.5) * joggle);
			_points.add(new Point(p.x, p.y, p.z));
			tree.add(p, i);
		}
		af = new AlphaComplex(_points);
		List<CTetrahedron> tetras = af.getTetrahedra();
		tetras = af.getTetrahedra();
		int ntetra = tetras.size();
		tetrainalphas = new double[ntetra];
		int i = 0;
		for (CTetrahedron tetra : tetras) {
			tetrainalphas[i] = af.getInAlpha(tetra);
			Point c = tetra.circumcenter();
			i++;
		}
		tetraindices = new int[ntetra][4];
		tetraorients = new double[ntetra];
		for (i = 0; i < ntetra; i++) {
			CTetrahedron tetra = tetras.get(i);
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
		List<CTetrahedron> tetras = af.getTetrahedra();
		int ntetra = tetras.size();
		tetrainalphas = new double[ntetra];
		int i = 0;
		for (CTetrahedron tetra : tetras) {
			tetrainalphas[i] = af.getInAlpha(tetra);
			Point c = tetra.circumcenter();
			i++;
		}
		tetraindices = new int[ntetra][4];
		tetraorients = new double[ntetra];
		for (i = 0; i < ntetra; i++) {
			CTetrahedron tetra = tetras.get(i);
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

	public WB_AlphaComplex(final List<? extends WB_Coordinate> points,
			final double joggle) {
		npoints = points.size();
		_points = new ArrayList<Point>(npoints);
		tree = new WB_KDTree<WB_Coordinate, Integer>();
		for (int i = 0; i < npoints; i++) {
			WB_Point p = new WB_Point(points.get(i).xd()
					+ (Math.random() - 0.5) * joggle, points.get(i).yd()
					+ (Math.random() - 0.5) * joggle, points.get(i).zd()
					+ (Math.random() - 0.5) * joggle);
			_points.add(new Point(p.x, p.y, p.z));
			tree.add(p, i);
		}
		af = new AlphaComplex(_points);
		List<CTetrahedron> tetras = af.getTetrahedra();
		tetras = af.getTetrahedra();
		int ntetra = tetras.size();
		tetrainalphas = new double[ntetra];
		int i = 0;
		for (CTetrahedron tetra : tetras) {
			tetrainalphas[i] = af.getInAlpha(tetra);
			Point c = tetra.circumcenter();
			i++;
		}
		tetraindices = new int[ntetra][4];
		tetraorients = new double[ntetra];
		for (i = 0; i < ntetra; i++) {
			CTetrahedron tetra = tetras.get(i);
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

	public int[] getAlphaShape(final double filter) {
		List<CTriangle> tris = af.getAlphaShape(filter);
		int[] result = new int[3 * tris.size()];
		for (int i = 0; i < tris.size(); i++) {
			CTriangle tri = tris.get(i);
			for (int j = 0; j < 3; j++) {
				int index = tree.getNearestNeighbor(convert(tri.getPoint(j))).value;
				result[3 * i + j] = index;
			}
		}
		return result;
	}

	public int[][] getAlphaShapeFacelist(final double filter) {
		List<CTriangle> tris = af.getAlphaShape(filter);
		int[][] result = new int[tris.size()][3];
		for (int i = 0; i < tris.size(); i++) {
			CTriangle tri = tris.get(i);
			for (int j = 0; j < 3; j++) {
				int index = tree.getNearestNeighbor(convert(tri.getPoint(j))).value;
				result[i][j] = index;
			}
		}
		return result;
	}

	public int[][] getAlphaComplexHull(final double filter) {
		List<int[]> tmpresult = new ArrayList<int[]>();
		List<CTetrahedron> tetras = af.getTetrahedra();
		int ntetra = tetras.size();
		for (int i = 0; i < ntetra; i++) {
			CTetrahedron tetra = tetras.get(i);
			for (int j = 0; j < 4; j++) {
				CTriangle tri = tetra.getTriangle(j);
				CTetrahedron T0 = tri.getAdjacentTetrahedron(0);
				double a0 = (T0.containsBigPoint()) ? Double.NaN : af
						.getInAlpha(T0);
				CTetrahedron T1 = tri.getAdjacentTetrahedron(1);
				double a1 = (T1.containsBigPoint()) ? Double.NaN : af
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
					int[] tmp = new int[3];
					for (int k = 0; k < 3; k++) {
						int index = tree.getNearestNeighbor(convert(tri
								.getPoint(k))).value;
						tmp[k] = index;
					}
					tmpresult.add(tmp);
				}
			}
		}
		int[][] result = new int[tmpresult.size()][3];
		for (int i = 0; i < tmpresult.size(); i++) {
			for (int j = 0; j < 3; j++) {
				result[i][j] = tmpresult.get(i)[j];
			}
		}
		return result;
	}

	public WB_FaceListMesh getAlphaShapeMesh(final double filter) {
		final List<CTriangle> tris = af.getAlphaShape(filter);
		final int[][] result = new int[tris.size()][3];
		final Map<Integer, Integer> indexkeys = new FastMap<Integer, Integer>();
		final List<WB_Point> points = new FastList<WB_Point>();
		for (int i = 0; i < tris.size(); i++) {
			final CTriangle tri = tris.get(i);
			for (int j = 0; j < 3; j++) {
				final WB_Point point = convert(tri.getPoint(j));
				final int index = tree.getNearestNeighbor(point).value;
				final Integer id = indexkeys.get(index);
				if (id == null) {
					indexkeys.put(index, points.size());
					result[i][j] = points.size();
					points.add(point);
				} else {
					result[i][j] = id;
				}
			}
		}
		return WB_GeometryFactory.instance().createMesh(points, result);
	}

	private static WB_Point convert(final CVertex v) {
		return new WB_Point(v.x(), v.y(), v.z());
	}

	private static WB_Point convert(final Point v) {
		return new WB_Point(v.x(), v.y(), v.z());
	}
}
