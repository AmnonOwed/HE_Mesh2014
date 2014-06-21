package wblut.hemesh;

import java.util.Iterator;

public class HE_LoopHalfedgeOuterCirculator implements Iterator<HE_Halfedge> {

	private final HE_PathHalfedge _start;
	private HE_PathHalfedge _current;

	public HE_LoopHalfedgeOuterCirculator(final HE_Loop loop) {
		_start = loop.getLoopHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {
		if (_start == null)
			return false;
		return (_current == null) || (_current.getPrevInPath() != _start);
	}

	@Override
	public HE_Halfedge next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getPrevInPath();
		}
		return _current.getHalfedge().getPair();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}