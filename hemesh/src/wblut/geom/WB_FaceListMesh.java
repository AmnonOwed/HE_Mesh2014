/*
 * 
 */
package wblut.geom;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javolution.util.FastTable;

/**
 * 
 */
public class WB_FaceListMesh implements WB_Mesh {
    
    /**
     * 
     */
    protected int[][] faces;
    
    /**
     * 
     */
    protected WB_CoordinateSequence vertices;
    
    /**
     * 
     */
    protected WB_AABB aabb;
    
    /**
     * 
     */
    WB_Vector[] vertexNormals = null;
    
    /**
     * 
     */
    WB_Vector[] faceNormals = null;
    
    /**
     * 
     */
    int[][] vvNeighbors = null;
    
    /**
     * 
     */
    int[][] vfNeighbors = null;
    
    /**
     * 
     */
    int[][] ffNeighbors = null;
    
    /**
     * 
     */
    boolean vNormalsUpdated, fNormalsUpdated, vvNeighborsUpdated,
    vfNeighborsUpdated, ffNeighborsUpdated;
    
    /**
     * 
     */
    List<int[]> tris;
    
    /**
     * 
     */
    WB_Vector[] pdir1 = null;
    
    /**
     * 
     */
    WB_Vector[] pdir2 = null;
    
    /**
     * 
     */
    double[] curv1 = null;
    
    /**
     * 
     */
    double[] curv2 = null;
    
    /**
     * 
     */
    double k1min;
    
    /**
     * 
     */
    double k2min;
    
    /**
     * 
     */
    double Kmin;
    
    /**
     * 
     */
    double k1max;
    
    /**
     * 
     */
    double k2max;
    
    /**
     * 
     */
    double Kmax;
    
    /**
     * 
     */
    double[][] dcurv = null;
    
    /**
     * 
     */
    double[][] cornerareas = null;
    
    /**
     * 
     */
    double[] pointareas = null;
    
    /**
     * 
     */
    boolean areasUpdated;
    
    /**
     * 
     */
    boolean curvaturesUpdated;
    
    /**
     * 
     */
    boolean DCurvaturesUpdated;
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     */
    protected WB_FaceListMesh() {
    }

