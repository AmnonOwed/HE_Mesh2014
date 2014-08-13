package wblut.external.poly2tri.triangulation;

public abstract class TriangulationDebugContext {
	protected TriangulationContext<?> _tcx;

	public TriangulationDebugContext(final TriangulationContext<?> tcx) {
		_tcx = tcx;
	}

	public abstract void clear();
}
