package wblut.hemesh;

public abstract class HE_Machine {
	public static final HET_ProgressTracker tracker = HET_ProgressTracker
			.instance();

	public abstract HE_Mesh apply(HE_Mesh mesh);

	public abstract HE_Mesh apply(HE_Selection selection);

	public String getStatus() {

		return tracker.getStatus();
	}

}
