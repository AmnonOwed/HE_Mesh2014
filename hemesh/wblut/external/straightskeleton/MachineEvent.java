/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public abstract class MachineEvent implements Comparable<MachineEvent> {
	public double height;

	public MachineEvent(double height) {
		this.height = height;
	}

	public MachineEvent() {
	}

	public int compareTo(MachineEvent o) {
		return Double.compare(height, o.height);
	}

	public abstract HeightEvent createHeightEvent(Machine machine,
			List<Edge> edgesToChange);
}
