/*
 * 
 */
package wblut.geom;

import java.util.List;

/**
 * 
 */
public class WB_VoronoiCell2D {
    
    /**
     * 
     */
    WB_Polygon polygon;
    
    /**
     * 
     */
    int index;
    
    /**
     * 
     */
    double area;
    
    /**
     * 
     */
    WB_Point centroid;
    
    /**
     * 
     */
    WB_Point generator;
    
    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @param points 
     * @param index 
     * @param generator 
     * @param area 
     * @param centroid 
     */
    protected WB_VoronoiCell2D(final WB_CoordinateSequence points,
	    final int index, final WB_Point generator, final double area,
	    final WB_Point centroid) {
	polygon = geometryfactory.createSimplePolygon(points);
	this.index = index;
	this.area = area;
	this.centroid = centroid;
	this.generator = generator;
    }

    /**
     * 
     *
     * @param points 
     * @param index 
     * @param generator 
     * @param area 
     * @param centroid 
     */
    protected WB_VoronoiCell2D(final List<? extends WB_Coordinate> points,
	    final int index, final WB_Point generator, final double area,
	    final WB_Point centroid) {
	polygon = geometryfactory.createSimplePolygon(points);
	this.index = index;
	this.area = area;
	this.centroid = centroid;
	this.generator = generator;
    }

    /**
     * 
     *
     * @param points 
     * @param index 
     * @param generator 
     * @param area 
     * @param centroid 
     */
    protected WB_VoronoiCell2D(final WB_Coordinate[] points, final int index,
	    final WB_Point generator, final double area, final WB_Point centroid) {
	polygon = geometryfactory.createSimplePolygon(points);
	this.index = index;
	this.area = area;
	this.centroid = centroid;
	this.generator = generator;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Polygon getPolygon() {
	return polygon;
    }

    /**
     * 
     *
     * @return 
     */
    public int getIndex() {
	return index;
    }

    /**
     * 
     *
     * @return 
     */
    public double getArea() {
	return area;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point getCentroid() {
	return centroid;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point getGenerator() {
	return generator;
    }
}
