package wblut.geom;

public interface WB_Mesh extends WB_Geometry {
    public WB_Point getCenter();

    public WB_AABB getAABB();

    public WB_Vector getFaceNormal(final int id);

    public WB_Point getFaceCenter(final int id);

    public WB_Vector getVertexNormal(final int i);

    public int getNumberOfFaces();

    public int getNumberOfVertices();

    public WB_Coordinate getVertex(final int i);

    public WB_CoordinateSequence getPoints();

    public int[][] getFacesAsInt();

    public int[][] getEdgesAsInt();
}
