package wblut.geom;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javolution.util.FastList;

public class WB_Mesh implements WB_Geometry {
	protected int[][] faces;
	/** points of line. */
	protected WB_PointSequence vertices;
	protected WB_AABB aabb;

	Object[] vertexNormals = null;
	Object[] faceNormals = null;
	int[][] vvNeighbors = null;
	int[][] vfNeighbors = null;
	int[][] ffNeighbors = null;
	boolean vNormalsUpdated, fNormalsUpdated, vvNeighborsUpdated,
			vfNeighborsUpdated, ffNeighborsUpdated;

	List<int[]> tris;

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_Mesh() {

	}

	protected WB_Mesh(final WB_PointSequence points, final int[][] faces) {

		vertices = geometryfactory.createPointSequence(points);
		this.faces = new int[faces.length][];
		int i = 0;
		for (final int[] face : faces) {
			this.faces[i] = new int[face.length];
			for (int j = 0; j < face.length; j++) {
				this.faces[i][j] = face[j];
			}
			i++;
		}

	}

	protected WB_Mesh(final Collection<? extends WB_Coordinate> points,
			final int[][] faces) {

		vertices = geometryfactory.createPointSequence(points);
		this.faces = new int[faces.length][];
		int i = 0;
		for (final int[] face : faces) {
			this.faces[i] = new int[face.length];
			for (int j = 0; j < face.length; j++) {
				this.faces[i][j] = face[j];
			}
			i++;
		}

	}

	protected WB_Mesh(final WB_Coordinate[] points, final int[][] faces) {

		vertices = geometryfactory.createPointSequence(points);
		this.faces = new int[faces.length][];
		int i = 0;
		for (final int[] face : faces) {
			this.faces[i] = new int[face.length];
			for (int j = 0; j < face.length; j++) {
				this.faces[i][j] = face[j];
			}
			i++;
		}

	}

	protected WB_Mesh(final double[] points, final int[][] faces) {

		vertices = geometryfactory.createPointSequence(points);
		this.faces = new int[faces.length][];
		int i = 0;
		for (final int[] face : faces) {
			this.faces[i] = new int[face.length];
			for (int j = 0; j < face.length; j++) {
				this.faces[i][j] = face[j];
			}
			i++;
		}

	}

	public int[][] getFaces() {
		return faces;
	}

	public int[][] getEdges() {
		if (faces == null) {
			return null;
		}
		int[] face;
		int noe = 0;

		for (final int[] face2 : faces) {
			face = face2;
			noe += face.length;
		}
		noe /= 2;
		final int[][] edges = new int[noe][2];
		int id = 0;
		for (final int[] face2 : faces) {
			face = face2;
			final int fl = face.length;
			for (int j = 0; j < fl; j++) {
				if (face[j] < face[(j + 1) % fl]) {
					edges[id][0] = face[j];
					edges[id++][1] = face[(j + 1) % fl];
				}
			}
		}
		return edges;

	}

