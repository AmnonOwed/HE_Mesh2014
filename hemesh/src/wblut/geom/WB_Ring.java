package wblut.geom;

import java.util.ArrayList;
import java.util.List;

import wblut.math.WB_Epsilon;

public class WB_Ring extends WB_PolyLine {

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	/**
	 * Get direction.
	 * 
	 * @return direction
	 */
	@Override
	public WB_IndexedVector getDirection(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return directions.getVector(i);
	}

	/**
	 * Get a normal to the line.
	 * 
	 * @return a normal
	 */
	@Override
	public WB_Vector getNormal(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		WB_Vector n = geometryfactory.createVector(0, 0, 1);
		n = n.cross(directions.getVector(i));
		final double d = n.getLength3D();
		n = n.div(d);
		if (WB_Epsilon.isZero(d)) {
			n = geometryfactory.createVector(1, 0, 0);
		}
		return n;
	}

	/**
	 * a.x+b.y+c=0
	 * 
	 * @return a for a 2D line
	 */
	@Override
	public double a(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return -directions.get(i, 1);
	}

	/**
	 * a.x+b.y+c=0
	 * 
	 * @return b for a 2D line
	 */
	@Override
	public double b(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return directions.get(i, 0);
	}

	/**
	 * a.x+b.y+c=0
	 * 
	 * @return c for a 2D line
	 */
	@Override
	public double c(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return points.get(i, 0) * directions.get(i, 1) - points.get(i, 1)
				* directions.get(i, 0);
	}

	@Override
	public double getLength(final int i) {

		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return incLengths[i] - ((i == 0) ? 0 : incLengths[i - 1]);
	}

	protected WB_Ring() {
	};

	protected WB_Ring(final List<? extends WB_Coordinate> points) {
		n = points.size();
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		hc = -1;

	}

	protected WB_Ring(final WB_Coordinate[] points) {
		n = points.length;
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		hc = -1;

	}

	protected WB_Ring(final WB_CoordinateSequence points) {
		n = points.size();
		this.points = geometryfactory.createPointSequence(points);
		getDirections();
		hc = -1;

	}

	private void getDirections() {
		final List<WB_Vector> dirs = new ArrayList<WB_Vector>(n);
		incLengths = new double[n];
		for (int i = 0; i < n; i++) {
			final int in = (i + 1) % n;
			final WB_Vector v = geometryfactory.createVector(points.get(in, 0)
					- points.get(i, 0), points.get(in, 1) - points.get(i, 1),
					points.get(in, 2) - points.get(i, 2));
			incLengths[i] = (i == 0) ? v.getLength3D() : incLengths[i - 1]
					+ v.getLength3D();
			v.normalizeSelf();
			dirs.add(v);
		}
		directions = geometryfactory.createVectorSequence(dirs);

	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof WB_Ring)) {
			return false;
		}
		final WB_Ring L = (WB_Ring) o;
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

		return WB_GeometryType.RING;
	}

	@Override
	public int getDimension() {
		return 1;
	}

	@Override
	public int getEmbeddingDimension() {
		return 2;
	}

	public boolean isCCW() {

		final int nPts = size();
		// sanity check
		if (nPts < 3) {
			throw new IllegalArgumentException(
					"Ring has fewer than 3 points, so orientation cannot be determined");
		}

		// find highest point
		WB_IndexedPoint hiPt = getPoint(0);
		int hiIndex = 0;
		for (int i = 1; i <= nPts; i++) {
			final WB_IndexedPoint p = getPoint(i);
			if (p.yd() > hiPt.yd()) {
				hiPt = p;
				hiIndex = i;
			}
		}

		// find distinct point before highest point
		int iPrev = hiIndex;
		do {
			iPrev = iPrev - 1;
			if (iPrev < 0) {
				iPrev = nPts;
			}
		} while (getPoint(iPrev).equals(hiPt) && iPrev != hiIndex);

		// find distinct point after highest point
		int iNext = hiIndex;
		do {
			iNext = (iNext + 1) % nPts;
		} while (getPoint(iNext).equals(hiPt) && iNext != hiIndex);

		final WB_IndexedPoint prev = getPoint(iPrev);
		final WB_IndexedPoint next = getPoint(iNext);

		/**
		 * This check catches cases where the ring contains an A-B-A
		 * configuration of points. This can happen if the ring does not contain
		 * 3 distinct points (including the case where the input array has fewer
		 * than 4 elements), or it contains coincident line segments.
		 */
		if (prev.equals(hiPt) || next.equals(hiPt) || prev.equals(next)) {
			return false;
		}
		final WB_Predicates pred = new WB_Predicates();
		// System.out.println(prev + " " + hiPt + " " + next);
		final int disc = (int) Math.signum(pred.orientTri(prev, hiPt, next));

		/**
		 * If disc is exactly 0, lines are collinear. There are two possible
		 * cases: (1) the lines lie along the x axis in opposite directions (2)
		 * the lines lie on top of one another
		 * 
		 * (1) is handled by checking if next is left of prev ==> CCW (2) will
		 * never happen if the ring is valid, so don't check for it (Might want
		 * to assert this)
		 */
		boolean isCCW = false;
		if (disc == 0) {
			// poly is CCW if prev x is right of next x
			isCCW = (prev.xd() > next.xd());
		} else {
			// if area is positive, points are ordered CCW
			isCCW = (disc > 0);
		}
		return isCCW;
	}

	@Override
	public WB_Ring apply(final WB_Transform T) {
		return geometryfactory.createRing(points.applyAsPoint(T));

	}

	public WB_IndexedPoint getPoint(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter " + i
					+ " must between 0 and " + (n - 1) + ".");
		}
		return points.getPoint(i);
	}

	public double getd(final int i, final int j) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter " + i
					+ " must between 0 and " + (n - 1) + ".");
		}
		return points.get(i, j);
	}

	public float getf(final int i, final int j) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter " + i
					+ " must between 0 and " + (n - 1) + ".");
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
			return new WB_Point(points.getPoint(0));
		}
		int index = 0;
		while (t > incLengths[index]) {
			index++;
		}

		final double x = t - incLengths[index];

		return points.getPoint(index).addMul(x, directions.getVector(index));
	}

	public WB_Point getParametricPointOnLine(final double t) {
		if ((t < 0) || (t > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		final double ft = t - (int) t;
		if (ft == 0.0) {
			return new WB_Point(points.getPoint((int) t));
		}

		return points.getPoint((int) t).mulAddMul(1 - ft, ft,
				points.getPoint(1 + (int) t));
	}

	public int getNumberOfPoints() {
		return n;
	}

	public WB_Segment getSegment(final int i) {
		if ((i < 0) || (i > n - 1)) {
			throw new IllegalArgumentException("Parameter must between 0 and "
					+ (n - 1) + ".");
		}
		return geometryfactory
				.createSegment(getPoint(i), getPoint((i + 1) % n));
	}

}