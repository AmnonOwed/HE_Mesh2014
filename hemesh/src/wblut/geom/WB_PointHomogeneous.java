/**
 * 
 */
package wblut.geom;

import wblut.WB_Epsilon;

/**
 * @author Frederik Vanhoutte, W:Blut
 */
public class WB_PointHomogeneous extends WB_Point4D {

	private boolean pointAtInfinity;

	public WB_PointHomogeneous() {
		x = y = z = 0;
		w = 0;
		pointAtInfinity = false;
	}

	public WB_PointHomogeneous(final double x, final double y, final double z,
			final double w) {
		this.x = w * x;
		this.y = w * y;
		this.z = w * z;
		this.w = w;
		pointAtInfinity = false;
	}

	public WB_PointHomogeneous(final double x, final double y, final double z,
			final double w, final boolean atInfinity) {
		if (atInfinity) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = 0;
		} else {
			this.x = w * x;
			this.y = w * y;
			this.z = w * z;
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	public WB_PointHomogeneous(final WB_PointHomogeneous v) {
		w = v.w;
		x = v.x;
		y = v.y;
		z = v.z;

		pointAtInfinity = v.pointAtInfinity;
	}

	public WB_PointHomogeneous get() {
		return new WB_PointHomogeneous(this);

	}

	public WB_PointHomogeneous(final WB_Coordinate v, final double w) {
		x = w * v.xd();
		y = w * v.yd();
		z = w * v.zd();
		this.w = w;
		pointAtInfinity = false;
	}

	public WB_PointHomogeneous(final WB_Coordinate v, final double w,
			final boolean atInfinity) {

		if (atInfinity) {
			x = v.xd();
			y = v.yd();
			z = v.zd();
			this.w = 0;
		} else {
			x = w * v.xd();
			y = w * v.yd();
			z = w * v.zd();
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	public void _set(final double x, final double y, final double z,
			final double w) {
		this.x = x * w;
		this.y = y * w;
		this.z = z * w;
		this.w = w;
		pointAtInfinity = false;
	}

	public void _set(final double x, final double y, final double z,
			final double w, final boolean atInfinity) {
		if (atInfinity) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.w = 0;

		} else {
			this.x = w * x;
			this.y = w * y;
			this.z = w * z;
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	public void _set(final WB_Coordinate v, final double w,
			final boolean atInfinity) {

		if (atInfinity) {
			x = v.xd();
			y = v.yd();
			z = v.zd();
			this.w = 0;

		} else {
			x = w * v.xd();
			y = w * v.yd();
			z = w * v.zd();
			this.w = w;
		}
		pointAtInfinity = atInfinity;

	}

	public void _set(final WB_PointHomogeneous p) {
		x = p.x;
		y = p.y;
		z = p.z;
		w = p.w;
		pointAtInfinity = p.pointAtInfinity;
	}

	public WB_Point project() {
		if (pointAtInfinity) {
			return new WB_Point(x, y, z);
		} else if (WB_Epsilon.isZero(w)) {
			return new WB_Point(0, 0, 0);
		}
		final double iw = 1.0 / w;
		return new WB_Point(x * iw, y * iw, z * iw);
	}

	public void setWeight(final double w) {
		final WB_Point p = project();
		_set(p, w, pointAtInfinity);

	}

	public static WB_PointHomogeneous interpolate(final WB_PointHomogeneous p0,
			final WB_PointHomogeneous p1, final double t) {
		return new WB_PointHomogeneous(p0.x + t * (p1.x - p0.x), p0.y + t
				* (p1.y - p0.y), p0.z + t * (p1.z - p0.z), p0.w + t
				* (p1.w - p0.w));

	}

	public boolean isInfinite() {
		return pointAtInfinity;
	}

}
