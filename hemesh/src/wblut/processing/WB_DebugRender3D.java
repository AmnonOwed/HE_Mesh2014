package wblut.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Vertex;

public class WB_DebugRender3D {
	private final PGraphics home;
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_DebugRender3D(final PApplet home) {
		this.home = home.g;
	}

	public WB_DebugRender3D(final PGraphics home) {
		this.home = home;
	}

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

	public void drawFaceNormal(final HE_Face f, final double d) {
		final WB_Point p1 = f.getFaceCenter();
		final WB_Point p2 = new WB_Point(f.getFaceNormal().mul(d))._addSelf(p1);
		home.line(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(), p2.zf());
	}

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
					(fc.xf() + (float) d * fn.xf()),
					(fc.yf() + (float) d * fn.yf()),
					(fc.zf() + (float) d * fn.zf()));
		}
	}

	public void drawFaceTypes(final HE_MeshStructure mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getFaceType() == WB_Convex.CONVEX) {
				home.fill(0, 255, 0);
			} else if (f.getFaceType() == WB_Convex.CONCAVE) {
				home.fill(255, 0, 0);
			} else {
				home.fill(0, 0, 255);
			}
			drawFace(f);
		}
	}

	public void drawHalfedge(final HE_Halfedge he, final double d,
			final double s) {
		final WB_Point c = he.getHalfedgeCenter();
		c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(),
				he.getVertex().zf(), c.xf(), c.yf(), c.zf());
		if (he.getHalfedgeType() == WB_Convex.CONVEX) {
			home.stroke(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
			home.stroke(255, 0, 0);
		} else {
			home.stroke(0, 0, 255);
		}
		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) s);
		home.popMatrix();
	}

	public void drawHalfedge(final HE_Halfedge he, final double d,
			final double s, final double f) {
		final WB_Point c = WB_Point.interpolate(he.getVertex(),
				he.getEndVertex(), f);
		c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

		home.stroke(255, 0, 0);
		home.line(he.getVertex().xf(), he.getVertex().yf(),
				he.getVertex().zf(), c.xf(), c.yf(), c.zf());
		if (he.getHalfedgeType() == WB_Convex.CONVEX) {
			home.stroke(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
			home.stroke(255, 0, 0);
		} else {
			home.stroke(0, 0, 255);
		}
		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) s);
		home.popMatrix();
	}

	public void drawHalfedge(final Long key, final double d, final double s,
			final HE_MeshStructure mesh) {
		final HE_Halfedge he = mesh.getHalfedgeByKey(key);
		drawHalfedge(he, d, s);

	}

	public void drawHalfedges(final double d, final double f,
			final HE_MeshStructure mesh) {

		WB_Point c;

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		home.pushStyle();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (he.getFace() != null) {
				c = WB_Point.interpolate(he.getVertex(), he.getEndVertex(), f);
				c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

				home.stroke(255, 0, 0);
				home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());

				if (he.getHalfedgeType() == WB_Convex.CONVEX) {
					home.stroke(0, 255, 0);
					home.fill(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
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
				c = WB_Point.interpolate(he.getVertex(), he.getEndVertex(), f);
				c._addSelf(he.getPair().getHalfedgeNormal()._mulSelf(-d));

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

	public void drawHalfedges(final double d, final HE_MeshStructure mesh) {

		WB_Point c;

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		home.pushStyle();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (he.getFace() != null) {
				c = he.getHalfedgeCenter();
				c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

				home.stroke(255, 0, 0);
				home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());
				if (he.getHalfedgeType() == WB_Convex.CONVEX) {
					home.stroke(0, 255, 0);
					home.fill(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
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
				c._addSelf(he.getPair().getHalfedgeNormal()._mulSelf(-d));

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

	public void drawHalfedgeSimple(final HE_Halfedge he, final double d,
			final double s) {
		final WB_Point c = he.getHalfedgeCenter();
		c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

		home.line(he.getVertex().xf(), he.getVertex().yf(),
				he.getVertex().zf(), c.xf(), c.yf(), c.zf());

		home.pushMatrix();
		home.translate(c.xf(), c.yf(), c.zf());
		home.box((float) s);
		home.popMatrix();
	}

	public void drawVertexNormals(final double d, final HE_MeshStructure mesh) {
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		WB_Vector vn;
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();

			vn = v.getVertexNormal();
			draw(v.pos, vn, d);
		}
	}

	private void draw(final WB_Coordinate p, final WB_Vector v, final double d) {
		home.line(p.xf(), p.yf(), p.zf(), p.xf() + (float) d * v.xf(), p.yf()
				+ (float) d * v.yf(), p.zf() + (float) d * v.zf());
	}

	private void drawFace(final HE_Face f) {
		if (f.getFaceOrder() > 2) {
			if (f.getFaceType() == WB_Convex.CONVEX) {
				List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
				tmpVertices = f.getFaceVertices();
				drawConvexShapeFromVertices(tmpVertices, false, null);
			} else {
				drawConcaveFace(f);

			}
		}
	}

	private void drawConcaveFace(final HE_Face f) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			home.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1).pos;
			v1 = vertices.get(tri.i2).pos;
			v2 = vertices.get(tri.i3).pos;

			home.vertex(v0.xf(), v0.yf(), v0.zf());

			home.vertex(v1.xf(), v1.yf(), v1.zf());

			home.vertex(v2.xf(), v2.yf(), v2.zf());
			home.endShape();

		}
	}

	private void drawConvexShapeFromVertices(final List<HE_Vertex> vertices,
			final boolean smooth, final HE_MeshStructure mesh) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {
			if (smooth) {
				home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final WB_Vector n0 = v0.getVertexNormal();
				final HE_Vertex v1 = vertices.get(1);
				final WB_Vector n1 = v1.getVertexNormal();
				final HE_Vertex v2 = vertices.get(2);
				final WB_Vector n2 = v2.getVertexNormal();
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n1.xf(), n1.yf(), n1.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.normal(n2.xf(), n2.yf(), n2.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.endShape();
			} else {
				home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);

				final HE_Vertex v2 = vertices.get(2);

				home.vertex(v0.xf(), v0.yf(), v0.zf());

				home.vertex(v1.xf(), v1.yf(), v1.zf());

				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.endShape();

			}
		} else if (degree == 4) {
			if (smooth) {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final WB_Vector n0 = v0.getVertexNormal();
				final WB_Vector n1 = v1.getVertexNormal();
				final WB_Vector n2 = v2.getVertexNormal();
				final WB_Vector n3 = v3.getVertexNormal();

				home.beginShape(PConstants.TRIANGLES);
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n1.xf(), n1.yf(), n1.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.normal(n2.xf(), n2.yf(), n2.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n2.xf(), n2.yf(), n2.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.normal(n3.xf(), n3.yf(), n3.zf());
				home.vertex(v3.xf(), v3.yf(), v3.zf());

				home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);

				home.beginShape(PConstants.TRIANGLES);
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.vertex(v3.xf(), v3.yf(), v3.zf());

				home.endShape();

			}
		} else if (degree == 5) {
			if (smooth) {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);

				final WB_Vector n0 = v0.getVertexNormal();
				final WB_Vector n1 = v1.getVertexNormal();
				final WB_Vector n2 = v2.getVertexNormal();
				final WB_Vector n3 = v3.getVertexNormal();
				final WB_Vector n4 = v4.getVertexNormal();

				home.beginShape(PConstants.TRIANGLES);
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n1.xf(), n1.yf(), n1.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.normal(n2.xf(), n2.yf(), n2.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n2.xf(), n2.yf(), n2.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.normal(n3.xf(), n3.yf(), n3.zf());
				home.vertex(v3.xf(), v3.yf(), v3.zf());
				home.normal(n0.xf(), n0.yf(), n0.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.normal(n3.xf(), n3.yf(), n3.zf());
				home.vertex(v3.xf(), v3.yf(), v3.zf());
				home.normal(n4.xf(), n4.yf(), n4.zf());
				home.vertex(v4.xf(), v4.yf(), v4.zf());

				home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);
				home.beginShape(PConstants.TRIANGLES);
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.vertex(v1.xf(), v1.yf(), v1.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.vertex(v2.xf(), v2.yf(), v2.zf());
				home.vertex(v3.xf(), v3.yf(), v3.zf());
				home.vertex(v0.xf(), v0.yf(), v0.zf());
				home.vertex(v3.xf(), v3.yf(), v3.zf());
				home.vertex(v4.xf(), v4.yf(), v4.zf());
				home.endShape();
			}
		} else {
			final ArrayList<HE_Vertex> subset = new ArrayList<HE_Vertex>();
			int div = 3;
			final int rem = vertices.size() - 5;
			if (rem == 1) {
				div = 3;
			} else if (rem == 2) {
				div = 4;
			} else {
				div = 5;
			}

			for (int i = 0; i < div; i++) {
				subset.add(vertices.get(i));
			}
			final ArrayList<HE_Vertex> toRemove = new ArrayList<HE_Vertex>();
			toRemove.add(vertices.get(1));
			if (div > 3) {
				toRemove.add(vertices.get(2));
			}
			if (div > 4) {
				toRemove.add(vertices.get(3));
			}
			vertices.removeAll(toRemove);
			drawConvexShapeFromVertices(subset, smooth, mesh);
			drawConvexShapeFromVertices(vertices, smooth, mesh);
		}

	}

}
