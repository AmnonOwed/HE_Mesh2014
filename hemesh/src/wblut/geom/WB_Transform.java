package wblut.geom;

import wblut.WB_Epsilon;
import wblut.math.WB_M33;
import wblut.math.WB_M44;

public class WB_Transform {

	private double _xt, _yt, _zt;
	static WB_GeometryFactory geometryfactory = WB_GeometryFactory.instance();
	private double x, y, z;
	private int id;
	private boolean purerot;

	/** Transform matrix. */
	private WB_M44 T;

	/** Inverse transform matrix. */
	private WB_M44 invT;

	/**
	 * Instantiates a new WB_Transfrom.
	 */
	public WB_Transform() {
		T = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		invT = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	/**
	 * Add translation to transform.
	 * 
	 * @param v
	 *            vector
	 * @return self
	 */
	public WB_Transform addTranslate(final WB_Coordinate v) {
		T = new WB_M44(1, 0, 0, v.xd(), 0, 1, 0, v.yd(), 0, 0, 1, v.zd(), 0, 0,
				0, 1).mult(T);
		invT = invT.mult(new WB_M44(1, 0, 0, -v.xd(), 0, 1, 0, -v.yd(), 0, 0,
				1, -v.zd(), 0, 0, 0, 1));
		return this;
	}

	public WB_Transform addTranslate(double f, final WB_Coordinate v) {
		T = new WB_M44(1, 0, 0, f * v.xd(), 0, 1, 0, f * v.yd(), 0, 0, 1, f
				* v.zd(), 0, 0, 0, 1).mult(T);
		invT = invT.mult(new WB_M44(1, 0, 0, -f * v.xd(), 0, 1, 0, -f * v.yd(),
				0, 0, 1, -f * v.zd(), 0, 0, 0, 1));
		return this;
	}

	/**
	 * Add non-uniform scale to transform.
	 * 
	 * @param s
	 *            scaling vector
	 * @return self
	 */
	public WB_Transform addScale(final WB_Coordinate s) {
		T = new WB_M44(s.xd(), 0, 0, 0, 0, s.yd(), 0, 0, 0, 0, s.zd(), 0, 0, 0,
				0, 1).mult(T);
		invT = invT.mult(new WB_M44(1.0 / s.xd(), 0, 0, 0, 0, 1.0 / s.yd(), 0,
				0, 0, 0, 1.0 / s.zd(), 0, 0, 0, 0, 1));
		return this;
	}

	/**
	 * Add non-uniform scale to transform.
	 * 
	 * @param sx
	 *            scaling vector
	 * @param sy
	 *            scaling vector
	 * @param sz
	 *            scaling vector
	 * @return self
	 */
	public WB_Transform addScale(final double sx, final double sy,
			final double sz) {
		T = new WB_M44(sx, 0, 0, 0, 0, sy, 0, 0, 0, 0, sz, 0, 0, 0, 0, 1)
				.mult(T);
		invT = invT.mult(new WB_M44(1.0 / sx, 0, 0, 0, 0, 1.0 / sy, 0, 0, 0, 0,
				1.0 / sz, 0, 0, 0, 0, 1));
		return this;
	}

	/**
	 * Add uniform scale to transform.
	 * 
	 * @param s
	 *            scaling point
	 * @return self
	 */
	public WB_Transform addScale(final double s) {
		T = new WB_M44(s, 0, 0, 0, 0, s, 0, 0, 0, 0, s, 0, 0, 0, 0, 1).mult(T);
		invT = invT.mult(new WB_M44(1 / s, 0, 0, 0, 0, 1 / s, 0, 0, 0, 0,
				1 / s, 0, 0, 0, 0, 1));
		return this;
	}

	/**
	 * Add rotation about X-axis.
	 * 
	 * @param angle
	 *            angle in radians
	 * @return self
	 */
	public WB_Transform addRotateX(final double angle) {
		final double s = Math.sin(angle);
		final double c = Math.cos(angle);
		final WB_M44 tmp = new WB_M44(1, 0, 0, 0, 0, c, -s, 0, 0, s, c, 0, 0,
				0, 0, 1);
		T = tmp.mult(T);
		invT = invT.mult(tmp.getTranspose());
		return this;
	}

	/**
	 * Add rotation about Y-axis.
	 * 
	 * @param angle
	 *            angle in radians
	 * @return self
	 */
	public WB_Transform addRotateY(final double angle) {
		final double s = Math.sin(angle);
		final double c = Math.cos(angle);
		final WB_M44 tmp = new WB_M44(c, 0, s, 0, 0, 1, 0, 0, -s, 0, c, 0, 0,
				0, 0, 1);
		T = tmp.mult(T);
		invT = invT.mult(tmp.getTranspose());
		return this;
	}

	/**
	 * Add rotation about Z-axis.
	 * 
	 * @param angle
	 *            angle in radians
	 * @return self
	 */
	public WB_Transform addRotateZ(final double angle) {
		final double s = Math.sin(angle);
		final double c = Math.cos(angle);
		final WB_M44 tmp = new WB_M44(c, -s, 0, 0, s, c, 0, 0, 0, 0, 1, 0, 0,
				0, 0, 1);
		T = tmp.mult(T);
		invT = invT.mult(tmp.getTranspose());
		return this;
	}

	/**
	 * Add rotation about arbitrary axis in origin.
	 * 
	 * @param angle
	 *            angle in radians
	 * @param axis
	 *            WB_Vector
	 * @return self
	 */
	public WB_Transform addRotate(final double angle, final WB_Coordinate axis) {
		final WB_Vector a = new WB_Vector(axis);
		a._normalizeSelf();
		final double s = Math.sin(angle);
		final double c = Math.cos(angle);
		final WB_M44 tmp = new WB_M44(a.xd() * a.xd() + (1.f - a.xd() * a.xd())
				* c, a.xd() * a.yd() * (1.f - c) - a.zd() * s, a.xd() * a.zd()
				* (1.f - c) + a.yd() * s, 0,

		a.xd() * a.yd() * (1.f - c) + a.zd() * s, a.yd() * a.yd()
				+ (1.f - a.yd() * a.yd()) * c, a.yd() * a.zd() * (1.f - c)
				- a.xd() * s, 0,

		a.xd() * a.zd() * (1.f - c) - a.yd() * s, a.yd() * a.zd() * (1.f - c)
				+ a.xd() * s, a.zd() * a.zd() + (1.f - a.zd() * a.zd()) * c, 0,

		0, 0, 0, 1);
		T = tmp.mult(T);
		invT = invT.mult(tmp.getTranspose());
		return this;
	}

	/**
	 * Add rotation about arbitrary axis in point.
	 * 
	 * @param angle
	 *            angle in radians
	 * @param p
	 *            point
	 * @param axis
	 *            WB_Vector
	 * @return self
	 */
	public WB_Transform addRotateAboutAxis(final double angle,
			final WB_Coordinate p, final WB_Coordinate axis) {
		addTranslate(-1, p);
		addRotate(angle, axis);
		addTranslate(p);
		return this;
	}

	/**
	 * Add a object-to-world transform.
	 * 
	 * @param origin
	 *            object origin in world coordinates
	 * @param up
	 *            object up direction in world coordinates
	 * @param front
	 *            object front direction in world coordinates
	 * @return self
	 */
	public WB_Transform addObjectToWorld(final WB_Coordinate origin,
			final WB_Coordinate up, final WB_Coordinate front) {
		final WB_Vector dir = new WB_Vector(origin, front);
		dir._normalizeSelf();
		final WB_Vector tup = new WB_Vector(origin, up);
		tup._normalizeSelf();
		final WB_Vector right = dir.cross(tup);
		final WB_Vector newUp = right.cross(dir);

		final WB_M44 tmp = new WB_M44(right.xd(), dir.xd(), newUp.xd(),
				origin.xd(), right.yd(), dir.yd(), newUp.yd(), origin.yd(),
				right.zd(), dir.zd(), newUp.zd(), origin.zd(), 0, 0, 0, 1);
		T = tmp.mult(T);
		invT = invT.mult(tmp.inverse());
		return this;
	}

	/**
	 * Adds the reflect x.
	 * 
	 * @return the w b_ transform
	 */
	public WB_Transform addReflectX() {
		addScale(-1, 1, 1);
		return this;
	}

	/**
	 * Adds the reflect y.
	 * 
	 * @return the w b_ transform
	 */
	public WB_Transform addReflectY() {
		addScale(1, -1, 1);
		return this;
	}

	/**
	 * Adds the reflect z.
	 * 
	 * @return the w b_ transform
	 */
	public WB_Transform addReflectZ() {
		addScale(1, 1, -1);
		return this;
	}

	/**
	 * Adds the invert.
	 * 
	 * @return the w b_ transform
	 */
	public WB_Transform addInvert() {
		addScale(-1, -1, -1);
		return this;
	}

	/**
	 * Adds the reflect x.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ transform
	 */
	public WB_Transform addReflectX(final WB_Coordinate p) {
		addTranslate(-1, p);
		addScale(-1, 1, 1);
		addTranslate(p);
		return this;
	}

	/**
	 * Adds the reflect y.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ transform
	 */
	public WB_Transform addReflectY(final WB_Coordinate p) {
		addTranslate(-1, p);
		addScale(1, -1, 1);
		addTranslate(p);
		return this;
	}

	/**
	 * Adds the reflect z.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ transform
	 */
	public WB_Transform addReflectZ(final WB_Coordinate p) {
		addTranslate(-1, p);
		addScale(1, 1, -1);
		addTranslate(p);
		return this;
	}

	/**
	 * Adds the invert.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ transform
	 */
	public WB_Transform addInvert(final WB_Coordinate p) {
		addTranslate(-1, p);
		addScale(-1, -1, -1);
		addTranslate(p);
		return this;
	}

	/**
	 * Adds the reflect.
	 * 
	 * @param P
	 *            the p
	 * @return the w b_ transform
	 */
	public WB_Transform addReflect(final WB_Plane P) {
		final WB_M33 tmp = P.getNormal().tensor(P.getNormal());
		final double Qn = P.getOrigin().dot(P.getNormal());
		final WB_M44 Tr = new WB_M44(1 - 2 * tmp.m11, -2 * tmp.m12, -2
				* tmp.m13, 0, -2 * tmp.m21, 1 - 2 * tmp.m22, -2 * tmp.m23, 0,
				-2 * tmp.m31, -2 * tmp.m32, 1 - 2 * tmp.m33, 0, 2 * Qn
						* P.getNormal().xd(), 2 * Qn * P.getNormal().yd(), 2
						* Qn * P.getNormal().zd(), 1);

		T = Tr.mult(T);
		invT = invT.mult(Tr);
		return this;

	}

	/**
	 * Adds the shear.
	 * 
	 * @param P
	 *            the p
	 * @param v
	 *            the v
	 * @param angle
	 *            the angle
	 * @return the w b_ transform
	 */
	public WB_Transform addShear(final WB_Plane P, final WB_Coordinate v,
			final double angle) {
		final WB_Vector lv = new WB_Vector(v);
		lv._normalizeSelf();
		double tana = Math.tan(angle);
		final WB_M33 tmp = P.getNormal().tensor(lv);
		final double Qn = P.getOrigin().dot(P.getNormal());
		WB_M44 Tr = new WB_M44(1 + tana * tmp.m11, tana * tmp.m12, tana
				* tmp.m13, 0, tana * tmp.m21, 1 + tana * tmp.m22, tana
				* tmp.m23, 0, tana * tmp.m31, tana * tmp.m32, 1 + tana
				* tmp.m33, 0, -Qn * lv.xd(), -Qn * lv.yd(), -Qn * lv.zd(), 1);

		T = Tr.mult(T);
		tana *= -1;
		Tr = new WB_M44(1 + tana * tmp.m11, tana * tmp.m12, tana * tmp.m13, 0,
				tana * tmp.m21, 1 + tana * tmp.m22, tana * tmp.m23, 0, tana
						* tmp.m31, tana * tmp.m32, 1 + tana * tmp.m33, 0, -Qn
						* lv.xd(), -Qn * lv.yd(), -Qn * lv.zd(), 1);
		invT = invT.mult(Tr);
		return this;

	}

	/**
	 * Apply transform to line.
	 * 
	 * @param L
	 *            line
	 * @return new WB_line
	 */
	public WB_Line apply(final WB_Line L) {
		return new WB_Line(applyAsPoint(L.getOrigin()),
				applyAsNormal(L.getDirection()));
	}

	/**
	 * Apply transform to line.
	 * 
	 * @param L
	 *            line
	 * @param result
	 *            WB_Line to store result
	 */
	public void applyInto(final WB_Line L, final WB_Line result) {
		result.set(applyAsPoint(L.getOrigin()), applyAsNormal(L.getDirection()));
	}

	/**
	 * Apply transform to line.
	 * 
	 * @param L
	 *            line
	 */
	public void applySelf(final WB_Line L) {
		L.set(applyAsPoint(L.getOrigin()), applyAsNormal(L.getDirection()));
	}

	/**
	 * Apply transform to plane.
	 * 
	 * @param P
	 *            plane
	 * @return new WB_Plane
	 */
	public WB_Plane apply(final WB_Plane P) {
		return new WB_Plane(applyAsPoint(P.getOrigin()),
				applyAsNormal(P.getNormal()));
	}

	/**
	 * Apply transform to plane.
	 * 
	 * @param P
	 *            plane
	 * @param result
	 *            plane to store result
	 */
	public void applyInto(final WB_Plane P, final WB_Plane result) {
		result.set(applyAsPoint(P.getOrigin()), applyAsNormal(P.getNormal()));
	}

	/**
	 * Apply transform to plane.
	 * 
	 * @param P
	 *            plane
	 */
	public void applySelf(final WB_Plane P) {
		P.set(applyAsPoint(P.getOrigin()), applyAsNormal(P.getNormal()));
	}

	/**
	 * Apply transform to ray.
	 * 
	 * @param R
	 *            ray
	 * @return new WB_ray
	 */
	public WB_Ray apply(final WB_Ray R) {
		return new WB_Ray(applyAsPoint(R.getOrigin()),
				applyAsVector(R.getDirection()));
	}

	/**
	 * Apply transform to ray.
	 * 
	 * @param R
	 *            ray
	 * @param result
	 *            WB_Ray to store result
	 */
	public void applyInto(final WB_Ray R, final WB_Ray result) {
		result.set(applyAsPoint(R.getOrigin()), applyAsVector(R.getDirection()));
	}

	/**
	 * Apply transform to ray.
	 * 
	 * @param R
	 *            ray
	 */
	public void applySelf(final WB_Ray R) {
		R.set(applyAsPoint(R.getOrigin()), applyAsVector(R.getDirection()));
	}

	/**
	 * Apply transform to segment.
	 * 
	 * @param S
	 *            segment
	 * @return new WB_Segment
	 */
	public WB_Segment apply(final WB_Segment S) {
		return new WB_Segment(applyAsPoint(S.getOrigin()),
				applyAsPoint(S.getEndpoint()));
	}

	/**
	 * Apply transform to segment.
	 * 
	 * @param S
	 *            segment
	 * @param result
	 *            WB_Segment to store result
	 */
	public void applyInto(final WB_Segment S, final WB_Segment result) {
		result.set(applyAsPoint(S.getOrigin()), applyAsPoint(S.getEndpoint()));
	}

	/**
	 * Apply transform to segment.
	 * 
	 * @param S
	 *            segment
	 */
	public void applySelf(final WB_Segment S) {
		S.set(applyAsPoint(S.getOrigin()), applyAsPoint(S.getEndpoint()));
	}

	/**
	 * Invert transform.
	 */
	public void inverse() {
		WB_M44 tmp;
		tmp = T;
		T = invT;
		invT = tmp;
	}

	/**
	 * Clear transform.
	 */
	public void clear() {
		T = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		invT = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
	}

	/**
	 * Apply transform to point.
	 * 
	 * @param p
	 *            point
	 * @return new WB_XYZ
	 */
	public WB_Point applyAsPoint(final WB_Coordinate p) {
		final double xp = T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd()
				+ T.m14;
		final double yp = T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd()
				+ T.m24;
		final double zp = T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd()
				+ T.m34;
		double wp = T.m41 * p.xd() + T.m42 * p.yd() + T.m43 * p.zd() + T.m44;
		if (WB_Epsilon.isZero(wp)) {
			return new WB_Point(xp, yp, zp);
		}
		wp = 1.0 / wp;
		return new WB_Point(xp * wp, yp * wp, zp * wp);
	}

	/**
	 * Apply as point.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return the w b_ point3d
	 */
	public WB_Point applyAsPoint(final double x, final double y, final double z) {
		final double xp = T.m11 * x + T.m12 * y + T.m13 * z + T.m14;
		final double yp = T.m21 * x + T.m22 * y + T.m23 * z + T.m24;
		final double zp = T.m31 * x + T.m32 * y + T.m33 * z + T.m34;
		double wp = T.m41 * x + T.m42 * y + T.m43 * z + T.m44;
		if (WB_Epsilon.isZero(wp)) {
			return new WB_Point(xp, yp, zp);
		}
		wp = 1.0 / wp;
		return new WB_Point(xp * wp, yp * wp, zp * wp);
	}

	/**
	 * Apply transform to point.
	 * 
	 * @param p
	 *            point
	 */
	public void applySelfAsPoint(final WB_MutableCoordinate p) {
		final double x = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd() + T.m14);
		final double y = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd() + T.m24);
		final double z = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd() + T.m34);
		double wp = (T.m41 * p.xd() + T.m42 * p.yd() + T.m43 * p.zd() + T.m44);
		wp = 1.0 / wp;
		p._set(x * wp, y * wp, z * wp);
	}

	/**
	 * Apply transform to vector.
	 * 
	 * @param p
	 *            vector
	 * @return new WB_Vector
	 */
	public WB_Vector applyAsVector(final WB_Coordinate p) {
		final double xp = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd());
		final double yp = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd());
		final double zp = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd());
		return new WB_Vector(xp, yp, zp);
	}

	/**
	 * Apply as vector.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return the w b_ vector3d
	 */
	public WB_Vector applyAsVector(final double x, final double y,
			final double z) {
		final double xp = (T.m11 * x + T.m12 * y + T.m13 * z);
		final double yp = (T.m21 * x + T.m22 * y + T.m23 * z);
		final double zp = (T.m31 * x + T.m32 * y + T.m33 * z);
		return new WB_Vector(xp, yp, zp);
	}

	/**
	 * Apply transform to vector.
	 * 
	 * @param p
	 *            vector
	 */
	public void applySelfAsVector(final WB_MutableCoordinate p) {
		final double x = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd());
		final double y = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd());
		final double z = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd());
		p._set(x, y, z);
	}

	/**
	 * Apply as normal.
	 * 
	 * @param p
	 *            the p
	 * @return the w b_ vector3d
	 */
	public WB_Vector applyAsNormal(final WB_Coordinate p) {
		final double nx = (invT.m11 * p.xd() + invT.m21 * p.yd() + invT.m31
				* p.zd());
		final double ny = (invT.m12 * p.xd() + invT.m22 * p.yd() + invT.m32
				* p.zd());
		final double nz = (invT.m13 * p.xd() + invT.m23 * p.yd() + invT.m33
				* p.zd());
		return new WB_Vector(nx, ny, nz);
	}

	/**
	 * Apply as normal.
	 * 
	 * @param x
	 *            the x
	 * @param y
	 *            the y
	 * @param z
	 *            the z
	 * @return the w b_ vector3d
	 */
	public WB_Vector applyAsNormal(final double x, final double y,
			final double z) {
		final double nx = (invT.m11 * x + invT.m21 * y + invT.m31 * z);
		final double ny = (invT.m12 * x + invT.m22 * y + invT.m32 * z);
		final double nz = (invT.m13 * x + invT.m23 * y + invT.m33 * z);
		return new WB_Vector(nx, ny, nz);
	}

	/**
	 * Apply transform to normal.
	 * 
	 * @param n
	 *            normal
	 */
	public void applySelfAsNormal(final WB_MutableCoordinate n) {
		final double x = (invT.m11 * n.xd() + invT.m21 * n.yd() + invT.m31
				* n.zd());
		final double y = (invT.m12 * n.xd() + invT.m22 * n.yd() + invT.m32
				* n.zd());
		final double z = (invT.m13 * n.xd() + invT.m23 * n.yd() + invT.m33
				* n.zd());
		n._set(x, y, z);
	}

	/**
	 * Adds the from cs to cs.
	 * 
	 * @param CS1
	 *            the c s1
	 * @param CS2
	 *            the c s2
	 * @return the w b_ transform
	 */
	public WB_Transform addFromCSToCS(final WB_CoordinateSystem CS1,
			final WB_CoordinateSystem CS2) {
		addFromCSToWorld(CS1);
		addFromWorldToCS(CS2);
		return this;
	}

	/**
	 * Adds the from cs to world.
	 * 
	 * @param CS
	 *            the cs
	 * @return the w b_ transform
	 */
	public WB_Transform addFromCSToWorld(final WB_CoordinateSystem CS) {
		WB_CoordinateSystem current = CS;
		while (!current.isWorld()) {
			addFromCSToParent(current);
			current = current.getParent();
		}
		return this;
	}

	/**
	 * Adds the from world to cs.
	 * 
	 * @param CS
	 *            the cs
	 * @return the w b_ transform
	 */
	public WB_Transform addFromWorldToCS(final WB_CoordinateSystem CS) {
		WB_Transform tmp = new WB_Transform();
		tmp.addFromCSToWorld(CS);
		T = tmp.invT.mult(T);
		invT = invT.mult(tmp.T);
		return this;
	}

	/**
	 * Adds the from cs to parent.
	 * 
	 * @param CS
	 *            the cs
	 * @return the w b_ transform
	 */
	public WB_Transform addFromCSToParent(final WB_CoordinateSystem CS) {
		WB_CoordinateSystem WCS = WB_CoordinateSystem.WORLD();
		if (CS.isWorld()) {
			return this;
		}
		final WB_Vector ex1 = CS.getX(), ey1 = CS.getY(), ez1 = CS.getZ();
		final WB_Point o1 = CS.getOrigin();
		final WB_Vector ex2 = WCS.getX(), ey2 = WCS.getY(), ez2 = WCS.getZ();
		final WB_Point o2 = WCS.getOrigin();
		final double xx = ex2.dot(ex1);
		final double xy = ex2.dot(ey1);
		final double xz = ex2.dot(ez1);
		final double yx = ey2.dot(ex1);
		final double yy = ey2.dot(ey1);
		final double yz = ey2.dot(ez1);
		final double zx = ez2.dot(ex1);
		final double zy = ez2.dot(ey1);
		final double zz = ez2.dot(ez1);
		final WB_M44 tmp = new WB_M44(xx, xy, xz, 0, yx, yy, yz, 0, zx, zy, zz,
				0, 0, 0, 0, 1);
		final WB_M44 invtmp = new WB_M44(xx, yx, zx, 0, xy, yy, zy, 0, xz, yz,
				zz, 0, 0, 0, 0, 1);
		T = tmp.mult(T);
		invT = invT.mult(invtmp);
		addTranslate(o1._subSelf(o2));
		return this;
	}

	/**
	 * Adds the from parent to cs.
	 * 
	 * @param CS
	 *            the cs
	 * @return the w b_ transform
	 */
	public WB_Transform addFromParentToCS(final WB_CoordinateSystem CS) {
		if (CS.isWorld()) {
			return this;
		}
		WB_Transform tmp = new WB_Transform();
		tmp.addFromCSToParent(CS);
		T = tmp.invT.mult(T);
		invT = invT.mult(tmp.T);
		return this;
	}

	public void applyAsPoint(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		_xt = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd() + T.m14);
		_yt = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd() + T.m24);
		_zt = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd() + T.m34);
		double wp = (T.m41 * p.xd() + T.m42 * p.yd() + T.m43 * p.zd() + T.m44);
		wp = 1.0 / wp;
		result._set(_xt * wp, _yt * wp, _zt * wp);
	}

	public void applyAsPoint(final WB_Coordinate p,
			final WB_CoordinateSequence result, final int i) {
		_xt = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd() + T.m14);
		_yt = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd() + T.m24);
		_zt = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd() + T.m34);
		double wp = (T.m41 * p.xd() + T.m42 * p.yd() + T.m43 * p.zd() + T.m44);
		wp = 1.0 / wp;
		result._setI(i, _xt * wp, _yt * wp, _zt * wp);
	}

	public void applyAsPoint(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z + T.m14);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z + T.m24);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z + T.m34);
		double wp = (T.m41 * x + T.m42 * y + T.m43 * z + T.m44);
		wp = 1.0 / wp;
		result._set(_xt * wp, _yt * wp, _zt * wp);
	}

	public void applyAsPoint(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z + T.m14);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z + T.m24);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z + T.m34);
		double wp = (T.m41 * x + T.m42 * y + T.m43 * z + T.m44);
		wp = 1.0 / wp;
		result._setI(i, _xt * wp, _yt * wp, _zt * wp);
	}

	public void applyAsPoint(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z + T.m14);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z + T.m24);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z + T.m34);
		double wp = (T.m41 * x + T.m42 * y + T.m43 * z + T.m44);
		wp = 1.0 / wp;
		result._set(_xt * wp, _yt * wp, _zt * wp);
	}

	public void applyAsPoint(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z + T.m14);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z + T.m24);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z + T.m34);
		double wp = (T.m41 * x + T.m42 * y + T.m43 * z + T.m44);
		wp = 1.0 / wp;
		result._setI(j, _xt * wp, _yt * wp, _zt * wp);
	}

	public void applyAsVector(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		_xt = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd());
		_yt = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd());
		_zt = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd());
		result._set(_xt, _yt, _zt);
	}

	public void applyAsVector(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyAsVector(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyAsVector(final WB_Coordinate p,
			final WB_CoordinateSequence result, final int i) {
		_xt = (T.m11 * p.xd() + T.m12 * p.yd() + T.m13 * p.zd());
		_yt = (T.m21 * p.xd() + T.m22 * p.yd() + T.m23 * p.zd());
		_zt = (T.m31 * p.xd() + T.m32 * p.yd() + T.m33 * p.zd());
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyAsVector(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z);
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyAsVector(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (T.m11 * x + T.m12 * y + T.m13 * z);
		_yt = (T.m21 * x + T.m22 * y + T.m23 * z);
		_zt = (T.m31 * x + T.m32 * y + T.m33 * z);
		result._setI(j, _xt, _yt, _zt);
	}

	public void applyAsNormal(final WB_Coordinate n,
			final WB_MutableCoordinate result) {
		_xt = (invT.m11 * n.xd() + invT.m21 * n.yd() + invT.m31 * n.zd());
		_yt = (invT.m12 * n.xd() + invT.m22 * n.yd() + invT.m32 * n.zd());
		_zt = (invT.m13 * n.xd() + invT.m23 * n.yd() + invT.m33 * n.zd());
		result._set(_xt, _yt, _zt);
	}

	public void applyAsNormal(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		_xt = (invT.m11 * x + invT.m21 * y + invT.m31 * z);
		_yt = (invT.m12 * x + invT.m22 * y + invT.m32 * z);
		_zt = (invT.m13 * x + invT.m23 * y + invT.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyAsNormal(final WB_CoordinateSequence source, final int i,
			final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (invT.m11 * x + invT.m21 * y + invT.m31 * z);
		_yt = (invT.m12 * x + invT.m22 * y + invT.m32 * z);
		_zt = (invT.m13 * x + invT.m23 * y + invT.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyAsNormal(final WB_Coordinate n,
			final WB_CoordinateSequence result, final int i) {
		_xt = (invT.m11 * n.xd() + invT.m21 * n.yd() + invT.m31 * n.zd());
		_yt = (invT.m12 * n.xd() + invT.m22 * n.yd() + invT.m32 * n.zd());
		_zt = (invT.m13 * n.xd() + invT.m23 * n.yd() + invT.m33 * n.zd());
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyAsNormal(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		_xt = (invT.m11 * x + invT.m21 * y + invT.m31 * z);
		_yt = (invT.m12 * x + invT.m22 * y + invT.m32 * z);
		_zt = (invT.m13 * x + invT.m23 * y + invT.m33 * z);
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyAsNormal(final WB_CoordinateSequence source, final int i,
			final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (invT.m11 * x + invT.m21 * y + invT.m31 * z);
		_yt = (invT.m12 * x + invT.m22 * y + invT.m32 * z);
		_zt = (invT.m13 * x + invT.m23 * y + invT.m33 * z);
		result._setI(j, _xt, _yt, _zt);
	}

	public void applyInvAsPoint(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		_xt = (invT.m11 * p.xd() + invT.m12 * p.yd() + invT.m13 * p.zd() + invT.m14);
		_yt = (invT.m21 * p.xd() + invT.m22 * p.yd() + invT.m23 * p.zd() + invT.m24);
		_zt = (invT.m31 * p.xd() + invT.m32 * p.yd() + invT.m33 * p.zd() + invT.m34);
		double wp = (invT.m41 * p.xd() + invT.m42 * p.yd() + invT.m43 * p.zd() + invT.m44);
		wp = 1.0 / wp;
		result._set(_xt * wp, _yt * wp, _zt * wp);
	}

	public void applyInvAsPoint(final WB_Coordinate p,
			final WB_CoordinateSequence result, final int i) {
		_xt = (invT.m11 * p.xd() + invT.m12 * p.yd() + invT.m13 * p.zd() + invT.m14);
		_yt = (invT.m21 * p.xd() + invT.m22 * p.yd() + invT.m23 * p.zd() + invT.m24);
		_zt = (invT.m31 * p.xd() + invT.m32 * p.yd() + invT.m33 * p.zd() + invT.m34);
		double wp = (invT.m41 * p.xd() + invT.m42 * p.yd() + invT.m43 * p.zd() + invT.m44);
		wp = 1.0 / wp;
		result._setI(i, _xt * wp, _yt * wp, _zt * wp);
	}

	public void applyInvAsPoint(final double x, final double y, final double z,
			final WB_MutableCoordinate result) {
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z + invT.m14);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z + invT.m24);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z + invT.m34);
		double wp = (invT.m41 * x + invT.m42 * y + invT.m43 * z + invT.m44);
		wp = 1.0 / wp;
		result._set(_xt * wp, _yt * wp, _zt * wp);
	}

	public void applyInvAsPoint(final double x, final double y, final double z,
			final WB_CoordinateSequence result, final int i) {
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z + invT.m14);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z + invT.m24);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z + invT.m34);
		double wp = (invT.m41 * x + invT.m42 * y + invT.m43 * z + invT.m44);
		wp = 1.0 / wp;
		result._setI(i, _xt * wp, _yt * wp, _zt * wp);
	}

	public void applyInvAsPoint(final WB_CoordinateSequence source,
			final int i, final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z + invT.m14);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z + invT.m24);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z + invT.m34);
		double wp = (invT.m41 * x + invT.m42 * y + invT.m43 * z + invT.m44);
		wp = 1.0 / wp;
		result._set(_xt * wp, _yt * wp, _zt * wp);
	}

	public void applyInvAsPoint(final WB_CoordinateSequence source,
			final int i, final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z + invT.m14);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z + invT.m24);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z + invT.m34);
		double wp = (invT.m41 * x + invT.m42 * y + invT.m43 * z + invT.m44);
		wp = 1.0 / wp;
		result._setI(j, _xt * wp, _yt * wp, _zt * wp);
	}

	public void applyInvAsVector(final WB_Coordinate p,
			final WB_MutableCoordinate result) {
		_xt = (invT.m11 * p.xd() + invT.m12 * p.yd() + invT.m13 * p.zd());
		_yt = (invT.m21 * p.xd() + invT.m22 * p.yd() + invT.m23 * p.zd());
		_zt = (invT.m31 * p.xd() + invT.m32 * p.yd() + invT.m33 * p.zd());
		result._set(_xt, _yt, _zt);
	}

	public void applyInvAsVector(final double x, final double y,
			final double z, final WB_MutableCoordinate result) {
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyInvAsVector(final WB_CoordinateSequence source,
			final int i, final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyInvAsVector(final WB_Coordinate p,
			final WB_CoordinateSequence result, final int i) {
		_xt = (invT.m11 * p.xd() + invT.m12 * p.yd() + invT.m13 * p.zd());
		_yt = (invT.m21 * p.xd() + invT.m22 * p.yd() + invT.m23 * p.zd());
		_zt = (invT.m31 * p.xd() + invT.m32 * p.yd() + invT.m33 * p.zd());
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyInvAsVector(final double x, final double y,
			final double z, final WB_CoordinateSequence result, final int i) {
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z);
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyInvAsVector(final WB_CoordinateSequence source,
			final int i, final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (invT.m11 * x + invT.m12 * y + invT.m13 * z);
		_yt = (invT.m21 * x + invT.m22 * y + invT.m23 * z);
		_zt = (invT.m31 * x + invT.m32 * y + invT.m33 * z);
		result._setI(j, _xt, _yt, _zt);
	}

	public void applyInvAsNormal(final WB_Coordinate n,
			final WB_MutableCoordinate result) {
		_xt = (T.m11 * n.xd() + T.m21 * n.yd() + T.m31 * n.zd());
		_yt = (T.m12 * n.xd() + T.m22 * n.yd() + T.m32 * n.zd());
		_zt = (T.m13 * n.xd() + T.m23 * n.yd() + T.m33 * n.zd());
		result._set(_xt, _yt, _zt);
	}

	public void applyInvAsNormal(final double x, final double y,
			final double z, final WB_MutableCoordinate result) {
		_xt = (T.m11 * x + T.m21 * y + T.m31 * z);
		_yt = (T.m12 * x + T.m22 * y + T.m32 * z);
		_zt = (T.m13 * x + T.m23 * y + T.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyInvAsNormal(final WB_CoordinateSequence source,
			final int i, final WB_MutableCoordinate result) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (T.m11 * x + T.m21 * y + T.m31 * z);
		_yt = (T.m12 * x + T.m22 * y + T.m32 * z);
		_zt = (T.m13 * x + T.m23 * y + T.m33 * z);
		result._set(_xt, _yt, _zt);
	}

	public void applyInvAsNormal(final WB_Coordinate n,
			final WB_CoordinateSequence result, final int i) {
		_xt = (T.m11 * n.xd() + T.m21 * n.yd() + T.m31 * n.zd());
		_yt = (T.m12 * n.xd() + T.m22 * n.yd() + T.m32 * n.zd());
		_zt = (T.m13 * n.xd() + T.m23 * n.yd() + T.m33 * n.zd());
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyInvAsNormal(final double x, final double y,
			final double z, final WB_CoordinateSequence result, final int i) {
		_xt = (T.m11 * x + T.m21 * y + T.m31 * z);
		_yt = (T.m12 * x + T.m22 * y + T.m32 * z);
		_zt = (T.m13 * x + T.m23 * y + T.m33 * z);
		result._setI(i, _xt, _yt, _zt);
	}

	public void applyInvAsNormal(final WB_CoordinateSequence source,
			final int i, final WB_CoordinateSequence result, final int j) {
		id = 4 * i;
		x = source.getRaw(id++);
		y = source.getRaw(id++);
		z = source.getRaw(id++);
		_xt = (T.m11 * x + T.m21 * y + T.m31 * z);
		_yt = (T.m12 * x + T.m22 * y + T.m32 * z);
		_zt = (T.m13 * x + T.m23 * y + T.m33 * z);
		result._setI(j, _xt, _yt, _zt);
	}

	public WB_Transform(final WB_Transform Trans) {
		T = Trans.T.get();
		invT = Trans.invT.get();
		purerot = Trans.purerot;
	}

	public WB_Transform(final WB_Coordinate sourceOrigin,
			final WB_Coordinate sourceDirection,
			final WB_Coordinate targetOrigin,
			final WB_Coordinate targetDirection) {
		T = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		invT = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		purerot = true;
		addTranslate(-1, sourceOrigin);
		final WB_Vector v1 = geometryfactory
				.createNormalizedVector(sourceDirection);
		final WB_Vector v2 = geometryfactory
				.createNormalizedVector(targetDirection);
		WB_Vector axis = v1.cross(v2);
		final double l = axis.getLength();
		if (WB_Epsilon.isZero(l)) {
			if (v1.dot(v2) < 0.0) {
				axis = geometryfactory
						.createNormalizedPerpendicularVector(sourceDirection);
				addRotate(Math.PI, axis);
			}
		} else {
			final double angle = Math.atan2(l, v1.dot(v2));
			axis._normalizeSelf();
			addRotate(angle, axis);
		}

		addTranslate(targetOrigin);
	}

	public WB_Transform(final WB_Coordinate sourceDirection,
			final WB_Coordinate targetDirection) {
		T = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		invT = new WB_M44(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
		purerot = true;

		final WB_Vector v1 = geometryfactory
				.createNormalizedVector(sourceDirection);
		final WB_Vector v2 = geometryfactory
				.createNormalizedVector(targetDirection);
		WB_Vector axis = v1.cross(v2);
		final double l = axis.getLength();
		if (WB_Epsilon.isZero(l)) {
			if (v1.dot(v2) < 0.0) {
				axis = geometryfactory
						.createNormalizedPerpendicularVector(sourceDirection);
				addRotate(Math.PI, axis);
			}
		} else {
			final double angle = Math.atan2(l, v1.dot(v2));
			axis._normalizeSelf();
			addRotate(angle, axis);
		}

	}

	public WB_Transform get() {
		return new WB_Transform(this);
	}

	@Override
	public String toString() {
		final String s = "WB_Transform T:" + "\n" + "[" + T.m11 + ", " + T.m12
				+ ", " + T.m13 + ", " + T.m14 + "]" + "\n" + "[" + T.m21 + ", "
				+ T.m22 + ", " + T.m23 + ", " + T.m24 + "]" + "\n" + "["
				+ T.m31 + ", " + T.m32 + ", " + T.m33 + ", " + T.m34 + "]"
				+ "\n" + "[" + T.m41 + ", " + T.m42 + ", " + T.m43 + ", "
				+ T.m44 + "]";
		return s;
	}

	public WB_Vector getEulerAnglesXYZ() {
		if (!purerot) {
			return null;
		}
		double theta, phi, psi;
		if (WB_Epsilon.isEqualAbs(Math.abs(T.m31), 1.0)) {
			phi = 0.0;
			if (T.m31 < 0) {
				theta = Math.PI * 0.5;
				psi = Math.atan2(T.m12, T.m13);
			} else {
				theta = -Math.PI * 0.5;
				psi = Math.atan2(-T.m12, -T.m13);
			}
		} else {
			theta = -Math.asin(T.m31);
			final double ic = 1.0 / Math.cos(theta);
			psi = Math.atan2(T.m32 * ic, T.m33 * ic);
			phi = Math.atan2(T.m21 * ic, T.m11 * ic);

		}

		return geometryfactory.createVector(psi, theta, phi);

	}

}
