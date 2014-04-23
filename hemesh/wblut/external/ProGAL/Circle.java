package wblut.external.ProGAL;

import wblut.WB_Epsilon;

/*
 * If the circle has center c, radius r, and unit-length normal vector n, compute unit-length vectors u and v so that {u,v,n} are mutually
 * orthogonal. The circle is then parameterized by P(t) = c + r*(cos(t)*u + sin(t)*v) for 0 <= t < 2*pi. 
 */

/**
 * Part of ProGAL: http://www.diku.dk/~rfonseca/ProGAL/
 * 
 * Original copyright notice:
 * 
 * Copyright (c) 2013, Dept. of Computer Science - Univ. of Copenhagen. All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * A circle in (x,y,z)-space represented by a center-point, a radius and a
 * normal-vector.
 */
public class Circle implements Shape {
	private final Point center;
	private final double radius;
	private final Vector normal;

	public Circle(Point center, double radius, Vector normal) {
		this.center = center;
		this.radius = radius;
		this.normal = normal.normalizeNew();
	}

	/**
	 * A circle with given center through a given point and with specified
	 * normal vector
	 */
	public Circle(Point center, Point through, Vector normal) {
		this.center = center;
		radius = center.distance(through);
		this.normal = normal.normalizeNew();
	}

	/*
	 * Given three points p0, p1, p2 and a vector v, find the circle or just the
	 * radius of the circle through the projection of the points onto the plane
	 * with v as its normal vector. Claim: radius is minimized when of the plane
	 * goes through p0, p1, p2
	 */
	/*
	 * public Circle3d(Point3d p0, Point3d p1, Point3d p2, Vector3d v) { //
	 * create the plane through the origo with v as its normal vector Plane3d
	 * plane = new Plane3d(v, new Point3d(0,0,0)); Point3d q0 =
	 * plane.projectPoint(p0); Point3d q1 = plane.projectPoint(p1); Point3d q2 =
	 * plane.projectPoint(p2); double a = q0.getSquaredDistance(q1); double b =
	 * q1.getSquaredDistance(q2); double c = q2.getSquaredDistance(q0); radius =
	 * Math.sqrt(a*b*c)/Math.sqrt(2*a*b + 2*b*c + 2*c*a - a*a - b*b - c*c);
	 * normalVector = Vector3d.crossProduct(new Vector3d(p0,p1), new
	 * Vector3d(p0,p2)); }
	 */

	/*
	 * returns the radius of the circle through 3 given points (without creating
	 * the circle)
	 */
	/*
	 * public static double getRadius(Point3d p0, Point3d p1, Point3d p2) {
	 * 
	 * // get the plane through q0, q1, q2
	 * 
	 * Point3d q0 = new Point3d(0,0,0); Point3d q1 = new Point3d(p1.x-p0.x,
	 * p1.y-p0.y, p1.z-p0.z); Point3d q2 = new Point3d(p2.x-p0.x, p2.y-p0.y,
	 * p2.z-p0.z);
	 * 
	 * // get the plane through q0, q1, q2
	 * 
	 * Plane3d plane = new Plane3d(q0,q1,q2); Vector3d verticalVector = new
	 * Vector3d(0,0,1); double angle = Vector3d.getAngle(plane.n,
	 * verticalVector); Vector3d rotationVector = Vector3d.crossProduct(plane.n,
	 * verticalVector); rotationVector.normalizeThis();
	 * q1.rotation(rotationVector, angle); q2.rotation(rotationVector, angle);
	 * Point2d r0 = new Point2d(0,0); Point2d r1 = new Point2d(q1.x, q1.y);
	 * Point2d r2 = new Point2d(q2.x, q2.y); Circle2d circle2 = new Circle2d(r0,
	 * r1, r2); return circle2.getRadius(); }
	 */

	/** Get the center of the circle. */
	@Override
	public Point getCenter() {
		return center;
	}

	/** Get the radius of the circle. */
	public double getRadius() {
		return radius;
	}

	/** Get the normal of the circle. */
	public Vector getNormalVector() {
		return normal;
	}

	public Vector getNormal() {
		return normal;
	}

	/** Create the equilateral circle of two points. */
	public static Circle getEquilateralCircle(Point a, Point b) {
		Point center = Point.getMidpoint(a, b);
		Vector ab = a.vectorTo(b);
		double radius = Math.sqrt(3) * ab.length() / 2;
		return new Circle(center, radius, ab);
	}

	/**
	 * Returns a string-representation of this circle formatted with two
	 * decimals precision.
	 */
	@Override
	public String toString() {
		return toString(2);
	}

	/**
	 * Returns a string-representation of this circle formatted with
	 * <code>dec</code> decimals precision.
	 */
	public String toString(int dec) {
		return String.format(
				"Circle[center=%s,radius=%" + dec + "f,normal=%s]",
				center.toString(dec), radius, normal.toString(dec));
	}

	/** Writes this circle to <code>System.out</code> with 2 decimals precision. */
	public void toConsole() {
		toConsole(2);
	}

	/**
	 * Writes this circle to <code>System.out</code> with <code>dec</code>
	 * decimals precision.
	 */
	public void toConsole(int dec) {
		System.out.println(toString(dec));
	}

