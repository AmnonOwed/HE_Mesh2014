/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

/**
 * 
 */
public class HE_EdgeIterator implements Iterator<HE_Halfedge> {
    
    /**
     * 
     */
    Iterator<HE_Halfedge> _itr;
    
    /**
     * 
     */
    Iterator<HE_Halfedge> _nitr;
    
    /**
     * 
     */
    HE_Halfedge nexthe;

    /**
     * 
     *
     * @param mesh 
     */
    public HE_EdgeIterator(final HE_MeshStructure mesh) {
	_itr = mesh.halfedges.iterator();
	_nitr = mesh.halfedges.iterator();
	if (_itr.hasNext()) {
	    nexthe = _nitr.next();
	}
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
	do {
	    // reached end of list, status of nexthe is irrelevant
	    if (!_itr.hasNext()) {
		return false;
	    }
	    // reached one but last element of list, status of nexthe is only
	    // thing that matters
	    if (!_nitr.hasNext()) {
		return nexthe.isEdge();
	    }
	    // somewhere in the middle, if nexhthe is an edge, stop here.
	    // Otherwise progress until nexthe is an edge
	    if (nexthe.isEdge()) {
		return true;
	    }
	    _itr.next();
	    nexthe = _nitr.next();
	} while (true);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public HE_Halfedge next() {
	if (_nitr.hasNext()) {
	    nexthe = _nitr.next();
	}
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