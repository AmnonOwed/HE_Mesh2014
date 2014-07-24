package wblut.hemesh;

import java.util.Iterator;

public class HE_EdgeIterator implements Iterator<HE_Halfedge> {

	Iterator<HE_Halfedge> _itr;
	Iterator<HE_Halfedge> _nitr;
	HE_Halfedge nexthe;

	public HE_EdgeIterator(final HE_MeshStructure mesh) {
		_itr = mesh.halfedges.iterator();
		_nitr = mesh.halfedges.iterator();
		if (_itr.hasNext()) {
			nexthe = _nitr.next();
		}
	}

	@Override
	public boolean hasNext() {
		if (_itr.hasNext() == false) {
			return false;
		}
		while (!nexthe.isEdge()) {
			if (_itr.hasNext() == false) {
				return false;
			}
			_itr.next();
			nexthe = _nitr.next();
		}
		return true;
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