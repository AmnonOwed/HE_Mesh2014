/*
 * 
 */
package wblut.geom;

import java.util.Collections;
import java.util.List;
import javolution.util.FastTable;

/**
 * 
 */
public class WB_Danzer {
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     */
    public static enum Type {
	
	/**
	 * 
	 */
	A, 
 /**
  * 
  */
 B, 
 /**
  * 
  */
 C, 
 /**
  * 
  */
 K
    }

    /**
     * 
     */
    static class DanzerTile {
	
	/**
	 * 
	 */
	public int p1, p2, p3, p4;
	
	/**
	 * 
	 */
	public Type type;
	
	/**
	 * 
	 */
	public int generation;
	
	/**
	 * 
	 */
	public double h, s, b;

	/**
	 * 
	 *
	 * @param t 
	 * @param g 
	 */
	public DanzerTile(final Type t, final int g) {
	    type = t;
	    p1 = p2 = p3 = p4 = -1;
	    generation = g;
	}
    }

    /**
     * 
     */
    final static double theta = Math.PI / 7.0;
    
    /**
     * 
     */
    final static double psi = Math.PI / 3.5;
    
    /**
     * 
     */
    final static double beta = (3.0 * Math.PI) / 7.0;
    
    /**
     * 
     */
    final static double phi = Math.PI / 1.75;
    
    /**
     * 
     */
    final static double sintheta = Math.sin(theta);
    
    /**
     * 
     */
    final static double sinhtheta = Math.sin(0.5 * theta);
    
    /**
     * 
     */
    final static double sinpsi = Math.sin(psi);
    
    /**
     * 
     */
    final static double sinbeta = Math.sin(beta);
    
    /**
     * 
     */
    final static double sinhbeta = Math.sin(0.5 * beta);
    
    /**
     * 
     */
    final static double sinphi = Math.sin(phi);
    
    /**
     * 
     */
    final static double costheta = Math.cos(theta);
    
    /**
     * 
     */
    final static double coshtheta = Math.cos(0.5 * theta);
    
    /**
     * 
     */
    final static double cospsi = Math.cos(psi);
    
    /**
     * 
     */
    final static double cosbeta = Math.cos(beta);
    
    /**
     * 
     */
    final static double coshbeta = Math.cos(0.5 * beta);
    
    /**
     * 
     */
    final static double cosphi = Math.cos(phi);
    
    /**
     * 
     */
    final double gamma = sintheta / (sintheta + sinpsi);
    
    /**
     * 
     */
    protected double scale;
    
    /**
     * 
     */
    protected double a, b, c, r1, r2, r3;
    
    /**
     * 
     */
    protected Type type;
    
    /**
     * 
     */
    protected List<WB_Point> points;
    
    /**
     * 
     */
    protected List<DanzerTile> tiles;

    /**
     * 
     *
     * @param sc 
     * @param t 
     */
    public WB_Danzer(final double sc, final Type t) {
	this(sc, t, geometryfactory.createEmbeddedPlane());
    }

    /**
     * 
     *
     * @param sc 
     * @param t 
     * @param context 
     */
    public WB_Danzer(final double sc, final Type t, final WB_Context2D context) {
	c = sc;
	b = (c / sinbeta) * sintheta;
	a = (c / sinbeta) * sinpsi;
	r1 = c / (a + (2 * c));
	r2 = c / (a + b + c);
	r3 = b / (a + b + c);
	points = new FastTable<WB_Point>();
	tiles = new FastTable<DanzerTile>();
	type = t;
	final DanzerTile T = new DanzerTile(type, 0);
	switch (type) {
	case A:
	    WB_Point p = geometryfactory.createPoint();
	    context.pointTo3D(0, 0.5 * sinbeta * c, p);
	    points.add(p);
	    p = geometryfactory.createPoint();
	    context.pointTo3D(-0.5 * b, -0.5 * sinbeta * c, p);
	    points.add(p);
	    p = geometryfactory.createPoint();
	    context.pointTo3D(0.5 * b, -0.5 * sinbeta * c, p);
	    points.add(p);
	    break;
	case C:
	    p = geometryfactory.createPoint();
	    context.pointTo3D(-0.5 * a * coshbeta, 0, p);
	    points.add(p);
	    p = geometryfactory.createPoint();
	    context.pointTo3D(0.5 * a * coshbeta, -a * cospsi, p);
	    points.add(p);
	    p = geometryfactory.createPoint();
	    context.pointTo3D(0.5 * a * coshbeta, a * cospsi, p);
	    points.add(p);
	    break;
	case B:
	    p = geometryfactory.createPoint();
	    context.pointTo3D(0, 0.5 * sinbeta * c, p);
	    points.add(p);
	    p = geometryfactory.createPoint();
	    context.pointTo3D(-a * sinhtheta, (-a * coshtheta)
		    + (0.5 * sinbeta * c), p);
	    points.add(p);
	    p = geometryfactory.createPoint();
	    context.pointTo3D(0.5 * b, -0.5 * sinbeta * c, p);
	    points.add(p);
	    break;
	}
	T.p1 = 0;
	T.p2 = 1;
	T.p3 = 2;
	tiles.add(T);
    }

