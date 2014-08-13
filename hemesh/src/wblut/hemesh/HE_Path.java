package wblut.hemesh;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_HasData;

public class HE_Path extends HE_Element implements WB_HasData {

	protected HE_PathHalfedge _halfedge;

	private HashMap<String, Object> _data;

	public HE_Path() {
		super();
	}

	public HE_Path(final HE_Halfedge loop) {
		super();
		_halfedge = new HE_PathHalfedge(loop);
		HE_Halfedge he = loop;
		final HE_PathHalfedge first = _halfedge;
		HE_PathHalfedge current = first;
		HE_PathHalfedge next;
		while (he.getNextInFace() != loop) {
			next = new HE_PathHalfedge(he = he.getNextInFace());
			current.setNext(next);
			next.setPrev(current);
			current = next;
		}
		current.setNext(first);
		first.setPrev(current);
	}

	public long key() {
		return super.getKey();
	}

	public int getPathOrder() {
		int result = 0;
		if (_halfedge == null) {
			return 0;
		}
		HE_PathHalfedge he = _halfedge;
		do {
			result++;
			he = he.getNextInPath();
		} while ((he != _halfedge) && (he != null));
		return result;
	}

	public double getPathLength() {
		double result = 0;
		if (_halfedge == null) {
			return result;
		}
		HE_PathHalfedge he = _halfedge;
		do {
			result += he.getHalfedge().getLength();
			he = he.getNextInPath();
		} while ((he != _halfedge) && (he != null));
		return result;
	}

	public double[] getPathIncLengths() {
		final double[] result = new double[getPathOrder() + 1];
		if (_halfedge == null) {
			return result;
		}

		HE_PathHalfedge he = _halfedge;
		result[0] = 0;
		int i = 1;
		do {
			result[i] = result[i - 1] + he.getHalfedge().getLength();
			he = he.getNextInPath();
			i++;
		} while ((he != _halfedge) && (he != null));
		return result;
	}

	public List<HE_Halfedge> getHalfedges() {
		final List<HE_Halfedge> fhe = new FastTable<HE_Halfedge>();
		if (_halfedge == null) {
			return fhe;
		}
		HE_PathHalfedge he = _halfedge;
		do {
			if (!fhe.contains(he.getHalfedge())) {
				fhe.add(he.getHalfedge());
			}
			he = he.getNextInPath();
		} while ((he != _halfedge) && (he != null));
		return fhe;
	}

	public List<HE_Vertex> getPathVertices() {
		final List<HE_Vertex> fhe = new FastTable<HE_Vertex>();
		if (_halfedge == null) {
			return fhe;
		}
		HE_PathHalfedge he = _halfedge;
		do {
			if (!fhe.contains(he.getHalfedge())) {
				fhe.add(he.getHalfedge().getVertex());
			}
			he = he.getNextInPath();
		} while ((he != _halfedge) && (he != null));
		return fhe;
	}

	public List<HE_Halfedge> getPathEdges() {
		final List<HE_Halfedge> fe = new FastTable<HE_Halfedge>();
		if (_halfedge == null) {
			return fe;
		}
		HE_PathHalfedge he = _halfedge;
		do {
			if (he.getHalfedge().isEdge()) {
				fe.add(he.getHalfedge());
			}
			else {
				fe.add(he.getHalfedge().getPair());
			}
			he = he.getNextInPath();
		} while ((he != _halfedge) && (he != null));
		return fe;
	}

	public HE_PathHalfedge getPathHalfedge() {
		return _halfedge;
	}

	public void setPathHalfedge(final HE_PathHalfedge halfedge) {
		_halfedge = halfedge;
	}

	public void clearPathHalfedge() {
		_halfedge = null;

	}

	public List<HE_Face> getPathInnerFaces() {
		final List<HE_Face> ff = new FastTable<HE_Face>();
		if (getPathHalfedge() == null) {
			return ff;
		}
		HE_PathHalfedge lhe = _halfedge;
		HE_Halfedge he;
		do {
			he = lhe.getHalfedge();
			if (he.getFace() != null) {
				if (!ff.contains(he.getFace())) {
					ff.add(he.getFace());
				}
			}
			lhe = lhe.getNextInPath();
		} while ((lhe != _halfedge) && (lhe != null));
		return ff;

	}

	public List<HE_Face> getPathOuterFaces() {
		final List<HE_Face> ff = new FastTable<HE_Face>();
		if (getPathHalfedge() == null) {
			return ff;
		}
		HE_PathHalfedge lhe = _halfedge;
		HE_Halfedge hep;
		do {
			hep = lhe.getHalfedge().getPair();
			if (hep.getFace() != null) {
				if (!ff.contains(hep.getFace())) {
					ff.add(hep.getFace());
				}
			}
			lhe = lhe.getNextInPath();
		} while ((lhe != _halfedge) && (lhe != null));
		return ff;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		String s = "HE_Path key: " + key() + ". Connects " + getPathOrder()
				+ " vertices: ";
		HE_PathHalfedge he = _halfedge;
		if (he != null) {
			for (int i = 0; i < getPathOrder() - 1; i++) {
				s += he.getHalfedge().getVertex()._key + "-";
				he = he.getNextInPath();
			}
			s += he.getHalfedge().getEndVertex()._key + ".";
		}
		return s;
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

	@Override
	public void clear() {
		_data = null;
		_halfedge = null;
	}

}
