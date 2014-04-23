package wblut.geom;

public class WB_Ray extends WB_Linear {

	public static WB_Ray X() {
		return new WB_Ray(0, 0, 0, 1, 0, 0);
	}

	public static WB_Ray Y() {
		return new WB_Ray(0, 0, 0, 0, 1, 0);
	}

	public static WB_Ray Z() {
		return new WB_Ray(0, 0, 0, 0, 0, 1);
	}

	public WB_Ray() {
		origin = new WB_Point();
		direction = new WB_Vector(1, 0, 0);

	}

	public WB_Ray(final WB_Coordinate o, final WB_Coordinate d) {
		origin = new WB_Point(o);
		direction = new WB_Vector(d);
		direction._normalizeSelf();
	}

	public WB_Ray(final double ox, final double oy, final double oz,
			final double dx, final double dy, final double dz) {
		origin = new WB_Point(ox, oy, oz);
		direction = new WB_Vector(dx, dy, dz);
		direction._normalizeSelf();
	}

	@Override
	public String toString() {
		return "Ray: " + origin.toString() + " " + direction.toString();
	}

}