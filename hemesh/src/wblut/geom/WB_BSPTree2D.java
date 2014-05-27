package wblut.geom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;
import wblut.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_BSPTree2D {

	private WB_BSPNode2D root;

	public WB_BSPTree2D() {
		root = null;
	}

	private void build(final WB_BSPNode2D tree, final List<WB_Segment> segs) {
		WB_Segment cseg = null;
		final Iterator<WB_Segment> S2DItr = segs.iterator();
		if (S2DItr.hasNext()) {
			cseg = S2DItr.next();
		}
		tree.partition = new WB_Line2D(cseg.getOrigin(), cseg.getDirection());
		final FastList<WB_Segment> _segs = new FastList<WB_Segment>();

		_segs.add(cseg);
		final FastList<WB_Segment> pos_list = new FastList<WB_Segment>();
		final FastList<WB_Segment> neg_list = new FastList<WB_Segment>();
		WB_Segment seg = null;
		while (S2DItr.hasNext()) {
			seg = S2DItr.next();
			final WB_Classification result = tree.partition
					.classifySegmentToLine2D(seg);

			if (result == WB_Classification.FRONT) {
				pos_list.add(seg);
			} else if (result == WB_Classification.BACK) {
				neg_list.add(seg);
			} else if (result == WB_Classification.CROSSING) { /* spanning */
				final WB_Segment[] split_seg = WB_Intersection.splitSegment2D(
						seg, tree.partition);
				if (split_seg != null) {
					pos_list.add(split_seg[0]);
					neg_list.add(split_seg[1]);
				} else {

				}
			} else if (result == WB_Classification.ON) {
				_segs.add(seg);
			}
		}
		if (!pos_list.isEmpty()) {
			tree.pos = new WB_BSPNode2D();
			build(tree.pos, pos_list);
		}
		if (!neg_list.isEmpty()) {
			tree.neg = new WB_BSPNode2D();
			build(tree.neg, neg_list);
		}
		if (tree.segments != null) {
			tree.segments.clear();
		}
		tree.segments.addAll(_segs);
	}

	public void build(final List<WB_Segment> segments) {
		if (root == null) {
			root = new WB_BSPNode2D();
		}
		build(root, segments);
	}

	public void build(final WB_SimplePolygon2D poly) {
		if (root == null) {
			root = new WB_BSPNode2D();
		}
		build(root, poly.toExplicitSegments());
	}

	public int pointLocation(final WB_Point p) {
		return pointLocation(root, p);

	}

	public int pointLocation(final double x, final double y) {
		return pointLocation(root, new WB_Point(x, y));

	}

	private int pointLocation(final WB_BSPNode2D node, final WB_Point p) {
		final WB_Classification type = node.partition.classifyPointToLine2D(p);
		if (type == WB_Classification.FRONT) {
			if (node.pos != null) {
				return pointLocation(node.pos, p);
			} else {
				return 1;
			}
		} else if (type == WB_Classification.BACK) {
			if (node.neg != null) {
				return pointLocation(node.neg, p);
			} else {
				return -1;
			}
		} else {
			for (int i = 0; i < node.segments.size(); i++) {
				if (WB_Epsilon.isZero(WB_Distance.getDistance2D(p,
						node.segments.get(i)))) {
					return 0;
				}
			}
			if (node.pos != null) {
				return pointLocation(node.pos, p);
			} else if (node.neg != null) {
				return pointLocation(node.neg, p);
			} else {
				return 0;

			}
		}
	}

	public void partitionSegment(final WB_Segment S,
			final List<WB_Segment> pos, final List<WB_Segment> neg,
			final List<WB_Segment> coSame, final List<WB_Segment> coDiff) {

		partitionSegment(root, S, pos, neg, coSame, coDiff);

	}

	private void partitionSegment(final WB_BSPNode2D node, final WB_Segment S,
			final List<WB_Segment> pos, final List<WB_Segment> neg,
			final List<WB_Segment> coSame, final List<WB_Segment> coDiff) {

		final WB_Classification type = node.partition
				.classifySegmentToLine2D(S);

		if (type == WB_Classification.CROSSING) {
			final WB_Segment[] split = WB_Intersection.splitSegment2D(S,
					node.partition);
			if (split != null) {
				getSegmentPosPartition(node, split[0], pos, neg, coSame, coDiff);
				getSegmentNegPartition(node, split[1], pos, neg, coSame, coDiff);
			}
		} else if (type == WB_Classification.FRONT) {
			getSegmentPosPartition(node, S, pos, neg, coSame, coDiff);

		} else if (type == WB_Classification.BACK) {
			getSegmentNegPartition(node, S, pos, neg, coSame, coDiff);

		} else if (type == WB_Classification.ON) {
			partitionCoincidentSegments(node, S, pos, neg, coSame, coDiff);
		}

	}

	private void partitionCoincidentSegments(final WB_BSPNode2D node,
			final WB_Segment S, final List<WB_Segment> pos,
			final List<WB_Segment> neg, final List<WB_Segment> coSame,
			final List<WB_Segment> coDiff) {

		FastList<WB_Segment> partSegments = new FastList<WB_Segment>();
		partSegments.add(S);
		WB_Segment thisS, otherS;
		final WB_Line2D L = node.partition;
		for (int i = 0; i < node.segments.size(); i++) {
			final FastList<WB_Segment> newpartSegments = new FastList<WB_Segment>();
			otherS = node.segments.get(i);
			final double v0 = L.getT(otherS.getOrigin());
			final double v1 = L.getT(otherS.getEndpoint());

			for (int j = 0; j < partSegments.size(); j++) {
				thisS = partSegments.get(j);
				final double u0 = L.getT(thisS.getOrigin());
				final double u1 = L.getT(thisS.getEndpoint());
				double[] intersection;
				if (u0 < u1) {
					intersection = WB_Intersection.getIntervalIntersection2D(
							u0, u1, WB_Math.min(v0, v1), WB_Math.max(v0, v1));

					if (intersection[0] == 2) {
						final WB_Point pi = L.getPoint(intersection[1]);
						final WB_Point pj = L.getPoint(intersection[2]);
						if (u0 < intersection[1]) {
							newpartSegments.add(new WB_Segment(thisS
									.getOrigin(), pi));

						}
						coSame.add(new WB_Segment(pi, pj));
						if (u1 > intersection[2]) {
							newpartSegments.add(new WB_Segment(pj, thisS
									.getEndpoint()));
						}
					} else {// this segment doesn't coincide with an edge
						newpartSegments.add(thisS);
					}

				} else {
					intersection = WB_Intersection.getIntervalIntersection2D(
							u1, u0, WB_Math.min(v0, v1), WB_Math.max(v0, v1));

					if (intersection[0] == 2) {
						final WB_Point pi = L.getPoint(intersection[1]);
						final WB_Point pj = L.getPoint(intersection[2]);
						if (u1 < intersection[1]) {
							newpartSegments.add(new WB_Segment(pi, thisS
									.getEndpoint()));
						}
						coDiff.add(new WB_Segment(pj, pi));
						if (u0 > intersection[2]) {
							newpartSegments.add(new WB_Segment(thisS
									.getOrigin(), pj));
						}
					} else {
						newpartSegments.add(thisS);
					}
				}

			}
			partSegments = newpartSegments;
		}

		for (int i = 0; i < partSegments.size(); i++) {
			getSegmentPosPartition(node, partSegments.get(i), pos, neg, coSame,
					coDiff);
			getSegmentNegPartition(node, partSegments.get(i), pos, neg, coSame,
					coDiff);

		}

	}

	private void getSegmentPosPartition(final WB_BSPNode2D node,
			final WB_Segment S, final List<WB_Segment> pos,
			final List<WB_Segment> neg, final List<WB_Segment> coSame,
			final List<WB_Segment> coDiff) {
		if (node.pos != null) {
			partitionSegment(node.pos, S, pos, neg, coSame, coDiff);
		} else {
			pos.add(S);
		}

	}

	private void getSegmentNegPartition(final WB_BSPNode2D node,
			final WB_Segment S, final List<WB_Segment> pos,
			final List<WB_Segment> neg, final List<WB_Segment> coSame,
			final List<WB_Segment> coDiff) {
		if (node.neg != null) {
			partitionSegment(node.neg, S, pos, neg, coSame, coDiff);
		} else {
			neg.add(S);
		}
	}

	public ArrayList<WB_Segment> toSegments() {
		final ArrayList<WB_Segment> segments = new ArrayList<WB_Segment>();
		addSegments(root, segments);
		return segments;

	}

	private void addSegments(final WB_BSPNode2D node,
			final ArrayList<WB_Segment> segments) {
		segments.addAll(node.segments);
		if (node.pos != null) {
			addSegments(node.pos, segments);
		}
		if (node.neg != null) {
			addSegments(node.neg, segments);
		}

	}

	public WB_BSPTree2D negate() {
		final WB_BSPTree2D negTree = new WB_BSPTree2D();
		negTree.root = negate(root);
		return negTree;
	}

	private WB_BSPNode2D negate(final WB_BSPNode2D node) {
		final WB_BSPNode2D negNode = new WB_BSPNode2D();
		negNode.partition = new WB_Line2D(node.partition.getOrigin(),
				node.partition.getDirection().mul(-1));
		for (int i = 0; i < node.segments.size(); i++) {
			final WB_Segment seg = node.segments.get(i);
			negNode.segments.add(new WB_Segment(seg.getEndpoint(), seg
					.getOrigin()));
		}
		if (node.pos != null) {
			negNode.neg = negate(node.pos);
		}
		if (node.neg != null) {
			negNode.pos = negate(node.neg);
		}
		return negNode;
	}

	public void partitionPolygon(final WB_SimplePolygon2D P,
			final List<WB_SimplePolygon2D> pos,
			final List<WB_SimplePolygon2D> neg) {

		partitionPolygon(root, P, pos, neg);

	}

	private void partitionPolygon(final WB_BSPNode2D node,
			final WB_SimplePolygon2D P, final List<WB_SimplePolygon2D> pos,
			final List<WB_SimplePolygon2D> neg) {

		if (P.n > 2) {
			final WB_Classification type = node.partition
					.classifyPolygonToLine2D(P);

			if (type == WB_Classification.CROSSING) {
				final WB_SimplePolygon2D[] split = WB_Intersection
						.splitPolygon2D(P, node.partition);
				if (split[0].n > 2) {
					getPolygonPosPartition(node, split[0], pos, neg);
				}
				if (split[1].n > 2) {
					getPolygonNegPartition(node, split[1], pos, neg);
				}
			} else if (type == WB_Classification.FRONT) {
				getPolygonPosPartition(node, P, pos, neg);

			} else if (type == WB_Classification.BACK) {
				getPolygonNegPartition(node, P, pos, neg);

			}
		}

	}

	private void getPolygonPosPartition(final WB_BSPNode2D node,
			final WB_SimplePolygon2D P, final List<WB_SimplePolygon2D> pos,
			final List<WB_SimplePolygon2D> neg) {
		if (node.pos != null) {
			partitionPolygon(node.pos, P, pos, neg);
		} else {
			pos.add(P);
		}

	}

	private void getPolygonNegPartition(final WB_BSPNode2D node,
			final WB_SimplePolygon2D P, final List<WB_SimplePolygon2D> pos,
			final List<WB_SimplePolygon2D> neg) {
		if (node.neg != null) {
			partitionPolygon(node.neg, P, pos, neg);
		} else {
			neg.add(P);
		}
	}

}
