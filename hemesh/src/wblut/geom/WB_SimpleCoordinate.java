package wblut.geom;

public class WB_SimpleCoordinate extends WB_AbstractVector {
    public WB_SimpleCoordinate() {
	super();
    }

    public WB_SimpleCoordinate(final double x, final double y) {
	super(x, y);
    }

    public WB_SimpleCoordinate(final double x, final double y, final double z) {
	super(x, y, z);
    }

    public WB_SimpleCoordinate(final double[] x) {
	super(x);
    }

    public WB_SimpleCoordinate(final double[] fromPoint, final double[] toPoint) {
	super(fromPoint, toPoint);
    }

    public WB_SimpleCoordinate(final WB_Coordinate v) {
	super(v);
    }
}
