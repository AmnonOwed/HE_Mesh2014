/*
 *
 */
package wblut.geom;

import gnu.trove.list.array.TDoubleArrayList;
import java.util.Collection;

/**
 * Storing lots and lots of WB_Coordinates can fill the Java Heap Memory.
 * WB_CoordinateSequence tries to avoid this by storing the coordinates in a
 * single TDoubleArrayList. A WB_SequenceVector or WB_SequencePoint adds a view
 * in this data structure that acts identical to a WB_Vector or WB_Point.
 *
 */
public class WB_CoordinateSequence {
    /**
     *
     */
    private TDoubleArrayList ordinates;
    /**
     *
     */
    private int n;
    /**
     *
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     *
     */
    protected WB_CoordinateSequence() {
    }

    /**
     *
     *
     * @param tuples
     */
    protected WB_CoordinateSequence(
	    final Collection<? extends WB_Coordinate> tuples) {
	ordinates = new TDoubleArrayList(4 * tuples.size(), Double.NaN);
	n = tuples.size();
	for (final WB_Coordinate p : tuples) {
	    ordinates.add(p.xd());
	    ordinates.add(p.yd());
	    ordinates.add(p.zd());
	    ordinates.add(p.wd());
	}
    }

    /**
     *
     *
     * @param tuples
     */
    protected WB_CoordinateSequence(final WB_Coordinate[] tuples) {
	ordinates = new TDoubleArrayList(4 * tuples.length, Double.NaN);
	n = tuples.length;
	for (final WB_Coordinate p : tuples) {
	    ordinates.add(p.xd());
	    ordinates.add(p.yd());
	    ordinates.add(p.zd());
	    ordinates.add(p.wd());
	}
    }

    /**
     *
     *
     * @param tuples
     */
    protected WB_CoordinateSequence(final WB_CoordinateSequence tuples) {
	ordinates = new TDoubleArrayList(4 * tuples.size(), Double.NaN);
	n = tuples.size();
	for (int i = 0; i < (4 * n); i++) {
	    ordinates.add(tuples.getRaw(i));
	}
    }

    /**
     *
     *
     * @param ordinates
     */
    protected WB_CoordinateSequence(final double[] ordinates) {
	this.ordinates = new TDoubleArrayList(ordinates.length, Double.NaN);
	n = ordinates.length / 4;
	for (int i = 0; i < ordinates.length; i++) {
	    this.ordinates.add(ordinates[i]);
	}
    }

