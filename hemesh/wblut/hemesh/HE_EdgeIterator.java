package wblut.hemesh;

import java.util.Iterator;

public class HE_EdgeIterator implements Iterator<HE_Edge> {

	Iterator<HE_Edge> _itr;

	public HE_EdgeIterator(final HE_MeshStructure mesh) {
		_itr = mesh.edges.iterator();
	}

	@Override
	public boolean hasNext() {
		return _itr.hasNext();
	}

	@Override
	public HE_Edge next() {
		return _itr.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}