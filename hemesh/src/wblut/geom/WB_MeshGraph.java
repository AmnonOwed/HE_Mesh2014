/*
 *
 */
package wblut.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import javolution.util.FastTable;
import wblut.hemesh.HE_Mesh;

/**
 *
 */
public class WB_MeshGraph {
    /**
     *
     */
    private final WB_GVertex[] vertices;
    /**
     *
     */
    private int lastSource;

    /**
     *
     *
     * @param mesh
     */
    public WB_MeshGraph(final WB_Mesh mesh) {
	vertices = new WB_GVertex[mesh.getNumberOfVertices()];
	for (int i = 0; i < mesh.getNumberOfVertices(); i++) {
	    vertices[i] = new WB_GVertex(i, mesh.getVertex(i));
	}
	final int[][] meshedges = mesh.getEdgesAsInt();
	WB_Coordinate p0;
	WB_Coordinate p1;
	WB_GVertex v0;
	WB_GVertex v1;
	double d;
	for (int i = 0; i < meshedges.length; i++) {
	    if (meshedges[i][0] != meshedges[i][1]) {
		p0 = mesh.getVertex(meshedges[i][0]);
		p1 = mesh.getVertex(meshedges[i][1]);
		d = WB_GeometryOp.getDistance3D(p0, p1);
		v0 = vertices[meshedges[i][0]];
		v1 = vertices[meshedges[i][1]];
		v0.adjacencies.add(new WB_GEdge(v1, d));
		v1.adjacencies.add(new WB_GEdge(v0, d));
	    }
	}
	lastSource = -1;
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public int getVertex(final int i) {
	return vertices[i].index;
    }

    /**
     *
     *
     * @param i
     */
    public void computePaths(final int i) {
	final WB_GVertex source = vertices[i];
	for (int j = 0; j < vertices.length; j++) {
	    vertices[j].reset();
	}
	source.minDistance = 0.;
	final PriorityQueue<WB_GVertex> vertexQueue = new PriorityQueue<WB_GVertex>();
	vertexQueue.add(source);
	while (!vertexQueue.isEmpty()) {
	    final WB_GVertex u = vertexQueue.poll();
	    // Visit each edge exiting u
	    for (final WB_GEdge e : u.adjacencies) {
		final WB_GVertex v = e.target;
		final double weight = e.weight;
		final double distanceThroughU = u.minDistance + weight;
		if (distanceThroughU < v.minDistance) {
		    vertexQueue.remove(v);
		    v.minDistance = distanceThroughU;
		    v.previous = u;
		    vertexQueue.add(v);
		}
	    }
	}
	lastSource = i;
    }

    /**
     *
     *
     * @param source
     * @param target
     * @return
     */
    public int[] getShortestPath(final int source, final int target) {
	if (source != lastSource) {
	    computePaths(source);
	}
	if (source == target) {
	    return new int[] { source };
	}
	final List<WB_GVertex> path = new ArrayList<WB_GVertex>();
	for (WB_GVertex vertex = vertices[target]; vertex != null; vertex = vertex.previous) {
	    path.add(vertex);
	}
	Collections.reverse(path);
	final int[] result = new int[path.size()];
	for (int i = 0; i < path.size(); i++) {
	    result[i] = path.get(i).index;
	}
	return result;
    }

    /**
     *
     *
     * @param i
     * @return
     */
    public WB_Frame getFrame(final int i) {
	final WB_Frame frame = new WB_Frame();
	computePaths(i);
	for (final WB_GVertex v : vertices) {
	    frame.addNode(v.pos, 0);
	}
	for (final WB_GVertex v : vertices) {
	    final int[] path = getShortestPath(i, v.index);
	    for (int j = 0; j < (path.length - 1); j++) {
		frame.nodes.get(path[j]).value = Math.max(
			frame.nodes.get(path[j]).value,
			1.0 - ((j * 1.0) / path.length));
		frame.addStrut(path[j], path[j + 1]);
	    }
	    frame.nodes.get(path[path.length - 1]).value = Math.max(
		    frame.nodes.get(path[path.length - 1]).value,
		    1.0 / path.length);
	}
	return frame;
    }

    /**
     *
     *
     * @param i
     * @param maxnodes
     * @return
     */
    public WB_Frame getFrame(final int i, final int maxnodes) {
	final WB_Frame frame = new WB_Frame();
	computePaths(i);
	for (final WB_GVertex v : vertices) {
	    frame.addNode(v.pos, 0);
	}
	for (final WB_GVertex v : vertices) {
	    final int[] path = getShortestPath(i, v.index);
	    final int nodes = Math.min(maxnodes, path.length);
	    for (int j = 0; j < (nodes - 1); j++) {
		frame.nodes.get(path[j]).value = Math.max(
			frame.nodes.get(path[j]).value,
			1.0 - ((j * 1.0) / nodes));
		frame.addStrut(path[j], path[j + 1]);
	    }
	    frame.nodes.get(path[nodes - 1]).value = Math.max(
		    frame.nodes.get(path[nodes - 1]).value, 1.0 / nodes);
	}
	return frame;
    }

    public WB_Frame getFrame(final int i, final int maxnodes, final int cuttail) {
	final WB_Frame frame = new WB_Frame();
	computePaths(i);
	for (final WB_GVertex v : vertices) {
	    frame.addNode(v.pos, 0);
	}
	for (final WB_GVertex v : vertices) {
	    final int[] path = getShortestPath(i, v.index);
	    final int nodes = Math.min(maxnodes, path.length - cuttail);
	    if (nodes <= 1) {
		continue;
	    }
	    for (int j = 0; j < (nodes - 1); j++) {
		frame.nodes.get(path[j]).value = Math.max(
			frame.nodes.get(path[j]).value,
			1.0 - ((j * 1.0) / nodes));
		frame.addStrut(path[j], path[j + 1]);
	    }
	    frame.nodes.get(path[nodes - 1]).value = Math.max(
		    frame.nodes.get(path[nodes - 1]).value, 1.0 / nodes);
	}
	return frame;
    }

    /**
     *
     *
     * @param args
     */
    public static void main(final String[] args) {
	final WB_Geodesic geo = new WB_Geodesic(1.0, 2, 0,
		WB_Geodesic.ICOSAHEDRON);
	WB_MeshGraph graph = new WB_MeshGraph(geo.getMesh());
	for (final WB_GVertex v : graph.vertices) {
	    final int[] path = graph.getShortestPath(5, v.index);
	    System.out.println("Distance to " + v + ": " + v.minDistance);
	    System.out.print("Path: ");
	    for (int i = 0; i < (path.length - 1); i++) {
		System.out.print(path[i] + "->");
	    }
	    System.out.println(path[path.length - 1] + ".");
	}
	final HE_Mesh mesh = new HE_Mesh(geo.getMesh());
	mesh.smooth();
	graph = new WB_MeshGraph(mesh);
	for (final WB_GVertex v : graph.vertices) {
	    final int[] path = graph.getShortestPath(0, v.index);
	    System.out.println("Distance to " + v + ": " + v.minDistance);
	    System.out.print("Path: ");
	    for (int i = 0; i < (path.length - 1); i++) {
		System.out.print(path[i] + "->");
	    }
	    System.out.println(path[path.length - 1] + ".");
	}
	for (final WB_GVertex v : graph.vertices) {
	    final int[] path = graph.getShortestPath(5, v.index);
	    System.out.println("Distance to " + v + ": " + v.minDistance);
	    System.out.print("Path: ");
	    for (int i = 0; i < (path.length - 1); i++) {
		System.out.print(path[i] + "->");
	    }
	    System.out.println(path[path.length - 1] + ".");
	}
    }

    /**
     *
     */
    public class WB_GVertex implements Comparable<WB_GVertex> {
	/**
	 *
	 */
	public final int index;
	/**
	 *
	 */
	public List<WB_GEdge> adjacencies;
	/**
	 *
	 */
	public double minDistance = Double.POSITIVE_INFINITY;
	/**
	 *
	 */
	public WB_GVertex previous;
	/**
	 *
	 */
	WB_Coordinate pos;

	/**
	 *
	 *
	 * @param id
	 * @param pos
	 */
	public WB_GVertex(final int id, final WB_Coordinate pos) {
	    index = id;
	    adjacencies = new FastTable<WB_GEdge>();
	    this.pos = new WB_Point(pos);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
	    return ("Vertex " + index);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(final WB_GVertex other) {
	    return Double.compare(minDistance, other.minDistance);
	}

	/**
	 *
	 */
	public void reset() {
	    minDistance = Double.POSITIVE_INFINITY;
	    previous = null;
	}
    }

    /**
     *
     */
    public class WB_GEdge {
	/**
	 *
	 */
	public final WB_GVertex target;
	/**
	 *
	 */
	public final double weight;

	/**
	 *
	 *
	 * @param argTarget
	 * @param argWeight
	 */
	public WB_GEdge(final WB_GVertex argTarget, final double argWeight) {
	    target = argTarget;
	    weight = argWeight;
	}
    }
}
