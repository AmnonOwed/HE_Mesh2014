/*
 *
 */
package wblut.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.opengl.PGraphics3D;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.WB_Circle;
import wblut.geom.WB_ClassificationConvex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateOp;
import wblut.geom.WB_CoordinateSequence;
import wblut.geom.WB_Curve;
import wblut.geom.WB_FaceListMesh;
import wblut.geom.WB_Frame;
import wblut.geom.WB_FrameNode;
import wblut.geom.WB_FrameStrut;
import wblut.geom.WB_Geometry;
import wblut.geom.WB_GeometryCollection;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Line;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Ring;
import wblut.geom.WB_Segment;
import wblut.geom.WB_SequencePoint;
import wblut.geom.WB_Transform;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Triangulation2D;
import wblut.geom.WB_Vector;
import wblut.hemesh.HE_EdgeIterator;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_FaceEdgeCirculator;
import wblut.hemesh.HE_FaceHalfedgeInnerCirculator;
import wblut.hemesh.HE_FaceIntersection;
import wblut.hemesh.HE_FaceIterator;
import wblut.hemesh.HE_FaceVertexCirculator;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Intersection;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Path;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_TextureCoordinate;
import wblut.hemesh.HE_Vertex;
import wblut.hemesh.HE_VertexIterator;

/**
 *
 */
public class WB_Render3D {
    /**
     *
     */
    private final PGraphics3D home;
    /**
     *
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     *
     *
     * @param home
     */
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

    /**
     *
     *
     * @param home
     */
    public WB_Render3D(final PGraphics3D home) {
	this.home = home;
    }

    /**
     *
     *
     * @param p
     */
    public void drawPoint(final WB_Coordinate p) {
	home.point(p.xf(), p.yf(), p.zf());
    }

    /**
     *
     *
     * @param p
     * @param r
     */
    public void drawPoint(final WB_Coordinate p, final double r) {
	home.pushMatrix();
	home.translate(p.xf(), p.yf(), p.zf());
	home.box((float) r);
	home.popMatrix();
    }

    /**
     *
     *
     * @param p
     * @param v
     * @param r
     */
    public void drawVector(final WB_Coordinate p, final WB_Coordinate v,
	    final double r) {
	home.pushMatrix();
	home.translate(p.xf(), p.yf(), p.zf());
	home.line(0f, 0f, 0f, (float) (r * v.xd()), (float) (r * v.yd()),
		(float) (r * v.zd()));
	home.popMatrix();
    }

    /**
     *
     *
     * @param L
     * @param d
     */
    public void drawLine(final WB_Line L, final double d) {
	home.line((float) (L.getOrigin().xd() - (d * L.getDirection().xd())),
		(float) (L.getOrigin().yd() - (d * L.getDirection().yd())),
		(float) (L.getOrigin().zd() - (d * L.getDirection().zd())),
		(float) (L.getOrigin().xd() + (d * L.getDirection().xd())),
		(float) (L.getOrigin().yd() + (d * L.getDirection().yd())),
		(float) (L.getOrigin().zd() + (d * L.getDirection().zd())));
    }

    /**
     *
     *
     * @param R
     * @param d
     */
    public void drawRay(final WB_Ray R, final double d) {
	home.line((float) (R.getOrigin().xd()), (float) (R.getOrigin().yd()),
		(float) (R.getOrigin().zd()),
		(float) (R.getOrigin().xd() + (d * R.getDirection().xd())),
		(float) (R.getOrigin().yd() + (d * R.getDirection().yd())),
		(float) (R.getOrigin().zd() + (d * R.getDirection().zd())));
    }

    /**
     *
     *
     * @param S
     */
    public void drawSegment(final WB_Segment S) {
	home.line((float) (S.getOrigin().xd()), (float) (S.getOrigin().yd()),
		(float) (S.getOrigin().zd()), (float) (S.getEndpoint().xd()),
		(float) (S.getEndpoint().yd()), (float) (S.getEndpoint().zd()));
    }

    /**
     *
     *
     * @param p
     * @param q
     */
    public void drawSegment(final WB_Coordinate p, final WB_Coordinate q) {
	home.line((float) (p.xd()), (float) (p.yd()), (float) (p.zd()),
		(float) (q.xd()), (float) (q.yd()), (float) (q.zd()));
    }

    /**
     *
     *
     * @param P
     */
    public void drawPolyLine(final WB_PolyLine P) {
	for (int i = 0; i < (P.getNumberOfPoints() - 1); i++) {
	    home.line((float) (P.getPoint(i).xd()),
		    (float) (P.getPoint(i).yd()), (float) (P.getPoint(i).zd()),
		    (float) (P.getPoint(i + 1).xd()),
		    (float) (P.getPoint(i + 1).yd()),
		    (float) (P.getPoint(i + 1).zd()));
	}
    }

    /**
     *
     *
     * @param P
     */
    public void drawRing(final WB_Ring P) {
	for (int i = 0, j = P.getNumberOfPoints() - 1; i < P
		.getNumberOfPoints(); j = i++) {
	    home.line((float) (P.getPoint(j).xd()),
		    (float) (P.getPoint(j).yd()), (float) (P.getPoint(j).zd()),
		    (float) (P.getPoint(i).xd()), (float) (P.getPoint(i).yd()),
		    (float) (P.getPoint(i).zd()));
	}
    }

    /**
     *
     *
     * @param P
     */
    public void drawSimplePolygon(final WB_Polygon P) {
	{
	    home.beginShape(PConstants.POLYGON);
	    for (int i = 0; i < P.getNumberOfPoints(); i++) {
		vertex(P.getPoint(i));
	    }
	}
	home.endShape();
    }

    /**
     *
     *
     * @param P
     */
    public void drawPolygon(final WB_Polygon P) {
	final int[][] tris = P.getTriangles();
	for (final int[] tri : tris) {
	    drawTriangle(P.getPoint(tri[0]), P.getPoint(tri[1]),
		    P.getPoint(tri[2]));
	}
    }

    /**
     *
     *
     * @param P
     */
    public void drawPolygonEdges(final WB_Polygon P) {
	final int[] npc = P.getNumberOfPointsPerContour();
	int index = 0;
	for (int i = 0; i < P.getNumberOfContours(); i++) {
	    home.beginShape();
	    for (int j = 0; j < npc[i]; j++) {
		vertex(P.getPoint(index++));
	    }
	    home.endShape(PConstants.CLOSE);
	}
    }

    /**
     *
     *
     * @param C
     */
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

    /**
     *
     *
     * @param p1
     * @param p2
     * @param p3
     */
    public void drawTriangle(final WB_Coordinate p1, final WB_Coordinate p2,
	    final WB_Coordinate p3) {
	home.beginShape(PConstants.TRIANGLE);
	vertex(p1);
	vertex(p2);
	vertex(p3);
	home.endShape();
    }

    /**
     *
     *
     * @param tri
     * @param points
     */
    public void drawTriangle(final int[] tri,
	    final List<? extends WB_Coordinate> points) {
	home.beginShape(PConstants.TRIANGLE);
	vertex(points.get(tri[0]));
	vertex(points.get(tri[1]));
	vertex(points.get(tri[2]));
	home.endShape();
    }

    /**
     *
     *
     * @param tri
     * @param points
     */
    public void drawTriangle(final int[] tri, final WB_Coordinate[] points) {
	home.beginShape(PConstants.TRIANGLE);
	vertex(points[tri[0]]);
	vertex(points[tri[1]]);
	vertex(points[tri[2]]);
	home.endShape();
    }

    /**
     *
     *
     * @param tri
     * @param points
     */
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

