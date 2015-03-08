/*
 * 
 */
package wblut.hemesh;

import java.util.ArrayList;
import wblut.geom.WB_Plane;
import wblut.geom.WB_Point;
import wblut.geom.WB_Vector;
import wblut.math.WB_Math;

/**
 * 
 */
public class HEMC_WeairePhelan extends HEMC_MultiCreator {
    
    /**
     * 
     */
    private static final double[][] dodecahedronPoints = {
	    { 0.31498, 0, 0.62996 }, { -0.31498, 0, 0.62996 },
	    { 0.41997, 0.41997, 0.41997 }, { 0, 0.62996, 0.31498 },
	    { -0.41997, 0.41997, 0.41997 }, { -0.41997, -0.41997, 0.41997 },
	    { 0, -0.62996, .31498 }, { .41997, -.41997, .41997 },
	    { .62996, .31498, 0 }, { -.62996, .31498, 0 },
	    { -.62996, -.31498, 0 }, { .62996, -.31498, 0 },
	    { .41997, .41997, -.41997 }, { 0, .62996, -.31498 },
	    { -.41997, .41997, -.41997 }, { -.41997, -.41997, -.41997 },
	    { 0, -.62996, -.31498 }, { .41997, -.41997, -.41997 },
	    { .31498, 0, -.62996 }, { -.31498, 0, -.62996 } };
    
    /**
     * 
     */
    private static final double[][] tetrakaidecahedronPoints = {
	    { .314980, .370039, .5 }, { -.314980, .370039, .5 },
	    { -.5, 0, .5 }, { -.314980, -.370039, .5 },
	    { .314980, -.370039, .5 }, { .5, 0, .5 },
	    { .419974, .580026, 0.080026 }, { -.419974, .580026, 0.080026 },
	    { -.685020, 0, .129961 }, { -.419974, -.580026, 0.080026 },
	    { .419974, -.580026, 0.080026 }, { .685020, 0, .129961 },
	    { .580026, .419974, -0.080026 }, { 0, .685020, -0.129961 },
	    { -.580026, .419974, -0.080026 },
	    { -.580026, -.419974, -0.080026 }, { 0, -.685020, -.129961 },
	    { .580026, -.419974, -0.080026 }, { .370039, .314980, -.5 },
	    { 0, .5, -.5 }, { -.370039, .314980, -.5 },
	    { -.370039, -.314980, -.5 }, { 0, -.5, -.5 },
	    { .370039, -.314980, -.5 } };
    
    /**
     * 
     */
    private final HE_Mesh dodecahedron;
    
    /**
     * 
     */
    private final HE_Mesh tetrakaidecahedron;
    
    /**
     * 
     */
    private WB_Point origin;
    
    /**
     * 
     */
    private WB_Vector extents;
    
    /**
     * 
     */
    private int U, V, W;
    
    /**
     * 
     */
    private double scU, scV, scW;
    
    /**
     * 
     */
    private boolean cropUp, cropVp, cropWp;
    
    /**
     * 
     */
    private boolean cropUm, cropVm, cropWm;
    
    /**
     * 
     */
    private static int[] colors = new int[] { -65536, -16384, -8519936,
	    -16711870, -16712705, -16761857, -8126209, -65351 };

