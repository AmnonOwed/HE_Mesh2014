package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javolution.util.FastList;
import wblut.hemesh.HEC_Geodesic;
import wblut.hemesh.HE_Mesh;
import wblut.math.WB_Math;
import wblut.math.WB_RandomSphere;

public class WB_Frame {

	private FastList<WB_FrameStrut> struts;

	private FastList<WB_FrameNode> nodes;

	public WB_Frame() {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
	}

	public WB_Frame(final WB_Point[] points,
			final WB_IndexedSegment[] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1(), connection.i2());
		}
	}

	public WB_Frame(final WB_Point[] points,
			final Collection<WB_IndexedSegment> connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1(), connection.i2());
		}
	}

	public void add(final WB_Point[] points,
			final Collection<WB_IndexedSegment> connections) {

		if (struts == null) {
			struts = new FastList<WB_FrameStrut>();
		}
		if (nodes == null) {
			nodes = new FastList<WB_FrameNode>();
		}
		final int nodeoffset = nodes.size();

		for (final WB_Point point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1() + nodeoffset, connection.i2() + nodeoffset);
		}
	}

	public void add(final WB_Frame frame) {

		if (struts == null) {
			struts = new FastList<WB_FrameStrut>();
		}
		if (nodes == null) {
			nodes = new FastList<WB_FrameNode>();
		}
		final int nodeoffset = nodes.size();

		for (final WB_FrameNode node : frame.nodes) {
			addNode(node, node.getValue());
		}
		for (final WB_IndexedSegment connection : frame.getIndexedSegments()) {
			addStrut(connection.i1() + nodeoffset, connection.i2() + nodeoffset);
		}
	}

	public WB_Frame(final Collection<WB_Point> points,
			final Collection<WB_IndexedSegment> connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point, 1);
		}
		for (final WB_IndexedSegment connection : connections) {
			addStrut(connection.i1(), connection.i2());
		}
	}

	public WB_Frame(final WB_Point[] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final Collection<WB_Point> points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final double[][] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final double[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final float[][] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final float[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final int[][] points, final int[][] connections) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final int[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
		for (final int[] connection : connections) {
			addStrut(connection[0], connection[1]);
		}
	}

	public WB_Frame(final WB_Point[] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
	}

	public WB_Frame(final Collection<WB_Point> points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final WB_Point point : points) {
			addNode(point.x, point.y, point.z, 1);
		}
	}

	public WB_Frame(final double[][] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final double[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}
	}

	public WB_Frame(final float[][] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final float[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}

	}

	public WB_Frame(final int[][] points) {
		struts = new FastList<WB_FrameStrut>();
		nodes = new FastList<WB_FrameNode>();
		for (final int[] point : points) {
			addNode(point[0], point[1], point[2], 1);
		}

	}

	public int addNode(final double x, final double y, final double z,
			final double v) {
		final int n = nodes.size();
		nodes.add(new WB_FrameNode(new WB_Point(x, y, z), n, v));
		return n;
	}

	public int addNode(final WB_Point pos, final double v) {
		final int n = nodes.size();
		nodes.add(new WB_FrameNode(pos, n, v));
		return n;
	}

	public void removeNode(final WB_FrameNode node) {
		for (final WB_FrameStrut strut : node.getStruts()) {
			removeStrut(strut);
		}
		nodes.remove(node);
	}

	public int addNodes(final Collection<WB_Point> pos) {
		int n = nodes.size();
		final Iterator<WB_Point> pItr = pos.iterator();
		while (pItr.hasNext()) {
			nodes.add(new WB_FrameNode(pItr.next(), n, 1));
			n++;
		}
		return n;
	}

	public int addStrut(final int i, final int j) {
		if (i == j) {
			throw new IllegalArgumentException(
					"Strut can't connect a node to itself: " + i + " " + j
							+ ".");
		}
		final int nn = nodes.size();
		if ((i < 0) || (j < 0) || (i >= nn) || (j >= nn)) {
			throw new IllegalArgumentException(
					"Strut indices outside node range.");
		}
		final int n = struts.size();
		WB_FrameStrut strut;
		if (i <= j) {
			strut = new WB_FrameStrut(nodes.get(i), nodes.get(j), n);
		} else {
			strut = new WB_FrameStrut(nodes.get(j), nodes.get(i), n);
		}
		if (!nodes.get(i).addStrut(strut)) {
			System.out.println("WB_Frame : Strut " + i + "-" + j
					+ " already added.");
		} else if (!nodes.get(j).addStrut(strut)) {
			System.out.println("WB_Frame : Strut " + i + "-" + j
					+ " already added.");
		} else {

			struts.add(strut);
		}
		return n;
	}

	public void removeStrut(final WB_FrameStrut strut) {
		nodes.get(strut.getStartIndex()).removeStrut(strut);
		nodes.get(strut.getEndIndex()).removeStrut(strut);
		struts.remove(strut);
	}

	public ArrayList<WB_FrameStrut> getStruts() {
		final ArrayList<WB_FrameStrut> result = new ArrayList<WB_FrameStrut>();
		result.addAll(struts);
		return result;
	}

	public ArrayList<WB_Segment> getSegments() {
		final ArrayList<WB_Segment> result = new ArrayList<WB_Segment>();
		for (final WB_FrameStrut strut : struts) {
			result.add(strut.toSegment());
		}
		return result;
	}

	public ArrayList<WB_IndexedSegment> getIndexedSegments() {
		final ArrayList<WB_Point> apoints = getPoints();
		WB_Point[] ipoints = new WB_Point[apoints.size()];
		ipoints = apoints.toArray(ipoints);
		final ArrayList<WB_IndexedSegment> result = new ArrayList<WB_IndexedSegment>();
		for (final WB_FrameStrut strut : struts) {
			result.add(new WB_IndexedSegment(strut.getStartIndex(), strut
					.getEndIndex(), ipoints));
		}
		return result;
	}

	public int getNumberOfStruts() {
		return struts.size();
	}

	public ArrayList<WB_FrameNode> getNodes() {
		final ArrayList<WB_FrameNode> result = new ArrayList<WB_FrameNode>();
		result.addAll(nodes);
		return result;
	}

	public ArrayList<WB_Point> getPoints() {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
		result.addAll(nodes);
		return result;
	}

	public WB_Point[] getPointsAsArray() {
		final ArrayList<WB_Point> result = new ArrayList<WB_Point>();
		result.addAll(nodes);
		final ArrayList<WB_Point> apoints = getPoints();
		final WB_Point[] ipoints = new WB_Point[apoints.size()];
		return apoints.toArray(ipoints);

	}

	public int getNumberOfNodes() {
		return nodes.size();
	}

	public WB_FrameNode getNode(final int i) {
		if ((i < 0) || (i >= nodes.size())) {
			throw new IllegalArgumentException("Index outside of node range.");
		}
		return nodes.get(i);

	}

	public WB_FrameStrut getStrut(final int i) {
		if ((i < 0) || (i >= struts.size())) {
			throw new IllegalArgumentException("Index outside of strut range.");
		}
		return struts.get(i);

	}

	public double getDistanceToFrame(final WB_Point p) {
		double d = Double.POSITIVE_INFINITY;
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_Segment S = new WB_Segment(strut.start(), strut.end());
			d = Math.min(d, WB_Distance3D.distance(p, S));
		}
		return d;
	}

	public int getClosestNodeOnFrame(final WB_Point p) {
		double mind = Double.POSITIVE_INFINITY;
		int q = -1;
		for (int i = 0; i < nodes.size(); i++) {

			final double d = WB_Distance3D.sqDistance(p, nodes.get(i));
			if (d < mind) {
				mind = d;
				q = i;
			}

		}
		return q;
	}

	public WB_Point getClosestPointOnFrame(final WB_Point p) {
		double mind = Double.POSITIVE_INFINITY;
		WB_Point q = new WB_Point(p);
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_Segment S = new WB_Segment(strut.start(), strut.end());

			final double d = WB_Distance3D.distance(p, S);
			if (d < mind) {
				mind = d;
				q = WB_Intersection.getClosestPoint(S, p);
			}

		}
		return q;
	}

	public double getDistanceToFrame(final double x, final double y,
			final double z) {
		double d = Double.POSITIVE_INFINITY;
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_Segment S = new WB_Segment(strut.start(), strut.end());
			d = Math.min(d, WB_Distance3D.distance(new WB_Point(x, y, z), S));
		}
		return d;
	}

	public WB_Point getClosestPointOnFrame(final double x, final double y,
			final double z) {
		double mind = Double.POSITIVE_INFINITY;
		WB_Point q = new WB_Point(x, y, z);
		for (int i = 0; i < struts.size(); i++) {
			final WB_FrameStrut strut = struts.get(i);
			final WB_Segment S = new WB_Segment(strut.start(), strut.end());

			final double d = WB_Distance3D.distance(new WB_Point(x, y, z), S);
			if (d < mind) {
				mind = d;
				q = WB_Intersection.getClosestPoint(S, new WB_Point(x, y, z));
			}

		}
		return q;
	}

	public WB_Frame smoothBiNodes() {
		final WB_Point[] newPos = new WB_Point[nodes.size()];
		int id = 0;
		for (final WB_FrameNode node : nodes) {
			if (node.getOrder() == 2) {
				newPos[id] = node.getNeighbor(0).add(node.getNeighbor(1));

				newPos[id]._mulSelf(0.5);
				newPos[id]._addSelf(node);
				newPos[id]._mulSelf(0.5);
			}
			id++;
		}
		id = 0;
		for (final WB_FrameNode node : nodes) {
			if (node.getOrder() == 2) {
				node._set(newPos[id]);
			}
			id++;
		}
		return this;
	}

	public WB_Frame refine(final double threshold) {
		final WB_Frame result = new WB_Frame();
		final FastList<WB_Point> npoints = new FastList<WB_Point>();
		for (final WB_FrameNode node : nodes) {
			result.addNode(node, node.getValue());
		}
		for (final WB_FrameStrut strut : struts) {
			if (strut.getLength() > threshold) {
				final WB_Point start = strut.start();
				final WB_Point end = strut.end();
				final WB_Point mid = WB_Point.interpolate(start, end, 0.5);
				result.addNode(mid, 0.5 * (strut.start().getValue() + strut
						.end().getValue()));
			}
		}
		final int n = getNumberOfNodes();
		int id = 0;

		for (final WB_FrameStrut strut : struts) {
			if (strut.getLength() > threshold) {
				final int start = strut.getStartIndex();
				final int end = strut.getEndIndex();
				result.addStrut(start, n + id);
				result.addStrut(n + id, end);
				id++;
			} else {
				final int start = strut.getStartIndex();
				final int end = strut.getEndIndex();
				result.addStrut(start, end);
			}
		}

		return result;

	}

	public List<WB_Point> toPointCloud(int n, double r, double d, int l,
			double rr, double dr) {
		List<WB_Point> points = new FastList<WB_Point>();

		double sl, dsl;
		int divs;
		WB_Plane P;
		WB_Vector u, localu, v;
		WB_Point offset;
		WB_Point p;
		WB_RandomSphere rnd = new WB_RandomSphere();
		double da = 2.0 * Math.PI / n;
		for (WB_FrameStrut strut : struts) {
			sl = strut.getLength() - 2 * rr;

			if (sl > 0) {
				divs = (int) WB_Math.max(1, Math.round(sl / d));
				dsl = sl / divs;
				P = strut.toPlane();
				u = P.getU().mul(r);
				v = strut.toNormVector().get();
				offset = strut.start().addMul(rr, v);
				v._mulSelf(dsl);
				for (int i = 0; i <= divs; i++) {
					for (int j = 0; j < n; j++) {
						p = strut.start().addMul(i, v);
						localu = u.get();
						localu.rotateAboutAxis(j * da, new WB_Point(),
								P.getNormal());
						p._addSelf(localu);
						p._addSelf(rnd.nextVector()._mulSelf(dr));
						points.add(p);
					}
				}
			}
		}
		for (WB_FrameNode node : nodes) {
			final HE_Mesh ball = new HE_Mesh(new HEC_Geodesic().setRadius(rr)
					.setLevel(l).setCenter(node));

			for (WB_Point q : ball.getVerticesAsPoint()) {
				points.add(q._addSelf(rnd.nextVector()._mulSelf(dr)));
			}

		}
		return points;

	}
}
