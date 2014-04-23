package wblut.processing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;
import wblut.geom.Segment;
import wblut.geom.SimplePolygon;
import wblut.geom.Triangle;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBNode;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_Circle;
import wblut.geom.WB_Convex;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_CoordinateSequence;
import wblut.geom.WB_Curve;
import wblut.geom.WB_Frame;
import wblut.geom.WB_FrameNode;
import wblut.geom.WB_FrameStrut;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Line;
import wblut.geom.WB_Line2D;
import wblut.geom.WB_Mesh;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_PolyLine;
import wblut.geom.WB_Ray;
import wblut.geom.WB_SimpleMesh;
import wblut.geom.WB_SimplePolygon2D;
import wblut.geom.WB_Triangle2D;
import wblut.geom.WB_Vector;
import wblut.hemesh.HET_Selector;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_Halfedge;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;
import wblut.hemesh.HE_Vertex;

/**
 * WB_Render.
 * 
 * @author Frederik Vanhoutte, W:Blut
 */
public class WB_Render {

	/** Home applet. */
	protected PApplet _home;

	public WB_Render(final PApplet home) {
		_home = home;

	}

	public PShape toSmoothPShape(final HE_Mesh mesh) {
		final PShape retained = _home.createShape();
		retained.beginShape(_home.TRIANGLES);
		final HE_Mesh lmesh = mesh.get();
		lmesh.triangulate();
		WB_Vector n = new WB_Vector();
		Iterator<HE_Face> fItr = lmesh.fItr();
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
		final PShape retained = _home.createShape();
		retained.beginShape(_home.TRIANGLES);
		final HE_Mesh lmesh = mesh.get();
		lmesh.triangulate();
		Iterator<HE_Face> fItr = lmesh.fItr();
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

	// RENDER

	/**
	 * Draw one face.
	 * 
	 * @param key
	 *            key of face
	 * @param mesh
	 *            the mesh
	 */
	public void drawFace(final Long key, final HE_Mesh mesh) {
		List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		tmpVertices = f.getFaceVertices();
		drawConvexShapeFromVertices(tmpVertices, false, null);

	}

	/**
	 * Draw one face.
	 * 
	 * @param f
	 *            face
	 */
	public void drawFace(final HE_Face f) {
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

	/**
	 * Draw face xy.
	 * 
	 * @param f
	 *            the f
	 */
	public void drawFaceXY(final HE_Face f) {
		if (f.getFaceOrder() > 2) {
			if (f.getFaceType() == WB_Convex.CONVEX) {
				List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
				tmpVertices = f.getFaceVertices();
				drawConvexShapeFromVerticesXY(tmpVertices, false, null);
			} else {
				drawConcaveFaceXY(f);

			}
		}
	}

	/**
	 * Draw face.
	 * 
	 * @param f
	 *            the f
	 * @param pg
	 *            the pg
	 */
	public void drawFace(final HE_Face f, final PGraphics pg) {
		if (f.getFaceOrder() > 2) {
			if (f.getFaceType() == WB_Convex.CONVEX) {
				List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
				tmpVertices = f.getFaceVertices();
				drawConvexShapeFromVertices(tmpVertices, false, null, pg);
			} else {
				drawConcaveFace(f, pg);

			}
		}
	}

	/**
	 * Draw one face.
	 * 
	 * @param key
	 *            key of face
	 * @param smooth
	 *            the smooth
	 * @param mesh
	 *            the mesh
	 */
	public void drawFace(final Long key, final boolean smooth,
			final HE_Mesh mesh) {
		drawFace(mesh.getFaceByKey(key), smooth, mesh);

	}

	/**
	 * Draw one face.
	 * 
	 * @param f
	 *            face
	 * @param smooth
	 *            the smooth
	 * @param mesh
	 *            the mesh
	 */
	public void drawFace(final HE_Face f, final boolean smooth,
			final HE_Mesh mesh) {
		if (f.getFaceOrder() > 2) {
			if (f.getFaceType() == WB_Convex.CONVEX) {
				List<HE_Vertex> tmpVertices = new ArrayList<HE_Vertex>();
				tmpVertices = f.getFaceVertices();
				drawConvexShapeFromVertices(tmpVertices, smooth, mesh);
			} else {
				drawConcaveFace(f);

			}
		}
	}

	/**
	 * Draw one facenormal.
	 * 
	 * @param f
	 *            face
	 * @param d
	 *            the d
	 */
	public void drawFaceNormal(final HE_Face f, final double d) {
		final WB_Point p1 = f.getFaceCenter();
		final WB_Point p2 = new WB_Point(f.getFaceNormal().mul(d))._addSelf(p1);
		_home.line(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(), p2.zf());
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 * @param d
	 *            the d
	 * @param mesh
	 *            the mesh
	 */
	public void drawFacenormals(final double d, final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceNormal(fItr.next(), d);
		}
	}

	/**
	 * Draw one edge.
	 * 
	 * @param key
	 *            key of edge
	 * @param mesh
	 *            the mesh
	 */
	public void drawEdge(final Long key, final HE_Mesh mesh) {
		final HE_Edge e = mesh.getEdgeByKey(key);
		_home.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
				.getStartVertex().zf(), e.getEndVertex().xf(), e.getEndVertex()
				.yf(), e.getEndVertex().zf());
	}

	/**
	 * Draw one edge.
	 * 
	 * @param e
	 *            edge
	 */
	public void drawEdge(final HE_Edge e) {
		_home.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
				.getStartVertex().zf(), e.getEndVertex().xf(), e.getEndVertex()
				.yf(), e.getEndVertex().zf());
	}

	/**
	 * Draw one vertex as box.
	 * 
	 * @param key
	 *            key of vertex
	 * @param d
	 *            size of box
	 * @param mesh
	 *            the mesh
	 */
	public void drawVertex(final Long key, final double d, final HE_Mesh mesh) {

		final HE_Vertex v = mesh.getVertexByKey(key);
		_home.pushMatrix();
		_home.translate((v.xf()), (v.yf()), (v.zf()));
		_home.box((float) d);
		_home.popMatrix();

	}

	/**
	 * Draw one vertex as box.
	 * 
	 * @param v
	 *            vertex
	 * @param d
	 *            size of box
	 */
	public void drawVertex(final HE_Vertex v, final double d) {

		_home.pushMatrix();
		_home.translate((v.xf()), (v.yf()), (v.zf()));
		_home.box((float) d);
		_home.popMatrix();

	}

	/**
	 * Draw point.
	 * 
	 * @param v
	 *            the v
	 * @param d
	 *            the d
	 */
	public void drawPoint(final WB_Coordinate v, final double d) {
		_home.pushMatrix();
		_home.translate((v.xf()), (v.yf()), (v.zf()));
		_home.box((float) d);
		_home.popMatrix();
	}

	/**
	 * Draw points.
	 * 
	 * @param points
	 *            the points
	 * @param d
	 *            the d
	 */
	public void drawPoints(final WB_Coordinate[] points, final double d) {
		for (WB_Coordinate v : points) {
			_home.pushMatrix();
			_home.translate((v.xf()), (v.yf()), (v.zf()));
			_home.box((float) d);
			_home.popMatrix();
		}
	}

	/**
	 * Draw point.
	 * 
	 * @param points
	 *            the points
	 * @param d
	 *            the d
	 */
	public void drawPoints(final Collection<? extends WB_Coordinate> points,
			final double d) {
		for (final WB_Coordinate v : points) {
			drawPoint(v, d);
		}
	}

