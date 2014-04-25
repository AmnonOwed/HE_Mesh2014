package wblut.external.straightskeleton;

import java.util.Comparator;
import java.util.Iterator;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * @author twak
 */
public class Corner extends Point3d implements Iterable<Corner> {
	public Edge nextL, prevL;

	public Corner nextC, prevC;

	public Corner(double x, double y, double z) {
		super(x, y, z);
	}

	public Corner(Tuple3d in) {
		super(in);
	}

	public Corner(double x, double y) {
		super(x, y, 0);
	}

	public Point3d getLoc3() {
		return new Point3d(x, y, 0);
	}

	/**
	 * Corners (unlike point3ds) are only equal to themselves. We never move a
	 * point, but can create multiple (uniques) at one location. We also change
	 * prev/next pointers to edges and other corners but need to retain hashing
	 * behaviour. Therefore we revert to the system hash.
	 * 
	 * @param t1
	 * @return true/false
	 */
	@Override
	public boolean equals(Object t1) {
		return this == t1;
		// try
		// {
		// Corner other = (Corner )t1;
		// return super.equals( t1 ) &&
		// other.nextL == nextL &&
		// other.prevL == prevL;
		// }
		// catch (ClassCastException e)
		// { return false; }
	}

	@Override
	public String toString() {
		return String.format("(%f,%f,%f)", x, y, z);
	}

	/**
	 * We rely on the fact that we can shift the point's heights without
	 * changing their locations in hashmaps.
	 * 
	 * @return integer hashCode
	 */
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	public Iterator iterator() {
		return new CornerIterator(this);
	}

	/**
	 * Over all corners in the same loop as the one given
	 */
	public class CornerIterator implements Iterator<Corner> {
		Corner s, n, start;

		public CornerIterator(Corner start) {
			s = start;
			n = null;
		}

		public boolean hasNext() {
			if (s == null)
				return false;
			if (n == null)
				return true;
			return n != s;
		}

		public Corner next() {
			if (n == null)
				n = s;

			Corner out = n;
			n = n.nextC;
			return out;
		}

		public void remove() {
			throw new UnsupportedOperationException("Not supported yet.");
		}
	}

	public static class CornerDistanceComparator implements Comparator<Point3d> {
		Point3d start;

		public CornerDistanceComparator(Point3d corner) {
			this.start = corner;
		}

		public int compare(Point3d o1, Point3d o2) {
			return Double.compare(start.distanceSquared(o1),
					start.distanceSquared(o2));
		}
	}

	public static void replace(Corner old, Corner neu, Skeleton skel) {
		old.prevL.currentCorners.remove(old);
		old.nextL.currentCorners.remove(old);

		old.nextC.prevC = neu;
		old.prevC.nextC = neu;
		neu.prevC = old.prevC;
		neu.nextC = old.nextC;

		neu.nextL = old.nextL;
		neu.prevL = old.prevL;

		neu.prevL.currentCorners.add(neu);
		neu.nextL.currentCorners.add(neu);

		skel.liveCorners.remove(old);
		skel.liveCorners.add(neu);
	}

	/**
	 * Clones this set of corners.
	 * 
	 * Creates new edges, corners and machines from the given set. Preserves
	 * currentCorners.
	 * 
	 * @param ribbon
	 * @return cloned set of corners
	 */
	public static LoopL<Corner> dupeNewAll(LoopL<Corner> ribbon) {

		final Cache<Corner, Corner> cacheC = new Cache<Corner, Corner>() {
			Cache<Machine, Machine> cacheM = new Cache<Machine, Machine>() {

				@Override
				public Machine create(Machine i) {
					return new Machine(i.currentAngle);
				}
			};

			Cache<Edge, Edge> cacheE = new Cache<Edge, Edge>() {

				@Override
				public Edge create(Edge i) {
					Edge out = new Edge(getCorner(i.start), getCorner(i.end));
					out.setAngle(i.getAngle());
					out.machine = cacheM.get(i.machine);

					for (Corner c : i.currentCorners) {
						out.currentCorners.add(getCorner(c));
					}

					return out;
				}
			};

			public Corner getCorner(Corner input) // wrapper for inner caches -
													// stupid java
			{
				Corner ner = get(input);

				return ner;
			}

			@Override
			public Corner create(Corner i) {
				Corner ner = new Corner(i.x, i.y);

				cache.put(i, ner);

				ner.nextC = get(i.nextC); // Cache<Corner,Corner>.get()
				ner.prevC = get(i.prevC);
				ner.nextL = cacheE.get(i.nextL);
				ner.prevL = cacheE.get(i.prevL);

				return ner;
			}
		};

		LoopL<Corner> loopl = new LoopL();
		for (Loop<Corner> pLoop : ribbon) {
			Loop<Corner> loop = new Loop();
			loopl.add(loop);
			for (Corner c : pLoop)
				loop.append(cacheC.get(c));
		}

		return loopl;
	}

	public static LoopL<Point3d> dupeNewAllPoints(LoopL<Point3d> ribbon) {

		final Cache<Point3d, Point3d> cacheC = new Cache<Point3d, Point3d>() {
			@Override
			public Point3d create(Point3d i) {
				return new Point3d(i);
			}
		};

		LoopL<Point3d> loopl = new LoopL();
		for (Loop<Point3d> pLoop : ribbon) {
			Loop<Point3d> loop = new Loop();
			loopl.add(loop);
			for (Point3d c : pLoop)
				loop.append(cacheC.get(c));
		}

		return loopl;
	}

	public static LoopL<Point3d> dupeNewAllPoints(LoopL<Point3d> ribbon,
			final double height) {

		final Cache<Point3d, Point3d> cacheC = new Cache<Point3d, Point3d>() {
			@Override
			public Point3d create(Point3d i) {
				return new Point3d(i.x, i.y, height);
			}
		};

		LoopL<Point3d> loopl = new LoopL();
		for (Loop<Point3d> pLoop : ribbon) {
			Loop<Point3d> loop = new Loop();
			loopl.add(loop);
			for (Point3d c : pLoop)
				loop.append(cacheC.get(c));
		}

		return loopl;
	}

	public static LContext<Corner> findLContext(LoopL<Corner> in, Corner c) {
		for (Loop<Corner> loop : in)
			for (Loopable<Corner> lc : loop.loopableIterator())
				if (lc.get() == c)
					return new LContext<Corner>(lc, loop);

		return null;
	}
}
