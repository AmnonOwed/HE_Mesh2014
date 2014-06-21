package wblut.geom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

import javolution.util.FastTable;
import wblut.hemesh.HE_Mesh;

public class WB_MeshGraph {
	private WB_GVertex[] vertices;
	private int lastSource;

	public WB_MeshGraph(WB_Mesh mesh) {
		vertices = new WB_GVertex[mesh.getNumberOfVertices()];
		for (int i = 0; i < mesh.getNumberOfVertices(); i++) {
			vertices[i] = new WB_GVertex(i);

		}

		int[][] meshedges = mesh.getEdgesAsInt();
		WB_Coordinate p0;
		WB_Coordinate p1;
		WB_GVertex v0;
		WB_GVertex v1;

		double d;
		for (int i = 0; i < meshedges.length; i++) {
			if (meshedges[i][0] != meshedges[i][1]) {
				p0 = mesh.getVertex(meshedges[i][0]);
				p1 = mesh.getVertex(meshedges[i][1]);
				d = WB_Distance.getDistance3D(p0, p1);
				v0 = vertices[meshedges[i][0]];
				v1 = vertices[meshedges[i][1]];
				v0.adjacencies.add(new WB_GEdge(v1, d));
				v1.adjacencies.add(new WB_GEdge(v0, d));
			}
		}
		lastSource = -1;

	}

	public int getVertex(int i) {
		return vertices[i].index;
	}

	public void computePaths(int i) {

		WB_GVertex source = vertices[i];
		for (int j = 0; j < vertices.length; j++) {
			vertices[j].reset();

		}
		source.minDistance = 0.;
		PriorityQueue<WB_GVertex> vertexQueue = new PriorityQueue<WB_GVertex>();
		vertexQueue.add(source);

		while (!vertexQueue.isEmpty()) {
			WB_GVertex u = vertexQueue.poll();

			// Visit each edge exiting u
			for (WB_GEdge e : u.adjacencies) {
				WB_GVertex v = e.target;
				double weight = e.weight;
				double distanceThroughU = u.minDistance + weight;
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

	public int[] getShortestPath(int source, int target) {

		if (source != lastSource)
			computePaths(source);

		if (source == target)
			return new int[] { source };

		List<WB_GVertex> path = new ArrayList<WB_GVertex>();
		for (WB_GVertex vertex = vertices[target]; vertex != null; vertex = vertex.previous)
			path.add(vertex);
		Collections.reverse(path);
		int[] result = new int[path.size()];
		for (int i = 0; i < path.size(); i++) {
			result[i] = path.get(i).index;
		}
		return result;
	}

	public static void main(String[] args) {
		WB_Geodesic geo = new WB_Geodesic(1.0, 8, 8, WB_Geodesic.ICOSAHEDRON);
		WB_MeshGraph graph = new WB_MeshGraph(geo.getMesh());
		for (WB_GVertex v : graph.vertices) {
			int[] path = graph.getShortestPath(5, v.index);
			System.out.println("Distance to " + v + ": " + v.minDistance);
			System.out.print("Path: ");
			for (int i = 0; i < path.length - 1; i++) {
				System.out.print(path[i] + "->");
			}
			System.out.println(path[path.length - 1] + ".");
		}
		HE_Mesh mesh = new HE_Mesh(geo.getMesh());
		mesh.smooth();
		graph = new WB_MeshGraph(mesh);
		for (WB_GVertex v : graph.vertices) {
			int[] path = graph.getShortestPath(0, v.index);
			System.out.println("Distance to " + v + ": " + v.minDistance);
			System.out.print("Path: ");
			for (int i = 0; i < path.length - 1; i++) {
				System.out.print(path[i] + "->");
			}
			System.out.println(path[path.length - 1] + ".");
		}

		for (WB_GVertex v : graph.vertices) {
			int[] path = graph.getShortestPath(5, v.index);
			System.out.println("Distance to " + v + ": " + v.minDistance);
			System.out.print("Path: ");
			for (int i = 0; i < path.length - 1; i++) {
				System.out.print(path[i] + "->");
			}
			System.out.println(path[path.length - 1] + ".");
		}
	}

	public class WB_GVertex implements Comparable<WB_GVertex> {

		public final int index;
		public List<WB_GEdge> adjacencies;
		public double minDistance = Double.POSITIVE_INFINITY;
		public WB_GVertex previous;

		public WB_GVertex(int id) {
			index = id;
			adjacencies = new FastTable<WB_GEdge>();
		}

		public String toString() {
			return ("Vertex " + index);
		}

		public int compareTo(WB_GVertex other) {
			return Double.compare(minDistance, other.minDistance);
		}

		public void reset() {
			minDistance = Double.POSITIVE_INFINITY;
			previous = null;
		}

	}

	public class WB_GEdge {
		public final WB_GVertex target;
		public final double weight;

		public WB_GEdge(WB_GVertex argTarget, double argWeight) {
			target = argTarget;
			weight = argWeight;
		}
	}

}
