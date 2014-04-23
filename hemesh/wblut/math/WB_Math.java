/**
 * 
 */
package wblut.math;

import wblut.geom.WB_Coordinate;
import wblut.geom.WB_LatLon;
import wblut.geom.WB_Vector;

/**
 * A collection of fast and approximated math functions. Not as robust as the
 * JAVA implementations when dealing with special values (NaN etc). Some
 * functions only give an approximation.
 * 
 * @author Frederik Vanhoutte, W:Blut
 * 
 */
public class WB_Math {

	/**
	 * Fast absolute value.
	 * 
	 * @param x
	 *            the x
	 * @return abs(x)
	 */
	public static double fastAbs(final double x) {
		return (x > 0) ? x : -x;
	}

	/**
	 * Fast max.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return max(x,y)
	 */
	public static double max(final double x, final double y) {
		return (y > x) ? y : x;
	}

	/**
	 * Fast min.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return min(x,y)
	 */
	public static double min(final double x, final double y) {
		return (y < x) ? y : x;
	}

	/**
	 * Fast max.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return max(x,y)
	 */
	public static int max(final int x, final int y) {
		return (y > x) ? y : x;
	}

	/**
	 * Fast min.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @return min(x,y)
	 */
	public static int min(final int x, final int y) {
		return (y < x) ? y : x;
	}

	/**
	 * Maximum of three values.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return max(x,y,z)
	 */
	public static double max(final double x, final double y, final double z) {
		return (y > x) ? ((z > y) ? z : y) : ((z > x) ? z : x);
	}

	/**
	 * Minimum of three values.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return min(x,y,z)
	 */
	public static double min(final double x, final double y, final double z) {
		return (y < x) ? ((z < y) ? z : y) : ((z < x) ? z : x);
	}

	/**
	 * Largest integer smaller than value.
	 * 
	 * @param x
	 *            the x
	 * @return result
	 */
	public static final int floor(final float x) {
		return (x >= 0) ? (int) x : (int) x - 1;
	}

	/**
	 * Fast log2 approximation for floats.
	 * 
	 * @param i
	 *            the i
	 * @return result
	 */
	public static final float fastLog2(final float i) {
		float x = Float.floatToRawIntBits(i);
		x *= 1.0f / (1 << 23);
		x -= 127;
		float y = x - floor(x);
		y = (y - y * y) * 0.346607f;
		return x + y;
	}

	/**
	 * Fast square power approximation for floats.
	 * 
	 * @param i
	 *            the i
	 * @return result
	 */
	public static final float fastPow2(final float i) {
		float x = i - floor(i);
		x = (x - x * x) * 0.33971f;
		return Float.intBitsToFloat((int) ((i + 127 - x) * (1 << 23)));
	}

	/**
	 * Fast power approximation for floats.
	 * 
	 * @param a
	 *            the a
	 * @param b
	 *            exponent
	 * @return result
	 */
	public static final float fastPow(final float a, final float b) {
		return fastPow2(b * fastLog2(a));
	}

	/**
	 * Fast inverse sqrt approximation for floats.
	 * 
	 * @param x
	 *            the x
	 * @return result
	 */
	public static final float fastInvSqrt(float x) {
		final float half = 0.5F * x;
		int i = Float.floatToIntBits(x);
		i = 0x5f375a86 - (i >> 1);
		x = Float.intBitsToFloat(i);
		return x * (1.5F - half * x * x);
	}

	/**
	 * Fast sqrt approximation for floats.
	 * 
	 * @param x
	 *            the x
	 * @return result
	 */
	public static final float fastSqrt(final float x) {
		return 1f / fastInvSqrt(x);
	}

	private static final int DIGITS = 52;

	public static int getExp(final double v) {
		if (v == 0) {
			return 0;
		}
		return (int) ((0x7ff0000000000000L & Double.doubleToLongBits(v)) >> DIGITS) - 1022;
	}

	/** sqrt(a^2 + b^2) without under/overflow. **/

	public static double hypot(final double a, final double b) {
		double r;
		if (Math.abs(a) > Math.abs(b)) {
			r = b / a;
			r = Math.abs(a) * Math.sqrt(1 + r * r);
		} else if (b != 0) {
			r = a / b;
			r = Math.abs(b) * Math.sqrt(1 + r * r);
		} else {
			r = 0.0;
		}
		return r;
	}

