package wblut.external.straightskeleton;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * A marker for a result that is out of bounds.
 * 
 * @author twak
 */
public class OOB extends Point3d {
	public OOB(Tuple3d wrap) {
		super(wrap);
	}
}
