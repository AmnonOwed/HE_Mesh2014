package wblut.geom;

import java.util.List;

import javolution.util.FastTable;
import wblut.geom.interfaces.Segment;
import wblut.math.WB_Math;

public class WB_Segment extends WB_Linear implements Segment {

	protected double length;
	private WB_Point endpoint;

	public WB_Segment() {
		super();
		endpoint = new WB_Point();
		length = 0;

	}

	public WB_Segment(final WB_Coordinate o, final WB_Coordinate d,
			final double l) {
		super(o, d);
		length = l;
		endpoint = new WB_Point(direction);
		endpoint._mulSelf(l)._addSelf(origin);
	}

	public WB_Segment(final WB_Coordinate p1, final WB_Coordinate p2) {
		super(p1, new WB_Vector(p1, p2));
		endpoint = new WB_Point(p2);
		length = Math.sqrt(WB_Distance.getSqDistance3D(p1, p2));
	}

	public WB_Segment(final double p1x, final double p1y, final double p1z,
			final double p2x, final double p2y, final double p2z) {
		super(new WB_Point(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y,
				p2z - p1z));
		endpoint = new WB_Point(p2x, p2y, p2z);
		length = Math.sqrt(WB_Distance.getSqDistance3D(origin, endpoint));

	}

	@Override
	public WB_Point getParametricPointOnSegment(final double t) {
		final WB_Point result = new WB_Point(direction);
		result._scaleSelf(WB_Math.clamp(t, 0, 1) * length);
		result.moveBy(origin);
		return result;
	}

	@Override
	public void getParametricPointOnSegmentInto(final double t,
			final WB_MutableCoordinate result) {
		result._set(direction.mul(WB_Math.clamp(t, 0, 1) * length)._addSelf(
				origin));

	}

	public WB_Point getEndpoint() {
		return endpoint;
	}

	public WB_Point getCenter() {
		return endpoint.add(origin)._mulSelf(0.5);
	}

	public double getLength() {
		return length;
	}

	public static List<WB_Segment> negate(final List<WB_Segment> segs) {
		final List<WB_Segment> neg = new FastTable<WB_Segment>();
		for (int i = 0; i < segs.size(); i++) {
			neg.add(segs.get(i).negate());
		}

		return neg;

	}

	public WB_Segment negate() {
		return new WB_Segment(endpoint, origin);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.WB_Segment2D#reverse()
	 */
	public void reverse() {
		set(endpoint, origin);
	}

	@Override
	public WB_Point getPoint(int i) {
		if (i == 0)
			return origin;
		if (i == 1)
			return endpoint;
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
		return geometryfactory.createSegment(origin.applyAsPoint(T),
				endpoint.applyAsPoint(T));
	}

}