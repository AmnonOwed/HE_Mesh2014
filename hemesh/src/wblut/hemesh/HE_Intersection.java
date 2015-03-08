/*
 * 
 */
package wblut.hemesh;

import java.util.List;
import javolution.util.FastTable;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_AABBTree.WB_AABBNode;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Segment;
import wblut.geom.WB_Triangle;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class HE_Intersection {
    
    /**
     * 
     *
     * @param face 
     * @param line 
     * @return 
     */
    public static HE_FaceIntersection getIntersection(final HE_Face face,
	    final WB_Line line) {
	final WB_Plane P = face.toPlane();
	HE_FaceIntersection p = null;
	final WB_IntersectionResult lpi = WB_GeometryOp.getIntersection3D(line,
		P);
	if (lpi.intersection) {
	    p = new HE_FaceIntersection(face, (WB_Point) lpi.object);
	    final WB_Point cp = WB_GeometryOp.getClosestPoint3D(p.point,
		    face.toPlanarPolygon());
	    if (WB_Epsilon.isZero(WB_GeometryOp.getDistance3D(cp, p.point))) {
		return p;
	    }
	}
	return null;
    }

    /**
     * 
     *
     * @param face 
     * @param ray 
     * @return 
     */
    public static HE_FaceIntersection getIntersection(final HE_Face face,
	    final WB_Ray ray) {
	final WB_Plane P = face.toPlane();
	HE_FaceIntersection p = null;
	final WB_IntersectionResult lpi = WB_GeometryOp.getIntersection3D(ray,
		P);
	if (lpi.intersection) {
	    p = new HE_FaceIntersection(face, (WB_Point) lpi.object);
	    final WB_Point cp = WB_GeometryOp.getClosestPoint3D(p.point,
		    face.toPlanarPolygon());
	    if (WB_Epsilon.isZero(WB_GeometryOp.getDistance3D(cp, p.point))) {
		return new HE_FaceIntersection(face, p.point);
	    }
	}
	return null;
    }

    /**
     * 
     *
     * @param face 
     * @param segment 
     * @return 
     */
    public static HE_FaceIntersection getIntersection(final HE_Face face,
	    final WB_Segment segment) {
	final WB_Plane P = face.toPlane();
	HE_FaceIntersection p = null;
	final WB_IntersectionResult lpi = WB_GeometryOp.getIntersection3D(
		segment, P);
	if (lpi.intersection) {
	    p = new HE_FaceIntersection(face, (WB_Point) lpi.object);
	    final WB_Point cp = WB_GeometryOp.getClosestPoint3D(p.point,
		    face.toPlanarPolygon());
	    if (WB_Epsilon.isZero(WB_GeometryOp.getDistance3D(cp, p.point))) {
		return p;
	    }
	}
	return null;
    }

    /**
     * 
     *
     * @param e 
     * @param P 
     * @return 
     */
    public static double getIntersection(final HE_Halfedge e, final WB_Plane P) {
	final WB_IntersectionResult i = WB_GeometryOp.getIntersection3D(
		e.getStartVertex(), e.getEndVertex(), P);
	if (i.intersection == false) {
	    return -1.0;// intersection beyond endpoints
	}
	return i.t1;// intersection on edge
    }

    /**
     * 
     *
     * @param tree 
     * @param ray 
     * @return 
     */
    public static List<HE_FaceIntersection> getIntersection(
	    final WB_AABBTree tree, final WB_Ray ray) {
	final List<HE_FaceIntersection> p = new FastTable<HE_FaceIntersection>();
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray,
		tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, ray);
	    if (sect != null) {
		p.add(sect);
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param segment 
     * @return 
     */
    public static List<HE_FaceIntersection> getIntersection(
	    final WB_AABBTree tree, final WB_Segment segment) {
	final List<HE_FaceIntersection> p = new FastTable<HE_FaceIntersection>();
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(
		segment, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, segment);
	    if (sect != null) {
		p.add(sect);
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param line 
     * @return 
     */
    public static List<HE_FaceIntersection> getIntersection(
	    final WB_AABBTree tree, final WB_Line line) {
	final List<HE_FaceIntersection> p = new FastTable<HE_FaceIntersection>();
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(line,
		tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, line);
	    if (sect != null) {
		p.add(sect);
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param P 
     * @return 
     */
    public static List<WB_Segment> getIntersection(final WB_AABBTree tree,
	    final WB_Plane P) {
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp
		.getIntersection3D(P, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	final List<WB_Segment> cuts = new FastTable<WB_Segment>();
	for (final HE_Face face : candidates) {
	    cuts.addAll(WB_GeometryOp.getIntersection3D(face.toPolygon(), P));
	}
	return cuts;
    }

    /**
     * 
     *
     * @param tree 
     * @param P 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final WB_AABBTree tree, final WB_Plane P) {
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp
		.getIntersection3D(P, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	return candidates;
    }

    /**
     * 
     *
     * @param tree 
     * @param T 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final WB_AABBTree tree, final WB_Triangle T) {
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp
		.getIntersection3D(T, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	return candidates;
    }

    /**
     * 
     *
     * @param tree 
     * @param R 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final WB_AABBTree tree, final WB_Ray R) {
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp
		.getIntersection3D(R, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	return candidates;
    }

    /**
     * 
     *
     * @param tree 
     * @param L 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final WB_AABBTree tree, final WB_Line L) {
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp
		.getIntersection3D(L, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	return candidates;
    }

    /**
     * 
     *
     * @param tree 
     * @param segment 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final WB_AABBTree tree, final WB_Segment segment) {
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(
		segment, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	return candidates;
    }

    /**
     * 
     *
     * @param tree 
     * @param ray 
     * @return 
     */
    public static HE_FaceIntersection getClosestIntersection(
	    final WB_AABBTree tree, final WB_Ray ray) {
	HE_FaceIntersection p = null;
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray,
		tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	double d2, d2min = Double.POSITIVE_INFINITY;
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, ray);
	    if (sect != null) {
		d2 = sect.point.getSqDistance3D(ray.getOrigin());
		if (d2 < d2min) {
		    p = sect;
		    d2min = d2;
		}
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param ray 
     * @return 
     */
    public static HE_FaceIntersection getFurthestIntersection(
	    final WB_AABBTree tree, final WB_Ray ray) {
	HE_FaceIntersection p = null;
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(ray,
		tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	double d2, d2max = -1;
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, ray);
	    if (sect != null) {
		d2 = sect.point.getSqDistance3D(ray.getOrigin());
		if (d2 > d2max) {
		    p = sect;
		    d2max = d2;
		}
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param line 
     * @return 
     */
    public static HE_FaceIntersection getClosestIntersection(
	    final WB_AABBTree tree, final WB_Line line) {
	HE_FaceIntersection p = null;
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(line,
		tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	double d2, d2min = Double.POSITIVE_INFINITY;
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, line);
	    if (sect != null) {
		d2 = sect.point.getSqDistance3D(line.getOrigin());
		if (d2 < d2min) {
		    p = sect;
		    d2min = d2;
		}
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param line 
     * @return 
     */
    public static HE_FaceIntersection getFurthestIntersection(
	    final WB_AABBTree tree, final WB_Line line) {
	HE_FaceIntersection p = null;
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(line,
		tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	double d2, d2max = -1;
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, line);
	    if (sect != null) {
		d2 = sect.point.getSqDistance3D(line.getOrigin());
		if (d2 > d2max) {
		    p = sect;
		    d2max = d2;
		}
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param segment 
     * @return 
     */
    public static HE_FaceIntersection getClosestIntersection(
	    final WB_AABBTree tree, final WB_Segment segment) {
	HE_FaceIntersection p = null;
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(
		segment, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	double d2, d2min = Double.POSITIVE_INFINITY;
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, segment);
	    if (sect != null) {
		d2 = sect.point.getSqDistance3D(segment.getOrigin());
		if (d2 < d2min) {
		    p = sect;
		    d2min = d2;
		}
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param tree 
     * @param segment 
     * @return 
     */
    public static HE_FaceIntersection getFurthestIntersection(
	    final WB_AABBTree tree, final WB_Segment segment) {
	HE_FaceIntersection p = null;
	final List<HE_Face> candidates = new FastTable<HE_Face>();
	final List<WB_AABBNode> nodes = WB_GeometryOp.getIntersection3D(
		segment, tree);
	for (final WB_AABBNode n : nodes) {
	    candidates.addAll(n.getFaces());
	}
	double d2, d2max = -1;
	for (final HE_Face face : candidates) {
	    final HE_FaceIntersection sect = getIntersection(face, segment);
	    if (sect != null) {
		d2 = sect.point.getSqDistance3D(segment.getOrigin());
		if (d2 > d2max) {
		    p = sect;
		    d2max = d2;
		}
	    }
	}
	return p;
    }

    /**
     * 
     *
     * @param mesh 
     * @param ray 
     * @return 
     */
    public static List<HE_FaceIntersection> getIntersection(final HE_Mesh mesh,
	    final WB_Ray ray) {
	return getIntersection(new WB_AABBTree(mesh, 10), ray);
    }

    /**
     * 
     *
     * @param mesh 
     * @param segment 
     * @return 
     */
    public static List<HE_FaceIntersection> getIntersection(final HE_Mesh mesh,
	    final WB_Segment segment) {
	return getIntersection(new WB_AABBTree(mesh, 10), segment);
    }

    /**
     * 
     *
     * @param mesh 
     * @param line 
     * @return 
     */
    public static List<HE_FaceIntersection> getIntersection(final HE_Mesh mesh,
	    final WB_Line line) {
	return getIntersection(new WB_AABBTree(mesh, 10), line);
    }

    /**
     * 
     *
     * @param mesh 
     * @param P 
     * @return 
     */
    public static List<WB_Segment> getIntersection(final HE_Mesh mesh,
	    final WB_Plane P) {
	return getIntersection(new WB_AABBTree(mesh, 10), P);
    }

    /**
     * 
     *
     * @param mesh 
     * @param P 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final HE_Mesh mesh, final WB_Plane P) {
	return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), P);
    }

    /**
     * 
     *
     * @param mesh 
     * @param R 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final HE_Mesh mesh, final WB_Ray R) {
	return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), R);
    }

    /**
     * 
     *
     * @param mesh 
     * @param L 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final HE_Mesh mesh, final WB_Line L) {
	return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), L);
    }

    /**
     * 
     *
     * @param mesh 
     * @param segment 
     * @return 
     */
    public static List<HE_Face> getPotentialIntersectedFaces(
	    final HE_Mesh mesh, final WB_Segment segment) {
	return getPotentialIntersectedFaces(new WB_AABBTree(mesh, 10), segment);
    }

    /**
     * 
     *
     * @param mesh 
     * @param ray 
     * @return 
     */
    public static HE_FaceIntersection getClosestIntersection(
	    final HE_Mesh mesh, final WB_Ray ray) {
	return getClosestIntersection(new WB_AABBTree(mesh, 10), ray);
    }

    /**
     * 
     *
     * @param mesh 
     * @param ray 
     * @return 
     */
    public static HE_FaceIntersection getFurthestIntersection(
	    final HE_Mesh mesh, final WB_Ray ray) {
	return getFurthestIntersection(new WB_AABBTree(mesh, 10), ray);
    }

    /**
     * 
     *
     * @param mesh 
     * @param line 
     * @return 
     */
    public static HE_FaceIntersection getClosestIntersection(
	    final HE_Mesh mesh, final WB_Line line) {
	return getClosestIntersection(new WB_AABBTree(mesh, 10), line);
    }

    /**
     * 
     *
     * @param mesh 
     * @param line 
     * @return 
     */
    public static HE_FaceIntersection getFurthestIntersection(
	    final HE_Mesh mesh, final WB_Line line) {
	return getFurthestIntersection(new WB_AABBTree(mesh, 10), line);
    }

    /**
     * 
     *
     * @param mesh 
     * @param segment 
     * @return 
     */
    public static HE_FaceIntersection getClosestIntersection(
	    final HE_Mesh mesh, final WB_Segment segment) {
	return getClosestIntersection(new WB_AABBTree(mesh, 10), segment);
    }

    /**
     * 
     *
     * @param mesh 
     * @param segment 
     * @return 
     */
    public static HE_FaceIntersection getFurthestIntersection(
	    final HE_Mesh mesh, final WB_Segment segment) {
	return getFurthestIntersection(new WB_AABBTree(mesh, 10), segment);
    }
}
