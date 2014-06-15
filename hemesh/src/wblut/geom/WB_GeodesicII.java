package wblut.geom;

public class WB_GeodesicII {

	public static final int TETRAHEDRON = 0;
	public static final int OCTAHEDRON = 1;
	public static final int CUBE = 2;
	public static final int DODECAHEDRON = 3;
	public static final int ICOSAHEDRON = 4;

	private double[][] centralanglesabc;
	private static double PI = Math.PI;
	private static double[][] surfaceanglesABC = new double[][] {
			{ PI / 3.0, PI / 3.0, PI / 2.0 }, { PI / 3.0, PI / 4.0, PI / 2.0 },
			{ PI / 4.0, PI / 3.0, PI / 2.0 }, { PI / 5.0, PI / 3.0, PI / 2.0 },
			{ PI / 3.0, PI / 5.0, PI / 2.0 } };

	public WB_Point[][] LCDPoints;
	public WB_Point[] triacon;
	private WB_Plane P;
	private int v, hv;
	private WB_FaceListMesh mesh;
	private static WB_GeometryFactory gf = WB_GeometryFactory.instance();

	private double radius;
	private int type;
	private boolean flat;

	public WB_GeodesicII(double radius, int v) {
		this(radius, v, ICOSAHEDRON);

	}

	public WB_GeodesicII(double radius, int v, int type) {
		assert (v > 0);
		this.type = type;
		this.radius = radius;
		this.v = (v / 2) * 2;
		hv = this.v / 2;
		centralanglesabc = new double[5][3];
		for (int i = 0; i < 5; i++) {
			centralanglesabc[i][0] = Math.acos(Math.cos(surfaceanglesABC[i][0])
					/ Math.sin(surfaceanglesABC[i][1]));// cos a = cos A / sin B
			centralanglesabc[i][1] = Math.acos(Math.cos(surfaceanglesABC[i][1])
					/ Math.sin(surfaceanglesABC[i][0]));// cos b = cos B / sin A
			centralanglesabc[i][2] = Math.acos(Math.cos(centralanglesabc[i][0])
					* Math.cos(centralanglesabc[i][1]));// cos c = cos a x cos b
		}
	}

	public WB_FaceListMesh getMesh() {
		createMesh();
		return mesh;
	}

	public void setFlat(boolean b) {
		flat = b;
	}

	private void createMesh() {
		LCDPoints = new WB_Point[hv + 1][hv + 1];
		LCDPoints[0][0] = new WB_Point(0, 0, radius);
		for (int i = 1; i < hv; i++) {
			LCDPoints[i][0] = new WB_Point(0, 0, radius);
			LCDPoints[i][0].rotateAboutAxis(i * centralanglesabc[type][0] / hv,
					0, 0, 0, 0, 1, 0);
		}
		LCDPoints[hv][0] = new WB_Point(0, 0, radius);
		LCDPoints[hv][0].rotateAboutAxis(centralanglesabc[type][0], 0, 0, 0, 0,
				1, 0);

		double[][] subtrianglesabc = new double[hv][3];
		double[][] subtrianglesABC = new double[hv][3];
		for (int i = 0; i < hv - 1; i++) {
			subtrianglesabc[i][0] = (i + 1) * centralanglesabc[type][0] / hv;
			subtrianglesABC[i][1] = surfaceanglesABC[type][1];
			subtrianglesABC[i][2] = surfaceanglesABC[type][2];
			subtrianglesABC[i][0] = Math.acos(Math.cos(subtrianglesabc[i][0])
					* Math.sin(subtrianglesABC[i][1])); // cos A = cos a x sin B
			subtrianglesabc[i][1] = Math.acos(Math.cos(subtrianglesABC[i][1])
					/ Math.sin(subtrianglesABC[i][0])); // cos b = cos B / sin A
			subtrianglesabc[i][2] = Math.acos(Math.cos(subtrianglesabc[i][0])
					* Math.cos(subtrianglesabc[i][1]));// cos c = cos a x cos b
		}

		for (int i = 1; i < hv; i++) {
			LCDPoints[0][i] = new WB_Point(0, 0, radius);
			LCDPoints[0][i].rotateAboutAxis(subtrianglesabc[i - 1][1], 0, 0, 0,
					1, 0, 0);
		}
		LCDPoints[0][hv] = new WB_Point(0, 0, radius);
		LCDPoints[0][hv].rotateAboutAxis(centralanglesabc[type][1], 0, 0, 0, 1,
				0, 0);

		for (int i = 1; i < hv; i++) {
			for (int j = 1; j <= hv - i; j++) {
				LCDPoints[i][j] = new WB_Point(LCDPoints[i][0]);
				LCDPoints[i][j].rotateAboutAxis(subtrianglesabc[j - 1][1], 0,
						0, 0, 1, 0, 0);
			}
		}
		triacon = new WB_Point[(hv + 1) * (hv + 1)];
		int oddeven = hv % 2;

		double qv = hv / 2.0;
		for (int i = 0; i <= hv; i++) {
			for (int j = 0; j <= hv - i; j++) {
				if ((i + j) % 2 == oddeven) {
					triacon[index(qv + (i - j) / 2.0, qv + (i + j) / 2.0, hv)] = LCDPoints[i][j];

					if (j != 0) {

						triacon[index(qv + (i + j) / 2.0, qv + (i - j) / 2.0,
								hv)] = new WB_Point(LCDPoints[i][j].xd(),
								-LCDPoints[i][j].yd(), LCDPoints[i][j].zd());
					}
					if (i != 0) {
						triacon[index(qv - (i + j) / 2.0, qv - (i - j) / 2.0,
								hv)] = new WB_Point(-LCDPoints[i][j].xd(),
								LCDPoints[i][j].yd(), LCDPoints[i][j].zd());
					}
					if ((i != 0) && (j != 0)) {
						triacon[index(qv - (i - j) / 2.0, qv - (i + j) / 2.0,
								hv)] = new WB_Point(-LCDPoints[i][j].xd(),
								-LCDPoints[i][j].yd(), LCDPoints[i][j].zd());
					}
				}

			}
		}

		int[][] faces = new int[2 * hv * hv][];
		int index = 0;
		for (int i = 0; i < hv; i++) {
			for (int j = 0; j < hv; j++) {
				faces[index++] = new int[] { index(i, j, hv),
						index(i + 1, j, hv), index(i, j + 1, hv) };
				faces[index++] = new int[] { index(i, j + 1, hv),
						index(i + 1, j, hv), index(i + 1, j + 1, hv) };
			}
		}

		mesh = gf.createMesh(triacon, faces);

	}

	private int index(double i, double j, int hv) {
		return (int) i + (int) j * (hv + 1);

	}

}
