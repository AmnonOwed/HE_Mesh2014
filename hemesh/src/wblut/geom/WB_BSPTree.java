package wblut.geom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastTable;
import wblut.hemesh.HEMC_SplitMesh;
import wblut.hemesh.HE_Mesh;
import wblut.math.WB_Epsilon;

public class WB_BSPTree {

	private WB_BSPNode root;

	public WB_BSPTree() {
		root = null;
	}

	private void build(final WB_BSPNode tree, final List<WB_Polygon> polygons) {
		if (polygons.size() > 0) {
			WB_Polygon cpol = null;
			final Iterator<WB_Polygon> PItr = polygons.iterator();
			if (PItr.hasNext()) {
				cpol = PItr.next();
			}
			tree.partition = cpol.getPlane();
			final FastTable<WB_Polygon> _pols = new FastTable<WB_Polygon>();

			_pols.add(cpol);
			final FastTable<WB_Polygon> pos_list = new FastTable<WB_Polygon>();
			final FastTable<WB_Polygon> neg_list = new FastTable<WB_Polygon>();
			WB_Polygon pol = null;
			while (PItr.hasNext()) {
				pol = PItr.next();
				final WB_Classification result = tree.partition
						.classifyPolygonToPlane(pol);

				if (result == WB_Classification.FRONT) {
					pos_list.add(pol);
				}
				else if (result == WB_Classification.BACK) {
					neg_list.add(pol);
				}
				else if (result == WB_Classification.CROSSING) { /* spanning */

					final WB_Polygon[] split = pol.splitPolygon(tree.partition);
					final WB_Polygon frontPoly = split[0];
					final WB_Polygon backPoly = split[1];

					if (frontPoly.getNumberOfShellPoints() > 2) {
						pos_list.add(frontPoly);
					}
					if (backPoly.getNumberOfShellPoints() > 2) {
						neg_list.add(backPoly);
					}
				}
				else if (result == WB_Classification.ON) {
					_pols.add(pol);
				}
			}
			if (!pos_list.isEmpty()) {
				tree.pos = new WB_BSPNode();
				build(tree.pos, pos_list);
			}
			if (!neg_list.isEmpty()) {
				tree.neg = new WB_BSPNode();
				build(tree.neg, neg_list);
			}
			if (tree.polygons != null) {
				tree.polygons.clear();
			}
			tree.polygons.addAll(_pols);
		}
	}

	private void build(final WB_BSPNode tree, final WB_Polygon[] polygons) {
		if (polygons.length > 0) {
			final WB_Polygon cpol = polygons[0];

			tree.partition = cpol.getPlane();
			final FastTable<WB_Polygon> _pols = new FastTable<WB_Polygon>();

			_pols.add(cpol);
			final FastTable<WB_Polygon> pos_list = new FastTable<WB_Polygon>();
			final FastTable<WB_Polygon> neg_list = new FastTable<WB_Polygon>();
			WB_Polygon pol = null;
			for (int i = 1; i < polygons.length; i++) {
				pol = polygons[i];
				final WB_Classification result = tree.partition
						.classifyPolygonToPlane(pol);

				if (result == WB_Classification.FRONT) {
					pos_list.add(pol);
				}
				else if (result == WB_Classification.BACK) {
					neg_list.add(pol);
				}
				else if (result == WB_Classification.CROSSING) { /* spanning */
					final WB_Polygon[] split = pol.splitPolygon(tree.partition);
					final WB_Polygon frontPoly = split[0];
					final WB_Polygon backPoly = split[1];
					if (frontPoly.getNumberOfShellPoints() > 2) {
						pos_list.add(frontPoly);
					}
					if (backPoly.getNumberOfShellPoints() > 2) {
						neg_list.add(backPoly);
					}
				}
				else if (result == WB_Classification.ON) {
					_pols.add(pol);
				}
			}
			if (!pos_list.isEmpty()) {
				tree.pos = new WB_BSPNode();
				build(tree.pos, pos_list);
			}
			if (!neg_list.isEmpty()) {
				tree.neg = new WB_BSPNode();
				build(tree.neg, neg_list);
			}
			if (tree.polygons != null) {
				tree.polygons.clear();
			}
			tree.polygons.addAll(_pols);
		}
	}

	public void build(final List<WB_Polygon> polygons) {
		if (root == null) {
			root = new WB_BSPNode();
		}
		build(root, polygons);
	}

	public void build(final WB_Polygon[] polygons) {
		if (root == null) {
			root = new WB_BSPNode();
		}
		build(root, polygons);
	}

