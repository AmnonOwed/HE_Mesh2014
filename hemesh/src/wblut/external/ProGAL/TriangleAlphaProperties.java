package wblut.external.ProGAL;

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
 */
public class TriangleAlphaProperties implements SimplexAlphaProperties {
	private Interval singular, regular, interior;
	private boolean attached;
	private boolean onConvexHull;
	private double inAlphaComplex;

	public TriangleAlphaProperties(Interval i, Interval r, Interval s,
			boolean a, double inAlpha) {
		this.singular = s;
		this.regular = r;
		this.interior = i;
		this.attached = a;
		this.inAlphaComplex = inAlpha;
	}

	public TriangleAlphaProperties(double muDown, double muUp, double rho,
			boolean ch, boolean a) {
		if (a)
			this.singular = null;
		else
			this.singular = new Interval(rho, muDown);
		if (ch)
			this.regular = new Interval(muDown, Double.POSITIVE_INFINITY);
		else
			this.regular = new Interval(muDown, muUp);
		if (ch)
			this.interior = null;
		else
			this.interior = new Interval(muUp, Double.POSITIVE_INFINITY);
		this.attached = a;
		this.onConvexHull = ch;
		if (a)
			this.inAlphaComplex = muDown;
		else
			this.inAlphaComplex = rho;
	}

	public boolean isAttached() {
		return attached;
	}

	public boolean getOnConvexHull() {
		return onConvexHull;
	}

	public double getInAlphaComplex() {
		return inAlphaComplex;
	}

	public Interval getSingularInterval() {
		return singular;
	}

	public Interval getRegularInterval() {
		return regular;
	}

	public Interval getInteriorInterval() {
		return interior;
	}

	public int getSimplexType() {
		return 2;
	}
}
