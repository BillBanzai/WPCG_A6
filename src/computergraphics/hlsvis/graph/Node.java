package computergraphics.hlsvis.graph;

/**
 * Node in a graph.
 * 
 * @author Philipp Jenke
 *
 */
public class Node<T> {

	/**
	 * Content of the node.
	 */
	private T element;

	/**
	 * Konstruktor
	 */
	public Node(T element) {
		this.element = element;
	}

	/**
	 * Getter.
	 */
	public T getElement() {
		return element;
	}

	@Override
	public String toString() {
		return element.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Node)) {
			return false;
		}
		Node<?> otherNode = (Node<?>) other;
		return getElement().equals(otherNode.getElement());
	}
}
