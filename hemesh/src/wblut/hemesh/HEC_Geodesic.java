package wblut.hemesh;

import wblut.geom.WB_FaceListMesh;
import wblut.geom.WB_Geodesic;

public class HEC_Geodesic extends HEC_Creator {

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

	public HEC_Geodesic() {
		super();
		radius = 0f;
		type = 4;
		b = c = 4;
	}

	public HEC_Geodesic(final double R) {
		this();
		this.radius = R;
		b = c = 4;
	}

	public HEC_Geodesic setRadius(final double R) {
		this.radius = R;
		return this;
	}

	public HEC_Geodesic setB(final int b) {
		this.b = b;
		return this;
	}

	public HEC_Geodesic setC(final int c) {
		this.c = c;
		return this;
	}

	public HEC_Geodesic setType(final int t) {
		type = t;
		return this;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {

		final WB_Geodesic geo = new WB_Geodesic(radius, b, c, type);
		return new HE_Mesh(new HEC_FromMesh(geo));
	}
}
