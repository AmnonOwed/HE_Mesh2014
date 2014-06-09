package wblut.geom;

import java.util.List;

import javolution.util.FastTable;
import wblut.geom.interfaces.Segment;
import wblut.math.WB_Math;

public class WB_IndexedSegment extends WB_Linear implements Segment {

	private int i1;

	private int i2;

	private double length;

	private WB_Point[] points;

	public WB_IndexedSegment(final int i1, final int i2, final WB_Point[] points) {
		super(points[i1], new WB_Vector(points[i1], points[i2]));
		this.i1 = i1;
		this.i2 = i2;
		this.points = points;
		length = WB_Distance.getDistance3D(points[i1], points[i2]);
	}

	public WB_Point getParametricPointOnSegment(final double t) {
		final WB_Point result = new WB_Point(direction);
		result._scaleSelf(WB_Math.clamp(t, 0, 1) * length);
		result.moveBy(points[i1]);
		return result;
	}

	public void getParametricPointOnSegmentInto(final double t,
			final WB_MutableCoordinate result) {
		result._set(direction.mul(WB_Math.clamp(t, 0, 1) * length)._addSelf(
				points[i1]));

	}

	public WB_Point getCenter() {
		return WB_Point.interpolate(points[i1], points[i2], 0.5);
	}

	public WB_Point getEndpoint() {
		return points[i2];
	}

	@Override
	public WB_Point getOrigin() {
		return points[i1];
	}

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

	public static List<WB_IndexedSegment> negate(
			final List<WB_IndexedSegment> segs) {
		final List<WB_IndexedSegment> neg = new FastTable<WB_IndexedSegment>();
		for (int i = 0; i < segs.size(); i++) {
			neg.add(segs.get(i).negate());
		}

		return neg;

	}

	public WB_IndexedSegment negate() {
		return new WB_IndexedSegment(i2, i1, points);

	}

	public void reverse() {
		direction._mulSelf(-1);
		origin = points[i2];
		int tmp = i2;
		i2 = i1;
		i1 = tmp;
	}

	@Override
	public WB_Point getPoint(int i) {
		if (i == 0)
			return getOrigin();
		if (i == 1)
			return getEndpoint();
		return null;
	}

	@Override
	public int getDimension() {
		return 1;
	}

	@Override
	public WB_GeometryType getType() {

		return WB_GeometryType.SEGMENT;
	}

	@Override
	public int getEmbeddingDimension() {
		return 1;
	}

	@Override
	public WB_Geometry apply(WB_Transform T) {
		return geometryfactory.createSegment(getOrigin().applyAsPoint(T),
				getEndpoint().applyAsPoint(T));
	}

}