    /**
     * 
     *
     * @param points 
     * @param faces 
     */
    protected WB_FaceListMesh(final WB_CoordinateSequence points,
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

    /**
     * 
     *
     * @param mesh 
     */
    protected WB_FaceListMesh(final WB_FaceListMesh mesh) {
	vertices = geometryfactory.createPointSequence(mesh.vertices);
	this.faces = new int[mesh.faces.length][];
	int i = 0;
	for (final int[] face : mesh.faces) {
	    this.faces[i] = new int[face.length];
	    for (int j = 0; j < face.length; j++) {
		this.faces[i][j] = face[j];
	    }
	    i++;
	}
    }

    /**
     * 
     *
     * @param points 
     * @param faces 
     */
    protected WB_FaceListMesh(final Collection<? extends WB_Coordinate> points,
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

    /**
     * 
     *
     * @param points 
     * @param faces 
     */
    protected WB_FaceListMesh(final WB_Coordinate[] points, final int[][] faces) {
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

    /**
     * 
     *
     * @param points 
     * @param faces 
     */
    protected WB_FaceListMesh(final double[] points, final int[][] faces) {
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

    /**
     * 
     *
     * @return 
     */
    public WB_FaceListMesh get() {
	return new WB_FaceListMesh(this);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getFacesAsInt()
     */
    @Override
    public int[][] getFacesAsInt() {
	return faces;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getEdgesAsInt()
     */
    @Override
    public int[][] getEdgesAsInt() {
	if (faces == null) {
	    return null;
	}
	int noe = 0;
	for (final int[] f : faces) {
	    noe += f.length;
	}
	noe /= 2;
	final int[][] edges = new int[noe][2];
	int id = 0;
	for (final int[] f : faces) {
	    final int fl = f.length;
	    for (int j = 0; j < fl; j++) {
		if (f[j] < f[(j + 1) % fl]) {
		    edges[id][0] = f[j];
		    edges[id++][1] = f[(j + 1) % fl];
		}
	    }
	}
	return edges;
    }

    /**
     * 
     *
     * @param id 
     * @param d 
     * @return 
     */
    public WB_Plane getPlane(final int id, final double d) {
	final int[] face = getFace(id);
	final WB_Vector normal = geometryfactory.createVector();
	final WB_Point center = geometryfactory.createPoint();
	WB_Vector tmp;
	WB_SequencePoint p0;
	WB_SequencePoint p1;
	for (int i = 0, j = face.length - 1; i < face.length; j = i, i++) {
	    p0 = vertices.getPoint(face[j]);
	    p1 = vertices.getPoint(face[i]);
	    center.addSelf(p1);
	    tmp = geometryfactory.createVector((p0.yd() - p1.yd())
		    * (p0.zd() + p1.zd()),
		    (p0.zd() - p1.zd()) * (p0.xd() + p1.xd()),
		    (p0.xd() - p1.xd()) * (p0.yd() + p1.yd()));
	    normal.addSelf(tmp);
	}
	normal.normalizeSelf();
	center.divSelf(face.length);
	return geometryfactory.createPlane(center.addMul(d, normal), normal);
    }

    /**
     * 
     *
     * @param id 
     * @return 
     */
    public WB_Plane getPlane(final int id) {
	return getPlane(id, 0);
    }

    /**
     * 
     *
     * @param d 
     * @return 
     */
    public List<WB_Plane> getPlanes(final double d) {
	final List<WB_Plane> planes = new FastTable<WB_Plane>();
	for (int i = 0; i < faces.length; i++) {
	    planes.add(getPlane(i, d));
	}
	return planes;
    }

    /**
     * 
     *
     * @return 
     */
    public List<WB_Plane> getPlanes() {
	return getPlanes(0);
    }

    /**
     * 
     *
     * @param id 
     * @return 
     */
    public WB_Polygon getPolygon(final int id) {
	return geometryfactory.createSimplePolygon(vertices
		.getSubSequence(faces[id]));
    }

    /**
     * 
     *
     * @return 
     */
    public List<WB_Polygon> getPolygons() {
	final List<WB_Polygon> polygons = new FastTable<WB_Polygon>();
	for (int i = 0; i < faces.length; i++) {
	    polygons.add(getPolygon(i));
	}
	return polygons;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getCenter()
     */
    @Override
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

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getAABB()
     */
    @Override
    public WB_AABB getAABB() {
	return new WB_AABB(vertices);
    }

    /**
     * 
     *
     * @param AABB 
     * @return 
     */
    public WB_FaceListMesh isoFitInAABB(final WB_AABB AABB) {
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
	final List<WB_SequencePoint> rescaled = new FastTable<WB_SequencePoint>();
	for (int i = 0; i < vertices.size(); i++) {
	    final WB_SequencePoint p = vertices.getPoint(i);
	    p.addSelf(-scx, -scy, -scz);
	    p.mulSelf(f);
	    p.addSelf(acx, acy, acz);
	    rescaled.add(p);
	}
	return geometryfactory.createMesh(rescaled, faces);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_FaceListMesh triangulate() {
	return triangulateST();
    }

    /**
     * 
     *
     * @return 
     */
    private WB_FaceListMesh triangulateST() {
	tris = new FastTable<int[]>();
	int[] face;
	int[][] triangles;
	int id = 0;
	for (final int[] face2 : faces) {
	    face = face2;
	    if (face.length == 3) {
		addTriangle(face);
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

    /**
     * 
     *
     * @return 
     */
    @SuppressWarnings("unused")
    private WB_FaceListMesh triangulateMT() {
	tris = Collections.synchronizedList(new FastTable<int[]>());
	final int threadCount = Runtime.getRuntime().availableProcessors();
	final int dfaces = faces.length / threadCount;
	final ExecutorService executor = Executors
		.newFixedThreadPool(threadCount);
	int i = 0;
	for (i = 0; i < (threadCount - 1); i++) {
	    final Runnable runner = new TriangulateRunner(dfaces * i,
		    (dfaces * (i + 1)) - 1);
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

    /**
     * 
     */
    class TriangulateRunner implements Runnable {
	
	/**
	 * 
	 */
	int start;
	
	/**
	 * 
	 */
	int end;
	
	/**
	 * 
	 */
	int[][] triangles;

	/**
	 * 
	 *
	 * @param s 
	 * @param e 
	 */
	TriangulateRunner(final int s, final int e) {
	    start = s;
	    end = e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    int[] face;
	    for (int i = start; i <= end; i++) {
		face = faces[i];
		if (face.length == 3) {
		    addTriangle(face);
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

    /**
     * 
     *
     * @param tri 
     */
    synchronized void addTriangle(final int[] tri) {
	tris.add(tri);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getFaceNormal(int)
     */
    @Override
    public WB_Vector getFaceNormal(final int id) {
	if (!fNormalsUpdated) {
	    updateFaceNormalsST();
	}
	return faceNormals[id];
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getFaceCenter(int)
     */
    @Override
    public WB_Point getFaceCenter(final int id) {
	final WB_Point c = geometryfactory.createPoint();
	for (int i = 0; i < faces[id].length; i++) {
	    c.addSelf(getVertex(faces[id][i]));
	}
	c.divSelf(faces[id].length);
	return c;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getVertexNormal(int)
     */
    @Override
    public WB_Vector getVertexNormal(final int i) {
	if (!vNormalsUpdated) {
	    updateVertexNormals();
	}
	return vertexNormals[i];
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getNumberOfFaces()
     */
    @Override
    public int getNumberOfFaces() {
	return faces.length;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getNumberOfVertices()
     */
    @Override
    public int getNumberOfVertices() {
	return vertices.size();
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getVertex(int)
     */
    @Override
    public WB_SequencePoint getVertex(final int i) {
	return vertices.getPoint(i);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Mesh#getPoints()
     */
    @Override
    public WB_CoordinateSequence getPoints() {
	return vertices;
    }

    /**
     * 
     */
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

    /**
     * 
     */
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
	vertexNormals = new WB_Vector[nv];
	for (int i = 0; i < nv; i++) {
	    vertexNormals[i] = geometryfactory.createVector();
	}
	int i = 0;
	WB_SequencePoint p0, p1, p2;
	for (final int[] face : faces) {
	    for (int j = 0; j < face.length; j++) {
		p0 = vertices.getPoint(face[j]);
		p1 = vertices.getPoint(face[(j + 1) % face.length]);
		p2 = vertices.getPoint(face[((j - 1) + face.length)
			% face.length]);
		final WB_Vector P10 = geometryfactory
			.createNormalizedVectorFromTo(p0, p1);
		final WB_Vector P20 = geometryfactory
			.createNormalizedVectorFromTo(p0, p2);
		final double w = P10.getAngleNorm(P20);
		vertexNormals[face[j]].addMulSelf(w, faceNormals[i]);
	    }
	    i++;
	}
	for (final WB_Vector v : vertexNormals) {
	    v.normalizeSelf();
	}
	vNormalsUpdated = true;
    }

    /**
     * 
     */
    private void updateFaceNormalsST() {
	final int nf = faces.length;
	if (fNormalsUpdated) {
	    return;
	}
	faceNormals = new WB_Vector[nf];
	WB_SequencePoint p0, p1;
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
		tmp.addSelf(tmp2);
	    }
	    faceNormals[i] = tmp;
	    faceNormals[i].normalizeSelf();
	}
	fNormalsUpdated = true;
    }

    /**
     * 
     */
    private void updateFaceNormalsMT() {
	final int nf = faces.length;
	if (fNormalsUpdated) {
	    return;
	}
	faceNormals = new WB_Vector[nf];
	final int threadCount = Runtime.getRuntime().availableProcessors();
	final int dfaces = nf / threadCount;
	final ExecutorService executor = Executors
		.newFixedThreadPool(threadCount);
	int i = 0;
	for (i = 0; i < (threadCount - 1); i++) {
	    final Runnable runner = new FaceNormalRunner(dfaces * i,
		    (dfaces * (i + 1)) - 1);
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

    /**
     * 
     */
    class FaceNormalRunner implements Runnable {
	
	/**
	 * 
	 */
	int start;
	
	/**
	 * 
	 */
	int end;

	/**
	 * 
	 *
	 * @param s 
	 * @param e 
	 */
	FaceNormalRunner(final int s, final int e) {
	    start = s;
	    end = e;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    WB_SequencePoint p0, p1;
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
		    tmp.addSelf(tmp2);
		}
		faceNormals[i] = tmp;
		faceNormals[i].normalizeSelf();
	    }
	}
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public int[] vfNeighbors(final int i) {
	if (!vfNeighborsUpdated) {
	    updatevfNeighbors();
	}
	return vfNeighbors[i];
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public int[] getFace(final int i) {
	return faces[i];
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_FaceListMesh apply(final WB_Transform WB_Point) {
	final FastTable<WB_Point> newvertices = new FastTable<WB_Point>();
	int id = 0;
	WB_Point point;
	for (int i = 0; i < vertices.size(); i++) {
	    point = geometryfactory.createPoint();
	    WB_Point.applyAsPoint(vertices.getRaw(id++), vertices.getRaw(id++),
		    vertices.getRaw(id++), point);
	    newvertices.add(point);
	    id++;
	}
	return geometryfactory.createMesh(newvertices, faces);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.MESH;
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double k1(final int i) {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return curv1[i];
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double k2(final int i) {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return curv2[i];
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double K(final int i) {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return curv2[i] * curv1[i];
    }

    /**
     * 
     *
     * @return 
     */
    public double k1min() {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return k1min;
    }

    /**
     * 
     *
     * @return 
     */
    public double k2min() {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return k2min;
    }

    /**
     * 
     *
     * @return 
     */
    public double Kmin() {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return Kmin;
    }

    /**
     * 
     *
     * @return 
     */
    public double k1max() {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return k1max;
    }

    /**
     * 
     *
     * @return 
     */
    public double k2max() {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return k2max;
    }

    /**
     * 
     *
     * @return 
     */
    public double Kmax() {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return Kmax;
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public WB_Vector k1dir(final int i) {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return pdir1[i];
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public WB_Vector k2dir(final int i) {
	if (!curvaturesUpdated) {
	    updateCurvatures();
	}
	return pdir2[i];
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double[] DCurv(final int i) {
	if (!DCurvaturesUpdated) {
	    updateDCurvatures();
	}
	return dcurv[i];
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public double DCurvInvariant(final int i) {
	if (!DCurvaturesUpdated) {
	    updateDCurvatures();
	}
	return (dcurv[i][0] * dcurv[i][0]) + (dcurv[i][1] * dcurv[i][1])
		+ (dcurv[i][2] * dcurv[i][2]) + (dcurv[i][3] * dcurv[i][3]);
    }

    /**
     * 
     */
    private void updateCurvatures() {
	final WB_TriangleMesh tri = (WB_TriangleMesh) geometryfactory
		.createTriMesh(this);
	tri.updateCurvatures();
	k1min = tri.k1min;
	k2min = tri.k2min;
	Kmin = tri.Kmin;
	k1max = tri.k1max;
	k2max = tri.k2max;
	Kmax = tri.Kmax;
	curv1 = tri.curv1;
	curv2 = tri.curv2;
	pdir1 = tri.pdir1;
	pdir2 = tri.pdir2;
	curvaturesUpdated = true;
    }

    /**
     * 
     */
    private void updateDCurvatures() {
	final WB_TriangleMesh tri = (WB_TriangleMesh) geometryfactory
		.createTriMesh(this);
	tri.updateDCurvatures();
	k1min = tri.k1min;
	k2min = tri.k2min;
	Kmin = tri.Kmin;
	k1max = tri.k1max;
	k2max = tri.k2max;
	Kmax = tri.Kmax;
	curv1 = tri.curv1;
	curv2 = tri.curv2;
	pdir1 = tri.pdir1;
	pdir2 = tri.pdir2;
	dcurv = tri.dcurv;
	curvaturesUpdated = true;
	DCurvaturesUpdated = true;
    }
}
