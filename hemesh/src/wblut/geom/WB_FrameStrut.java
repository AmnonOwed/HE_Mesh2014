/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_FrameStrut {
    
    /**
     * 
     */
    private final WB_FrameNode start;
    
    /**
     * 
     */
    private final WB_FrameNode end;
    
    /**
     * 
     */
    private final int index;
    
    /**
     * 
     */
    private double radiuss;
    
    /**
     * 
     */
    private double radiuse;
    
    /**
     * 
     */
    private double offsets;
    
    /**
     * 
     */
    private double offsete;

    /**
     * 
     *
     * @param s 
     * @param e 
     * @param id 
     */
    public WB_FrameStrut(final WB_FrameNode s, final WB_FrameNode e,
	    final int id) {
	start = s;
	end = e;
	index = id;
    }

    /**
     * 
     *
     * @param s 
     * @param e 
     * @param id 
     * @param r 
     */
    public WB_FrameStrut(final WB_FrameNode s, final WB_FrameNode e,
	    final int id, final double r) {
	start = s;
	end = e;
	index = id;
	radiuss = radiuse = r;
    }

    /**
     * 
     *
     * @param s 
     * @param e 
     * @param id 
     * @param rs 
     * @param re 
     */
    public WB_FrameStrut(final WB_FrameNode s, final WB_FrameNode e,
	    final int id, final double rs, final double re) {
	start = s;
	end = e;
	index = id;
	radiuss = rs;
	radiuse = re;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_FrameNode start() {
	return start;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_FrameNode end() {
	return end;
    }

    /**
     * 
     *
     * @return 
     */
    public int getStartIndex() {
	return start.getIndex();
    }

    /**
     * 
     *
     * @return 
     */
    public int getEndIndex() {
	return end.getIndex();
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
    public WB_Vector toVector() {
	return end().subToVector3D(start());
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Vector toNormVector() {
	final WB_Vector v = end().subToVector3D(start());
	v.normalizeSelf();
	return v;
    }

    /**
     * 
     *
     * @return 
     */
    public double getSqLength() {
	return WB_GeometryOp.getSqDistance3D(end(), start());
    }

    /**
     * 
     *
     * @return 
     */
    public double getLength() {
	return WB_GeometryOp.getDistance3D(end(), start());
    }

    /**
     * 
     *
     * @return 
     */
    public double getRadiusStart() {
	return radiuss;
    }

    /**
     * 
     *
     * @return 
     */
    public double getRadiusEnd() {
	return radiuse;
    }

    /**
     * 
     *
     * @param r 
     */
    public void setRadiusStart(final double r) {
	radiuss = r;
    }

    /**
     * 
     *
     * @param r 
     */
    public void setRadiusEnd(final double r) {
	radiuse = r;
    }

    /**
     * 
     *
     * @return 
     */
    public double getOffsetStart() {
	return offsets;
    }

    /**
     * 
     *
     * @return 
     */
    public double getOffsetEnd() {
	return offsete;
    }

    /**
     * 
     *
     * @param o 
     */
    public void setOffsetStart(final double o) {
	offsets = o;
    }

    /**
     * 
     *
     * @param o 
     */
    public void setOffsetEnd(final double o) {
	offsete = o;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point getCenter() {
	return end().add(start()).mulSelf(0.5);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Segment toSegment() {
	return new WB_Segment(start, end);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Plane toPlane() {
	return new WB_Plane(start().toPoint(), toVector());
    }
}
