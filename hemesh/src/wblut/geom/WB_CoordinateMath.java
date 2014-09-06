package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_Math;

public class WB_CoordinateMath {

	public static double angleBetween(final double cx, final double cy,
			final double cz, final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		final WB_Vector v0 = new WB_Vector(p1x - cx, p1y - cy, p1z - cz);
		final WB_Vector v1 = new WB_Vector(p2x - cx, p2y - cy, p2z - cz);
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

	public static double cosAngleBetween(final double cx, final double cy,
			final double cz, final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		final WB_Vector v0 = new WB_Vector(p1x - cx, p1y - cy, p1z - cz);
		final WB_Vector v1 = new WB_Vector(p2x - cx, p2y - cy, p2z - cz);
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

	public static double angleBetween(final double v1x, final double v1y,
			final double v1z, final double v2x, final double v2y,
			final double v2z) {
		final WB_Vector v0 = new WB_Vector(v1x, v1y, v1z);
		final WB_Vector v1 = new WB_Vector(v2x, v2y, v2z);
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

	public static double cosAngleBetween(final double v1x, final double v1y,
			final double v1z, final double v2x, final double v2y,
			final double v2z) {
		final WB_Vector v0 = new WB_Vector(v1x, v1y, v1z);
		final WB_Vector v1 = new WB_Vector(v2x, v2y, v2z);
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

	public static double angleBetweenNorm(final double cx, final double cy,
			final double cz, final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		final WB_Vector v0 = new WB_Vector(p1x - cx, p1y - cy, p1z - cz);
		final WB_Vector v1 = new WB_Vector(p2x - cx, p2y - cy, p2z - cz);
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return Math.acos(d);

	}

	public static double cosAngleBetweenNorm(final double cx, final double cy,
			final double cz, final double p1x, final double p1y,
			final double p1z, final double p2x, final double p2y,
			final double p2z) {
		final WB_Vector v0 = new WB_Vector(p1x - cx, p1y - cy, p1z - cz);
		final WB_Vector v1 = new WB_Vector(p2x - cx, p2y - cy, p2z - cz);
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return d;

	}

	public static double angleBetweenNorm(final double v1x, final double v1y,
			final double v1z, final double v2x, final double v2y,
			final double v2z) {
		final WB_Vector v0 = new WB_Vector(v1x, v1y, v1z);
		final WB_Vector v1 = new WB_Vector(v2x, v2y, v2z);
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return Math.acos(d);

	}

	public static double cosAngleBetweenNorm(final double v1x,
			final double v1y, final double v1z, final double v2x,
			final double v2y, final double v2z) {
		final WB_Vector v0 = new WB_Vector(v1x, v1y, v1z);
		final WB_Vector v1 = new WB_Vector(v2x, v2y, v2z);
		double d = v0.dot(v1);
		if (d < -1.0) {
			d = -1.0;
		}
		if (d > 1.0) {
			d = 1.0;
		}
		return d;

	}

	protected static int calculateHashCode(final double x, final double y,
			final double z) {
		int result = 17;

		final long a = Double.doubleToLongBits(x);
		result += 31 * result + (int) (a ^ (a >>> 32));

		final long b = Double.doubleToLongBits(y);
		result += 31 * result + (int) (b ^ (b >>> 32));

		final long c = Double.doubleToLongBits(z);
		result += 31 * result + (int) (c ^ (c >>> 32));

		return result;

	}

	public static double dot(final double x1, final double y1, final double z1,
			final double x2, final double y2, final double z2) {
		final double k0 = x1 * x2;
		final double k1 = y1 * y2;
		final double k2 = z1 * z2;

		final double exp0 = WB_Math.getExp(k0);
		final double exp1 = WB_Math.getExp(k1);
		final double exp2 = WB_Math.getExp(k2);
		if (exp0 < exp1) {
			if (exp0 < exp2) {
				return (k1 + k2) + k0;
			}
			else {
				return (k0 + k1) + k2;
			}
		}
		else {
			if (exp1 < exp2) {
				return (k0 + k2) + k1;
			}
			else {
				return (k0 + k1) + k2;
			}
		}

	}

	public static double dot2D(final double x1, final double y1,
			final double x2, final double y2) {
		return x1 * x2 + y1 * y2;
	}

	public static WB_Coordinate cross(final WB_Coordinate p,
			final WB_Coordinate q) {
		return new WB_Point(p.yd() * q.zd() - p.zd() * q.yd(), p.zd() * q.xd()
				- p.xd() * q.zd(), p.xd() * q.yd() - p.yd() * q.xd());
	}

	public static WB_Coordinate cross(final WB_Coordinate c,
			final WB_Coordinate p, final WB_Coordinate q) {
		final WB_Vector v1 = new WB_Vector(c, p);
		final WB_Vector v2 = new WB_Vector(c, q);
		return cross(v1, v2);
	}

	public static WB_Coordinate interpolate(final WB_Coordinate p0,
			final WB_Coordinate p1, final double t) {
		return new WB_Point(p0.xd() + t * (p1.xd() - p0.xd()), p0.yd() + t
				* (p1.yd() - p0.yd()), p0.zd() + t * (p1.zd() - p0.zd()));

	}

	public static double scalarTriple(final WB_Coordinate p,
			final WB_Coordinate q, final WB_Coordinate r) {
		final WB_Coordinate c = cross(q, r);
		return dot(p.xd(), p.yd(), p.zd(), c.xd(), c.yd(), c.zd());
	}

	public static WB_M33 tensor(final WB_Coordinate p, final WB_Coordinate q) {
		return new WB_M33(p.xd() * q.xd(), p.xd() * q.yd(), p.xd() * q.zd(),
				p.yd() * q.xd(), p.yd() * q.yd(), p.yd() * q.zd(), p.zd()
				* q.xd(), p.zd() * q.yd(), p.zd() * q.zd());
	}

	/**
	 * @deprecated Use {@link #getLength3D(double,double,double)} instead
	 */
	public static double getLength(final double x, final double y,
			final double z) {
				return getLength3D(x, y, z);
			}

	public static double getLength3D(final double x, final double y,
			final double z) {
		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * @deprecated Use {@link #getSqLength3D(double,double,double)} instead
	 */
	public static double getSqLength(final double x, final double y,
			final double z) {
				return getSqLength3D(x, y, z);
			}

	public static double getSqLength3D(final double x, final double y,
			final double z) {
		return x * x + y * y + z * z;
	}

	/**
	 * @deprecated Use {@link #getDistance3D(double,double,double,double,double,double)} instead
	 */
	public static double getDistance(final double x1, final double y1,
			final double z1, final double x2, final double y2, final double z2) {
				return getDistance3D(x1, y1, z1, x2, y2, z2);
			}

	public static double getDistance3D(final double x1, final double y1,
			final double z1, final double x2, final double y2, final double z2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)
				+ (z2 - z1) * (z2 - z1));
	}

	/**
	 * @deprecated Use {@link #getSqDistance3D(double,double,double,double,double,double)} instead
	 */
	public static double getSqDistance(final double x1, final double y1,
			final double z1, final double x2, final double y2, final double z2) {
				return getSqDistance3D(x1, y1, z1, x2, y2, z2);
			}

	public static double getSqDistance3D(final double x1, final double y1,
			final double z1, final double x2, final double y2, final double z2) {
		return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1) + (z2 - z1)
				* (z2 - z1);
	}

	public static double getLength2D(final double x, final double y) {
		return Math.sqrt(x * x + y * y);
	}

	public static double getSqLength2D(final double x, final double y) {
		return x * x + y * y;
	}

	public static double getDistance2D(final double x1, final double y1,
			final double x2, final double y2) {
		return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
	}

	public static double getSqDistance2D(final double x1, final double y1,
			final double x2, final double y2) {
		return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
	}

	public static boolean isZero(final double x, final double y, final double z) {
		return (getSqLength3D(x, y, z) < WB_Epsilon.SQEPSILON);
	}

}
