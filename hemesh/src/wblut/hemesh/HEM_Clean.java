package wblut.hemesh;

import wblut.hemesh.HEM_Modifier;
import wblut.hemesh.HE_Mesh;
import wblut.hemesh.HE_Selection;


public class HEM_Clean extends HEM_Modifier {

	
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
		
		mesh.clean();
		return mesh;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		
		selection.parent.clean();
		return selection.parent;
	}
}
