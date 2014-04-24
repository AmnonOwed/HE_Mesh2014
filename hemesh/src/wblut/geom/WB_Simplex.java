package wblut.geom;


public interface WB_Simplex extends WB_Geometry {
	public WB_Point getPoint(int i);

	@Override
	public int getDimension();

	public WB_Point getCenter();
}
