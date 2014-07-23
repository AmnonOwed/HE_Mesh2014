package wblut.hemesh;

/**
 * Random Access Set of HE_Element
 * Combines advantages of an ArrayList - random access, sizeable -
 * with those of a HashMap - fast lookup, unique members -.
 */

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class HE_RAS<E extends HE_Element> extends AbstractSet<E> {

	public HE_RAS() {
	}

	public HE_RAS(final Collection<E> items) {

	}

	@Override
	public abstract boolean add(final E item);

	/**
	 * Override element at position <code>id</code> with last element.
	 *
	 * @param id
	 */
	public abstract E removeAt(final int id);

	public abstract boolean remove(final E item);

	public abstract E get(final int i);

	public abstract E getByIndex(final int i);

	public abstract E getByKey(final Long key);

	public abstract int getIndex(final E object);

	public abstract E pollRandom(final Random rnd);

	@Override
	public abstract int size();

	public abstract boolean contains(final E object);

	public abstract boolean containsKey(final Long key);

	@Override
	public abstract Iterator<E> iterator();

	public abstract List<E> getObjects();
}