package wblut.geom;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Collection;

public class WB_CoordinateSequence {
	private TDoubleArrayList ordinates;
	private int n;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_CoordinateSequence() {

	}

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

	protected WB_CoordinateSequence(final WB_CoordinateSequence tuples) {
		ordinates = new TDoubleArrayList(4 * tuples.size(), Double.NaN);
		n = tuples.size();

		for (int i = 0; i < 4 * n; i++) {
			ordinates.add(tuples.getRaw(i));

		}
	}

	protected WB_CoordinateSequence(final double[] ordinates) {
		this.ordinates = new TDoubleArrayList(ordinates.length, Double.NaN);
		n = ordinates.length / 4;
		for (int i = 0; i < ordinates.length; i++) {
			this.ordinates.add(ordinates[i]);

		}
	}

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

	public double getX(final int i) {
		return ordinates.get(i * 4);
	}

	public double getY(final int i) {
		return ordinates.get(i * 4 + 1);
	}

	public double getZ(final int i) {
		return ordinates.get(i * 4 + 2);
	}

	public double get(final int i, final int j) {
		return ordinates.get(i * 4 + j);
	}

	public double getRaw(final int i) {
		return ordinates.get(i);
	}

	public WB_IndexedPoint getPoint(final int i) {
		if (i >= n) {
			throw (new IndexOutOfBoundsException());
		}
		return new WB_IndexedPoint(i, this);
	}

	public WB_IndexedVector getVector(final int i) {
		if (i >= n) {
			throw (new IndexOutOfBoundsException());
		}
		return new WB_IndexedVector(i, this);
	}

	public int size() {
		return n;
	}

	public void _set(final int i, final WB_Coordinate p) {
		int id = i * 4;
		ordinates.set(id++, p.xd());
		ordinates.set(id++, p.yd());
		ordinates.set(id++, p.zd());
		ordinates.set(id, p.wd());
	}

	public void _set(final int i, final double x, final double y, final double z) {
		int id = i * 4;
		ordinates.set(id++, x);
		ordinates.set(id++, y);
		ordinates.set(id, z);
	}

	public void _setRaw(final int i, final double v) {

		ordinates.set(i, v);

	}

	public WB_AABB getAABB() {
		return new WB_AABB(this);
	}

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

	public WB_CoordinateSequence getSubSequence(final int start, final int end) {
		final WB_CoordinateSequence subseq = new WB_CoordinateSequence();

		final int n = end - start + 1;
		final TDoubleArrayList subordinates = new TDoubleArrayList(4 * n,
				Double.NaN);

		System.arraycopy(ordinates, 4 * start, subordinates, 0, 4 * n);
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	public WB_CoordinateSequence getCopy() {
		final WB_CoordinateSequence subseq = new WB_CoordinateSequence();

		final TDoubleArrayList subordinates = new TDoubleArrayList(4 * n,
				Double.NaN);

		System.arraycopy(ordinates, 0, subordinates, 0, 4 * n);
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	public WB_CoordinateSequence applyAsNormal(final WB_Transform T) {
		final WB_CoordinateSequence result = getCopy();
		return result._applyAsNormalSelf(T);
	}

	public WB_CoordinateSequence applyAsPoint(final WB_Transform T) {
		final WB_CoordinateSequence result = getCopy();
		return result._applyAsPointSelf(T);
	}

	public WB_CoordinateSequence applyAsVector(final WB_Transform T) {
		final WB_CoordinateSequence result = getCopy();
		return result._applyAsVectorSelf(T);
	}

	public WB_CoordinateSequence _applyAsNormalSelf(final WB_Transform T) {
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

	public WB_CoordinateSequence _applyAsPointSelf(final WB_Transform T) {
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

	public WB_CoordinateSequence _applyAsVectorSelf(final WB_Transform T) {
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