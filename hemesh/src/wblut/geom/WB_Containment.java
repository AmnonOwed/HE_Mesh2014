package wblut.geom;

import java.util.LinkedList;

import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.interfaces.Triangle;
import wblut.math.WB_Epsilon;

public class WB_Containment {

	public static boolean contains(final WB_Coordinate p, final WB_AABBTree tree) {
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (contains(p, current.getAABB())) {
				if (current.isLeaf()) {
					return true;
				}
				else {
					if (current.getPosChild() != null) {
						queue.add(current.getPosChild());
					}
					if (current.getNegChild() != null) {
						queue.add(current.getNegChild());
					}
					if (current.getMidChild() != null) {
						queue.add(current.getMidChild());
					}
				}
			}

		}

		return false;
	}

	public static boolean contains(final WB_Coordinate p, final WB_AABB AABB) {
		return (p.xd() >= AABB.getMinX()) && (p.yd() >= AABB.getMinY())
				&& (p.zd() >= AABB.getMinZ()) && (p.xd() < AABB.getMaxX())
				&& (p.yd() < AABB.getMaxY()) && (p.zd() < AABB.getMaxZ());

	}

	public static boolean sameSide(final WB_Coordinate p1,
			final WB_Coordinate p2, final WB_Coordinate A, final WB_Coordinate B) {
		final WB_Point t1 = new WB_Point(B).subSelf(A);
		WB_Point t2 = new WB_Point(p1).subSelf(A);
		WB_Point t3 = new WB_Point(p2).subSelf(A);
		t2 = t1.cross(t2);
		t3 = t1.cross(t3);
		final double t = t2.dot(t3);
		if (t >= WB_Epsilon.EPSILON) {
			return true;
		}
		return false;
	}

	public static boolean contains(final WB_Coordinate p,
			final WB_Coordinate A, final WB_Coordinate B, final WB_Coordinate C) {
		if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistanceToLine3D(A, B, C))) {
			return false;
		}
		if (sameSide(p, A, B, C) && sameSide(p, B, A, C)
				&& sameSide(p, C, A, B)) {
			return true;
		}
		return false;
	}

	public static boolean contains(final WB_Coordinate p, final Triangle T) {
		return contains(p, T.p1(), T.p2(), T.p3());
	}

}
