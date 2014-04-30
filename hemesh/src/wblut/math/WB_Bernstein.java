package wblut.math;

public class WB_Bernstein {

	/**
	 * Calculate the (n+1) coefficients of the Bernstein polynomial of order n,
	 * for the value u (0..1)
	 * 
	 * @param u
	 *            value at which to evaluate the Bernstein coefficients
	 * @param order
	 *            order of the Bernstein polynomial
	 * @return n+1 coefficients at value u
	 */
	public static double[] BernsteinCoefficientsOfOrderN(final double u,
			final int order) {
		final double[] B = new double[order + 1];
		B[0] = 1.0;
		final double u1 = 1.0 - u;
		double saved, temp;
		;
		for (int j = 1; j <= order; j++) {
			saved = 0.0;
			for (int k = 0; k < j; k++) {
				temp = B[k];
				B[k] = saved + u1 * temp;
				saved = u * temp;
			}
			B[j] = saved;
		}

		return B;
	}
}
