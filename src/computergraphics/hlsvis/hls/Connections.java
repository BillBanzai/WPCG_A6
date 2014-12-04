package computergraphics.hlsvis.hls;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of all possible transport routes, needs to be a fully
 * connected graph.
 * 
 * @author Philipp Jenke
 *
 */
public class Connections {

	/**
	 * List of connections.
	 */
	private List<Connection> connections = new ArrayList<Connection>();

	/**
	 * Construktor.
	 */
	public Connections() {
	}

	/**
	 * Init instance with all available connections.
	 */
	public void initWithAllConnections() {
		connections = TransportNetwork.getAllConnections();
	}

	/**
	 * Create message description.
	 */
	public String toJson() {
		String s = "{\n\"" + HlsConstants.TRANSPORTBEZIEHUNGEN + "\":\n[\n";
		for (int i = 0; i < connections.size(); i++) {
			Connection transportBeziehung = connections.get(i);
			s += "{\"" + HlsConstants.STARTLOKATION + "\":\""
					+ transportBeziehung.getStartLocation().toString()
					+ "\",\"" + HlsConstants.ZIELLOKATION + "\":\""
					+ transportBeziehung.getTargetLocation().toString()
					+ "\",\"" + HlsConstants.DAUER + "\":"
					+ transportBeziehung.getDistance() + "}";
			if (i != connections.size() - 1) {
				s += ",";
			}
			s += "\n";
		}
		s += "]\n}";
		return s;
	}

	/**
	 * Setter.
	 */
	public void addConnection(Connection connection) {
		connections.add(connection);
	}

	/**
	 * Getter.
	 */
	public int getNumberOfConnections() {
		return connections.size();
	}

	/**
	 * Getter.
	 */
	public Connection getConnection(int index) {
		return connections.get(index);
	}

}
