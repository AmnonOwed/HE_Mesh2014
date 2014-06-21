package wblut.hemesh;

import java.util.Iterator;

public class HE_LoopEdgeCirculator implements Iterator<HE_Edge> {

	private final HE_PathHalfedge _start;
	private HE_PathHalfedge _current;

	public HE_LoopEdgeCirculator(final HE_Loop loop) {
		_start = loop.getLoopHalfedge();
		_current = null;
	}

	@Override
	public boolean hasNext() {
		if (_start == null)
			return false;
		return (_current == null) || (_current.getNextInPath() != _start);
	}

	@Override
	public HE_Edge next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getNextInPath();
		}
		return _current.getHalfedge().getEdge();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}
