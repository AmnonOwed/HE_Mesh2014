/*
 * 
 */
package wblut.geom;

import java.util.ArrayList;
import javolution.util.FastMap;
import wblut.math.WB_Epsilon;
import wblut.math.WB_Math;

/**
 * 
 */
public class WB_GeomGrid {
    
    /**
     * 
     */
    private final FastMap<Integer, WB_GeomGridCell> cells;
    
    /**
     * 
     */
    private final int W, H, WH, D;
    
    /**
     * 
     */
    private final double dx, dy, dz, idx, idy, idz;
    
    /**
     * 
     */
    private final WB_Point min;
    
    /**
     * 
     */
    private final WB_Point max;
    
    /**
     * 
     */
    private final WB_AABB aabb;

    /**
     * The Class Index.
     */
    class Index {
	/** The k. */
	int i, j, k;
	/** The inside. */
	boolean inside;

	/**
	 * 
	 *
	 * @param i 
	 * @param j 
	 * @param k 
	 * @param inside 
	 */
	Index(final int i, final int j, final int k, final boolean inside) {
	    this.i = i;
	    this.j = j;
	    this.k = k;
	    this.inside = inside;
	}

	/**
	 * 
	 *
	 * @return 
	 */
	int index() {
	    return i + (j * W) + (k * WH);
	}

	/**
	 * 
	 *
	 * @param id 
	 * @return 
	 */
	boolean equals(final Index id) {
	    return (i == id.i) && (j == id.j) && (k == id.k);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return ("i " + i + " j " + j + " k " + k + " inside " + inside);
	}

	/**
	 * 
	 *
	 * @return 
	 */
	public Index get() {
	    return new Index(i, j, k, inside);
	}
    }

    /**
     * 
     *
     * @param minx 
     * @param miny 
     * @param minz 
     * @param maxx 
     * @param maxy 
     * @param maxz 
     * @param W 
     * @param H 
     * @param D 
     */
    public WB_GeomGrid(final double minx, final double miny, final double minz,
	    final double maxx, final double maxy, final double maxz,
	    final int W, final int H, final int D) {
	this.W = W;
	this.H = H;
	this.D = D;
	WH = W * H;
	min = new WB_Point(minx, miny, minz);
	max = new WB_Point(maxx, maxy, maxz);
	aabb = new WB_AABB(min, max);
	cells = new FastMap<Integer, WB_GeomGridCell>();
	dx = (maxx - minx) / W;
	dy = (maxy - miny) / H;
	dz = (maxz - minz) / D;
	idx = 1.0 / dx;
	idy = 1.0 / dy;
	idz = 1.0 / dz;
    }

