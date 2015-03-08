/*
 * 
 */
package wblut.processing;

import java.util.Iterator;
import java.util.List;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import wblut.geom.WB_ClassificationConvex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Vertex;

/**
 * 
 */
public class WB_DebugRender3D {
    
    /**
     * 
     */
    private final PGraphics home;
    
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
    public WB_DebugRender3D(final PApplet home) {
	this.home = home.g;
    }

    /**
     * 
     *
     * @param home 
     */
    public WB_DebugRender3D(final PGraphics home) {
	this.home = home;
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

    /**
     * 
     *
     * @param p 
     * @param v 
     * @param d 
     */
    private void draw(final WB_Coordinate p, final WB_Vector v, final double d) {
	home.line(p.xf(), p.yf(), p.zf(), p.xf() + ((float) d * v.xf()), p.yf()
		+ ((float) d * v.yf()), p.zf() + ((float) d * v.zf()));
    }

    /**
     * 
     *
     * @param f 
     */
    private void drawFace(final HE_Face f) {
	if (f.getFaceOrder() > 2) {
	    final int[][] tris = f.getTriangles();
	    final List<HE_Vertex> vertices = f.getFaceVertices();
	    WB_Coordinate v0, v1, v2;
	    int[] tri;
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
