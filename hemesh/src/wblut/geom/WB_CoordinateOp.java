/*
 * 
 */
package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 *
 * WB_CoordinateUtil contains convenience functions for doing calculations on
 * coordinates.
 *
 *
 */
public class WB_CoordinateOp {
    /**
     * Get angle in radians. Angle is defined by corner point c, and two end
     * points p and q. The angle is always in the range [-PI,PI].
     *
     * @param cx
     * @param cy
     * @param cz
     * @param px
     * @param py
     * @param pz
     * @param qx
     * @param qy
     * @param qz
     * @return angle
     */
    public static double angleBetween(final double cx, final double cy,
	    final double cz, final double px, final double py, final double pz,
	    final double qx, final double qy, final double qz) {
	final WB_Vector v0 = new WB_Vector(px - cx, py - cy, pz - cz);
	final WB_Vector v1 = new WB_Vector(qx - cx, qy - cy, qz - cz);
	v0.normalizeSelf();
	v1.normalizeSelf();
	double d = v0.dot(v1);
	if (d < -1.0) {
	    d = -1.0;
	}
	if (d > 1.0) {
	    d = 1.0;
	}
	return Math.acos(d);
    }

    /**
     *
     * Get cosine of angle. Angle is defined by corner point c, and two end
     * points p and q. The angle is always in the range [-PI,PI].
     *
     * @param cx
     * @param cy
     * @param cz
     * @param px
     * @param py
     * @param pz
     * @param qx
     * @param qy
     * @param qz
     * @return cosine of angle
     */
    public static double cosAngleBetween(final double cx, final double cy,
	    final double cz, final double px, final double py, final double pz,
	    final double qx, final double qy, final double qz) {
	final WB_Vector v0 = new WB_Vector(px - cx, py - cy, pz - cz);
	final WB_Vector v1 = new WB_Vector(qx - cx, qy - cy, qz - cz);
	v0.normalizeSelf();
	v1.normalizeSelf();
	double d = v0.dot(v1);
	if (d < -1.0) {
	    d = -1.0;
	}
	if (d > 1.0) {
	    d = 1.0;
	}
	return d;
    }

    /**
     *
     * Get angle in radians. Angle is defined by two non-normalized vectors u
     * and v. The angle is always in the range [-PI,PI].
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return angle
     */
    public static double angleBetween(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz) {
	final WB_Vector v0 = new WB_Vector(ux, uy, uz);
	final WB_Vector v1 = new WB_Vector(vx, vy, vz);
	v0.normalizeSelf();
	v1.normalizeSelf();
	double d = v0.dot(v1);
	if (d < -1.0) {
	    d = -1.0;
	}
	if (d > 1.0) {
	    d = 1.0;
	}
	return Math.acos(d);
    }

    /**
     *
     * Get cosine of angle. Angle is defined by two non-normalized vectors u and
     * v. The angle is always in the range [-PI,PI].
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return cosine of angle
     */
    public static double cosAngleBetween(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz) {
	final WB_Vector v0 = new WB_Vector(ux, uy, uz);
	final WB_Vector v1 = new WB_Vector(vx, vy, vz);
	v0.normalizeSelf();
	v1.normalizeSelf();
	double d = v0.dot(v1);
	if (d < -1.0) {
	    d = -1.0;
	}
	if (d > 1.0) {
	    d = 1.0;
	}
	return d;
    }

    /**
     *
     * Get angle in radians. Angle is defined by two normalized vectors u and v.
     * The angle is always in the range [-PI,PI].
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return angle
     */
    public static double angleBetweenNorm(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz) {
	final WB_Vector v0 = new WB_Vector(ux, uy, uz);
	final WB_Vector v1 = new WB_Vector(vx, vy, vz);
	double d = v0.dot(v1);
	if (d < -1.0) {
	    d = -1.0;
	}
	if (d > 1.0) {
	    d = 1.0;
	}
	return Math.acos(d);
    }

