package wblut.geom;

import java.util.List;

import javolution.util.FastTable;
import wblut.hemesh.HE_Face;

public class WB_AABBNode<T extends WB_Coordinate> {

	protected int level;

	protected WB_AABB aabb;

	protected WB_AABBNode positive;

	protected WB_AABBNode negative;

	protected WB_AABBNode mid;

	protected WB_Plane separator;

	protected List<HE_Face> faces;

	protected boolean isLeaf;

	public WB_AABBNode() {
		level = -1;
		faces = new FastTable<HE_Face>();
	}

	public WB_AABB getAABB() {
		return aabb;
	}

	public WB_Plane getSeparator() {
		return separator;
	}

	public int getLevel() {
		return level;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public List<HE_Face> getFaces() {
		return faces;
	}

	public WB_AABBNode getPosChild() {
		return positive;

	}

	public WB_AABBNode getNegChild() {
		return negative;

	}

	public WB_AABBNode getMidChild() {
		return mid;

	}

}
