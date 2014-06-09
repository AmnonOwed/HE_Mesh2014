package wblut.geom.interfaces;

import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Simplex;

public interface Triangle extends WB_Simplex {

	public WB_Plane getPlane();

	public WB_Point getCenter();

	public WB_Point getCentroid();

	public WB_Point getCircumcenter();

	public WB_Point getOrthocenter();

	public WB_Point getPointFromTrilinear(final double x, final double y,
			final double z);

	public WB_Point getPointFromBarycentric(final double x, final double y,
			final double z);

	public WB_Point getBarycentric(final WB_Coordinate p);

	public WB_Point p1();

	public WB_Point p2();

	public WB_Point p3();

}
