package wblut.hemesh;

public interface HE_Machine {

	public double progress = 0;

	public HE_Mesh apply(HE_Mesh mesh);

	public HE_Mesh apply(HE_Selection selection);

}