	/** Intersection of 2 circles in the same plane */
	public Point[] getIntersection(Circle c) {
		Point[] intersectionPoints = null;
		double dist = center.distance(c.getCenter());
		double sum = radius + c.getRadius();
		if (dist - sum > WB_Epsilon.EPSILON)
			return intersectionPoints;
		else {
			double diff = Math.abs(radius - c.getRadius());
			if (dist - diff < -WB_Epsilon.EPSILON)
				return intersectionPoints;
			else {
				if (Math.abs(dist - sum) < WB_Epsilon.EPSILON) {
					Vector v = new Vector(center, c.getCenter());
					double fraction = radius / (radius + c.getRadius());
					intersectionPoints = new Point[1];
					intersectionPoints[0] = center.addThis(v
							.multiplyNew(fraction));
					return intersectionPoints;
				} else {
					if (dist - diff < WB_Epsilon.EPSILON) {
						Vector v;
						intersectionPoints = new Point[1];
						if (radius > c.radius) {
							v = new Vector(center, c.getCenter());
							v.scaleToLengthThis(radius);
							intersectionPoints[0] = center.add(v);
						} else {
							v = new Vector(c.getCenter(), center);
							v.scaleToLengthThis(c.getRadius());
							intersectionPoints[0] = c.getCenter().add(v);
						}
					} else {
						double alpha = Math
								.acos((radius * radius + dist * dist - c
										.getRadius() * c.getRadius())
										/ (2 * radius * dist));
						Vector v = new Vector(center, c.getCenter());
						v.scaleToLengthThis(radius);
						intersectionPoints = new Point[2];
						getNormalVector().rotateIn(v, alpha);
						intersectionPoints[0] = center.add(v);
						getNormalVector().rotateIn(v, -2 * alpha);
						intersectionPoints[1] = center.add(v);
					}
				}
			}
		}
		return intersectionPoints;
	}

	/**
	 * returns the smallest rotation angle (direction determined by vector dir)
	 * needed to bring point p on this circle to be on the line l as well.
	 * Returns null if there is no intersection or just one intersection.
	 * 
	 * @return
	 */
	public Double getFirstIntersection(Line line, Point p, Vector dir) {
		double dist = line.getDistance(center);
		if (dist > radius - WB_Epsilon.EPSILON)
			return null;
		Vector op = new Vector(center, p);
		double r2 = radius * radius;
		if (dist < 0.001) {
			Vector oco = line.dir.multiplyNew(radius);
			double cosBeta = op.dot(oco) / r2;
			Vector cr = op.crossNew(oco);
			cr.divideThis(r2);
			double sinBeta = cr.length();
			double beta = Math.atan2(sinBeta, cosBeta);
			System.out.println("beta = " + Math.toDegrees(beta));
			if (normal.dot(dir) > 0) {
				if (normal.dot(cr) > 0)
					return beta;
				else
					return 2 * Math.PI - beta;
			} else {
				if (normal.dot(cr) > 0)
					return 2 * Math.PI - beta;
				else
					return beta;
			}
		}
		Point co = line.orthogonalProjection(center);
		double oco2 = dist * dist;
		double pco2 = co.distanceSquared(p);
		// double cosAlpha = Math.sqrt(1-oco2/r2);
		double cosAlpha = dist / radius;
		double cosBeta = (r2 + oco2 - pco2) / (2 * radius * dist);
		Vector oco = new Vector(center, co);
		LineSegment seg1 = new LineSegment(center, co); // seg1.toScene(scene,0.03,
														// Color.red);
		LineSegment seg2 = new LineSegment(center, p); // seg2.toScene(scene,
														// 0.03, Color.pink);
		Vector cr = op.crossNew(oco);
		cr.divideThis(op.length() * oco.length());
		double sinBeta = cr.length();
		double alpha = Math.acos(cosAlpha);
		System.out.println("alpha = " + Math.toDegrees(cosAlpha));
		double beta = Math.atan2(sinBeta, cosBeta);
		System.out.println("beta = " + Math.toDegrees(beta));
		if (normal.dot(dir) > 0) {
			if (normal.dot(cr) > 0)
				return beta - alpha;
			else
				return 2 * Math.PI - beta - alpha;
		} else {
			if (normal.dot(cr) > 0)
				return 2 * Math.PI - beta - alpha;
			else
				return beta - alpha;
		}
	}

	/**
	 * returns the smallest rotation angle (direction determined by vector dir)
	 * needed to bring point p on this circle to be on the circle c as well.
	 * Returns null if there is no intersection or just one intersection.
	 * 
	 * @return
	 */
	public Double getFirstIntersection(Circle c, Point p, Vector dir) {
		double dist = center.distance(c.getCenter());
		if (dist > radius + c.getRadius() - WB_Epsilon.EPSILON)
			return null;
		double r2d2 = radius * radius + dist * dist;
		double rd2 = 2 * radius * dist;
		double cosAlpha = (r2d2 - c.getRadius() * c.getRadius()) / rd2;
		System.out.println("cosAlpha =" + cosAlpha + " " + Math.acos(cosAlpha));
		double distP2 = p.distanceSquared(c.getCenter());
		double cosBeta = (r2d2 - distP2) / rd2;
		System.out.println("cosBeta =" + cosAlpha + " " + Math.acos(cosBeta));
		Vector op = new Vector(center, p);
		Vector oco = new Vector(center, c.getCenter());
		Vector cr = op.crossNew(oco);
		cr.divideThis(op.length() * oco.length());
		double sinBeta = cr.length();
		double alpha = Math.acos(cosAlpha);
		System.out.println("alpha = " + Math.toDegrees(cosAlpha));
		double beta = Math.atan2(sinBeta, cosBeta);
		System.out.println("beta = " + Math.toDegrees(beta));
		if (normal.dot(dir) > 0) {
			if (normal.dot(cr) > 0)
				return beta - alpha;
			else
				return 2 * Math.PI - beta - alpha;
		} else {
			if (normal.dot(cr) > 0)
				return 2 * Math.PI - beta - alpha;
			else
				return beta - alpha;
		}
	}

}
