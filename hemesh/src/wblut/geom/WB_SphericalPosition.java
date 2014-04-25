package wblut.geom;

import java.util.ArrayList;
import java.util.List;

import wblut.math.WB_Math;

public class WB_SphericalPosition extends WB_LatLon {
	public static final WB_SphericalPosition ZERO = new WB_SphericalPosition(WB_Angle.ZERO,
			WB_Angle.ZERO, 0d);

	public final double elevation;

	public static WB_SphericalPosition fromRadians(double latitude, double longitude,
			double elevation) {
		return new WB_SphericalPosition(WB_Angle.fromRadians(latitude),
				WB_Angle.fromRadians(longitude), elevation);
	}

	public static WB_SphericalPosition fromDegrees(double latitude, double longitude,
			double elevation) {
		return new WB_SphericalPosition(WB_Angle.fromDegrees(latitude),
				WB_Angle.fromDegrees(longitude), elevation);
	}

	public static WB_SphericalPosition fromDegrees(double latitude, double longitude) {
		return new WB_SphericalPosition(WB_Angle.fromDegrees(latitude),
				WB_Angle.fromDegrees(longitude), 0);
	}

	public WB_SphericalPosition(WB_Angle latitude, WB_Angle longitude, double elevation) {
		super(latitude, longitude);
		this.elevation = elevation;
	}

	public WB_SphericalPosition(WB_LatLon latLon, double elevation) {
		super(latLon);
		this.elevation = elevation;
	}

	// A class that makes it easier to pass around position lists.
	public static class PositionList {
		public List<? extends WB_SphericalPosition> list;

		public PositionList(List<? extends WB_SphericalPosition> list) {
			this.list = list;
		}
	}

	/**
	 * Obtains the elevation of this position
	 * 
	 * @return this position's elevation
	 */
	public double getElevation() {
		return this.elevation;
	}

	/**
	 * Obtains the elevation of this position
	 * 
	 * @return this position's elevation
	 */
	public double getAltitude() {
		return this.elevation;
	}

	public WB_SphericalPosition add(WB_SphericalPosition that) {
		WB_Angle lat = WB_Angle.normalizedLatitude(this.latitude
				.add(that.latitude));
		WB_Angle lon = WB_Angle.normalizedLongitude(this.longitude
				.add(that.longitude));

		return new WB_SphericalPosition(lat, lon, this.elevation + that.elevation);
	}

	public WB_SphericalPosition subtract(WB_SphericalPosition that) {
		WB_Angle lat = WB_Angle.normalizedLatitude(this.latitude
				.subtract(that.latitude));
		WB_Angle lon = WB_Angle.normalizedLongitude(this.longitude
				.subtract(that.longitude));

		return new WB_SphericalPosition(lat, lon, this.elevation - that.elevation);
	}

	/**
	 * Returns the linear interpolation of <code>value1</code> and
	 * <code>value2</code>, treating the geographic locations as simple 2D
	 * coordinate pairs, and treating the elevation values as 1D scalars.
	 * 
	 * @param amount
	 *            the interpolation factor
	 * @param value1
	 *            the first position.
	 * @param value2
	 *            the second position.
	 * 
	 * @return the linear interpolation of <code>value1</code> and
	 *         <code>value2</code>.
	 * 
	 * @throws IllegalArgumentException
	 *             if either position is null.
	 */
	public static WB_SphericalPosition interpolate(double amount, WB_SphericalPosition value1,
			WB_SphericalPosition value2) {
		if (value1 == null || value2 == null) {

			throw new IllegalArgumentException();
		}

		if (amount < 0)
			return value1;
		else if (amount > 1)
			return value2;

		WB_LatLon latLon = WB_LatLon.interpolate(amount, value1, value2);
		// Elevation is independent of geographic interpolation method (i.e.
		// rhumb, great-circle, linear), so we
		// interpolate elevation linearly.
		double elevation = WB_Math.mix(amount, value1.getElevation(),
				value2.getElevation());

		return new WB_SphericalPosition(latLon, elevation);
	}

	/**
	 * Returns the an interpolated location along the great-arc between
	 * <code>value1</code> and <code>value2</code>. The position's elevation
	 * components are linearly interpolated as a simple 1D scalar value. The
	 * interpolation factor <code>amount</code> defines the weight given to each
	 * value, and is clamped to the range [0, 1]. If <code>a</code> is 0 or
	 * less, this returns <code>value1</code>. If <code>amount</code> is 1 or
	 * more, this returns <code>value2</code>. Otherwise, this returns the
	 * position on the great-arc between <code>value1</code> and
	 * <code>value2</code> with a linearly interpolated elevation component, and
	 * corresponding to the specified interpolation factor.
	 * 
	 * @param amount
	 *            the interpolation factor
	 * @param value1
	 *            the first position.
	 * @param value2
	 *            the second position.
	 * 
	 * @return an interpolated position along the great-arc between
	 *         <code>value1</code> and <code>value2</code>, with a linearly
	 *         interpolated elevation component.
	 * 
	 * @throws IllegalArgumentException
	 *             if either location is null.
	 */
	public static WB_SphericalPosition interpolateGreatCircle(double amount,
			WB_SphericalPosition value1, WB_SphericalPosition value2) {
		if (value1 == null || value2 == null) {
			throw new IllegalArgumentException();
		}

		WB_LatLon latLon = WB_LatLon.interpolateGreatCircle(amount, value1,
				value2);
		// Elevation is independent of geographic interpolation method (i.e.
		// rhumb, great-circle, linear), so we
		// interpolate elevation linearly.
		double elevation = WB_Math.mix(amount, value1.getElevation(),
				value2.getElevation());

		return new WB_SphericalPosition(latLon, elevation);
	}

