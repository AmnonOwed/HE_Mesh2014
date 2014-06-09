package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javolution.util.FastTable;

public class WB_Polygon extends WB_Ring {

	int[][] triangles;
	int numberOfHoles;
	int[] nph;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_Polygon(final Collection<? extends WB_Coordinate> points) {
		n = points.size();
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		numberOfHoles = 0;
		nph = new int[0];
	}

	protected WB_Polygon(final WB_Coordinate... points) {
		n = points.length;
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		numberOfHoles = 0;
		nph = new int[0];
	}

	protected WB_Polygon(final WB_CoordinateSequence points) {
		n = points.size();
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		numberOfHoles = 0;
		nph = new int[0];
	}

	protected WB_Polygon(final Collection<? extends WB_Coordinate> points,
			final Collection<? extends WB_Coordinate> innerpoints) {

		n = points.size();
		final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
		tmp.addAll(points);
		tmp.addAll(innerpoints);
		this.points = geometryfactory.createPointSequence(tmp);
		getDirections();
		numberOfHoles = 1;
		nph = new int[1];
		nph[0] = innerpoints.size();
	}

	protected WB_Polygon(final WB_Coordinate[] points,
			final WB_Coordinate[] innerpoints) {

		n = points.length;
		final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
		for (final WB_Coordinate p : points) {
			tmp.add(p);
		}
		for (final WB_Coordinate p : innerpoints) {
			tmp.add(p);

		}
		this.points = geometryfactory.createPointSequence(tmp);
		getDirections();
		numberOfHoles = 1;
		nph = new int[1];
		nph[0] = innerpoints.length;
	}

	protected WB_Polygon(final Collection<? extends WB_Coordinate> points,
			final List<? extends WB_Coordinate>[] innerpoints) {

		n = points.size();
		final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
		for (final WB_Coordinate p : points) {
			tmp.add(p);
		}

		numberOfHoles = innerpoints.length;
		nph = new int[innerpoints.length];
		int i = 0;
		for (final List<? extends WB_Coordinate> innerpoint : innerpoints) {
			for (final WB_Coordinate p : innerpoint) {
				tmp.add(p);
			}
			nph[i++] = innerpoint.size();
		}

		this.points = geometryfactory.createPointSequence(tmp);
		getDirections();
	}

	protected WB_Polygon(final WB_Coordinate[] points,
			final WB_Coordinate[][] innerpoints) {

		n = points.length;
		final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
		for (final WB_Coordinate p : points) {
			tmp.add(p);
		}

		numberOfHoles = innerpoints.length;
		nph = new int[innerpoints.length];
		int i = 0;
		for (final WB_Coordinate[] innerpoint : innerpoints) {
			for (final WB_Coordinate p : innerpoint) {
				tmp.add(p);
			}
			nph[i++] = innerpoint.length;
		}

		this.points = geometryfactory.createPointSequence(tmp);
		getDirections();

	}

	private void getDirections() {
		final List<WB_Vector> dirs = new ArrayList<WB_Vector>(n);
		incLengths = new double[n];
		for (int i = 0; i < n; i++) {
			final int in = (i + 1) % n;
			final WB_Vector v = geometryfactory.createVector(points.get(in, 0)
					- points.get(i, 0), points.get(in, 1) - points.get(i, 1),
					points.get(in, 2) - points.get(i, 2));
			incLengths[i] = (i == 0) ? v.getLength() : incLengths[i - 1]
					+ v.getLength();
			v._normalizeSelf();
			dirs.add(v);
		}
		directions = geometryfactory.createVectorSequence(dirs);
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_Polygon)) {
			return false;
		}
		final WB_Polygon L = (WB_Polygon) o;
		if (getNumberOfPoints() != L.getNumberOfPoints()) {
			return false;
		}
		for (int i = 0; i < n; i++) {
			if (!getPoint(i).equals(L.getPoint(i))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public WB_GeometryType getType() {

		return WB_GeometryType.POLYGON;
	}

	@Override
	public int getDimension() {
		return 2;
	}

	@Override
	public int getEmbeddingDimension() {
		return 2;
	}

	@Override
	public int size() {
		return points.size();
	}

	public int getNumberOfHoles() {
		return numberOfHoles;

	}

	public int[] getNumberOfPointsPerHole() {

		return nph;
	}

	public WB_Triangulation2DWithPoints triangulate() {

		final WB_KDTree<WB_Point, Integer> pointmap = new WB_KDTree<WB_Point, Integer>(
				points.size());
		final List<WB_Point> pts = new FastTable<WB_Point>();
		for (int i = 0; i < n; i++) {
			pts.add(points.getCoordinate(i));
		}
		int index = n;
		final List[] hpts = new FastTable[numberOfHoles];
		for (int i = 0; i < numberOfHoles; i++) {
			hpts[i] = new FastTable<WB_Point>();
			for (int j = 0; j < nph[i]; j++) {
				hpts[i].add(points.getCoordinate(index++));
			}
		}
		triangles = null;
		return WB_Triangulate.getPolygonTriangulation2D(pts, hpts, true,
				geometryfactory.createEmbeddedPlane(getPlane(0)));

	}

	public int[][] getTriangles() {
		if (triangles == null) {
			WB_Triangulation2DWithPoints triangulation = triangulate();
			final WB_KDTree<WB_Point, Integer> pointmap = new WB_KDTree<WB_Point, Integer>(
					points.size());

			for (int i = 0; i < points.size(); i++) {
				pointmap.add(points.getCoordinate(i), i);
			}

			triangles = triangulation.getTriangles();

			final List<WB_Point> tripoints = triangulation.getPoints();
			final int[] intmap = new int[tripoints.size()];
			int index = 0;
			for (final WB_Point point : tripoints) {
				final int found = pointmap.getNearestNeighbor(point).value;
				intmap[index++] = found;
			}
			for (final int[] T : triangles) {
				T[0] = intmap[T[0]];
				T[1] = intmap[T[1]];
				T[2] = intmap[T[2]];
			}

		}

		return triangles;
	}

	public WB_Plane getPlane(final double d) {
		final WB_Vector normal = geometryfactory.createVector();
		for (int i = 0, j = getNumberOfPoints() - 1; i < getNumberOfPoints(); j = i, i++) {

			normal._addSelf(
					(points.get(j, 1) - points.get(i, 1))
							* (points.get(j, 2) + points.get(i, 2)),
					(points.get(j, 2) - points.get(i, 2))
							* (points.get(j, 0) + points.get(i, 0)),
					(points.get(j, 0) - points.get(i, 0))
							* (points.get(j, 1) + points.get(i, 1)));

		}
		normal._normalizeSelf();
		return geometryfactory.createPlane(
				points.getCoordinate(0).addMul(d, normal), normal);

	}

	public WB_Vector getNormal() {
		final WB_Vector normal = geometryfactory.createVector();
		for (int i = 0, j = getNumberOfPoints() - 1; i < getNumberOfPoints(); j = i, i++) {

			normal._addSelf(
					(points.get(j, 1) - points.get(i, 1))
							* (points.get(j, 2) + points.get(i, 2)),
					(points.get(j, 2) - points.get(i, 2))
							* (points.get(j, 0) + points.get(i, 0)),
					(points.get(j, 0) - points.get(i, 0))
							* (points.get(j, 1) + points.get(i, 1)));

		}
		normal._normalizeSelf();

		return normal;

	}

	public WB_Point getPoint(final int i) {

		return points.getCoordinate(i);
	}

	public double getd(final int i, final int j) {

		return points.get(i, j);
	}

	public float getf(final int i, final int j) {

		return (float) points.get(i, j);
	}

}