    /**
     *
     *
     * @param tuples
     */
    protected WB_CoordinateSequence(final double[][] tuples) {
	ordinates = new TDoubleArrayList(tuples.length, Double.NaN);
	n = tuples.length;
	for (final double[] p : tuples) {
	    ordinates.add(p[0]);
	    ordinates.add(p[1]);
	    ordinates.add(p[2]);
	    ordinates.add(p[3]);
	}
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public double getX(final int i) {
	return ordinates.get(i * 4);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public double getY(final int i) {
	return ordinates.get((i * 4) + 1);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public double getZ(final int i) {
	return ordinates.get((i * 4) + 2);
    }

    /**
     *
     *
     * @param i
     * @param j
     * @return
     */
    public double get(final int i, final int j) {
	return ordinates.get((i * 4) + j);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public double getRaw(final int i) {
	return ordinates.get(i);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public WB_SequencePoint getPoint(final int i) {
	if (i >= n) {
	    throw (new IndexOutOfBoundsException());
	}
	return new WB_SequencePoint(i, this);
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public WB_SequenceVector getVector(final int i) {
	if (i >= n) {
	    throw (new IndexOutOfBoundsException());
	}
	return new WB_SequenceVector(i, this);
    }

    /**
     *
     *
     * @return
     */
    public int size() {
	return n;
    }

    /**
     *
     *
     * @param i
     * @param p
     */
    public void _set(final int i, final WB_Coordinate p) {
	int id = i * 4;
	ordinates.set(id++, p.xd());
	ordinates.set(id++, p.yd());
	ordinates.set(id++, p.zd());
	ordinates.set(id, p.wd());
    }

    /**
     *
     *
     * @param i
     * @param x
     * @param y
     * @param z
     */
    public void _set(final int i, final double x, final double y, final double z) {
	int id = i * 4;
	ordinates.set(id++, x);
	ordinates.set(id++, y);
	ordinates.set(id, z);
    }

    /**
     *
     *
     * @param i
     * @param v
     */
    public void _setRaw(final int i, final double v) {
	ordinates.set(i, v);
    }

    /**
     *
     *
     * @return
     */
    public WB_AABB getAABB() {
	return new WB_AABB(this);
    }

    /**
     *
     *
     * @param indices
     * @return
     */
    public WB_CoordinateSequence getSubSequence(final int[] indices) {
	final WB_CoordinateSequence subseq = new WB_CoordinateSequence();
	final int n = indices.length;
	final TDoubleArrayList subordinates = new TDoubleArrayList(4 * n,
		Double.NaN);
	int fi;
	for (int i = 0; i < n; i++) {
	    fi = 4 * indices[i];
	    subordinates.add(ordinates.get(fi++));
	    subordinates.add(ordinates.get(fi++));
	    subordinates.add(ordinates.get(fi++));
	    subordinates.add(ordinates.get(fi));
	}
	subseq.ordinates = subordinates;
	subseq.n = n;
	return subseq;
    }

    /**
     *
     *
     * @param start
     * @param end
     * @return
     */
    public WB_CoordinateSequence getSubSequence(final int start, final int end) {
	final WB_CoordinateSequence subseq = new WB_CoordinateSequence();
	final int n = (end - start) + 1;
	final TDoubleArrayList subordinates = new TDoubleArrayList(4 * n,
		Double.NaN);
	System.arraycopy(ordinates, 4 * start, subordinates, 0, 4 * n);
	subseq.ordinates = subordinates;
	subseq.n = n;
	return subseq;
    }

    /**
     *
     *
     * @return
     */
    public WB_CoordinateSequence getCopy() {
	final WB_CoordinateSequence subseq = new WB_CoordinateSequence();
	final TDoubleArrayList subordinates = new TDoubleArrayList(4 * n,
		Double.NaN);
	System.arraycopy(ordinates, 0, subordinates, 0, 4 * n);
	subseq.ordinates = subordinates;
	subseq.n = n;
	return subseq;
    }

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_CoordinateSequence applyAsNormal(final WB_Transform T) {
	final WB_CoordinateSequence result = getCopy();
	return result.applyAsNormalSelf(T);
    }

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_CoordinateSequence applyAsPoint(final WB_Transform T) {
	final WB_CoordinateSequence result = getCopy();
	return result.applyAsPointSelf(T);
    }

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_CoordinateSequence applyAsVector(final WB_Transform T) {
	final WB_CoordinateSequence result = getCopy();
	return result.applyAsVectorSelf(T);
    }

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_CoordinateSequence applyAsNormalSelf(final WB_Transform T) {
	int id = 0;
	double x1, y1, z1;
	for (int j = 0; j < size(); j++) {
	    x1 = ordinates.get(id++);
	    y1 = ordinates.get(id++);
	    z1 = ordinates.get(id++);
	    id++;
	    T.applyAsNormal(x1, y1, z1, getVector(j));
	}
	return this;
    }

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_CoordinateSequence applyAsPointSelf(final WB_Transform T) {
	int id = 0;
	double x1, y1, z1;
	for (int j = 0; j < size(); j++) {
	    x1 = ordinates.get(id++);
	    y1 = ordinates.get(id++);
	    z1 = ordinates.get(id++);
	    id++;
	    T.applyAsPoint(x1, y1, z1, getPoint(j));
	}
	return this;
    }

    /**
     *
     *
     * @param T
     * @return
     */
    public WB_CoordinateSequence applyAsVectorSelf(final WB_Transform T) {
	int id = 0;
	double x1, y1, z1;
	for (int j = 0; j < size(); j++) {
	    x1 = ordinates.get(id++);
	    y1 = ordinates.get(id++);
	    z1 = ordinates.get(id++);
	    id++;
	    T.applyAsVector(x1, y1, z1, getVector(j));
	}
	return this;
    }
}