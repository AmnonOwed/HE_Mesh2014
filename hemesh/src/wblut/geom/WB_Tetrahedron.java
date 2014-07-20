package wblut.geom;

public class WB_Tetrahedron implements WB_Simplex {

	WB_Point p1;

	WB_Point p2;

	WB_Point p3;

	WB_Point p4;

	protected WB_Tetrahedron() {

	}

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	protected WB_Tetrahedron(final WB_Coordinate p1, final WB_Coordinate p2,
			final WB_Coordinate p3, final WB_Coordinate p4) {
		this.p1 = geometryfactory.createPoint(p1);
		this.p2 = geometryfactory.createPoint(p2);
		this.p3 = geometryfactory.createPoint(p3);
		this.p4 = geometryfactory.createPoint(p4);
	}

	public WB_Point p1() {
		return p1;
	}

	public WB_Point p2() {
		return p2;
	}

	public WB_Point p3() {
		return p3;
	}

	public WB_Point p4() {
		return p4;
	}

	@Override
	public WB_GeometryType getType() {
		return WB_GeometryType.TETRAHEDRON;
	}

	@Override
	public int getDimension() {
		return 3;
	}

	@Override
	public int getEmbeddingDimension() {
		return 3;
	}

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

	@Override
	public WB_Point getCenter() {
		return geometryfactory.createMidpoint(p1, p2, p3, p3);
	}

	/** Get the volume of the tetrahedron. */

	public double getVolume() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		return Math.abs(a.dot(b._crossSelf(c))) / 6.0;
	}

	/** Calculate the radius of the circumsphere. */

	public double getCircumradius() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		final WB_Vector O = b.cross(c)._mulSelf(a.dot(a));
		O._addSelf(c.cross(a)._mulSelf(b.dot(b)));
		O._addSelf(a.cross(b)._mulSelf(c.dot(c)));
		O._mulSelf(1.0 / (2 * a.dot(b._crossSelf(c))));
		return O.getLength();
	}

	/** Find the center of the circumscribing sphere. */

	public WB_Point getCircumcenter() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		final WB_Vector O = b.cross(c)._mulSelf(a.dot(a));
		O._addSelf(c.cross(a)._mulSelf(b.dot(b)));
		O._addSelf(a.cross(b)._mulSelf(c.dot(c)));
		O._mulSelf(1.0 / (2 * a.dot(b._crossSelf(c))));
		return p4.add(O);
	}

	/** Find the circumscribing sphere */

	public WB_Sphere getCircumsphere() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		final WB_Vector O = b.cross(c)._mulSelf(a.dot(a));
		O._addSelf(c.cross(a)._mulSelf(b.dot(b)));
		O._addSelf(a.cross(b)._mulSelf(c.dot(c)));
		O._mulSelf(1.0 / (2 * a.dot(b._crossSelf(c))));
		return geometryfactory.createSphereWithRadius(p4.add(O), O.getLength());
	}

	/** Calculate the radius of the insphere. */

	public double getInradius() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		final WB_Vector bXc = b.cross(c);
		final double sixV = Math.abs(a.dot(bXc));
		c._crossSelf(a);
		a._crossSelf(b);
		final double denom = bXc.getLength() + c.getLength() + a.getLength()
				+ (bXc._addMulSelf(2, a).getLength());
		return sixV / denom;
	}

	/** Find the center of the inscribed sphere. */

	public WB_Point getIncenter() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		final WB_Vector bXc = b.cross(c);
		final WB_Vector cXa = c.cross(a);
		final WB_Vector aXb = a.cross(b);
		final double bXcLength = bXc.getLength();
		final double cXaLength = cXa.getLength();
		final double aXbLength = aXb.getLength();
		final double dLength = bXc._addSelf(cXa)._addSelf(aXb).getLength();
		final WB_Vector O = a._mulSelf(bXcLength);
		O._addSelf(b._mulSelf(cXaLength));
		O._addSelf(c._mulSelf(aXbLength));
		O._divSelf(bXcLength + cXaLength + aXbLength + dLength);
		return p4.add(O);
	}

	public WB_Sphere getInsphere() {
		final WB_Vector a = geometryfactory.createVectorFromTo(p1, p4);
		final WB_Vector b = geometryfactory.createVectorFromTo(p2, p4);
		final WB_Vector c = geometryfactory.createVectorFromTo(p3, p4);
		final WB_Vector bXc = b.cross(c);
		final WB_Vector cXa = c.cross(a);
		final WB_Vector aXb = a.cross(b);
		final double bXcLength = bXc.getLength();
		final double cXaLength = cXa.getLength();
		final double aXbLength = aXb.getLength();
		final double dLength = bXc._addSelf(cXa)._addSelf(aXb).getLength();
		final WB_Vector O = a._mulSelf(bXcLength);
		O._addSelf(b._mulSelf(cXaLength));
		O._addSelf(c._mulSelf(aXbLength));
		O._divSelf(bXcLength + cXaLength + aXbLength + dLength);
		return geometryfactory.createSphereWithRadius(p4.add(O), O.getLength());

	}

	public boolean isAcute() {
		return (geometryfactory.getCosDihedralAngle(p1, p2, p3, p4) > 0.0)
				&& (geometryfactory.getCosDihedralAngle(p1, p2, p4, p3) > 0.0)
				&& (geometryfactory.getCosDihedralAngle(p1, p3, p4, p2) > 0.0)
				&& (geometryfactory.getCosDihedralAngle(p3, p1, p2, p4) > 0.0)
				&& (geometryfactory.getCosDihedralAngle(p2, p1, p3, p4) > 0.0)
				&& (geometryfactory.getCosDihedralAngle(p2, p1, p4, p3) > 0.0);
	}

	@Override
	public WB_Geometry apply(final WB_Transform T) {
		return geometryfactory.createTetrahedron(p1.applyAsPoint(T),
				p2.applyAsPoint(T), p3.applyAsPoint(T), p4.applyAsPoint(T));
	}
}