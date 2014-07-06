package wblut.hemesh;

import java.util.HashMap;
import java.util.List;

import javolution.util.FastTable;
import wblut.geom.WB_HasData;

public class HE_Path extends HE_Element implements WB_HasData {

	private HE_PathHalfedge _pathHalfedge;

	private HashMap<String, Object> _data;

	public HE_Path() {
		super();
	}

	public long key() {
		return super.getKey();
	}

	public int getPathOrder() {
		int result = 0;
		if (_pathHalfedge == null) {
			return 0;
		}
		HE_PathHalfedge he = _pathHalfedge;
		do {
			result++;
			he = he.getNextInPath();
		} while ((he != _pathHalfedge) && (he != null));
		return result;
	}

	public double getPathLength() {
		double result = 0;
		if (_pathHalfedge == null) {
			return result;
		}
		HE_PathHalfedge he = _pathHalfedge;
		do {
			result += he.getHalfedge().getLength();
			he = he.getNextInPath();
		} while ((he != _pathHalfedge) && (he != null));
		return result;
	}

	public double[] getIncPathLengths() {
		final double[] result = new double[getPathOrder() + 1];
		if (_pathHalfedge == null) {
			return result;
		}

		HE_PathHalfedge he = _pathHalfedge;
		result[0] = 0;
		int i = 1;
		do {
			result[i] = result[i - 1] + he.getHalfedge().getLength();
			he = he.getNextInPath();
			i++;
		} while ((he != _pathHalfedge) && (he != null));
		return result;
	}

	public List<HE_Halfedge> getPathHalfedges() {
		final List<HE_Halfedge> fhe = new FastTable<HE_Halfedge>();
		if (_pathHalfedge == null) {
			return fhe;
		}
		HE_PathHalfedge he = _pathHalfedge;
		do {
			if (!fhe.contains(he.getHalfedge())) {
				fhe.add(he.getHalfedge());
			}
			he = he.getNextInPath();
		} while ((he != _pathHalfedge) && (he != null));
		return fhe;
	}

	public List<HE_Edge> getPathEdges() {
		final List<HE_Edge> fe = new FastTable<HE_Edge>();
		if (_pathHalfedge == null) {
			return fe;
		}
		HE_PathHalfedge he = _pathHalfedge;
		do {
			if (!fe.contains(he.getHalfedge().getEdge())) {
				fe.add(he.getHalfedge().getEdge());
			}
			he = he.getNextInPath();
		} while ((he != _pathHalfedge) && (he != null));
		return fe;
	}

	public HE_PathHalfedge getPathHalfedge() {
		return _pathHalfedge;
	}

	public void setPathHalfedge(final HE_PathHalfedge halfedge) {
		_pathHalfedge = halfedge;
	}

	public void clearPathHalfedge() {
		_pathHalfedge = null;

	}

	public List<HE_Face> getInnerFaces() {
		final List<HE_Face> ff = new FastTable<HE_Face>();
		if (getPathHalfedge() == null) {
			return ff;
		}
		HE_PathHalfedge lhe = _pathHalfedge;
		HE_Halfedge he;
		do {
			he = lhe.getHalfedge();
			if (he.getFace() != null) {
				if (!ff.contains(he.getFace())) {
					ff.add(he.getFace());
				}
			}
			lhe = lhe.getNextInPath();
		} while ((lhe != _pathHalfedge) && (lhe != null));
		return ff;

	}

	public List<HE_Face> getOuterFaces() {
		final List<HE_Face> ff = new FastTable<HE_Face>();
		if (getPathHalfedge() == null) {
			return ff;
		}
		HE_PathHalfedge lhe = _pathHalfedge;
		HE_Halfedge hep;
		do {
			hep = lhe.getHalfedge().getPair();
			if (hep.getFace() != null) {
				if (!ff.contains(hep.getFace())) {
					ff.add(hep.getFace());
				}
			}
			lhe = lhe.getNextInPath();
		} while ((lhe != _pathHalfedge) && (lhe != null));
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
		 HE_PathHalfedge he = _pathHalfedge;
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

}
