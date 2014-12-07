package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javolution.util.FastTable;

public class WB_Polygon extends WB_Ring {
    int[][] triangles;
    int numberOfContours;
    int[] numberOfPointsPerContour;
    int numberOfShellPoints;
    private static final WB_GeometryFactory gf = WB_GeometryFactory.instance();

    protected WB_Polygon(final Collection<? extends WB_Coordinate> points) {
	numberOfPoints = points.size();
	numberOfShellPoints = points.size();
	this.points = gf.createPointSequence(points);
	calculateDirections();
	numberOfContours = 1;
	numberOfPointsPerContour = new int[] { numberOfPoints };
    }

    protected WB_Polygon(final WB_Coordinate... points) {
	numberOfPoints = points.length;
	numberOfShellPoints = points.length;
	this.points = gf.createPointSequence(points);
	calculateDirections();
	numberOfContours = 1;
	numberOfPointsPerContour = new int[] { numberOfPoints };
    }

    protected WB_Polygon(final WB_CoordinateSequence points) {
	numberOfPoints = points.size();
	numberOfShellPoints = points.size();
	this.points = gf.createPointSequence(points);
	calculateDirections();
	numberOfContours = 1;
	numberOfPointsPerContour = new int[] { numberOfPoints };
    }

    protected WB_Polygon(final Collection<? extends WB_Coordinate> points,
	    final Collection<? extends WB_Coordinate> innerpoints) {
	numberOfShellPoints = points.size();
	numberOfPoints = points.size() + innerpoints.size();
	final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
	tmp.addAll(points);
	tmp.addAll(innerpoints);
	this.points = gf.createPointSequence(tmp);
	calculateDirections();
	numberOfContours = 2;
	numberOfPointsPerContour = new int[] { numberOfShellPoints,
		innerpoints.size() };
    }

    protected WB_Polygon(final WB_Coordinate[] points,
	    final WB_Coordinate[] innerpoints) {
	numberOfShellPoints = points.length;
	numberOfPoints = points.length + innerpoints.length;
	final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
	for (final WB_Coordinate p : points) {
	    tmp.add(p);
	}
	for (final WB_Coordinate p : innerpoints) {
	    tmp.add(p);
	}
	this.points = gf.createPointSequence(tmp);
	calculateDirections();
	numberOfContours = 2;
	numberOfPointsPerContour = new int[] { numberOfShellPoints,
		innerpoints.length };
    }

    protected WB_Polygon(final Collection<? extends WB_Coordinate> points,
	    final List<? extends WB_Coordinate>[] innerpoints) {
	numberOfShellPoints = points.size();
	numberOfPoints = points.size();
	final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
	for (final WB_Coordinate p : points) {
	    tmp.add(p);
	}
	numberOfContours = innerpoints.length + 1;
	numberOfPointsPerContour = new int[innerpoints.length + 1];
	numberOfPointsPerContour[0] = numberOfShellPoints;
	int i = 1;
	for (final List<? extends WB_Coordinate> hole : innerpoints) {
	    for (final WB_Coordinate p : hole) {
		tmp.add(p);
	    }
	    numberOfPointsPerContour[i++] = hole.size();
	    numberOfPoints += hole.size();
	}
	this.points = gf.createPointSequence(tmp);
	calculateDirections();
    }

    protected WB_Polygon(final WB_Coordinate[] points,
	    final WB_Coordinate[][] innerpoints) {
	numberOfShellPoints = points.length;
	numberOfPoints = points.length;
	final ArrayList<WB_Coordinate> tmp = new ArrayList<WB_Coordinate>();
	for (final WB_Coordinate p : points) {
	    tmp.add(p);
	}
	numberOfContours = innerpoints.length + 1;
	numberOfPointsPerContour = new int[innerpoints.length + 1];
	numberOfPointsPerContour[0] = numberOfShellPoints;
	int i = 1;
	for (final WB_Coordinate[] hole : innerpoints) {
	    for (final WB_Coordinate p : hole) {
		tmp.add(p);
	    }
	    numberOfPointsPerContour[i++] = hole.length;
	    numberOfPoints += hole.length;
	}
	this.points = gf.createPointSequence(tmp);
	calculateDirections();
    }