    /**
     * 
     */
    public void inflate() {
	final List<DanzerTile> newTiles = new FastTable<DanzerTile>();
	for (int i = 0; i < tiles.size(); i++) {
	    newTiles.addAll(inflateTileInt(tiles.get(i)));
	}
	tiles = newTiles;
    }

    /**
     * 
     *
     * @param rep 
     */
    public void inflate(final int rep) {
	for (int r = 0; r < rep; r++) {
	    inflate();
	}
    }

    /**
     * 
     *
     * @param T 
     * @return 
     */
    protected List<DanzerTile> inflateTileInt(final DanzerTile T) {
	final List<DanzerTile> newTiles = new FastTable<DanzerTile>();
	final WB_Point p1 = points.get(T.p1);
	final WB_Point p2 = points.get(T.p2);
	final WB_Point p3 = points.get(T.p3);
	final int cnp = points.size();
	final Type type = T.type;
	switch (type) {
	case A:
	    WB_Point q1 = geometryfactory.createInterpolatedPoint(p1, p2, r1);
	    points.add(q1);
	    points.add(geometryfactory.createInterpolatedPoint(p1, p3, r1));
	    points.add(geometryfactory.createInterpolatedPoint(p2, p1, r1));
	    points.add(geometryfactory.createInterpolatedPoint(p3, p1, r1));
	    final WB_Point q2 = geometryfactory.createInterpolatedPoint(p2, p3,
		    a / (a + b));
	    points.add(q2);
	    points.add(geometryfactory.createInterpolatedPoint(q1, q2, c
		    / (a + c)));
	    DanzerTile nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p1;
	    nT.p2 = cnp;
	    nT.p3 = cnp + 1;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = cnp + 5;
	    nT.p2 = cnp;
	    nT.p3 = cnp + 1;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p2;
	    nT.p2 = cnp + 2;
	    nT.p3 = cnp + 5;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p3;
	    nT.p2 = cnp + 3;
	    nT.p3 = cnp + 5;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp;
	    nT.p2 = cnp + 2;
	    nT.p3 = cnp + 5;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 1;
	    nT.p2 = cnp + 3;
	    nT.p3 = cnp + 5;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 5;
	    nT.p2 = cnp + 4;
	    nT.p3 = T.p3;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.C, T.generation + 1);
	    nT.p1 = cnp + 4;
	    nT.p2 = T.p2;
	    nT.p3 = cnp + 5;
	    newTiles.add(nT);
	    break;
	case B:
	    points.add(geometryfactory.createInterpolatedPoint(p1, p2, r2));
	    points.add(geometryfactory.createInterpolatedPoint(p1, p3, r1));
	    points.add(geometryfactory.createInterpolatedPoint(p2, p1, r3));
	    points.add(geometryfactory.createInterpolatedPoint(p3, p1, r1));
	    points.add(geometryfactory.createInterpolatedPoint(p2, p3, a
		    / (a + b)));
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p1;
	    nT.p2 = cnp + 1;
	    nT.p3 = cnp;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 3;
	    nT.p2 = cnp + 1;
	    nT.p3 = cnp;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.C, T.generation + 1);
	    nT.p1 = cnp + 2;
	    nT.p2 = cnp;
	    nT.p3 = cnp + 3;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 3;
	    nT.p2 = cnp + 2;
	    nT.p3 = T.p2;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.C, T.generation + 1);
	    nT.p1 = cnp + 4;
	    nT.p3 = cnp + 3;
	    nT.p2 = T.p2;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 3;
	    nT.p2 = cnp + 4;
	    nT.p3 = T.p3;
	    newTiles.add(nT);
	    break;
	case C:
	    q1 = geometryfactory.createInterpolatedPoint(p1, p2, r2);
	    points.add(q1);
	    points.add(geometryfactory.createInterpolatedPoint(p1, p3, r3));
	    points.add(geometryfactory.createInterpolatedPoint(p2, p1, r3));
	    points.add(geometryfactory.createInterpolatedPoint(p3, p1, r2));
	    points.add(geometryfactory.createInterpolatedPoint(p2, p3, r1));
	    points.add(geometryfactory.createInterpolatedPoint(p3, p2, r1));
	    points.add(geometryfactory.createInterpolatedPoint(q1, p3, r3));
	    points.add(geometryfactory.createInterpolatedPoint(p3, q1, r2));
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p3;
	    nT.p3 = cnp + 3;
	    nT.p2 = cnp + 7;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 6;
	    nT.p2 = cnp + 7;
	    nT.p3 = cnp + 3;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.C, T.generation + 1);
	    nT.p1 = cnp + 1;
	    nT.p3 = cnp + 6;
	    nT.p2 = cnp + 3;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 6;
	    nT.p2 = cnp + 1;
	    nT.p3 = T.p1;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p1;
	    nT.p3 = cnp;
	    nT.p2 = cnp + 6;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.A, T.generation + 1);
	    nT.p1 = T.p3;
	    nT.p2 = cnp + 5;
	    nT.p3 = cnp + 7;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 4;
	    nT.p2 = cnp + 5;
	    nT.p3 = cnp + 7;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.C, T.generation + 1);
	    nT.p1 = cnp + 6;
	    nT.p2 = cnp + 7;
	    nT.p3 = cnp + 4;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 4;
	    nT.p2 = cnp + 6;
	    nT.p3 = cnp;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.C, T.generation + 1);
	    nT.p1 = cnp + 2;
	    nT.p3 = cnp + 4;
	    nT.p2 = cnp;
	    newTiles.add(nT);
	    nT = new DanzerTile(Type.B, T.generation + 1);
	    nT.p1 = cnp + 4;
	    nT.p2 = cnp + 2;
	    nT.p3 = T.p2;
	    newTiles.add(nT);
	}
	return newTiles;
    }

    /**
     * 
     *
     * @param i 
     * @return 
     */
    public DanzerTile tile(final int i) {
	return tiles.get(i);
    }

    /**
     * 
     *
     * @return 
     */
    public int oldest() {
	int result = Integer.MAX_VALUE;
	for (final DanzerTile T : tiles) {
	    result = Math.min(T.generation, result);
	    if (result == 0) {
		return 0;
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @return 
     */
    public int youngest() {
	int result = -1;
	for (final DanzerTile T : tiles) {
	    result = Math.max(T.generation, result);
	}
	return result;
    }

    /**
     * 
     *
     * @param i 
     */
    public void inflateTile(final int i) {
	tiles.addAll(inflateTileInt(tiles.get(i)));
	tiles.remove(i);
    }

    /**
     * 
     */
    public void inflateOldest() {
	inflateOldest(0);
    }

    /**
     * 
     *
     * @param r 
     */
    public void inflateOldest(final int r) {
	final int age = oldest();
	Collections.shuffle(tiles);
	for (final DanzerTile T : tiles) {
	    if (T.generation <= (age + r)) {
		tiles.addAll(inflateTileInt(T));
		tiles.remove(T);
		return;
	    }
	}
    }

    /**
     * 
     *
     * @param i 
     */
    public void removeTile(final int i) {
	tiles.remove(i);
    }

    /**
     * 
     *
     * @return 
     */
    public int size() {
	return tiles.size();
    }

    /**
     * 
     *
     * @return 
     */
    public int numberOfPoints() {
	return points.size();
    }

    /**
     * 
     *
     * @return 
     */
    public List<WB_Point> points() {
	return points;
    }

    /**
     * 
     *
     * @return 
     */
    public List<WB_Polygon> getTiles() {
	final List<WB_Polygon> faces = new FastTable<WB_Polygon>();
	clean();
	for (final DanzerTile T : tiles) {
	    faces.add(geometryfactory.createSimplePolygon(points.get(T.p1),
		    points.get(T.p2), points.get(T.p3)));
	}
	return faces;
    }

    /**
     * 
     */
    private void clean() {
	final boolean[] used = new boolean[points.size()];
	final int[] newindices = new int[points.size()];
	for (final DanzerTile T : tiles) {
	    used[T.p1] = true;
	    used[T.p2] = true;
	    used[T.p3] = true;
	}
	int ni = 0;
	final List<WB_Point> newpoints = new FastTable<WB_Point>();
	for (int i = 0; i < points.size(); i++) {
	    if (used[i]) {
		newindices[i] = ni++;
		newpoints.add(points.get(i));
	    }
	}
	for (final DanzerTile T : tiles) {
	    T.p1 = newindices[T.p1];
	    T.p2 = newindices[T.p2];
	    T.p3 = newindices[T.p3];
	}
	points = newpoints;
    }
}
