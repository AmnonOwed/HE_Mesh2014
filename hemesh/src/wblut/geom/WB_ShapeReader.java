/*
 * Conversion of ShapeReader class by Frederik Vanhoutte (W:Blut)
 *
 *
 * The JTS Topology Suite is a collection of Java classes that
 * implement the fundamental operations required to validate a given
 * geo-spatial data set to a known topological specification.
 *
 * Copyright (C) 2001 Vivid Solutions
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * For more information, contact:
 *
 *     Vivid Solutions
 *     Suite #1A
 *     2328 Government Street
 *     Victoria BC  V8T 5G5
 *     Canada
 *
 *     (250)385-6040
 *     www.vividsolutions.com
 */
package wblut.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;
import com.vividsolutions.jts.algorithm.CGAlgorithms;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateList;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Converts a Java2D shape or the more general PathIterator into a List of
 * WB_polygon.
 * <p>
 * The coordinate system for Java2D is typically screen coordinates, which has
 * the Y axis inverted relative to the usual coordinate system.
 * <p>
 * PathIterators to be converted are expected to be linear or flat. That is,
 * they should contain only <tt>SEG_MOVETO</tt>, <tt>SEG_LINETO</tt>, and
 * <tt>SEG_CLOSE</tt> segment types. Any other segment types will cause an
 * exception.
 *
 * @author Martin Davis
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
class WB_ShapeReader {
    
    /**
     * 
     */
    private static AffineTransform INVERT_Y = AffineTransform.getScaleInstance(
	    1, -1);
    
    /**
     * 
     */
    private static GeometryFactory JTSgf = new GeometryFactory();
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     */
    public WB_ShapeReader() {
    }

    /**
     * 
     *
     * @param shp 
     * @param flatness 
     * @return 
     */
    public List<WB_Polygon> read(final Shape shp, final double flatness) {
	final PathIterator pathIt = shp.getPathIterator(INVERT_Y, flatness);
	return read(pathIt);
    }

    /**
     * 
     *
     * @param pathIt 
     * @return 
     */
    public List<WB_Polygon> read(final PathIterator pathIt) {
	final List<Coordinate[]> pathPtSeq = toCoordinates(pathIt);
	final RingTree tree = new RingTree();
	for (int i = 0; i < pathPtSeq.size(); i++) {
	    final Coordinate[] pts = pathPtSeq.get(i);
	    final LinearRing ring = JTSgf.createLinearRing(pts);
	    tree.add(ring);
	}
	return tree.extractPolygons();
    }

    /**
     * 
     *
     * @param pathIt 
     * @return 
     */
    private static List<Coordinate[]> toCoordinates(final PathIterator pathIt) {
	final List<Coordinate[]> coordArrays = new ArrayList<Coordinate[]>();
	while (!pathIt.isDone()) {
	    final Coordinate[] pts = nextCoordinateArray(pathIt);
	    if (pts == null) {
		break;
	    }
	    coordArrays.add(pts);
	}
	return coordArrays;
    }

    /**
     * 
     *
     * @param pathIt 
     * @return 
     */
    @SuppressWarnings("unchecked")
    private static Coordinate[] nextCoordinateArray(final PathIterator pathIt) {
	final double[] pathPt = new double[6];
	CoordinateList coordList = null;
	boolean isDone = false;
	while (!pathIt.isDone()) {
	    final int segType = pathIt.currentSegment(pathPt);
	    switch (segType) {
	    case PathIterator.SEG_MOVETO:
		if (coordList != null) {
		    // don't advance pathIt, to retain start of next path if any
		    isDone = true;
		} else {
		    coordList = new CoordinateList();
		    coordList.add(new Coordinate(pathPt[0], pathPt[1]));
		    pathIt.next();
		}
		break;
	    case PathIterator.SEG_LINETO:
		coordList.add(new Coordinate(pathPt[0], pathPt[1]));
		pathIt.next();
		break;
	    case PathIterator.SEG_CLOSE:
		coordList.closeRing();
		pathIt.next();
		isDone = true;
		break;
	    default:
		throw new IllegalArgumentException(
			"unhandled (non-linear) segment type encountered");
	    }
	    if (isDone) {
		break;
	    }
	}
	return coordList.toCoordinateArray();
    }

