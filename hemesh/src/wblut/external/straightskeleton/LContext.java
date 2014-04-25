package wblut.external.straightskeleton;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Loopl context - an item and it's location in a lopp of loops
 * 
 * @author twak
 * @param <E>
 *            the type of item
 */
public class LContext<E> {

	public Loopable<E> loopable;
	public Loop<E> loop;
	public Object hook; // attachement for misc extensions

	public LContext(Loopable<E> loopable, Loop<E> loop) {
		this.loopable = loopable;
		this.loop = loop;
	}

	public E get() {
		return loopable.get();
	}
}
