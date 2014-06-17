package wblut.geom;

public class WB_TriGrid {
	double scale;
	double c60 = Math.cos(Math.PI / 3.0);
	double s60 = Math.sin(Math.PI / 3.0);

	public WB_TriGrid() {
		this.scale = 1.0;
	}

	public WB_TriGrid(double scale) {
		this.scale = scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public WB_Point getPoint(int b, int c) {
		return new WB_Point(scale * (c60 * c + b), scale * s60 * c, 0);
	}
}
