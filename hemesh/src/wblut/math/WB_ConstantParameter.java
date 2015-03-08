/*
 * 
 */
package wblut.math;

/**
 * The Class WB_ConstantParameter.
 *
 * @author Frederik Vanhoutte, W:Blut
 * 
 *         A parameter which is constant, i.e. a single unchanging value. As a
 *         generic class the value needs to be an object. For example, Double,
 *         Float or Integer. Primitives such as double, float or int cannot be
 *         passed as objects.
 * @param <T>            the generic type
 */
public class WB_ConstantParameter<T> implements WB_Parameter<T> {
    /** The value. */
    T value;

    /**
     * Instantiates a new w b_ constant parameter.
     * 
     * @param value
     *            constant value.
     */
    public WB_ConstantParameter(final T value) {
	this.value = value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.math.WB_Parameter#value(double, double, double)
     */
    @Override
    public T value(final double x, final double y, final double z) {
	return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.math.WB_Parameter#value()
     */
    @Override
    public T value() {
	return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.math.WB_Parameter#value(double)
     */
    @Override
    public T value(final double x) {
	return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.math.WB_Parameter#value(double, double)
     */
    @Override
    public T value(final double x, final double y) {
	return value;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.math.WB_Parameter#value(double, double, double, double)
     */
    @Override
    public T value(final double x, final double y, final double z,
	    final double t) {
	return value;
    }
}
