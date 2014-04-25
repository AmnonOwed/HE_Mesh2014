package wblut.external.ProGAL;

import java.util.Stack;

import wblut.external.ProGAL.CVertex.DegenerateCase;
import wblut.external.ProGAL.ExactJavaPredicates.PlaneConfig;
import wblut.external.ProGAL.ExactJavaPredicates.SphereConfig;

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

class Flips {
	enum ApexConfig {
		CONVEX, CONCAVE, COPLANAR
	};

	static class Flip {
		private int id;
		private CTetrahedron t;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public CTetrahedron getT() {
			return t;
		}

		public void setT(CTetrahedron t) {
			this.t = t;
		}

		public Flip(int id, CTetrahedron t) {
			super();
			this.id = id;
			this.t = t;
		}

	}

	static class Flip14 {

		private final Flips flips;

		public Flip14(Flips flips) {
			super();
			this.flips = flips;
		}

		public CTetrahedron flip14(CTetrahedron t, CVertex p) {
			CVertex p0, p1, p2, p3;

			p0 = t.getPoint(0);
			p1 = t.getPoint(1);
			p2 = t.getPoint(2);
			p3 = t.getPoint(3);

			CTetrahedron p0p1p2p = new CTetrahedron(p0, p1, p2, p);
			CTetrahedron p0p1p3p = new CTetrahedron(p0, p1, p3, p);
			CTetrahedron p0p2p3p = new CTetrahedron(p0, p2, p3, p);
			CTetrahedron p1p2p3p = new CTetrahedron(p1, p2, p3, p);

			p0p1p2p.setNeighbour(0, p1p2p3p);
			p0p1p2p.setNeighbour(1, p0p2p3p);
			p0p1p2p.setNeighbour(2, p0p1p3p);
			p0p1p2p.setNeighbour(3, t.getNeighbour(3));

			p0p1p3p.setNeighbour(0, p1p2p3p);
			p0p1p3p.setNeighbour(1, p0p2p3p);
			p0p1p3p.setNeighbour(2, p0p1p2p);
			p0p1p3p.setNeighbour(3, t.getNeighbour(2));

			p0p2p3p.setNeighbour(0, p1p2p3p);
			p0p2p3p.setNeighbour(1, p0p1p3p);
			p0p2p3p.setNeighbour(2, p0p1p2p);
			p0p2p3p.setNeighbour(3, t.getNeighbour(1));

			p1p2p3p.setNeighbour(0, p0p2p3p);
			p1p2p3p.setNeighbour(1, p0p1p3p);
			p1p2p3p.setNeighbour(2, p0p1p2p);
			p1p2p3p.setNeighbour(3, t.getNeighbour(0));

			if (t.getNeighbour(0) != null)
				t.getNeighbour(0).updateNeighbour(t, p1p2p3p);
			if (t.getNeighbour(1) != null)
				t.getNeighbour(1).updateNeighbour(t, p0p2p3p);
			if (t.getNeighbour(2) != null)
				t.getNeighbour(2).updateNeighbour(t, p0p1p3p);
			if (t.getNeighbour(3) != null)
				t.getNeighbour(3).updateNeighbour(t, p0p1p2p);

			if (p.isDegenerate()) {

				if (p.getDegCase() == CVertex.DegenerateCase.ONFACE) {
					CVertex index = p.getDegPointOpposite();

					if (index != t.getPoint(0) && index != t.getPoint(1)
							&& index != t.getPoint(2)) {
						p0p1p2p.setFlat(true);
					} else {
						flips.addFlip(3, p0p1p2p);
					}
					if (index != t.getPoint(0) && index != t.getPoint(1)
							&& index != t.getPoint(3)) {
						p0p1p3p.setFlat(true);
					} else {
						flips.addFlip(3, p0p1p3p);
					}
					if (index != t.getPoint(0) && index != t.getPoint(2)
							&& index != t.getPoint(3)) {
						p0p2p3p.setFlat(true);
					} else {
						flips.addFlip(3, p0p2p3p);
					}
					if (index != t.getPoint(1) && index != t.getPoint(2)
							&& index != t.getPoint(3)) {
						p1p2p3p.setFlat(true);
					} else {
						flips.addFlip(3, p1p2p3p);
					}
					if (p0p1p2p.isFlat())
						flips.addFlip(3, p0p1p2p);
					if (p0p1p3p.isFlat())
						flips.addFlip(3, p0p1p3p);
					if (p0p2p3p.isFlat())
						flips.addFlip(3, p0p2p3p);
					if (p1p2p3p.isFlat())
						flips.addFlip(3, p1p2p3p);
				}

				if (p.getDegCase() == CVertex.DegenerateCase.ONEDGE) {

				}

			}

			else {
				flips.addFlip(3, p0p1p2p);
				flips.addFlip(3, p0p1p3p);
				flips.addFlip(3, p0p2p3p);
				flips.addFlip(3, p1p2p3p);
			}

			flips.addTetrahedron(p0p1p2p);
			flips.addTetrahedron(p0p1p3p);
			flips.addTetrahedron(p0p2p3p);
			flips.addTetrahedron(p1p2p3p);

			t.setModified(true);

			return p1p2p3p;

		}

	}

