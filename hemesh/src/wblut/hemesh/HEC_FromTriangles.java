package wblut.hemesh;

import java.util.Collection;

import javolution.util.FastTable;
import wblut.geom.WB_Point;
import wblut.geom.interfaces.Triangle;

/**
 * Creates a new mesh from a list of triangles. Duplicate vertices are fused.
 *
 * @param <T>
 *            the generic type
 * @author Frederik Vanhoutte (W:Blut)
 */
public class HEC_FromTriangles extends HEC_Creator {

	/** Source triangles. */
	FastTable<Triangle> triangles;

	/**
	 * Instantiates a new HEC_FromTriangles.
	 *
	 */
	public HEC_FromTriangles() {
		super();
		override = true;
	}

	/**
	 * Sets the source triangles.
	 *
	 * @param ts
	 *            source triangles
	 * @return self
	 */
	public HEC_FromTriangles setTriangles(final Triangle[] ts) {
		triangles = new FastTable<Triangle>();
		for (final Triangle tri : ts) {
			triangles.add(tri);
		}
		return this;
	}

	/**
	 * Sets the source triangles.
	 *
	 * @param ts
	 *            source triangles
	 * @return self
	 */
	public HEC_FromTriangles setTriangles(
			final Collection<? extends Triangle> ts) {

		triangles = new FastTable<Triangle>();
		triangles.addAll(ts);
		return this;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {
		if (triangles != null) {

			final WB_Point[] vertices = new WB_Point[triangles.size() * 3];
			final int[][] faces = new int[triangles.size()][3];
			for (int i = 0; i < triangles.size(); i++) {
				vertices[3 * i] = triangles.get(i).p1();
				vertices[3 * i + 1] = triangles.get(i).p2();
				vertices[3 * i + 2] = triangles.get(i).p3();

				faces[i][0] = 3 * i;
				faces[i][1] = 3 * i + 1;
				faces[i][2] = 3 * i + 2;
			}
			// System.out.println("HEC_FromTriangles: passing " +
			// triangles.size()
			// + " triangles as faces.");
			final HEC_FromFacelist ffl = new HEC_FromFacelist()
			.setVertices(vertices).setFaces(faces).setDuplicate(true);
			return ffl.createBase();
		}
		return null;
	}

}