	public static final double SECOND_TO_MILLIS = 1000.0;
	public static final double MINUTE_TO_MILLIS = 60.0 * SECOND_TO_MILLIS;
	public static final double HOUR_TO_MILLIS = 60.0 * MINUTE_TO_MILLIS;
	public static final double DAY_TO_MILLIS = 24.0 * HOUR_TO_MILLIS;

	public static final double METERS_TO_KILOMETERS = 1e-3;
	public static final double METERS_TO_MILES = 0.000621371192;
	public static final double METERS_TO_NAUTICAL_MILES = 0.000539956803;
	public static final double METERS_TO_YARDS = 1.0936133;
	public static final double METERS_TO_FEET = 3.280839895;

	public static final double SQUARE_METERS_TO_SQUARE_KILOMETERS = 1e-6;
	public static final double SQUARE_METERS_TO_SQUARE_MILES = 3.86102159e-7;
	public static final double SQUARE_METERS_TO_SQUARE_YARDS = 1.19599005;
	public static final double SQUARE_METERS_TO_SQUARE_FEET = 10.7639104;
	public static final double SQUARE_METERS_TO_HECTARES = 1e-4;
	public static final double SQUARE_METERS_TO_ACRES = 0.000247105381;

	public static final WB_LatLon LONGITUDE_OFFSET_180 = WB_LatLon.fromDegrees(
			0, 180);

	/**
	 * Convenience method to compute the log base 2 of a value.
	 * 
	 * @param value
	 *            the value to take the log of.
	 * 
	 * @return the log base 2 of the specified value.
	 */
	public static double logBase2(double value) {
		return Math.log(value) / Math.log(2d);
	}

	/**
	 * Convenience method for testing whether a value is a power of two.
	 * 
	 * @param value
	 *            the value to test for power of 2
	 * 
	 * @return true if power of 2, else false
	 */
	public static boolean isPowerOfTwo(int value) {
		return (value == powerOfTwoCeiling(value));
	}

	/**
	 * Returns the value that is the nearest power of 2 greater than or equal to
	 * the given value.
	 * 
	 * @param reference
	 *            the reference value. The power of 2 returned is greater than
	 *            or equal to this value.
	 * 
	 * @return the value that is the nearest power of 2 greater than or equal to
	 *         the reference value
	 */
	public static int powerOfTwoCeiling(int reference) {
		int power = (int) Math.ceil(Math.log(reference) / Math.log(2d));
		return (int) Math.pow(2d, power);
	}

	/**
	 * Returns the value that is the nearest power of 2 less than or equal to
	 * the given value.
	 * 
	 * @param reference
	 *            the reference value. The power of 2 returned is less than or
	 *            equal to this value.
	 * 
	 * @return the value that is the nearest power of 2 less than or equal to
	 *         the reference value
	 */
	public static int powerOfTwoFloor(int reference) {
		int power = (int) Math.floor(Math.log(reference) / Math.log(2d));
		return (int) Math.pow(2d, power);
	}

	/**
	 * Populate an array with the successive powers of a number.
	 * 
	 * @param base
	 *            the number whose powers to compute.
	 * @param numPowers
	 *            the number of powers to compute.
	 * 
	 * @return an array containing the requested values. Each element contains
	 *         the value b^i, where b is the base and i is the element number
	 *         (0, 1, etc.).
	 */
	protected static int[] computePowers(int base, int numPowers) {
		int[] powers = new int[numPowers];

		powers[0] = 1;
		for (int i = 1; i < numPowers; i++) {
			powers[i] += base * powers[i - 1];
		}

		return powers;
	}

	/**
	 * Clamps a value to a given range.
	 * 
	 * @param v
	 *            the value to clamp.
	 * @param min
	 *            the floor.
	 * @param max
	 *            the ceiling
	 * 
	 * @return the nearest value such that min <= v <= max.
	 */
	public static double clamp(double v, double min, double max) {
		return v < min ? min : v > max ? max : v;
	}

	/**
	 * Clamps an integer value to a given range.
	 * 
	 * @param v
	 *            the value to clamp.
	 * @param min
	 *            the floor.
	 * @param max
	 *            the ceiling
	 * 
	 * @return the nearest value such that min <= v <= max.
	 */
	public static int clamp(int v, int min, int max) {
		return v < min ? min : v > max ? max : v;
	}

