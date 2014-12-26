package wblut.geom;

import wblut.math.WB_Math;

public class WB_IndexedSegment extends WB_Segment {
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    private int i1;
    private int i2;
    private final double length;
    private final WB_Point[] points;

    public WB_IndexedSegment(final int i1, final int i2, final WB_Point[] points) {
	super(points[i1], new WB_Vector(points[i1], points[i2]));
	this.i1 = i1;
	this.i2 = i2;
	this.points = points;
	length = WB_Distance.getDistance3D(points[i1], points[i2]);
    }

    @Override
    public WB_Point getParametricPointOnSegment(final double t) {
	final WB_Point result = new WB_Point(direction);
	result.scaleSelf(WB_Math.clamp(t, 0, 1) * length);
	result.addSelf(points[i1]);
	return result;
    }

    @Override
    public void getParametricPointOnSegmentInto(final double t,
	    final WB_MutableCoordinate result) {
	result.set(direction.mul(WB_Math.clamp(t, 0, 1) * length).addSelf(
		points[i1]));
    }

    @Override
    public WB_Point getCenter() {
	return gf.createMidpoint(points[i1], points[i2]);
    }

    @Override
    public WB_Point getEndpoint() {
	return points[i2];
    }

    @Override
    public WB_Point getOrigin() {
	return points[i1];
    }

    @Override
    public double getLength() {
	return length;
    }

    public int i1() {
	return i1;
    }

    public int i2() {
	return i2;
    }

    public WB_Point[] points() {
	return points;
    }

    @Override
    public WB_IndexedSegment negate() {
	return new WB_IndexedSegment(i2, i1, points);
    }

    @Override
    public void reverse() {
	direction.mulSelf(-1);
	origin = points[i2];
	final int tmp = i2;
	i2 = i1;
	i1 = tmp;
    }

    @Override
    public WB_Point getPoint(final int i) {
	if (i == 0) {
	    return getOrigin();
	}
	if (i == 1) {
	    return getEndpoint();
	}
	return null;
    }

    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.SEGMENT;
    }

    @Override
    public WB_Geometry apply(final WB_Transform T) {
	return geometryfactory.createSegment(getOrigin().applyAsPoint(T),
		getEndpoint().applyAsPoint(T));
    }
}
