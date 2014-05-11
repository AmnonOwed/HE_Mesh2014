package wblut.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.opengl.PGraphics3D;
import wblut.geom.WB_Convex;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_IndexedTriangle2D;
import wblut.geom.WB_Point;
import wblut.hemesh.HE_Edge;
import wblut.hemesh.HE_Face;
import wblut.hemesh.HE_MeshStructure;
import wblut.hemesh.HE_Vertex;

public class WB_SelectRender3D {
	private final PApplet home;
	private PGraphics3D selector;
	private int[] samples;
	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	/** The current_color. */
	protected int currentColor;

	/** The color to object. */
	protected HashMap<Integer, Long> colorToObject;
	private double scale;

	public WB_SelectRender3D(final PApplet home) {
		scale = 1;// doesn't work yet
		selector = (PGraphics3D) home.createGraphics(
				(int) (home.width * scale), (int) (home.height * scale),
				home.P3D);
		selector.beginDraw();
		selector.background(0);
		selector.noLights();
		selector.endDraw();
		this.home = home;
		colorToObject = new HashMap<Integer, Long>();
		currentColor = -16777216;
		samples = new int[5];

	}

	private void drawConcaveFace(final HE_Face f) {

		final List<WB_IndexedTriangle2D> tris = f.triangulate();
		final List<HE_Vertex> vertices = f.getFaceVertices();
		WB_Point v0, v1, v2;
		WB_IndexedTriangle2D tri;
		for (int i = 0; i < tris.size(); i++) {
			tri = tris.get(i);
			selector.beginShape(PConstants.TRIANGLES);

			v0 = vertices.get(tri.i1).pos;
			v1 = vertices.get(tri.i2).pos;
			v2 = vertices.get(tri.i3).pos;

			selector.vertex(v0.xf(), v0.yf(), v0.zf());

			selector.vertex(v1.xf(), v1.yf(), v1.zf());

			selector.vertex(v2.xf(), v2.yf(), v2.zf());
			selector.endShape();

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
			final HE_MeshStructure mesh) {
		final int degree = vertices.size();
		if (degree < 3) {
			// yeah, right...
		} else if (degree == 3) {

			selector.beginShape(PConstants.TRIANGLES);
			final HE_Vertex v0 = vertices.get(0);
			final HE_Vertex v1 = vertices.get(1);

			final HE_Vertex v2 = vertices.get(2);

			selector.vertex(v0.xf(), v0.yf(), v0.zf());

			selector.vertex(v1.xf(), v1.yf(), v1.zf());

			selector.vertex(v2.xf(), v2.yf(), v2.zf());
			selector.endShape();

		} else if (degree == 4) {

			final HE_Vertex v0 = vertices.get(0);
			final HE_Vertex v1 = vertices.get(1);
			final HE_Vertex v2 = vertices.get(2);
			final HE_Vertex v3 = vertices.get(3);

			selector.beginShape(PConstants.TRIANGLES);
			selector.vertex(v0.xf(), v0.yf(), v0.zf());
			selector.vertex(v1.xf(), v1.yf(), v1.zf());
			selector.vertex(v2.xf(), v2.yf(), v2.zf());
			selector.vertex(v0.xf(), v0.yf(), v0.zf());
			selector.vertex(v2.xf(), v2.yf(), v2.zf());
			selector.vertex(v3.xf(), v3.yf(), v3.zf());

			selector.endShape();

		} else if (degree == 5) {

			final HE_Vertex v0 = vertices.get(0);
			final HE_Vertex v1 = vertices.get(1);
			final HE_Vertex v2 = vertices.get(2);
			final HE_Vertex v3 = vertices.get(3);
			final HE_Vertex v4 = vertices.get(4);
			selector.beginShape(PConstants.TRIANGLES);
			selector.vertex(v0.xf(), v0.yf(), v0.zf());
			selector.vertex(v1.xf(), v1.yf(), v1.zf());
			selector.vertex(v2.xf(), v2.yf(), v2.zf());
			selector.vertex(v0.xf(), v0.yf(), v0.zf());
			selector.vertex(v2.xf(), v2.yf(), v2.zf());
			selector.vertex(v3.xf(), v3.yf(), v3.zf());
			selector.vertex(v0.xf(), v0.yf(), v0.zf());
			selector.vertex(v3.xf(), v3.yf(), v3.zf());
			selector.vertex(v4.xf(), v4.yf(), v4.zf());
			selector.endShape();

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
			drawConvexShapeFromVertices(subset, mesh);
			drawConvexShapeFromVertices(vertices, mesh);
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
				drawConvexShapeFromVertices(tmpVertices, null);
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
	public void drawFaces(final HE_MeshStructure mesh) {
		selector.beginDraw();
		selector.setMatrix(home.getMatrix());
		selector.scale((float) scale);
		selector.clear();
		final Iterator<HE_Face> fItr = mesh.fItr();
		HE_Face f;
		selector.strokeWeight(1.0f);
		while (fItr.hasNext()) {
			f = fItr.next();
			setKey(f.key());
			drawFace(f);
		}

		selector.endDraw();

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

	public void drawEdges(final HE_MeshStructure mesh, double d) {
		selector.beginDraw();
		selector.setMatrix(home.getMatrix());
		selector.scale((float) scale);
		selector.clear();
		selector.strokeWeight((float) d);
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;

		while (eItr.hasNext()) {
			e = eItr.next();
			setKey(e.key());
			selector.line(e.getStartVertex().xf(), e.getStartVertex().yf(), e
					.getStartVertex().zf(), e.getEndVertex().xf(), e
					.getEndVertex().yf(), e.getEndVertex().zf());

		}
		selector.endDraw();
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
	public void drawVertices(final HE_MeshStructure mesh, final double d) {
		selector.beginDraw();
		selector.setMatrix(home.getMatrix());
		selector.scale((float) scale);
		selector.clear();
		final Iterator<HE_Vertex> vItr = mesh.vItr();
		HE_Vertex v;
		selector.strokeWeight(1.0f);
		while (vItr.hasNext()) {
			v = vItr.next();
			setKey(v.key());
			selector.pushMatrix();
			selector.translate(v.xf(), v.yf(), v.zf());
			selector.box((float) d);
			selector.popMatrix();
		}
		selector.endDraw();
	}

	public long getKeyAA(final int x, final int y) {

		int locx = (int) (x * scale);
		int locy = (int) (y * scale);
		selector.loadPixels();
		// COLOR -16777216 (black) to -1 => ID -1 (no object) to 16777214
		samples[0] = selector.pixels[locy * selector.width + locx];

		int lx = (locx <= 0) ? 0 : locx - 1;
		int ly = (locy <= 0) ? 0 : locy - 1;
		int ux = (locx >= selector.width - 1) ? locx : locx + 1;
		int uy = (locy >= selector.height - 1) ? locy : locy + 1;
		samples[1] = selector.pixels[ly * selector.width + lx];
		if (samples[0] != samples[1])
			return -1;
		samples[2] = selector.pixels[ly * selector.width + ux];
		if (samples[0] != samples[2])
			return -1;
		samples[3] = selector.pixels[uy * selector.width + lx];
		if (samples[0] != samples[3])
			return -1;
		samples[4] = selector.pixels[uy * selector.width + ux];
		if (samples[0] != samples[4])
			return -1;
		Long selection = colorToObject.get(samples[0]);
		return (selection == null) ? -1 : selection;

	}

	public long getKey(final int x, final int y) {

		int locx = (int) (x * scale);
		int locy = (int) (y * scale);
		selector.loadPixels();
		// COLOR -16777216 (black) to -1 => ID -1 (no object) to 16777214
		samples[0] = selector.pixels[locy * selector.width + locx];

		Long selection = colorToObject.get(samples[0]);
		return (selection == null) ? -1 : selection;

	}

	public long getKey() {
		return getKey(home.mouseX, home.mouseY);
	}

	public long getKeyAA() {
		return getKeyAA(home.mouseX, home.mouseY);
	}

	/**
	 * Set the key.
	 * 
	 * @param i
	 *            new key
	 */
	private void setKey(final Long i) {
		if (i < 0 || i > 16777214) {
			PApplet.println("[HE_Selector error] setKey(): ID out of range");
			return;
		}
		// ID 0 to 16777214 => COLOR -16777215 to -1 (white)
		// -16777216 is black
		currentColor++;
		colorToObject.put(currentColor, i);
		selector.fill(currentColor);
		selector.stroke(currentColor);

	}

	private void clear() {
		selector.background(home.color(0));
		colorToObject = new HashMap<Integer, Long>();
		currentColor = -16777216;
	}

	public void image() {
		home.image(selector, 0, 0);

	}
}
