package wblut.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.opengl.PGraphics3D;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateSequence;
import wblut.geom.WB_Curve;
import wblut.geom.WB_FaceListMesh;
import wblut.geom.WB_Frame;
import wblut.geom.WB_FrameNode;
import wblut.geom.WB_FrameStrut;
import wblut.geom.WB_Geometry;
import wblut.geom.WB_GeometryCollection;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Grid3D;
import wblut.geom.WB_IndexedPoint;
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
import wblut.geom.interfaces.Segment;
import wblut.geom.interfaces.SimplePolygon;
import wblut.geom.interfaces.Triangle;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_FaceIntersection;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Intersection;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;

public class WB_Render3D {
	private final PGraphics3D home;
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_Render3D(final PApplet home) {
		if (home.g == null) {
			throw new IllegalArgumentException(
					"WB_Render3D can only be used after size()");
		}
		if (!(home.g instanceof PGraphics3D)) {
			throw new IllegalArgumentException(
					"WB_Render3D can only be with P3D, OPENGL or derived ProcessingPGraphics object");
		}
		this.home = (PGraphics3D) home.g;
	}

	public WB_Render3D(final PGraphics3D home) {
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

	public void drawVector(final WB_Coordinate p, final WB_Coordinate v,
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
			home.beginShape(PConstants.POLYGON);
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
		home.endShape(PConstants.CLOSE);
		final int[] nph = P.getNumberOfPointsPerHole();
		for (int i = 0; i < P.getNumberOfHoles(); i++) {
			home.beginShape();
			for (int j = 0; j < nph[i]; j++) {
				vertex(P.getPoint(index++));
			}
			home.endShape(PConstants.CLOSE);
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

	public void drawTriangle(final WB_Coordinate p1, final WB_Coordinate p2,
			final WB_Coordinate p3) {
		home.beginShape(PConstants.TRIANGLE);
		vertex(p1);
		vertex(p2);
		vertex(p3);
		home.endShape();
	}

	public void drawTriangle(final int[] tri,
			final List<? extends WB_Coordinate> points) {
		home.beginShape(PConstants.TRIANGLE);
		vertex(points.get(tri[0]));
		vertex(points.get(tri[1]));
		vertex(points.get(tri[2]));
		home.endShape();
	}

	public void drawTriangle(final int[] tri, final WB_Coordinate[] points) {
		home.beginShape(PConstants.TRIANGLE);
		vertex(points[tri[0]]);
		vertex(points[tri[1]]);
		vertex(points[tri[2]]);
		home.endShape();
	}

	public void drawTriangulation(final WB_Triangulation2D tri,
			final List<? extends WB_Coordinate> points) {
		final int[][] triangles = tri.getTriangles();
		home.beginShape(PConstants.TRIANGLES);
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
		home.beginShape(PConstants.QUAD);
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
			}
			else if (f.length == 1) {
				drawPoint((WB_Coordinate) geometry, f[0]);
			}
		}
		else if (geometry instanceof WB_Segment) {
			if (f.length == 0) {
				drawSegment((WB_Segment) geometry);
			}
		}
		else if (geometry instanceof WB_Ray) {
			if (f.length == 1) {
				drawRay((WB_Ray) geometry, f[0]);
			}
		}
		else if (geometry instanceof WB_Line) {
			if (f.length == 1) {
				drawLine((WB_Line) geometry, f[0]);
			}
		}
		else if (geometry instanceof WB_Circle) {
			if (f.length == 0) {
				drawCircle((WB_Circle) geometry);
			}
		}
		else if (geometry instanceof WB_Triangle) {
			if (f.length == 0) {
				drawTriangle((WB_Triangle) geometry);
			}
		}
		else if (geometry instanceof WB_Polygon) {
			if (f.length == 0) {
				drawPolygon((WB_Polygon) geometry);
			}
		}
		else if (geometry instanceof WB_Ring) {
			if (f.length == 0) {
				drawRing((WB_Ring) geometry);
			}
		}
		else if (geometry instanceof WB_PolyLine) {
			if (f.length == 0) {
				drawPolyLine((WB_PolyLine) geometry);
			}
		}
		else if (geometry instanceof WB_GeometryCollection) {
			final WB_GeometryCollection geo = (WB_GeometryCollection) geometry;
			for (int i = 0; i < geo.getNumberOfGeometries(); i++) {
				draw(geo.getGeometry(i), f);
			}
		}
		else if (geometry instanceof WB_Plane) {
			if (f.length == 1) {
				drawPlane((WB_Plane) geometry, f[0]);
			}
		}
		else if (geometry instanceof WB_FaceListMesh) {
			if (f.length == 0) {
				drawMesh((WB_FaceListMesh) geometry);
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
			home.beginShape(PConstants.POLYGON);
			for (final int indice : indices) {
				home.vertex((float) points.get(indice, 0),
						(float) points.get(indice, 1),
						(float) points.get(indice, 2));
			}
			home.endShape(PConstants.CLOSE);
		}
	}

	public void drawMesh(final WB_Mesh mesh) {
		if (mesh == null) {
			return;
		}
		for (final int[] face : mesh.getFacesAsInt()) {
			drawPolygon(face, mesh.getPoints());
		}

	}

	public PShape toSmoothPShape(final WB_FaceListMesh mesh) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final WB_FaceListMesh lmesh = geometryfactory.createTriMesh(mesh);
		final WB_Vector v = geometryfactory.createVector();
		final WB_CoordinateSequence seq = lmesh.getPoints();
		WB_IndexedPoint p = seq.getPoint(0);

		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			v._set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.getPoint(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			v._set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.getPoint(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			v._set(lmesh.getVertexNormal(id));
			retained.normal(v.xf(), v.yf(), v.zf());
			p = seq.getPoint(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		retained.disableStyle();
		return retained;
	}

	public PShape toFacetedPShape(final WB_FaceListMesh mesh) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final WB_FaceListMesh lmesh = geometryfactory.createTriMesh(mesh);
		final WB_CoordinateSequence seq = lmesh.getPoints();
		WB_IndexedPoint p = seq.getPoint(0);
		for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
			int id = lmesh.getFace(i)[0];
			p = seq.getPoint(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[1];
			p = seq.getPoint(id);
			retained.vertex(p.xf(), p.yf(), p.zf());
			id = lmesh.getFace(i)[2];
			p = seq.getPoint(id);
			;
			retained.vertex(p.xf(), p.yf(), p.zf());
		}
		retained.endShape();
		retained.disableStyle();
		return retained;
	}

	public PShape toSmoothPShape(final HE_Mesh mesh) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.get();
		lmesh.triangulate();
		WB_Vector n = new WB_Vector();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				n = v.getVertexNormal();
				retained.normal(n.xf(), n.yf(), n.zf());
				retained.vertex(v.xf(), v.yf(), v.zf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());

		}
		retained.endShape();
		retained.disableStyle();
		return retained;
	}

	public PShape toFacetedPShape(final HE_Mesh mesh) {
		final PShape retained = home.createShape();
		retained.beginShape(PConstants.TRIANGLES);
		final HE_Mesh lmesh = mesh.get();
		lmesh.triangulate();
		final Iterator<HE_Face> fItr = lmesh.fItr();
		HE_Face f;
		HE_Vertex v;
		HE_Halfedge he;
		while (fItr.hasNext()) {
			f = fItr.next();
			he = f.getHalfedge();
			do {
				v = he.getVertex();
				retained.vertex(v.xf(), v.yf(), v.zf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());

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

	public void drawGrid(final WB_Grid3D<Object> grid) {
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

	public void drawAABB(final WB_AABB AABB) {
		home.pushMatrix();
		home.translate(AABB.getCenter().xf(), AABB.getCenter().yf(), AABB
				.getCenter().zf());
		home.box((float) AABB.getWidth(), (float) AABB.getHeight(),
				(float) AABB.getDepth());
		home.popMatrix();
	}

	public void draw(final Collection<? extends WB_Coordinate> points,
			final double d) {
		for (final WB_Coordinate point : points) {
			home.pushMatrix();
			home.translate(point.xf(), point.yf(), point.zf());
			home.box((float) d);
			home.popMatrix();
		}
	}

	public void draw(final Collection<WB_Circle> circles) {
		final Iterator<WB_Circle> citr = circles.iterator();
		while (citr.hasNext()) {
			draw(citr.next());
		}

	}

	public void draw(final Collection<WB_Curve> curves, final int steps) {
		final Iterator<WB_Curve> citr = curves.iterator();
		while (citr.hasNext()) {
			drawCurve(citr.next(), steps);
		}
	}

	public void drawTree(final WB_AABBTree tree) {
		drawNode(tree.getRoot());

	}

	public void drawTree(final WB_AABBTree tree, final int level) {
		drawNode(tree.getRoot(), level);

	}

	public void draw(final WB_Coordinate point, final double d) {
		home.pushMatrix();
		home.translate(point.xf(), point.yf(), point.zf());
		home.box((float) d);
		home.popMatrix();
	}

	public void draw(final WB_Coordinate p, final WB_Coordinate q) {
		home.line(p.xf(), p.yf(), p.zf(), q.xf(), q.yf(), q.zf());
	}

	public void draw(final WB_Coordinate p, final WB_Vector v, final double d) {
		home.line(p.xf(), p.yf(), p.zf(), p.xf() + (float) d * v.xf(), p.yf()
				+ (float) d * v.yf(), p.zf() + (float) d * v.zf());
	}

	public void drawCurve(final WB_Curve C, final int steps) {
		final int n = Math.max(1, steps);
		WB_Point p0 = C.curvePoint(0);
		WB_Point p1;
		final double du = 1.0 / n;
		for (int i = 0; i < n; i++) {
			p1 = C.curvePoint((i + 1) * du);
			home.line(p0.xf(), p0.yf(), p0.zf(), p1.xf(), p1.yf(), p1.zf());
			p0 = p1;
		}

	}

	public void drawFrame(final WB_Frame frame) {
		final ArrayList<WB_FrameStrut> struts = frame.getStruts();
		for (int i = 0; i < frame.getNumberOfStruts(); i++) {
			drawFrameStrut(struts.get(i));
		}
	}

	public void drawFrameNode(final WB_FrameNode node, final double s) {
		home.pushMatrix();
		home.translate(node.xf(), node.yf(), node.zf());
		home.box((float) s);
		home.popMatrix();
	}

	public void drawFrameStrut(final WB_FrameStrut strut) {
		home.line(strut.start().xf(), strut.start().yf(), strut.start().zf(),
				strut.end().xf(), strut.end().yf(), strut.end().zf());
	}

	public void drawBezierEdges(final HE_MeshStructure mesh) {
		HE_Halfedge he;
		WB_Coordinate p0;
		WB_Coordinate p1;
		WB_Coordinate p2;
		WB_Coordinate p3;
		HE_Face f;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			home.beginShape();
			he = f.getHalfedge();
			p0 = he.getPrevInFace().getHalfedgeCenter();
			home.vertex(p0.xf(), p0.yf(), p0.zf());

			do {

				p1 = he.getVertex();
				p2 = he.getVertex();
				p3 = he.getHalfedgeCenter();

				home.bezierVertex(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(),
						p2.zf(), p3.xf(), p3.yf(), p3.zf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
			home.endShape();

		}
	}

	/**
	 * Draw one edge.
	 *
	 * @param e
	 *            edge
	 */
	public void drawEdge(final HE_Halfedge e) {
		home.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
				.getStartVertex().zf(), e.getEndVertex().xf(), e.getEndVertex()
				.yf(), e.getEndVertex().zf());
	}

	public void drawEdge(final long key, final HE_Mesh mesh) {
		final HE_Halfedge e = mesh.getHalfedgeByKey(key);
		if (e != null) {
			drawEdge(e);
		}
	}

	/**
	 * Draw edges.
	 *
	 * @param meshes
	 *            the meshes
	 */
	public void drawEdges(final Collection<? extends HE_MeshStructure> meshes) {
		final Iterator<? extends HE_MeshStructure> mItr = meshes.iterator();
		while (mItr.hasNext()) {
			drawEdges(mItr.next());
		}
	}

	/**
	 * Draw mesh edges.
	 *
	 * @param mesh
	 *            the mesh
	 */
	public void drawEdges(final HE_MeshStructure mesh) {
		final Iterator<HE_Halfedge> eItr = mesh.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			home.line(e.getVertex().xf(), e.getVertex().yf(), e.getVertex()
					.zf(), e.getEndVertex().xf(), e.getEndVertex().yf(), e
					.getEndVertex().zf());

		}
	}

	/**
	 * Draw edges of selection.
	 *
	 * @param selection
	 *            selection to draw
	 */
	public void drawEdges(final HE_Selection selection) {
		final Iterator<HE_Halfedge> eItr = selection.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			home.line(e.getVertex().xf(), e.getVertex().yf(), e.getVertex()
					.zf(), e.getEndVertex().xf(), e.getEndVertex().yf(), e
					.getEndVertex().zf());
		}
	}

	/**
	 * Draw mesh edges.
	 *
	 * @param label
	 *            the label
	 * @param mesh
	 *            the mesh
	 */
	public void drawEdgesWithLabel(final int label, final HE_MeshStructure mesh) {
		final Iterator<HE_Halfedge> eItr = mesh.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getLabel() == label) {
				home.line(e.getVertex().xf(), e.getVertex().yf(), e.getVertex()
						.zf(), e.getEndVertex().xf(), e.getEndVertex().yf(), e
						.getEndVertex().zf());
			}

		}

	}

	public void drawEdgesWithInternalLabel(final int label,
			final HE_MeshStructure mesh) {
		final Iterator<HE_Halfedge> eItr = mesh.eItr();
		HE_Halfedge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getInternalLabel() == label) {
				home.line(e.getVertex().xf(), e.getVertex().yf(), e.getVertex()
						.zf(), e.getEndVertex().xf(), e.getEndVertex().yf(), e
						.getEndVertex().zf());
			}

		}

	}

	public void drawFace(final HE_Face f) {
		drawFace(f, false);
	}

	public void drawFace(final HE_Face f, final boolean smooth) {
		final int fo = f.getFaceOrder();

		if (fo < 3) {
		}
		else if (f.getFaceOrder() == 3) {
			final int[] tri = new int[] { 0, 1, 2 };
			final List<HE_Vertex> vertices = f.getFaceVertices();
			HE_Vertex v0, v1, v2;
			WB_Vector n0, n1, n2;
			if (smooth) {
				home.beginShape(PConstants.TRIANGLES);
				v0 = vertices.get(tri[0]);
				n0 = v0.getVertexNormal();
				v1 = vertices.get(tri[1]);
				n1 = v1.getVertexNormal();
				v2 = vertices.get(tri[2]);
				n2 = v2.getVertexNormal();
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n1.xf(), n1.yf(), n1.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.normal(n2.xf(), n2.yf(), n2.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.endShape();

			}
			else {
				home.beginShape(PConstants.TRIANGLES);
				v0 = vertices.get(tri[0]);
				v1 = vertices.get(tri[1]);
				v2 = vertices.get(tri[2]);
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.endShape();

			}

		}
		else {
			final int[][] tris = f.getTriangles();
			final List<HE_Vertex> vertices = f.getFaceVertices();
			HE_Vertex v0, v1, v2;
			WB_Vector n0, n1, n2;
			int[] tri;
			if (smooth) {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					n0 = v0.getVertexNormal();
					v1 = vertices.get(tri[1]);
					n1 = v1.getVertexNormal();
					v2 = vertices.get(tri[2]);
					n2 = v2.getVertexNormal();
					home.normal(n0.xf(), n0.yf(), n0.zf());
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.normal(n1.xf(), n1.yf(), n1.zf());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.normal(n2.xf(), n2.yf(), n2.zf());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();
				}
			}
			else {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					v1 = vertices.get(tri[1]);
					v2 = vertices.get(tri[2]);
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();

				}
			}
		}
	}

	public void drawFaceFC(final HE_Face f, final boolean smooth) {
		if (f.getFaceOrder() > 2) {
			home.pushStyle();
			home.fill(f.getColor());
			final int[][] tris = f.getTriangles();
			final List<HE_Vertex> vertices = f.getFaceVertices();
			HE_Vertex v0, v1, v2;
			WB_Vector n0, n1, n2;
			int[] tri;
			if (smooth) {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					n0 = v0.getVertexNormal();
					v1 = vertices.get(tri[1]);
					n1 = v1.getVertexNormal();
					v2 = vertices.get(tri[2]);
					n2 = v2.getVertexNormal();
					home.normal(n0.xf(), n0.yf(), n0.zf());
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.normal(n1.xf(), n1.yf(), n1.zf());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.normal(n2.xf(), n2.yf(), n2.zf());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();
				}
			}
			else {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					v1 = vertices.get(tri[1]);
					v2 = vertices.get(tri[2]);
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();

				}
			}
			home.popStyle();
		}
	}

	public void drawFaceHC(final HE_Face f, final boolean smooth) {
		if (f.getFaceOrder() > 2) {
			final int[][] tris = f.getTriangles();
			final List<HE_Vertex> vertices = f.getFaceVertices();
			final List<HE_Halfedge> halfedges = f.getFaceHalfedges();
			HE_Vertex v0, v1, v2;
			WB_Vector n0, n1, n2;
			int[] tri;
			if (smooth) {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					n0 = v0.getVertexNormal();
					v1 = vertices.get(tri[1]);
					n1 = v1.getVertexNormal();
					v2 = vertices.get(tri[2]);
					n2 = v2.getVertexNormal();
					home.fill(halfedges.get(tri[0]).getColor());
					home.normal(n0.xf(), n0.yf(), n0.zf());
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.fill(halfedges.get(tri[1]).getColor());
					home.normal(n1.xf(), n1.yf(), n1.zf());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.fill(halfedges.get(tri[2]).getColor());
					home.normal(n2.xf(), n2.yf(), n2.zf());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();
				}
			}
			else {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					v1 = vertices.get(tri[1]);
					v2 = vertices.get(tri[2]);
					home.fill(halfedges.get(tri[0]).getColor());
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.fill(halfedges.get(tri[1]).getColor());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.fill(halfedges.get(tri[2]).getColor());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();

				}
			}
		}
	}

	public void drawFaceVC(final HE_Face f, final boolean smooth) {
		if (f.getFaceOrder() > 2) {
			final int[][] tris = f.getTriangles();
			final List<HE_Vertex> vertices = f.getFaceVertices();
			HE_Vertex v0, v1, v2;
			WB_Vector n0, n1, n2;
			int[] tri;
			if (smooth) {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					n0 = v0.getVertexNormal();
					v1 = vertices.get(tri[1]);
					n1 = v1.getVertexNormal();
					v2 = vertices.get(tri[2]);
					n2 = v2.getVertexNormal();
					home.fill(v0.getColor());
					home.normal(n0.xf(), n0.yf(), n0.zf());
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.fill(v1.getColor());
					home.normal(n1.xf(), n1.yf(), n1.zf());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.fill(v2.getColor());
					home.normal(n2.xf(), n2.yf(), n2.zf());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();
				}
			}
			else {
				for (int i = 0; i < tris.length; i++) {
					tri = tris[i];
					home.beginShape(PConstants.TRIANGLES);
					v0 = vertices.get(tri[0]);
					v1 = vertices.get(tri[1]);
					v2 = vertices.get(tri[2]);
					home.fill(v0.getColor());
					home.vertex(v0.xf(), v0.yf(), v0.zf());
					home.fill(v1.getColor());
					home.vertex(v1.xf(), v1.yf(), v1.zf());
					home.fill(v2.getColor());
					home.vertex(v2.xf(), v2.yf(), v2.zf());
					home.endShape();

				}
			}
		}
	}

	public void drawFace(final Long key, final boolean smooth,
			final HE_MeshStructure mesh) {
		final HE_Face f = mesh.getFaceByKey(key);
		if (f != null) {
			drawFace(f, smooth);
		}

	}

	public void drawFace(final Long key, final HE_MeshStructure mesh) {

		final HE_Face f = mesh.getFaceByKey(key);
		if (f != null) {
			drawFace(f, false);
		}

	}

	public void drawFaceFC(final HE_Face f) {
		drawFaceFC(f, false);
	}

	public void drawFaceHC(final HE_Face f) {
		drawFaceHC(f, false);
	}

	public void drawFaceVC(final HE_Face f) {
		drawFaceVC(f, false);
	}

	public void drawFaces(final Collection<? extends HE_MeshStructure> meshes) {
		final Iterator<? extends HE_MeshStructure> mItr = meshes.iterator();
		while (mItr.hasNext()) {
			drawFaces(mItr.next());
		}
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 *
	 * @param mesh
	 *            the mesh
	 */
	public void drawFaces(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next());
		}
	}

	/**
	 * Draw mesh faces matching label. Typically used with noStroke();
	 *
	 * @param label
	 *            the label
	 * @param mesh
	 *            the mesh
	 */
	public void drawFacesWithLabel(final int label, final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == label) {
				drawFace(f);
			}

		}
	}

	public void drawFacesWithInternalLabel(final int label,
			final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getInternalLabel() == label) {
				drawFace(f);
			}

		}
	}

	public void drawFacesFC(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceFC(fItr.next());
		}
	}

	public void drawFacesHC(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceHC(fItr.next());
		}
	}

	public void drawFacesVC(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceVC(fItr.next());
		}
	}

	/**
	 * Draw one face using vertex normals.
	 *
	 * @param key
	 *            key of face
	 * @param mesh
	 *            the mesh
	 */
	public void drawFaceSmooth(final Long key, final HE_MeshStructure mesh) {
		new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		if (f != null) {
			drawFace(f, true);
		}
	}

	public void drawFaceSmooth(final HE_Face f) {
		new ArrayList<HE_Vertex>();
		drawFace(f, true);
	}

	public void drawFaceSmoothFC(final Long key, final HE_MeshStructure mesh) {
		new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		if (f != null) {
			drawFaceFC(f, true);
		}
	}

	public void drawFaceSmoothFC(final HE_Face f) {
		new ArrayList<HE_Vertex>();
		drawFaceFC(f, true);
	}

	public void drawFaceSmoothVC(final Long key, final HE_MeshStructure mesh) {
		new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		if (f != null) {
			drawFaceVC(f, true);
		}
	}

	public void drawFaceSmoothVC(final HE_Face f) {
		new ArrayList<HE_Vertex>();
		drawFaceVC(f, true);
	}

	public void drawFaceSmoothHC(final Long key, final HE_MeshStructure mesh) {
		new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		if (f != null) {
			drawFaceHC(f, true);
		}
	}

	public void drawFaceSmoothHC(final HE_Face f) {
		new ArrayList<HE_Vertex>();
		drawFaceHC(f, true);
	}

	/**
	 * Draw mesh faces using vertex normals. Typically used with noStroke().
	 *
	 * @param mesh
	 *            the mesh
	 */
	public void drawFacesSmooth(final HE_MeshStructure mesh) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), true);
		}

	}

	public void drawFacesSmoothFC(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceFC(fItr.next(), true);
		}
	}

	public void drawFacesSmoothHC(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceHC(fItr.next(), true);
		}
	}

	public void drawFacesSmoothVC(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceVC(fItr.next(), true);
		}
	}

	/**
	 * Draw leaf node.
	 *
	 * @param node
	 *            the node
	 */
	private void drawLeafNode(final WB_AABBNode node) {
		if (node.isLeaf()) {
			drawAABB(node.getAABB());
		}
		else {
			if (node.getPosChild() != null) {
				drawLeafNode(node.getPosChild());
			}
			if (node.getNegChild() != null) {
				drawLeafNode(node.getNegChild());
			}
			if (node.getMidChild() != null) {
				drawLeafNode(node.getMidChild());
			}
		}
	}

	/**
	 * Draw leafs.
	 *
	 * @param tree
	 *            the tree
	 */
	public void drawLeafs(final WB_AABBTree tree) {
		drawLeafNode(tree.getRoot());

	}

	/**
	 * Draw node.
	 *
	 * @param node
	 *            the node
	 */
	public void drawNode(final WB_AABBNode node) {
		drawAABB(node.getAABB());

		if (node.getPosChild() != null) {
			drawNode(node.getPosChild());
		}
		if (node.getNegChild() != null) {
			drawNode(node.getNegChild());
		}
		if (node.getMidChild() != null) {
			drawNode(node.getMidChild());
		}
	}

	/**
	 * Draw node.
	 *
	 * @param node
	 *            the node
	 * @param level
	 *            the level
	 */
	private void drawNode(final WB_AABBNode node, final int level) {
		if (node.getLevel() == level) {
			drawAABB(node.getAABB());
		}
		if (node.getLevel() < level) {
			if (node.getPosChild() != null) {
				drawNode(node.getPosChild(), level);
			}
			if (node.getNegChild() != null) {
				drawNode(node.getNegChild(), level);
			}
			if (node.getMidChild() != null) {
				drawNode(node.getMidChild(), level);
			}
		}
	}

	/**
	 * Draw nodes.
	 *
	 * @param frame
	 *            the frame
	 * @param s
	 *            the s
	 */
	public void drawNodes(final WB_Frame frame, final double s) {
		final ArrayList<WB_FrameNode> nodes = frame.getNodes();
		for (int i = 0; i < frame.getNumberOfNodes(); i++) {
			drawFrameNode(nodes.get(i), s);
		}
	}

	public void drawPoints(final Collection<? extends WB_Coordinate> points,
			final double d) {
		for (final WB_Coordinate v : points) {
			drawPoint(v, d);
		}
	}

	public void drawPoints(final WB_Coordinate[] points, final double d) {
		for (final WB_Coordinate v : points) {
			home.pushMatrix();
			home.translate((v.xf()), (v.yf()), (v.zf()));
			home.box((float) d);
			home.popMatrix();
		}
	}

	public void drawPolygon(final Collection<? extends SimplePolygon> polygons) {
		final Iterator<? extends SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon(polyItr.next());
		}

	}

	public void drawPolygon(final SimplePolygon polygon) {
		WB_Point v1;
		final int n = polygon.getN();
		home.beginShape(PConstants.POLYGON);
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			home.vertex(v1.xf(), v1.yf(), v1.zf());

		}
		home.endShape(PConstants.CLOSE);
	}

	public void drawPolygon2D(final Collection<SimplePolygon> polygons) {
		final Iterator<SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2D(polyItr.next());
		}

	}

	public void drawPolygon2D(final SimplePolygon polygon) {
		WB_Point v1;
		final int n = polygon.getN();
		home.beginShape(PConstants.POLYGON);
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			home.vertex(v1.xf(), v1.yf());

		}
		home.endShape(PConstants.CLOSE);
	}

	public void drawPolygon2DEdges(
			final Collection<? extends SimplePolygon> polygons) {
		final Iterator<? extends SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2DEdges(polyItr.next());
		}

	}

	public void drawPolygon2DEdges(final SimplePolygon polygon) {
		WB_Point v1, v2;
		final int n = polygon.getN();
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			v1 = polygon.getPoint(i);
			v2 = polygon.getPoint(j);
			home.line(v1.xf(), v1.yf(), v2.xf(), v2.yf());
		}
	}

	public void drawPolygon2DVertices(final Collection<SimplePolygon> polygons,
			final double d) {
		final Iterator<SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2DVertices(polyItr.next(), d);
		}

	}

	public void drawPolygon2DVertices(final SimplePolygon polygon,
			final double d) {
		WB_Point v1;
		final int n = polygon.getN();
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);

			home.ellipse(v1.xf(), v1.yf(), (float) d, (float) d);

		}
	}

	public void drawPolygonEdges(
			final Collection<? extends SimplePolygon> polygons) {
		final Iterator<? extends SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygonEdges(polyItr.next());
		}

	}

	public void drawPolygonEdges(final SimplePolygon polygon) {
		WB_Point v1, v2;
		final int n = polygon.getN();
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			v1 = polygon.getPoint(i);
			v2 = polygon.getPoint(j);
			home.line(v1.xf(), v1.yf(), v1.zf(), v2.xf(), v2.yf(), v2.zf());
		}
	}

	public void drawPolygonVertices(final Collection<SimplePolygon> polygons,
			final double d) {
		final Iterator<SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygonVertices(polyItr.next(), d);
		}

	}

	public void drawPolygonVertices(final SimplePolygon polygon, final double d) {
		WB_Point v1;
		final int n = polygon.getN();
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			home.pushMatrix();
			home.translate(v1.xf(), v1.yf(), v1.zf());
			home.box((float) d);
			home.popMatrix();
		}
	}

	public void drawPolylineEdges(final Collection<WB_PolyLine> polylines) {
		final Iterator<WB_PolyLine> polyItr = polylines.iterator();
		while (polyItr.hasNext()) {
			drawPolylineEdges(polyItr.next());
		}

	}

	public void drawPolylineEdges(final WB_PolyLine P) {
		for (int i = 0; i < P.getNumberOfPoints() - 1; i++) {

			home.line((P.getPoint(i).xf()), (P.getPoint(i).yf()),
					(P.getPoint(i).zf()), (P.getPoint(i + 1).xf()),
					(P.getPoint(i + 1).yf()), (P.getPoint(i + 1).zf()));
		}
	}

	public void drawPolylineVertices(final Collection<WB_PolyLine> polylines,
			final double d) {
		final Iterator<WB_PolyLine> polyItr = polylines.iterator();
		while (polyItr.hasNext()) {
			drawPolylineVertices(polyItr.next(), d);
		}

	}

	public void drawPolylineVertices(final WB_PolyLine P, final double d) {
		WB_IndexedPoint v1;
		for (int i = 0; i < P.getNumberOfPoints(); i++) {
			v1 = P.getPoint(i);
			home.pushMatrix();
			home.translate(v1.xf(), v1.yf(), v1.zf());
			home.box((float) d);
			home.popMatrix();

		}

	}

	public void drawSegment(final Collection<? extends Segment> segments) {
		final Iterator<? extends Segment> segItr = segments.iterator();
		while (segItr.hasNext()) {
			drawSegment(segItr.next());
		}

	}

	public void drawSegment(final Segment segment) {

		home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
				.getOrigin().zf(), segment.getEndpoint().xf(), segment
				.getEndpoint().yf(), segment.getEndpoint().zf());

	}

	public void drawSegment2D(final Collection<? extends Segment> segments) {
		final Iterator<? extends Segment> segItr = segments.iterator();
		while (segItr.hasNext()) {
			drawSegment2D(segItr.next());
		}

	}

	public void drawSegment2D(final Segment segment) {

		home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
				.getEndpoint().xf(), segment.getEndpoint().yf());

	}

	public void drawSegment2D(final Segment[] segments) {
		for (final Segment segment : segments) {
			drawSegment2D(segment);
		}

	}

	public void drawTriangle(final Collection<? extends Triangle> triangles) {

		final Iterator<? extends Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle(triItr.next());
		}

	}

	public void drawTriangle(final Triangle triangle) {
		home.beginShape();
		home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
		home.vertex(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());
		home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
		home.endShape();
	}

	public void drawTriangle2D(final Collection<? extends Triangle> triangles) {

		final Iterator<? extends Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle2D(triItr.next());
		}

	}

	public void drawTriangle2D(final Triangle triangle) {
		home.beginShape();
		home.vertex(triangle.p1().xf(), triangle.p1().yf());
		home.vertex(triangle.p2().xf(), triangle.p2().yf());
		home.vertex(triangle.p3().xf(), triangle.p3().yf());
		home.endShape();
	}

	public void drawTriangle2D(final Triangle[] triangles) {

		for (final Triangle triangle : triangles) {
			drawTriangle2D(triangle);
		}

	}

	public void drawTriangle2DEdges(
			final Collection<? extends Triangle> triangles) {
		final Iterator<? extends Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle2DEdges(triItr.next());
		}

	}

	public void drawTriangle2DEdges(final Triangle triangle) {

		home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p2().xf(),
				triangle.p2().yf());
		home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p2().xf(),
				triangle.p2().yf());
		home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p3().xf(),
				triangle.p3().yf());

	}

	public void drawTriangle2DEdges(final Triangle[] triangles) {

		for (final Triangle triangle : triangles) {
			drawTriangle2DEdges(triangle);
		}

	}

	public void drawTriangleEdges(final Collection<? extends Triangle> triangles) {
		final Iterator<? extends Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangleEdges(triItr.next());
		}

	}

	public void drawTriangleEdges(final Triangle triangle) {

		home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
				triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
		home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf(),
				triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
		home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
				triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());

	}

	public void drawVertex(final Long key, final double d,
			final HE_MeshStructure mesh) {
		final HE_Vertex v = mesh.getVertexByKey(key);
		if (v != null) {
			home.pushMatrix();
			home.translate((v.xf()), (v.yf()), (v.zf()));
			home.box((float) d);
			home.popMatrix();
		}
	}

	public void drawVertices(final double d, final HE_MeshStructure mesh) {
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			home.pushMatrix();
			home.translate(v.xf(), v.yf(), v.zf());
			home.box((float) d);
			home.popMatrix();

		}
	}

	// -----------------------------------------------------------------------Ray
	// Picker
	// Code written by Alberto Massa
	// Adapted by Frederik Vanhoutte

	// -----------------------------------------------------------------------
	// Comparator
	class EyeProximityComparator implements Comparator<HE_Face> {
		@Override
		public int compare(final HE_Face o1, final HE_Face o2) {
			final WB_Segment s1 = new WB_Segment(unproject.ptStartPos,
					o1.getFaceCenter());
			final WB_Segment s2 = new WB_Segment(unproject.ptStartPos,
					o2.getFaceCenter());
			final double l1 = s1.getLength();
			final double l2 = s2.getLength();
			if (l1 < l2) {
				return -1;
			}
			if (l1 > l2) {
				return 1;
			}
			return 0;
		}
	}

	private final Unproject unproject = new Unproject();

	public WB_Ray getPickingRay(final int x, final int y) {
		unproject.captureViewMatrix(home);
		unproject.calculatePickPoints(x, y, home.height);
		return new WB_Ray(unproject.ptStartPos, unproject.ptEndPos);
	}

	public HE_Face pickClosestFace(final HE_Mesh mesh, final int x, final int y) {
		final WB_Ray mouseRay3d = getPickingRay(x, y);

		final HE_FaceIntersection p = HE_Intersection.getClosestIntersection(
				mesh, mouseRay3d);
		return (p == null) ? null : p.face;
	}

	public HE_Face pickFurthestFace(final HE_Mesh mesh, final int x, final int y) {
		final WB_Ray mouseRay3d = getPickingRay(x, y);

		final HE_FaceIntersection p = HE_Intersection.getFurthestIntersection(
				mesh, mouseRay3d);
		return (p == null) ? null : p.face;
	}

	public HE_Face pickClosestFace(final WB_AABBTree meshtree, final int x,
			final int y) {
		final WB_Ray mouseRay3d = getPickingRay(x, y);

		final HE_FaceIntersection p = HE_Intersection.getClosestIntersection(
				meshtree, mouseRay3d);
		return (p == null) ? null : p.face;
	}

	public HE_Face pickFurthestFace(final WB_AABBTree meshtree, final int x,
			final int y) {
		final WB_Ray mouseRay3d = getPickingRay(x, y);

		final HE_FaceIntersection p = HE_Intersection.getFurthestIntersection(
				meshtree, mouseRay3d);
		return (p == null) ? null : p.face;
	}

	public List<HE_Face> pickFaces(final HE_Mesh mesh, final int x, final int y) {
		final WB_Ray mouseRay3d = getPickingRay(x, y);

		final List<HE_FaceIntersection> p = HE_Intersection.getIntersection(
				mesh, mouseRay3d);
		final List<HE_Face> result = new ArrayList<HE_Face>();
		for (final HE_FaceIntersection fi : p) {
			result.add(fi.face);
		}
		return result;
	}

	public List<HE_Face> pickFaces(final WB_AABBTree meshtree, final int x,
			final int y) {
		final WB_Ray mouseRay3d = getPickingRay(x, y);
		final List<HE_FaceIntersection> p = HE_Intersection.getIntersection(
				meshtree, mouseRay3d);
		final List<HE_Face> result = new ArrayList<HE_Face>();
		for (final HE_FaceIntersection fi : p) {
			result.add(fi.face);
		}
		return result;
	}

	// -----------------------------------------------------------------------
	// Unproject
	private class Unproject {

		private boolean m_bValid = false;
		private final PMatrix3D m_pMatrix = new PMatrix3D();

		private final int[] m_aiViewport = new int[4];

		// Store the near and far ray positions.
		public WB_Point ptStartPos = new WB_Point();
		public WB_Point ptEndPos = new WB_Point();

		public boolean calculatePickPoints(final int x, final int y,
				final int height) {
			// Calculate positions on the near and far 3D
			// frustum planes.
			m_bValid = true; // Have to do both in order to reset PVector on
			// error.
			if (!gluUnProject(x, height - y, 0.0, ptStartPos)) {
				m_bValid = false;
			}
			if (!gluUnProject(x, height - y, 1.0, ptEndPos)) {
				m_bValid = false;
			}
			return m_bValid;
		}

		public void captureViewMatrix(final PGraphics3D g3d) {
			// Call this to capture the selection matrix after
			// you have called perspective() or ortho() and applied
			// your pan, zoom and camera angles - but before
			// you start drawing or playing with the
			// matrices any further.

			if (g3d != null) { // Check for a valid 3D canvas.

				// Capture current projection matrix.
				m_pMatrix.set(g3d.projection);

				// Multiply by current modelview matrix.
				m_pMatrix.apply(g3d.modelview);

				// Invert the resultant matrix.
				m_pMatrix.invert();

				// Store the viewport.
				m_aiViewport[0] = 0;
				m_aiViewport[1] = 0;
				m_aiViewport[2] = g3d.width;
				m_aiViewport[3] = g3d.height;
			}

		}

		// Maintain own projection matrix.
		public PMatrix3D getMatrix() {
			return m_pMatrix;
		}

		// -------------------------

		// Maintain own viewport data.
		public int[] getViewport() {
			return m_aiViewport;
		}

		// -------------------------

		public boolean gluUnProject(final double winx, final double winy,
				final double winz, final WB_Point result) {

			final double[] in = new double[4];
			final double[] out = new double[4];

			// Transform to normalized screen coordinates (-1 to 1).
			in[0] = ((winx - m_aiViewport[0]) / m_aiViewport[2]) * 2.0 - 1.0;
			in[1] = ((winy - m_aiViewport[1]) / m_aiViewport[3]) * 2.0 - 1.0;
			in[2] = ((winz > 1) ? 1.0 : ((winz < 0) ? 0.0 : winz)) * 2.0 - 1.0;
			in[3] = 1.0;

			// Calculate homogeneous coordinates.
			out[0] = m_pMatrix.m00 * in[0] + m_pMatrix.m01 * in[1]
					+ m_pMatrix.m02 * in[2] + m_pMatrix.m03 * in[3];
			out[1] = m_pMatrix.m10 * in[0] + m_pMatrix.m11 * in[1]
					+ m_pMatrix.m12 * in[2] + m_pMatrix.m13 * in[3];
			out[2] = m_pMatrix.m20 * in[0] + m_pMatrix.m21 * in[1]
					+ m_pMatrix.m22 * in[2] + m_pMatrix.m23 * in[3];
			out[3] = m_pMatrix.m30 * in[0] + m_pMatrix.m31 * in[1]
					+ m_pMatrix.m32 * in[2] + m_pMatrix.m33 * in[3];

			if (out[3] == 0.0) { // Check for an invalid result.
				result._set(0, 0, 0);
				return false;
			}

			// Scale to world coordinates.
			out[3] = 1.0 / out[3];
			result._set(out[0] * out[3], out[1] * out[3], out[2] * out[3]);
			return true;

		}

		// True if near and far points calculated.
		public boolean isValid() {
			return m_bValid;
		}
	}

}
