/* Poly2Tri
 * Copyright (c) 2009-2010, Poly2Tri Contributors
 * http://code.google.com/p/poly2tri/
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of Poly2Tri nor the names of its contributors may be
 *   used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package wblut.external.poly2tri.triangulation.delaunay.sweep;

import java.util.ArrayDeque;
import java.util.Collections;

import org.apache.log4j.Logger;

import wblut.external.poly2tri.triangulation.TriangleProvider;
import wblut.external.poly2tri.triangulation.TriangulationConstraint;
import wblut.external.poly2tri.triangulation.TriangulationContext;
import wblut.external.poly2tri.triangulation.TriangulationPoint;
import wblut.external.poly2tri.triangulation.delaunay.DelaunayTriangle;

/**
 *
 * @author Thomas �hl�n, thahlen@gmail.com
 *
 */
public class DTSweepContext extends TriangulationContext<DTSweepDebugContext> {
	private final static Logger logger = Logger.getLogger(DTSweepContext.class);

	// Inital triangle factor, seed triangle will extend 30% of
	// PointSet width to both left and right.
	private final float ALPHA = 0.3f;

	/** Advancing front **/
	protected AdvancingFront aFront;
	/** head point used with advancing front */
	private TriangulationPoint _head;
	/** tail point used with advancing front */
	private TriangulationPoint _tail;
	protected Basin basin = new Basin();
	protected EdgeEvent edgeEvent = new EdgeEvent();

	private final DTSweepPointComparator _comparator = new DTSweepPointComparator();

	public DTSweepContext() {
		clear();
	}

	@Override
	public void isDebugEnabled(final boolean b) {
		if (b) {
			if (_debug == null) {
				_debug = new DTSweepDebugContext(this);
			}
		}
		_debugEnabled = b;
	}

	public void removeFromList(final DelaunayTriangle triangle) {
		_triList.remove(triangle);
		// TODO: remove all neighbor pointers to this triangle
		// for( int i=0; i<3; i++ )
		// {
		// if( triangle.neighbors[i] != null )
		// {
		// triangle.neighbors[i].clearNeighbor( triangle );
		// }
		// }
		// triangle.clearNeighbors();
	}

	protected void meshClean(final DelaunayTriangle triangle) {
		DelaunayTriangle t1, t2;
		if (triangle != null) {
			final ArrayDeque<DelaunayTriangle> deque = new ArrayDeque<DelaunayTriangle>();
			deque.addFirst(triangle);
			triangle.isInterior(true);

			while (!deque.isEmpty()) {
				t1 = deque.removeFirst();
				_triUnit.addTriangle(t1);
				for (int i = 0; i < 3; ++i) {
					if (!t1.cEdge[i]) {
						t2 = t1.neighbors[i];
						if (t2 != null && !t2.isInterior()) {
							t2.isInterior(true);
							deque.addLast(t2);
						}
					}
				}
			}
		}
	}

	@Override
	public void clear() {
		super.clear();
		_triList.clear();
	}

	public AdvancingFront getAdvancingFront() {
		return aFront;
	}

	public void setHead(final TriangulationPoint p1) {
		_head = p1;
	}

	public TriangulationPoint getHead() {
		return _head;
	}

	public void setTail(final TriangulationPoint p1) {
		_tail = p1;
	}

	public TriangulationPoint getTail() {
		return _tail;
	}

	public void addNode(final AdvancingFrontNode node) {
		// System.out.println( "add:" + node.key + ":" +
		// System.identityHashCode(node.key));
		// m_nodeTree.put( node.getKey(), node );
		aFront.addNode(node);
	}

	public void removeNode(final AdvancingFrontNode node) {
		// System.out.println( "remove:" + node.key + ":" +
		// System.identityHashCode(node.key));
		// m_nodeTree.delete( node.getKey() );
		aFront.removeNode(node);
	}

	public AdvancingFrontNode locateNode(final TriangulationPoint point) {
		return aFront.locateNode(point);
	}

	public void createAdvancingFront() {
		AdvancingFrontNode head, tail, middle;
		// Initial triangle
		final DelaunayTriangle iTriangle = new DelaunayTriangle(_points.get(0),
				getTail(), getHead());
		addToList(iTriangle);

		head = new AdvancingFrontNode(iTriangle.points[1]);
		head.triangle = iTriangle;
		middle = new AdvancingFrontNode(iTriangle.points[0]);
		middle.triangle = iTriangle;
		tail = new AdvancingFrontNode(iTriangle.points[2]);

		aFront = new AdvancingFront(head, tail);
		aFront.addNode(middle);

		// TODO: I think it would be more intuitive if head is middles next and
		// not previous
		// so swap head and tail
		aFront.head.next = middle;
		middle.next = aFront.tail;
		middle.prev = aFront.head;
		aFront.tail.prev = middle;
	}

	class Basin {
		AdvancingFrontNode leftNode;
		AdvancingFrontNode bottomNode;
		AdvancingFrontNode rightNode;
		public double width;
		public boolean leftHighest;
	}

	class EdgeEvent {
		DTSweepConstraint constrainedEdge;
		public boolean right;
	}

	/**
	 * Try to map a node to all sides of this triangle that don't have a
	 * neighbor.
	 *
	 * @param t
	 */
	public void mapTriangleToNodes(final DelaunayTriangle t) {
		AdvancingFrontNode n;
		for (int i = 0; i < 3; i++) {
			if (t.neighbors[i] == null) {
				n = aFront.locatePoint(t.pointCW(t.points[i]));
				if (n != null) {
					n.triangle = t;
				}
			}
		}
	}

	@Override
	public void prepareTriangulation(final TriangleProvider t) {
		super.prepareTriangulation(t);

		double xmax, xmin;
		double ymax, ymin;

		xmax = xmin = _points.get(0).xd();
		ymax = ymin = _points.get(0).yd();
		// Calculate bounds. Should be combined with the sorting
		for (final TriangulationPoint p : _points) {
			if (p.xd() > xmax) {
				xmax = p.xd();
			}
			if (p.xd() < xmin) {
				xmin = p.xd();
			}
			if (p.yd() > ymax) {
				ymax = p.yd();
			}
			if (p.yd() < ymin) {
				ymin = p.yd();
			}
		}

		final double deltaX = ALPHA * (xmax - xmin);
		final double deltaY = ALPHA * (ymax - ymin);
		final TriangulationPoint p1 = new TriangulationPoint(xmax + deltaX,
				ymin - deltaY);
		final TriangulationPoint p2 = new TriangulationPoint(xmin - deltaX,
				ymin - deltaY);

		setHead(p1);
		setTail(p2);

		// long time = System.nanoTime();
		// Sort the points along y-axis
		Collections.sort(_points, _comparator);
		// logger.info( "Triangulation setup [{}ms]", ( System.nanoTime() - time
		// ) / 1e6 );
	}

	public void finalizeTriangulation() {
		_triUnit.addTriangles(_triList);
		_triList.clear();
	}

	@Override
	public TriangulationConstraint newConstraint(final TriangulationPoint a,
			final TriangulationPoint b) {
		return new DTSweepConstraint(a, b);
	}

}
