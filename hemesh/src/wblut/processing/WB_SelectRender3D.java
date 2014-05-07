package wblut.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import wblut.geom.WB_Convex;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.hemesh.HET_Selector;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Vertex;

public class WB_SelectRender3D {
	private final PApplet home;
	private HET_Selector selector;
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public WB_SelectRender3D(final HET_Selector selector) {
		this.selector = selector;
		this.home = selector.home();
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

	/**
	 * Draw one face.
	 * 
	 * @param f
	 *            face
	 */
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

	// RENDER

	/**
	 * Draw mesh faces. Typically used with noStroke();
	 * 
	 * @param selector
	 *            selector tool
	 * @param mesh
	 *            the mesh
	 * @return key of face at mouse position
	 */
	public Long drawFaces(final HE_MeshStructure mesh) {
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
		return selector.get();
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

	public Long drawEdges(final HE_MeshStructure mesh) {
		selector.clear();
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		while (eItr.hasNext()) {
			e = eItr.next();
			selector.start(e.key());
			home.beginShape();
			home.vertex(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf());
			home.vertex(e.getEndVertex().xf(), e.getEndVertex().yf(), e
					.getEndVertex().zf());
			home.endShape();

		}

		selector.stop();
		return selector.get();
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
	public Long drawVertices(final HE_MeshStructure mesh, final double d) {
		selector.clear();
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		while (vItr.hasNext()) {

			v = vItr.next();
			selector.start(v.key());
			home.pushMatrix();
			home.translate(v.xf(), v.yf(), v.zf());
			home.box((float) d);
			home.popMatrix();

		}
		selector.stop();
		return selector.get();
	}

}
