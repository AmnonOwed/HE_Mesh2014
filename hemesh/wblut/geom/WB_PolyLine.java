package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wblut.WB_Epsilon;

public class WB_PolyLine implements WB_Geometry {

	WB_CoordinateSequence<WB_Point> points;

	WB_CoordinateSequence<WB_Vector> directions;

	double[] incLengths;

	int n;

	int hc;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_Point getPoint(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return points.getCoordinate(i);
	}

	public double getd(final int i, final int j) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return points.get(i, j);
	}

	public float getf(final int i, final int j) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return (float) points.get(i, j);
	}

	public WB_Point getPointOnLine(final double t) {
		if ((t < 0) || (t > incLengths[n - 1])) {
			throw new IllegalArgumentException(
					"Parameter must between 0 and length of polyline"
							+ incLengths[n - 1] + " .");
		}
		if (t == 0) {
			return points.getCoordinate(0);
		}
		int index = 0;
		while (t > incLengths[index]) {
			index++;
		}

		final double d = incLengths[index + 1] - incLengths[index];
		final double x = t - incLengths[index];

		return points.getCoordinate(index)._addMulSelf(x,
				directions.getCoordinate(index));
	}

	public WB_Point getParametricPointOnLine(final double t) {
		if ((t < 0) || (t > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		final double ft = t - (int) t;
		if (ft == 0.0) {
			return points.getCoordinate((int) t);
		}

		return points.getCoordinate((int) t)._mulAddMulSelf(1 - ft, ft,
				points.getCoordinate(1 + (int) t));
	}

	public WB_Vector getDirection(final int i) {
		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		return directions.getCoordinate(i);
	}

	public WB_Vector getNormal(final int i) {
		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		WB_Vector n = geometryfactory.createVector(0, 0, 1);
		n = n.cross(directions.getCoordinate(i));
		final double d = n.getLength();
		n = n.div(d);
		if (WB_Epsilon.isZero(d)) {
			n = geometryfactory.createVector(1, 0, 0);
		}
		return n;
	}

	public double a(final int i) {
		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		return -directions.get(i, 1);
	}

	public double b(final int i) {
		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		return directions.get(i, 0);
	}

	public double c(final int i) {
		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		return points.get(i, 0) * directions.get(i, 1) - points.get(i, 1)
				* directions.get(i, 0);
	}

	public int getNumberOfPoints() {
		return n;
	}

	public WB_Segment getSegment(final int i) {
		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		return geometryfactory.createSegment(getPoint(i), getPoint(i + 1));
	}

	public double getLength(final int i) {

		if ((i < 0) || (i > n - 2)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 2) + ".");
		}
		return incLengths[i + 1] - incLengths[i];
	}

	protected WB_PolyLine() {

	}

	protected WB_PolyLine(final Collection<? extends WB_Coordinate> points) {
		n = points.size();
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		hc = -1;
	}

	protected WB_PolyLine(final WB_Coordinate[] points) {
		n = points.length;
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		hc = -1;
	}

	protected WB_PolyLine(final WB_CoordinateSequence points) {
		n = points.size();
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		hc = -1;
	}

	private void getDirections() {

		final List<WB_Vector> dirs = new ArrayList<WB_Vector>(points.size() - 1);
		incLengths = new double[points.size() - 1];
		final WB_Point p;
		final WB_Point pn;
		for (int i = 0; i < points.size() - 1; i++) {

			final WB_Vector v = geometryfactory.createVector(
					points.get(i + 1, 0) - points.get(i, 0),
					points.get(i + 1, 1) - points.get(i, 1),
					points.get(i + 1, 2) - points.get(i, 2));
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
		if (!(o instanceof WB_PolyLine)) {
			return false;
		}
		final WB_PolyLine L = (WB_PolyLine) o;
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
	public int hashCode() {
		if (hc == -1) {
			hc = points.getCoordinate(0).hashCode();
			for (int i = 1; i < points.size(); i++) {
				hc = 31 * hc + points.getCoordinate(i).hashCode();

			}
		}
		return hc;

	}

	@Override
	public WB_GeometryType getType() {

		return WB_GeometryType.POLYLINE;
	}

	@Override
	public int getDimension() {
		return 1;
	}

	@Override
	public int getEmbeddingDimension() {
		return 2;
	}

	public int size() {
		return points.size();
	}

	@Override
	public WB_PolyLine apply(final WB_Transform T) {
		return geometryfactory.createPolyLine(points.applyAsPoint(T));
	}

}