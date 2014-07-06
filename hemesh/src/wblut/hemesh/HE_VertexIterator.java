package wblut.hemesh;

import java.util.Iterator;

public class HE_VertexIterator implements Iterator<HE_Vertex> {

	Iterator<HE_Vertex> _itr;

	public HE_VertexIterator(final HE_MeshStructure mesh) {
		_itr = mesh.vertices.iterator();
	}

	@Override
	public boolean hasNext() {
		return _itr.hasNext();
	}

	@Override
	public HE_Vertex next() {
		return _itr.next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
