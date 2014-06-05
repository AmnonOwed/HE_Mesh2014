package wblut.geom;

import java.util.Collection;
import java.util.List;

import javolution.util.FastTable;

public class WB_VectorSequence implements WB_CoordinateSequence<WB_Vector> {
	double[] ordinates;
	int n;
	double x1, y1, z1, x2, y2, z2;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_VectorSequence() {

	}

	protected WB_VectorSequence(final Collection<? extends WB_Coordinate> tuples) {
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

	protected WB_VectorSequence(final WB_Coordinate[] tuples) {
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

	protected WB_VectorSequence(final WB_CoordinateSequence tuples) {
		ordinates = new double[4 * tuples.size()];
		n = tuples.size();
		final int index = 0;
		for (int i = 0; i < 4 * n; i++) {
			ordinates[i] = tuples.getRaw(i);

		}
	}

	protected WB_VectorSequence(final double[] ordinates) {
		this.ordinates = ordinates;
		n = ordinates.length / 4;
	}

	protected WB_VectorSequence(final double[][] tuples) {
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

	@Override
	public double x(final int i) {
		return ordinates[4 * i];
	}

	@Override
	public double y(final int i) {
		return ordinates[4 * i + 1];
	}

	@Override
	public double z(final int i) {
		return ordinates[4 * i + 2];
	}

	@Override
	public double get(final int i, final int j) {
		return ordinates[4 * i + j];
	}

	@Override
	public double getRaw(final int i) {
		return ordinates[i];
	}

	@Override
	public double getLength(final int i) {
		final int index = 4 * i;
		return Math.sqrt(sq(ordinates[index]) + sq(ordinates[index + 1])
				+ sq(ordinates[index + 2]));

	}

	@Override
	public double getSqLength(final int i) {
		final int index = 4 * i;
		return sq(ordinates[index]) + sq(ordinates[index + 1])
				+ sq(ordinates[index + 2]);

	}

	@Override
	public double getDistance(final int i, final int j) {
		final int index = 4 * i;
		final int index2 = 4 * j;
		return Math.sqrt(sq(ordinates[index] - ordinates[index2])
				+ sq(ordinates[index + 1] - ordinates[index2 + 1])
				+ sq(ordinates[index + 2] - ordinates[index2 + 2]));
	}

	@Override
	public double getSqDistance(final int i, final int j) {
		final int index = 4 * i;
		final int index2 = 4 * j;
		return sq(ordinates[index] - ordinates[index2])
				+ sq(ordinates[index + 1] - ordinates[index2 + 1])
				+ sq(ordinates[index + 2] - ordinates[index2 + 2]);
	}

	@Override
	public double getDistance(final int i, final WB_Coordinate p) {
		final int index = 4 * i;
		return Math.sqrt(sq(ordinates[index] - p.xd())
				+ sq(ordinates[index + 1] - p.yd())
				+ sq(ordinates[index + 2] - p.zd()));
	}

	@Override
	public double getSqDistance(final int i, final WB_Coordinate p) {
		final int index = 4 * i;
		return sq(ordinates[index] - p.xd())
				+ sq(ordinates[index + 1] - p.yd())
				+ sq(ordinates[index + 2] - p.zd());
	}

	private double sq(final double x) {
		return x * x;

	}

	@Override
	public List<WB_Vector> getAsList() {
		final List<WB_Vector> list = new FastTable<WB_Vector>();
		for (int i = 0; i < n; i++) {

			list.add(geometryfactory.createVector(ordinates[4 * i],
					ordinates[4 * i + 1], ordinates[4 * i + 2]));
		}
		return list;
	}

	@Override
	public WB_Vector getCoordinate(final int i) {
		if (i >= n) {
			throw (new IndexOutOfBoundsException());
		}
		return geometryfactory.createVector(ordinates[4 * i],
				ordinates[4 * i + 1], ordinates[4 * i + 2]);
	}

	@Override
	public int size() {
		return n;
	}

	// Operators on entire sequence

	@Override
	public WB_CoordinateSequence<WB_Vector> add(final WB_Coordinate p) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._addSelf(p);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> add(
			final WB_CoordinateSequence seq, final int i) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._addSelf(seq, i);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> add(final double x, final double y,
			final double z) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._addSelf(x, y, z);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> addMul(final double f,
			final WB_Coordinate p) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._addMulSelf(f, p);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> addMul(final double f,
			final WB_CoordinateSequence seq, final int i) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._addMulSelf(f, seq, i);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> mulAddMul(final double f,
			final double g, final WB_Coordinate p) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._mulAddMulSelf(f, g, p);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> mulAddMul(final double f,
			final double g, final WB_CoordinateSequence seq, final int i) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._mulAddMulSelf(f, g, seq, i);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> sub(final WB_Coordinate p) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._subSelf(p);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> sub(
			final WB_CoordinateSequence seq, final int i) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._subSelf(seq, i);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> mul(final double f) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._mulSelf(f);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> div(final double f) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._divSelf(f);
	}

	@Override
	public double[] dot(final WB_Coordinate p) {
		final double[] result = new double[size()];
		final double x1 = p.xd();
		final double y1 = p.yd();
		final double z1 = p.zd();
		int id = 0;
		for (int i = 0; i < size(); i++) {
			result[i] = 0;

			result[i] += ordinates[id++] * x1;
			result[i] += ordinates[id++] * y1;
			result[i] += ordinates[id++] * z1;

		}
		return result;

	}

	@Override
	public double[] dot(final WB_CoordinateSequence seq, final int i) {
		final double[] result = new double[size()];
		final double x1 = seq.get(i, 0);
		final double y1 = seq.get(i, 1);
		final double z1 = seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			result[j] = 0;
			result[i] += ordinates[id++] * x1;
			result[i] += ordinates[id++] * y1;
			result[i] += ordinates[id++] * z1;
		}
		return result;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> cross(final WB_Coordinate p) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._crossSelf(p);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> cross(
			final WB_CoordinateSequence seq, final int i) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._crossSelf(seq, i);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> normalize() {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._normalizeSelf();
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> rescale(final double d) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._normalizeSelf()._mulSelf(d);
	}

	@Override
	public double[] getAngle(final WB_Coordinate v) {
		final double[] result = new double[size()];
		for (int i = 0; i < size(); i++) {
			result[i] = geometryfactory.createVector(v).getAngle(this, i);
		}
		return result;
	}

	@Override
	public double[] getAngle(final WB_CoordinateSequence seq, final int i) {
		final double[] result = new double[size()];
		double d;
		final double x1 = seq.get(i, 0);
		final double y1 = seq.get(i, 1);
		final double z1 = seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			d = ordinates[id++] * x1;
			d += ordinates[id++] * y1;
			d += ordinates[id++] * z1;

			d /= (getLength(j) * seq.getLength(i));
			if (d < -1.0) {
				d = -1.0;
			}
			if (d > 1.0) {
				d = 1.0;
			}
			result[i] = Math.acos(d);
		}
		return result;
	}

	@Override
	public double[] getAngleNorm(final WB_Coordinate v) {
		final double[] result = new double[size()];
		double d;
		final double x1 = v.xd();
		final double y1 = v.yd();
		final double z1 = v.zd();
		int id = 0;
		for (int j = 0; j < size(); j++) {
			d = ordinates[id++] * x1;
			d += ordinates[id++] * y1;
			d += ordinates[id++] * z1;
			if (d < -1.0) {
				d = -1.0;
			}
			if (d > 1.0) {
				d = 1.0;
			}
			result[j] = Math.acos(d);
		}
		return result;
	}

	@Override
	public double[] getAngleNorm(final WB_CoordinateSequence seq, final int i) {
		final double[] result = new double[size()];
		double d;
		final double x1 = seq.get(i, 0);
		final double y1 = seq.get(i, 1);
		final double z1 = seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			d = ordinates[id++] * x1;
			d += ordinates[id++] * y1;
			d += ordinates[id++] * z1;
			if (d < -1.0) {
				d = -1.0;
			}
			if (d > 1.0) {
				d = 1.0;
			}
			result[j] = Math.acos(d);
		}
		return result;
	}

