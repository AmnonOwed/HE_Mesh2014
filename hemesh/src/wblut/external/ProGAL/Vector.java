package wblut.external.ProGAL;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import wblut.core.WB_Epsilon;

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
 * A vector in (x,y,z)-space represented with double precision.
 * 
 */
public class Vector {

	protected double[] coords;
	protected final int dim;

	/** Construct a vector pointing from origo to p. */

	public Vector(double x, double y, double z) {
		dim = 3;
		coords = new double[] { x, y, z };

	}

	public Vector(Point p) {
		dim = 3;
		coords = new double[dim];
		for (int d = 0; d < dim; d++)
			coords[d] = p.coords[d];
	}

	/**
	 * Constructs a vector between two points p1 and p2 - added by pawel
	 * 12-11-2011
	 */
	public Vector(Point p1, Point p2) {
		dim = 3;
		coords = new double[dim];
		for (int d = 0; d < dim; d++)
			coords[d] = p2.coords[d] - p1.coords[d];
	}

	/** Construct a vector that is a clone of v. */
	public Vector(Vector v) {
		dim = v.dim;
		coords = new double[dim];
		for (int d = 0; d < dim; d++)
			coords[d] = v.coords[d];
	}

	/** Construct a vector with the specified coordinates */
	public Vector(double[] coords) {
		dim = coords.length;
		this.coords = coords;
	}

	public Vector(int dim) {
		this.dim = dim;
		this.coords = new double[dim];
	}

	/** Get the i'th coordinate. */
	public double getCoord(int i) {
		return coords[i];
	}

	/** Get the i'th coordinate. */
	public double get(int i) {
		return getCoord(i);
	}

	/** Get the first coordinate. */
	public double x() {
		return coords[0];
	}

	/** Get the second coordinate. */
	public double y() {
		return coords[1];
	}

	/** Get the third coordinate. */
	public double z() {
		return coords[2];
	}

	/** Set the first coordinate */
	public void setX(double x) {
		this.coords[0] = x;
	}

	/** Set the second coordinate */
	public void setY(double y) {
		this.coords[1] = y;
	}

	/** Set the third coordinate */
	public void setZ(double z) {
		this.coords[2] = z;
	}

	/** Set the d'th coordinate. */
	public void set(int d, double v) {
		coords[d] = v;
	}

	/** Set the d'th coordinate. */
	public void setCoord(int d, double v) {
		coords[d] = v;
	}

	/** Set all coordinates of this vector equal to those of v */
	public void set(Vector v) {
		this.coords[0] = v.coords[0];
		this.coords[1] = v.coords[1];
		this.coords[2] = v.coords[2];
	}

	/** Get the dot-product of this vector and v. */
	public double dot(Vector v) {
		return coords[0] * v.coords[0] + coords[1] * v.coords[1] + coords[2]
				* v.coords[2];
	}

	/** Get the angle between this vector and v. */
	public double angle(Vector v) {
		return Math.acos(Math.min(
				1,
				this.dot(v)
						/ Math.sqrt(this.getLengthSquared()
								* v.getLengthSquared())));
	}

	/**
	 * Add v to this vector and return the result (without changing this
	 * object).
	 */
	public Vector addNew(Vector v) {
		return new Vector(coords[0] + v.coords[0], coords[1] + v.coords[1],
				coords[2] + v.coords[2]);
	}

	/** Add v to this vector and return the result (changing this object). */
	public Vector addThis(Vector v) {
		coords[0] += v.coords[0];
		coords[1] += v.coords[1];
		coords[2] += v.coords[2];
		return this;
	}

	/**
	 * Subtract v from this vector and return the result (without changing this
	 * object).
	 */
	public Vector subtractNew(Vector v) {
		return new Vector(coords[0] - v.coords[0], coords[1] - v.coords[1],
				coords[2] - v.coords[2]);
	}

	/** Subract v from this vector and return the result (changing this object). */
	public Vector subtractThis(Vector v) {
		coords[0] -= v.coords[0];
		coords[1] -= v.coords[1];
		coords[2] -= v.coords[2];
		return this;
	}

	/**
	 * Multiply this vector by s and return the result (without changing this
	 * object).
	 */

