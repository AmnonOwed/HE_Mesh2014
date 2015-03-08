/*
 * 
 */
package wblut.geom;

import java.security.InvalidParameterException;

/**
 * 
 */
public class WB_Geodesic implements WB_MeshCreator {
    
    /**
     * 
     */
    public static final int TETRAHEDRON = 0;
    
    /**
     * 
     */
    public static final int OCTAHEDRON = 1;
    
    /**
     * 
     */
    public static final int CUBE = 2;
    
    /**
     * 
     */
    public static final int DODECAHEDRON = 3;
    
    /**
     * 
     */
    public static final int ICOSAHEDRON = 4;
    
    /**
     * 
     */
    private WB_FaceListMesh mesh;
    
    /**
     * 
     */
    private final double radius;
    
    /**
     * 
     */
    private final int type;
    
    /**
     * 
     */
    private final int b;
    
    /**
     * 
     */
    private final int c;

    /**
     * 
     *
     * @param radius 
     * @param b 
     * @param c 
     */
    public WB_Geodesic(final double radius, final int b, final int c) {
	this(radius, b, c, ICOSAHEDRON);
    }

    /**
     * 
     *
     * @param radius 
     * @param b 
     * @param c 
     * @param type 
     */
    public WB_Geodesic(final double radius, final int b, final int c,
	    final int type) {
	if (((b + c) == 0) || (b < 0) || (c < 0)) {
	    throw new InvalidParameterException("Invalid values for b and c.");
	}
	this.b = b;
	this.c = c;
	this.type = type;
	this.radius = radius;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_MeshCreator#getMesh()
     */
    @Override
    public WB_FaceListMesh getMesh() {
	createMesh();
	return mesh;
    }

    /**
     * 
     */
    private void createMesh() {
	if (b == c) {
	    final WB_GeodesicII geo = new WB_GeodesicII(radius, b + c, type);
	    mesh = geo.getMesh();
	} else if ((b == 0) || (c == 0)) {
	    if ((type == 2) || (type == 3)) {
		throw new InvalidParameterException(
			"Invalid type for this class of geodesic.");
	    }
	    final int ltype = (type == 4) ? 2 : type;
	    final WB_GeodesicI geo = new WB_GeodesicI(radius, b + c, ltype, 1);
	    mesh = geo.getMesh();
	} else {
	    if ((type == 2) || (type == 3)) {
		throw new InvalidParameterException(
			"Invalid type for this class of geodesic.");
	    }
	    final int ltype = (type == 4) ? 2 : type;
	    final WB_GeodesicIII geo = new WB_GeodesicIII(radius, b, c, ltype);
	    mesh = geo.getMesh();
	}
    }
}