    /**
     *
     *
     * @param tri
     * @param points
     */
    public void drawTriangulationEdges(final WB_Triangulation2D tri,
	    final List<? extends WB_Coordinate> points) {
	final int[][] edges = tri.getEdges();
	for (final int[] edge : edges) {
	    drawSegment(points.get(edge[0]), points.get(edge[1]));
	}
    }

    /**
     *
     *
     * @param P
     * @param d
     */
    public void drawPlane(final WB_Plane P, final double d) {
	home.beginShape(PConstants.QUAD);
	home.vertex((float) (P.getOrigin().xd() - (d * P.getU().xd()) - (d * P
		.getV().xd())), (float) (P.getOrigin().yd()
			- (d * P.getU().yd()) - (d * P.getV().yd())), (float) (P
				.getOrigin().zd() - (d * P.getU().zd()) - (d * P.getV().zd())));
	home.vertex(
		(float) ((P.getOrigin().xd() - (d * P.getU().xd())) + (d * P
			.getV().xd())), (float) ((P.getOrigin().yd() - (d * P
				.getU().yd())) + (d * P.getV().yd())), (float) ((P
					.getOrigin().zd() - (d * P.getU().zd())) + (d * P
						.getV().zd())));
	home.vertex((float) (P.getOrigin().xd() + (d * P.getU().xd()) + (d * P
		.getV().xd())), (float) (P.getOrigin().yd()
			+ (d * P.getU().yd()) + (d * P.getV().yd())), (float) (P
				.getOrigin().zd() + (d * P.getU().zd()) + (d * P.getV().zd())));
	home.vertex(
		(float) ((P.getOrigin().xd() + (d * P.getU().xd())) - (d * P
			.getV().xd())), (float) ((P.getOrigin().yd() + (d * P
				.getU().yd())) - (d * P.getV().yd())), (float) ((P
					.getOrigin().zd() + (d * P.getU().zd())) - (d * P
						.getV().zd())));
	home.endShape();
    }

    /**
     *
     *
     * @param p
     */
    private void vertex(final WB_Coordinate p) {
	home.vertex(p.xf(), p.yf(), p.zf());
    }

