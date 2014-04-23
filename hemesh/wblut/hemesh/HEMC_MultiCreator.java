package wblut.hemesh;

public abstract class HEMC_MultiCreator {

	protected int _numberOfMeshes;

	public HEMC_MultiCreator() {
		super();
		_numberOfMeshes = 0;
	}

	public HE_Mesh[] create() {
		final HE_Mesh[] result = new HE_Mesh[0];
		return result;
	}

	public int numberOfMeshes() {
		return _numberOfMeshes;
	}

}
