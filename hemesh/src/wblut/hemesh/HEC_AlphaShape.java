/*
 *
 */
package wblut.hemesh;

import java.util.Collection;
import java.util.Iterator;
import wblut.geom.WB_AlphaComplex;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

/**
 *
 */
public class HEC_AlphaShape extends HEC_Creator {
    /**
     *
     */
    private WB_Point[] points;
    /**
     *
     */
    private WB_Point[] jpoints;
    /**
     *
     */
    private double alpha;
    /**
     *
     */
    private WB_AlphaComplex ac;

    /**
     *
     */
    public HEC_AlphaShape() {
	super();
	ac = null;
	alpha = Double.POSITIVE_INFINITY;
	override = true;
    }

    /**
     *
     *
     * @param points
     * @return
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
     *
     *
     * @param points
     * @return
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
     *
     *
     * @param points
     * @return
     */
    public HEC_AlphaShape setPoints(final Collection<? extends WB_Point> points) {
	this.points = new WB_Point[points.size()];
	int i = 0;
	for (final WB_Point p : points) {
	    this.points[i] = p.get();
	    i++;
	}
	jpoints = this.points;
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @return
     */
    public HEC_AlphaShape setPointsFromVertices(
	    final Collection<HE_Vertex> points) {
	this.points = new WB_Point[points.size()];
	final Iterator<HE_Vertex> itr = points.iterator();
	int i = 0;
	while (itr.hasNext()) {
	    this.points[i] = itr.next().get().getPoint();
	    i++;
	}
	jpoints = this.points;
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @return
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
     *
     *
     * @param points
     * @return
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
     *
     *
     * @param points
     * @return
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
     *
     *
     * @param a
     * @return
     */
    public HEC_AlphaShape setAlpha(final double a) {
	alpha = a;
	return this;
    }

    /**
     *
     *
     * @return
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
	if (ac == null) {
	    ac = new WB_AlphaComplex(jpoints);
	}
	final int[][] faceIndices = ac.getAlphaComplexShape(alpha);
	final HEC_FromFacelist ffl = new HEC_FromFacelist().setVertices(points)
		.setFaces(faceIndices).setDuplicate(false);
	final HE_Mesh result = ffl.createBase();
	result.cleanUnusedElementsByFace();
	return result;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPoints(final WB_Point[] points, final double joggle) {
	this.points = new WB_Point[points.length];
	jpoints = new WB_Point[points.length];
	WB_Vector v;
	for (int i = 0; i < points.length; i++) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = points[i];
	    jpoints[i] = points[i].get().addSelf(v);
	}
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPoints(final HE_Vertex[] points,
	    final double joggle) {
	this.points = new WB_Point[points.length];
	jpoints = new WB_Point[points.length];
	WB_Vector v;
	for (int i = 0; i < points.length; i++) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = new WB_Point(points[i]);
	    jpoints[i] = points[i].get().getPoint().addSelf(v);
	}
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPoints(
	    final Collection<? extends WB_Point> points, final double joggle) {
	this.points = new WB_Point[points.size()];
	jpoints = new WB_Point[points.size()];
	WB_Vector v;
	int i = 0;
	for (final WB_Point p : points) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = p;
	    jpoints[i] = p.get().addSelf(v);
	    i++;
	}
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPointsFromVertices(
	    final Collection<HE_Vertex> points, final double joggle) {
	this.points = new WB_Point[points.size()];
	this.jpoints = new WB_Point[points.size()];
	final Iterator<HE_Vertex> itr = points.iterator();
	int i = 0;
	WB_Vector v;
	while (itr.hasNext()) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = itr.next().getPoint();
	    jpoints[i] = this.points[i].get().addSelf(v);
	    i++;
	}
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPoints(final double[][] points, final double joggle) {
	final int n = points.length;
	this.points = new WB_Point[n];
	this.jpoints = new WB_Point[n];
	WB_Vector v;
	for (int i = 0; i < n; i++) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	    jpoints[i] = this.points[i].get().addSelf(v);
	}
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPoints(final float[][] points, final double joggle) {
	final int n = points.length;
	this.points = new WB_Point[n];
	this.jpoints = new WB_Point[n];
	WB_Vector v;
	for (int i = 0; i < n; i++) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	    jpoints[i] = this.points[i].get().addSelf(v);
	}
	ac = null;
	return this;
    }

    /**
     *
     *
     * @param points
     * @param joggle
     * @return
     */
    public HEC_AlphaShape setPoints(final int[][] points, final double joggle) {
	final int n = points.length;
	this.points = new WB_Point[n];
	this.jpoints = new WB_Point[n];
	WB_Vector v;
	for (int i = 0; i < n; i++) {
	    v = new WB_Vector(Math.random(), Math.random(), Math.random());
	    v.subSelf(-0.5, -0.5, -0.5).mulSelf(joggle);
	    this.points[i] = new WB_Point(points[i][0], points[i][1],
		    points[i][2]);
	    jpoints[i] = this.points[i].get().addSelf(v);
	}
	ac = null;
	return this;
    }
}
