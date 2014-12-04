/**
 * Prof. Philipp Jenke
 * Hochschule für Angewandte Wissenschaften (HAW), Hamburg
 * Lecture demo program.
 */
package computergraphics.hlsvis.graph;

import java.util.List;

/**
 * Representation of a generic graph.
 * 
 * @author Philipp Jenke
 * 
 */
public interface IGraph<T> {

	/**
	 * Add node.
	 */
	public void addNode(Node<T> node);

	/**
	 * Add edge, the order of nodes is irrelevant.
	 * 
	 * @param firstNode
	 *            First node of the edge.
	 * @param secondNode
	 *            Second node of the edge.
	 */
	public void addEdge(Node<T> startNode, Node<T> targetNode, double weight);

	/**
	 * Getter.
	 */
	public int getNumberOfNodes();

	/**
	 * Getter.
	 */
	public int getNumberOfEdges();

	/**
	 * Getter.
	 */
	public Node<T> getNode(int nodeIndex);

	/**
	 * Return the list of adjacent nodes (with a connecting edge).
	 */
	public List<Node<T>> getNeighbors(Node<T> node);

	/**
	 * Return the list of indices of adjacent nodes (with a connecting edge).
	 */
	public List<Integer> getNeighborIndices(int nodeIndex);

	/**
	 * Getter for the edge weight.
	 */
	public double getEdgeWeight(int startIndex, int targetIndex);

	/**
	 * Getter for the edge weight.
	 */
	public double getEdgeWeight(Node<T> startNode, Node<T> targetNode);

	/**
	 * Setter for the edge weight.
	 */
	public void setWeight(Node<String> startKnoten, Node<String> zielKnoten,
			double gewicht);

	/**
	 * Checks of an edge between the nodes exists.
	 */
	public boolean edgeExistst(Node<T> startNode, Node<T> targetNode);
}