    /**
     *
     *
     * Get cosine of angle. Angle is defined by two normalized vectors u and v.
     * The angle is always in the range [-PI,PI].
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return cosine of angle
     */
    public static double cosAngleBetweenNorm(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz) {
	final WB_Vector v0 = new WB_Vector(ux, uy, uz);
	final WB_Vector v1 = new WB_Vector(vx, vy, vz);
	double d = v0.dot(v1);
	if (d < -1.0) {
	    d = -1.0;
	}
	if (d > 1.0) {
	    d = 1.0;
	}
	return d;
    }

    /**
     * Return hascode for a triplet of double coordinates.
     *
     * @param x 
     * @param y 
     * @param z 
     * @return hashcode
     */
    protected static int calculateHashCode(final double x, final double y,
	    final double z) {
	int result = 17;
	final long a = Double.doubleToLongBits(x);
	result += (31 * result) + (int) (a ^ (a >>> 32));
	final long b = Double.doubleToLongBits(y);
	result += (31 * result) + (int) (b ^ (b >>> 32));
	final long c = Double.doubleToLongBits(z);
	result += (31 * result) + (int) (c ^ (c >>> 32));
	return result;
    }

    /**
     *
     * Dot product of two 3D vectors. Components are sorted before addition for
     * greater robustness.
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return dot product
     */
    public static double dot(final double ux, final double uy, final double uz,
	    final double vx, final double vy, final double vz) {
	final double k0 = ux * vx;
	final double k1 = uy * vy;
	final double k2 = uz * vz;
	final double exp0 = WB_Math.getExp(k0);
	final double exp1 = WB_Math.getExp(k1);
	final double exp2 = WB_Math.getExp(k2);
	if (exp0 < exp1) {
	    if (exp0 < exp2) {
		return (k1 + k2) + k0;
	    } else {
		return (k0 + k1) + k2;
	    }
	} else {
	    if (exp1 < exp2) {
		return (k0 + k2) + k1;
	    } else {
		return (k0 + k1) + k2;
	    }
	}
    }

    /**
     *
     * Dot product of two 2D vectors.
     *
     * @param ux
     * @param uy
     * @param vx
     * @param vy
     * @return dot product
     */
    public static double dot2D(final double ux, final double uy,
	    final double vx, final double vy) {
	return (ux * vx) + (uy * vy);
    }

    /**
     *
     * Cross product of two vectors.
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return double[] with coordinates of cross product
     */
    public static double[] cross(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz) {
	return new double[] { (uy * vz) - (uz * vy), (uz * vx) - (ux * vz),
		(ux * vy) - (uy * vx) };
    }

    /**
     *
     * Cross product of two vectors given as three points: center point c, and
     * two end points p and q.
     *
     * @param cx
     * @param cy
     * @param cz
     * @param px
     * @param py
     * @param pz
     * @param qx
     * @param qy
     * @param qz
     * @return double[] with coordinates of cross product
     */
    public static double[] cross(final double cx, final double cy,
	    final double cz, final double px, final double py, final double pz,
	    final double qx, final double qy, final double qz) {
	return cross(px - cx, py - cy, pz - cz, qx - cx, qy - cy, qz - cz);
    }

    /**
     * Interpolated point: p + t*(q-p) or alternatively, (1-t)*p+t*q.
     *
     * @param px 
     * @param py 
     * @param pz 
     * @param qx 
     * @param qy 
     * @param qz 
     * @param t 
     * @return double[] with coordinates of interpolated point
     */
    public static double[] interpolate(final double px, final double py,
	    final double pz, final double qx, final double qy, final double qz,
	    final double t) {
	return new double[] { px + (t * (qx - px)), py + (t * (qy - py)),
		pz + (t * (qz - pz)) };
    }

    /**
     *
     * Scalar triple of three vectors: u.(v x w)
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @param wx
     * @param wy
     * @param wz
     * @return scalar triple
     */
    public static double scalarTriple(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz,
	    final double wx, final double wy, final double wz) {
	final double[] c = cross(vx, vy, vz, wx, wy, wz);
	return dot(ux, uy, uz, c[0], c[1], c[2]);
    }

