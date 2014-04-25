package wblut.hemesh;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastList;

public class HE_Loop extends HE_Element implements WB_HasData {

	/** Halfedge associated with this face. */
	private HE_LoopHalfedge _loopHalfedge;

	/** The _data. */
	private HashMap<String, Object> _data;

	private boolean _sorted;

	/**
	 * Instantiates a new HE_Loop.
	 */
	public HE_Loop() {
		super();
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
	 * Get number of vertices in face.
	 * 
	 * @return number of vertices
	 */
	public int getLoopOrder() {

		int result = 0;
		if (_loopHalfedge == null) {
			return 0;
		}
		HE_LoopHalfedge he = _loopHalfedge;
		do {

			result++;
			he = he.getNextInLoop();
		} while (he != _loopHalfedge);

		return result;

	}

	/**
	 * Get halfedges of face as arraylist of HE_Halfedge. The halfedge of the
	 * leftmost vertex is returned first.
	 * 
	 * @return halfedges
	 */
	public List<HE_Halfedge> getLoopHalfedges() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Halfedge> fhe = new FastList<HE_Halfedge>();
		if (_loopHalfedge == null) {
			return fhe;
		}
		HE_LoopHalfedge he = _loopHalfedge;
		do {
			if (!fhe.contains(he.getHalfedge())) {
				fhe.add(he.getHalfedge());
			}

			he = he.getNextInLoop();
		} while (he != _loopHalfedge);

		return fhe;

	}

	/**
	 * Get edges of face as arraylist of HE_Edge. The edge of the leftmost
	 * vertex is returned first.
	 * 
	 * @return edges
	 */
	public List<HE_Edge> getLoopEdges() {
		if (!_sorted) {
			sort();
		}
		final List<HE_Edge> fe = new FastList<HE_Edge>();
		if (_loopHalfedge == null) {
			return fe;
		}
		HE_LoopHalfedge he = _loopHalfedge;
		do {

			if (!fe.contains(he.getHalfedge().getEdge())) {
				fe.add(he.getHalfedge().getEdge());
			}
			he = he.getNextInLoop();
		} while (he != _loopHalfedge);

		return fe;

	}

	/**
	 * Get halfedge.
	 * 
	 * @return halfedge
	 */
	public HE_LoopHalfedge getLoopHalfedge() {
		return _loopHalfedge;
	}

	/**
	 * Sets the halfedge.
	 * 
	 * @param halfedge
	 *            the new halfedge
	 */
	public void setLoopHalfedge(final HE_LoopHalfedge halfedge) {
		_loopHalfedge = halfedge;
		_sorted = false;
	}

	/**
	 * Clear halfedge.
	 */
	public void clearLoopHalfedge() {
		_loopHalfedge = null;
		_sorted = false;
	}

	/**
	 * Sort halfedges in lexicographic order.
	 */
	public void sort() {
		if (_loopHalfedge != null) {

			HE_LoopHalfedge he = _loopHalfedge;
			HE_LoopHalfedge leftmost = he;
			do {
				he = he.getNextInLoop();
				if (he.getHalfedge().getVertex()
						.compareTo(leftmost.getHalfedge().getVertex()) < 0) {
					leftmost = he;
				}
			} while (he != _loopHalfedge);
			_loopHalfedge = leftmost;
			_sorted = true;
		}
	}

	/**
	 * Get inner neighboring faces as arraylist of HE_Face. The face of the
	 * leftmost halfedge is returned first.
	 * 
	 * @return neighboring faces
	 */
	public List<HE_Face> getInnerFaces() {
		if (!isSorted()) {
			sort();
		}
		final List<HE_Face> ff = new FastList<HE_Face>();
		if (getLoopHalfedge() == null) {
			return ff;
		}
		HE_LoopHalfedge lhe = _loopHalfedge;
		HE_Halfedge he;
		do {
			he = lhe.getHalfedge();
			if (he.getFace() != null) {
				if (!ff.contains(he.getFace())) {
					ff.add(he.getFace());
				}
			}
			lhe = lhe.getNextInLoop();
		} while (lhe != _loopHalfedge);

		return ff;

	}

	/**
	 * Get outer neighboring faces as arraylist of HE_Face. The face of the
	 * leftmost halfedge is returned first.
	 * 
	 * @return neighboring faces
	 */
	public List<HE_Face> getOuterFaces() {
		if (!isSorted()) {
			sort();
		}
		final List<HE_Face> ff = new FastList<HE_Face>();
		if (getLoopHalfedge() == null) {
			return ff;
		}
		HE_LoopHalfedge lhe = _loopHalfedge;
		HE_Halfedge hep;
		do {
			hep = lhe.getHalfedge().getPair();
			if (hep.getFace() != null) {
				if (!ff.contains(hep.getFace())) {
					ff.add(hep.getFace());
				}
			}
			lhe = lhe.getNextInLoop();
		} while (lhe != _loopHalfedge);

		return ff;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		String s = "HE_Loop key: " + key() + ". Connects " + getLoopOrder()
				+ " vertices: ";
		HE_LoopHalfedge he = _loopHalfedge;
		for (int i = 0; i < getLoopOrder() - 1; i++) {
			s += he.getHalfedge().getVertex()._key + "-";
			he = he.getNextInLoop();
		}
		s += he.getHalfedge().getVertex()._key + ".";

		return s;
	}

	/**
	 * Checks if is sorted.
	 * 
	 * @return true, if is sorted
	 */
	public boolean isSorted() {
		return _sorted;
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
