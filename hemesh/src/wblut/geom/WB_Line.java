package wblut.geom;

public class WB_Line extends WB_Linear {

	public static final WB_Line X() {
		return new WB_Line(0, 0, 0, 1, 0, 0);
	}

	public static final WB_Line Y() {
		return new WB_Line(0, 0, 0, 0, 1, 0);
	}

	public static final WB_Line Z() {
		return new WB_Line(0, 0, 0, 0, 0, 1);
	}

	public WB_Line() {
		super();

	}

	public WB_Line(final WB_Coordinate o, final WB_Coordinate d) {
		super(o, d);

	}

	public WB_Line(final double ox, final double oy, final double oz,
			final double dx, final double dy, final double dz) {
		super(new WB_Point(ox, oy, oz), new WB_Vector(dx, dy, dz));

	}

	/**
	 * a.x+b.y+c=0
	 * 
	 * @return a for a 2D line
	 */
	public double a() {
		return -direction.yd();
	}

	/**
	 * a.x+b.y+c=0
	 * 
	 * @return b for a 2D line
	 */
	public double b() {
		return direction.xd();
	}

	/**
	 * a.x+b.y+c=0
	 * 
	 * @return c for a 2D line
	 */
	public double c() {
		return origin.xd() * direction.yd() - origin.yd() * direction.xd();
	}

	@Override
	public String toString() {
		return "Line: " + origin.toString() + " " + direction.toString();
	}
}
