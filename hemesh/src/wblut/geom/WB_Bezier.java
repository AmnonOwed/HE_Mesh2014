/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Bernstein;

/**
 * 
 */
public class WB_Bezier implements WB_Curve {
    
    /**
     * 
     */
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    
    /**
     * 
     */
    protected WB_Point[] points;
    
    /**
     * 
     */
    protected int n;

    /**
     * 
     *
     * @param controlPoints 
     */
    public WB_Bezier(final WB_Point[] controlPoints) {
	points = controlPoints;
	n = points.length - 1;
    }

    /**
     * 
     *
     * @param controlPoints 
     */
    public WB_Bezier(final WB_PointHomogeneous[] controlPoints) {
	n = controlPoints.length - 1;
	points = new WB_Point[n + 1];
	for (int i = 0; i < (n + 1); i++) {
	    points[i] = new WB_Point(controlPoints[i].project());
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.nurbs.WB_Curve#curvePoint(double)
     */
    @Override
    public WB_Point curvePoint(final double u) {
	final double[] B = WB_Bernstein.getBernsteinCoefficientsOfOrderN(u, n);
	final WB_Point C = new WB_Point();
	for (int k = 0; k <= n; k++) {
	    C.addMulSelf(B[k], points[k]);
	}
	return C;
    }

    /**
     * 
     *
     * @return 
     */
    public double n() {
	return n;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point[] points() {
	return points;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.nurbs.WB_Curve#loweru()
     */
    @Override
    public double loweru() {
	return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.nurbs.WB_Curve#upperu()
     */
    @Override
    public double upperu() {
	return 1;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Bezier elevateDegree() {
	final WB_Point[] npoints = new WB_Point[n + 2];
	npoints[0] = points[0];
	npoints[n + 1] = points[n];
	final double inp = 1.0 / (n + 1);
	for (int i = 1; i <= n; i++) {
	    npoints[i] = gf.createInterpolatedPoint(points[i], points[i - 1], i
		    * inp);
	}
	return new WB_Bezier(npoints);
    }
}
