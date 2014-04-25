package wblut.hemesh;

import java.util.ArrayList;

/**
 * 
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEMC_Explode extends HEMC_MultiCreator {

	/** Source mesh. */
	private HE_Mesh mesh;

	/**
	 * Instantiates a new HEMC_SplitMesh.
	 * 
	 */
	public HEMC_Explode() {
		super();
	}

	/**
	 * Set source mesh.
	 * 
	 * @param mesh
	 *            mesh to split
	 * @return self
	 */
	public HEMC_Explode setMesh(final HE_Mesh mesh) {
		this.mesh = mesh;
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HE_MultiCreator#create()
	 */
	@Override
	public HE_Mesh[] create() {
		final ArrayList<HE_Mesh> result = new ArrayList<HE_Mesh>();
		if (mesh == null) {
			_numberOfMeshes = 0;
			HE_Mesh[] resarray = new HE_Mesh[result.size()];
			return (HE_Mesh[]) result.toArray(resarray);
		}

		HE_Mesh[] resarray = new HE_Mesh[result.size()];
		return (HE_Mesh[]) result.toArray(resarray);

	}
}
