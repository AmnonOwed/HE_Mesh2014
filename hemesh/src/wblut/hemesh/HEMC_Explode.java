package wblut.hemesh;

import java.util.ArrayList;
import java.util.List;

import javolution.util.FastTable;

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
			final HE_Mesh[] resarray = new HE_Mesh[result.size()];
			return result.toArray(resarray);
		}
		final List<HE_Face> faces = mesh.getFaces();
		mesh.setInternalLabel(0); // face not visited
		int index = 1;
		do {
			final HE_Face start = faces.get(0);
			start.setInternalLabel(index);// visited
			final List<HE_Face> submesh = new FastTable<HE_Face>();
			submesh.add(start);
			final List<HE_Face> facesToProcess = new FastTable<HE_Face>();
			facesToProcess.add(start);
			do {
				final List<HE_Face> neighbors = facesToProcess.get(0)
						.getNeighborFaces();
				facesToProcess.remove(0);
				for (final HE_Face neighbor : neighbors) {
					if (neighbor.getInternalLabel() == 0) {
						neighbor.setInternalLabel(index);// visited
						submesh.add(neighbor);
						facesToProcess.add(neighbor);
					}
				}
			} while (facesToProcess.size() > 0);
			faces.removeAll(submesh);
			result.add(mesh.getSubmeshFromFaceInternalLabel(index++));

		} while (faces.size() > 0);

		final HE_Mesh[] resarray = new HE_Mesh[result.size()];
		return result.toArray(resarray);

	}

}
