/*
 *
 */
package wblut.hemesh;

import wblut.geom.WB_Coordinate;

public class HE_TextureCoordinate extends HE_Element {
    private double u, v, w;
    public static final HE_TextureCoordinate ZERO = new HE_TextureCoordinate();

    public HE_TextureCoordinate() {
	u = v = w = 0;
    }

    public HE_TextureCoordinate(final WB_Coordinate uvw) {
	u = uvw.xd();
	v = uvw.yd();
	w = uvw.zd();
    }

    public HE_TextureCoordinate(final HE_TextureCoordinate uvw) {
	u = uvw.ud();
	v = uvw.vd();
	w = uvw.wd();
    }

    public HE_TextureCoordinate(final double f,
	    final HE_TextureCoordinate uvw1, final HE_TextureCoordinate uvw2) {
	final double omf = 1.0 - f;
	u = f * uvw1.ud() + omf * uvw2.ud();
	v = f * uvw1.vd() + omf * uvw2.vd();
	w = f * uvw1.wd() + omf * uvw2.wd();
    }

    public HE_TextureCoordinate(final double u, final double v) {
	this.u = u;
	this.v = v;
	w = 0;
    }

    public HE_TextureCoordinate(final double u, final double v, final double w) {
	this.u = u;
	this.v = v;
	this.w = w;
    }

    /**
     *
     *
     * @param el
     */
    public void copyProperties(final HE_TextureCoordinate el) {
	super.copyProperties(el);
	u = el.u;
	v = el.v;
	w = el.w;
    }

    /*
     * (non-Javadoc)
     *
     * @see wblut.hemesh.HE_Element#clear()
     */
    @Override
    public void clear() {
	u = v = w = 0;
    }

    public double ud() {
	return u;
    }

    public double vd() {
	return v;
    }

    public double wd() {
	return w;
    }

    public float uf() {
	return (float) u;
    }

    public float vf() {
	return (float) v;
    }

    public float wf() {
	return (float) w;
    }

    public void setUVW(final double u, final double v, final double w) {
	this.u = u;
	this.v = v;
	this.w = w;
    }

    public void setUVW(final WB_Coordinate u) {
	this.u = u.xd();
	this.v = u.yd();
	this.w = u.zd();
    }

    @Override
    public String toString() {
	return "Texture Coordinate: [u=" + ud() + ", v=" + vd() + ", w=" + wd()
		+ "]";
    }
}
