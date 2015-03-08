/*
 * 
 */
package wblut.processing;

import java.util.Collection;
import java.util.List;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_Geometry;
import wblut.geom.WB_GeometryCollection;
import wblut.geom.WB_Line;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Polygon;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Ring;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Triangle;
import wblut.geom.WB_Triangulation2D;

/**
 * 
 */
public class WB_Render2D {
    
    /**
     * 
     */
    private final PGraphics home;

    /**
     * 
     *
     * @param home 
     */
    public WB_Render2D(final PApplet home) {
	this.home = home.g;
    }

    /**
     * 
     *
     * @param home 
     */
    public WB_Render2D(final PGraphics home) {
	this.home = home;
    }

    /**
     * 
     *
     * @param p 
     */
    public void drawPoint(final WB_Coordinate p) {
	home.point(p.xf(), p.yf());
    }

    /**
     * 
     *
     * @param p 
     * @param r 
     */
    public void drawPoint(final WB_Coordinate p, final double r) {
	home.ellipse(p.xf(), p.yf(), 2 * (float) r, 2 * (float) r);
    }

    /**
     * 
     *
     * @param v 
     * @param p 
     * @param r 
     */
    public void drawVector(final WB_Coordinate v, final WB_Coordinate p,
	    final double r) {
	home.pushMatrix();
	home.translate(p.xf(), p.yf());
	home.line(0f, 0f, (float) (r * v.xd()), (float) (r * v.yd()));
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
		(float) (L.getOrigin().xd() + (d * L.getDirection().xd())),
		(float) (L.getOrigin().yd() + (d * L.getDirection().yd())));
    }

    /**
     * 
     *
     * @param R 
     * @param d 
     */
    public void drawRay(final WB_Ray R, final double d) {
	home.line((float) (R.getOrigin().xd()), (float) (R.getOrigin().yd()),
		(float) (R.getOrigin().xd() + (d * R.getDirection().xd())),
		(float) (R.getOrigin().yd() + (d * R.getDirection().yd())));
    }

    /**
     * 
     *
     * @param S 
     */
    public void drawSegment(final WB_Segment S) {
	home.line((float) (S.getOrigin().xd()), (float) (S.getOrigin().yd()),
		(float) (S.getEndpoint().xd()), (float) (S.getEndpoint().yd()));
    }

    /**
     * 
     *
     * @param p 
     * @param q 
     */
    public void drawSegment(final WB_Coordinate p, final WB_Coordinate q) {
	home.line((float) (p.xd()), (float) (p.yd()), (float) (q.xd()),
		(float) (q.yd()));
    }

    /**
     * 
     *
     * @param P 
     */
    public void drawPolyLine(final WB_PolyLine P) {
	for (int i = 0; i < (P.getNumberOfPoints() - 1); i++) {
	    home.line((float) (P.getPoint(i).xd()),
		    (float) (P.getPoint(i).yd()),
		    (float) (P.getPoint(i + 1).xd()),
		    (float) (P.getPoint(i + 1).yd()));
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
		    (float) (P.getPoint(j).yd()), (float) (P.getPoint(i).xd()),
		    (float) (P.getPoint(i).yd()));
	}
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
	home.ellipse((float) C.getCenter().xd(), (float) C.getCenter().yd(),
		2 * (float) C.getRadius(), 2 * (float) C.getRadius());
    }

    /**
     * 
     *
     * @param T 
     */
    public void drawTriangle(final WB_Triangle T) {
	home.beginShape(PConstants.TRIANGLE);
	vertex(T.p1());
	vertex(T.p2());
	vertex(T.p3());
	home.endShape();
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
	vertex(points.get(0));
	vertex(points.get(1));
	vertex(points.get(2));
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
	vertex(points[0]);
	vertex(points[1]);
	vertex(points[2]);
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
     * @param p 
     */
    private void vertex(final WB_Coordinate p) {
	home.vertex(p.xf(), p.yf());
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
}