	public WB_Plane getPlane(final int id, final double d) {
		final int[] face = getFace(id);
		final WB_Vector normal = geometryfactory.createVector();
		final WB_Point center = geometryfactory.createPoint();
		WB_Vector tmp;
		WB_Point p0;
		WB_Point p1;
		for (int i = 0, j = face.length - 1; i < face.length; j = i, i++) {
			p0 = vertices.getCoordinate(face[j]);
			p1 = vertices.getCoordinate(face[i]);
			center._addSelf(p1);
			tmp = geometryfactory.createVector((p0.yd() - p1.yd())
					* (p0.zd() + p1.zd()),
					(p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
					(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
			normal._addSelf(tmp);

		}
		normal._normalizeSelf();
		center._divSelf(face.length);
		// System.out.println(center + " " + normal);
		return geometryfactory.createPlane(center.addMul(d, normal), normal);

	}

	public WB_Plane getPlane(final int id) {
		return getPlane(id, 0);
	}

	public List<WB_Plane> getPlanes(final double d) {
		final List<WB_Plane> planes = new FastList<WB_Plane>(faces.length);
		for (int i = 0; i < faces.length; i++) {
			planes.add(getPlane(i, d));
		}
		return planes;

	}

	public List<WB_Plane> getPlanes() {
		return getPlanes(0);

	}

	public WB_Polygon getPolygon(final int id) {
		return geometryfactory.createSimplePolygon(vertices
				.getSubSequence(faces[id]));
	}

	public List<WB_Polygon> getPolygons() {
		final List<WB_Polygon> polygons = new FastList<WB_Polygon>(faces.length);
		for (int i = 0; i < faces.length; i++) {
			polygons.add(getPolygon(i));
		}
		return polygons;
	}

	public WB_Point getCenter() {
		double cx = 0;
		double cy = 0;
		double cz = 0;
		for (int i = 0; i < vertices.size(); i++) {
			cx += vertices.get(i, 0);
			cy += vertices.get(i, 1);
			cz += vertices.get(i, 2);
		}
		cx /= vertices.size();
		cy /= vertices.size();
		cz /= vertices.size();
		return geometryfactory.createPoint(cx, cy, cz);

	}

	public WB_AABB getAABB() {
		return new WB_AABB(vertices);
	}

	public WB_Mesh isoFitInAABB(final WB_AABB AABB) {
		final WB_AABB self = getAABB();
		final double scx = self.getCenterX();
		final double acx = AABB.getCenterX();
		final double scy = self.getCenterY();
		final double acy = AABB.getCenterY();
		final double scz = self.getCenterZ();
		final double acz = AABB.getCenterZ();

		double f = Math.min(AABB.getWidth() / self.getWidth(), AABB.getHeight()
				/ self.getHeight());
		f = Math.min(f, AABB.getDepth() / self.getDepth());
		final WB_Vector center = geometryfactory.createVector(acx, acy, acz);
		final List rescaled = new FastList(vertices.size());
		for (int i = 0; i < vertices.size(); i++) {
			final WB_Point p = vertices.getCoordinate(i);
			p._addSelf(-scx, -scy, -scz);
			p._mulSelf(f);
			p._addSelf(acx, acy, acz);
			rescaled.add(p);
		}

		return geometryfactory.createMesh(rescaled, faces);
	}

	public WB_Mesh triangulate() {

		return triangulateST();

	}

	private WB_Mesh triangulateST() {
		tris = new FastList<int[]>();
		int[] face;
		int[][] triangles;
		int id = 0;
		for (final int[] face2 : faces) {
			face = face2;
			if (face.length == 3) {
				addTriangle(face);
			} else if (face.length == 4) {
				addTriangle(new int[] { face[0], face[1], face[2] });
				addTriangle(new int[] { face[0], face[2], face[3] });
			} else {
				triangles = WB_Triangulate.getPolygonTriangulation2D(face,
						vertices, true,
						geometryfactory.createEmbeddedPlane(getPlane(id)))
						.getTriangles();
				for (final int[] triangle : triangles) {
					addTriangle(triangle);
				}

			}
			id++;

		}
		faces = new int[tris.size()][3];
		int i = 0;
		for (final int[] tri : tris) {
			faces[i++] = tri;
		}
		return this;

	}

	private WB_Mesh triangulateMT() {
		tris = Collections.synchronizedList(new FastList<int[]>());
		final int threadCount = Runtime.getRuntime().availableProcessors();
		final int dfaces = faces.length / threadCount;
		final ExecutorService executor = Executors
				.newFixedThreadPool(threadCount);
		int i = 0;
		for (i = 0; i < threadCount - 1; i++) {

			final Runnable runner = new TriangulateRunner(dfaces * i, dfaces
					* (i + 1) - 1);
			executor.submit(runner);
		}

		final Runnable runner = new TriangulateRunner(dfaces * i,
				faces.length - 1);
		executor.submit(runner);
		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}
		faces = new int[tris.size()][3];
		i = 0;
		for (final int[] tri : tris) {
			faces[i++] = tri;
		}
		return this;

	}

	class TriangulateRunner implements Runnable {
		int start;
		int end;
		int[][] triangles;

		TriangulateRunner(final int s, final int e) {
			start = s;
			end = e;

		}

		@Override
		public void run() {
			int[] face;

			for (int i = start; i <= end; i++) {
				face = faces[i];
				if (face.length == 3) {
					addTriangle(face);
				} else if (face.length == 4) {
					addTriangle(new int[] { face[0], face[1], face[2] });
					addTriangle(new int[] { face[0], face[2], face[3] });
				} else {
					triangles = WB_Triangulate.getPolygonTriangulation2D(face,
							vertices, true,
							geometryfactory.createEmbeddedPlane(getPlane(i)))
							.getTriangles();
					for (final int[] triangle : triangles) {
						addTriangle(triangle);
					}

				}

			}

		}
	}

	synchronized void addTriangle(final int[] tri) {

		tris.add(tri);
	}

	public WB_Vector getFaceNormal(final int id) {
		if (!fNormalsUpdated) {
			updateFaceNormalsST();
		}
		return (WB_Vector) faceNormals[id];
	}

	public WB_Point getFaceCenter(final int id) {
		final WB_Point c = geometryfactory.createPoint();
		for (int i = 0; i < faces[id].length; i++) {
			c._addSelf(getVertex(faces[id][i]));

		}
		c._divSelf(faces[id].length);
		return c;
	}

	public WB_Vector getVertexNormal(final int i) {
		if (!vNormalsUpdated) {
			updateVertexNormals();
		}
		return (WB_Vector) vertexNormals[i];
	}

	public int getNumberOfFaces() {
		return faces.length;
	}

	public int getNumberOfVertices() {
		return vertices.size();
	}

	public WB_Point getVertex(final int i) {
		return vertices.getCoordinate(i);
	}

	public WB_PointSequence getVertices() {
		return vertices;
	}

	private void updatevfNeighbors() {

		if (vfNeighborsUpdated) {
			return;
		}
		final int nv = vertices.size();
		final int nf = faces.length;
		final int[] numadjacentfaces = new int[nv];
		for (final int[] face : faces) {
			for (int i = 0; i < face.length; i++) {
				numadjacentfaces[face[i]]++;
			}

		}
		vfNeighbors = new int[nv][];
		for (int i = 0; i < nv; i++) {
			vfNeighbors[i] = new int[numadjacentfaces[i]];
			for (int j = 0; j < numadjacentfaces[i]; j++) {
				vfNeighbors[i][j] = -1;
			}
		}
		for (int i = 0; i < nf; i++) {
			final int[] face = faces[i];
			for (int j = 0; j < face.length; j++) {
				int counter = 0;
				while (vfNeighbors[face[j]][counter] != -1) {
					counter++;
				}
				vfNeighbors[face[j]][counter] = i;
			}
		}
		vfNeighborsUpdated = true;
		/*
		 * updateffNeighbors();
		 * 
		 * for (int i = 0; i < nv; i++) { if (vfNeighbors[i].length == 0)
		 * continue; int f = vfNeighbors[i][0]; int fPrev = prevFace(i, f);
		 * while (fPrev >= 0 && fPrev != vfNeighbors[i][0]) { f = fPrev; fPrev =
		 * prevFace(i, f); } int counter = 0; int fStart = f; do {
		 * vfNeighbors[i][counter++] = f; f = nextFace(i, f); } while (f >= 0 &&
		 * f != fStart);
		 * 
		 * }
		 */

	}

	private void updateVertexNormals() {
		updateVertexNormalsAngle();
	}

	/**
	 * The normal of a vertex v computed as a weighted sum f the incident face
	 * normals. The weight is simply the angle of the involved wedge. Described
	 * in:
	 * 
	 * G. Thurmer, C. A. Wuthrich
	 * "Computing vertex normals from polygonal facets" Journal of Graphics
	 * Tools, 1998
	 */
	private void updateVertexNormalsAngle() {
		final int nv = vertices.size();
		if (vNormalsUpdated) {
			return;
		}
		if (!fNormalsUpdated) {
			updateFaceNormalsMT();
		}
		vertexNormals = new Object[nv];
		for (int i = 0; i < nv; i++) {
			vertexNormals[i] = geometryfactory.createVector();
		}

		final int nf = faces.length;
		int i = 0;
		WB_Point p0, p1, p2;
		for (final int[] face : faces) {
			for (int j = 0; j < face.length; j++) {
				p0 = vertices.getCoordinate(face[j]);
				p1 = vertices.getCoordinate(face[(j + 1) % face.length]);
				p2 = vertices.getCoordinate(face[(j - 1 + face.length)
						% face.length]);
				final WB_Vector P10 = geometryfactory.createNormalizedVector(
						p0, p1);
				final WB_Vector P20 = geometryfactory.createNormalizedVector(
						p0, p2);
				final double w = P10.getAngleNorm(P20);
				((WB_Vector) vertexNormals[face[j]])._addMulSelf(w,
						(WB_Vector) faceNormals[i]);
			}
			i++;

		}
		for (final Object v : vertexNormals) {
			((WB_Vector) v)._normalizeSelf();

		}
		vNormalsUpdated = true;

	}

	private void updateFaceNormalsST() {
		final int nf = faces.length;
		if (fNormalsUpdated) {
			return;
		}

		faceNormals = new Object[nf];
		WB_Point p0, p1;
		for (int i = 0; i < nf; i++) {
			final int[] face = faces[i];
			final WB_Vector tmp = geometryfactory.createVector();

			for (int j = 0, k = face.length - 1; j < face.length; k = j++) {
				p1 = getVertex(face[j]);
				p0 = getVertex(face[k]);
				final WB_Vector tmp2 = geometryfactory.createVector(
						(p0.yd() - p1.yd()) * (p0.zd() + p1.zd()),
						(p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
						(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
				tmp._addSelf(tmp2);
			}

			faceNormals[i] = tmp._normalizeSelf();
		}

		fNormalsUpdated = true;

	}

	private void updateFaceNormalsMT() {
		final int nf = faces.length;
		if (fNormalsUpdated) {
			return;
		}
		faceNormals = new Object[nf];
		final int threadCount = Runtime.getRuntime().availableProcessors();
		final int dfaces = nf / threadCount;
		final ExecutorService executor = Executors
				.newFixedThreadPool(threadCount);
		int i = 0;
		for (i = 0; i < threadCount - 1; i++) {

			final Runnable runner = new FaceNormalRunner(dfaces * i, dfaces
					* (i + 1) - 1);
			executor.submit(runner);
		}

		final Runnable runner = new FaceNormalRunner(dfaces * i,
				faces.length - 1);
		executor.submit(runner);
		executor.shutdown();

		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (final InterruptedException e) {
			e.printStackTrace();
		}

		fNormalsUpdated = true;

	}

	class FaceNormalRunner implements Runnable {
		int start;
		int end;

		FaceNormalRunner(final int s, final int e) {
			start = s;
			end = e;

		}

		@Override
		public void run() {
			WB_Point p0, p1;
			for (int i = start; i < end; i++) {
				final int[] face = faces[i];
				final WB_Vector tmp = geometryfactory.createVector();

				for (int j = 0, k = face.length - 1; j < face.length; k = j++) {
					p1 = getVertex(face[j]);
					p0 = getVertex(face[k]);
					final WB_Vector tmp2 = geometryfactory.createVector(
							(p0.yd() - p1.yd()) * (p0.zd() + p1.zd()),
							(p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
							(p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
					tmp._addSelf(tmp2);
				}

				faceNormals[i] = tmp._normalizeSelf();
			}

		}
	}

	public int[] vfNeighbors(final int i) {
		if (!vfNeighborsUpdated) {
			updatevfNeighbors();
		}
		return vfNeighbors[i];
	}

	public int[] getFace(final int i) {
		return faces[i];
	}

	@Override
	public WB_Mesh apply(final WB_Transform WB_Point) {
		final FastList newvertices = new FastList();
		int id = 0;
		WB_Point point;
		for (int i = 0; i < vertices.size(); i++) {
			point = geometryfactory.createPoint();
			WB_Point.applyAsPoint(vertices.getRaw(id++), vertices.getRaw(id++),
					vertices.getRaw(id++), point);
			newvertices.add(point);
		}
		return geometryfactory.createMesh(newvertices, faces);
	}

	@Override
	public WB_GeometryType getType() {

		return WB_GeometryType.MESH;
	}

	@Override
	public int getDimension() {

		return 2;
	}

	@Override
	public int getEmbeddingDimension() {

		return 3;
	}

	public double k1(final int i) {

		return 0;
	}

	public double k2(final int i) {
		return 0;
	}

	public double K(final int i) {
		return 0;
	}

	public double k1min() {

		return 0;
	}

	public double k2min() {
		return 0;
	}

	public double Kmin() {
		return 0;
	}

	public double k1max() {

		return 0;
	}

	public double k2max() {
		return 0;
	}

	public double Kmax() {
		return 0;
	}

	public WB_Vector k1dir(final int i) {
		return null;
	}

	public WB_Vector k2dir(final int i) {
		return null;
	}

	public double[] DCurv(final int i) {
		return null;
	}

	public double DCurvInvariant(final int i) {
		return 0;
	}

}
