/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Math;

/**
 * 
 */
public class WB_Ray extends WB_Linear implements WB_Curve {
    
    /**
     * 
     *
     * @return 
     */
    public static WB_Ray X() {
	return new WB_Ray(0, 0, 0, 1, 0, 0);
    }

    /**
     * 
     *
     * @return 
     */
    public static WB_Ray Y() {
	return new WB_Ray(0, 0, 0, 0, 1, 0);
    }

    /**
     * 
     *
     * @return 
     */
    public static WB_Ray Z() {
	return new WB_Ray(0, 0, 0, 0, 0, 1);
    }

    /**
     * 
     */
    public WB_Ray() {
	origin = new WB_Point();
	direction = new WB_Vector(1, 0, 0);
    }

    /**
     * 
     *
     * @param o 
     * @param d 
     */
    public WB_Ray(final WB_Coordinate o, final WB_Coordinate d) {
	origin = new WB_Point(o);
	direction = new WB_Vector(d);
	direction.normalizeSelf();
    }

    /**
     * 
     *
     * @param ox 
     * @param oy 
     * @param oz 
     * @param dx 
     * @param dy 
     * @param dz 
     */
    public WB_Ray(final double ox, final double oy, final double oz,
	    final double dx, final double dy, final double dz) {
	origin = new WB_Point(ox, oy, oz);
	direction = new WB_Vector(dx, dy, dz);
	direction.normalizeSelf();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	return "Ray: " + origin.toString() + " " + direction.toString();
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Linear#set(wblut.geom.WB_Coordinate, wblut.geom.WB_Coordinate)
     */
    @Override
    public void set(final WB_Coordinate o, final WB_Coordinate d) {
	origin = new WB_Point(o);
	direction = new WB_Vector(d);
	direction.normalizeSelf();
    }

    /**
     * 
     *
     * @param p1 
     * @param p2 
     */
    public void setFromPoints(final WB_Coordinate p1, final WB_Coordinate p2) {
	origin = new WB_Point(p1);
	direction = new WB_Vector(p1, p2);
	direction.normalizeSelf();
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Linear#getPointOnLine(double)
     */
    @Override
    public WB_Point getPointOnLine(final double t) {
	final WB_Point result = new WB_Point(direction);
	result.scaleSelf(WB_Math.max(0, t));
	result.addSelf(origin);
	return result;
    }

    /**
     * 
     *
     * @param t 
     * @param p 
     */
    public void getPointOnLineInto(final double t, final WB_Point p) {
	p.set(direction);
	if (t > 0) {
	    p.scaleSelf(t);
	}
	p.addSelf(origin);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Linear#getOrigin()
     */
    @Override
    public WB_Point getOrigin() {
	return origin;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Linear#getDirection()
     */
    @Override
    public WB_Vector getDirection() {
	return direction;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Curve#curvePoint(double)
     */
    @Override
    public WB_Point curvePoint(final double u) {
	return this.getPointOnLine(u);
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
	return Double.POSITIVE_INFINITY;
    }
}