    /**
     * 
     */
    public HEMC_WeairePhelan() {
	super();
	dodecahedron = new HE_Mesh(
		new HEC_ConvexHull().setPoints(dodecahedronPoints));
	tetrakaidecahedron = new HE_Mesh(
		new HEC_ConvexHull().setPoints(tetrakaidecahedronPoints));
	dodecahedron.fuseCoplanarFaces(0.1);
	tetrakaidecahedron.fuseCoplanarFaces(0.1);
	cropUp = false;
	cropVp = false;
	cropWp = false;
	cropUm = false;
	cropVm = false;
	cropWm = false;
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public HEMC_WeairePhelan setOrigin(final WB_Point p) {
	origin = p.get();
	return this;
    }

    /**
     * 
     *
     * @param v 
     * @return 
     */
    public HEMC_WeairePhelan setExtents(final WB_Vector v) {
	extents = v.get();
	return this;
    }

    /**
     * 
     *
     * @param scU 
     * @param scV 
     * @param scW 
     * @return 
     */
    public HEMC_WeairePhelan setScale(final double scU, final double scV,
	    final double scW) {
	this.scU = scU;
	this.scV = scV;
	this.scW = scW;
	return this;
    }

    /**
     * 
     *
     * @param U 
     * @param V 
     * @param W 
     * @return 
     */
    public HEMC_WeairePhelan setNumberOfUnits(final int U, final int V,
	    final int W) {
	this.U = WB_Math.max(1, U);
	this.V = WB_Math.max(1, V);
	this.W = WB_Math.max(1, W);
	return this;
    }

    /**
     * 
     *
     * @param crop 
     * @return 
     */
    public HEMC_WeairePhelan setCrop(final boolean crop) {
	cropUm = crop;
	cropVm = crop;
	cropWm = crop;
	cropUp = crop;
	cropVp = crop;
	cropWp = crop;
	return this;
    }

    /**
     * 
     *
     * @param cropU 
     * @param cropV 
     * @param cropW 
     * @return 
     */
    public HEMC_WeairePhelan setCrop(final boolean cropU, final boolean cropV,
	    final boolean cropW) {
	cropUm = cropU;
	cropVm = cropV;
	cropWm = cropW;
	cropUp = cropU;
	cropVp = cropV;
	cropWp = cropW;
	return this;
    }

    /**
     * 
     *
     * @param cropUm 
     * @param cropVm 
     * @param cropWm 
     * @param cropUp 
     * @param cropVp 
     * @param cropWp 
     * @return 
     */
    public HEMC_WeairePhelan setCrop(final boolean cropUm,
	    final boolean cropVm, final boolean cropWm, final boolean cropUp,
	    final boolean cropVp, final boolean cropWp) {
	this.cropUm = cropUm;
	this.cropVm = cropVm;
	this.cropWm = cropWm;
	this.cropUp = cropUp;
	this.cropVp = cropVp;
	this.cropWp = cropWp;
	return this;
    }

    /**
     * 
     *
     * @param offset 
     * @return 
     */
    private HE_Mesh[] singleCell(final WB_Vector offset) {
	final HE_Mesh[] cells = new HE_Mesh[8];
	cells[0] = tetrakaidecahedron.get();
	cells[0].move(0, 0, -.5);
	cells[1] = tetrakaidecahedron.get();
	cells[1].rotateAboutAxis(0.5 * Math.PI, 0, 0, 0, 0, 0, 1);
	cells[1].move(0, 0, 0.5);
	cells[2] = tetrakaidecahedron.get();
	cells[2].rotateAboutAxis(-0.5 * Math.PI, 0, 0, 0, 0, 1, 0);
	cells[2].move(-.5, 1, 1);
	cells[3] = tetrakaidecahedron.get();
	cells[3].rotateAboutAxis(0.5 * Math.PI, 0, 0, 0, 0, 1, 0);
	cells[3].move(.5, 1, 1);
	cells[4] = tetrakaidecahedron.get();
	cells[4].rotateAboutAxis(0.5 * Math.PI, 0, 0, 0, 1, 0, 0);
	cells[4].move(1, .5, 0);
	cells[5] = tetrakaidecahedron.get();
	cells[5].rotateAboutAxis(-0.5 * Math.PI, 0, 0, 0, 1, 0, 0);
	cells[5].move(1, -.5, 0);
	cells[6] = dodecahedron.get();
	cells[6].move(1, 0, 1);
	cells[7] = dodecahedron.get();
	cells[7].rotateAboutAxis(-0.5 * Math.PI, 0, 0, 0, 0, 1, 0);
	cells[7].move(0, 1, 0);
	for (int i = 0; i < 8; i++) {
	    cells[i].scale(0.5 * scU, 0.5 * scV, 0.5 * scW, new WB_Point(0, 0,
		    0));
	    cells[i].move(offset);
	    cells[i].setColor(colors[i]);
	}
	return cells;
    }

    /*
     * (non-Javadoc)
     * 
     * @see wblut.hemesh.HE_MultiCreator#create()
     */
    @Override
    public HE_Mesh[] create() {
	if (scU == 0) {
	    scU = extents.xd() / U;
	}
	if (scV == 0) {
	    scV = extents.yd() / V;
	}
	if (scW == 0) {
	    scW = extents.zd() / W;
	}
	final ArrayList<HE_Mesh> tmp = new ArrayList<HE_Mesh>();
	HE_Mesh[] tmpCells;
	final ArrayList<WB_Plane> planes = new ArrayList<WB_Plane>(6);
	if (cropUm) {
	    planes.add(new WB_Plane(origin, new WB_Vector(1, 0, 0)));
	}
	if (cropVm) {
	    planes.add(new WB_Plane(origin, new WB_Vector(0, 1, 0)));
	}
	if (cropWm) {
	    planes.add(new WB_Plane(origin, new WB_Vector(0, 0, 1)));
	}
	final WB_Point end = origin.add(extents);
	if (cropUp) {
	    planes.add(new WB_Plane(end, new WB_Vector(-1, 0, 0)));
	}
	if (cropVp) {
	    planes.add(new WB_Plane(end, new WB_Vector(0, -1, 0)));
	}
	if (cropWp) {
	    planes.add(new WB_Plane(end, new WB_Vector(0, 0, -1)));
	}
	final HEM_MultiSlice ms = new HEM_MultiSlice().setPlanes(planes);
	for (int i = 0; i < (U + 1); i++) {
	    for (int j = 0; j < (V + 1); j++) {
		for (int k = 0; k < (W + 1); k++) {
		    final WB_Vector offset = new WB_Vector(origin.xd()
			    + ((i - 0.5) * scU), origin.yd()
			    + ((j - 0.5) * scV), origin.zd()
			    + ((k - 0.5) * scW));
		    tmpCells = singleCell(offset);
		    for (int c = 0; c < 8; c++) {
			if (planes.size() > 0) {
			    tmpCells[c].modify(ms);
			}
			if (tmpCells[c].getNumberOfVertices() > 0) {
			    tmp.add(tmpCells[c]);
			}
		    }
		}
	    }
	}
	final HE_Mesh[] result = new HE_Mesh[tmp.size()];
	HE_Mesh cell;
	for (int i = 0; i < tmp.size(); i++) {
	    cell = tmp.get(i);
	    result[i] = cell;
	}
	_numberOfMeshes = tmp.size();
	return result;
    }
}