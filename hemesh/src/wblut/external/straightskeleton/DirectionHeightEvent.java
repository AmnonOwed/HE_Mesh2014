package wblut.external.straightskeleton;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 */

public class DirectionHeightEvent implements HeightEvent {
	protected double height;
	// newAngle is the angle that this edge will turn towards at the above
	// height
	// length is the distance until the next event (only used for horizontal
	// directions)
	public double newAngle;
	Machine machine;

	public DirectionHeightEvent(Machine machine, double angle) {
		this(machine, 0, angle);
	}

	public double getAngle() {
		return newAngle;
	}

	public DirectionHeightEvent(Machine machine, double height, double angle) {
		super();
		this.machine = machine;
		this.height = height;
		this.newAngle = angle;
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public boolean process(Skeleton skel) {
		// System.out.println("machine "+machine.toString()+" at "+height+" setting angle "+newAngle
		// );

		// set the new angle
		machine.currentAngle = newAngle;

		SkeletonCapUpdate update = new SkeletonCapUpdate(skel);

		// add in the output edges for the outgoing face:
		LoopL<Corner> cap = update.getCap(height);

		CornerClone cc = new CornerClone(cap);

		// preserve corner information for assigning parents, later
		DHash<Corner, Corner> nOCorner = cc.nOCorner.shallowDupe();

		for (Corner c : cc.output.eIterator()) {
			// corners are untouched if neither attached edge has this machine
			if (c.nextL.machine == machine || c.prevL.machine == machine)
				nOCorner.removeA(c);

			// segments are untouched if they don't contain this machine
			if (c.nextL.machine == machine)
				cc.nOSegments.removeA(c);
		}

		update.update(cc.output, cc.nOSegments, nOCorner);

		/**
		 * Must now update the parent field in output for the new edges, so that
		 * we have a know where a face came from
		 */
		for (Corner c : skel.liveCorners) {
			if (c.nextL.machine == machine) {
				// Face neu = skel.output.faces.get( c.nextL.start );

				Corner old = update.getOldBaseLookup().get(cc.nOCorner.get(c));

				skel.output.setParent(c.nextL.start, old.nextL.start);

				// neu.parent = skel.output.faces.get( old.nextL.start );
			}
		}

		machine.findNextHeight(skel);
		return true;
	}
}

// skel.replaceEdges( toChange, new EdgeCreator()
// {
// public List<Corner> getEdges( Edge old, Corner startH, Corner endH )
// {
// Edge e = new Edge( startH, endH );
// e.setAngle( newAngle );
// e.machine = machine;
//
// startH.nextL = e;
// endH.prevL = e;
//
// List<Corner> out = new ArrayList();
// out.add( startH );
// out.add( endH );
// return out;
// }

// public Set<Feature> getFeaturesFor( Edge edgeH )
// {
// return profileFeatures;
// }
// }, height );
