/**
 * Prof. Philipp Jenke
 * Hochschule für Angewandte Wissenschaften (HAW), Hamburg
 * Lecture demo program.
 */
package computergraphics.hlsvis.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compute the shortest path between two nodes in a graph using the algorithm of
 * Dijkstra.
 * 
 * @author Philipp Jenke
 * 
 */
public class ShortestPathDijkstra<T> {

	/**
	 * Computed distance to each node.
	 */
	private Map<Node<T>, Double> distances = new HashMap<Node<T>, Double>();

	/**
	 * Computed predecessors of each node
	 */
	private Map<Node<T>, Node<T>> predecessors = new HashMap<Node<T>, Node<T>>();

	/**
	 * This is the active queue.
	 */
	private Set<Node<T>> Q = new HashSet<Node<T>>();

	/**
	 * Compute the shortest path from a start node to the destination node in a
	 * graph.
	 * 
	 * @return
	 */
	public List<Node<T>> computeShortestPath(IGraph<T> graph,
			Node<T> startNode, Node<T> destinationNode) {

		initialize(graph, startNode);

		while (!Q.isEmpty()) {
			Node<T> node = getNodeWithShortestDistance();
			Q.remove(node);
			List<Node<T>> neighbors = graph.getNeighbors(node);
			for (Node<T> neighborNode : neighbors) {
				if (Q.contains(neighborNode)) {
					updateDistance(graph, node, neighborNode);
				}
			}
		}

		// Compute the path from the predecessors
		List<Node<T>> path = computePath(startNode, destinationNode);
		return path;
	}

	/**
	 * Compute the shorted path from the predecessors.
	 */
	private List<Node<T>> computePath(Node<T> startNode, Node<T> destinationNode) {
		ArrayList<Node<T>> path = new ArrayList<Node<T>>();
		path.add(destinationNode);
		Node<T> currentNode = destinationNode;
		while (predecessors.get(currentNode) != null) {
			currentNode = predecessors.get(currentNode);
			path.add(0, currentNode);
		}
		return path;
	}

	/**
	 * Update the computed distance for the node 'neighborNode'.
	 */
	private void updateDistance(IGraph<T> graph, Node<T> node,
			Node<T> neighborNode) {
		double alternativeDistance = distances.get(node)
				+ graph.getEdgeWeight(node, neighborNode);
		if (alternativeDistance < distances.get(neighborNode)) {
			distances.put(neighborNode, alternativeDistance);
			predecessors.put(neighborNode, node);
		}
	}

	/**
	 * @return
	 */
	private Node<T> getNodeWithShortestDistance() {
		Node<T> bestNode = null;
		double bestDistance = Double.POSITIVE_INFINITY;
		for (Node<T> node : Q) {
			if (bestNode == null || distances.get(node) < bestDistance) {
				bestDistance = distances.get(node);
				bestNode = node;
			}
		}
		return bestNode;
	}

	private void initialize(IGraph<T> graph, Node<T> startNode) {
		// Distances
		distances.clear();
		predecessors.clear();
		Q.clear();

		for (int nodeIndex = 0; nodeIndex < graph.getNumberOfNodes(); nodeIndex++) {
			if (graph.getNode(nodeIndex) == startNode) {
				distances.put(startNode, new Double(0));
			} else {
				distances.put(graph.getNode(nodeIndex),
						Double.POSITIVE_INFINITY);
			}
		}

		// Predecessors
		for (int nodeIndex = 0; nodeIndex < graph.getNumberOfNodes(); nodeIndex++) {
			predecessors.put(graph.getNode(nodeIndex), null);
		}

		// List of all nodes
		for (int nodeIndex = 0; nodeIndex < graph.getNumberOfNodes(); nodeIndex++) {
			Q.add(graph.getNode(nodeIndex));
		}
	}
}
