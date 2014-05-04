package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javolution.util.FastList;

import com.vividsolutions.jts.algorithm.ConvexHull;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;
import com.vividsolutions.jts.triangulate.quadedge.QuadEdgeSubdivision;

public class WB_Voronoi {
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public static List<WB_VoronoiCell3D> getVoronoi3D(
			final WB_Coordinate[] points, final WB_AABB aabb, double precision) {
		WB_Delaunay triangulation = WB_Delaunay.getTriangulation3D(points,
				precision);

		final int nv = points.length;
		final List<WB_VoronoiCell3D> result = new FastList<WB_VoronoiCell3D>(nv);
		for (int i = 0; i < nv; i++) {

			int[] tetras = triangulation.Vertices[i];

			final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
			for (int t = 0; t < tetras.length; t++) {
				hullpoints.add(triangulation.circumcenters[tetras[t]]);
			}
			final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(hullpoints,
					geometryfactory.createPoint(points[i]), i);

			if (vor.cell != null)
				vor.constrain(aabb);
			if (vor.cell != null) {
				result.add(vor);
			}
		}
		return result;
	}

	public static List<WB_VoronoiCell3D> getVoronoi3D(
			final List<? extends WB_Coordinate> points, final WB_AABB aabb,
			double precision) {

		WB_Delaunay triangulation = WB_Delaunay.getTriangulation3D(points,
				precision);

		final int nv = points.size();
		final List<WB_VoronoiCell3D> result = new FastList<WB_VoronoiCell3D>(nv);
		for (int i = 0; i < nv; i++) {

			int[] tetras = triangulation.Vertices[i];

			final List<WB_Point> hullpoints = new ArrayList<WB_Point>();
			for (int t = 0; t < tetras.length; t++) {
				hullpoints.add(triangulation.circumcenters[tetras[t]]);
			}
			hullpoints.add(new WB_Point(points.get(i)));
			final WB_VoronoiCell3D vor = new WB_VoronoiCell3D(hullpoints,
					geometryfactory.createPoint(points.get(i)), i);
			if (vor.cell != null)
				vor.constrain(aabb);
			if (vor.cell != null) {
				result.add(vor);
			}
		}
		return result;
	}

	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Point[] points,
			final WB_Context2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return getVoronoi2D(coords, context);
	}

	public static List<WB_VoronoiCell2D> getVoronoi2D(
			final Collection<? extends WB_Point> points,
			final WB_Context2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return getVoronoi2D(coords, context);
	}

	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Point[] points,
			final double d, final WB_Context2D context) {
		return getVoronoi2D(points, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getVoronoi2D(
			final Collection<? extends WB_Point> points, final double d,
			final WB_Context2D context) {
		return getVoronoi2D(points, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getVoronoi2D(final WB_Point[] points,
			final double d, final int c, final WB_Context2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return getVoronoi2D(coords, d, c, context);
	}

	public static List<WB_VoronoiCell2D> getVoronoi2D(
			final Collection<? extends WB_Point> points, final double d,
			final int c, final WB_Context2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return getVoronoi2D(coords, d, c, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final WB_Point[] points, final WB_Context2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return getClippedVoronoi2D(coords, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final WB_Point[] points, final WB_Point[] boundary,
			final WB_Context2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, context));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return getClippedVoronoi2D(coords, bdcoords, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final Collection<? extends WB_Point> points,
			final WB_Context2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return getClippedVoronoi2D(coords, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final Collection<? extends WB_Point> points,
			final Collection<? extends WB_Point> boundary,
			final WB_Context2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Point p : boundary) {
			bdcoords.add(toCoordinate(p, id, context));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return getClippedVoronoi2D(coords, bdcoords, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final WB_Point[] points, final double d, final WB_Context2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return getClippedVoronoi2D(coords, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final WB_Point[] points, final WB_Point[] boundary, final double d,
			final WB_Context2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, context));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return getClippedVoronoi2D(coords, bdcoords, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final Collection<? extends WB_Point> points,
			final Collection<? extends WB_Point> boundary, final double d,
			final WB_Context2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Point p : boundary) {
			bdcoords.add(toCoordinate(p, id, context));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return getClippedVoronoi2D(coords, bdcoords, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final Collection<? extends WB_Point> points, final double d,
			final WB_Context2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return getClippedVoronoi2D(coords, d, 2, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final WB_Point[] points, final double d, final int c,
			final WB_Context2D context) {
		final int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		return getClippedVoronoi2D(coords, d, c, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final WB_Point[] points, final WB_Point[] boundary, final double d,
			final int c, final WB_Context2D context) {
		int n = points.length;
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			coords.add(toCoordinate(points[i], i, context));
		}
		n = boundary.length;
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		for (int i = 0; i < n; i++) {
			bdcoords.add(toCoordinate(boundary[i], i, context));
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return getClippedVoronoi2D(coords, bdcoords, d, c, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final Collection<? extends WB_Point> points, final double d,
			final int c, final WB_Context2D context) {
		final int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		return getClippedVoronoi2D(coords, d, c, context);
	}

	public static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final Collection<? extends WB_Point> points,
			final Collection<? extends WB_Point> boundary, final double d,
			final int c, final WB_Context2D context) {
		int n = points.size();
		final ArrayList<Coordinate> coords = new ArrayList<Coordinate>(n);
		int id = 0;
		for (final WB_Point p : points) {
			coords.add(toCoordinate(p, id, context));
			id++;
		}
		n = boundary.size();
		final ArrayList<Coordinate> bdcoords = new ArrayList<Coordinate>(n);
		id = 0;
		for (final WB_Point p : boundary) {
			bdcoords.add(toCoordinate(p, id, context));
			id++;
		}
		if (!bdcoords.get(0).equals(bdcoords.get(n - 1))) {
			bdcoords.add(bdcoords.get(0));
		}
		return getClippedVoronoi2D(coords, bdcoords, d, c, context);
	}

	private static List<WB_VoronoiCell2D> getVoronoi2D(
			final ArrayList<Coordinate> coords, final WB_Context2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes
				.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>(
				npolys);
		for (int i = 0; i < npolys; i++) {
			final Polygon poly = (Polygon) polys.getGeometryN(i);
			final Coordinate[] polycoord = poly.getCoordinates();
			final List<WB_Point> polypoints = new FastList<WB_Point>(
					polycoord.length);
			for (final Coordinate element : polycoord) {
				polypoints.add(toPoint(element.x, element.y, context));
			}
			final Point centroid = poly.getCentroid();
			final WB_Point pc = (centroid == null) ? null : toPoint(
					centroid.getX(), centroid.getY(), context);
			final int index = (int) ((Coordinate) poly.getUserData()).z;

			final double area = poly.getArea();
			result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory
					.createPoint(coords.get(index)), area, pc));
		}
		return result;
	}

	private static List<WB_VoronoiCell2D> getVoronoi2D(
			final ArrayList<Coordinate> coords, final double d, final int c,
			final WB_Context2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes
				.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>(
				npolys);
		for (int i = 0; i < npolys; i++) {
			Geometry poly = polys.getGeometryN(i);
			poly = poly.buffer(-d, c);
			poly = polys.getGeometryN(0);
			final Coordinate[] polycoord = poly.getCoordinates();
			final List<WB_Point> polypoints = new FastList<WB_Point>(
					polycoord.length);
			;
			for (final Coordinate element : polycoord) {
				polypoints.add(toPoint(element.x, element.y, context));
			}
			final Point centroid = poly.getCentroid();
			final WB_Point pc = (centroid == null) ? null : toPoint(
					centroid.getX(), centroid.getY(), context);
			final int index = (int) ((Coordinate) poly.getUserData()).z;
			final double area = poly.getArea();
			result.add(new WB_VoronoiCell2D(polypoints, index, geometryfactory
					.createPoint(coords.get(index)), area, pc));
		}
		return result;
	}

	private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final ArrayList<Coordinate> coords, final WB_Context2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes
				.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>(
				npolys);
		Coordinate[] coordsArray = new Coordinate[coords.size()];
		coordsArray = coords.toArray(coordsArray);
		final ConvexHull ch = new ConvexHull(coordsArray, new GeometryFactory());
		final Geometry hull = ch.getConvexHull();
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			final Geometry intersect = poly.intersection(hull.getGeometryN(0));
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			if (intersect.getGeometryType().equals("Polygon")) {
				poly = (Polygon) intersect.getGeometryN(0);
				final Coordinate[] polycoord = poly.getCoordinates();
				final List<WB_Point> polypoints = new FastList<WB_Point>(
						polycoord.length);
				;
				for (final Coordinate element : polycoord) {
					polypoints.add(toPoint(element.x, element.y, context));
				}
				final Point centroid = poly.getCentroid();
				final WB_Point pc = (centroid == null) ? null : toPoint(
						centroid.getX(), centroid.getY(), context);
				final int index = (int) cellindex;
				final double area = poly.getArea();
				result.add(new WB_VoronoiCell2D(polypoints, index,
						geometryfactory.createPoint(coords.get(index)), area,
						pc));
			}
		}
		return result;
	}

	private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final ArrayList<Coordinate> coords, final double d, final int c,
			final WB_Context2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes
				.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>(
				npolys);
		Coordinate[] coordsArray = new Coordinate[coords.size()];
		coordsArray = coords.toArray(coordsArray);
		final ConvexHull ch = new ConvexHull(coordsArray, new GeometryFactory());
		final Geometry hull = ch.getConvexHull();
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly.intersection(hull.getGeometryN(0));
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			if (intersect.getGeometryType().equals("Polygon")) {
				poly = (Polygon) intersect.getGeometryN(0);
				final Coordinate[] polycoord = poly.getCoordinates();
				final List<WB_Point> polypoints = new FastList<WB_Point>(
						polycoord.length);
				;
				for (final Coordinate element : polycoord) {
					polypoints.add(toPoint(element.x, element.y, context));
				}
				final Point centroid = poly.getCentroid();
				final WB_Point pc = (centroid == null) ? null : toPoint(
						centroid.getX(), centroid.getY(), context);
				final int index = (int) cellindex;
				final double area = poly.getArea();
				result.add(new WB_VoronoiCell2D(polypoints, index,
						geometryfactory.createPoint(coords.get(index)), area,
						pc));
			}
		}
		return result;
	}

	private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final ArrayList<Coordinate> coords,
			final ArrayList<Coordinate> bdcoords, final WB_Context2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes
				.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>(
				npolys);
		Coordinate[] bdcoordsArray = new Coordinate[bdcoords.size()];
		bdcoordsArray = bdcoords.toArray(bdcoordsArray);
		final Polygon hull = new GeometryFactory().createPolygon(bdcoordsArray);
		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			final Geometry intersect = poly.intersection(hull);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			if (intersect.getGeometryType().equals("Polygon")
					&& !intersect.isEmpty()) {
				poly = (Polygon) intersect.getGeometryN(0);
				final Coordinate[] polycoord = poly.getCoordinates();
				final List<WB_Point> polypoints = new FastList<WB_Point>(
						polycoord.length);
				;
				for (final Coordinate element : polycoord) {
					polypoints.add(toPoint(element.x, element.y, context));
				}
				final Point centroid = poly.getCentroid();
				final WB_Point pc = (centroid == null) ? null : toPoint(
						centroid.getX(), centroid.getY(), context);
				final int index = (int) cellindex;
				final double area = poly.getArea();
				result.add(new WB_VoronoiCell2D(polypoints, index,
						geometryfactory.createPoint(coords.get(index)), area,
						pc));
			}
		}
		return result;
	}

	private static List<WB_VoronoiCell2D> getClippedVoronoi2D(
			final ArrayList<Coordinate> coords,
			final ArrayList<Coordinate> bdcoords, final double d, final int c,
			final WB_Context2D context) {
		final DelaunayTriangulationBuilder dtb = new DelaunayTriangulationBuilder();
		dtb.setSites(coords);
		final QuadEdgeSubdivision qes = dtb.getSubdivision();
		final GeometryCollection polys = (GeometryCollection) qes
				.getVoronoiDiagram(new GeometryFactory());
		final int npolys = polys.getNumGeometries();
		final List<WB_VoronoiCell2D> result = new FastList<WB_VoronoiCell2D>(
				npolys);
		Coordinate[] bdcoordsArray = new Coordinate[bdcoords.size()];
		bdcoordsArray = bdcoords.toArray(bdcoordsArray);
		final Polygon hull = new GeometryFactory().createPolygon(bdcoordsArray);

		for (int i = 0; i < npolys; i++) {
			Polygon poly = (Polygon) polys.getGeometryN(i);
			Geometry intersect = poly.intersection(hull);
			intersect = intersect.buffer(-d, c);
			final double cellindex = ((Coordinate) poly.getUserData()).z;
			if (intersect.getGeometryType().equals("Polygon")
					&& !intersect.isEmpty()) {
				poly = (Polygon) intersect.getGeometryN(0);
				final Coordinate[] polycoord = poly.getCoordinates();
				final List<WB_Point> polypoints = new FastList<WB_Point>(
						polycoord.length);
				;
				for (final Coordinate element : polycoord) {
					polypoints.add(toPoint(element.x, element.y, context));
				}
				final Point centroid = poly.getCentroid();
				final WB_Point pc = (centroid == null) ? null : toPoint(
						centroid.getX(), centroid.getY(), context);
				final int index = (int) cellindex;
				final double area = poly.getArea();
				result.add(new WB_VoronoiCell2D(polypoints, index,
						geometryfactory.createPoint(coords.get(index)), area,
						pc));
			}
		}
		return result;
	}

	private static Coordinate toCoordinate(final WB_Coordinate p, final int i,
			final WB_Context2D context) {
		final WB_Point tmp = geometryfactory.createPoint();
		context.pointTo2D(p, tmp);
		final Coordinate c = new Coordinate(tmp.xd(), tmp.yd(), i);
		return c;

	}

	private static WB_Point toPoint(final double x, final double y,
			final WB_Context2D context) {
		final WB_Point tmp = geometryfactory.createPoint();
		context.pointTo3D(x, y, 0, tmp);
		return tmp;

	}
}
