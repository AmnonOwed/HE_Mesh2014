package wblut.hemesh;

import wblut.geom.WB_Point;

/**
 * Torus.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */

public class HEC_Torus extends HEC_Creator {

	/** Tube radius. */
	private double Ri;

	/** Torus Radius. */
	private double Ro;

	/** Facets. */
	private int tubefacets;

	/** Height steps. */
	private int torusfacets;

	/** The twist. */
	private int twist;

	/**
	 * Instantiates a new cylinder.
	 * 
	 */
	public HEC_Torus() {
		super();
		Ri = 0;
		Ro = 0;
		tubefacets = 6;
		torusfacets = 6;
	}

	/**
	 * Instantiates a new torus.
	 * 
	 * @param Ri
	 *            the ri
	 * @param Ro
	 *            the ro
	 * @param tubefacets
	 *            the tubefacets
	 * @param torusfacets
	 *            the torusfacets
	 */
	public HEC_Torus(final double Ri, final double Ro, final int tubefacets,
			final int torusfacets) {
		this();
		this.Ri = Ri;
		this.Ro = Ro;
		this.tubefacets = tubefacets;
		this.torusfacets = torusfacets;
	}

	/**
	 * Sets the radius.
	 * 
	 * @param Ri
	 *            the ri
	 * @param Ro
	 *            the ro
	 * @return the hE c_ torus
	 */
	public HEC_Torus setRadius(final double Ri, final double Ro) {
		this.Ri = Ri;
		this.Ro = Ro;
		return this;
	}

	/**
	 * Sets the tube facets.
	 * 
	 * @param facets
	 *            the facets
	 * @return the hE c_ torus
	 */
	public HEC_Torus setTubeFacets(final int facets) {
		tubefacets = facets;
		return this;
	}

	/**
	 * Sets the torus facets.
	 * 
	 * @param facets
	 *            the facets
	 * @return the hE c_ torus
	 */
	public HEC_Torus setTorusFacets(final int facets) {
		torusfacets = facets;
		return this;
	}

	/**
	 * Sets the twist.
	 * 
	 * @param t
	 *            the t
	 * @return the hE c_ torus
	 */
	public HEC_Torus setTwist(final int t) {
		twist = Math.max(0, t);
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_Creator#create()
	 */
	@Override
	protected HE_Mesh createBase() {

		final WB_Point[] vertices = new WB_Point[tubefacets * torusfacets];

		final double dtua = 2 * Math.PI / tubefacets;
		final double dtoa = 2 * Math.PI / torusfacets;
		final double dtwa = twist * dtoa / tubefacets;

		int id = 0;

		WB_Point basevertex;
		for (int j = 0; j < torusfacets; j++) {
			final double ca = Math.cos(j * dtoa);
			final double sa = Math.sin(j * dtoa);
			for (int i = 0; i < tubefacets; i++) {
				basevertex = new WB_Point(Ro + Ri
						* Math.cos(dtua * i + j * dtwa), 0, Ri
						* Math.sin(dtua * i + j * dtwa));

				vertices[id] = new WB_Point(ca * basevertex.x, sa
						* basevertex.x, basevertex.z);
				id++;
			}

		}

		final int nfaces = tubefacets * torusfacets;
		id = 0;
		final int[][] faces = new int[nfaces][];
		for (int j = 0; j < torusfacets - 1; j++) {
			for (int i = 0; i < tubefacets; i++) {
				faces[id] = new int[4];
				faces[id][0] = i + j * tubefacets;
				faces[id][1] = i + ((j + 1) % torusfacets) * tubefacets;
				faces[id][2] = (i + 1) % tubefacets + ((j + 1) % torusfacets)
						* tubefacets;
				faces[id][3] = (i + 1) % tubefacets + j * tubefacets;
				id++;
			}
		}
		for (int i = 0; i < tubefacets; i++) {
			faces[id] = new int[4];
			faces[id][0] = i + (torusfacets - 1) * tubefacets;
			faces[id][1] = (i + twist) % tubefacets;
			faces[id][2] = (i + twist + 1) % tubefacets;
			faces[id][3] = (i + 1) % tubefacets + (torusfacets - 1)
					* tubefacets;
			id++;
		}
		final HEC_FromFacelist fl = new HEC_FromFacelist();
		fl.setVertices(vertices).setFaces(faces);
		return fl.createBase();

	}
}
