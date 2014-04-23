package wblut.external.ProGAL;

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
 * A line segment spanned by two points, a and b.
 */
public class LineSegment implements Simplex {
	protected Point a, b;

	/** Constructs a segment between points a and b. */
	public LineSegment(Point a, Point b) {
		this.a = a;
		this.b = b;
	}

	/** Constructs a segment from a to a+v. */
	public LineSegment(Point a, Vector v) {
		this.a = a;
		this.b = a.add(v);
	}

	/** Get the first point spanning the segment. */
	public Point getA() {
		return a;
	}

	/** Get the second point spanning the segment. */
	public Point getB() {
		return b;
	}

	/** Change the first point spanning the segment. */
	public void setA(Point a) {
		this.a = a;
	}

	/** Change the second point spanning the segment. */
	public void setB(Point b) {
		this.b = b;
	}

	/** Get the direction of the segment. This method returns a new object. */
	public Vector getAToB() {
		return a.vectorTo(b);
	}

	/** Get the length of the segment. */
	public double getLength() {
		return Math.sqrt(getLengthSquared());
	}

	/** Get the squared length of the segment. */
	public double getLengthSquared() {
		return a.distanceSquared(b);
	}

	/**
	 * Get the point on the segment closest to a given point q. This method
	 * always returns a new object.
	 */
	public Point getClosestPoint(Point q) {
		Vector dir = a.vectorTo(b);
		;
		Vector aq = a.vectorTo(q);
		;
		double t = dir.dot(aq) / dir.getLengthSquared();
		t = Math.min(1, Math.max(0, t));
		return new Point(a.x() + t * dir.x(), a.y() + t * dir.y(), a.z() + t
				* dir.z());
	}

	/** Gets the squared distance from q to the nearest point on this segment. */
	public double getSquaredDistance(Point q) {
		return q.distanceSquared(getClosestPoint(q));
	}

	/** Gets the distance from q to the nearest point on this segment. */
	public double getDistance(Point q) {
		return q.distance(getClosestPoint(q));
	}

	/** Gets the midpoint of the segment. */
	public Point getMidPoint() {
		return Point.getMidpoint(a, b);
	}

	/**
	 * Returns the mid-point of this line-segment. Since a line-segment can be
	 * interpreted as a geometric shape (a 1-simplex) the Shape interface
	 * requires the getCenter method to be implemented. TODO: Test
	 */
	@Override
	public Point getCenter() {
		return getMidPoint();
	}

	/** TODO: Comment and test */
	@Override
	public Point getPoint(int i) {
		if (i < 0 || i > 1)
			throw new IllegalArgumentException("Invalid index (" + i
					+ ") 1-simplex has two points only");
		if (i == 0)
			return a;
		else
			return b;
	}

	/**
	 * Return the 'dimension' of this object. Required by the interface Simplex.
	 */
	@Override
	public int getDimension() {
		return 1;
	}

	/** Returns true iff the argument is a line-segment and equals this. */
	@Override
	public boolean equals(Object o) {
		if (o instanceof LineSegment)
			return equals((LineSegment) o);
		return false;
	}

	/**
	 * Returns true iff this line-segment and ls are the same. Two line-segments
	 * are not considered equal if they have the same end-points but in
	 * different orders.
	 */
	public boolean equals(LineSegment ls) {
		return a.equals(ls.a) && b.equals(ls.b);
	}

	/** Returns a deep clone of this line segment. */
	@Override
	public LineSegment clone() {
		return new LineSegment(a.clone(), b.clone());
	}

	/** Returns a string representation of this segments. */
	@Override
	public String toString() {
		return "Segment[" + a + "," + b + "]";
	}

	/**
	 * Returns a string representation of this segments with <code>dec</code>
	 * decimals precision.
	 */
	public String toString(int dec) {
		return "Segment[" + a.toString(dec) + "," + b.toString(dec) + "]";
	}

	/** Writes this segment to <code>System.out</code>. */
	public void toConsole() {
		System.out.println(toString());
	}

	/**
	 * Writes this segment to <code>System.out</code> with <code>dec</code>
	 * decimals precision.
	 */
	public void toConsole(int dec) {
		System.out.println(toString(dec));
	}

}
