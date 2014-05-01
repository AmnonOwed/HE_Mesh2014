package wblut.processing;

import java.util.Collection;
import java.util.List;

import processing.core.PApplet;
import processing.core.PShape;
import wblut.geom.WB_AABB;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateSequence;
import wblut.geom.WB_Geometry;
import wblut.geom.WB_GeometryCollection;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Grid3D;
import wblut.geom.WB_Line;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Ring;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Triangulation2D;
import wblut.geom.WB_Vector;

public class WB_Render3D {
	private final PApplet home;
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_Render3D(final PApplet home) {
		this.home = home;
	}

	public void drawPoint(final WB_Coordinate p) {
		home.point(p.xf(), p.yf(), p.zf());
	}

	public void drawPoint(final WB_Coordinate p, final double r) {
		home.pushMatrix();
		home.translate(p.xf(), p.yf(), p.zf());
		home.box((float) r);
		home.popMatrix();
	}

	public void drawVector(final WB_Coordinate v, final WB_Coordinate p,
			final double r) {
		home.pushMatrix();
		home.translate(p.xf(), p.yf(), p.zf());
		home.line(0f, 0f, 0f, (float) (r * v.xd()), (float) (r * v.yd()),
				(float) (r * v.zd()));
		home.popMatrix();
	}

	public void drawLine(final WB_Line L, final double d) {
		home.line((float) (L.getOrigin().xd() - d * L.getDirection().xd()),
				(float) (L.getOrigin().yd() - d * L.getDirection().yd()),
				(float) (L.getOrigin().zd() - d * L.getDirection().zd()),
				(float) (L.getOrigin().xd() + d * L.getDirection().xd()),
				(float) (L.getOrigin().yd() + d * L.getDirection().yd()),
				(float) (L.getOrigin().zd() + d * L.getDirection().zd()));
	}

	public void drawRay(final WB_Ray R, final double d) {
		home.line((float) (R.getOrigin().xd()), (float) (R.getOrigin().yd()),
				(float) (R.getOrigin().zd()), (float) (R.getOrigin().xd() + d
						* R.getDirection().xd()),
				(float) (R.getOrigin().yd() + d * R.getDirection().yd()),
				(float) (R.getOrigin().zd() + d * R.getDirection().zd()));
	}

	public void drawSegment(final WB_Segment S) {
		home.line((float) (S.getOrigin().xd()), (float) (S.getOrigin().yd()),
				(float) (S.getOrigin().zd()), (float) (S.getEndpoint().xd()),
				(float) (S.getEndpoint().yd()), (float) (S.getEndpoint().zd()));
	}

	public void drawSegment(final WB_Coordinate p, final WB_Coordinate q) {
		home.line((float) (p.xd()), (float) (p.yd()), (float) (p.zd()),
				(float) (q.xd()), (float) (q.yd()), (float) (q.zd()));
	}

	public void drawPolyLine(final WB_PolyLine P) {
		for (int i = 0; i < P.getNumberOfPoints() - 1; i++) {

			home.line((float) (P.getPoint(i).xd()),
					(float) (P.getPoint(i).yd()), (float) (P.getPoint(i).zd()),
					(float) (P.getPoint(i + 1).xd()),
					(float) (P.getPoint(i + 1).yd()),
					(float) (P.getPoint(i + 1).zd()));
		}
	}

	public void drawRing(final WB_Ring P) {
		for (int i = 0, j = P.getNumberOfPoints() - 1; i < P
				.getNumberOfPoints(); j = i++) {

			home.line((float) (P.getPoint(j).xd()),
					(float) (P.getPoint(j).yd()), (float) (P.getPoint(j).zd()),
					(float) (P.getPoint(i).xd()), (float) (P.getPoint(i).yd()),
					(float) (P.getPoint(i).zd()));
		}
	}

	public void drawSimplePolygon(final WB_Polygon P) {
		{
			home.beginShape(home.POLYGON);
			for (int i = 0; i < P.getNumberOfPoints(); i++) {
				vertex(P.getPoint(i));
			}
		}
		home.endShape();
	}

