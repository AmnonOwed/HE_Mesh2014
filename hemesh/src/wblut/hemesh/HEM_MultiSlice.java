/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import wblut.geom.WB_GeometryOp;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;

/**
 * Multiple planar cuts of a mesh. Faces on positive side of cut plane are
 * removed.
 *
 * @author Frederik Vanhoutte (W:Blut)
 *
 */
public class HEM_MultiSlice extends HEM_Modifier {
    /** Cut planes. */
    private ArrayList<WB_Plane> planes;
    /** Labels of cut faces. */
    private int[] labels;
    /** Reverse planar cuts. */
    private boolean reverse = false;
    /** Keep center of cut mesh. */
    private boolean keepCenter = false;
    /** Center used to sort cut planes. */
    private WB_Point center;
    /** Cap holes?. */
    private boolean capHoles = true;
    /** The simple cap. */
    private boolean simpleCap = true;
    /** Original faces?. */
    public HE_Selection origFaces;
    /** New faces?. */
    public HE_Selection newFaces;
    /** The offset. */
    private double offset;

    /**
     * Set offset.
     *
     * @param d
     *            offset
     * @return self
     */
    public HEM_MultiSlice setOffset(final double d) {
	offset = d;
	return this;
    }

    /**
     * Instantiates a new HEM_MultiSlice.
     */
    public HEM_MultiSlice() {
	super();
    }

    /**
     * Set cut planes from an arrayList of WB_Plane.
     *
     * @param planes
     *            arrayList of WB_Plane
     * @return self
     */
    public HEM_MultiSlice setPlanes(final Collection<WB_Plane> planes) {
	this.planes = new ArrayList<WB_Plane>();
	this.planes.addAll(planes);
	return this;
    }

    /**
     * Set cut planes from an array of WB_Plane.
     *
     * @param planes
     *            arrayList of WB_Plane
     * @return self
     */
    public HEM_MultiSlice setPlanes(final WB_Plane[] planes) {
	this.planes = new ArrayList<WB_Plane>();
	for (final WB_Plane plane : planes) {
	    this.planes.add(plane);
	}
	return this;
    }

    /**
     * Set labels of cut planes. Cap faces will be labeled.
     *
     * @param labels
     *            array of int
     * @return self
     */
    public HEM_MultiSlice setLabels(final int[] labels) {
	this.labels = labels;
	return this;
    }

    /**
     * Set reverse option.
     *
     * @param b
     *            true, false
     * @return self
     */
    public HEM_MultiSlice setReverse(final Boolean b) {
	reverse = b;
	return this;
    }

    /**
     * Set center for cut plane sorting.
     *
     * @param c
     *            center
     * @return self
     */
    public HEM_MultiSlice setCenter(final WB_Point c) {
	center = c.get();
	return this;
    }

    /**
     * Set option to cap holes.
     *
     * @param b
     *            true, false;
     * @return self
     */
    public HEM_MultiSlice setCap(final Boolean b) {
	capHoles = b;
	return this;
    }

    /**
     * Sets the simple cap.
     *
     * @param b
     *            the b
     * @return the hE m_ multi slice
     */
    public HEM_MultiSlice setSimpleCap(final Boolean b) {
	simpleCap = b;
	return this;
    }

    /**
     * Set option to reset mesh center.
     *
     * @param b
     *            true, false;
     * @return self
     */
    public HEM_MultiSlice setKeepCenter(final Boolean b) {
	keepCenter = b;
	return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_Modifier#apply(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Mesh mesh) {
	origFaces = new HE_Selection(mesh);
	newFaces = new HE_Selection(mesh);
	if (planes == null) {
	    return mesh;
	}
	if (labels == null) {
	    labels = new int[planes.size()];
	    for (int i = 0; i < planes.size(); i++) {
		labels[i] = i;
	    }
	}
	Iterator<HE_Face> fItr = mesh.fItr();
	mesh.resetFaceInternalLabels();
	final HEM_Slice slice = new HEM_Slice();
	slice.setReverse(reverse).setCap(capHoles).setOffset(offset)
	.setSimpleCap(simpleCap);
	if (center != null) {
	    final double[] r = new double[planes.size()];
	    for (int i = 0; i < planes.size(); i++) {
		final WB_Plane P = planes.get(i);
		r[i] = WB_GeometryOp.getSqDistance3D(P.getOrigin(), center);
	    }
	    for (int i = planes.size(); --i >= 0;) {
		for (int m = 0; m < i; m++) {
		    if (r[m] > r[m + 1]) {
			Collections.swap(planes, m, m + 1);
			final double tmp = r[m];
			r[m] = r[m + 1];
			r[m + 1] = tmp;
			final int tmpid = labels[m];
			labels[m] = labels[m + 1];
			labels[m + 1] = tmpid;
		    }
		}
	    }
	}
	boolean unique = false;
	WB_Plane Pi;
	WB_Plane Pj;
	final int stop = planes.size();
	for (int i = 0; i < stop; i++) {
	    Pi = planes.get(i);
	    unique = true;
	    for (int j = 0; j < i; j++) {
		Pj = planes.get(j);
		if (WB_Plane.isEqual(Pi, Pj)) {
		    unique = false;
		    break;
		}
	    }
	    if (unique) {
		slice.setPlane(Pi);
		slice.setKeepCenter(true);
		slice.apply(mesh);
		fItr = slice.cap.fItr();
		while (fItr.hasNext()) {
		    fItr.next().setInternalLabel(labels[i]);
		}
	    }
	}
	fItr = mesh.fItr();
	HE_Face f;
	while (fItr.hasNext()) {
	    f = fItr.next();
	    if (f.getInternalLabel() == -1) {
		origFaces.add(f);
	    } else {
		newFaces.add(f);
	    }
	}
	if (!keepCenter) {
	    mesh.resetCenter();
	}
	return mesh;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * wblut.hemesh.modifiers.HEB_Modifier#modifySelected(wblut.hemesh.HE_Mesh)
     */
    @Override
    public HE_Mesh apply(final HE_Selection selection) {
	return apply(selection.parent);
    }
}
