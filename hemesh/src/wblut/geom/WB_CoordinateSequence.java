package wblut.geom;

import java.util.Collection;

public class WB_CoordinateSequence {
	private double[] ordinates;
	int n;
	double x1, y1, z1, x2, y2, z2;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_CoordinateSequence() {

	}

	protected WB_CoordinateSequence(
			final Collection<? extends WB_Coordinate> tuples) {
		ordinates = new double[4 * tuples.size()];
		n = tuples.size();
		int index = 0;
		for (final WB_Coordinate p : tuples) {
			ordinates[index++] = p.xd();
			ordinates[index++] = p.yd();
			ordinates[index++] = p.zd();
			ordinates[index++] = p.wd();
		}
	}

	protected WB_CoordinateSequence(final WB_Coordinate[] tuples) {
		ordinates = new double[4 * tuples.length];
		n = tuples.length;
		int index = 0;
		for (final WB_Coordinate p : tuples) {
			ordinates[index++] = p.xd();
			ordinates[index++] = p.yd();
			ordinates[index++] = p.zd();
			ordinates[index++] = p.wd();
		}
	}

	protected WB_CoordinateSequence(final WB_CoordinateSequence tuples) {
		ordinates = new double[4 * tuples.size()];
		n = tuples.size();
		final int index = 0;
		for (int i = 0; i < 4 * n; i++) {
			ordinates[i] = tuples.getRaw(i);

		}
	}

	protected WB_CoordinateSequence(final double[] ordinates) {
		this.ordinates = ordinates;
		n = ordinates.length / 4;
	}

	protected WB_CoordinateSequence(final double[][] tuples) {
		ordinates = new double[tuples.length * 4];
		n = tuples.length;
		int index = 0;
		for (final double[] p : tuples) {
			ordinates[index++] = p[0];
			ordinates[index++] = p[1];
			ordinates[index++] = p[2];
			ordinates[index++] = p[3];
		}
	}

	public double getX(final int i) {
		return ordinates[i << 2];
	}

	public double getY(final int i) {
		return ordinates[(i << 2) + 1];
	}

	public double getZ(final int i) {
		return ordinates[(i << 2) + 2];
	}

	public double get(final int i, final int j) {
		return ordinates[(i << 2) + j];
	}

	public double getRaw(final int i) {
		return ordinates[i];
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
		int id = i << 2;
		ordinates[id++] = p.xd();
		ordinates[id++] = p.yd();
		ordinates[id] = p.zd();
	}

	public void _set(final int i, final double x, final double y, final double z) {
		int id = i << 2;
		ordinates[id++] = x;
		ordinates[id++] = y;
		ordinates[id] = z;
	}

	public void _setRaw(final int i, final double v) {

		ordinates[i] = v;
	}

	public WB_AABB getAABB() {
		return new WB_AABB(this);
	}

	public WB_CoordinateSequence getSubSequence(final int[] indices) {
		final WB_CoordinateSequence subseq = new WB_CoordinateSequence();

		final int n = indices.length;
		final double[] subordinates = new double[3 * n];
		int id = 0;
		int fi;
		for (int i = 0; i < n; i++) {
			fi = 3 * indices[i];
			subordinates[id++] = ordinates[fi++];
			subordinates[id++] = ordinates[fi++];
			subordinates[id++] = ordinates[fi];
			id++;
		}
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	public WB_CoordinateSequence getSubSequence(final int start, final int end) {
		final WB_CoordinateSequence subseq = new WB_CoordinateSequence();

		final int n = end - start + 1;
		final double[] subordinates = new double[3 * n];
		final int id = 0;
		final int fi;
		System.arraycopy(ordinates, 3 * start, subordinates, 0, 3 * n);
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	public WB_CoordinateSequence getCopy() {
		final WB_CoordinateSequence subseq = new WB_CoordinateSequence();

		final int n = size();
		final double[] subordinates = new double[3 * n];
		System.arraycopy(ordinates, 0, subordinates, 0, 3 * n);
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	public WB_CoordinateSequence applyAsNormal(final WB_Transform T) {
		final WB_CoordinateSequence result = getCopy();
		return result._applyAsVectorSelf(T);
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
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id++];
			y1 = ordinates[id++];
			z1 = ordinates[id++];
			T.applyAsNormal(x1, y1, z1, getVector(j));
		}
		return this;
	}

	public WB_CoordinateSequence _applyAsPointSelf(final WB_Transform T) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id++];
			y1 = ordinates[id++];
			z1 = ordinates[id++];
			T.applyAsPoint(x1, y1, z1, getPoint(j));
		}
		return this;
	}

	public WB_CoordinateSequence _applyAsVectorSelf(final WB_Transform T) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id++];
			y1 = ordinates[id++];
			z1 = ordinates[id++];
			T.applyAsVector(x1, y1, z1, getVector(j));
		}
		return this;
	}

}
