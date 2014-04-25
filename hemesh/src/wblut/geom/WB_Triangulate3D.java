package wblut.geom;

import java.util.ArrayList;
import java.util.List;

import wblut.external.ProGAL.CTetrahedron;
import wblut.external.ProGAL.CVertex;
import wblut.external.ProGAL.DelaunayComplex;
import wblut.external.ProGAL.Point;

public class WB_Triangulate3D {

	public static int[] getTriangulation(final WB_Point[] points) {
		WB_Predicates predicates = new WB_Predicates();
		int n = points.length;
		List<Point> tmppoints = new ArrayList<Point>(n);
		WB_KDTree<WB_Point, Integer> tree = new WB_KDTree<WB_Point, Integer>();
		for (int i = 0; i < n; i++) {
			tmppoints.add(new Point(points[i].x, points[i].y, points[i].z));
			tree.add(points[i], i);
		}
		DelaunayComplex dc = new DelaunayComplex(tmppoints);
		List<CTetrahedron> tetras = dc.getTetrahedra();
		List<CVertex> vertices = dc.getVertices();

		int nt = tetras.size();
		List<int[]> tmpresult = new ArrayList<int[]>();
		for (int i = 0; i < nt; i++) {
			int[] tmp = new int[4];
			CTetrahedron tetra = tetras.get(i);
			int index = tree.getNearestNeighbor(convert(tetra.getPoint(0))).value;
			tmp[0] = index;
			index = tree.getNearestNeighbor(convert(tetra.getPoint(1))).value;
			tmp[1] = index;
			index = tree.getNearestNeighbor(convert(tetra.getPoint(2))).value;
			tmp[2] = index;
			index = tree.getNearestNeighbor(convert(tetra.getPoint(3))).value;
			tmp[3] = index;
			double o = predicates.orientTetra(points[tmp[0]].coords(),
					points[tmp[1]].coords(), points[tmp[2]].coords(),
					points[tmp[3]].coords());
			if (o != 0) {
				tmpresult.add(tmp);
			}
		}
		int[] result = new int[4 * tmpresult.size()];
		for (int i = 0; i < tmpresult.size(); i++) {
			for (int j = 0; j < 4; j++) {
				result[4 * i + j] = tmpresult.get(i)[j];
			}
		}
		return result;
	}

	public static int[] getTriangulation(final List<? extends WB_Point> points) {
		WB_Predicates predicates = new WB_Predicates();
		int n = points.size();
		List<Point> tmppoints = new ArrayList<Point>(n);
		WB_KDTree<WB_Point, Integer> tree = new WB_KDTree<WB_Point, Integer>();
		int i = 0;
		for (WB_Point p : points) {
			tmppoints.add(new Point(p.x, p.y, p.z));
			tree.add(p, i);
			i++;
		}
		DelaunayComplex dc = new DelaunayComplex(tmppoints);
		List<CTetrahedron> tetras = dc.getTetrahedra();
		List<CVertex> vertices = dc.getVertices();

		int nt = tetras.size();
		List<int[]> tmpresult = new ArrayList<int[]>();
		for (i = 0; i < nt; i++) {
			int[] tmp = new int[4];
			CTetrahedron tetra = tetras.get(i);
			int index = tree.getNearestNeighbor(convert(tetra.getPoint(0))).value;
			tmp[0] = index;
			index = tree.getNearestNeighbor(convert(tetra.getPoint(1))).value;
			tmp[1] = index;
			index = tree.getNearestNeighbor(convert(tetra.getPoint(2))).value;
			tmp[2] = index;
			index = tree.getNearestNeighbor(convert(tetra.getPoint(3))).value;
			tmp[3] = index;
			double o = predicates.orientTetra(points.get(tmp[0]).coords(),
					points.get(tmp[1]).coords(), points.get(tmp[2]).coords(),
					points.get(tmp[3]).coords());
			if (o != 0) {
				tmpresult.add(tmp);
			}
		}
		int[] result = new int[4 * tmpresult.size()];
		for (i = 0; i < tmpresult.size(); i++) {
			for (int j = 0; j < 4; j++) {
				result[4 * i + j] = tmpresult.get(i)[j];
			}
		}
		return result;
	}

	private static WB_Point convert(final CVertex v) {
		return new WB_Point(v.x(), v.y(), v.z());
	}

}
