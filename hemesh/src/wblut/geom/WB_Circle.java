package wblut.geom;

import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_Circle implements WB_Geometry {
    WB_Point center;
    WB_Vector normal;
    double radius;
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    protected WB_Circle() {
	center = new WB_Point();
	radius = 0;
    }

    protected WB_Circle(final WB_Coordinate center, final double radius) {
	this.center = geometryfactory.createPoint(center);
	this.radius = WB_Math.fastAbs(radius);
	normal = geometryfactory.createVector(0, 0, 1);
    }

    protected WB_Circle(final WB_Coordinate center, final WB_Coordinate normal,
	    final double radius) {
	this.center = geometryfactory.createPoint(center);
	this.radius = WB_Math.fastAbs(radius);
	this.normal = geometryfactory.createNormalizedVector(normal);
    }

    protected WB_Circle(final double x, final double y, final double r) {
	center = new WB_Point(x, y);
	radius = r;
    }

    public double getRadius() {
	return radius;
    }

    public WB_Point getCenter() {
	return center;
    }

    public WB_Vector getNormal() {
	return normal;
    }

    @Override
    public boolean equals(final Object o) {
	if (o == this) {
	    return true;
	}
	if (!(o instanceof WB_Circle)) {
	    return false;
	}
	return WB_Epsilon.isEqualAbs(radius, ((WB_Circle) o).getRadius())
		&& center.equals(((WB_Circle) o).getCenter())
		&& normal.equals(((WB_Circle) o).getNormal());
    }

    @Override
    public int hashCode() {
	return 31 * center.hashCode() + hashCode(radius);
    }

    private int hashCode(final double v) {
	final long tmp = Double.doubleToLongBits(v);
	return (int) (tmp ^ (tmp >>> 32));
    }

    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.CIRCLE;
    }

    public boolean isTangent(final WB_Circle C) {
	final double d = center.getDistance3D(C.getCenter());
	return WB_Epsilon.isZero(d - WB_Math.fastAbs(C.getRadius() - radius))
		|| WB_Epsilon.isZero(d
			- WB_Math.fastAbs(C.getRadius() + radius));
    }

    @Override
    public WB_Circle apply(final WB_Transform T) {
	return geometryfactory.createCircleWithRadius(geometryfactory
		.createPoint(center).applyAsPoint(T), geometryfactory
		.createVector(normal).applyAsNormal(T), radius);
    }

    public WB_Circle applySelf(final WB_Transform T) {
	center.applyAsPoint(T);
	normal.applyAsNormal(T);
	return this;
    }

    public void set(final WB_Circle c) {
	center = c.getCenter();
	radius = c.getRadius();
    }

    public void setCenter(final double x, final double y) {
	center.set(x, y);
    }

    public void setCenter(final double x, final double y, final double z) {
	center.set(x, y, z);
    }

    public void setCenter(final WB_Coordinate c) {
	center.set(c);
    }

    public void setRadius(final double radius) {
	this.radius = radius;
    }
}
