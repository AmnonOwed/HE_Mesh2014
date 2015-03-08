/*
 * 
 */
package wblut.hemesh;

import java.util.Iterator;

/**
 * 
 */
public class HE_FaceIterator implements Iterator<HE_Face> {
    
    /**
     * 
     */
    Iterator<HE_Face> _itr;

    /**
     * 
     *
     * @param mesh 
     */
    public HE_FaceIterator(final HE_MeshStructure mesh) {
	_itr = mesh.faces.iterator();
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
    public HE_Face next() {
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