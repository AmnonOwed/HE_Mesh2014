/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javolution.util.FastMap;
import wblut.geom.WB_Coordinate;
import wblut.geom.WB_GeometryFactory;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.math.WB_ConstantParameter;
import wblut.math.WB_Parameter;

// TODO: Auto-generated Javadoc
/**
 * Catmull-Clark subdivision of a mesh.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HES_CatmullClark extends HES_Subdividor {
    
    /**
     * 
     */
    private static WB_GeometryFactory gf = WB_GeometryFactory.instance();
    /** Keep edges?. */
    private boolean keepEdges;
    /** Keep boundary?. */
    private boolean keepBoundary = false;
    /** The blend factor. */
    private WB_Parameter<Double> blendFactor;

    /**
     * Instantiates a new hE s_ catmull clark.
     */
    public HES_CatmullClark() {
	super();
	blendFactor = new WB_ConstantParameter<Double>(1.0);
    }

    /**
     * Keep edges of selection fixed when subdividing selection?.
     *
     * @param b
     *            true/false
     * @return self
     */
    public HES_CatmullClark setKeepEdges(final boolean b) {
	keepEdges = b;
	return this;
    }

    /**
     * Keep boundary edges fixed?.
     *
     * @param b
     *            true/false
     * @return self
     */
    public HES_CatmullClark setKeepBoundary(final boolean b) {
	keepBoundary = b;
	return this;
    }

    /**
     * Sets the blend factor.
     *
     * @param f
     *            the f
     * @return the hE s_ catmull clark
     */
    public HES_CatmullClark setBlendFactor(final double f) {
	blendFactor = new WB_ConstantParameter<Double>(f);
	return this;
    }

    /**
     * Sets the blend factor.
     *
     * @param f
     *            the f
     * @return the hE s_ catmull clark
     */
    public HES_CatmullClark setBlendFactor(final WB_Parameter<Double> f) {
	blendFactor = f;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	tracker.setDefaultStatus("Starting HES_CatmullClark");
	mesh.resetVertexInternalLabels();
	final HashMap<Long, WB_Point> avgFC = new HashMap<Long, WB_Point>();
	HE_Vertex v;
	Iterator<HE_Vertex> vItr = mesh.vItr();
	HE_Halfedge he;
	WB_Point p;
	tracker.setDefaultStatus("Creating averaged face center points.",
		mesh.getNumberOfVertices());
	while (vItr.hasNext()) {
	    v = vItr.next();
	    he = v.getHalfedge();
	    final WB_Point afc = new WB_Point();
	    int c = 0;
	    do {
		if (he.getFace() != null) {
		    afc.addSelf(he.getFace().getFaceCenter());
		    c++;
		}
		he = he.getNextInVertex();
	    } while (he != v.getHalfedge());
	    afc.divSelf(c);
	    avgFC.put(v.key(), afc);
	    tracker.incrementCounter();
	}
	mesh.splitFacesQuad();
	final FastMap<Long, WB_Coordinate> newPositions = new FastMap<Long, WB_Coordinate>();
	final HE_Selection all = mesh.selectAllFaces();
	final List<HE_Vertex> boundary = all.getOuterVertices();
	final List<HE_Vertex> inner = all.getInnerVertices();
	tracker.setDefaultStatus("Creating new positions for inner vertices.",
		inner.size());
	HE_Vertex n;
	List<HE_Vertex> neighbors;
	vItr = inner.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getInternalLabel() == -1) {
		p = avgFC.get(v.key());
		neighbors = v.getNeighborVertices();
		final int order = neighbors.size();
		final double io = 1.0 / order;
		for (int i = 0; i < order; i++) {
		    n = neighbors.get(i);
		    p.addSelf(2.0 * io * n.xd(), 2.0 * io * n.yd(), 2.0 * io
			    * n.zd());
		}
		p.addMulSelf(order - 3, v);
		p.divSelf(order);
		newPositions.put(
			v.key(),
			gf.createInterpolatedPoint(v, p,
				blendFactor.value(v.xd(), v.yd(), v.zd())));
	    } else {
		p = new WB_Point();
		neighbors = v.getNeighborVertices();
		final int order = neighbors.size();
		boolean edgePoint = false;
		for (int i = 0; i < order; i++) {
		    n = neighbors.get(i);
		    p.addSelf(n);
		    if (n.getInternalLabel() == -1) {
			edgePoint = true;
		    }
		}
		p.divSelf(order);
		if (edgePoint) {
		    newPositions.put(
			    v.key(),
			    gf.createInterpolatedPoint(v, p,
				    blendFactor.value(v.xd(), v.yd(), v.zd())));
		} else {
		    newPositions.put(v.key(), v);
		}
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus(
		"Creating new positions for boundary vertices.",
		boundary.size());
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (keepBoundary) {
		newPositions.put(v.key(), v);
	    } else {
		p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		double c = 1;
		int nc = 0;
		for (int i = 0; i < neighbors.size(); i++) {
		    n = neighbors.get(i);
		    if (boundary.contains(n)) {
			p.addSelf(n);
			nc++;
			c++;
		    }
		}
		newPositions.put(
			v.key(),
			(nc > 1) ? gf.createInterpolatedPoint(v,
				p.scaleSelf(1.0 / c),
				blendFactor.value(v.xd(), v.yd(), v.zd())) : v);
	    }
	    tracker.incrementCounter();
	}
	tracker.setDefaultStatus("Setting new positions.");
	vItr = inner.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(newPositions.get(v.key()));
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(newPositions.get(v.key()));
	}
	tracker.setDefaultStatus("Exiting HEM_CatmullClark.");
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.hemesh.subdividors.HEB_Subdividor#subdivideSelected(wblut.hemesh
     * .HE_Mesh, wblut.hemesh.HE_Selection)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	selection.parent.resetVertexInternalLabels();
	final HashMap<Long, WB_Point> avgFC = new HashMap<Long, WB_Point>();
	HE_Vertex v;
	Iterator<HE_Vertex> vItr = selection.parent.vItr();
	HE_Halfedge he;
	WB_Point p;
	while (vItr.hasNext()) {
	    v = vItr.next();
	    he = v.getHalfedge();
	    final WB_Point afc = new WB_Point();
	    int c = 0;
	    do {
		if (he.getFace() != null) {
		    if (selection.contains(he.getFace())) {
			afc.addSelf(he.getFace().getFaceCenter());
			c++;
		    }
		}
		he = he.getNextInVertex();
	    } while (he != v.getHalfedge());
	    afc.divSelf(c);
	    avgFC.put(v.key(), afc);
	}
	selection.parent.splitFacesQuad(selection);
	final FastMap<Long, WB_Coordinate> newPositions = new FastMap<Long, WB_Coordinate>();
	selection.collectVertices();
	final List<HE_Vertex> boundary = selection.getBoundaryVertices();
	final List<HE_Vertex> outer = selection.getOuterVertices();
	final List<HE_Vertex> inner = selection.getInnerVertices();
	List<HE_Face> sharedFaces;
	vItr = outer.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (boundary.contains(v)) {
		vItr.remove();
	    }
	}
	HE_Vertex n;
	List<HE_Vertex> neighbors;
	vItr = inner.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (v.getInternalLabel() == -1) {
		p = avgFC.get(v.key());
		neighbors = v.getNeighborVertices();
		final int order = neighbors.size();
		final double io = 1.0 / order;
		for (int i = 0; i < order; i++) {
		    n = neighbors.get(i);
		    p.addSelf(2.0 * io * n.xd(), 2.0 * io * n.yd(), 2.0 * io
			    * n.zd());
		}
		p.addMulSelf(order - 3, v);
		p.divSelf(order);
		newPositions.put(
			v.key(),
			gf.createInterpolatedPoint(v, p,
				blendFactor.value(v.xd(), v.yd(), v.zd())));
	    } else {
		p = new WB_Point();
		neighbors = v.getNeighborVertices();
		final int order = neighbors.size();
		boolean edgePoint = false;
		for (int i = 0; i < order; i++) {
		    n = neighbors.get(i);
		    p.addSelf(n);
		    if (n.getInternalLabel() == -1) {
			edgePoint = true;
		    }
		}
		p.divSelf(order);
		if (edgePoint) {
		    newPositions.put(
			    v.key(),
			    gf.createInterpolatedPoint(v, p,
				    blendFactor.value(v.xd(), v.yd(), v.zd())));
		} else {
		    newPositions.put(v.key(), v);
		}
	    }
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    if (keepBoundary) {
		newPositions.put(v.key(), v);
	    } else {
		p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		double c = 1;
		int nc = 0;
		for (int i = 0; i < neighbors.size(); i++) {
		    n = neighbors.get(i);
		    if ((boundary.contains(n)) && (selection.contains(n))) {
			p.addSelf(n);
			nc++;
			c++;
		    }
		}
		newPositions.put(
			v.key(),
			(nc > 1) ? gf.createInterpolatedPoint(v,
				p.scaleSelf(1.0 / c),
				blendFactor.value(v.xd(), v.yd(), v.zd())) : v);
	    }
	}
	List<WB_Plane> planes;
	List<HE_Face> faceStar;
	HE_Face f;
	WB_Plane P;
	vItr = outer.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    planes = new ArrayList<WB_Plane>();
	    if (keepEdges) {
		newPositions.put(v.key(), v);
	    } else {
		faceStar = v.getFaceStar();
		for (int i = 0; i < faceStar.size(); i++) {
		    f = faceStar.get(i);
		    if (!selection.contains(f)) {
			P = f.toPlane();
			boolean unique = true;
			for (int j = 0; j < planes.size(); j++) {
			    if (WB_Plane.isEqual(planes.get(j), P)) {
				unique = false;
				break;
			    }
			}
			if (unique) {
			    planes.add(P);
			}
		    }
		}
		p = new WB_Point(v);
		neighbors = v.getNeighborVertices();
		double c = 1;
		int nc = 0;
		for (int i = 0; i < neighbors.size(); i++) {
		    n = neighbors.get(i);
		    if (outer.contains(n)) {
			sharedFaces = selection.parent.getSharedFaces(v, n);
			boolean singleFaceGap = true;
			for (int j = 0; j < sharedFaces.size(); j++) {
			    if (selection.contains(sharedFaces.get(j))) {
				singleFaceGap = false;
				break;
			    }
			}
			if (!singleFaceGap) {
			    p.addSelf(n);
			    c++;
			    nc++;
			}
		    }
		}
		if (nc > 1) {
		    p.scaleSelf(1.0 / c);
		    if (planes.size() == 1) {
			p = WB_GeometryOp.getClosestPoint3D(p, planes.get(0));
		    }/*
		     * else if (planes.size() == 2) { final WB_Line L =
		     * WB_Intersect.intersect(planes.get(0), planes.get(1)).L;
		     * p = WB_ClosestPoint.getClosestPoint(p, L); p.set(v); }
		     */
		    else {
			p.set(v);
		    }
		} else {
		    p.set(v);
		}
		newPositions.put(
			v.key(),
			gf.createInterpolatedPoint(v, p,
				blendFactor.value(v.xd(), v.yd(), v.zd())));
	    }
	}
	vItr = inner.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(newPositions.get(v.key()));
	}
	vItr = boundary.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(newPositions.get(v.key()));
	}
	vItr = outer.iterator();
	while (vItr.hasNext()) {
	    v = vItr.next();
	    v.set(newPositions.get(v.key()));
	}
	return selection.parent;
    }
}
