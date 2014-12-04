/**
 * Prof. Philipp Jenke
 * Hochschule für Angewandte Wissenschaften (HAW), Hamburg
 * Lecture demo program.
 */
package computergraphics.hlsvis.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Interface IGraph using an adjacency list.
 * 
 * @author Philipp Jenke
 * 
 */
public class GraphAdjacencyList<T> implements IGraph<T> {

	/**
	 * Representation of an edge.
	 * 
	 * @author Philipp Jenke
	 *
	 */
	private class Edge {
		/**
		 * Weight.
		 */
		private double weight = Double.POSITIVE_INFINITY;

		/**
		 * Target node index.
		 */
		private int targetIndex = -1;

		/**
		 * Constructor.
		 */
		public Edge(int targetIndex, double weight) {
			this.targetIndex = targetIndex;
			this.weight = weight;
		}

		/**
		 * Getter.
		 */
		public double getWeight() {
			return weight;
		}

		/**
		 * Getter.
		 */
		public int getTargetIndex() {
			return targetIndex;
		}

		/**
		 * Setter.
		 */
		public void setWeight(double weight) {
			this.weight = weight;
		}
	}

	/**
	 * Listof nodes in the graph.
	 */
	private final List<Node<T>> nodes = new ArrayList<Node<T>>();

	/**
	 * List of edge lists. There are as many edge lists as there are nodes.
	 * Every edge list is a list itself; a list of edges from the node to other
	 * nodes.
	 */
	private final List<List<Edge>> edges = new ArrayList<List<Edge>>();

	/**
	 * Konstruktor.
	 */
	public GraphAdjacencyList() {

	}

	@Override
	public void addNode(Node<T> node) {
		nodes.add(node);
	}

	@Override
	public void addEdge(Node<T> start, Node<T> ende, double weight) {
		int firstNodeIndex = nodes.indexOf(start);
		int secondNodeIndex = nodes.indexOf(ende);
		while (edges.size() <= firstNodeIndex
				|| edges.size() <= secondNodeIndex) {
			edges.add(new ArrayList<Edge>());
		}

		if (firstNodeIndex >= 0 && secondNodeIndex >= 0) {
			edges.get(firstNodeIndex).add(new Edge(secondNodeIndex, weight));
			edges.get(secondNodeIndex).add(new Edge(firstNodeIndex, weight));
		}
	}

	@Override
	public int getNumberOfNodes() {
		return nodes.size();
	}

	@Override
	public int getNumberOfEdges() {
		int numberOfEdges = 0;
		for (List<Edge> nodeEdges : edges) {
			numberOfEdges += nodeEdges.size();
		}
		return numberOfEdges / 2;
	}

	@Override
	public Node<T> getNode(int nodeIndex) {
		return nodes.get(nodeIndex);
	}

	@Override
	public List<Node<T>> getNeighbors(Node<T> node) {
		List<Node<T>> neighbors = new ArrayList<Node<T>>();
		int index = nodes.indexOf(node);
		if (index < 0) {
			return neighbors;
		}
		List<Integer> n = getNeighborIndices(index);
		for (Integer i : n) {
			neighbors.add(nodes.get(i));
		}
		return neighbors;
	}

	@Override
	public List<Integer> getNeighborIndices(int nodeIndex) {
		List<Integer> neighbors = new ArrayList<Integer>();
		if (nodeIndex < 0 || nodeIndex >= edges.size()) {
			return neighbors;
		}
		for (Edge edge : edges.get(nodeIndex)) {
			neighbors.add(edge.getTargetIndex());
		}
		return neighbors;
	}

	@Override
	public double getEdgeWeight(int startIndex, int targetIndex) {
		for (Edge edge : edges.get(startIndex)) {
			if (targetIndex == edge.getTargetIndex()) {
				return edge.getWeight();
			}
		}
		return Double.NEGATIVE_INFINITY;
	}

	@Override
	public double getEdgeWeight(Node<T> startNode, Node<T> targetNode) {
		return getEdgeWeight(nodes.indexOf(startNode),
				nodes.indexOf(targetNode));
	}

	@Override
	public void setWeight(Node<String> startNode, Node<String> targetNode,
			double weight) {

		int startIndex = nodes.indexOf(startNode);
		int targetIndex = nodes.indexOf(targetNode);

		Edge edgeStartTarget = getEdge(startIndex, targetIndex);
		if (edgeStartTarget != null) {
			edgeStartTarget.setWeight(weight);
		}
		Edge edgeTargetStart = getEdge(targetIndex, startIndex);
		if (edgeTargetStart != null) {
			edgeTargetStart.setWeight(weight);
		}
	}

	/**
	 * Returns the edge between start and target index. Return null if the edge
	 * cannot be found.
	 */
	private Edge getEdge(int startIndex, int targetIndex) {

		if (startIndex < 0 || startIndex >= edges.size()
				|| targetIndex < 0 || targetIndex >= edges.size()) {
			return null;
		}

		for (Edge edge : edges.get(startIndex)) {
			if (edge.getTargetIndex() == targetIndex) {
				return edge;
			}
		}
		return null;
	}

	@Override
	public boolean edgeExistst(Node<T> startNode, Node<T> targetNode) {
		int startIndex = nodes.indexOf(startNode);
		int endIndex = nodes.indexOf(targetNode);
		return getEdge(startIndex, endIndex) != null;
	}

	@Override
	public String toString() {
		return "Graph (adjacency list), #nodes: " + getNumberOfNodes()
				+ ", #edges: " + getNumberOfEdges();
	}
}
