package wblut.geom;

public class WB_Grid3D<T> {
	private final Object[] values;
	private final int Ni, Nj, Nk;
	private final int Nij;
	private final double lx, ly, lz;
	private final double ux, uy, uz;
	private final double dx, dy, dz;

	public WB_Grid3D(final int Ni, final int Nj, final int Nk, final double lx,
			final double ly, final double lz, final double ux, final double uy,
			final double uz) {
		this.Ni = Ni;
		this.Nj = Nj;
		this.Nk = Nk;
		Nij = Ni * Nj;
		this.lx = lx;
		this.ly = ly;
		this.lz = lz;
		this.ux = ux;
		this.uy = uy;
		this.uz = uz;
		dx = (ux - lx) / Ni;
		dy = (uy - ly) / Nj;
		dz = (uz - lz) / Nk;
		values = new Object[Nij * Nk];
	}

	public void set(final int i, final int j, final int k, final T value) {
		values[index(i, j, k)] = value;
	}

	public void setRaw(final int raw, final T value) {
		values[raw] = value;
	}

	public int Ni() {
		return Ni;
	}

	public int Nj() {
		return Nj;
	}

	public int Nk() {
		return Nk;
	}

	public T getValue(final int i, final int j, final int k) {
		return (T) values[index(i, j, k)];
	}

	public T getValueRaw(final int raw) {
		return (T) values[raw];
	}

	public double getCenterX(final int i) {
		return lx + (i + 0.5) * dx;
	}

	public double getCenterY(final int j) {
		return ly + (j + 0.5) * dy;
	}

	public double getCenterZ(final int k) {
		return lz + (k + 0.5) * dz;
	}

	public double getLowX(final int i) {
		return lx + i * dx;
	}

	public double getLowY(final int j) {
		return ly + j * dy;
	}

	public double getLowZ(final int k) {
		return lz + k * dz;
	}

	public double getHighX(final int i) {
		return lx + (i + 1) * dx;
	}

	public double getHighY(final int j) {
		return ly + (j + 1) * dy;
	}

	public double getHighZ(final int k) {
		return lz + (k + 1) * dz;
	}

	private int index(final int i, final int j, final int k) {
		return Nij * k + Ni * j + i;
	}

}
