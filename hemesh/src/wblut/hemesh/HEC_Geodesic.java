package wblut.hemesh;

import java.util.Iterator;

import wblut.geom.WB_Distance;
import wblut.geom.WB_Point;

public class HEC_Geodesic extends HEC_Creator {

	private double R;

	private int level;

	private int type;

	public HEC_Geodesic() {
		super();
		R = 0f;
	}

	public HEC_Geodesic(final double R, final int L) {
		this();
		this.R = R;
		level = L;
	}

	public HEC_Geodesic setRadius(final double R) {
		this.R = R;
		return this;
	}

	public HEC_Geodesic setLevel(final int L) {
		level = L;
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

		HE_Mesh result;
		HEC_Creator ic = new HEC_Icosahedron().setOuterRadius(R);
		if (type == 1) {
			ic = new HEC_Tetrahedron().setOuterRadius(R);
		} else if (type == 2) {
			ic = new HEC_Octahedron().setOuterRadius(R);
		}
		result = ic.createBase();
		final HES_PlanarMidEdge pmes = new HES_PlanarMidEdge();
		result.subdivide(pmes, level);
		final WB_Point bc = new WB_Point(0, 0, 0);
		HE_Vertex v;
		final Iterator<HE_Vertex> vItr = result.vItr();
		while (vItr.hasNext()) {
			v = vItr.next();
			final double d = Math.sqrt(WB_Distance.getSqDistance3D(v, bc));
			v.pos._mulSelf(R / d);
		}

		return result;
	}
}