	/**
	 * Returns the interpolation factor for <code>v</code> given the specified
	 * range <code>[x, y]</code>. The interpolation factor is a number between 0
	 * and 1 (inclusive), representing the value's relative position between
	 * <code>x</code> and <code>y</code>. For example, 0 corresponds to
	 * <code>x</code>, 1 corresponds to <code>y</code>, and anything in between
	 * corresponds to a linear combination of <code>x</code> and <code>y</code>.
	 * 
	 * @param v
	 *            the value to compute the interpolation factor for.
	 * @param x
	 *            the first value.
	 * @param y
	 *            the second value.
	 * 
	 * @return the interpolation factor for <code>v</code> given the specified
	 *         range <code>[x, y]</code>
	 */
	public static double computeInterpolationFactor(double v, double x, double y) {
		return clamp((v - x) / (y - x), 0d, 1d);
	}

	/**
	 * Returns the linear interpolation of <code>x</code> and <code>y</code>
	 * according to the function: <code>(1 - a) *
	 * x + a * y</code>. The interpolation factor <code>a</code> defines the
	 * weight given to each value, and is clamped to the range [0, 1]. If
	 * <code>a</code> is 0 or less, this returns x. If <code>a</code> is 1 or
	 * more, this returns <code>y</code>. Otherwise, this returns the linear
	 * interpolation of <code>x</code> and <code>y</code>. For example, when
	 * <code>a</code> is <code>0.5</code> this returns <code>(x + y)/2</code>.
	 * 
	 * @param a
	 *            the interpolation factor.
	 * @param x
	 *            the first value.
	 * @param y
	 *            the second value.
	 * 
	 * @return the linear interpolation of <code>x</code> and <code>y</code>.
	 */
	public static double mix(double a, double x, double y) {
		double t = clamp(a, 0d, 1d);
		return x + t * (y - x);
	}

	/**
	 * Returns the smooth hermite interpolation of <code>x</code> and
	 * <code>y</code> according to the function: <code>(1
	 * - t) * x + t * y</code>, where <code>t = a * a * (3 - 2 * a)</code>. The
	 * interpolation factor <code>a</code> defines the weight given to each
	 * value, and is clamped to the range [0, 1]. If <code>a</code> is 0 or
	 * less, this returns <code>x</code>. If <code>a</code> is 1 or more, this
	 * returns <code>y</code>. Otherwise, this returns the smooth hermite
	 * interpolation of <code>x</code> and <code>y</code>. Like the linear
	 * function {@link #mix(double, double, double)}, when <code>a</code> is
	 * <code>0.5</code> this returns <code>(x + y)/2</code>. But unlike the
	 * linear function, the hermite function's slope gradually increases when
	 * <code>a</code> is near 0, then gradually decreases when <code>a</code> is
	 * near 1. This is a useful property where a more gradual transition from
	 * <code>x</code> to <code>y</code> is desired.
	 * 
	 * @param a
	 *            the interpolation factor.
	 * @param x
	 *            the first value.
	 * @param y
	 *            the second value.
	 * 
	 * @return the smooth hermite interpolation of <code>x</code> and
	 *         <code>y</code>.
	 */
	public static double mixSmooth(double a, double x, double y) {
		double t = clamp(a, 0d, 1d);
		t = t * t * (3d - 2d * t);
		return x + t * (y - x);
	}

	/**
	 * converts meters to feet.
	 * 
	 * @param meters
	 *            the value in meters.
	 * 
	 * @return the value converted to feet.
	 */
	public static double convertMetersToFeet(double meters) {
		return (meters * METERS_TO_FEET);
	}

	/**
	 * converts meters to miles.
	 * 
	 * @param meters
	 *            the value in meters.
	 * 
	 * @return the value converted to miles.
	 */
	public static double convertMetersToMiles(double meters) {
		return (meters * METERS_TO_MILES);
	}

	/**
	 * Converts distance in feet to distance in meters.
	 * 
	 * @param feet
	 *            the distance in feet.
	 * 
	 * @return the distance converted to meters.
	 */
	public static double convertFeetToMeters(double feet) {
		return (feet / METERS_TO_FEET);
	}

	/**
	 * Converts time in seconds to time in milliseconds.
	 * 
	 * @param seconds
	 *            time in seconds.
	 * 
	 * @return time in milliseconds.
	 */
	public static double convertSecondsToMillis(double seconds) {
		return (seconds * SECOND_TO_MILLIS);
	}

	/**
	 * Converts time in milliseconds to time in seconds.
	 * 
	 * @param millis
	 *            time in milliseconds.
	 * 
	 * @return time in seconds.
	 */
	public static double convertMillisToSeconds(double millis) {
		return millis / SECOND_TO_MILLIS;
	}

