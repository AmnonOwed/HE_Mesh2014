package wblut.geom;

public interface WB_MutableCoordinateMath extends WB_CoordinateMath {
    public WB_Coordinate addMulSelf(final double f, final double x,
	    final double y, final double z);

    public WB_Coordinate addMulSelf(final double f, final WB_Coordinate p);

    public WB_Coordinate addSelf(final double x, final double y, final double z);

    public WB_Coordinate addSelf(final WB_Coordinate p);

    public WB_Coordinate applyAsNormalSelf(final WB_Transform T);

    public WB_Coordinate applyAsPointSelf(final WB_Transform T);

    public WB_Coordinate applyAsVectorSelf(final WB_Transform T);

    public WB_Coordinate crossSelf(final WB_Coordinate p);

    public WB_Coordinate divSelf(final double f);

    public WB_Coordinate mulAddMulSelf(final double f, final double g,
	    final WB_Coordinate p);

    public WB_Coordinate mulSelf(final double f);

    public double normalizeSelf();

    public WB_Coordinate subSelf(final double x, final double y, final double z);

    public WB_Coordinate subSelf(final WB_Coordinate v);

    public WB_Coordinate trimSelf(final double d);

    public WB_Coordinate applySelf(final WB_Transform T);
}
