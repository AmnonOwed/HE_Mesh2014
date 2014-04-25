package wblut.geom;

import javolution.util.FastList;

public class WB_BSPNode2D {

	protected WB_Line2D partition;

	protected FastList<WB_Segment> segments;

	protected WB_BSPNode2D pos = null;

	protected WB_BSPNode2D neg = null;

	public WB_BSPNode2D() {
		segments = new FastList<WB_Segment>();
	}

}