	/**
	 * Returns the an interpolated location along the rhumb line between
	 * <code>value1</code> and <code>value2</code>. The position's elevation
	 * components are linearly interpolated as a simple 1D scalar value. The
	 * interpolation factor <code>amount</code> defines the weight given to each
	 * value, and is clamped to the range [0, 1]. If <code>a</code> is 0 or
	 * less, this returns <code>value1</code>. If <code>amount</code> is 1 or
	 * more, this returns <code>value2</code>. Otherwise, this returns the
	 * position on the rhumb line between <code>value1</code> and
	 * <code>value2</code> with a linearly interpolated elevation component, and
	 * corresponding to the specified interpolation factor.
	 * 
	 * @param amount
	 *            the interpolation factor
	 * @param value1
	 *            the first position.
	 * @param value2
	 *            the second position.
	 * 
	 * @return an interpolated position along the great-arc between
	 *         <code>value1</code> and <code>value2</code>, with a linearly
	 *         interpolated elevation component.
	 * 
	 * @throws IllegalArgumentException
	 *             if either location is null.
	 */
	public static WB_SphericalPosition interpolateRhumb(double amount,
			WB_SphericalPosition value1, WB_SphericalPosition value2) {
		if (value1 == null || value2 == null) {
			throw new IllegalArgumentException();
		}

		WB_LatLon latLon = WB_LatLon.interpolateRhumb(amount, value1, value2);
		// Elevation is independent of geographic interpolation method (i.e.
		// rhumb, great-circle, linear), so we
		// interpolate elevation linearly.
		double elevation = WB_Math.mix(amount, value1.getElevation(),
				value2.getElevation());

		return new WB_SphericalPosition(latLon, elevation);
	}

	public static boolean positionsCrossDateLine(
			Iterable<? extends WB_SphericalPosition> positions) {
		if (positions == null) {
			throw new IllegalArgumentException();
		}

		WB_SphericalPosition pos = null;
		for (WB_SphericalPosition posNext : positions) {
			if (pos != null) {
				// A segment cross the line if end pos have different longitude
				// signs
				// and are more than 180 degress longitude apart
				if (Math.signum(pos.getLongitude().degrees) != Math
						.signum(posNext.getLongitude().degrees)) {
					double delta = Math.abs(pos.getLongitude().degrees
							- posNext.getLongitude().degrees);
					if (delta > 180 && delta < 360)
						return true;
				}
			}
			pos = posNext;
		}

		return false;
	}

	/**
	 * Computes a new set of positions translated from a specified reference
	 * position to a new reference position.
	 * 
	 * @param oldPosition
	 *            the original reference position.
	 * @param newPosition
	 *            the new reference position.
	 * @param positions
	 *            the positions to translate.
	 * 
	 * @return the translated positions, or null if the positions could not be
	 *         translated.
	 * 
	 * @throws IllegalArgumentException
	 *             if any argument is null.
	 */
	public static List<WB_SphericalPosition> computeShiftedPositions(
			WB_SphericalPosition oldPosition, WB_SphericalPosition newPosition,
			Iterable<? extends WB_SphericalPosition> positions) {
		// TODO: Account for dateline spanning
		if (oldPosition == null || newPosition == null) {
			throw new IllegalArgumentException();
		}

		if (positions == null) {

			throw new IllegalArgumentException();
		}

		ArrayList<WB_SphericalPosition> newPositions = new ArrayList<WB_SphericalPosition>();

		double elevDelta = newPosition.getElevation()
				- oldPosition.getElevation();

		for (WB_SphericalPosition pos : positions) {
			WB_Angle distance = WB_LatLon.greatCircleDistance(oldPosition, pos);
			WB_Angle azimuth = WB_LatLon.greatCircleAzimuth(oldPosition, pos);
			WB_LatLon newLocation = WB_LatLon.greatCircleEndPosition(
					newPosition, azimuth, distance);
			double newElev = pos.getElevation() + elevDelta;

			newPositions.add(new WB_SphericalPosition(newLocation, newElev));
		}

		return newPositions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		WB_SphericalPosition position = (WB_SphericalPosition) o;

		// noinspection RedundantIfStatement
		if (Double.compare(position.elevation, elevation) != 0)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		long temp;
		temp = elevation != +0.0d ? Double.doubleToLongBits(elevation) : 0L;
		result = 31 * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	public String toString() {
		return "(" + this.latitude.toString() + ", "
				+ this.longitude.toString() + ", " + this.elevation + ")";
	}
}
