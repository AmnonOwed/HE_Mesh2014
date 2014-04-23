package wblut.geom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import wblut.WB_Epsilon;

public class WB_AABB {

	protected double[] _min;

	protected double[] _max;

	private HashMap<String, Object> _data;

	int _id;

	public WB_AABB(final WB_Coordinate p) {
		_min = new double[3];
		_max = new double[3];
		setToNull();
		expandToInclude(p);
	}

	public WB_AABB() {
		_min = new double[3];
		_max = new double[3];
		setToNull();
	}

	public WB_AABB(final WB_Coordinate[] points) {
		if (points == null) {
			throw new NullPointerException("Array not initialized.");
		}
		if (points.length == 0) {
			throw new IllegalArgumentException("Array has zero size.");
		}
		WB_Coordinate point = points[0];
		if (point == null) {
			throw new NullPointerException("Array point not initialized.");
		}
		init();
		for (int i = 0; i < points.length; i++) {
			for (int j = 0; j < 3; j++) {
				point = points[i];
				if (point == null) {
					throw new NullPointerException(
							"Array point not initialized.");
				}
				if (_min[j] > point.getd(j)) {
					_min[j] = point.getd(j);
				}
				if (_max[j] < point.getd(j)) {
					_max[j] = point.getd(j);
				}
			}
		}
	}

	public WB_AABB(final Collection<? extends WB_Coordinate> points) {
		if (points == null) {
			throw new IllegalArgumentException("Collection not initialized.");
		}
		if (points.size() == 0) {
			throw new IllegalArgumentException("Collection has zero size.");
		}
		WB_Coordinate fpoint = points.iterator().next();
		if (fpoint == null) {
			throw new NullPointerException("Collection point not initialized.");
		}
		init();
		for (WB_Coordinate point : points) {
			if (point == null) {
				throw new NullPointerException(
						"Collection point not initialized.");
			}
			for (int j = 0; j < 3; j++) {
				if (_min[j] > point.getd(j)) {
					_min[j] = point.getd(j);
				}
				if (_max[j] < point.getd(j)) {
					_max[j] = point.getd(j);
				}
			}
		}
	}

