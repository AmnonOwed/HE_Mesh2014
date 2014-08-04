package wblut.external.poly2tri;

import wblut.external.poly2tri.triangulation.TriangulationPoint;

public class PolygonPoint extends TriangulationPoint {
	protected PolygonPoint _next;
	protected PolygonPoint _previous;

	public PolygonPoint(final double x, final double y) {
		super(x, y);
	}

	public PolygonPoint(final double x, final double y, final double z) {
		super(x, y, z);
	}

	public void setPrevious(final PolygonPoint p) {
		_previous = p;
	}

	public void setNext(final PolygonPoint p) {
		_next = p;
	}

	public PolygonPoint getNext() {
		return _next;
	}

	public PolygonPoint getPrevious() {
		return _previous;
	}
}
