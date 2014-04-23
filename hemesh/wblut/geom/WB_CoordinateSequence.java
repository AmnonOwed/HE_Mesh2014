package wblut.geom;

import java.util.List;

public interface WB_CoordinateSequence<P extends WB_MutableCoordinate> {

	/**
	 * Get x of i'th tuple in sequence
	 * 
	 * @return x
	 */
	public double x(int i);

	/**
	 * Get y of i'th tuple in sequence
	 * 
	 * @return y
	 */
	public double y(int i);

	/**
	 * Get z of i'th tuple in sequence
	 * 
	 * @return z
	 */
	public double z(int i);

	/**
	 * Get i'th ordinate of j'th tuple in sequence
	 * 
	 * @return i'th ordinate
	 */
	public double get(int i, int j);

	public double getRaw(int i);

	/**
	 * Get i'th coordinate in sequence
	 * 
	 * @return i'th coordinate
	 */
	public P getCoordinate(int i);

	public double getLength(int i);

	public double getSqLength(int i);

	public double getDistance(int i, int j);

	public double getSqDistance(int i, int j);

	public double getDistance(int i, WB_Coordinate p);

	public double getSqDistance(int i, WB_Coordinate p);

	/**
	 * Get number of coordinates in sequence
	 * 
	 * @return number of coordinates
	 */
	public int size();

	public List<P> getAsList();

	public WB_CoordinateSequence<P> getCopy();

	public WB_CoordinateSequence<P> getSubSequence(int[] indices);

	public WB_CoordinateSequence<P> getSubSequence(int start, int end);

	// Operators on entire sequence

	public WB_CoordinateSequence<P> add(final WB_Coordinate p);