	static class Flip23 {
		private int a1, b1, cid;
		private CTetrahedron t3 = null;

		private final Flips flips;

		public Flip23(Flips flips) {
			super();
			this.flips = flips;
			t3 = null;

		}

		public CTetrahedron getT3() {
			return t3;
		}

		public void setT3(CTetrahedron t3) {
			this.t3 = t3;
		}

		public int getCid() {
			return cid;
		}

		public void setCid(int cid) {
			this.cid = cid;
		}

		public int getA1() {
			return a1;
		}

		public void setA1(int a1) {
			this.a1 = a1;
		}

		public int getB1() {
			return b1;
		}

		public void setB1(int b1) {
			this.b1 = b1;
		}

		public CTetrahedron flip23(CTetrahedron t, int pid, int did) {

			CVertex p = t.getPoint(pid);
			CTetrahedron t2 = t.getNeighbour(pid);
			CVertex d = t2.getPoint(did);

			int aid = (pid + 1) % 4;
			int bid = (pid + 2) % 4;
			int cid = (pid + 3) % 4;

			int a2id = findpoint(t2, t.getPoint(aid));
			int b2id = findpoint(t2, t.getPoint(bid));
			int c2id = findpoint(t2, t.getPoint(cid));

			CVertex a = t.getPoint(aid);
			CVertex b = t.getPoint(bid);
			CVertex c = t.getPoint(cid);

			CTetrahedron pabd = new CTetrahedron(p, a, b, d);
			CTetrahedron pacd = new CTetrahedron(p, a, c, d);
			CTetrahedron pbcd = new CTetrahedron(p, b, c, d);

			if (p.isDegenerate()) {
				if (p.getDegCase() == DegenerateCase.ONEDGE) {
					// TODO: There is some code missing from the C++ here
				}
			}

			pabd.setNeighbour(0, t2.getNeighbour(c2id));
			pabd.setNeighbour(1, pbcd);
			pabd.setNeighbour(2, pacd);
			pabd.setNeighbour(3, t.getNeighbour(cid));

			pacd.setNeighbour(0, t2.getNeighbour(b2id));
			pacd.setNeighbour(1, pbcd);
			pacd.setNeighbour(2, pabd);
			pacd.setNeighbour(3, t.getNeighbour(bid));

			pbcd.setNeighbour(0, t2.getNeighbour(a2id));
			pbcd.setNeighbour(1, pacd);
			pbcd.setNeighbour(2, pabd);
			pbcd.setNeighbour(3, t.getNeighbour(aid));

			if (t.getNeighbour(aid) != null)
				(t.getNeighbour(aid)).updateNeighbour(t, pbcd);
			if (t2.getNeighbour(a2id) != null)
				(t2.getNeighbour(a2id)).updateNeighbour(t2, pbcd);
			if (t.getNeighbour(bid) != null)
				(t.getNeighbour(bid)).updateNeighbour(t, pacd);
			if (t2.getNeighbour(b2id) != null)
				(t2.getNeighbour(b2id)).updateNeighbour(t2, pacd);
			if (t.getNeighbour(cid) != null)
				(t.getNeighbour(cid)).updateNeighbour(t, pabd);
			if (t2.getNeighbour(c2id) != null)
				(t2.getNeighbour(c2id)).updateNeighbour(t2, pabd);

			flips.addFlip(0, pabd);
			flips.addFlip(0, pacd);
			flips.addFlip(0, pbcd);

			flips.addTetrahedron(pabd);
			flips.addTetrahedron(pacd);
			flips.addTetrahedron(pbcd);

			t.setModified(true);
			t2.setModified(true);

			return pabd;
		}

		public int findpoint(CTetrahedron t, CVertex p) {
			for (int i = 0; i < 4; i++) {
				if (t.getPoint(i) == p) {
					return i;
				}
			}
			System.out.println("Problemer med findpoint\n");
			// never happends:
			return -1;
		}

	}

	static class Flip32 {

