package wblut.geom;

import java.util.Collection;
import java.util.List;

import javolution.util.FastList;

public class WB_GeometryCollection implements WB_Geometry {
	List<WB_Geometry> geometries;

	protected WB_GeometryCollection() {
		geometries = new FastList<WB_Geometry>();

	}

	protected WB_GeometryCollection(final Collection<WB_Geometry> collection) {
		geometries = new FastList<WB_Geometry>(collection.size());
		geometries.addAll(collection);
	}

	protected WB_GeometryCollection(final WB_Geometry... collection) {
		geometries = new FastList<WB_Geometry>(collection.length);
		for (final WB_Geometry geom : collection) {
			geometries.add(geom);
		}
	}

	@Override
	public WB_GeometryType getType() {
		return WB_GeometryType.COLLECTION;
	}

	public static final WB_GeometryFactory geometryfactory = WB_GeometryFactory
			.instance();

	@Override
	public int getDimension() {
		int dim = 0;
		for (final WB_Geometry geom : geometries) {
			dim = Math.max(dim, geom.getDimension());

		}

		return dim;
	}

	@Override
	public int getEmbeddingDimension() {
		int embdim = 0;
		for (final WB_Geometry geom : geometries) {
			embdim = Math.max(embdim, geom.getEmbeddingDimension());
		}
		return embdim;
	}

	public WB_Geometry getGeometry(final int i) {
		if (geometries == null) {
			return null;
		}
		return geometries.get(i);
	}

	public int getNumberOfGeometries() {
		if (geometries == null) {
			return 0;
		}
		return geometries.size();
	}

	public void add(final WB_Geometry... geometry) {
		for (final WB_Geometry geom : geometry) {
			geometries.add(geom);
		}

	}

	public void add(final Collection<? extends WB_Geometry> geometry) {
		geometries.addAll(geometry);

	}

	public void add(final WB_GeometryCollection geometry) {
		for (int i = 0; i < geometry.getNumberOfGeometries(); i++) {
			geometries.add(geometry.getGeometry(i));
		}

	}

	@Override
	public WB_Geometry apply(final WB_Transform T) {
		final WB_GeometryCollection collection = geometryfactory
				.createCollection();
		for (int i = 0; i < getNumberOfGeometries(); i++) {
			collection.add(getGeometry(i).apply(T));
		}
		return collection;
	}

}
