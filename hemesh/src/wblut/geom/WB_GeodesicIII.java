package wblut.geom;

public class WB_GeodesicIII {

	public static final int TETRAHEDRON = 0;
	public static final int OCTAHEDRON = 1;
	public static final int ICOSAHEDRON = 2;

	public WB_Point[] flatPPT;
	public WB_Point[] PPT;
	private int b, c, v;
	private WB_FaceListMesh[] meshes;
	private final double radius;
	private final int type;
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();

	public WB_GeodesicIII(double radius, int b, int c, int type) {

		this.type = type;
		this.radius = radius;
		switch (type) {
		case TETRAHEDRON:

			break;
		case OCTAHEDRON:

			break;
		case ICOSAHEDRON:
		default:

		}

		createMeshes();
	}

	public WB_FaceListMesh getMesh(int i) {
		return meshes[i];

	}

	public WB_FaceListMesh[] getMeshes() {
		return meshes;

	}

	private void createMeshes() {

	}

}