		private final Flips flips;
		private final Flip23 f23;

		public Flip32(Flip23 f23, Flips flips) {
			this.f23 = f23;
			this.flips = flips;

		}

		public CTetrahedron flip32(CTetrahedron t1, CTetrahedron t2,
				CTetrahedron t3, int pid, int did) {
			int i;

			int c1 = -1;

			int a1 = f23.getA1();
			int b1 = f23.getB1();

			for (i = 0; i < 4; i++) {
				if (i != a1 && i != pid && i != b1) {
					c1 = i;
					break;
				}
			}

			if (c1 == -1) {
				System.out.println("Problemer med c1");
			}

			CTetrahedron pacd = new CTetrahedron(t1.getPoint(pid),
					t1.getPoint(a1), t1.getPoint(c1), t2.getPoint(did));
			CTetrahedron pbcd = new CTetrahedron(t1.getPoint(pid),
					t1.getPoint(b1), t1.getPoint(c1), t2.getPoint(did));

			int a2 = t2.findpoint(t1.getPoint(a1));
			int a3 = t3.findpoint(t1.getPoint(a1));

			int b2 = t2.findpoint(t1.getPoint(b1));
			int b3 = t3.findpoint(t1.getPoint(b1));

			pacd.setNeighbour(0, t2.getNeighbour(b2));
			pacd.setNeighbour(1, pbcd);
			pacd.setNeighbour(2, t3.getNeighbour(b3));
			pacd.setNeighbour(3, t1.getNeighbour(b1));

			pbcd.setNeighbour(0, t2.getNeighbour(a2));
			pbcd.setNeighbour(1, pacd);
			pbcd.setNeighbour(2, t3.getNeighbour(a3));
			pbcd.setNeighbour(3, t1.getNeighbour(a1));

			if (t1.getNeighbour(a1) != null)
				t1.getNeighbour(a1).updateNeighbour(t1, pbcd);
			if (t1.getNeighbour(b1) != null)
				t1.getNeighbour(b1).updateNeighbour(t1, pacd);
			if (t2.getNeighbour(a2) != null)
				t2.getNeighbour(a2).updateNeighbour(t2, pbcd);
			if (t2.getNeighbour(b2) != null)
				t2.getNeighbour(b2).updateNeighbour(t2, pacd);
			if (t3.getNeighbour(a3) != null)
				t3.getNeighbour(a3).updateNeighbour(t3, pbcd);
			if (t3.getNeighbour(b3) != null)
				t3.getNeighbour(b3).updateNeighbour(t3, pacd);

			flips.addFlip(0, pacd);
			flips.addFlip(0, pbcd);

			flips.addTetrahedron(pacd);
			flips.addTetrahedron(pbcd);

			t1.setModified(true);
			t2.setModified(true);
			t3.setModified(true);

			return pacd;

		}

	}

	static class Flip44 {

		private final Flips flips;
		private final Flip23 f23;

		public Flip44(Flip23 f23, Flips flips) {
			super();
			this.flips = flips;
			this.f23 = f23;
		}

