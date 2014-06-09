package wblut.geom.interfaces;

import java.util.List;

import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_IndexedSegment;
import wblut.geom.WB_IndexedTriangle;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_SimplePolygon2D;

public interface SimplePolygon {

	public void set(final SimplePolygon poly);

	public WB_Point closestPoint(final WB_Coordinate p);

	public int closestIndex(final WB_Coordinate p);

	public WB_Plane getPlane();

	public WB_Convex isConvex(final int i);

	public List<WB_IndexedTriangle> triangulate();

	public List<WB_IndexedSegment> getSegments();

	public WB_SimplePolygon2D toPolygon2D();

	public int getN();

	public int getIndex(int i);

	public WB_Point getPoint(int i);

	public WB_Point[] getPoints();

}