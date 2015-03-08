/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_NurbsSwungSurface {
    
    /**
     * 
     *
     * @param xzprofile 
     * @param xytrajectory 
     * @param alpha 
     * @return 
     */
    public static WB_BSplineSurface getSwungSurface(final WB_BSpline xzprofile,
	    final WB_BSpline xytrajectory, final double alpha) {
	final int n = xzprofile.n();
	final int m = xytrajectory.n();
	final WB_Point[][] points = new WB_Point[n + 1][m + 1];
	for (int i = 0; i <= n; i++) {
	    for (int j = 0; j <= m; j++) {
		points[i][j] = new WB_Point(alpha * xzprofile.points()[i].xd()
			* xytrajectory.points()[j].xd(), alpha
			* xzprofile.points()[i].xd()
			* xytrajectory.points()[j].yd(),
			xzprofile.points()[i].zd());
	    }
	}
	return new WB_BSplineSurface(points, xzprofile.knot(),
		xytrajectory.knot());
    }

    /**
     * 
     *
     * @param xzprofile 
     * @param xytrajectory 
     * @param alpha 
     * @return 
     */
    public static WB_RBSplineSurface getSwungSurface(
	    final WB_RBSpline xzprofile, final WB_RBSpline xytrajectory,
	    final double alpha) {
	final int n = xzprofile.n();
	final int m = xytrajectory.n();
	final WB_Point[][] points = new WB_Point[n + 1][m + 1];
	final double[][] weights = new double[n + 1][m + 1];
	for (int i = 0; i <= n; i++) {
	    for (int j = 0; j <= m; j++) {
		points[i][j] = new WB_Point(alpha * xzprofile.points()[i].xd()
			* xytrajectory.points()[j].xd(), alpha
			* xzprofile.points()[i].xd()
			* xytrajectory.points()[j].yd(),
			xzprofile.points()[i].zd());
		weights[i][j] = xzprofile.weights()[i]
			* xytrajectory.weights()[j];
	    }
	}
	return new WB_RBSplineSurface(points, xzprofile.knot(),
		xytrajectory.knot(), weights);
    }

    /**
     * 
     *
     * @param xzprofile 
     * @param xytrajectory 
     * @param alpha 
     * @return 
     */
    public static WB_RBSplineSurface getSwungSurface(
	    final WB_BSpline xzprofile, final WB_RBSpline xytrajectory,
	    final double alpha) {
	final int n = xzprofile.n();
	final int m = xytrajectory.n();
	final WB_Point[][] points = new WB_Point[n + 1][m + 1];
	final double[][] weights = new double[n + 1][m + 1];
	for (int i = 0; i <= n; i++) {
	    for (int j = 0; j <= m; j++) {
		points[i][j] = new WB_Point(alpha * xzprofile.points()[i].xd()
			* xytrajectory.points()[j].xd(), alpha
			* xzprofile.points()[i].xd()
			* xytrajectory.points()[j].yd(),
			xzprofile.points()[i].zd());
		weights[i][j] = xytrajectory.weights()[j];
	    }
	}
	return new WB_RBSplineSurface(points, xzprofile.knot(),
		xytrajectory.knot(), weights);
    }

    /**
     * 
     *
     * @param xzprofile 
     * @param xytrajectory 
     * @param alpha 
     * @return 
     */
    public static WB_RBSplineSurface getSwungSurface(
	    final WB_RBSpline xzprofile, final WB_BSpline xytrajectory,
	    final double alpha) {
	final int n = xzprofile.n();
	final int m = xytrajectory.n();
	final WB_Point[][] points = new WB_Point[n + 1][m + 1];
	final double[][] weights = new double[n + 1][m + 1];
	for (int i = 0; i <= n; i++) {
	    for (int j = 0; j <= m; j++) {
		points[i][j] = new WB_Point(alpha * xzprofile.points()[i].xd()
			* xytrajectory.points()[j].xd(), alpha
			* xzprofile.points()[i].xd()
			* xytrajectory.points()[j].yd(),
			xzprofile.points()[i].zd());
		weights[i][j] = xzprofile.weights()[i];
	    }
	}
	return new WB_RBSplineSurface(points, xzprofile.knot(),
		xytrajectory.knot(), weights);
    }
}