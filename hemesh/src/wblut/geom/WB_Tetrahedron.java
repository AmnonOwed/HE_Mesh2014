/*
 * 
 */
package wblut.geom;

/**
 * 
 */
public class WB_Tetrahedron implements WB_Simplex {
    
    /**
     * 
     */
    WB_Point p1;
    
    /**
     * 
     */
    WB_Point p2;
    
    /**
     * 
     */
    WB_Point p3;
    
    /**
     * 
     */
    WB_Point p4;

    /**
     * 
     */
    protected WB_Tetrahedron() {
    }

    /**
     * 
     */
    public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
	    .instance();

    /**
     * 
     *
     * @param p1 
     * @param p2 
     * @param p3 
     * @param p4 
     */
    protected WB_Tetrahedron(final WB_Coordinate p1, final WB_Coordinate p2,
	    final WB_Coordinate p3, final WB_Coordinate p4) {
	this.p1 = geometryfactory.createPoint(p1);
	this.p2 = geometryfactory.createPoint(p2);
	this.p3 = geometryfactory.createPoint(p3);
	this.p4 = geometryfactory.createPoint(p4);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p1() {
	return p1;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p2() {
	return p2;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p3() {
	return p3;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Point p4() {
	return p4;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#getType()
     */
    @Override
    public WB_GeometryType getType() {
	return WB_GeometryType.TETRAHEDRON;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Simplex#getPoint(int)
     */
    @Override
    public WB_Point getPoint(final int i) {
	if (i == 0) {
	    return p1;
	} else if (i == 1) {
	    return p2;
	} else if (i == 2) {
	    return p3;
	} else if (i == 3) {
	    return p4;
	}
	return null;
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Simplex#getCenter()
     */
    @Override
    public WB_Point getCenter() {
	return geometryfactory.createMidpoint(p1, p2, p3, p3);
    }

    /**
     *  Get the volume of the tetrahedron.
     *
     * @return 
     */
    public double getVolume() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	return Math.abs(a.dot(b.crossSelf(c))) / 6.0;
    }

    /**
     *  Calculate the radius of the circumsphere.
     *
     * @return 
     */
    public double getCircumradius() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	final WB_Vector O = b.cross(c).mulSelf(a.dot(a));
	O.addSelf(c.cross(a).mulSelf(b.dot(b)));
	O.addSelf(a.cross(b).mulSelf(c.dot(c)));
	O.mulSelf(1.0 / (2 * a.dot(b.crossSelf(c))));
	return O.getLength3D();
    }

    /**
     *  Find the center of the circumscribing sphere.
     *
     * @return 
     */
    public WB_Point getCircumcenter() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	final WB_Vector O = b.cross(c).mulSelf(a.dot(a));
	O.addSelf(c.cross(a).mulSelf(b.dot(b)));
	O.addSelf(a.cross(b).mulSelf(c.dot(c)));
	O.mulSelf(1.0 / (2 * a.dot(b.crossSelf(c))));
	return p4.add(O);
    }

    /**
     *  Find the circumscribing sphere.
     *
     * @return 
     */
    public WB_Sphere getCircumsphere() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	final WB_Vector O = b.cross(c).mulSelf(a.dot(a));
	O.addSelf(c.cross(a).mulSelf(b.dot(b)));
	O.addSelf(a.cross(b).mulSelf(c.dot(c)));
	O.mulSelf(1.0 / (2 * a.dot(b.crossSelf(c))));
	return geometryfactory.createSphereWithRadius(p4.add(O),
		O.getLength3D());
    }

    /**
     *  Calculate the radius of the insphere.
     *
     * @return 
     */
    public double getInradius() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	final WB_Vector bXc = b.cross(c);
	final double sixV = Math.abs(a.dot(bXc));
	c.crossSelf(a);
	a.crossSelf(b);
	final double denom = bXc.getLength3D() + c.getLength3D()
		+ a.getLength3D() + (bXc.addMulSelf(2, a).getLength3D());
	return sixV / denom;
    }

    /**
     *  Find the center of the inscribed sphere.
     *
     * @return 
     */
    public WB_Point getIncenter() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	final WB_Vector bXc = b.cross(c);
	final WB_Vector cXa = c.cross(a);
	final WB_Vector aXb = a.cross(b);
	final double bXcLength = bXc.getLength3D();
	final double cXaLength = cXa.getLength3D();
	final double aXbLength = aXb.getLength3D();
	final double dLength = bXc.addSelf(cXa).addSelf(aXb).getLength3D();
	final WB_Vector O = a.mulSelf(bXcLength);
	O.addSelf(b.mulSelf(cXaLength));
	O.addSelf(c.mulSelf(aXbLength));
	O.divSelf(bXcLength + cXaLength + aXbLength + dLength);
	return p4.add(O);
    }

    /**
     * 
     *
     * @return 
     */
    public WB_Sphere getInsphere() {
	final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
	final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
	final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
	final WB_Vector bXc = b.cross(c);
	final WB_Vector cXa = c.cross(a);
	final WB_Vector aXb = a.cross(b);
	final double bXcLength = bXc.getLength3D();
	final double cXaLength = cXa.getLength3D();
	final double aXbLength = aXb.getLength3D();
	final double dLength = bXc.addSelf(cXa).addSelf(aXb).getLength3D();
	final WB_Vector O = a.mulSelf(bXcLength);
	O.addSelf(b.mulSelf(cXaLength));
	O.addSelf(c.mulSelf(aXbLength));
	O.divSelf(bXcLength + cXaLength + aXbLength + dLength);
	return geometryfactory.createSphereWithRadius(p4.add(O),
		O.getLength3D());
    }

    /**
     * 
     *
     * @return 
     */
    public boolean isAcute() {
	return (geometryfactory.getCosDihedralAngle(p1, p2, p3, p4) > 0.0)
		&& (geometryfactory.getCosDihedralAngle(p1, p2, p4, p3) > 0.0)
		&& (geometryfactory.getCosDihedralAngle(p1, p3, p4, p2) > 0.0)
		&& (geometryfactory.getCosDihedralAngle(p3, p1, p2, p4) > 0.0)
		&& (geometryfactory.getCosDihedralAngle(p2, p1, p3, p4) > 0.0)
		&& (geometryfactory.getCosDihedralAngle(p2, p1, p4, p3) > 0.0);
    }

    /* (non-Javadoc)
     * @see wblut.geom.WB_Geometry#apply(wblut.geom.WB_Transform)
     */
    @Override
    public WB_Geometry apply(final WB_Transform T) {
	return geometryfactory.createTetrahedron(p1.applyAsPoint(T),
		p2.applyAsPoint(T), p3.applyAsPoint(T), p4.applyAsPoint(T));
    }
}