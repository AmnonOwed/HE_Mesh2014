package wblut.external.straightskeleton;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 *
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 *
 * Data type form mappings between two sets of sets.
 *
 * @author twak
 */
public class SetCorrespondence<A extends Object, B extends Object> {
	Map<A, Set<A>> whichA = new LinkedHashMap<A, Set<A>>();
	Map<B, Set<B>> whichB = new LinkedHashMap<B, Set<B>>();
	DHash<Set<A>, Set<B>> setToSet = new DHash<Set<A>, Set<B>>();

	/**
	 * If either a or b belongs to an existing set, both a and b collapse into
	 * that a/b set pair. Symmetrical operation.
	 *
	 * @param a
	 *            the A
	 * @param b
	 *            the B
	 */
	public void put(final A a, final B b) {
		Set<A> aSet = getSet(a, whichA);
		Set<B> bSet = getSet(b, whichB);

		final Set shouldBeA = setToSet.teg(bSet);
		final Set shouldBeB = setToSet.get(aSet);

		if (shouldBeA != null && shouldBeA != aSet) // existing B set already
		// references another A set
		{
			assert (aSet.size() == 1);
			shouldBeA.addAll(aSet);
			for (final A a2 : aSet) {
				whichA.put(a2, shouldBeA);
			}
			aSet = shouldBeA;
		}
		else {
			whichA.put(a, aSet); // either new set, or
		}

		if (shouldBeB != null && shouldBeB != bSet) // a references another b
		// (not bSet)
		{
			assert (bSet.size() == 1);
			shouldBeB.addAll(bSet);
			for (final B b2 : bSet) {
				whichB.put(b2, shouldBeB);
			}
			bSet = shouldBeB;
		}
		else {
			whichB.put(b, bSet);
		}

		setToSet.put(aSet, bSet);
	}

	public Set<B> getSetA(final A a) {
		final Set<A> aSet = getSet(a, whichA);
		final Set<B> bSet = setToSet.get(aSet);
		if (bSet == null) {
			return new HashSet();
		}
		return bSet;
	}

	public Set<A> getSetB(final B b) {
		final Set<B> bSet = getSet(b, whichB);
		final Set<A> aSet = setToSet.teg(bSet);
		if (aSet == null) {
			return new HashSet();
		}
		return aSet;
	}

	public Set getSet(final Object o, final Map set) {
		Set res = (Set) set.get(o);
		if (res == null) {
			res = new LinkedHashSet();
			res.add(o);
			set.put(o, res);
		}
		assert (res.contains(o));
		return res;
	}

	public void removeA(final A a) {
		final Set<A> aSet = getSet(a, whichA);
		aSet.remove(a);
		whichA.remove(a);
		if (aSet.isEmpty()) {
			setToSet.removeA(aSet);
		}
	}

	public Cache<A, Collection<B>> asCache() {
		return new Cache<A, Collection<B>>() {
			@Override
			public Collection<B> create(final A i) {
				return getSetA(i);
			}
		};
	}

	public class ConvertB<C> {
		SetCorrespondence<B, C> lookup;

		public ConvertB(final SetCorrespondence<B, C> lookup) {
			this.lookup = lookup;
		}

		public SetCorrespondence<A, C> convert() {
			final SetCorrespondence<A, C> out = new SetCorrespondence<A, C>();

			out.whichA = new LinkedHashMap<A, Set<A>>(whichA);
			out.whichB = new LinkedHashMap<C, Set<C>>(lookup.whichB);

			for (final Set<A> sa : setToSet.ab.keySet()) {
				for (final A a : sa) {
					for (final B b : getSetA(a)) {
						for (final C c : lookup.getSetA(b)) {
							out.put(a, c);
						}
					}
				}
			}

			return out;
		}
	}
}
