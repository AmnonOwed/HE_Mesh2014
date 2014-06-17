package wblut.geom;

import java.security.InvalidParameterException;

public class WB_Geodesic {

	public static final int TETRAHEDRON = 0;
	public static final int OCTAHEDRON = 1;
	public static final int CUBE = 2;
	public static final int DODECAHEDRON = 3;
	public static final int ICOSAHEDRON = 4;

	private WB_FaceListMesh mesh;
	private double radius;
	private int type;
	private int b;
	private int c;

	public WB_Geodesic(double radius, int b, int c) {
		this(radius, b, c, ICOSAHEDRON);

	}

	public WB_Geodesic(double radius, int b, int c, int type) {
		if ((b + c) <= 0)
			throw new InvalidParameterException("Invalid values for b and c.");
		this.b = b;
		this.c = c;
		this.type = type;
		this.radius = radius;
	}

	public WB_FaceListMesh getMesh() {
		createMesh();
		return mesh;
	}

	private void createMesh() {
		if (b == c) {
			WB_GeodesicII geo = new WB_GeodesicII(radius, b + c, type);
			mesh = geo.getMesh();
		} else if (b == 0 || c == 0) {
			if (type == 2 || type == 3)
				throw new InvalidParameterException(
						"Invalid type for this class of geodesic.");
			int ltype = (type == 4) ? 2 : type;
			WB_GeodesicI geo = new WB_GeodesicI(radius, b + c, ltype, 1);
			mesh = geo.getMesh();
		} else {
			if (type == 2 || type == 3)
				throw new InvalidParameterException(
						"Invalid type for this class of geodesic.");
			int ltype = (type == 4) ? 2 : type;
			WB_GeodesicIII geo = new WB_GeodesicIII(radius, b, c, ltype);
			mesh = geo.getMesh();

		}
	}
}
