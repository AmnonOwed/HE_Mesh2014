package wblut.hemesh;

/**
 * Random Access Set of HE_Element
 * Combines advantages of an ArrayList - random access, sizeable -
 * with those of a HashMap - fast lookup, unique members -.
 */
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javolution.util.FastMap;
import javolution.util.FastSortedMap;
import javolution.util.FastTable;

public class HE_RASJavolution<E extends HE_Element> extends HE_RAS<E> {
    List<E> objects;
    FastMap<Long, Integer> indices;// TLongIntMap indices;

    public HE_RASJavolution() {
	objects = new FastTable<E>();
	indices = new FastSortedMap<Long, Integer>(); // TLongIntHashMap(10,
	// 0.5f,
	// -1L, -1);
    }

    public HE_RASJavolution(final int n) {
	this();
    }

    public HE_RASJavolution(final Collection<E> items) {
	this();
	for (final E e : items) {
	    add(e);
	}
    }

    @Override
    public boolean add(final E item) {
	if (item == null) {
	    return false;
	}
	if (indices.putIfAbsent(item._key, objects.size()) == null) {
	    objects.add(item);
	    return true;
	}
	return false;
    }

    /**
     * Override element at position <code>id</code> with last element.
     *
     * @param id
     */
    @Override
    public E removeAt(final int id) {
	if (id >= objects.size()) {
	    return null;
	}
	final E res = objects.get(id);
	res.clear();
	indices.remove(res._key);
	final E last = objects.remove(objects.size() - 1);
	// skip filling the hole if last is removed
	if (id < objects.size()) {
	    indices.put(last._key, id);
	    objects.set(id, last);
	}
	return res;
    }

    @Override
    public boolean remove(final E item) {
	if (item == null) {
	    return false;
	}
	// @SuppressWarnings(value = "element-type-mismatch")
	final Integer retrieval = indices.get(item._key);
	final int id = (retrieval == null) ? -1 : retrieval;
	if (id == -1) {
	    return false;
	}
	item.clear();
	removeAt(id);
	return true;
    }

    @Override
    public E get(final int i) {
	return objects.get(i);
    }

    @Override
    public E getByIndex(final int i) {
	return objects.get(i);
    }

    @Override
    public E getByKey(final Long key) {
	final Integer retrieval = indices.get(key);
	;
	final int i = (retrieval == null) ? -1 : retrieval;
	if (i == -1) {
	    return null;
	}
	return objects.get(i);
    }

    @Override
    public int getIndex(final E object) {
	return indices.get(object._key);
    }

    @Override
    public E pollRandom(final Random rnd) {
	if (objects.isEmpty()) {
	    return null;
	}
	final int id = rnd.nextInt(objects.size());
	return removeAt(id);
    }

    @Override
    public int size() {
	return objects.size();
    }

    @Override
    public boolean contains(final E object) {
	if (object == null) {
	    return false;
	}
	return indices.containsKey(object._key);
    }

    @Override
    public boolean containsKey(final Long key) {
	return indices.containsKey(key);
    }

    @Override
    public Iterator<E> iterator() {
	return objects.iterator();
    }

    @Override
    public List<E> getObjects() {
	return objects;
    }
}