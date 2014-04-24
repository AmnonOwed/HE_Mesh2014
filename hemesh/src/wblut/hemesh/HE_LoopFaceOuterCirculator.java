package wblut.hemesh;

import java.util.Iterator;

public class HE_LoopFaceOuterCirculator implements Iterator<HE_Face> {

	private final HE_LoopHalfedge _start;
	private HE_LoopHalfedge _current;

	public HE_LoopFaceOuterCirculator(final HE_Loop loop) {
		_start = loop.getLoopHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {

		return (_current == null) || (_current.getPrevInLoop() != _start);
	}

	@Override
	public HE_Face next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getPrevInLoop();
		}
		return _current.getHalfedge().getPair().getFace();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}