	/**
	 * Converts time in minutes to time in milliseconds.
	 * 
	 * @param minutes
	 *            time in minutes.
	 * 
	 * @return time in milliseconds.
	 */
	public static double convertMinutesToMillis(double minutes) {
		return (minutes * MINUTE_TO_MILLIS);
	}

	/**
	 * Converts time in milliseconds to time in minutes.
	 * 
	 * @param millis
	 *            time in milliseconds.
	 * 
	 * @return time in minutes.
	 */
	public static double convertMillisToMinutes(double millis) {
		return millis / MINUTE_TO_MILLIS;
	}

	/**
	 * Converts time in hours to time in milliseconds.
	 * 
	 * @param hours
	 *            time in hours.
	 * 
	 * @return time in milliseconds.
	 */
	public static double convertHoursToMillis(double hours) {
		return (hours * HOUR_TO_MILLIS);
	}

	/**
	 * Converts time in milliseconds to time in hours.
	 * 
	 * @param mills
	 *            time in milliseconds.
	 * 
	 * @return time in hours.
	 */
	public static double convertMillisToHours(double mills) {
		return mills / HOUR_TO_MILLIS;
	}

	/**
	 * Convert time in days to time in milliseconds.
	 * 
	 * @param millis
	 *            time in days.
	 * 
	 * @return time in milliseconds.
	 */
	public static double convertDaysToMillis(double millis) {
		return millis * DAY_TO_MILLIS;
	}

	/**
	 * Convert time in milliseconds to time in days.
	 * 
	 * @param millis
	 *            time in milliseconds.
	 * 
	 * @return time in days.
	 */
	public static double convertMillisToDays(double millis) {
		return millis / DAY_TO_MILLIS;
	}

	/**
	 * Returns the normal vector corresponding to the triangle defined by three
	 * vertices (a, b, c).
	 * 
	 * @param a
	 *            the triangle's first vertex.
	 * @param b
	 *            the triangle's second vertex.
	 * @param c
	 *            the triangle's third vertex.
	 * 
	 * @return the triangle's unit-length normal vector.
	 * 
	 * @throws IllegalArgumentException
	 *             if any of the specified vertices are null.
	 */
	public static WB_Vector computeTriangleNormal(WB_Vector a, WB_Vector b,
			WB_Vector c) {
		if (a == null || b == null || c == null) {
			throw new IllegalArgumentException();
		}

		double x = ((b.y - a.y) * (c.z - a.z)) - ((b.z - a.z) * (c.y - a.y));
		double y = ((b.z - a.z) * (c.x - a.x)) - ((b.x - a.x) * (c.z - a.z));
		double z = ((b.x - a.x) * (c.y - a.y)) - ((b.y - a.y) * (c.x - a.x));

		double length = (x * x) + (y * y) + (z * z);
		if (length == 0d)
			return new WB_Vector(x, y, z);

		length = Math.sqrt(length);
		return new WB_Vector(x / length, y / length, z / length);
	}

	/**
	 * Returns the area enclosed by the specified (x, y) points (the z and w
	 * coordinates are ignored). If the specified points do not define a closed
	 * loop, then the loop is automatically closed by simulating appending the
	 * first point to the last point.
	 * 
	 * @param points
	 *            the (x, y) points which define the 2D polygon.
	 * 
	 * @return the area enclosed by the specified coordinates.
	 * 
	 * @throws IllegalArgumentException
	 *             if points is null.
	 */
	public static double computePolygonAreaFromVertices(
			Iterable<? extends WB_Coordinate> points) {
		if (points == null) {
			throw new IllegalArgumentException();
		}

		java.util.Iterator<? extends WB_Coordinate> iter = points.iterator();
		if (!iter.hasNext()) {
			return 0;
		}

		double area = 0;
		WB_Coordinate firstPoint = iter.next();
		WB_Coordinate point = firstPoint;

		while (iter.hasNext()) {
			WB_Coordinate nextLocation = iter.next();

			area += point.xd() * nextLocation.yd();
			area -= nextLocation.xd() * point.yd();

			point = nextLocation;
		}

		// Include the area connecting the last point to the first point, if
		// they're not already equal.
		if (!point.equals(firstPoint)) {
			area += point.xd() * firstPoint.yd();
			area -= firstPoint.xd() * point.yd();
		}

		area /= 2.0;
		return area;
	}

	// **************************************************************//
	// ******************** Geometry Construction ******************//
	// **************************************************************//

}
