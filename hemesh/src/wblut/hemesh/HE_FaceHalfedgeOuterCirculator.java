/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

/**
 * 
 */
public class HE_FaceHalfedgeOuterCirculator implements Iterator<HE_Halfedge> {
    
    /**
     * 
     */
    private final HE_Halfedge _start;
    
    /**
     * 
     */
    private HE_Halfedge _current;

    /**
     * 
     *
     * @param f 
     */
    public HE_FaceHalfedgeOuterCirculator(final HE_Face f) {
	_start = f.getHalfedge();
	_current = null;
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
	if (_start == null) {
	    return false;
	}
	return ((_current == null) || (_current.getPrevInFace() != _start))
		&& (_start != null);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public HE_Halfedge next() {
	if (_current == null) {
	    _current = _start;
	} else {
	    _current = _current.getPrevInFace();
	}
	return _current.getPair();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
	throw new UnsupportedOperationException();
    }
}