    /**
     *
     * Tensor product of two 3D vectors.
     *
     * @param ux
     * @param uy
     * @param uz
     * @param vx
     * @param vy
     * @param vz
     * @return double[][] tensor as 3x3 array of double [row][col]
     */
    public static double[][] tensor3D(final double ux, final double uy,
	    final double uz, final double vx, final double vy, final double vz) {
	return new double[][] { { ux * vx, ux * vy, ux * vz },
		{ uy * vx, uy * vy, uy * vz }, { uz * vx, uz * vy, uz * vz } };
    }

    /**
     *
     * Length of 3D vector.
     *
     * @param ux
     * @param uy
     * @param uz
     * @return length
     */
    public static double getLength3D(final double ux, final double uy,
	    final double uz) {
	return Math.sqrt((ux * ux) + (uy * uy) + (uz * uz));
    }

    /**
     *
     * Square length of 3D vector.
     *
     * @param ux
     * @param uy
     * @param uz
     * @return square length
     */
    public static double getSqLength3D(final double ux, final double uy,
	    final double uz) {
	return (ux * ux) + (uy * uy) + (uz * uz);
    }

    /**
     *
     * Distance between two 3D points.
     *
     * @param px
     * @param py
     * @param pz
     * @param qx
     * @param qy
     * @param qz
     * @return distance
     */
    public static double getDistance3D(final double px, final double py,
	    final double pz, final double qx, final double qy, final double qz) {
	return Math.sqrt(((qx - px) * (qx - px)) + ((qy - py) * (qy - py))
		+ ((qz - pz) * (qz - pz)));
    }

    /**
     *
     * Square distance between two 3D points.
     *
     * @param px
     * @param py
     * @param pz
     * @param qx
     * @param qy
     * @param qz
     * @return square distance
     */
    public static double getSqDistance3D(final double px, final double py,
	    final double pz, final double qx, final double qy, final double qz) {
	return ((qx - px) * (qx - px)) + ((qy - py) * (qy - py))
		+ ((qz - pz) * (qz - pz));
    }

    /**
     *
     * Length of 2D vector.
     *
     * @param ux
     * @param uy
     * @return length
     */
    public static double getLength2D(final double ux, final double uy) {
	return Math.sqrt((ux * ux) + (uy * uy));
    }

    /**
     *
     * Square length of 2D vector.
     *
     * @param ux
     * @param uy
     * @return square length
     */
    public static double getSqLength2D(final double ux, final double uy) {
	return (ux * ux) + (uy * uy);
    }

    /**
     * Distance between two 2D points.
     *
     * @param px 
     * @param py 
     * @param qx 
     * @param qy 
     * @return distance
     */
    public static double getDistance2D(final double px, final double py,
	    final double qx, final double qy) {
	return Math.sqrt(((qx - px) * (qx - px)) + ((qy - py) * (qy - py)));
    }

    /**
     *
     * Square distance between two 2D points.
     *
     * @param px
     * @param py
     * @param qx
     * @param qy
     * @return square distance
     */
    public static double getSqDistance2D(final double px, final double py,
	    final double qx, final double qy) {
	return ((qx - px) * (qx - px)) + ((qy - py) * (qy - py));
    }

    /**
     *
     * Check if the square length of 3D vector is smaller than the SQEPSILON
     * tolerance defined in {@link wblut.math.WB_Epsilon#SQEPSILON}
     *
     * @param ux
     * @param uy
     * @param uz
     * @return
     */
    public static boolean isZero3D(final double ux, final double uy,
	    final double uz) {
	return (getSqLength3D(ux, uy, uz) < WB_Epsilon.SQEPSILON);
    }

    /**
     *
     * Check if the square length of 2D vector is smaller than the SQEPSILON
     * tolerance defined in {@link wblut.math.WB_Epsilon#SQEPSILON}
     *
     * @param ux
     * @param uy
     * @param uz
     * @return
     */
    public static boolean isZero2D(final double ux, final double uy,
	    final double uz) {
	return (getSqLength2D(ux, uy) < WB_Epsilon.SQEPSILON);
    }
}
