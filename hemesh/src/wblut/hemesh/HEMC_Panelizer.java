/*
 * 
 */
package wblut.hemesh;

/**
 * Planar cut of a mesh. Both parts are returned as separate meshes.
 * 
 * @author Frederik Vanhoutte (W:Blut)
 * 
 */
public class HEMC_Panelizer extends HEMC_MultiCreator {
    /** Source mesh. */
    private HE_Mesh mesh;
    /** The thickness. */
    private double thickness;
    /** The range. */
    private double range;

    /**
     * Set thickness.
     * 
     * @param d
     *            offset
     * @return self
     */
    public HEMC_Panelizer setThickness(final double d) {
	thickness = d;
	range = 0;
	return this;
    }

    /**
     * Sets the thickness.
     * 
     * @param dmin
     *            the dmin
     * @param dmax
     *            the dmax
     * @return the hEM c_ panelizer
     */
    public HEMC_Panelizer setThickness(final double dmin, final double dmax) {
	thickness = dmin;
	range = dmax - dmin;
	return this;
    }

    /**
     * Set source mesh.
     * 
     * @param mesh
     *            mesh to panelize
     * @return self
     */
    public HEMC_Panelizer setMesh(final HE_Mesh mesh) {
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
	if (mesh == null) {
	    _numberOfMeshes = 0;
	    return null;
	}
	final HE_Mesh[] result = new HE_Mesh[mesh.getNumberOfFaces()];
	int id = 0;
	final HEC_Polygon pc = new HEC_Polygon().setThickness(thickness);
	for (final HE_Face f : mesh.getFacesAsList()) {
	    pc.setThickness(thickness + (Math.random() * range));
	    pc.setPolygon(f.toPolygon());
	    result[id] = new HE_Mesh(pc);
	    id++;
	}
	return result;
    }
}
