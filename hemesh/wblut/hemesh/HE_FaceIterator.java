package wblut.hemesh;

import java.util.Iterator;

public class HE_FaceIterator implements Iterator<HE_Face> {

	Iterator<HE_Face> _itr;

	public HE_FaceIterator(final HE_MeshStructure mesh) {
		_itr = mesh.faces.iterator();
	}

	@Override
	public boolean hasNext() {
		return _itr.hasNext();
	}

	@Override
	public HE_Face next() {
		return _itr.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}