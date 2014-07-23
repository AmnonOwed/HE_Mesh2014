package wblut.hemesh;

/**
 * Divides all faces in mesh or selection in triangles connecting the face
 * center with each edge and deletes all original non-boundary edges with a
 * dihedral angle larger that parameter limitAngle.
 *
 *
 * @author frederikvanhoutte
 *
 */
public class HEM_Diagrid extends HEM_Modifier {

	private double limitAngle;

	/**
	 *
	 */
	public HEM_Diagrid() {
		limitAngle = 1.001 * 0.5 * Math.PI;

	}

	/**
	 * Set the lower limit dihedral angle
	 *
	 * @param a
	 *            : limit angle in radius, edges with dihedral angle lower than
	 *            this angle are not removed. Default value is PI/2
	 * @return
	 */
	public HEM_Diagrid setLimitAngle(final double a) {
		limitAngle = a;
		return this;

	}

	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		final HE_Selection sel = mesh.selectAllEdges();
		mesh.triSplitFaces();

		final HE_EdgeIterator eitr = new HE_EdgeIterator(sel);
		HE_Edge e;
		while (eitr.hasNext()) {
			e = eitr.next();
			if (!e.isBoundary() && e.getDihedralAngle() > (limitAngle)) {
				mesh.deleteEdge(e);
			}
		}
		return mesh;
	}

	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		selection.collectEdgesByFace();
		selection.parent.triSplitFaces(selection);
		final HE_RAS<HE_Edge> border = new HE_RASTrove<HE_Edge>();
		border.addAll(selection.getOuterEdges());
		final HE_EdgeIterator eitr = new HE_EdgeIterator(selection);
		HE_Edge e;
		while (eitr.hasNext()) {
			e = eitr.next();
			if (!border.contains(e) && e.getDihedralAngle() > (limitAngle)) {
				selection.parent.deleteEdge(e);
			}
		}
		return selection.parent;
	}

}
