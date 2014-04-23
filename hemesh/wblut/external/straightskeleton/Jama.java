package wblut.external.straightskeleton;

import wblut.external.JAMA.Matrix;

/**
 * Part of campskeleton: http://code.google.com/p/campskeleton/
 * 
 * Original Copyright Notice: http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 
 * @author twak
 */
public class Jama {
	/**
	 * 
	 * @param A
	 *            Each rows of A is a cartesian point
	 * @param b
	 *            the offset of the matrix (normally 0?)
	 * @return
	 */
	public static Vector3d solve(final Matrix3d A, final Tuple3d offset) {
		final double[][] as = new double[][] { { A.m00, A.m01, A.m02 },
				{ A.m10, A.m11, A.m12 }, { A.m20, A.m21, A.m22 } };

		final Matrix am = new Matrix(as);

		final double[] bs = new double[] { offset.x, offset.y, offset.z };
		final Matrix bm = new Matrix(bs, 3);

		final double[][] out = am.solve(bm).getArray();

		// use one point on the plain to determine the offset
		final double d = offset.x * out[0][0] + offset.y * out[1][0] + offset.z
				* out[2][0];

		return new Vector3d(out[0][0], out[1][0], out[2][0]);
	}

	public static void main(final String[] args) {
	}
}