	public Vector multiplyNew(double s) {
		return new Vector(coords[0] * s, coords[1] * s, coords[2] * s);
	}

	/** Multiply this vector by s and return the result (changing this object). */

	public Vector multiplyThis(double s) {
		coords[0] *= s;
		coords[1] *= s;
		coords[2] *= s;
		return this;
	}

	/**
	 * Divide this vector by s and return the result (without changing this
	 * object).
	 */

	public Vector divideNew(double s) {
		return new Vector(coords[0] / s, coords[1] / s, coords[2] / s);
	}

	/** Divide this vector by s and return the result (changing this object). */

	public Vector divideThis(double s) {
		coords[0] /= s;
		coords[1] /= s;
		coords[2] /= s;
		return this;
	}

	/**
	 * Normalize this vector and return the result (without changing this
	 * object).
	 */

	public Vector normalizeNew() {
		return this.divideNew(length());
	}

	/** Normalize this vector and return the result (changing this object). */

	public Vector normalizeThis() {
		return this.divideThis(length());
	}

	public Vector normalizeFastNew() {
		return this.multiplyNew(invSqrt(getLengthSquared()));
	}

	public Vector normalizeThisFast() {
		return this.multiplyThis(invSqrt(getLengthSquared()));
	}

	/** The fast inverse square root hack from quake 3 */
	private static double invSqrt(double x) {
		double xhalf = 0.5d * x;
		long i = Double.doubleToLongBits(x);
		i = 0x5fe6ec85e7de30daL - (i >> 1);
		x = Double.longBitsToDouble(i);
		x = x * (1.5d - xhalf * x * x);
		return x;
	}

	/**
	 * Scale this vector to a certain length (returns new object and does not
	 * change this object).
	 */

	public Vector scaleToLengthNew(double length) {
		return multiplyNew(length / length());
	}

	/** Scale this vector to a certain length (changes this object). */

	public Vector scaleToLengthThis(double length) {
		return multiplyThis(length / length());
	}

	/**
	 * Get the cross-product of this vector and v (without changing this
	 * object).
	 */
	public Vector crossNew(Vector v) {
		return new Vector(coords[1] * v.coords[2] - coords[2] * v.coords[1],
				coords[2] * v.coords[0] - coords[0] * v.coords[2], coords[0]
						* v.coords[1] - coords[1] * v.coords[0]);
	}

	/**
	 * Get the cross-product of this vector and v and store the result in this
	 * vector (changes this object).
	 */
	public Vector crossThis(Vector v) {
		double newX = coords[1] * v.coords[2] - coords[2] * v.coords[1], newY = coords[2]
				* v.coords[0] - coords[0] * v.coords[2], newZ = coords[0]
				* v.coords[1] - coords[1] * v.coords[0];
		this.coords[0] = newX;
		this.coords[1] = newY;
		this.coords[2] = newZ;
		return this;
	}

	/**
	 * Perform a right-handed rotation of v around this vector. TODO: Test
	 */
	public Vector rotateIn(Vector v, double angle) {
		double l = length();
		if (l == 0)
			throw new Error("Trying to rotate around 0-vector");

		double ux = coords[0] / l;
		double uy = coords[1] / l;
		double uz = coords[2] / l;
		double sin = sin(angle);
		double cos = cos(angle);

		double a00 = (ux * ux + cos * (1.0 - ux * ux));
		double a10 = (ux * uy * (1.0 - cos) + uz * sin);
		double a20 = (uz * ux * (1 - cos) - uy * sin);

		double a01 = (ux * uy * (1 - cos) - uz * sin);
		double a11 = (uy * uy + cos * (1.0 - uy * uy));
		double a21 = (uy * uz * (1.0 - cos) + ux * sin);

		double a02 = (uz * ux * (1.0 - cos) + uy * sin);
		double a12 = (uy * uz * (1.0 - cos) - ux * sin);
		double a22 = (uz * uz + cos * (1.0 - uz * uz));

		double newX = a00 * v.coords[0] + a01 * v.coords[1] + a02 * v.coords[2];
		double newY = a10 * v.coords[0] + a11 * v.coords[1] + a12 * v.coords[2];
		double newZ = a20 * v.coords[0] + a21 * v.coords[1] + a22 * v.coords[2];
		v.setX(newX);
		v.setY(newY);
		v.setZ(newZ);
		return v;
	}

