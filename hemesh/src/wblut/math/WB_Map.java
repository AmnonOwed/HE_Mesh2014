package wblut.math;

import wblut.geom.WB_Coordinate;
import wblut.geom.WB_MutableCoordinate;

public interface WB_Map {
    public void map(WB_Coordinate p, WB_MutableCoordinate result);
}
