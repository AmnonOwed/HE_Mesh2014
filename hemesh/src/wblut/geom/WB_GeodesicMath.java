package wblut.geom;

import wblut.WB_Epsilon;

public class WB_GeodesicMath {
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();

	public static class WB_GreatCircleIntersection {
		public double[] p0;
		public double[] p1;
		public double dihedral;

		public WB_GreatCircleIntersection(double[] p0, double[] p1,
				double dihedral) {
			this.p0 = p0;
			this.p1 = p1;
			this.dihedral = dihedral;
		}
	}

	public static WB_GreatCircleIntersection getGreatCircleIntersection(
			WB_Coordinate v1, WB_Coordinate v2, WB_Coordinate v3,
			WB_Coordinate v4) {
		WB_Point origin = gf.createPoint(0, 0, 0);
		WB_Vector r1 = vnor(v1, origin, v2);
		WB_Vector r2 = vnor(v3, origin, v4);
		WB_Vector r3 = vnor(r1, origin, r2);

		if (WB_Epsilon.isZeroSq(r3.getSqLength())) {
			return null;
		}
		r3._normalizeSelf();
		WB_Point p0 = gf.createPoint(r3);
		WB_Point p1 = p0.mul(-1);
		double dihedral = Math.acos(Math.abs(r1.dot(r2))
				/ (r1.getLength() * r2.getLength()));
		p0._addSelf(origin);
		p1._addSelf(origin);
		return new WB_GreatCircleIntersection(p0.coords(), p1.coords(),
				dihedral);
	}

	public static double[] getPointOnGreatCircle(WB_Coordinate v1,
			WB_Coordinate v2, double f) {
		WB_Point origin = gf.createPoint(0, 0, 0);
		double angle = Math.acos(vcos(v1, origin, v2));
		double isa = 1.0 / Math.sin(angle);
		double alpha = Math.sin((1.0 - f) * angle) * isa;
		double beta = Math.sin(f * angle) * isa;
		WB_Point r0 = new WB_Point(v1).mul(alpha);
		WB_Point r1 = new WB_Point(v2).mul(beta);

		return r0.add(r1).coords();

	}

	private static WB_Vector vnor(WB_Coordinate v1, WB_Coordinate v2,
			WB_Coordinate v3) {
		WB_Vector r0 = new WB_Vector(v2, v1);
		WB_Vector r1 = new WB_Vector(v2, v3);
		return r1.cross(r0);
	}

	private static double vcos(WB_Coordinate v1, WB_Coordinate v2,
			WB_Coordinate v3) {
		WB_Vector r0 = new WB_Vector(v2, v1);
		WB_Vector r1 = new WB_Vector(v2, v3);
		return r0.dot(r1) / (r0.getLength() * r1.getLength());
	}

}
