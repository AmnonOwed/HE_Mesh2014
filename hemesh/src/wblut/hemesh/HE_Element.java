package wblut.hemesh;

import wblut.geom.WB_GeometryFactory;

public abstract class HE_Element {
	protected static long _currentKey;
	protected final long _key;
	protected int _label;
	protected final static WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	public HE_Element() {
		_key = _currentKey;
		_currentKey++;

		_label = -1;

	}

	public final void setLabel(final int label) {
		_label = label;

	}

	public final long getKey() {
		return _key;

	}

	public final int getLabel() {
		return _label;

	}

}