    // The JTS implementation of ShapeReader does not handle overlapping
    // polygons well. All code below this point is my solution for this. A
    // hierarchical tree that orders rings from the outside in. All input has to
    // be well-ordered: CW for shell, CCW for hole.
    /**
     * 
     */
    private static class RingNode {
	
	/**
	 * 
	 */
	@SuppressWarnings("unused")
	RingNode parent;
	
	/**
	 * 
	 */
	List<RingNode> children;
	
	/**
	 * 
	 */
	LinearRing ring;
	
	/**
	 * 
	 */
	Polygon poly;// redundant, but useful for within/contains checks
	
	/**
	 * 
	 */
	boolean hole;

	/**
	 * 
	 */
	RingNode() {
	    parent = null;
	    ring = null;
	    children = new ArrayList<RingNode>();
	    hole = true;
	}

	/**
	 * 
	 *
	 * @param parent 
	 * @param ring 
	 */
	RingNode(final RingNode parent, final LinearRing ring) {
	    this.parent = parent;
	    this.ring = ring;
	    final Coordinate[] coords = ring.getCoordinates();
	    poly = JTSgf.createPolygon(coords);
	    hole = CGAlgorithms.isCCW(coords);
	    children = new ArrayList<RingNode>();
	}
    }

    /**
     * 
     */
    private static class RingTree {
	
	/**
	 * 
	 */
	RingNode root;

	/**
	 * 
	 */
	RingTree() {
	    root = new RingNode();
	}

	/**
	 * 
	 *
	 * @param ring 
	 */
	void add(final LinearRing ring) {
	    final Polygon poly = JTSgf.createPolygon(ring);
	    RingNode currentParent = root;
	    RingNode foundParent;
	    do {
		foundParent = null;
		for (int i = 0; i < currentParent.children.size(); i++) {
		    final RingNode node = currentParent.children.get(i);
		    final Polygon other = node.poly;
		    if (poly.within(other)) {
			foundParent = node;
			currentParent = node;
			break;
		    }
		}
	    } while (foundParent != null);
	    final RingNode newNode = new RingNode(currentParent, ring);
	    final List<RingNode> nodesToRemove = new ArrayList<RingNode>();
	    for (int i = 0; i < currentParent.children.size(); i++) {
		final RingNode node = currentParent.children.get(i);
		final Polygon other = node.poly;
		if (other.within(poly)) {
		    newNode.children.add(node);
		    nodesToRemove.add(node);
		}
	    }
	    currentParent.children.removeAll(nodesToRemove);
	    currentParent.children.add(newNode);
	}

	/**
	 * 
	 *
	 * @return 
	 */
	List<WB_Polygon> extractPolygons() {
	    final List<WB_Polygon> polygons = new ArrayList<WB_Polygon>();
	    final List<RingNode> shellNodes = new ArrayList<RingNode>();
	    addExteriorNodes(root, shellNodes);
	    for (final RingNode node : shellNodes) {
		int count = 0;
		for (int i = 0; i < node.children.size(); i++) {
		    if (node.children.get(i).hole) {
			count++;
		    }
		}
		final LinearRing[] holes = new LinearRing[count];
		int index = 0;
		for (int i = 0; i < node.children.size(); i++) {
		    if (node.children.get(i).hole) {
			holes[index++] = node.children.get(i).ring;
		    }
		}
		final Geometry result = JTSgf.createPolygon(node.ring, holes);
		if (result.getGeometryType().equals("Polygon")) {
		    polygons.add(geometryfactory
			    .createPolygonFromJTSPolygon((Polygon) result));
		} else if (result.getGeometryType().equals("MultiPolygon")) {
		    for (int j = 0; j < result.getNumGeometries(); j++) {
			final Geometry ggeo = result.getGeometryN(j);
			polygons.add(geometryfactory
				.createPolygonFromJTSPolygon((Polygon) ggeo));
		    }
		}
	    }
	    return polygons;
	}

	/**
	 * 
	 *
	 * @param parent 
	 * @param shellNodes 
	 */
	void addExteriorNodes(final RingNode parent,
		final List<RingNode> shellNodes) {
	    for (final RingNode node : parent.children) {
		if (node.hole == false) {
		    shellNodes.add(node);
		}
		addExteriorNodes(node, shellNodes);
	    }
	}
    }
}
