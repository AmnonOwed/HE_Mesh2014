/*
 * 
 */
package wblut.geom;

import java.util.List;
import javolution.util.FastTable;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_Segment extends WB_Linear implements WB_Simplex, WB_Curve {
    
    /**
     * 
     */
    protected double length;
    
    /**
     * 
     */
    private final WB_Point endpoint;

    /**
     * 
     */
    public WB_Segment() {
	super();
	endpoint = new WB_Point();
	length = 0;
    }

    /**
     * 
     *
     * @param o 
     * @param d 
     * @param l 
     */
    public WB_Segment(final WB_Coordinate o, final WB_Coordinate d,
	    final double l) {
	super(o, d);
	length = l;
	endpoint = new WB_Point(direction);
	endpoint.mulSelf(l).addSelf(origin);
    }

    /**
     * 
     *
     * @param p1 
     * @param p2 
     */
    public WB_Segment(final WB_Coordinate p1, final WB_Coordinate p2) {
	super(p1, new WB_Vector(p1, p2));
	endpoint = new WB_Point(p2);
	length = Math.sqrt(WB_GeometryOp.getSqDistance3D(p1, p2));
    }

    /**
     * 
     *
     * @param p1x 
     * @param p1y 
     * @param p1z 
     * @param p2x 
     * @param p2y 
     * @param p2z 
     */
    public WB_Segment(final double p1x, final double p1y, final double p1z,
	    final double p2x, final double p2y, final double p2z) {
	super(new WB_Point(p1x, p1y, p1z), new WB_Vector(p2x - p1x, p2y - p1y,
		p2z - p1z));
	endpoint = new WB_Point(p2x, p2y, p2z);
	length = Math.sqrt(WB_GeometryOp.getSqDistance3D(origin, endpoint));
    }

    /**
     * 
     *
     * @param t 
     * @return 
     */
    public WB_Point getParametricPointOnSegment(final double t) {
	final WB_Point result = new WB_Point(direction);
	result.scaleSelf(WB_Math.clamp(t, 0, 1) * length);
	result.addSelf(origin);
	return result;
    }

    /**
     * 
     *
     * @param t 
     * @param result 
     */
    public void getParametricPointOnSegmentInto(final double t,
	    final WB_MutableCoordinate result) {
	result.set(direction.mul(WB_Math.clamp(t, 0, 1) * length).addSelf(
		origin));
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point getEndpoint() {
	return endpoint;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Simplex#getCenter()
     */
    @Override
    public WB_Point getCenter() {
	return endpoint.add(origin).mulSelf(0.5);
    }

    /**
     * 
     *
     * @return 
     */
    public double getLength() {
	return length;
    }

    /**
     * 
     *
     * @param segs 
     * @return 
     */
    public static List<WB_Segment> negate(final List<WB_Segment> segs) {
	final List<WB_Segment> neg = new FastTable<WB_Segment>();
	for (int i = 0; i < segs.size(); i++) {
	    neg.add(segs.get(i).negate());
	}
	return neg;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Segment negate() {
	return new WB_Segment(endpoint, origin);
    }

    /**
     * 
     */
    public void reverse() {
	set(endpoint, origin);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Simplex#getPoint(int)
     */
    @Override
    public WB_Point getPoint(final int i) {
	if (i == 0) {
	    return origin;
	}
	if (i == 1) {
	    return endpoint;
	}
	return null;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.SEGMENT;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Geometry apply(final WB_Transform T) {
	return geometryfactory.createSegment(origin.applyAsPoint(T),
		endpoint.applyAsPoint(T));
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Curve#curvePoint(double)
     */
    @Override
    public WB_Point curvePoint(final double u) {
	return this.getParametricPointOnSegment(u);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Curve#loweru()
     */
    @Override
    public double loweru() {
	return 0;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Curve#upperu()
     */
    @Override
    public double upperu() {
	return 1;
    }
}