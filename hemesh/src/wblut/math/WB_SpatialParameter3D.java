package wblut.math;


public class WB_SpatialParameter3D<T> implements WB_Parameter<T> {

	WB_Function3D<T> value;

	public WB_SpatialParameter3D(final WB_Function3D<T> value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.math.WB_Parameter#value()
	 */
	public T value() {

		return value.f(0, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.math.WB_Parameter#value(double)
	 */
	public T value(final double x) {

		return value.f(x, 0, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.math.WB_Parameter#value(double, double)
	 */
	public T value(final double x, final double y) {
		return value.f(x, y, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.math.WB_Parameter#value(double, double, double)
	 */
	public T value(final double x, final double y, final double z) {
		return value.f(x, y, z);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.math.WB_Parameter#value(double, double, double, double)
	 */
	public T value(final double x, final double y, final double z,
			final double t) {
		return value.f(x, y, z);
	}

}