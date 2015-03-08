/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

/**
 * 
 */
public class HE_FaceVertexCirculator implements Iterator<HE_Vertex> {
    
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
    public HE_FaceVertexCirculator(final HE_Face f) {
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
	return (_current == null) || (_current.getNextInFace() != _start);
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#next()
     */
    @Override
    public HE_Vertex next() {
	if (_current == null) {
	    _current = _start;
	} else {
	    _current = _current.getNextInFace();
	}
	return _current.getVertex();
    }

    /* (non-Javadoc)
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
	throw new UnsupportedOperationException();
    }
}