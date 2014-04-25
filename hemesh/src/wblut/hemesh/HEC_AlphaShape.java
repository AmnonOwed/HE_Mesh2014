package wblut.hemesh;

import java.util.Collection;
import java.util.Iterator;

import wblut.geom.WB_AlphaComplex;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 * Creates the convex hull of a collection of points.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEC_AlphaShape extends HEC_Creator {

	/** Points. */
	private WB_Point[] points;

	/** The jpoints. */
	private WB_Point[] jpoints;

	/** Number of points. */
	private int numberOfPoints;

	/** The alpha. */
	private double alpha;

	/** The ac. */
	private WB_AlphaComplex ac;

	/**
	 * Instantiates a new HEC_ConvexHull.
	 * 
	 */
	public HEC_AlphaShape() {
		super();
		ac = null;
		alpha = Double.POSITIVE_INFINITY;
		override = true;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            array of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final WB_Point[] points) {
		this.points = new WB_Point[points.length];
		for (int i = 0; i < points.length; i++) {
			this.points[i] = new WB_Point(points[i]);
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            array of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final HE_Vertex[] points) {
		this.points = new WB_Point[points.length];
		for (int i = 0; i < points.length; i++) {
			this.points[i] = new WB_Point(points[i]);
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            any Collection of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final Collection<? extends WB_Point> points) {

		this.points = new WB_Point[points.size()];

		int i = 0;
		for (WB_Point p : points) {
			this.points[i] = p.get();
			i++;
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            any Collection of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPointsFromVertices(
			final Collection<HE_Vertex> points) {

		this.points = new WB_Point[points.size()];
		final Iterator<HE_Vertex> itr = points.iterator();
		int i = 0;
		while (itr.hasNext()) {
			this.points[i] = itr.next().get().pos;
			i++;
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            2D array of double of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final double[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            2D array of float of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final float[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            2D array of float of vertex positions
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final int[][] points) {
		final int n = points.length;
		this.points = new WB_Point[n];

		for (int i = 0; i < n; i++) {
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
		}
		jpoints = this.points;
		ac = null;
		return this;
	}

	/**
	 * Sets the alpha.
	 * 
	 * @param a
	 *            the a
	 * @return the hE c_ alpha shape
	 */
	public HEC_AlphaShape setAlpha(final double a) {
		alpha = a;
		return this;
	}

	/**
	 * Gets the alpha.
	 * 
	 * @return the alpha
	 */
	public double getAlpha() {

		return alpha;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	public HE_Mesh createBase() {
		if (points == null) {
			return new HE_Mesh();
		}
		numberOfPoints = points.length;
		if (ac == null)
			ac = new WB_AlphaComplex(jpoints);
		final int[][] faceIndices = ac.getAlphaShapeFacelist(alpha);
		final HEC_FromFacelist ffl = new HEC_FromFacelist().setVertices(points)
				.setFaces(faceIndices).setDuplicate(false);
		final HE_Mesh result = ffl.createBase();

		result.cleanUnusedElementsByFace();
		return result;

	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            array of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final WB_Point[] points, double joggle) {
		this.points = new WB_Point[points.length];
		jpoints = new WB_Point[points.length];
		WB_Vector v;
		for (int i = 0; i < points.length; i++) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = points[i];
			jpoints[i] = points[i].get()._addSelf(v);
		}

		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            array of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final HE_Vertex[] points, double joggle) {
		this.points = new WB_Point[points.length];
		jpoints = new WB_Point[points.length];
		WB_Vector v;
		for (int i = 0; i < points.length; i++) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = new WB_Point(points[i]);
			jpoints[i] = points[i].get().pos._addSelf(v);
		}
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            any Collection of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPoints(
			final Collection<? extends WB_Point> points, double joggle) {

		this.points = new WB_Point[points.size()];
		jpoints = new WB_Point[points.size()];
		WB_Vector v;
		int i = 0;
		for (WB_Point p : points) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = p;
			jpoints[i] = p.get()._addSelf(v);
			i++;
		}
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            any Collection of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPointsFromVertices(
			final Collection<HE_Vertex> points, double joggle) {

		this.points = new WB_Point[points.size()];
		this.jpoints = new WB_Point[points.size()];
		final Iterator<HE_Vertex> itr = points.iterator();
		int i = 0;
		WB_Vector v;
		while (itr.hasNext()) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = itr.next().pos;
			jpoints[i] = this.points[i].get()._addSelf(v);
			i++;
		}
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            2D array of double of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final double[][] points, double joggle) {
		final int n = points.length;
		this.points = new WB_Point[n];
		this.jpoints = new WB_Point[n];
		WB_Vector v;
		for (int i = 0; i < n; i++) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
			jpoints[i] = this.points[i].get()._addSelf(v);
		}
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            2D array of float of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final float[][] points, double joggle) {
		final int n = points.length;
		this.points = new WB_Point[n];
		this.jpoints = new WB_Point[n];
		WB_Vector v;
		for (int i = 0; i < n; i++) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
			jpoints[i] = this.points[i].get()._addSelf(v);
		}
		ac = null;
		return this;
	}

	/**
	 * Set points that define vertices.
	 * 
	 * @param points
	 *            2D array of float of vertex positions
	 * @param joggle
	 *            the joggle
	 * @return self
	 */
	public HEC_AlphaShape setPoints(final int[][] points, double joggle) {
		final int n = points.length;
		this.points = new WB_Point[n];
		this.jpoints = new WB_Point[n];
		WB_Vector v;
		for (int i = 0; i < n; i++) {
			v = new WB_Vector(Math.random(), Math.random(), Math.random());
			v._subSelf(-0.5, -0.5, -0.5)._mulSelf(joggle);
			this.points[i] = new WB_Point(points[i][0], points[i][1],
					points[i][2]);
			jpoints[i] = this.points[i].get()._addSelf(v);
		}
		ac = null;
		return this;
	}

}
