/*
 * Copyright (c) 2010, Frederik Vanhoutte This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * http://creativecommons.org/licenses/LGPL/2.1/ This library is distributed in
 * the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package wblut.geom;

import java.util.ArrayList;

import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

public class WB_Circle implements WB_Geometry {

	WB_Point center;

	WB_Vector normal;

	double radius;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_Circle() {
		center = new WB_Point();
		radius = 0;
	}

	public WB_Circle(final WB_Coordinate center, final double radius) {
		this.center = geometryfactory.createPoint(center);
		this.radius = WB_Math.fastAbs(radius);
		normal = geometryfactory.createVector(0, 0, 1);

	}

	public WB_Circle(final WB_Coordinate center, final WB_Coordinate normal,
			final double radius) {
		this.center = geometryfactory.createPoint(center);
		this.radius = WB_Math.fastAbs(radius);
		this.normal = geometryfactory.createNormalizedVector(normal);

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

	@Override
	public int getDimension() {
		return 1;
	}

	@Override
	public int getEmbeddingDimension() {
		return 2;
	}

	public boolean isTangent(final WB_Circle C) {
		final double d = center.getDistance(C.getCenter());
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
		center._set(x, y);
	}

	public void setCenter(final double x, final double y, double z) {
		center._set(x, y, z);
	}

	public void setCenter(WB_Coordinate c) {
		center._set(c);
	}

	public void setRadius(final double radius) {
		this.radius = radius;
	}

	public WB_Circle(final double x, final double y, final double r) {
		center = new WB_Point(x, y);
		radius = r;
	}

	public static WB_Circle getCircleThrough3Points(final WB_Coordinate p0,
			final WB_Coordinate p1, final WB_Coordinate p2) {
		final WB_Triangle T = new WB_Triangle(p0, p1, p2);
		return T.getCircumcircle();
	}

	public static WB_Circle getCircleTangentTo3Lines(final WB_Line L0,
			final WB_Line L1, final WB_Line L2) {
		final WB_Point p0 = (WB_Point) WB_Intersection
				.getClosestPoint2D(L0, L1).object;
		final WB_Point p1 = (WB_Point) WB_Intersection
				.getClosestPoint2D(L1, L2).object;
		final WB_Point p2 = (WB_Point) WB_Intersection
				.getClosestPoint2D(L0, L2).object;

		final WB_Triangle T = new WB_Triangle(p0, p1, p2);
		return T.getIncircle();
	}

	public static ArrayList<WB_Circle> getCircleThrough2Points(
			final WB_Coordinate p0, final WB_Coordinate p1, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>();
		final WB_Circle C0 = new WB_Circle(p0, r);
		final WB_Circle C1 = new WB_Circle(p1, r);
		final ArrayList<WB_Point> intersection = WB_Intersection
				.getIntersection2D(C0, C1);
		for (int i = 0; i < intersection.size(); i++) {
			result.add(new WB_Circle(intersection.get(i), r));

		}
		return result;
	}

	public static ArrayList<WB_Circle> getCircleTangentToLineThroughPoint(
			final WB_Line L, final WB_Coordinate p, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>();
		double cPrime = L.c() + L.a() * p.xd() + L.b() * p.yd();
		if (WB_Epsilon.isZero(cPrime)) {
			result.add(new WB_Circle(new WB_Point(p)._addSelf(L.a(), L.b(), r),
					r));
			result.add(new WB_Circle(
					new WB_Point(p)._addSelf(L.a(), L.b(), -r), r));
			return result;
		}
		double a, b;

		if (cPrime < 0) {
			a = -L.a();
			b = -L.b();
			cPrime *= -1;
		} else {

			a = L.a();
			b = L.b();
		}
		final double tmp1 = cPrime - r;
		double tmp2 = r * r - tmp1 * tmp1;
		if (WB_Epsilon.isZero(tmp2)) {
			result.add(new WB_Circle(new WB_Point(p)._addSelf(a, b, -tmp1), r));
			return result;
		} else if (tmp2 < 0) {
			return result;
		} else {
			tmp2 = Math.sqrt(tmp2);
			final WB_Point tmpp = new WB_Point(p.xd() - a * tmp1, p.yd() - b
					* tmp1);
			result.add(new WB_Circle(tmpp.add(b, -a, tmp2), r));
			result.add(new WB_Circle(tmpp.add(-b, a, tmp2), r));
			return result;
		}

	}

	public static ArrayList<WB_Circle> getCircleTangentTo2Lines(
			final WB_Line L0, final WB_Line L1, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(4);
		final double discrm0 = r;// Math.sqrt(L0.a() * L0.a() + L0.b() * L0.b())
		// * r;
		final double discrm1 = r;// Math.sqrt(L1.a() * L1.a() + L1.b() * L1.b())
		// * r;
		final double invDenom = 1.0 / (-L1.a() * L0.b() + L0.a() * L1.b());
		double cx = -(L1.b() * (L0.c() + discrm0) - L0.b() * (L1.c() + discrm1))
				* invDenom;
		double cy = +(L1.a() * (L0.c() + discrm0) - L0.a() * (L1.c() + discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point(cx, cy), r));
		cx = -(L1.b() * (L0.c() + discrm0) - L0.b() * (L1.c() - discrm1))
				* invDenom;
		cy = +(L1.a() * (L0.c() + discrm0) - L0.a() * (L1.c() - discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point(cx, cy), r));
		cx = -(L1.b() * (L0.c() - discrm0) - L0.b() * (L1.c() + discrm1))
				* invDenom;
		cy = +(L1.a() * (L0.c() - discrm0) - L0.a() * (L1.c() + discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point(cx, cy), r));
		cx = -(L1.b() * (L0.c() - discrm0) - L0.b() * (L1.c() - discrm1))
				* invDenom;
		cy = +(L1.a() * (L0.c() - discrm0) - L0.a() * (L1.c() - discrm1))
				* invDenom;
		result.add(new WB_Circle(new WB_Point(cx, cy), r));

		return result;
	}

	public static ArrayList<WB_Circle> getCircleThroughPointTangentToCircle(
			final WB_Coordinate p, final WB_Circle C, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(4);
		final double dcp = WB_Distance.getDistance2D(p, C.getCenter());

		if (dcp > C.getRadius() + 2 * r) {
			return result;

		} else if (dcp < C.getRadius() - 2 * r) {
			return result;

		} else {
			final WB_Circle ctmp1 = new WB_Circle(p, r);
			WB_Circle ctmp2 = new WB_Circle(C.getCenter(), r + C.getRadius());
			ArrayList<WB_Point> intersection = WB_Intersection
					.getIntersection2D(ctmp1, ctmp2);
			for (int i = 0; i < intersection.size(); i++) {
				result.add(new WB_Circle(intersection.get(i), r));
			}
			ctmp2 = new WB_Circle(C.getCenter(), WB_Math.fastAbs(r
					- C.getRadius()));
			intersection = WB_Intersection.getIntersection2D(ctmp1, ctmp2);
			for (int i = 0; i < intersection.size(); i++) {
				result.add(new WB_Circle(intersection.get(i), r));
			}
		}

		return result;

	}

	public static ArrayList<WB_Circle> getCircleTangentToLineAndCircle(
			final WB_Line L, final WB_Circle C, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(8);
		final double d = WB_Distance.getDistance2D(C.getCenter(), L);
		if (d > 2 * r + C.getRadius()) {
			return result;
		}
		final WB_Line L1 = new WB_Line(L.getOrigin().add(L.getDirection().yd(),
				-L.getDirection().xd(), r), L.getDirection());
		final WB_Line L2 = new WB_Line(L.getOrigin().add(
				-L.getDirection().yd(), +L.getDirection().xd(), r),
				L.getDirection());
		final WB_Circle C1 = new WB_Circle(C.getCenter(), C.getRadius() + r);
		final WB_Circle C2 = new WB_Circle(C.getCenter(), WB_Math.fastAbs(C
				.getRadius() - r));
		final ArrayList<WB_Point> intersections = new ArrayList<WB_Point>();
		intersections.addAll(WB_Intersection.getIntersection2D(L1, C1));
		intersections.addAll(WB_Intersection.getIntersection2D(L1, C2));
		intersections.addAll(WB_Intersection.getIntersection2D(L2, C1));
		intersections.addAll(WB_Intersection.getIntersection2D(L2, C2));
		for (int i = 0; i < intersections.size(); i++) {
			result.add(new WB_Circle(intersections.get(i), r));
		}
		return result;
	}

	public static ArrayList<WB_Circle> getCircleTangentToTwoCircles(
			final WB_Circle C0, final WB_Circle C1, final double r) {
		final ArrayList<WB_Circle> result = new ArrayList<WB_Circle>(2);

		final WB_Circle C0r = new WB_Circle(C0.getCenter(), C0.getRadius() + r);
		final WB_Circle C1r = new WB_Circle(C1.getCenter(), C1.getRadius() + r);

		final ArrayList<WB_Point> intersections = new ArrayList<WB_Point>();
		intersections.addAll(WB_Intersection.getIntersection2D(C0r, C1r));

		for (int i = 0; i < intersections.size(); i++) {
			result.add(new WB_Circle(intersections.get(i), r));
		}
		return result;
	}
}
