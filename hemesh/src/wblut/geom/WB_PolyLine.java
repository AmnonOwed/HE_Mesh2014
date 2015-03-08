/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class WB_PolyLine implements WB_Geometry {
    
    /**
     * 
     */
    WB_CoordinateSequence points;
    
    /**
     * 
     */
    WB_CoordinateSequence directions;
    
    /**
     * 
     */
    double[] incLengths;
    
    /**
     * 
     */
    int numberOfPoints;
    
    /**
     * 
     */
    int hashcode;
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public WB_SequencePoint getPoint(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter " + i
		    + " must between 0 and " + (numberOfPoints - 1) + ".");
	}
	return points.getPoint(i);
    }

    /**
     * 
     *
     * @param i 
     * @param j 
     * @return 
     */
    public double getd(final int i, final int j) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter " + i
		    + " must between 0 and " + (numberOfPoints - 1) + ".");
	}
	return points.get(i, j);
    }

    /**
     * 
     *
     * @param i 
     * @param j 
     * @return 
     */
    public float getf(final int i, final int j) {
	if ((i < 0) || (i > (numberOfPoints - 1))) {
	    throw new IllegalArgumentException("Parameter " + i
		    + " must between 0 and " + (numberOfPoints - 1) + ".");
	}
	return (float) points.get(i, j);
    }

    /**
     * 
     *
     * @param t 
     * @return 
     */
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

    /**
     * 
     *
     * @param t 
     * @return 
     */
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

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public WB_SequenceVector getDirection(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	return directions.getVector(i);
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public WB_Vector getNormal(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	WB_Vector normal = geometryfactory.createVector(0, 0, 1);
	normal = normal.cross(directions.getVector(i));
	final double d = normal.getLength3D();
	normal = normal.div(d);
	if (WB_Epsilon.isZero(d)) {
	    normal = geometryfactory.createVector(1, 0, 0);
	}
	return normal;
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double a(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	return -directions.get(i, 1);
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double b(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	return directions.get(i, 0);
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double c(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	return (points.get(i, 0) * directions.get(i, 1))
		- (points.get(i, 1) * directions.get(i, 0));
    }

    /**
     * 
     *
     * @return 
     */
    public int getNumberOfPoints() {
	return numberOfPoints;
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public WB_Segment getSegment(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	return geometryfactory.createSegment(getPoint(i), getPoint(i + 1));
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double getLength(final int i) {
	if ((i < 0) || (i > (numberOfPoints - 2))) {
	    throw new IllegalArgumentException("Parameter must between 0 and "
		    + (numberOfPoints - 2) + ".");
	}
	return incLengths[i + 1] - incLengths[i];
    }

    /**
     * 
     */
    protected WB_PolyLine() {
    }

    /**
     * 
     *
     * @param points 
     */
    protected WB_PolyLine(final Collection<? extends WB_Coordinate> points) {
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
    protected WB_PolyLine(final WB_Coordinate[] points) {
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
    protected WB_PolyLine(final WB_CoordinateSequence points) {
	numberOfPoints = points.size();
	this.points = geometryfactory.createPointSequence(points);
	getDirections();
	hashcode = -1;
    }

    /**
     * 
     */
    private void getDirections() {
	final List<WB_Vector> dirs = new ArrayList<WB_Vector>(points.size() - 1);
	incLengths = new double[points.size() - 1];
	for (int i = 0; i < (points.size() - 1); i++) {
	    final WB_Vector v = geometryfactory.createVector(
		    points.get(i + 1, 0) - points.get(i, 0),
		    points.get(i + 1, 1) - points.get(i, 1),
		    points.get(i + 1, 2) - points.get(i, 2));
	    incLengths[i] = (i == 0) ? v.getLength3D() : incLengths[i - 1]
		    + v.getLength3D();
	    v.normalizeSelf();
	    dirs.add(v);
	}
	directions = geometryfactory.createVectorSequence(dirs);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
	for (int i = 0; i < numberOfPoints; i++) {
	    if (!getPoint(i).equals(L.getPoint(i))) {
		return false;
	    }
	}
	return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
	if (hashcode == -1) {
	    hashcode = points.getPoint(0).hashCode();
	    for (int i = 1; i < points.size(); i++) {
		hashcode = (31 * hashcode) + points.getPoint(i).hashCode();
	    }
	}
	return hashcode;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.POLYLINE;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_PolyLine apply(final WB_Transform T) {
	return geometryfactory.createPolyLine(points.applyAsPoint(T));
    }
}