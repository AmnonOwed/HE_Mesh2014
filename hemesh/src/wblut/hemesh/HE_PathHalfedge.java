/*
 * 
 */
package wblut.hemesh;

import java.util.HashMap;
import wblut.geom.WB_HasData;

/**
 * 
 */
public class HE_PathHalfedge extends HE_Element implements WB_HasData {
    
    /**
     * 
     */
    private HE_Halfedge _he;
    
    /**
     * 
     */
    private HE_PathHalfedge _next;
    
    /**
     * 
     */
    private HE_PathHalfedge _prev;
    
    /**
     * 
     */
    private HashMap<String, Object> _data;

    /**
     * 
     */
    public HE_PathHalfedge() {
	super();
    }

    /**
     * 
     *
     * @param he 
     */
    public HE_PathHalfedge(final HE_Halfedge he) {
	super();
	_he = he;
    }

    /**
     * 
     */
    public void clearNext() {
	if (_next != null) {
	    _next.clearPrev();
	}
	_next = null;
    }

    /**
     * 
     */
    private void clearPrev() {
	_prev = null;
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Halfedge getHalfedge() {
	return _he;
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Vertex getVertex() {
	return _he.getVertex();
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Vertex getStartVertex() {
	return _he.getVertex();
    }

    /**
     * 
     *
     * @return 
     */
    public HE_Vertex getEndVertex() {
	return _he.getEndVertex();
    }

    /**
     * 
     *
     * @param he 
     */
    public void setHalfedge(final HE_Halfedge he) {
	_he = he;
    }

    /**
     * 
     *
     * @return 
     */
    public HE_PathHalfedge getNextInPath() {
	return _next;
    }

    /**
     * 
     *
     * @return 
     */
    public HE_PathHalfedge getPrevInPath() {
	return _prev;
    }

    /**
     * 
     *
     * @return 
     */
    public Long key() {
	return super.getKey();
    }

    /**
     * 
     *
     * @param he 
     */
    public void setNext(final HE_PathHalfedge he) {
	_next = he;
    }

    /**
     * 
     *
     * @param he 
     */
    public void setPrev(final HE_PathHalfedge he) {
	_prev = he;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.Point3D#toString()
     */
    @Override
    public String toString() {
	return "HE_LoopHalfedge key: " + key() + ".";
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.core.WB_HasData#setData(java.lang.String, java.lang.Object)
     */
    @Override
    public void setData(final String s, final Object o) {
	if (_data == null) {
	    _data = new HashMap<String, Object>();
	}
	_data.put(s, o);
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.core.WB_HasData#getData(java.lang.String)
     */
    @Override
    public Object getData(final String s) {
	return _data.get(s);
    }

    /* (non-Javadoc)
     * @see wblut.hemesh.HE_Element#clear()
     */
    @Override
    public void clear() {
	_data = null;
	_he = null;
	_next = null;
	_prev = null;
    }
}
