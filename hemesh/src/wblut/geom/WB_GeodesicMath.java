/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Epsilon;

/**
 * 
 */
public class WB_GeodesicMath {
    
    /**
     * 
     */
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();

    /**
     * 
     */
    public static class WB_GreatCircleIntersection {
	
	/**
	 * 
	 */
	public double[] p0;
	
	/**
	 * 
	 */
	public double[] p1;
	
	/**
	 * 
	 */
	public double dihedral;

	/**
	 * 
	 *
	 * @param p0 
	 * @param p1 
	 * @param dihedral 
	 */
	public WB_GreatCircleIntersection(final double[] p0, final double[] p1,
		final double dihedral) {
	    this.p0 = p0;
	    this.p1 = p1;
	    this.dihedral = dihedral;
	}
    }

    /**
     * 
     *
     * @param v1 
     * @param v2 
     * @param v3 
     * @param v4 
     * @return 
     */
    public static WB_GreatCircleIntersection getGreatCircleIntersection(
	    final WB_Coordinate v1, final WB_Coordinate v2,
	    final WB_Coordinate v3, final WB_Coordinate v4) {
	final WB_Point origin = gf.createPoint(0, 0, 0);
	final WB_Vector r1 = vnor(v1, origin, v2);
	final WB_Vector r2 = vnor(v3, origin, v4);
	final WB_Vector r3 = vnor(r1, origin, r2);
	if (WB_Epsilon.isZeroSq(r3.getSqLength3D())) {
	    return null;
	}
	r3.normalizeSelf();
	final WB_Point p0 = gf.createPoint(r3);
	final WB_Point p1 = p0.mul(-1);
	final double dihedral = Math.acos(Math.abs(r1.dot(r2))
		/ (r1.getLength3D() * r2.getLength3D()));
	p0.addSelf(origin);
	p1.addSelf(origin);
	return new WB_GreatCircleIntersection(p0.coords(), p1.coords(),
		dihedral);
    }

    /**
     * 
     *
     * @param v1 
     * @param v2 
     * @param f 
     * @return 
     */
    public static double[] getPointOnGreatCircle(final WB_Coordinate v1,
	    final WB_Coordinate v2, final double f) {
	final WB_Point origin = gf.createPoint(0, 0, 0);
	final double angle = Math.acos(vcos(v1, origin, v2));
	final double isa = 1.0 / Math.sin(angle);
	final double alpha = Math.sin((1.0 - f) * angle) * isa;
	final double beta = Math.sin(f * angle) * isa;
	final WB_Point r0 = new WB_Point(v1).mul(alpha);
	final WB_Point r1 = new WB_Point(v2).mul(beta);
	return r0.add(r1).coords();
    }

    /**
     * 
     *
     * @param v1 
     * @param v2 
     * @param v3 
     * @return 
     */
    private static WB_Vector vnor(final WB_Coordinate v1,
	    final WB_Coordinate v2, final WB_Coordinate v3) {
	final WB_Vector r0 = new WB_Vector(v2, v1);
	final WB_Vector r1 = new WB_Vector(v2, v3);
	return r1.cross(r0);
    }

    /**
     * 
     *
     * @param v1 
     * @param v2 
     * @param v3 
     * @return 
     */
    private static double vcos(final WB_Coordinate v1, final WB_Coordinate v2,
	    final WB_Coordinate v3) {
	final WB_Vector r0 = new WB_Vector(v2, v1);
	final WB_Vector r1 = new WB_Vector(v2, v3);
	return r0.dot(r1) / (r0.getLength3D() * r1.getLength3D());
    }
}
