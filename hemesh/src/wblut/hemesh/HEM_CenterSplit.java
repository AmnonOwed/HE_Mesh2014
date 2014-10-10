package wblut.hemesh;

import wblut.hemesh.HEM_Modifier;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;


public class HEM_CenterSplit extends HEM_Modifier {

	
	public HEM_CenterSplit() {

		super();
	
	}

	
	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		
		mesh.splitFacesCenter();
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		
		selection.parent.splitFacesCenter(selection);
		return selection.parent;
	}
}
