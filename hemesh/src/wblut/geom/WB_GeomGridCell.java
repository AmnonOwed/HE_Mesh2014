/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;

/**
 * 
 */
public class WB_GeomGridCell {
    
    /**
     * 
     */
    protected int index;
    
    /**
     * 
     */
    protected WB_AABB aabb;
    
    /**
     * 
     */
    protected ArrayList<WB_Point> points;
    
    /**
     * 
     */
    protected ArrayList<WB_Segment> segments;

    /**
     * 
     *
     * @param index 
     * @param min 
     * @param max 
     */
    public WB_GeomGridCell(final int index, final WB_Coordinate min,
	    final WB_Coordinate max) {
	this.index = index;
	points = new ArrayList<WB_Point>();
	segments = new ArrayList<WB_Segment>();
	aabb = new WB_AABB(min, max);
    }

    /**
     * 
     *
     * @param p 
     */
    public void addPoint(final WB_Coordinate p) {
	points.add(new WB_Point(p));
    }

    /**
     * 
     *
     * @param p 
     */
    public void removePoint(final WB_Point p) {
	points.remove(p);
    }

    /**
     * 
     *
     * @param seg 
     */
    public void addSegment(final WB_Segment seg) {
	segments.add(seg);
    }

    /**
     * 
     *
     * @param seg 
     */
    public void removeSegment(final WB_Segment seg) {
	segments.remove(seg);
    }

    /**
     * 
     *
     * @return 
     */
    public ArrayList<WB_Point> getPoints() {
	return points;
    }

    /**
     * 
     *
     * @return 
     */
    public ArrayList<WB_Segment> getSegments() {
	return segments;
    }

    /**
     * 
     *
     * @return 
     */
    public int getIndex() {
	return index;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_AABB getAABB() {
	return aabb;
    }

    /**
     * 
     *
     * @return 
     */
    public boolean isEmpty() {
	return points.isEmpty() && segments.isEmpty();
    }
}
