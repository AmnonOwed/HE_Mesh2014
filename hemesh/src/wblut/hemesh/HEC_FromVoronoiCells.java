/*
 * 
 */
package wblut.hemesh;

import java.util.Collection;
import java.util.Iterator;
import javolution.util.FastTable;
import wblut.geom.WB_Point;

/**
 * 
 */
public class HEC_FromVoronoiCells extends HEC_Creator {
    
    /**
     * 
     */
    private HE_Mesh[] cells;
    
    /**
     * 
     */
    private boolean[] on;

    /**
     * 
     */
    public HEC_FromVoronoiCells() {
	super();
	override = true;
	cells = null;
	on = null;
    }

    /**
     * 
     *
     * @param cells 
     * @return 
     */
    public HEC_FromVoronoiCells setCells(final HE_Mesh[] cells) {
	this.cells = cells;
	return this;
    }

    /**
     * 
     *
     * @param cells 
     * @return 
     */
    public HEC_FromVoronoiCells setCells(final Collection<HE_Mesh> cells) {
	this.cells = new HE_Mesh[cells.size()];
	final int i = 0;
	for (final HE_Mesh cell : cells) {
	    this.cells[i] = cell;
	}
	return this;
    }

    /**
     * 
     *
     * @param on 
     * @return 
     */
    public HEC_FromVoronoiCells setActive(final boolean[] on) {
	this.on = on;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.creators.HEC_Creator#createBase()
     */
    @Override
    protected HE_Mesh createBase() {
	if (cells == null) {
	    return new HE_Mesh();
	}
	if (on == null) {
	    return new HE_Mesh();
	}
	if (on.length > cells.length) {
	    return new HE_Mesh();
	}
	final int n = on.length;
	final FastTable<HE_Face> tmpfaces = new FastTable<HE_Face>();
	int nv = 0;
	for (int i = 0; i < n; i++) {
	    final HE_Mesh m = cells[i];
	    if (on[i]) {
		final Iterator<HE_Face> fItr = m.fItr();
		while (fItr.hasNext()) {
		    final HE_Face f = fItr.next();
		    if (f.getInternalLabel() == -1) {
			tmpfaces.add(f);
			nv += f.getFaceOrder();
		    } else if (!on[f.getInternalLabel()]) {
			tmpfaces.add(f);
			nv += f.getFaceOrder();
		    }
		}
	    }
	}
	final WB_Point[] vertices = new WB_Point[nv];
	final int[][] faces = new int[tmpfaces.size()][];
	final int[] labels = new int[tmpfaces.size()];
	final int[] intlabels = new int[tmpfaces.size()];
	final int[] colors = new int[tmpfaces.size()];
	int cid = 0;
	for (int i = 0; i < tmpfaces.size(); i++) {
	    final HE_Face f = tmpfaces.get(i);
	    faces[i] = new int[f.getFaceOrder()];
	    labels[i] = f.getLabel();
	    intlabels[i] = f.getInternalLabel();
	    colors[i] = f.getColor();
	    HE_Halfedge he = f.getHalfedge();
	    for (int j = 0; j < f.getFaceOrder(); j++) {
		vertices[cid] = he.getVertex().getPoint();
		faces[i][j] = cid;
		he = he.getNextInFace();
		cid++;
	    }
	}
	final HEC_FromFacelist ffl = new HEC_FromFacelist()
		.setVertices(vertices).setFaces(faces).setDuplicate(true);
	final HE_Mesh result = ffl.createBase();
	final Iterator<HE_Face> fItr = result.fItr();
	int i = 0;
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    f.setLabel(labels[i]);
	    f.setInternalLabel(intlabels[i]);
	    f.setColor(colors[i]);
	    i++;
	}
	return result;
    }
}
