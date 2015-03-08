/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_TriGrid {
    
    /**
     * 
     */
    double scale;
    
    /**
     * 
     */
    double c60 = Math.cos(Math.PI / 3.0);
    
    /**
     * 
     */
    double s60 = Math.sin(Math.PI / 3.0);

    /**
     * 
     */
    public WB_TriGrid() {
	this.scale = 1.0;
    }

    /**
     * 
     *
     * @param scale 
     */
    public WB_TriGrid(final double scale) {
	this.scale = scale;
    }

    /**
     * 
     *
     * @param scale 
     */
    public void setScale(final double scale) {
	this.scale = scale;
    }

    /**
     * 
     *
     * @param b 
     * @param c 
     * @return 
     */
    public WB_Point getPoint(final int b, final int c) {
	return new WB_Point(scale * ((c60 * c) + b), scale * s60 * c, 0);
    }
}
