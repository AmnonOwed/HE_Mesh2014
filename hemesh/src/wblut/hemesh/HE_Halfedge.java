package wblut.hemesh;

import java.util.HashMap;

import wblut.geom.WB_Convex;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Half-edge element of half-edge data structure.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HE_Halfedge extends HE_Element implements WB_HasData, WB_HasColor {

	/** Start vertex of halfedge. */
	private HE_Vertex _vertex;

	/** Halfedge pair. */
	private HE_Halfedge _pair;

	/** Next halfedge in face. */
	private HE_Halfedge _next;

	/** Previous halfedge in face. */
	private HE_Halfedge _prev;

	/** Associated edge. */
	private HE_Edge _edge;

	/** Associated face. */
	private HE_Face _face;

	/** The _data. */
	private HashMap<String, Object> _data;

	private int hecolor;

	/**
	 * Instantiates a new HE_Halfedge.
	 */
	public HE_Halfedge() {
		super();
		hecolor = -1;
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
	 * Get previous halfedge in face.
	 * 
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInFace() {
		return _prev;
	}

	/**
	 * Get next halfedge in face.
	 * 
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInFace() {
		return _next;
	}

	/**
	 * Get next halfedge in vertex.
	 * 
	 * @return next halfedge
	 */
	public HE_Halfedge getNextInVertex() {
		if (_pair == null) {
			return null;
		}
		return _pair.getNextInFace();
	}

	/**
	 * Get previous halfedge in vertex.
	 * 
	 * @return previous halfedge
	 */
	public HE_Halfedge getPrevInVertex() {
		if (_prev == null) {
			return null;
		}
		return getPrevInFace().getPair();
	}

	/**
	 * Get paired halfedge.
	 * 
	 * @return paired halfedge
	 */
	public HE_Halfedge getPair() {

		return _pair;
	}

	/**
	 * Set next halfedge in face.
	 * 
	 * @param he
	 *            next halfedge
	 */
	public void setNext(final HE_Halfedge he) {
		_next = he;
		he.setPrev(this);
	}

	/**
	 * Sets previous halfedge in face, only to be called by setNext.
	 * 
	 * @param he
	 *            next halfedge
	 */
	private void setPrev(final HE_Halfedge he) {
		_prev = he;

	}

	/**
	 * Mutually pair halfedges.
	 * 
	 * @param he
	 *            halfedge to pair
	 */
	public void setPair(final HE_Halfedge he) {
		_pair = he;
		he.setPairInt(this);
	}

	/**
	 * Pair halfedges, only to be called by setPair.
	 * 
	 * @param he
	 *            halfedge to pair
	 */
	private void setPairInt(final HE_Halfedge he) {
		_pair = he;
	}

	/**
	 * Get type of face vertex associated with halfedge.
	 * 
	 * @return HE.FLAT, HE.CONVEX, HE.CONCAVE
	 */
	public WB_Convex getHalfedgeType() {

		if (_vertex == null) {
			return null;
		}
		WB_Vector v = _vertex.pos.subToVector(getPrevInFace()._vertex);
		v._normalizeSelf();
		final WB_Vector vn = getNextInFace()._vertex.pos.subToVector(_vertex);
		vn._normalizeSelf();
		v = v.cross(vn);
		final WB_Vector n;
		if (_face == null) {
			n = _pair._face.getFaceNormal()._mulSelf(-1);
		} else {
			n = _face.getFaceNormal();
		}
		final double dot = n.dot(v);

		if (v.isParallel(vn)) {
			return WB_Convex.FLAT;
		} else if (dot > 0) {
			return WB_Convex.CONVEX;
		} else {
			return WB_Convex.CONCAVE;
		}

	}

	/**
	 * Get tangent WB_Vector of halfedge.
	 * 
	 * @return tangent
	 */
	public WB_Vector getHalfedgeTangent() {
		if (_edge != null) {
			return (_edge.getHalfedge() == this) ? _edge.getEdgeTangent()
					: _edge.getEdgeTangent().mul(-1);
		}
		if ((_pair != null) && (_vertex != null) && (_pair.getVertex() != null)) {
			final WB_Vector v = _pair.getVertex().pos.subToVector(_vertex);
			v._normalizeSelf();
			return v;
		}
		return null;
	}

	/**
	 * Get center of halfedge.
	 * 
	 * @return center
	 */
	public WB_Point getHalfedgeCenter() {
		if (_edge != null) {
			return _edge.getEdgeCenter();
		}
		if ((_next != null) && (_vertex != null) && (_next.getVertex() != null)) {
			return _next.getVertex().pos.add(_vertex)._mulSelf(0.5);
		}
		return null;
	}

	/**
	 * Get edge of halfedge.
	 * 
	 * @return edge
	 */
	public HE_Edge getEdge() {
		return _edge;
	}

	/**
	 * Sets the edge.
	 * 
	 * @param edge
	 *            the new edge
	 */
	public void setEdge(final HE_Edge edge) {
		_edge = edge;
	}

	/**
	 * Get face of halfedge.
	 * 
	 * @return face
	 */
	public HE_Face getFace() {
		return _face;
	}

	/**
	 * Sets the face.
	 * 
	 * @param face
	 *            the new face
	 */
	public void setFace(final HE_Face face) {
		if (_face != null) {
			_face._sorted = false;
		}
		_face = face;
		_face._sorted = false;
	}

	/**
	 * Get vertex of halfedge.
	 * 
	 * @return vertex
	 */
	public HE_Vertex getVertex() {
		return _vertex;
	}

	/**
	 * Sets the vertex.
	 * 
	 * @param vertex
	 *            the new vertex
	 */
	public void setVertex(final HE_Vertex vertex) {
		_vertex = vertex;
	}

	/**
	 * Get end vertex of halfedge.
	 * 
	 * @return vertex
	 */
	public HE_Vertex getEndVertex() {
		if (_pair != null) {
			return _pair._vertex;
		}
		return _next._vertex;
	}

	/**
	 * Clear next.
	 */
	public void clearNext() {
		_next = null;
	}

	/**
	 * Clear prev
	 */
	public void clearPrev() {
		_prev = null;
	}

	/**
	 * Clear pair
	 */
	public void clearPair() {
		_pair = null;

	}

	/**
	 * Clear edge.
	 */
	public void clearEdge() {
		_edge = null;
	}

	/**
	 * Clear face.
	 */
	public void clearFace() {
		if (_face != null) {
			_face._sorted = false;
		}
		_face = null;

	}

	/**
	 * Clear vertex.
	 */
	public void clearVertex() {
		_vertex = null;
	}

	/**
	 * Get halfedge normal.
	 * 
	 * @return in-face normal of face, points inwards
	 */
	public WB_Vector getHalfedgeNormal() {
		WB_Vector fn;
		if ((getFace() == null) && (getPair() == null)) {
			return null;
		}

		if (getFace() == null) {
			if (getPair().getFace() == null) {
				return null;
			}
			fn = getPair().getFace().getFaceNormal();
		} else {
			fn = getFace().getFaceNormal();
		}
		final HE_Vertex vn = getNextInFace().getVertex();
		final WB_Vector _normal = new WB_Vector(vn);
		_normal._subSelf(getVertex());
		_normal._set(fn.cross(_normal));
		_normal._normalizeSelf();
		return _normal;

	}

	/**
	 * Get area of faces bounding halfedge.
	 * 
	 * @return area
	 */
	public double getHalfedgeArea() {
		if (getEdge() == null) {
			return 0;
		}
		return 0.5 * getEdge().getEdgeArea();
	}

	/**
	 * Get angle between adjacent faces.
	 * 
	 * @return angle
	 */
	public double getHalfedgeDihedralAngle() {
		if (getEdge() == null) {
			return Double.NaN;
		}
		return getEdge().getDihedralAngle();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.geom.Point3D#toString()
	 */
	@Override
	public String toString() {
		return "HE_Halfedge key: " + key() + ", belongs to edge "
				+ getEdge().key() + ", paired with halfedge " + getPair().key()
				+ ". Vertex: " + getVertex().key() + ".";
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

	@Override
	public int getColor() {

		return hecolor;
	}

	@Override
	public void setColor(int color) {
		hecolor = color;

	}

}