	public void build(final HE_Mesh mesh) {
		if (root == null) {
			root = new WB_BSPNode();
		}

		build(root, mesh.getPolygons());
	}

	public int pointLocation(final WB_Point p) {
		return pointLocation(root, p);

	}

	public int pointLocation(final double x, final double y, final double z) {
		return pointLocation(root, new WB_Point(x, y, z));

	}

	private int pointLocation(final WB_BSPNode node, final WB_Point p) {
		final WB_Classification type = node.partition.classifyPointToPlane(p);
		if (type == WB_Classification.FRONT) {
			if (node.pos != null) {
				return pointLocation(node.pos, p);
			}
			else {
				return 1;
			}
		}
		else if (type == WB_Classification.BACK) {
			if (node.neg != null) {
				return pointLocation(node.neg, p);
			}
			else {
				return -1;
			}
		}
		else {
			for (int i = 0; i < node.polygons.size(); i++) {
				if (WB_Epsilon.isZeroSq(WB_Distance.getSqDistance3D(p,
						node.polygons.get(i)))) {
					return 0;
				}
			}
			if (node.pos != null) {
				return pointLocation(node.pos, p);
			}
			else if (node.neg != null) {
				return pointLocation(node.neg, p);
			}
			else {
				return 0;

			}
		}
	}

	public void partitionPolygon(final WB_Polygon polygon,
			final List<WB_Polygon> pos, final List<WB_Polygon> neg,
			final List<WB_Polygon> coSame, final List<WB_Polygon> coDiff) {

		partitionPolygon(root, polygon, pos, neg, coSame, coDiff);

	}

	private void partitionPolygon(final WB_BSPNode node,
			final WB_Polygon polygon, final List<WB_Polygon> pos,
			final List<WB_Polygon> neg, final List<WB_Polygon> coSame,
			final List<WB_Polygon> coDiff) {

		final WB_Classification type = node.partition
				.classifyPolygonToPlane(polygon);

		if (type == WB_Classification.CROSSING) {

			final WB_Polygon[] split = polygon.splitPolygon(node.partition);
			final WB_Polygon frontPoly = split[0];
			final WB_Polygon backPoly = split[1];
			if (frontPoly.getNumberOfShellPoints() > 2) {
				getPolygonPosPartition(node, frontPoly, pos, neg, coSame,
						coDiff);
			}
			if (backPoly.getNumberOfShellPoints() > 2) {
				getPolygonNegPartition(node, backPoly, pos, neg, coSame, coDiff);
			}

		}
		else if (type == WB_Classification.FRONT) {
			getPolygonPosPartition(node, polygon, pos, neg, coSame, coDiff);

		}
		else if (type == WB_Classification.BACK) {
			getPolygonNegPartition(node, polygon, pos, neg, coSame, coDiff);

		}
		else if (type == WB_Classification.ON) {
			partitionCoincidentPolygons(node, polygon, pos, neg, coSame, coDiff);
		}

	}

	private void partitionCoincidentPolygons(final WB_BSPNode node,
			final WB_Polygon polygon, final List<WB_Polygon> pos,
			final List<WB_Polygon> neg, final List<WB_Polygon> coSame,
			final List<WB_Polygon> coDiff) {

		/*
		 * FastTable<WB_Polygon> partSegments = new FastTable<WB_Polygon>();
		 * partSegments.add(S); WB_Polygon thisS, otherS; final WB_Line2D L =
		 * node.partition; for (int i = 0; i < node.segments.size(); i++) {
		 * final FastTable<WB_Polygon> newpartSegments = new
		 * FastTable<WB_Polygon>(); otherS = node.segments.get(i); final double
		 * v0 = L.getT(otherS.origin()); final double v1 = L.getT(otherS.end());
		 * for (int j = 0; j < partSegments.size(); j++) { thisS =
		 * partSegments.get(j); final double u0 = L.getT(thisS.origin()); final
		 * double u1 = L.getT(thisS.end()); double[] intersection; if (u0 <= u1)
		 * { intersection = WB_Intersection2D.intervalIntersection(u0, u1, v0,
		 * v1); if (intersection[0] == 2) { final WB_XY pi =
		 * L.getPoint(intersection[1]); final WB_XY pj =
		 * L.getPoint(intersection[2]); if (u0 < intersection[1]) {
		 * newpartSegments.add(new WB_Polygon(thisS.origin(), pi)); }
		 * coSame.add(new WB_Polygon(pi, pj)); if (u1 > intersection[2]) {
		 * newpartSegments .add(new WB_Polygon(pj, thisS.end())); } } else {//
		 * this segment doesn't coincide with an edge
		 * newpartSegments.add(thisS); } } else { intersection =
		 * WB_Intersection2D.intervalIntersection(u1, u0, v0, v1); if
		 * (intersection[0] == 2) { final WB_XY pi =
		 * L.getPoint(intersection[1]); final WB_XY pj =
		 * L.getPoint(intersection[2]); if (u1 < intersection[1]) {
		 * newpartSegments .add(new WB_Polygon(pi, thisS.end())); }
		 * coDiff.add(new WB_Polygon(pj, pi)); if (u0 > intersection[2]) {
		 * newpartSegments.add(new WB_Polygon(thisS.origin(), pj)); } } else {
		 * newpartSegments.add(thisS); } } } partSegments = newpartSegments; }
		 * for (int i = 0; i < partSegments.size(); i++) {
		 * getSegmentPosPartition(node, partSegments.get(i), pos, neg, coSame,
		 * coDiff); getSegmentNegPartition(node, partSegments.get(i), pos, neg,
		 * coSame, coDiff); }
		 */
	}

