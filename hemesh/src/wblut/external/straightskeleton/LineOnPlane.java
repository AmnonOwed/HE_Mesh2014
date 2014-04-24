package wblut.external.straightskeleton;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * A result of an intersection that is really a line
 * 
 * @author twak
 */
public class LineOnPlane extends Point3d {
	public Tuple3d direction;
	public double distance;

	public LineOnPlane(Tuple3d start, Tuple3d direction, double distance) {
		super(start);
		this.direction = direction;
		this.distance = distance;
	}
}
