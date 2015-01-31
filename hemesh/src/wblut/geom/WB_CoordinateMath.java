package wblut.geom;

import wblut.math.WB_M33;

public interface WB_CoordinateMath {
    public WB_Coordinate add(final double x, final double y, final double z);;

    public void addInto(final double x, final double y, final double z,
	    final WB_MutableCoordinate result);

    public WB_Coordinate add(final WB_Coordinate p);

    public void addInto(final WB_Coordinate p, final WB_MutableCoordinate result);

    public WB_Coordinate addMul(final double f, final double x, final double y,
	    final double z);

    public void addMulInto(final double f, final double x, final double y,
	    final double z, final WB_MutableCoordinate result);

    public WB_Coordinate addMul(final double f, final WB_Coordinate p);

    public void addMulInto(final double f, final WB_Coordinate p,
	    final WB_MutableCoordinate result);

    public WB_Coordinate cross(final WB_Coordinate p);

    public void crossInto(final WB_Coordinate p,
	    final WB_MutableCoordinate result);

    public WB_Coordinate div(final double f);

    public void divInto(final double f, final WB_MutableCoordinate result);

    public double dot(final WB_Coordinate p);

    public double dot2D(final WB_Coordinate p);

    public WB_Coordinate mul(final double f);

    public void mulInto(final double f, final WB_MutableCoordinate result);

    public WB_Coordinate mulAddMul(final double f, final double g,
	    final WB_Coordinate p);

    public void mulAddMulInto(final double f, final double g,
	    final WB_Coordinate p, final WB_MutableCoordinate result);

    public double scalarTriple(final WB_Coordinate v, final WB_Coordinate w);

    public WB_Coordinate sub(final double x, final double y, final double z);

    public void subInto(final double x, final double y, final double z,
	    final WB_MutableCoordinate result);

    public WB_Coordinate sub(final WB_Coordinate p);

    public void subInto(final WB_Coordinate p, final WB_MutableCoordinate result);

    public WB_M33 tensor(final WB_Coordinate v);

    public double absDot(final WB_Coordinate p);

    public double absDot2D(final WB_Coordinate p);
}
