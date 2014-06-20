package wblut.hemesh;

import java.util.HashMap;

import wblut.geom.WB_Distance;
import wblut.geom.WB_HasData;
import wblut.geom.WB_Point;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Vector;

/**
 * Edge element of half-edge data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HE_Edge extends HE_Element implements WB_HasData {

	/** Halfedge associated with this edge. */
	private HE_Halfedge _halfedge;

	/** The _data. */
	private HashMap<String, Object> _data;

	/**
	 * Instantiates a new HE_Edge.
	 */
	public HE_Edge() {
		super();
	}

	/**
	 * Get key.
	 * 
	 * @return key
	 */

	public long key() {
		return super.getKey();
	}

	/**
	 * Get edge center.
	 * 
	 * @return edge center
	 */

	public WB_Point getEdgeCenter() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new NullPointerException("Vertices missing in edge.");
		}
		final WB_Point center = getStartVertex().pos.add(getEndVertex().pos)
				._mulSelf(0.5);
		return center;

	}

	public WB_Point getEdgeCenter(double d) {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new NullPointerException("Vertices missing in edge.");
		}
		final WB_Point center = getStartVertex().pos.add(getEndVertex().pos)
				._mulSelf(0.5);
		return center._addMulSelf(d, getEdgeNormal());

	}

	/**
	 * Return tangent WB_Vector.
	 * 
	 * @return tangent
	 */

	public WB_Vector getEdgeTangent() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new NullPointerException("Vertices missing in edge.");
		}
		final WB_Vector v = getEndVertex().pos
				.subToVector(getStartVertex().pos);
		v._normalizeSelf();
		return v;
	}

	/**
	 * Return edge segment. The semantically lower vertex is set first.
	 * 
	 * @return segment
	 */

	public WB_Segment toOrderedSegment() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new IllegalArgumentException("Vertices missing in edge.");
		}
		if (getStartVertex().pos.smallerThan(getEndVertex().pos)) {
			return new WB_Segment(getStartVertex(), getEndVertex());
		} else {
			return new WB_Segment(getEndVertex(), getStartVertex());
		}

	}

	/**
	 * Return edge segment.
	 * 
	 * @return segment
	 */

	public WB_Segment toSegment() {
		if ((getStartVertex() == null) || (getEndVertex() == null)) {
			throw new IllegalArgumentException("Vertices missing in edge.");
		}

		return new WB_Segment(getStartVertex(), getEndVertex());

	}

	/**
	 * Gets the length.
	 * 
	 * @return the length
	 */
	public double getLength() {
		return WB_Distance.getDistance3D(getStartVertex(), getEndVertex());
	}

	/**
	 * Get halfedge.
	 * 
	 * @return halfedge
	 */

	public HE_Halfedge getHalfedge() {
		return _halfedge;
	}

	/**
	 * Sets the halfedge.
	 * 
	 * @param halfedge
	 *            the new halfedge
	 */

	public void setHalfedge(final HE_Halfedge halfedge) {
		_halfedge = halfedge;
	}

	/**
	 * Clear halfedge.
	 */

	public void clearHalfedge() {
		_halfedge = null;
	}

	/**
	 * Get first vertex.
	 * 
	 * @return first vertex
	 */

	public HE_Vertex getStartVertex() {
		return _halfedge.getVertex();
	}

	/**
	 * Get second vertex.
	 * 
	 * @return second vertex
	 */

	public HE_Vertex getEndVertex() {
		return _halfedge.getPair().getVertex();
	}

	/**
	 * Get first face of an edge.
	 * 
	 * @return first face
	 */
	public HE_Face getFirstFace() {
		return getHalfedge().getFace();
	}

	/**
	 * Get second face of an edge.
	 * 
	 * @return second face
	 */
	public HE_Face getSecondFace() {
		return getHalfedge().getPair().getFace();
	}

	/**
	 * Get edge normal.
	 * 
	 * @return edge normal
	 */
	public WB_Vector getEdgeNormal() {

		if ((getFirstFace() == null) && (getSecondFace() == null)) {
			return null;
		}
		final WB_Vector n1 = (getFirstFace() != null) ? getFirstFace()
				.getFaceNormal() : new WB_Vector(0, 0, 0);
		final WB_Vector n2 = (getSecondFace() != null) ? getSecondFace()
				.getFaceNormal() : new WB_Vector(0, 0, 0);
		final WB_Vector n = new WB_Vector(n1.xd() + n2.xd(), n1.yd() + n2.yd(),
				n1.zd() + n2.zd());
		n._normalizeSelf();
		return n;
	}

	/**
	 * Get area of faces bounding edge.
	 * 
	 * @return area
	 */
	public double getEdgeArea() {
		if ((getFirstFace() == null) && (getSecondFace() == null)) {
			return Double.NaN;
		}
		double result = 0;
		int n = 0;
		if (getFirstFace() != null) {
			result += getFirstFace().getFaceArea();
			n++;
		}
		if (getSecondFace() != null) {
			result += getSecondFace().getFaceArea();
			n++;
		}

		return result / n;

	}

	/**
	 * Return angle between adjacent faces.
	 * 
	 * @return angle
	 */
	public double getDihedralAngle() {

		if ((getFirstFace() == null) || (getSecondFace() == null)) {
			return Double.NaN;
		} else {
			final WB_Vector n1 = getFirstFace().getFaceNormal();
			final WB_Vector n2 = getSecondFace().getFaceNormal();
			return Math.PI - Math.acos(n1.dot(n2));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Edge key: " + key() + ", connects vertex "
				+ getStartVertex().key() + " to vertex " + getEndVertex().key()
				+ ".";
	}

	/**
	 * Checks if is boundary.
	 * 
	 * @return true, if is boundary
	 */
	public boolean isBoundary() {
		return (_halfedge.getFace() == null)
				|| (_halfedge.getPair().getFace() == null);
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
