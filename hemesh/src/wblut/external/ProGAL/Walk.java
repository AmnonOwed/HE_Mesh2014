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

class Walk {
	private final ExactJavaPredicates primitives;

	public Walk(ExactJavaPredicates primitives) {
		this.primitives = primitives;
	}

	public CTetrahedron walk(CTetrahedron t, CVertex p) {
		int i;
		boolean next = false;

		while (true) {
			for (i = 0; i < 4; i++) {

				if (primitives.inplane(t.getPoint((i + 1) % 4),
						t.getPoint((i + 2) % 4), t.getPoint((i + 3) % 4), p)) {
					p.setDegenerate(true);
					p.setDegCase(CVertex.DegenerateCase.ONFACE);
					p.setDegPointOpposite(t.getPoint(i));

					if (primitives.inplane(t.getPoint((i + 1) % 4),
							t.getPoint((i + 2) % 4), t.getPoint(i % 4), p)) {
						p.setDegCase(CVertex.DegenerateCase.ONEDGE);
						p.setDegPointA(t.getPoint((i + 1) % 4));
						p.setDegPointB(t.getPoint((i + 2) % 4));
					}

					if (primitives.inplane(t.getPoint((i + 2) % 4),
							t.getPoint((i + 3) % 4), t.getPoint(i % 4), p)) {
						p.setDegCase(CVertex.DegenerateCase.ONEDGE);
						p.setDegPointA(t.getPoint((i + 2) % 4));
						p.setDegPointB(t.getPoint((i + 3) % 4));
					}

					if (primitives.inplane(t.getPoint((i + 1) % 4),
							t.getPoint((i + 3) % 4), t.getPoint(i % 4), p)) {
						p.setDegCase(CVertex.DegenerateCase.ONEDGE);
						p.setDegPointA(t.getPoint((i + 1) % 4));
						p.setDegPointB(t.getPoint((i + 3) % 4));
					}
					return t;
				}
				ExactJavaPredicates.PlaneConfig pc = primitives.diffsides(
						t.getPoint((i + 1) % 4), t.getPoint((i + 2) % 4),
						t.getPoint((i + 3) % 4), p, t.getPoint(i));
				// System.out.println("walk .. "+t);
				if (pc == ExactJavaPredicates.PlaneConfig.DIFF) {
					next = true;
					t = t.getNeighbour(i);
					break;
				}

			}
			if (next)
				next = false;
			else
				return t;
		}

	}

}
