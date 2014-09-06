package wblut.geom;

public class WB_Projection {

	public static WB_Point projectOnPlane(WB_Coordinate p, WB_Plane P) {
		WB_Point projection = new WB_Point(p);
		WB_Vector po = new WB_Vector(P.getOrigin(), p);
		WB_Vector n = P.getNormal();

		return projection.subSelf(n.mulSelf(n.dot(po)));
	}

}
