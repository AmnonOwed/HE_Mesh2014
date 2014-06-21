package wblut.geom;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.hash.TIntDoubleHashMap;

public class WB_HashGrid {

	private final TIntDoubleMap values;

	private final double defaultValue;

	private final int K, L, M, KL;

	public WB_HashGrid(final int K, final int L, final int M,
			final double defaultValue) {
		this.K = K;
		this.L = L;
		this.M = M;
		KL = K * L;
		values = new TIntDoubleHashMap(10, 0.5f, -1, Double.NaN);
		this.defaultValue = defaultValue;
	}

	public WB_HashGrid(final int K, final int L, final int M) {
		this.K = K;
		this.L = L;
		this.M = M;
		KL = K * L;
		values = new TIntDoubleHashMap(10, 0.5f, -1, Double.NaN);
		defaultValue = -10000000;
	}

	public boolean setValue(final double value, final int i, final int j,
			final int k) {
		final int id = safeIndex(i, j, k);
		if (id > 0) {
			values.put(id, value);
			return true;
		}
		return false;
	}

	public boolean addValue(final double value, final int i, final int j,
			final int k) {
		final int id = safeIndex(i, j, k);
		if (id > 0) {
			final double v = values.get(id);
			if (v == Double.NaN) {
				values.put(id, value);
			} else {
				values.put(id, v + value);
			}
			return true;
		}
		return false;
	}

	public boolean clearValue(final int i, final int j, final int k) {
		final int id = safeIndex(i, j, k);
		if (id > 0) {
			values.remove(id);
			return true;
		}
		return false;
	}

	public double getValue(final int i, final int j, final int k) {
		final int id = safeIndex(i, j, k);
		if (id == -1) {
			return defaultValue;
		}
		if (id > 0) {
			final Double val = values.get(id);
			if (val != null) {
				return val.doubleValue();
			}

		}
		return defaultValue;

	}

	private int safeIndex(final int i, final int j, final int k) {
		if (i < 0) {
			return -1;
		}
		if (i > K - 1) {
			return -1;
		}
		if (j < 0) {
			return -1;
		}
		if (j > L - 1) {
			return -1;
		}

		if (k < 0) {
			return -1;
		}
		if (k > M - 1) {
			return -1;
		}
		return i + j * K + k * KL;
	}

	public int getW() {
		return K;
	}

	public int getH() {
		return L;
	}

	public int getD() {
		return M;
	}

	public double getDefaultValue() {

		return defaultValue;
	}

	public int[] getKeys() {
		return values.keys();
	}

	public int size() {
		return values.size();

	}

}