		public void flip44(CTetrahedron t1, CTetrahedron t2, CTetrahedron t3,
				CTetrahedron t4, CVertex p, CVertex d) {

			int a1 = f23.getA1();
			int b1 = f23.getB1();
			int cid = f23.getCid();

			CVertex a = t1.getPoint(a1);
			CVertex b = t1.getPoint(b1);
			CVertex c = t1.getPoint(cid);

			int eid = findlastid(d, a, b, t3);
			CVertex e = t3.getPoint(eid);

			CTetrahedron dacp = new CTetrahedron(d, a, c, p);
			CTetrahedron dbcp = new CTetrahedron(d, b, c, p);
			CTetrahedron dbep = new CTetrahedron(d, b, e, p);
			CTetrahedron daep = new CTetrahedron(d, a, e, p);

			dacp.setNeighbour(0, t1.getNeighbour(b1));
			dacp.setNeighbour(1, dbcp);
			dacp.setNeighbour(2, daep);
			dacp.setNeighbour(3, t2.getNeighbour(t2.findpoint(b)));

			dbcp.setNeighbour(0, t1.getNeighbour(a1));
			dbcp.setNeighbour(1, dacp);
			dbcp.setNeighbour(2, dbep);
			dbcp.setNeighbour(3, t2.getNeighbour(t2.findpoint(a)));

			dbep.setNeighbour(0, t4.getNeighbour(t4.findpoint(a)));
			dbep.setNeighbour(1, daep);
			dbep.setNeighbour(2, dbcp);
			dbep.setNeighbour(3, t3.getNeighbour(t3.findpoint(a)));

			daep.setNeighbour(0, t4.getNeighbour(t4.findpoint(b)));
			daep.setNeighbour(1, dbep);
			daep.setNeighbour(2, dacp);
			daep.setNeighbour(3, t3.getNeighbour(t3.findpoint(b)));

			if (t1.getNeighbour(a1) != null)
				t1.getNeighbour(a1).updateNeighbour(t1, dbcp);
			if (t1.getNeighbour(b1) != null)
				t1.getNeighbour(b1).updateNeighbour(t1, dacp);
			if (t2.getNeighbour(t2.findpoint(a)) != null)
				t2.getNeighbour(t2.findpoint(a)).updateNeighbour(t2, dbcp);
			if (t2.getNeighbour(t2.findpoint(b)) != null)
				t2.getNeighbour(t2.findpoint(b)).updateNeighbour(t2, dacp);
			if (t3.getNeighbour(t3.findpoint(a)) != null)
				t3.getNeighbour(t3.findpoint(a)).updateNeighbour(t3, dbep);
			if (t3.getNeighbour(t3.findpoint(b)) != null)
				t3.getNeighbour(t3.findpoint(b)).updateNeighbour(t3, daep);
			if (t4.getNeighbour(t4.findpoint(a)) != null)
				t4.getNeighbour(t4.findpoint(a)).updateNeighbour(t4, dbep);
			if (t4.getNeighbour(t4.findpoint(b)) != null)
				t4.getNeighbour(t4.findpoint(b)).updateNeighbour(t4, daep);

			t1.setModified(true);
			t2.setModified(true);
			t3.setModified(true);
			t4.setModified(true);

			flips.addFlip(3, dacp);
			flips.addFlip(3, dbcp);
			flips.addFlip(3, dbep);
			flips.addFlip(3, daep);

			flips.addTetrahedron(dacp);
			flips.addTetrahedron(dbcp);
			flips.addTetrahedron(dbep);
			flips.addTetrahedron(daep);

		}

		private int findlastid(CVertex q1, CVertex q2, CVertex q3,
				CTetrahedron t) {
			CVertex tmp;
			for (int i = 0; i < 4; i++) {
				tmp = t.getPoint(i);
				if (tmp != q1 && tmp != q2 && tmp != q3) {
					return i;
				}
			}
			System.out.println("Failure is not an option");
			return -1;
		}
	}

	ExactJavaPredicates primitives;

	private Stack<CTetrahedron> tetrahedrastack;
	private final Stack<Flip> flipstack;
	private final Flip23 f23;
	private final Flip32 f32;
	private final Flip44 f44;

	public Flips(ExactJavaPredicates primitives) {
		this.primitives = primitives;
		tetrahedrastack = new Stack<CTetrahedron>();
		flipstack = new Stack<Flip>();
		f23 = new Flip23(this);
		f32 = new Flip32(f23, this);
		f44 = new Flip44(f23, this);
	}

	public CTetrahedron fixDelaunay() {
		CTetrahedron next_t = null;

		while (!flipstack.empty()) {

			Flip f = flipstack.pop();
			CTetrahedron t = f.getT();
			int pid = f.getId();
			CVertex p = t.getPoint(pid);
			CTetrahedron t2 = t.getNeighbour(pid);

			if (!t.isModified()) {

				if (t2 != null && t.isFlat()) {

					if (p.getDegCase() == DegenerateCase.ONFACE) {
						int did = apex(t, t2);
						next_t = f23.flip23(t, pid, did);
					} else if (p.getDegCase() == DegenerateCase.ONEDGE) {

					}
				}

				else if (t2 != null) {
					int did = apex(t, t2);
					CVertex d = t2.getPoint(did);

					if (primitives.insphere(t, d) == SphereConfig.INSIDE) {
						ApexConfig flipcase = apexConfig(t, p, pid, t2, d);

						if (flipcase == ApexConfig.CONVEX) {

							next_t = f23.flip23(t, pid, did);
						} else if (flipcase == ApexConfig.CONCAVE) {

							if (f23.getT3() != null) {

								next_t = f32.flip32(t, t2, f23.getT3(), pid,
										did);

								f23.setT3(null);

							}
						} else if (flipcase == ApexConfig.COPLANAR) {

							if (config44(t, t2, p, d)) {
								// HMM?
							} else {

							}

						}

					}

				}
			}
		}
		return next_t;
	}

