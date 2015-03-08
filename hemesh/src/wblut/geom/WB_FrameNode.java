/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import javolution.util.FastTable;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_FrameNode extends WB_Point {
    
    /**
     * 
     */
    private final FastTable<WB_FrameStrut> struts;
    
    /**
     * 
     */
    protected final int index;
    
    /**
     * 
     */
    protected double value;

    /**
     * 
     *
     * @param pos 
     * @param id 
     * @param v 
     */
    public WB_FrameNode(final WB_Coordinate pos, final int id, final double v) {
	super(pos);
	index = id;
	struts = new FastTable<WB_FrameStrut>();
	value = (v == 0) ? 10 * WB_Epsilon.EPSILON : v;
    }

    /**
     * 
     *
     * @param strut 
     * @return 
     */
    public boolean addStrut(final WB_FrameStrut strut) {
	if ((strut.start() != this) && (strut.end() != this)) {
	    return false;
	}
	for (int i = 0; i < struts.size(); i++) {
	    if ((struts.get(i).start() == strut.start())
		    && (struts.get(i).end() == strut.end())) {
		return false;
	    }
	}
	struts.add(strut);
	return true;
    }

    /**
     * 
     *
     * @param strut 
     * @return 
     */
    public boolean removeStrut(final WB_FrameStrut strut) {
	if ((strut.start() != this) && (strut.end() != this)) {
	    return false;
	}
	struts.remove(strut);
	return true;
    }

    /**
     * 
     *
     * @return 
     */
    public ArrayList<WB_FrameStrut> getStruts() {
	final ArrayList<WB_FrameStrut> result = new ArrayList<WB_FrameStrut>();
	result.addAll(struts);
	return result;
    }

    /**
     * 
     *
     * @return 
     */
    public ArrayList<WB_FrameNode> getNeighbors() {
	final ArrayList<WB_FrameNode> result = new ArrayList<WB_FrameNode>();
	for (int i = 0; i < struts.size(); i++) {
	    if (struts.get(i).start() == this) {
		result.add(struts.get(i).end());
	    } else {
		result.add(struts.get(i).start());
	    }
	}
	return result;
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
    public double findSmallestSpan() {
	double minAngle = Double.MAX_VALUE;
	for (int i = 0; i < getOrder(); i++) {
	    minAngle = Math.min(minAngle, findSmallestSpanAroundStrut(i));
	}
	return minAngle;
    }

    /**
     * 
     *
     * @param strut 
     * @return 
     */
    public double findSmallestSpanAroundStrut(final WB_FrameStrut strut) {
	return findSmallestSpanAroundStrut(struts.indexOf(strut));
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double findSmallestSpanAroundStrut(final int i) {
	final int n = struts.size();
	if ((i < 0) || (i >= n)) {
	    throw new IllegalArgumentException("Index beyond strut range.");
	}
	final ArrayList<WB_FrameNode> nnodes = getNeighbors();
	if (n == 1) {
	    return 2 * Math.PI;
	} else if (n == 2) {
	    final WB_Vector u = nnodes.get(0).subToVector3D(this);
	    final WB_Vector w = nnodes.get(1).subToVector3D(this);
	    u.normalizeSelf();
	    w.normalizeSelf();
	    final double udw = WB_Math.clamp(u.dot(w), -1, 1);
	    if (udw < (WB_Epsilon.EPSILON - 1)) {
		return Math.PI;
	    } else {
		return Math.acos(udw);
	    }
	} else {
	    double minAngle = Double.MAX_VALUE;
	    final WB_Vector u = nnodes.get(i).subToVector3D(this);
	    u.normalizeSelf();
	    for (int j = 0; j < n; j++) {
		if (i != j) {
		    final WB_Vector w = nnodes.get(j).subToVector3D(this);
		    w.normalizeSelf();
		    final double a = Math.acos(u.dot(w));
		    minAngle = WB_Math.min(minAngle, a);
		}
	    }
	    return minAngle;
	}
    }

    /**
     * 
     *
     * @return 
     */
    public double findShortestStrut() {
	double minLength = Double.MAX_VALUE;
	for (int i = 0; i < struts.size(); i++) {
	    minLength = Math.min(minLength, struts.get(i).getSqLength());
	}
	return Math.sqrt(minLength);
    }

    /**
     * 
     *
     * @return 
     */
    public int getOrder() {
	return struts.size();
    }

    /**
     * 
     *
     * @return 
     */
    public double getValue() {
	return value;
    }

    /**
     * 
     *
     * @param v 
     */
    public void setValue(final double v) {
	value = (v == 0) ? 10 * WB_Epsilon.EPSILON : v;
    }

    /**
     * 
     *
     * @param index 
     * @return 
     */
    public WB_FrameStrut getStrut(final int index) {
	if ((index < 0) || (index >= struts.size())) {
	    throw new IllegalArgumentException("Index outside of strut range.");
	}
	return struts.get(index);
    }

    /**
     * 
     *
     * @param index 
     */
    public void removeStrut(final int index) {
	if ((index < 0) || (index >= struts.size())) {
	    throw new IllegalArgumentException("Index outside of strut range.");
	}
	struts.remove(index);
    }

    /**
     * 
     *
     * @param index 
     * @return 
     */
    public WB_FrameNode getNeighbor(final int index) {
	if ((index < 0) || (index >= struts.size())) {
	    throw new IllegalArgumentException("Index outside of strut range.");
	}
	if (struts.get(index).start() == this) {
	    return struts.get(index).end();
	}
	return struts.get(index).start();
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point toPoint() {
	return new WB_Point(xd(), yd(), zd());
    }
}