	public void drawPolygon(final WB_Polygon P) {
		final int[][] tris = P.getTriangles();
		for (final int[] tri : tris) {
			drawTriangle(P.getPoint(tri[0]), P.getPoint(tri[1]),
					P.getPoint(tri[2]));
		}
	}

	public void drawPolygonEdges(final WB_Polygon P) {
		home.beginShape();
		int index = 0;
		for (int i = 0; i < P.getNumberOfPoints(); i++) {
			vertex(P.getPoint(index++));
		}
		home.endShape(home.CLOSE);
		final int[] nph = P.getNumberOfPointsPerHole();
		for (int i = 0; i < P.getNumberOfHoles(); i++) {
			home.beginShape();
			for (int j = 0; j < nph[i]; j++) {
				vertex(P.getPoint(index++));
			}
			home.endShape(home.CLOSE);
		}

	}

	public void drawCircle(final WB_Circle C) {
		home.pushMatrix();

		home.translate((float) C.getCenter().xd(), (float) C.getCenter().yd(),
				(float) C.getCenter().zd());

		final WB_Transform T = new WB_Transform(geometryfactory.Z(),
				C.getNormal());
		final WB_Vector angles = T.getEulerAnglesXYZ();
		home.rotateZ(angles.zf());
		home.rotateY(angles.yf());
		home.rotateX(angles.xf());
		home.ellipse(0, 0, 2 * (float) C.getRadius(), 2 * (float) C.getRadius());
		home.popMatrix();
	}

	public void drawTriangle(final WB_Triangle T) {
		home.beginShape(home.TRIANGLE);
		vertex(T.p1());
		vertex(T.p2());
		vertex(T.p3());
		home.endShape();
	}

	public void drawTriangle(final WB_Coordinate p1, final WB_Coordinate p2,
			final WB_Coordinate p3) {
		home.beginShape(home.TRIANGLE);
		vertex(p1);
		vertex(p2);
		vertex(p3);
		home.endShape();
	}

	public void drawTriangle(final int[] tri,
			final List<? extends WB_Coordinate> points) {
		home.beginShape(home.TRIANGLE);
		vertex(points.get(tri[0]));
		vertex(points.get(tri[1]));
		vertex(points.get(tri[2]));
		home.endShape();
	}

	public void drawTriangle(final int[] tri, final WB_Coordinate[] points) {
		home.beginShape(home.TRIANGLE);
		vertex(points[tri[0]]);
		vertex(points[tri[1]]);
		vertex(points[tri[2]]);
		home.endShape();
	}

	public void drawTriangulation(final WB_Triangulation2D tri,
			final List<? extends WB_Coordinate> points) {
		final int[][] triangles = tri.getTriangles();
		home.beginShape(home.TRIANGLES);
		for (final int[] triangle : triangles) {
			vertex(points.get(triangle[0]));
			vertex(points.get(triangle[1]));
			vertex(points.get(triangle[2]));
		}
		home.endShape();
	}

	public void drawTriangulationEdges(final WB_Triangulation2D tri,
			final List<? extends WB_Coordinate> points) {
		final int[][] edges = tri.getEdges();

		for (final int[] edge : edges) {
			drawSegment(points.get(edge[0]), points.get(edge[1]));
		}

	}

	public void drawPlane(final WB_Plane P, final double d) {
		home.beginShape(home.QUAD);
		home.vertex((float) (P.getOrigin().xd() - d * P.getU().xd() - d
				* P.getV().xd()), (float) (P.getOrigin().yd() - d
				* P.getU().yd() - d * P.getV().yd()), (float) (P.getOrigin()
				.zd() - d * P.getU().zd() - d * P.getV().zd()));
		home.vertex((float) (P.getOrigin().xd() - d * P.getU().xd() + d
				* P.getV().xd()), (float) (P.getOrigin().yd() - d
				* P.getU().yd() + d * P.getV().yd()), (float) (P.getOrigin()
				.zd() - d * P.getU().zd() + d * P.getV().zd()));
		home.vertex((float) (P.getOrigin().xd() + d * P.getU().xd() + d
				* P.getV().xd()), (float) (P.getOrigin().yd() + d
				* P.getU().yd() + d * P.getV().yd()), (float) (P.getOrigin()
				.zd() + d * P.getU().zd() + d * P.getV().zd()));
		home.vertex((float) (P.getOrigin().xd() + d * P.getU().xd() - d
				* P.getV().xd()), (float) (P.getOrigin().yd() + d
				* P.getU().yd() - d * P.getV().yd()), (float) (P.getOrigin()
				.zd() + d * P.getU().zd() - d * P.getV().zd()));
		home.endShape();
	}