	/**
	 * Draw one half-edge.
	 * 
	 * @param key
	 *            key of half-edge
	 * @param d
	 *            offset from edge
	 * @param s
	 *            the s
	 * @param mesh
	 *            the mesh
	 */
	public void drawHalfedge(final Long key, final double d, final double s,
			final HE_Mesh mesh) {
		final HE_Halfedge he = mesh.getHalfedgeByKey(key);
		drawHalfedge(he, d, s);

	}

	/**
	 * Draw halfedge simple.
	 * 
	 * @param he
	 *            the he
	 * @param d
	 *            the d
	 * @param s
	 *            the s
	 */
	public void drawHalfedgeSimple(final HE_Halfedge he, final double d,
			final double s) {
		final WB_Point c = he.getHalfedgeCenter();
		c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

		_home.line(he.getVertex().xf(), he.getVertex().yf(), he.getVertex()
				.zf(), c.xf(), c.yf(), c.zf());

		_home.pushMatrix();
		_home.translate(c.xf(), c.yf(), c.zf());
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	 * Draw one half-edge.
	 * 
	 * @param he
	 *            halfedge
	 * @param d
	 *            offset from edge
	 * @param s
	 *            the s
	 */
	public void drawHalfedge(final HE_Halfedge he, final double d,
			final double s) {
		final WB_Point c = he.getHalfedgeCenter();
		c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

		_home.stroke(255, 0, 0);
		_home.line(he.getVertex().xf(), he.getVertex().yf(), he.getVertex()
				.zf(), c.xf(), c.yf(), c.zf());
		if (he.getHalfedgeType() == WB_Convex.CONVEX) {
			_home.stroke(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
			_home.stroke(255, 0, 0);
		} else {
			_home.stroke(0, 0, 255);
		}
		_home.pushMatrix();
		_home.translate(c.xf(), c.yf(), c.zf());
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	 * Draw halfedge.
	 * 
	 * @param he
	 *            the he
	 * @param d
	 *            the d
	 * @param s
	 *            the s
	 * @param f
	 *            the f
	 */
	public void drawHalfedge(final HE_Halfedge he, final double d,
			final double s, final double f) {
		final WB_Point c = WB_Point.interpolate(he.getVertex(),
				he.getEndVertex(), f);
		c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

		_home.stroke(255, 0, 0);
		_home.line(he.getVertex().xf(), he.getVertex().yf(), he.getVertex()
				.zf(), c.xf(), c.yf(), c.zf());
		if (he.getHalfedgeType() == WB_Convex.CONVEX) {
			_home.stroke(0, 255, 0);
		} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
			_home.stroke(255, 0, 0);
		} else {
			_home.stroke(0, 0, 255);
		}
		_home.pushMatrix();
		_home.translate(c.xf(), c.yf(), c.zf());
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawFaces(final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next());
		}
	}

	/**
	 * Draw faces xy.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawFacesXY(final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFaceXY(fItr.next());
		}
	}

	/**
	 * Draw faces.
	 * 
	 * @param meshes
	 *            the meshes
	 */
	public void drawFaces(final Collection<HE_Mesh> meshes) {
		final Iterator<HE_Mesh> mItr = meshes.iterator();
		while (mItr.hasNext()) {
			drawFaces(mItr.next());
		}
	}

	/**
	 * Draw faces.
	 * 
	 * @param mesh
	 *            the mesh
	 * @param pg
	 *            the pg
	 */
	public void drawFaces(final HE_Mesh mesh, final PGraphics pg) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), pg);
		}
	}

	/**
	 * Draw faces.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawFaces(final WB_SimpleMesh mesh) {
		final int nf = mesh.getFaces().length;
		for (int i = 0; i < nf; i++) {
			final int[] verts = mesh.getFaces()[i];
			final int nv = verts.length;
			_home.beginShape(PConstants.POLYGON);
			for (int j = 0; j < nv; j++) {
				final WB_Point p = mesh.getVertex(verts[j]);
				_home.vertex(p.xf(), p.yf(), p.zf());

			}

		}
		_home.endShape(PConstants.CLOSE);
	}

	/**
	 * Draw mesh face types.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawFaceTypes(final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getFaceType() == WB_Convex.CONVEX) {
				_home.fill(0, 255, 0);
			} else if (f.getFaceType() == WB_Convex.CONCAVE) {
				_home.fill(255, 0, 0);
			} else {
				_home.fill(0, 0, 255);
			}
			drawFace(f);
		}
	}

	/**
	 * Draw mesh face normals.
	 * 
	 * @param d
	 *            the d
	 * @param mesh
	 *            the mesh
	 */
	public void drawFaceNormals(final double d, final HE_Mesh mesh) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		WB_Point fc;
		WB_Vector fn;
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			fc = f.getFaceCenter();
			fn = f.getFaceNormal();
			_home.line(fc.xf(), fc.yf(), fc.zf(),
					(fc.xf() + (float) d * fn.xf()),
					(fc.yf() + (float) d * fn.yf()),
					(fc.zf() + (float) d * fn.zf()));
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
	public void drawFaces(final int label, final HE_Mesh mesh) {
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
	 * Draw faces.
	 * 
	 * @param label
	 *            the label
	 * @param mesh
	 *            the mesh
	 * @param pg
	 *            the pg
	 */
	public void drawFaces(final int label, final HE_Mesh mesh,
			final PGraphics pg) {
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			if (f.getLabel() == label) {
				drawFace(f, pg);
			}
		}
	}

	/**
	 * Draw mesh edges.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawEdges(final HE_Mesh mesh) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			_home.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf(), e.getEndVertex().xf(), e
					.getEndVertex().yf(), e.getEndVertex().zf());

		}
	}

	/**
	 * Draw edges.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawEdges(final WB_SimpleMesh mesh) {
		final int nf = mesh.getFaces().length;
		for (int i = 0; i < nf; i++) {
			final int[] verts = mesh.getFaces()[i];
			final int nv = verts.length;
			for (int j = 0, k = nv - 1; j < nv; k = j, j++) {
				final WB_Point p = mesh.getVertex(verts[k]);
				final WB_Point q = mesh.getVertex(verts[j]);
				if (p.smallerThan(q)) {
					_home.line(p.xf(), p.yf(), p.zf(), q.xf(), q.yf(), q.zf());
				}
			}

		}

	}

	/**
	 * Draw edges.
	 * 
	 * @param meshes
	 *            the meshes
	 */
	public void drawEdges(final Collection<HE_Mesh> meshes) {
		final Iterator<HE_Mesh> mItr = meshes.iterator();
		while (mItr.hasNext()) {
			drawEdges(mItr.next());
		}
	}

	/**
	 * Draw edges.
	 * 
	 * @param mesh
	 *            the mesh
	 * @param pg
	 *            the pg
	 */
	public void drawEdges(final HE_Mesh mesh, final PGraphics pg) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			pg.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf(), e.getEndVertex().xf(), e
					.getEndVertex().yf(), e.getEndVertex().zf());

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
	public void drawEdges(final int label, final HE_Mesh mesh) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getLabel() == label) {
				_home.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
						.getStartVertex().zf(), e.getEndVertex().xf(), e
						.getEndVertex().yf(), e.getEndVertex().zf());
			}

		}
	}

	/**
	 * Draw edges.
	 * 
	 * @param label
	 *            the label
	 * @param mesh
	 *            the mesh
	 * @param pg
	 *            the pg
	 */
	public void drawEdges(final int label, final HE_Mesh mesh,
			final PGraphics pg) {
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			if (e.getLabel() == label) {
				pg.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
						.getStartVertex().zf(), e.getEndVertex().xf(), e
						.getEndVertex().yf(), e.getEndVertex().zf());
			}

		}
	}

	/**
	 * Draw mesh boundary edges.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawBoundaryEdges(final HE_Mesh mesh) {
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() == null) {
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), he.getNextInFace().getVertex().xf(),
						he.getNextInFace().getVertex().yf(), he.getNextInFace()
								.getVertex().zf());
			}

		}
	}

	/**
	 * Draw mesh vertices as box.
	 * 
	 * @param d
	 *            size of box
	 * @param mesh
	 *            the mesh
	 */
	public void drawVertices(final double d, final HE_Mesh mesh) {
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			_home.pushMatrix();
			_home.translate(v.xf(), v.yf(), v.zf());
			_home.box((float) d);
			_home.popMatrix();

		}

	}

	/**
	 * Draw bad vertices.
	 * 
	 * @param d
	 *            the d
	 * @param mesh
	 *            the mesh
	 */
	public void drawBadVertices(final double d, final HE_Mesh mesh) {
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			if (!mesh.contains(v.getHalfedge())) {
				_home.pushMatrix();
				_home.translate(v.xf(), v.yf(), v.zf());
				_home.box((float) d);
				_home.popMatrix();
			}

		}

	}

	/**
	 * Draw vertex normals.
	 * 
	 * @param d
	 *            the d
	 * @param mesh
	 *            the mesh
	 */
	public void drawVertexNormals(final double d, final HE_Mesh mesh) {
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		WB_Vector vn;
		HE_Vertex v;
		while (vItr.hasNext()) {
			v = vItr.next();

			vn = v.getVertexNormal();
			draw(v.pos, vn, d);
		}
	}

	/**
	 * Draw mesh halfedges, for debugging purposes.
	 * 
	 * @param d
	 *            offset from edge
	 * @param mesh
	 *            the mesh
	 */
	public void drawHalfedges(final double d, final HE_Mesh mesh) {

		WB_Point c;

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		_home.pushStyle();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (he.getFace() != null) {
				c = he.getHalfedgeCenter();
				c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());
				if (he.getHalfedgeType() == WB_Convex.CONVEX) {
					_home.stroke(0, 255, 0);
					_home.fill(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
					_home.stroke(255, 0, 0);
					_home.fill(255, 0, 0);
				} else {
					_home.stroke(0, 0, 255);
					_home.fill(0, 0, 255);
				}
				_home.pushMatrix();
				_home.translate(c.xf(), c.yf(), c.zf());
				_home.box((float) d);
				_home.popMatrix();
			} else {
				c = he.getHalfedgeCenter();
				c._addSelf(he.getPair().getHalfedgeNormal()._mulSelf(-d));

				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());
				_home.stroke(0, 255, 255);

				_home.pushMatrix();
				_home.translate(c.xf(), c.yf(), c.zf());
				_home.box((float) d);
				_home.popMatrix();

			}
		}
		_home.popStyle();
	}

	/**
	 * Draw halfedges.
	 * 
	 * @param d
	 *            the d
	 * @param f
	 *            the f
	 * @param mesh
	 *            the mesh
	 */
	public void drawHalfedges(final double d, final double f, final HE_Mesh mesh) {

		WB_Point c;

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		_home.pushStyle();
		while (heItr.hasNext()) {
			he = heItr.next();

			if (he.getFace() != null) {
				c = WB_Point.interpolate(he.getVertex(), he.getEndVertex(), f);
				c._addSelf(he.getHalfedgeNormal()._mulSelf(d));

				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());

				if (he.getHalfedgeType() == WB_Convex.CONVEX) {
					_home.stroke(0, 255, 0);
					_home.fill(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
					_home.stroke(255, 0, 0);
					_home.fill(255, 0, 0);
				} else {
					_home.stroke(0, 0, 255);
					_home.fill(0, 0, 255);
				}
				_home.pushMatrix();
				_home.translate(c.xf(), c.yf(), c.zf());
				_home.box((float) d);
				_home.popMatrix();
			} else {
				c = WB_Point.interpolate(he.getVertex(), he.getEndVertex(), f);
				c._addSelf(he.getPair().getHalfedgeNormal()._mulSelf(-d));

				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());
				_home.stroke(0, 255, 255);

				_home.pushMatrix();
				_home.translate(c.xf(), c.yf(), c.zf());
				_home.box((float) d);
				_home.popMatrix();

			}
		}
		_home.popStyle();
	}

	/**
	 * Draw unpaired mesh halfedges, for debugging purposes.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawUnpairedHalfedges(final HE_Mesh mesh) {

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		_home.pushStyle();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair() == null) {
				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), he.getNextInFace().getVertex().xf(),
						he.getNextInFace().getVertex().yf(), he.getNextInFace()
								.getVertex().zf());
			}

		}
		_home.popStyle();
	}

	/**
	 * Draw unpaired mesh halfedges, for debugging purposes.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawBoundaryHalfedges(final HE_Mesh mesh) {

		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = mesh.heItr();
		_home.pushStyle();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getPair().getFace() == null) {
				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), he.getNextInFace().getVertex().xf(),
						he.getNextInFace().getVertex().yf(), he.getNextInFace()
								.getVertex().zf());
			}

		}
		_home.popStyle();
	}

	/**
	 * Draw faces of selection. Typically used with noStroke();
	 * 
	 * @param selection
	 *            selection to draw
	 */
	public void drawFaces(final HE_Selection selection) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = selection.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next());
		}
	}

	/**
	 * Draw edges of selection.
	 * 
	 * @param selection
	 *            selection to draw
	 */
	public void drawEdges(final HE_Selection selection) {
		final Iterator<HE_Edge> eItr = selection.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			_home.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf(), e.getEndVertex().xf(), e
					.getEndVertex().yf(), e.getEndVertex().zf());
		}
	}

	/**
	 * Draw vertices of selection as boxes.
	 * 
	 * @param d
	 *            size of box
	 * @param selection
	 *            selection to draw
	 */
	public void drawVertices(final double d, final HE_Selection selection) {
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = selection.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			_home.pushMatrix();
			_home.translate(v.xf(), v.yf(), v.zf());
			_home.box((float) d);
			_home.popMatrix();

		}
	}

	/**
	 * Draw mesh half-edges of selection, for debugging purposes.
	 * 
	 * @param d
	 *            offset from edge
	 * @param selection
	 *            selection to draw
	 */
	public void drawHalfedges(final double d, final HE_Selection selection) {
		WB_Point c;
		HE_Halfedge he;
		final Iterator<HE_Halfedge> heItr = selection.heItr();
		while (heItr.hasNext()) {
			he = heItr.next();
			if (he.getFace() != null) {
				c = he.getHalfedgeCenter();
				c._addSelf(he.getHalfedgeNormal()._mulSelf(d));
				_home.stroke(255, 0, 0);
				_home.line(he.getVertex().xf(), he.getVertex().yf(), he
						.getVertex().zf(), c.xf(), c.yf(), c.zf());
				if (he.getHalfedgeType() == WB_Convex.CONVEX) {
					_home.stroke(0, 255, 0);
				} else if (he.getHalfedgeType() == WB_Convex.CONCAVE) {
					_home.stroke(255, 0, 0);
				} else {
					_home.stroke(0, 0, 255);
				}
				_home.pushMatrix();
				_home.translate(c.xf(), c.yf(), c.zf());
				_home.box((float) d);
				_home.popMatrix();
			}
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
	public void drawFaceSmooth(final Long key, final HE_Mesh mesh) {
		new ArrayList<HE_Vertex>();
		final HE_Face f = mesh.getFaceByKey(key);
		drawFace(f, true, mesh);

	}

	/**
	 * Draw mesh faces using vertex normals. Typically used with noStroke().
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawFacesSmooth(final HE_Mesh mesh) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), true, mesh);
		}

	}

	/**
	 * Draw faces of selection using vertex normals. Typically used with
	 * noStroke().
	 * 
	 * @param selection
	 *            selection to draw
	 */
	public void drawFacesSmooth(final HE_Selection selection) {
		new ArrayList<HE_Vertex>();
		final Iterator<HE_Face> fItr = selection.fItr();
		while (fItr.hasNext()) {
			drawFace(fItr.next(), true, selection.parent);
		}

	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 * @param selector
	 *            selector tool
	 * @param mesh
	 *            the mesh
	 * @return key of face at mouse position
	 */
	public Long drawFaces(final HET_Selector selector, final HE_Mesh mesh) {
		new ArrayList<HE_Vertex>();
		selector.clear();
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			selector.start(f.key());
			drawFace(f);
		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh edges.
	 * 
	 * @param selector
	 *            selector tool
	 * @param mesh
	 *            the mesh
	 * @return key of edge at mouse position
	 */

	public Long drawEdges(final HET_Selector selector, final HE_Mesh mesh) {
		selector.clear();
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			selector.start(e.key());
			_home.beginShape();
			_home.vertex(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf());
			_home.vertex(e.getEndVertex().xf(), e.getEndVertex().yf(), e
					.getEndVertex().zf());
			_home.endShape();

		}

		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh vertices as box.
	 * 
	 * @param selector
	 *            selector tool
	 * @param d
	 *            size of box
	 * @param mesh
	 *            the mesh
	 * @return key of vertex at mouse position
	 */
	public Long drawVertices(final HET_Selector selector, final double d,
			final HE_Mesh mesh) {
		selector.clear();
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {

			v = vItr.next();
			selector.start(v.key());
			_home.pushMatrix();
			_home.translate(v.xf(), v.yf(), v.zf());
			_home.box((float) d);
			_home.popMatrix();

		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 * @param selection
	 *            selection to draw
	 * @param selector
	 *            selector tool
	 * @return key of face at mouse position
	 */
	public Long drawFaces(final HE_Selection selection,
			final HET_Selector selector) {
		new ArrayList<HE_Vertex>();
		selector.clear();
		final Iterator<HE_Face> fItr = selection.fItr();
		HE_Face f;
		while (fItr.hasNext()) {
			f = fItr.next();
			selector.start(f.key());
			drawFace(f);
		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh edges.
	 * 
	 * @param selection
	 *            selection to draw
	 * @param selector
	 *            selector tool
	 * @return key of edge at mouse position
	 */

	public Long drawEdges(final HE_Selection selection,
			final HET_Selector selector) {
		selector.clear();
		final Iterator<HE_Edge> eItr = selection.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			selector.start(e.key());
			_home.beginShape();
			_home.vertex(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf());
			_home.vertex(e.getEndVertex().xf(), e.getEndVertex().yf(), e
					.getEndVertex().zf());
			_home.endShape();

		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw mesh vertices as box.
	 * 
	 * @param d
	 *            size of box
	 * @param selection
	 *            selection to draw
	 * @param selector
	 *            selector tool
	 * @return key of vertex at mouse position
	 */
	public Long drawVertices(final double d, final HE_Selection selection,
			final HET_Selector selector) {
		selector.clear();
		final Iterator<HE_Vertex> vItr = selection.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {

			v = vItr.next();
			selector.start(v.key());
			_home.pushMatrix();
			_home.translate(v.xf(), v.yf(), v.zf());
			_home.box((float) d);
			_home.popMatrix();

		}
		selector.stop();
		return selector.get(_home.mouseX, _home.mouseY);
	}

	/**
	 * Draw concave face.
	 * 
	 * @param f
	 *            the f
	 */
	private void drawConcaveFace(final HE_Face f) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			_home.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1).pos;
			v1 = vertices.get(tri.i2).pos;
			v2 = vertices.get(tri.i3).pos;

			_home.vertex(v0.xf(), v0.yf(), v0.zf());

			_home.vertex(v1.xf(), v1.yf(), v1.zf());

			_home.vertex(v2.xf(), v2.yf(), v2.zf());
			_home.endShape();

		}
	}

	/**
	 * Draw concave face xy.
	 * 
	 * @param f
	 *            the f
	 */
	private void drawConcaveFaceXY(final HE_Face f) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			_home.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1).pos;
			v1 = vertices.get(tri.i2).pos;
			v2 = vertices.get(tri.i3).pos;

			_home.vertex(v0.xf(), v0.yf(), 0);

			_home.vertex(v1.xf(), v1.yf(), 0);

			_home.vertex(v2.xf(), v2.yf(), 0);
			_home.endShape();

		}
	}

	/**
	 * Draw concave face.
	 * 
	 * @param f
	 *            the f
	 * @param pg
	 *            the pg
	 */
	private void drawConcaveFace(final HE_Face f, final PGraphics pg) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			pg.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1).pos;
			v1 = vertices.get(tri.i2).pos;
			v2 = vertices.get(tri.i3).pos;

			pg.vertex(v0.xf(), v0.yf(), v0.zf());

			pg.vertex(v1.xf(), v1.yf(), v1.zf());

			pg.vertex(v2.xf(), v2.yf(), v2.zf());
			pg.endShape();

		}
	}

	/**
	 * Draw arbitrary convex face. Used internally by drawFace().
	 * 
	 * @param vertices
	 *            vertices of face
	 * @param smooth
	 *            use vertex normals?
	 * @param mesh
	 *            the mesh
	 */
	private void drawConvexShapeFromVertices(final List<HE_Vertex> vertices,
			final boolean smooth, final HE_Mesh mesh) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {
			if (smooth) {
				_home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final WB_Vector n0 = v0.getVertexNormal();
				final HE_Vertex v1 = vertices.get(1);
				final WB_Vector n1 = v1.getVertexNormal();
				final HE_Vertex v2 = vertices.get(2);
				final WB_Vector n2 = v2.getVertexNormal();
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.normal(n1.xf(), n1.yf(), n1.zf());
				_home.vertex(v1.xf(), v1.yf(), v1.zf());
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.endShape();
			} else {
				_home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);

				final HE_Vertex v2 = vertices.get(2);

				_home.vertex(v0.xf(), v0.yf(), v0.zf());

				_home.vertex(v1.xf(), v1.yf(), v1.zf());

				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.endShape();

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

				_home.beginShape(PConstants.TRIANGLES);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.normal(n1.xf(), n1.yf(), n1.zf());
				_home.vertex(v1.xf(), v1.yf(), v1.zf());
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.normal(n3.xf(), n3.yf(), n3.zf());
				_home.vertex(v3.xf(), v3.yf(), v3.zf());

				_home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);

				_home.beginShape(PConstants.TRIANGLES);
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.vertex(v1.xf(), v1.yf(), v1.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.vertex(v3.xf(), v3.yf(), v3.zf());

				_home.endShape();

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

				_home.beginShape(PConstants.TRIANGLES);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.normal(n1.xf(), n1.yf(), n1.zf());
				_home.vertex(v1.xf(), v1.yf(), v1.zf());
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.normal(n3.xf(), n3.yf(), n3.zf());
				_home.vertex(v3.xf(), v3.yf(), v3.zf());
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.normal(n3.xf(), n3.yf(), n3.zf());
				_home.vertex(v3.xf(), v3.yf(), v3.zf());
				_home.normal(n4.xf(), n4.yf(), n4.zf());
				_home.vertex(v4.xf(), v4.yf(), v4.zf());

				_home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);
				_home.beginShape(PConstants.TRIANGLES);
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.vertex(v1.xf(), v1.yf(), v1.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.vertex(v2.xf(), v2.yf(), v2.zf());
				_home.vertex(v3.xf(), v3.yf(), v3.zf());
				_home.vertex(v0.xf(), v0.yf(), v0.zf());
				_home.vertex(v3.xf(), v3.yf(), v3.zf());
				_home.vertex(v4.xf(), v4.yf(), v4.zf());
				_home.endShape();
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

	/**
	 * Draw convex shape from vertices xy.
	 * 
	 * @param vertices
	 *            the vertices
	 * @param smooth
	 *            the smooth
	 * @param mesh
	 *            the mesh
	 */
	private void drawConvexShapeFromVerticesXY(final List<HE_Vertex> vertices,
			final boolean smooth, final HE_Mesh mesh) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {
			if (smooth) {
				_home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final WB_Vector n0 = v0.getVertexNormal();
				final HE_Vertex v1 = vertices.get(1);
				final WB_Vector n1 = v1.getVertexNormal();
				final HE_Vertex v2 = vertices.get(2);
				final WB_Vector n2 = v2.getVertexNormal();
				_home.normal(n0.xf(), n0.yf(), 0);
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.normal(n1.xf(), n1.yf(), 0);
				_home.vertex(v1.xf(), v1.yf(), 0);
				_home.normal(n2.xf(), n2.yf(), 0);
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.endShape();
			} else {
				_home.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);

				final HE_Vertex v2 = vertices.get(2);

				_home.vertex(v0.xf(), v0.yf(), 0);

				_home.vertex(v1.xf(), v1.yf(), 0);

				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.endShape();

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

				_home.beginShape(PConstants.TRIANGLES);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.normal(n1.xf(), n1.yf(), n1.zf());
				_home.vertex(v1.xf(), v1.yf(), 0);
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.normal(n3.xf(), n3.yf(), n3.zf());
				_home.vertex(v3.xf(), v3.yf(), 0);

				_home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);

				_home.beginShape(PConstants.TRIANGLES);
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.vertex(v1.xf(), v1.yf(), 0);
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.vertex(v3.xf(), v3.yf(), 0);

				_home.endShape();

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

				_home.beginShape(PConstants.TRIANGLES);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.normal(n1.xf(), n1.yf(), n1.zf());
				_home.vertex(v1.xf(), v1.yf(), 0);
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.normal(n2.xf(), n2.yf(), n2.zf());
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.normal(n3.xf(), n3.yf(), n3.zf());
				_home.vertex(v3.xf(), v3.yf(), 0);
				_home.normal(n0.xf(), n0.yf(), n0.zf());
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.normal(n3.xf(), n3.yf(), n3.zf());
				_home.vertex(v3.xf(), v3.yf(), 0);
				_home.normal(n4.xf(), n4.yf(), n4.zf());
				_home.vertex(v4.xf(), v4.yf(), 0);

				_home.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);
				_home.beginShape(PConstants.TRIANGLES);
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.vertex(v1.xf(), v1.yf(), 0);
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.vertex(v2.xf(), v2.yf(), 0);
				_home.vertex(v3.xf(), v3.yf(), 0);
				_home.vertex(v0.xf(), v0.yf(), 0);
				_home.vertex(v3.xf(), v3.yf(), 0);
				_home.vertex(v4.xf(), v4.yf(), 0);
				_home.endShape();
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

	/**
	 * Draw convex shape from vertices.
	 * 
	 * @param vertices
	 *            the vertices
	 * @param smooth
	 *            the smooth
	 * @param mesh
	 *            the mesh
	 * @param pg
	 *            the pg
	 */
	private void drawConvexShapeFromVertices(final List<HE_Vertex> vertices,
			final boolean smooth, final HE_Mesh mesh, final PGraphics pg) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {
			if (smooth) {
				pg.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final WB_Vector n0 = v0.getVertexNormal();
				final HE_Vertex v1 = vertices.get(1);
				final WB_Vector n1 = v1.getVertexNormal();
				final HE_Vertex v2 = vertices.get(2);
				final WB_Vector n2 = v2.getVertexNormal();
				pg.normal(n0.xf(), n0.yf(), n0.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.normal(n1.xf(), n1.yf(), n1.zf());
				pg.vertex(v1.xf(), v1.yf(), v1.zf());
				pg.normal(n2.xf(), n2.yf(), n2.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.endShape();
			} else {
				pg.beginShape(PConstants.TRIANGLES);
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);

				final HE_Vertex v2 = vertices.get(2);

				pg.vertex(v0.xf(), v0.yf(), v0.zf());

				pg.vertex(v1.xf(), v1.yf(), v1.zf());

				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.endShape();

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

				pg.beginShape(PConstants.TRIANGLES);
				pg.normal(n0.xf(), n0.yf(), n0.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.normal(n1.xf(), n1.yf(), n1.zf());
				pg.vertex(v1.xf(), v1.yf(), v1.zf());
				pg.normal(n2.xf(), n2.yf(), n2.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.normal(n0.xf(), n0.yf(), n0.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.normal(n2.xf(), n2.yf(), n2.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.normal(n3.xf(), n3.yf(), n3.zf());
				pg.vertex(v3.xf(), v3.yf(), v3.zf());

				pg.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);

				pg.beginShape(PConstants.TRIANGLES);
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.vertex(v1.xf(), v1.yf(), v1.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.vertex(v3.xf(), v3.yf(), v3.zf());

				pg.endShape();

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

				pg.beginShape(PConstants.TRIANGLES);
				pg.normal(n0.xf(), n0.yf(), n0.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.normal(n1.xf(), n1.yf(), n1.zf());
				pg.vertex(v1.xf(), v1.yf(), v1.zf());
				pg.normal(n2.xf(), n2.yf(), n2.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.normal(n0.xf(), n0.yf(), n0.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.normal(n2.xf(), n2.yf(), n2.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.normal(n3.xf(), n3.yf(), n3.zf());
				pg.vertex(v3.xf(), v3.yf(), v3.zf());
				pg.normal(n0.xf(), n0.yf(), n0.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.normal(n3.xf(), n3.yf(), n3.zf());
				pg.vertex(v3.xf(), v3.yf(), v3.zf());
				pg.normal(n4.xf(), n4.yf(), n4.zf());
				pg.vertex(v4.xf(), v4.yf(), v4.zf());

				pg.endShape();
			} else {
				final HE_Vertex v0 = vertices.get(0);
				final HE_Vertex v1 = vertices.get(1);
				final HE_Vertex v2 = vertices.get(2);
				final HE_Vertex v3 = vertices.get(3);
				final HE_Vertex v4 = vertices.get(4);
				pg.beginShape(PConstants.TRIANGLES);
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.vertex(v1.xf(), v1.yf(), v1.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.vertex(v2.xf(), v2.yf(), v2.zf());
				pg.vertex(v3.xf(), v3.yf(), v3.zf());
				pg.vertex(v0.xf(), v0.yf(), v0.zf());
				pg.vertex(v3.xf(), v3.yf(), v3.zf());
				pg.vertex(v4.xf(), v4.yf(), v4.zf());
				pg.endShape();
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

	/**
	 * Draw mesh edges as Bezier curves.
	 * 
	 * @param mesh
	 *            the mesh
	 */
	public void drawBezierEdges(final HE_Mesh mesh) {
		HE_Halfedge he;
		WB_Point p0;
		WB_Point p1;
		WB_Point p2;
		WB_Point p3;
		HE_Face f;
		final Iterator<HE_Face> fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			_home.beginShape();
			he = f.getHalfedge();
			p0 = he.getPrevInFace().getHalfedgeCenter();
			_home.vertex(p0.xf(), p0.yf(), p0.zf());

			do {

				p1 = he.getVertex().pos;
				p2 = he.getVertex().pos;
				p3 = he.getHalfedgeCenter();

				_home.bezierVertex(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(),
						p2.zf(), p3.xf(), p3.yf(), p3.zf());
				he = he.getNextInFace();
			} while (he != f.getHalfedge());
			_home.endShape();

		}
	}

	/**
	 * Draw polygon edges.
	 * 
	 * @param polygons
	 *            the polygons
	 */
	public void drawPolygonEdges(
			final Collection<? extends SimplePolygon> polygons) {
		final Iterator<? extends SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygonEdges(polyItr.next());
		}

	}

	/**
	 * Draw polygon edges.
	 * 
	 * @param polygon
	 *            the polygon
	 */
	public void drawPolygonEdges(final SimplePolygon polygon) {
		WB_Point v1, v2;
		final int n = polygon.getN();
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			v1 = polygon.getPoint(i);
			v2 = polygon.getPoint(j);
			_home.line(v1.xf(), v1.yf(), v1.zf(), v2.xf(), v2.yf(), v2.zf());
		}
	}

	/**
	 * Draw polygon vertices.
	 * 
	 * @param polygons
	 *            the polygons
	 * @param d
	 *            the d
	 */
	public void drawPolygonVertices(final Collection<SimplePolygon> polygons,
			final double d) {
		final Iterator<SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygonVertices(polyItr.next(), d);
		}

	}

	/**
	 * Draw polygon vertices.
	 * 
	 * @param polygon
	 *            the polygon
	 * @param d
	 *            the d
	 */
	public void drawPolygonVertices(final SimplePolygon polygon, final double d) {
		WB_Point v1;
		final int n = polygon.getN();
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			_home.pushMatrix();
			_home.translate(v1.xf(), v1.yf(), v1.zf());
			_home.box((float) d);
			_home.popMatrix();
		}
	}

	/**
	 * Draw polygon.
	 * 
	 * @param polygons
	 *            the polygons
	 */
	public void drawPolygon(final Collection<? extends SimplePolygon> polygons) {
		final Iterator<? extends SimplePolygon> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon(polyItr.next());
		}

	}

	/**
	 * Draw polygon.
	 * 
	 * @param polygon
	 *            the polygon
	 */
	public void drawPolygon(final SimplePolygon polygon) {
		WB_Point v1;
		final int n = polygon.getN();
		_home.beginShape(PConstants.POLYGON);
		for (int i = 0; i < n; i++) {
			v1 = polygon.getPoint(i);
			_home.vertex(v1.xf(), v1.yf(), v1.zf());

		}
		_home.endShape(PConstants.CLOSE);
	}

	/**
	 * Draw polyline edges.
	 * 
	 * @param polylines
	 *            the polylines
	 */
	public void drawPolylineEdges(final Collection<WB_PolyLine> polylines) {
		final Iterator<WB_PolyLine> polyItr = polylines.iterator();
		while (polyItr.hasNext()) {
			drawPolylineEdges(polyItr.next());
		}

	}

	/**
	 * Draw polyline edges.
	 * 
	 * @param polyline
	 *            the polyline
	 */
	public void drawPolylineEdges(final WB_PolyLine P) {
		for (int i = 0; i < P.getNumberOfPoints() - 1; i++) {

			_home.line((P.getPoint(i).xf()), (P.getPoint(i).yf()),
					(P.getPoint(i).zf()), (P.getPoint(i + 1).xf()),
					(P.getPoint(i + 1).yf()), (P.getPoint(i + 1).zf()));
		}
	}

	/**
	 * Draw polyline vertices.
	 * 
	 * @param polylines
	 *            the polylines
	 * @param d
	 *            the d
	 */
	public void drawPolylineVertices(final Collection<WB_PolyLine> polylines,
			final double d) {
		final Iterator<WB_PolyLine> polyItr = polylines.iterator();
		while (polyItr.hasNext()) {
			drawPolylineVertices(polyItr.next(), d);
		}

	}

	/**
	 * Draw polyline vertices.
	 * 
	 * @param polyline
	 *            the polyline
	 * @param d
	 *            the d
	 */
	public void drawPolylineVertices(final WB_PolyLine P, final double d) {
		WB_Point v1;
		for (int i = 0; i < P.getNumberOfPoints(); i++) {
			v1 = P.getPoint(i);
			_home.pushMatrix();
			_home.translate(v1.xf(), v1.yf(), v1.zf());
			_home.box((float) d);
			_home.popMatrix();

		}

	}

	/**
	 * Draw triangle edges.
	 * 
	 * @param triangles
	 *            the triangles
	 */
	public void drawTriangleEdges(final Collection<Triangle> triangles) {
		final Iterator<Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangleEdges(triItr.next());
		}

	}

	/**
	 * Draw triangle edges.
	 * 
	 * @param triangle
	 *            the triangle
	 */
	public void drawTriangleEdges(final Triangle triangle) {

		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
				triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
		_home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf(),
				triangle.p2().xf(), triangle.p2().yf(), triangle.p2().zf());
		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf(),
				triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());

	}

	/**
	 * Draw triangle.
	 * 
	 * @param triangles
	 *            the triangles
	 */
	public void drawTriangle(final Collection<Triangle> triangles) {

		final Iterator<Triangle> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle(triItr.next());
		}

	}

	/**
	 * Draw triangle.
	 * 
	 * @param triangle
	 *            the triangle
	 */
	public void drawTriangle(final Triangle triangle) {
		_home.beginShape();
		_home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
		_home.vertex(triangle.p3().xf(), triangle.p3().yf(), triangle.p3().zf());
		_home.vertex(triangle.p1().xf(), triangle.p1().yf(), triangle.p1().zf());
		_home.endShape();
	}

	/**
	 * Draw polygon2 d edges.
	 * 
	 * @param polygons
	 *            the polygons
	 */
	public void drawPolygon2DEdges(final Collection<WB_SimplePolygon2D> polygons) {
		final Iterator<WB_SimplePolygon2D> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2DEdges(polyItr.next());
		}

	}

	/**
	 * Draw polygon2 d edges.
	 * 
	 * @param polygon
	 *            the polygon
	 */
	public void drawPolygon2DEdges(final WB_SimplePolygon2D polygon) {
		WB_Point v1, v2;
		final int n = polygon.n;
		for (int i = 0, j = n - 1; i < n; j = i, i++) {
			v1 = polygon.points[i];
			v2 = polygon.points[j];
			_home.line(v1.xf(), v1.yf(), v2.xf(), v2.yf());
		}
	}

	/**
	 * Draw polygon2 d vertices.
	 * 
	 * @param polygons
	 *            the polygons
	 * @param d
	 *            the d
	 */
	public void drawPolygon2DVertices(
			final Collection<WB_SimplePolygon2D> polygons, final double d) {
		final Iterator<WB_SimplePolygon2D> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2DVertices(polyItr.next(), d);
		}

	}

	/**
	 * Draw polygon2 d vertices.
	 * 
	 * @param polygon
	 *            the polygon
	 * @param d
	 *            the d
	 */
	public void drawPolygon2DVertices(final WB_SimplePolygon2D polygon,
			final double d) {
		WB_Point v1;
		final int n = polygon.n;
		for (int i = 0; i < n; i++) {
			v1 = polygon.points[i];

			_home.ellipse(v1.xf(), v1.yf(), (float) d, (float) d);

		}
	}

	/**
	 * Draw polygon2 d.
	 * 
	 * @param polygons
	 *            the polygons
	 */
	public void drawPolygon2D(final Collection<WB_SimplePolygon2D> polygons) {
		final Iterator<WB_SimplePolygon2D> polyItr = polygons.iterator();
		while (polyItr.hasNext()) {
			drawPolygon2D(polyItr.next());
		}

	}

	/**
	 * Draw polygon2 d.
	 * 
	 * @param polygon
	 *            the polygon
	 */
	public void drawPolygon2D(final WB_SimplePolygon2D polygon) {
		WB_Point v1;
		final int n = polygon.n;
		_home.beginShape(PConstants.POLYGON);
		for (int i = 0; i < n; i++) {
			v1 = polygon.points[i];
			_home.vertex(v1.xf(), v1.yf());

		}
		_home.endShape(PConstants.CLOSE);
	}

	/**
	 * Draw triangle2 d edges.
	 * 
	 * @param triangles
	 *            the triangles
	 */
	public void drawTriangle2DEdges(final Collection<WB_Triangle2D> triangles) {
		final Iterator<WB_Triangle2D> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle2DEdges(triItr.next());
		}

	}

	/**
	 * Draw triangle2 d edges.
	 * 
	 * @param triangles
	 *            the triangles
	 */
	public void drawTriangle2DEdges(final WB_Triangle2D[] triangles) {

		for (final WB_Triangle2D triangle : triangles) {
			drawTriangle2DEdges(triangle);
		}

	}

	/**
	 * Draw triangle2 d edges.
	 * 
	 * @param triangle
	 *            the triangle
	 */
	public void drawTriangle2DEdges(final WB_Triangle2D triangle) {

		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p2().xf(),
				triangle.p2().yf());
		_home.line(triangle.p3().xf(), triangle.p3().yf(), triangle.p2().xf(),
				triangle.p2().yf());
		_home.line(triangle.p1().xf(), triangle.p1().yf(), triangle.p3().xf(),
				triangle.p3().yf());

	}

	/**
	 * Draw triangle2 d.
	 * 
	 * @param triangles
	 *            the triangles
	 */
	public void drawTriangle2D(final Collection<WB_Triangle2D> triangles) {

		final Iterator<WB_Triangle2D> triItr = triangles.iterator();
		while (triItr.hasNext()) {
			drawTriangle2D(triItr.next());
		}

	}

	/**
	 * Draw triangle2 d.
	 * 
	 * @param triangles
	 *            the triangles
	 */
	public void drawTriangle2D(final WB_Triangle2D[] triangles) {

		for (final WB_Triangle2D triangle : triangles) {
			drawTriangle2D(triangle);
		}

	}

	/**
	 * Draw triangle2 d.
	 * 
	 * @param triangle
	 *            the triangle
	 */
	public void drawTriangle2D(final WB_Triangle2D triangle) {
		_home.beginShape();
		_home.vertex(triangle.p1().xf(), triangle.p1().yf());
		_home.vertex(triangle.p2().xf(), triangle.p2().yf());
		_home.vertex(triangle.p3().xf(), triangle.p3().yf());
		_home.endShape();
	}

	/**
	 * Draw segment2 d.
	 * 
	 * @param segments
	 *            the segments
	 */
	public void drawSegment2D(final Collection<? extends Segment> segments) {
		final Iterator<? extends Segment> segItr = segments.iterator();
		while (segItr.hasNext()) {
			drawSegment2D(segItr.next());
		}

	}

	/**
	 * Draw segment2 d.
	 * 
	 * @param segments
	 *            the segments
	 */
	public void drawSegment2D(final Segment[] segments) {
		for (final Segment segment : segments) {
			drawSegment2D(segment);
		}

	}

	/**
	 * Draw segment2 d.
	 * 
	 * @param segment
	 *            the segment
	 */
	public void drawSegment2D(final Segment segment) {

		_home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
				.getEndpoint().xf(), segment.getEndpoint().yf());

	}

	/**
	 * Draw segment.
	 * 
	 * @param segments
	 *            the segments
	 */
	public void drawSegment(final Collection<? extends Segment> segments) {
		final Iterator<? extends Segment> segItr = segments.iterator();
		while (segItr.hasNext()) {
			drawSegment(segItr.next());
		}

	}

	/**
	 * Draw segment.
	 * 
	 * @param segment
	 *            the segment
	 */
	public void drawSegment(final Segment segment) {

		_home.line(segment.getOrigin().xf(), segment.getOrigin().yf(), segment
				.getOrigin().zf(), segment.getEndpoint().xf(), segment
				.getEndpoint().yf(), segment.getEndpoint().zf());

	}

	/**
	 * Draw.
	 * 
	 * @param C
	 *            the c
	 */
	public void draw(final WB_Circle C) {

		_home.ellipse(C.getCenter().xf(), C.getCenter().yf(),
				(float) (2 * C.getRadius()), (float) (2 * C.getRadius()));

	}

	/**
	 * Draw.
	 * 
	 * @param circles
	 *            the circles
	 */
	public void draw(final Collection<WB_Circle> circles) {
		final Iterator<WB_Circle> citr = circles.iterator();
		while (citr.hasNext()) {
			draw(citr.next());
		}

	}

	/**
	 * Draw.
	 * 
	 * @param L
	 *            the l
	 * @param s
	 *            the s
	 */
	public void draw(final WB_Line2D L, final double s) {

		_home.line(L.getOrigin().xf() - (float) (s * L.getDirection().x), L
				.getOrigin().yf() - (float) (s * L.getDirection().y), L
				.getOrigin().xf() + (float) (s * L.getDirection().x), L
				.getOrigin().yf() + (float) (s * L.getDirection().y));

	}

	/**
	 * Draw.
	 * 
	 * @param R
	 *            the r
	 * @param s
	 *            the s
	 */
	public void draw(final WB_Ray R, final double s) {

		_home.line(R.getOrigin().xf(), R.getOrigin().yf(), R.getOrigin().zf(),
				R.getOrigin().xf() + (float) (s * R.getDirection().x), R
						.getOrigin().yf() + (float) (s * R.getDirection().y), R
						.getOrigin().zf() + (float) (s * R.getDirection().z));

	}

	/**
	 * Draw.
	 * 
	 * @param C
	 *            the c
	 * @param steps
	 *            the steps
	 */
	public void draw(final WB_Curve C, final int steps) {
		final int n = Math.max(1, steps);
		WB_Point p0 = C.curvePoint(0);
		WB_Point p1;
		final double du = 1.0 / n;
		for (int i = 0; i < n; i++) {
			p1 = C.curvePoint((i + 1) * du);
			_home.line(p0.xf(), p0.yf(), p0.zf(), p1.xf(), p1.yf(), p1.zf());
			p0 = p1;
		}

	}

	/**
	 * Draw.
	 * 
	 * @param curves
	 *            the curves
	 * @param steps
	 *            the steps
	 */
	public void draw(final Collection<WB_Curve> curves, final int steps) {
		final Iterator<WB_Curve> citr = curves.iterator();
		while (citr.hasNext()) {
			draw(citr.next(), steps);
		}
	}

	/**
	 * Draw2 d.
	 * 
	 * @param C
	 *            the c
	 * @param steps
	 *            the steps
	 */
	public void draw2D(final WB_Curve C, final int steps) {
		final int n = Math.max(1, steps);
		WB_Point p0 = C.curvePoint(0);
		WB_Point p1;
		final double du = 1.0 / n;
		for (int i = 0; i < n; i++) {
			p1 = C.curvePoint((i + 1) * du);
			_home.line(p0.xf(), p0.yf(), p1.xf(), p1.yf());
			p0 = p1;
		}

	}

	/**
	 * Draw2 d.
	 * 
	 * @param curves
	 *            the curves
	 * @param steps
	 *            the steps
	 */
	public void draw2D(final Collection<WB_Curve> curves, final int steps) {
		final Iterator<WB_Curve> citr = curves.iterator();
		while (citr.hasNext()) {
			draw2D(citr.next(), steps);
		}
	}

	/**
	 * Draw.
	 * 
	 * @param frame
	 *            the frame
	 */
	public void draw(final WB_Frame frame) {
		final ArrayList<WB_FrameStrut> struts = frame.getStruts();
		for (int i = 0; i < frame.getNumberOfStruts(); i++) {
			draw(struts.get(i));
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
			draw(nodes.get(i), s);
		}
	}

	/**
	 * Draw.
	 * 
	 * @param strut
	 *            the strut
	 */
	public void draw(final WB_FrameStrut strut) {
		_home.line(strut.start().xf(), strut.start().yf(), strut.start().zf(),
				strut.end().xf(), strut.end().yf(), strut.end().zf());
	}

	/**
	 * Draw.
	 * 
	 * @param node
	 *            the node
	 * @param s
	 *            the s
	 */
	public void draw(final WB_FrameNode node, final double s) {
		_home.pushMatrix();
		_home.translate(node.xf(), node.yf(), node.zf());
		_home.box((float) s);
		_home.popMatrix();
	}

	/**
	 * Get calling applet.
	 * 
	 * @return home applet
	 */
	public PApplet home() {
		return _home;
	}

	/**
	 * Draw.
	 * 
	 * @param p
	 *            the p
	 * @param q
	 *            the q
	 */
	public void draw(final WB_Coordinate p, final WB_Coordinate q) {
		_home.line(p.xf(), p.yf(), p.zf(), q.xf(), q.yf(), q.zf());
	}

	/**
	 * Draw.
	 * 
	 * @param p
	 *            the p
	 * @param v
	 *            the v
	 * @param d
	 *            the d
	 */
	public void draw(final WB_Coordinate p, final WB_Vector v, final double d) {
		_home.line(p.xf(), p.yf(), p.zf(), p.xf() + (float) d * v.xf(), p.yf()
				+ (float) d * v.yf(), p.zf() + (float) d * v.zf());
	}

	/**
	 * Draw.
	 * 
	 * @param P
	 *            the p
	 * @param W
	 *            the w
	 */
	public void draw(final WB_Plane P, final double W) {
		final double hw = 0.5 * W;
		WB_Point p = P.extractPoint(-hw, -hw);
		_home.beginShape(PConstants.QUAD);
		_home.vertex(p.xf(), p.yf(), p.zf());
		p = P.extractPoint(hw, -hw);
		_home.vertex(p.xf(), p.yf(), p.zf());
		p = P.extractPoint(hw, hw);
		_home.vertex(p.xf(), p.yf(), p.zf());
		p = P.extractPoint(-hw, hw);
		_home.vertex(p.xf(), p.yf(), p.zf());
		_home.endShape();
	}

	/**
	 * Draw.
	 * 
	 * @param L
	 *            the l
	 * @param W
	 *            the w
	 */
	public void draw(final WB_Line L, final double W) {
		final double hw = 0.5 * W;
		final WB_Point p1 = L.getPoint(-hw);
		final WB_Point p2 = L.getPoint(hw);
		_home.line(p1.xf(), p1.yf(), p1.zf(), p2.xf(), p2.yf(), p2.zf());
	}

	/**
	 * Draw.
	 * 
	 * @param AABB
	 *            the aabb
	 */
	public void draw(final WB_AABB AABB) {
		_home.pushMatrix();
		_home.translate(AABB.getCenter().xf(), AABB.getCenter().yf(), AABB
				.getCenter().zf());
		_home.box((float) AABB.getWidth(), (float) AABB.getHeight(),
				(float) AABB.getDepth());
		_home.popMatrix();
	}

	/**
	 * Draw.
	 * 
	 * @param tree
	 *            the tree
	 */
	public void draw(final WB_AABBTree tree) {
		drawNode(tree.getRoot());

	}

	/**
	 * Draw.
	 * 
	 * @param tree
	 *            the tree
	 * @param level
	 *            the level
	 */
	public void draw(final WB_AABBTree tree, final int level) {
		drawNode(tree.getRoot(), level);

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
	 * Draw.
	 * 
	 * @param node
	 *            the node
	 */
	public void draw(final WB_AABBNode node) {
		draw(node.getAABB());
	}

	/**
	 * Draw node.
	 * 
	 * @param node
	 *            the node
	 */
	private void drawNode(final WB_AABBNode node) {
		draw(node.getAABB());

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
			draw(node.getAABB());
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
	 * Draw leaf node.
	 * 
	 * @param node
	 *            the node
	 */
	private void drawLeafNode(final WB_AABBNode node) {
		if (node.isLeaf()) {
			draw(node.getAABB());
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
	 * Draw.
	 * 
	 * @param point
	 *            the point
	 * @param d
	 *            the d
	 */
	public void draw(final WB_Coordinate point, final double d) {
		_home.pushMatrix();
		_home.translate(point.xf(), point.yf(), point.zf());
		_home.box((float) d);
		_home.popMatrix();
	}

	/**
	 * Draw.
	 * 
	 * @param points
	 *            the points
	 * @param d
	 *            the d
	 */
	public void draw(final Collection<? extends WB_Coordinate> points,
			final double d) {
		for (final WB_Coordinate point : points) {
			_home.pushMatrix();
			_home.translate(point.xf(), point.yf(), point.zf());
			_home.box((float) d);
			_home.popMatrix();
		}
	}

	private void drawPolygon(final int[] indices,
			final WB_CoordinateSequence points) {
		if (points != null && indices != null) {
			_home.beginShape(PApplet.POLYGON);
			for (final int indice : indices) {
				_home.vertex((float) points.get(indice, 0),
						(float) points.get(indice, 1),
						(float) points.get(indice, 2));
			}
			_home.endShape(PApplet.CLOSE);
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
}
