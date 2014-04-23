package wblut.hemesh;

import java.util.Iterator;

public class HE_LoopHalfedgeOuterCirculator implements Iterator<HE_Halfedge> {

	private final HE_LoopHalfedge _start;
	private HE_LoopHalfedge _current;

	public HE_LoopHalfedgeOuterCirculator(final HE_Loop loop) {
		_start = loop.getLoopHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {

		return (_current == null) || (_current.getPrevInLoop() != _start);
	}

	@Override
	public HE_Halfedge next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getPrevInLoop();
		}
		return _current.getHalfedge().getPair();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}