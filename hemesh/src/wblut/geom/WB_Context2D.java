package wblut.geom;


public interface WB_Context2D {

	public void pointTo2D(WB_Coordinate p, WB_MutableCoordinate result);

	public void pointTo2D(double x, double y, double z, WB_MutableCoordinate result);

	public void pointTo3D(WB_Coordinate p, WB_MutableCoordinate result);

	public void pointTo3D(double x, double y, double z, WB_MutableCoordinate result);

	public void pointTo3D(double x, double y, WB_MutableCoordinate result);

	public void vectorTo2D(WB_Coordinate p, WB_MutableCoordinate result);

	public void vectorTo2D(double x, double y, double z, WB_MutableCoordinate result);

	public void vectorTo3D(WB_Coordinate p, WB_MutableCoordinate result);

	public void vectorTo3D(double x, double y, double z, WB_MutableCoordinate result);

	public void vectorTo3D(double x, double y, WB_MutableCoordinate result);

}
