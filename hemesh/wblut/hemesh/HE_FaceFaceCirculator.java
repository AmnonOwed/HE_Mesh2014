package wblut.hemesh;

import java.util.Iterator;

public class HE_FaceFaceCirculator implements Iterator<HE_Face> {

	private final HE_Halfedge _start;
	private HE_Halfedge _current;

	public HE_FaceFaceCirculator(final HE_Face f) {
		_start = f.getHalfedge();
		_current = null;

	}

	@Override
	public boolean hasNext() {

		return (_current == null) || (_current.getNextInFace() != _start);
	}

	@Override
	public HE_Face next() {
		if (_current == null) {
			_current = _start;
		} else {
			_current = _current.getNextInFace();
		}
		return _current.getFace();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}

}