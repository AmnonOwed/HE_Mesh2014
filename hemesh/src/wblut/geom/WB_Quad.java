/*
 * 
 */
package wblut.geom;

/**
 * Placeholder for quad..
 */
public class WB_Quad {
    /** First point. */
    public WB_Point p1;
    /** Second point. */
    public WB_Point p2;
    /** Third point. */
    public WB_Point p3;
    /** Fourth point. */
    public WB_Point p4;

    /**
     * Instantiates a new WB_Quad. No copies are made.
     * 
     * @param p1
     *            first point
     * @param p2
     *            second point
     * @param p3
     *            third point
     * @param p4
     *            fourth point
     */
    public WB_Quad(final WB_Point p1, final WB_Point p2, final WB_Point p3,
	    final WB_Point p4) {
	this.p1 = p1;
	this.p2 = p2;
	this.p3 = p3;
	this.p4 = p4;
    }
}