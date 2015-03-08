/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.List;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class WB_Ring extends WB_PolyLine {
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * Get direction.
     *
     * @param i 
     * @return direction
     */
    @Override
    public WB_SequenceVector getDirection(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	return directions.getVector(i);
    }

    /**
     * Get a normal to the line.
     *
     * @param i 
     * @return a normal
     */
    @Override
    public WB_Vector getNormal(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
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
     * @param i 
     * @return a for a 2D line
     */
    @Override
    public double a(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	return -directions.get(i, 1);
    }

    /**
     * a.x+b.y+c=0
     *
     * @param i 
     * @return b for a 2D line
     */
    @Override
    public double b(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	return directions.get(i, 0);
    }

    /**
     * a.x+b.y+c=0
     *
     * @param i 
     * @return c for a 2D line
     */
    @Override
    public double c(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	return (points.get(i, 0) * directions.get(i, 1))
		- (points.get(i, 1) * directions.get(i, 0));
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getLength(int)
     */
    @Override
    public double getLength(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	return incLengths[i] - ((i == 0) ? 0 : incLengths[i - 1]);
    }

    /**
     * 
     */
    protected WB_Ring() {
    };

    /**
     * 
     *
     * @param points 
     */
    protected WB_Ring(final List<? extends WB_Coordinate> points) {
	numberOfPoints = points.size();
	this.points = geometryfactory.createPointSequence(points);
	getDirections();
	hashcode = -1;
    }

    /**
     * 
     *
     * @param points 
     */
    protected WB_Ring(final WB_Coordinate[] points) {
	numberOfPoints = points.length;
	this.points = geometryfactory.createPointSequence(points);
	getDirections();
	hashcode = -1;
    }

    /**
     * 
     *
     * @param points 
     */
    protected WB_Ring(final WB_CoordinateSequence points) {
	numberOfPoints = points.size();
	this.points = geometryfactory.createPointSequence(points);
	getDirections();
	hashcode = -1;
    }

    /**
     * 
     */
    private void getDirections() {
	final List<WB_Vector> dirs = new ArrayList<WB_Vector>(numberOfPoints);
	incLengths = new double[numberOfPoints];
	for (int i = 0; i < numberOfPoints; i++) {
	    final int in = (i + 1) % numberOfPoints;
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#equals(java.lang.Object)
     */
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
	for (int i = 0; i < numberOfPoints; i++) {
	    if (!getPoint(i).equals(L.getPoint(i))) {
		return false;
	    }
	}
	return true;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.RING;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean isCCW() {
	final int nPts = getNumberOfPoints();
	// sanity check
	if (nPts < 3) {
	    throw new IllegalArgumentException(
		    "Ring has fewer than 3 points, so orientation cannot be determined");
	}
	// find highest point
	WB_SequencePoint hiPt = getPoint(0);
	int hiIndex = 0;
	for (int i = 1; i <= nPts; i++) {
	    final WB_SequencePoint p = getPoint(i);
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
	} while (getPoint(iPrev).equals(hiPt) && (iPrev != hiIndex));
	// find distinct point after highest point
	int iNext = hiIndex;
	do {
	    iNext = (iNext + 1) % nPts;
	} while (getPoint(iNext).equals(hiPt) && (iNext != hiIndex));
	final WB_SequencePoint prev = getPoint(iPrev);
	final WB_SequencePoint next = getPoint(iNext);
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Ring apply(final WB_Transform T) {
	return geometryfactory.createRing(points.applyAsPoint(T));
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getPoint(int)
     */
    @Override
    public WB_SequencePoint getPoint(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter " + i
		    + " must between 0 and " + (numberOfPoints - 1) + ".");
	}
	return points.getPoint(i);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getd(int, int)
     */
    @Override
    public double getd(final int i, final int j) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter " + i
		    + " must between 0 and " + (numberOfPoints - 1) + ".");
	}
	return points.get(i, j);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getf(int, int)
     */
    @Override
    public float getf(final int i, final int j) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter " + i
		    + " must between 0 and " + (numberOfPoints - 1) + ".");
	}
	return (float) points.get(i, j);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getPointOnLine(double)
     */
    @Override
    public WB_Point getPointOnLine(final double t) {
	if ((t < 0) || (t > incLengths[numberOfPoints - 1])) {
	    throw new IllegalArgumentException(
		    "Parameter must between 0 and length of polyline"
			    + incLengths[numberOfPoints - 1] + " .");
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getParametricPointOnLine(double)
     */
    @Override
    public WB_Point getParametricPointOnLine(final double t) {
	if ((t < 0) || (t > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	final double ft = t - (int) t;
	if (ft == 0.0) {
	    return new WB_Point(points.getPoint((int) t));
	}
	return points.getPoint((int) t).mulAddMul(1 - ft, ft,
		points.getPoint(1 + (int) t));
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getNumberOfPoints()
     */
    @Override
    public int getNumberOfPoints() {
	return numberOfPoints;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_PolyLine#getSegment(int)
     */
    @Override
    public WB_Segment getSegment(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 1) + ".");
	}
	return geometryfactory.createSegment(getPoint(i), getPoint((i + 1)
		% numberOfPoints));
    }
}