package wblut.math;

import wblut.geom.WB_Angle;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_LatLon;
import wblut.geom.WB_Vector;

public class WB_Quaternion {
	// Multiplicative identity quaternion.
	public static final WB_Quaternion IDENTITY = new WB_Quaternion(0, 0, 0, 1);

	public final double x;
	public final double y;
	public final double z;
	public final double w;

	// 4 values in a quaternion.
	private static final int NUM_ELEMENTS = 4;
	// Cached computations.
	private int hashCode;

	public WB_Quaternion(final double x, final double y, final double z,
			final double w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}

		final WB_Quaternion that = (WB_Quaternion) obj;
		return (this.x == that.x) && (this.y == that.y) && (this.z == that.z)
				&& (this.w == that.w);
	}

	@Override
	public final int hashCode() {
		if (this.hashCode == 0) {
			int result;
			long tmp;
			tmp = Double.doubleToLongBits(this.x);
			result = (int) (tmp ^ (tmp >>> 32));
			tmp = Double.doubleToLongBits(this.y);
			result = 31 * result + (int) (tmp ^ (tmp >>> 32));
			tmp = Double.doubleToLongBits(this.z);
			result = 31 * result + (int) (tmp ^ (tmp >>> 32));
			tmp = Double.doubleToLongBits(this.w);
			result = 31 * result + (int) (tmp ^ (tmp >>> 32));
			this.hashCode = result;
		}
		return this.hashCode;
	}

	public static WB_Quaternion fromArray(final double[] compArray,
			final int offset) {
		if (compArray == null) {

			throw new IllegalArgumentException();
		}
		if ((compArray.length - offset) < NUM_ELEMENTS) {

			throw new IllegalArgumentException();
		}

		// noinspection PointlessArithmeticExpression
		return new WB_Quaternion(compArray[0 + offset], compArray[1 + offset],
				compArray[2 + offset], compArray[3 + offset]);
	}

	public final double[] toArray(final double[] compArray, final int offset) {
		if (compArray == null) {

			throw new IllegalArgumentException();
		}
		if ((compArray.length - offset) < NUM_ELEMENTS) {
			throw new IllegalArgumentException();
		}

		// noinspection PointlessArithmeticExpression
		compArray[0 + offset] = this.x;
		compArray[1 + offset] = this.y;
		compArray[2 + offset] = this.z;
		compArray[3 + offset] = this.w;
		return compArray;
	}

	@Override
	public final String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		sb.append(this.x).append(", ");
		sb.append(this.y).append(", ");
		sb.append(this.z).append(", ");
		sb.append(this.w);
		sb.append(")");
		return sb.toString();
	}

	public final double getX() {
		return this.x;
	}

	public final double getY() {
		return this.y;
	}

	public final double getZ() {
		return this.z;
	}

	public final double getW() {
		return this.w;
	}

	public final double x() {
		return this.x;
	}

	public final double y() {
		return this.y;
	}

	public final double z() {
		return this.z;
	}

	public final double w() {
		return this.w;
	}

	public static WB_Quaternion fromAxisAngle(final WB_Angle angle,
			final WB_Coordinate axis) {
		if (angle == null) {

			throw new IllegalArgumentException();
		}
		if (axis == null) {
			throw new IllegalArgumentException();
		}

		return fromAxisAngle(angle, axis.xd(), axis.yd(), axis.zd(), true);
	}

	public static WB_Quaternion fromAxisAngle(final WB_Angle angle,
			final double axisX, final double axisY, final double axisZ) {
		if (angle == null) {

			throw new IllegalArgumentException();
		}
		return fromAxisAngle(angle, axisX, axisY, axisZ, true);
	}

	private static WB_Quaternion fromAxisAngle(final WB_Angle angle,
			double axisX, double axisY, double axisZ, final boolean normalize) {
		if (angle == null) {

			throw new IllegalArgumentException();
		}

		if (normalize) {
			final double length = Math.sqrt((axisX * axisX) + (axisY * axisY)
					+ (axisZ * axisZ));
			if (!isZero(length) && (length != 1.0)) {
				axisX /= length;
				axisY /= length;
				axisZ /= length;
			}
		}

		final double s = angle.sinHalfAngle();
		final double c = angle.cosHalfAngle();
		return new WB_Quaternion(axisX * s, axisY * s, axisZ * s, c);
	}

	public static WB_Quaternion fromMatrix(final WB_M33 matrix) {
		if (matrix == null) {

			throw new IllegalArgumentException();
		}

		final double t = 1.0 + matrix.m11 + matrix.m22 + matrix.m33;
		double x, y, z, w;
		double s;
		final double EPSILON = 0.00000001;
		if (t > EPSILON) {
			s = 2.0 * Math.sqrt(t);
			x = (matrix.m32 - matrix.m23) / s;
			y = (matrix.m13 - matrix.m31) / s;
			z = (matrix.m21 - matrix.m12) / s;
			w = s / 4.0;
		}
		else if ((matrix.m11 > matrix.m22) && (matrix.m11 > matrix.m33)) {
			s = 2.0 * Math.sqrt(1.0 + matrix.m11 - matrix.m22 - matrix.m33);
			x = s / 4.0;
			y = (matrix.m21 + matrix.m12) / s;
			z = (matrix.m13 + matrix.m31) / s;
			w = (matrix.m32 - matrix.m23) / s;
		}
		else if (matrix.m22 > matrix.m33) {
			s = 2.0 * Math.sqrt(1.0 + matrix.m22 - matrix.m11 - matrix.m33);
			x = (matrix.m21 + matrix.m12) / s;
			y = s / 4.0;
			z = (matrix.m32 + matrix.m23) / s;
			w = (matrix.m13 - matrix.m31) / s;
		}
		else {
			s = 2.0 * Math.sqrt(1.0 + matrix.m33 - matrix.m11 - matrix.m22);
			x = (matrix.m13 + matrix.m31) / s;
			y = (matrix.m32 + matrix.m23) / s;
			z = s / 4.0;
			w = (matrix.m21 - matrix.m12) / s;
		}
		return new WB_Quaternion(x, y, z, w);
	}

	/**
	 * Returns a WB_Quaternion created from three Euler angle rotations. The
	 * angles represent rotation about their respective unit-axes. The angles
	 * are applied in the order X, Y, Z. Angles can be extracted by calling
	 * {@link #getRotationX}, {@link #getRotationY}, {@link #getRotationZ}.
	 *
	 * @param x
	 *            WB_Angle rotation about unit-X axis.
	 * @param y
	 *            WB_Angle rotation about unit-Y axis.
	 * @param z
	 *            WB_Angle rotation about unit-Z axis.
	 * @return WB_Quaternion representation of the combined X-Y-Z rotation.
	 */
	public static WB_Quaternion fromRotationXYZ(final WB_Angle x,
			final WB_Angle y, final WB_Angle z) {
		if (x == null || y == null || z == null) {

			throw new IllegalArgumentException();
		}

		final double cx = x.cosHalfAngle();
		final double cy = y.cosHalfAngle();
		final double cz = z.cosHalfAngle();
		final double sx = x.sinHalfAngle();
		final double sy = y.sinHalfAngle();
		final double sz = z.sinHalfAngle();

		// The order in which the three Euler angles are applied is critical.
		// This can be thought of as multiplying
		// three quaternions together, one for each Euler angle (and
		// corresponding unit axis). Like matrices,
		// quaternions affect vectors in reverse order. For example, suppose we
		// construct a quaternion
		// Q = (QX * QX) * QZ
		// then transform some vector V by Q. This can be thought of as first
		// transforming V by QZ, then QY, and
		// finally by QX. This means that the order of quaternion multiplication
		// is the reverse of the order in which
		// the Euler angles are applied.
		//
		// The ordering below refers to the order in which angles are applied.
		//
		// QX = (sx, 0, 0, cx)
		// QY = (0, sy, 0, cy)
		// QZ = (0, 0, sz, cz)
		//
		// 1. XYZ Ordering
		// (QZ * QY * QX)
		// qw = (cx * cy * cz) + (sx * sy * sz);
		// qx = (sx * cy * cz) - (cx * sy * sz);
		// qy = (cx * sy * cz) + (sx * cy * sz);
		// qz = (cx * cy * sz) - (sx * sy * cz);
		//
		// 2. ZYX Ordering
		// (QX * QY * QZ)
		// qw = (cx * cy * cz) - (sx * sy * sz);
		// qx = (sx * cy * cz) + (cx * sy * sz);
		// qy = (cx * sy * cz) - (sx * cy * sz);
		// qz = (cx * cy * sz) + (sx * sy * cz);
		//

		final double qw = (cx * cy * cz) + (sx * sy * sz);
		final double qx = (sx * cy * cz) - (cx * sy * sz);
		final double qy = (cx * sy * cz) + (sx * cy * sz);
		final double qz = (cx * cy * sz) - (sx * sy * cz);

		return new WB_Quaternion(qx, qy, qz, qw);
	}

	/**
	 * Returns a WB_Quaternion created from latitude and longitude rotations.
	 * Latitude and longitude can be extracted from a WB_Quaternion by calling
	 * {@link #getLatLon}.
	 *
	 * @param latitude
	 *            WB_Angle rotation of latitude.
	 * @param longitude
	 *            WB_Angle rotation of longitude.
	 * @return WB_Quaternion representing combined latitude and longitude
	 *         rotation.
	 */
	public static WB_Quaternion fromLatLon(final WB_Angle latitude,
			final WB_Angle longitude) {
		if (latitude == null || longitude == null) {

			throw new IllegalArgumentException();
		}

		final double clat = latitude.cosHalfAngle();
		final double clon = longitude.cosHalfAngle();
		final double slat = latitude.sinHalfAngle();
		final double slon = longitude.sinHalfAngle();

		// The order in which the lat/lon angles are applied is critical. This
		// can be thought of as multiplying two
		// quaternions together, one for each lat/lon angle. Like matrices,
		// quaternions affect vectors in reverse
		// order. For example, suppose we construct a quaternion
		// Q = QLat * QLon
		// then transform some vector V by Q. This can be thought of as first
		// transforming V by QLat, then QLon. This
		// means that the order of quaternion multiplication is the reverse of
		// the order in which the lat/lon angles
		// are applied.
		//
		// The ordering below refers to order in which angles are applied.
		//
		// QLat = (0, slat, 0, clat)
		// QLon = (slon, 0, 0, clon)
		//
		// 1. WB_LatLon Ordering
		// (QLon * QLat)
		// qw = clat * clon;
		// qx = clat * slon;
		// qy = slat * clon;
		// qz = slat * slon;
		//
		// 2. LonLat Ordering
		// (QLat * QLon)
		// qw = clat * clon;
		// qx = clat * slon;
		// qy = slat * clon;
		// qz = - slat * slon;
		//

		final double qw = clat * clon;
		final double qx = clat * slon;
		final double qy = slat * clon;
		final double qz = 0.0 - slat * slon;

		return new WB_Quaternion(qx, qy, qz, qw);
	}

	public final WB_Quaternion add(final WB_Quaternion quaternion) {
		if (quaternion == null) {

			throw new IllegalArgumentException();
		}

		return new WB_Quaternion(this.x + quaternion.x, this.y + quaternion.y,
				this.z + quaternion.z, this.w + quaternion.w);
	}

	public final WB_Quaternion subtract(final WB_Quaternion quaternion) {
		if (quaternion == null) {
			throw new IllegalArgumentException();
		}

		return new WB_Quaternion(this.x - quaternion.x, this.y - quaternion.y,
				this.z - quaternion.z, this.w - quaternion.w);
	}

	public final WB_Quaternion multiplyComponents(final double value) {
		return new WB_Quaternion(this.x * value, this.y * value,
				this.z * value, this.w * value);
	}

	public final WB_Quaternion multiply(final WB_Quaternion quaternion) {
		if (quaternion == null) {

			throw new IllegalArgumentException();
		}

		return new WB_Quaternion((this.w * quaternion.x)
				+ (this.x * quaternion.w) + (this.y * quaternion.z)
				- (this.z * quaternion.y), (this.w * quaternion.y)
				+ (this.y * quaternion.w) + (this.z * quaternion.x)
				- (this.x * quaternion.z), (this.w * quaternion.z)
				+ (this.z * quaternion.w) + (this.x * quaternion.y)
				- (this.y * quaternion.x), (this.w * quaternion.w)
				- (this.x * quaternion.x) - (this.y * quaternion.y)
				- (this.z * quaternion.z));
	}

	public final WB_Quaternion divideComponents(final double value) {
		if (isZero(value)) {
			throw new IllegalArgumentException();
		}

		return new WB_Quaternion(this.x / value, this.y / value,
				this.z / value, this.w / value);
	}

	public final WB_Quaternion divideComponents(final WB_Quaternion quaternion) {
		if (quaternion == null) {

			throw new IllegalArgumentException();
		}

		return new WB_Quaternion(this.x / quaternion.x, this.y / quaternion.y,
				this.z / quaternion.z, this.w / quaternion.w);
	}

	public final WB_Quaternion getConjugate() {
		return new WB_Quaternion(0.0 - this.x, 0.0 - this.y, 0.0 - this.z,
				this.w);
	}

	public final WB_Quaternion getNegative() {
		return new WB_Quaternion(0.0 - this.x, 0.0 - this.y, 0.0 - this.z,
				0.0 - this.w);
	}

	public final double getLength() {
		return Math.sqrt(this.getLengthSquared());
	}

	public final double getLengthSquared() {
		return (this.x * this.x) + (this.y * this.y) + (this.z * this.z)
				+ (this.w * this.w);
	}

	public final WB_Quaternion normalize() {
		final double length = this.getLength();
		// Vector has zero length.
		if (isZero(length)) {
			return this;
		}
		else {
			return new WB_Quaternion(this.x / length, this.y / length, this.z
					/ length, this.w / length);
		}
	}

	public final double dot(final WB_Quaternion quaternion) {
		if (quaternion == null) {

			throw new IllegalArgumentException();
		}

		return (this.x * quaternion.x) + (this.y * quaternion.y)
				+ (this.z * quaternion.z) + (this.w * quaternion.w);
	}

	public final WB_Quaternion getInverse() {
		final double length = this.getLength();
		// Vector has zero length.
		if (isZero(length)) {
			return this;
		}
		else {
			return new WB_Quaternion((0.0 - this.x) / length, (0.0 - this.y)
					/ length, (0.0 - this.z) / length, this.w / length);
		}
	}

	public static WB_Quaternion mix(final double amount,
			final WB_Quaternion value1, final WB_Quaternion value2) {
		if ((value1 == null) || (value2 == null)) {
			throw new IllegalArgumentException();
		}

		if (amount < 0.0) {
			return value1;
		}
		else if (amount > 1.0) {
			return value2;
		}

		final double t1 = 1.0 - amount;
		return new WB_Quaternion((value1.x * t1) + (value2.x * amount),
				(value1.y * t1) + (value2.y * amount), (value1.z * t1)
				+ (value2.z * amount), (value1.w * t1)
				+ (value2.w * amount));
	}

	public static WB_Quaternion slerp(final double amount,
			final WB_Quaternion value1, final WB_Quaternion value2) {
		if ((value1 == null) || (value2 == null)) {
			throw new IllegalArgumentException();
		}

		if (amount < 0.0) {
			return value1;
		}
		else if (amount > 1.0) {
			return value2;
		}

		double dot = value1.dot(value2);
		double x2, y2, z2, w2;
		if (dot < 0.0) {
			dot = 0.0 - dot;
			x2 = 0.0 - value2.x;
			y2 = 0.0 - value2.y;
			z2 = 0.0 - value2.z;
			w2 = 0.0 - value2.w;
		}
		else {
			x2 = value2.x;
			y2 = value2.y;
			z2 = value2.z;
			w2 = value2.w;
		}

		double t1, t2;

		final double EPSILON = 0.0001;
		if ((1.0 - dot) > EPSILON) // standard case (slerp)
		{
			final double angle = Math.acos(dot);
			final double sinAngle = Math.sin(angle);
			t1 = Math.sin((1.0 - amount) * angle) / sinAngle;
			t2 = Math.sin(amount * angle) / sinAngle;
		}
		else // just lerp
		{
			t1 = 1.0 - amount;
			t2 = amount;
		}

		return new WB_Quaternion((value1.x * t1) + (x2 * t2), (value1.y * t1)
				+ (y2 * t2), (value1.z * t1) + (z2 * t2), (value1.w * t1)
				+ (w2 * t2));
	}

	// ============== Accessor Functions ======================= //
	// ============== Accessor Functions ======================= //
	// ============== Accessor Functions ======================= //

	public final WB_Angle getAngle() {
		double w = this.w;

		final double length = this.getLength();
		if (!isZero(length) && (length != 1.0)) {
			w /= length;
		}

		final double radians = 2.0 * Math.acos(WB_Math.clamp(w, -1, 1));
		if (Double.isNaN(radians)) {
			return null;
		}

		return WB_Angle.fromRadians(radians);
	}

	public final WB_Vector getAxis() {
		double x = this.x;
		double y = this.y;
		double z = this.z;

		final double length = this.getLength();
		if (!isZero(length) && (length != 1.0)) {
			x /= length;
			y /= length;
			z /= length;
		}

		final double vecLength = Math.sqrt((x * x) + (y * y) + (z * z));
		if (!isZero(vecLength) && (vecLength != 1.0)) {
			x /= vecLength;
			y /= vecLength;
			z /= vecLength;
		}

		return new WB_Vector(x, y, z);
	}

	public final WB_Angle getRotationX() {
		final double radians = Math.atan2((2.0 * this.x * this.w)
				- (2.0 * this.y * this.z), 1.0 - 2.0 * (this.x * this.x) - 2.0
				* (this.z * this.z));
		if (Double.isNaN(radians)) {
			return null;
		}

		return WB_Angle.fromRadians(radians);
	}

	public final WB_Angle getRotationY() {
		final double radians = Math.atan2((2.0 * this.y * this.w)
				- (2.0 * this.x * this.z), 1.0 - (2.0 * this.y * this.y)
				- (2.0 * this.z * this.z));
		if (Double.isNaN(radians)) {
			return null;
		}

		return WB_Angle.fromRadians(radians);
	}

	public final WB_Angle getRotationZ() {
		final double radians = Math.asin((2.0 * this.x * this.y)
				+ (2.0 * this.z * this.w));
		if (Double.isNaN(radians)) {
			return null;
		}

		return WB_Angle.fromRadians(radians);
	}

	public final WB_LatLon getLatLon() {
		final double latRadians = Math.asin((2.0 * this.y * this.w)
				- (2.0 * this.x * this.z));
		final double lonRadians = Math.atan2((2.0 * this.y * this.z)
				+ (2.0 * this.x * this.w), (this.w * this.w)
				- (this.x * this.x) - (this.y * this.y) + (this.z * this.z));
		if (Double.isNaN(latRadians) || Double.isNaN(lonRadians)) {
			return null;
		}

		return WB_LatLon.fromRadians(latRadians, lonRadians);
	}

	private static final Double PositiveZero = +0.0d;

	private static final Double NegativeZero = -0.0d;

	private static boolean isZero(final double value) {
		return (PositiveZero.compareTo(value) == 0)
				|| (NegativeZero.compareTo(value) == 0);
	}
}
