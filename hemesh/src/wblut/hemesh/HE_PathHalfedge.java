package wblut.hemesh;

import java.util.HashMap;

import wblut.geom.WB_HasData;

public class HE_PathHalfedge extends HE_Element implements WB_HasData {
	private HE_Halfedge _he;

	private HE_PathHalfedge _next;

	private HE_PathHalfedge _prev;

	private HashMap<String, Object> _data;

	public HE_PathHalfedge() {
		super();
	}

	public HE_PathHalfedge(final HE_Halfedge he) {
		super();
		_he = he;
	}

	public void clearNext() {
		if (_next != null) {
			_next.clearPrev();
		}
		_next = null;
	}

	private void clearPrev() {
		_prev = null;
	}

	public HE_Halfedge getHalfedge() {
		return _he;
	}

	public void setHalfedge(final HE_Halfedge he) {
		_he = he;
	}

	public HE_PathHalfedge getNextInPath() {
		return _next;
	}

	public HE_PathHalfedge getPrevInPath() {
		return _prev;
	}

	public Long key() {
		return super.getKey();
	}

	public void setNext(final HE_PathHalfedge he) {
		_next = he;
		he.setPrev(this);
	}

	private void setPrev(final HE_PathHalfedge he) {
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

}
