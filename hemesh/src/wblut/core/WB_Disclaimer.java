package wblut.core;

public class WB_Disclaimer {
    public static final WB_Disclaimer CURRENT_DISCLAIMER = new WB_Disclaimer();

    @Override
    public String toString() {
	final String dis = "";
	return dis;
    }

    public static String disclaimer() {
	return CURRENT_DISCLAIMER.toString();
    }
}