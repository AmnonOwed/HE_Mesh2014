package wblut.geom;

import javolution.util.FastTable;

public class WB_BSPNode {

	protected WB_Plane partition;

	protected FastTable<WB_Polygon> polygons;

	protected WB_BSPNode pos = null;

	protected WB_BSPNode neg = null;

	public WB_BSPNode() {
		polygons = new FastTable<WB_Polygon>();
	}

}