	private void getPolygonPosPartition(final WB_BSPNode node,
			final WB_Polygon polygon, final List<WB_Polygon> pos,
			final List<WB_Polygon> neg, final List<WB_Polygon> coSame,
			final List<WB_Polygon> coDiff) {
		if (node.pos != null) {
			partitionPolygon(node.pos, polygon, pos, neg, coSame, coDiff);
		}
		else {
			pos.add(polygon);
		}

	}

	private void getPolygonNegPartition(final WB_BSPNode node,
			final WB_Polygon polygon, final List<WB_Polygon> pos,
			final List<WB_Polygon> neg, final List<WB_Polygon> coSame,
			final List<WB_Polygon> coDiff) {
		if (node.neg != null) {
			partitionPolygon(node.neg, polygon, pos, neg, coSame, coDiff);
		}
		else {
			neg.add(polygon);
		}
	}

	public void partitionMesh(final HE_Mesh mesh, final List<HE_Mesh> pos,
			final List<HE_Mesh> neg) {

		partitionMesh(root, mesh, pos, neg);

	}

	private void partitionMesh(final WB_BSPNode node, final HE_Mesh mesh,
			final List<HE_Mesh> pos, final List<HE_Mesh> neg) {

		final HEMC_SplitMesh sm = new HEMC_SplitMesh();
		sm.setMesh(mesh);
		sm.setPlane(node.partition);
		final HE_Mesh[] split = sm.create();

		if (split[0].getNumberOfVertices() > 4) {
			getMeshPosPartition(node, split[0], pos, neg);
		}
		if (split[0].getNumberOfVertices() > 4) {
			getMeshNegPartition(node, split[1], pos, neg);
		}

	}

	private void getMeshPosPartition(final WB_BSPNode node, final HE_Mesh mesh,
			final List<HE_Mesh> pos, final List<HE_Mesh> neg) {
		if (node.pos != null) {
			partitionMesh(node.pos, mesh, pos, neg);
		}
		else {
			pos.add(mesh);
		}

	}

	private void getMeshNegPartition(final WB_BSPNode node, final HE_Mesh mesh,
			final List<HE_Mesh> pos, final List<HE_Mesh> neg) {
		if (node.neg != null) {
			partitionMesh(node.neg, mesh, pos, neg);
		}
		else {
			neg.add(mesh);
		}
	}

	public ArrayList<WB_Polygon> toPolygons() {
		final ArrayList<WB_Polygon> polygons = new ArrayList<WB_Polygon>();
		addPolygons(root, polygons);
		return polygons;

	}

	private void addPolygons(final WB_BSPNode node,
			final ArrayList<WB_Polygon> polygons) {
		polygons.addAll(node.polygons);
		if (node.pos != null) {
			addPolygons(node.pos, polygons);
		}
		if (node.neg != null) {
			addPolygons(node.neg, polygons);
		}

	}

	public WB_BSPTree negate() {
		final WB_BSPTree negTree = new WB_BSPTree();
		negTree.root = negate(root);
		return negTree;
	}

	private WB_BSPNode negate(final WB_BSPNode node) {
		final WB_BSPNode negNode = new WB_BSPNode();
		negNode.partition = node.partition.get();
		negNode.partition.flipNormal();
		for (int i = 0; i < node.polygons.size(); i++) {
			final WB_Polygon pol = node.polygons.get(i);
			negNode.polygons.add(pol.negate());
		}
		if (node.pos != null) {
			negNode.neg = negate(node.pos);
		}
		if (node.neg != null) {
			negNode.pos = negate(node.neg);
		}
		return negNode;
	}
}