	private void vertex(final WB_Coordinate p) {
		home.vertex(p.xf(), p.yf(), p.zf());

	}

	public void draw(final WB_Geometry geometry, final double... f) {
		if (geometry instanceof WB_Coordinate) {
			if (f.length == 0) {
				drawPoint((WB_Coordinate) geometry);
			} else if (f.length == 1) {
				drawPoint((WB_Coordinate) geometry, f[0]);
			}
		} else if (geometry instanceof WB_Segment) {
			if (f.length == 0) {
				drawSegment((WB_Segment) geometry);
			}
		} else if (geometry instanceof WB_Ray) {
			if (f.length == 1) {
				drawRay((WB_Ray) geometry, f[0]);
			}
		} else if (geometry instanceof WB_Line) {
			if (f.length == 1) {
				drawLine((WB_Line) geometry, f[0]);
			}
		} else if (geometry instanceof WB_Circle) {
			if (f.length == 0) {
				drawCircle((WB_Circle) geometry);
			}
		} else if (geometry instanceof WB_Triangle) {
			if (f.length == 0) {
				drawTriangle((WB_Triangle) geometry);
			}
		} else if (geometry instanceof WB_Polygon) {
			if (f.length == 0) {
				drawPolygon((WB_Polygon) geometry);
			}
		} else if (geometry instanceof WB_Ring) {
			if (f.length == 0) {
				drawRing((WB_Ring) geometry);
			}
		} else if (geometry instanceof WB_PolyLine) {
			if (f.length == 0) {
				drawPolyLine((WB_PolyLine) geometry);
			}
		} else if (geometry instanceof WB_GeometryCollection) {
			final WB_GeometryCollection geo = (WB_GeometryCollection) geometry;
			for (int i = 0; i < geo.getNumberOfGeometries(); i++) {
				draw(geo.getGeometry(i), f);
			}
		} else if (geometry instanceof WB_Plane) {
			if (f.length == 1) {
				drawPlane((WB_Plane) geometry, f[0]);
			}
		} else if (geometry instanceof WB_Mesh) {
			if (f.length == 0) {
				drawMesh((WB_Mesh) geometry);
			}
		}

	}

	public void draw(final Collection<? extends WB_Geometry> geometry,
			final double... f) {
		for (final WB_Geometry geo : geometry) {
			draw(geo, f);

		}
	}

	public void draw(final WB_Geometry[] geometry, final double... f) {
		for (final WB_Geometry geo : geometry) {
			draw(geo, f);

		}
	}

	private void drawPolygon(final int[] indices,
			final WB_CoordinateSequence points) {
		if (points != null && indices != null) {
			home.beginShape(PApplet.POLYGON);
			for (final int indice : indices) {
				home.vertex((float) points.get(indice, 0),
						(float) points.get(indice, 1),
						(float) points.get(indice, 2));
			}
			home.endShape(PApplet.CLOSE);
		}
	}

	public void drawMesh(final WB_Mesh mesh) {
		if (mesh == null) {
			return;
		}
		for (final int[] face : mesh.getFaces()) {
			drawPolygon(face, mesh.getVertices());
		}

	}

