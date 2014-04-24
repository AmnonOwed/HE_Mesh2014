package wblut.geom;

public class WB_EmbeddedPlane extends WB_CoordinateSystem implements
		WB_Context2D {

	private double offset;
	double x, y, z;
	int id;
	private final WB_Transform T2D3D;
	private int mode;
	public static final int YZ = 0;
	public static final int ZX = 1;
	public static final int XY = 2;
	public static final int ZY = 3;
	public static final int XZ = 4;
	public static final int YX = 5;

	public static final int PLANE = 6;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_EmbeddedPlane() {
		this(XY, 0);
	}

	protected WB_EmbeddedPlane(final int mode, final double offset) {
		super();
		this.mode = mode;
		this.offset = offset;
		if (mode < 0 || mode > 5) {
			throw (new IndexOutOfBoundsException());
		}
		if (mode == YZ) {
			set(geometryfactory.createPoint(offset, 0, 0), geometryfactory.Y(),
					geometryfactory.Z(), geometryfactory.X(),
					geometryfactory.WORLD());

			this.mode = YZ;
		} else if (mode == ZX) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.Z(),
					geometryfactory.X(), geometryfactory.Y(),
					geometryfactory.WORLD());

			this.mode = ZX;
		} else if (mode == ZY) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.Z(),
					geometryfactory.Y(), geometryfactory.minX(),
					geometryfactory.WORLD());

			this.mode = ZY;
		} else if (mode == XZ) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.X(),
					geometryfactory.Z(), geometryfactory.minY(),
					geometryfactory.WORLD());

			this.mode = XZ;
		} else if (mode == YX) {
			set(geometryfactory.createPoint(0, offset, 0), geometryfactory.Y(),
					geometryfactory.X(), geometryfactory.minZ(),
					geometryfactory.WORLD());

			this.mode = YX;
		} else {// XY
			set(geometryfactory.createPoint(0, 0, offset), geometryfactory.X(),
					geometryfactory.Y(), geometryfactory.Z(),
					geometryfactory.WORLD());
			this.mode = XY;
		}
		T2D3D = getTransformToWorld();
	}

	protected WB_EmbeddedPlane(final int mode) {
		this(mode, 0);

	}

	protected WB_EmbeddedPlane(final WB_Plane P) {
		super(P.getOrigin(), P.getU(), P.getV(), P.getW(), geometryfactory
				.WORLD());
		mode = PLANE;
		T2D3D = getTransformToWorld();
	}

	protected WB_EmbeddedPlane(final WB_Plane P, final double offset) {
		super(P.getOrigin().addMul(offset, P.getNormal()), P.getU(), P.getV(),
				P.getW(), geometryfactory.WORLD());
		mode = PLANE;
		T2D3D = getTransformToWorld();
	}

	@Override
	public void pointTo2D(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(p.xd(), p.yd(), p.zd() - offset);
			break;
		case YZ:
			result._set(p.yd(), p.zd(), p.xd() - offset);
			break;
		case ZX:
			result._set(p.zd(), p.xd(), p.yd() - offset);
			break;
		case YX:
			result._set(p.yd(), p.xd(), offset - p.zd());
			break;
		case ZY:
			result._set(p.zd(), p.yd(), offset - p.xd());
			break;
		case XZ:
			result._set(p.xd(), p.zd(), offset - p.yd());
			break;

		default:
			T2D3D.applyInvAsPoint(p, result);
		}
	}

	@Override
	public void pointTo2D(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(x, y, z - offset);
			break;
		case YZ:
			result._set(y, z, x - offset);
			break;
		case ZX:
			result._set(z, x, y - offset);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(z, y, offset - x);
			break;
		case XZ:
			result._set(x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsPoint(x, y, z, result);
		}
	}

	@Override
	public void pointTo2D(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);

		switch (mode) {
		case XY:
			result._set(x, y, z - offset);
			break;
		case YZ:
			result._set(y, z, x - offset);
			break;
		case ZX:
			result._set(z, x, y - offset);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(z, y, offset - x);
			break;
		case XZ:
			result._set(x, z, offset - y);
			break;
		default:
			T2D3D.applyInvAsPoint(source, i, result);
		}
	}

	@Override
	public void pointTo3D(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(p.xd(), p.yd(), p.zd() + offset);
			break;
		case YZ:
			result._set(p.zd() + offset, p.xd(), p.yd());
			break;
		case ZX:
			result._set(p.yd(), p.zd() + offset, p.xd());
			break;
		case YX:
			result._set(p.yd(), p.xd(), offset - p.zd());
			break;
		case ZY:
			result._set(offset - p.zd(), p.yd(), p.xd());
			break;
		case XZ:
			result._set(p.xd(), offset - p.zd(), p.yd());
			break;

		default:
			T2D3D.applyAsPoint(p, result);
		}
	}

	@Override
	public void pointTo3D(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(x, y, z + offset);
			break;
		case YZ:
			result._set(z + offset, x, y);
			break;
		case ZX:
			result._set(y, z + offset, x);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(offset - z, y, x);
			break;
		case XZ:
			result._set(x, offset - z, y);
			break;

		default:
			T2D3D.applyAsPoint(x, y, z, result);
		}
	}

	@Override
	public void pointTo3D(final double x, final double y,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(x, y, offset);
			break;
		case YZ:
			result._set(0, x, y + offset);
			break;
		case ZX:
			result._set(y, 0, x + offset);
			break;
		case YX:
			result._set(y, x, offset);
			break;
		case ZY:
			result._set(offset, y, x);
			break;
		case XZ:
			result._set(x, offset, y);
			break;

		default:
			T2D3D.applyAsPoint(x, y, 0, result);
		}
	}

	@Override
	public void pointTo3D(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._set(x, y, z + offset);
			break;
		case YZ:
			result._set(z + offset, x, y);
			break;
		case ZX:
			result._set(y, z + offset, x);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(offset - z, y, x);
			break;
		case XZ:
			result._set(x, offset - z, y);
			break;

		default:
			T2D3D.applyAsPoint(source, i, result);
		}
	}

	@Override
	public void pointTo2D(final WB_Coordinate p,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, p.xd(), p.yd(), p.zd() - offset);
			break;
		case YZ:
			result._setI(i, p.yd(), p.zd(), p.xd() - offset);
			break;
		case ZX:
			result._setI(i, p.zd(), p.xd(), p.yd() - offset);
			break;
		case YX:
			result._setI(i, p.yd(), p.xd(), offset - p.zd());
			break;
		case ZY:
			result._setI(i, p.zd(), p.yd(), offset - p.xd());
			break;
		case XZ:
			result._setI(i, p.xd(), p.zd(), offset - p.yd());
			break;

		default:
			T2D3D.applyInvAsPoint(p, result, i);
		}
	}

	@Override
	public void pointTo2D(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, x, y, z - offset);
			break;
		case YZ:
			result._setI(i, y, z, x - offset);
			break;
		case ZX:
			result._setI(i, z, x, y - offset);
			break;
		case YX:
			result._setI(i, y, x, offset - z);
			break;
		case ZY:
			result._setI(i, z, y, offset - x);
			break;
		case XZ:
			result._setI(i, x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsPoint(x, y, z, result, i);
		}
	}

	@Override
	public void pointTo2D(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._setI(j, x, y, z - offset);
			break;
		case YZ:
			result._setI(j, y, z, x - offset);
			break;
		case ZX:
			result._setI(j, z, x, y - offset);
			break;
		case YX:
			result._setI(j, y, x, offset - z);
			break;
		case ZY:
			result._setI(j, z, y, offset - x);
			break;
		case XZ:
			result._setI(j, x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsPoint(source, j, result, i);
		}
	}

	@Override
	public void pointTo3D(final WB_Coordinate p,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, p.xd(), p.yd(), p.zd() + offset);
			break;
		case YZ:
			result._setI(i, p.zd() + offset, p.xd(), p.yd());
			break;
		case ZX:
			result._setI(i, p.yd(), p.zd() + offset, p.xd());
			break;
		case YX:
			result._setI(i, p.yd(), p.xd(), offset - p.zd());
			break;
		case ZY:
			result._setI(i, offset - p.zd(), p.yd(), p.xd());
			break;
		case XZ:
			result._setI(i, p.xd(), offset - p.zd(), p.yd());
			break;

		default:
			T2D3D.applyAsPoint(p, result, i);
		}
	}

	@Override
	public void pointTo3D(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, x, y, z + offset);
			break;
		case YZ:
			result._setI(i, z + offset, x, y);
			break;
		case ZX:
			result._setI(i, y, z + offset, x);
			break;
		case YX:
			result._setI(i, y, x, offset - z);
			break;
		case ZY:
			result._setI(i, offset - z, y, x);
			break;
		case XZ:
			result._setI(i, x, offset - z, y);
			break;

		default:
			T2D3D.applyAsPoint(x, y, z, result, i);
		}
	}

	@Override
	public void pointTo3D(final double x, final double y,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, x, y, offset);
			break;
		case YZ:
			result._setI(i, 0, x, y + offset);
			break;
		case ZX:
			result._setI(i, y, 0, x + offset);
			break;
		case YX:
			result._setI(i, y, x, offset);
			break;
		case ZY:
			result._setI(i, offset, y, x);
			break;
		case XZ:
			result._setI(i, x, offset, y);
			break;

		default:
			T2D3D.applyAsPoint(x, y, 0, result, i);
		}
	}

	@Override
	public void pointTo3D(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._setI(j, x, y, z + offset);
			break;
		case YZ:
			result._setI(j, z + offset, x, y);
			break;
		case ZX:
			result._setI(j, y, z + offset, x);
			break;
		case YX:
			result._setI(j, y, x, offset - z);
			break;
		case ZY:
			result._setI(j, offset - z, y, x);
			break;
		case XZ:
			result._setI(j, x, offset - z, y);
			break;

		default:
			T2D3D.applyAsPoint(source, i, result, j);
		}
	}

	@Override
	public void vectorTo2D(final WB_Coordinate v,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(v.xd(), v.yd(), v.zd() - offset);
			break;
		case YZ:
			result._set(v.yd(), v.zd(), v.xd() - offset);
			break;
		case ZX:
			result._set(v.zd(), v.xd(), v.yd() - offset);
			break;
		case YX:
			result._set(v.yd(), v.xd(), offset - v.zd());
			break;
		case ZY:
			result._set(v.zd(), v.yd(), offset - v.xd());
			break;
		case XZ:
			result._set(v.xd(), v.zd(), offset - v.yd());
			break;

		default:
			T2D3D.applyInvAsVector(v, result);
		}
	}

	@Override
	public void vectorTo2D(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(x, y, z - offset);
			break;
		case YZ:
			result._set(y, z, x - offset);
			break;
		case ZX:
			result._set(z, x, y - offset);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(z, y, offset - x);
			break;
		case XZ:
			result._set(x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsVector(x, y, z, result);
		}
	}

	@Override
	public void vectorTo2D(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._set(x, y, z - offset);
			break;
		case YZ:
			result._set(y, z, x - offset);
			break;
		case ZX:
			result._set(z, x, y - offset);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(z, y, offset - x);
			break;
		case XZ:
			result._set(x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsVector(source, i, result);
		}
	}

	@Override
	public void vectorTo3D(final WB_Coordinate v,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(v.xd(), v.yd(), v.zd() + offset);
			break;
		case YZ:
			result._set(v.zd() + offset, v.xd(), v.yd());
			break;
		case ZX:
			result._set(v.yd(), v.zd() + offset, v.xd());
			break;
		case YX:
			result._set(v.yd(), v.xd(), offset - v.zd());
			break;
		case ZY:
			result._set(offset - v.zd(), v.yd(), v.xd());
			break;
		case XZ:
			result._set(v.xd(), offset - v.zd(), v.yd());
			break;

		default:
			T2D3D.applyAsVector(v, result);
		}
	}

	@Override
	public void vectorTo3D(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(x, y, z + offset);
			break;
		case YZ:
			result._set(z + offset, x, y);
			break;
		case ZX:
			result._set(y, z + offset, x);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(offset - z, y, x);
			break;
		case XZ:
			result._set(x, offset - z, y);
			break;

		default:
			T2D3D.applyAsVector(x, y, z, result);
		}
	}

	@Override
	public void vectorTo3D(final double x, final double y,
			final WB_MutableCoordinate result) {
		switch (mode) {
		case XY:
			result._set(x, y, offset);
			break;
		case YZ:
			result._set(0, x, y + offset);
			break;
		case ZX:
			result._set(y, 0, x + offset);
			break;
		case YX:
			result._set(y, x, offset);
			break;
		case ZY:
			result._set(offset, y, x);
			break;
		case XZ:
			result._set(x, offset, y);
			break;

		default:
			T2D3D.applyAsVector(x, y, 0, result);
		}
	}

	@Override
	public void vectorTo3D(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._set(x, y, z + offset);
			break;
		case YZ:
			result._set(z + offset, x, y);
			break;
		case ZX:
			result._set(y, z + offset, x);
			break;
		case YX:
			result._set(y, x, offset - z);
			break;
		case ZY:
			result._set(offset - z, y, x);
			break;
		case XZ:
			result._set(x, offset - z, y);
			break;

		default:
			T2D3D.applyAsVector(source, i, result);
		}
	}

	@Override
	public void vectorTo2D(final WB_Coordinate v,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, v.xd(), v.yd(), v.zd() - offset);
			break;
		case YZ:
			result._setI(i, v.yd(), v.zd(), v.xd() - offset);
			break;
		case ZX:
			result._setI(i, v.zd(), v.xd(), v.yd() - offset);
			break;
		case YX:
			result._setI(i, v.yd(), v.xd(), offset - v.zd());
			break;
		case ZY:
			result._setI(i, v.zd(), v.yd(), offset - v.xd());
			break;
		case XZ:
			result._setI(i, v.xd(), v.zd(), offset - v.yd());
			break;

		default:
			T2D3D.applyInvAsVector(v, result, i);
		}
	}

	@Override
	public void vectorTo2D(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, x, y, z - offset);
			break;
		case YZ:
			result._setI(i, y, z, x - offset);
			break;
		case ZX:
			result._setI(i, z, x, y - offset);
			break;
		case YX:
			result._setI(i, y, x, offset - z);
			break;
		case ZY:
			result._setI(i, z, y, offset - x);
			break;
		case XZ:
			result._setI(i, x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsVector(x, y, z, result, i);
		}
	}

	@Override
	public void vectorTo2D(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._setI(j, x, y, z - offset);
			break;
		case YZ:
			result._setI(j, y, z, x - offset);
			break;
		case ZX:
			result._setI(j, z, x, y - offset);
			break;
		case YX:
			result._setI(j, y, x, offset - z);
			break;
		case ZY:
			result._setI(j, z, y, offset - x);
			break;
		case XZ:
			result._setI(j, x, z, offset - y);
			break;

		default:
			T2D3D.applyInvAsVector(source, j, result, i);
		}
	}

	@Override
	public void vectorTo3D(final WB_Coordinate v,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, v.xd(), v.yd(), v.zd() + offset);
			break;
		case YZ:
			result._setI(i, v.zd() + offset, v.xd(), v.yd());
			break;
		case ZX:
			result._setI(i, v.yd(), v.zd() + offset, v.xd());
			break;
		case YX:
			result._setI(i, v.yd(), v.xd(), offset - v.zd());
			break;
		case ZY:
			result._setI(i, offset - v.zd(), v.yd(), v.xd());
			break;
		case XZ:
			result._setI(i, v.xd(), offset - v.zd(), v.yd());
			break;

		default:
			T2D3D.applyAsVector(v, result, i);
		}
	}

	@Override
	public void vectorTo3D(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, x, y, z + offset);
			break;
		case YZ:
			result._setI(i, z + offset, x, y);
			break;
		case ZX:
			result._setI(i, y, z + offset, x);
			break;
		case YX:
			result._setI(i, y, x, offset - z);
			break;
		case ZY:
			result._setI(i, offset - z, y, x);
			break;
		case XZ:
			result._setI(i, x, offset - z, y);
			break;

		default:
			T2D3D.applyAsVector(x, y, z, result, i);
		}
	}

	@Override
	public void vectorTo3D(final double x, final double y,
			final WB_CoordinateSequence result, final int i) {
		switch (mode) {
		case XY:
			result._setI(i, x, y, offset);
			break;
		case YZ:
			result._setI(i, 0, x, y + offset);
			break;
		case ZX:
			result._setI(i, y, 0, x + offset);
			break;
		case YX:
			result._setI(i, y, x, offset);
			break;
		case ZY:
			result._setI(i, offset, y, x);
			break;
		case XZ:
			result._setI(i, x, offset, y);
			break;

		default:
			T2D3D.applyAsVector(x, y, 0, result, i);
		}
	}

	@Override
	public void vectorTo3D(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		switch (mode) {
		case XY:
			result._setI(j, x, y, z + offset);
			break;
		case YZ:
			result._setI(j, z + offset, x, y);
			break;
		case ZX:
			result._setI(j, y, z + offset, x);
			break;
		case YX:
			result._setI(j, y, x, offset - z);
			break;
		case ZY:
			result._setI(j, offset - z, y, x);
			break;
		case XZ:
			result._setI(j, x, offset - z, y);
			break;

		default:
			T2D3D.applyAsVector(source, i, result, j);
		}
	}

}