	public WB_CoordinateSequence<P> add(final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> add(final double x, double y, double z);

	public WB_CoordinateSequence<P> addMul(final double f, final WB_Coordinate p);

	public WB_CoordinateSequence<P> addMul(double f,
			final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> mulAddMul(final double f, final double g,
			final WB_Coordinate p);

	public WB_CoordinateSequence<P> mulAddMul(double f, double g,
			final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> sub(final WB_Coordinate p);

	public WB_CoordinateSequence<P> sub(final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> mul(final double f);

	public WB_CoordinateSequence<P> div(final double f);

	public double[] dot(final WB_Coordinate p);

	public double[] dot(final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> cross(final WB_Coordinate p);

	public WB_CoordinateSequence<P> cross(final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> normalize();

	public WB_CoordinateSequence<P> rescale(final double d);

	public double[] getAngle(final WB_Coordinate v);

	public double[] getAngle(final WB_CoordinateSequence seq, int i);

	public double[] getAngleNorm(final WB_Coordinate v);

	public double[] getAngleNorm(final WB_CoordinateSequence seq, int i);

	// UNSAFE!!!

	public WB_CoordinateSequence<P> _addSelf(WB_Coordinate p);

	public WB_CoordinateSequence<P> _addSelf(final WB_CoordinateSequence seq,
			int i);

	public WB_CoordinateSequence<P> _addSelf(final double x, double y, double z);

	public WB_CoordinateSequence<P> _addMulSelf(double f, WB_Coordinate p);

	public WB_CoordinateSequence<P> _addMulSelf(double f,
			final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> _mulAddMulSelf(double f, double g,
			WB_Coordinate p);

	public WB_CoordinateSequence<P> _mulAddMulSelf(double f, double g,
			final WB_CoordinateSequence seq, int i);

	public WB_CoordinateSequence<P> _subSelf(WB_Coordinate p);

	public WB_CoordinateSequence<P> _subSelf(final WB_CoordinateSequence seq,
			int i);

	public WB_CoordinateSequence<P> _subSelf(final double x, double y, double z);

	public WB_CoordinateSequence<P> _mulSelf(double f);

	public WB_CoordinateSequence<P> _divSelf(double g);

	public WB_CoordinateSequence<P> _crossSelf(WB_Coordinate p);

	public WB_CoordinateSequence<P> _crossSelf(final WB_CoordinateSequence seq,
			int i);

	public WB_CoordinateSequence<P> _crossSelf(final double x, double y,
			double z);

	public void _setAll(WB_Coordinate p);

	public void _setAll(final WB_CoordinateSequence seq, int i);

	public void _setAll(double x, double y, double z);

	public WB_CoordinateSequence<P> _normalizeSelf();

	// Operators on one index

	public P addI(int i, final WB_Coordinate p);

	public P addI(int i, final WB_CoordinateSequence seq, int j);

	public P addI(int i, final double x, double y, double z);

	public P addMulI(int i, final double f, final WB_Coordinate p);

	public P addMulI(int i, double f, final WB_CoordinateSequence seq, int j);

	public P mulAddMulI(int i, final double f, final double g,
			final WB_Coordinate p);

	public P mulAddMulI(int i, double f, double g,
			final WB_CoordinateSequence seq, int j);

	public P subI(int i, final WB_Coordinate p);

	public P subI(int i, final WB_CoordinateSequence seq, int j);

	public P mulI(int i, final double f);

	public P divI(int i, final double f);

	public double dotI(int i, final WB_Coordinate p);

	public double dotI(int i, final WB_CoordinateSequence seq, int j);

	public P crossI(int i, final WB_Coordinate p);

	public P crossI(int i, final WB_CoordinateSequence seq, int j);

	public P normalizeI(int i);

	public P rescaleI(int i, final double d);

	public double getAngleI(int i, final WB_Coordinate v);

	public double getAngleI(int i, final WB_CoordinateSequence seq, int j);

	public double getAngleNormI(int i, final WB_Coordinate v);

	public double getAngleNormI(int i, final WB_CoordinateSequence seq, int j);

	// UNSAFE!!!

	public WB_CoordinateSequence<P> _addSelfI(int i, WB_Coordinate p);

	public WB_CoordinateSequence<P> _addSelfI(int i,
			final WB_CoordinateSequence seq, int j);

	public WB_CoordinateSequence<P> _addSelfI(int i, final double x, double y,
			double z);

	public WB_CoordinateSequence<P> _addMulSelfI(int i, double f,
			WB_Coordinate p);

	public WB_CoordinateSequence<P> _addMulSelfI(int i, double f,
			final WB_CoordinateSequence seq, int j);

	public WB_CoordinateSequence<P> _mulAddMulSelfI(int i, double f, double g,
			WB_Coordinate p);

	public WB_CoordinateSequence<P> _mulAddMulSelfI(int i, double f, double g,
			final WB_CoordinateSequence seq, int j);

	public WB_CoordinateSequence<P> _subSelfI(int i, WB_Coordinate p);

	public WB_CoordinateSequence<P> _subSelfI(int i,
			final WB_CoordinateSequence seq, int j);

	public WB_CoordinateSequence<P> _subSelfI(int i, final double x, double y,
			double z);

	public WB_CoordinateSequence<P> _mulSelfI(int i, double f);

	public WB_CoordinateSequence<P> _divSelfI(int i, double g);

	public WB_CoordinateSequence<P> _crossSelfI(int i, WB_Coordinate p);

	public WB_CoordinateSequence<P> _crossSelfI(int i,
			final WB_CoordinateSequence seq, int j);

	public WB_CoordinateSequence<P> _crossSelfI(int i, final double x,
			double y, double z);

	public void _setI(int i, WB_Coordinate p);

	public void _setI(int i, final WB_CoordinateSequence seq, int j);

	public void _setI(int i, double x, double y, double z);

	public WB_CoordinateSequence<P> _normalizeSelfI(int i);

	public void addIntoI(int i, final WB_Coordinate p,
			WB_MutableCoordinate result);

	public void addIntoI(int i, final WB_CoordinateSequence seq, int j,
			WB_MutableCoordinate result);

	public void addIntoI(int i, final double x, double y, double z,
			WB_MutableCoordinate result);

	public void addMulIntoI(int i, final double f, final WB_Coordinate p,
			WB_MutableCoordinate result);

	public void addMulIntoI(int i, double f, final WB_CoordinateSequence seq,
			int j, WB_MutableCoordinate result);

	public void mulAddMulIntoI(int i, final double f, final double g,
			final WB_Coordinate p, WB_MutableCoordinate result);

	public void mulAddMulIntoI(int i, double f, double g,
			final WB_CoordinateSequence seq, int j, WB_MutableCoordinate result);

	public void subIntoI(int i, final WB_Coordinate p,
			WB_MutableCoordinate result);

	public void subIntoI(int i, final WB_CoordinateSequence seq, int j,
			WB_MutableCoordinate result);

	public void mulIntoI(int i, final double f, WB_MutableCoordinate result);

	public void divIntoI(int i, final double f, WB_MutableCoordinate result);

	public void crossIntoI(int i, final WB_Coordinate p,
			WB_MutableCoordinate result);

	public void crossIntoI(int i, final WB_CoordinateSequence seq, int j,
			WB_MutableCoordinate result);

	public void normalizeIntoI(int i, WB_MutableCoordinate result);

	public void rescaleIntoI(int i, final double d, WB_MutableCoordinate result);

	public WB_CoordinateSequence<P> applyAsPoint(WB_Transform T);

	public WB_CoordinateSequence<P> applyAsVector(WB_Transform T);

	public WB_CoordinateSequence<P> applyAsNormal(WB_Transform T);

	public WB_CoordinateSequence<P> _applyAsPointSelf(WB_Transform T);

	public WB_CoordinateSequence<P> _applyAsVectorSelf(WB_Transform T);

	public WB_CoordinateSequence<P> _applyAsNormalSelf(WB_Transform T);

	public P applyAsPointI(int i, WB_Transform T);

	public P applyAsVectorI(int i, WB_Transform T);

	public P applyAsNormalI(int i, WB_Transform T);

	public void _applyAsPointSelfI(int i, WB_Transform T);

	public void _applyAsVectorSelfI(int i, WB_Transform T);

	public void _applyAsNormalSelfI(int i, WB_Transform T);

	public void applyAsPointIntoI(int i, WB_Transform T,
			WB_MutableCoordinate result);

	public void applyAsVectorIntoI(int i, WB_Transform T,
			WB_MutableCoordinate result);

	public void applyAsNormalIntoI(int i, WB_Transform T,
			WB_MutableCoordinate result);

	public void applyAsPointIntoI(int i, WB_Transform T,
			WB_CoordinateSequence result, int j);

	public void applyAsVectorIntoI(int i, WB_Transform T,
			WB_CoordinateSequence result, int j);

	public void applyAsNormalIntoI(int i, WB_Transform T,
			WB_CoordinateSequence result, int j);

	public WB_AABB getAABB();

}
