package wblut.hemesh;

import java.util.ArrayList;
import java.util.Collection;

import wblut.geom.WB_KDTree;
import wblut.geom.WB_KDTree.WB_KDEntry;
import wblut.geom.WB_Point;

/**
 * Creates the Voronoi cells of a collection of points, constrained by a mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEMC_VoronoiCells extends HEMC_MultiCreator {

	/** Points. */
	private WB_Point[] points;

	/** Number of points. */
	private int numberOfPoints;

	/** Container. */
	private HE_Mesh container;

	/** Treat container as surface?. */
	private boolean surface;

	/** The simple cap. */
	private boolean simpleCap;

	/** Offset. */
	private double offset;

	/** The inner. */
	public HE_Selection[] inner;

	/** The outer. */
	public HE_Selection[] outer;

	/** Create divided skin of container. */
	private boolean createSkin;

	/** The limit. */
	public int limit;

	/**
	 * Instantiates a new HEMC_VoronoiCells.
	 *
	 */
	public HEMC_VoronoiCells() {
		super();
		simpleCap = true;
	}

	/**
	 * Set mesh, defines both points and container.
	 *
	 * @param mesh
	 *            HE_Mesh
	 * @param addCenter
	 *            add mesh center as extra point?
	 * @return self
	 */
	public HEMC_VoronoiCells setMesh(final HE_Mesh mesh, final boolean addCenter) {
		if (addCenter) {
			points = new WB_Point[mesh.getNumberOfVertices() + 1];
			final WB_Point[] tmp = mesh.getVerticesAsPoint();
			for (int i = 0; i < mesh.getNumberOfVertices(); i++) {
				points[i] = tmp[i];
			}
			points[mesh.getNumberOfVertices()] = mesh.getCenter();
		}
		else {
			points = mesh.getVerticesAsPoint();
		}
		container = mesh;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            array of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final WB_Point[] points) {
		this.points = points;
		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            collection of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final Collection<WB_Point> points) {
		final int n = points.size();
		this.points = new WB_Point[n];
		int i = 0;

		for (final WB_Point point : points) {
			this.points[i] = point;
			i++;
		}

		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            2D array of double of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final double[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set points that define cell centers.
	 *
	 * @param points
	 *            2D array of float of vertex positions
	 * @return self
	 */
	public HEMC_VoronoiCells setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}

		return this;
	}

	/**
	 * Set number of points.
	 *
	 * @param N
	 *            number of points
	 * @return self
	 */
	public HEMC_VoronoiCells setN(final int N) {
		numberOfPoints = N;
		return this;
	}

	/**
	 * Set voronoi cell offset.
	 *
	 * @param o
	 *            offset
	 * @return self
	 */
	public HEMC_VoronoiCells setOffset(final double o) {
		offset = o;
		return this;
	}

	/**
	 * Set enclosing mesh limiting cells.
	 *
	 * @param container
	 *            enclosing mesh
	 * @return self
	 */
	public HEMC_VoronoiCells setContainer(final HE_Mesh container) {
		this.container = container;
		return this;
	}

	/**
	 * Set optional surface mesh mode.
	 *
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEMC_VoronoiCells setSurface(final boolean b) {
		surface = b;
		return this;
	}

	/**
	 * Sets the simple cap.
	 *
	 * @param b
	 *            the b
	 * @return the hEM c_ voronoi cells
	 */
	public HEMC_VoronoiCells setSimpleCap(final boolean b) {
		simpleCap = b;
		return this;
	}

	/**
	 * Create skin mesh?.
	 *
	 * @param b
	 *            true, false
	 * @return self
	 */
	public HEMC_VoronoiCells setCreateSkin(final boolean b) {
		createSkin = b;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {
		HE_Mesh[] result;
		if (container == null) {
			result = new HE_Mesh[1];
			result[0] = new HE_Mesh();
			_numberOfMeshes = 0;
			return result;
		}
		if (points == null) {
			result = new HE_Mesh[1];
			result[0] = container;
			_numberOfMeshes = 1;
			return result;
		}
		if (numberOfPoints == 0) {
			numberOfPoints = points.length;
		}

		final ArrayList<HE_Mesh> lresult = new ArrayList<HE_Mesh>();
		final ArrayList<HE_Selection> linnersel = new ArrayList<HE_Selection>();
		final ArrayList<HE_Selection> loutersel = new ArrayList<HE_Selection>();
		final HEC_VoronoiCell cvc = new HEC_VoronoiCell();
		cvc.setPoints(points).setN(numberOfPoints).setContainer(container)
		.setSurface(surface).setOffset(offset).setSimpleCap(simpleCap);
		if (limit > 0) {
			final WB_KDTree<WB_Point, Integer> tree = new WB_KDTree<WB_Point, Integer>();
			for (int i = 0; i < numberOfPoints; i++) {
				tree.add(points[i], i);
			}

			for (int i = 0; i < numberOfPoints; i++) {
				cvc.setCellIndex(i);
				// System.out.println("HEMC_VoronoiCells: creating cell "
				// + (i + 1) + " of " + numberOfPoints + ".");
				final WB_KDEntry<WB_Point, Integer>[] closest = tree
						.getNearestNeighbors(points[i], limit);
				final ArrayList<Integer> indicesToUse = new ArrayList<Integer>();
				for (int j = 0; j < limit; j++) {
					indicesToUse.add(closest[j].value);
				}

				cvc.setLimitPoints(true).setPointsToUse(indicesToUse);

				final HE_Mesh mesh = cvc.createBase();
				linnersel.add(cvc.inner);
				loutersel.add(cvc.outer);
				lresult.add(mesh);
			}

		}
		else {
			for (int i = 0; i < numberOfPoints; i++) {
				cvc.setCellIndex(i);
				// System.out.println("HEMC_VoronoiCells: creating cell "
				// + (i + 1) + " of " + numberOfPoints + ".");
				final HE_Mesh mesh = cvc.createBase();
				linnersel.add(cvc.inner);
				loutersel.add(cvc.outer);
				lresult.add(mesh);
			}

		}

		result = new HE_Mesh[(createSkin) ? lresult.size() + 1 : lresult.size()];
		inner = new HE_Selection[lresult.size()];
		outer = new HE_Selection[lresult.size()];
		_numberOfMeshes = lresult.size();
		for (int i = 0; i < _numberOfMeshes; i++) {
			result[i] = lresult.get(i);
			inner[i] = linnersel.get(i);
			outer[i] = loutersel.get(i);
		}

		if (createSkin) {
			final boolean[] on = new boolean[_numberOfMeshes];
			for (int i = 0; i < _numberOfMeshes; i++) {
				on[i] = true;
			}

			result[_numberOfMeshes] = new HE_Mesh(new HEC_FromVoronoiCells()
			.setActive(on).setCells(result));
		}
		return result;

	}

}
