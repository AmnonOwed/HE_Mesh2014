/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;
import wblut.geom.WB_AABB;
import wblut.geom.WB_AABBTree;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Point;
import wblut.geom.WB_Ray;
import wblut.geom.WB_Vector;
import wblut.math.WB_Epsilon;

/**
 * 
 */
public class HEC_ShrinkWrap extends HEC_Creator {
    
    /**
     * 
     */
    private HE_Mesh source;
    
    /**
     * 
     */
    private int level;
    
    /**
     * 
     */
    private WB_Point wcenter;
    
    /**
     * 
     */
    private WB_AABBTree tree;

    /**
     * 
     */
    public HEC_ShrinkWrap() {
	super();
	override = true;
	toModelview = false;
	level = 2;
    }

    /**
     * 
     *
     * @param mesh 
     * @return 
     */
    public HEC_ShrinkWrap setSource(final HE_Mesh mesh) {
	source = mesh;
	return this;
    }

    /**
     * 
     *
     * @param mesh 
     * @param tree 
     * @return 
     */
    public HEC_ShrinkWrap setSource(final HE_Mesh mesh, final WB_AABBTree tree) {
	source = mesh;
	this.tree = tree;
	return this;
    }

    /**
     * 
     *
     * @param level 
     * @return 
     */
    public HEC_ShrinkWrap setLevel(final int level) {
	this.level = level;
	return this;
    }

    /**
     * 
     *
     * @param c 
     * @return 
     */
    public HEC_ShrinkWrap setWrapCenter(final WB_Point c) {
	wcenter = c;
	return this;
    }

    /**
     * 
     *
     * @param x 
     * @param y 
     * @param z 
     * @return 
     */
    public HEC_ShrinkWrap setWrapCenter(final double x, final double y,
	    final double z) {
	wcenter = new WB_Point(x, y, z);
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Creator#create()
     */
    @Override
    public HE_Mesh createBase() {
	HE_Mesh result = new HE_Mesh();
	if (source == null) {
	    return result;
	}
	final WB_AABB aabb = source.getAABB();
	if (wcenter == null) {
	    wcenter = aabb.getCenter();
	}
	final double radius = WB_GeometryOp
		.getDistance3D(center, aabb.getMax()) + WB_Epsilon.EPSILON;
	final HE_Mesh sphere = new HE_Mesh(new HEC_Geodesic().setB(level)
		.setC(0).setRadius(radius).setCenter(wcenter));
	result = sphere.get();
	final Iterator<HE_Vertex> vItr = sphere.vItr();
	final Iterator<HE_Vertex> vmodItr = result.vItr();
	HE_Vertex v, vmod;
	WB_Ray R;
	if (tree == null) {
	    tree = new WB_AABBTree(source, 4);
	}
	ArrayList<HE_Vertex> undecided = new ArrayList<HE_Vertex>();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    vmod = vmodItr.next();
	    R = new WB_Ray(v, v.getVertexNormal().mulSelf(-1));
	    final WB_Point p = HE_Intersection.getClosestIntersection(tree, R).point;
	    if (p != null) {
		if (WB_GeometryOp.getDistance3D(v, p) < radius) {
		    vmod.set(p);
		} else {
		    undecided.add(vmod);
		}
	    } else {
		undecided.add(vmod);
	    }
	}
	ArrayList<HE_Vertex> newundecided;
	while (undecided.size() > 0) {
	    newundecided = new ArrayList<HE_Vertex>();
	    for (int i = 0; i < undecided.size(); i++) {
		v = undecided.get(i);
		boolean lost = true;
		int decNeighbors = 0;
		double dist = 0;
		for (final HE_Vertex n : v.getNeighborVertices()) {
		    if (!undecided.contains(n)) {
			lost = false;
			dist += WB_GeometryOp.getDistance3D(wcenter, n);
			decNeighbors++;
		    }
		}
		if (lost) {
		    newundecided.add(v);
		} else {
		    dist /= decNeighbors;
		    final WB_Vector dv = v.getPoint().subToVector3D(wcenter);
		    dv.normalizeSelf();
		    v.set(wcenter.addMul(dist, dv));
		}
	    }
	    if (undecided.size() == newundecided.size()) {
		break;
	    }
	    undecided = newundecided;
	}
	return result;
    }
}
