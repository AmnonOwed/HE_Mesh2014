package wblut.hemesh;

import java.util.Iterator;

public class HE_VertexHalfedgeInCirculator implements Iterator<HE_Halfedge> {

	private HE_Halfedge _start;
	private HE_Halfedge _current;

	public HE_VertexHalfedgeInCirculator(HE_Vertex v) {
		_start = v.getHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {
		if (_start == null)
			return false;
		return ((_current == null) || (_current.getPrevInVertex() != _start))
				&& (_start != null);
	}

	@Override
	public HE_Halfedge next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getPrevInVertex();
		}
		return _current.getPair();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}
