package wblut.geom;

import wblut.math.WB_Math;

public class WB_SurfaceBlend implements WB_Surface {

	private final WB_Surface surfA;

	private final WB_Surface surfB;

	public WB_SurfaceBlend(final WB_Surface surfA, final WB_Surface surfB) {
		this.surfA = surfA;
		this.surfB = surfB;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.nurbs.WB_Surface#loweru()
	 */
	public double loweru() {
		return WB_Math.max(surfA.loweru(), surfB.loweru());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.nurbs.WB_Surface#lowerv()
	 */
	public double lowerv() {
		return WB_Math.max(surfA.lowerv(), surfB.lowerv());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.nurbs.WB_Surface#surfacePoint(double, double)
	 */
	public WB_Point surfacePoint(final double u, final double v) {
		return (surfA.surfacePoint(u, v)._addSelf(surfB.surfacePoint(u, v)))
				._mulSelf(0.5);

	}

	public WB_Point surfacePoint(final double u, final double v, final double t) {
		if (t == 0) {
			return surfA.surfacePoint(u, v);
		}
		if (t == 1) {
			return surfB.surfacePoint(u, v);
		}
		final WB_Point A = surfA.surfacePoint(u, v);
		return A._addSelf(t, surfB.surfacePoint(u, v)._subSelf(A));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.nurbs.WB_Surface#upperu()
	 */
	public double upperu() {
		return WB_Math.min(surfA.upperu(), surfB.upperu());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wblut.nurbs.WB_Surface#upperv()
	 */
	public double upperv() {
		return WB_Math.min(surfA.upperv(), surfB.upperv());
	}

}
