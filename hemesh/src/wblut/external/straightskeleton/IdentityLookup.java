package wblut.external.straightskeleton;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Hashset doesn't let you get the item being looked up :(
 * 
 * @author twak
 */
public class IdentityLookup<E> {
	public Map<E, E> map = new LinkedHashMap<E, E>();

	public void put(E e) {
		map.put(e, e);
	}

	public E get(E e) {
		E out = map.get(e);
		if (out == null) {
			put(e);
			return e;
		} else
			return out;
	}

}
