/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Bernstein;

/**
 * 
 */
public class WB_RBezier extends WB_Bezier {
    
    /**
     * 
     */
    private final double[] weights;
    
    /**
     * 
     */
    protected WB_PointHomogeneous[] wpoints;

    /**
     * 
     *
     * @param controlPoints 
     */
    public WB_RBezier(final WB_Point[] controlPoints) {
	super(controlPoints);
	weights = new double[n + 1];
	wpoints = new WB_PointHomogeneous[n + 1];
	for (int i = 0; i < (n + 1); i++) {
	    weights[i] = 1.0;
	    wpoints[i] = new WB_PointHomogeneous(points[i], weights[i]);
	}
    }

    /**
     * 
     *
     * @param controlPoints 
     */
    public WB_RBezier(final WB_PointHomogeneous[] controlPoints) {
	super(controlPoints);
	weights = new double[n + 1];
	for (int i = 0; i < (n + 1); i++) {
	    weights[i] = controlPoints[i].w;
	}
	wpoints = new WB_PointHomogeneous[n + 1];
	for (int i = 0; i < (n + 1); i++) {
	    wpoints[i] = new WB_PointHomogeneous(controlPoints[i]);
	}
    }

    /**
     * 
     *
     * @param controlPoints 
     * @param weights 
     */
    public WB_RBezier(final WB_Point[] controlPoints, final double[] weights) {
	super(controlPoints);
	this.weights = weights;
	wpoints = new WB_PointHomogeneous[n + 1];
	for (int i = 0; i < (n + 1); i++) {
	    wpoints[i] = new WB_PointHomogeneous(points[i], weights[i]);
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
	final WB_PointHomogeneous C = new WB_PointHomogeneous();
	for (int k = 0; k <= n; k++) {
	    C.add(wpoints[k], B[k]);
	}
	return new WB_Point(C.project());
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.geom.WB_Bezier#elevateDegree()
     */
    @Override
    public WB_RBezier elevateDegree() {
	final WB_PointHomogeneous[] npoints = new WB_PointHomogeneous[n + 2];
	npoints[0] = wpoints[0];
	npoints[n + 1] = wpoints[n];
	final double inp = 1.0 / (n + 1);
	for (int i = 1; i <= n; i++) {
	    npoints[i] = WB_PointHomogeneous.interpolate(wpoints[i],
		    wpoints[i - 1], i * inp);
	}
	return new WB_RBezier(npoints);
    }
}
