package wblut.hemesh;

import java.util.Iterator;

public class HE_HalfedgeIterator implements Iterator<HE_Halfedge> {

	Iterator<HE_Halfedge> _itr;

	public HE_HalfedgeIterator(final HE_MeshStructure mesh) {
		_itr = mesh.halfedges.iterator();
	}

	@Override
	public boolean hasNext() {
		return _itr.hasNext();
	}

	@Override
	public HE_Halfedge next() {
		return _itr.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();

	}
}
