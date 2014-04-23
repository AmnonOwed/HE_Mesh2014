package wblut.geom;

import java.util.LinkedList;

import wblut.WB_Epsilon;

public class WB_Containment {

	public static boolean contains(final WB_Point p, final WB_AABBTree tree) {
		final LinkedList<WB_AABBNode> queue = new LinkedList<WB_AABBNode>();
		queue.add(tree.getRoot());
		WB_AABBNode current;
		while (!queue.isEmpty()) {
			current = queue.pop();
			if (contains(p, current.getAABB())) {
				if (current.isLeaf()) {
					return true;
				} else {
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

	public static boolean contains(final WB_Point p, final WB_AABB AABB) {
		return (p.x >= AABB.getMinX()) && (p.y >= AABB.getMinY())
				&& (p.z >= AABB.getMinZ()) && (p.x < AABB.getMaxX())
				&& (p.y < AABB.getMaxY()) && (p.z < AABB.getMaxZ());

	}

	public static boolean sameSide(final WB_Point p1, final WB_Point p2,
			final WB_Point A, final WB_Point B) {
		final WB_Point t1 = B.get()._subSelf(A);
		WB_Point t2 = p1.get()._subSelf(A);
		WB_Point t3 = p2.get()._subSelf(A);
		t2 = t1.cross(t2);
		t3 = t1.cross(t3);
		final double t = t2.dot(t3);
		if (t >= WB_Epsilon.EPSILON) {
			return true;
		}
		return false;
	}

	public static boolean contains(final WB_Point p, final WB_Point A,
			final WB_Point B, final WB_Point C) {
		if (WB_Epsilon.isZeroSq(WB_Distance3D.sqDistanceToLine(A, B, C))) {
			return false;
		}
		if (sameSide(p, A, B, C) && sameSide(p, B, A, C)
				&& sameSide(p, C, A, B)) {
			return true;
		}
		return false;
	}

	public static boolean contains(final WB_Point p, final Triangle T) {
		return contains(p, T.p1(), T.p2(), T.p3());
	}

}
