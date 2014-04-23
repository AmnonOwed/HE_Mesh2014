package wblut.external.ProGAL;

import java.util.Random;

/**
 * Part of ProGAL: http://www.diku.dk/~rfonseca/ProGAL/
 * 
 * Original copyright notice:
 * 
 * Copyright (c) 2013, Dept. of Computer Science - Univ. of Copenhagen. All
 * rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * A wrapper for static randomization functions.
 * 
 * @author rfonseca
 */
public class Randomization {
	private static java.util.Random rand = new java.util.Random();

	public static Random getGenerator() {
		return rand;
	}

	/**
	 * Return a uniform random number between (including) i1 and (not including)
	 * i2.
	 * 
	 * @param i1
	 *            lower bound of random number
	 * @param i2
	 *            upper bound of random number
	 */
	public static int randBetween(int i1, int i2) {
		int max = Math.max(i1, i2);
		int min = Math.min(i1, i2);
		return (int) (rand.nextDouble() * (max - min) + min);
	}

	/**
	 * Return a uniform random double between (including) d1 and (not including)
	 * d2
	 * 
	 * @param d1
	 *            lower bound of random number
	 * @param d2
	 *            upper bound of random number
	 */
	public static double randBetween(double d1, double d2) {
		double max = Math.max(d1, d2);
		double min = Math.min(d1, d2);
		return rand.nextDouble() * (max - min) + min;
	}

	/**
	 * Generate a random permutation of integers between (including) 0 and (not
	 * including) max
	 * 
	 * @param max
	 *            the length of the permutation
	 */
	public static int[] randomPermutation(int max) {
		int[] ret = new int[max];
		for (int i = 0; i < max; i++) {
			ret[i] = i;
		}
		randomizeInPlace(ret);
		return ret;
	}

	/**
	 * Randomize an array.
	 * 
	 * @param as
	 *            the array to be randomized
	 */
	public static void randomizeInPlace(int[] as) {
		for (int i = 0; i < as.length; i++) {
			int r = randBetween(i, as.length);
			int t = as[i];
			as[i] = as[r];
			as[r] = t;
		}
	}

	/**
	 * Seeds the random generater used by this class.
	 * 
	 * @param s
	 *            seed
	 */
	public static void seed(long s) {
		rand = new java.util.Random(s);
	}

	public static Object[] shuffle(Object[] arr) {
		for (int i = arr.length; i > 1; i--) {
			int r = randBetween(0, i);
			Object tmp = arr[i - 1];
			arr[i - 1] = arr[r];
			arr[r] = tmp;
		}
		return arr;
	}

}
