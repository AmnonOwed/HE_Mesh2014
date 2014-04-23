package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import wblut.WB_Epsilon;

public class WB_VoronoiCell3D {
	WB_Point generator;
	int index;
	WB_Mesh cell;
	boolean open;
	boolean sliced;
	boolean[] onBoundary;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_VoronoiCell3D(final WB_Coordinate[] points,
			final WB_Point generator, final int index) {
		this.generator = generator;
		this.index = index;

		cell = geometryfactory.createConvexHull(points, false);
		onBoundary = new boolean[cell.getNumberOfVertices()];
	}

	public WB_VoronoiCell3D(final List<? extends WB_Coordinate> points,
			final WB_Point generator, final int index) {
		this.generator = generator;
		this.index = index;

		cell = geometryfactory.createConvexHull(points, false);
		onBoundary = new boolean[cell.getNumberOfVertices()];
	}

	public void constrain(final WB_AABB container) {
		final WB_AABB aabb = cell.getAABB();
		if (container.contains(aabb)) {
			return;
		}

		if (aabb.intersects(container)) {
			final double[] min = container._min;
			final double[] max = container._max;
			final WB_Point mmm = geometryfactory.createPoint(min[0], min[1],
					min[2]);
			final WB_Point Mmm = geometryfactory.createPoint(max[0], min[1],
					min[2]);
			final WB_Point mMm = geometryfactory.createPoint(min[0], max[1],
					min[2]);
			final WB_Point mmM = geometryfactory.createPoint(min[0], min[1],
					max[2]);
			final WB_Point MMM = geometryfactory.createPoint(max[0], max[1],
					max[2]);
			final WB_Point mMM = geometryfactory.createPoint(min[0], max[1],
					max[2]);
			final WB_Point MmM = geometryfactory.createPoint(max[0], min[1],
					max[2]);
			final WB_Point MMm = geometryfactory.createPoint(max[0], max[1],
					min[2]);
			final List<WB_Plane> planes = new ArrayList<WB_Plane>(6);
			planes.add(geometryfactory.createPlane(mmm, mMm, Mmm));
			planes.add(geometryfactory.createPlane(mmm, mmM, mMm));
			planes.add(geometryfactory.createPlane(mmm, Mmm, mmM));
			planes.add(geometryfactory.createPlane(MMM, mMM, MmM));
			planes.add(geometryfactory.createPlane(MMM, MMm, mMM));
			planes.add(geometryfactory.createPlane(MMM, MmM, MMm));
			constrain(planes);
		} else {
			cell = null;

		}

	}

	public void constrain(final WB_Mesh convexMesh, final double d) {
		constrain(convexMesh.getPlanes(d));
	}

	public void constrain(final WB_Mesh convexMesh) {
		constrain(convexMesh.getPlanes(0));
	}

	public void constrain(final Collection<? extends WB_Plane> planes) {
		for (final WB_Plane WB_Point : planes) {
			if (cell != null) {
				slice(WB_Point);
			}
		}
		if (cell != null) {
			onBoundary = new boolean[cell.getNumberOfVertices()];
			double d;
			WB_Point p;
			pointloop: for (int i = 0; i < cell.getNumberOfVertices(); i++) {
				p = cell.getVertex(i);
				for (final WB_Plane WB_Point : planes) {
					d = WB_Distance.distanceToPlane3D(p, WB_Point);
					if (WB_Epsilon.isZero(d)) {
						onBoundary[i] = true;
						continue pointloop;
					}
				}

			}

			final int hfl = cell.getNumberOfFaces();
			for (int i = hfl - 1; i > -1; i--) {
				final int[] face = cell.getFace(i);
				boolean boundary = true;
				for (int j = 0; j < face.length; j++) {
					if (!onBoundary[face[j]]) {
						boundary = false;
						break;
					}
				}
				if (boundary) {
					open = true;
				}
			}
		}
	}

	private void slice(final WB_Plane WB_Point) {

		final WB_Classification[] classifyPoints = ptsPlane(WB_Point);
		final List<WB_Point> newPoints = new ArrayList<WB_Point>();

		for (int i = 0; i < classifyPoints.length; i++) {
			if (classifyPoints[i] != WB_Classification.BACK) {
				newPoints.add(cell.getVertex(i));
			}
		}

		final int[][] edges = cell.getEdges();
		for (final int[] edge : edges) {
			if (((classifyPoints[edge[0]] == WB_Classification.BACK) && (classifyPoints[edge[1]] == WB_Classification.FRONT))
					|| ((classifyPoints[edge[1]] == WB_Classification.BACK) && (classifyPoints[edge[0]] == WB_Classification.FRONT))) {
				final WB_Point a = cell.getVertex(edge[0]);
				final WB_Point b = cell.getVertex(edge[1]);
				final WB_Vector ab = geometryfactory.createVector(a, b);
				final double t = (WB_Point.d() - WB_Point.getNormal().dot(a))
						/ WB_Point.getNormal().dot(ab);
				newPoints.add(a.addMul(t, ab));
				sliced = true;
			}
		}

		cell = geometryfactory.createConvexHull(newPoints, false);

	}

	private WB_Classification[] ptsPlane(final WB_Plane WB_Point) {
		final WB_Classification[] result = new WB_Classification[cell
				.getNumberOfVertices()];

		for (int i = 0; i < cell.getNumberOfVertices(); i++) {
			result[i] = WB_Classify.classifyPointToPlane(WB_Point,
					cell.getVertex(i));
		}
		return result;

	}

	public int getIndex() {
		return index;
	}

	public WB_Point getGenerator() {
		return generator;
	}

	public WB_Mesh getMesh() {
		return cell;
	}

	public boolean[] boundaryArray() {
		return onBoundary;
	}

	public boolean isOpen() {
		return open;
	}

	public boolean isSliced() {
		return sliced;
	}

}