	// UNSAFE!!!

	@Override
	public WB_CoordinateSequence<WB_Vector> _addSelf(final WB_Coordinate p) {
		final double x1 = p.xd();
		final double y1 = p.yd();
		final double z1 = p.zd();
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] += x1;
			ordinates[id++] += y1;
			ordinates[id++] += z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addSelf(
			final WB_CoordinateSequence seq, final int i) {
		final double x1 = seq.get(i, 0);
		final double y1 = seq.get(i, 1);
		final double z1 = seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] += x1;
			ordinates[id++] += y1;
			ordinates[id++] += z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addSelf(final double x,
			final double y, final double z) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] += x;
			ordinates[id++] += y;
			ordinates[id++] += z;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addMulSelf(final double f,
			final WB_Coordinate p) {
		final double x1 = f * p.xd();
		final double y1 = f * p.yd();
		final double z1 = f * p.zd();
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] += x1;
			ordinates[id++] += y1;
			ordinates[id++] += z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addMulSelf(final double f,
			final WB_CoordinateSequence seq, final int i) {
		final double x1 = f * seq.get(i, 0);
		final double y1 = f * seq.get(i, 1);
		final double z1 = f * seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] += x1;
			ordinates[id++] += y1;
			ordinates[id++] += z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _mulAddMulSelf(final double f,
			final double g, final WB_Coordinate p) {
		final double x1 = g * p.xd();
		final double y1 = g * p.yd();
		final double z1 = g * p.zd();
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id] *= f;
			ordinates[id++] += x1;
			ordinates[id] *= f;
			ordinates[id++] += y1;
			ordinates[id] *= f;
			ordinates[id++] += z1;

		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _mulAddMulSelf(final double f,
			final double g, final WB_CoordinateSequence seq, final int i) {
		final double x1 = g * seq.get(i, 0);
		final double y1 = g * seq.get(i, 1);
		final double z1 = g * seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id] *= f;
			ordinates[id++] += x1;
			ordinates[id] *= f;
			ordinates[id++] += y1;
			ordinates[id] *= f;
			ordinates[id++] += z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _subSelf(final WB_Coordinate p) {
		final double x1 = p.xd();
		final double y1 = p.yd();
		final double z1 = p.zd();
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] -= x1;
			ordinates[id++] -= y1;
			ordinates[id++] -= z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _subSelf(
			final WB_CoordinateSequence seq, final int i) {
		final double x1 = seq.get(i, 0);
		final double y1 = seq.get(i, 1);
		final double z1 = seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] -= x1;
			ordinates[id++] -= y1;
			ordinates[id++] -= z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _subSelf(final double x,
			final double y, final double z) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] -= x;
			ordinates[id++] -= y;
			ordinates[id++] -= z;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _mulSelf(final double f) {

		for (int j = 0; j < ordinates.length; j++) {
			ordinates[j] *= f;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _divSelf(final double g) {
		final double ig = 1.0 / g;
		for (int j = 0; j < ordinates.length; j++) {
			ordinates[j] *= ig;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _crossSelf(final WB_Coordinate p) {
		final double x2 = p.xd();
		final double y2 = p.yd();
		final double z2 = p.zd();
		double x1, y1, z1;
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id];
			y1 = ordinates[id + 1];
			z1 = ordinates[id + 2];
			ordinates[id++] = y1 * z2 - y2 * z1;
			ordinates[id++] = z1 * x2 - z2 * x1;
			ordinates[id++] = x1 * z2 - x2 * z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _crossSelf(
			final WB_CoordinateSequence seq, final int i) {
		final double x2 = seq.get(i, 0);
		final double y2 = seq.get(i, 1);
		final double z2 = seq.get(i, 2);
		double x1, y1, z1;
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id];
			y1 = ordinates[id + 1];
			z1 = ordinates[id + 2];
			ordinates[id++] = y1 * z2 - y2 * z1;
			ordinates[id++] = z1 * x2 - z2 * x1;
			ordinates[id++] = x1 * z2 - x2 * z1;
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _crossSelf(final double x,
			final double y, final double z) {
		double x1, y1, z1;
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id];
			y1 = ordinates[id + 1];
			z1 = ordinates[id + 2];
			ordinates[id++] = y1 * z - y * z1;
			ordinates[id++] = z1 * x - z * x1;
			ordinates[id++] = x1 * z - x * z1;
		}
		return this;
	}

	@Override
	public void _setAll(final WB_Coordinate p) {
		final double x1 = p.xd();
		final double y1 = p.yd();
		final double z1 = p.zd();
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] = x1;
			ordinates[id++] = y1;
			ordinates[id++] = z1;
		}
	}

	@Override
	public void _setAll(final WB_CoordinateSequence seq, final int i) {
		final double x1 = seq.get(i, 0);
		final double y1 = seq.get(i, 1);
		final double z1 = seq.get(i, 2);
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] = x1;
			ordinates[id++] = y1;
			ordinates[id++] = z1;
		}
	}

	@Override
	public void _setAll(final double x, final double y, final double z) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			ordinates[id++] = x;
			ordinates[id++] = y;
			ordinates[id++] = z;
		}
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _normalizeSelf() {
		double invd;
		int id = 0;
		for (int j = 0; j < size(); j++) {
			invd = getLength(j);
			ordinates[id++] *= invd;
			ordinates[id++] *= invd;
			ordinates[id++] *= invd;
		}
		return this;
	}

	// Operators on one index

	@Override
	public WB_Vector addI(final int i, final WB_Coordinate p) {
		return getCoordinate(i)._addSelf(p);
	}

	@Override
	public WB_Vector addI(final int i, final WB_CoordinateSequence seq,
			final int j) {
		return getCoordinate(i)._addSelf(seq, j);
	}

	@Override
	public WB_Vector addI(final int i, final double x, final double y,
			final double z) {
		return getCoordinate(i)._addSelf(x, y, z);
	}

	@Override
	public WB_Vector addMulI(final int i, final double f, final WB_Coordinate p) {
		return getCoordinate(i)._addMulSelf(f, p);
	}

	@Override
	public WB_Vector addMulI(final int i, final double f,
			final WB_CoordinateSequence seq, final int j) {
		return getCoordinate(i)._addMulSelf(f, seq, j);
	}

	@Override
	public WB_Vector mulAddMulI(final int i, final double f, final double g,
			final WB_Coordinate p) {
		return getCoordinate(i)._mulAddMulSelf(f, g, p);
	}

	@Override
	public WB_Vector mulAddMulI(final int i, final double f, final double g,
			final WB_CoordinateSequence seq, final int j) {
		return getCoordinate(i)._mulAddMulSelf(f, g, seq, j);
	}

	@Override
	public WB_Vector subI(final int i, final WB_Coordinate p) {
		return getCoordinate(i)._subSelf(p);
	}

	@Override
	public WB_Vector subI(final int i, final WB_CoordinateSequence seq,
			final int j) {
		return getCoordinate(i)._subSelf(seq, j);
	}

	@Override
	public WB_Vector mulI(final int i, final double f) {
		return getCoordinate(i)._mulSelf(f);
	}

	@Override
	public WB_Vector divI(final int i, final double f) {
		return getCoordinate(i)._divSelf(f);
	}

	@Override
	public double dotI(final int i, final WB_Coordinate p) {
		int id = 4 * i;
		double result = ordinates[id++] * p.xd();
		result = ordinates[id++] * p.yd();
		result = ordinates[id] * p.zd();
		return result;
	}

	@Override
	public double dotI(final int i, final WB_CoordinateSequence seq, final int j) {
		int id = 4 * i;
		double result = ordinates[id++] * seq.get(j, 0);
		result = ordinates[id++] * seq.get(j, 1);
		result = ordinates[id] * seq.get(j, 2);
		return result;
	}

	@Override
	public WB_Vector crossI(final int i, final WB_Coordinate p) {
		return getCoordinate(i)._crossSelf(p);
	}

	@Override
	public WB_Vector crossI(final int i, final WB_CoordinateSequence seq,
			final int j) {
		return getCoordinate(i)._crossSelf(seq, j);
	}

	@Override
	public WB_Vector normalizeI(final int i) {
		WB_Vector v = getCoordinate(i);
		v._normalizeSelf();
		return v;
	}

	@Override
	public WB_Vector rescaleI(final int i, final double d) {
		WB_Vector v = getCoordinate(i);
		v._normalizeSelf();
		v._mulSelf(d);

		return v;
	}

	@Override
	public double getAngleI(final int i, final WB_Coordinate v) {
		return geometryfactory.createVector(v).getAngle(this, i);
	}

	@Override
	public double getAngleI(final int i, final WB_CoordinateSequence seq,
			final int j) {
		return getCoordinate(i).getAngle(seq, j);
	}

	@Override
	public double getAngleNormI(final int i, final WB_Coordinate v) {
		return geometryfactory.createVector(v).getAngleNorm(this, i);
	}

	@Override
	public double getAngleNormI(final int i, final WB_CoordinateSequence seq,
			final int j) {
		return getCoordinate(i).getAngleNorm(seq, j);
	}

	// UNSAFE!!!

	@Override
	public WB_CoordinateSequence<WB_Vector> _addSelfI(final int i,
			final WB_Coordinate p) {
		int id = 4 * i;
		ordinates[id++] += p.xd();
		ordinates[id++] += p.yd();
		ordinates[id] += p.zd();
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addSelfI(final int i,
			final WB_CoordinateSequence seq, final int j) {
		int id = 4 * i;
		ordinates[id++] += seq.get(j, 0);
		ordinates[id++] += seq.get(j, 1);
		ordinates[id] += seq.get(j, 2);
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addSelfI(final int i,
			final double x, final double y, final double z) {
		int id = 4 * i;
		ordinates[id++] += x;
		ordinates[id++] += y;
		ordinates[id] += z;
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addMulSelfI(final int i,
			final double f, final WB_Coordinate p) {
		int id = 4 * i;
		ordinates[id++] += f * p.xd();
		ordinates[id++] += f * p.yd();
		ordinates[id] += f * p.zd();
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _addMulSelfI(final int i,
			final double f, final WB_CoordinateSequence seq, final int j) {
		int id = 4 * i;
		ordinates[id++] += f * seq.get(j, 0);
		ordinates[id++] += f * seq.get(j, 1);
		ordinates[id] += f * seq.get(j, 2);
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _mulAddMulSelfI(final int i,
			final double f, final double g, final WB_Coordinate p) {
		int id = 4 * i;
		ordinates[id] *= f;
		ordinates[id++] += g * p.xd();
		ordinates[id] *= f;
		ordinates[id++] += g * p.yd();
		ordinates[id] *= f;
		ordinates[id++] += g * p.zd();
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _mulAddMulSelfI(final int i,
			final double f, final double g, final WB_CoordinateSequence seq,
			final int j) {
		int id = 4 * i;
		ordinates[id] *= f;
		ordinates[id++] += g * seq.get(j, 0);
		ordinates[id] *= f;
		ordinates[id++] += g * seq.get(j, 1);
		ordinates[id] *= f;
		ordinates[id++] += g * seq.get(j, 2);
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _subSelfI(final int i,
			final WB_Coordinate p) {
		int id = 4 * i;
		ordinates[id++] -= p.xd();
		ordinates[id++] -= p.yd();
		ordinates[id] -= p.zd();
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _subSelfI(final int i,
			final WB_CoordinateSequence seq, final int j) {
		int id = 4 * i;
		ordinates[id++] -= seq.get(j, 0);
		ordinates[id++] -= seq.get(j, 1);
		ordinates[id] -= seq.get(j, 2);
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _subSelfI(final int i,
			final double x, final double y, final double z) {
		int id = 4 * i;
		ordinates[id++] -= x;
		ordinates[id++] -= y;
		ordinates[id] -= z;
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _mulSelfI(final int i,
			final double f) {
		int id = 4 * i;
		ordinates[id++] *= f;
		ordinates[id++] *= f;
		ordinates[id] *= f;
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _divSelfI(final int i,
			final double g) {
		int id = 4 * i;
		final double ig = 1.0 / g;
		ordinates[id++] *= ig;
		ordinates[id++] *= ig;
		ordinates[id] *= ig;
		return this;

	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _crossSelfI(final int i,
			final WB_Coordinate p) {
		x2 = p.xd();
		y2 = p.yd();
		z2 = p.zd();
		double x1, y1, z1;
		int id = 4 * i;
		x1 = ordinates[id];
		y1 = ordinates[id + 1];
		z1 = ordinates[id + 2];
		ordinates[id++] = y1 * z2 - y2 * z1;
		ordinates[id++] = z1 * x2 - z2 * x1;
		ordinates[id++] = x1 * z2 - x2 * z1;

		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _crossSelfI(final int i,
			final WB_CoordinateSequence seq, final int j) {
		x2 = seq.get(j, 0);
		y2 = seq.get(j, 1);
		z2 = seq.get(j, 2);
		double x1, y1, z1;
		int id = 4 * i;
		x1 = ordinates[id];
		y1 = ordinates[id + 1];
		z1 = ordinates[id + 2];
		ordinates[id++] = y1 * z2 - y2 * z1;
		ordinates[id++] = z1 * x2 - z2 * x1;
		ordinates[id++] = x1 * z2 - x2 * z1;

		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _crossSelfI(final int i,
			final double x, final double y, final double z) {
		int id = 4 * i;
		x1 = ordinates[id];
		y1 = ordinates[id + 1];
		z1 = ordinates[id + 2];
		ordinates[id++] = y1 * z - y * z1;
		ordinates[id++] = z1 * x - z * x1;
		ordinates[id++] = x1 * z - x * z1;

		return this;
	}

	@Override
	public void _setI(final int i, final WB_Coordinate p) {
		int id = 4 * i;
		ordinates[id++] = p.xd();
		ordinates[id++] = p.yd();
		ordinates[id] = p.zd();
	}

	@Override
	public void _setI(final int i, final WB_CoordinateSequence seq, final int j) {
		int id = 4 * i;
		ordinates[id++] = seq.get(j, 0);
		ordinates[id++] = seq.get(j, 1);
		ordinates[id] = seq.get(j, 2);
	}

	@Override
	public void _setI(final int i, final double x, final double y,
			final double z) {
		int id = 4 * i;
		ordinates[id++] = x;
		ordinates[id++] = y;
		ordinates[id] = z;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _normalizeSelfI(final int i) {
		int id = 4 * i;
		final double invd = 1.0 / getLength(i);
		ordinates[id++] *= invd;
		ordinates[id++] *= invd;
		ordinates[id] *= invd;
		return this;
	}

	@Override
	public void addIntoI(final int i, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		result._set(x1 + p.xd(), y1 + p.yd(), z1 + p.zd());
	}

	@Override
	public void addIntoI(final int i, final WB_CoordinateSequence seq,
			final int j, final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		x2 = seq.get(j, 0);
		y2 = seq.get(j, 1);
		z2 = seq.get(j, 2);
		result._set(x1 + x2, y1 + y2, z1 + z2);

	}

	@Override
	public void addIntoI(final int i, final double x, final double y,
			final double z, final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);

		result._set(x1 + x, y1 + y, z1 + z);
	}

	@Override
	public void addMulIntoI(final int i, final double f, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		result._set(x1 + f * p.xd(), y1 + f * p.yd(), z1 + f * p.zd());
	}

	@Override
	public void addMulIntoI(final int i, final double f,
			final WB_CoordinateSequence seq, final int j,
			final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		x2 = f * seq.get(j, 0);
		y2 = f * seq.get(j, 1);
		z2 = f * seq.get(j, 2);
		result._set(x1 + x2, y1 + y2, z1 + z2);
	}

	@Override
	public void mulAddMulIntoI(final int i, final double f, final double g,
			final WB_Coordinate p, final WB_MutableCoordinate result) {
		x1 = f * get(i, 0);
		y1 = f * get(i, 1);
		z1 = f * get(i, 2);
		result._set(x1 + g * p.xd(), y1 + g * p.yd(), z1 + g * p.zd());
	}

	@Override
	public void mulAddMulIntoI(final int i, final double f, final double g,
			final WB_CoordinateSequence seq, final int j,
			final WB_MutableCoordinate result) {
		x1 = f * get(i, 0);
		y1 = f * get(i, 1);
		z1 = f * get(i, 2);
		x2 = g * seq.get(j, 0);
		y2 = g * seq.get(j, 1);
		z2 = g * seq.get(j, 2);
		result._set(x1 + x2, y1 + y2, z1 + z2);
	}

	@Override
	public void subIntoI(final int i, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		result._set(x1 - p.xd(), y1 - p.yd(), z1 - p.zd());
	}

	@Override
	public void subIntoI(final int i, final WB_CoordinateSequence seq,
			final int j, final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		x2 = seq.get(j, 0);
		y2 = seq.get(j, 1);
		z2 = seq.get(j, 2);
		result._set(x1 - x2, y1 - y2, z1 - z2);
	}

	@Override
	public void mulIntoI(final int i, final double f,
			final WB_MutableCoordinate result) {
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		result._set(f * x1, f * y1, f * z1);
	}

	@Override
	public void divIntoI(final int i, final double f,
			final WB_MutableCoordinate result) {
		final double invf = 1.0 / f;
		x1 = get(i, 0);
		y1 = get(i, 1);
		z1 = get(i, 2);
		result._set(invf * x1, invf * y1, invf * z1);
	}

	@Override
	public void crossIntoI(final int i, final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		x2 = p.xd();
		y2 = p.yd();
		z2 = p.zd();
		double x1, y1, z1;
		int id = 4 * i;
		x1 = ordinates[id++];
		y1 = ordinates[id++];
		z1 = ordinates[id];
		result._set(y1 * z2 - y2 * z1, z1 * x2 - z2 * x1, x1 * z2 - x2 * z1);
	}

	@Override
	public void crossIntoI(final int i, final WB_CoordinateSequence seq,
			final int j, final WB_MutableCoordinate result) {
		x2 = seq.get(j, 0);
		y2 = seq.get(j, 1);
		z2 = seq.get(j, 2);
		int id = 4 * i;
		x1 = ordinates[id++];
		y1 = ordinates[id++];
		z1 = ordinates[id];
		result._set(y1 * z2 - y2 * z1, z1 * x2 - z2 * x1, x1 * z2 - x2 * z1);
	}

	@Override
	public void normalizeIntoI(final int i, final WB_MutableCoordinate result) {
		int id = 4 * i;
		final double invd = 1.0 / getLength(i);

		x1 = ordinates[id++] * invd;
		y1 = ordinates[id++] * invd;
		z1 = ordinates[id] * invd;
		result._set(x1, y1, z1);
	}

	@Override
	public void rescaleIntoI(final int i, final double d,
			final WB_MutableCoordinate result) {
		int id = 4 * i;
		final double invd = d / getLength(i);

		x1 = ordinates[id++] * invd;
		y1 = ordinates[id++] * invd;
		z1 = ordinates[id] * invd;
		result._set(x1, y1, z1);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> applyAsPoint(final WB_Transform T) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._applyAsPointSelf(T);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _applyAsPointSelf(
			final WB_Transform T) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id++];
			y1 = ordinates[id++];
			z1 = ordinates[id++];
			T.applyAsPoint(x1, y1, z1, this, j);
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> applyAsVector(final WB_Transform T) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._applyAsVectorSelf(T);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _applyAsVectorSelf(
			final WB_Transform T) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id++];
			y1 = ordinates[id++];
			z1 = ordinates[id++];
			T.applyAsVector(x1, y1, z1, this, j);
		}
		return this;
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> applyAsNormal(final WB_Transform T) {
		final WB_CoordinateSequence<WB_Vector> result = getCopy();
		return result._applyAsVectorSelf(T);
	}

	@Override
	public WB_CoordinateSequence<WB_Vector> _applyAsNormalSelf(
			final WB_Transform T) {
		int id = 0;
		for (int j = 0; j < size(); j++) {
			x1 = ordinates[id++];
			y1 = ordinates[id++];
			z1 = ordinates[id++];
			T.applyAsVector(x1, y1, z1, this, j);
		}
		return this;
	}

	@Override
	public WB_Vector applyAsPointI(final int i, final WB_Transform T) {
		final WB_Vector result = geometryfactory.createVector();
		T.applyAsPoint(this, i, result);
		return result;
	}

	@Override
	public WB_Vector applyAsVectorI(final int i, final WB_Transform T) {
		final WB_Vector result = geometryfactory.createVector();
		T.applyAsVector(this, i, result);
		return result;
	}

	@Override
	public WB_Vector applyAsNormalI(final int i, final WB_Transform T) {
		final WB_Vector result = geometryfactory.createVector();
		T.applyAsNormal(this, i, result);
		return result;
	}

	@Override
	public void _applyAsPointSelfI(final int i, final WB_Transform T) {
		T.applyAsPoint(this, i, this, i);
	}

	@Override
	public void _applyAsVectorSelfI(final int i, final WB_Transform T) {
		T.applyAsVector(this, i, this, i);

	}

	@Override
	public void _applyAsNormalSelfI(final int i, final WB_Transform T) {
		T.applyAsNormal(this, i, this, i);

	}

	@Override
	public void applyAsPointIntoI(final int i, final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsPoint(this, i, result);

	}

	@Override
	public void applyAsVectorIntoI(final int i, final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsVector(this, i, result);
	}

	@Override
	public void applyAsNormalIntoI(final int i, final WB_Transform T,
			final WB_MutableCoordinate result) {
		T.applyAsNormal(this, i, result);
	}

	@Override
	public void applyAsPointIntoI(final int i, final WB_Transform T,
			final WB_CoordinateSequence result, final int j) {
		T.applyAsPoint(this, i, result, j);
	}

	@Override
	public void applyAsVectorIntoI(final int i, final WB_Transform T,
			final WB_CoordinateSequence result, final int j) {
		T.applyAsVector(this, i, result, j);
	}

	@Override
	public void applyAsNormalIntoI(final int i, final WB_Transform T,
			final WB_CoordinateSequence result, final int j) {
		T.applyAsNormal(this, i, result, j);
	}

	@Override
	public WB_AABB getAABB() {
		return new WB_AABB(this);
	}

	@Override
	public WB_VectorSequence getSubSequence(final int[] indices) {
		final WB_VectorSequence subseq = new WB_VectorSequence();

		final int n = indices.length;
		final double[] subordinates = new double[3 * n];
		int id = 0;
		int fi;
		for (int i = 0; i < n; i++) {
			fi = 3 * indices[i];
			subordinates[id++] = ordinates[fi++];
			subordinates[id++] = ordinates[fi++];
			subordinates[id++] = ordinates[fi];
		}
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	@Override
	public WB_VectorSequence getSubSequence(final int start, final int end) {
		final WB_VectorSequence subseq = new WB_VectorSequence();

		final int n = end - start + 1;
		final double[] subordinates = new double[3 * n];
		final int id = 0;
		final int fi;
		System.arraycopy(ordinates, 3 * start, subordinates, 0, 3 * n);
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

	@Override
	public WB_VectorSequence getCopy() {
		final WB_VectorSequence subseq = new WB_VectorSequence();

		final int n = size();
		final double[] subordinates = new double[3 * n];
		System.arraycopy(ordinates, 0, subordinates, 0, 3 * n);
		subseq.ordinates = subordinates;
		subseq.n = n;

		return subseq;
	}

}