	/** Convert this vector to a point. */
	public Point toPoint() {
		return new Point(coords[0], coords[1], coords[2]);
	}

	/**
	 * Returns a string-representation of this vector formatted with two
	 * decimals precision.
	 */
	@Override
	public String toString() {
		return toString(2);
	}

	/**
	 * Returns a string-representation of this vector formatted with
	 * <code>dec</code> decimals precision.
	 */
	public String toString(int dec) {
		return String.format("Vector3d[%." + dec + "f,%." + dec + "f,%." + dec
				+ "f]", coords[0], coords[1], coords[2]);
	}

	/** Writes this vector to <code>System.out</code>. */

	public void toConsole() {
		toConsole(2);
	}

	/**
	 * Writes this vector to <code>System.out</code> with <code>dec</code>
	 * decimals precision.
	 */

	public void toConsole(int dec) {
		System.out.println(toString(dec));
	}

	/** Create a clone of this vector. */
	@Override
	public Vector clone() {
		return new Vector(coords[0], coords[1], coords[2]);
	}

	// /////// Static methods and fields ////////

	/** Get the angle between vector u and v. */
	public static double getAngle(Vector u, Vector v) {
		return u.angle(v);
	}

	/** Get the dihedral angle between 3 non-colinear vectors b1, b2, b3. */
	public static double getDihedralAngle(Vector b1, Vector b2, Vector b3) {
		Vector b2xb3 = b2.crossNew(b3);
		double y = b1.multiplyNew(b2.length()).dot(b2xb3);
		double x = b1.crossNew(b2).dot(b2xb3);
		return Math.atan2(y, x);
	}

	/** Get a vector (one of many possible) orthonormal to vector v. */
	public Vector getOrthonormal() {
		if (Math.abs(z()) > WB_Epsilon.EPSILON) {
			Vector a = new Vector(1, 0, -x() / z());
			return a.normalizeThis();
		} else {
			return new Vector(0, 0, 1);
		}

	}

	/*
	 * added by pawel 12-11-2011
	 */
	public boolean isParallel(Vector v) {
		return crossNew(v).isZeroVector();
	}

	/** Get the squared length of this vector. */
	public double getLengthSquared() {
		double sum = 0;
		for (int d = 0; d < dim; d++)
			sum += coords[d] * coords[d];
		return sum;
	}

	/** Get the length of this vector. */
	public double length() {
		return Math.sqrt(getLengthSquared());
	}

	/** Return true if the length of this vector is 0. */
	// rettet af Pawel 12-11-2011
	public boolean isZeroVector() {
		for (int d = 0; d < dim; d++)
			if (Math.abs(coords[d]) > WB_Epsilon.EPSILON)
				return false;
		return true;
	}

	/** Return true if this vector equals v. */
	public boolean equals(Vector v) {
		for (int d = 0; d < dim; d++)
			if (Math.abs(coords[d] - v.coords[d]) > WB_Epsilon.EPSILON)
				return false;
		return true;
	}

	@Override
	public boolean equals(Object v) {
		if (v instanceof Vector)
			return equals((Vector) v);
		return false;
	}

	/*
	 * returns cosinus of the dihedral angle between three vectors added by
	 * pawel on 12-11-2011
	 */
	public static double getCosDihedralAngle(Vector u, Vector v, Vector w) {
		if (u.isParallel(v))
			throw new Error("Vectors u and v are colinear");
		if (v.isParallel(w))
			throw new Error("Vectors v and w are colinear");
		Vector uv = u.crossNew(u.addNew(v));
		Vector vw = v.crossNew(v.addNew(w));
		return (uv.dot(vw) / (uv.length() * vw.length()));
	}

	/** An immutable vector pointing in the (1,0,0)-direction. */
	public static Vector X = new Vector(1, 0, 0);

	/** An immutable vector pointing in the (0,1,0)-direction. */
	public static Vector Y = new Vector(0, 1, 0);

	/** An immutable vector pointing in the (0,0,1)-direction. */
	public static Vector Z = new Vector(0, 0, 1);

	public void setCoords(double[] newCoords) {

		this.coords = newCoords;

	}

}
