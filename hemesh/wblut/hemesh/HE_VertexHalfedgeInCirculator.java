package wblut.hemesh;

import java.util.Iterator;

public class HE_VertexHalfedgeInCirculator<V extends HE_Vertex> implements
		Iterator<HE_Halfedge> {

	private HE_Halfedge _start;
	private HE_Halfedge _current;

	public HE_VertexHalfedgeInCirculator(HE_Vertex v) {
		_start = v.getHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {

		return (_current == null) || (_current.getPrevInVertex() != _start);
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
