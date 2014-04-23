package wblut.geom;

public interface Segment extends WB_Simplex {

	public WB_Point getPoint(final double t);

	public void getPointInto(final double t, final WB_Point result);

	public WB_Point getParametricPoint(final double t);

	public void getParametricPointInto(final double t, final WB_Point result);

	public WB_Point getOrigin();

	public WB_Point getEndpoint();

	public WB_Point getCenter();

	public WB_Vector getDirection();

	public double getLength();

	public void reverse();

}