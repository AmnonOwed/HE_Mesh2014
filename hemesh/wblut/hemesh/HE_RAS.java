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
import java.util.Map;
import java.util.Random;

import javolution.util.FastList;
import javolution.util.FastMap;

public class HE_RAS<E extends HE_Element> extends AbstractSet<E> {
	List<E> objects;
	Map<Long, Integer> indices;

	public HE_RAS() {
		objects = new FastList<E>();
		indices = new FastMap<Long, Integer>();
	}

	public HE_RAS(int n) {
		objects = new FastList<E>(n);
		indices = new FastMap<Long, Integer>(n);
	}

	public HE_RAS(Collection<E> items) {
		objects = new FastList<E>(items.size());
		indices = new FastMap<Long, Integer>(items.size());
		for (E item : items) {
			indices.put(item._key, objects.size());
			objects.add(item);
		}
	}

	@Override
	public boolean add(E item) {
		if (indices.containsKey(item._key)) {
			return false;
		}
		indices.put(item._key, objects.size());
		objects.add(item);
		return true;
	}

	/**
	 * Override element at position <code>id</code> with last element.
	 * 
	 * @param id
	 */
	public E removeAt(int id) {
		if (id >= objects.size()) {
			return null;
		}
		E res = objects.get(id);
		indices.remove(res._key);
		E last = objects.remove(objects.size() - 1);
		// skip filling the hole if last is removed
		if (id < objects.size()) {
			indices.put(last._key, id);
			objects.set(id, last);
		}
		return res;
	}

	public boolean remove(E item) {
		@SuppressWarnings(value = "element-type-mismatch")
		Integer id = indices.get(item._key);
		if (id == null) {
			return false;
		}
		removeAt(id);
		return true;
	}

	public E get(int i) {
		return objects.get(i);
	}

	public E getByKey(Long key) {
		Integer i = indices.get(key);
		if (i == null)
			return null;
		return objects.get(i);
	}

	public int getIndex(E object) {
		Integer i = indices.get(object._key);
		if (i == null)
			return -1;
		return i;
	}

	public E pollRandom(Random rnd) {
		if (objects.isEmpty()) {
			return null;
		}
		int id = rnd.nextInt(objects.size());
		return removeAt(id);
	}

	@Override
	public int size() {
		return objects.size();
	}

	public boolean contains(E object) {
		return indices.containsKey(object._key);
	}

	public boolean containsKey(Long key) {
		return indices.containsKey(key);
	}

	@Override
	public Iterator<E> iterator() {
		return objects.iterator();
	}

	public List<E> getObjects() {
		return objects;
	}

}