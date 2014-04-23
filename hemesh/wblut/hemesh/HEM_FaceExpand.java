package wblut.hemesh;

import java.util.ArrayList;
import java.util.Iterator;

import javolution.util.FastMap;
import wblut.geom.WB_Intersection;
import wblut.geom.WB_IntersectionResult;
import wblut.geom.WB_Line;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;

public class HEM_FaceExpand extends HEM_Modifier {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.subdividors.HES_Subdividor#subdivide(wblut.hemesh.HE_Mesh)
	 */

	private double d;

	public HEM_FaceExpand() {

	}

	public HEM_FaceExpand setDistance(final double d) {
		this.d = d;
		return this;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.hemesh.HEM_Modifier#apply(wblut.hemesh.HE_Mesh)
	 */
	@Override
	public HE_Mesh apply(final HE_Mesh mesh) {
		if (d == 0) {
			return mesh;
		}

		Iterator<HE_Face> fItr = mesh.fItr();
		final FastMap<Long, Integer> halfedgeCorrelation = new FastMap<Long, Integer>();
		final ArrayList<WB_Point> newVertices = new ArrayList<WB_Point>();
		HE_Face f;
		HE_Halfedge he;
		WB_Vector fn;
		WB_Vector vn;
		WB_Vector en;
		HE_Vertex v;
		int vertexCount = 0;
		while (fItr.hasNext()) {
			f = fItr.next();
			final WB_Plane planef = f.toPlane(d);
			he = f.getHalfedge();
			System.out.println(he);
			fn = f.getFaceNormal();
			do {

				v = he.getVertex();
				System.out.println(v + " " + v.getHalfedge());
				vn = v.getVertexNormal();
				System.out.println(vn);
				final WB_Point p;
				if (vn.isParallel(fn)) {
					p = new WB_Point(v);
					p._addSelf(d, fn);

				} else {
					final WB_Plane planef2 = he.getPair().getFace().toPlane(d);
					final WB_IntersectionResult ir = WB_Intersection
							.getIntersection(planef, planef2);
					final WB_Line lineff;
					if (ir.dimension == 1) {
						lineff = (WB_Line) ir.object;
					} else {
						en = he.getEdge().getEdgeNormal();
						lineff = new WB_Line(v.pos.addMul(d, en),
								he.getHalfedgeTangent());
					}
					p = WB_Intersection.getClosestPoint(v, lineff);
					System.out.println(lineff + " " + p);
				}

				halfedgeCorrelation.put(he.key(), vertexCount);
				vertexCount++;
				newVertices.add(p);
				he = he.getNextInFace();

			} while (he != f.getHalfedge());
		}
		final int[][] faces = new int[mesh.getNumberOfFaces()
				+ mesh.getNumberOfEdges() + mesh.getNumberOfVertices()][];
		final int[] labels = new int[mesh.getNumberOfFaces()
				+ mesh.getNumberOfEdges() + mesh.getNumberOfVertices()];
		final int[] noe = { mesh.getNumberOfFaces(), mesh.getNumberOfEdges(),
				mesh.getNumberOfVertices() };
		int currentFace = 0;
		fItr = mesh.fItr();
		while (fItr.hasNext()) {
			f = fItr.next();
			faces[currentFace] = new int[f.getFaceOrder()];
			he = f.getHalfedge();
			int i = 0;
			labels[currentFace] = currentFace;
			do {
				faces[currentFace][i] = halfedgeCorrelation.get(he.key());
				he = he.getNextInFace();
				i++;
			} while (he != f.getHalfedge());
			currentFace++;
		}
		final Iterator<HE_Edge> eItr = mesh.eItr();
		HE_Edge e;
		int currentEdge = 0;
		while (eItr.hasNext()) {

			e = eItr.next();
			faces[currentFace] = new int[4];
			faces[currentFace][3] = halfedgeCorrelation.get(e.getHalfedge()
					.key());
			faces[currentFace][2] = halfedgeCorrelation.get(e.getHalfedge()
					.getNextInFace().key());
			faces[currentFace][1] = halfedgeCorrelation.get(e.getHalfedge()
					.getPair().key());
			faces[currentFace][0] = halfedgeCorrelation.get(e.getHalfedge()
					.getPair().getNextInFace().key());
			labels[currentFace] = currentEdge;
			currentEdge++;
			currentFace++;
		}
		final Iterator<HE_Vertex> vItr = mesh.vItr();

		int currentVertex = 0;
		while (vItr.hasNext()) {
			v = vItr.next();
			faces[currentFace] = new int[v.getVertexOrder()];
			he = v.getHalfedge();
			int i = v.getVertexOrder() - 1;
			do {
				faces[currentFace][i] = halfedgeCorrelation.get(he.key());
				he = he.getNextInVertex();
				i--;
			} while (he != v.getHalfedge());
			labels[currentFace] = currentVertex;
			currentVertex++;
			currentFace++;
		}
		final HEC_FromFacelist fl = new HEC_FromFacelist().setFaces(faces)
				.setVertices(newVertices).setDuplicate(true);
		mesh.set(fl.create());

		return mesh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * wblut.hemesh.subdividors.HES_Subdividor#subdivideSelected(wblut.hemesh
	 * .HE_Mesh, wblut.hemesh.HE_Selection)
	 */
	@Override
	public HE_Mesh apply(final HE_Selection selection) {
		return selection.parent;
	}

}
