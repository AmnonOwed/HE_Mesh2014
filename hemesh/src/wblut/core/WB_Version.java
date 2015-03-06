package wblut.core;

public class WB_Version {
    public static final WB_Version CURRENT_VERSION = new WB_Version();
    public static final int MAJOR = 2;
    public static final int MINOR = 0;
    public static final int PATCH = 10;
    private static final String releaseInfo = "Quahog";

    public static void main(final String[] args) {
	System.out.println(CURRENT_VERSION);
    }

    private WB_Version() {
    }

    public static int getMajor() {
	return MAJOR;
    }

    public static int getMinor() {
	return MINOR;
    }

    public static int getPatch() {
	return PATCH;
    }

    @Override
    public String toString() {
	final String ver = "W:Blut HE_Mesh " + MAJOR + "." + MINOR + "."
		+ PATCH;
	if (releaseInfo != null && releaseInfo.length() > 0) {
	    return ver + " " + releaseInfo
		    + System.getProperty("line.separator");
	}
	return ver;
    }

    public static String version() {
	return CURRENT_VERSION.toString();
    }
}