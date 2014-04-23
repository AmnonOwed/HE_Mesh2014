package wblut.external.straightskeleton;

import java.util.List;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * @author twak
 */
public class DirectionEvent extends MachineEvent {
	public double angle;

	public DirectionEvent(double angle, double height) {
		super(height);
		this.angle = angle;
	}

	public DirectionEvent() {
	}

	@Override
	public HeightEvent createHeightEvent(Machine m, List<Edge> edgesToChange) {
		return new DirectionHeightEvent(m, height, angle);
	}
}
