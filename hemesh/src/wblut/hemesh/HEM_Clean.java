/*
 * 
 */
package wblut.hemesh;

import wblut.geom.WB_Polygon;

/**
 * 
 */
public class HEM_Clean extends HEM_Modifier {
    
    /**
     * 
     */
    public HEM_Clean() {
	super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	final WB_Polygon[] polygons = mesh.getPolygons();
	final HEC_FromPolygons creator = new HEC_FromPolygons();
	creator.setPolygons(polygons);
	mesh.setNoCopy(creator.create());
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	return apply(selection.parent);
    }
}