	public PShape toPShape(final WB_Mesh mesh) {
		final PShape retained = home.createShape();
		retained.beginShape(home.TRIANGLES);
		final WB_Mesh lmesh = geometryfactory.createTriMesh(mesh);
		final WB_Vector v = geometryfactory.createVector();
		final WB_Point p = geometryfactory.createPoint();
		final WB_CoordinateSequence seq = lmesh.getVertices();
		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			v._set(lmesh.getVertexNormal(id));
			retained.normal((float) v.xd(), (float) v.yd(), (float) v.zd());
			p._set(seq, id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			v._set(lmesh.getVertexNormal(id));
			retained.normal((float) v.xd(), (float) v.yd(), (float) v.zd());
			p._set(seq, id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			v._set(lmesh.getVertexNormal(id));
			retained.normal((float) v.xd(), (float) v.yd(), (float) v.zd());
			p._set(seq, id);
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		retained.disableStyle();
		return retained;
	}

	public void drawTetrahedron(final WB_Coordinate p0, final WB_Coordinate p1,
			final WB_Coordinate p2, final WB_Coordinate p3) {
		home.beginShape(PApplet.TRIANGLES);
		vertex(p0);
		vertex(p1);
		vertex(p2);

		vertex(p1);
		vertex(p0);
		vertex(p3);

		vertex(p2);
		vertex(p1);
		vertex(p3);

		vertex(p0);
		vertex(p2);
		vertex(p3);

		home.endShape();
	}

	public void drawTetrahedron(final int[] indices,
			final List<? extends WB_Coordinate> points) {
		if (points != null && indices != null) {
			drawTetrahedron(points.get(indices[0]), points.get(indices[1]),
					points.get(indices[2]), points.get(indices[3]));
		}
	}

	public void drawTetrahedra(final int[] indices,
			final List<? extends WB_Coordinate> points) {
		if (points != null && indices != null) {
			for (int i = 0; i < indices.length; i += 4) {
				drawTetrahedron(points.get(indices[i]),
						points.get(indices[i + 1]), points.get(indices[i + 2]),
						points.get(indices[i + 3]));
			}
		}
	}

	public void drawTetrahedron(final int[] indices,
			final WB_Coordinate[] points) {
		if (points != null && indices != null) {
			drawTetrahedron(points[indices[0]], points[indices[1]],
					points[indices[2]], points[indices[3]]);
		}
	}

	public void drawTetrahedra(final int[] indices, final WB_Coordinate[] points) {
		if (points != null && indices != null) {
			for (int i = 0; i < indices.length; i += 4) {
				drawTetrahedron(points[indices[i]], points[indices[i + 1]],
						points[indices[i + 2]], points[indices[i + 3]]);
			}
		}
	}

	public void drawGrid(final WB_Grid3D grid) {
		float x, y, z;
		float xl, yl, zl;
		float xu, yu, zu;
		xl = (float) grid.getLowX(0);
		yl = (float) grid.getLowY(0);
		zl = (float) grid.getLowZ(0);
		xu = (float) grid.getLowX(grid.Ni());
		yu = (float) grid.getLowY(grid.Nj());
		zu = (float) grid.getLowZ(grid.Nk());

		for (int i = 0; i < grid.Ni() + 1; i++) {
			x = (float) grid.getLowX(i);
			for (int j = 0; j < grid.Nj() + 1; j++) {
				y = (float) grid.getLowY(j);
				home.line(x, y, zl, x, y, zu);
			}
		}

		for (int i = 0; i < grid.Ni() + 1; i++) {
			x = (float) grid.getLowX(i);
			for (int k = 0; k < grid.Nk() + 1; k++) {
				z = (float) grid.getLowZ(k);
				home.line(x, yl, z, x, yu, z);
			}
		}

		for (int j = 0; j < grid.Nj() + 1; j++) {
			y = (float) grid.getLowY(j);
			for (int k = 0; k < grid.Nk() + 1; k++) {
				z = (float) grid.getLowZ(k);
				home.line(xl, y, z, xu, y, z);
			}
		}
	}

	public void draw(final WB_AABB AABB) {
		home.pushMatrix();
		home.translate(AABB.getCenter().xf(), AABB.getCenter().yf(), AABB
				.getCenter().zf());
		home.box((float) AABB.getWidth(), (float) AABB.getHeight(),
				(float) AABB.getDepth());
		home.popMatrix();
	}

}
