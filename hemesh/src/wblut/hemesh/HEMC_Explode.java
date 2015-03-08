/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.List;

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
	    return new HE_Mesh[0];
	}
	mesh.resetFaceInternalLabels();
	HE_Face start = mesh.getFaceByIndex(0);
	final int index = 0;
	int lastfound = 0;
	HE_Selection submesh;
	do {
	    for (int i = lastfound; i < mesh.getNumberOfFaces(); i++) {
		start = mesh.getFaceByIndex(i);
		lastfound = i;
		if (start.getInternalLabel() == -1) {
		    break;
		}
	    }
	    if (start.getInternalLabel() != -1) {
		break;
	    }
	    start.setInternalLabel(index);// visited
	    submesh = new HE_Selection(mesh);
	    submesh.add(start);
	    HE_RAS<HE_Face> facesToProcess = new HE_RASTrove<HE_Face>();
	    HE_RAS<HE_Face> newFacesToProcess;
	    facesToProcess.add(start);
	    List<HE_Face> neighbors;
	    do {
		newFacesToProcess = new HE_RASTrove<HE_Face>();
		for (final HE_Face f : facesToProcess) {
		    neighbors = f.getNeighborFaces();
		    for (final HE_Face neighbor : neighbors) {
			if (neighbor.getInternalLabel() == -1) {
			    neighbor.setInternalLabel(index);// visited
			    submesh.add(neighbor);
			    newFacesToProcess.add(neighbor);
			}
		    }
		}
		facesToProcess = newFacesToProcess;
	    } while (facesToProcess.size() > 0);
	    result.add(submesh.getAsMesh());
	} while (true);
	final HE_Mesh[] resarray = new HE_Mesh[result.size()];
	return result.toArray(resarray);
    }
}