	public WB_AABB(final double[] min, final double[] max) {
		if (min.length == 3 && max.length == 3) {
			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 3; i++) {
				if (min[i] < max[i]) {
					_min[i] = min[i];
					_max[i] = max[i];
				} else {
					_min[i] = max[i];
					_max[i] = min[i];
				}
			}
		} else if (min.length == 2 && max.length == 2) {
			_min = new double[2];
			_max = new double[2];
			for (int i = 0; i < 2; i++) {
				if (min[i] < max[i]) {
					_min[i] = min[i];
					_max[i] = max[i];
				} else {
					_min[i] = max[i];
					_max[i] = min[i];
				}
			}
			_min[2] = _max[2] = 0;

		} else {
			throw new IllegalArgumentException();
		}
	}

	public WB_AABB(final float[] min, final float[] max) {
		if (min.length == 3 && max.length == 3) {
			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 3; i++) {
				if (min[i] < max[i]) {
					_min[i] = min[i];
					_max[i] = max[i];
				} else {
					_min[i] = max[i];
					_max[i] = min[i];
				}
			}
		} else if (min.length == 2 && max.length == 2) {
			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 2; i++) {
				if (min[i] < max[i]) {
					_min[i] = min[i];
					_max[i] = max[i];
				} else {
					_min[i] = max[i];
					_max[i] = min[i];
				}
			}
			_min[2] = _max[2] = 0;

		} else {
			throw new IllegalArgumentException();
		}
	}

	public WB_AABB(final int[] min, final int[] max) {
		if (min.length == 3 && max.length == 3) {
			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 3; i++) {
				if (min[i] < max[i]) {
					_min[i] = min[i];
					_max[i] = max[i];
				} else {
					_min[i] = max[i];
					_max[i] = min[i];
				}
			}
		} else if (min.length == 2 && max.length == 2) {
			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 2; i++) {
				if (min[i] < max[i]) {
					_min[i] = min[i];
					_max[i] = max[i];
				} else {
					_min[i] = max[i];
					_max[i] = min[i];
				}
			}
			_min[2] = _max[2] = 0;

		} else {
			throw new IllegalArgumentException();
		}
	}

	public WB_AABB(final WB_Coordinate min, final WB_Coordinate max) {
		_min = new double[3];
		_max = new double[3];
		for (int i = 0; i < 3; i++) {
			if (min.getd(i) < max.getd(i)) {
				_min[i] = min.getd(i);
				_max[i] = max.getd(i);
			} else {
				_min[i] = max.getd(i);
				_max[i] = min.getd(i);
			}
		}
	}

	public WB_AABB(final double minx, final double miny, final double maxx,
			final double maxy) {
		this();
		expandToInclude(minx, miny, 0);
		expandToInclude(maxx, maxy, 0);
	}

	public WB_AABB(final double minx, final double miny, final double minz,
			final double maxx, final double maxy, final double maxz) {
		this();
		expandToInclude(minx, miny, minz);
		expandToInclude(maxx, maxy, maxz);
	}

	public WB_AABB(final double[] values) {
		if (values.length == 0) {
			_min = new double[3];
			_max = new double[3];
			setToNull();

		}

		else if (values.length == 6) {

			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 3; i++) {
				_min[i] = values[i];
				_max[i] = values[i + 3];
			}
		} else if (values.length == 4) {

			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 2; i++) {
				_min[i] = values[i];
				_max[i] = values[i + 2];
			}
			_min[2] = _max[2] = 0;
		} else {
			throw new IllegalArgumentException();

		}
	}

	public WB_AABB(final int[] values) {
		if (values.length == 0) {
			_min = new double[3];
			_max = new double[3];
			setToNull();

		}

		else if (values.length == 6) {

			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 3; i++) {
				_min[i] = values[i];
				_max[i] = values[i + 3];
			}
		} else if (values.length == 4) {

			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 2; i++) {
				_min[i] = values[i];
				_max[i] = values[i + 2];
			}
			_min[2] = _max[2] = 0;
		} else {
			throw new IllegalArgumentException();

		}
	}

	public WB_AABB(final float[] values) {
		if (values.length == 0) {
			_min = new double[3];
			_max = new double[3];
			setToNull();

		}

		else if (values.length == 6) {

			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 3; i++) {
				_min[i] = values[i];
				_max[i] = values[i + 3];
			}
		} else if (values.length == 4) {

			_min = new double[3];
			_max = new double[3];
			for (int i = 0; i < 2; i++) {
				_min[i] = values[i];
				_max[i] = values[i + 2];
			}
			_min[2] = _max[2] = 0;
		} else {
			throw new IllegalArgumentException();

		}
	}

	public WB_AABB(final WB_CoordinateSequence<? extends WB_Coordinate> points) {
		this();
		int id = 0;
		double val;
		for (int i = 0; i < points.size(); i++) {
			for (int j = 0; j < 3; j++) {
				val = points.getRaw(id++);
				if (_min[j] > val) {
					_min[j] = val;
				}
				if (_max[j] < val) {
					_max[j] = val;
				}
			}
		}
	}

	public double getSize(final int i) {
		if (isNull()) {
			return 0;
		}
		return _max[i] - _min[i];
	}

	public double getMin(final int i) {
		return _min[i];
	}

	public double getMax(final int i) {
		return _max[i];
	}

	public int minOrdinate() {
		if (isNull()) {
			return 0;
		}
		double res = Double.POSITIVE_INFINITY;
		int ord = 0;
		for (int i = 0; i < 3; i++) {
			double w = getSize(i);
			if (res > w) {
				res = w;
				ord = i;
			}
		}
		return ord;
	}

	public int maxOrdinate() {
		if (isNull()) {
			return 0;
		}
		double res = Double.NEGATIVE_INFINITY;
		int ord = 0;
		for (int i = 0; i < 3; i++) {
			double w = getSize(i);
			if (res < w) {
				res = w;
				ord = i;
			}
		}
		return ord;
	}

	public void expandToInclude(final WB_Coordinate p) {
		expandToInclude(p.xd(), p.yd(), p.zd());
	}

	public void expandBy(final double distance) {
		if (isNull()) {
			return;
		}
		for (int i = 0; i < 3; i++) {
			_min[i] -= distance;
			_max[i] += distance;
			if (_min[i] > _max[i]) {
				setToNull();
				return;
			}
		}
	}

	public void expandBy(final double[] delta) {
		if (isNull()) {
			return;
		}
		for (int i = 0; i < 3; i++) {
			_min[i] -= delta[i];
			_max[i] += delta[i];
			if (_min[i] > _max[i]) {
				setToNull();
				return;
			}
		}
	}

	public void expandBy(final double dx, double dy, double dz) {
		if (isNull()) {
			return;
		}

		_min[0] -= dx;
		_max[0] += dx;
		if (_min[0] > _max[0]) {
			setToNull();
			return;
		}
		_min[1] -= dy;
		_max[1] += dy;
		if (_min[1] > _max[1]) {
			setToNull();
			return;
		}
		_min[2] -= dz;
		_max[2] += dz;
		if (_min[2] > _max[2]) {
			setToNull();
			return;
		}

	}

	public void expandToInclude(final double[] p) {
		if (isNull()) {
			for (int i = 0; i < 3; i++) {
				_min[i] = p[i];
				_max[i] = p[i];
			}
		} else {
			for (int i = 0; i < 3; i++) {
				if (p[i] < _min[i]) {
					_min[i] = p[i];
				}
				if (p[i] > _max[i]) {
					_max[i] = p[i];
				}
			}
		}
	}

	public void expandToInclude(final double x, double y, double z) {
		if (isNull()) {

			_min[0] = x;
			_max[0] = x;
			_min[1] = y;
			_max[1] = y;
			_min[2] = z;
			_max[2] = z;

		} else {

			if (x < _min[0]) {
				_min[0] = x;
			}
			if (x > _max[0]) {
				_max[0] = x;
			}
			if (y < _min[1]) {
				_min[1] = y;
			}
			if (y > _max[1]) {
				_max[1] = y;
			}
			if (z < _min[2]) {
				_min[2] = z;
			}
			if (z > _max[2]) {
				_max[2] = z;
			}

		}
	}

	public void expandToInclude(final WB_AABB other) {
		expandToInclude(other._min);
		expandToInclude(other._max);
	}

	public void translate(final double[] d) {
		if (isNull()) {
			return;
		}
		for (int i = 0; i < 3; i++) {
			_min[i] += d[i];
			_max[i] += d[i];
		}
	}

	public boolean intersects(final WB_AABB other) {
		if (isNull() || other.isNull()) {
			return false;
		}
		for (int i = 0; i < 3; i++) {
			if (other._min[i] > _max[i]) {
				return false;
			}
			if (other._max[i] < _min[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean intersects(final WB_Coordinate p) {
		return intersects(p.xd(), p.yd(), p.zd());
	}

	public boolean intersects(final double[] x) {
		if (isNull()) {
			return false;
		}
		for (int i = 0; i < 3; i++) {
			if (x[i] > _max[i]) {
				return false;
			}
			if (x[i] < _min[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean intersects(final double x, double y, double z) {
		if (isNull()) {
			return false;
		}

		if (x > _max[0]) {
			return false;
		}
		if (x < _min[0]) {
			return false;
		}
		if (y > _max[1]) {
			return false;
		}
		if (y < _min[1]) {
			return false;
		}
		if (z > _max[2]) {
			return false;
		}
		if (z < _min[2]) {
			return false;
		}
		return true;
	}

	public boolean contains(final WB_AABB other) {
		return covers(other);
	}

	public boolean contains(final WB_Coordinate p) {
		return covers(p);
	}

	public boolean contains(final double[] x) {
		return covers(x);
	}

	public boolean covers(final double[] x) {
		if (isNull()) {
			return false;
		}
		for (int i = 0; i < 3; i++) {
			if (x[i] > _max[i]) {
				return false;
			}
			if (x[i] < _min[i]) {
				return false;
			}
		}
		return true;
	}

	public boolean covers(final double x, double y, double z) {
		if (isNull()) {
			return false;
		}

		if (x > _max[0]) {
			return false;
		}
		if (x < _min[0]) {
			return false;
		}
		if (y > _max[1]) {
			return false;
		}
		if (y < _min[1]) {
			return false;
		}
		if (z > _max[2]) {
			return false;
		}
		if (z < _min[2]) {
			return false;
		}
		return true;
	}

	public boolean covers(final WB_Coordinate p) {
		return covers(p.xd(), p.yd(), p.zd());
	}

	public boolean covers(final WB_AABB other) {
		if (isNull() || other.isNull()) {
			return false;
		}
		for (int i = 0; i < 3; i++) {
			if (other._max[i] > _max[i]) {
				return false;
			}
			if (other._min[i] < _min[i]) {
				return false;
			}
		}
		return true;
	}

	public double getDistance(final WB_AABB other) {
		if (intersects(other)) {
			return 0;
		}
		double dx = 0;
		double sqr = 0;
		for (int i = 0; i < 3; i++) {
			if (_max[i] < other._min[i]) {
				dx = other._min[i] - _max[i];
			} else if (_min[i] > other._max[i]) {
				dx = _min[i] - other._max[i];
			}
			sqr += dx * dx;
		}
		return Math.sqrt(sqr);
	}

	public double getDistanceSquare(final WB_AABB other) {
		if (intersects(other)) {
			return 0;
		}
		double dx = 0;
		double sqr = 0;
		for (int i = 0; i < 3; i++) {
			if (_max[i] < other._min[i]) {
				dx = other._min[i] - _max[i];
			} else if (_min[i] > other._max[i]) {
				dx = _min[i] - other._max[i];
			}
			sqr += dx * dx;
		}
		return sqr;
	}

	public double getDistance(final WB_Coordinate tuple) {
		double dx = 0;
		double sqr = 0;
		for (int i = 0; i < 3; i++) {
			if (_max[i] < tuple.getd(i)) {
				sqr += (dx = tuple.getd(i) - _max[i]) * dx;
			} else if (_min[i] > tuple.getd(i)) {
				sqr += (dx = _min[i] - tuple.getd(i)) * dx;
			}
		}
		return Math.sqrt(sqr);
	}

	public double getDistanceSquare(final WB_Coordinate tuple) {
		double dx = 0;
		double sqr = 0;
		for (int i = 0; i < 3; i++) {
			if (_max[i] < tuple.getd(i)) {
				sqr += (dx = tuple.getd(i) - _max[i]) * dx;
			} else if (_min[i] > tuple.getd(i)) {
				sqr += (dx = _min[i] - tuple.getd(i)) * dx;
			}
		}
		return sqr;
	}

	public boolean equals(final WB_AABB other) {
		if (isNull()) {
			return other.isNull();
		}
		for (int i = 0; i < 3; i++) {
			if (other._max[i] != _max[i]) {
				return false;
			}
			if (other._min[i] != _min[i]) {
				return false;
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		String string = "WB_AABB [";
		int i = 0;
		for (i = 0; i < 3 - 1; i++) {
			string += _min[0] + ":" + _max[0] + ", ";
		}
		string += _min[i] + ":" + _max[i] + "]";
		return string;
	}

	public int numberOfPoints() {
		if (isNull()) {
			return 0;
		}
		return 8;
	}

	public int numberOfSegments() {
		if (isNull()) {
			return 0;
		}
		return 12;
	}

	public int numberOfTriangles() {
		if (isNull()) {
			return 0;
		}
		return 12;
	}

	public int numberOfFaces() {
		if (isNull()) {
			return 0;
		}
		return 6;
	}

	public List<double[]> getCoords() {
		if (isNull()) {
			return null;
		}
		int n = numberOfPoints();
		List<double[]> points = new ArrayList<double[]>(n);
		double[] values;
		for (int i = 0; i < n; i++) {
			values = new double[3];
			int disc = 1;
			for (int j = 0; j < 3; j++) {
				if (((i / disc) % 2) == 0) {
					values[j] = _min[j];
				} else {
					values[j] = _max[j];
				}
				disc *= 2;
			}
			points.add(values);
		}
		return points;
	}

	public double[] getCoord(final int i) {
		double[] values = new double[3];
		int disc = 1;
		for (int j = 0; j < 3; j++) {
			if (((i / disc) % 2) == 0) {
				values[j] = _min[j];
			} else {
				values[j] = _max[j];
			}
			disc *= 2;
		}
		return (values);
	}

	public List<int[]> getSegments() {
		List<double[]> points = getCoords();
		List<int[]> segments = new ArrayList<int[]>(numberOfSegments());
		for (int i = 0; i < points.size(); i++) {
			for (int j = i + 1; j < points.size(); j++) {
				int comp = 0;
				for (int k = 0; k < 3; k++) {
					if (points.get(i)[k] != points.get(j)[k]) {
						comp++;
					}
					if (comp > 1) {
						break;
					}
				}
				if (comp == 1) {
					int[] seg = { i, j };
					segments.add(seg);
				}
			}
		}
		return segments;
	}

	public void setData(final String s, final Object o) {
		if (_data == null) {
			_data = new HashMap<String, Object>();
		}
		_data.put(s, o);
	}

	public Object getData(final String s) {
		return _data.get(s);
	}

	public int getId() {
		return _id;
	}

	public void setId(final int id) {
		_id = id;
	}

	public boolean isDegenerate() {
		return ((getTrueDim() < 3) && (getTrueDim() > -1));
	}

	public void set(final WB_AABB src) {
		System.arraycopy(src._min, 0, _min, 0, 3);
		System.arraycopy(src._max, 0, _max, 0, 3);
	}

	private void init() {
		_min = new double[3];
		_max = new double[3];
		for (int i = 0; i < 3; i++) {
			_min[i] = Double.POSITIVE_INFINITY;
			_max[i] = Double.NEGATIVE_INFINITY;
		}
	}

	public WB_AABB get() {
		return new WB_AABB(_min, _max);
	}

	public WB_AABB union(final WB_AABB aabb) {
		double[] newmin = new double[3];
		double[] newmax = new double[3];
		for (int i = 0; i < 3; i++) {
			newmin[i] = Math.min(_min[i], aabb._min[i]);
			newmax[i] = Math.max(_max[i], aabb._max[i]);
		}
		return new WB_AABB(newmin, newmax);
	}

	public WB_AABB intersection(final WB_AABB other) {
		if (isNull() || other.isNull() || !intersects(other)) {
			return null;
		}
		double[] newmin = new double[3];
		double[] newmax = new double[3];
		for (int i = 0; i < 3; i++) {
			newmin[i] = Math.max(_min[i], other._min[i]);
			newmax[i] = Math.min(_max[i], other._max[i]);
		}
		return new WB_AABB(newmin, newmax);
	}

	public static boolean intersects(final WB_Coordinate p1,
			final WB_Coordinate p2, final WB_Coordinate q) {
		if (((q.xd() >= (p1.xd() < p2.xd() ? p1.xd() : p2.xd())) && (q.xd() <= (p1
				.xd() > p2.xd() ? p1.xd() : p2.xd())))
				&& ((q.yd() >= (p1.yd() < p2.yd() ? p1.yd() : p2.yd())) && (q
						.yd() <= (p1.yd() > p2.yd() ? p1.yd() : p2.yd())))
				&& ((q.zd() >= (p1.zd() < p2.zd() ? p1.zd() : p2.zd())) && (q
						.zd() <= (p1.zd() > p2.zd() ? p1.yd() : p2.zd())))) {
			return true;
		}
		return false;
	}

	public static boolean intersects(final WB_Coordinate p1,
			final WB_Coordinate p2, final WB_Coordinate q1,
			final WB_Coordinate q2) {
		double minq = Math.min(q1.xd(), q2.xd());
		double maxq = Math.max(q1.xd(), q2.xd());
		double minp = Math.min(p1.xd(), p2.xd());
		double maxp = Math.max(p1.xd(), p2.xd());
		if (minp > maxq) {
			return false;
		}
		if (maxp < minq) {
			return false;
		}
		minq = Math.min(q1.yd(), q2.yd());
		maxq = Math.max(q1.yd(), q2.yd());
		minp = Math.min(p1.yd(), p2.yd());
		maxp = Math.max(p1.yd(), p2.yd());
		if (minp > maxq) {
			return false;
		}
		if (maxp < minq) {
			return false;
		}
		minq = Math.min(q1.zd(), q2.zd());
		maxq = Math.max(q1.zd(), q2.zd());
		minp = Math.min(p1.zd(), p2.zd());
		maxp = Math.max(p1.zd(), p2.zd());
		if (minp > maxq) {
			return false;
		}
		if (maxp < minq) {
			return false;
		}
		return true;
	}

	public WB_Point getCenter() {
		double[] center = new double[3];
		for (int i = 0; i < 3; i++) {
			center[i] = 0.5 * (_min[i] + _max[i]);
		}
		return new WB_Point(center);
	}

	public double getWidth() {
		return getSize(0);
	}

	public double getHeight() {
		return getSize(1);
	}

	public double getDepth() {
		return getSize(2);
	}

	public double getMinX() {
		return _min[0];
	}

	public double getCenterX() {
		return 0.5 * (_min[0] + _max[0]);
	}

	public double getCenterY() {
		return 0.5 * (_min[1] + _max[1]);
	}

	public double getCenterZ() {
		return 0.5 * (_min[2] + _max[2]);
	}

	public double getMaxX() {
		return _max[0];
	}

	public double getMinY() {
		return _min[1];
	}

	public double getMaxY() {
		return _max[1];
	}

	public double getMinZ() {
		return _min[2];
	}

	public double getMaxZ() {
		return _max[2];
	}

	public double getArea() {
		return getWidth() * getHeight() * getDepth();
	}

	public double minExtent() {
		if (isNull()) {
			return 0.0;
		}
		double w = getWidth();
		double h = getHeight();
		double d = getDepth();
		if (w < h) {
			return (w < d) ? w : d;
		}
		return (h < d) ? h : d;
	}

	public double maxExtent() {
		if (isNull()) {
			return 0.0;
		}
		double w = getWidth();
		double h = getHeight();
		double d = getDepth();
		if (w > h) {
			return (w > d) ? w : d;
		}
		return (h > d) ? h : d;
	}

	public void translate(final double x, final double y, final double z) {
		if (isNull()) {
			return;
		}
		_min[0] += x;
		_max[0] += x;
		_min[1] += y;
		_max[1] += y;
		_min[2] += z;
		_max[2] += z;
	}

	public List<int[]> getTriangles() {
		List<int[]> tris = new ArrayList<int[]>();
		int[] tri01 = { 4, 5, 6 };
		int[] tri02 = { 5, 7, 6 };
		tris.add(tri01);
		tris.add(tri02);
		int[] tri11 = { 0, 2, 1 };
		int[] tri12 = { 2, 3, 1 };
		tris.add(tri11);
		tris.add(tri12);
		int[] tri21 = { 0, 1, 4 };
		int[] tri22 = { 1, 5, 4 };
		tris.add(tri21);
		tris.add(tri22);
		int[] tri31 = { 3, 2, 7 };
		int[] tri32 = { 2, 6, 7 };
		tris.add(tri31);
		tris.add(tri32);
		int[] tri41 = { 0, 4, 2 };
		int[] tri42 = { 4, 6, 2 };
		tris.add(tri41);
		tris.add(tri42);
		int[] tri51 = { 1, 3, 5 };
		int[] tri52 = { 3, 7, 5 };
		tris.add(tri51);
		tris.add(tri52);
		return tris;
	}

	public List<int[]> getFaces() {
		List<int[]> faces = new ArrayList<int[]>();
		int[] face0 = { 4, 5, 7, 6 };
		faces.add(face0);
		int[] face1 = { 0, 2, 3, 1 };
		faces.add(face1);
		int[] face2 = { 0, 1, 5, 4 };
		faces.add(face2);
		int[] face3 = { 3, 2, 6, 7 };
		faces.add(face3);
		int[] face4 = { 0, 4, 6, 2 };
		faces.add(face4);
		int[] face5 = { 1, 3, 7, 5 };
		faces.add(face5);
		return faces;
	}

	public WB_Point getMin() {
		return new WB_Point(_min);
	}

	public WB_Point getMax() {
		return new WB_Point(_max);
	}

	public int getDim() {
		return 3;
	}

	public int getTrueDim() {
		if (!isValid()) {
			return -1;
		}
		int dim = 0;
		for (int i = 0; i < 3; i++) {
			if ((_max[i] - _min[i]) >= WB_Epsilon.EPSILON) {
				dim++;
			}
		}
		return dim;
	}

	public void pad(final double factor) {
		final WB_Point c = getCenter();
		for (int i = 0; i < 3; i++) {
			_min[i] = c.getd(i) + (factor + 1.0) * (_min[i] - c.getd(i));
			_max[i] = c.getd(i) + (factor + 1.0) * (_max[i] - c.getd(i));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		int result = 17;
		for (int i = 0; i < 3; i++) {
			result = 37 * result + hashCode(_min[i]);
			result = 37 * result + hashCode(_max[i]);
		}
		return result;
	}

	private int hashCode(final double v) {
		final long tmp = Double.doubleToLongBits(v);
		return (int) (tmp ^ (tmp >>> 32));
	}

	public void setToNull() {
		for (int i = 0; i < 3; i++) {
			_min[i] = 0;
			_max[i] = -1;
		}
	}

	public boolean isNull() {
		return _max[0] < _min[0];
	}

	public boolean isValid() {
		return !isNull();
	}
}
