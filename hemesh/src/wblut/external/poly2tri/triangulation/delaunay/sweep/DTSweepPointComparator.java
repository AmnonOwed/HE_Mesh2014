package wblut.external.poly2tri.triangulation.delaunay.sweep;

import java.util.Comparator;

import wblut.external.poly2tri.triangulation.TriangulationPoint;

public class DTSweepPointComparator implements Comparator<TriangulationPoint> {
	public int compare(final TriangulationPoint p1, final TriangulationPoint p2) {
		if (p1.yd() < p2.yd()) {
			return -1;
		}
		else if (p1.yd() > p2.yd()) {
			return 1;
		}
		else {
			if (p1.xd() < p2.xd()) {
				return -1;
			}
			else if (p1.xd() > p2.xd()) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
}
