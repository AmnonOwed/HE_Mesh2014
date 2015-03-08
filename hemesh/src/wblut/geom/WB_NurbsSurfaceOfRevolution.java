/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_NurbsSurfaceOfRevolution {
    
    /**
     * 
     *
     * @param C 
     * @param p 
     * @param axis 
     * @param theta 
     * @return 
     */
    public static WB_RBSplineSurface getSurfaceOfRevolution(final WB_BSpline C,
	    final WB_Coordinate p, final WB_Coordinate axis, double theta) {
	final WB_Vector v = new WB_Vector(axis);
	if (theta < 0) {
	    theta *= -1;
	    v.mulSelf(-1);
	}
	while (theta > 360) {
	    theta -= 360;
	}
	int narcs;
	final WB_Line L = new WB_Line(p, v);
	final double[] U;
	if (theta <= 90) {
	    narcs = 1;
	    U = new double[6];
	} else if (theta <= 180) {
	    narcs = 2;
	    U = new double[8];
	    U[3] = 0.5;
	    U[4] = 0.5;
	} else if (theta <= 270) {
	    U = new double[10];
	    narcs = 3;
	    U[3] = 1.0 / 3;
	    U[4] = U[3];
	    U[5] = 2.0 / 3;
	    U[6] = U[5];
	} else {
	    U = new double[12];
	    narcs = 4;
	    U[3] = 0.25;
	    U[4] = U[3];
	    U[5] = 0.5;
	    U[6] = U[5];
	    U[7] = 0.75;
	    U[8] = U[7];
	}
	final WB_Point[][] points = new WB_Point[1 + (2 * narcs)][C.n() + 1];
	final double[][] weights = new double[1 + (2 * narcs)][C.n() + 1];
	final double dtheta = ((theta / narcs) * Math.PI) / 180;
	int i = 0;
	int j = 3 + (2 * (narcs - 1));
	for (i = 0; i < 3; j++, i++) {
	    U[i] = 0;
	    U[j] = 1;
	}
	final double wm = Math.cos(dtheta * 0.5);
	double angle = 0;
	final double[] cosines = new double[narcs + 1];
	final double[] sines = new double[narcs + 1];
	for (i = 1; i <= narcs; i++) {
	    angle = angle + dtheta;
	    cosines[i] = Math.cos(angle);
	    sines[i] = Math.sin(angle);
	}
	for (j = 0; j <= C.n(); j++) {
	    final WB_Point O = WB_GeometryOp
		    .getClosestPoint3D(C.points()[j], L);
	    final WB_Vector X = C.points()[j].subToVector3D(O);
	    final double r = X.normalizeSelf();
	    final WB_Vector Y = new WB_Vector(v).crossSelf(X);
	    final WB_Point P0 = new WB_Point(C.points()[j]);
	    points[0][j] = new WB_Point(P0);
	    weights[0][j] = 1;
	    final WB_Vector T0 = new WB_Vector(Y);
	    int index = 0;
	    angle = 0.0;
	    for (i = 1; i <= narcs; i++) {
		final WB_Point P2 = new WB_Point(O);
		P2.addMulSelf(r * cosines[i], X);
		P2.addMulSelf(r * sines[i], Y);
		points[index + 2][j] = new WB_Point(P2);
		weights[index + 2][j] = 1;
		final WB_Vector T2 = Y.mul(cosines[i]);
		T2.addMulSelf(-sines[i], X);
		final WB_Line L1 = new WB_Line(P0, T0);
		final WB_Line L2 = new WB_Line(P2, T2);
		final WB_IntersectionResult is = WB_GeometryOp
			.getClosestPoint3D(L1, L2);
		final WB_Point p1 = (is.dimension == 0) ? (WB_Point) is.object
			: ((WB_Segment) is.object).getOrigin();
		points[index + 1][j] = p1;
		weights[index + 1][j] = wm;
		index = index + 2;
		if (i < narcs) {
		    P0.set(P2);
		    T0.set(T2);
		}
	    }
	}
	final WB_NurbsKnot UKnot = new WB_NurbsKnot(2, U);
	return new WB_RBSplineSurface(points, UKnot, C.knot(), weights);
    }

    /**
     * 
     *
     * @param C 
     * @param p 
     * @param axis 
     * @param theta 
     * @return 
     */
    public static WB_RBSplineSurface getSurfaceOfRevolution(
	    final WB_RBSpline C, final WB_Coordinate p,
	    final WB_Coordinate axis, double theta) {
	final WB_Vector v = new WB_Vector(axis);
	if (theta < 0) {
	    theta *= -1;
	    v.mulSelf(-1);
	}
	while (theta > 360) {
	    theta -= 360;
	}
	int narcs;
	final WB_Line L = new WB_Line(p, v);
	final double[] U;
	if (theta <= 90) {
	    narcs = 1;
	    U = new double[6];
	} else if (theta <= 180) {
	    narcs = 2;
	    U = new double[8];
	    U[3] = 0.5;
	    U[4] = 0.5;
	} else if (theta <= 270) {
	    U = new double[10];
	    narcs = 3;
	    U[3] = 1.0 / 3;
	    U[4] = U[3];
	    U[5] = 2.0 / 3;
	    U[6] = U[5];
	} else {
	    U = new double[12];
	    narcs = 4;
	    U[3] = 0.25;
	    U[4] = U[3];
	    U[5] = 0.5;
	    U[6] = U[5];
	    U[7] = 0.75;
	    U[8] = U[7];
	}
	final WB_Point[][] points = new WB_Point[1 + (2 * narcs)][C.n() + 1];
	final double[][] weights = new double[1 + (2 * narcs)][C.n() + 1];
	final double dtheta = ((theta / narcs) * Math.PI) / 180;
	int i = 0;
	int j = 3 + (2 * (narcs - 1));
	for (i = 0; i < 3; j++, i++) {
	    U[i] = 0;
	    U[j] = 1;
	}
	final double wm = Math.cos(dtheta * 0.5);
	double angle = 0;
	final double[] cosines = new double[narcs + 1];
	final double[] sines = new double[narcs + 1];
	for (i = 1; i <= narcs; i++) {
	    angle = angle + dtheta;
	    cosines[i] = Math.cos(angle);
	    sines[i] = Math.sin(angle);
	}
	for (j = 0; j <= C.n(); j++) {
	    final WB_Point O = WB_GeometryOp
		    .getClosestPoint3D(C.points()[j], L);
	    final WB_Vector X = C.points()[j].subToVector3D(O);
	    final double r = X.normalizeSelf();
	    final WB_Vector Y = new WB_Vector(v).crossSelf(X);
	    final WB_Point P0 = new WB_Point(C.points()[j]);
	    points[0][j] = new WB_Point(P0);
	    weights[0][j] = C.wpoints()[j].w;
	    final WB_Vector T0 = new WB_Vector(Y);
	    int index = 0;
	    angle = 0.0;
	    for (i = 1; i <= narcs; i++) {
		final WB_Point P2 = new WB_Point(O);
		P2.addMulSelf(r * cosines[i], X);
		P2.addMulSelf(r * sines[i], Y);
		points[index + 2][j] = new WB_Point(P2);
		weights[index + 2][j] = C.wpoints()[j].w;
		final WB_Vector T2 = Y.mul(cosines[i]);
		T2.addMulSelf(-sines[i], X);
		final WB_Line L1 = new WB_Line(P0, T0);
		final WB_Line L2 = new WB_Line(P2, T2);
		final WB_IntersectionResult is = WB_GeometryOp
			.getClosestPoint3D(L1, L2);
		final WB_Point p1 = (is.dimension == 0) ? (WB_Point) is.object
			: ((WB_Segment) is.object).getOrigin();
		points[index + 1][j] = p1;
		weights[index + 1][j] = wm * C.wpoints()[j].w;
		index = index + 2;
		if (i < narcs) {
		    P0.set(P2);
		    T0.set(T2);
		}
	    }
	}
	final WB_NurbsKnot UKnot = new WB_NurbsKnot(2, U);
	return new WB_RBSplineSurface(points, UKnot, C.knot(), weights);
    }
}
