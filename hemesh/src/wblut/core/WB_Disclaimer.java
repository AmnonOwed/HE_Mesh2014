/*
 *
 */
package wblut.core;

/**
 *
 */
public class WB_Disclaimer {
    /**
     *
     */
    public static final WB_Disclaimer CURRENT_DISCLAIMER = new WB_Disclaimer();

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	final String dis = "License: https://github.com/wblut/HE_Mesh2014#license";
	return dis;
    }

    /**
     * @return
     */
    public static String disclaimer() {
	return CURRENT_DISCLAIMER.toString();
    }
}