	private boolean config44(CTetrahedron t1, CTetrahedron t2, CVertex p,
			CVertex d) {
		int cid = f23.getCid();

		CVertex c = t1.getPoint(cid);
		int c2id = t2.findpoint(c);

		CTetrahedron t3 = t2.getNeighbour(c2id);
		CTetrahedron t4 = t1.getNeighbour(cid);

		if (t3 != null && t4 != null) {
			int d3id = t3.findpoint(d);

			if (t3.getNeighbour(d3id) == t4) {
				f44.flip44(t1, t2, t3, t4, p, d);
				return true;
			}
		}
		return false;
	}

	public void addFlip(int id, CTetrahedron t) {
		flipstack.push(new Flip(id, t));
	}

	public void addTetrahedron(CTetrahedron t) {
		tetrahedrastack.push(t);
	}

	public int apex(CTetrahedron t1, CTetrahedron t2) {
		for (int i = 0; i < 4; i++) {
			if (t2.getNeighbour(i) == t1) {
				return i;
			}
		}
		System.out.println("Problemer med apex\n");
		return -1;
	}

	public ApexConfig apexConfig(CTetrahedron t1, CVertex p, int pid,
			CTetrahedron t2, CVertex d) {
		boolean concave = false;
		PlaneConfig case1, case2, case3;

		if ((case1 = primitives.diffsides(p, t1.getPoint((pid + 1) % 4),
				t1.getPoint((pid + 2) % 4), t1.getPoint((pid + 3) % 4), d)) == PlaneConfig.DIFF) {
			f23.setT3(findthird(t1, t2, (pid + 3) % 4));
			f23.setA1((pid + 1) % 4);
			f23.setB1((pid + 2) % 4);
			concave = true;
			if (f23.getT3() != null)
				return ApexConfig.CONCAVE;
		}
		if ((case2 = primitives.diffsides(p, t1.getPoint((pid + 1) % 4),
				t1.getPoint((pid + 3) % 4), t1.getPoint((pid + 2) % 4), d)) == PlaneConfig.DIFF) {
			f23.setT3(findthird(t1, t2, (pid + 2) % 4));
			f23.setA1((pid + 1) % 4);
			f23.setB1((pid + 3) % 4);
			concave = true;
			if (f23.getT3() != null)
				return ApexConfig.CONCAVE;
		}

		if ((case3 = primitives.diffsides(p, t1.getPoint((pid + 2) % 4),
				t1.getPoint((pid + 3) % 4), t1.getPoint((pid + 1) % 4), d)) == PlaneConfig.DIFF) {
			f23.setT3(findthird(t1, t2, (pid + 1) % 4));
			f23.setA1((pid + 2) % 4);
			f23.setB1((pid + 3) % 4);
			concave = true;
			if (f23.getT3() != null)
				return ApexConfig.CONCAVE;
		}
		if (concave)
			return ApexConfig.CONCAVE;
		if (case1 == PlaneConfig.COPLANAR) {
			f23.setCid((pid + 3) % 4);
			f23.setA1((pid + 1) % 4);
			f23.setB1((pid + 2) % 4);

			return ApexConfig.COPLANAR;
		}
		if (case2 == PlaneConfig.COPLANAR) {
			f23.setCid((pid + 2) % 4);
			f23.setA1((pid + 1) % 4);
			f23.setB1((pid + 3) % 4);

			return ApexConfig.COPLANAR;
		}
		if (case3 == PlaneConfig.COPLANAR) {
			f23.setCid((pid + 1) % 4);
			f23.setA1((pid + 2) % 4);
			f23.setB1((pid + 3) % 4);
			return ApexConfig.COPLANAR;
		} else
			return ApexConfig.CONVEX;
	}

	public CTetrahedron findthird(CTetrahedron t1, CTetrahedron t2, int c1) {
		int c2 = findpoint(t2, t1.getPoint(c1));

		if (t1.getNeighbour(c1) == t2.getNeighbour(c2)) {
			return t1.getNeighbour(c1);
		}

		return null;
	}

	public int findpoint(CTetrahedron t, CVertex p) {
		for (int i = 0; i < 4; i++) {
			if (t.getPoint(i) == p) {
				return i;
			}
		}
		return -1;
	}

	public Stack<CTetrahedron> getTetrahedrastack() {
		return tetrahedrastack;
	}

	public Flip23 getFlip23() {
		return f23;
	}

	public Flip32 getFlip32() {
		return f32;
	}

	public Stack<Flip> getFlipstack() {
		return flipstack;
	}

	public void setTetrahedrastack(Stack<CTetrahedron> tetrahedrastack) {
		this.tetrahedrastack = tetrahedrastack;
	}

}