    private void calculateDirections() {
	final List<WB_Vector> dirs = new ArrayList<WB_Vector>(numberOfPoints);
	incLengths = new double[numberOfPoints];
	int offset = 0;
	for (int j = 0; j < numberOfContours; j++) {
	    final int n = numberOfPointsPerContour[j];
	    for (int i = 0; i < n; i++) {
		final int in = offset + (i + 1) % n;
		final WB_Vector v = gf.createVector(
			points.get(in, 0) - points.get(offset + i, 0),
			points.get(in, 1) - points.get(offset + i, 1),
			points.get(in, 2) - points.get(offset + i, 2));
		incLengths[offset + i] = (i == 0) ? v.getLength3D()
			: incLengths[offset + i - 1] + v.getLength3D();
		v.normalizeSelf();
		dirs.add(v);
	    }
	    offset += n;
	}
	directions = gf.createVectorSequence(dirs);
    }

    @Override
    public boolean equals(final Object o) {
	if (o == this) {
	    return true;
	}
	if (!(o instanceof WB_Polygon)) {
	    return false;
	}
	final WB_Polygon L = (WB_Polygon) o;
	if (getNumberOfPoints() != L.getNumberOfPoints()) {
	    return false;
	}
	for (int i = 0; i < numberOfPoints; i++) {
	    if (!getPoint(i).equals(L.getPoint(i))) {
		return false;
	    }
	}
	return true;
    }

    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.POLYGON;
    }

    @Override
    public int getNumberOfPoints() {
	return points.size();
    }

    public int getNumberOfShellPoints() {
	return numberOfShellPoints;
    }

    public int getNumberOfHoles() {
	return numberOfContours - 1;
    }

    public int getNumberOfContours() {
	return numberOfContours;
    }

    public int[] getNumberOfPointsPerContour() {
	return numberOfPointsPerContour;
    }

    public int[][] getTriangles() {
	if (triangles == null) {
	    if (numberOfShellPoints < 3) {
		return new int[][] { { 0, 0, 0 } };
	    } else if (numberOfShellPoints == 3 && numberOfContours == 1) {
		return new int[][] { { 0, 1, 2 } };
	    } else if (numberOfShellPoints == 4 && numberOfContours == 1) {
		return WB_Triangulate.triangulateQuad(points.getPoint(0),
			points.getPoint(1), points.getPoint(2),
			points.getPoint(3));
	    } else {
		final WB_Triangulation2D triangulation = WB_Triangulate
			.getPolygonTriangulation2D(this, true);
		triangles = triangulation.getTriangles();
	    }
	}
	return triangles;
    }

    public int[][] getTriangles(final boolean optimize) {
	if (triangles == null) {
	    if (numberOfShellPoints < 3) {
		return new int[][] { { 0, 0, 0 } };
	    } else if (numberOfShellPoints == 3 && numberOfContours == 1) {
		return new int[][] { { 0, 1, 2 } };
	    } else if (numberOfShellPoints == 4 && numberOfContours == 1) {
		return WB_Triangulate.triangulateQuad(points.getPoint(0),
			points.getPoint(1), points.getPoint(2),
			points.getPoint(3));
	    } else {
		final WB_Triangulation2D triangulation = WB_Triangulate
			.getPolygonTriangulation2D(this, optimize);
		triangles = triangulation.getTriangles();
	    }
	}
	return triangles;
    }

    public WB_Plane getPlane(final double d) {
	final WB_Vector normal = gf.createVector();
	for (int i = 0, j = getNumberOfShellPoints() - 1; i < getNumberOfShellPoints(); j = i, i++) {
	    normal.addSelf(
		    (points.get(j, 1) - points.get(i, 1))
		    * (points.get(j, 2) + points.get(i, 2)),
		    (points.get(j, 2) - points.get(i, 2))
		    * (points.get(j, 0) + points.get(i, 0)),
		    (points.get(j, 0) - points.get(i, 0))
		    * (points.get(j, 1) + points.get(i, 1)));
	}
	normal.normalizeSelf();
	if (normal.getSqLength3D() < 0.5) {
	    return null;
	}
	return gf.createPlane(points.getPoint(0).addMul(d, normal), normal);
    }

    public WB_Plane getPlane() {
	return getPlane(0);
    }

    public WB_Vector getNormal() {
	final WB_Vector normal = gf.createVector();
	for (int i = 0, j = getNumberOfShellPoints() - 1; i < getNumberOfShellPoints(); j = i, i++) {
	    normal.addSelf(
		    (points.get(j, 1) - points.get(i, 1))
		    * (points.get(j, 2) + points.get(i, 2)),
		    (points.get(j, 2) - points.get(i, 2))
		    * (points.get(j, 0) + points.get(i, 0)),
		    (points.get(j, 0) - points.get(i, 0))
		    * (points.get(j, 1) + points.get(i, 1)));
	}
	normal.normalizeSelf();
	return normal;
    }

    @Override
    public WB_IndexedPoint getPoint(final int i) {
	return points.getPoint(i);
    }

    @Override
    public double getd(final int i, final int j) {
	return points.get(i, j);
    }

    @Override
    public float getf(final int i, final int j) {
	return (float) points.get(i, j);
    }

    public boolean isSimple() {
	return numberOfContours == 1;
    }

    public WB_Polygon toPolygon2D() {
	final List<WB_Point> shellpoints = new FastTable<WB_Point>();
	final WB_Plane P = getPlane(0);
	for (int i = 0; i < numberOfShellPoints; i++) {
	    shellpoints.add(P.localPoint2D(points.getPoint(i)));
	}
	if (isSimple()) {
	    return new WB_Polygon(shellpoints);
	} else {
	    @SuppressWarnings("unchecked")
	    final List<WB_Point>[] holepoints = new FastTable[numberOfContours - 1];
	    int index = numberOfShellPoints;
	    for (int i = 0; i < numberOfContours - 1; i++) {
		holepoints[i] = new FastTable<WB_Point>();
		for (int j = 0; j < numberOfPointsPerContour[i + 1]; j++) {
		    holepoints[i].add(P.localPoint2D(points.getPoint(index++)));
		}
	    }
	    return new WB_Polygon(shellpoints, holepoints);
	}
    }

    public WB_Polygon negate() {
	final List<WB_Point> shellpoints = new FastTable<WB_Point>();
	for (int i = numberOfShellPoints - 1; i >= 0; i--) {
	    shellpoints.add(new WB_Point(points.getPoint(i)));
	}
	if (isSimple()) {
	    return new WB_Polygon(shellpoints);
	} else {
	    @SuppressWarnings("unchecked")
	    final List<WB_Point>[] holepoints = new FastTable[numberOfContours - 1];
	    int index = numberOfShellPoints;
	    for (int i = 0; i < numberOfContours - 1; i++) {
		holepoints[i] = new FastTable<WB_Point>();
		for (int j = numberOfPointsPerContour[i + 1] - 1; j >= 0; j--) {
		    holepoints[i].add(new WB_Point(points.getPoint(index++)));
		}
	    }
	    return new WB_Polygon(shellpoints, holepoints);
	}
    }

    // TODO all functions below only support simple polygons
    public static WB_Polygon[] splitPolygon(final WB_Polygon poly,
	    final WB_Plane P) {
	if (!poly.isSimple()) {
	    throw new UnsupportedOperationException(
		    "Only simple polygons are supported at this time!");
	}
	int numFront = 0;
	int numBack = 0;
	final List<WB_Coordinate> frontVerts = new FastTable<WB_Coordinate>();
	final List<WB_Coordinate> backVerts = new FastTable<WB_Coordinate>();
	final int numVerts = poly.numberOfShellPoints;
	if (numVerts > 0) {
	    WB_Coordinate a = poly.points.getPoint(numVerts - 1);
	    WB_Classification aSide = P.classifyPointToPlane(a);
	    WB_Coordinate b;
	    WB_Classification bSide;
	    for (int n = 0; n < numVerts; n++) {
		final WB_IntersectionResult i;
		b = poly.points.getPoint(n);
		bSide = P.classifyPointToPlane(b);
		if (bSide == WB_Classification.FRONT) {
		    if (aSide == WB_Classification.BACK) {
			i = WB_Intersection.getIntersection3D(b, a, P);
			frontVerts.add((WB_Point) i.object);
			numFront++;
			backVerts.add((WB_Point) i.object);
			numBack++;
		    }
		    frontVerts.add(b);
		    numFront++;
		} else if (bSide == WB_Classification.BACK) {
		    if (aSide == WB_Classification.FRONT) {
			i = WB_Intersection.getIntersection3D(a, b, P);
			frontVerts.add((WB_Point) i.object);
			numFront++;
			backVerts.add((WB_Point) i.object);
			numBack++;
		    } else if (aSide == WB_Classification.ON) {
			backVerts.add(a);
			numBack++;
		    }
		    backVerts.add(b);
		    numBack++;
		} else {
		    frontVerts.add(b);
		    numFront++;
		    if (aSide == WB_Classification.BACK) {
			backVerts.add(b);
			numBack++;
		    }
		}
		a = b;
		aSide = bSide;
	    }
	}
	final WB_Polygon[] result = new WB_Polygon[2];
	result[0] = new WB_Polygon(frontVerts);
	result[1] = new WB_Polygon(backVerts);
	return result;
    }

    public WB_Polygon[] splitPolygon(final WB_Plane P) {
	return splitPolygon(this, P);
    }

    public static WB_Polygon trimConvexPolygon(WB_Polygon poly, final double d) {
	final WB_Polygon cpoly = new WB_Polygon(poly.points);
	final int n = cpoly.numberOfShellPoints; // get number of vertices
	final WB_Plane P = cpoly.getPlane(); // get plane of poly
	WB_Coordinate p1, p2;
	WB_Point origin;
	WB_Vector v, normal;
	for (int i = 0, j = n - 1; i < n; j = i, i++) {
	    p1 = cpoly.getPoint(i);// startpoint of edge
	    p2 = cpoly.getPoint(j);// endpoint of edge
	    // vector along edge
	    v = gf.createNormalizedVectorFromTo(p1, p2);
	    // edge normal is perpendicular to edge and plane normal
	    normal = v.cross(P.getNormal());
	    // center of edge
	    origin = new WB_Point(p1).addSelf(p2).mulSelf(0.5);
	    // offset cutting plane origin by the desired distance d
	    origin.addMulSelf(d, normal);
	    final WB_Polygon[] split = splitPolygon(poly, new WB_Plane(origin,
		    normal));
	    poly = split[0];
	}
	return poly;
    }

    public WB_Polygon trimConvexPolygon(final double d) {
	return trimConvexPolygon(this, d);
    }

    public static WB_Polygon trimConvexPolygon(WB_Polygon poly, final double[] d) {
	final WB_Polygon cpoly = new WB_Polygon(poly.points);
	final int n = cpoly.numberOfShellPoints; // get number of vertices
	final WB_Plane P = cpoly.getPlane(); // get plane of poly
	WB_Coordinate p1, p2;
	WB_Point origin;
	WB_Vector v, normal;
	for (int i = 0, j = n - 1; i < n; j = i, i++) {
	    p1 = cpoly.getPoint(i);// startpoint of edge
	    p2 = cpoly.getPoint(j);// endpoint of edge
	    // vector along edge
	    v = gf.createNormalizedVectorFromTo(p1, p2);
	    // edge normal is perpendicular to edge and plane normal
	    normal = v.cross(P.getNormal());
	    // center of edge
	    origin = new WB_Point(p1).addSelf(p2).mulSelf(0.5);
	    // offset cutting plane origin by the desired distance d
	    origin.addMulSelf(d[j], normal);
	    final WB_Polygon[] split = splitPolygon(poly, new WB_Plane(origin,
		    normal));
	    poly = split[0];
	}
	return poly;
    }

    public WB_Polygon trimConvexPolygon(final double[] d) {
	return trimConvexPolygon(this, d);
    }

    public WB_Coordinate closestPoint(final WB_Coordinate p) {
	double d = Double.POSITIVE_INFINITY;
	int id = -1;
	for (int i = 0; i < this.numberOfShellPoints; i++) {
	    final double cd = WB_Distance.getSqDistance3D(p, getPoint(i));
	    if (cd < d) {
		id = i;
		d = cd;
	    }
	}
	return getPoint(id);
    }

    public int closestIndex(final WB_Coordinate p) {
	double d = Double.POSITIVE_INFINITY;
	int id = -1;
	for (int i = 0; i < this.numberOfShellPoints; i++) {
	    final double cd = WB_Distance.getSqDistance3D(p, getPoint(i));
	    if (cd < d) {
		id = i;
		d = cd;
	    }
	}
	return id;
    }

    public List<WB_Segment> toSegments() {
	final List<WB_Segment> segments = new FastTable<WB_Segment>();
	for (int i = 0, j = this.numberOfShellPoints - 1; i < this.numberOfShellPoints; j = i, i++) {
	    segments.add(new WB_Segment(getPoint(j), getPoint(i)));
	}
	return segments;
    }
}