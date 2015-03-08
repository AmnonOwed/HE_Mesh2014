/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

/**
 * 
 */
public class HE_HalfedgeIterator implements Iterator<HE_Halfedge> {
    
    /**
     * 
     */
    Iterator<HE_Halfedge> _itr;

    /**
     * 
     *
     * @param mesh 
     */
    public HE_HalfedgeIterator(final HE_MeshStructure mesh) {
	_itr = mesh.halfedges.iterator();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
	return _itr.hasNext();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public HE_Halfedge next() {
	return _itr.next();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
	throw new UnsupportedOperationException();
    }
}