    /**
     * 
     *
     * @param p 
     */
    public void addPoint(final WB_Coordinate p) {
	final Index id = safeijk(p);
	if (id != null) {
	    WB_GeomGridCell cell = cells.get(id);
	    if (cell == null) {
		final int index = index(id);
		cell = getNewCellForIndex(id);
		cell.addPoint(p);
		cells.put(index, cell);
	    } else {
		cell.addPoint(p);
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     * @param r 
     */
    public void addPoint(final WB_Coordinate p, final double r) {
	final ArrayList<WB_GeomGridCell> fatcells = getCellsInNeighborhood(p,
		r, true);
	for (final WB_GeomGridCell fatcell : fatcells) {
	    final int id = fatcell.getIndex();
	    final WB_GeomGridCell cell = cells.get(id);
	    if (cell == null) {
		fatcell.addPoint(p);
		cells.put(id, fatcell);
	    } else {
		cell.addPoint(p);
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     */
    public void removePoint(final WB_Point p) {
	final Index id = safeijk(p);
	if (id != null) {
	    final WB_GeomGridCell cell = cells.get(index(id));
	    if (cell != null) {
		cell.removePoint(p);
		if (cell.isEmpty()) {
		    cells.remove(id);
		}
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     * @param r 
     */
    public void removePoint(final WB_Point p, final double r) {
	final ArrayList<WB_GeomGridCell> fatcells = getCellsInNeighborhood(p,
		r, true);
	for (final WB_GeomGridCell fatcell : fatcells) {
	    final int id = fatcell.getIndex();
	    final WB_GeomGridCell cell = cells.get(id);
	    if (cell != null) {
		cell.removePoint(p);
		if (cell.isEmpty()) {
		    cells.remove(id);
		}
	    }
	}
    }

    /**
     * 
     *
     * @param S 
     * @param r 
     */
    public void addSegment(final WB_Segment S, final double r) {
	final ArrayList<WB_GeomGridCell> fatcells = getCellsInNeighborhood(S,
		r, true);
	for (final WB_GeomGridCell fatcell : fatcells) {
	    final int id = fatcell.getIndex();
	    final WB_GeomGridCell cell = cells.get(id);
	    if (cell == null) {
		fatcell.addSegment(S);
		cells.put(id, fatcell);
	    } else {
		cell.addSegment(S);
	    }
	}
    }

    /**
     * 
     *
     * @param S 
     */
    public void addSegment(final WB_Segment S) {
	final ArrayList<Index> traversedIndices = indicesTraversed(S);
	for (final Index id : traversedIndices) {
	    WB_GeomGridCell cell = cells.get(id);
	    if (cell == null) {
		final int index = index(id);
		cell = getNewCellForIndex(id);
		cell.addSegment(S);
		cells.put(index, cell);
	    } else {
		cell.addSegment(S);
	    }
	}
    }

    /**
     * 
     *
     * @param S 
     */
    public void removeSegment(final WB_Segment S) {
	final ArrayList<Index> traversedIndices = indicesTraversed(S);
	for (final Index id : traversedIndices) {
	    final WB_GeomGridCell cell = cells.get(id);
	    if (cell != null) {
		cell.removeSegment(S);
		if (cell.isEmpty()) {
		    cells.remove(index(id));
		}
	    }
	}
    }

    /**
     * 
     *
     * @param S 
     * @param r 
     */
    public void removeSegment(final WB_Segment S, final double r) {
	final ArrayList<WB_GeomGridCell> fatcells = getCellsInNeighborhood(S,
		r, true);
	for (final WB_GeomGridCell fatcell : fatcells) {
	    final int id = fatcell.getIndex();
	    final WB_GeomGridCell cell = cells.get(id);
	    if (cell != null) {
		cell.removeSegment(S);
		if (cell.isEmpty()) {
		    cells.remove(id);
		}
	    }
	}
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Point index(final WB_Coordinate p) {
	final Index id = ijk(p);
	return new WB_Point(id.i, id.j, id.k);
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public WB_Point safeIndex(final WB_Coordinate p) {
	final Index id = safeijk(p);
	if (id == null) {
	    return null;
	}
	return new WB_Point(id.i, id.j, id.k);
    }

    /**
     * 
     *
     * @param i 
     * @param j 
     * @param k 
     * @return 
     */
    public ArrayList<WB_Point> getPoints(final int i, final int j, final int k) {
	if (i < 0) {
	    return null;
	}
	if (i > (W - 1)) {
	    return null;
	}
	if (j < 0) {
	    return null;
	}
	if (j > (H - 1)) {
	    return null;
	}
	if (k < 0) {
	    return null;
	}
	if (k > (D - 1)) {
	    return null;
	}
	final WB_GeomGridCell cell = cells.get(index(i, j, k));
	if (cell == null) {
	    return new ArrayList<WB_Point>();
	}
	return cell.getPoints();
    }

    /**
     * 
     *
     * @param p 
     * @return 
     */
    public ArrayList<WB_Point> getPointsInSameCell(final WB_Coordinate p) {
	final Index id = safeijk(p);
	if (id == null) {
	    return new ArrayList<WB_Point>();
	}
	final WB_GeomGridCell cell = cells.get(index(id));
	if (cell == null) {
	    return new ArrayList<WB_Point>();
	}
	return cell.getPoints();
    }

    /**
     * 
     *
     * @param p 
     * @param r 
     * @param all 
     * @return 
     */
    public ArrayList<WB_GeomGridCell> getCellsInNeighborhood(
	    final WB_Coordinate p, final double r, final boolean all) {
	final ArrayList<WB_GeomGridCell> result = new ArrayList<WB_GeomGridCell>();
	final Index id = safeijk(p);
	WB_GeomGridCell cell;
	final double r2 = r * r;
	int neighbor;
	final int ri = (int) (r / dx) + 1;
	final int rj = (int) (r / dy) + 1;
	final int rk = (int) (r / dz) + 1;
	for (int di = -ri; di <= ri; di++) {
	    for (int dj = -rj; dj <= rj; dj++) {
		for (int dk = -rk; dk <= rk; dk++) {
		    neighbor = safeIndex(id.i + di, id.j + dj, id.k + dk);
		    if (neighbor > -1) {
			cell = cells.get(neighbor);
			if (cell != null) {
			    if (WB_GeometryOp
				    .getSqDistance3D(p, cell.getAABB()) <= r2) {
				result.add(cell);
			    }
			} else if (all) {
			    cell = getNewCellForIndex(id.i + di, id.j + dj,
				    id.k + dk);
			    if (WB_GeometryOp
				    .getSqDistance3D(p, cell.getAABB()) <= r2) {
				result.add(cell);
			    }
			}
		    }
		}
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @param S 
     * @param r 
     * @param all 
     * @return 
     */
    public ArrayList<WB_GeomGridCell> getCellsInNeighborhood(
	    final WB_Segment S, final double r, final boolean all) {
	final ArrayList<WB_GeomGridCell> result = new ArrayList<WB_GeomGridCell>();
	final Index ido = ijk(S.getOrigin());
	final Index ide = ijk(S.getEndpoint());
	WB_GeomGridCell cell;
	double r2 = (r + (0.5 * WB_Math.max(dx, dy, dz)));
	r2 *= r2;
	int neighbor;
	final int ri = (int) (r / dx) + 1;
	final int rj = (int) (r / dy) + 1;
	final int rk = (int) (r / dz) + 1;
	int is = Math.min(ido.i - ri, ide.i - ri);
	is = Math.max(0, is);
	int js = Math.min(ido.j - rj, ide.j - rj);
	js = Math.max(0, js);
	int ks = Math.min(ido.k - rk, ide.k - rk);
	ks = Math.max(0, ks);
	int ie = Math.max(ido.i + ri, ide.i + ri);
	ie = Math.min(W - 1, ie);
	int je = Math.max(ido.j + rj, ide.j + rj);
	je = Math.min(H - 1, je);
	int ke = Math.max(ido.k + rk, ide.k + rk);
	ke = Math.min(D - 1, ke);
	for (int di = is; di <= ie; di++) {
	    for (int dj = js; dj <= je; dj++) {
		for (int dk = ks; dk <= ke; dk++) {
		    neighbor = safeIndex(di, dj, dk);
		    if (neighbor > -1) {
			cell = cells.get(neighbor);
			if (cell != null) {
			    if (WB_GeometryOp.getSqDistance3D(cell.getAABB()
				    .getCenter(), S) <= r2) {
				result.add(cell);
			    }
			} else if (all) {
			    cell = getNewCellForIndex(di, dj, dk);
			    if (WB_GeometryOp.getSqDistance3D(cell.getAABB()
				    .getCenter(), S) <= r2) {
				result.add(cell);
			    }
			}
		    }
		}
	    }
	}
	return result;
    }

    /**
     * 
     *
     * @return 
     */
    public ArrayList<WB_GeomGridCell> getCells() {
	final ArrayList<WB_GeomGridCell> cellList = new ArrayList<WB_GeomGridCell>();
	cellList.addAll(cells.values());
	return cellList;
    }

    /**
     * 
     *
     * @return 
     */
    public WB_AABB getAABB() {
	return aabb;
    }

    /**
     * 
     *
     * @param i 
     * @param j 
     * @param k 
     * @return 
     */
    private int index(final int i, final int j, final int k) {
	return i + (j * W) + (k * WH);
    }

    /**
     * 
     *
     * @param id 
     * @return 
     */
    private int index(final Index id) {
	return id.i + (id.j * W) + (id.k * WH);
    }

    /**
     * 
     *
     * @param i 
     * @param j 
     * @param k 
     * @return 
     */
    private int safeIndex(final int i, final int j, final int k) {
	if (i < 0) {
	    return -1;
	}
	if (i > (W - 1)) {
	    return -1;
	}
	if (j < 0) {
	    return -1;
	}
	if (j > (H - 1)) {
	    return -1;
	}
	if (k < 0) {
	    return -1;
	}
	if (k > (D - 1)) {
	    return -1;
	}
	return i + (j * W) + (k * WH);
    }

    /**
     * Safeijk.
     *
     * @param p
     *            the p
     * @return the index
     */
    private Index safeijk(final WB_Coordinate p) {
	final int i = (int) ((p.xd() - min.xd()) * idx);
	if (i < 0) {
	    return null;
	}
	if (i > (W - 1)) {
	    return null;
	}
	final int j = (int) ((p.yd() - min.yd()) * idy);
	if (j < 0) {
	    return null;
	}
	if (j > (H - 1)) {
	    return null;
	}
	final int k = (int) ((p.zd() - min.zd()) * idz);
	if (k < 0) {
	    return null;
	}
	if (k > (D - 1)) {
	    return null;
	}
	return new Index(i, j, k, true);
    }

    /**
     * Ijk.
     *
     * @param p
     *            the p
     * @return the index
     */
    private Index ijk(final WB_Coordinate p) {
	final int i = ((p.xd() - min.xd()) < 0) ? (int) ((p.xd() - min.xd()) * idx) - 1
		: (int) ((p.xd() - min.xd()) * idx);
	final int j = ((p.yd() - min.yd()) < 0) ? (int) ((p.yd() - min.yd()) * idy) - 1
		: (int) ((p.yd() - min.yd()) * idy);
	final int k = ((p.zd() - min.zd()) < 0) ? (int) ((p.zd() - min.zd()) * idz) - 1
		: (int) ((p.zd() - min.zd()) * idz);
	boolean inside = true;
	if (i < 0) {
	    inside = false;
	}
	if (i > (W - 1)) {
	    inside = false;
	}
	if (j < 0) {
	    inside = false;
	}
	if (j > (H - 1)) {
	    inside = false;
	}
	if (k < 0) {
	    inside = false;
	}
	if (k > (D - 1)) {
	    inside = false;
	}
	return new Index(i, j, k, inside);
    }

    /**
     * Indices traversed.
     *
     * @param segment
     *            the segment
     * @return the array list
     */
    public ArrayList<Index> indicesTraversed(final WB_Segment segment) {
	final ArrayList<Index> indicesTraversed = new ArrayList<Index>();
	if (!WB_GeometryOp.checkIntersection3D(segment, aabb)) {
	    return indicesTraversed;
	}
	final Index start = ijk(segment.getOrigin());
	final Index end = ijk(segment.getEndpoint());
	if (start.inside) {
	    indicesTraversed.add(start);
	}
	if (start.equals(end)) {
	    return indicesTraversed;
	}
	final WB_Vector dir = segment.getDirection();
	double x, y, z;
	Index current;
	Index prev;
	final int signx = (dir.xd() < 0) ? -1 : 1;
	final int signy = (dir.yd() < 0) ? -1 : 1;
	final int signz = (dir.zd() < 0) ? -1 : 1;
	x = segment.getOrigin().xd() - min.xd();
	y = segment.getOrigin().yd() - min.yd();
	z = segment.getOrigin().zd() - min.zd();
	current = ijk(segment.getOrigin());
	final double idx = (WB_Epsilon.isZero(dir.xd())) ? Double.POSITIVE_INFINITY
		: 1.0 / dir.xd();
	final double idy = (WB_Epsilon.isZero(dir.yd())) ? Double.POSITIVE_INFINITY
		: 1.0 / dir.yd();
	final double idz = (WB_Epsilon.isZero(dir.zd())) ? Double.POSITIVE_INFINITY
		: 1.0 / dir.zd();
	final double tdx = signx * dx * idx;
	final double tdy = signy * dy * idy;
	final double tdz = signz * dz * idz;
	double tnx, tny, tnz; // distance along ray to next x,y,z grid boundary;
	tnx = (signx > 0) ? ((current.i + 1) * dx) - x : (current.i * dx) - x;
	tnx *= idx;// distance along ray to next x crossing
	tny = (signy > 0) ? ((current.j + 1) * dy) - y : (current.j * dy) - y;
	tny *= idy;// distance along ray to next y crossing
	tnz = (signz > 0) ? ((current.k + 1) * dz) - z : (current.k * dz) - z;
	tnz *= idz;// distance along ray to next z crossing
	do {
	    prev = current.get();
	    if ((tnx <= tny) && (tnx <= tnz)) {// x crossing comes first
		current.i += signx;
		tnx += tdx;
	    } else if ((tny <= tnx) && (tny <= tnz)) {// y crossing comes first
		current.j += signy;
		tny += tdy;
	    } else {// z crossing comes first
		current.k += signz;
		tnz += tdz;
	    }
	    current.inside = true;
	    if (current.i < 0) {
		current.inside = false;
	    }
	    if (current.i > (W - 1)) {
		current.inside = false;
	    }
	    if (current.j < 0) {
		current.inside = false;
	    }
	    if (current.j > (H - 1)) {
		current.inside = false;
	    }
	    if (current.k < 0) {
		current.inside = false;
	    }
	    if (current.k > (D - 1)) {
		current.inside = false;
	    }
	    if (current.inside) {
		final Index newindex = new Index(current.i, current.j,
			current.k, current.inside);
		if (!newindex.equals(prev)) {
		    indicesTraversed.add(newindex);
		}
	    }
	} while (!current.equals(end)
		&& ((signx * current.i) <= (signx * end.i))
		&& ((signy * current.j) <= (signy * end.j))
		&& ((signz * current.k) <= (signz * end.k))
		&& (!current.equals(prev)));
	return indicesTraversed;
    }

    /**
     * Cells traversed.
     *
     * @param segment
     *            the segment
     * @param all
     *            the all
     * @return the array list
     */
    public ArrayList<WB_GeomGridCell> cellsTraversed(final WB_Segment segment,
	    final boolean all) {
	final ArrayList<WB_GeomGridCell> result = new ArrayList<WB_GeomGridCell>();
	for (final Index id : indicesTraversed(segment)) {
	    final WB_GeomGridCell cell = cells.get(index(id));
	    if (cell != null) {
		result.add(cell);
	    } else if (all) {
		result.add(getNewCellForIndex(id));
	    }
	}
	return result;
    }

    /**
     * Gets the new cell for index.
     *
     * @param id
     *            the id
     * @return the new cell for index
     */
    private WB_GeomGridCell getNewCellForIndex(final Index id) {
	return new WB_GeomGridCell(index(id), new WB_Point((id.i * dx)
		+ min.xd(), (id.j * dy) + min.yd(), (id.k * dz) + min.zd()),
		new WB_Point((id.i * dx) + min.xd() + dx, (id.j * dy)
			+ min.yd() + dy, (id.k * dz) + min.zd() + dz));
    }

    /**
     * Gets the new cell for index.
     *
     * @param i
     *            the i
     * @param j
     *            the j
     * @param k
     *            the k
     * @return the new cell for index
     */
    private WB_GeomGridCell getNewCellForIndex(final int i, final int j,
	    final int k) {
	return new WB_GeomGridCell(index(i, j, k), new WB_Point((i * dx)
		+ min.xd(), (j * dy) + min.yd(), (k * dz) + min.zd()),
		new WB_Point((i * dx) + min.xd() + dx,
			(j * dy) + min.yd() + dy, (k * dz) + min.zd() + dz));
    }
}