    /**
     *
     *
     * @param geometry
     * @param f
     */
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
	} else if (geometry instanceof WB_FaceListMesh) {
	    if (f.length == 0) {
		drawMesh((WB_FaceListMesh) geometry);
	    }
	}
    }

    /**
     *
     *
     * @param geometry
     * @param f
     */
    public void draw(final Collection<? extends WB_Geometry> geometry,
	    final double... f) {
	for (final WB_Geometry geo : geometry) {
	    draw(geo, f);
	}
    }

    /**
     *
     *
     * @param geometry
     * @param f
     */
    public void draw(final WB_Geometry[] geometry, final double... f) {
	for (final WB_Geometry geo : geometry) {
	    draw(geo, f);
	}
    }

    /**
     *
     *
     * @param indices
     * @param points
     */
    private void drawPolygon(final int[] indices,
	    final WB_CoordinateSequence points) {
	if ((points != null) && (indices != null)) {
	    home.beginShape(PConstants.POLYGON);
	    for (final int indice : indices) {
		home.vertex((float) points.get(indice, 0),
			(float) points.get(indice, 1),
			(float) points.get(indice, 2));
	    }
	    home.endShape(PConstants.CLOSE);
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawMesh(final WB_Mesh mesh) {
	if (mesh == null) {
	    return;
	}
	for (final int[] face : mesh.getFacesAsInt()) {
	    drawPolygon(face, mesh.getPoints());
	}
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toSmoothPShape(final WB_FaceListMesh mesh) {
	final PShape retained = home.createShape();
	retained.beginShape(PConstants.TRIANGLES);
	final WB_FaceListMesh lmesh = geometryfactory.createTriMesh(mesh);
	final WB_Vector v = geometryfactory.createVector();
	final WB_CoordinateSequence seq = lmesh.getPoints();
	WB_SequencePoint p = seq.getPoint(0);
	for (int i = 0; i < lmesh.getNumberOfFaces(); i++) {
	    int id = lmesh.getFace(i)[0];
	    v.set(lmesh.getVertexNormal(id));
	    retained.normal(v.xf(), v.yf(), v.zf());
	    p = seq.getPoint(id);
	    retained.vertex(p.xf(), p.yf(), p.zf());
	    id = lmesh.getFace(i)[1];
	    v.set(lmesh.getVertexNormal(id));
	    retained.normal(v.xf(), v.yf(), v.zf());
	    p = seq.getPoint(id);
	    retained.vertex(p.xf(), p.yf(), p.zf());
	    id = lmesh.getFace(i)[2];
	    v.set(lmesh.getVertexNormal(id));
	    retained.normal(v.xf(), v.yf(), v.zf());
	    p = seq.getPoint(id);
	    retained.vertex(p.xf(), p.yf(), p.zf());
	}
	retained.endShape();
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toFacetedPShape(final WB_FaceListMesh mesh) {
	final PShape retained = home.createShape();
	retained.beginShape(PConstants.TRIANGLES);
	final WB_FaceListMesh lmesh = geometryfactory.createTriMesh(mesh);
	final WB_CoordinateSequence seq = lmesh.getPoints();
	WB_SequencePoint p = seq.getPoint(0);
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
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
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
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toSmoothPShapeWithFaceColor(final HE_Mesh mesh) {
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
	    retained.fill(f.getColor());
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
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toSmoothPShapeWithVertexColor(final HE_Mesh mesh) {
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
		retained.fill(v.getColor());
		n = v.getVertexNormal();
		retained.normal(n.xf(), n.yf(), n.zf());
		retained.vertex(v.xf(), v.yf(), v.zf());
		he = he.getNextInFace();
	    } while (he != f.getHalfedge());
	}
	retained.endShape();
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
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
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toFacetedPShapeWithFaceColor(final HE_Mesh mesh) {
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
	    retained.fill(f.getColor());
	    do {
		v = he.getVertex();
		retained.vertex(v.xf(), v.yf(), v.zf());
		he = he.getNextInFace();
	    } while (he != f.getHalfedge());
	}
	retained.endShape();
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toFacetedPShapeWithVertexColor(final HE_Mesh mesh) {
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
		retained.fill(v.getColor());
		retained.vertex(v.xf(), v.yf(), v.zf());
		he = he.getNextInFace();
	    } while (he != f.getHalfedge());
	}
	retained.endShape();
	return retained;
    }

    /**
     *
     *
     * @param mesh
     * @return
     */
    public PShape toWireframePShape(final HE_MeshStructure mesh) {
	// tracker.setDefaultStatus("Creating PShape.");
	final PShape retained = home.createShape();
	if (mesh instanceof HE_Selection) {
	    ((HE_Selection) mesh).collectEdgesByFace();
	}
	// tracker.setDefaultStatus("Writing Edges.", mesh.getNumberOfEdges());
	final HE_EdgeIterator eItr = mesh.eItr();
	HE_Halfedge e;
	HE_Vertex v;
	retained.beginShape(PConstants.LINES);
	while (eItr.hasNext()) {
	    e = eItr.next();
	    v = e.getVertex();
	    retained.vertex(v.xf(), v.yf(), v.zf());
	    v = e.getEndVertex();
	    retained.vertex(v.xf(), v.yf(), v.zf());
	    // tracker.incrementCounter();
	}
	retained.endShape();
	// tracker.setDefaultStatus("Pshape created.");
	return retained;
    }

    /**
     *
     *
     * @param p0
     * @param p1
     * @param p2
     * @param p3
     */
    public void drawTetrahedron(final WB_Coordinate p0, final WB_Coordinate p1,
	    final WB_Coordinate p2, final WB_Coordinate p3) {
	home.beginShape(PConstants.TRIANGLES);
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

    /**
     *
     *
     * @param indices
     * @param points
     */
    public void drawTetrahedron(final int[] indices,
	    final List<? extends WB_Coordinate> points) {
	if ((points != null) && (indices != null)) {
	    drawTetrahedron(points.get(indices[0]), points.get(indices[1]),
		    points.get(indices[2]), points.get(indices[3]));
	}
    }

    /**
     *
     *
     * @param indices
     * @param points
     */
    public void drawTetrahedra(final int[] indices,
	    final List<? extends WB_Coordinate> points) {
	if ((points != null) && (indices != null)) {
	    for (int i = 0; i < indices.length; i += 4) {
		drawTetrahedron(points.get(indices[i]),
			points.get(indices[i + 1]), points.get(indices[i + 2]),
			points.get(indices[i + 3]));
	    }
	}
    }

    /**
     *
     *
     * @param indices
     * @param points
     */
    public void drawTetrahedron(final int[] indices,
	    final WB_Coordinate[] points) {
	if ((points != null) && (indices != null)) {
	    drawTetrahedron(points[indices[0]], points[indices[1]],
		    points[indices[2]], points[indices[3]]);
	}
    }

    /**
     *
     *
     * @param indices
     * @param points
     */
    public void drawTetrahedra(final int[] indices, final WB_Coordinate[] points) {
	if ((points != null) && (indices != null)) {
	    for (int i = 0; i < indices.length; i += 4) {
		drawTetrahedron(points[indices[i]], points[indices[i + 1]],
			points[indices[i + 2]], points[indices[i + 3]]);
	    }
	}
    }

    /**
     *
     *
     * @param AABB
     */
    public void drawAABB(final WB_AABB AABB) {
	home.pushMatrix();
	home.translate(AABB.getCenter().xf(), AABB.getCenter().yf(), AABB
		.getCenter().zf());
	home.box((float) AABB.getWidth(), (float) AABB.getHeight(),
		(float) AABB.getDepth());
	home.popMatrix();
    }

    /**
     *
     *
     * @param points
     * @param d
     */
    public void draw(final Collection<? extends WB_Coordinate> points,
	    final double d) {
	for (final WB_Coordinate point : points) {
	    home.pushMatrix();
	    home.translate(point.xf(), point.yf(), point.zf());
	    home.box((float) d);
	    home.popMatrix();
	}
    }

    /**
     *
     *
     * @param circles
     */
    public void draw(final Collection<WB_Circle> circles) {
	final Iterator<WB_Circle> citr = circles.iterator();
	while (citr.hasNext()) {
	    draw(citr.next());
	}
    }

    /**
     *
     *
     * @param curves
     * @param steps
     */
    public void draw(final Collection<WB_Curve> curves, final int steps) {
	final Iterator<WB_Curve> citr = curves.iterator();
	while (citr.hasNext()) {
	    drawCurve(citr.next(), steps);
	}
    }

    /**
     *
     *
     * @param tree
     */
    public void drawTree(final WB_AABBTree tree) {
	drawNode(tree.getRoot());
    }

    /**
     *
     *
     * @param tree
     * @param level
     */
    public void drawTree(final WB_AABBTree tree, final int level) {
	drawNode(tree.getRoot(), level);
    }

    /**
     *
     *
     * @param point
     * @param d
     */
    public void draw(final WB_Coordinate point, final double d) {
	home.pushMatrix();
	home.translate(point.xf(), point.yf(), point.zf());
	home.box((float) d);
	home.popMatrix();
    }

    /**
     *
     *
     * @param p
     * @param q
     */
    public void draw(final WB_Coordinate p, final WB_Coordinate q) {
	home.line(p.xf(), p.yf(), p.zf(), q.xf(), q.yf(), q.zf());
    }

    /**
     *
     *
     * @param p
     * @param v
     * @param d
     */
    public void draw(final WB_Coordinate p, final WB_Vector v, final double d) {
	home.line(p.xf(), p.yf(), p.zf(), p.xf() + ((float) d * v.xf()), p.yf()
		+ ((float) d * v.yf()), p.zf() + ((float) d * v.zf()));
    }

    /**
     *
     *
     * @param C
     * @param steps
     */
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

    /**
     *
     *
     * @param frame
     */
    public void drawFrame(final WB_Frame frame) {
	final ArrayList<WB_FrameStrut> struts = frame.getStruts();
	for (int i = 0; i < frame.getNumberOfStruts(); i++) {
	    drawFrameStrut(struts.get(i));
	}
    }

    /**
     *
     *
     * @param node
     * @param s
     */
    public void drawFrameNode(final WB_FrameNode node, final double s) {
	home.pushMatrix();
	home.translate(node.xf(), node.yf(), node.zf());
	home.box((float) s);
	home.popMatrix();
    }

    /**
     *
     *
     * @param strut
     */
    public void drawFrameStrut(final WB_FrameStrut strut) {
	home.line(strut.start().xf(), strut.start().yf(), strut.start().zf(),
		strut.end().xf(), strut.end().yf(), strut.end().zf());
    }

    /**
     *
     *
     * @param mesh
     */
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

    /**
     *
     *
     * @param key
     * @param mesh
     */
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
	final Iterator<HE_Face> fItr = selection.fItr();
	HE_Halfedge e;
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    e = f.getHalfedge();
	    do {
		if (e.isEdge() || e.isBoundary()
			|| !selection.contains(e.getPair().getFace())) {
		    home.line(e.getVertex().xf(), e.getVertex().yf(), e
			    .getVertex().zf(), e.getEndVertex().xf(), e
			    .getEndVertex().yf(), e.getEndVertex().zf());
		}
		e = e.getNextInFace();
	    } while (e != f.getHalfedge());
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

    /**
     *
     *
     * @param label
     * @param mesh
     */
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

    /**
     *
     *
     * @param f
     */
    public void drawFace(final HE_Face f) {
	drawFace(f, false);
    }

    public void drawFace(final HE_Face f, final PImage texture) {
	drawFace(f, texture, false);
    }

    public void drawFace(final HE_Face f, final PImage[] textures) {
	drawFace(f, textures, false);
    }

    /**
     *
     *
     * @param f
     * @param smooth
     */
    public void drawFace(final HE_Face f, final boolean smooth) {
	final int fo = f.getFaceOrder();
	final List<HE_Vertex> vertices = f.getFaceVertices();
	if ((fo < 3) || (vertices.size() < 3)) {
	} else if (fo == 3) {
	    final int[] tri = new int[] { 0, 1, 2 };
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
	    } else {
		home.beginShape(PConstants.TRIANGLES);
		v0 = vertices.get(tri[0]);
		v1 = vertices.get(tri[1]);
		v2 = vertices.get(tri[2]);
		home.vertex(v0.xf(), v0.yf(), v0.zf());
		home.vertex(v1.xf(), v1.yf(), v1.zf());
		home.vertex(v2.xf(), v2.yf(), v2.zf());
		home.endShape();
	    }
	} else {
	    final int[][] tris = f.getTriangles();
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
	    } else {
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

    public void drawFace(final HE_Face f, final PImage texture,
	    final boolean smooth) {
	final int fo = f.getFaceOrder();
	final List<HE_Vertex> vertices = f.getFaceVertices();
	if ((fo < 3) || (vertices.size() < 3)) {
	} else if (fo == 3) {
	    final int[] tri = new int[] { 0, 1, 2 };
	    HE_Vertex v0, v1, v2;
	    WB_Vector n0, n1, n2;
	    if (smooth) {
		home.beginShape(PConstants.TRIANGLES);
		home.texture(texture);
		v0 = vertices.get(tri[0]);
		n0 = v0.getVertexNormal();
		v1 = vertices.get(tri[1]);
		n1 = v1.getVertexNormal();
		v2 = vertices.get(tri[2]);
		n2 = v2.getVertexNormal();
		home.normal(n0.xf(), n0.yf(), n0.zf());
		home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0
			.getUVW(f).vf());
		home.normal(n1.xf(), n1.yf(), n1.zf());
		home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1
			.getUVW(f).vf());
		home.normal(n2.xf(), n2.yf(), n2.zf());
		home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2
			.getUVW(f).vf());
		home.endShape();
	    } else {
		home.beginShape(PConstants.TRIANGLES);
		home.texture(texture);
		v0 = vertices.get(tri[0]);
		v1 = vertices.get(tri[1]);
		v2 = vertices.get(tri[2]);
		home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0
			.getUVW(f).vf());
		home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1
			.getUVW(f).vf());
		home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2
			.getUVW(f).vf());
		home.endShape();
	    }
	} else {
	    final int[][] tris = f.getTriangles();
	    HE_Vertex v0, v1, v2;
	    WB_Vector n0, n1, n2;
	    int[] tri;
	    if (smooth) {
		for (int i = 0; i < tris.length; i++) {
		    tri = tris[i];
		    home.beginShape(PConstants.TRIANGLES);
		    home.texture(texture);
		    v0 = vertices.get(tri[0]);
		    n0 = v0.getVertexNormal();
		    v1 = vertices.get(tri[1]);
		    n1 = v1.getVertexNormal();
		    v2 = vertices.get(tri[2]);
		    n2 = v2.getVertexNormal();
		    home.normal(n0.xf(), n0.yf(), n0.zf());
		    home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(),
			    v0.getUVW(f).vf());
		    home.normal(n1.xf(), n1.yf(), n1.zf());
		    home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(),
			    v1.getUVW(f).vf());
		    home.normal(n2.xf(), n2.yf(), n2.zf());
		    home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(),
			    v2.getUVW(f).vf());
		    home.endShape();
		}
	    } else {
		for (int i = 0; i < tris.length; i++) {
		    tri = tris[i];
		    home.beginShape(PConstants.TRIANGLES);
		    home.texture(texture);
		    v0 = vertices.get(tri[0]);
		    v1 = vertices.get(tri[1]);
		    v2 = vertices.get(tri[2]);
		    home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(),
			    v0.getUVW(f).vf());
		    home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(),
			    v1.getUVW(f).vf());
		    home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(),
			    v2.getUVW(f).vf());
		    home.endShape();
		}
	    }
	}
    }

    public void drawFace(final HE_Face f, final PImage[] textures,
	    final boolean smooth) {
	final int fo = f.getFaceOrder();
	final int fti = f.getTextureId();
	final List<HE_Vertex> vertices = f.getFaceVertices();
	if ((fo < 3) || (vertices.size() < 3)) {
	} else if (fo == 3) {
	    final int[] tri = new int[] { 0, 1, 2 };
	    HE_Vertex v0, v1, v2;
	    WB_Vector n0, n1, n2;
	    if (smooth) {
		home.beginShape(PConstants.TRIANGLES);
		home.texture(textures[fti]);
		v0 = vertices.get(tri[0]);
		n0 = v0.getVertexNormal();
		v1 = vertices.get(tri[1]);
		n1 = v1.getVertexNormal();
		v2 = vertices.get(tri[2]);
		n2 = v2.getVertexNormal();
		home.normal(n0.xf(), n0.yf(), n0.zf());
		home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0
			.getUVW(f).vf());
		home.normal(n1.xf(), n1.yf(), n1.zf());
		home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1
			.getUVW(f).vf());
		home.normal(n2.xf(), n2.yf(), n2.zf());
		home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2
			.getUVW(f).vf());
		home.endShape();
	    } else {
		home.beginShape(PConstants.TRIANGLES);
		home.texture(textures[fti]);
		v0 = vertices.get(tri[0]);
		v1 = vertices.get(tri[1]);
		v2 = vertices.get(tri[2]);
		home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(), v0
			.getUVW(f).vf());
		home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(), v1
			.getUVW(f).vf());
		home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(), v2
			.getUVW(f).vf());
		home.endShape();
	    }
	} else {
	    final int[][] tris = f.getTriangles();
	    HE_Vertex v0, v1, v2;
	    WB_Vector n0, n1, n2;
	    int[] tri;
	    if (smooth) {
		for (int i = 0; i < tris.length; i++) {
		    tri = tris[i];
		    home.beginShape(PConstants.TRIANGLES);
		    home.texture(textures[fti]);
		    v0 = vertices.get(tri[0]);
		    n0 = v0.getVertexNormal();
		    v1 = vertices.get(tri[1]);
		    n1 = v1.getVertexNormal();
		    v2 = vertices.get(tri[2]);
		    n2 = v2.getVertexNormal();
		    home.normal(n0.xf(), n0.yf(), n0.zf());
		    home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(),
			    v0.getUVW(f).vf());
		    home.normal(n1.xf(), n1.yf(), n1.zf());
		    home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(),
			    v1.getUVW(f).vf());
		    home.normal(n2.xf(), n2.yf(), n2.zf());
		    home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(),
			    v2.getUVW(f).vf());
		    home.endShape();
		}
	    } else {
		for (int i = 0; i < tris.length; i++) {
		    tri = tris[i];
		    home.beginShape(PConstants.TRIANGLES);
		    home.texture(textures[fti]);
		    v0 = vertices.get(tri[0]);
		    v1 = vertices.get(tri[1]);
		    v2 = vertices.get(tri[2]);
		    home.vertex(v0.xf(), v0.yf(), v0.zf(), v0.getUVW(f).uf(),
			    v0.getUVW(f).vf());
		    home.vertex(v1.xf(), v1.yf(), v1.zf(), v1.getUVW(f).uf(),
			    v1.getUVW(f).vf());
		    home.vertex(v2.xf(), v2.yf(), v2.zf(), v2.getUVW(f).uf(),
			    v2.getUVW(f).vf());
		    home.endShape();
		}
	    }
	}
    }

    /**
     *
     *
     * @param f
     * @param smooth
     */
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
	    } else {
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

    /**
     *
     *
     * @param f
     * @param smooth
     */
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
	    } else {
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

    /**
     *
     *
     * @param f
     * @param smooth
     */
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
	    } else {
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

    /**
     *
     *
     * @param key
     * @param smooth
     * @param mesh
     */
    public void drawFace(final Long key, final boolean smooth,
	    final HE_MeshStructure mesh) {
	final HE_Face f = mesh.getFaceByKey(key);
	if (f != null) {
	    drawFace(f, smooth);
	}
    }

    /**
     *
     *
     * @param key
     * @param mesh
     */
    public void drawFace(final Long key, final HE_MeshStructure mesh) {
	final HE_Face f = mesh.getFaceByKey(key);
	if (f != null) {
	    drawFace(f, false);
	}
    }

    /**
     *
     *
     * @param f
     */
    public void drawFaceFC(final HE_Face f) {
	drawFaceFC(f, false);
    }

    /**
     *
     *
     * @param f
     */
    public void drawFaceHC(final HE_Face f) {
	drawFaceHC(f, false);
    }

    /**
     *
     *
     * @param f
     */
    public void drawFaceVC(final HE_Face f) {
	drawFaceVC(f, false);
    }

    /**
     *
     *
     * @param meshes
     */
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

    public void drawFaces(final HE_MeshStructure mesh, final PImage texture) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFace(fItr.next(), texture);
	}
    }

    public void drawFaces(final HE_MeshStructure mesh, final PImage[] textures) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFace(fItr.next(), textures);
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

    /**
     *
     *
     * @param label
     * @param mesh
     */
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

    /**
     *
     *
     * @param mesh
     */
    public void drawFacesFC(final HE_MeshStructure mesh) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFaceFC(fItr.next());
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawFacesHC(final HE_MeshStructure mesh) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFaceHC(fItr.next());
	}
    }

    /**
     *
     *
     * @param mesh
     */
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

    /**
     *
     *
     * @param f
     */
    public void drawFaceSmooth(final HE_Face f) {
	new ArrayList<HE_Vertex>();
	drawFace(f, true);
    }

    /**
     *
     *
     * @param key
     * @param mesh
     */
    public void drawFaceSmoothFC(final Long key, final HE_MeshStructure mesh) {
	new ArrayList<HE_Vertex>();
	final HE_Face f = mesh.getFaceByKey(key);
	if (f != null) {
	    drawFaceFC(f, true);
	}
    }

    /**
     *
     *
     * @param f
     */
    public void drawFaceSmoothFC(final HE_Face f) {
	new ArrayList<HE_Vertex>();
	drawFaceFC(f, true);
    }

    /**
     *
     *
     * @param key
     * @param mesh
     */
    public void drawFaceSmoothVC(final Long key, final HE_MeshStructure mesh) {
	new ArrayList<HE_Vertex>();
	final HE_Face f = mesh.getFaceByKey(key);
	if (f != null) {
	    drawFaceVC(f, true);
	}
    }

    /**
     *
     *
     * @param f
     */
    public void drawFaceSmoothVC(final HE_Face f) {
	new ArrayList<HE_Vertex>();
	drawFaceVC(f, true);
    }

    /**
     *
     *
     * @param key
     * @param mesh
     */
    public void drawFaceSmoothHC(final Long key, final HE_MeshStructure mesh) {
	new ArrayList<HE_Vertex>();
	final HE_Face f = mesh.getFaceByKey(key);
	if (f != null) {
	    drawFaceHC(f, true);
	}
    }

    /**
     *
     *
     * @param f
     */
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

    public void drawFacesSmooth(final HE_MeshStructure mesh,
	    final PImage texture) {
	new ArrayList<HE_Vertex>();
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFace(fItr.next(), texture, true);
	}
    }

    public void drawFacesSmooth(final HE_MeshStructure mesh,
	    final PImage[] textures) {
	new ArrayList<HE_Vertex>();
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFace(fItr.next(), textures, true);
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawFacesSmoothFC(final HE_MeshStructure mesh) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFaceFC(fItr.next(), true);
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawFacesSmoothHC(final HE_MeshStructure mesh) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	while (fItr.hasNext()) {
	    drawFaceHC(fItr.next(), true);
	}
    }

    /**
     *
     *
     * @param mesh
     */
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
	} else {
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

    /**
     *
     *
     * @param points
     * @param d
     */
    public void drawPoints(final Collection<? extends WB_Coordinate> points,
	    final double d) {
	for (final WB_Coordinate v : points) {
	    drawPoint(v, d);
	}
    }

    /**
     *
     *
     * @param points
     * @param d
     */
    public void drawPoints(final WB_Coordinate[] points, final double d) {
	for (final WB_Coordinate v : points) {
	    home.pushMatrix();
	    home.translate((v.xf()), (v.yf()), (v.zf()));
	    home.box((float) d);
	    home.popMatrix();
	}
    }

    /**
     *
     *
     * @param polygons
     */
    public void drawPolygon(final Collection<? extends WB_Polygon> polygons) {
	final Iterator<? extends WB_Polygon> polyItr = polygons.iterator();
	while (polyItr.hasNext()) {
	    drawPolygon(polyItr.next());
	}
    }

    /**
     *
     *
     * @param polygons
     */
    public void drawPolygonEdges(final Collection<? extends WB_Polygon> polygons) {
	final Iterator<? extends WB_Polygon> polyItr = polygons.iterator();
	while (polyItr.hasNext()) {
	    drawPolygonEdges(polyItr.next());
	}
    }

    /**
     *
     *
     * @param polygons
     * @param d
     */
    public void drawPolygonVertices(final Collection<WB_Polygon> polygons,
	    final double d) {
	final Iterator<WB_Polygon> polyItr = polygons.iterator();
	while (polyItr.hasNext()) {
	    drawPolygonVertices(polyItr.next(), d);
	}
    }

    /**
     *
     *
     * @param polygon
     * @param d
     */
    public void drawPolygonVertices(final WB_Polygon polygon, final double d) {
	WB_Coordinate v1;
	final int n = polygon.getNumberOfPoints();
	for (int i = 0; i < n; i++) {
	    v1 = polygon.getPoint(i);
	    home.pushMatrix();
	    home.translate(v1.xf(), v1.yf(), v1.zf());
	    home.box((float) d);
	    home.popMatrix();
	}
    }

    /**
     *
     *
     * @param polylines
     */
    public void drawPolylineEdges(final Collection<WB_PolyLine> polylines) {
	final Iterator<WB_PolyLine> polyItr = polylines.iterator();
	while (polyItr.hasNext()) {
	    drawPolylineEdges(polyItr.next());
	}
    }

    /**
     *
     *
     * @param P
     */
    public void drawPolylineEdges(final WB_PolyLine P) {
	for (int i = 0; i < (P.getNumberOfPoints() - 1); i++) {
	    home.line((P.getPoint(i).xf()), (P.getPoint(i).yf()),
		    (P.getPoint(i).zf()), (P.getPoint(i + 1).xf()),
		    (P.getPoint(i + 1).yf()), (P.getPoint(i + 1).zf()));
	}
    }

    /**
     *
     *
     * @param polylines
     * @param d
     */
    public void drawPolylineVertices(final Collection<WB_PolyLine> polylines,
	    final double d) {
	final Iterator<WB_PolyLine> polyItr = polylines.iterator();
	while (polyItr.hasNext()) {
	    drawPolylineVertices(polyItr.next(), d);
	}
    }

    /**
     *
     *
     * @param P
     * @param d
     */
    public void drawPolylineVertices(final WB_PolyLine P, final double d) {
	WB_SequencePoint v1;
	for (int i = 0; i < P.getNumberOfPoints(); i++) {
	    v1 = P.getPoint(i);
	    home.pushMatrix();
	    home.translate(v1.xf(), v1.yf(), v1.zf());
	    home.box((float) d);
	    home.popMatrix();
	}
    }

    /**
     *
     *
     * @param segments
     */
    public void drawSegment(final Collection<? extends WB_Segment> segments) {
	final Iterator<? extends WB_Segment> segItr = segments.iterator();
	while (segItr.hasNext()) {
	    drawSegment(segItr.next());
	}
    }

    /**
     *
     *
     * @param path
     */
    public void drawPath(final HE_Path path) {
	home.beginShape();
	for (final HE_Vertex v : path.getPathVertices()) {
	    home.vertex(v.xf(), v.yf(), v.zf());
	}
	if (path.isLoop()) {
	    home.endShape(PConstants.CLOSE);
	} else {
	    home.endShape(PConstants.OPEN);
	}
    }

    /**
     *
     *
     * @param segments
     */
    public void drawSegment2D(final Collection<? extends WB_Segment> segments) {
	final Iterator<? extends WB_Segment> segItr = segments.iterator();
	while (segItr.hasNext()) {
	    drawSegment2D(segItr.next());
	}
    }

    /**
     *
     *
     * @param segment
     */
    public void drawSegment2D(final WB_Segment segment) {
	home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
		.getEndpoint().xf(), segment.getEndpoint().yf());
    }

    /**
     *
     *
     * @param segments
     */
    public void drawSegment2D(final WB_Segment[] segments) {
	for (final WB_Segment segment : segments) {
	    drawSegment2D(segment);
	}
    }

    /**
     *
     *
     * @param triangles
     */
    public void drawTriangle(final Collection<? extends WB_Triangle> triangles) {
	final Iterator<? extends WB_Triangle> triItr = triangles.iterator();
	while (triItr.hasNext()) {
	    drawTriangle(triItr.next());
	}
    }

    /**
     *
     *
     * @param triangle
     */
    public void drawTriangle(final WB_Triangle triangle) {
	home.beginShape();
	home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
	home.vertex(triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
	home.vertex(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());
	home.endShape(PConstants.CLOSE);
    }

    /**
     *
     *
     * @param triangles
     */
    public void drawTriangle2D(final Collection<? extends WB_Triangle> triangles) {
	final Iterator<? extends WB_Triangle> triItr = triangles.iterator();
	while (triItr.hasNext()) {
	    drawTriangle2D(triItr.next());
	}
    }

    /**
     *
     *
     * @param triangle
     */
    public void drawTriangle2D(final WB_Triangle triangle) {
	home.beginShape();
	home.vertex(triangle.p1().xf(), triangle.p1().yf());
	home.vertex(triangle.p2().xf(), triangle.p2().yf());
	home.vertex(triangle.p3().xf(), triangle.p3().yf());
	home.endShape();
    }

    /**
     *
     *
     * @param triangles
     */
    public void drawTriangle2D(final WB_Triangle[] triangles) {
	for (final WB_Triangle triangle : triangles) {
	    drawTriangle2D(triangle);
	}
    }

    /**
     *
     *
     * @param triangles
     */
    public void drawTriangle2DEdges(
	    final Collection<? extends WB_Triangle> triangles) {
	final Iterator<? extends WB_Triangle> triItr = triangles.iterator();
	while (triItr.hasNext()) {
	    drawTriangle2DEdges(triItr.next());
	}
    }

    /**
     *
     *
     * @param triangle
     */
    public void drawTriangle2DEdges(final WB_Triangle triangle) {
	home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p2().xf(),
		triangle.p2().yf());
	home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p2().xf(),
		triangle.p2().yf());
	home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p3().xf(),
		triangle.p3().yf());
    }

    /**
     *
     *
     * @param triangles
     */
    public void drawTriangle2DEdges(final WB_Triangle[] triangles) {
	for (final WB_Triangle triangle : triangles) {
	    drawTriangle2DEdges(triangle);
	}
    }

    /**
     *
     *
     * @param triangles
     */
    public void drawTriangleEdges(
	    final Collection<? extends WB_Triangle> triangles) {
	final Iterator<? extends WB_Triangle> triItr = triangles.iterator();
	while (triItr.hasNext()) {
	    drawTriangleEdges(triItr.next());
	}
    }

    /**
     *
     *
     * @param triangle
     */
    public void drawTriangleEdges(final WB_Triangle triangle) {
	home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
		triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
	home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf(),
		triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
	home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
		triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());
    }

    /**
     *
     *
     * @param key
     * @param d
     * @param mesh
     */
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

    /**
     *
     *
     * @param d
     * @param mesh
     */
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
    /**
     *
     */
    class EyeProximityComparator implements Comparator<HE_Face> {
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
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

    /**
     *
     */
    private final Unproject unproject = new Unproject();

    // -----------------------------------------------------------------------
    // Unproject
    /**
     *
     */
    private class Unproject {
	/**
	 *
	 */
	private boolean m_bValid = false;
	/**
	 *
	 */
	private final PMatrix3D m_pMatrix = new PMatrix3D();
	/**
	 *
	 */
	private final int[] m_aiViewport = new int[4];
	// Store the near and far ray positions.
	/**
	 *
	 */
	public WB_Point ptStartPos = new WB_Point();
	/**
	 *
	 */
	public WB_Point ptEndPos = new WB_Point();

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param height
	 * @return
	 */
	public boolean calculatePickPoints(final double x, final double y,
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

	/**
	 *
	 *
	 * @param g3d
	 */
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

	// -------------------------
	/**
	 *
	 *
	 * @param winx
	 * @param winy
	 * @param winz
	 * @param result
	 * @return
	 */
	public boolean gluUnProject(final double winx, final double winy,
		final double winz, final WB_Point result) {
	    final double[] in = new double[4];
	    final double[] out = new double[4];
	    // Transform to normalized screen coordinates (-1 to 1).
	    in[0] = (((winx - m_aiViewport[0]) / m_aiViewport[2]) * 2.0) - 1.0;
	    in[1] = (((winy - m_aiViewport[1]) / m_aiViewport[3]) * 2.0) - 1.0;
	    in[2] = (((winz > 1) ? 1.0 : ((winz < 0) ? 0.0 : winz)) * 2.0) - 1.0;
	    in[3] = 1.0;
	    // Calculate homogeneous coordinates.
	    out[0] = (m_pMatrix.m00 * in[0]) + (m_pMatrix.m01 * in[1])
		    + (m_pMatrix.m02 * in[2]) + (m_pMatrix.m03 * in[3]);
	    out[1] = (m_pMatrix.m10 * in[0]) + (m_pMatrix.m11 * in[1])
		    + (m_pMatrix.m12 * in[2]) + (m_pMatrix.m13 * in[3]);
	    out[2] = (m_pMatrix.m20 * in[0]) + (m_pMatrix.m21 * in[1])
		    + (m_pMatrix.m22 * in[2]) + (m_pMatrix.m23 * in[3]);
	    out[3] = (m_pMatrix.m30 * in[0]) + (m_pMatrix.m31 * in[1])
		    + (m_pMatrix.m32 * in[2]) + (m_pMatrix.m33 * in[3]);
	    if (out[3] == 0.0) { // Check for an invalid result.
		result.set(0, 0, 0);
		return false;
	    }
	    // Scale to world coordinates.
	    out[3] = 1.0 / out[3];
	    result.set(out[0] * out[3], out[1] * out[3], out[2] * out[3]);
	    return true;
	}
    }

    /**
     *
     *
     * @param x
     * @param y
     * @return
     */
    public WB_Ray getPickingRay(final double x, final double y) {
	unproject.captureViewMatrix(home);
	unproject.calculatePickPoints(x, y, home.height);
	WB_Ray ray = new WB_Ray(unproject.ptStartPos, unproject.ptEndPos);
	final WB_Point o = ray.getOrigin();
	WB_Point e = ray.getPointOnLine(1000);
	double error = WB_CoordinateOp.getSqDistance2D(x, y,
		home.screenX(e.xf(), e.yf(), e.zf()),
		home.screenY(e.xf(), e.yf(), e.zf()));
	while (error > 1) {
	    final WB_Point ne = e.add(Math.random() - 0.5, Math.random() - 0.5,
		    Math.random() - 0.5);
	    final double nerror = WB_CoordinateOp.getSqDistance2D(x, y,
		    home.screenX(ne.xf(), ne.yf(), ne.zf()),
		    home.screenY(ne.xf(), ne.yf(), ne.zf()));
	    if (nerror < error) {
		error = nerror;
		e = ne;
	    }
	}
	ray = new WB_Ray(o, e.sub(o));
	return ray;
    }

    /**
     *
     *
     * @param mesh
     * @param x
     * @param y
     * @return
     */
    public HE_Face pickClosestFace(final HE_Mesh mesh, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final HE_FaceIntersection p = HE_Intersection.getClosestIntersection(
		mesh, mouseRay3d);
	return (p == null) ? null : p.face;
    }

    /**
     *
     *
     * @param mesh
     * @param x
     * @param y
     * @return
     */
    public HE_Vertex pickVertex(final HE_Mesh mesh, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final HE_FaceIntersection p = HE_Intersection.getClosestIntersection(
		mesh, mouseRay3d);
	if (p == null) {
	    return null;
	}
	final HE_Face f = p.face;
	final HE_FaceVertexCirculator fvc = new HE_FaceVertexCirculator(f);
	HE_Vertex trial;
	HE_Vertex closest = null;
	double d2 = 0;
	double d2min = Double.MAX_VALUE;
	while (fvc.hasNext()) {
	    trial = fvc.next();
	    d2 = trial.getPoint().getSqDistance3D(p.point);
	    if (d2 < d2min) {
		d2min = d2;
		closest = trial;
	    }
	}
	return closest;
    }

    /**
     *
     *
     * @param mesh
     * @param x
     * @param y
     * @return
     */
    public HE_Halfedge pickEdge(final HE_Mesh mesh, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final HE_FaceIntersection p = HE_Intersection.getClosestIntersection(
		mesh, mouseRay3d);
	if (p == null) {
	    return null;
	}
	final HE_Face f = p.face;
	final HE_FaceEdgeCirculator fec = new HE_FaceEdgeCirculator(f);
	HE_Halfedge trial;
	HE_Halfedge closest = null;
	double d2 = 0;
	double d2min = Double.MAX_VALUE;
	while (fec.hasNext()) {
	    trial = fec.next();
	    d2 = WB_GeometryOp.getDistanceToSegment3D(p.point, trial
		    .getStartVertex().getPoint(), trial.getEndVertex()
		    .getPoint());
	    if (d2 < d2min) {
		d2min = d2;
		closest = trial;
	    }
	}
	return closest;
    }

    /**
     *
     *
     * @param mesh
     * @param x
     * @param y
     * @return
     */
    public HE_Face pickFurthestFace(final HE_Mesh mesh, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final HE_FaceIntersection p = HE_Intersection.getFurthestIntersection(
		mesh, mouseRay3d);
	return (p == null) ? null : p.face;
    }

    /**
     *
     *
     * @param meshtree
     * @param x
     * @param y
     * @return
     */
    public HE_Face pickClosestFace(final WB_AABBTree meshtree, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final HE_FaceIntersection p = HE_Intersection.getClosestIntersection(
		meshtree, mouseRay3d);
	return (p == null) ? null : p.face;
    }

    /**
     *
     *
     * @param meshtree
     * @param x
     * @param y
     * @return
     */
    public HE_Face pickFurthestFace(final WB_AABBTree meshtree, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final HE_FaceIntersection p = HE_Intersection.getFurthestIntersection(
		meshtree, mouseRay3d);
	return (p == null) ? null : p.face;
    }

    /**
     *
     *
     * @param mesh
     * @param x
     * @param y
     * @return
     */
    public List<HE_Face> pickFaces(final HE_Mesh mesh, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final List<HE_FaceIntersection> p = HE_Intersection.getIntersection(
		mesh, mouseRay3d);
	final List<HE_Face> result = new ArrayList<HE_Face>();
	for (final HE_FaceIntersection fi : p) {
	    result.add(fi.face);
	}
	return result;
    }

    /**
     *
     *
     * @param meshtree
     * @param x
     * @param y
     * @return
     */
    public List<HE_Face> pickFaces(final WB_AABBTree meshtree, final double x,
	    final double y) {
	final WB_Ray mouseRay3d = getPickingRay(x, y);
	final List<HE_FaceIntersection> p = HE_Intersection.getIntersection(
		meshtree, mouseRay3d);
	final List<HE_Face> result = new ArrayList<HE_Face>();
	for (final HE_FaceIntersection fi : p) {
	    result.add(fi.face);
	}
	return result;
    }

    /**
     *
     *
     * @param d
     * @param mesh
     */
    public void drawBadVertices(final double d, final HE_MeshStructure mesh) {
	HE_Vertex v;
	final Iterator<HE_Vertex> vItr = mesh.vItr();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (!mesh.contains(v.getHalfedge())) {
		home.pushMatrix();
		home.translate(v.xf(), v.yf(), v.zf());
		home.box((float) d);
		home.popMatrix();
	    }
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawBoundaryEdges(final HE_MeshStructure mesh) {
	HE_Halfedge he;
	final Iterator<HE_Halfedge> heItr = mesh.heItr();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() == null) {
		home.line(he.getVertex().xf(), he.getVertex().yf(), he
			.getVertex().zf(), he.getNextInFace().getVertex().xf(),
			he.getNextInFace().getVertex().yf(), he.getNextInFace()
			.getVertex().zf());
	    }
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawBoundaryHalfedges(final HE_MeshStructure mesh) {
	HE_Halfedge he;
	final Iterator<HE_Halfedge> heItr = mesh.heItr();
	home.pushStyle();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getPair().getFace() == null) {
		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(), he
			.getVertex().zf(), he.getNextInFace().getVertex().xf(),
			he.getNextInFace().getVertex().yf(), he.getNextInFace()
			.getVertex().zf());
	    }
	}
	home.popStyle();
    }

    /**
     *
     *
     * @param f
     * @param d
     */
    public void drawFaceNormal(final HE_Face f, final double d) {
	final WB_Point p1 = f.getFaceCenter();
	final WB_Point p2 = new WB_Point(f.getFaceNormal().mul(d)).addSelf(p1);
	home.line(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(), p2.zf());
    }

    /**
     *
     *
     * @param d
     * @param mesh
     */
    public void drawFaceNormals(final double d, final HE_MeshStructure mesh) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	WB_Point fc;
	WB_Vector fn;
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    fc = f.getFaceCenter();
	    fn = f.getFaceNormal();
	    home.line(fc.xf(), fc.yf(), fc.zf(),
		    (fc.xf() + ((float) d * fn.xf())),
		    (fc.yf() + ((float) d * fn.yf())),
		    (fc.zf() + ((float) d * fn.zf())));
	}
    }

    /**
     *
     *
     * @param mesh
     */
    public void drawFaceTypes(final HE_MeshStructure mesh) {
	final Iterator<HE_Face> fItr = mesh.fItr();
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getFaceType() == WB_ClassificationConvex.CONVEX) {
		home.fill(0, 255, 0);
	    } else if (f.getFaceType() == WB_ClassificationConvex.CONCAVE) {
		home.fill(255, 0, 0);
	    } else {
		home.fill(0, 0, 255);
	    }
	    drawFace(f);
	}
    }

    /**
     *
     *
     * @param he
     * @param d
     * @param s
     */
    public void drawHalfedge(final HE_Halfedge he, final double d,
	    final double s) {
	final WB_Point c = he.getHalfedgeCenter();
	c.addSelf(he.getHalfedgeNormal().mulSelf(d));
	home.stroke(255, 0, 0);
	home.line(he.getVertex().xf(), he.getVertex().yf(),
		he.getVertex().zf(), c.xf(), c.yf(), c.zf());
	if (he.getHalfedgeType() == WB_ClassificationConvex.CONVEX) {
	    home.stroke(0, 255, 0);
	} else if (he.getHalfedgeType() == WB_ClassificationConvex.CONCAVE) {
	    home.stroke(255, 0, 0);
	} else {
	    home.stroke(0, 0, 255);
	}
	home.pushMatrix();
	home.translate(c.xf(), c.yf(), c.zf());
	home.box((float) s);
	home.popMatrix();
    }

    /**
     *
     *
     * @param he
     * @param d
     * @param s
     * @param f
     */
    public void drawHalfedge(final HE_Halfedge he, final double d,
	    final double s, final double f) {
	final WB_Point c = geometryfactory.createInterpolatedPoint(
		he.getVertex(), he.getEndVertex(), f);
	c.addSelf(he.getHalfedgeNormal().mulSelf(d));
	home.stroke(255, 0, 0);
	home.line(he.getVertex().xf(), he.getVertex().yf(),
		he.getVertex().zf(), c.xf(), c.yf(), c.zf());
	if (he.getHalfedgeType() == WB_ClassificationConvex.CONVEX) {
	    home.stroke(0, 255, 0);
	} else if (he.getHalfedgeType() == WB_ClassificationConvex.CONCAVE) {
	    home.stroke(255, 0, 0);
	} else {
	    home.stroke(0, 0, 255);
	}
	home.pushMatrix();
	home.translate(c.xf(), c.yf(), c.zf());
	home.box((float) s);
	home.popMatrix();
    }

    /**
     *
     *
     * @param key
     * @param d
     * @param s
     * @param mesh
     */
    public void drawHalfedge(final Long key, final double d, final double s,
	    final HE_MeshStructure mesh) {
	final HE_Halfedge he = mesh.getHalfedgeByKey(key);
	drawHalfedge(he, d, s);
    }

    /**
     *
     *
     * @param d
     * @param f
     * @param mesh
     */
    public void drawHalfedges(final double d, final double f,
	    final HE_MeshStructure mesh) {
	WB_Point c;
	HE_Halfedge he;
	final Iterator<HE_Halfedge> heItr = mesh.heItr();
	home.pushStyle();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() != null) {
		c = geometryfactory.createInterpolatedPoint(he.getVertex(),
			he.getEndVertex(), f);
		c.addSelf(he.getHalfedgeNormal().mulSelf(d));
		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(), he
			.getVertex().zf(), c.xf(), c.yf(), c.zf());
		if (he.getHalfedgeType() == WB_ClassificationConvex.CONVEX) {
		    home.stroke(0, 255, 0);
		    home.fill(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_ClassificationConvex.CONCAVE) {
		    home.stroke(255, 0, 0);
		    home.fill(255, 0, 0);
		} else {
		    home.stroke(0, 0, 255);
		    home.fill(0, 0, 255);
		}
		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) d);
		home.popMatrix();
	    } else {
		c = geometryfactory.createInterpolatedPoint(he.getVertex(),
			he.getEndVertex(), f);
		c.addSelf(he.getPair().getHalfedgeNormal().mulSelf(-d));
		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(), he
			.getVertex().zf(), c.xf(), c.yf(), c.zf());
		home.stroke(0, 255, 255);
		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) d);
		home.popMatrix();
	    }
	}
	home.popStyle();
    }

    /**
     *
     *
     * @param d
     * @param mesh
     */
    public void drawHalfedges(final double d, final HE_MeshStructure mesh) {
	WB_Point c;
	HE_Halfedge he;
	final Iterator<HE_Halfedge> heItr = mesh.heItr();
	home.pushStyle();
	while (heItr.hasNext()) {
	    he = heItr.next();
	    if (he.getFace() != null) {
		c = he.getHalfedgeCenter();
		c.addSelf(he.getHalfedgeNormal().mulSelf(d));
		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(), he
			.getVertex().zf(), c.xf(), c.yf(), c.zf());
		if (he.getHalfedgeType() == WB_ClassificationConvex.CONVEX) {
		    home.stroke(0, 255, 0);
		    home.fill(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_ClassificationConvex.CONCAVE) {
		    home.stroke(255, 0, 0);
		    home.fill(255, 0, 0);
		} else {
		    home.stroke(0, 0, 255);
		    home.fill(0, 0, 255);
		}
		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) d);
		home.popMatrix();
	    } else {
		c = he.getHalfedgeCenter();
		c.addSelf(he.getPair().getHalfedgeNormal().mulSelf(-d));
		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(), he
			.getVertex().zf(), c.xf(), c.yf(), c.zf());
		home.stroke(0, 255, 255);
		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) d);
		home.popMatrix();
	    }
	}
	home.popStyle();
    }

    /**
     *
     *
     * @param he
     * @param d
     * @param s
     */
    public void drawHalfedgeSimple(final HE_Halfedge he, final double d,
	    final double s) {
	final WB_Point c = he.getHalfedgeCenter();
	c.addSelf(he.getHalfedgeNormal().mulSelf(d));
	home.line(he.getVertex().xf(), he.getVertex().yf(),
		he.getVertex().zf(), c.xf(), c.yf(), c.zf());
	home.pushMatrix();
	home.translate(c.xf(), c.yf(), c.zf());
	home.box((float) s);
	home.popMatrix();
    }

    /**
     *
     *
     * @param d
     * @param mesh
     */
    public void drawVertexNormals(final double d, final HE_MeshStructure mesh) {
	final Iterator<HE_Vertex> vItr = mesh.vItr();
	WB_Vector vn;
	HE_Vertex v;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    vn = v.getVertexNormal();
	    draw(v, vn, d);
	}
    }

    public void setVertexColorFromTexture(final HE_Mesh mesh,
	    final PImage texture) {
	final HE_VertexIterator vitr = mesh.vItr();
	HE_Vertex v;
	HE_TextureCoordinate p;
	while (vitr.hasNext()) {
	    v = vitr.next();
	    p = v.getVertexUVW();
	    v.setColor(getColorFromPImage(p.ud(), p.vd(), texture));
	}
    }

    public void setHalfedgeColorFromTexture(final HE_Mesh mesh,
	    final PImage texture) {
	final HE_FaceIterator fitr = mesh.fItr();
	HE_Face f;
	HE_Halfedge he;
	HE_TextureCoordinate p;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    final HE_FaceHalfedgeInnerCirculator fhec = new HE_FaceHalfedgeInnerCirculator(
		    f);
	    while (fhec.hasNext()) {
		he = fhec.next();
		p = he.getVertex().getUVW(f);
		he.setColor(getColorFromPImage(p.ud(), p.vd(), texture));
	    }
	}
    }

    public void setFaceColorFromTexture(final HE_Mesh mesh, final PImage texture) {
	final HE_FaceIterator fitr = mesh.fItr();
	HE_Face f;
	HE_Vertex v;
	HE_TextureCoordinate uvw;
	while (fitr.hasNext()) {
	    f = fitr.next();
	    final HE_FaceVertexCirculator fvc = new HE_FaceVertexCirculator(f);
	    final WB_Point p = new WB_Point();
	    int id = 0;
	    while (fvc.hasNext()) {
		v = fvc.next();
		uvw = v.getUVW(f);
		p.addSelf(uvw.ud(), uvw.vd(), 0);
		id++;
	    }
	    p.divSelf(id);
	    f.setColor(getColorFromPImage(p.xd(), p.yd(), texture));
	}
    }

    private int getColorFromPImage(final double u, final double v,
	    final PImage texture) {
	return texture
		.get(Math.max(0,
			Math.min((int) (u * texture.width), texture.width - 1)),
			Math.max(0, Math.min((int) (v * texture.height),
				texture.height - 1)));
    }
}
