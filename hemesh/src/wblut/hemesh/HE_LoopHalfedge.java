package wblut.hemesh;

import java.util.HashMap;

import wblut.geom.WB_HasData;

public class HE_LoopHalfedge extends HE_Element implements WB_HasData {
	private HE_Halfedge _he;

	private HE_LoopHalfedge _next;

	private HE_LoopHalfedge _prev;

	/** The _data. */
	private HashMap<String, Object> _data;

	public HE_LoopHalfedge() {
		super();
	}

	/**
	 * Clear next.
	 */
	public void clearNext() {
		if (_next != null) {
			_next.clearPrev();
		}
		_next = null;
	}

	/**
	 * Clear prev, only to be called by clearNext.
	 */
	private void clearPrev() {
		_prev = null;
	}

	/**
	 * Get face of halfedge.
	 * 
	 * @return face
	 */
	public HE_Halfedge getHalfedge() {
		return _he;
	}

	/**
	 * Get next loop halfedge in face.
	 * 
	 * @return next halfedge
	 */
	public HE_LoopHalfedge getNextInLoop() {
		return _next;
	}

	/**
	 * Get previous loop halfedge in face.
	 * 
	 * @return previous halfedge
	 */
	public HE_LoopHalfedge getPrevInLoop() {
		return _prev;
	}

	/**
	 * Get key.
	 * 
	 * @return key
	 */
	public Long key() {
		return super.getKey();
	}

	/**
	 * Set next halfedge in loop.
	 * 
	 * @param he
	 *            next halfedge
	 */
	public void setNext(final HE_LoopHalfedge he) {
		_next = he;
		he.setPrev(this);
	}

	/**
	 * Sets previous halfedge in face, only to be called by setNext.
	 * 
	 * @param he
	 *            next halfedge
	 */
	private void setPrev(final HE_LoopHalfedge he) {
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
	public Object getData(final String s) {
		return _data.get(s);
	}

}
