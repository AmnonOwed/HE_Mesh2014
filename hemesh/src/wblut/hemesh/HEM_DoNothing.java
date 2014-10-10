package wblut.hemesh;

import wblut.hemesh.HEM_Modifier;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;


public class HEM_DoNothing extends HEM_Modifier {

	
	public HEM_DoNothing() {

		super();
	
	}

	
	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		
		return selection.parent;
	}
}
